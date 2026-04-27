package network.protocol;

import network.Packet;
import server.message.Message;

public class MessagePacket {
    private Message message;

    public MessagePacket(Message message) {
        this.message = message;
    }

    public MessagePacket(Packet packet) {
        packet.readByte();
        message = new Message(
                packet.readString(),
                packet.readString(),
                packet.readString(),
                packet.readLong()
        );
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(13);
        pk.writeString(message.message);
        pk.writeString(message.sender);
        pk.writeString(message.recipient);
        pk.writeLong(message.sendTime);
        return pk;
    }

    public Message getMessage() { return message; }
}