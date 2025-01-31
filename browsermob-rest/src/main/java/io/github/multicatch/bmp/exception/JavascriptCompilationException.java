package io.github.multicatch.bmp.exception;

import com.google.sitebricks.headless.Request;
import io.github.multicatch.bmp.proxy.bricks.ProxyResource;
import io.github.multicatch.bmp.filters.JavascriptRequestResponseFilter;

/**
 * Indicates that an error occurred when compiling javascript in {@link JavascriptRequestResponseFilter},
 * for use by {@link ProxyResource#addRequestFilter(int, Request)}
 * or {@link ProxyResource#addResponseFilter(int, Request)}.
 */
public class JavascriptCompilationException extends RuntimeException {
    public JavascriptCompilationException() {
        super();
    }

    public JavascriptCompilationException(String message) {
        super(message);
    }

    public JavascriptCompilationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JavascriptCompilationException(Throwable cause) {
        super(cause);
    }
}
