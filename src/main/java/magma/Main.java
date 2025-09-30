package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	private sealed interface Result<T, X> {}

	private interface Rule {
		Optional<Node> lex(String content);

		Optional<String> generate(Node node);
	}

	private record Ok<T, X>(T value) implements Result<T, X> {}

	private record Err<T, X>(X error) implements Result<T, X> {}

	private record CompileError(String reason, String sourceCode, List<CompileError> causes) {}

	private static final class Node {
		private final Map<String, String> strings = new HashMap<>();
		private final Map<String, List<Node>> nodeLists = new HashMap<>();
		private final Map<String, Node> nodes = new HashMap<>();
		private Optional<String> maybeType = Optional.empty();

		private Node withString(String key, String value) {
			strings.put(key, value);
			return this;
		}

		private Optional<String> findString(String key) {
			return Optional.ofNullable(strings.get(key));
		}

		public Node merge(Node node) {
			this.strings.putAll(node.strings);
			nodeLists.putAll(node.nodeLists);
			nodes.putAll(node.nodes);
			return this;
		}

		public Node withNodeList(String key, List<Node> values) {
			nodeLists.put(key, values);
			return this;
		}

		public Optional<List<Node>> findNodeList(String key) {
			return Optional.ofNullable(nodeLists.get(key));
		}

		public Node withNode(String key, Node node) {
			nodes.put(key, node);
			return this;
		}

		public Optional<Node> findNode(String key) {
			return Optional.ofNullable(nodes.get(key));
		}

		public Node retype(String type) {
			this.maybeType = Optional.of(type);
			return this;
		}

		public boolean is(String type) {
			return this.maybeType.isPresent() && maybeType.get().equals(type);
		}
	}

	private record StringRule(String key) implements Rule {
		@Override
		public Optional<Node> lex(String content) {
			return Optional.of(new Node().withString(getKey(), content));
		}

		@Override
		public Optional<String> generate(Node node) {
			return node.findString(key);
		}

		public String getKey() {
			return key;
		}
	}

	private record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
		@Override
		public Optional<Node> lex(String slice) {
			final int index = slice.indexOf(infix());
			if (index < 0) return Optional.empty();

			final String beforeContent = slice.substring(0, index);
			final String content = slice.substring(index + infix().length());

			return leftRule.lex(beforeContent).flatMap(left -> rightRule.lex(content).map(left::merge));
		}

		@Override
		public Optional<String> generate(Node node) {
			return leftRule.generate(node).flatMap(inner -> rightRule.generate(node).map(other -> inner + infix + other));
		}

	}

	private record PlaceholderRule(Rule rule) implements Rule {
		private static String wrap(String input) {
			return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
		}

		@Override
		public Optional<Node> lex(String content) {
			return rule.lex(content);
		}

		@Override
		public Optional<String> generate(Node node) {
			return rule().generate(node).map(PlaceholderRule::wrap);
		}
	}

	private record SuffixRule(Rule rule, String suffix) implements Rule {
		@Override
		public Optional<Node> lex(String input) {
			if (!input.endsWith(suffix())) return Optional.empty();
			final String slice = input.substring(0, input.length() - suffix().length());
			return getRule().lex(slice);
		}

		@Override
		public Optional<String> generate(Node node) {
			return rule.generate(node).map(value -> value + suffix());
		}

		public Rule getRule() {
			return rule;
		}
	}

	private record PrefixRule(String prefix, Rule rule) implements Rule {
		@Override
		public Optional<Node> lex(String content) {
			if (content.startsWith(prefix)) return rule.lex(content.substring(prefix.length()));
			else return Optional.empty();
		}

		@Override
		public Optional<String> generate(Node node) {
			return rule.generate(node).map(inner -> prefix + inner);
		}
	}

	private record OrRule(List<Rule> rules) implements Rule {
		@Override
		public Optional<Node> lex(String content) {
			return rules.stream().map(rule -> rule.lex(content)).flatMap(Optional::stream).findFirst();
		}

		@Override
		public Optional<String> generate(Node node) {
			return rules.stream().map(rule -> rule.generate(node)).flatMap(Optional::stream).findFirst();
		}
	}

	private record StripRule(Rule rule) implements Rule {
		@Override
		public Optional<Node> lex(String content) {
			return rule.lex(content.strip());
		}

		@Override
		public Optional<String> generate(Node node) {
			return rule.generate(node);
		}
	}

	private record DivideRule(String key, Rule rule) implements Rule {
		private static Stream<String> divide(String afterBraces) {
			final ArrayList<String> segments = new ArrayList<>();
			StringBuilder buffer = new StringBuilder();
			int depth = 0;
			for (int i = 0; i < afterBraces.length(); i++) {
				final char c = afterBraces.charAt(i);
				buffer.append(c);
				if (c == ';' && depth == 0) {
					segments.add(buffer.toString());
					buffer = new StringBuilder();
				} else if (c == '}' && depth == 1) {
					segments.add(buffer.toString());
					buffer = new StringBuilder();
					depth--;
				} else {
					if (c == '{') depth++;
					if (c == '}') depth--;
				}
			}

			segments.add(buffer.toString());
			return segments.stream();
		}

		@Override
		public Optional<Node> lex(String input) {
			final List<Node> children = divide(input).map(rule()::lex).flatMap(Optional::stream).toList();
			return Optional.of(new Node().withNodeList(key, children));
		}

		@Override
		public Optional<String> generate(Node value) {
			return value.findNodeList(key())
									.map(list -> list.stream()
																	 .map(rule()::generate)
																	 .flatMap(Optional::stream)
																	 .collect(Collectors.joining()));
		}
	}

	private record NodeRule(String key, Rule rule) implements Rule {
		@Override
		public Optional<Node> lex(String content) {
			return rule.lex(content).map(node -> new Node().withNode(key, node));
		}

		@Override
		public Optional<String> generate(Node node) {
			return Optional.empty();
		}
	}

	private record TypeRule(String type, Rule rule) implements Rule {
		@Override
		public Optional<Node> lex(String content) {
			return rule.lex(content).map(node -> node.retype(type));
		}

		@Override
		public Optional<String> generate(Node node) {
			return Optional.empty();
		}
	}

	public static void main(String[] args) {
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);
			Files.writeString(source.resolveSibling("main.c"), compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(String input) {
		return createJavaRootRule().lex(input)
															 .map(Main::transform)
															 .flatMap(createCRootRule()::generate)
															 .orElseGet(() -> PlaceholderRule.wrap(input));
	}

	private static Rule createJavaRootRule() {
		return new DivideRule("children", createJavaRootSegmentRule());
	}

	private static Rule createJavaRootSegmentRule() {
		return new StripRule(new OrRule(
				List.of(createClassRule(), createPrefixRule("package"), createPrefixRule("import"), createContentRule())));
	}

	private static Rule createPrefixRule(String type) {
		return new TypeRule(type, new PrefixRule(type + " ", new StringRule("content")));
	}

	private static Node transform(Node node) {
		final List<Node> newChildren = node.findNodeList("children")
																			 .orElse(Collections.emptyList())
																			 .stream()
																			 .map(Main::getNode)
																			 .flatMap(Optional::stream)
																			 .toList();

		return node.withNodeList("children", newChildren);
	}

	private static Optional<Node> getNode(Node node) {
		if (node.is("package") || node.is("import")) return Optional.empty();
		else if (node.is("class")) {
			final List<Node> copy = node.findNodeList("children").orElse(Collections.emptyList());
			final ArrayList<Node> copy0 = new ArrayList<>();
			copy0.add(node.findNode("header").orElse(new Node()));
			copy0.addAll(copy);
			return Optional.of(node.withNodeList("children", copy0));
		} else return Optional.of(node);
	}

	private static Rule createClassRule() {
		final NodeRule orRule = new NodeRule("header", new OrRule(List.of(createClassHeaderRule(), createContentRule())));
		final DivideRule children = new DivideRule("children", createJavaClassSegmentRule());
		return new TypeRule("class", new SuffixRule(new InfixRule(orRule, "{", children), "}"));
	}

	private static Rule createCRootRule() {
		return new DivideRule("children", createCRootSegmentRule());
	}

	private static Rule createCRootSegmentRule() {
		return new OrRule(List.of(new SuffixRule(createClassSegmentRule(), System.lineSeparator()), createContentRule()));
	}

	private static Rule createJavaClassSegmentRule() {
		return new StripRule(createClassSegmentRule());
	}

	private static Rule createClassSegmentRule() {
		return new OrRule(List.of(createStructHeaderRule(), createBlockRule(), createContentRule()));
	}

	private static Rule createContentRule() {
		return new TypeRule("content", new PlaceholderRule(new StringRule("input")));
	}

	private static Rule createBlockRule() {
		return new SuffixRule(new InfixRule(new PlaceholderRule(new StringRule("header")), "{",
																				new PlaceholderRule(new StringRule("content"))), "}");
	}

	private static Rule createStructHeaderRule() {
		return new TypeRule("struct", new PrefixRule("struct ", new SuffixRule(new StringRule("name"), " {};")));
	}

	private static Rule createClassHeaderRule() {
		return new InfixRule(new StringRule("temp"), "class ", new StripRule(new StringRule("name")));
	}
}
