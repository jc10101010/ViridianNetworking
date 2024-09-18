package packets.clientpackets;

import packets.ClientPacket;

//This class represents a client packet that is used to send a state update request to the server.
//It extends the ClientPacket class and is typically used when the client wants to request the state updates from the server.
public class ClientRequestPacket extends ClientPacket {

    //Constructor to initialize the packet and set the client data type to REQUEST.
    //REQUEST packets are sent by the client to ask the server for information, such as the state of the game or other players.
    public ClientRequestPacket() {
        clientDataType = clientDataTypeEnum.REQUEST; //Set the data type of this packet to REQUEST.
    }

    //Converts the packet into a string that can be sent over the network.
    //Since a request packet doesn't carry additional data, it simply wraps an empty string.
    @Override
    public String toString() {
        //Calls the parent class's wrapString method to generate the formatted string.
        //The final string will look like: "START CLIENT REQUEST END".
        return super.wrapString("");
    }
}
