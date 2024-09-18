package packets;

//Abstract class representing any packet that originates from the client.
//Inherits from the base Packet class and adds client-specific details such as data type.
public abstract class ClientPacket extends Packet {

    //Enum defining the different types of data that a client packet may contain.
    //Examples: JOIN (for joining the server), LEAVE (for leaving), REQUEST (state requests), or SET (setting values like position/rotation).
    public static enum clientDataTypeEnum {
        JOIN,
        LEAVE,
        REQUEST,
        SET
    }

    //The specific type of client data this packet represents.
    //This will be set by subclasses to indicate the type of client packet (e.g., JOIN, LEAVE).
    public clientDataTypeEnum clientDataType;

    //Constructor for ClientPacket that automatically sets the packet role to CLIENT.
    //This differentiates it from server-originated packets.
    public ClientPacket() {
        packetRole = packetRoleEnum.CLIENT; //Set the packet role to CLIENT as this class is specific to client packets.
    }

    //Method to wrap the packet's data into a string format.
    //This calls the parent class's wrapString method and prepends the client data type (JOIN, LEAVE, etc.) to the value.
    @Override
    public String wrapString(String valueToWrap) {
        //The clientDataType is added before the actual packet data, creating a formatted string like:
        //"START CLIENT JOIN <data> END"
        return super.wrapString(clientDataType.name() + " " + valueToWrap);
    }
}
