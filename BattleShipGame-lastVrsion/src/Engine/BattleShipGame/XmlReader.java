package Engine.BattleShipGame;

import javafx.concurrent.Task;
import javafx.util.Pair;
import jaxb.schema.generated.BattleShipGame;

import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;

public class XmlReader
{
    private static final String JAXB_XML_GAME_PACKAGE_NAME = "jaxb.schema.generated";
    private final int NUM_OF_PLAYERS = 2;
    private final String BASIC = "BASIC";
    private final String ADVANCE  = "ADVANCE";
    private BattleShipGame bsGame;

    private BattleShipGame deserializeFrom(InputStream in) throws JAXBException
    {
        JAXBContext context = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (BattleShipGame) unmarshaller.unmarshal(in);
    }

    public XmlReader() { }


    public BattleShipGame loadXML(InputStream inputStream) throws Exception {

        try
        {
            bsGame = deserializeFrom(inputStream);

        } catch (JAXBException e) {
            throw new Exception("Bad XML File");
        }

        String res = checkXml();
        if(!res.equals("Valid"))
        {
            throw new Exception(res);
        }
        return bsGame;
    }

    private String checkXml()
    {
        if(bsGame.getBoardSize()< 5 || bsGame.getBoardSize() > 20)
           return "Error! Board size is invalid";

        else if(!checkGameTypeIsValid())
            return "Error! gametype is invalid";

        else if(!checkShipsDirections())
           return "Error! this direction is not supported in the specific gametype";

        else if(!amountOfShipsIsValid())
            return "Error! amount of ships is invalid";

        else if(shipsLeakingOutOfBoard())
            return "Error! ships leaking out of board";

        else if(isSameSquare())
            return "Error! overlapping ships";

        else if(!noShipNearMe())
            return "Error! there are ships near each other";

        else
            return "Valid";

    }


    private boolean isSameSquare()
    {
        boolean res = false;
        int size = bsGame.getBoardSize();
        int[][] matrixCounters = new int[size][size];
        initMatrix(matrixCounters, size);

        for (int i = 0; i < NUM_OF_PLAYERS ; i++)
        {
            for (BattleShipGame.Boards.Board.Ship ship: bsGame.getBoards().getBoard().get(i).getShip())
            {
                for(BattleShipGame.ShipTypes.ShipType type: bsGame.getShipTypes().getShipType())
                {
                    if(ship.getShipTypeId().equals(type.getId()))
                    {
                        for (int j = 0; j < type.getLength() ; j++)
                        {
                            if(ship.getDirection().equals("ROW"))
                                matrixCounters[ship.getPosition().getX() - 1][ship.getPosition().getY() - 1 + j]++;

                            else if(ship.getDirection().equals("COLUMN"))
                                matrixCounters[ship.getPosition().getX() - 1 + j][ship.getPosition().getY() - 1]++;

                            else if(ship.getDirection().equals("UP_RIGHT"))
                            {
                                matrixCounters[ship.getPosition().getX() - 1 + j][ship.getPosition().getY() - 1]++;
                                if(j!=0)
                                    matrixCounters[ship.getPosition().getX() - 1][ship.getPosition().getY() - 1 + j]++;
                            }
                            else if(ship.getDirection().equals("RIGHT_UP"))
                            {
                                matrixCounters[ship.getPosition().getX() - 1 - j][ship.getPosition().getY() - 1]++;
                                if(j!=0)
                                    matrixCounters[ship.getPosition().getX() - 1][ship.getPosition().getY() - 1 - j]++;
                            }
                            else if(ship.getDirection().equals("DOWN_RIGHT"))
                            {
                                matrixCounters[ship.getPosition().getX() - 1 + j][ship.getPosition().getY() - 1]++;
                                if(j!=0)
                                    matrixCounters[ship.getPosition().getX() - 1][ship.getPosition().getY() - 1 - j]++;
                            }
                            else if(ship.getDirection().equals("RIGHT_DOWN"))
                            {
                                matrixCounters[ship.getPosition().getX() - 1 - j][ship.getPosition().getY() - 1]++;
                                if(j!=0)
                                    matrixCounters[ship.getPosition().getX() - 1][ship.getPosition().getY() - 1 + j]++;
                            }
                        }
                    }
                }
            }
            for (int j = 0; j < size ; j++)
            {
                for (int k = 0; k < size ; k++)
                {
                    if(matrixCounters[j][k] > 1)
                        res = true;
                }
            }
            initMatrix(matrixCounters,size);
        }
        return res;
    }

    private boolean checkShipsDirections()
    {
        for (int i = 0; i < NUM_OF_PLAYERS ; i++)
        {
            for (BattleShipGame.Boards.Board.Ship ship : bsGame.getBoards().getBoard().get(i).getShip())
            {
                for (BattleShipGame.ShipTypes.ShipType type : bsGame.getShipTypes().getShipType())
                {
                    if (ship.getShipTypeId().equals(type.getId()))
                    {
                        if (!(ship.getDirection().equals("ROW") || ship.getDirection().equals("COLUMN")) && bsGame.getGameType().equals(BASIC))
                        {
                            return false;
                        }
                        if ((ship.getDirection().equals("UP_RIGHT") || ship.getDirection().equals("RIGHT_UP") || ship.getDirection().equals("RIGHT_DOWN") || ship.getDirection().equals("DOWN_RIGHT")) &&  !bsGame.getGameType().equals(ADVANCE))
                        {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean checkGameTypeIsValid()
    {
        if(bsGame.getGameType().equals(BASIC) || bsGame.getGameType().equals(ADVANCE))
            return true;

        else
            return false;
    }

    private boolean noShipNearMe()
    {
        int size = bsGame.getBoardSize();
        int[][] matrix = new int[size][size];
        int ShipID = 1;
        initMatrix(matrix, size);

        for (int i = 0; i < NUM_OF_PLAYERS ; i++)
        {
            for (BattleShipGame.Boards.Board.Ship ship: bsGame.getBoards().getBoard().get(i).getShip())
            {
                for(BattleShipGame.ShipTypes.ShipType type: bsGame.getShipTypes().getShipType())
                {
                    if(ship.getShipTypeId().equals(type.getId()))
                    {
                        for (int j = 0; j < type.getLength() ; j++)
                        {
                            if(ship.getDirection().equals("ROW"))
                                matrix[ship.getPosition().getX() - 1][ship.getPosition().getY() - 1 + j] = ShipID;

                            else if(ship.getDirection().equals("COLUMN"))
                                matrix[ship.getPosition().getX() - 1 + j][ship.getPosition().getY() - 1] = ShipID;

                            else if(ship.getDirection().equals("UP_RIGHT"))
                            {
                                matrix[ship.getPosition().getX() - 1 + j][ship.getPosition().getY() - 1]++;
                                if(j!=0)
                                    matrix[ship.getPosition().getX() - 1][ship.getPosition().getY() - 1 + j]++;
                            }
                            else if(ship.getDirection().equals("RIGHT_UP"))
                            {
                                matrix[ship.getPosition().getX() - 1 - j][ship.getPosition().getY() - 1]++;
                                if(j!=0)
                                    matrix[ship.getPosition().getX() - 1][ship.getPosition().getY() - 1 - j]++;
                            }
                            else if(ship.getDirection().equals("DOWN_RIGHT"))
                            {
                                matrix[ship.getPosition().getX() - 1 + j][ship.getPosition().getY() - 1]++;
                                if(j!=0)
                                    matrix[ship.getPosition().getX() - 1][ship.getPosition().getY() - 1 - j]++;
                            }
                            else if(ship.getDirection().equals("RIGHT_DOWN"))
                            {
                                matrix[ship.getPosition().getX() - 1 - j][ship.getPosition().getY() - 1]++;
                                if(j!=0)
                                    matrix[ship.getPosition().getX() - 1][ship.getPosition().getY() - 1 + j]++;
                            }
                        }
                    }
                }
                ShipID++;
            }

            for (int j = 0; j < size; j++)
            {
                for (int k = 0; k < size ; k++)
                {
                    if(matrix[j][k] != 0 && checkSpecificSquare(j,k,size,matrix) == false)
                        return false;
                }
            }

            ShipID = 1;
            initMatrix(matrix, size);
        }
        return true;
    }

    private boolean checkSpecificSquare(int j, int k, int size, int[][] matrix)
    {
        if((k+1<size) && (matrix[j][k] != matrix[j][k+1] && matrix[j][k+1] != 0))
            return false;

        if((j+1<size) && (matrix[j][k] != matrix[j+1][k] && matrix[j+1][k] != 0))
            return false;

        if((j+1<size) && (k+1<size) && (matrix[j][k] != matrix[j+1][k+1] && matrix[j+1][k+1] != 0))
            return false;

        if((j-1>=0) && (k-1>=0) && (matrix[j][k] != matrix[j-1][k-1] && matrix[j-1][k-1] != 0))
            return false;

        if((j+1<size) && (k-1>=0) && (matrix[j][k] != matrix[j+1][k-1] && matrix[j+1][k-1] != 0))
            return false;

        if((j-1>=0) && (k+1<size) && (matrix[j][k] != matrix[j-1][k+1] && matrix[j-1][k+1] != 0))
            return false;

        if((j-1>=0) && (matrix[j][k] != matrix[j-1][k] && matrix[j-1][k] != 0))
            return false;

        if((k-1>=0) && (matrix[j][k] != matrix[j][k-1] && matrix[j][k-1] != 0))
            return false;

        return true;
    }

    private void initMatrix(int[][] matrix, int size)
    {
        for (int i = 0; i < size ; i++)
        {
            for (int j = 0; j < size ; j++)
                matrix[i][j] = 0;
        }
    }

    private boolean shipsLeakingOutOfBoard()
    {
        for (BattleShipGame.ShipTypes.ShipType shipType : bsGame.getShipTypes().getShipType())
        {
            for (BattleShipGame.Boards.Board.Ship ship : bsGame.getBoards().getBoard().get(0).getShip())
            {
                if (ship.getShipTypeId().equals(shipType.getId()))
                {
                    if (isSpecificShipLeaking(ship.getPosition().getX(), ship.getPosition().getY(), ship.getDirection(), shipType.getLength()))
                        return true;
                }
            }
        }

        for (BattleShipGame.ShipTypes.ShipType shipType : bsGame.getShipTypes().getShipType())
        {
            for (BattleShipGame.Boards.Board.Ship ship : bsGame.getBoards().getBoard().get(1).getShip())
            {
                if (ship.getShipTypeId().equals(shipType.getId()))
                {
                    if (isSpecificShipLeaking(ship.getPosition().getX(), ship.getPosition().getY(), ship.getDirection(), shipType.getLength()))
                        return true;
                }
            }
        }

        return false;
    }

    private boolean isSpecificShipLeaking(int x, int y, String direction, int length)
    {
        if(direction.equals("ROW"))
        {
            if(y+length-1 > bsGame.getBoardSize())
                return true;
        }
        else if(direction.equals("COLUMN"))
        {
            if (x+length-1 > bsGame.getBoardSize())
                return true;
        }
        else if(direction.equals("UP_RIGHT"))
        {
            if (x+length-1 > bsGame.getBoardSize())
                return true;
            if (y+length-1 > bsGame.getBoardSize())
                return true;
        }
        else if(direction.equals("RIGHT_UP"))
        {
            if (x-length-1 > bsGame.getBoardSize())
                return true;
            if (y-length-1 > bsGame.getBoardSize())
                return true;
        }
        else if(direction.equals("DOWN_RIGHT"))
        {
            if (x+length-1 > bsGame.getBoardSize())
                return true;
            if (y-length-1 > bsGame.getBoardSize())
                return true;
        }
        else if(direction.equals("RIGHT_DOWN"))
        {
            if (x-length-1 > bsGame.getBoardSize())
                return true;
            if (y+length-1 > bsGame.getBoardSize())
                return true;
        }

        return false;
    }

    private boolean amountOfShipsIsValid()
    {
        int checkAmount = 0;

        for (BattleShipGame.ShipTypes.ShipType shipType : bsGame.getShipTypes().getShipType())
        {
            for (BattleShipGame.Boards.Board.Ship ship : bsGame.getBoards().getBoard().get(0).getShip())
            {
                if (ship.getShipTypeId().equals(shipType.getId()))
                    checkAmount++;
            }
            if (checkAmount != shipType.getAmount())
                return false;

            checkAmount = 0;

            for (BattleShipGame.Boards.Board.Ship ship : bsGame.getBoards().getBoard().get(1).getShip())
            {
                if (ship.getShipTypeId().equals(shipType.getId()))
                    checkAmount++;
            }
            if (checkAmount != shipType.getAmount())
                return false;

            checkAmount = 0;
        }

        return true;
    }

}
