package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class UDPPeer {
    private final DatagramSocket socket;
    private volatile boolean running = false;
    private Thread receiveThread;

    private PacketReceiver packetReceiver;
    private Consumer<Exception> errorHandler;

    @FunctionalInterface
    public interface PacketReceiver {
        void onReceive(Packet packet, String ip, int port);
    }

    public UDPPeer(short port) throws SocketException {
        this.socket = new DatagramSocket(port & 0xFFFF);
        startReceiving();
    }

    public void send(Packet packet, String ip, int port) {
        try {
            byte[] data = packet.toByteArray();
            DatagramPacket datagram = new DatagramPacket(
                    data, data.length,
                    InetAddress.getByName(ip), port
            );
            socket.send(datagram);
        } catch (IOException e) {
            if (errorHandler != null) {
                errorHandler.accept(e);
            }
        }
    }

    public void broadcast(Packet packet, int port) {
        try {
            byte[] data = packet.toByteArray();
            DatagramPacket datagram = new DatagramPacket(
                    data, data.length,
                    InetAddress.getByName("255.255.255.255"), port
            );
            socket.send(datagram);
        } catch (IOException e) {
            if (errorHandler != null) errorHandler.accept(e);
        }
    }

    private void startReceiving() {
        running = true;
        receiveThread = new Thread(() -> {
            byte[] buffer = new byte[65507];
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);

            while (running && !socket.isClosed()) {
                try {
                    socket.receive(datagram);

                    byte[] data = new byte[datagram.getLength()];
                    System.arraycopy(datagram.getData(), 0, data, 0, datagram.getLength());

                    String ip = datagram.getAddress().getHostAddress();
                    int port = datagram.getPort();

                    Packet packet = Packet.fromByteArray(data);

                    if (packetReceiver != null) {
                        packetReceiver.onReceive(packet, ip, port);
                    }

                } catch (SocketException e) {
                    if (running && errorHandler != null) {
                        errorHandler.accept(e);
                    }
                    break;
                } catch (IOException e) {
                    if (running && errorHandler != null) {
                        errorHandler.accept(e);
                    }
                }
            }
        });
        receiveThread.start();
    }

    public void onReceive(PacketReceiver handler) {
        this.packetReceiver = handler;
    }

    public void onError(Consumer<Exception> handler) {
        this.errorHandler = handler;
    }

    public void close() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (receiveThread != null) {
            receiveThread.interrupt();
        }
    }

    public int getPort() {
        return socket.getLocalPort();
    }

    public boolean isRunning() {
        return running && !socket.isClosed();
    }
}