package com.thoughtworks.i0.container.grizzly.websockets;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.config.util.LogLevel;
import com.thoughtworks.i0.container.grizzly.EmbeddedGrizzly;
import com.thoughtworks.i0.container.grizzly.WebSocket;
import com.thoughtworks.i0.core.Application;
import com.thoughtworks.i0.core.ApplicationModule;

import static com.thoughtworks.i0.config.Configuration.config;

@EmbeddedGrizzly
@WebSocket
@Application("websocket")
public class WebSocektApplication extends ApplicationModule<Configuration> {
    @Override
    protected Configuration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
        return config().http().port(8051).end().logging().level(LogLevel.INFO).console().end().end().build();
    }
}
