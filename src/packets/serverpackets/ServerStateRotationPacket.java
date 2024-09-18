package packets.serverpackets;

import objects.Vertex;

//This class represents a server packet that is used to send the rotation state of a player (or object) to the client.
//It extends the ServerStatePacket class, which is a more general class for sending state updates (e.g., position, rotation).
public class ServerStateRotationPacket extends ServerStatePacket {
    
    public String name;   //The name or identifier of the player/object whose rotation is being sent
    public Vertex vertex; //The rotation values (pitch, yaw, roll or equivalent) represented by a Vertex object

    //Constructor to initialize the packet with the name of the player/object and the rotation data (stored as a Vertex).
    public ServerStateRotationPacket(String name, Vertex vertex) {
        this.serverStateType = serverStateTypeEnum.ROTATION; //Set the packet type to ROTATION
        this.name = name;    //Store the player/object name
        this.vertex = vertex; //Store the rotation data as a Vertex object
    }

    //Converts the packet into a string format that can be sent over the network.
    //The resulting string will contain the player's name followed by the rotation coordinates (x, y, z).
    @Override
    public String toString() {
        //The format will look like: "START SERVER ROTATION <name> <x> <y> <z> END"
        return super.wrapString(name + " " +  vertex.x + " " + vertex.y + " " + vertex.z);
    }

}
