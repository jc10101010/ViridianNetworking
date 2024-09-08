
# PACKET DEFINITION

## CLIENT PACKETS

#### ClientJoinPacket - Tells the server that the client would like to join
START CLIENT JOIN END

#### ClientLeavePacket - Tells the server that the client would like to leave
START CLIENT LEAVE END


#### ClientRequestPacket - Requests the current state of the game
START CLIENT REQUEST END

#### ClientSetPositionPacket - Tells the server where the client's player is
START CLIENT SET POSITION 6 2 6 END

#### ClientSetRotationPacket - Tells the server where the client's player is facing
START CLIENT SET ROTATION 6.234 2.234 6.343 END

################# EXAMPLE #################
START CLIENT JOIN END
START CLIENT SET POSITION 2 2 2 END
START CLIENT SET ROTATION 6.234 2.234 6.343 END
START CLIENT REQUEST END
START CLIENT LEAVE END
###########################################

## SERVER PACKETS

#### ServerYouJoinedPacket - Tells the client that they have joined, response to ClientJoinPacket
START SERVER YOUJOINED END

#### ServerStatePositionPacket - Tells the client where player NAME is, response to ClientRequestPacket
START SERVER STATE POSITION name 1 1 1.5 END

#### ServerStateRotationPacket - Tells the client where player NAME is facing, response to ClientRequestPacket
START SERVER STATE ROTATION name 3.2344 0.234 2.1 END

#### ServerConfirmSetPacket - Tells the client that their message was received, response to ClientSetPacket, ClientLeavePacket
START SERVER CONFIRM END

################# EXAMPLE #################
START SERVER YOUJOINED END
START SERVER STATE POSITION 1 1 1 1.5 END
START SERVER STATE ROTATION 1 3.2344 0.234 2.1 END
START SERVER CONFIRM END
###########################################