package client;

import network.UDPPeer;
import network.protocol.*;
import network.protocol.types.ConnectionBreakCause;
import server.message.Message;
import server.user.UserProfile;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ClientNetwork {
    private final Client client;
    private UDPPeer peer;
    private String serverIp;
    private int serverPort;
    private String username;
    private volatile boolean running = true;
    private Timer userListTimer;

    public ClientNetwork(Client client) {
        this.client = client;
    }

    public void connect(String serverIp, int serverPort, String username, String password, boolean isRegister) {
        try {
            if (peer != null) {
                peer.close();
            }
            this.serverIp = serverIp;
            this.serverPort = serverPort;
            this.username = username;
            this.peer = new UDPPeer((short) 0);
            setupReceiver();

            if (isRegister) {
                peer.send(new RegisterPacket(username, password).getPacket(), serverIp, serverPort);
            } else {
                peer.send(new LoginPacket(username, password).getPacket(), serverIp, serverPort);
            }
        } catch (Exception e) {
            client.onLoginFailed("Не удалось подключиться: " + e.getMessage());
        }
    }

    private void setupReceiver() {
        peer.onReceive((packet, ip, port) -> {
            if (!running) return;
            if (packet.getReadableBytes() < 1) return;

            packet.resetRead();
            byte id = packet.readByte();
            packet.resetRead();

            switch (id) {
                case 7 -> {
                    new LoginSucsessPacket(packet);
                    client.onLoginSuccess();
                }
                case 8 -> {
                    LoginFailPacket fail = new LoginFailPacket(packet);
                    client.onLoginFailed("Ошибка входа: " + fail.getCause());
                    close();
                }
                case 10 -> {
                    new RegisterSucsessPacket(packet);
                    client.onRegisterSuccess();
                }
                case 11 -> {
                    RegisterFailPacket fail = new RegisterFailPacket(packet);
                    client.onRegisterFailed(fail.getCause().toString());
                    close();
                }
                case 13 -> {
                    MessagePacket msg = new MessagePacket(packet);
                    Message m = msg.getMessage();
                    client.onMessageReceived(m.sender, m.message, m.sendTime);
                }
                case 19 -> {
                    ProfilePacket profilePkt = new ProfilePacket(packet);
                    UserProfile profile = new UserProfile(
                            profilePkt.getUsername(),
                            profilePkt.getDisplayName(),
                            profilePkt.getBio(),
                            profilePkt.getRegisteredAt()
                    );
                    client.onProfileReceived(profile);
                }
                case 22 -> {
                    new ProfileUpdateSuccessPacket(packet);
                    client.onProfileUpdateSuccess();
                }
                case 23 -> {
                    MessageChunkPacket chunk = new MessageChunkPacket(packet);
                    client.onHistoryReceived(chunk.getMessages(), chunk.getTotalCount(), chunk.getChunkIndex(), chunk.hasMore());
                }
                case 17 -> {
                    UserListPacket userList = new UserListPacket(packet);
                    client.onUserListReceived(userList.getUsers());
                }
            }
        });
    }

    public void sendMessage(String recipient, String message) {
        if (peer == null) return;
        peer.send(new MessageSendPacket(message, recipient).getPacket(), serverIp, serverPort);
    }

    public void requestUserList() {
        if (peer != null && username != null) {
            peer.send(new UserListRequestPacket(username).getPacket(), serverIp, serverPort);
        }
    }

    public void requestMessageHistory(int page, int pageSize, String conversationWith) {
        if (peer != null && username != null) {
            String target = conversationWith != null ? conversationWith : "";
            peer.send(new MessageHistoryRequestPacket(username, page, pageSize, target).getPacket(), serverIp, serverPort);
        }
    }

    public void requestProfile(String profileUsername) {
        if (peer != null) {
            peer.send(new ProfileRequestPacket(profileUsername).getPacket(), serverIp, serverPort);
        }
    }

    public void requestMyProfile() {
        requestProfile(username);
    }

    public void updateProfile(String displayName, String bio) {
        if (peer != null && username != null) {
            peer.send(new UpdateProfilePacket(displayName, bio).getPacket(), serverIp, serverPort);
        }
    }

    public void startUserListUpdater() {
        if (userListTimer != null) userListTimer.cancel();
        userListTimer = new Timer();
        userListTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                requestUserList();
            }
        }, 30000, 30000);
    }

    public void close() {
        running = false;
        if (userListTimer != null) {
            userListTimer.cancel();
        }
        if (peer != null) {
            peer.close();
        }
    }

    public void sendDisconnect() {
        if (peer != null && username != null) {
            ConnectionBreakPacket breakPacket = new ConnectionBreakPacket(ConnectionBreakCause.CONNECTION_LOST);
            peer.send(breakPacket.getPacket(), serverIp, serverPort);
        }
    }
}