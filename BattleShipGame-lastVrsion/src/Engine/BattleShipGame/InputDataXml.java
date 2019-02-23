package Engine.BattleShipGame;

import jaxb.schema.generated.BattleShipGame;

import java.util.ArrayList;

public class InputDataXml
{
    private String gameType;
    private int boardSize;
    private ArrayList<Ship> shipTypes;


    private class Ship
    {
        private String id;
        private String Category;
        private int amount;
        private int length;
        private int score;
    }
}
