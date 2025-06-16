package magma.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public record ThrowableError(IOException exception) implements Error {
    @Override
    public String display() {
        final var writer = new StringWriter();
        this.exception.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
