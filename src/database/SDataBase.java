package database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class SDataBase {
    private ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
    private String id = "";
    private String directory = "";

    private static final byte[] SALT = "YourSecretSalt123".getBytes();
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    public SDataBase(String directory, String id) {
        this.id = id;
        this.directory = directory;
        try {
            File file = new File(directory + "/" + id + ".db");
            if (file.exists()) {
                FileInputStream stream = new FileInputStream(file);
                Object data = decodeEncrypted(stream.readAllBytes(), id);
                if (data instanceof ConcurrentHashMap<?, ?> dataHashMap) {
                    map = (ConcurrentHashMap<Object, Object>) dataHashMap;
                }
                stream.close();
            }
        } catch (Exception e) {
            System.err.println("Failed to load database " + id + ": " + e.getMessage());
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveQuietly));
    }

    public String getId() {
        return id;
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

    public String getString(@NotNull Object key) {
        return getString(key, "");
    }

    public String getString(@NotNull Object key, String defaultValue) {
        if (!isValidKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value instanceof String instanced) return instanced;
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

    public void save() throws IOException {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, id + ".db");
        File tempFile = new File(dir, id + ".tmp");

        try (FileOutputStream stream = new FileOutputStream(tempFile)) {
            byte[] data = encodeEncrypted(map, id);
            stream.write(data);
            stream.getFD().sync();
        }

        if (file.exists()) {
            Files.delete(file.toPath());
        }

        if (!tempFile.renameTo(file)) {
            Files.copy(tempFile.toPath(), file.toPath());
            Files.delete(tempFile.toPath());
        }
    }

    public void saveQuietly() {
        try {
            save();
        } catch (IOException e) {
            System.err.println("Failed to save database " + id + ": " + e.getMessage());
        }
    }

    static public byte[] encodeEncrypted(@Nullable Object object, String dbId) {
        byte[] data = Serializer.encode(object);
        return encrypt(data, dbId);
    }

    static public @Nullable Object decodeEncrypted(byte[] buffer, String dbId) {
        byte[] decrypted = decrypt(buffer, dbId);
        if (decrypted == null) return null;
        return Serializer.decode(decrypted);
    }

    private static byte[] encrypt(byte[] data, String dbId) {
        try {
            SecretKey key = generateKey(dbId);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, new javax.crypto.spec.IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(data);

            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] decrypt(byte[] encryptedData, String dbId) {
        try {
            if (encryptedData.length < 16) return null;

            byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);
            byte[] data = Arrays.copyOfRange(encryptedData, 16, encryptedData.length);

            SecretKey key = generateKey(dbId);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new javax.crypto.spec.IvParameterSpec(iv));

            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static SecretKey generateKey(String dbId) {
        try {
            PBEKeySpec spec = new PBEKeySpec(dbId.toCharArray(), SALT, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate key", e);
        }
    }
}