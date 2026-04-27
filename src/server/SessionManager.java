package server;

import network.Packet;
import server.message.MessageDataBase;
import server.user.ClientSession;
import server.user.UserDataBase;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Server server;
    private final UserDataBase userDB;
    private final ConcurrentHashMap<String, ClientSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> addressToUsername = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastPing = new ConcurrentHashMap<>();

    public SessionManager(Server server, UserDataBase userDB) {
        this.server = server;
        this.userDB = userDB;
    }

    public void addSession(String username, ClientSession session) {
        sessions.put(username, session);
        String key = session.getIp() + ":" + session.getPort();
        addressToUsername.put(key, username);
        System.out.println("[ONLINE] " + username + " (" + key + ")");
    }

    public void removeSession(String username) {
        ClientSession session = sessions.remove(username);
        if (session != null) {
            String key = session.getIp() + ":" + session.getPort();
            addressToUsername.remove(key);
        }
        System.out.println("[OFFLINE] " + username);
    }

    public boolean isOnline(String username) {
        return sessions.containsKey(username);
    }

    public void sendToUser(String username, Packet packet) {
        ClientSession session = sessions.get(username);
        if (session != null) {
            server.getPeer().send(packet, session.getIp(), session.getPort());
        }
    }

    public String findUsernameByAddress(String ip, int port) {
        String key = ip + ":" + port;
        return addressToUsername.get(key);
    }

    public void broadcast(Packet packet, String excludeUser) {
        for (var entry : sessions.entrySet()) {
            if (!entry.getKey().equals(excludeUser)) {
                ClientSession session = entry.getValue();
                server.getPeer().send(packet, session.getIp(), session.getPort());
            }
        }
    }

    public void broadcastToAll(Packet packet) {
        for (ClientSession session : sessions.values()) {
            server.getPeer().send(packet, session.getIp(), session.getPort());
        }
    }

    public ArrayList<String> getAllUsernames() {
        return userDB.getAllUsernames();
    }

    public void updateActivity(String username) {
        lastPing.put(username, System.currentTimeMillis());
    }

    public void checkTimeouts() {
        long now = System.currentTimeMillis();
        for (String username : sessions.keySet()) {
            Long last = lastPing.get(username);
            if (last != null && now - last > 60_000) {
                removeSession(username);
            }
        }
    }

    public MessageDataBase getMessageDataBase() {
        return server.getMessageDataBase();
    }
}