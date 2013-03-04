package com.thoughtworks.i0.core.internal.servlet;

import com.google.common.hash.Hashing;
import com.thoughtworks.i0.core.internal.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.io.Resources.getResource;
import static com.google.common.io.Resources.toByteArray;
import static com.google.common.net.HttpHeaders.IF_MODIFIED_SINCE;
import static com.google.common.net.HttpHeaders.IF_NONE_MATCH;
import static java.lang.System.currentTimeMillis;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED;

public class AssetServlet extends HttpServlet {
    public static final Logger logger = LoggerFactory.getLogger(AssetServlet.class);

    private String resourcePath;
    private Map<String, String> mimeExtensions = new HashMap();
    public static final String DEFAULT_MIME_TYPE = "application/octet-stream";

    public AssetServlet(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            URL resource = getResource(resourcePath + request.getPathInfo());
            byte[] content = toByteArray(resource);
            long lastModified = getLastModified(resource);
            String etag = Hashing.md5().hashBytes(content).toString();

            if (etag.equals(request.getHeader(IF_NONE_MATCH)) || (request.getDateHeader(IF_MODIFIED_SINCE) >= lastModified)) {
                response.sendError(SC_NOT_MODIFIED);
                return;
            }
            for (String mimeType : fromNullable(getMimeType(request.getPathInfo())).asSet())
                response.setContentType(mimeType);
            response.setHeader(IF_NONE_MATCH, etag);
            response.setDateHeader(IF_MODIFIED_SINCE, lastModified);
            ServletOutputStream outputStream = response.getOutputStream();
            try {
                outputStream.write(content);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        } catch (RuntimeException e) {
            logger.warn(e.getMessage(), e);
            response.sendError(SC_NOT_FOUND);
        }
    }

    private String getMimeType(String file) {
        if (file == null) {
            return DEFAULT_MIME_TYPE;
        }
        int period = file.lastIndexOf(".");
        if (period < 0) {
            return DEFAULT_MIME_TYPE;
        }
        String mimeType = getServletContext().getMimeType(file);

        String extension = file.substring(period + 1);
        if(this.mimeExtensions.containsKey(extension) && mimeTypeNotFound(mimeType)){
            return this.mimeExtensions.get(extension);
        }
        return mimeType;
    }

    private boolean mimeTypeNotFound(String mimeType) {
        return mimeType == null || "text/plain".equalsIgnoreCase(mimeType);
    }

    private long getLastModified(URL resource) {
        long lastModified = getLastModifiedFromURL(resource);
        return ((lastModified < 1 ? currentTimeMillis() : lastModified) / 1000) * 1000;
    }

    private long getLastModifiedFromURL(URL resource) {
        String protocol = resource.getProtocol();
        URLConnection connection = null;
        try {
            if ("jar".equals(protocol)) {
                return ((JarURLConnection) resource.openConnection()).getJarEntry().getTime();
            } else {
                connection = resource.openConnection();
                return connection.getLastModified();
            }
        } catch (IOException e) {
            return 0;
        } finally {
            if (connection != null) try {
                connection.getInputStream().close();
            } catch (IOException e) {
            }
        }
    }

    public AssetServlet setMimeExtensions(Map<String, String> mimeExtensions) {
        this.mimeExtensions = mimeExtensions;
        return this;
    }
}