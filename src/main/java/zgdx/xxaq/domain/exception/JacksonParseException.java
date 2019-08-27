package zgdx.xxaq.domain.exception;

public class JacksonParseException extends RuntimeException{

    public JacksonParseException(Throwable cause) {
        super(cause);
    }

    public JacksonParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
