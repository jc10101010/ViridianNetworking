package packets.clientpackets.setters;

import packets.ClientPacket;

//Abstract class representing a packet that modifies or "sets" some aspect of the player's state (e.g., position or rotation).
//It extends the ClientPacket class and is designed to be subclassed for specific types of state updates.
public abstract class ClientSetPacket extends ClientPacket {

    //Enum defining the types of "set" operations that the client can request.
    //Currently supports POSITION (for updating the player's position) and ROTATION (for updating the player's orientation).
    public static enum clientSetTypeEnum {
        POSITION,
        ROTATION
    }

    //Field to store the specific type of "set" operation (POSITION or ROTATION).
    //This will be set by subclasses of ClientSetPacket, such as ClientSetPositionPacket or ClientSetRotationPacket.
    public clientSetTypeEnum clientSetType;

    //Constructor to initialize the packet and set the client data type to SET.
    //This tells the server that this packet is related to setting some aspect of the player's state.
    public ClientSetPacket() {
        clientDataType = clientDataTypeEnum.SET; //Set the client data type to SET, indicating that this is a state update packet.
    }

    //Method to wrap the "set" data into a string format that can be sent over the network.
    //This adds the clientSetType (e.g., POSITION or ROTATION) before the actual data, ensuring the correct format.
    @Override
    public String wrapString(String setValueToWrap) {
        //The format will look like: "START CLIENT SET <clientSetType> <data> END"
        //For example: "START CLIENT SET POSITION <x> <y> <z> END" or "START CLIENT SET ROTATION <x> <y> <z> END"
        return super.wrapString(clientSetType.name() + " " + setValueToWrap);
    }
}
