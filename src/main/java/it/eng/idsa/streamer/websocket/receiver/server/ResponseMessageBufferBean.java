package it.eng.idsa.streamer.websocket.receiver.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Milan Karajovic and Gabriele De Luca
 */

public class ResponseMessageBufferBean {

    //private BlockingQueue<byte[]> responseMessageQueue;

    private static ResponseMessageBufferBean instance;
    private static final Logger logger = LogManager.getLogger(ResponseMessageBufferBean.class);


    private ResponseMessageBufferBean() {
    //	this.responseMessageQueue = new ArrayBlockingQueue<>(1);
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

   /* public void add(byte[] msg) {
        try {
            responseMessageQueue.put(msg);
        } catch (InterruptedException e) {
           logger.error("ResponseMessageBufferBean error in add  with stack: "+ e.getMessage());
        }
    }

    public byte[] remove() {
        try {
            return responseMessageQueue.take();
        } catch (InterruptedException e) {
           logger.error("ResponseMessageBufferBean error in add  with stack: "+ e.getMessage());
        } finally {
            responseMessageQueue.clear();
        }
        return null;
    }*/
   private byte[] responseMessage = null;
    private boolean responseMessageIsReceived = false;

    public synchronized void add(byte[] msg) {
        if(responseMessageIsReceived) {
            try {
                wait();
            } catch(InterruptedException e) {
                logger.error("ResponseMessageBufferBean error in add method with stack: "+ e.getMessage());
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
                logger.error("ResponseMessageBufferBean error in remove method with stack: "+ e.getMessage());
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
