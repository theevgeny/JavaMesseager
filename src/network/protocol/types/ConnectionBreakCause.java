package network.protocol.types;

public enum ConnectionBreakCause {
    EMPTY,
    BAN,
    CONNECTION_LOST,
    SERVER_SHOTDOWN;

    public byte toByte() {
        return (byte) ordinal();
    }

    public static ConnectionBreakCause fromByte(byte b) {
        ConnectionBreakCause[] values = values();
        if (b < 0 || b >= values.length) {
            return EMPTY;
        }
        return values[b];
    }
}