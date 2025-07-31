package magma;

import magma.node.MapNode;
import magma.node.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public final class Main {
	private Main() {}

	private static String compileRoot(final String input) {
		return Lang.createJavaRootRule()
							 .lex(input)
							 .map(Main::transform)
							 .flatMap(Lang.createTSRootRule()::generate)
							 .orElse("");
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
		try {
			Path sourceRoot = Paths.get("src/java");
			Path targetRoot = Paths.get("src/node");
			
			Files.walk(sourceRoot)
				.filter(path -> path.toString().endsWith(".java"))
				.forEach(sourcePath -> {
					try {
						// Get relative path from source root
						Path relativePath = sourceRoot.relativize(sourcePath);
						
						// Create corresponding target path with .ts extension
						Path targetPath = targetRoot.resolve(
							relativePath.toString().replaceAll("\\.java$", ".ts")
						);
						
						// Create parent directories if they don't exist
						Files.createDirectories(targetPath.getParent());
						
						// Read source file, compile it, and write to target
						String content = Files.readString(sourcePath);
						String compiled = Main.compileRoot(content);
						Files.writeString(targetPath, compiled);
						
						System.out.println("Compiled: " + sourcePath + " -> " + targetPath);
					} catch (IOException e) {
						System.err.println("Error processing file " + sourcePath + ": " + e.getMessage());
					}
				});
		} catch (final IOException e) {
			System.err.println("Error walking directory: " + e.getMessage());
		}
	}
}
