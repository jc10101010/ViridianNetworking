package client;

import colours.InverseSqrShadow;
import core.RenderObject;
import core.Scene;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;
import objects.*;
import packets.Packet;
import packets.clientpackets.ClientJoinPacket;
import packets.clientpackets.ClientRequestPacket;
import packets.clientpackets.setters.ClientSetPositionPacket;
import packets.clientpackets.setters.ClientSetRotationPacket;
import packets.serverpackets.ServerStatePositionPacket;
import packets.serverpackets.ServerStateRotationPacket;
import packets.serverpackets.ServerYouJoinedPacket;

public class MPGameClient {
    
    //MAIN METHOD - Entry point for the client
    public static void main(String[] args) throws IOException, InterruptedException {
        //Setup input reader for manual input from the console
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        //Create a new game client with an empty scene and connect to the server
        MPGameClient client = new MPGameClient(new Scene(new ArrayList<>()));
        
        //Main loop for reading input, sending it to the server, and processing responses
        while (true) {
            String input = reader.readLine();
            client.sendString(input); //Send user input as a packet to the server
            client.receivePackets();   //Receive packets from the server
            
            //Process any received packets
            while (client.isPacketAvailable()) {
                System.out.println("Server said: " + client.getFirstPacket().toString());
            }
        }
        //client.close(); //Closing the client when necessary (currently commented out)
    }

    //ATTRIBUTES
    private DatagramChannel dataChannel; //Datagram channel for communication
    private ArrayList<Packet> unhandledPackets = new ArrayList<>(); //List of packets received but not yet processed
    private Packet currentPacket = null; //Current packet being processed
    public static Integer port = 4600; //Port number for connecting to the server
    private Vertex player_position, player_rotation; //Player's position and rotation
    private boolean has_joined_game; //Flag to track if the player has successfully joined the game
    private HashMap<String, RenderObject> nameToObject; //Mapping between player names and their respective objects in the scene
    private Scene game_scene; //The current game scene

    //CONSTRUCTOR - Initializes the client and sets up networking
    public MPGameClient(Scene game_scene) throws IOException, InterruptedException {
        //Open a DatagramChannel for communication
        dataChannel = DatagramChannel.open();
        dataChannel.bind(null); //Bind to any available port on the client
        dataChannel.configureBlocking(false); //Non-blocking mode for communication
        has_joined_game = false; //Player has not joined the game yet
        player_position = new Vertex(0, 0, 0); //Initialize player's position at origin
        player_rotation = new Vertex(0, 0, 0); //Initialize player's rotation
        this.game_scene = game_scene; //Set the current game scene
        this.nameToObject = new HashMap<>(); //Initialize the name-to-object mapping
    }

    //MAIN CLIENT LOOP - Sends player state to the server and processes responses
    public void run_client() throws IOException, InterruptedException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            //Send the appropriate packets to the server based on the game state
            if (has_joined_game) {
                sendPacket(new ClientSetPositionPacket(player_position)); //Send player's position
                sendPacket(new ClientSetRotationPacket(player_rotation)); //Send player's rotation
                sendPacket(new ClientRequestPacket()); //Request state updates from the server
            } else {
                sendPacket(new ClientJoinPacket()); //Send join request if not already in the game
            }
            
            //Receive and process incoming packets from the server
            receivePackets();

            while (isPacketAvailable()) {
                System.out.println("Server said: " + getFirstPacket().toString());
                processPacket(getFirstPacket()); //Process each received packet
            }

            Thread.sleep(50); //Control frame rate (approx. 20 frames per second)
        }
    }

    //Process received packets and handle them accordingly
    public void processPacket(Packet packet) {
        Class packetClass = packet.getClass();

        //If the server confirms that the client has joined the game
        if (packetClass.equals(ServerYouJoinedPacket.class)){
            if (!has_joined_game) {
                System.out.println("YOU JOINED THE GAME");
                has_joined_game = true; //Mark player as having joined the game
            } else {
                System.out.println("YOU ARE ALREADY IN THE GAME");
            }
            
        //If the server sends an update with another player's position
        } else if (packetClass.equals(ServerStatePositionPacket.class)) {
            if (has_joined_game) {
                ServerStatePositionPacket p = (ServerStatePositionPacket) packet;
                processPositionPacket(p); //Update the position of the player
                System.out.println("PLAYER : " + p.name + " IS AT LOCATION " + p.vertex.x + " " + p.vertex.y + " " + p.vertex.z);
            } else {
                System.out.println("YOU CANNOT LOAD POSITIONS, YOU ARE NOT IN A GAME");
            }
            
        //If the server sends an update with another player's rotation
        }  else if (packetClass.equals(ServerStateRotationPacket.class)) {
            if (has_joined_game) {
                ServerStateRotationPacket p = (ServerStateRotationPacket) packet;
                processRotationPacket(p); //Update the rotation of the player
                System.out.println("PLAYER : " + p.name + " IS AT LOCATION " + p.vertex.x + " " + p.vertex.y + " " + p.vertex.z);
            } else {
                System.out.println("YOU CANNOT LOAD ROTATIONS, YOU ARE NOT IN A GAME");
            }
        }
    }

    //Processes a packet containing another player's position update
    public void processPositionPacket(ServerStatePositionPacket packet) {
        String name = packet.name;
        Vertex player_pos = packet.vertex;

        //If the player is already known, update their position
        if (nameToObject.keySet().contains(name)) {
            RenderObject obj = nameToObject.get(name);
            obj.setPosition(player_pos);
        } else {
            //If the player is new, load their model and add them to the game scene
            RenderObject obj = RenderObject.loadObject("data/monkey.obj", "player", new InverseSqrShadow(new Color(0,0,255), game_scene), new Vertex(0,0f,0));
            nameToObject.put(name, obj); //Map their name to the object
            game_scene.addObject(obj); //Add the object to the game scene
        }
    }

    //Processes a packet containing another player's rotation update
    public void processRotationPacket(ServerStateRotationPacket packet) {
        String name = packet.name;
        Vertex player_pos = packet.vertex;

        //If the player is already known, update their rotation
        if (nameToObject.keySet().contains(name)) {
            RenderObject obj = nameToObject.get(name);
            Vertex alteredRotation = new Vertex(player_pos.x, (float) (player_pos.y + Math.PI), player_pos.z); //Adjust rotation for visual alignment
            obj.setRotation(alteredRotation);
        } else {
            //If the player is new, load their model and add them to the game scene
            RenderObject obj = RenderObject.loadObject("data/monkey.obj", "player", new InverseSqrShadow(new Color(0,0,255), game_scene), new Vertex(0,0f,0));
            nameToObject.put(name, obj); //Map their name to the object
            game_scene.addObject(obj); //Add the object to the game scene
        }
    }

    //Sends a packet to the server
    public void sendPacket(Packet packet) throws IOException {
        sendString(packet.toString());
    }

    //Sends a string message to the server
    public void sendString(String value) throws IOException {
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", port);
        ByteBuffer buffer = ByteBuffer.wrap(value.getBytes());
        dataChannel.send(buffer, serverAddress); //Send the message to the server
        buffer.clear(); //Clear the buffer after sending
    }

    //Receives packets from the server (non-blocking) and adds them to the unhandled packets list
    public void receivePackets() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketAddress add = dataChannel.receive(buffer);
        if (add != null) {
            unhandledPackets.add(Packet.byteBufferToPacket(buffer)); //Add received packet to unhandled list
            receivePackets(); //Recursively check for more packets
        }
    }

    //Checks if there are any unhandled packets available
    public boolean isPacketAvailable() {
        if (unhandledPackets.size() == 0) {
            return false; //No packets available
        } else {
            currentPacket = unhandledPackets.get(0); //Get the first unhandled packet
            unhandledPackets.remove(0); //Remove it from the queue
            return true; //Packet available
        }
    }

    //Returns the current packet being processed
    public Packet getFirstPacket() {
        return currentPacket;
    }

    //Closes the data channel (shuts down the client)
    public void close() throws IOException {
        dataChannel.close();
    }

    //GETTERS AND SETTERS -------------------------------------------------------

    public Vertex getPlayerPosition() {
        return player_position;
    }

    public void setPlayerPosition(Vertex player_new_position) {
        this.player_position = player_new_position;
    }

    public Vertex getPlayerRotation() {
        return player_rotation;
    }

    public void setPlayerRotation(Vertex player_new_rotation) {
        this.player_rotation = player_new_rotation;
    }

}
