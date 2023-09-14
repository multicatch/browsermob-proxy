package io.github.multicatch.bmp.mitm.exception;

import io.github.multicatch.bmp.mitm.TrustSource;

/**
 * Indicates that an error occurred while attempting to create or populate a {@link TrustSource}.
 */
public class TrustSourceException extends RuntimeException {
    public TrustSourceException() {
    }

    public TrustSourceException(String message) {
        super(message);
    }

    public TrustSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrustSourceException(Throwable cause) {
        super(cause);
    }
}
