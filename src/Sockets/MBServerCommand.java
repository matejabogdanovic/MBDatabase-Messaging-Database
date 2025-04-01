package Sockets;

enum MBServerCommand {
    unknownCommand(-1),
    sendMessage(0), 
	readMessage(1);

    private final int value;

    MBServerCommand(int value) {
        this.value = value;
    }
    static boolean isValid(int value) {
    	return value>=-1 && value <=1;
    }	
    int getValue() {
        return value;
    }


  
}
