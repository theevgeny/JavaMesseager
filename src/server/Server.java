package server;

import network.Packet;
import network.UDPPeer;
import network.protocol.ConnectionBreakPacket;
import network.protocol.UserListPacket;
import network.protocol.UserListRequestPacket;
import server.handler.*;
import server.message.MessageDataBase;
import server.user.UserDataBase;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final UDPPeer peer;
    private final SessionManager sessionManager;
    private final UserDataBase userDB;
    private final MessageDataBase messageDB;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final LoginRegisterHandler loginRegisterHandler;
    private final MessageHandler messageHandler;
    private final PingHandler pingHandler;
    private final ConnectionHandler connectionHandler;
    private final ProfileHandler profileHandler;

    public Server(int port) throws SocketException {
        this.peer = new UDPPeer((short) port);
        this.userDB = new UserDataBase();
        this.messageDB = new MessageDataBase("messages");
        this.sessionManager = new SessionManager(this, userDB);

        this.loginRegisterHandler = new LoginRegisterHandler(this, userDB);
        this.messageHandler = new MessageHandler(this);
        this.pingHandler = new PingHandler(this);
        this.connectionHandler = new ConnectionHandler(this);
        this.profileHandler = new ProfileHandler(this);

        setupReceiver();
        System.out.println("Server started on port " + port);
    }

    private void setupReceiver() {
        peer.onReceive((packet, ip, port) -> {
            executor.submit(() -> {
                try {
                    if (packet.getReadableBytes() < 1) return;
                    packet.resetRead();
                    byte packetId = packet.readByte();
                    packet.resetRead();

                    switch (packetId) {
                        case 1 -> pingHandler.handlePing(packet, ip, port);
                        case 3 -> connectionHandler.handleConnectionRequest(packet, ip, port);
                        case 6 -> loginRegisterHandler.handleLogin(packet, ip, port);
                        case 9 -> loginRegisterHandler.handleRegister(packet, ip, port);
                        case 12 -> messageHandler.handleMessageSend(packet, ip, port);
                        case 14 -> handleConnectionBreak(packet, ip, port);
                        case 16 -> messageHandler.handleMessagePoolRequest(packet, ip, port);
                        case 18 -> handleUserListRequest(packet, ip, port);
                        case 20 -> profileHandler.handleProfileRequest(packet, ip, port);
                        case 21 -> profileHandler.handleUpdateProfile(packet, ip, port);
                        case 24 -> messageHandler.handleMessageHistoryRequest(packet, ip, port);
                        default -> System.out.println("Unknown packet: " + packetId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        peer.onError(e -> System.err.println("UDP error: " + e.getMessage()));
    }

    private void handleConnectionBreak(Packet packet, String ip, int port) {
        ConnectionBreakPacket breakPacket = new ConnectionBreakPacket(packet);
        String username = sessionManager.findUsernameByAddress(ip, port);
        if (username != null) {
            sessionManager.removeSession(username);
            System.out.println("[DISCONNECT] " + username + " disconnected");
            ArrayList<String> allUsers = userDB.getAllUsernames();
            UserListPacket userList = new UserListPacket(allUsers);
            sessionManager.broadcastToAll(userList.getPacket());
        }
    }

    private void handleUserListRequest(Packet packet, String ip, int port) {
        UserListRequestPacket request = new UserListRequestPacket(packet);
        ArrayList<String> allUsers = userDB.getAllUsernames();
        UserListPacket response = new UserListPacket(allUsers);
        peer.send(response.getPacket(), ip, port);
        System.out.println("[USER LIST] Sent " + allUsers.size() + " users to " + ip + ":" + port);
    }

    public UDPPeer getPeer() { return peer; }
    public SessionManager getSessionManager() { return sessionManager; }
    public UserDataBase getUserDB() { return userDB; }
    public MessageDataBase getMessageDataBase() { return messageDB; }

    public static void main(String[] args) throws SocketException {
        new Server(25565);
    }
}