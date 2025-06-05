package magma;

import java.io.IOException;

/** Exception representing parsing errors. */
public class ParseException extends IOException {
    public ParseException(String message) {
        super(message);
    }
}
