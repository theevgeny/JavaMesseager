package network.protocol.types;

public enum RegisterFailCause {
    EMPTY,
    USERNAME_IS_ALREADY,
    PASSWORD_IS_SIMPLE,
    INVALID_USERNAME,
    ERROR;

    public byte toByte() {
        return (byte) ordinal();
    }

    public static RegisterFailCause fromByte(byte b) {
        RegisterFailCause[] values = values();
        if (b < 0 || b >= values.length) {
            return EMPTY;
        }
        return values[b];
    }
}