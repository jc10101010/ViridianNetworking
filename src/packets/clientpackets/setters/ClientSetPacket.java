package packets.clientpackets.setters;

import packets.ClientPacket;

public abstract class ClientSetPacket extends ClientPacket {// subclasses do not have datatypes for now
    public static enum clientSetTypeEnum {
        POSITION,
        ROTATION
    }
    public clientSetTypeEnum clientSetType;

    public ClientSetPacket() {
        clientDataType = clientDataTypeEnum.SET;
    }

    public String wrapString(String setValueToWrap) {
        return super.wrapString(clientSetType.name() + " " + setValueToWrap);
    }
}
