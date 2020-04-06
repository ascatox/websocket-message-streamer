package it.eng.idsa.streamer.websocket.sender.client;

/**
 * 
 * @author Milan Karajovic and Gabriele De Luca
 *
 */

public class ResponseMessageBufferClient {
	private byte[] responseMessage = null;
	private boolean responseMessageIsReceived = false;

	private static ResponseMessageBufferClient instance;

	private ResponseMessageBufferClient() {
	}

	public static ResponseMessageBufferClient getInstance() {
		if (instance == null) {
			synchronized (ResponseMessageBufferClient.class) {
				if (instance == null) {
					instance = new ResponseMessageBufferClient();
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
