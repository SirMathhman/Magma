package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
	private interface Rule {
		Optional<Node> lex(String content);

		Optional<String> generate(Node node);
	}

	private static final class Node {
		private final Map<String, String> strings;

		private Node(Map<String, String> strings) {this.strings = strings;}

		public Node() {
			this(new HashMap<>());
		}

		private Node withString(String key, String value) {
			strings.put(key, value);
			return this;
		}

		private Optional<String> find(String key) {
			return Optional.ofNullable(strings.get(key));
		}

		public Node merge(Node node) {
			this.strings.putAll(node.strings);
			return this;
		}
	}

	private record StringRule(String key) implements Rule {
		@Override
		public Optional<Node> lex(String content) {
			return Optional.of(new Node().withString(getKey(), content));
		}

		@Override
		public Optional<String> generate(Node node) {
			return node.find(key);
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
		@Override
		public Optional<Node> lex(String content) {
			return rule.lex(content);
		}

		@Override
		public Optional<String> generate(Node node) {
			return rule().generate(node).map(Main::wrap);
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
		return compileAll(input, Main::compileRootSegment);
	}

	private static String compileAll(String input, Function<String, String> mapper) {
		final ArrayList<String> segments = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		int depth = 0;
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
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
		return segments.stream().map(mapper).collect(Collectors.joining());
	}

	private static String compileRootSegment(String input) {
		final String stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) return "";
		return compileRootSegmentValue(stripped) + System.lineSeparator();
	}

	private static String compileRootSegmentValue(String input) {
		return compileClass(input).orElseGet(() -> wrap(input));
	}

	private static Optional<String> compileClass(String input) {
		if (!input.endsWith("}")) return Optional.empty();
		final String slice = input.substring(0, input.length() - "}".length());

		final int contentStart = slice.indexOf("{");
		if (contentStart < 0) return Optional.empty();
		final String beforeBraces = slice.substring(0, contentStart);
		final String afterBraces = slice.substring(contentStart + "{".length());

		final String s = createClassHeaderRule().lex(beforeBraces)
																						.flatMap(inner -> createStructHeaderRule().generate(inner))
																						.orElseGet(() -> wrap(beforeBraces));

		return Optional.of(s + " {};" + System.lineSeparator() + compileAll(afterBraces, Main::compileClassSegment));

	}

	private static String compileClassSegment(String input) {
		return createJavaClassSegmentRule().lex(input).flatMap(node -> createCRootSegmentRule().generate(node)).orElse("");
	}

	private static SuffixRule createCRootSegmentRule() {
		return new SuffixRule(createClassSegmentRule(), System.lineSeparator());
	}

	private static StripRule createJavaClassSegmentRule() {
		return new StripRule(createClassSegmentRule());
	}

	private static OrRule createClassSegmentRule() {
		return new OrRule(List.of(createBlockRule(), createContentRule()));
	}

	private static PlaceholderRule createContentRule() {
		return new PlaceholderRule(new StringRule("input"));
	}

	private static SuffixRule createBlockRule() {
		return new SuffixRule(new InfixRule(new PlaceholderRule(new StringRule("header")), "{",
																				new PlaceholderRule(new StringRule("content"))), "}");
	}

	private static PrefixRule createStructHeaderRule() {
		return new PrefixRule("struct ", new StringRule("name"));
	}

	private static InfixRule createClassHeaderRule() {
		return new InfixRule(new StringRule("temp"), "class ", new StringRule("name"));
	}

	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
