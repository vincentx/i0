package com.thoughtworks.i0.config.util;

import org.junit.Test;

import static com.thoughtworks.i0.config.util.Size.Unit.*;
import static com.thoughtworks.i0.config.util.Size.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SizeTest {

    @Test
    public void should_parse_kilobyte_size_from_string() {
        assertThat(valueOf("1k"), is(new Size(1L, KB)));
        assertThat(valueOf("1 k"), is(new Size(1L, KB)));
        assertThat(valueOf("1 KB"), is(new Size(1L, KB)));
        assertThat(valueOf("1 kilobyte"), is(new Size(1L, KB)));
        assertThat(valueOf("1 kilobytes"), is(new Size(1L, KB)));
    }

    @Test
    public void should_parse_megabyte_size_from_string() {
        assertThat(valueOf("1m"), is(new Size(1L, MB)));
        assertThat(valueOf("1 m"), is(new Size(1L, MB)));
        assertThat(valueOf("1 MB"), is(new Size(1L, MB)));
        assertThat(valueOf("1 megabyte"), is(new Size(1L, MB)));
        assertThat(valueOf("1 megabytes"), is(new Size(1L, MB)));
    }

    @Test
    public void should_parse_gigabyte_size_from_string() {
        assertThat(valueOf("1g"), is(new Size(1L, GB)));
        assertThat(valueOf("1 g"), is(new Size(1L, GB)));
        assertThat(valueOf("1 GB"), is(new Size(1L, GB)));
        assertThat(valueOf("1 gigabyte"), is(new Size(1L, GB)));
        assertThat(valueOf("1 gigabytes"), is(new Size(1L, GB)));
    }

    @Test
    public void should_convert_size_to_value_in_bits() {
        assertThat(new Size(1L, KB).value(), is(8L * 1024));
        assertThat(new Size(1L, MB).value(), is(8L * 1024 * 1024));
        assertThat(new Size(1L, GB).value(), is(8L * 1024 * 1024 * 1024));
    }
}
