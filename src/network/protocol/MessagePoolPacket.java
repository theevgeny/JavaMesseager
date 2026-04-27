package network.protocol;

import network.Packet;
import server.message.Message;
import java.util.ArrayList;

public class MessagePoolPacket {
    private ArrayList<Message> messages = new ArrayList<>();

    public MessagePoolPacket(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public MessagePoolPacket(Packet packet) {
        packet.readByte();
        messages = new ArrayList<>();

        while (packet.getReadableBytes() > 0) {
            try {
                String messageText = packet.readString();
                String sender = packet.readString();
                String recipient = packet.readString();
                long sendTime = packet.readLong();
                messages.add(new Message(messageText, sender, recipient, sendTime));
            } catch (Exception e) {
                break; // конец данных
            }
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(15);
        for (Message message : messages) {
            pk.writeString(message.message);
            pk.writeString(message.sender);
            pk.writeString(message.recipient);
            pk.writeLong(message.sendTime);
        }
        return pk;
    }

    public ArrayList<Message> getMessages() { return messages; }
}