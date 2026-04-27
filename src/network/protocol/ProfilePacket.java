package network.protocol;

import network.Packet;

public class ProfilePacket {
    private String username;
    private String displayName;
    private String bio;
    private long registeredAt;
    private int messageCount;
    private boolean online;

    public ProfilePacket(String username, String displayName, String bio, long registeredAt, int messageCount, boolean online) {
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
        this.registeredAt = registeredAt;
        this.messageCount = messageCount;
        this.online = online;
    }

    public ProfilePacket(Packet packet) {
        try {
            packet.readByte();
            this.username = packet.readString();
            this.displayName = packet.readString();
            this.bio = packet.readString();
            this.registeredAt = packet.readLong();
            this.messageCount = packet.readInt();
            this.online = packet.readBoolean();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(19);
        pk.writeString(username);
        pk.writeString(displayName);
        pk.writeString(bio);
        pk.writeLong(registeredAt);
        pk.writeInt(messageCount);
        pk.writeBoolean(online);
        return pk;
    }

    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getBio() { return bio; }
    public long getRegisteredAt() { return registeredAt; }
    public int getMessageCount() { return messageCount; }
    public boolean isOnline() { return online; }
}