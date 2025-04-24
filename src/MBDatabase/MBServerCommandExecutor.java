package MBDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
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
			packet -> readMessage(packet),
			packet -> getAllChats(packet)
		};   
	static private String getFileName(long id1, long id2) {
		Long first = id1 > id2?id2:id1; 
		Long second = first == id1?id2:id1;
		
		return "./messages/"+first+"_"+second+".txt";
	}
	static private String getControlFileName(long id) {
		return "./chats/"+id+".txt";
	}
	static MBPacket getAllChats(MBPacket packet) {
		System.out.println("EEE");
		Long id = (Long)packet.payload;
		File controlFile= new File(getControlFileName(id));
		ArrayList<Long> res = new ArrayList<Long>();
		if(controlFile.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(controlFile))) {
				String line;
	            while ((line = reader.readLine()) != null) {
	            	 String[] ids = line.replace("./messages/", "").replace(".txt", "").split("_");
	            	 long id1 = Long.parseLong(ids[0]);
	                 long id2 = Long.parseLong(ids[1]);
	                 
	                 long neededId = (id1 == id) ? id2 : id1;
	                 
	                 res.add(neededId);
	            }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new MBPacket(packet.packetId, MBServerCommand.getAllChats, res);
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
		if(!(new File(fileName).exists())) {
			
			try (FileWriter controlFileSender = new FileWriter(getControlFileName(sender), true);
					FileWriter controlFileReciever = new FileWriter(getControlFileName(reciever), true)){
				controlFileSender.write(fileName+'\n');
				controlFileReciever.write(fileName+'\n');
			} catch (Exception e) {
				return null;
			}
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
		        for (long i = 0; i < message.cnt + message.fromCnt; i++) {
		            line = reader.readPreviousLine();
		            if(line == null)break;
		            if(i < message.fromCnt)continue;
		            // parse content
		           String[] contentAndRest = line.split("(\\|)(?=[^|]+\\|[^|]+\\|[^|]+$)",2);
		           if(contentAndRest.length != 2)continue;
		           String[] idsAndDate = contentAndRest[1].split("\\|");
		           if(idsAndDate.length != 3)continue;
		           messages.add(new MBMessage(Long.parseLong(idsAndDate[0]), 
		        		   Long.parseLong(idsAndDate[1]), 
		        		   contentAndRest[0], 
		        		   idsAndDate[2]));
		            
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
