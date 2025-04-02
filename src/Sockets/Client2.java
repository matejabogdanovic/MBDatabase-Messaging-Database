package Sockets;

import java.io.IOException;
import java.net.UnknownHostException;

public class Client2 {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		System.out.println("Client.");
		MBDatabase db = new MBDatabase("localhost",12668);
		db.sendMessage("CITAM?", 1, 2);  
		db.sendMessage("CITAM.", 1, 2);  
		
		Thread.sleep(5000); // wait for server to read
		db.close();
		
		System.out.println("Client end."); 
	}
}
