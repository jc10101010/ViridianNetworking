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
    // MAIN METHOD
    public static void main(String[] args) throws IOException, InterruptedException {
        // Setup input reader and client
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        MPGameClient client = new MPGameClient(new Scene(new ArrayList<>()));
        // While input is not done. read it and send it
        while (true) {
            String input = reader.readLine();
            client.sendString(input);
            client.receivePackets();
            while (client.isPacketAvailable()) {
                System.out.println("Server said: " + client.getFirstPacket().toString());

            }
        }
        // client.close();
    }

    // ATTRIBUTES
    private DatagramChannel dataChannel;
    private ArrayList<Packet> unhandledPackets = new ArrayList<>();
    private Packet currentPacket = null;
    public static Integer port = 4600;
    private Vertex player_position, player_rotation;
    private boolean has_joined_game;
    private HashMap<String, RenderObject> nameToObject;
    private Scene game_scene;


    // NORMAL METHODS
    // Sets up a client that can send messgaes to the server, hooks up to localhost
    public MPGameClient(Scene game_scene) throws IOException, InterruptedException {
        // Open datagram channel
        dataChannel = DatagramChannel.open();
        dataChannel.bind(null);
        dataChannel.configureBlocking(false);
        has_joined_game = false;
        player_position = new Vertex(0, 0, 0);
        player_rotation = new Vertex(0, 0, 0);
        this.game_scene = game_scene;
        this.nameToObject = new HashMap<>();
    }

    public void run_client() throws IOException, InterruptedException{

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            //QUEUE PACKETS TO SEND
            if (has_joined_game) {
                sendPacket(new ClientSetPositionPacket(player_position));
                sendPacket(new ClientSetRotationPacket(player_rotation));
                sendPacket(new ClientRequestPacket());
            } else {
                sendPacket(new ClientJoinPacket());
            }

            // String input = reader.readLine();
            // sendString(input);
            
            receivePackets();

            while (isPacketAvailable()) {
                System.out.println("Server said: " + getFirstPacket().toString());
                processPacket(getFirstPacket());
            }

            Thread.sleep(50); 
        }
    }

    public void processPacket(Packet packet) {
        Class packetClass = packet.getClass();

        if (packetClass.equals(ServerYouJoinedPacket.class)){
            if (!has_joined_game) {
                System.out.println("YOU JOINED BROTHER");
                has_joined_game = true;
            } else {
                System.out.println("YOU CANT JOIN, YOUR ALREADY IN A GAME");
            }
            
        } else if (packetClass.equals(ServerStatePositionPacket.class)) {
            if (has_joined_game) {
                ServerStatePositionPacket p = (ServerStatePositionPacket) packet;
                processPositionPacket(p);
                System.out.println("PLAYER : " + p.name + " IS AT LOCATION " + p.vertex.x + " " + p.vertex.y + " " + p.vertex.z);
            } else {
                System.out.println("YOU CANNOT LOAD POSITIONS AS YOU ARE NOT IN A GAME");
            }
            
        }  else if (packetClass.equals(ServerStateRotationPacket.class)) {
            if (has_joined_game) {
                ServerStateRotationPacket p = (ServerStateRotationPacket) packet;
                processRotationPacket(p);
                System.out.println("PLAYER : " + p.name + " IS AT LOCATION " + p.vertex.x + " " + p.vertex.y + " " + p.vertex.z);
            } else {
                System.out.println("YOU CANNOT LOAD ROTATIONS AS YOU ARE NOT IN A GAME");
            }
            
        }
    }

    public void processPositionPacket(ServerStatePositionPacket packet) {
        String name = packet.name;
        Vertex player_pos = packet.vertex;

        //If player already known about
        if (nameToObject.keySet().contains(name)) {

            RenderObject obj = nameToObject.get(name);
            obj.setPosition(player_pos);

        } else {

            RenderObject obj = RenderObject.loadObject("data/monkey.obj", "player", new InverseSqrShadow(new Color(0,0,255), game_scene), new Vertex(0,0f,0));
            nameToObject.put(name, obj);
            game_scene.addObject(obj);

        }
    }

    public void processRotationPacket(ServerStateRotationPacket packet) {
        String name = packet.name;
        Vertex player_pos = packet.vertex;

        //If player already known about
        if (nameToObject.keySet().contains(name)) {

            RenderObject obj = nameToObject.get(name);
            Vertex alteredRotation = new Vertex(player_pos.x, (float) (player_pos.y + Math.PI), player_pos.z);
            obj.setRotation(alteredRotation);

        } else {

            RenderObject obj = RenderObject.loadObject("data/monkey.obj", "player", new InverseSqrShadow(new Color(0,0,255), game_scene), new Vertex(0,0f,0));
            nameToObject.put(name, obj);
            game_scene.addObject(obj);

        }
    }

    // Takes in string and sends it then sends it to server
    public void sendPacket(Packet packet) throws IOException {
        sendString(packet.toString());
    }

    public void sendString(String value) throws IOException {
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", port);
        ByteBuffer buffer = ByteBuffer.wrap(value.getBytes());
        dataChannel.send(buffer, serverAddress);
        buffer.clear();
    }

    // Recursive read function that reads all messages available to be read
    public void receivePackets() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketAddress add = dataChannel.receive(buffer);
        if (add != null) {
            unhandledPackets.add(Packet.byteBufferToPacket(buffer));
            receivePackets();
        }
    }

    public boolean isPacketAvailable() {
        if (unhandledPackets.size() == 0) {
            return false;
        } else {
            currentPacket = unhandledPackets.get(0);
            unhandledPackets.remove(0);
            return true;
        }
    }

    public Packet getFirstPacket() {
        return currentPacket;
    }

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
