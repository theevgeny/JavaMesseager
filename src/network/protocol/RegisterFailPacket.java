package network.protocol;

import network.Packet;
import network.protocol.types.RegisterFailCause;

public class RegisterFailPacket {
    private RegisterFailCause registerFailCause = RegisterFailCause.EMPTY;

    public RegisterFailPacket(RegisterFailCause registerFailCause) {
        this.registerFailCause = registerFailCause;
    }

    public RegisterFailPacket(Packet packet) {
        try {
            packet.readByte();
            this.registerFailCause = RegisterFailCause.fromByte(packet.readByte());
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Packet getPacket() {
        Packet pk = new Packet();
        pk.writeByte(11);
        pk.writeByte(registerFailCause.toByte());
        return pk;
    }

    public RegisterFailCause getCause() {
        return registerFailCause;
    }
}
