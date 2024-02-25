package packets;

public abstract class ClientPacket extends Packet {
    public static enum clientDataTypeEnum {
        JOIN,
        LEAVE,
        REQUEST,
        SET
    }
    public clientDataTypeEnum clientDataType;

    public ClientPacket() {
        packetRole = packetRoleEnum.CLIENT;
    }

    public String wrapString(String valueToWrap) {
        return super.wrapString(clientDataType.name() + " " + valueToWrap);
    }
}