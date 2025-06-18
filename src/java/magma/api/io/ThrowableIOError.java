package magma.api.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public record ThrowableIOError(IOException throwable) implements IOError {
    @Override
    public String display() {
        final var writer = new StringWriter();
        this.throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
