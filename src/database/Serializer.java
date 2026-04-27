package database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Serializer {

    static public byte[] encode(@Nullable Object object) {
        return switch (object) {
            case Boolean instance -> encode(instance.booleanValue());
            case Integer instance -> encode(instance.intValue());
            case Long instance -> encode(instance.longValue());
            case Float instance -> encode(instance.floatValue());
            case Double instance -> encode(instance.doubleValue());
            case String instance -> encode(instance);
            case List<?> instance -> encode((List<Object>) instance);
            case ConcurrentHashMap<?, ?> instance -> encode((ConcurrentHashMap<Object, Object>) instance);
            case null, default -> new byte[]{0};
        };
    }

    static public byte[] encode(boolean value) {
        byte[] buffer = new byte[2];
        buffer[0] = 1;
        buffer[1] = (byte) (value ? 1 : 0);
        return buffer;
    }

    static public byte[] encode(int value) {
        byte[] buffer = new byte[5];
        buffer[0] = 2;
        buffer[1] = (byte) (value >> 24);
        buffer[2] = (byte) (value >> 16);
        buffer[3] = (byte) (value >> 8);
        buffer[4] = (byte) value;
        return buffer;
    }

    static public byte[] encode(long value) {
        byte[] buffer = new byte[9];
        buffer[0] = 3;
        buffer[1] = (byte) (value >> 56);
        buffer[2] = (byte) (value >> 48);
        buffer[3] = (byte) (value >> 40);
        buffer[4] = (byte) (value >> 32);
        buffer[5] = (byte) (value >> 24);
        buffer[6] = (byte) (value >> 16);
        buffer[7] = (byte) (value >> 8);
        buffer[8] = (byte) value;
        return buffer;
    }

    static public byte[] encode(float value) {
        byte[] buffer = new byte[5];
        buffer[0] = 4;
        int converted = Float.floatToIntBits(value);
        buffer[1] = (byte) (converted >> 24);
        buffer[2] = (byte) (converted >> 16);
        buffer[3] = (byte) (converted >> 8);
        buffer[4] = (byte) converted;
        return buffer;
    }

    static public byte[] encode(double value) {
        byte[] buffer = new byte[9];
        buffer[0] = 5;
        long converted = Double.doubleToLongBits(value);
        buffer[1] = (byte) (converted >> 56);
        buffer[2] = (byte) (converted >> 48);
        buffer[3] = (byte) (converted >> 40);
        buffer[4] = (byte) (converted >> 32);
        buffer[5] = (byte) (converted >> 24);
        buffer[6] = (byte) (converted >> 16);
        buffer[7] = (byte) (converted >> 8);
        buffer[8] = (byte) converted;
        return buffer;
    }

    static public byte[] encode(@NotNull String value) {
        byte[] strBuffer = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        int sizeStr = strBuffer.length;
        byte[] buffer = new byte[sizeStr + 5];
        buffer[0] = 6;
        buffer[1] = (byte) (sizeStr >> 24);
        buffer[2] = (byte) (sizeStr >> 16);
        buffer[3] = (byte) (sizeStr >> 8);
        buffer[4] = (byte) sizeStr;
        System.arraycopy(strBuffer, 0, buffer, 5, sizeStr);
        return buffer;
    }

    static public byte[] encode(@NotNull List<@Nullable Object> list) {
        ByteArrayOutputStream byteVector = new ByteArrayOutputStream();
        byteVector.write((byte) 7);
        int listSize = list.size();
        byteVector.write((byte) (listSize >> 24));
        byteVector.write((byte) (listSize >> 16));
        byteVector.write((byte) (listSize >> 8));
        byteVector.write((byte) listSize);
        for (Object object : list) {
            byte[] encoded = encode(object);
            for (byte b : encoded) byteVector.write(b);
        }
        return byteVector.toByteArray();
    }

    static public byte[] encode(@NotNull Map<@Nullable Object, @Nullable Object> map) {
        ByteArrayOutputStream byteVector = new ByteArrayOutputStream();
        byteVector.write((byte) 8);
        int mapSize = map.size();
        byteVector.write((byte) (mapSize >> 24));
        byteVector.write((byte) (mapSize >> 16));
        byteVector.write((byte) (mapSize >> 8));
        byteVector.write((byte) mapSize);
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            byte[] encoded = encode(entry.getKey());
            for (byte b : encoded) byteVector.write(b);
            encoded = encode(entry.getValue());
            for (byte b : encoded) byteVector.write(b);
        }
        return byteVector.toByteArray();
    }

    static public @Nullable Object decode(byte[] buffer) {
        if (buffer.length < 1)
            return null;
        switch (buffer[0]) {
            case 1:
                if (buffer.length != 2) return null;
                return buffer[1] != 0;
            case 2:
                if (buffer.length != 5) return null;
                return ((buffer[1] & 0xFF) << 24) |
                        ((buffer[2] & 0xFF) << 16) |
                        ((buffer[3] & 0xFF) << 8) |
                        ((buffer[4] & 0xFF));
            case 3:
                if (buffer.length != 9) return null;
                return ((long) (buffer[1] & 0xFF) << 56) |
                        ((long) (buffer[2] & 0xFF) << 48) |
                        ((long) (buffer[3] & 0xFF) << 40) |
                        ((long) (buffer[4] & 0xFF) << 32) |
                        ((long) (buffer[5] & 0xFF) << 24) |
                        ((long) (buffer[6] & 0xFF) << 16) |
                        ((long) (buffer[7] & 0xFF) << 8) |
                        ((long) (buffer[8] & 0xFF));
            case 4:
                if (buffer.length != 5) return null;
                return Float.intBitsToFloat(((buffer[1] & 0xFF) << 24) |
                        ((buffer[2] & 0xFF) << 16) |
                        ((buffer[3] & 0xFF) << 8) |
                        ((buffer[4] & 0xFF)));
            case 5:
                if (buffer.length != 9) return null;
                return Double.longBitsToDouble(((long) (buffer[1] & 0xFF) << 56) |
                        ((long) (buffer[2] & 0xFF) << 48) |
                        ((long) (buffer[3] & 0xFF) << 40) |
                        ((long) (buffer[4] & 0xFF) << 32) |
                        ((long) (buffer[5] & 0xFF) << 24) |
                        ((long) (buffer[6] & 0xFF) << 16) |
                        ((long) (buffer[7] & 0xFF) << 8) |
                        ((long) (buffer[8] & 0xFF)));
            case 6:
                if (buffer.length < 5) return null;
                int sizeStr = ((buffer[1] & 0xFF) << 24) |
                        ((buffer[2] & 0xFF) << 16) |
                        ((buffer[3] & 0xFF) << 8) |
                        ((buffer[4] & 0xFF));
                if (buffer.length != sizeStr + 5) return null;
                return new String(buffer, 5, sizeStr, java.nio.charset.StandardCharsets.UTF_8);
            case 7:
                if (buffer.length < 5) return null;
                int sizeList = ((buffer[1] & 0xFF) << 24) |
                        ((buffer[2] & 0xFF) << 16) |
                        ((buffer[3] & 0xFF) << 8) |
                        ((buffer[4] & 0xFF));
                if (sizeList < 0) return null;
                int currentOffsetList = 5;
                ArrayList<Object> list = new ArrayList<>();
                for (int i = 0; i < sizeList; ++i) {
                    if (buffer.length - currentOffsetList < 1) return null;
                    DecodeResult decodeResult = decodeOfOffset(buffer, currentOffsetList);
                    currentOffsetList = decodeResult.newOffset;
                    list.add(decodeResult.value);
                }
                return list;
            case 8:
                if (buffer.length < 5) return null;
                int sizeMap = ((buffer[1] & 0xFF) << 24) |
                        ((buffer[2] & 0xFF) << 16) |
                        ((buffer[3] & 0xFF) << 8) |
                        ((buffer[4] & 0xFF));
                if (sizeMap < 0) return null;
                int currentOffsetMap = 5;
                Map<Object, Object> map = new ConcurrentHashMap<>();
                for (int i = 0; i < sizeMap; ++i) {
                    if (buffer.length - currentOffsetMap < 1) return null;
                    DecodeResult decodeResult = decodeOfOffset(buffer, currentOffsetMap);
                    currentOffsetMap = decodeResult.newOffset;
                    Object key = decodeResult.value;
                    if (buffer.length - currentOffsetMap < 1) return null;
                    decodeResult = decodeOfOffset(buffer, currentOffsetMap);
                    currentOffsetMap = decodeResult.newOffset;
                    map.put(key, decodeResult.value);
                }
                return map;
            default:
                return null;
        }
    }

    public static class DecodeResult {
        public final Object value;
        public final int newOffset;

        public DecodeResult(@Nullable Object value, int newOffset) {
            this.value = value;
            this.newOffset = newOffset;
        }
    }

    static public @NotNull DecodeResult decodeOfOffset(byte[] buffer, int offset) {
        if (buffer.length - offset < 1) {
            return new DecodeResult(null, offset + 1);
        }
        switch (buffer[offset]) {
            case 1:
                if (buffer.length - offset < 2)
                    return new DecodeResult(null, offset + 1);
                return new DecodeResult(buffer[offset + 1] != 0, offset + 2);
            case 2:
                if (buffer.length - offset < 5)
                    return new DecodeResult(null, offset + 1);
                return new DecodeResult(((buffer[offset + 1] & 0xFF) << 24) |
                        ((buffer[offset + 2] & 0xFF) << 16) |
                        ((buffer[offset + 3] & 0xFF) << 8) |
                        ((buffer[offset + 4] & 0xFF)), offset + 5);
            case 3:
                if (buffer.length - offset < 9)
                    return new DecodeResult(null, offset + 1);
                return new DecodeResult(((long) (buffer[offset + 1] & 0xFF) << 56) |
                        ((long) (buffer[offset + 2] & 0xFF) << 48) |
                        ((long) (buffer[offset + 3] & 0xFF) << 40) |
                        ((long) (buffer[offset + 4] & 0xFF) << 32) |
                        ((long) (buffer[offset + 5] & 0xFF) << 24) |
                        ((long) (buffer[offset + 6] & 0xFF) << 16) |
                        ((long) (buffer[offset + 7] & 0xFF) << 8) |
                        ((long) (buffer[offset + 8] & 0xFF)), offset + 9);
            case 4:
                if (buffer.length - offset < 5)
                    return new DecodeResult(null, offset + 1);
                return new DecodeResult(Float.intBitsToFloat(((buffer[offset + 1] & 0xFF) << 24) |
                        ((buffer[offset + 2] & 0xFF) << 16) |
                        ((buffer[offset + 3] & 0xFF) << 8) |
                        ((buffer[offset + 4] & 0xFF))), offset + 5);
            case 5:
                if (buffer.length - offset < 9)
                    return new DecodeResult(null, offset + 1);
                return new DecodeResult(Double.longBitsToDouble(((long) (buffer[offset + 1] & 0xFF) << 56) |
                        ((long) (buffer[offset + 2] & 0xFF) << 48) |
                        ((long) (buffer[offset + 3] & 0xFF) << 40) |
                        ((long) (buffer[offset + 4] & 0xFF) << 32) |
                        ((long) (buffer[offset + 5] & 0xFF) << 24) |
                        ((long) (buffer[offset + 6] & 0xFF) << 16) |
                        ((long) (buffer[offset + 7] & 0xFF) << 8) |
                        ((long) (buffer[offset + 8] & 0xFF))), offset + 9);
            case 6:
                if (buffer.length - offset < 5)
                    return new DecodeResult(null, offset + 1);
                int sizeStr = ((buffer[offset + 1] & 0xFF) << 24) |
                        ((buffer[offset + 2] & 0xFF) << 16) |
                        ((buffer[offset + 3] & 0xFF) << 8) |
                        ((buffer[offset + 4] & 0xFF));
                if (buffer.length - offset < sizeStr + 5 || sizeStr < 0)
                    return new DecodeResult(null, offset + 5);
                return new DecodeResult(new String(buffer, offset + 5, sizeStr, java.nio.charset.StandardCharsets.UTF_8), offset + sizeStr + 5);
            case 7:
                if (buffer.length - offset < 5)
                    return new DecodeResult(null, offset + 1);
                int sizeList = ((buffer[offset + 1] & 0xFF) << 24) |
                        ((buffer[offset + 2] & 0xFF) << 16) |
                        ((buffer[offset + 3] & 0xFF) << 8) |
                        ((buffer[offset + 4] & 0xFF));
                int currentOffsetList = offset + 5;
                ArrayList<Object> list = new ArrayList<>();
                for (int i = 0; i < sizeList; ++i) {
                    if (buffer.length - currentOffsetList < 1)
                        return new DecodeResult(null, currentOffsetList);
                    DecodeResult decodeResult = decodeOfOffset(buffer, currentOffsetList);
                    currentOffsetList = decodeResult.newOffset;
                    list.add(decodeResult.value);
                }
                return new DecodeResult(list, currentOffsetList);
            case 8:
                if (buffer.length - offset < 5)
                    return new DecodeResult(null, offset + 1);
                int sizeMap = ((buffer[offset + 1] & 0xFF) << 24) |
                        ((buffer[offset + 2] & 0xFF) << 16) |
                        ((buffer[offset + 3] & 0xFF) << 8) |
                        ((buffer[offset + 4] & 0xFF));
                if (sizeMap < 0)
                    return new DecodeResult(null, offset + 5);
                int currentOffsetMap = offset + 5;
                Map<Object, Object> map = new ConcurrentHashMap<>();
                for (int i = 0; i < sizeMap; ++i) {
                    if (buffer.length - currentOffsetMap < 1)
                        return new DecodeResult(null, currentOffsetMap);
                    DecodeResult decodeResult = decodeOfOffset(buffer, currentOffsetMap);
                    currentOffsetMap = decodeResult.newOffset;
                    Object key = decodeResult.value;
                    if (buffer.length - currentOffsetMap < 1)
                        return new DecodeResult(null, currentOffsetMap);
                    decodeResult = decodeOfOffset(buffer, currentOffsetMap);
                    currentOffsetMap = decodeResult.newOffset;
                    map.put(key, decodeResult.value);
                }
                return new DecodeResult(map, currentOffsetMap);
            default:
                return new DecodeResult(null, offset + 1);
        }
    }
}