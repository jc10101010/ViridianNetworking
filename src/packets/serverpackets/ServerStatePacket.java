package packets.serverpackets;

import packets.ServerPacket;

public abstract class ServerStatePacket extends ServerPacket{

    public static enum serverStateTypeEnum {
        POSITION,
        ROTATION
    }
    public serverStateTypeEnum serverStateType;

    public ServerStatePacket() {
        serverDataType = serverDataTypeEnum.STATE;
    }
    
    @Override
    public String wrapString(String toWrap) {
        return super.wrapString(serverStateType.name() + " " + toWrap);
    }
}