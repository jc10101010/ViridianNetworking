package packets.clientpackets;

import packets.ClientPacket;

public class ClientJoinPacket extends ClientPacket {
    
    public ClientJoinPacket() {
        clientDataType = clientDataTypeEnum.JOIN;
    }

    public String toString() {
        return super.wrapString("");
    } 
}
