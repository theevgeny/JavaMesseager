package network.protocol;

import network.Packet;

public class UpdateProfilePacket {
    private String displayName;
    private String bio;

    public UpdateProfilePacket(String displayName, String bio) {
        this.displayName = displayName;
        this.bio = bio;
    }

    public UpdateProfilePacket(Packet packet) {
        try {
            packet.readByte();
            this.displayName = packet.readString();
            this.bio = packet.readString();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(21);
        pk.writeString(displayName);
        pk.writeString(bio);
        return pk;
    }

    public String getDisplayName() { return displayName; }
    public String getBio() { return bio; }
}