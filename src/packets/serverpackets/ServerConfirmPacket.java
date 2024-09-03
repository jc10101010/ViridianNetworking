package packets.serverpackets;
import packets.ServerPacket;

import packets.ServerPacket;

public class ServerConfirmPacket extends ServerPacket{

    public ServerConfirmPacket() {
        serverDataType = serverDataTypeEnum.CONFIRM;
    }

    public String toString() {
        return super.wrapString("");
    }
}
