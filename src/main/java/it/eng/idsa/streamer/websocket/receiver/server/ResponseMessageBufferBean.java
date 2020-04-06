package it.eng.idsa.streamer.websocket.receiver.server;

/**
 * 
 * @author Milan Karajovic and Gabriele De Luca
 *
 */

public class ResponseMessageBufferBean {
	
	private byte[] responseMessage = null;
	private boolean responseMessageIsReceived = false;

	private static ResponseMessageBufferBean instance;

	private ResponseMessageBufferBean() {
	}

	public static ResponseMessageBufferBean getInstance() {
		if (instance == null) {
			synchronized (ResponseMessageBufferBean.class) {
				if (instance == null) {
					instance = new ResponseMessageBufferBean();
				}
			}
		}
		return instance;
	}
	
	public synchronized void add(byte[] msg) {
		if(responseMessageIsReceived) {
			try {
				wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.responseMessage = msg;
		responseMessageIsReceived = true;
		notify();
	}
	
	public synchronized byte[] remove() {
		if(!responseMessageIsReceived) {
			try {
				wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		responseMessageIsReceived = false;
		try {
			return responseMessage;
		}finally{
			notify();
			responseMessage = null;
		}
	}
}
