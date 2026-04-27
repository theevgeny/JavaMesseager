package network.protocol;

import network.Packet;
import network.protocol.types.RejectCause;

public class ConnectionRejectPacket {
    private RejectCause rejectCause = RejectCause.EMPTY;

    public ConnectionRejectPacket(RejectCause rejectCause) {
        this.rejectCause = rejectCause;
    }

    public ConnectionRejectPacket(Packet packet) {
        try {
            packet.readByte();
            this.rejectCause = RejectCause.fromByte(packet.readByte());
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(5);
        pk.writeByte(rejectCause.toByte());
        return pk;
    }
}
