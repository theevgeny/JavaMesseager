package network.protocol;

import network.Packet;
import network.protocol.types.LoginFailCause;

public class LoginFailPacket {
    private LoginFailCause loginFailCause = LoginFailCause.EMPTY;

    public LoginFailPacket(LoginFailCause loginFailCause) {
        this.loginFailCause = loginFailCause;
    }

    public LoginFailPacket(Packet packet) {
        try {
            packet.readByte();
            this.loginFailCause = LoginFailCause.fromByte(packet.readByte());
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(8);
        pk.writeByte(loginFailCause.toByte());
        return pk;
    }

    public LoginFailCause getCause() {
        return loginFailCause;
    }
}
