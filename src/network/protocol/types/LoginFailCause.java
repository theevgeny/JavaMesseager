package network.protocol.types;

public enum LoginFailCause {
    EMPTY,
    INVALID_USERNAME,
    INVALID_PASSWORD,
    ERROR;

    public byte toByte() {
        return (byte) ordinal();
    }

    public static LoginFailCause fromByte(byte b) {
        LoginFailCause[] values = values();
        if (b < 0 || b >= values.length) {
            return EMPTY;
        }
        return values[b];
    }
}
