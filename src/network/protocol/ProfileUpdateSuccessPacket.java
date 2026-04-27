package network.protocol;

import network.Packet;

public class ProfileUpdateSuccessPacket {
    public ProfileUpdateSuccessPacket() {}

    public ProfileUpdateSuccessPacket(Packet packet) {
        try {
            packet.readByte();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(22);
        return pk;
    }
}