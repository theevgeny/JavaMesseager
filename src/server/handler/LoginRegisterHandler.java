package server.handler;

import network.Packet;
import network.protocol.*;
import network.protocol.types.LoginFailCause;
import network.protocol.types.RegisterFailCause;
import server.Server;
import server.user.ClientSession;
import server.user.UserDataBase;

import java.util.ArrayList;

public class LoginRegisterHandler {
    private final Server server;
    private final UserDataBase userDB;

    public LoginRegisterHandler(Server server, UserDataBase userDB) {
        this.server = server;
        this.userDB = userDB;
    }

    public void handleLogin(Packet packet, String ip, int port) {
        LoginPacket login = new LoginPacket(packet);
        String username = login.getUsername();
        String password = login.getPassword();

        LoginFailCause cause = userDB.login(username, password);

        if (cause == null) {
            ClientSession session = new ClientSession(username, ip, port);
            server.getSessionManager().addSession(username, session);

            LoginSucsessPacket success = new LoginSucsessPacket();
            server.getPeer().send(success.getPacket(), ip, port);
            System.out.println("[LOGIN] " + username + " from " + ip + ":" + port);

            broadcastUserList();
        } else {
            LoginFailPacket fail = new LoginFailPacket(cause);
            server.getPeer().send(fail.getPacket(), ip, port);
            System.out.println("[LOGIN FAIL] " + username + " - " + cause);
        }
    }

    public void handleRegister(Packet packet, String ip, int port) {
        RegisterPacket register = new RegisterPacket(packet);
        String username = register.getUsername();
        String password = register.getPassword();

        RegisterFailCause cause = userDB.register(username, password);

        if (cause == null) {
            RegisterSucsessPacket success = new RegisterSucsessPacket();
            server.getPeer().send(success.getPacket(), ip, port);
            System.out.println("[REGISTER] New user: " + username);
        } else {
            RegisterFailPacket fail = new RegisterFailPacket(cause);
            server.getPeer().send(fail.getPacket(), ip, port);
            System.out.println("[REGISTER FAIL] " + username + " - " + cause);
        }
    }

    private void broadcastUserList() {
        ArrayList<String> allUsers = userDB.getAllUsernames();
        UserListPacket userListPacket = new UserListPacket(allUsers);
        server.getSessionManager().broadcastToAll(userListPacket.getPacket());
    }
}