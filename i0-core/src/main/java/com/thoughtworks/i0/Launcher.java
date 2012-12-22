package com.thoughtworks.i0;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.facet.FacetEnabler;
import com.thoughtworks.i0.internal.logging.Logging;
import com.thoughtworks.i0.internal.server.jetty.Embedded;
import com.thoughtworks.i0.internal.util.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.*;
import static com.thoughtworks.i0.internal.util.TypePredicates.isApplicationModule;

public class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
    private final Map<String, ApplicationModule> modules;
    private Function<String, ApplicationModule> lookupModule = new Function<String, ApplicationModule>() {
        @Nullable
        @Override
        public ApplicationModule apply(@Nullable String input) {
            checkArgument(modules.containsKey(input), "Module " + input + " not found");
            return modules.get(input);
        }
    };

    public Launcher(Map<String, ApplicationModule> modules, Optional<Configuration> configuration) {
        this.modules = modules;
        if (configuration.isPresent())
            for (ApplicationModule module : modules.values()) module.setConfiguration(configuration.get());
    }

    public Embedded launch(Optional<String> name, boolean standalone) throws Exception {
        return launch(name.transform(lookupModule).or(getDefaultModule()), standalone);
    }

    public static Embedded launch(ApplicationModule module, boolean standalone) throws Exception {
        Configuration configuration = module.getConfiguration();
        Logging.configure(configuration.getLogging());

        for(Map.Entry<Annotation, FacetEnabler> enabler : module.getEnablers().entrySet())
            enabler.getValue().performPreLaunchTasks(enabler.getKey(), configuration);

        Embedded server = new Embedded(configuration.getHttp());
        server.addServletContext(module.name(), true, module);
        server.start(standalone);
        return server;
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

    private ApplicationModule getDefaultModule() {
        checkArgument(modules.size() == 1, "Can not decide which module to launch.");
        return getFirst(modules.values(), null);
    }

    public static void main(String... arguments) throws Exception {
        checkArgument(arguments.length <= 1, "Too many arguments");
        Optional<String> module = arguments.length == 0 ? Optional.<String>absent() : Optional.of(arguments[1]);
        new Launcher(scanApplicationModules(), readConfiguration()).launch(module, true);
    }
}
