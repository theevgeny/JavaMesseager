package server.user;

public class User {
    private String username;
    private String password;
    private long createdAt;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.createdAt = System.currentTimeMillis();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public long getCreatedAt() { return createdAt; }
}