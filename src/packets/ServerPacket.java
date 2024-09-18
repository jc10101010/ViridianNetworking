package packets;

//Abstract class representing any packet that originates from the server.
//Inherits from the base Packet class and adds server-specific details such as data type.
public abstract class ServerPacket extends Packet {

    //Enum defining the different types of data that a server packet may contain.
    //Examples: STATE (for sending game state updates), CONFIRM (for confirming actions), YOUJOINED (notifying client they joined).
    public static enum serverDataTypeEnum {
        STATE,
        CONFIRM,
        YOUJOINED
    }

    //The specific type of server data this packet represents.
    //Subclasses will set this field to indicate the type of server packet (e.g., STATE, CONFIRM).
    public serverDataTypeEnum serverDataType;

    //Constructor for ServerPacket that automatically sets the packet role to SERVER.
    //This helps differentiate it from client-originated packets.
    public ServerPacket() {
        packetRole = packetRoleEnum.SERVER; //Set the packet role to SERVER since this class is specific to server packets.
    }

    //Method to wrap the packet's data into a formatted string.
    //This calls the parent class's wrapString method and prepends the server data type (e.g., STATE, CONFIRM) to the value.
    @Override
    public String wrapString(String valueToWrap) {
        //The serverDataType is added before the actual packet data, creating a formatted string like:
        //"START SERVER STATE <data> END"
        return super.wrapString(serverDataType.name() + " " + valueToWrap);
    }
}
