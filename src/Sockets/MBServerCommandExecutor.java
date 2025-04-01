package Sockets;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import Sockets.MBServerCommandParser.Func;

class MBServerCommandExecutor {
	interface Func{
		 String command(Map<String, Object> args);
	} 
	public static Func [] executeFunctions = new Func[]{
			args -> sendMessage(args),
			args -> readMessage(args) 
		};  
	
	static String sendMessage(Map<String, Object> args) {
		if(args == null) return null; // todo throw
		long sender = (long) args.get("sender");
		long reciever = (long) args.get("reciever");
		String message = (String) args.get("message");
		Long first, second;
		 
		if(sender > reciever) {
			first = reciever; 
			second = sender;  
		}else {
			first = sender;
			second = reciever;
		}
		System.out.println(first);
		try (FileWriter fw = new FileWriter("./messages/"+first+"_"+second+".txt", true)){
			fw.write(
					message+"|"+
					+sender+"|"+reciever+"|"+LocalDateTime.now()+"\n"
					);
			
		}catch (IOException e) {
			System.out.println(e);
		}finally {
			return null;
		}
	}
	
	static String readMessage(Map<String, Object> args) {
		return "PORUKA";
	}
}
