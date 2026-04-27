package server.user;

public class UserProfile {
    private String username;
    private String displayName;
    private String bio;
    private long registeredAt;
    private int messageCount;

    public UserProfile(String username) {
        this.username = username;
        this.displayName = username;
        this.bio = "";
        this.registeredAt = System.currentTimeMillis();
        this.messageCount = 0;
    }

    public UserProfile(String username, String displayName, String bio, long registeredAt) {
        this.username = username;
        this.displayName = displayName != null && !displayName.isEmpty() ? displayName : username;
        this.bio = bio != null ? bio : "";
        this.registeredAt = registeredAt;
        this.messageCount = 0;
    }

    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getBio() { return bio; }
    public long getRegisteredAt() { return registeredAt; }
    public int getMessageCount() { return messageCount; }

    public void setMessageCount(int count) { this.messageCount = count; }
    public void setDisplayName(String displayName) {
        this.displayName = displayName != null && !displayName.isEmpty() ? displayName : username;
    }
    public void setBio(String bio) {
        this.bio = bio != null ? bio : "";
    }
}