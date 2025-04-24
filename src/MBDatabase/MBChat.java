package MBDatabase;

import java.io.Serializable;

public class MBChat  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	long packetId;
	MBServerCommand command;
	Object payload;
	MBChat(long packetId, MBServerCommand command, Object payload) {
			this.packetId = packetId;
			this.command = command;
			this.payload = payload;
	
	}
}
