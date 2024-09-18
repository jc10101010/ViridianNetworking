package packets.serverpackets;

import packets.ServerPacket;

//Abstract class representing a packet that sends state information (e.g., position or rotation) from the server to the client.
//It extends the ServerPacket class and adds functionality to handle specific state types such as POSITION and ROTATION.
public abstract class ServerStatePacket extends ServerPacket {

    //Enum representing the different types of state that can be sent by the server.
    //Currently supports POSITION (to send location data) and ROTATION (to send orientation data).
    public static enum serverStateTypeEnum {
        POSITION,
        ROTATION
    }

    //Field to store the specific type of state the packet is sending (POSITION or ROTATION).
    //This is set by the subclasses of ServerStatePacket.
    public serverStateTypeEnum serverStateType;

    //Constructor to initialize the server data type to STATE, indicating this packet is related to sending state information.
    public ServerStatePacket() {
        serverDataType = serverDataTypeEnum.STATE; //Set the serverDataType to STATE, as all ServerStatePacket instances deal with state updates.
    }

    //Override the wrapString method to include the server state type (POSITION or ROTATION) in the string.
    //This ensures that the serialized string includes the state type along with the rest of the packet data.
    @Override
    public String wrapString(String toWrap) {
        //Format will look like: "START SERVER STATE <serverStateType> <data> END"
        //For example: "START SERVER STATE POSITION <name> <x> <y> <z> END"
        return super.wrapString(serverStateType.name() + " " + toWrap);
    }
}
