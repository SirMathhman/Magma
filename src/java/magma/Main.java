package magma;

import magma.node.MapNode;
import magma.node.Node;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Main {
	private Main() {}

	private static String compileRoot(final String input) {
		final Result<Node, String> lexResult = Lang.createJavaRootRule().lex(input); if (lexResult.isErr()) {
			System.err.println("Lexing error: " + lexResult.unwrapErr()); return "";
		}

		final Node transformedNode = Main.transform(lexResult.unwrap());

		final Result<String, String> generateResult = Lang.createTSRootRule().generate(transformedNode);
		if (generateResult.isErr()) {
			System.err.println("Generation error: " + generateResult.unwrapErr()); return "";
		}

		return generateResult.unwrap();
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

		try (final var paths = Files.walk(sourceRoot)) {
			paths.filter(path -> path.toString().endsWith(".java"))
					 .map(sourcePath -> Main.runWithFile(sourcePath, sourceRoot, targetRoot))
					 .flatMap(Optional::stream)
					 .findFirst()
					 .ifPresent(Throwable::printStackTrace);
		} catch (final IOException e) {
			System.err.println("Error walking directory: " + e.getMessage());
		}
	}

	private static Optional<IOException> runWithFile(final Path sourcePath,
																									 final Path sourceRoot,
																									 final Path targetRoot) {
		try {
			// Get relative path from source root
			final Path relativePath = sourceRoot.relativize(sourcePath);

			// Create corresponding target path with .ts extension
			final Path targetPath = targetRoot.resolve(relativePath.toString().replaceAll("\\.java$", ".ts"));

			// Create parent directories if they don't exist
			Files.createDirectories(targetPath.getParent());

			// Read source file, compile it, and write to target
			final String content = Files.readString(sourcePath);
			final String compiled = Main.compileRoot(content);
			Files.writeString(targetPath, compiled);

			System.out.println("Compiled: " + sourcePath + " -> " + targetPath);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(e);
		}
	}
}
