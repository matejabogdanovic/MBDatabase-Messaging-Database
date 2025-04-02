package MBDatabase;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MBServer {
	private int port;
	private static final int joinTime = 500; 
	private static final int maxConnectionThreads = 50; 
	private ArrayList<MBServerConnectionThread> runningConnectionThreads = new ArrayList<MBServerConnectionThread>();
	public MBServer(int port) throws IOException { 
		this.port = port; 
	}  

	private void acceptNewConnection(ServerSocket serverSocket) throws IOException {
		try {
		 
				Socket clientSocket = serverSocket.accept();
			System.out.println("Connection accepted.");
			MBServerConnectionThread thr =	new MBServerConnectionThread(clientSocket);
			thr.setDaemon(true);
			runningConnectionThreads.add(thr);
			thr.start();
		}catch (Exception e) {
			
		}
	}

	public void start() throws IOException {
		try(ServerSocket serverSocket = new ServerSocket(port);){
			System.out.println("Server.");
			int i = 0;
			while (!serverSocket.isClosed()) {
				if(i==2)break;
				acceptNewConnection(serverSocket);
				i++;   
			}  
			// wait joinTime for each thread to finish then just close server
			// threads are deamon and they will finish
			for (MBServerConnectionThread mbServerWorkingThread : runningConnectionThreads ) {
				mbServerWorkingThread.join();
			}
			System.out.println("Server end.");
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}

}
