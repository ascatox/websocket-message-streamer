package it.eng.idsa.streamer.websocket.sender;

import de.fraunhofer.iais.eis.Message;
import it.eng.idsa.multipart.builder.MultipartMessageBuilder;
import it.eng.idsa.multipart.domain.MultipartMessage;
import it.eng.idsa.multipart.processor.MultipartMessageProcessor;
import it.eng.idsa.streamer.WebSocketClientManager;
import it.eng.idsa.streamer.service.RejectionMessageService;
import it.eng.idsa.streamer.util.MultiPartMessageServiceUtil;
import it.eng.idsa.streamer.util.RejectionMessageType;
import it.eng.idsa.streamer.websocket.sender.client.FileStreamingBean;
import it.eng.idsa.streamer.websocket.receiver.server.HttpWebSocketServerBean;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.SslEngineFactory;
import org.asynchttpclient.netty.ssl.JsseSslEngineFactory;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.asynchttpclient.Dsl.asyncHttpClient;

/**
 * @author Antonio Scatoloni
 */

public class MessageWebSocketSender {
    private static final Logger logger = LogManager.getLogger(MessageWebSocketSender.class);
    public static final String REGEX_WSS = "(wss://)([^:^/]*)(:)(\\d*)";

    private static MessageWebSocketSender instance;

    private MessageWebSocketSender() {
    }

    public static MessageWebSocketSender getInstance() {
        if (instance == null) {
            synchronized (MessageWebSocketSender.class) {
                if (instance == null) {
                    instance = new MessageWebSocketSender();
                }
            }
        }
        return instance;
    }

    public String sendMultipartMessageWebSocketOverHttps(File file, String forwardTo)
            throws Exception {
        InputStream inputStream = new FileInputStream(file);
        String message = IOUtils.toString(inputStream, "UTF8");
        String header = MultiPartMessageServiceUtil.getHeader(message);
        String payload = MultiPartMessageServiceUtil.getPayload(message);
        return doSendMultipartMessageWebSocketOverHttps(header, payload, forwardTo, null);
    }

    public String sendMultipartMessageWebSocketOverHttps(String message, String forwardTo)
            throws Exception {
        String header = MultiPartMessageServiceUtil.getHeader(message);
        String payload = MultiPartMessageServiceUtil.getPayload(message);
        return doSendMultipartMessageWebSocketOverHttps(header, payload, forwardTo, null);
    }

    public String sendMultipartMessageWebSocketOverHttps(String header, String payload, String forwardTo)
            throws Exception {
        return doSendMultipartMessageWebSocketOverHttps(header, payload, forwardTo, null);
    }

    public String sendMultipartMessageWebSocketOverHttps(String header, String payload, String forwardTo, Message message)
            throws Exception {
        return doSendMultipartMessageWebSocketOverHttps(header, payload, forwardTo, message);
    }

    private String doSendMultipartMessageWebSocketOverHttps(String header, String payload, String forwardTo, Message message)
            throws Exception {
    	MultipartMessage multipartMessage = new MultipartMessageBuilder()
                .withHeaderContent(header)
                .withPayloadContent(payload)
                .build();
        FileStreamingBean fileStreamingBean = WebSocketClientManager.getFileStreamingWebSocket();
        WebSocket wsClient = createWebSocketClient(message, getWebSocketUrl(forwardTo));
        // Try to connect to the Server. Wait until you are not connected to the server.
        fileStreamingBean.setup(wsClient);
        fileStreamingBean.sendMultipartMessage(MultipartMessageProcessor.multipartMessagetoString(multipartMessage, false));
        //fileStreamingBean.sendMultipartMessage(multipartMessage);
        // We don't have status of the response (is it 200 OK or not). We have only the content of the response.
        String responseMessage = new String(WebSocketClientManager.getResponseMessageBufferWebSocketClient().remove());
        closeWSClient(wsClient, message);
        logger.info("received response: " + responseMessage);
        return responseMessage;
    }

    private WebSocket createWebSocketClient(Message message, String url) throws Exception {
        WebSocket wsClient = null;
        try {
            final SslEngineFactory ssl = getSslEngineFactory();

            DefaultAsyncHttpClientConfig clientConfig = new DefaultAsyncHttpClientConfig.Builder()
                    .setDisableHttpsEndpointIdentificationAlgorithm(true)
                    .setUseOpenSsl(true)
                    .setSslEngineFactory(ssl)
                    .build();

            WebSocketUpgradeHandler.Builder upgradeHandlerBuilder
                    = new WebSocketUpgradeHandler.Builder();
            WebSocketUpgradeHandler wsHandler = upgradeHandlerBuilder
                    .addWebSocketListener(WebSocketClientManager.getInputStreamSocketListenerWebSocketClient()).build();
            wsClient = asyncHttpClient(clientConfig)
                    .prepareGet(url)
                    .execute(wsHandler)
                    .get();
        } catch (Exception e) {
            logger.error("... can not create the WebSocket connection with error: "+e.getMessage());
            if (null != message)
                RejectionMessageService.sendRejectionMessage(
                        RejectionMessageType.REJECTION_COMMUNICATION_LOCAL_ISSUES,
                        message);
        }
        return wsClient;
    }

    private SslEngineFactory getSslEngineFactory() throws NoSuchAlgorithmException, KeyManagementException {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                   String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                   String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                }
        };
        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        return new JsseSslEngineFactory(sslContext);
    }


    private void closeWSClient(WebSocket wsClient, Message message) throws Exception {
        // Send the close frame 1000 (CLOSE), "Shutdown"; in this method we also close the wsClient.
        try {
            wsClient.sendCloseFrame(1000, "Shutdown");
        } catch (Exception e) {
            logger.error("Problems encountered during Client Shutdown with error: " + e.getMessage());
            if (null != message)
                RejectionMessageService.sendRejectionMessage(
                        RejectionMessageType.REJECTION_COMMUNICATION_LOCAL_ISSUES,
                        message);
        }
    }

    private String getWebSocketUrl(String forwardTo) {
        //Example of Forward-to : wss://localhost:8086
        logger.info("Use IDSCP port for WS over https! Forward to: "+forwardTo);
        Pattern pattern = Pattern.compile(REGEX_WSS);
        Matcher matcher = pattern.matcher(forwardTo);
        matcher.find();
        String webSocketHost = matcher.group(2);
        int webSocketPort = Integer.parseInt(matcher.group(4));
        return "wss://" + webSocketHost + ":" + webSocketPort + HttpWebSocketServerBean.WS_URL;
    }

}
