package network;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Packet {
    private byte[] buffer;
    private int readPosition = 0;
    private int writePosition = 0;

    public Packet() {
        this.buffer = new byte[1024];
    }

    public Packet(byte[] data) {
        this.buffer = data != null ? data : new byte[1024];
    }

    public Packet(int initialSize) {
        this.buffer = new byte[initialSize];
    }

    public int length() {
        return buffer.length;
    }

    public Packet writeByte(byte value) {
        ensureCapacity(writePosition + 1);
        buffer[writePosition++] = value;
        return this;
    }

    public Packet writeByte(int value) {
        return writeByte((byte) value);
    }

    public Packet writeShort(short value) {
        ensureCapacity(writePosition + 2);
        buffer[writePosition++] = (byte) (value >> 8);
        buffer[writePosition++] = (byte) value;
        return this;
    }

    public Packet writeShort(int value) {
        return writeShort((short) value);
    }

    public Packet writeInt(int value) {
        ensureCapacity(writePosition + 4);
        buffer[writePosition++] = (byte) (value >> 24);
        buffer[writePosition++] = (byte) (value >> 16);
        buffer[writePosition++] = (byte) (value >> 8);
        buffer[writePosition++] = (byte) value;
        return this;
    }

    public Packet writeLong(long value) {
        ensureCapacity(writePosition + 8);
        buffer[writePosition++] = (byte) (value >> 56);
        buffer[writePosition++] = (byte) (value >> 48);
        buffer[writePosition++] = (byte) (value >> 40);
        buffer[writePosition++] = (byte) (value >> 32);
        buffer[writePosition++] = (byte) (value >> 24);
        buffer[writePosition++] = (byte) (value >> 16);
        buffer[writePosition++] = (byte) (value >> 8);
        buffer[writePosition++] = (byte) value;
        return this;
    }

    public Packet writeFloat(float value) {
        return writeInt(Float.floatToIntBits(value));
    }

    public Packet writeDouble(double value) {
        return writeLong(Double.doubleToLongBits(value));
    }

    public Packet writeBoolean(boolean value) {
        return writeByte(value ? 1 : 0);
    }

    public Packet writeString(String value) {
        if (value == null) {
            writeInt(-1);
            return this;
        }

        byte[] strBytes = value.getBytes(StandardCharsets.UTF_8);
        writeInt(strBytes.length);
        ensureCapacity(writePosition + strBytes.length);
        System.arraycopy(strBytes, 0, buffer, writePosition, strBytes.length);
        writePosition += strBytes.length;
        return this;
    }

    public Packet writeBytes(byte[] value) {
        if (value == null) {
            writeInt(-1);
            return this;
        }

        writeInt(value.length);
        ensureCapacity(writePosition + value.length);
        System.arraycopy(value, 0, buffer, writePosition, value.length);
        writePosition += value.length;
        return this;
    }

    public byte readByte() {
        if (readPosition + 1 > writePosition) {
            throw new RuntimeException("Packet read overflow");
        }
        return buffer[readPosition++];
    }

    public int readUnsignedByte() {
        return readByte() & 0xFF;
    }

    public short readShort() {
        if (readPosition + 2 > writePosition) {
            throw new RuntimeException("Packet read overflow");
        }
        short value = (short) (((buffer[readPosition++] & 0xFF) << 8) |
                ((buffer[readPosition++] & 0xFF)));
        return value;
    }

    public int readUnsignedShort() {
        return readShort() & 0xFFFF;
    }

    public int readInt() {
        if (readPosition + 4 > writePosition) {
            throw new RuntimeException("Packet read overflow");
        }
        int value = ((buffer[readPosition++] & 0xFF) << 24) |
                ((buffer[readPosition++] & 0xFF) << 16) |
                ((buffer[readPosition++] & 0xFF) << 8) |
                ((buffer[readPosition++] & 0xFF));
        return value;
    }

    public long readLong() {
        if (readPosition + 8 > writePosition) {
            throw new RuntimeException("Packet read overflow");
        }
        long value = ((long) (buffer[readPosition++] & 0xFF) << 56) |
                ((long) (buffer[readPosition++] & 0xFF) << 48) |
                ((long) (buffer[readPosition++] & 0xFF) << 40) |
                ((long) (buffer[readPosition++] & 0xFF) << 32) |
                ((long) (buffer[readPosition++] & 0xFF) << 24) |
                ((long) (buffer[readPosition++] & 0xFF) << 16) |
                ((long) (buffer[readPosition++] & 0xFF) << 8) |
                ((long) (buffer[readPosition++] & 0xFF));
        return value;
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public boolean readBoolean() {
        return readByte() != 0;
    }

    public String readString() {
        int length = readInt();
        if (length == -1) return null;
        if (length == 0) return "";

        if (readPosition + length > writePosition) {
            throw new RuntimeException("Packet read overflow");
        }

        String value = new String(buffer, readPosition, length, StandardCharsets.UTF_8);
        readPosition += length;
        return value;
    }

    public byte[] readBytes() {
        int length = readInt();
        if (length == -1) return null;
        if (length == 0) return new byte[0];

        if (readPosition + length > writePosition) {
            throw new RuntimeException("Packet read overflow");
        }

        byte[] value = new byte[length];
        System.arraycopy(buffer, readPosition, value, 0, length);
        readPosition += length;
        return value;
    }

    private void ensureCapacity(int needed) {
        if (needed <= buffer.length) return;

        int newSize = buffer.length * 2;
        while (newSize < needed) {
            newSize *= 2;
        }
        buffer = Arrays.copyOf(buffer, newSize);
    }

    public void resetRead() {
        readPosition = 0;
    }

    public void resetWrite() {
        writePosition = 0;
    }

    public void clear() {
        readPosition = 0;
        writePosition = 0;
    }

    public int getReadableBytes() {
        return writePosition - readPosition;
    }

    public int getWritePosition() {
        return writePosition;
    }

    public int getReadPosition() {
        return readPosition;
    }

    public void skip(int bytes) {
        if (readPosition + bytes > writePosition) {
            throw new RuntimeException("Packet skip overflow");
        }
        readPosition += bytes;
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(buffer, writePosition);
    }

    public static Packet fromByteArray(byte[] data) {
        Packet packet = new Packet(data);
        packet.writePosition = data.length;
        return packet;
    }
}