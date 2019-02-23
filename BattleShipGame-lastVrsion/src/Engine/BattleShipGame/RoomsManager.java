package Engine.BattleShipGame;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by moran on 9/30/2016.
 */
@WebListener
public class RoomsManager implements ServletContextListener {

    private final Map<Integer, GameManager> games = new HashMap<>();
    private final List<SimplePlayer> onlinePlayers = new ArrayList<>();
    private final List<RoomInfo> roomList = new LinkedList<>(); //simplified game manager for converting to json
    private int count = 0;


    public boolean isPlayerExists(String name) {
        for (SimplePlayer player : onlinePlayers) {
            if(player.getName().equals(name))
                return true;
        }
        return false;
    }


    public Map<Integer, GameManager> getGames() {return games;}

    public void addPlayer(String name) {
        onlinePlayers.add(new SimplePlayer(name));
    }

    public synchronized void addGameManager(GameManager gameManager) {
        count++;
        games.put(count, gameManager);
        roomList.add(gameManager.getRoomInfo());
        gameManager.getRoomInfo().setRoomIdentifier(count);
    }

    public List<SimplePlayer> getPlayerList() { return onlinePlayers;}

    public List<RoomInfo> getRoomList() {
        return roomList;
    }

    public void removePlayer(String organizer) {
        Iterator<SimplePlayer> it = onlinePlayers.iterator();
        while (it.hasNext()){
            if(Objects.equals(it.next().getName(), organizer)){
                it.remove();
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().setAttribute("RoomsManager", this);
    }
}
