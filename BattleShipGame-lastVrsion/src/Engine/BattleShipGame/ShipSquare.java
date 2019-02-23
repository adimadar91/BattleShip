package Engine.BattleShipGame;

public class ShipSquare extends Square
{
    private boolean m_IsHit;

    public boolean GetIsHit()
    {
        return m_IsHit;
    }

    public void SetIsHot(boolean i_b) {m_IsHit = i_b;}
}
