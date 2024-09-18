package packets.clientpackets.setters;

import objects.Vertex;

//This class represents a client packet that is used to update the player's rotation on the server.
//It extends the ClientSetPacket class, which handles packets that modify or "set" some aspect of the player's state.
public class ClientSetRotationPacket extends ClientSetPacket {
    
    //A Vertex object representing the player's rotation in 3D space (pitch, yaw, roll or equivalent).
    public Vertex rotation;

    //Constructor that initializes the packet with the player's rotation.
    //It also sets the clientSetType to ROTATION, indicating that this packet is specifically for updating rotation.
    public ClientSetRotationPacket(Vertex rotation) {
        clientSetType = clientSetTypeEnum.ROTATION; //Set the client set type to ROTATION
        this.rotation = rotation; //Store the rotation data (x, y, z representing the 3D rotation)
    }

    //Converts the packet into a string format that can be sent over the network.
    //The string includes the x, y, and z components of the player's rotation.
    @Override
    public String toString() {
        //Calls the wrapString method to format the rotation data.
        //The final string will look like: "START CLIENT SET ROTATION <x> <y> <z> END".
        return wrapString(rotation.x + " " + rotation.y + " " + rotation.z);
    }
}
