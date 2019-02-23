package Engine.BattleShipGame;

import jaxb.schema.generated.BattleShipGame;
import java.util.ArrayList;
import java.util.List;

public class Board
{
    public class Signs
    {
        public static final char EMPTY = ' ';
        public static final char HIT = 'X';
        public static final char MISS = 'O';
        public static final char SHIPSIGN = '@';
        public static final char MINE = '$';
        public static final char HIT_MINE = '!';

    }

    public Board(int n)
    {
        m_BoardSize = n;
        shipsLst = new ArrayList<>();
        initBoard();
    }

    private int m_BoardSize;
    private Square m_square[][];
    private ArrayList<Ship> shipsLst;

    public ArrayList<Ship> getShipsLst() {
        return shipsLst;
    }

    public void AddMine(int row, int col)
    {
        m_square[row][col].SetSign(Signs.MINE);
    }

    private void initBoard()
    {
        m_square = new Square[m_BoardSize][m_BoardSize];

        for (int i = 0; i <m_BoardSize ; i++)
        {
            for (int j = 0; j <m_BoardSize ; j++)
            {
                m_square[i][j] = new Square();
                m_square[i][j].SetSign(Signs.EMPTY);
                m_square[i][j].SetY(j+1);
                m_square[i][j].SetX(i+1);
            }
        }
    }

    public void SetBoard(List<BattleShipGame.Boards.Board.Ship> shiplst, List<BattleShipGame.ShipTypes.ShipType> shipTypelst)
    {
        for (BattleShipGame.Boards.Board.Ship ship : shiplst)
        {
            int x = ship.getPosition().getX();
            int y = ship.getPosition().getY();

            for (BattleShipGame.ShipTypes.ShipType st : shipTypelst)
            {
                if (ship.getShipTypeId().equals(st.getId()))
                {
                    for (int i = 0; i < st.getLength(); i++)
                    {
                        if(ship.getDirection().equals("ROW"))
                            m_square[x-1][y-1+i].SetSign(Signs.SHIPSIGN);
                        else if (ship.getDirection().equals("COLUMN"))
                            m_square[x-1+i][y-1].SetSign(Signs.SHIPSIGN);
                        else if (ship.getDirection().equals("DOWN_RIGHT"))
                        {
                            m_square[x-1+i][y-1].SetSign(Signs.SHIPSIGN);
                            m_square[x-1][y-1-i].SetSign(Signs.SHIPSIGN);
                        }
                        else if (ship.getDirection().equals("UP_RIGHT"))
                        {
                            m_square[x-1+i][y-1].SetSign(Signs.SHIPSIGN);
                            m_square[x-1][y-1+i].SetSign(Signs.SHIPSIGN);
                        }
                        else if (ship.getDirection().equals("RIGHT_UP"))
                        {
                            m_square[x-1-i][y-1].SetSign(Signs.SHIPSIGN);
                            m_square[x-1][y-1-i].SetSign(Signs.SHIPSIGN);
                        }
                        else if (ship.getDirection().equals("RIGHT_DOWN"))
                        {
                            m_square[x-1-i][y-1].SetSign(Signs.SHIPSIGN);
                            m_square[x-1][y-1+i].SetSign(Signs.SHIPSIGN);
                        }
                    }
                    addShipToList(ship, st);
                }
            }
        }
    }

    private void addShipToList(BattleShipGame.Boards.Board.Ship ship, BattleShipGame.ShipTypes.ShipType st)
    {
        int x = ship.getPosition().getX();
        int y = ship.getPosition().getY();
        Ship shipToAdd = new Ship();

        shipToAdd.setAlive(true);

        if(ship.getDirection().equals("ROW"))
        {
            for (int i = 0; i <st.getLength() ; i++)
                shipToAdd.AddPositionToList(x,y+i);
        }
        else if (ship.getDirection().equals("COLUMN"))
        {
            for (int i = 0; i <st.getLength() ; i++)
                shipToAdd.AddPositionToList(x+i, y);
        }
        else if (ship.getDirection().equals("UP_RIGHT"))
        {
            for (int i = 0; i <st.getLength() ; i++) {
                shipToAdd.AddPositionToList(x + i, y);
                if (i!=0)
                    shipToAdd.AddPositionToList(x, y+i);
            }
        }
        else if (ship.getDirection().equals("RIGHT_UP"))
        {
            for (int i = 0; i <st.getLength() ; i++) {
                shipToAdd.AddPositionToList(x - i, y);
                if (i!=0)
                    shipToAdd.AddPositionToList(x, y - i);
            }
        }
        else if (ship.getDirection().equals("DOWN_RIGHT"))
        {
            for (int i = 0; i <st.getLength() ; i++) {
                shipToAdd.AddPositionToList(x + i, y);
                if (i!=0)
                    shipToAdd.AddPositionToList(x, y - i);
            }
        }
        else if (ship.getDirection().equals("RIGHT_DOWN"))
        {
            for (int i = 0; i <st.getLength() ; i++) {
                shipToAdd.AddPositionToList(x - i, y);
                if (i!=0)
                    shipToAdd.AddPositionToList(x, y + i);
            }
        }

        shipToAdd.type = st.getId();


        shipsLst.add(shipToAdd);
    }

    public Square[][] getM_squre()
        {
        return m_square;
    }
}
