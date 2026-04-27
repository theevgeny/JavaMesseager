package server.handler;

import network.Packet;
import network.protocol.PingPacket;
import network.protocol.PongPacket;
import server.Server;

public class PingHandler {
    private final Server server;

    public PingHandler(Server server) {
        this.server = server;
    }

    public void handlePing(Packet packet, String ip, int port) {
        PingPacket ping = new PingPacket(packet);
        PongPacket pong = new PongPacket(ping.getSendTime(), System.currentTimeMillis());
        server.getPeer().send(pong.toPacket(), ip, port);
    }
}