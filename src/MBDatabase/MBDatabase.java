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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MBDatabase implements Runnable{
	private Socket server;
	private OutputStream os;
	private InputStream is; 
	private ObjectOutputStream pout;
	private ObjectInputStream pin;
	private AtomicLong requestId = new AtomicLong(0);
	private Map<Long, Object> pendingAnswers = new HashMap<Long, Object>();
	
	private Thread requestHandlerThread = new Thread(this);
	public MBDatabase(String host, int port) throws UnknownHostException, IOException {
		try {
			this.server = new Socket(host,port);
			this.os = server.getOutputStream();
			this.is = server.getInputStream();
			this.pout = new ObjectOutputStream(os);
			this.pin = new ObjectInputStream(is); 
			this.requestHandlerThread.setDaemon(true);
			this.requestHandlerThread.start();
		}catch (Exception e) {
			close();
			throw e;
		} 
	} 
	
	@Override
	public void run() {
		Object s;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				
				s = pin.readObject();

				MBPacket packet = (MBPacket)s;
				System.out.println("Wait pending.");
				synchronized (pendingAnswers) {
					System.out.println("Pending ok.");
					pendingAnswers.put(packet.packetId, packet.payload);
					pendingAnswers.notifyAll();	
				}
				
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finished thread.");
		
	}
	public void sendMessage(String message, long sender, long reciever) throws IOException{
		synchronized (pout) {
			pout.writeObject(
					new MBPacket(requestId.getAndIncrement(), 
					MBServerCommand.sendMessage, 
					new MBMessage(sender, reciever, message)));
		}
	}
	
	public ArrayList<MBMessage> readMessage(long id1, long id2, int cnt) throws IOException, InterruptedException{
		return readMessage(id1, id2, 0, cnt);
	}
	@SuppressWarnings("unchecked")
	public ArrayList<MBMessage> readMessage(long id1, long id2, int fromCnt,  int cnt) throws IOException, InterruptedException {
		long request = requestId.getAndIncrement();
		ArrayList<MBMessage> res = null;
		synchronized (pout) {
			pout.writeObject( 
					new MBPacket(request, 
					MBServerCommand.readMessage, 
					new MBMessagesRequest(id1, id2, fromCnt, cnt)));
		}
		
		synchronized (pendingAnswers) {
			// todo wait max 5s ?
			while(!pendingAnswers.containsKey(request)) {
				pendingAnswers.wait(); 
			};
			res = (ArrayList<MBMessage>) pendingAnswers.get(request);
			pendingAnswers.remove(request);
		}
		 return res;
	}
	public void close() throws IOException {
		System.out.println("Interrupting"); 
		this.requestHandlerThread.interrupt();
		System.out.println("Interrupted");
	
		pout.writeObject(new MBPacket(requestId.getAndIncrement(), 
				MBServerCommand.closeConnection, null));
		if(this.pin != null)this.pin.close();
		if(this.pout != null)this.pout.close();
		if(this.is != null)this.is.close();
		if(this.os != null)this.os.close();
		if(this.server != null  && !this.server.isClosed())this.server.close();
		
	}
	 
}	
