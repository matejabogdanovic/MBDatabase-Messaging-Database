package MBDatabase;

enum MBServerCommand {
	closeConnection(-2),
    unknownCommand(-1),
    sendMessage(0), 
	readMessage(1), 
	getAllChats(2);
    private final int value;

    MBServerCommand(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }


  
}
