package packets.serverpackets;

import objects.Vertex;

//This class represents a server packet that is used to send the position state of a player or object to the client.
//It extends the ServerStatePacket class, which is a more general class for sending state updates such as position or rotation.
public class ServerStatePositionPacket extends ServerStatePacket {

    public String name;   //The name or identifier of the player/object whose position is being sent
    public Vertex vertex; //The position values (x, y, z coordinates) represented by a Vertex object

    //Constructor to initialize the packet with the player's name and position data (stored as a Vertex object).
    public ServerStatePositionPacket(String name, Vertex vertex) {
        this.serverStateType = serverStateTypeEnum.POSITION; //Set the packet type to POSITION
        this.name = name;    //Store the player/object name
        this.vertex = vertex; //Store the position data as a Vertex object (containing x, y, z coordinates)
    }

    //Converts the packet into a string format that can be sent over the network.
    //The resulting string will contain the player's name followed by the position coordinates (x, y, z).
    @Override
    public String toString() {
        //The format will look like: "START SERVER POSITION <name> <x> <y> <z> END"
        return super.wrapString(name + " " +  vertex.x + " " + vertex.y + " " + vertex.z);
    }

}
