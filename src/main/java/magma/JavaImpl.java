package magma;

import magma.Lib.Err;
import magma.Lib.IOError;
import magma.Lib.Ok;
import magma.Lib.Optional;
import magma.Lib.Result;
import magma.Main.Actual;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaImpl {
	private record JIOError(IOException e) implements IOError {
		@Override
		public String display() {
			final StringWriter writer = new StringWriter();
			this.e.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}

	@Actual
	static Optional<IOError> writeString(Path target, String output) {
		try {
			Files.writeString(target, output);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(new JIOError(e));
		}
	}

	@Actual
	static Optional<IOError> createDirectories(Path targetParent) {
		try {
			Files.createDirectories(targetParent);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(new JIOError(e));
		}
	}

	@Actual
	static Result<String, IOError> readString(Path source) {
		try {
			return new Ok<String, IOError>(Files.readString(source));
		} catch (IOException e) {
			return new Err<String, IOError>(new JIOError(e));
		}
	}
}
