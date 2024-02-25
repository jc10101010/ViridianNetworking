package packets.serverpackets;
import packets.ServerPacket;

import packets.ServerPacket;

public abstract class ServerConfirmPacket extends ServerPacket{
    public String wrapString(String toWrap) {
        return super.wrapString("CONFIRM " + toWrap);
    }
}
