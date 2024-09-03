package packets;

public abstract class ServerPacket extends Packet {
    public static enum serverDataTypeEnum {
        STATE,
        CONFIRM,
        YOUJOINED
    }
    public serverDataTypeEnum serverDataType;

    public ServerPacket() {
        packetRole = packetRoleEnum.SERVER;
    }

    public String wrapString(String valueToWrap) {
        return super.wrapString(serverDataType.name() + " " + valueToWrap);
    }
}