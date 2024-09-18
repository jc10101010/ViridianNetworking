package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import packets.Packet;
import packets.clientpackets.*;
import packets.clientpackets.setters.*;
import packets.serverpackets.*;

public class MPServer {
    //MAIN METHOD - Entry point for the server
    public static void main(String[] args) throws IOException {
        //Create a new server instance and run it
        MPServer server = new MPServer();
        server.runServer();
        //Close server once done (usually after exiting the loop)
        server.close();
    }

    //ATTRIBUTES

    //DatagramChannel for sending/receiving data via UDP
    private DatagramChannel dataChannel;
    //Stores the address of the most recent client interacting with the server
    private SocketAddress recentClient;

    //Holds the most recent packet received
    private Packet recentPacket = null;
    //Queue for outgoing packets in response to the client
    private ArrayList<Packet> responsePackets = new ArrayList<>();
    //Log of all received packets (useful for debugging or tracking history)
    private ArrayList<Packet> packetLog = new ArrayList<>();

    //A map of connected players associated with their network addresses
    private HashMap<SocketAddress, MPServerPlayer> players = new HashMap<>();
    private int playerCount = 0; //Keeps track of the number of players

    //The port number the server listens on
    public static final Integer port = 4600;

    //Constructor - Set up server channel and bind to the specified port
    public MPServer() throws IOException {
        //Setup networking for the server to listen for incoming connections
        dataChannel = DatagramChannel.open();
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        dataChannel.bind(address); //Bind to the localhost and port
        System.out.println("Server listening on port " + port + ": ...");
    }

    //Main server loop - continuously listens for incoming packets
    public void runServer() throws IOException {
        while (true) {
            //Receive and process incoming packets
            receivePacket();
            generateResponsePacket();

            //Send responses for all the packets that were generated
            if (responsePackets.size() != 0) {
                for (Packet packet : responsePackets) {
                    sendResponsePacket(packet);
                }
            }

            //Log the packet for reference and debugging purposes
            packetLog.add(recentPacket);
            if (recentPacket == null) {
                System.out.println("Client at " + recentClient + " did not send a valid packet");
            } else {
                System.out.println(
                        "Client at " + recentClient + " sent packet of type: " + recentPacket.getClass().toString());
            }
        }
    }

    //Receives a packet from the client and stores it as recentPacket
    public void receivePacket() throws IOException {
        //Prepare a buffer to receive data from the client
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //Receive data and store the client's address
        recentClient = dataChannel.receive(buffer);
        //Convert the buffer data into a Packet object
        recentPacket = Packet.byteBufferToPacket(buffer);
    }

    //Based on the recent packet, generate a response for the client
    public void generateResponsePacket() {
        //Clear the response packet list for this cycle
        responsePackets = new ArrayList<Packet>();

        //Only proceed if we have received a valid packet
        if (recentPacket != null) {
            Class packetClass = recentPacket.getClass();
            Set<SocketAddress> ips = players.keySet();

            //If the client is already a known player
            if (ips.contains(recentClient)) {
                //Process different types of packets based on their class
                if (packetClass.equals(ClientLeavePacket.class)) {
                    processLeavePacket(); //Player leaving the server
                } else if (packetClass.equals(ClientSetPositionPacket.class)) {
                    processSetPositionPacket(); //Player updating position
                } else if (packetClass.equals(ClientRequestPacket.class)) {
                    processRequestPacket(); //Player requesting server state
                } else if (packetClass.equals(ClientSetRotationPacket.class)) {
                    processSetRotationPacket(); //Player updating rotation
                } 
            } else {
                //If it's a new client, process their join request
                if (packetClass.equals(ClientJoinPacket.class)) {
                    processJoinPacket(); //Player joining the server
                }
            }
        }
    }

    //Process a client request packet, usually to get the state of other players
    public void processRequestPacket() {
        MPServerPlayer clientPlayer = players.get(recentClient);

        //For every other player on the server, send their state to the requesting player
        for (MPServerPlayer otherPlayer : players.values()) {
            if (!otherPlayer.equals(clientPlayer)) {
                //Add position and rotation state packets to the response queue
                responsePackets.add(new ServerStatePositionPacket(otherPlayer.getName(), otherPlayer.getPosition()));
                responsePackets.add(new ServerStateRotationPacket(otherPlayer.getName(), otherPlayer.getRotation()));
            }
        }
    }

    //Process a client leaving the game
    public void processLeavePacket() {
        playerCount--; //Decrement the player count
        //Remove the player from the map of active players
        players.remove(recentClient);
    }

    //Process a client updating their position
    public void processSetPositionPacket() {
        MPServerPlayer clientPlayer = players.get(recentClient);
        //Retrieve the new position from the packet
        ClientSetPositionPacket packet = (ClientSetPositionPacket) recentPacket;
        //Update the player's position
        clientPlayer.setPosition(packet.position);
        //Send a confirmation packet back to the client
        responsePackets.add(new ServerConfirmPacket());
    }

    //Process a client updating their rotation
    public void processSetRotationPacket() {
        MPServerPlayer clientPlayer = players.get(recentClient);
        //Retrieve the new rotation from the packet
        ClientSetRotationPacket packet = (ClientSetRotationPacket) recentPacket;
        //Update the player's rotation
        clientPlayer.setRotation(packet.rotation);
        //Send a confirmation packet back to the client
        responsePackets.add(new ServerConfirmPacket());
    }

    //Process a client joining the game
    public void processJoinPacket() {
        playerCount++; //Increment the player count
        //Generate a unique name for the new player
        String playerName = String.valueOf(playerCount);
        //Create a new player instance and add it to the player map
        MPServerPlayer clientPlayer = new MPServerPlayer(playerName);
        players.put(recentClient, clientPlayer);
        //Send a "You Joined" packet to the new player
        responsePackets.add(new ServerYouJoinedPacket());
    }

    //Send a response packet back to the client
    public void sendResponsePacket(Packet packet) throws IOException {
        //Wrap the packet data into a ByteBuffer and send it to the client
        ByteBuffer buffer = ByteBuffer.wrap(packet.toString().getBytes());
        dataChannel.send(buffer, recentClient);
    }

    //Close the server and release resources
    public void close() throws IOException {
        dataChannel.close();
    }

}
