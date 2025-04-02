package Sockets;
import java.io.IOException;

import MBDatabase.MBServer;


public class Server {
	 
	public static void main(String[] args) throws IOException{
		MBServer server = new MBServer(12668);
		server.start();  
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
	}
	

}
