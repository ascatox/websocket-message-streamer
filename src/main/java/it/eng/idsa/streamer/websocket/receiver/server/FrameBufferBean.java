package it.eng.idsa.streamer.websocket.receiver.server;

import java.util.concurrent.*;

/**
 *
 * @author Milan Karajovic and Gabriele De Luca
 *
 */

public class FrameBufferBean {
	// Pattern Producer Consumer implemented using BlockinqQueue is a newer and simpler implementation!
	private BlockingQueue<byte[]> frameQueue;

	private static FrameBufferBean instance;

	private FrameBufferBean() {
		this.frameQueue  = new ArrayBlockingQueue<>(1);
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

	public void add(byte[] msg) {
		try {
			frameQueue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public  byte[] remove() {
		try {
			return frameQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			frameQueue.clear();
		}
		return null;
	}
}
