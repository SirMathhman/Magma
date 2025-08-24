package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	private interface Rule {
		Optional<String> generate(MapNode node);

		Optional<MapNode> lex(String input);
	}

	private static class State {
		private final Collection<String> segments = new ArrayList<>();
		public int depth = 0;
		private StringBuilder buffer = new StringBuilder();

		private Stream<String> stream() {
			return segments.stream();
		}

		private State advance() {
			segments.add(buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		private State append(char c) {
			buffer.append(c);
			return this;
		}

		public boolean isLevel() {
			return depth == 0;
		}

		public State enter() {
			depth++;
			return this;
		}

		public State exit() {
			depth--;
			return this;
		}
	}

	public static final class MapNode {
		private final Map<String, String> strings = new HashMap<>();

		private MapNode withString(String key, String value) {
			strings.put(key, value);
			return this;
		}

		private Optional<String> findString(String key) {
			return Optional.ofNullable(strings.get(key));
		}

		public MapNode merge(MapNode other) {
			strings.putAll(other.strings);
			return this;
		}
	}

	public record StringRule(String key) implements Rule {
		@Override
		public Optional<MapNode> lex(String content) {
			return Optional.of(new MapNode().withString(key, content));
		}

		@Override
		public Optional<String> generate(MapNode node) {
			return node.findString(key);
		}
	}

	public record PlaceholderRule(Rule childRule) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			return childRule.generate(node).map(Main::wrap);
		}

		@Override
		public Optional<MapNode> lex(String input) {
			return childRule.lex(input);
		}
	}

	public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
		@Override
		public Optional<MapNode> lex(String input) {
			final var index = input.indexOf(infix());
			if (index < 0) return Optional.empty();

			final var leftSlice = input.substring(0, index);
			final var rightSlice = input.substring(index + infix().length());
			return leftRule.lex(leftSlice).flatMap(withModifiers -> rightRule.lex(rightSlice).map(withModifiers::merge));
		}

		@Override
		public Optional<String> generate(MapNode node) {
			return leftRule.generate(node)
										 .flatMap(
												 leftResult -> rightRule.generate(node).map(rightResult -> leftResult + infix + rightResult));
		}
	}

	private record StripRule(Rule rule) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			return rule.generate(node);
		}

		@Override
		public Optional<MapNode> lex(String input) {
			return rule.lex(input.strip());
		}
	}

	private record SuffixRule(Rule childRule, String suffix) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			return childRule.generate(node).map(result -> result + suffix);
		}

		@Override
		public Optional<MapNode> lex(String input) {
			if (!input.endsWith(suffix)) return Optional.empty();
			final var content = input.substring(0, input.length() - suffix.length());
			return childRule.lex(content);
		}
	}

	public static void main(String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "main.c"), compile(input) + "int main(){\r\n\treturn 0;\r\n}");
			new ProcessBuilder("clang", "main.c", "-o", "main.exe").inheritIO().start().waitFor();
		} catch (IOException | InterruptedException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(CharSequence input) {
		return divide(input).map(Main::compileRootSegment).collect(Collectors.joining());
	}

	private static String compileRootSegment(String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		return compileClass(strip).orElseGet(() -> wrap(strip));
	}

	private static Optional<String> compileClass(String strip) {
		final var name = new StringRule("name");
		final var infixRule = new InfixRule(new StringRule("modifiers"), "class ", new StripRule(name));
		final var content = new StripRule(new SuffixRule(new StringRule("content"), "}"));
		return new InfixRule(infixRule, "{", content).lex(strip).flatMap(Main::generate);
	}

	private static Optional<String> generate(MapNode mapNode) {
		return new InfixRule(
				new InfixRule(new PlaceholderRule(new StringRule("modifiers")), "struct ", new StringRule("name")),
				" {};" + System.lineSeparator(), new PlaceholderRule(new StringRule("content"))).generate(mapNode);
	}

	private static Stream<String> divide(CharSequence input) {
		var current = new State();
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			current = fold(current, c);
		}

		return current.advance().stream();
	}

	private static State fold(State current, char c) {
		final var appended = current.append(c);
		if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '{') return appended.enter();
		if (c == '}') return appended.exit();
		return appended;
	}

	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
