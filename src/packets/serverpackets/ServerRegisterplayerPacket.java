package packets.serverpackets;

import packets.ServerPacket;

public class ServerRegisterplayerPacket extends ServerPacket{
    private String name;

    public ServerRegisterplayerPacket(String name) {
        this.name = name;
    }

    public String toString() {
        return super.wrapString("ADD " + name);
    }
}
