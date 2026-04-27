package network.protocol;

import network.Packet;

public class PingPacket {
    private long sendTime = 0;

    public PingPacket(long sendTime) {
        this.sendTime = sendTime;
    }

    public PingPacket(Packet packet) {
        try {
            packet.readByte();
            this.sendTime = packet.readLong();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(1);
        pk.writeLong(sendTime);
        return pk;
    }

    public long getSendTime() { return sendTime; }
}
