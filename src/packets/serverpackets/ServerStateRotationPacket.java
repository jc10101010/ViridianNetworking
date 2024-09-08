package packets.serverpackets;

import objects.Vertex;

public class ServerStateRotationPacket extends ServerStatePacket{
    public String name;
    public Vertex vertex;

    public ServerStateRotationPacket(String name, Vertex vertex) {
        this.serverStateType = serverStateTypeEnum.ROTATION;
        this.name = name;
        this.vertex = vertex;
    }

    public String toString() {
        return super.wrapString(name + " " +  vertex.x + " " + vertex.y + " " + vertex.z);
    }

}
