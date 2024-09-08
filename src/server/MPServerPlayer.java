package server;

import java.util.ArrayList;
import objects.Vertex;
import packets.Packet;

public class MPServerPlayer {
    private String name;
    private Vertex position, rotation;
    private ArrayList<Packet> queue = new ArrayList<>();

    public MPServerPlayer(String playerName) {
        name = playerName;
        position = new Vertex(0, 0, 0);
        rotation = new Vertex(0, 0, 0);
    }

    public Vertex getPosition() {
        return position;
    }
    public void setPosition(Vertex position) {
        this.position = position;
    }

    
    public Vertex getRotation() {
        return rotation;
    }
    public void setRotation(Vertex rotation) {
        this.rotation = rotation;
    }

    public String getName() {
        return name;
    }

}
