package Engine.BattleShipGame;

import javafx.util.Pair;
import jaxb.schema.generated.BattleShipGame;

/**
 * Created by s on 04/10/2016.
 */
class RoomInfo {
    //simplified game manager info for converting to json

    private final int totalPlayers = 2;
    //private int spectators = 0;
    private int roomIdentifier;
    private String organizer;
    private String gameTitle;
    private String gameType;
    private int onlinePlayers = 0;
    private int amountOfMines;
    private int boardSize;

    //void addSpectator() { spectators++; }

    //void removeSpectator() {
        //spectators--;
   // }
    int getAmountOfMines() {
        return  amountOfMines;
    }



    void clearInfo() {
        onlinePlayers = 0;
        //spectators = 0;
    }

    void setRoomIdentifier(int roomIdentifier) { this.roomIdentifier = roomIdentifier;}

    String getOrganizer() {
        return organizer;
    }

    void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    String getGameType() {
        return gameType;
    }

    void setGameType(String gameType) {
        this.gameType = gameType;
    }


    String getGameTitle() {
        return gameTitle;
    }

    void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    int getTotalPlayers() {
        return totalPlayers;
    }



    int getOnlinePlayers() {
        return onlinePlayers;
    }

    void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    void increaseOnlinePlayers() {
        this.onlinePlayers++;
    }



    int getBoardSize()
    {
        return boardSize;
    }

    void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    void decreaseOnlinePlayers() {
        onlinePlayers--;
    }
}
