package network.protocol;

import network.Packet;

public class LoginSucsessPacket {

    public LoginSucsessPacket() {}

    public LoginSucsessPacket(Packet packet) {
        try {
            packet.readByte();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(7);
        return pk;
    }
}
