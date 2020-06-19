package it.eng.idsa.streamer.websocket.receiver.server;

import it.eng.idsa.streamer.WebSocketServerManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author Milan Karajovic and Gabriele De Luca
 */

public class FileRecreatorBeanServer implements Runnable {

    private static final Logger logger = LogManager.getLogger(FileRecreatorBeanServer.class);

    private static final int DEFAULT_STREAM_BUFFER_SIZE = 127;
    // TODO: should fix these paths and file name
    private static final String END_BINARY_FRAME_SEPARATOR = "�normal-IDS-ENG-SEPARATOR the-last-frame";


    private FrameBufferBean frameBuffer;
    private ArrayList<byte[]> fileByteArray = new ArrayList<byte[]>();
    private ByteBuffer byteBuffer = null;
    private RecreatedMultipartMessageBean recreatedmultipartMessage;

    private static FileRecreatorBeanServer instance;

    private FileRecreatorBeanServer() {

    }

    public static FileRecreatorBeanServer getInstance() {
        if (instance == null) {
            synchronized (FileRecreatorBeanServer.class) {
                if (instance == null) {
                    instance = new FileRecreatorBeanServer();
                }
            }
        }
        return instance;
    }

    public void setup() {
        this.frameBuffer = WebSocketServerManager.getFrameBufferWebSocket();
        this.recreatedmultipartMessage = WebSocketServerManager.recreatedMultipartMessageBeanWebSocket();
        HttpWebSocketServerBean httpWebSocketServerBean = WebSocketServerManager.getHttpsServerWebSocket();
        httpWebSocketServerBean.createServer();
    }


    @Override
    public void run() {
        receiveAllFrames();
        recreatedmultipartMessage.set(recreateMultipartMessageFromReceivedFrames());
    }

    private void receiveAllFrames() {
        boolean allFramesReceived = false;

        while (!allFramesReceived) {
            byte[] receivedFrame = this.frameBuffer.remove();

            try {
                if ((new String(receivedFrame, StandardCharsets.UTF_8)).equals(END_BINARY_FRAME_SEPARATOR)) {
                    allFramesReceived = true;
                    logger.info("Received the last frames: " + END_BINARY_FRAME_SEPARATOR);
                } else {
                    this.fileByteArray.add(receivedFrame.clone());
                }
            } finally {
                receivedFrame = null;
            }
        }
    }

    private byte[] getAllFrames(ArrayList<byte[]> fileByteArray) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DEFAULT_STREAM_BUFFER_SIZE * fileByteArray.size());
        // fileByteArray(0) is our header of the File, which should not be included in the byte[] of file
        for (int i = 1; i < fileByteArray.size(); i++) {
            byteBuffer.put(fileByteArray.get(i));
        }
        return byteBuffer.array();
    }

    private String recreateMultipartMessageFromReceivedFrames() {
        String multipartMessage = null;
        try {
            logger.info("Started process: Recreate the Multipart message from the received frames");
            multipartMessage = recreateMultipartMessage(this.fileByteArray);
            logger.info("Recreated the Multipart message from the received frames: length=" + multipartMessage.length());
        } catch (IOException e) {
            logger.error("Error on the process of recreation the file from the received frames:" + e.getMessage());
        }
        return multipartMessage;
    }

    private String recreateMultipartMessage(ArrayList<byte[]> fileByteArray) throws IOException {
        byte[] allFrames = getAllFrames(fileByteArray);
        String multipartMessage = new String(allFrames, StandardCharsets.UTF_8);
        return multipartMessage;
    }

}
