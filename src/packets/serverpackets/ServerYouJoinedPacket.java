package packets.serverpackets;

import packets.ServerPacket;

//This class represents a specific type of server packet that is sent to notify a client that they have successfully joined the server.
//It extends the ServerPacket class, which provides the basic structure for server-side packets.
public class ServerYouJoinedPacket extends ServerPacket {

    //Constructor that initializes the packet and sets the packet type to YOUJOINED.
    //This packet type indicates that the server is informing the client they have successfully joined the game/session.
    public ServerYouJoinedPacket() {
        serverDataType = serverDataTypeEnum.YOUJOINED; //Set the data type of this packet to YOUJOINED.
    }

    //Converts the packet into a string that can be sent over the network.
    //Since this packet doesn't carry additional data, it simply wraps an empty string.
    @Override
    public String toString() {
        return super.wrapString(""); //Call the parent class's wrapString method with an empty string.
                                     //The resulting string will look like: "START SERVER YOUJOINED END".
    }
}
