package network.protocol;

import network.Packet;

public class RegisterSucsessPacket {
    public RegisterSucsessPacket() {}

    public RegisterSucsessPacket(Packet packet) {
        try {
            packet.readByte();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(10);
        return pk;
    }
}
