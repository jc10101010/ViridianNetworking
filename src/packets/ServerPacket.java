package packets;

public abstract class ServerPacket extends Packet {
    public static enum serverDataTypeEnum {
        JOIN,
        LEAVE,
        REQUEST,
        SET
    }
    public clientDataTypeEnum clientDataType;

    public ServerPacket() {
        super();
        packetRole = packetRoleEnum.SERVER;
    }
}