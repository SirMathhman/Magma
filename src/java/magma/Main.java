package magma;

import magma.error.CompileError;
import magma.error.Error;
import magma.error.ThrowableError;
import magma.error.WrappedError;
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
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Main entry point for the Magma compiler.
 * <p>
 * This class handles the process of compiling Java source files to TypeScript.
 * It traverses the source directory, processes each Java file, and generates
 * corresponding TypeScript files in the target directory. The compilation
 * process includes lexing the Java code into an AST, transforming the AST,
 * and then generating TypeScript code from the transformed AST.
 * <p>
 * The class also manages directory synchronization and cleanup of outdated
 * generated files.
 */
final class Main {
	private static final Pattern PATTERN = Pattern.compile("\\.java$");

	private Main() {}

	private static Result<String, CompileError> compileRoot(final String input) {
		// First, lex the input
		final Result<Node, CompileError> lexResult = Lang.createJavaRootRule().lex(input);

		// Log lexing errors if any
		lexResult.match(ok -> ok, err -> {
			System.err.println("Lexing error: " + err.getMessage()); return err;
		});

		// Transform the node and generate the output
		return lexResult.flatMapValue(node -> {
			final Node transformedNode = Main.transform(node);
			final Result<String, CompileError> generateResult = Lang.createTSRootRule().generate(transformedNode);

			// Log generation errors if any
			generateResult.match(ok -> ok, err -> {
				System.err.println("Generation error: " + err.getMessage()); return err;
			});

			return generateResult;
		});
	}

	/**
	 * Filters the AST by removing Java-specific package and import declarations.
	 * This transformation prepares the AST for TypeScript code generation by
	 * removing nodes that don't have TypeScript equivalents.
	 *
	 * @param root The root node of the Java AST
	 * @return A new MapNode containing only the filtered children nodes
	 */
	private static Node transform(final Node root) {
		final List<Node> newChildren = root.findNodeList("children")
																			 .orElse(Collections.emptyList())
																			 .stream()
																			 .filter(node -> !node.is("package") && !node.is("import"))
																			 .toList();

		return new MapNode().withNodeList("children", newChildren);
	}

	public static void main(final String[] args) {
		final Path sourceRoot = Paths.get("src/java");
		final Path targetRoot = Paths.get("src/node");

		try {
			// Ensure target directory exists
			Files.createDirectories(targetRoot);
		} catch (final IOException e) {
			final ThrowableError error = new ThrowableError(e);
			System.err.println("Failed to create target directory: " + error.display()); return;
		}

		// Collect all existing files in the target directory
		final var targetFilesResult = Main.collect(targetRoot, ".ts");
		final List<Path> targetFiles = targetFilesResult.match(ok -> ok, err -> {
			System.err.println("Failed to collect target files: " + err.display()); return List.of();
		});

		final var sourcePathsResult = Main.collect(sourceRoot, ".java"); if (sourcePathsResult.isErr()) {
			sourcePathsResult.match(ok -> ok, err -> {
				System.err.println("Failed to collect source files: " + err.display()); return List.of();
			}); return;
		}

		// Track generated files and sync directories
		final List<Path> generatedFiles = new ArrayList<>();
		final Result<Void, ThrowableError> syncResult = Main.syncDirectoryStructure(sourceRoot, targetRoot);
		if (syncResult.isErr()) {
			syncResult.match(ok -> ok, err -> {
				System.err.println("Failed to sync directory structure: " + err.display()); return Void.TYPE;
			}); return;
		}

		final List<Path> sourcePaths = sourcePathsResult.match(ok -> ok, err -> List.of());
		for (final Path sourcePath : sourcePaths) {
			final Result<Path, Error> result = Main.runWithFile(sourcePath, sourceRoot, targetRoot, generatedFiles);
			if (result.isErr()) {
				// If any file fails, print the error and stop processing
				result.match(ok -> ok, err -> {
					System.err.println(err.display()); return Path.of("");
				}); break;
			}
		}

		// Delete files in target directory that were not generated
		Main.cleanupTargetDirectory(targetFiles, generatedFiles);
	}

	private static Result<List<Path>, ThrowableError> collect(final Path root, final String extension) {
		try (final var paths = Files.walk(root)) {
			return new Ok<>(paths.filter(path -> path.toString().endsWith(extension) && Files.isRegularFile(path)).toList());
		} catch (final IOException e) {
			return new Err<>(new ThrowableError(e));
		}
	}

	private static Result<Path, Error> runWithFile(final Path sourcePath,
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

			return compileResult.mapValue(compiledContent -> {
				try {
					Files.writeString(targetPath, compiledContent);

					// Add the generated file to the list
					generatedFiles.add(targetPath);

					System.out.println("Compiled: " + sourcePath + " -> " + targetPath); return targetPath;
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}).mapErr(err -> {
				final String errorMessage = "Compilation failed for " + sourcePath + ": " + err.display();
				return new WrappedError(err);
			});
		} catch (final IOException e) {
			return new Err<>(new ThrowableError(e));
		} catch (final RuntimeException e) {
			if (e.getCause() instanceof IOException) return new Err<>(new ThrowableError(e.getCause()));
			return new Err<>(new ThrowableError(e));
		}
	}

	private static void cleanupTargetDirectory(final List<Path> targetFiles, final List<Path> generatedFiles) {
		// Find files that exist in target directory but weren't generated
		targetFiles.stream()
							 .filter(targetFile -> !generatedFiles.contains(targetFile))
							 .sorted((a, b) -> b.toString().length() - a.toString().length()) // Delete deeper paths first
							 .forEach(fileToDelete -> {
								 try {
									 Files.deleteIfExists(fileToDelete);
									 System.out.println("Deleted: " + fileToDelete + " (not generated in this run)");
								 } catch (final IOException e) {
									 final ThrowableError error = new ThrowableError(e);
									 System.err.println("Failed to delete " + fileToDelete + ": " + error.display());
								 }
							 });
	}

	private static Result<Void, ThrowableError> syncDirectoryStructure(final Path sourceRoot, final Path targetRoot) {
		try (final var paths = Files.walk(sourceRoot)) {
			paths.filter(Files::isDirectory).forEach(sourceDir -> {
				try {
					final Path targetDir = targetRoot.resolve(sourceRoot.relativize(sourceDir));
					Files.createDirectories(targetDir);
				} catch (final IOException e) {
					final ThrowableError error = new ThrowableError(e);
					System.err.println("Failed to create directory: " + error.display());
				}
			}); return new Ok<>(null);
		} catch (final IOException e) {
			return new Err<>(new ThrowableError(e));
		}
	}
}
