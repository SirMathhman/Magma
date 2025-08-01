package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

record Application(Path source) {
	private static Optional<IOException> writeString(final Path target, final String output) {
		try {
			Files.writeString(target, output);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(e);
		}
	}

	Optional<IOException> run() {
		if (!Files.exists(this.source)) return Optional.empty();

		final var fileName = this.source.getFileName().toString();
		final var separator = fileName.indexOf('.');
		final var name = fileName.substring(0, separator);

		final var target = this.source.resolveSibling(name + ".c");

		return this.readString().match(input -> {
			if (!input.isEmpty()) return Optional.of(new IOException(""));
			return Application.writeString(target, input);
		}, Optional::of);
	}

	private Result<String, IOException> readString() {
		try {
			return new Ok<>(Files.readString(this.source));
		} catch (final IOException e) {
			return new Err<>(e);
		}
	}
}