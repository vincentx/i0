package com.thoughtworks.i0.container.grizzly;

import com.thoughtworks.i0.container.grizzly.websockets.WebSocektApplication;
import com.thoughtworks.i0.core.Launcher;
import com.thoughtworks.i0.core.ServletContainer;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WebSocketConnection;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.client.WebSocketClientFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WebSocketTest {
    private ServletContainer server;
    private WebSocketClientFactory factory;

    @Before
    public void before() throws Exception {
        server = Launcher.launch(new WebSocektApplication(), false);
        factory = new WebSocketClientFactory();
        factory.start();
    }

    @After
    public void after() throws Exception {
        server.stop();
        factory.stop();
    }

    @Test
    public void should_launch_embedded_grizzly_with_websocket_support() throws Exception {
        TestSocket socket = new TestSocket();
        WebSocketClient client = factory.newWebSocketClient(socket);
        client.connect(new URI("ws://localhost:8051/websocket/echo1"));

        assertThat(socket.connected(), is(true));

        socket.send("message");

        assertThat(socket.received(), is("message"));
    }

    @Test
    public void should_launch_embedded_grizzly_with_injected_websocket() throws Exception {
        TestSocket socket = new TestSocket();
        WebSocketClient client = factory.newWebSocketClient(socket);
        client.connect(new URI("ws://localhost:8051/websocket/echo2"));

        assertThat(socket.connected(), is(true));

        socket.send("message");

        assertThat(socket.received(), is("injected message"));
    }

    class TestSocket extends WebSocketAdapter {
        private String received;
        private WebSocketConnection connection;
        public CountDownLatch open = new CountDownLatch(1);
        public CountDownLatch message = new CountDownLatch(1);

        @Override
        public void onWebSocketConnect(WebSocketConnection connection) {
            this.connection = connection;
            open.countDown();
        }

        public boolean connected() throws Exception {
            return open.await(500, TimeUnit.MILLISECONDS);
        }

        public String received() throws Exception {
            message.await(500, TimeUnit.MILLISECONDS);
            return received;
        }

        public void send(String message) throws IOException {
            connection.write(message);
        }

        @Override
        public void onWebSocketText(String message) {
            this.received = message;
            this.message.countDown();
        }

    }
}
