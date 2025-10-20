package magma;

import magma.Lib.Actual;
import magma.Lib.ArrayList;
import magma.Lib.Err;
import magma.Lib.IOError;
import magma.Lib.ListCollector;
import magma.Lib.Ok;
import magma.Lib.Path;
import magma.Lib.Result;
import magma.Lib.Streams;
import magma.Options.None;
import magma.Options.Option;
import magma.Options.Some;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.stream.Stream;

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
		public Option<IOError> createDirectories() {
			try {
				Files.createDirectories(this.path);
				return new None<IOError>();
			} catch (IOException e) {
				return new Some<IOError>(new JIOError(e));
			}
		}

		@Override
		public Option<IOError> writeString(String output) {
			try {
				Files.writeString(this.path, output);
				return new None<IOError>();
			} catch (IOException e) {
				return new Some<IOError>(new JIOError(e));
			}
		}

		@Override
		public Path getParent() {
			return new JavaPath(this.path.getParent());
		}

		@Override
		public Result<ArrayList<Path>, IOError> walk() {
			try (final Stream<java.nio.file.Path> stream = Files.walk(this.path)) {
				final Path[] array = stream.map(JavaPath::new).toArray(Path[]::new);
				return new Ok<ArrayList<Path>, IOError>(Streams.fromRef(array).collect(new ListCollector<Path>()));
			} catch (IOException e) {
				return new Err<ArrayList<Path>, IOError>(new JIOError(e));
			}
		}

		@Override
		public String asString() {
			return this.path.toString();
		}

		@Override
		public Path relativize(Path path) {
			final java.nio.file.Path fold = this.unwrap(path);
			return new JavaPath(this.path.relativize(fold));
		}

		private java.nio.file.Path unwrap(Path path) {
			return path.stream().map(java.nio.file.Paths::get).fold(java.nio.file.Path::resolve).orElseGet(() -> java.nio.file.Paths.get(
					"."));
		}

		@Override
		public Path resolveByPath(Path path) {
			return new JavaPath(path.stream().foldWithInitial(this.path, java.nio.file.Path::resolve));
		}

		@Override
		public Lib.Stream<String> stream() {
			final int length = this.path.getNameCount();
			return Streams.fromLength(length).map(this.path::getName).map(java.nio.file.Path::toString);
		}

		@Override
		public Path getFileName() {
			return new JavaPath(this.path.getFileName());
		}

		@Override
		public Path resolveByString(String name) {
			return new JavaPath(this.path.resolve(name));
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
