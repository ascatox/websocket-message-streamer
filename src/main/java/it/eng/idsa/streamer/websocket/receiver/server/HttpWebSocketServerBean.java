package it.eng.idsa.streamer.websocket.receiver.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.net.BindException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * Jetty Server instantiation with WebSocket over SSL
 *
 * @author Antonio Scatoloni
 */
public class HttpWebSocketServerBean {
    private static final Logger logger = LogManager.getLogger(HttpWebSocketServerBean.class);
    public static final String WS_URL = "/incoming-data-channel-received-message";

    private static HttpWebSocketServerBean instance;
    private static ResourceBundle configuration = ResourceBundle.getBundle("config");

    private HttpWebSocketServerBean() {

    }

    public static HttpWebSocketServerBean getInstance() {
        if (instance == null) {
            synchronized (HttpWebSocketServerBean.class) {
                if (instance == null) {
                    instance = new HttpWebSocketServerBean();
                }
            }
        }
        return instance;
    }

    private Server server;

    public Server createServer() {
        if (null == server) {
            try {
                setup();
                start();
            } catch (Exception e) {
                logger.error("Error on starting JETTY Server with stack: " + e.getMessage());
            }
        }
        return server;
    }

    public void setup() {
        URL url = getClass().getClassLoader().getResource(configuration.getString("server.ssl.key-store"));
        Resource resource = Resource.newResource(url);
        Path keystorePath = null;
        if (null != FileRecreatorBeanExecutor.getInstance().getKeystorePath())
            keystorePath = Paths.get(FileRecreatorBeanExecutor.getInstance().getKeystorePath());
        String password = configuration.getString("server.ssl.key-password");
        if (null != FileRecreatorBeanExecutor.getInstance().getKeystorePassword())
            password = FileRecreatorBeanExecutor.getInstance().getKeystorePassword();
        int port = Integer.parseInt(configuration.getString("server.ssl.port"));
        if (null != FileRecreatorBeanExecutor.getInstance().getPort())
            port = FileRecreatorBeanExecutor.getInstance().getPort();
        String path = WS_URL;
        if (null != FileRecreatorBeanExecutor.getInstance().getPath())
            path = FileRecreatorBeanExecutor.getInstance().getPath();
        HttpConfiguration http_config = getHttpConfiguration(port);
        SslContextFactory sslContextFactory = null;
        if (null == keystorePath)
            sslContextFactory = getSslContextFactory(resource, password);
        else
            sslContextFactory = getSslContextFactory(keystorePath, password);
        HttpConfiguration https_config = new HttpConfiguration(http_config);

        server = new Server();
        ServerConnector connector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory,
                        HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(https_config));
        connector.setPort(port);
        //connector.setReuseAddress(true);
        server.addConnector(connector);

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        handler.addServlet(HttpWebSocketMessagingServlet.class, path);
        server.setHandler(handler);
    }

    public void start() {
        try {
            server.start();
            //server.join();
        } catch (BindException e) {
            logger.warn("Port ALREADY used: " + e.getMessage());
        } catch (Exception e) {
            logger.error("ERROR on starting Jetty Server: " + e.getMessage());
        }
    }

    public void stop() throws Exception {
        if (null != server && (server.isStarted() || server.isRunning())) {
            server.stop();
            server = null;
        }
    }

    private SslContextFactory getSslContextFactory(Path keystorePath, String password) {
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(keystorePath.toAbsolutePath().toString());
        sslContextFactory.setKeyStorePassword(password);
        sslContextFactory.setKeyManagerPassword(password);
        return sslContextFactory;
    }

    private SslContextFactory getSslContextFactory(Resource resource, String password) {
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStoreResource(resource);
        sslContextFactory.setKeyStorePassword(password);
        sslContextFactory.setKeyManagerPassword(password);
        return sslContextFactory;
    }

    private HttpConfiguration getHttpConfiguration(int port) {
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(port);
        http_config.addCustomizer(new SecureRequestCustomizer());
        return http_config;
    }

}