package packets.clientpackets;

import packets.ClientPacket;

public class ClientLeavePacket extends ClientPacket {
    
    public ClientLeavePacket() {
        clientDataType = clientDataTypeEnum.LEAVE;
    }

    public String toString() {
        return super.wrapString("");
    } 
}
