package MBDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

class MBServerConnectionThread extends Thread{
	private Socket clientSocket;
	private static final int joinTime = 50; 
	private static final int maxWorkingThreads = 100;
	private ArrayList<MBServerWorkingThread> runningWorkingThreads = new ArrayList<MBServerWorkingThread>();
	public MBServerConnectionThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}   

	@Override
	public void run() {
		try(InputStream is = clientSocket.getInputStream();
				ObjectInputStream reader = new ObjectInputStream(is);
				OutputStream os = clientSocket.getOutputStream();
				ObjectOutputStream writer = new ObjectOutputStream(os);
				){ 
					Object command;
					while(true) {
						command = reader.readObject();
						System.out.println("Accept.");
						
						if(!(command instanceof MBPacket)) continue;
						MBPacket packet = (MBPacket) command;
						if(packet.command == MBServerCommand.closeConnection)break;
						
						MBServerWorkingThread thr =	new MBServerWorkingThread(packet ,writer);
						runningWorkingThreads.add(thr);
						thr.start();

					}
					for (MBServerWorkingThread mbServerWorkingThread : runningWorkingThreads) {
						mbServerWorkingThread.join(joinTime);
					}
					System.out.println("Connection end.");
				} catch (IOException e) {
					
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		
	}
}
