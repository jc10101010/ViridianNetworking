package packets.clientpackets;

import packets.ClientPacket;

public class ClientRequestPacket extends ClientPacket {

    public ClientRequestPacket() {
        clientDataType = clientDataTypeEnum.REQUEST;
    }

    public String toString() {
        return super.wrapString("");
    } 
}
