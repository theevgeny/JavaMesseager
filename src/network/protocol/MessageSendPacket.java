package network.protocol;

import network.Packet;

public class MessageSendPacket {
    private String message = "";
    private String usernameRecipient = "";

    public MessageSendPacket(String message, String usernameRecipient) {
        this.message = message;
        this.usernameRecipient = usernameRecipient;
    }

    public MessageSendPacket(Packet packet) {
        try {
            packet.readByte();
            this.message = packet.readString();
            this.usernameRecipient = packet.readString();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(12);
        pk.writeString(message);
        pk.writeString(usernameRecipient);
        return pk;
    }

    public String getMessage() { return message; }
    public String getRecipient() { return usernameRecipient; }
}
