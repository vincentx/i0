package com.thoughtworks.i0.jetty.internal;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.inject.Injector;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.net.websocket.Session;
import javax.net.websocket.annotations.WebSocketClose;
import javax.net.websocket.annotations.WebSocketMessage;
import javax.net.websocket.annotations.WebSocketOpen;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.tryFind;

public class WebSocketEndPointServlet extends HttpServlet {
    @Inject
    private Injector injector;


    private WebSocketServletFactory factory;

    @Override
    public void destroy() {
        factory.cleanup();
    }

    @Override
    public void init() throws ServletException {
        try {
            WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);

            for (String value : fromNullable(getInitParameter("bufferSize")).asSet())
                policy.setBufferSize(Integer.parseInt(value));

            for (String value : fromNullable(getInitParameter("maxIdleTime")).asSet())
                policy.setIdleTimeout(Integer.parseInt(value));

            for (String value : fromNullable(getInitParameter("maxTextMessageSize")).asSet())
                policy.setMaxTextMessageSize(Integer.parseInt(value));

            for (String value : fromNullable(getInitParameter("maxBinaryMessageSize")).asSet())
                policy.setMaxBinaryMessageSize(Integer.parseInt(value));

            WebSocketServletFactory baseFactory = new WebSocketFactory();

            factory = baseFactory.createFactory(policy);

            for (String value : of(getInitParameter("socketClass")).asSet())
                factory.register(Class.forName(value));

            factory.init();
        } catch (Exception x) {
            throw new ServletException(x);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (factory.isUpgradeRequest(request, response))
            if (factory.acceptWebSocket(request, response) || response.isCommitted())
                return;
        super.service(request, response);
    }

    public class WebSocketFactory extends WebSocketServerFactory {

        public WebSocketFactory() {
        }

        public WebSocketFactory(WebSocketPolicy policy) {
            super(policy);
        }

        @Override
        public WebSocketServletFactory createFactory(WebSocketPolicy policy) {
            return new WebSocketFactory(policy);
        }

        @Override
        public Object createWebSocket(UpgradeRequest req, UpgradeResponse resp) {
            Object webSocket = super.createWebSocket(req, resp);
            injector.injectMembers(webSocket);
            return new EndPointAdapter(webSocket);
        }
    }

    private class SessionMethod {
        private Optional<Method> method;

        public SessionMethod(Class<? extends Annotation> annotation, Class<?> aClass) {
            method = tryFind(copyOf(aClass.getMethods()), annotateWith(annotation));
        }

        public void invoke(Object target, Session<?> session) throws Exception {
            for (Method open : method.asSet()) {
                List<Object> parameters = new ArrayList<>();
                for (Class<?> parameter : open.getParameterTypes())
                    if (parameter.equals(Session.class)) parameters.add(session);
                open.invoke(target, parameters.toArray());
            }
        }
    }

    private Predicate<Method> annotateWith(final Class<? extends Annotation> annotation) {
        return new Predicate<Method>() {

            @Override
            public boolean apply(@Nullable Method input) {
                return input.isAnnotationPresent(annotation);
            }
        };
    }

    private class SessionAndMessageMethod {
        private Optional<Method> method;

        public SessionAndMessageMethod(Class<?> aClass) {
            method = tryFind(copyOf(aClass.getMethods()), annotateWith(WebSocketMessage.class));
        }


        public void invoke(Object target, Session<?> session, String message) throws Exception {
            for (Method open : method.asSet()) {
                List<Object> parameters = new ArrayList<>();
                for (Class<?> parameter : open.getParameterTypes())
                    if (parameter.equals(Session.class)) parameters.add(session);
                    else if (parameter.equals(String.class)) parameters.add(message);
                Object result = open.invoke(target, parameters.toArray());
                if (result != null) session.getRemote().sendString(result.toString());
            }
        }
    }


    public class EndPointAdapter extends WebSocketAdapter {
        private Object socket;

        private SessionMethod webSocketOpen;
        private SessionMethod webSocketClose;
        private SessionAndMessageMethod webSocketMessage;

        private Session<?> session;

        public EndPointAdapter(Object socket) {
            this.socket = socket;
            webSocketOpen = new SessionMethod(WebSocketOpen.class, socket.getClass());
            webSocketClose = new SessionMethod(WebSocketClose.class, socket.getClass());
            webSocketMessage = new SessionAndMessageMethod(socket.getClass());
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            try {
                webSocketClose.invoke(socket, session);
            } catch (Exception e) {
                propagate(e);
            }
        }

        @Override
        public void onWebSocketText(String message) {
            try {
                webSocketMessage.invoke(socket, session, message);
            } catch (Exception e) {
                propagate(e);
            }
        }

        @Override
        public void onWebSocketConnect(WebSocketConnection connection) {
            this.session = (Session<?>) connection;
            try {
                webSocketOpen.invoke(socket, session);
            } catch (Exception e) {
                propagate(e);
            }
        }
    }
}
