package network.protocol;

import network.Packet;

public class ConnectionRequestPacket {
    private int protocolVersion = 0;

    public ConnectionRequestPacket(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public ConnectionRequestPacket(Packet packet) {
        try {
            packet.readByte();
            this.protocolVersion = packet.readInt();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(3);
        pk.writeInt(protocolVersion);
        return pk;
    }

    public int getProtocolVersion() { return protocolVersion; }
}
