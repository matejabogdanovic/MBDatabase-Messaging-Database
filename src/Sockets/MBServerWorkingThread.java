package Sockets;

import java.io.PrintWriter;
import java.util.Map;

class MBServerWorkingThread extends Thread {
	private String command;
	private PrintWriter writer;
	private long requestId;
	MBServerWorkingThread(long requestId ,String command, PrintWriter writer) {
		this.requestId = requestId;
		this.command = command;
		this.writer = writer;  
	}
	private void executeCommand() {
		// cmd is index in functions arrays => first parse function (get args)
		// then execute function 
		int cmd = MBServerCommandParser.getCommand(command);
		System.out.println("Command: " + cmd);
		if(cmd < 0)return;
		Map<String, Object> args = MBServerCommandParser.parseFunctions[cmd].command(command);
		if(args == null)return;
		String res = MBServerCommandExecutor.executeFunctions[cmd].command(args);
		
		// send data
		if(res!=null) { 
			synchronized (writer) {
				System.out.println("WRITING: "+requestId+"$"+cmd+"$"+res);
				writer.println(requestId+"$"+cmd+"$"+res);
			}
		}
	}   
	@Override
	public void run() {
		System.out.println("Executing command: "+ command);
		executeCommand();
	}
}
