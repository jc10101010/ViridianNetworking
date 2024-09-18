# Viridian Networking

**Viridian Networking** is a lightweight, UDP-based networking solution in Java designed to facilitate basic multiplayer functionality in 3D games. It utilizes Java NIO's `DatagramChannel` for real-time communication between clients and servers. This demo integrates Viridian Networking with **TurquoiseGraphics**, a simple 3D engine, showcasing how multiplayer gameplay can be implemented over UDP.

## Features

- **UDP Communication**: Offers low-latency, connectionless communication suitable for real-time multiplayer interactions.
- **Client-Server Architecture**: The server manages game state and multiple clients, while clients handle local player updates and render the game environment.
- **Custom Packet Protocol**: Utilizes specifically defined packet formats for various actions like joining, leaving, and updating player positions and rotations.
- **Scalable Design**: Supports multiple players, each uniquely identified by their network address.

## Getting Started

### Server Setup

Start the server by running:

```bash
bash ./crServer.sh
```

- Listens on UDP port `4600` (configurable).
- Manages connected players and handles incoming packets.
- Responds with relevant state updates to clients.

### Client Setup (Game Demo)

Run the 3D game demo:

```bash
bash ./crGame.sh
```

- Connects to the server and begins sending position and rotation updates.
- Renders the 3D game state locally.

### Console Client (Manual Testing)

For manual input testing via the console:

```bash
bash ./crClient.sh
```

- Allows you to send client packets directly to the server using standard input.

---

## Packet Protocol

Communication between the client and server is handled through a custom packet-based protocol.

### Client Packets

- **ClientJoinPacket**: Signals the client's intent to join the game.
  ```
  START CLIENT JOIN END
  ```
- **ClientLeavePacket**: Notifies the server of the client's departure.
  ```
  START CLIENT LEAVE END
  ```
- **ClientRequestPacket**: Requests the current game state from the server.
  ```
  START CLIENT REQUEST END
  ```
- **ClientSetPositionPacket**: Updates the server with the client's position.
  ```
  START CLIENT SET POSITION <x> <y> <z> END
  ```
  *Example:*
  ```
  START CLIENT SET POSITION 6 2 6 END
  ```
- **ClientSetRotationPacket**: Updates the server with the client's rotation.
  ```
  START CLIENT SET ROTATION <x> <y> <z> END
  ```
  *Example:*
  ```
  START CLIENT SET ROTATION 6.234 2.234 6.343 END
  ```

**Client Packet Sequence Example:**
```
START CLIENT JOIN END
START CLIENT SET POSITION 2 2 2 END
START CLIENT SET ROTATION 6.234 2.234 6.343 END
START CLIENT REQUEST END
START CLIENT LEAVE END
```

### Server Packets

- **ServerYouJoinedPacket**: Confirms the client's successful join.
  ```
  START SERVER YOUJOINED END
  ```
- **ServerStatePositionPacket**: Provides another player's position.
  ```
  START SERVER STATE POSITION <name> <x> <y> <z> END
  ```
  *Example:*
  ```
  START SERVER STATE POSITION player1 1 1 1.5 END
  ```
- **ServerStateRotationPacket**: Provides another player's rotation.
  ```
  START SERVER STATE ROTATION <name> <x> <y> <z> END
  ```
  *Example:*
  ```
  START SERVER STATE ROTATION player1 3.2344 0.234 2.1 END
  ```
- **ServerConfirmSetPacket**: Confirms receipt and processing of a client's packet.
  ```
  START SERVER CONFIRM END
  ```

**Server Packet Sequence Example:**
```
START SERVER YOUJOINED END
START SERVER STATE POSITION player1 1 1 1.5 END
START SERVER STATE ROTATION player1 3.2344 0.234 2.1 END
START SERVER CONFIRM END
```

---

## Architecture Overview

### Server

The server maintains the game state and manages multiple clients.

**Key Responsibilities:**

- **Player Management**: Tracks connected players using their network addresses.
- **State Processing**: Updates game state based on incoming position and rotation data from clients.
- **State Distribution**: Sends relevant state updates to clients upon request.

### Client

The client communicates with the server to update and retrieve game state information.

**Key Responsibilities:**

- **State Updates**: Sends the client's position and rotation to the server.

---

## Usage Flow

1. **Start the Server**: Run `bash ./crServer.sh`. The server awaits client connections.
2. **Client Joins the Game**: The client sends a `ClientJoinPacket`. Upon confirmation, it begins sending position and rotation updates.
3. **State Synchronization**: The client continuously updates the server with its state and receives updates about other players.
4. **Game State Requests**: Clients can send `ClientRequestPacket` to receive the latest game state from the server.
5. **Client Leaves the Game**: Sending a `ClientLeavePacket` informs the server to remove the client from the game state.

---

## Extending Viridian Networking

### Adding New Packet Types

To introduce new packet types (e.g., `ClientSetHealthPacket`), follow these steps:

#### 1. Define the Packet's Purpose

Determine what the new packet will achieve—for instance, updating a player's health status.

#### 2. Implement the Packet on the Client Side

- **Create the Packet Class**: Add a new class in the `packets.clientpackets.setters` package.
  ```java
  public class ClientSetHealthPacket extends ClientSetPacket {
      public int health;

      public ClientSetHealthPacket(int health) {
          clientSetType = clientSetTypeEnum.HEALTH;
          this.health = health;
      }

      @Override
      public String toString() {
          return wrapString(String.valueOf(health));
      }
  }
  ```
- **Update Enums**: Add the new type to `clientSetTypeEnum` in `ClientSetPacket.java`.
  ```java
  public static enum clientSetTypeEnum {
      POSITION,
      ROTATION,
      HEALTH
  }
  ```
- **Send the Packet**: Use the client instance to send the new packet.
  ```java
  client.sendPacket(new ClientSetHealthPacket(80));
  ```

#### 3. Handle the Packet on the Server Side

- **Process the Packet**: Implement logic in `MPServer.java`.
  ```java
  public void processSetHealthPacket() {
      MPServerPlayer clientPlayer = players.get(recentClient);
      int newHealth = ((ClientSetHealthPacket) recentPacket).health;
      clientPlayer.setHealth(newHealth);
      responsePackets.add(new ServerConfirmPacket());
  }
  ```
- **Update Packet Processing**: Include the new packet in `generateResponsePacket()`.
  ```java
  else if (packetClass.equals(ClientSetHealthPacket.class)) {
      processSetHealthPacket();
  }
  ```
- **Extend Player Class**: Modify `MPServerPlayer.java` to include health.
  ```java
  public class MPServerPlayer {
      private int health = 100;

      public int getHealth() { return health; }
      public void setHealth(int health) { this.health = health; }
  }
  ```

#### 4. Update the Packet Conversion Logic

Modify `stringToPacket()` in `Packet.java` to recognize the new packet format.

```java
if (tokens[2].equals("SET")) {
    if (tokens[3].equals("HEALTH")) {
        int health = Integer.parseInt(tokens[4]);
        packet = new ClientSetHealthPacket(health);
    }
}
```

#### 5. (Optional) Notify Other Clients

If necessary, create a corresponding server packet (e.g., `ServerStateHealthPacket`) to inform other clients about health changes.
