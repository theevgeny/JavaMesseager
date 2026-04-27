package network.protocol;

import network.Packet;
import java.util.ArrayList;

public class UserListPacket {
    private ArrayList<String> users = new ArrayList<>();

    public UserListPacket(ArrayList<String> users) {
        this.users = users;
    }

    public UserListPacket(Packet packet) {
        packet.readByte(); // пропустить ID
        users = new ArrayList<>();

        while (packet.getReadableBytes() > 0) {
            try {
                String username = packet.readString();
                users.add(username);
            } catch (Exception e) {
                break;
            }
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(17);
        for (String user : users) {
            pk.writeString(user);
        }
        return pk;
    }

    public ArrayList<String> getUsers() {
        return users;
    }
}