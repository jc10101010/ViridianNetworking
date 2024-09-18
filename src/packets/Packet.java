package packets;

import java.nio.ByteBuffer;
import objects.Vertex;
import packets.clientpackets.*;
import packets.clientpackets.setters.*;
import packets.serverpackets.*;

//A Packet is an abstract object representing a message exchanged between the server and the client.
//Each packet contains data specific to its purpose (e.g., player position, rotation, requests).
//Packets can be parsed from a string using stringToPacket() and converted back into a string using toString().

public abstract class Packet {
    
    //Enum to indicate whether a packet is from the SERVER or CLIENT
    public static enum packetRoleEnum {
        SERVER,
        CLIENT
    }

    public packetRoleEnum packetRole; //Role of the packet (SERVER or CLIENT)

    //Method to wrap a string with start and end markers, along with the packet role (SERVER/CLIENT)
    //This is a common operation for serializing the packet to be sent over the network.
    public String wrapString(String toWrap) {
        return "START " + packetRole.name() + " " + toWrap + " END"; //Example output: START CLIENT <data> END
    }

    //Abstract method that all packet subclasses must implement to convert themselves to a string
    public abstract String toString();

    //Convert a byte buffer (received over the network) into a Packet object by converting it to a string first
    public static Packet byteBufferToPacket(ByteBuffer buffer) {
        String msg = byteBufferToString(buffer); //Convert the buffer to a string
        Packet packet = null;
        try {
            packet = stringToPacket(msg); //Parse the string into the appropriate packet object
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return packet; //Return the generated Packet object
    }

    //Convert a ByteBuffer to a String by extracting the byte data and constructing a string from it
    public static String byteBufferToString(ByteBuffer buffer) {
        buffer.flip(); //Prepare the buffer for reading (switch from writing to reading mode)
        int limits = buffer.limit(); //Get the limit (number of bytes to read)
        byte bytes[] = new byte[limits]; //Create a byte array to hold the buffer data
        buffer.get(bytes, 0, limits); //Read the buffer into the byte array
        return new String(bytes); //Convert the byte array to a String and return
    }

    //Converts a string message into a Packet object by parsing the string
    //The string is expected to follow a predefined format that identifies the packet type and contents.
    public static Packet stringToPacket(String msg) throws IndexOutOfBoundsException {
        Packet packet = null;
        String[] tokens = msg.split(" "); //Split the message into tokens based on spaces
        
        //Ensure the string starts with "START" and ends with "END"
        if (hasStartAndEnd(tokens)) {
            //If the packet originates from a client
            if (tokens[1].equals("CLIENT")) {
                if (tokens[2].equals("REQUEST")) {
                    packet = new ClientRequestPacket(); //Handle a client request packet

                } else if (tokens[2].equals("SET")) {
                    //Handle client position or rotation updates
                    if (tokens[3].equals("POSITION")) {
                        Vertex vertex = tokensToVertex(tokens[4], tokens[5], tokens[6]); //Parse position data
                        packet = new ClientSetPositionPacket(vertex); //Create a position update packet

                    } else if (tokens[3].equals("ROTATION")) {
                        Vertex vertex = tokensToVertex(tokens[4], tokens[5], tokens[6]); //Parse rotation data
                        packet = new ClientSetRotationPacket(vertex); //Create a rotation update packet
                    }
                } else if (tokens[2].equals("JOIN")) {
                    packet = new ClientJoinPacket(); //Handle client join packet

                } else if (tokens[2].equals("LEAVE")) {
                    packet = new ClientLeavePacket(); //Handle client leave packet
                }
            //If the packet originates from the server
            } else if (tokens[1].equals("SERVER")) {
                if (tokens[2].equals("STATE")) {
                    //Handle server state update for position or rotation
                    if (tokens[3].equals("POSITION")) {
                        String name = tokens[4]; //Get the player's name
                        Vertex vertex = tokensToVertex(tokens[5], tokens[6], tokens[7]); //Parse position data
                        packet = new ServerStatePositionPacket(name, vertex); //Create position state packet

                    } else if (tokens[3].equals("ROTATION")) {
                        String name = tokens[4]; //Get the player's name
                        Vertex vertex = tokensToVertex(tokens[5], tokens[6], tokens[7]); //Parse rotation data
                        packet = new ServerStateRotationPacket(name, vertex); //Create rotation state packet
                    }
                } else if (tokens[2].equals("CONFIRM")) {
                    packet = new ServerConfirmPacket(); //Handle server confirmation packet

                } else if (tokens[2].equals("YOUJOINED")) {
                    packet = new ServerYouJoinedPacket(); //Handle server 'You Joined' packet
                }
            }
        }
        return packet; //Return the constructed packet
    }

    //Helper method to check if a message has valid "START" and "END" tokens
    private static boolean hasStartAndEnd(String[] tokens) {
        if (tokens.length < 2) { //Ensure there are enough tokens to check
            return false;
        }
        return (tokens[0].equals("START") && tokens[tokens.length - 1].equals("END")); //Check first and last tokens
    }

    //Converts 3 string tokens into a Vertex object (representing 3D coordinates)
    private static Vertex tokensToVertex(String val1, String val2, String val3) {
        Vertex vertex = null;
        try {
            vertex = new Vertex(0, 0, 0); //Create a new Vertex object
            vertex.x = Float.parseFloat(val1); //Parse the X coordinate from the string
            vertex.y = Float.parseFloat(val2); //Parse the Y coordinate
            vertex.z = Float.parseFloat(val3); //Parse the Z coordinate
        } catch (NumberFormatException e) {
            vertex = null; //If parsing fails, return null
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return vertex; //Return the parsed Vertex
    }

}
