package magma;

import magma.divide.DivideState;
import magma.divide.MutableDivideState;
import magma.node.MapNode;
import magma.node.Node;
import magma.rule.InfixRule;
import magma.rule.PlaceholderRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.StringRule;
import magma.rule.SuffixRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

public final class Main {
	private Main() {}

	private static String wrapInComment(final String content) {
		return "/*" + System.lineSeparator() + content + System.lineSeparator() + "*/";
	}

	private static String compileRoot(final String input) {
		return String.join("", Main.divide(input).stream().map(Main::compileRootSegment).toList());
	}

	private static Collection<String> divide(final String input) {
		DivideState current = new MutableDivideState(input);
		while (true) {
			final var maybeNext = current.pop();
			if (maybeNext.isEmpty()) break;
			final var next = maybeNext.get();
			current = Main.fold(next.left(), next.right());
		}

		return current.advance().stream().toList();
	}

	private static DivideState fold(final DivideState state, final char c) {
		final var appended = state.append(c);
		if ('{' == c) return appended.enter();
		else if ('}' == c) return appended.exit();
		else if (';' == c && appended.isLevel())
			return appended.advance();
		return appended;
	}

	private static Optional<String> extractClassName(final String declaration) {
		final int classIndex = declaration.indexOf("class ");
		if (-1 == classIndex) return Optional.empty();
		final int start = classIndex + 6;
		final int end = declaration.indexOf(' ', start);
		return Optional.of(-1 == end ? declaration.substring(start) : declaration.substring(start, end));
	}

	private static Optional<String> extractClassBody(final String declaration) {
		final int openBrace = declaration.indexOf('{');
		if (-1 == openBrace) return Optional.empty();
		return Optional.of(declaration.substring(openBrace));
	}

	private static Optional<String> compileClass(final String input) {
		return Main.extractClassBody(input)
							 .flatMap(body -> Main.extractClassName(input)
																		.map(name -> Main.generate(
																				new MapNode().withString("name", name).withString("body", body))));
	}

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		if (strip.contains("class ")) return Main.compileClass(strip).orElseGet(() -> Main.wrapInComment(strip));
		return Main.wrapInComment(strip);
	}

	private static String generate(final Node node) {
		return Main.createClassRule().generate(node).orElse("");
	}

	private static InfixRule createClassRule() {
		final Rule nameRule = new StringRule("name");
		final Rule bodyRule = new StringRule("body");

		return new InfixRule(new SuffixRule(new PrefixRule(nameRule, "export class "), " {"),
												 new SuffixRule(new PlaceholderRule(bodyRule), "}"), "");
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
