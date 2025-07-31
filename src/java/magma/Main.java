package magma;

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

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		if (strip.contains("class ")) {
			final Optional<String> className = Main.extractClassName(strip);
			if (className.isPresent()) return Main.generate(new MapNode().withString("name", className.get()));
		}
		return Main.wrapInComment(strip);
	}

	private static String generate(final Node node) {
		return "export class " + node.findString("name").orElse("") + " {}";
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
