package Engine.BattleShipGame;

import java.util.ArrayList;
import java.util.List;

public class Ship
{
    public class ShipPosition
    {
        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public boolean isHit() {
            return isHit;
        }

        public void setHit(boolean hit) {
            isHit = hit;
        }

        private int x;
        private int y;
        private boolean isHit;
    }

    public ArrayList<ShipPosition> positions;
    private boolean isAlive;
    public String type;

    public void setAliveIfNeeded()
    {
        boolean flag = false;

        for (ShipPosition p : positions)
        {
            if (p.isHit == false)
            {
                isAlive = true;
                flag = true;
            }
        }
        if (flag == false)
            isAlive = false;
    }

    public Ship()
    {
        positions = new ArrayList<>();
    }


    public void AddPositionToList(int x, int y)
    {
        ShipPosition p = new ShipPosition();
        p.setX(x);
        p.setY(y);
        p.setHit(false);
        positions.add(p);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
