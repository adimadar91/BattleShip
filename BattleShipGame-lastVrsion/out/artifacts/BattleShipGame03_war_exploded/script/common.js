const organizer = "organizer";
const roomid = "roomid";
const playerType = "playerType";
const requestType = "requestType";
const roomsURL = "rooms";
const gameURL = "game";

function createBoards(myBoard,trackingBoard)
{
    var tableBoard = $("#myBoard");
    tableBoard.addClass("board");

    var tableTracking = $("#trackingBoard");
    tableTracking.addClass("board");

    printBoard(myBoard, tableBoard);
    printBoard(trackingBoard, tableTracking);
}

function printBoard(board, table)
{
    var theBoard = board.m_square;
    var size = theBoard.length;

    for (var row = 0; row < size; row++)
    {
        var tr = document.createElement('tr');

        for (var column = 0; column < size; column++)
        {
            var td = document.createElement('td');
            td.setAttribute("row", row);
            td.setAttribute("column", column);
            td.innerText = theBoard[row][column].m_sign;
            td.classList.add("waves-effect", "waves-light");
            tr.appendChild(td);
        }
        table.append(tr);
    }

}



