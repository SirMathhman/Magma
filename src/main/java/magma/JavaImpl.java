package magma;

import magma.Lib.Err;
import magma.Lib.IOError;
import magma.Lib.Ok;
import magma.Lib.Optional;
import magma.Lib.Path;
import magma.Lib.Result;
import magma.Main.Actual;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;

public class JavaImpl {
	public record JIOError(IOException e) implements IOError {
		@Override
		public String display() {
			final StringWriter writer = new StringWriter();
			this.e.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}

	private record JavaPath(java.nio.file.Path path) implements Path {
		@Override
		public Result<String, IOError> readString() {
			try {
				return new Ok<String, IOError>(Files.readString(this.path));
			} catch (IOException e) {
				return new Err<String, IOError>(new JIOError(e));
			}
		}

		@Override
		public Optional<IOError> createDirectories() {
			try {
				Files.createDirectories(this.path);
				return Optional.empty();
			} catch (IOException e) {
				return Optional.of(new JIOError(e));
			}
		}

		@Override
		public Optional<IOError> writeString(String output) {
			try {
				Files.writeString(this.path, output);
				return Optional.empty();
			} catch (IOException e) {
				return Optional.of(new JIOError(e));
			}
		}

		@Override
		public Path getParent() {
			return new JavaPath(this.path.getParent());
		}

		@Override
		public boolean exists() {
			return Files.exists(this.path);
		}
	}

	static class Paths {
		@Actual
		public static Path get(String first, String... more) {
			return new JavaPath(java.nio.file.Paths.get(first, more));
		}
	}
}
