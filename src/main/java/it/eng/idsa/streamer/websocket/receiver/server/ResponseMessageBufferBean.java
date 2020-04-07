package it.eng.idsa.streamer.websocket.receiver.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Milan Karajovic and Gabriele De Luca
 */

public class ResponseMessageBufferBean {

    private BlockingQueue<byte[]> responseMessageQueue;

    private static ResponseMessageBufferBean instance;

    private ResponseMessageBufferBean() {
    	this.responseMessageQueue = new ArrayBlockingQueue<>(1);
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

    public void add(byte[] msg) {
        try {
            responseMessageQueue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public byte[] remove() {
        try {
            return responseMessageQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseMessageQueue.clear();
        }
        return null;
    }
}
