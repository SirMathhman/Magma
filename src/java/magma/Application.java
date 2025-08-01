package magma;

import magma.error.CompileError;
import magma.error.Error;
import magma.error.ThrowableError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

record Application(Path source) {
	private static Optional<Error> writeString(final Path target, final String output) {
		try {
			Files.writeString(target, output);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(new ThrowableError(e));
		}
	}

	Optional<Error> run() {
		if (!Files.exists(this.source)) return Optional.empty();

		final var fileName = this.source.getFileName().toString();
		final var separator = fileName.indexOf('.');
		final var name = fileName.substring(0, separator);

		final var target = this.source.resolveSibling(name + ".c");

		return this.readString().match(input -> {
			if (!input.isEmpty()) return Optional.of(new CompileError("Invalid input", input));
			return Application.writeString(target, input);
		}, Optional::of);
	}

	private Result<String, Error> readString() {
		try {
			return new Ok<>(Files.readString(this.source));
		} catch (final IOException e) {
			return new Err<>(new ThrowableError(e));
		}
	}
}