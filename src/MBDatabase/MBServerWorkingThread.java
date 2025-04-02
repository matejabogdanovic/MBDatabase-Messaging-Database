package MBDatabase;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

class MBServerWorkingThread extends Thread {
	private MBPacket command;
	private ObjectOutputStream writer;

	MBServerWorkingThread(MBPacket command, ObjectOutputStream writer) {
		this.command = command;
		this.writer = writer;  
	}
	private void executeCommand() throws IOException {

		MBPacket res = MBServerCommandExecutor.
				executeFunctions[command.command.getValue()].
				command(command);
		
		// send data
		if(res!=null) { 
			synchronized (writer) {
				writer.writeObject(res);
			}
		}
	}   
	@Override
	public void run() {
		System.out.println("Executing command: "+ command);
		try {
			executeCommand();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
