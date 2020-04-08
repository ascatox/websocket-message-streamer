package it.eng.idsa.streamer.websocket.receiver.server;


import it.eng.idsa.streamer.WebSocketServerManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;

import java.nio.charset.StandardCharsets;

/**
 * HttpWebSocketMessagingLogic will be responsible for parsing received data
 *
 * @author Antonio Scatoloni
 */
public class HttpWebSocketMessagingLogic {
    private static final Logger logger = LogManager.getLogger(HttpWebSocketMessagingLogic.class);

    private static HttpWebSocketMessagingLogic instance;

    public static final String CLOSURE_FRAME = "�normal closure";
    public static final String END_BINARY_FRAME_SEPARATOR = "�normal-IDS-ENG-SEPARATOR the-last-frame";

    private HttpWebSocketMessagingLogic() {
    }

    public static HttpWebSocketMessagingLogic getInstance() {
        if (instance == null) {
            instance = new HttpWebSocketMessagingLogic();
        }
        return instance;
    }

    public void onMessage(Session session, byte[] message) {
        String receivedMessage = new String(message, StandardCharsets.UTF_8);
       if (receivedMessage.equals(CLOSURE_FRAME)) {
            // The last frame is received - skip this frame
            // This indicate that Client WebSocket now is closed
        } else {
            // Put the received frame in the frameBuffer
           WebSocketServerManager.getFrameBufferWebSocket().add(message.clone());
            if (receivedMessage.equals(END_BINARY_FRAME_SEPARATOR)) {
                ResponseMessageSendPartialServer responseMessageSendPartialServer = WebSocketServerManager.responseMessageSendPartialWebSocket();
                responseMessageSendPartialServer.setup(session);
                Thread responseMessageSendPartialServerThread = new Thread(responseMessageSendPartialServer, "ResponseMessageSendPartialServer");
                responseMessageSendPartialServerThread.start();
            }
           logger.info(HttpWebSocketMessagingLogic.class.getSimpleName() +" DATA RECEIVED FROM SOCKET -> " + receivedMessage);

       }
    }

}