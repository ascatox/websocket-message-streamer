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

In the Bootstrap of your application `init the library in this way (**Spring Boot** not required):
```
@Configuration
public class WebSockeMessageStreamerConfig {

    @Bean
    public FileRecreatorBeanExecutor fileRecreatorBeanTimer() throws SchedulerException {
       FileRecreatorBeanExecutor fileRecreatorBeanExecutor = WebSocketServerManager.fileRecreatorBeanExecutor();
              fileRecreatorBeanExecutor.setPort(9060); //optional default 9000
              fileRecreatorBeanExecutor.setKeystorePassword("ssl-server.jks"); //optional default classpath: ssl-server.jks
              fileRecreatorBeanExecutor.setKeystorePassword("password");
        return fileRecreatorBeanExecutor;
    }
}

```


In A Rest Api or wherever you need, use the WS Client in this way:

```
 @PostMapping("/start")
    @ResponseBody
    public String start(@RequestHeader("Forward-To") String forwardTo, @RequestBody String fileName) throws Exception {
        Resource resource = resourceLoader.getResource("classpath:examples-multipart-messages/" + fileName);
        return WebSocketClientManager.messageWebSocketSender().sendMultipartMessageWebSocketOverHttps(resource.getFile(), forwardTo);
    }

```

`forward-To` is the address of a WS Server.