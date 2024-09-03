package packets.serverpackets;
import packets.ServerPacket;

public class ServerConfirmJoinPacket extends ServerConfirmPacket{
    public String toString() {
        return super.wrapString("JOIN");
    }
}
