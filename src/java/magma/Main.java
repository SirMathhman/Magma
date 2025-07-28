package magma;

import magma.error.ApplicationError;
import magma.error.CompileError;
import magma.error.Error;
import magma.error.ThrowableError;
import magma.node.MapNode;
import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Main {
	private static final Path TARGET_DIRECTORY = Paths.get(".", "src", "node");

	private Main() {}

	public static void main(final String[] args) {
		final var sourceDirectory = Paths.get(".", "src", "java");
		Main.walk(sourceDirectory)
				.<Error>mapErr(ThrowableError::new)
				.match(files -> Main.runWithFiles(files, sourceDirectory), Optional::of)
				.ifPresent(error -> System.err.println(error.display()));
	}

	private static Optional<Error> runWithFiles(final Collection<Path> files, final Path sourceDirectory) {
		final var sources = files.stream()
														 .filter(Files::isRegularFile)
														 .filter(path -> path.toString().endsWith(".java"))
														 .collect(Collectors.toSet());

		return Main.runWithSources(sources, sourceDirectory)
							 .match(targets -> Main.pruneTargets(targets).map(ThrowableError::new), Optional::of);
	}

	private static Optional<IOException> pruneTargets(final Collection<Path> targets) {
		return Main.walk(Main.TARGET_DIRECTORY).match(files -> {
			final var extra = files.stream()
														 .filter(Files::isRegularFile)
														 .filter(target -> !targets.contains(target))
														 .collect(Collectors.toSet());

			return Main.deleteAllIfExist(extra);
		}, Optional::of);
	}

	private static Result<Set<Path>, IOException> walk(final Path directory) {
		try (final Stream<Path> stream = Files.walk(directory)) {
			return new Ok<>(stream.collect(Collectors.toSet()));
		} catch (final IOException e) {
			return new Err<>(e);
		}
	}

	private static Optional<IOException> deleteAllIfExist(final Collection<Path> paths) {
		return paths.stream().map(Main::deleteIfExists).flatMap(Optional::stream).findFirst();
	}

	private static Optional<IOException> deleteIfExists(final Path path) {
		try {
			Files.deleteIfExists(path);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(e);
		}
	}

	private static Result<List<Path>, Error> runWithSources(final Collection<Path> sources, final Path sourceDirectory) {
		return sources.stream()
									.<Result<List<Path>, Error>>reduce(new Ok<>(new ArrayList<>()),
																										 (maybeCurrent, path) -> Main.getListErrorResult(sourceDirectory,
																																																		 maybeCurrent,
																																																		 path),
																										 (_, next) -> next);
	}

	private static Result<List<Path>, Error> getListErrorResult(final Path sourceDirectory,
																															final Result<List<Path>, Error> maybeCurrent,
																															final Path path) {
		return maybeCurrent.flatMapValue(current -> Main.runWithSource(path, sourceDirectory).mapValue(result -> {
			current.add(result);
			return current;
		}));
	}

	private static Result<Path, Error> runWithSource(final Path source, final Path sourceDirectory) {
		final var relativeParent = sourceDirectory.relativize(source.getParent());
		final var fileName = source.getFileName().toString();
		final var separator = fileName.lastIndexOf('.');
		final var name = fileName.substring(0, separator);

		final var targetParent = Main.TARGET_DIRECTORY.resolve(relativeParent);
		final var maybeError = Main.ensureDirectories(targetParent);
		if (maybeError.isPresent()) return new Err<>(new ThrowableError(maybeError.get()));

		final var target = targetParent.resolve(name + ".ts");

		return Main.readString(source)
							 .<Error>mapErr(ThrowableError::new)
							 .flatMapValue(input -> Main.compileAndWriteTarget(input, target));
	}

	private static Result<Path, Error> compileAndWriteTarget(final String input, final Path target) {
		return Main.compile(input)
							 .<Error>mapErr(ApplicationError::new)
							 .flatMapValue(output -> Main.writeTarget(target, output));
	}

	private static Result<Path, Error> writeTarget(final Path target, final CharSequence output) {
		final var maybeError = Main.writeString(target, output);
		if (maybeError.isPresent()) return new Err<>(new ApplicationError(new ThrowableError(maybeError.get())));
		return new Ok<>(target);
	}

	private static Optional<IOException> writeString(final Path target, final CharSequence output) {
		try {
			Files.writeString(target, output);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(e);
		}
	}

	private static Result<String, IOException> readString(final Path source) {
		try {
			return new Ok<>(Files.readString(source));
		} catch (final IOException e) {
			return new Err<>(e);
		}
	}

	private static Optional<IOException> ensureDirectories(final Path targetParent) {
		if (!Files.exists(targetParent)) return Main.createDirectories(targetParent);
		return Optional.empty();
	}

	private static Optional<IOException> createDirectories(final Path targetParent) {
		try {
			Files.createDirectories(targetParent);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(e);
		}
	}

	private static Result<String, CompileError> compile(final String input) {
		return Lang.createJavaRootRule()
							 .lex(input)
							 .mapValue(Main::modify)
							 .flatMapValue(node -> Lang.createTSRootRule().generate(node));
	}

	private static Node modify(final Node root) {
		final var newChildren = root.findNodeList("children")
																.orElse(Collections.emptyList())
																.stream()
																.filter(node -> !node.is("package"))
																.toList();

		return new MapNode().withNodeList("children", newChildren);
	}
}
