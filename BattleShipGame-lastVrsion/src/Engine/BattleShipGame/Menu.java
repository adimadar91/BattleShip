package Engine.BattleShipGame;

public class Menu
{
    public static final int numOfOptions = 6;
    public static final int ReadXML = 1;
    public static final int StartGame = 2;
    public static final int ShowState = 3;
    public static final int MakeMove = 4;
    public static final int ShowStatistics = 5;
    public static final int Exit = 6;

    private String[] m_options = new String[numOfOptions];

    public Menu()
    {
        initOptions();

    }

    public String[] getM_options() {
        return m_options;
    }

    private void initOptions()
    {
        m_options[0] = "1. Read XML file";
        m_options[1] = "2. Start game";
        m_options[2] = "3. Show game state";
        m_options[3] = "4. Make move";
        m_options[4] = "5. Show statistics";
        m_options[5] = "6. End game";
    }
}
