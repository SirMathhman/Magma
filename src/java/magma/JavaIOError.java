package magma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public record JavaIOError(IOException error) implements IOError {
    @Override
    public String display() {
        final var writer = new StringWriter();
        this.error.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
