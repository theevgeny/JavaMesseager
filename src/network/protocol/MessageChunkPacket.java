package network.protocol;

import network.Packet;
import server.message.Message;
import java.util.ArrayList;

public class MessageChunkPacket {
    private ArrayList<Message> messages;
    private int totalCount;
    private int chunkIndex;
    private boolean hasMore;

    public MessageChunkPacket(ArrayList<Message> messages, int totalCount, int chunkIndex, boolean hasMore) {
        this.messages = messages;
        this.totalCount = totalCount;
        this.chunkIndex = chunkIndex;
        this.hasMore = hasMore;
    }

    public MessageChunkPacket(Packet packet) {
        try {
            packet.readByte();
            this.totalCount = packet.readInt();
            this.chunkIndex = packet.readInt();
            this.hasMore = packet.readBoolean();
            this.messages = new ArrayList<>();

            int messageCount = packet.readInt();
            for (int i = 0; i < messageCount; i++) {
                String messageText = packet.readString();
                String sender = packet.readString();
                String recipient = packet.readString();
                long sendTime = packet.readLong();
                messages.add(new Message(messageText, sender, recipient, sendTime));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            messages = new ArrayList<>();
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(23);
        pk.writeInt(totalCount);
        pk.writeInt(chunkIndex);
        pk.writeBoolean(hasMore);
        pk.writeInt(messages.size());

        for (Message message : messages) {
            pk.writeString(message.message);
            pk.writeString(message.sender);
            pk.writeString(message.recipient);
            pk.writeLong(message.sendTime);
        }
        return pk;
    }

    public ArrayList<Message> getMessages() { return messages; }
    public int getTotalCount() { return totalCount; }
    public int getChunkIndex() { return chunkIndex; }
    public boolean hasMore() { return hasMore; }
}