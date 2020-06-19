package it.eng.idsa.streamer.websocket.receiver.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Milan Karajovic and Gabriele De Luca
 */

public class RecreatedMultipartMessageBean {
    private BlockingQueue<String> multipartMessageQueue;
    private static RecreatedMultipartMessageBean instance;
    private static final Logger logger = LogManager.getLogger(RecreatedMultipartMessageBean.class);


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
             logger.error("RecreatedMultipartMessageBean error in set method  with stack: "+ e.getMessage());
        }
    }

    public String remove() {
        try {
            return multipartMessageQueue.take();
        } catch (InterruptedException e) {
              logger.error("RecreatedMultipartMessageBean error in remove method  with stack: "+ e.getMessage());
        } finally {
            //multipartMessageQueue.clear();
        }
        return null;
    }
    /*private String multipartMessage = null;
    private boolean multipartMessageIsRecreated = false;

    public synchronized void set(String multipartMessage) {
        if(multipartMessageIsRecreated) {
            try {
                wait();
            } catch(InterruptedException e) {
                logger.error("RecreatedMultipartMessageBean error method in set with stack: "+ e.getMessage());
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
                logger.error("RecreatedMultipartMessageBean error method in remove with stack: "+ e.getMessage());
            }
        }

        multipartMessageIsRecreated = false;
        try {
            return multipartMessage;
        }finally{
            notify();
            multipartMessage = null;
        }
    }*/
}
