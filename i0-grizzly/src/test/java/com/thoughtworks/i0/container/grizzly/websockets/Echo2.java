package com.thoughtworks.i0.container.grizzly.websockets;

import javax.inject.Inject;

import javax.websocket.server.WebSocketEndpoint;
import javax.websocket.WebSocketMessage;


@WebSocketEndpoint("/echo2")
public class Echo2 {
    @Inject
    Service service;

    @WebSocketMessage
    public String echo(String message) {
        return service.message() + " " + message;
    }

}