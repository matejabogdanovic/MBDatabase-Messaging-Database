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
		ArrayList<MBMessage> msgs= db.readMessage(2, 1, 2);
		System.out.println("MESSAGE IS:" + msgs.toString());  
		Thread.sleep(5000); // wait for server to read
		db.close();
		 
		System.out.println("Client end."); 
	}
	
}
