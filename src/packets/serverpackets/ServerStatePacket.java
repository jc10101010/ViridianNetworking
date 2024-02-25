package packets.serverpackets;

import packets.ServerPacket;

public abstract class ServerStatePacket extends ServerPacket{
    
    @Override
    public String wrapString(String toWrap) {
        return super.wrapString("STATE " + toWrap);
    }
}