package com.thoughtworks.i0.gradle

import org.junit.Test

import static com.thoughtworks.i0.gradle.Configuration.*
import static org.hamcrest.CoreMatchers.is
import static org.junit.Assert.assertThat

class ConfigurationTest {

    @Test
    public void should_use_default_value() {
        def configuration = configurable(
                java: configurable(version: "1.7", guava: "13.0.1")
        )

        assertThat(configuration.java.version, is("1.7"))
        assertThat(configuration.java.guava, is("13.0.1"))
    }

    @Test
    public void should_configure_item_value() {
        def configuration = configurable(
                java: configurable(version: "1.7", guava: "13.0.1")
        )

        configuration.java {
            version "1.6"
            guava "12.0.0"
        }

        assertThat(configuration.java.version, is("1.6"))
        assertThat(configuration.java.guava, is("12.0.0"))
    }

    @Test
    public void nullable_item_should_not_be_present_if_not_config() {
        def configuration = configurable(
                java: configurable(version: "1.7", guava: "13.0.1"),
                scala: nullable(version: "2.9")
        )

        assertThat(configuration.scala.present, is(false))
    }

    @Test
    public void nullable_item_should_be_present_if_config() {
        def configuration = configurable(
                java: configurable(version: "1.7", guava: "13.0.1"),
                scala: nullable(version: "2.9")
        )

        configuration.scala { version "2.8" }

        assertThat(configuration.scala.present, is(true))
        assertThat(configuration.scala.version, is("2.8"))
    }

    @Test
    public void should_return_all_configured_item() {
        def configuration = configurable(
                java: nullable(version: "1.7", guava: "13.0.1"),
                scala: nullable(version: "2.9")
        )

        configuration.scala { version "2.8" }

        assertThat(configuration.__configured(), is(["scala"].toSet()))
    }
}
