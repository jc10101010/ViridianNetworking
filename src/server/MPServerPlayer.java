package server;

import objects.Vertex;

public class MPServerPlayer {
    //Attributes for player state and data
    private String name;  //Player's unique identifier (name)
    private Vertex position, rotation;  //Position and rotation of the player in the game world

    //Constructor to initialize a new player with a name
    public MPServerPlayer(String playerName) {
        name = playerName;  //Assign player name
        //Initialize player's position and rotation to (0, 0, 0)
        position = new Vertex(0, 0, 0);
        rotation = new Vertex(0, 0, 0);
    }

    //Getter for the player's current position
    public Vertex getPosition() {
        return position;
    }

    //Setter to update the player's position
    public void setPosition(Vertex position) {
        this.position = position;
    }

    //Getter for the player's current rotation
    public Vertex getRotation() {
        return rotation;
    }

    //Setter to update the player's rotation
    public void setRotation(Vertex rotation) {
        this.rotation = rotation;
    }

    //Getter for the player's name
    public String getName() {
        return name;
    }
}
