package database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class SDataBaseSection {
    private final ConcurrentHashMap<Object, Object> map;

    public SDataBaseSection(ConcurrentHashMap<Object, Object> map) {
        this.map = map;
    }

    public int getInt(@NotNull Object key) {
        return getInt(key, 0);
    }

    public int getInt(@NotNull Object key, int defaultValue) {
        if (!isValidKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value instanceof Integer instanced) return instanced;
        return defaultValue;
    }

    private boolean isValidKey(@Nullable Object object) {
        return object == null
                || object instanceof String
                || object instanceof Double
                || object instanceof Integer
                || object instanceof Long
                || object instanceof Float;
    }

    private boolean isValidValue(@Nullable Object object) {
        return object == null
                || object instanceof String
                || object instanceof Double
                || object instanceof Integer
                || object instanceof Long
                || object instanceof Boolean
                || object instanceof Float
                || object instanceof List<?>
                || object instanceof ConcurrentHashMap<?, ?>;
    }

    public boolean put(@NotNull Object key, @Nullable Object value) {
        if (isValidKey(key) && isValidValue(value)) {
            map.put(key, value);
            return true;
        }
        return false;
    }

    public @Nullable Object get(@NotNull Object key) {
        if (!isValidKey(key)) return null;
        return map.get(key);
    }

    public @Nullable Object remove(@NotNull Object key) {
        if (!isValidKey(key)) return null;
        return map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public boolean contains(@NotNull Object key) {
        if (!isValidKey(key)) return false;
        return map.containsKey(key);
    }

    public boolean getBool(@NotNull Object key) {
        return getBool(key, false);
    }

    public boolean getBool(@NotNull Object key, boolean defaultValue) {
        if (!isValidKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value instanceof Boolean instanced) return instanced;
        return defaultValue;
    }

    public long getLong(@NotNull Object key) {
        return getLong(key, 0L);
    }

    public long getLong(@NotNull Object key, long defaultValue) {
        if (!isValidKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value instanceof Long instanced) return instanced;
        return defaultValue;
    }

    public String getString(@NotNull Object key) {
        return getString(key, "");
    }

    public String getString(@NotNull Object key, String defaultValue) {
        if (!isValidKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value instanceof String instanced) return instanced;
        return defaultValue;
    }

    public float getFloat(@NotNull Object key) {
        return getFloat(key, 0F);
    }

    public float getFloat(@NotNull Object key, float defaultValue) {
        if (!isValidKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value instanceof Float instanced) return instanced;
        return defaultValue;
    }

    public double getDouble(@NotNull Object key) {
        return getDouble(key, 0D);
    }

    public double getDouble(@NotNull Object key, double defaultValue) {
        if (!isValidKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value instanceof Double instanced) return instanced;
        return defaultValue;
    }

    public List<Object> getList(@NotNull Object key) {
        return getList(key, new ArrayList<>());
    }

    public List<Object> getList(@NotNull Object key, List<Object> defaultValue) {
        if (!isValidKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value instanceof List<?> instanced) return (List<Object>) instanced;
        return defaultValue;
    }

    public ConcurrentHashMap<Object, Object> getMap(@NotNull Object key) {
        return getMap(key, new ConcurrentHashMap<>());
    }

    public ConcurrentHashMap<Object, Object> getMap(@NotNull Object key, ConcurrentHashMap<Object, Object> defaultValue) {
        if (!isValidKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value instanceof ConcurrentHashMap<?, ?> instanced) return (ConcurrentHashMap<Object, Object>) instanced;
        return defaultValue;
    }

    public SDataBaseSection getSection(@NotNull Object key) {
        if (map.get(key) instanceof ConcurrentHashMap<?, ?> node)
            return new SDataBaseSection((ConcurrentHashMap<Object, Object>) node);
        return new SDataBaseSection(new ConcurrentHashMap<>());
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public @NotNull ConcurrentHashMap<Object, Object> getRawMap() {
        return map;
    }
}