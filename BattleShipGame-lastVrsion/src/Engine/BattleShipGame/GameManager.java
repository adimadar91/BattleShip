package Engine.BattleShipGame;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.util.Pair;
import jaxb.schema.generated.BattleShipGame;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameManager {
    public final int NUM_OF_PLAYERS = 2;
    public final String BASIC = "BASIC";
    public final String ADVANCE = "ADVANCE";


    private String systemMessage;
    public StringProperty CurrPlayerName = new SimpleStringProperty();

    private boolean Option2Selected = false;
    public IntegerProperty counter = new SimpleIntegerProperty(0);
    public IntegerProperty turns = new SimpleIntegerProperty();
    public IntegerProperty CurrPlayerMinesAmount = new SimpleIntegerProperty();
    public int m_BoardSize;
    private String GameType;
    public Player[] Players = new Player[2];
    private BattleShipGame bsGame;
    private LocalDateTime m_StartTime;
    private LocalDateTime startTurn;
    public Replay replay;
    public CurrentMove currentMove;
    private RoomInfo roomInfo = new RoomInfo();
    private boolean gameRunningProperty = false;
    private boolean gameOver = false;


    public GameManager(BattleShipGame bsGame) {
        this.bsGame = bsGame;
        //roomInfo.setGameTitle(bsGame.gameTitle());
        roomInfo.setGameType(bsGame.getGameType());
        GameType = bsGame.getGameType();
        roomInfo.setBoardSize(bsGame.getBoardSize());

    }

    public List<Pair<String, String>> makePlayerList() {
        List<Pair<String, String>> retValue = new ArrayList<Pair<String, String>>();
        if(Players[0] != null)
            retValue.add(new Pair<>(Players[0].NAME, Players[0].getM_stat().m_Score.getValue().toString()));
        if(Players[1] != null)
            retValue.add(new Pair<>(Players[1].NAME, Players[1].getM_stat().m_Score.getValue().toString()));
        return retValue;
    }

    public boolean compareUserToCurrentPlayer(String username){
        return username.equals(Players[CurrPlayer()].NAME);
    }

    public RoomInfo getRoomInfo() {
        return roomInfo;
    }

    private int CurrPlayer() {
        return counter.getValue() % NUM_OF_PLAYERS;
    }

    private int OpponentPlayer() {
        return (counter.getValue() % NUM_OF_PLAYERS + 1) % NUM_OF_PLAYERS;
    }

    public boolean MinesOption(int row, int col) {
        if (squareIsValidForMine(row, col) && Players[CurrPlayer()].get_MinesCounter() > 0) {
            Players[CurrPlayer()].GetMyBoard().AddMine(row, col);
            Players[CurrPlayer()].set_MinesCounter();

            counter.set(counter.getValue() + 1);
            CurrPlayerMinesAmount.set(Players[CurrPlayer()].get_MinesCounter());
            CurrPlayerName.setValue(Players[CurrPlayer()].NAME);
            turns.set(turns.getValue() + 1);
            CurrPlayerMinesAmount.set(Players[CurrPlayer()].get_MinesCounter());
            return true;
        }
        return false;
    }


    public boolean checkUniqueUser(String username) { //TODO test this works

        if (Players[0] != null) {
            if (Players[0].NAME.equals(username))
                return false;
        }

        if (Players[1] != null) {
            if (Players[1].NAME.equals(username))
                return false;
        }

        return true;
    }


    public synchronized boolean addPlayer(String organizer)
    {
        m_BoardSize = bsGame.getBoardSize();


        if (roomInfo.getOnlinePlayers() < roomInfo.getTotalPlayers() && !gameRunningProperty) {
            Player player = new Player(organizer, roomInfo.getBoardSize(), bsGame);
            player.GetMyBoard().SetBoard(bsGame.getBoards().getBoard().get(0).getShip(), bsGame.getShipTypes().getShipType());

            if (roomInfo.getGameType().equals(ADVANCE)) {
                player.initMinesCounter(roomInfo.getAmountOfMines());
            } else {
                player.initMinesCounter(2);
            }
            if (roomInfo.getOnlinePlayers() == 0) {
                Players[0] = player;
            } else {
                Players[1] = player;
            }

            updateShipsAmount(player);
            roomInfo.increaseOnlinePlayers();

            if (roomInfo.getOnlinePlayers() == 1) {
                CurrPlayerName.setValue(Players[0].NAME);
                CurrPlayerMinesAmount.setValue(Players[0].get_MinesCounter());

            }
            if (roomInfo.getOnlinePlayers() == roomInfo.getTotalPlayers()) {
                gameRunningProperty = true;
                gameOver = false;
                startDurationForTheFirstTime();
            }

            return true;
        }

        return false;
    }

    public boolean getGameRunning() {
        return gameRunningProperty;
    }


    public boolean squareIsValidForMine(int row, int col) {
        if (checkIfSquareIsShip(row, col, Players[CurrPlayer()].GetMyBoard().getM_squre())) {
            //MyApp.ShowMsg("Error! your chosen square is part of ship");
            return false;
        }
        if (checkIfSquareIsMine(row, col, Players[CurrPlayer()].GetMyBoard().getM_squre())) {
            //MyApp.ShowMsg("Error! your chosen square has mine already");
            return false;
        }
        if (!checkBordersOfSquare(row, col, Players[CurrPlayer()].GetMyBoard().getM_squre())) {
            //MyApp.ShowMsg("Error! there are ships near this square");
            return false;
        }

        return true;
    }

    private boolean checkIfSquareIsMine(int j, int k, Square[][] matrix) {
        if (matrix[j][k].GetSign() != Board.Signs.MINE && matrix[j][k].GetSign() != Board.Signs.HIT_MINE)
            return false;
        return true;
    }

    private boolean checkIfSquareIsShip(int j, int k, Square[][] matrix) {
        if (matrix[j][k].GetSign() != Board.Signs.SHIPSIGN)
            return false;
        return true;
    }

    private boolean checkBordersOfSquare(int j, int k, Square[][] matrix) {
        int size = m_BoardSize;

        if ((k + 1 < size) && (matrix[j][k + 1].GetSign() == Board.Signs.SHIPSIGN))
            return false;

        if ((j + 1 < size) && (matrix[j + 1][k].GetSign() == Board.Signs.SHIPSIGN))
            return false;

        if ((j + 1 < size) && (k + 1 < size) && (matrix[j + 1][k + 1].GetSign() == Board.Signs.SHIPSIGN))
            return false;

        if ((j - 1 >= 0) && (k - 1 >= 0) && (matrix[j - 1][k - 1].GetSign() == Board.Signs.SHIPSIGN))
            return false;

        if ((j + 1 < size) && (k - 1 >= 0) && (matrix[j + 1][k - 1].GetSign() == Board.Signs.SHIPSIGN))
            return false;

        if ((j - 1 >= 0) && (k + 1 < size) && (matrix[j - 1][k + 1].GetSign() == Board.Signs.SHIPSIGN))
            return false;

        if ((j - 1 >= 0) && (matrix[j - 1][k].GetSign() == Board.Signs.SHIPSIGN))
            return false;

        if ((k - 1 >= 0) && (matrix[j][k - 1].GetSign() == Board.Signs.SHIPSIGN))
            return false;

        return true;
    }

    private boolean checkSpecificSquare(int j, int k, int size, int[][] matrix) {
        if ((k + 1 < size) && (matrix[j][k] != matrix[j][k + 1] && matrix[j][k + 1] != 0))
            return false;

        if ((j + 1 < size) && (matrix[j][k] != matrix[j + 1][k] && matrix[j + 1][k] != 0))
            return false;

        if ((j + 1 < size) && (k + 1 < size) && (matrix[j][k] != matrix[j + 1][k + 1] && matrix[j + 1][k + 1] != 0))
            return false;

        if ((j - 1 >= 0) && (k - 1 >= 0) && (matrix[j][k] != matrix[j - 1][k - 1] && matrix[j - 1][k - 1] != 0))
            return false;

        if ((j + 1 < size) && (k - 1 >= 0) && (matrix[j][k] != matrix[j + 1][k - 1] && matrix[j + 1][k - 1] != 0))
            return false;

        if ((j - 1 >= 0) && (k + 1 < size) && (matrix[j][k] != matrix[j - 1][k + 1] && matrix[j - 1][k + 1] != 0))
            return false;

        if ((j - 1 >= 0) && (matrix[j][k] != matrix[j - 1][k] && matrix[j - 1][k] != 0))
            return false;

        if ((k - 1 >= 0) && (matrix[j][k] != matrix[j][k - 1] && matrix[j][k - 1] != 0))
            return false;

        return true;
    }


    public void setOrganizer(String organizer) {
        roomInfo.setOrganizer(organizer);
    }


    private boolean squareIsValid(String userSelection) {
        char[] charArray = userSelection.toCharArray();

        if (charArray.length != Square.SQUARE_SIZE) {
            // UIManager.ShowMsg("wrong input, please enter a square, for example: B3: ");
            return false;
        }
        if (charArray[0] < 'A' || charArray[0] > 'A' + m_BoardSize - 1) {
            //UIManager.ShowMsg("this Square doesn't exist, please try again.");
            return false;
        }
        if (charArray[1] < '1' || charArray[1] > '1' + m_BoardSize - 1) {
            // UIManager.ShowMsg("this Square doesn't exist, please try again.");
            return false;
        }

        return true;

    }


    public void startGameOptionFX() {
        m_StartTime = LocalDateTime.now();
        replay = new Replay();
        initForStartGame();
        //addCurrentStateToReplay(0,-1,-1);
    }

    public void addCurrentStateToReplay(int i, int row, int col) {
        replay.m_index = i;
        replay.m_maxIndex = i;
        currentMove = new CurrentMove();
        currentMove.player1MyBoard = Players[0].GetMyBoard();
        currentMove.player1Stat = Players[0].getM_stat();
        currentMove.player1TrackingBoard = Players[0].GetTrackingBoard();

        currentMove.player2MyBoard = Players[1].GetMyBoard();
        currentMove.player2Stat = Players[1].getM_stat();
        currentMove.player2TrackingBoard = Players[1].GetTrackingBoard();

        currentMove.currentPlayerName = Players[OpponentPlayer()].NAME;

        currentMove.row = row;
        currentMove.col = col;

        replay.moves.add(currentMove);
    }

    public void startDurationForTheFirstTime() {
        startTurn = LocalDateTime.now();
    }

    public StringBuilder makeMoveOptionFX(int row, int col)
    {
        StringBuilder res = new StringBuilder("");
        boolean increaseCounter = false;
        Duration duration = Duration.between(startTurn, LocalDateTime.now());

        char attackingResult = setTrackingBoard(row, col);

        if (attackingResult == Board.Signs.SHIPSIGN)
        {
            setOpponentBoard(row, col, Board.Signs.HIT);
            if (isThisMoveKilledShip(row, col))
            {
                doWhenKilledShip(row, col);
                res.append("you killed a ship.\n");
            }
            if (GameType.equals(BASIC))
            {
                Players[CurrPlayer()].getM_stat().setM_ScoreForBasic();
            }

            res.append("Good!!! You have another shot\n");
        }
        else if (attackingResult == Board.Signs.EMPTY)
        {
            setOpponentBoard(row, col, Board.Signs.MISS);
            Players[CurrPlayer()].getM_stat().setM_MissCounter();
            increaseCounter = true;
            res.append("you missed turn now of other player\n");

        }
        else if (attackingResult == Board.Signs.MINE)
        {
            res.append("you attacked a mine\n");
            setOpponentBoard(row, col, Board.Signs.HIT_MINE);
            increaseCounter = attackMyself(row, col);
        }
        else // this square was attacked already
        {
            res.append("You already tried to attack this square, try again. \n");
        }

        boolean isFinish = checkIfFinishGame();
        Players[CurrPlayer()].getM_stat().setM_MovesCounter();

        if (attackingResult != Board.Signs.MINE)
            Players[CurrPlayer()].getM_stat().setM_Avg(duration.getSeconds());

        if (increaseCounter)
        {
            counter.set(counter.getValue() + 1);
            CurrPlayerName.setValue(Players[CurrPlayer()].NAME);
        }
        turns.set(turns.getValue() + 1);
        CurrPlayerMinesAmount.set(Players[CurrPlayer()].get_MinesCounter());

        //addCurrentStateToReplay(turns.getValue() - 1, row, col);

        startTurn = LocalDateTime.now();


        if (isFinish)
        {
            gameOver = true;
            systemMessage = String.format("Game over. %1s won. please wait... leaving the room. player1 stats: %2s    .player2 stats: %3s ", CurrPlayerName.getValue().toString(), Players[0].getM_stat().statInfoString(), Players[1].getM_stat().statInfoString());
        }

        return res;
    }

    private void doWhenKilledShip(int row, int col) {
        int score = 0;
        int x = 0, y = 0;

        OneLoop:
        for (Ship ship : Players[OpponentPlayer()].GetMyBoard().getShipsLst()) {
            for (Ship.ShipPosition p : ship.positions) {
                if (p.getX() == row + 1 && p.getY() == col + 1) {
                    x = ship.positions.get(0).getX();
                    y = ship.positions.get(0).getY();
                    break OneLoop;
                }
            }
        }

        TwoLoop:
        for (BattleShipGame.Boards.Board.Ship ship : bsGame.getBoards().getBoard().get(OpponentPlayer()).getShip()) {
            for (BattleShipGame.ShipTypes.ShipType type : bsGame.getShipTypes().getShipType()) {
                if (ship.getShipTypeId().equals(type.getId()) && ship.getPosition().getX() == x && ship.getPosition().getY() == y) {
                    if (bsGame.getGameType().equals("ADVANCE"))
                    {
                        score = type.getScore();
                    }
                    else
                    {
                        score = 0;
                    }
                    int update = Players[OpponentPlayer()].ShipsAmount.get(type.getId());
                    update = update - 1;
                    Players[OpponentPlayer()].ShipsAmount.put(type.getId(), update);
                    break TwoLoop;
                }
            }
        }
        Players[CurrPlayer()].getM_stat().setM_Score(score);
    }

    private boolean attackMyself(int row, int col) {
        counter.set(counter.getValue() + 1);
        CurrPlayerName.setValue(Players[CurrPlayer()].NAME);

        char attackingResult = setTrackingBoard(row, col);

        if (attackingResult == Board.Signs.SHIPSIGN) {
            setOpponentBoard(row, col, Board.Signs.HIT);
            if (isThisMoveKilledShip(row, col)) {
                doWhenKilledShip(row, col);
            }
            if (GameType.equals(BASIC))
            {
                Players[CurrPlayer()].getM_stat().setM_ScoreForBasic();
            }
        } else if (attackingResult == Board.Signs.EMPTY) {
            setOpponentBoard(row, col, Board.Signs.MISS);
            Players[CurrPlayer()].getM_stat().setM_MissCounter();
        } else if (attackingResult == Board.Signs.MINE) {
            setOpponentBoard(row, col, Board.Signs.HIT_MINE);
        }

        return false;
    }

    private boolean isThisMoveKilledShip(int row, int col) {
        for (Ship ship : Players[OpponentPlayer()].GetMyBoard().getShipsLst()) {
            for (Ship.ShipPosition p : ship.positions) {
                if (p.getX() == row + 1 && p.getY() == col + 1) {
                    return !(ship.isAlive());
                }
            }
        }
        return false;
    }

    public void endGameOption() {
        //MyApp.ShowMsg("you choose to end the game. you can now start the game again or load new Xml file and play new game");
        counter.set(counter.getValue() + 1);
        CurrPlayerName.setValue(Players[CurrPlayer()].NAME);
        handleFinishGame();
    }


    public void handleFinishGameFX() {
        //MyApp.ShowMsg(Players[CurrPlayer()].NAME + " won the game " + Players[OpponentPlayer()].NAME + " lose the game");
        initForFinish();
    }

    private void handleFinishGame() {
        initForFinish();
    }

    private void initForFinish() {
        counter.set(0);
        turns.set(0);
    }

    private boolean checkIfFinishGame() {
        for (int i = 0; i < m_BoardSize; i++) {
            for (int j = 0; j < m_BoardSize; j++) {
                if (Players[OpponentPlayer()].GetMyBoard().getM_squre()[i][j].GetSign() == Board.Signs.SHIPSIGN)
                    return false;
            }
        }
        return true;
    }

    private boolean CheckIfAllShipsDead(Board b) // this method will be used in ex02
    {
        for (Ship s : b.getShipsLst()) {
            if (s.isAlive() == true)
                return false;
        }

        return true;
    }

    private void setOpponentBoard(int row, int col, char sign) {
        Players[OpponentPlayer()].GetMyBoard().getM_squre()[row][col].SetSign(sign);
        for (Ship ship : Players[OpponentPlayer()].GetMyBoard().getShipsLst()) {
            for (Ship.ShipPosition p : ship.positions) {
                if (p.getX() == row + 1 && p.getY() == col + 1) {
                    p.setHit(true);
                    ship.setAliveIfNeeded();
                }
            }
        }
    }

    private char setTrackingBoard(int row, int col) {
        int x = OpponentPlayer();
        int y = CurrPlayer();

        if (Players[OpponentPlayer()].GetMyBoard().getM_squre()[row][col].GetSign() == Board.Signs.SHIPSIGN) {
            Players[CurrPlayer()].GetTrackingBoard().getM_squre()[row][col].SetSign(Board.Signs.HIT);
            return Board.Signs.SHIPSIGN;
        } else if (Players[OpponentPlayer()].GetMyBoard().getM_squre()[row][col].GetSign() == Board.Signs.EMPTY) {
            Players[CurrPlayer()].GetTrackingBoard().getM_squre()[row][col].SetSign(Board.Signs.MISS);
            return Board.Signs.EMPTY;
        } else if (Players[OpponentPlayer()].GetMyBoard().getM_squre()[row][col].GetSign() == Board.Signs.MINE) {
            Players[CurrPlayer()].GetTrackingBoard().getM_squre()[row][col].SetSign(Board.Signs.HIT_MINE);
            return Board.Signs.MINE;
        } else {
            return Board.Signs.MISS; // as default, we can also return HIT, it doesn't matter.
        }
    }

    public String TimeFromStart() {
        LocalDateTime Now = LocalDateTime.now();
        LocalDateTime StartTime = LocalDateTime.from(m_StartTime);

        Duration duration = Duration.between(StartTime, Now);

        return formatDuration(duration);
    }

    private static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    private void initForStartGame() {
        turns.set(0);
        GameType = bsGame.getGameType();
        m_BoardSize = bsGame.getBoardSize();
        Players = new Player[NUM_OF_PLAYERS];
        Players[0] = new Player(Player.PlayerNames.PLAYER1_NAME, m_BoardSize, bsGame);
        Players[1] = new Player(Player.PlayerNames.PLAYER2_NAME, m_BoardSize, bsGame);
        Players[0].GetMyBoard().SetBoard(bsGame.getBoards().getBoard().get(0).getShip(), bsGame.getShipTypes().getShipType());
        Players[1].GetMyBoard().SetBoard(bsGame.getBoards().getBoard().get(1).getShip(), bsGame.getShipTypes().getShipType());
        if (GameType.equals(ADVANCE)) {

            Players[0].initMinesCounter(bsGame.getMine().getAmount());
            Players[1].initMinesCounter(bsGame.getMine().getAmount());
        } else {
            Players[0].initMinesCounter(0);
            Players[1].initMinesCounter(0);
        }
        //    updateShipsAmount();
        CurrPlayerName.setValue(Players[0].NAME);
        CurrPlayerMinesAmount.setValue(Players[0].get_MinesCounter());
    }

    private void updateShipsAmount(Player player) {
        for (BattleShipGame.ShipTypes.ShipType type : bsGame.getShipTypes().getShipType()) {
            player.ShipsAmount.put(type.getId(), type.getAmount());
        }
    }


    public String GetCurrentSignInMyBoard(int row, int col) {
        return String.valueOf(Players[CurrPlayer()].GetMyBoard().getM_squre()[row][col].GetSign());
    }

    public String GetCurrentSignInTrackingBoard(int row, int col) {
        return String.valueOf(Players[CurrPlayer()].GetTrackingBoard().getM_squre()[row][col].GetSign());
    }

    public String GetCurrentSignInTrackingBoardForReplay(int row, int col) {
        if (Players[0].NAME.equals(replay.moves.get(replay.m_index).currentPlayerName))
            return String.valueOf(replay.moves.get(replay.m_index).player1TrackingBoard.getM_squre()[row][col].GetSign());
        else
            return String.valueOf(replay.moves.get(replay.m_index).player2TrackingBoard.getM_squre()[row][col].GetSign());

    }

    public String GetCurrentSignInMyBoardForReplay(int row, int col) {
        if (Players[0].NAME.equals(replay.moves.get(replay.m_index).currentPlayerName))
            return String.valueOf(replay.moves.get(replay.m_index).player1MyBoard.getM_squre()[row][col].GetSign());
        else
            return String.valueOf(replay.moves.get(replay.m_index).player2MyBoard.getM_squre()[row][col].GetSign());
    }

    public String getShipsState() {
        StringBuilder res = new StringBuilder();
        res.append("Player1: ");
        res.append("\n");

        for (String key : Players[0].ShipsAmount.keySet()) {
            res.append(key + ": ");
            res.append(Players[0].ShipsAmount.get(key));
            res.append("\n");

        }
        res.append("\n");
        res.append("Player2: ");
        res.append("\n");

        for (String key : Players[1].ShipsAmount.keySet()) {
            res.append(key + ": ");
            res.append(Players[1].ShipsAmount.get(key));
            res.append("\n");
        }
        res.append("\n");

        return res.toString();
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public void resetGame()
    {
        initForFinish();
    }


    private void removePlayerFromList(String username)
    {
        roomInfo.decreaseOnlinePlayers();
        if (Players[0] != null && username.equals(Players[0].NAME))
        {
            Players[0] = null;
        }
        else
        {
            Players[1] = null;
        }
    }





    public synchronized void removePlayer(String username)
    {
        if(!gameRunningProperty)
        {
            removePlayerFromList(username);
        }
        else
        {
            if(Players[0] != null || Players[1] != null)
            {
                gameOver = true;
                gameRunningProperty = false;
                removePlayerFromList(username);
                if (Players[0] == null)
                {
                    systemMessage = String.format("Game over. %1s has left the room. you can leave the room %2s", CurrPlayerName.getValue(), Players[1].getM_statStr());
                }
                else if(Players[1] == null)
                {
                    systemMessage = String.format("Game over. %1s has left the room. you can leave the room %2s", CurrPlayerName.getValue(), Players[0].getM_statStr());

                }
                resetGame();
            }
        }
    }

    public void setGameTitle(String gameTitle) {
        roomInfo.setGameTitle(gameTitle);
    }

    public class GameDetails {
        //holds info to be sent with polling from game servlet
        //simple object for gsoning to client

        private boolean isActivePlayer = false;
        private String currentPlayerName;
        private List<Pair<String, String>> playerList; //player type, name, score of player
        private boolean isGameOver;
        private String GameType;

        GameDetails(GameManager gameManager, String username)
        {
            if (gameManager.CurrPlayerName.getValue().equals(username))
            {
                isActivePlayer = true;
            }

            currentPlayerName = gameManager.CurrPlayerName.getValue();
            playerList = gameManager.makePlayerList();
            isGameOver = gameManager.gameOver;
            GameType = gameManager.bsGame.getGameType();
        }
    }

    public GameDetails getGameDetails(String username)
    {
        return new GameDetails(this, username);
    }
}