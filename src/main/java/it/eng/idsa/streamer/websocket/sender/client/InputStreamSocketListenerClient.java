package it.eng.idsa.streamer.websocket.sender.client;

import it.eng.idsa.streamer.WebSocketClientManager;
import it.eng.idsa.streamer.websocket.receiver.server.FileRecreatorBeanExecutor;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;

/**
 * @author Milan Karajovic and Gabriele De Luca
 */

public class InputStreamSocketListenerClient implements WebSocketListener {


    @Override
    public void onOpen(WebSocket websocket) {
        String forwardTo = FileRecreatorBeanExecutor.getInstance().getForwardTo();
        if (null != forwardTo && !forwardTo.isEmpty()) {
            websocket.sendTextFrame("Forward-To:" + forwardTo.trim());
        }
    }

    @Override
    public void onClose(WebSocket websocket, int code, String reason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError(Throwable t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBinaryFrame(byte[] payload, boolean finalFragment, int rsv) {
        WebSocketClientManager.getResponseMessageBufferWebSocketClient().add(payload);
    }
}
