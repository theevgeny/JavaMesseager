package network.protocol;

import network.Packet;

public class ConnectionAcceptPacket {
    private byte encryptionKeySize = 16;
    private byte[] encryptionKey = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public ConnectionAcceptPacket(byte[] encryptionKey) {
        if (encryptionKey.length > 128) throw new IllegalArgumentException();
        this.encryptionKeySize = (byte)encryptionKey.length;
        this.encryptionKey = encryptionKey;
    }

    public ConnectionAcceptPacket(Packet packet) {
        try {
            packet.readByte();
            this.encryptionKeySize = packet.readByte();
            for (byte i = 0; i < this.encryptionKeySize; ++i) {
                this.encryptionKey[i] = packet.readByte();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(4);
        pk.writeByte(encryptionKeySize);
        for (int i = 0; i < encryptionKeySize; ++i) {
            pk.writeByte(encryptionKey[i]);
        }
        return pk;
    }
}
