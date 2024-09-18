package packets.clientpackets;

import packets.ClientPacket;

//This class represents a client packet that is used to notify the server that the client is leaving the game or session.
//It extends the ClientPacket class, which provides the basic structure for all client-originated packets.
public class ClientLeavePacket extends ClientPacket {

    //Constructor to initialize the packet and set the client data type to LEAVE.
    //LEAVE packets are sent by the client to inform the server that the client is disconnecting or leaving the game/session.
    public ClientLeavePacket() {
        clientDataType = clientDataTypeEnum.LEAVE; //Set the data type of this packet to LEAVE, indicating the client is leaving.
    }

    //Converts the packet into a string format that can be sent over the network.
    //Since a leave packet doesn't carry additional data, it simply wraps an empty string.
    @Override
    public String toString() {
        //Calls the parent class's wrapString method to generate the formatted string.
        //The final string will look like: "START CLIENT LEAVE END".
        return super.wrapString("");
    }
}
