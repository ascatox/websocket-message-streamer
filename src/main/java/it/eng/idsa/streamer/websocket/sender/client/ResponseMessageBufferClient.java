package it.eng.idsa.streamer.websocket.sender.client;

import it.eng.idsa.streamer.websocket.receiver.server.ResponseMessageBufferBean;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Milan Karajovic and Gabriele De Luca
 */

public class ResponseMessageBufferClient {

	//private BlockingQueue<byte[]> responseMessageQueue;
    private static ResponseMessageBufferClient instance;
    private static final Logger logger = LogManager.getLogger(ResponseMessageBufferClient.class);


    private ResponseMessageBufferClient() {
        //this.responseMessageQueue = new ArrayBlockingQueue<>(1);
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



    /*public void add(byte[] msg) {
        try {
            responseMessageQueue.put(msg);
        } catch (InterruptedException e) {
           logger.error("ResponseMessageBufferClient error in add  with stack: "+ e.getMessage());
        }
    }

    public byte[] remove() {
        try {
            return responseMessageQueue.take();
        } catch (InterruptedException e) {
            logger.error("ResponseMessageBufferClient error in remove  with stack: "+ e.getMessage());
        } finally {
            responseMessageQueue.clear(); //with capacity 1 not mandatory
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
                logger.error("ResponseMessageBufferClient error in add method with stack: "+ e.getMessage());
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
                logger.error("ResponseMessageBufferClient error in remove method with stack: "+ e.getMessage());
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
