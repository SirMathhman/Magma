package magma.error;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public record ThrowableError(IOException e) implements Error {
	@Override
	public String display() {
		final var writer = new StringWriter();
		this.e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}
}
