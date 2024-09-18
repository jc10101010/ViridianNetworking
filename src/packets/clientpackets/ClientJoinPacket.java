package packets.clientpackets;

import packets.ClientPacket;

//This class represents a client packet that is used to notify the server that the client is joining the game or session.
//It extends the ClientPacket class, which provides the basic structure for all packets sent from the client to the server.
public class ClientJoinPacket extends ClientPacket {

    //Constructor to initialize the packet and set the client data type to JOIN.
    //JOIN packets are sent by the client to inform the server that they are attempting to join the game/session.
    public ClientJoinPacket() {
        clientDataType = clientDataTypeEnum.JOIN; //Set the data type of this packet to JOIN, indicating a client join request.
    }

    //Converts the packet into a string format that can be sent over the network.
    //Since a join packet doesn't carry additional data, it simply wraps an empty string.
    @Override
    public String toString() {
        //Calls the parent class's wrapString method to generate the formatted string.
        //The final string will look like: "START CLIENT JOIN END".
        return super.wrapString("");
    }
}
