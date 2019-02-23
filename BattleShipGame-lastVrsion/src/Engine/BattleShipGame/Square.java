package Engine.BattleShipGame;

public class Square
{
    private int m_X;
    private int m_Y;
    private char m_sign;
    public static final int SQUARE_SIZE = 2;

    public void SetX(int i_x)
    {
        m_X = i_x;
    }

    public int GetX()
    {
        return m_X;
    }

    public void SetY(int i_y)
    {
        m_Y = i_y;
    }

    public int GetY()
    {
        return m_Y;
    }

    public void  SetSign(char i_sign){m_sign = i_sign;}

    public char GetSign(){return m_sign;}
}
