package it.eng.idsa.streamer.websocket.sender.client;

import it.eng.idsa.streamer.WebSocketClientManager;
import org.asynchttpclient.ws.WebSocket;

public class ResponseMessageReceiverClient implements Runnable{

	private WebSocket wsClient = null;

	private static ResponseMessageReceiverClient instance;

	private ResponseMessageReceiverClient() {
	}

	public static ResponseMessageReceiverClient getInstance() {
		if (instance == null) {
			synchronized (ResponseMessageReceiverClient.class) {
				if (instance == null) {
					instance = new ResponseMessageReceiverClient();
				}
			}
		}
		return instance;
	}

	public void setup(WebSocket wsClient) {
		this.wsClient = wsClient;
	}
	
	@Override
	public void run() {
		byte[] responseMessage = WebSocketClientManager.responseMessageBufferWebSocketClient().remove();
		
		// Send the close frame 200 (OK), "Shutdown"; in this method we also close the wsClient.
		try {
		   wsClient.sendCloseFrame(200, "Shutdown");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
