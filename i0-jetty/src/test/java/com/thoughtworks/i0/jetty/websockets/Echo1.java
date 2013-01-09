package com.thoughtworks.i0.jetty.websockets;

import javax.net.websocket.Session;
import javax.net.websocket.annotations.WebSocketEndpoint;
import javax.net.websocket.annotations.WebSocketMessage;
import java.io.IOException;

@WebSocketEndpoint("/echo1")
public class Echo1 {

    @WebSocketMessage
    public void echo(String message, Session<?> session) throws IOException {
        session.getRemote().sendString(message);
    }

}