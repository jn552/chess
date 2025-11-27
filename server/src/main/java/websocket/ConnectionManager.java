package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    public void add(Session session, Integer gameID) {
        connections.put(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(int gameID, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        for (Map.Entry<Session, Integer> pair : connections.entrySet()) {
            if (pair.getKey().isOpen() && pair.getValue().equals(gameID)) {
                pair.getKey().getRemote().sendString(msg);
            }
        }
    }

}
