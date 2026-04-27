package network.protocol;

import network.Packet;

public class MessagePoolRequestPacket {
    private String username = "";

    public MessagePoolRequestPacket(String username) {
        this.username = username;
    }

    public MessagePoolRequestPacket(Packet packet) {
        try {
            packet.readByte();
            this.username = packet.readString();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(16);
        pk.writeString(username);
        return pk;
    }

    public String getUsername() { return username; }
}
