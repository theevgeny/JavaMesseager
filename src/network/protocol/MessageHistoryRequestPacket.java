package network.protocol;

import network.Packet;

public class MessageHistoryRequestPacket {
    private String username;
    private int page;
    private int pageSize;
    private String conversationWith;

    public MessageHistoryRequestPacket(String username, int page, int pageSize, String conversationWith) {
        this.username = username;
        this.page = page;
        this.pageSize = pageSize;
        this.conversationWith = conversationWith;
    }

    public MessageHistoryRequestPacket(Packet packet) {
        try {
            packet.readByte();
            this.username = packet.readString();
            this.page = packet.readInt();
            this.pageSize = packet.readInt();
            this.conversationWith = packet.readString();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(24);
        pk.writeString(username);
        pk.writeInt(page);
        pk.writeInt(pageSize);
        pk.writeString(conversationWith);
        return pk;
    }

    public String getUsername() { return username; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public String getConversationWith() { return conversationWith; }
}