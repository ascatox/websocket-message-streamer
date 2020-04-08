package it.eng.idsa.streamer;

import it.eng.idsa.streamer.websocket.sender.MessageWebSocketSender;
import it.eng.idsa.streamer.websocket.sender.client.FileStreamingBean;
import it.eng.idsa.streamer.websocket.sender.client.InputStreamSocketListenerClient;
import it.eng.idsa.streamer.websocket.sender.client.ResponseMessageBufferClient;
import it.eng.idsa.streamer.websocket.sender.client.ResponseMessageReceiverClient;

/**
 * @author Milan Karajovic and Gabriele De Luca
 */

public class WebSocketClientManager {

    public static FileStreamingBean getFileStreamingWebSocket() {
        return FileStreamingBean.getInstance();
    }

    public static InputStreamSocketListenerClient getInputStreamSocketListenerWebSocketClient() {
        return new InputStreamSocketListenerClient();
    }

    public static ResponseMessageBufferClient getResponseMessageBufferWebSocketClient() {
        return ResponseMessageBufferClient.getInstance();
    }

    public static ResponseMessageReceiverClient getResponseMessageReceiverWebSocketClient() {
        return ResponseMessageReceiverClient.getInstance();
    }

    public static MessageWebSocketSender getMessageWebSocketSender() {
        return MessageWebSocketSender.getInstance();
    }

}
