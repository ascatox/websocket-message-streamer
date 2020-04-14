# WebSocket Message Streamer

## Installation
Clone the repo and launch `mvn install`

In a maven project include this dependency: 
```
<dependency>
    <groupId>it.eng.idsa</groupId>
    <artifactId>websocket-message-streamer</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```


## Usage

In the Bootstrap of your application `init` the library in this way (**Spring Boot** not required):
```
@Configuration
public class WebSockeMessageStreamerConfig {

    @Bean
    public FileRecreatorBeanExecutor fileRecreatorBeanTimer() throws SchedulerException {
       FileRecreatorBeanExecutor fileRecreatorBeanExecutor = WebSocketServerManager.fileRecreatorBeanExecutor();
              fileRecreatorBeanExecutor.setPort(9060); //optional default 9000
              fileRecreatorBeanExecutor.setKeystorePassword("server.jks"); //optional default classpath: ssl-server.jks
              fileRecreatorBeanExecutor.setKeystorePassword("password");
        return fileRecreatorBeanExecutor;
    }

      @Bean
        public IncomingDataAppResourceOverWs incomingDataAppResourceOverWs(){
            IncomingDataAppResourceOverWs incomingDataAppResourceOverWs = new IncomingDataAppResourceOverWs();
            WebSocketServerManager.messageWebSocketResponse().addPropertyChangeListener(incomingDataAppResourceOverWs);
            return incomingDataAppResourceOverWs;
        }
}

```
Example of class to manage the `Response` arrived from Server: 

```
public class IncomingDataAppResourceOverWs implements PropertyChangeListener {

    private String responseMessage;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.setResponseMessage((String) evt.getNewValue());
        WebSocketServerManager.messageWebSocketResponse().sendResponse(createDummyResponse(getResponseMessage()));
    }


    private String createDummyResponse(String responseMessage) {
        String responseString = null;
        try {
            String header = "A simple Header";
            // Put check sum in the payload
            String payload = "{\"checksum\":\"ABC123\"}";
            // prepare multipart message.
            responseString = new MultiPartMessage.Builder()
                    .setHeader(header)
                    .setPayload(payload)
                    .build()
                    .toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }
}

```


In A Rest Api or wherever you need, use the WS Client in this way:

```
 @PostMapping("sendFile")
    @ResponseBody
    public String sendFile(@RequestHeader("Forward-To") String forwardTo, @RequestBody String fileName) throws Exception {
        Resource resource = resourceLoader.getResource("classpath:examples-multipart-messages/" + fileName);
        return WebSocketClientManager.messageWebSocketSender().sendMultipartMessageWebSocketOverHttps(resource.getFile(), forwardTo);
    }

```

`forward-To` is the address of a WS Server.