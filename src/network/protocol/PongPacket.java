package network.protocol;

import network.Packet;

public class PongPacket {
    private long sendTime = 0;
    private long receiveTime = 0;

    public PongPacket(long sendTime, long receiveTime) {
        this.sendTime = sendTime;
        this.receiveTime = receiveTime;
    }

    public PongPacket(Packet packet) {
        try {
            packet.readByte();
            this.sendTime = packet.readLong();
            this.receiveTime = packet.readLong();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet toPacket() {
        Packet pk = new Packet();
        pk.writeByte(2);
        pk.writeLong(sendTime);
        pk.writeLong(receiveTime);
        return pk;
    }
}
