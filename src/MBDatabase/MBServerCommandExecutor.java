package MBDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;


class MBServerCommandExecutor {
	private static HashMap<String, Boolean> openedFiles = new HashMap<String, Boolean>();
	
	interface Func{
		 MBPacket command(MBPacket packet) ;
	}  
	public static Func [] executeFunctions = new Func[]{
			packet -> sendMessage(packet),
			packet -> readMessage(packet) 
		};   
	static private String getFileName(long id1, long id2) {
		Long first = id1 > id2?id2:id1; 
		Long second = first == id1?id2:id1;
		
		return "./messages/"+first+"_"+second+".txt";
	}
	static MBPacket sendMessage(MBPacket packet) {
		if(packet == null) return null; 
		MBMessage message = (MBMessage) packet.payload;
		long sender = message.sender;
		long reciever = message.reciever;
		String text = message.content;

		String fileName = getFileName(sender, reciever);
		synchronized (openedFiles) {
			while(openedFiles.containsKey(fileName)) {
				try {
					openedFiles.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				}
			}
			openedFiles.put(fileName, true);
		}
		try (FileWriter fw = new FileWriter(fileName, true)){
			fw.write(
					text +"|"+
					+sender+"|"+reciever+"|"+LocalDateTime.now()+"\n"
					);
		}	
		catch (IOException e) {
			System.out.println(e);
		}finally {	
			synchronized (openedFiles) {
				openedFiles.remove(fileName);
				openedFiles.notifyAll();
			}
		}
		
		return null;
	}
	
	static MBPacket readMessage(MBPacket packet)  {
	
		if(packet == null) return null;
		MBMessagesRequest message = (MBMessagesRequest) packet.payload;

		
		ArrayList<MBMessage> messages = new ArrayList<MBMessage>();
		String fileName = getFileName(message.id1, message.id2);
		synchronized (openedFiles) {
			while(openedFiles.containsKey(fileName)) {
				try {
					openedFiles.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				}
			}
			openedFiles.put(fileName, true);
		}
		try {
			File file = new File(fileName);
				
			if(file.exists()) {
				ReverseFileReader reader = new ReverseFileReader(fileName);
		        String line;
		        for (long i = 0; i < message.cnt; i++) {
		            line = reader.readPreviousLine();
		            if(line == null)break;
		            messages.add(new MBMessage(1, 2, line));
		            
		         }
		
		        reader.close();
	        }
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }finally {
	    	synchronized (openedFiles) {
					openedFiles.remove(fileName);
					openedFiles.notifyAll();
		 	}
		}
		
		
		
		return new MBPacket(packet.packetId, MBServerCommand.readMessage, messages);
	}
}
