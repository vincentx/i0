package com.thoughtworks.i0.core.internal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class IOUtils {
    public static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

    public static void closeQuietly(ServletOutputStream outputStream) throws IOException {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (Exception e) {
            LOGGER.info("Failed to close output stream: " + e.getMessage(), e);
        }
    }
}
