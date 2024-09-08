package packets.clientpackets.setters;

import objects.Vertex;

public class ClientSetRotationPacket extends ClientSetPacket {
    public Vertex rotation;

    public ClientSetRotationPacket(Vertex rotation) {
        clientSetType = clientSetTypeEnum.ROTATION;
        this.rotation = rotation;
    }

    public String toString() {
        return wrapString(rotation.x + " " + rotation.y + " " + rotation.z);
    }

}
