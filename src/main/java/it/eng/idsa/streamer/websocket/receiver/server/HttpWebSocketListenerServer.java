package it.eng.idsa.streamer.websocket.receiver.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketPartialListener;

import java.nio.ByteBuffer;

/**
 * Example MessagingListenerServer using PartialListener.
 * @author Antonio Scatoloni
 */
public class HttpWebSocketListenerServer implements WebSocketPartialListener {
    private static final Logger logger = LogManager.getLogger(HttpWebSocketListenerServer.class);
    private Session session;

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        this.session = null;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        this.session = session;
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        logger.error(cause);
    }

    @Override
    public void onWebSocketPartialBinary(ByteBuffer byteBuffer, boolean b) {
        byte[] arr = new byte[byteBuffer.remaining()];
        byteBuffer.get(arr);
        HttpWebSocketMessagingLogic.getInstance().onMessage(session, arr);
    }

    @Override
    public void onWebSocketPartialText(String s, boolean b) {
        HttpWebSocketMessagingLogic.getInstance().onMessage(session, s.getBytes());
    }
}