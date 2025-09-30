package magma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Stream;

public class Main {
	private sealed interface Result<T, X> {
		<R> Result<R, X> map(Function<T, R> fn);

		<R> Result<R, X> flatMap(Function<T, Result<R, X>> fn);

	}

	private interface Rule {
		Result<Node, CompileError> lex(String content);

		Result<String, CompileError> generate(Node node);
	}

	private interface Context {
		String display();
	}

	private interface Error {
		String display();
	}

	private record Ok<T, X>(T value) implements Result<T, X> {
		@Override
		public <R> Result<R, X> map(Function<T, R> fn) {
			return new Ok<>(fn.apply(this.value));
		}

		@Override
		public <R> Result<R, X> flatMap(Function<T, Result<R, X>> fn) {
			return fn.apply(this.value);
		}
	}

	private record Err<T, X>(X error) implements Result<T, X> {
		@Override
		public <R> Result<R, X> map(Function<T, R> fn) {
			return new Err<>(error);
		}

		@Override
		public <R> Result<R, X> flatMap(Function<T, Result<R, X>> fn) {
			return new Err<>(error);
		}
	}

	private record StringContext(String context) implements Context {
		@Override
		public String display() {
			return context;
		}
	}

	private record NodeContext(Node node) implements Context {
		@Override
		public String display() {
			return node.display();
		}
	}

	private record CompileError(String reason, Context context, List<CompileError> causes) implements Error {
		public CompileError(String reason, Context sourceCode) {
			this(reason, sourceCode, Collections.emptyList());
		}

		@Override
		public String display() {
			return format(0, 0);
		}

		private String format(int depth, int index) {
			StringJoiner joiner = new StringJoiner(System.lineSeparator());
			for (int i = 0; i < causes.size(); i++) {
				CompileError error = causes.get(i);
				String format = error.format(depth + 1, i);
				joiner.add(format);
			}

			final String formattedChildren = joiner.toString();
			return "\t".repeat(depth) + index + ") " + reason + ": " + context.display() + formattedChildren;
		}
	}

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

		public String display() {
			return format(0);
		}

		private String format(int depth) {
			return maybeType.map(inner -> inner + " ").orElse("") + "{" + "strings=" + strings + ", nodes=" + nodes +
						 ", nodeLists=" + nodeLists + '}';
		}
	}

	private record StringRule(String key) implements Rule {
		@Override
		public Result<Node, CompileError> lex(String content) {
			return new Ok<>(new Node().withString(getKey(), content));
		}

		@Override
		public Result<String, CompileError> generate(Node node) {
			return node.findString(key)
								 .<Result<String, CompileError>>map(Ok::new)
								 .orElseGet(
										 () -> new Err<>(new CompileError("String '" + key + "' not present.", new NodeContext(node))));
		}

		public String getKey() {
			return key;
		}
	}

	private record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
		@Override
		public Result<Node, CompileError> lex(String input) {
			final int index = input.indexOf(infix());
			if (index < 0) return new Err<>(new CompileError("Infix '" + infix + "' not present", new StringContext(input)));

			final String beforeContent = input.substring(0, index);
			final String content = input.substring(index + infix().length());

			return leftRule.lex(beforeContent).flatMap(left -> rightRule.lex(content).map(left::merge));
		}

		@Override
		public Result<String, CompileError> generate(Node node) {
			return leftRule.generate(node).flatMap(inner -> rightRule.generate(node).map(other -> inner + infix + other));
		}

	}

	private record PlaceholderRule(Rule rule) implements Rule {
		private static String wrap(String input) {
			return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
		}

		@Override
		public Result<Node, CompileError> lex(String content) {
			return rule.lex(content);
		}

		@Override
		public Result<String, CompileError> generate(Node node) {
			return rule().generate(node).map(PlaceholderRule::wrap);
		}
	}

	private record SuffixRule(Rule rule, String suffix) implements Rule {
		@Override
		public Result<Node, CompileError> lex(String input) {
			if (!input.endsWith(suffix()))
				return new Err<>(new CompileError("Suffix '" + suffix + "' not present", new StringContext(input)));
			final String slice = input.substring(0, input.length() - suffix().length());
			return getRule().lex(slice);
		}

		@Override
		public Result<String, CompileError> generate(Node node) {
			return rule.generate(node).map(value -> value + suffix());
		}

		public Rule getRule() {
			return rule;
		}
	}

	private record PrefixRule(String prefix, Rule rule) implements Rule {
		@Override
		public Result<Node, CompileError> lex(String content) {
			if (content.startsWith(prefix)) return rule.lex(content.substring(prefix.length()));
			else return new Err<>(new CompileError("Prefix '" + prefix + "' not present", new StringContext(content)));
		}

		@Override
		public Result<String, CompileError> generate(Node node) {
			return rule.generate(node).map(inner -> prefix + inner);
		}
	}

	private record OrRule(List<Rule> rules) implements Rule {
		@Override
		public Result<Node, CompileError> lex(String content) {
			final ArrayList<CompileError> errors = new ArrayList<>();
			for (Rule rule : rules) {
				Result<Node, CompileError> res = rule.lex(content);
				if (res instanceof Ok<?, ?> ok) {
					@SuppressWarnings("unchecked")
					Ok<Node, CompileError> nodeOk = (Ok<Node, CompileError>) ok;
					return nodeOk;
				} else if (res instanceof Err<?, ?> err) {
					@SuppressWarnings("unchecked")
					Err<Node, CompileError> nodeErr = (Err<Node, CompileError>) err;
					errors.add(nodeErr.error());
				}
			}
			return new Err<>(new CompileError("No alternative matched for input", new StringContext(content), errors));
		}

		@Override
		public Result<String, CompileError> generate(Node node) {
			final ArrayList<CompileError> errors = new ArrayList<>();
			for (Rule rule : rules) {
				Result<String, CompileError> res = rule.generate(node);
				if (res instanceof Ok<?, ?> ok) {
					@SuppressWarnings("unchecked")
					Ok<String, CompileError> strOk = (Ok<String, CompileError>) ok;
					return strOk;
				} else if (res instanceof Err<?, ?> err) {
					@SuppressWarnings("unchecked")
					Err<String, CompileError> strErr = (Err<String, CompileError>) err;
					errors.add(strErr.error());
				}
			}
			return new Err<>(new CompileError("No generator matched for node", new NodeContext(node), errors));
		}
	}

	private record StripRule(Rule rule) implements Rule {
		@Override
		public Result<Node, CompileError> lex(String content) {
			return rule.lex(content.strip());
		}

		@Override
		public Result<String, CompileError> generate(Node node) {
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
		public Result<Node, CompileError> lex(String input) {
			final ArrayList<Node> children = new ArrayList<>();
			final ArrayList<CompileError> errors = new ArrayList<>();
			divide(input).forEach(segment -> {
				Result<Node, CompileError> res = rule().lex(segment);
				if (res instanceof Ok<?, ?> ok) {
					@SuppressWarnings("unchecked")
					Ok<Node, CompileError> nodeOk = (Ok<Node, CompileError>) ok;
					children.add(nodeOk.value());
				} else if (res instanceof Err<?, ?> err) {
					@SuppressWarnings("unchecked")
					Err<Node, CompileError> nodeErr = (Err<Node, CompileError>) err;
					errors.add(nodeErr.error());
				}
			});
			if (!errors.isEmpty()) return new Err<>(
					new CompileError("Errors while lexing divided segments for '" + key + "'", new StringContext(input), errors));
			return new Ok<>(new Node().withNodeList(key, children));
		}

		@Override
		public Result<String, CompileError> generate(Node value) {
			return value.findNodeList(key()).<Result<String, CompileError>>map(list -> {
				final StringBuilder sb = new StringBuilder();
				final ArrayList<CompileError> errors = new ArrayList<>();
				for (Node child : list) {
					Result<String, CompileError> res = rule().generate(child);
					if (res instanceof Ok<?, ?> ok) {
						@SuppressWarnings("unchecked")
						Ok<String, CompileError> strOk = (Ok<String, CompileError>) ok;
						sb.append(strOk.value());
					} else if (res instanceof Err<?, ?> err) {
						@SuppressWarnings("unchecked")
						Err<String, CompileError> strErr = (Err<String, CompileError>) err;
						errors.add(strErr.error());
					}
				}
				if (!errors.isEmpty()) return new Err<>(
						new CompileError("Errors while generating divided segments for '" + key + "'", new NodeContext(value),
														 errors));
				return new Ok<>(sb.toString());
			}).orElseGet(() -> new Err<>(new CompileError("Node list '" + key + "' not present", new NodeContext(value))));
		}
	}

	private record NodeRule(String key, Rule rule) implements Rule {
		@Override
		public Result<Node, CompileError> lex(String content) {
			return rule.lex(content).map(node -> new Node().withNode(key, node));
		}

		@Override
		public Result<String, CompileError> generate(Node node) {
			return new Err<>(new CompileError("Cannot generate for node group '" + key + "'", new NodeContext(node)));
		}
	}

	private record TypeRule(String type, Rule rule) implements Rule {
		@Override
		public Result<Node, CompileError> lex(String content) {
			return rule.lex(content).map(node -> node.retype(type));
		}

		@Override
		public Result<String, CompileError> generate(Node node) {
			if (node.is(type)) return rule.generate(node);
			else return new Err<>(new CompileError("Type '" + type + "' not present", new NodeContext(node)));
		}
	}

	private record ThrowableError(Throwable e) implements Error {
		@Override
		public String display() {
			final StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}

	private record ApplicationError(Error error) implements Error {
		public String display() {
			return error.display();
		}
	}

	public static void main(String[] args) {
		extracted().ifPresent(error -> System.out.println(error.display()));
	}

	private static Optional<ApplicationError> extracted() {
		final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		return switch (readString(source)) {
			case Err<String, ThrowableError>(ThrowableError error) -> Optional.of(new ApplicationError(error));
			case Ok<String, ThrowableError>(String input) -> {
				final Result<String, CompileError> result = compile(input);
				yield switch (result) {
					case Err<String, CompileError> v -> Optional.of(new ApplicationError(v.error));
					case Ok<String, CompileError> v -> {
						final Path path = source.resolveSibling("main.c");
						yield writeString(path, v.value).map(ThrowableError::new).map(ApplicationError::new);
					}
				};
			}
		};
	}

	private static Optional<IOException> writeString(Path path, String result) {
		try {
			Files.writeString(path, result);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(e);
		}
	}

	private static Result<String, ThrowableError> readString(Path source) {
		try {
			return new Ok<>(Files.readString(source));
		} catch (IOException e) {
			return new Err<>(new ThrowableError(e));
		}
	}

	private static Result<String, CompileError> compile(String input) {
		return createJavaRootRule().lex(input).map(Main::transform).flatMap(createCRootRule()::generate);
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
