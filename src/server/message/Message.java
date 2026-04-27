package server.message;

import java.util.Objects;

public class Message {
    public final String message;
    public final String sender;
    public final String recipient;
    public final long sendTime;

    public Message() {
        message = "";
        sender = "";
        recipient = "";
        sendTime = 0;
    }

    public Message(String message, String sender, String recipient, long sendTime) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
        this.sendTime = sendTime;
    }

    public Message(String message, String sender, String recipient) {
        this(message, sender, recipient, System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return String.format("[%s -> %s] %s", sender, recipient, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message msg = (Message) o;
        return sendTime == msg.sendTime &&
                Objects.equals(message, msg.message) &&
                Objects.equals(sender, msg.sender) &&
                Objects.equals(recipient, msg.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, sender, recipient, sendTime);
    }
}