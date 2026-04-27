package client;

import client.ui.LoginDialog;
import client.ui.MainWindow;
import server.message.Message;
import server.user.UserProfile;
import java.util.ArrayList;

public class Client {
    private ClientNetwork network;
    private LoginDialog loginDialog;
    private MainWindow mainWindow;
    private String username;

    public Client() {
        this.network = new ClientNetwork(this);
    }

    public void start() {
        loginDialog = new LoginDialog(this);
        loginDialog.show();
    }

    public void attemptLogin(String serverIp, int serverPort, String username, String password, boolean isRegister) {
        this.username = username;
        network.connect(serverIp, serverPort, username, password, isRegister);
    }

    public void onLoginSuccess() {
        if (loginDialog != null) {
            loginDialog.close();
        }
        mainWindow = new MainWindow(this);
        mainWindow.show();
        network.requestUserList();
        network.requestMessageHistory(0, 50, null);
        network.startUserListUpdater();
        network.requestMyProfile();
    }

    public void onLoginFailed(String reason) {
        if (loginDialog != null) {
            loginDialog.showError(reason);
        }
    }

    public void onRegisterSuccess() {
        if (loginDialog != null) {
            loginDialog.showInfo("Регистрация прошла успешно! Пожалуйста, войдите в систему.");
        }
    }

    public void onRegisterFailed(String reason) {
        if (loginDialog != null) {
            loginDialog.showError("Ошибка регистрации: " + reason);
        }
    }

    public void onMessageReceived(String sender, String message, long timestamp) {
        if (sender.equals(username)) return;
        if (mainWindow != null) {
            mainWindow.appendMessage(sender, message, timestamp, false);
            mainWindow.updateStatus("Новое сообщение от " + sender);
            mainWindow.onNewMessageReceived(sender, message);
        }
    }

    public void onUserListReceived(ArrayList<String> users) {
        if (mainWindow != null) {
            mainWindow.updateUserList(users);
        }
    }

    public void onHistoryReceived(ArrayList<Message> messages, int totalCount, int page, boolean hasMore) {
        if (mainWindow != null) {
            mainWindow.onHistoryReceived(messages, totalCount, page, hasMore);
        }
    }

    public void onProfileReceived(UserProfile profile) {
        if (mainWindow != null) {
            mainWindow.onProfileReceived(profile);
        }
    }

    public void onProfileUpdateSuccess() {
        if (mainWindow != null) {
            mainWindow.onProfileUpdateSuccess();
        }
    }

    public void sendMessage(String recipient, String message) {
        if (recipient == null || recipient.equals(username)) {
            if (mainWindow != null) {
                mainWindow.showError("Невозможно отправить сообщение себе");
            }
            return;
        }
        network.sendMessage(recipient, message);
        if (mainWindow != null) {
            mainWindow.appendMessage(username, message, System.currentTimeMillis(), true);
        }
    }

    public void requestConversation(String withUser, int page, int pageSize) {
        network.requestMessageHistory(page, pageSize, withUser);
    }

    public void requestProfile(String username) {
        network.requestProfile(username);
    }

    public void updateProfile(String displayName, String bio) {
        network.updateProfile(displayName, bio);
    }

    public void logout() {
        network.sendDisconnect();
        network.close();
        if (mainWindow != null) {
            mainWindow.close();
        }
        start();
    }

    public String getUsername() {
        return username;
    }

    public MainWindow getMainWindow() {
        return mainWindow;
    }
}