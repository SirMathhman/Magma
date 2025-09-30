package magma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
		String display(int depth);
	}

	private interface Error {
		String display();
	}

	private sealed interface JavaRootSegment permits JavaClass, JavaContent, JavaImport, JavaPackage {}

	private sealed interface CRootSegment permits CStructure, JavaContent {}

	private sealed interface JavaClassSegment permits JavaBlock, JavaClass, JavaContent, JavaStruct {}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Type {
		String value();
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
		public String display(int depth) {
			return context;
		}
	}

	private record NodeContext(Node node) implements Context {
		@Override
		public String display(int depth) {
			return node.format(depth);
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
			StringBuilder joiner = new StringBuilder();
			for (int i = 0; i < causes.size(); i++) {
				CompileError error = causes.get(i);
				String format = error.format(depth + 1, i);
				joiner.append(format);
			}

			final String formattedChildren = joiner.toString();
			final String s = depth == 0 ? "" : System.lineSeparator() + "\t".repeat(depth);
			return s + index + ") " + reason + ": " + context.display(depth) + formattedChildren;
		}
	}

	private static final class Node {
		private final Map<String, String> strings = new HashMap<>();
		private final Map<String, List<Node>> nodeLists = new HashMap<>();
		private final Map<String, Node> nodes = new HashMap<>();
		private Optional<String> maybeType = Optional.empty();

		private static String escape(String value) {
			return value.replace("\\", "\\\\")
									.replace("\"", "\\\"")
									.replace("\n", "\\n")
									.replace("\r", "\\r")
									.replace("\t", "\\t");
		}

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

		private String format(int depth) {
			StringBuilder builder = new StringBuilder();
			appendJson(builder, depth);
			return builder.toString();
		}

		private void appendJson(StringBuilder builder, int depth) {
			final String indent = "\t".repeat(depth);
			final String childIndent = "\t".repeat(depth + 1);
			builder.append(indent).append("{");
			boolean hasFields = false;

			if (maybeType.isPresent()) {
				builder.append("\n").append(childIndent).append("\"@type\": \"").append(escape(maybeType.get())).append("\"");
				hasFields = true;
			}

			final List<Map.Entry<String, String>> sortedStrings =
					strings.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList();
			for (Map.Entry<String, String> entry : sortedStrings) {
				builder.append(hasFields ? ",\n" : "\n");
				builder.append(childIndent)
							 .append('"')
							 .append(escape(entry.getKey()))
							 .append("\": \"")
							 .append(escape(entry.getValue()))
							 .append("\"");
				hasFields = true;
			}

			final List<Map.Entry<String, Node>> sortedNodes =
					nodes.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList();
			for (Map.Entry<String, Node> entry : sortedNodes) {
				builder.append(hasFields ? ",\n" : "\n");
				builder.append(childIndent).append('"').append(escape(entry.getKey())).append("\": ");
				entry.getValue().appendJson(builder, depth + 1);
				hasFields = true;
			}

			final List<Map.Entry<String, List<Node>>> sortedLists =
					nodeLists.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList();
			for (Map.Entry<String, List<Node>> entry : sortedLists) {
				builder.append(hasFields ? ",\n" : "\n");
				builder.append(childIndent).append('"').append(escape(entry.getKey())).append("\": [");
				List<Node> list = entry.getValue();
				if (!list.isEmpty()) {
					builder.append("\n");
					for (int i = 0; i < list.size(); i++) {
						builder.append("\t".repeat(depth + 2));
						list.get(i).appendJson(builder, depth + 2);
						if (i < list.size() - 1) builder.append(",\n");
						else builder.append("\n");
					}
					builder.append(childIndent);
				}
				builder.append("]");
				hasFields = true;
			}

			if (hasFields) builder.append("\n").append(indent);
			builder.append("}");
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

	private record JavaRoot(List<JavaRootSegment> children) {}

	private record CRoot(List<CRootSegment> children) {}

	@Type("class")
	private record JavaClass(String name, List<JavaClassSegment> children) implements JavaRootSegment, JavaClassSegment {}

	@Type("import")
	private record JavaImport(String content) implements JavaRootSegment {}

	@Type("package")
	private record JavaPackage(String content) implements JavaRootSegment {}

	@Type("content")
	private record JavaContent(String input) implements JavaRootSegment, JavaClassSegment, CRootSegment {}

	@Type("struct")
	private record JavaStruct(String name) implements JavaClassSegment {}

	@Type("block")
	private record JavaBlock(String header, String content) implements JavaClassSegment {}

	@Type("struct")
	private record CStructure(String name) implements CRootSegment {}

	public static void main(String[] args) {
		run().ifPresent(error -> System.out.println(error.display()));
	}

	private static Optional<ApplicationError> run() {
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
		return createJavaRootRule().lex(input).flatMap(Main::transform).flatMap(createCRootRule()::generate);
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

	private static <T> Result<T, CompileError> deserialize(Class<T> clazz, Node node) {
		if (clazz == null) return new Err<>(new CompileError("Target class must not be null", new NodeContext(node)));
		if (node == null)
			return new Err<>(new CompileError("Cannot deserialize null node", new StringContext(clazz.getName())));

		if (clazz.isSealed() && !clazz.isRecord()) return deserializeSealed(clazz, node);
		if (!clazz.isRecord()) return new Err<>(
				new CompileError("Unsupported deserialization target '" + clazz.getName() + "'", new NodeContext(node)));

		final Optional<String> expectedType = resolveTypeIdentifier(clazz);
		if (expectedType.isPresent()) {
			if (node.maybeType.isEmpty()) return new Err<>(
					new CompileError("Node type information missing for '" + clazz.getSimpleName() + "'", new NodeContext(node)));
			if (!node.is(expectedType.get())) return new Err<>(
					new CompileError("Expected node type '" + expectedType.get() + "' but found '" + node.maybeType.get() + "'",
													 new NodeContext(node)));
		}

		final RecordComponent[] components = clazz.getRecordComponents();
		final Object[] arguments = new Object[components.length];
		final ArrayList<CompileError> errors = new ArrayList<>();

		for (int i = 0; i < components.length; i++) {
			final RecordComponent component = components[i];
			final Result<Object, CompileError> componentResult = deserializeComponent(component, node);
			if (componentResult instanceof Ok<?, ?> ok) {
				@SuppressWarnings("unchecked")
				final Ok<Object, CompileError> valueOk = (Ok<Object, CompileError>) ok;
				arguments[i] = valueOk.value();
			} else if (componentResult instanceof Err<?, ?> err) {
				@SuppressWarnings("unchecked")
				final Err<Object, CompileError> valueErr = (Err<Object, CompileError>) err;
				errors.add(valueErr.error());
			}
		}

		if (!errors.isEmpty()) return new Err<>(
				new CompileError("Failed to deserialize '" + clazz.getSimpleName() + "'", new NodeContext(node), errors));

		try {
			final Class<?>[] parameterTypes = Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new);
			final Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
			if (!constructor.canAccess(null)) constructor.setAccessible(true);
			return new Ok<>(constructor.newInstance(arguments));
		} catch (ReflectiveOperationException e) {
			return new Err<>(new CompileError("Reflection failure while instantiating '" + clazz.getSimpleName() + "'",
																				new NodeContext(node),
																				List.of(new CompileError(e.getMessage(), new StringContext(clazz.getName())))));
		}
	}

	private static <T> Result<Node, CompileError> serialize(Class<T> clazz, T node) {
		if (clazz == null)
			return new Err<>(new CompileError("Target class must not be null", new StringContext("serialize")));
		if (node == null) return new Err<>(new CompileError("Cannot serialize null instance of '" + clazz.getName() + "'",
																												new StringContext("serialize")));

		if (clazz.isSealed() && !clazz.isRecord()) {
			@SuppressWarnings("unchecked")
			final Class<? extends T> concreteClass = (Class<? extends T>) node.getClass();
			if (!clazz.isAssignableFrom(concreteClass)) return new Err<>(new CompileError(
					"Instance of type '" + concreteClass.getName() + "' is not assignable to '" + clazz.getName() + "'",
					new StringContext(concreteClass.getName())));
			return serializeRaw(concreteClass, concreteClass.cast(node));
		}

		if (!clazz.isRecord()) return new Err<>(
				new CompileError("Unsupported serialization target '" + clazz.getName() + "'",
												 new StringContext(clazz.getName())));

		final Node result = new Node();
		resolveTypeIdentifier(clazz).ifPresent(result::retype);

		final RecordComponent[] components = clazz.getRecordComponents();
		final ArrayList<CompileError> errors = new ArrayList<>();

		for (RecordComponent component : components) {
			final Method accessor = component.getAccessor();
			try {
				final Object value = accessor.invoke(node);
				final Result<Void, CompileError> writeResult = writeComponent(result, component, value);
				if (writeResult instanceof Err<?, ?> err) {
					@SuppressWarnings("unchecked")
					final Err<Void, CompileError> writeErr = (Err<Void, CompileError>) err;
					errors.add(writeErr.error());
				}
			} catch (IllegalAccessException | InvocationTargetException e) {
				errors.add(new CompileError("Failed to read component '" + component.getName() + "'",
																		new StringContext(clazz.getName()),
																		List.of(new CompileError(e.getMessage(), new StringContext(component.getName())))));
			}
		}

		if (!errors.isEmpty()) return new Err<>(
				new CompileError("Failed to serialize '" + clazz.getSimpleName() + "'", new StringContext(clazz.getName()),
												 errors));

		return new Ok<>(result);
	}

	private static Optional<String> resolveTypeIdentifier(Class<?> clazz) {
		Type annotation = clazz.getAnnotation(Type.class);
		if (annotation == null) return Optional.empty();
		return Optional.of(annotation.value());
	}

	private static <T> Result<T, CompileError> deserializeSealed(Class<T> clazz, Node node) {
		if (node.maybeType.isEmpty()) return new Err<>(
				new CompileError("Missing node type for sealed type '" + clazz.getName() + "'", new NodeContext(node)));

		final String nodeType = node.maybeType.get();
		for (Class<?> permitted : clazz.getPermittedSubclasses()) {
			final Optional<String> identifier = resolveTypeIdentifier(permitted);
			if (identifier.isPresent() && identifier.get().equals(nodeType)) {
				@SuppressWarnings({"rawtypes", "unchecked"})
				final Result<T, CompileError> cast = (Result<T, CompileError>) deserialize((Class) permitted, node);
				return cast;
			}
		}

		return new Err<>(
				new CompileError("No permitted subtype of '" + clazz.getName() + "' matched node type '" + nodeType + "'",
												 new NodeContext(node)));
	}

	private static Result<Object, CompileError> deserializeComponent(RecordComponent component, Node node) {
		final String key = component.getName();
		final Class<?> type = component.getType();

		if (type == String.class) {
			final Optional<String> direct = node.findString(key);
			if (direct.isPresent()) return new Ok<>(direct.get());
			final Optional<String> nested = findStringInChildren(node, key);
			if (nested.isPresent()) return new Ok<>(nested.get());
			return new Err<>(missingFieldError(key, type, node));
		}

		if (List.class.isAssignableFrom(type)) return deserializeListComponent(component, node);

		return deserializeNestedComponent(component, node);
	}

	private static Result<Object, CompileError> deserializeListComponent(RecordComponent component, Node node) {
		final java.lang.reflect.Type genericType = component.getGenericType();
		if (!(genericType instanceof ParameterizedType parameterized) || parameterized.getActualTypeArguments().length != 1)
			return new Err<>(
					new CompileError("Component '" + component.getName() + "' must declare a single generic parameter",
													 new NodeContext(node)));

		final java.lang.reflect.Type argumentType = parameterized.getActualTypeArguments()[0];
		final Class<?> elementClass = erase(argumentType);
		final Optional<List<Node>> maybeList = node.findNodeList(component.getName());
		if (maybeList.isEmpty()) return new Err<>(missingFieldError(component.getName(), elementClass, node));

		final ArrayList<Object> results = new ArrayList<>();
		final ArrayList<CompileError> errors = new ArrayList<>();
		for (Node child : maybeList.get()) {
			final Result<Object, CompileError> childResult = deserializeRaw(elementClass, child);
			switch (childResult) {
				case Err<Object, CompileError> v -> errors.add(v.error);
				case Ok<Object, CompileError> v -> results.add(v.value);
			}
		}

		if (!errors.isEmpty()) return new Err<>(
				new CompileError("Failed to deserialize list component '" + component.getName() + "'", new NodeContext(node),
												 errors));

		return new Ok<>(List.copyOf(results));
	}

	private static Result<Object, CompileError> deserializeNestedComponent(RecordComponent component, Node node) {
		final String key = component.getName();
		final Optional<Node> maybeChild = node.findNode(key);
		if (maybeChild.isEmpty()) return new Err<>(missingFieldError(key, component.getType(), node));
		return deserializeRaw(component.getType(), maybeChild.get());
	}

	private static Result<Object, CompileError> deserializeRaw(Class<?> type, Node node) {
		@SuppressWarnings({"rawtypes", "unchecked"})
		final Result<Object, CompileError> rawResult = (Result<Object, CompileError>) deserialize((Class) type, node);
		return rawResult;
	}

	private static Result<Node, CompileError> serializeRaw(Class<?> clazz, Object value) {
		@SuppressWarnings({"rawtypes", "unchecked"})
		final Result<Node, CompileError> result = (Result<Node, CompileError>) serialize((Class) clazz, value);
		return result;
	}

	private static CompileError missingFieldError(String key, Class<?> type, Node node) {
		return new CompileError("Required component '" + key + "' of type '" + type.getSimpleName() + "' not present",
														new NodeContext(node));
	}

	private static Optional<String> findStringInChildren(Node node, String key) {
		for (Node child : node.nodes.values()) {
			final Optional<String> nested = child.findString(key).or(() -> findStringInChildren(child, key));
			if (nested.isPresent()) return nested;
		}
		for (List<Node> children : node.nodeLists.values())
			for (Node child : children) {
				final Optional<String> nested = child.findString(key).or(() -> findStringInChildren(child, key));
				if (nested.isPresent()) return nested;
			}
		return Optional.empty();
	}

	private static Result<Void, CompileError> writeComponent(Node target, RecordComponent component, Object value) {
		final String key = component.getName();
		final Class<?> type = component.getType();

		if (type == String.class) {
			if (value == null) return new Err<>(new CompileError("Component '" + key + "' was null", new StringContext(key)));
			target.withString(key, (String) value);
			return new Ok<>(null);
		}

		if (List.class.isAssignableFrom(type)) return writeListComponent(target, component, value);

		if (value == null) return new Err<>(new CompileError("Component '" + key + "' was null", new StringContext(key)));

		final Result<Node, CompileError> nestedResult = serializeRaw(type, value);

		return switch (nestedResult) {
			case Err<Node, CompileError> v -> new Err<>(v.error);
			case Ok<Node, CompileError> v -> {
				target.withNode(key, v.value);
				yield new Ok<>(null);
			}
		};
	}

	private static Result<Void, CompileError> writeListComponent(Node target, RecordComponent component, Object value) {
		if (value == null) return new Err<>(
				new CompileError("Component '" + component.getName() + "' was null", new StringContext(component.getName())));
		if (!(value instanceof List<?> listValue)) return new Err<>(
				new CompileError("Component '" + component.getName() + "' is not a List instance",
												 new StringContext(component.getName())));

		final java.lang.reflect.Type genericType = component.getGenericType();
		if (!(genericType instanceof ParameterizedType parameterized) || parameterized.getActualTypeArguments().length != 1)
			return new Err<>(
					new CompileError("Component '" + component.getName() + "' must declare a single generic parameter",
													 new StringContext(component.getName())));

		final Class<?> elementClass = erase(parameterized.getActualTypeArguments()[0]);
		final ArrayList<Node> serializedChildren = new ArrayList<>();
		final ArrayList<CompileError> errors = new ArrayList<>();

		for (Object element : listValue) {
			final Result<Node, CompileError> serialized = serializeRaw(elementClass, element);
			switch (serialized) {
				case Err<Node, CompileError> v -> errors.add(v.error);
				case Ok<Node, CompileError> v -> serializedChildren.add(v.value);
			}
		}

		if (!errors.isEmpty()) return new Err<>(
				new CompileError("Failed to serialize list component '" + component.getName() + "'",
												 new StringContext(component.getName()), errors));

		target.withNodeList(component.getName(), List.copyOf(serializedChildren));
		return new Ok<>(null);
	}

	private static Class<?> erase(java.lang.reflect.Type type) {
		if (type instanceof Class<?> clazz) return clazz;
		if (type instanceof ParameterizedType parameterized && parameterized.getRawType() instanceof Class<?> raw)
			return raw;
		throw new IllegalArgumentException("Cannot erase type '" + type + "'");
	}

	private static Result<Node, CompileError> transform(Node node) {
		return switch (deserialize(JavaRoot.class, node)) {
			case Err<JavaRoot, CompileError> v -> new Err<>(v.error);
			case Ok<JavaRoot, CompileError> v -> getNodeCompileErrorResult(v.value).flatMap(n -> serialize(CRoot.class, n));
		};
	}

	private static Result<CRoot, CompileError> getNodeCompileErrorResult(JavaRoot value) {
		final List<CRootSegment> newChildren = value.children.stream().flatMap(segment -> switch (segment) {
			case JavaClass javaClass -> flattenClass(javaClass);
			case JavaContent content -> Stream.of(content);
			case JavaImport _, JavaPackage _ -> Stream.of();
		}).toList();
		return new Ok<>(new CRoot(newChildren));
	}

	private static Stream<CRootSegment> flattenClass(JavaClass clazz) {
		final Stream<CRootSegment> nested = clazz.children.stream().flatMap(member -> switch (member) {
			case JavaClass javaClass -> flattenClass(javaClass);
			case JavaStruct struct -> Stream.of(new CStructure(struct.name()));
			case JavaContent content -> Stream.of(content);
			case JavaBlock block -> {
				block.header();
				block.content();
				yield Stream.of();
			}
		});

		return Stream.concat(Stream.of(new CStructure(clazz.name())), nested);
	}

	private static Rule createClassRule() {
		final NodeRule header = new NodeRule("header", createClassHeaderRule());
		final DivideRule children = new DivideRule("children", createJavaClassSegmentRule());
		return new TypeRule("class", new SuffixRule(new InfixRule(header, "{", children), "}"));
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
		return new TypeRule("block", new SuffixRule(new InfixRule(new PlaceholderRule(new StringRule("header")), "{",
																															new PlaceholderRule(new StringRule("content"))), "}"));
	}

	private static Rule createStructHeaderRule() {
		return new TypeRule("struct", new PrefixRule("struct ", new SuffixRule(new StringRule("name"), " {};")));
	}

	private static Rule createClassHeaderRule() {
		return new InfixRule(new StringRule("temp"), "class ", new StripRule(new StringRule("name")));
	}
}
