package network.protocol;

import network.Packet;

public class UserListRequestPacket {
    private String requester = "";

    public UserListRequestPacket(String requester) {
        this.requester = requester;
    }

    public UserListRequestPacket(Packet packet) {
        try {
            packet.readByte();
            this.requester = packet.readString();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(18); // ID для запроса списка пользователей
        pk.writeString(requester);
        return pk;
    }

    public String getRequester() {
        return requester;
    }
}