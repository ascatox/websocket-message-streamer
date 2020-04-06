package it.eng.idsa.streamer.websocket.receiver.server;

/**
 * 
 * @author Milan Karajovic and Gabriele De Luca
 *
 */

public class RecreatedMultipartMessageBean {
	private String multipartMessage = null;
	private boolean multipartMessageIsRecreated = false;

	private static RecreatedMultipartMessageBean instance;

	private RecreatedMultipartMessageBean() {
	}

	public static RecreatedMultipartMessageBean getInstance() {
		if (instance == null) {
			synchronized (RecreatedMultipartMessageBean.class) {
				if (instance == null) {
					instance = new RecreatedMultipartMessageBean();
				}
			}
		}
		return instance;
	}


	public synchronized void set(String multipartMessage) {
		if(multipartMessageIsRecreated) {
			try {
				wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.multipartMessage = multipartMessage;
		multipartMessageIsRecreated = true;
		notify();
	}
	
	public synchronized String remove() {
		if(!multipartMessageIsRecreated) {
			try {
				wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		multipartMessageIsRecreated = false;
		try {
			return multipartMessage;
		}finally{
			notify();
			multipartMessage = null;
		}
	}
}
