package it.eng.idsa.streamer.websocket.sender.client;

import it.eng.idsa.streamer.WebSocketClientManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.asynchttpclient.ws.WebSocket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class FileStreamingBean {

    private static final String START_BINARY_FRAME_SEPARATOR = "�normal-IDS-ENG-SEPARATOR the-first-rame";

    private static final String END_BINARY_FRAME_SEPARATOR = "�normal-IDS-ENG-SEPARATOR the-last-frame";

    private static final Logger logger = LogManager.getLogger(FileStreamingBean.class);

    private WebSocket wsClient = null;
    private String serverIP;
    private int serverPort;

    private static final int DEFAULT_STREAM_BUFFER_SIZE = 127;
    private int streamBufferSize = DEFAULT_STREAM_BUFFER_SIZE;

    private static FileStreamingBean instance;

    private FileStreamingBean() {
    }

    public static FileStreamingBean getInstance() {
        if (instance == null) {
            synchronized (FileStreamingBean.class) {
                if (instance == null) {
                    instance = new FileStreamingBean();
                }
            }
        }
        return instance;
    }

    public void setup(WebSocket wsClient) {
        this.wsClient = wsClient;
    }

    public void sendMultipartMessage(String multipartMessage) throws KeyManagementException, NoSuchAlgorithmException, InterruptedException, ExecutionException, IOException {
        // Convert multipartMessage to the InputStream
        InputStream multipartMessageStream = new ByteArrayInputStream(multipartMessage.getBytes());

        if (wsClient.isOpen()) {
            try {
                // Send multipartMessageStream as stream of the frames using the webSocket
                sendStreamMessage(wsClient, multipartMessageStream);
            } finally {
                if (multipartMessageStream != null) {
                    multipartMessageStream.close();
                }
            }
        }

        ResponseMessageReceiverClient responseMessageReceiverClient = WebSocketClientManager.getResponseMessageReceiverWebSocketClient();
        //Thread responseMessageReceiverClientThread = new Thread(responseMessageReceiverClient, "ResponseMssageReceiverClientThread");

    }

    // Streaming the frames
    private void sendStreamMessage(WebSocket webSocket, InputStream in) throws IOException {
        byte[] readbuf = new byte[streamBufferSize];
        byte[] writebuf = new byte[streamBufferSize];
        int rn;
        int wn = 0;

        // The first Frame should be BinaryFrame
        webSocket.sendBinaryFrame(START_BINARY_FRAME_SEPARATOR.getBytes(), false, 0);

        // Send content of the inputStream using the Frames
        byte[] tempwritebuf = new byte[streamBufferSize];
//	    int counterPUSH = 0;
        while ((rn = in.read(readbuf, 0, readbuf.length)) != -1) {
            if (wn > 0) {
                tempwritebuf = writebuf.clone();
//				System.out.println("============================");
//				System.out.println("The message PUSH (number " + ++counterPUSH + "): " + new String(tempwritebuf, StandardCharsets.UTF_8));
//				System.out.println("============================");
                webSocket.sendContinuationFrame(tempwritebuf, false, 0);
            }
            System.arraycopy(readbuf, 0, writebuf, 0, rn);
            wn = rn;
        }

        // a bug in grizzly? we need to create a byte array with the exact length
        if (wn < writebuf.length) {
            byte[] tmpbuf = writebuf;
            tempwritebuf = new byte[wn];
            writebuf = new byte[wn];
            System.arraycopy(tmpbuf, 0, writebuf, 0, wn);
        } // ends

        // Send the last frame frame of the inputStream
        tempwritebuf = writebuf.clone();
        webSocket.sendContinuationFrame(tempwritebuf, true, 0);
        logger.info("Sent the last frame from the large message");
        webSocket.sendBinaryFrame(END_BINARY_FRAME_SEPARATOR.getBytes(), false, 0);
        logger.info("Sent the the-end-binary-frame-separator");
    }

}
