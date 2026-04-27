package network.protocol;

import network.Packet;
import network.protocol.types.ConnectionBreakCause;

public class ConnectionBreakPacket {
    private ConnectionBreakCause connectionBreakCause = ConnectionBreakCause.EMPTY;

    public ConnectionBreakPacket(ConnectionBreakCause connectionBreakCause) {
        this.connectionBreakCause = connectionBreakCause;
    }

    public ConnectionBreakPacket(Packet packet) {
        try {
            packet.readByte();
            this.connectionBreakCause = ConnectionBreakCause.fromByte(packet.readByte());
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(14);
        pk.writeByte(connectionBreakCause.toByte());
        return pk;
    }
}
