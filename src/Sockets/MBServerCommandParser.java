package Sockets;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class MBServerCommandParser {
	interface Func{ 
		 Map<String, Object> command(String args);
	} 
	public static Func [] parseFunctions = new Func[]{
			args -> sendMessage(args),
			args -> readMessage(args)  
		};
	static int getCommand(String command) {
		String[] commandSplitted = command.split("\\$", 2);
		if(commandSplitted.length != 2)return -1;
		int commandId = Integer.parseInt(commandSplitted[0]);
		return MBServerCommand.isValid(commandId)?commandId:-1;
	}
 
	static Map<String, Object> sendMessage(String args) {
		String[] argsSplitted = args.split("\\$", 4);
		if(argsSplitted.length != 4) {
			// todo throw
			return null;
		}
		Map<String, Object> result = new HashMap<>();
        result.put("sender", Long.parseLong(argsSplitted[1]));
        result.put("reciever", Long.parseLong(argsSplitted[2]));
        result.put("message", argsSplitted[3]);
        return result;
	}
	static Map<String, Object> readMessage(String args) {
		Map<String, Object> result = new HashMap<>();
        return result;
	}
}
