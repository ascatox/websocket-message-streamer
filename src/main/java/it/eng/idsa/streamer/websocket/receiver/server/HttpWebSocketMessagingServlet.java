package it.eng.idsa.streamer.websocket.receiver.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Well, it's just a servlet declaration.
 * @author Antonio Scatoloni
 */
public class HttpWebSocketMessagingServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
       factory.register(HttpWebSocketListenerServer.class);
    }

}