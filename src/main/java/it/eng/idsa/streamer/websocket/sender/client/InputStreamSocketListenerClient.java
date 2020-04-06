package it.eng.idsa.streamer.websocket.sender.client;

import it.eng.idsa.streamer.WebSocketClientManager;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;

/**
 * 
 * @author Milan Karajovic and Gabriele De Luca
 *
 */

public class InputStreamSocketListenerClient implements WebSocketListener {


	@Override
	public void onOpen(WebSocket websocket) {
		// TODO Auto-generated method stub
		
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
		WebSocketClientManager.responseMessageBufferWebSocketClient().add(payload);
	  }
}
