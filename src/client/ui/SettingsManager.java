package client.ui;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class SettingsManager {
    private static final Preferences prefs = Preferences.userNodeForPackage(SettingsManager.class);

    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_THEME = "theme";
    private static final String KEY_FONT_SIZE = "fontSize";

    private static boolean notificationsEnabled = true;
    private static boolean soundEnabled = true;
    private static String theme = "Dark";
    private static int fontSize = 13;

    static {
        load();
    }

    public static void load() {
        notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS, true);
        soundEnabled = prefs.getBoolean(KEY_SOUND, true);
        theme = prefs.get(KEY_THEME, "Dark");
        fontSize = prefs.getInt(KEY_FONT_SIZE, 13);
        if (fontSize < 8) fontSize = 13;
    }

    public static void save() {
        prefs.putBoolean(KEY_NOTIFICATIONS, notificationsEnabled);
        prefs.putBoolean(KEY_SOUND, soundEnabled);
        prefs.put(KEY_THEME, theme);
        prefs.putInt(KEY_FONT_SIZE, fontSize);
    }

    public static boolean areNotificationsEnabled() { return notificationsEnabled; }
    public static boolean isSoundEnabled() { return soundEnabled; }
    public static String getTheme() { return theme; }
    public static int getFontSize() { return fontSize; }

    public static void setNotificationsEnabled(boolean enabled) { notificationsEnabled = enabled; }
    public static void setSoundEnabled(boolean enabled) { soundEnabled = enabled; }
    public static void setTheme(String newTheme) { theme = newTheme; }
    public static void setFontSize(int size) { fontSize = size; }

    public static void applyFontSize(Component comp) {
        if (comp instanceof JComponent) {
            Font f = comp.getFont();
            if (f != null) {
                comp.setFont(f.deriveFont((float) fontSize));
            }
            if (comp instanceof Container) {
                for (Component child : ((Container) comp).getComponents()) {
                    applyFontSize(child);
                }
            }
        }
    }

    public static void showNotification(String title, String message) {
        if (!notificationsEnabled) return;
        if (SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                Image icon = Toolkit.getDefaultToolkit().createImage("icon.png");
                TrayIcon trayIcon = new TrayIcon(icon, "Мессенджер");
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);
                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
                tray.remove(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    public static void playMessageSound() {
        if (soundEnabled) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}