package com.thoughtworks.i0;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.server.JettyServer;
import com.thoughtworks.i0.util.ClassScanner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public class Launcher {

    private final Map<String, ApplicationModule> modules;
    private final Configuration configuration;

    public Launcher(Configuration configuration) throws Exception {
        this(scanApplicationModules(), configuration);
    }

    private static Map<String, ApplicationModule> scanApplicationModules() throws Exception {
        Map<String, ApplicationModule> modules = new HashMap<>();

        ClassScanner scanner = null;
        String packages = System.getProperty("module_packages");
        if(packages != null){
            scanner = new ClassScanner(packages.split(","));
        }else{
            scanner = new ClassScanner(Launcher.class.getProtectionDomain().getCodeSource());
        }

        Set<Class<?>> applicationModules = scanner.findBySuperClass(ApplicationModule.class);

        for (Class<?> module : applicationModules)
            if (ApplicationModule.isApplicationModule(module)) {
                ApplicationModule applicationModule = ApplicationModule.initialize(module);
                modules.put(applicationModule.getApplication().name(), applicationModule);
            }
        return modules;
    }

    public Launcher(Map<String, ApplicationModule> modules, Configuration configuration) {
        this.modules = modules;
        this.configuration = configuration;
        for (ApplicationModule module : modules.values()) module.setConfiguration(configuration);
    }

    public JettyServer launch(boolean standalone, final String module) throws Exception {
        checkArgument(modules.containsKey(module), "Module '" + module + "' not found");
        JettyServer server = new JettyServer(module, configuration.getHttp().getPort(), modules.get(module));
        server.start(standalone);
        return server;
    }

    public static void main(String... arguments) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Configuration configuration = mapper.readValue(new File("./config.yml"), Configuration.class);

        Launcher launcher = new Launcher(configuration);
        if (arguments.length > 0) launcher.launch(true, arguments[0]);
        else if (launcher.modules.size() == 1) {
            launcher.launch(true, launcher.modules.keySet().iterator().next());
        }

    }
}
