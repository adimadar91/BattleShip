package Engine.BattleShipGame;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import jaxb.schema.generated.BattleShipGame;

import java.util.Dictionary;
import java.util.Hashtable;

public class Player
{
    public String getM_statStr() {
       return m_stat.statInfoString();
    }

    public class PlayerNames
    {
        public static final String PLAYER1_NAME = "Player 1";
        public static final String PLAYER2_NAME = "Player 2";
    }

    public final String NAME;
    private Board m_MyBoard;
    private Board m_TrackingBoard;
    private Statistics m_stat;
    private IntegerProperty m_MinesCounter = new SimpleIntegerProperty();
    public Hashtable<String, Integer> ShipsAmount;

    public Player(String i_Name, int i_BoardSize, BattleShipGame bs)
    {
        NAME = i_Name;
        initPlayer(i_Name, i_BoardSize);
        if (bs.getGameType().equals("ADVANCE"))
        {
            m_MinesCounter.set(bs.getMine().getAmount());
        }
        else
        {
            m_MinesCounter.set(0);
        }
        ShipsAmount = new Hashtable<>();
    }

    private void initPlayer(String i_name, int i_boardSize)
    {
        m_MyBoard = new Board(i_boardSize);
        m_TrackingBoard = new Board(i_boardSize);
        m_stat = new Statistics();
    }

    public int get_MinesCounter()
    {
        return m_MinesCounter.getValue();
    }

    public void set_MinesCounter()
    {
        this.m_MinesCounter.set(m_MinesCounter.getValue()-1);
    }
    public void initMinesCounter(int x)
    {
        m_MinesCounter.setValue(x);
    }

    public Board GetMyBoard()
    {
        return m_MyBoard;
    }

    public Board GetTrackingBoard()
    {
        return m_TrackingBoard;
    }

    public Statistics getM_stat() {
        return m_stat;
    }
}
