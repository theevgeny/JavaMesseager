package server.user;

public class ClientSession {
    private final String username;
    private final String ip;
    private final int port;

    public ClientSession(String username, String ip, int port) {
        this.username = username;
        this.ip = ip;
        this.port = port;
    }

    public String getUsername() { return username; }
    public String getIp() { return ip; }
    public int getPort() { return port; }
}