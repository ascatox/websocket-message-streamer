package it.eng.idsa.streamer;

import it.eng.idsa.streamer.websocket.receiver.MessageWebSocketResponse;
import it.eng.idsa.streamer.websocket.receiver.server.FileRecreatorBeanExecutor;
import it.eng.idsa.streamer.websocket.receiver.server.*;
import org.quartz.SchedulerException;

/**
 * @author Milan Karajovic and Gabriele De Luca
 */

public class WebSocketServerManager {


    public static FrameBufferBean frameBufferWebSocket() {
        return FrameBufferBean.getInstance();
    }

    /**
     * @return
     * @author Antonio Scatoloni
     */
    public static HttpWebSocketServerBean httpsServerWebSocket() {
        return HttpWebSocketServerBean.getInstance();
    }

    public static FileRecreatorBeanServer fileRecreatorBeanWebSocket() {
        return FileRecreatorBeanServer.getInstance();
    }

    /**
     * @return
     * @author Antonio Scatoloni
     */
    public static HttpWebSocketMessagingLogic messagingLogic() {
        return HttpWebSocketMessagingLogic.getInstance();
    }

    public static RecreatedMultipartMessageBean recreatedMultipartMessageBeanWebSocket() {
        return RecreatedMultipartMessageBean.getInstance();
    }

    public static ResponseMessageBufferBean responseMessageBufferWebSocket() {
        return ResponseMessageBufferBean.getInstance();
    }

    public static ResponseMessageSendPartialServer responseMessageSendPartialWebSocket() {
        return ResponseMessageSendPartialServer.getInstance();
    }

    public static FileRecreatorBeanExecutor fileRecreatorBeanExecutor() throws SchedulerException {
        return FileRecreatorBeanExecutor.getInstance();
    }

    public static MessageWebSocketResponse messageWebSocketResponse() {
        return MessageWebSocketResponse.getInstance();
    }

}
