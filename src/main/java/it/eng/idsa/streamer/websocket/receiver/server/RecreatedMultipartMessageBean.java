package it.eng.idsa.streamer.websocket.receiver.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Milan Karajovic and Gabriele De Luca
 */

public class RecreatedMultipartMessageBean {
    private BlockingQueue<String> multipartMessageQueue;

    private static RecreatedMultipartMessageBean instance;

    private RecreatedMultipartMessageBean() {
        this.multipartMessageQueue = new ArrayBlockingQueue<>(1);
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


    public void set(String multipartMessage) {
        try {
            multipartMessageQueue.put(multipartMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String remove() {
        try {
            return multipartMessageQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            multipartMessageQueue.clear();
        }
        return null;
    }
}
