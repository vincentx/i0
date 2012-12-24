package com.thoughtworks.i0.config.util;

import org.junit.Test;

import static com.thoughtworks.i0.config.util.Duration.Unit.*;
import static com.thoughtworks.i0.config.util.Duration.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DurationTest {

    @Test
    public void should_parse_millisecond_duration_from_string() {
        assertThat(valueOf("1ms"), is(new Duration(1L, MILLISECONDS)));
        assertThat(valueOf("1 ms"), is(new Duration(1L, MILLISECONDS)));
        assertThat(valueOf("1 millisecond"), is(new Duration(1L, MILLISECONDS)));
        assertThat(valueOf("1 milliseconds"), is(new Duration(1L, MILLISECONDS)));
    }

    @Test
    public void should_parse_second_duration_from_string() {
        assertThat(valueOf("1s"), is(new Duration(1L, SECONDS)));
        assertThat(valueOf("1 s"), is(new Duration(1L, SECONDS)));
        assertThat(valueOf("1 second"), is(new Duration(1L, SECONDS)));
        assertThat(valueOf("1 seconds"), is(new Duration(1L, SECONDS)));
    }

    @Test
    public void should_parse_minute_duration_from_string() {
        assertThat(valueOf("1m"), is(new Duration(1L, MINUTES)));
        assertThat(valueOf("1 m"), is(new Duration(1L, MINUTES)));
        assertThat(valueOf("1 minute"), is(new Duration(1L, MINUTES)));
        assertThat(valueOf("1 minutes"), is(new Duration(1L, MINUTES)));
    }

    @Test
    public void should_parse_hour_duration_from_string() {
        assertThat(valueOf("1h"), is(new Duration(1L, HOURS)));
        assertThat(valueOf("1 h"), is(new Duration(1L, HOURS)));
        assertThat(valueOf("1 hour"), is(new Duration(1L, HOURS)));
        assertThat(valueOf("1 hours"), is(new Duration(1L, HOURS)));
    }

    @Test
    public void should_parse_day_duration_from_string() {
        assertThat(valueOf("1d"), is(new Duration(1L, DAYS)));
        assertThat(valueOf("1 d"), is(new Duration(1L, DAYS)));
        assertThat(valueOf("1 day"), is(new Duration(1L, DAYS)));
        assertThat(valueOf("1 days"), is(new Duration(1L, DAYS)));
    }

    @Test
    public void should_convert_duration_to_value_in_milliseconds() {
        assertThat(new Duration(1L, MILLISECONDS).value(), is(1L));
        assertThat(new Duration(1L, SECONDS).value(), is(1000L));
        assertThat(new Duration(1L, MINUTES).value(), is(1000L * 60));
        assertThat(new Duration(1L, HOURS).value(), is(1000L * 60 * 60));
        assertThat(new Duration(1L, DAYS).value(), is(1000L * 60 * 60 * 24));
    }
}
