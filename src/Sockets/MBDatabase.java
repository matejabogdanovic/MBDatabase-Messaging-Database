package Sockets;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MBDatabase implements Runnable{
	private Socket server;
	private OutputStream os;
	private InputStream is; 
	private PrintWriter pout;
	private BufferedReader pin;
	private AtomicLong requestId = new AtomicLong(0);
	private Map<Long, String> pendingAnswers = new HashMap<Long, String>();
	
	private Thread requestHandlerThread = new Thread(this);
	public MBDatabase(String host, int port) throws UnknownHostException, IOException {
		try {
			this.server = new Socket(host,port);
			this.os = server.getOutputStream();
			this.is = server.getInputStream();
			this.pout = new PrintWriter(os, true);
			this.pin = new BufferedReader(new InputStreamReader(is)); 
			this.requestHandlerThread.start();
		}catch (Exception e) {
			close();
			throw e;
		} 
	} 
	
	@Override
	public void run() {
		String s;
		try {
			while (!requestHandlerThread.isInterrupted()) {
				s=pin.readLine();
				if(s==null) { 
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
					continue;
				}
				String[] commandSplitted = s.split("\\$",2);
				// len != 2?? 
				System.out.println("SPLITTED: " + commandSplitted.toString());
				synchronized (pendingAnswers) {
					pendingAnswers.put(Long.parseLong(commandSplitted[0]), commandSplitted[1]);
					pendingAnswers.notifyAll();	
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void sendMessage(String message, Number sender, Number reciever) throws IOException{
		synchronized (pout) {
			pout.println(requestId.getAndIncrement()+"$"+MBServerCommand.sendMessage.getValue()+"$"+sender.toString() + "$" + reciever.toString() + "$" + message);
		}
	}
	public String readMessage(Number id1, Number id2, int cnt) throws InterruptedException {
		long request = requestId.getAndIncrement();
		String res = null;
		synchronized (pout) {
			pout.println(request+"$"+MBServerCommand.readMessage.getValue()+"$"+id1.toString() + "$" + id2.toString());
		}
		
		synchronized (pendingAnswers) {
			// todo wait max 5s ?
			while(!pendingAnswers.containsKey(request)) {
				pendingAnswers.wait(); 
			};
			res = pendingAnswers.get(request);
			pendingAnswers.remove(request);
		}
		 return res;
	}
	public void close() throws IOException {
		pout.println("close");
		if(this.pin != null)this.pin.close();
		if(this.pout != null)this.pout.close();
		if(this.is != null)this.is.close();
		if(this.os != null)this.os.close();
		if(this.server != null  && !this.server.isClosed())this.server.close();
		this.requestHandlerThread.interrupt();
	}
	
}	
