# Custom Database with Java Sockets

## Overview
This project implements a <b>horrible</b> custom database using Java sockets for communication between a multi-threaded server and multiple clients. <br>
The server listens for incoming requests and processes and stores messages in separate files for each chat.<br>
Clients connect to the server to send and retrieve messages.<br>
Note that this project is designed to be used with Spring Boot or other similar frameworks.
## Features
- Multi-threaded server for handling concurrent client connections
- Each client connection is handled by `MBServerConnectionThread`
- Requests are processed in a separate `MBServerWorkingThread`
- Communication over TCP sockets
- Messages are stored in text files, one per user pair (`id1_id2.txt`)
- Clients can send messages to other users
- Clients can retrieve a specific number of messages between two users
- Graceful client disconnection

## Server
To start the server, create an instance of `MBServer` and start it:

```java
MBServer server = new MBServer(port);
server.start();
```

The server listens for incoming client connections and creates a new `MBServerConnectionThread` for each connection. <br>
This thread continuously listens for client requests and, upon receiving one, spawns an `MBServerWorkingThread` to handle the request asynchronously.

## Client
To connect a client to the server, instantiate `MBDatabase` with the server's IP address and port:

```java
MBDatabase db = new MBDatabase(ip, port);
```

### Sending Messages
To send a message from one user to another, use:

```java
db.sendMessage("Message text", senderId, receiverId);
```

The server stores messages in a text file named `id1_id2.txt`, where `id1` and `id2` are the user IDs.

### Reading Messages
To retrieve the last `count` messages exchanged between two users:

```java
ArrayList<MBMessage> msgs = db.readMessage(id1, id2, count);
// same as ArrayList<MBMessage> msgs = db.readMessage(id1, id2, 0, count);
// 3. argument is starting message, so in this case, read count messages starting from latest message recieved
System.out.println("Messages are: " + msgs.toString());
```
### Get All Chats
To get all users that `id` has a chat with:
```java
ArrayList<Long> chats = db.getAllChats(id);
```

### Closing the Connection
When the client is shutting down, close the connection properly:

```java
db.close();
```

## Requirements
- Java 8 or later
- Open network port for server-client communication

## Notes
- A `./messages/` folder must exist in the root directory for storing message files.
- The server must be running before clients can connect.
- Messages are stored in text files named based on user pairs (`id1_id2.txt`).
- Clients should always call `db.close()` to release resources when disconnecting.


