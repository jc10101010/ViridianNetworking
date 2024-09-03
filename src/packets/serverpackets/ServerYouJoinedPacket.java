package packets.serverpackets;

import packets.ServerPacket;

public class ServerYouJoinedPacket extends ServerPacket{
 
    public ServerYouJoinedPacket() {
        serverDataType = serverDataTypeEnum.YOUJOINED;
    }

    public String toString() {
        return super.wrapString("");
    }

}
