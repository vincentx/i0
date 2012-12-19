package com.thoughtworks.i0.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.thoughtworks.i0.config.builder.HttpConfigurationBuilder;
import com.thoughtworks.i0.config.builder.LoggingConfigurationBuilder;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.IOException;
import java.io.InputStream;

@XmlType
public class Configuration {

    public static <T extends Configuration> T read(InputStream configStream, Class<T> configClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(new GuavaModule());
        mapper.setAnnotationIntrospector(new AnnotationIntrospectorPair(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()), new JacksonAnnotationIntrospector()));
        mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy());
        return mapper.readValue(configStream, configClass);
    }

    @NotNull
    private HttpConfiguration http = new HttpConfigurationBuilder().build();

    @NotNull
    private LoggingConfiguration logging = new LoggingConfigurationBuilder().build();

    @XmlElement
    public HttpConfiguration getHttp() {
        return http;
    }

    @XmlElement
    public LoggingConfiguration getLogging() {
        return logging;
    }


}
