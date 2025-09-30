package magma.compile.error;

import java.io.PrintWriter;
import java.io.StringWriter;

public record ThrowableError(Throwable e) implements Error {
	@Override
	public String display() {
		final StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}
}
