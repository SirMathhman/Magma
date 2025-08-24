package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
		private final Map<String, List<MapNode>> nodeLists = new HashMap<>();
		private Optional<String> maybeType = Optional.empty();

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

		public boolean is(String type) {
			return maybeType.isPresent() && maybeType.get().equals(type);
		}

		public MapNode retype(String type) {
			this.maybeType = Optional.of(type);
			return this;
		}

		public MapNode withNodeList(String key, List<MapNode> values) {
			nodeLists.put(key, values);
			return this;
		}

		public Optional<List<MapNode>> findNodeList(String key) {
			return Optional.ofNullable(nodeLists.get(key));
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

	private record TypeRule(String type, Rule rule) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			if (node.is(type)) return rule.generate(node);
			return Optional.empty();
		}

		@Override
		public Optional<MapNode> lex(String input) {
			return rule.lex(input).map(node -> node.retype(type));
		}
	}

	private record OrRule(List<Rule> rules) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			return rules.stream().map(rule -> rule.generate(node)).flatMap(Optional::stream).findFirst();
		}

		@Override
		public Optional<MapNode> lex(String input) {
			return rules.stream().map(rule -> rule.lex(input)).flatMap(Optional::stream).findFirst();
		}
	}

	private record PrefixRule(String prefix, Rule rule) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			return rule.generate(node).map(result -> prefix + result);
		}

		@Override
		public Optional<MapNode> lex(String input) {
			if (!input.startsWith(prefix)) return Optional.empty();
			final var content = input.substring(prefix.length());
			return rule.lex(content);
		}
	}

	private record DivideRule(String key, Rule rule) {
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

		private Optional<MapNode> lex(CharSequence input) {
			final var list = divide(input).map(rule::lex).flatMap(Optional::stream).toList();
			return Optional.of(new MapNode().withNodeList(key, list));
		}

		private String generate(MapNode root) {
			return String.join("", root.findNodeList(key)
																 .orElse(Collections.emptyList())
																 .stream()
																 .map(rule::generate)
																 .flatMap(Optional::stream)
																 .toList());
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
		final var lex = new DivideRule("children", createJavaRootSegmentRule()).lex(input);
		final var root = transform(lex.orElse(new MapNode()));
		return new DivideRule("children", new OrRule(
				List.of(createStructRule(), new PlaceholderRule(new StringRule("content"))))).generate(root);
	}

	private static MapNode transform(MapNode root) {
		final var oldChildren = root.findNodeList("children").orElse(Collections.emptyList());
		final var newChildren = oldChildren.stream().filter(node -> !node.is("package") && !node.is("import")).flatMap(node -> {
			final var content = node.findString("content").orElse("");
			return Stream.of(node.retype("struct"), new MapNode().withString("content", content));
		}).toList();

		return new MapNode().withNodeList("children", newChildren);
	}

	private static OrRule createJavaRootSegmentRule() {
		return new OrRule(List.of(createNamespaceRule("package"), createNamespaceRule("import "), createClassRule()));
	}

	private static TypeRule createNamespaceRule(String type) {
		return new TypeRule(type, new StripRule(new PrefixRule(type + " ", new SuffixRule(new StringRule("value"), ";"))));
	}

	private static Rule createClassRule() {
		final var name = new StringRule("name");
		final var infixRule = new InfixRule(new StringRule("modifiers"), "class ", new StripRule(name));
		final var content = new StripRule(new SuffixRule(new StringRule("content"), "}"));
		return new TypeRule("class", new InfixRule(infixRule, "{", content));
	}

	private static Rule createStructRule() {
		return new TypeRule("struct", new SuffixRule(
				new InfixRule(new PlaceholderRule(new StringRule("modifiers")), "struct ", new StringRule("name")),
				" {};" + System.lineSeparator()));
	}

	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
