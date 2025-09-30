package magma;

import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.Type;
import magma.compile.context.NodeContext;
import magma.compile.context.StringContext;
import magma.compile.error.ApplicationError;
import magma.compile.error.CompileError;
import magma.compile.error.ThrowableError;
import magma.compile.rule.DivideRule;
import magma.compile.rule.InfixRule;
import magma.compile.rule.NodeRule;
import magma.compile.rule.OrRule;
import magma.compile.rule.PlaceholderRule;
import magma.compile.rule.PrefixRule;
import magma.compile.rule.Rule;
import magma.compile.rule.StringRule;
import magma.compile.rule.StripRule;
import magma.compile.rule.SuffixRule;
import magma.compile.rule.TypeRule;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class Main {
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
					case Err<String, CompileError> v -> Optional.of(new ApplicationError(v.error()));
					case Ok<String, CompileError> v -> {
						final Path path = source.resolveSibling("main.c");
						yield writeString(path, v.value()).map(ThrowableError::new).map(ApplicationError::new);
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
		if (Objects.isNull(clazz))
			return new Err<>(new CompileError("Target class must not be absent", new NodeContext(node)));
		if (Objects.isNull(node))
			return new Err<>(new CompileError("Cannot deserialize absent node", new StringContext(clazz.getName())));

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
			if (componentResult instanceof Ok<Object, CompileError>(Object value)) arguments[i] = value;
			else if (componentResult instanceof Err<Object, CompileError>(CompileError error)) errors.add(error);
		}

		if (!errors.isEmpty()) return new Err<>(
				new CompileError("Failed to deserialize '" + clazz.getSimpleName() + "'", new NodeContext(node), errors));

		try {
			final Class<?>[] parameterTypes = Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new);
			final Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
			// Ensure constructor is accessible for reflection
			constructor.setAccessible(true);
			return new Ok<>(constructor.newInstance(arguments));
		} catch (ReflectiveOperationException e) {
			return new Err<>(new CompileError("Reflection failure while instantiating '" + clazz.getSimpleName() + "'",
																				new NodeContext(node),
																				List.of(new CompileError(e.getMessage(), new StringContext(clazz.getName())))));
		}
	}

	private static <T> Result<Node, CompileError> serialize(Class<T> clazz, T node) {
		if (Objects.isNull(clazz))
			return new Err<>(new CompileError("Target class must not be absent", new StringContext("serialize")));
		if (Objects.isNull(node)) return new Err<>(
				new CompileError("Cannot serialize absent instance of '" + clazz.getName() + "'",
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
				final Optional<CompileError> writeResult = writeComponent(result, component, value);
				writeResult.ifPresent(errors::add);
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
		if (Objects.isNull(annotation)) return Optional.empty();
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
				case Err<Object, CompileError> v -> errors.add(v.error());
				case Ok<Object, CompileError> v -> results.add(v.value());
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

	private static Optional<CompileError> writeComponent(Node target, RecordComponent component, Object value) {
		final String key = component.getName();
		final Class<?> type = component.getType();

		final CompileError absentError = new CompileError("Component '" + key + "' was absent", new StringContext(key));
		if (type == String.class) {
			if (Objects.isNull(value)) return Optional.of(absentError);
			target.withString(key, (String) value);
			return Optional.empty();
		}

		if (List.class.isAssignableFrom(type)) return writeListComponent(target, component, value);

		if (Objects.isNull(value)) return Optional.of(absentError);

		final Result<Node, CompileError> nestedResult = serializeRaw(type, value);

		return switch (nestedResult) {
			case Err<Node, CompileError> v -> Optional.of(v.error());
			case Ok<Node, CompileError> v -> {
				target.withNode(key, v.value());
				yield Optional.empty();
			}
		};
	}

	private static Optional<CompileError> writeListComponent(Node target, RecordComponent component, Object value) {
		if (Objects.isNull(value)) return Optional.of(
				new CompileError("Component '" + component.getName() + "' was absent", new StringContext(component.getName())));
		if (!(value instanceof List<?> listValue)) return Optional.of(
				new CompileError("Component '" + component.getName() + "' is not a List instance",
												 new StringContext(component.getName())));

		final java.lang.reflect.Type genericType = component.getGenericType();
		if (!(genericType instanceof ParameterizedType parameterized) || parameterized.getActualTypeArguments().length != 1)
			return Optional.of(
					new CompileError("Component '" + component.getName() + "' must declare a single generic parameter",
													 new StringContext(component.getName())));

		final Class<?> elementClass = erase(parameterized.getActualTypeArguments()[0]);
		final ArrayList<Node> serializedChildren = new ArrayList<>();
		final ArrayList<CompileError> errors = new ArrayList<>();

		for (Object element : listValue) {
			final Result<Node, CompileError> serialized = serializeRaw(elementClass, element);
			switch (serialized) {
				case Err<Node, CompileError> v -> errors.add(v.error());
				case Ok<Node, CompileError> v -> serializedChildren.add(v.value());
			}
		}

		if (!errors.isEmpty()) return Optional.of(
				new CompileError("Failed to serialize list component '" + component.getName() + "'",
												 new StringContext(component.getName()), errors));

		target.withNodeList(component.getName(), List.copyOf(serializedChildren));
		return Optional.empty();
	}

	private static Class<?> erase(java.lang.reflect.Type type) {
		if (type instanceof Class<?> clazz) return clazz;
		if (type instanceof ParameterizedType parameterized && parameterized.getRawType() instanceof Class<?> raw)
			return raw;
		throw new IllegalArgumentException("Cannot erase type '" + type + "'");
	}

	private static Result<Node, CompileError> transform(Node node) {
		return switch (deserialize(Lang.JavaRoot.class, node)) {
			case Err<Lang.JavaRoot, CompileError> v -> new Err<>(v.error());
			case Ok<Lang.JavaRoot, CompileError> v ->
					getNodeCompileErrorResult(v.value()).flatMap(n -> serialize(Lang.CRoot.class, n));
		};
	}

	private static Result<Lang.CRoot, CompileError> getNodeCompileErrorResult(Lang.JavaRoot value) {
		final List<Lang.CRootSegment> newChildren = value.children().stream().flatMap(segment -> switch (segment) {
			case Lang.JavaClass javaClass -> flattenClass(javaClass);
			case Lang.Content content -> Stream.of(content);
			case Lang.JavaImport _, Lang.JavaPackage _ -> Stream.of();
		}).toList();
		return new Ok<>(new Lang.CRoot(newChildren));
	}

	private static Stream<Lang.CRootSegment> flattenClass(Lang.JavaClass clazz) {
		final Stream<Lang.CRootSegment> nested = clazz.children().stream().flatMap(member -> switch (member) {
			case Lang.JavaClass javaClass -> flattenClass(javaClass);
			case Lang.JavaStruct struct -> Stream.of(new Lang.CStructure(struct.name()));
			case Lang.Content content -> Stream.of(content);
			case Lang.JavaBlock _ -> Stream.of();
		});

		return Stream.concat(Stream.of(new Lang.CStructure(clazz.name())), nested);
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
