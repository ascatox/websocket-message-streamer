package it.eng.idsa.streamer.websocket.receiver;

import it.eng.idsa.streamer.WebSocketServerManager;
import it.eng.idsa.streamer.websocket.receiver.server.ResponseMessageBufferBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MessageWebSocketResponse {

    private static final Logger logger = LogManager.getLogger(MessageWebSocketResponse.class);
    private static MessageWebSocketResponse instance;
    private PropertyChangeSupport support;
    private String multipartMessage;


    private MessageWebSocketResponse() {
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setMultipartMessage(String multipartMessage) {
        //logger.info("Message arrived to consumer with data: " + multipartMessage);
        support.firePropertyChange("multipartMessage", this.multipartMessage, multipartMessage);
        this.multipartMessage = multipartMessage;
    }

    public static MessageWebSocketResponse getInstance() {
        if (instance == null) {
            synchronized (MessageWebSocketResponse.class) {
                if (instance == null) {
                    instance = new MessageWebSocketResponse();
                }
            }
        }
        return instance;
    }

    public void sendResponse(String response) {
        final ResponseMessageBufferBean responseMessageBufferBean = WebSocketServerManager.responseMessageBufferWebSocket();
        responseMessageBufferBean.add(response.getBytes()); //Unlock
    }
}

