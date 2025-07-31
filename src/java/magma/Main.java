package magma;

import magma.divide.DivideState;
import magma.divide.MutableDivideState;
import magma.rule.InfixRule;
import magma.rule.PlaceholderRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

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

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";

		return Main.createJavaClassRule()
							 .lex(strip)
							 .flatMap(Main.createTSClassRule()::generate)
							 .orElseGet(() -> Main.wrapInComment(strip));
	}

	private static InfixRule createTSClassRule() {
		final Rule name = new StringRule("name");
		final Rule body = new StringRule("body");

		return new InfixRule(new SuffixRule(new PrefixRule(name, "export class "), " {"), "",
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
