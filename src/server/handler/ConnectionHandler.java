package server.handler;

import network.Packet;
import network.protocol.ConnectionAcceptPacket;
import network.protocol.ConnectionRejectPacket;
import network.protocol.ConnectionRequestPacket;
import network.protocol.types.RejectCause;
import server.Server;

import static server.GlobalData.protocolVersion;

public class ConnectionHandler {
    private final Server server;

    public ConnectionHandler(Server server) {
        this.server = server;
    }

    public void handleConnectionRequest(Packet packet, String ip, int port) {
        ConnectionRequestPacket req = new ConnectionRequestPacket(packet);
        int clientVersion = req.getProtocolVersion();

        if (clientVersion == protocolVersion) {
            byte[] key = new byte[16];
            ConnectionAcceptPacket accept = new ConnectionAcceptPacket(key);
            server.getPeer().send(accept.getPacket(), ip, port);
            System.out.println("[CONNECT] Client accepted from " + ip + ":" + port);
        } else if (clientVersion < protocolVersion) {
            ConnectionRejectPacket reject = new ConnectionRejectPacket(RejectCause.BELOW_VERSION);
            server.getPeer().send(reject.getPacket(), ip, port);
        } else {
            ConnectionRejectPacket reject = new ConnectionRejectPacket(RejectCause.ABOVE_VERSION);
            server.getPeer().send(reject.getPacket(), ip, port);
        }
    }
}