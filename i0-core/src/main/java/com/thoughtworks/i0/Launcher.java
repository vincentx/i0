package com.thoughtworks.i0;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.internal.logging.Logging;
import com.thoughtworks.i0.internal.migration.Migration;
import com.thoughtworks.i0.internal.server.jetty.Embedded;
import com.thoughtworks.i0.internal.util.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static com.thoughtworks.i0.internal.util.TypePredicates.isApplicationModule;

public class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
    private final Map<String, ApplicationModule> modules;
    private final Optional<Configuration> configuration;

    public Launcher(Map<String, ApplicationModule> modules, Optional<Configuration> configuration) {
        this.modules = modules;
        this.configuration = configuration;
        if (configuration.isPresent())
            for (ApplicationModule module : modules.values()) module.setConfiguration(configuration.get());
    }

    public Embedded launch(boolean standalone, final String... moduleNames) throws Exception {
        String[] launchSet = toArray(moduleNames.length == 0 ? modules.keySet() : copyOf(moduleNames), String.class);
        if (!configuration.isPresent() && launchSet.length == 1)
            return launchWithDefaultConfiguration(standalone, launchSet[0]);
        return createServer(configuration.get(), launchSet, standalone);
    }

    private Embedded launchWithDefaultConfiguration(boolean standalone, String name) throws Exception {
        Preconditions.checkArgument(modules.containsKey(name), "Module " + name + " not found");
        return createServer(modules.get(name).getConfiguration(), new String[]{name}, standalone);
    }

    private Embedded createServer(Configuration configuration, String[] moduleNames, boolean standalone) throws Exception {
        Logging.configure(configuration.getLogging());
        if (configuration.getDatabase().isPresent()) Migration.migrate(configuration.getDatabase().get());
        Embedded server = new Embedded(configuration.getHttp());

        for (String name : moduleNames) {
            Preconditions.checkArgument(modules.containsKey(name), "Module " + name + " not found");
            server.addServletContext(name, true, modules.get(name));
        }
        server.start(standalone);
        return server;
    }

    public static Embedded launch(ApplicationModule module, boolean standalone) throws Exception {
        return launch(module, null, standalone);
    }

    public static Embedded launch(ApplicationModule module, Configuration configuration, boolean standalone) throws Exception {
        return new Launcher(modules(module), fromNullable(configuration)).launch(standalone);
    }

    private static Map<String, ApplicationModule> modules(ApplicationModule... modules) {
        ImmutableMap.Builder<String, ApplicationModule> builder = ImmutableMap.builder();
        for (ApplicationModule module : modules)
            builder.put(module.name(), module);
        return builder.build();
    }

    private static Optional<Configuration> readConfiguration() throws IOException {
        File configFile = new File("./config.yml");
        if (configFile.exists()) {
            if (logger.isInfoEnabled()) logger.info("Reading configuration from file: " + configFile.getAbsolutePath());
            return Optional.of(Configuration.read(new FileInputStream(configFile)));
        }
        if (logger.isInfoEnabled())
            logger.info("No configuration found, will use default configuration for each module");
        return Optional.absent();
    }

    private static Map<String, ApplicationModule> scanApplicationModules() throws Exception {
        ClassScanner scanner = new ClassScanner(Launcher.class.getProtectionDomain().getCodeSource());
        if (logger.isInfoEnabled()) logger.info("Scanning for application module classes.");
        Set<Class<?>> found = scanner.findBy(isApplicationModule);


        if (logger.isInfoEnabled())
            logger.info(found.isEmpty() ? "No application module classes found" : ("Application module classes found:\n  {}"),
                    on("\n  ").join(found));

        return modules(toArray(transform(found, CREATE_INSTANCE), ApplicationModule.class));
    }

    private static final Function<Class<?>, ApplicationModule> CREATE_INSTANCE = new Function<Class<?>, ApplicationModule>() {
        @Nullable
        @Override
        public ApplicationModule apply(@Nullable Class<?> input) {
            try {
                return (ApplicationModule) input.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Throwables.propagate(e);
            }
            return null;
        }
    };

    public static void main(String... arguments) throws Exception {
        new Launcher(scanApplicationModules(), readConfiguration()).launch(true, arguments);
    }

}
