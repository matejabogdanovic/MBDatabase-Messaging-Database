package MBDatabase;

import java.io.Serializable;

public class MBMessagesRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	public long id1;
	public long id2;
	public long fromCnt;
	public long cnt;
	public MBMessagesRequest(long id1, long id2, long fromCnt, long cnt) {
		this.id1 = id1;
		this.id2 = id2;
		this.fromCnt = fromCnt;
		this.cnt = cnt;
	} 
}
