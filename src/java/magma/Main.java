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
			final CompileError error = lexResult.match(ok -> null, err -> err);
			System.err.println("Lexing error: " + error.getMessage()); return new Err<>(error);
		}

		final Node node = lexResult.match(ok -> ok, err -> null); final Node transformedNode = Main.transform(node);

		final Result<String, CompileError> generateResult = Lang.createTSRootRule().generate(transformedNode);
		if (generateResult.isErr()) {
			final CompileError error = generateResult.match(ok -> null, err -> err);
			System.err.println("Generation error: " + error.getMessage()); return new Err<>(error);
		}

		final String value = generateResult.match(ok -> ok, err -> null); return new magma.result.Ok<>(value);
	}

	private static Node transform(final Node root) {
		final List<Node> newChildren = root.findNodeList("children")
																			 .orElse(Collections.emptyList())
																			 .stream().filter(node -> !node.is("package") && !node.is("import"))
																			 .toList();

		return new MapNode().withNodeList("children", newChildren);
	}

	public static void main(final String[] args) {
		final Path sourceRoot = Paths.get("src/java");
		final Path targetRoot = Paths.get("src/node");

		// Collect all existing files in the target directory
		final var targetFilesResult = Main.collect(targetRoot); final List<Path> targetFiles =
				targetFilesResult.isErr() ? List.of() : targetFilesResult.match(ok -> ok, err -> null);

		final var maybe = Main.collect(sourceRoot); if (maybe.isErr()) {
			maybe.match(ok -> null, err -> {
				err.printStackTrace(); return null;
			}); return;
		}

		// Track generated files
		final List<Path> generatedFiles = new java.util.ArrayList<>();
		final List<Path> paths = maybe.match(ok -> ok, err -> null); for (final Path sourcePath : paths) {
			final Optional<IOException> result = Main.runWithFile(sourcePath, sourceRoot, targetRoot, generatedFiles);
			if (result.isPresent()) {
				// If any file fails, print the error and stop processing
				result.get().printStackTrace(); break;
			}
		}

		// Delete files in target directory that were not generated
		Main.cleanupTargetDirectory(targetFiles, generatedFiles);
	}

	private static Result<List<Path>, IOException> collect(final Path root) {
		try (final var paths = Files.walk(root)) {
			// If we're collecting from source directory, filter for Java files
			// If we're collecting from target directory, filter for TypeScript files
			String extension = root.toString().contains("src/java") ? ".java" : ".ts";
			return new Ok<>(paths.filter(path -> path.toString().endsWith(extension) && Files.isRegularFile(path)).toList());
		} catch (final IOException e) {
			return new Err<>(e);
		}
	}

	private static Optional<IOException> runWithFile(final Path sourcePath,
																									 final Path sourceRoot,
																									 final Path targetRoot,
																									 final List<Path> generatedFiles) {
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
				final String errorMessage =
						compileResult.match(ok -> "", err -> "Compilation failed for " + sourcePath + ": " + err.display());
				return Optional.of(new IOException(errorMessage));
			}

			final String compiledContent = compileResult.match(ok -> ok, err -> null);
			Files.writeString(targetPath, compiledContent);

			// Add the generated file to the list
			generatedFiles.add(targetPath);

			System.out.println("Compiled: " + sourcePath + " -> " + targetPath);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(e);
		}
	}

	private static void cleanupTargetDirectory(final List<Path> targetFiles, final List<Path> generatedFiles) {
		// Find files that exist in target directory but weren't generated
		targetFiles.stream().filter(targetFile -> !generatedFiles.contains(targetFile)).forEach(fileToDelete -> {
			try {
				Files.delete(fileToDelete); System.out.println("Deleted: " + fileToDelete + " (not generated in this run)");
			} catch (IOException e) {
				System.err.println("Failed to delete " + fileToDelete + ": " + e.getMessage());
			}
		});
	}
}
