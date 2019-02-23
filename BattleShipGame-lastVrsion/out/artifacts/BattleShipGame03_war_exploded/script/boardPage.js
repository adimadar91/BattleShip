(function () {

    var refreshRate = 1000; //miliseconds
    var checkGameStartInterval;
    var updateDetailsInterval;
    var ajaxUpdateBoardInterval;
    var titleIdInterval;
    var isGameOver = false;
    var reviewOffset = 0;
    var isReplayMode = false;
    var currentRoomId;  // this enables for the same session to be in different rooms
    const organizer = "organizer";
    const spectator = "spectator";
    const roomid = "roomid";
    const requestType = "requestType";
    const roomsURL = "rooms";
    const gameURL = "game";





    $(document).ready(function(){
        // $('.mine').on("dragstart", function (event) {
        //     var dt = event.originalEvent.dataTransfer;
        //     dt.setData('Text', $(this).attr('id'));
        //     console.log("drag");
        // });
        $('table td').on("dragenter dragover drop", function (event) {
            event.preventDefault();
            if (event.type === 'drop') {
                var data = event.originalEvent.dataTransfer.getData('Text',$(this).attr('id'));
                de=$('#'+data).detach();
                de.appendTo($(this));
                console.log("drop");
            };
        });
    })





    function allowDrop(ev) {
        ev.preventDefault();
    }

    // function drag(ev) {
    //     ev.dataTransfer.setData("text", ev.target.id);
    // }

    function drop(ev) {
        ev.preventDefault();
        var data = ev.dataTransfer.getData("text");
        ev.target.appendChild(document.getElementById(data));
    }

    function Coordinate(row, column) {
        this.key = row;
        this.value = column;
    }

    $(document).ready(function () { //DO NOT MOVE THIS FUNCTION
        currentRoomId = Cookies.get(roomid);
    });

    function makeUserOptions(requestType) {
        if (Cookies.get(spectator)) {
            return {
                requestType: requestType,
                roomid: currentRoomId,
                spectator: true
            }
        }
        else {
            return {
                requestType: requestType,
                roomid: currentRoomId,
                organizer: Cookies.get(organizer)
            }
        }
    }

//region board

    function expandPageWidthAccordingToBoard(width) {
        var widthOfBlock = $(".block").width() + 2;
        var actualWidthOfBoard = widthOfBlock * width;
        var widthOfControl = $("#controlPanel").width();

        if (actualWidthOfBoard + widthOfControl > 970) {
            $(".container").css("width", "100%");    //increase the size of all the containers for a larger board
        }
    }




        function updateBoard(board, boardStr) {
            var size = board.length;
            for (var row = 0; row < size; row++) {
                for (var col = 0; col < size; col++) {
                    $(document.getElementById(boardStr).rows[row].getElementsByTagName("td")[col]).innerText = board[row][col].m_sign;
                }
            }
        }

    function ajaxUpdateBoard() {
        $.ajax({
            data: {
                requestType: "board",
                roomid: currentRoomId,
                organizer: Cookies.get(organizer)
            },
            url: gameURL,
            success: function (response) {
                createBoards(response.myBoard, response.trackingBoard);
                expandPageWidthAccordingToBoard(response.myBoard.length);
            }

        });
    }


    $(document).on("click", "td.toggler", function () {
        $(this).toggleClass('selected').siblings().removeClass('selected');

        $(this).closest('tr').siblings().each(function() {
            $(this).children('td').each(function() {
                $(this).removeClass('selected');
            });
        });
    });


        function createBoards(myBoard, trackingBoard) {


            var tableBoard = $("#myBoard");
            tableBoard.addClass("board");

            var tableTracking = $("#trackingBoard");
            tableTracking.addClass("board");

            printBoard(myBoard, tableBoard, true);
            printBoard(trackingBoard, tableTracking, false);
        }

        function printBoard(theBoard, table, isMyBoard) {
            var selected = table.find("td.toggler.selected").attr('id');
            table.empty();
            var size = theBoard.length;

            for (var row = 0; row < size; row++) {
                var tr = document.createElement('tr');

                for (var column = 0; column < size; column++) {
                    var td = document.createElement('td');
                    td.setAttribute("row", row);
                    td.setAttribute("column", column);
                   // td.setAttribute("ondrop", "drop(event)");
                    //td.setAttribute("ondragover", "allowDrop(event)");

                    td.innerText = theBoard[row][column].m_sign;
                    td.classList.add("waves-effect", "waves-light", "tdBoard");


                    if(isMyBoard) {
                         td.classList.add("toggler")
                         if (selected != undefined) {
                             $('#' + selected.toString()).addClass('selected');
                         }
                    }

                    if (!isMyBoard) {
                        td.onclick = function () {
                            var row = $(this).attr("row");
                            var column = $(this).attr("column");
                            var pos = new Coordinate(row, column);

                            $.ajax({
                                data: {
                                    requestType: "move",
                                    "selectedCoord": JSON.stringify(pos),
                                    roomid: currentRoomId,
                                    organizer: Cookies.get(organizer),
                                },
                                url: gameURL,
                                success: function (result) {
                                    showMessage("msg", result.messages);
                                }
                            });
                            //doTurnDone();
                        };
                    }
                    tr.appendChild(td);
                }
                table.append(tr);
            }

        }

        $(document).ready(function ajaxBoard() {
            $.ajax({
                data: {
                    requestType: "board",
                    roomid: currentRoomId,
                    organizer: Cookies.get(organizer)
                },
                url: gameURL,
                success: function (response) {
                    createBoards(response.myBoard, response.trackingBoard);
                    expandPageWidthAccordingToBoard(response.myBoard.length);
                    var mine = document.createElement('mine');

                    mine.onclick = function ()
                    {


                        var row = $(this).attr("row");
                        var column = $(this).attr("column");
                        var pos = new Coordinate(row, column);


                    };


                },

            });
        });

//endregion

//region game controls


        function doTurnDone(move) {

            $.ajax({
                data: "turnDone",
                url: gameURL,
                success: function (response) {
                    if (response.isSuccessful) {
                        $("#table").addClass('disabled').prop('disabled', true);
                    }
                }
            });
        }

        $(document).ready(function () {
            $("#sidePanel *").addClass('disabled').prop('disabled', true);
        });

//endregion

//region polling

        function checkIfGameStarted() {
            $.ajax({
                data: {
                    requestType: "checkGameStart",
                    roomid: currentRoomId,
                    organizer: Cookies.get(organizer)
                },
                url: gameURL,
                success: function (response) {
                    updatePlayerList(response.value);

                    if (response.key) { //if started
                        showMessage("BattleShip", "Game started");
                        $("#sidePanel *").removeClass('disabled').prop('disabled', false);
                        blinkTitleWithMessage("Game started");
                        clearInterval(checkGameStartInterval);
                        updateDetailsInterval = setInterval(updateDetails, refreshRate);
                        ajaxUpdateBoardInterval = setInterval(ajaxUpdateBoard, refreshRate);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    if (textStatus === "timeout") {
                        showMessage("Timeout", "No connection", true);
                    }
                    else if (XMLHttpRequest.readyState == 0) {
                        showMessage("Error", "Lost connection with server", true);
                    }
                },
                timeout: 10000
            });
        }

//activate the timer calls after the page is loaded
        $(document).ready(function () {
            //The users list is refreshed automatically
            checkGameStartInterval = setInterval(checkIfGameStarted, refreshRate);
        });

        function updateDetails() {  //TODO: replay mode like moran's project
            $.ajax({
                data: {
                    requestType: "gameDetails",
                    roomid: currentRoomId,
                    organizer: Cookies.get(organizer)
                },
                url: gameURL,
                success: function (response) {
                    $("#username").text("Username: " + Cookies.get(organizer));
                    $("#roomid").text("Room ID: " + Cookies.get(roomid));
                    $("#currPlayer").text("Current Player: " + response.currentPlayerName);
                    $("#gameType").text("Game Type: " + response.GameType);

                    updatePlayerList(response.playerList);

                    if (response.isGameOver) { //if game over
                        handleGameOver();
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    if (textStatus === "timeout") {
                        showMessage("Timeout", "No connection", true);
                    }
                    else if (XMLHttpRequest.readyState == 0) {
                        showMessage("Error", "Lost connection with server", true);
                    }
                },
                timeout: 10000
            });
        }


        function updatePlayerList(playerList) {
            $("#userslist").empty();

            $.each(playerList || [], function (index, element) {

                $('<tr>' +
                    '<td>' + element.key + '</td>' +
                    '<td>' + element.value + '</td>' +
                    '</tr>').appendTo($("#userslist"));
            });
        }

//endregion

//region end game

        function handleGameOver() {
            $("#sidePanel *").addClass('disabled').prop('disabled', true);
            clearInterval(updateDetailsInterval);
            clearInterval(ajaxUpdateBoardInterval);
            $.ajax({
                data: {
                    requestType: "systemMessage",
                    roomid: currentRoomId,
                    organizer: Cookies.get(organizer)
                },
                url: gameURL,
                success: function (response) {
                    showMessage("Game Over", response);
                    blinkTitleWithMessage("Game Over");
                    isGameOver = true;

                    eventFire(document.getElementById('leaveRoom'), 'click');

                }
            });
        }

    function eventFire(el, etype)
    {
        setTimeout(function ()
        {
            if (el.fireEvent) {
                el.fireEvent('on' + etype);
            } else {
                var evObj = document.createEvent('Events');
                evObj.initEvent(etype, true, false);
                el.dispatchEvent(evObj);
            }

        }, 5000);


    }

    function sleep(milliseconds) {
        var start = new Date().getTime();
        for (var i = 0; i < 1e7; i++) {
            if ((new Date().getTime() - start) > milliseconds){
                break;
            }
        }
    }

        $(document).on("click", "#leaveRoom", function (e) {
            $.ajax({
                data: {
                    requestType: "leaveRoom",
                    roomid: currentRoomId,
                    organizer: Cookies.get(organizer)
                },
                url: gameURL,
                success: function (response) {
                    if (typeof response.redirect !== "undefined") {
                        Cookies.remove(roomid);
                        document.location.href = response.redirect;
                    }
                }
            });
        });


//endregion

//region ui related
    function showMessage(title, message, isError) {

        if (isError) {
            $('.modal-header').css('background-color', '#ff4444');
        }
        else {
            $('.modal-header').css('background-color', '#00C851');
        }

        $('#modalTitle').text(title);
        $('#modalMessage').text(message);

        if ($('#messageModal').is(':hidden')) {
            $('#messageModal').modal('show');
        }
    }


    function showMessageWithSleep(title, message, isError) {

        setTimeout(function ()
        {
            if (isError) {
                $('.modal-header').css('background-color', '#ff4444');
            }
            else {
                $('.modal-header').css('background-color', '#00C851');
            }

            $('#modalTitle').text(title);
            $('#modalMessage').text(message);

            if ($('#messageModal').is(':hidden')) {
                $('#messageModal').modal('show');
            }

        }, 7000);
    }


    function blinkTitleWithMessage(message) {
        var oldTitle = document.title;
        var msg = message;

        var blink = function () {
            document.title = document.title == msg ? ' ' : msg;
        };
        var clear = function () {
            clearInterval(titleIdInterval);
            document.title = oldTitle;
            window.onmousemove = null;
            titleIdInterval = null;
        };
        clearInterval(titleIdInterval);
        titleIdInterval = setInterval(blink, 1000);
        window.onmousemove = clear;
    }

//endregion



}());