package magma;

import magma.node.MapNode;
import magma.node.Node;
import magma.rule.DivideRule;
import magma.rule.InfixRule;
import magma.rule.OrRule;
import magma.rule.PlaceholderRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;

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
		return Main.createJavaRootRule().lex(input).map(Main::transform).flatMap(Main::generate).orElse("");
	}

	private static Node transform(final Node root) {
		final List<Node> newChildren = root.findNodeList("children")
																			 .orElse(Collections.emptyList())
																			 .stream()
																			 .filter(node -> !node.is("package") || !node.is("import"))
																			 .toList();

		return new MapNode().withNodeList("children", newChildren);
	}

	private static DivideRule createJavaRootRule() {
		return new DivideRule(Main.createJavaRootSegmentRule());
	}

	private static Optional<String> generate(final Node root) {
		return new DivideRule(Main.createTSRootSegmentRule()).generate(root);
	}


	private static OrRule createTSRootSegmentRule() {
		return new OrRule(List.of(Main.createTSClassRule(), Main.createTypePlaceholderRule()));
	}

	private static OrRule createJavaRootSegmentRule() {
		return new OrRule(
				List.of(Main.createNamespacedRule("package"), Main.createNamespacedRule("import"), Main.createJavaClassRule(),
								Main.createTypePlaceholderRule()));
	}

	private static TypeRule createTypePlaceholderRule() {
		return new TypeRule("placeholder", new PlaceholderRule(new StringRule("content")));
	}

	private static Rule createNamespacedRule(final String type) {
		final var content = new PrefixRule(type + " ", new SuffixRule(new StringRule("content"), ";"));
		return new TypeRule(type, new StripRule(content));
	}

	private static InfixRule createTSClassRule() {
		final Rule name = new StringRule("name");
		final Rule body = new StringRule("body");

		return new InfixRule(new SuffixRule(new PrefixRule("export class ", name), " {"), "",
												 new SuffixRule(new PlaceholderRule(body), "}"));
	}

	private static InfixRule createJavaClassRule() {
		final Rule name = new StringRule("name");
		final Rule body = new StringRule("body");

		// More flexible rule that can handle modifiers before "class"
		// and additional content between class name and opening brace
		return new InfixRule(new StringRule("modifiers"), "class ",
												 new InfixRule(name, "{", new StripRule(new SuffixRule(body, "}"))));
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
