package it.eng.idsa.streamer.websocket.receiver.server;

/**
 * 
 * @author Milan Karajovic and Gabriele De Luca
 *
 */

public class FrameBufferBean {
	private byte[] frame = null;
	private boolean frameIsReceived = false;

	private static FrameBufferBean instance;

	private FrameBufferBean() {
	}

	public static FrameBufferBean getInstance() {
		if (instance == null) {
			synchronized (FrameBufferBean.class) {
				if (instance == null) {
					instance = new FrameBufferBean();
				}
			}
		}
		return instance;
	}

	public synchronized void add(byte[] msg) {
		if(frameIsReceived) {
			try {
				wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.frame = msg;
		frameIsReceived = true;
		notify();
	}
	
	public synchronized byte[] remove() {
		if(!frameIsReceived) {
			try {
				wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		frameIsReceived = false;
		try {
			return frame;
		}finally{
			notify();
			frame = null;
		}
	}
}
