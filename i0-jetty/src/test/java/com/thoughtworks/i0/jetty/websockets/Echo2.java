package com.thoughtworks.i0.jetty.websockets;

import javax.inject.Inject;
import javax.net.websocket.annotations.WebSocketEndpoint;
import javax.net.websocket.annotations.WebSocketMessage;

@WebSocketEndpoint("/echo2")
public class Echo2 {
    @Inject
    Service service;

    @WebSocketMessage
    public String echo(String message) {
        return service.message() + " " + message;
    }

}