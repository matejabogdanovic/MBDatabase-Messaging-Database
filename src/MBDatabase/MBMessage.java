package MBDatabase;

import java.io.Serializable;

public class MBMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	public long sender;
	public long reciever;
	public String content;
	public String date = ""; 
	public MBMessage(long sender, long reciever, String content, 
			String date) {
		this(sender, reciever, content);
		this.date = date;
	} 
	public MBMessage(long sender, long reciever, String content) {
		this.sender = sender;
		this.reciever = reciever;
		this.content = content; 
	} 
	@Override
	public String toString() {
		return "From: " + sender + 
				" To: " + reciever + 
				" Message: " + content + 
				" Date: " + date;
	}
}
