package Sockets;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import MBDatabase.MBDatabase;
import MBDatabase.MBMessage;
 
public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		System.out.println("Client."); 
		MBDatabase db = new MBDatabase("localhost",12668);
		//db.sendMessage("Kad cemo napolje?", 1, 2);  
		//db.sendMessage("Aj posle.", 2, 1);  
		Thread.sleep(5000); // wait for server to read
		ArrayList<MBMessage> msgs= db.readMessage(3, 1, 1);
		System.out.println("MESSAGE IS:" + msgs.toString());  
		ArrayList<MBMessage> msgs2= db.readMessage(4, 1, 0, 2);
		System.out.println("MESSAGE IS:" + msgs2.toString());
		ArrayList<Long> chats = db.getAllChats(3);
		System.out.println("Chats with:" + chats.toString());
		Thread.sleep(5000); // wait for server to read
		db.close();
		 
		System.out.println("Client end."); 
	}
	
}
