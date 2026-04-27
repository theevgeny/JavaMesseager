package server.handler;

import network.Packet;
import network.protocol.*;
import server.Server;
import server.user.UserProfile;

public class ProfileHandler {
    private final Server server;

    public ProfileHandler(Server server) {
        this.server = server;
    }

    public void handleProfileRequest(Packet packet, String ip, int port) {
        ProfileRequestPacket request = new ProfileRequestPacket(packet);
        String requestedUsername = request.getUsername();

        UserProfile profile = server.getUserDB().getProfile(requestedUsername);
        int messageCount = server.getMessageDataBase().getMessageCount(requestedUsername);
        boolean isOnline = server.getSessionManager().isOnline(requestedUsername);

        ProfilePacket response = new ProfilePacket(
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getRegisteredAt(),
                messageCount,
                isOnline
        );

        server.getPeer().send(response.getPacket(), ip, port);
        System.out.println("[PROFILE] Sent profile for " + requestedUsername + " to " + ip + ":" + port);
    }

    public void handleUpdateProfile(Packet packet, String ip, int port) {
        UpdateProfilePacket request = new UpdateProfilePacket(packet);

        String username = server.getSessionManager().findUsernameByAddress(ip, port);
        if (username == null) {
            System.out.println("[PROFILE] Unauthorized profile update from " + ip + ":" + port);
            return;
        }

        boolean success = server.getUserDB().updateProfile(username, request.getDisplayName(), request.getBio());

        if (success) {
            ProfileUpdateSuccessPacket successPacket = new ProfileUpdateSuccessPacket();
            server.getPeer().send(successPacket.getPacket(), ip, port);
            System.out.println("[PROFILE] " + username + " updated profile");
        }
    }
}