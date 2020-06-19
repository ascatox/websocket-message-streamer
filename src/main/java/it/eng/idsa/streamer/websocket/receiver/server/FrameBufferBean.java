package it.eng.idsa.streamer.websocket.receiver.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.*;

/**
 * @author Milan Karajovic and Gabriele De Luca
 */

public class FrameBufferBean {
    private BlockingQueue<byte[]> frameQueue;

    private static FrameBufferBean instance;
    private static final Logger logger = LogManager.getLogger(FrameBufferBean.class);


    private FrameBufferBean() {
        this.frameQueue = new ArrayBlockingQueue<>(1);
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
            logger.error("FrameBufferBean error in add method  with stack: " + e.getMessage());
        }
    }

    public byte[] remove() {
        try {
            return frameQueue.take();
        } catch (InterruptedException e) {
            logger.error("FrameBufferBean error in remove method with stack: " + e.getMessage());
        } finally {
            //frameQueue.clear();
        }
        return null;
    }

}
