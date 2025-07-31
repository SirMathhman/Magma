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
			final String content = Files.readString(Paths.get("src/java/magma/Main.java"));
			final Path targetPath = Path.of("./src/node/magma/Main.ts");
			Files.createDirectories(targetPath.getParent());
			Files.writeString(targetPath, Main.compileRoot(content));
		} catch (final IOException e) {
			System.err.println("Error copying file: " + e.getMessage());
		}
	}
}
