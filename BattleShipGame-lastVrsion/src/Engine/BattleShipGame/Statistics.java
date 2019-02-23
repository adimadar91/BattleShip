package Engine.BattleShipGame;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

public class Statistics
{
    public IntegerProperty m_MovesCounter = new SimpleIntegerProperty(0);
    public IntegerProperty m_Score = new SimpleIntegerProperty(0);
    public IntegerProperty m_MissCounter = new SimpleIntegerProperty(0);
    public LongProperty m_Avg = new SimpleLongProperty(0);

    public long getM_Avg() {
        return m_Avg.get();
    }

    public void setM_Avg(long duration)
    {
        m_Avg.set((m_Avg.get() * (m_MovesCounter.get()-1) + duration)/ (m_MovesCounter.get())) ;
    }

    public int getM_MissCounter() {
        return m_MissCounter.get();
    }

    public void setM_MissCounter() {
        this.m_MissCounter.set(m_MissCounter.get()+1);
    }

    public int getM_Score() {
        return m_Score.get();
    }

    public void setM_Score(int score)
    {
        this.m_Score.set(m_Score.get()+ score);
    }

    public int getM_MovesCounter()
    {
        return m_MovesCounter.get();
    }

    public void setM_MovesCounter() {
        this.m_MovesCounter.set(m_MovesCounter.get()+1);
    }

    public void setM_ScoreForBasic() {
        this.m_Score.set(m_Score.get()+ 1);
    }

    public String statInfoString()
    {
        StringBuilder res = new StringBuilder("");
        res.append(System.getProperty("line.separator"));
        res.append("Score: ");
        res.append(m_Score.intValue());
        res.append(System.getProperty("line.separator"));
        res.append(", Avg turn time: ");
        res.append(m_Avg.longValue());
        res.append(System.getProperty("line.separator"));
        res.append(", Moves: ");
        res.append(m_MovesCounter.intValue());
        res.append(System.getProperty("line.separator"));
        res.append(", Misses: ");
        res.append(m_MissCounter.intValue());
        res.append(System.getProperty("line.separator"));


        return res.toString();
    }
}
