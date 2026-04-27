package network.protocol.types;

public enum RejectCause {
    EMPTY,
    BELOW_VERSION,
    ABOVE_VERSION,
    BAN;

    public byte toByte() {
        return (byte) ordinal();
    }

    public static RejectCause fromByte(byte b) {
        RejectCause[] values = values();
        if (b < 0 || b >= values.length) {
            return EMPTY;
        }
        return values[b];
    }
}