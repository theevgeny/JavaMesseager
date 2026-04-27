package network.protocol;

import network.Packet;

public class ProfileRequestPacket {
    private String username;

    public ProfileRequestPacket(String username) {
        this.username = username;
    }

    public ProfileRequestPacket(Packet packet) {
        try {
            packet.readByte();
            this.username = packet.readString();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(20);
        pk.writeString(username);
        return pk;
    }

    public String getUsername() { return username; }
}