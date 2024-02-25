package packets.serverpackets;

import packets.ServerPacket;
import objects.Vertex;

public class ServerStatePositionPacket extends ServerStatePacket{
    public String name;
    public Vertex vertex;

    public ServerStatePositionPacket(String name, Vertex vertex) {
        this.name = name;
        this.vertex = vertex;
    }

    public String toString() {
        return super.wrapString("POSITION " + name + " " +  vertex.x + " " + vertex.y + " " + vertex.z);
    }

}
