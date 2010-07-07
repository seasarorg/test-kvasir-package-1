package org.seasar.kvasir.cms.publish;

public class PublishRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PublishRuntimeException() {
    }

    public PublishRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PublishRuntimeException(String message) {
        super(message);
    }

    public PublishRuntimeException(Throwable cause) {
        super(cause);
    }
}
