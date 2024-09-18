package packets.serverpackets;

import packets.ServerPacket;

//This class represents a server packet that is used to send confirmation messages to the client.
//It extends the ServerPacket class and is typically used to acknowledge that a certain action has been successfully processed by the server.
public class ServerConfirmPacket extends ServerPacket {

    //Constructor to initialize the packet and set the server data type to CONFIRM.
    //CONFIRM packets are used to indicate successful completion or acknowledgment of a request made by the client.
    public ServerConfirmPacket() {
        serverDataType = serverDataTypeEnum.CONFIRM; //Set the data type of this packet to CONFIRM.
    }

    //Converts the packet into a string that can be sent over the network.
    //Since a confirmation packet doesn't need to carry additional data, it simply wraps an empty string.
    @Override
    public String toString() {
        //Calls the parent class's wrapString method to generate the formatted string.
        //The final string will look like: "START SERVER CONFIRM END".
        return super.wrapString("");
    }
}
