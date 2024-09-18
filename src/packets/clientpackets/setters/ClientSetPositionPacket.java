package packets.clientpackets.setters;

import objects.Vertex;

//This class represents a client packet that is used to update the player's position on the server.
//It extends the ClientSetPacket class, which handles packets that modify or "set" some aspect of the player's state.
public class ClientSetPositionPacket extends ClientSetPacket {
    
    //A Vertex object representing the player's position in 3D space (x, y, z coordinates).
    public Vertex position;

    //Constructor that initializes the packet with the player's position.
    //It also sets the clientSetType to POSITION, indicating that this packet is specifically for updating the player's position.
    public ClientSetPositionPacket(Vertex position) {
        clientSetType = clientSetTypeEnum.POSITION; //Set the client set type to POSITION
        this.position = position; //Store the position data (x, y, z representing the player's 3D position)
    }

    //Converts the packet into a string format that can be sent over the network.
    //The string includes the x, y, and z components of the player's position.
    @Override
    public String toString() {
        //Calls the wrapString method to format the position data.
        //The final string will look like: "START CLIENT SET POSITION <x> <y> <z> END".
        return wrapString(position.x + " " + position.y + " " + position.z);
    }
}
