package magma;

import magma.error.CompileError;
import magma.node.MapNode;
import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

final class Main {
	private static final Pattern PATTERN = Pattern.compile("\\.java$");

	private Main() {}

	private static Result<String, CompileError> compileRoot(final String input) {
		final Result<Node, CompileError> lexResult = Lang.createJavaRootRule().lex(input); if (lexResult.isErr()) {
			System.err.println("Lexing error: " + lexResult.unwrapErr().getMessage());
			return new Err<>(lexResult.unwrapErr());
		}

		final Node transformedNode = Main.transform(lexResult.unwrap());

		final Result<String, CompileError> generateResult = Lang.createTSRootRule().generate(transformedNode);
		if (generateResult.isErr()) {
			System.err.println("Generation error: " + generateResult.unwrapErr().getMessage());
			return new Err<>(generateResult.unwrapErr());
		}

		return new magma.result.Ok<>(generateResult.unwrap());
	}

	private static Node transform(final Node root) {
		final List<Node> newChildren = root.findNodeList("children")
																			 .orElse(Collections.emptyList())
																			 .stream()
																			 .filter(node -> !node.is("package") || !node.is("import"))
																			 .toList();

		return new MapNode().withNodeList("children", newChildren);
	}

	public static void main(final String[] args) {
		final Path sourceRoot = Paths.get("src/java");
		final Path targetRoot = Paths.get("src/node");

		final var maybe = Main.collect(sourceRoot); if (maybe.isErr()) maybe.unwrapErr().printStackTrace();

		for (final Path sourcePath : maybe.unwrap()) {
			final Optional<IOException> result = Main.runWithFile(sourcePath, sourceRoot, targetRoot);
			if (result.isPresent()) {
				// If any file fails, print the error and stop processing
				result.get().printStackTrace(); break;
			}
		}
	}

	private static Result<List<Path>, IOException> collect(final Path sourceRoot) {
		try (final var paths = Files.walk(sourceRoot)) {
			return new Ok<>(paths.filter(path -> path.toString().endsWith(".java")).toList());
		} catch (final IOException e) {
			return new Err<>(e);
		}
	}

	private static Optional<IOException> runWithFile(final Path sourcePath,
																									 final Path sourceRoot,
																									 final Path targetRoot) {
		try {
			// Get relative path from source root
			final Path relativePath = sourceRoot.relativize(sourcePath);

			// Create corresponding target path with .ts extension
			final Path targetPath = targetRoot.resolve(Main.PATTERN.matcher(relativePath.toString()).replaceAll(".ts"));

			// Create parent directories if they don't exist
			Files.createDirectories(targetPath.getParent());

			// Read source file, compile it, and write to target
			final String content = Files.readString(sourcePath);
			final Result<String, CompileError> compileResult = Main.compileRoot(content);

			if (compileResult.isErr()) {
				return Optional.of(
						new IOException("Compilation failed for " + sourcePath + ": " + compileResult.unwrapErr().display()));
			}

			Files.writeString(targetPath, compileResult.unwrap());

			System.out.println("Compiled: " + sourcePath + " -> " + targetPath);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(e);
		}
	}
}
