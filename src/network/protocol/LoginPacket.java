package network.protocol;

import network.Packet;

public class LoginPacket {
    private String username = "";
    private String password = "";

    public LoginPacket(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginPacket(Packet packet) {
        try {
            packet.readByte();
            this.username = packet.readString();
            this.password = packet.readString();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(6);
        pk.writeString(username);
        pk.writeString(password);
        return pk;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
