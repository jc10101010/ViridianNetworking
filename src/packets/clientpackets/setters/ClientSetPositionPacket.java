package packets.clientpackets.setters;

import objects.Vertex;

public class ClientSetPositionPacket extends ClientSetPacket {
    public Vertex position;

    public ClientSetPositionPacket(Vertex position) {
        clientSetType = clientSetTypeEnum.POSITION;
        this.position = position;
    }

    public String toString() {
        return wrapString(position.x + " " + position.y + " " + position.z);
    }

}
