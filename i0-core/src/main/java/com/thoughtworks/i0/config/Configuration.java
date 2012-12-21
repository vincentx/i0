package com.thoughtworks.i0.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.google.common.base.Optional;
import com.thoughtworks.i0.config.builder.ConfigurationBuilder;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Optional.absent;

@XmlType
public class Configuration {

    public static Configuration read(InputStream configStream) throws IOException {
        return getMapper().readValue(configStream, Configuration.class);
    }

    public static String dump(Configuration configuration) throws JsonProcessingException {
        return getMapper().writeValueAsString(configuration);
    }

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(new GuavaModule());
        mapper.setAnnotationIntrospector(new AnnotationIntrospectorPair(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()), new JacksonAnnotationIntrospector()));
        mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy());
        return mapper;
    }

    @NotNull
    private HttpConfiguration http;

    @NotNull
    private LoggingConfiguration logging;

    @NotNull
    private Optional<DatabaseConfiguration> database = absent();

    private Configuration() {
        ConfigurationBuilder config = ConfigurationBuilder.config();
        http = config.http().build();
        logging = config.logging().build();
    }

    public Configuration(HttpConfiguration http, LoggingConfiguration logging, Optional<DatabaseConfiguration> database) {
        this.http = http;
        this.logging = logging;
        this.database = database;
    }

    @XmlElement
    public HttpConfiguration getHttp() {
        return http;
    }

    @XmlElement
    public LoggingConfiguration getLogging() {
        return logging;
    }

    @XmlElement
    public Optional<DatabaseConfiguration> getDatabase() {
        return database;
    }

}
