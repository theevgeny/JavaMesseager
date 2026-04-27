package server.user;

import database.SDataBase;
import database.SDataBaseSection;
import network.protocol.types.LoginFailCause;
import network.protocol.types.RegisterFailCause;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserDataBase {
    private final SDataBase db;
    private final SDataBaseSection profiles;

    public UserDataBase() {
        this.db = new SDataBase(Paths.get("").toAbsolutePath() + "/db", "users");

        if (db.get("profiles") == null) {
            db.put("profiles", new HashMap<>());
        }
        this.profiles = db.getSection("profiles");
    }

    public RegisterFailCause register(String username, String password) {
        if (username == null || username.trim().isEmpty())
            return RegisterFailCause.INVALID_USERNAME;
        if (username.length() < 3 || username.length() > 20)
            return RegisterFailCause.INVALID_USERNAME;
        if (password == null || password.length() < 3)
            return RegisterFailCause.PASSWORD_IS_SIMPLE;
        if (db.contains(username))
            return RegisterFailCause.USERNAME_IS_ALREADY;

        db.put(username, password);

        UserProfile profile = new UserProfile(username);
        saveProfile(profile);

        db.saveQuietly();
        return null;
    }

    public LoginFailCause login(String username, String password) {
        if (!db.contains(username))
            return LoginFailCause.INVALID_USERNAME;
        String stored = (String) db.get(username);
        if (stored == null || !stored.equals(password))
            return LoginFailCause.INVALID_PASSWORD;
        return null;
    }

    public ArrayList<String> getAllUsernames() {
        ArrayList<String> usernames = new ArrayList<>();
        Map<Object, Object> rawMap = db.getRawMap();
        for (Object key : rawMap.keySet()) {
            if (key instanceof String && !key.equals("profiles")) {
                usernames.add((String) key);
            }
        }
        return usernames;
    }

    public UserProfile getProfile(String username) {
        Map<Object, Object> profileMap = profiles.getMap(username);
        if (profileMap == null || profileMap.isEmpty()) {
            UserProfile defaultProfile = new UserProfile(username);
            saveProfile(defaultProfile);
            return defaultProfile;
        }

        String displayName = (String) profileMap.getOrDefault("displayName", username);
        String bio = (String) profileMap.getOrDefault("bio", "");
        long registeredAt = profileMap.containsKey("registeredAt") ?
                (long) profileMap.get("registeredAt") : System.currentTimeMillis();

        return new UserProfile(username, displayName, bio, registeredAt);
    }

    public void saveProfile(UserProfile profile) {
        HashMap<Object, Object> profileMap = new HashMap<>();
        profileMap.put("displayName", profile.getDisplayName());
        profileMap.put("bio", profile.getBio());
        profileMap.put("registeredAt", profile.getRegisteredAt());
        profiles.put(profile.getUsername(), profileMap);
        db.saveQuietly();
    }

    public boolean updateProfile(String username, String displayName, String bio) {
        if (!db.contains(username)) return false;

        UserProfile profile = getProfile(username);
        if (displayName != null && !displayName.trim().isEmpty()) {
            profile.setDisplayName(displayName.trim());
        }
        if (bio != null) {
            String trimmedBio = bio.length() > 500 ? bio.substring(0, 500) : bio;
            profile.setBio(trimmedBio);
        }
        saveProfile(profile);
        return true;
    }
}