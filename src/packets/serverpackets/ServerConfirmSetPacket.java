package packets.serverpackets;
import packets.ServerPacket;

public class ServerConfirmSetPacket extends ServerConfirmPacket{
    public String toString() {
        return super.wrapString("SET");
    }
}
