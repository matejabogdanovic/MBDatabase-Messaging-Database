package Sockets;

import java.io.Serializable;

class MBPacket implements Serializable {
	private static final long serialVersionUID = 1L;
	long packetId;
	MBServerCommand command;
	Object payload;
	MBPacket(long packetId, MBServerCommand command, Object payload) {
		this.packetId = packetId;
		this.command = command;
		this.payload = payload;
	} 
	
}
