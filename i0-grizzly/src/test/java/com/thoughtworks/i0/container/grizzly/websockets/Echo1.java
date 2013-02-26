package com.thoughtworks.i0.container.grizzly.websockets;

import javax.websocket.Session;
import javax.websocket.server.WebSocketEndpoint;
import javax.websocket.WebSocketMessage;
import java.io.IOException;

@WebSocketEndpoint("/echo1")
public class Echo1 {

    @WebSocketMessage
    public void echo(String message, Session session) throws IOException {
        session.getRemote().sendString(message);
    }

}