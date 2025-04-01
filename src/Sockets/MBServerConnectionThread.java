package Sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				OutputStream os = clientSocket.getOutputStream();
				PrintWriter writer = new PrintWriter(os, true);
				){ 
					String command;
					while(!(command=reader.readLine()).equals("close")) {
						System.out.println("Accept.");
				
						String[] commandSplitted = command.split("\\$",2);
						// if(commandSplitted.length != 2)??
						MBServerWorkingThread thr =	new MBServerWorkingThread(Long.parseLong(commandSplitted[0]),commandSplitted[1], writer);
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
				} 
		
	}
}
