package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Main {
	private enum JPrimitiveType implements JType {
		Void(CPrimitiveType.Void), String(new CPointerType(CPrimitiveType.Char));

		private final CType cType;

		JPrimitiveType(CType cType) {this.cType = cType;}

		@Override
		public CType toCType() {
			return this.cType;
		}
	}

	private enum CPrimitiveType implements CType {
		Char("char"), Void("void");

		private final String content;

		CPrimitiveType(String content) {this.content = content;}

		@Override
		public String generate() {
			return this.content;
		}
	}

	private sealed interface Result<T, X> permits Err, Ok {}

	private sealed interface CType
			permits CFunctionType, CIdentifier, CPlaceholder, CPointerType, CPrimitiveType, CStructureType, CTemplateType {
		String generate();
	}

	private sealed interface CDefinable permits CDefinition, CPlaceholder {
		String generate();
	}

	private sealed interface JMethodHeader permits JConstructor, JDefinition, JPlaceholder {
		CDefinable toCDefinition();
	}

	private interface JType {
		CType toCType();

		default List<String> findTypeParameters() {
			return new List<String>();
		}

		default JType remap(Map<String, JType> mapping) {
			return this;
		}
	}

	private sealed interface JExpression permits JIdentifier, JInvocation, JMemberAccess, JPlaceholder {
		CExpression toCExpression();
	}

	private sealed interface CExpression permits CFieldAccess, CIdentifier, CInvocation, CPlaceholder {
		String generate();
	}

	private interface Head<T> {
		Optional<T> next();
	}

	private interface Collector<T, C> {
		C createInitial();

		C fold(C current, T element);
	}

	private sealed interface JClassSegment permits JClassSegmentWrapper, JPlaceholder {
		CStructureSegment toCStructureSegment();
	}

	private sealed interface JIncompleteClassSegment permits JClassSegmentWrapper, JIncompleteMethod, JPlaceholder {
		JClassSegment complete();

		Optional<JDefinition> createDefinition();
	}

	private interface CStructureSegment {
		String generate();
	}

	private record Stream<T>(Head<T> head) {
		public static <T> Stream<T> fromOptional(Optional<T> optional) {
			return new Stream<T>(optional.<Head<T>>map(SingleHead::new).orElseGet(EmptyHead::new));
		}

		public static <T> Stream<T> fromArray(T[] array) {
			return new Stream<Integer>(new LengthHead(array.length)).map(index -> array[index]);
		}

		public Stream<T> concat(Stream<T> second) {
			return new Stream<T>(() -> this.next().or(second::next));
		}

		private Optional<T> next() {
			return this.head.next();
		}

		public <R> Stream<R> map(Function<T, R> mapper) {
			return new Stream<R>(new MapHead<T, R>(this.head, mapper));
		}

		public <C> C collect(Collector<T, C> collector) {
			return this.fold(collector.createInitial(), collector::fold);
		}

		public <R> R fold(R initial, BiFunction<R, T, R> folder) {
			var current = initial;
			while (true) {
				final var maybeNext = this.head.next();
				if (maybeNext.isPresent()) {
					final var next = maybeNext.get();
					current = folder.apply(current, next);
				} else {
					return current;
				}
			}
		}

		public List<T> toList() {
			return this.collect(new ListCollector<T>());
		}

		public Stream<T> filter(Predicate<T> predicate) {
			return this.flatMap(element -> {
				if (predicate.test(element)) {
					return new Stream<T>(new SingleHead<T>(element));
				}
				return new Stream<T>(new EmptyHead<T>());
			});
		}

		private <R> Stream<R> flatMap(Function<T, Stream<R>> mapper) {
			return new Stream<R>(new FlatMapHead<T, R>(this.head, mapper));
		}

		public Optional<T> findFirst() {
			return this.head.next();
		}
	}

	private static final class LengthHead implements Head<Integer> {
		private final int length;
		private int counter = 0;

		public LengthHead(int length) {this.length = length;}

		@Override
		public Optional<Integer> next() {
			if (this.counter < this.length) {
				final var value = this.counter;
				this.counter++;
				return Optional.of(value);
			} else {
				return Optional.empty();
			}
		}
	}

	private record List<T>(java.util.List<T> nativeList) {
		public List() {
			this(new ArrayList<T>());
		}

		@SafeVarargs
		public static <T> List<T> of(T... elements) {
			return new List<T>(new ArrayList<T>(Arrays.asList(elements)));
		}

		public void forEach(Consumer<T> consumer) {
			this.nativeList.forEach(consumer);
		}

		public Stream<T> stream() {
			return new Stream<Integer>(new LengthHead(this.nativeList.size())).map(this.nativeList::get);
		}

		public List<T> addLast(T definition) {
			this.nativeList.addLast(definition);
			return this;
		}

		public List<T> reversed() {
			return new List<T>(this.nativeList.reversed());
		}

		public int size() {
			return this.nativeList.size();
		}

		public T get(int index) {
			return this.nativeList.get(index);
		}

		public T getLast() {
			return this.nativeList.getLast();
		}

		public List<T> removeLast() {
			this.nativeList.removeLast();
			return this;
		}

		public List<T> set(int index, T element) {
			this.nativeList.set(index, element);
			return this;
		}

		public boolean isEmpty() {
			return this.nativeList.isEmpty();
		}

		public List<T> addFirst(T element) {
			this.nativeList.addFirst(element);
			return this;
		}

		public List<T> mapLast(Function<T, T> mapper) {
			this.nativeList.set(this.nativeList.size() - 1, mapper.apply(this.nativeList.getLast()));
			return this;
		}

		public List<T> addAllLast(List<T> others) {
			return others.stream().fold(this, List::addLast);
		}

		public boolean contains(T element) {
			return this.nativeList.contains(element);
		}
	}

	private record Err<T, X>(X error) implements Result<T, X> {}

	private record Ok<T, X>(T value) implements Result<T, X> {}

	private record CPointerType(CType child) implements CType {
		@Override
		public String generate() {
			return this.child.generate() + "*";
		}
	}

	private record CTemplateType(String base, List<CType> typeArguments) implements CType {
		@Override
		public String generate() {
			final var cTemplateType = this;
			final var stream = cTemplateType.typeArguments.stream();
			final var stringStream = stream.map(CType::generate);
			final var joined = stringStream.collect(new Collectors.Joiner(", "));
			return this.base + "<" + joined + ">";
		}
	}

	static class Collectors {
		private static class Joiner implements Collector<String, String> {
			private final String delimiter;

			public Joiner(String delimiter) {this.delimiter = delimiter;}

			@Override
			public String createInitial() {
				return "";
			}

			@Override
			public String fold(String current, String element) {
				if (current.isEmpty()) {
					return element;
				}
				return current + this.delimiter + element;
			}
		}
	}

	private record CIdentifier(String input) implements CType, CExpression {
		@Override
		public String generate() {
			return this.input;
		}
	}

	private record CPlaceholder(String input) implements CType, CDefinable, CExpression, CStructureSegment {
		private static String wrap(String input) {
			final var replaced = input.replace("/*", "start").replace("*/", "end");
			return "/*" + replaced + "*/";
		}

		@Override
		public String generate() {
			return wrap(this.input);
		}
	}

	private record CDefinition(CType type, String name) implements CDefinable {
		public String generate() {
			return this.type.generate() + " " + this.name;
		}
	}

	private record JDefinition(Optional<String> beforeType, JType type, String name) implements JMethodHeader {
		@Override
		public CDefinable toCDefinition() {
			return new CDefinition(this.type.toCType(), this.name);
		}

		public JDefinition mapType(Function<JType, JType> mapper) {
			return new JDefinition(this.beforeType, mapper.apply(this.type), this.name);
		}
	}

	private record JConstructor(String input) implements JMethodHeader {
		@Override
		public CDefinable toCDefinition() {
			final var type = new CIdentifier(this.input);
			return new CDefinition(type, "new_" + this.input);
		}
	}

	private record JPlaceholder(String input)
			implements JMethodHeader, JType, JExpression, JIncompleteClassSegment, JClassSegment {
		@Override
		public CDefinable toCDefinition() {
			return new CPlaceholder(this.input);
		}

		@Override
		public CType toCType() {
			return new CPlaceholder(this.input);
		}

		@Override
		public CExpression toCExpression() {
			return new CPlaceholder(this.input);
		}

		@Override
		public JClassSegment complete() {
			return new JPlaceholder(this.input);
		}

		@Override
		public Optional<JDefinition> createDefinition() {
			return Optional.empty();
		}

		@Override
		public CStructureSegment toCStructureSegment() {
			return new CPlaceholder(this.input);
		}
	}

	private record JArrayType(JType type) implements JType {
		@Override
		public CType toCType() {
			return new CPointerType(this.type.toCType());
		}
	}

	private record JGenericType(String base, List<JType> typeArguments) implements JType {
		@Override
		public CType toCType() {
			return new CTemplateType(this.base, this.typeArguments.stream().map(JType::toCType).toList());
		}
	}

	private record JIdentifier(String value) implements JType, JExpression {
		@Override
		public CType toCType() {
			return new CIdentifier(this.value);
		}

		@Override
		public CExpression toCExpression() {
			return new CIdentifier(this.value);
		}
	}

	private record CFieldAccess(CExpression child, String name) implements CExpression {
		@Override
		public String generate() {
			return this.child.generate() + "." + this.name;
		}
	}

	private record JMemberAccess(JExpression child, String name) implements JExpression {
		@Override
		public CExpression toCExpression() {
			if (this.child instanceof JIdentifier(String enumName)) {
				if (enumNames.contains(enumName)) {
					return new CIdentifier(enumName + "_" + this.name);
				}
			}

			return new CFieldAccess(this.child.toCExpression(), this.name);
		}
	}

	private static final class Frame {
		private final Optional<String> maybeStructureName;
		private final Map<String, JType> definedTypes;
		private List<JDefinition> definedMembers;

		private Frame(Optional<String> maybeStructureName,
									List<JDefinition> definedMembers,
									Map<String, JType> definedTypes) {
			this.maybeStructureName = maybeStructureName;
			this.definedMembers = definedMembers;
			this.definedTypes = definedTypes;
		}

		public Frame() {
			this(Optional.empty(), new List<JDefinition>(), new HashMap<String, JType>());
		}

		private Optional<JClassType> toClassType() {
			return this.maybeStructureName.map(structureName -> new JClassType(structureName, this.definedMembers));
		}

		public void defineAllExpressions(List<JDefinition> definitions) {
			definitions.forEach(this::defineExpression);
		}

		public void defineExpression(JDefinition definition) {
			assert !this.isVar(definition);
			this.definedMembers = this.definedMembers.addLast(definition);
		}

		private boolean isVar(JDefinition definition) {
			final var type = definition.type;
			return type instanceof JIdentifier(var value) && value.equals("var");
		}

		public Frame withStructureName(String structureName) {
			return new Frame(Optional.of(structureName), this.definedMembers, this.definedTypes);
		}

		public Optional<JType> resolveType(String key) {
			return Optional.ofNullable(this.definedTypes.get(key));
		}

		public Frame defineType(String key, JType type) {
			this.definedTypes.put(key, type);
			return this;
		}
	}

	private record CStructureType(String name, List<CDefinition> fields) implements CType {
		@Override
		public String generate() {
			return this.name;
		}
	}

	private record JClassType(String name, List<JDefinition> members) implements JType {
		@Override
		public CType toCType() {
			return new CStructureType(this.name,
																this.members
																		.stream()
																		.map(definition -> new CDefinition(definition.type.toCType(), definition.name))
																		.toList());
		}

		public Optional<JType> resolve(String name) {
			return this.members
					.stream()
					.filter(definition -> definition.name.equals(name))
					.map(definition -> definition.type)
					.findFirst();
		}

		public JClassType attachMembers(List<JDefinition> otherMembers) {
			return new JClassType(this.name, this.members.addAllLast(otherMembers));
		}
	}

	private static final class Scope {
		private List<Frame> frames;

		private Scope(List<Frame> frames) {this.frames = frames;}

		public Scope() {
			this(new List<Frame>().addLast(new Frame()));
		}

		private Optional<JType> resolveExpression(String input) {
			if (input.equals("this")) {
				return this.getThisType().map(type -> type);
			}

			return this.frames
					.reversed()
					.stream()
					.map(frame -> frame.definedMembers.stream().filter(definition -> definition.name.equals(input)).findFirst())
					.flatMap(Stream::fromOptional)
					.map(JDefinition::type)
					.findFirst()
					.map(this::finalizeType);
		}

		private Optional<JClassType> getThisType() {
			return this.frames
					.reversed()
					.stream()
					.map(Frame::toClassType)
					.flatMap(Stream::fromOptional)
					.findFirst()
					.map(value -> value);
		}

		private JType finalizeType(JType type) {
			if (type instanceof JGenericType(var base, var typeArguments)) {
				final var maybeResolved = scope.resolveType(base);
				if (maybeResolved.isPresent()) {
					final var jType = maybeResolved.get();
					final var typeParameters = jType.findTypeParameters();
					final var mapping = this.createMapping(typeArguments, typeParameters);
					return jType.remap(mapping);
				} else {
					return new JPlaceholder("Unknown base generic type: " + base);
				}
			} else {
				return type;
			}
		}

		private Map<String, JType> createMapping(List<JType> typeArguments, List<String> typeParameters) {
			final var mapping = new HashMap<String, JType>();
			for (var i = 0; i < typeParameters.size(); i++) {
				final var typeParameter = typeParameters.get(i);
				final var typeArgument = typeArguments.get(i);
				mapping.put(typeParameter, typeArgument);
			}
			return mapping;
		}

		private Optional<JType> resolveType(String key) {
			return this.frames
					.reversed()
					.stream()
					.map(frame -> frame.resolveType(key))
					.flatMap(Stream::fromOptional)
					.findFirst();
		}

		public Scope enter() {
			this.frames = this.frames.addLast(new Frame());
			return this;
		}

		public Scope defineAll(List<JDefinition> definitions) {
			this.frames.getLast().defineAllExpressions(definitions);
			return this;
		}

		public Scope exit() {
			this.frames = this.frames.removeLast();
			return this;
		}

		public Scope define(JDefinition definition) {
			this.frames.getLast().defineExpression(definition);
			return this;
		}

		public Scope withStructureName(String name) {
			this.frames = this.frames.set(this.frames.size() - 1, this.frames.getLast().withStructureName(name));
			return this;
		}

		public String getCurrentStructName() {
			return this.frames
					.reversed()
					.stream()
					.map(frame -> frame.maybeStructureName)
					.flatMap(Stream::fromOptional)
					.findFirst()
					.orElse("?");
		}

		public Scope defineType(String name, JType type) {
			this.frames = this.frames.mapLast(last -> last.defineType(name, type));
			return this;
		}
	}

	private record CInvocation(CExpression cExpression, List<CExpression> arguments) implements CExpression {
		@Override
		public String generate() {
			final var joined = this.arguments.stream().map(CExpression::generate).collect(new Collectors.Joiner(", "));
			return this.cExpression.generate() + "(" + joined + ")";
		}
	}

	private record JInvocation(JExpression caller, List<JExpression> arguments) implements JExpression {
		@Override
		public CExpression toCExpression() {
			return new CInvocation(this.caller.toCExpression(),
														 this.arguments.stream().map(JExpression::toCExpression).toList());
		}
	}

	private record CFunctionType(CType returnType, List<CType> paramTypes) implements CType {
		@Override
		public String generate() {
			final var joinedParameterTypes =
					this.paramTypes.stream().map(CType::generate).collect(new Collectors.Joiner(", "));
			return this.returnType.generate() + " (*)(" + joinedParameterTypes + ")";
		}
	}

	private record JMethodType(JType returnType, List<JType> paramTypes) implements JType {
		@Override
		public CType toCType() {
			return new CFunctionType(this.returnType.toCType(), this.paramTypes.stream().map(JType::toCType).toList());
		}
	}

	private record AllMatch<T>(Predicate<T> predicate) implements Collector<T, Boolean> {
		@Override
		public Boolean createInitial() {
			return true;
		}

		@Override
		public Boolean fold(Boolean current, T element) {
			return current && this.predicate.test(element);
		}
	}

	private record MapHead<T, R>(Head<T> head, Function<T, R> mapper) implements Head<R> {
		@Override
		public Optional<R> next() {
			return this.head.next().map(this.mapper);
		}
	}

	private static class ListCollector<T> implements Collector<T, List<T>> {
		@Override
		public List<T> createInitial() {
			return new List<T>();
		}

		@Override
		public List<T> fold(List<T> current, T element) {
			return current.addLast(element);
		}
	}

	private static class SingleHead<T> implements Head<T> {
		private final T element;
		private boolean retrieved = false;

		public SingleHead(T element) {
			this.element = element;
		}

		@Override
		public Optional<T> next() {
			if (this.retrieved) {
				return Optional.empty();
			}

			this.retrieved = true;
			return Optional.of(this.element);
		}
	}

	private static class EmptyHead<T> implements Head<T> {
		@Override
		public Optional<T> next() {
			return Optional.empty();
		}
	}

	private static class FlatMapHead<T, R> implements Head<R> {
		private final Head<T> head;
		private final Function<T, Stream<R>> mapper;
		private Stream<R> current;

		public FlatMapHead(Head<T> head, Function<T, Stream<R>> mapper) {
			this.head = head;
			this.mapper = mapper;
			this.current = head.next().map(mapper).orElseGet(() -> new Stream<R>(new EmptyHead<R>()));
		}

		@Override
		public Optional<R> next() {
			while (true) {
				final var maybeNext = this.current.next();
				if (maybeNext.isPresent()) {
					return maybeNext;
				}

				final var nextHead = this.head.next();
				if (nextHead.isEmpty()) {
					return Optional.empty();
				}

				this.current = this.mapper.apply(nextHead.get());
			}
		}
	}

	private record CStructureSegmentWrapper(String output) implements CStructureSegment {
		@Override
		public String generate() {
			return this.output;
		}
	}

	private record JClassSegmentWrapper(String output) implements JIncompleteClassSegment, JClassSegment {
		@Override
		public JClassSegment complete() {
			return new JClassSegmentWrapper(this.output);
		}

		@Override
		public Optional<JDefinition> createDefinition() {
			return Optional.empty();
		}

		@Override
		public CStructureSegment toCStructureSegment() {
			return new CStructureSegmentWrapper(this.output);
		}
	}

	private record CFunction(CDefinable header, List<CDefinable> cParameters, String content) {
		private String generate() {
			return this.header().generate() + "(" +
						 this.cParameters().stream().map(CDefinable::generate).collect(new Collectors.Joiner(", ")) + ")" +
						 this.content + System.lineSeparator();
		}
	}

	private record JIncompleteMethod(JMethodHeader header, List<JDefinition> parameters, String content)
			implements JIncompleteClassSegment {

		@Override
		public JClassSegment complete() {
			final var function = this.completeWithParameters();
			functions = functions.addLast(function);
			return new JClassSegmentWrapper("");
		}

		@Override
		public Optional<JDefinition> createDefinition() {
			final var parameterTypes = this.parameters.stream().map(definition -> definition.type).toList();

			if (this.header instanceof JDefinition definition) {
				return Optional.of(definition.mapType(type -> new JMethodType(type, parameterTypes)));
			} else {
				return Optional.empty();
			}
		}

		private CFunction completeWithParameters() {
			var cParameters = this.parameters.stream().map(JDefinition::toCDefinition).toList();
			CDefinable outputDefinition;
			if (this.header instanceof JDefinition jDefinition) {
				cParameters = cParameters.addFirst(new CDefinition(new CPointerType(CPrimitiveType.Void), "_ref"));
				outputDefinition =
						new CDefinition(jDefinition.type.toCType(), jDefinition.name + "_" + scope.getCurrentStructName());
			} else {
				outputDefinition = this.header.toCDefinition();
			}

			var withBraces = this.content();
			if (!withBraces.startsWith("{") || !withBraces.endsWith("}")) {
				return new CFunction(outputDefinition, cParameters, ";");
			}

			final var content1 = withBraces.substring(1, withBraces.length() - 1);

			scope = scope.enter().defineAll(this.parameters()).enter();
			final var compiledContent = compileStatements(content1, Main::compileMethodSegment);
			scope = scope.exit().exit();

			final String outputContent;
			if (this.header instanceof JConstructor(var name)) {
				outputContent = generateStatement(name + " this") + compiledContent + generateStatement("return this");
			} else if (this.header instanceof JDefinition) {
				outputContent = generateDereferenceThis(scope.getCurrentStructName()) + compiledContent;
			} else {
				outputContent = compiledContent;
			}

			final var contentWithBraces = " {" + outputContent + System.lineSeparator() + "}";
			return new CFunction(outputDefinition, cParameters, contentWithBraces);
		}
	}

	public static final List<String> enumNames = new List<String>();
	private static List<String> structures = new List<String>();
	private static List<CFunction> functions = new List<CFunction>();
	private static List<String> globals = new List<String>();
	private static Scope scope = new Scope();

	public static void main(String[] args) {
		run().ifPresent(Throwable::printStackTrace);
	}

	private static Optional<IOException> run() {
		final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		return switch (readString(source)) {
			case Ok(var input) -> {
				final var target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");
				final var output = compile(input);
				yield writeString(target, output);
			}
			case Err<String, IOException> v -> Optional.of(v.error);
		};
	}

	private static Optional<IOException> writeString(Path target, String output) {
		try {
			Files.writeString(target, output);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(e);
		}
	}

	private static Result<String, IOException> readString(Path source) {
		try {
			return new Ok<String, IOException>(Files.readString(source));
		} catch (IOException e) {
			return new Err<String, IOException>(e);
		}
	}

	private static String compile(String input) {
		scope = scope.enter();

		final var compiled = compileStatements(input, Main::compileRootSegment);
		final var joinedGlobals = globals.stream().collect(new Collectors.Joiner(""));
		final var joinedStructures = structures.stream().collect(new Collectors.Joiner(""));
		final var joinedFunctions = functions.stream().map(CFunction::generate).collect(new Collectors.Joiner(""));
		return joinedGlobals + joinedStructures + joinedFunctions + compiled;
	}

	private static String compileStatements(String input, Function<String, String> mapper) {
		return divide(input).map(mapper).collect(new Collectors.Joiner(""));
	}

	private static Stream<String> divide(String input) {
		var segments = new List<String>();
		var buffer = new StringBuilder();
		var depth = 0;
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments = segments.addLast(buffer.toString());
				buffer = new StringBuilder();
			} else if (c == '}' && depth == 1) {
				segments = segments.addLast(buffer.toString());
				buffer = new StringBuilder();
				depth--;
			} else if (c == '{') {
				depth++;
			} else if (c == '}') {
				depth--;
			}
		}
		segments = segments.addLast(buffer.toString());
		return segments.stream();
	}

	private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return compileStructure(stripped, "class")
				.map(JClassSegmentWrapper::complete)
				.map(JClassSegment::toCStructureSegment)
				.map(CStructureSegment::generate)
				.orElseGet(() -> CPlaceholder.wrap(stripped));
	}

	private static Optional<JClassSegmentWrapper> compileStructure(String stripped, String type) {
		final var i = stripped.indexOf(type + " ");
		if (i >= 0) {
			final var substring = stripped.substring(i + (type + " ").length()).strip();
			if (substring.endsWith("}")) {
				final var substring1 = substring.substring(0, substring.length() - 1);
				final var i1 = substring1.indexOf("{");
				if (i1 >= 0) {
					var beforeContent = substring1.substring(0, i1).strip();
					final var content = substring1.substring(i1 + 1).strip();

					final var i2 = beforeContent.indexOf("permits");
					var variants = new List<String>();
					if (i2 >= 0) {
						final var stripped1 = beforeContent.substring(i2 + "permits".length()).strip().split(Pattern.quote(","));

						variants = Stream.fromArray(stripped1).map(String::strip).filter(segment -> !segment.isEmpty()).toList();

						beforeContent = beforeContent.substring(0, i2).strip();
					}

					Optional<JType> maybeImplements = Optional.empty();
					final var i4 = beforeContent.indexOf("implements ");
					if (i4 >= 0) {
						final var substring2 = beforeContent.substring(i4 + "implements ".length());
						maybeImplements = Optional.of(parseType(substring2.strip()));

						beforeContent = beforeContent.substring(0, i4).strip();
					}

					var recordFields = new List<JDefinition>();
					if (beforeContent.endsWith(")")) {
						final var substring2 = beforeContent.substring(0, beforeContent.length() - 1);
						final var i3 = substring2.indexOf("(");
						if (i3 >= 0) {
							final var substring4 = substring2.substring(i3 + 1);
							recordFields = Stream
									.fromArray(substring4.split(Pattern.quote(",")))
									.map(String::strip)
									.filter(slice -> !slice.isEmpty())
									.map(Main::parseDefinition)
									.flatMap(Stream::fromOptional)
									.toList();

							beforeContent = substring2.substring(0, i3);
						}
					}

					var typeParameters = new List<String>();
					if (beforeContent.endsWith(">")) {
						final var substring2 = beforeContent.substring(0, beforeContent.length() - 1);
						final var i3 = substring2.indexOf("<");
						if (i3 >= 0) {
							final var substring3 = substring2.substring(i3 + 1).strip().split(Pattern.quote(","));
							typeParameters =
									Stream.fromArray(substring3).map(String::strip).filter(slice -> !slice.isEmpty()).toList();

							beforeContent = beforeContent.substring(0, i3).strip();
						}
					}

					var templateString = "";
					if (!typeParameters.isEmpty()) {
						final var joined =
								typeParameters.stream().map(slice -> "typename " + slice).collect(new Collectors.Joiner(", "));

						templateString = "template <" + joined + ">" + System.lineSeparator();
					}

					final String typeArguments;
					if (typeParameters.isEmpty()) {
						typeArguments = "";
					} else {
						typeArguments = "<" + typeParameters.stream().collect(new Collectors.Joiner(", ")) + ">";
					}

					var beforeStruct = "";
					if (maybeImplements.isPresent()) {
						final var superType = maybeImplements.get().toCType();
						final var superTypeString = superType.generate();

						final var thisType = beforeContent + typeArguments;
						final var s = generateStatement(superTypeString + "Data data");
						final var s1 = generateStatement("data.err = this");
						final var s2 = generateStatement("return " + superTypeString + " { " + beforeContent + "Type, data " +
																						 "}");
						final var content1 = s + s1 + s2;
						final var s3 = "to" + superTypeString + "_" + beforeContent;
						final var outputContent = "{" + generateDereferenceThis(thisType) + content1 + System.lineSeparator() +
																			"}";

						final var refDef = new CDefinition(new CPointerType(CPrimitiveType.Void), "_ref");
						functions =
								functions.addLast(new CFunction(new CDefinition(superType, s3), List.of(refDef), outputContent));
					}

					var generatedFields = new List<CDefinition>();
					if (!variants.isEmpty()) {
						final var enumFields = variants
								.stream()
								.map(segment -> System.lineSeparator() + "\t" + segment + "Type")
								.collect(new Collectors.Joiner(","));

						final var tagType = beforeContent + "Tag";
						final var generatedEnum =
								"enum " + tagType + " {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();

						final var unionFields = variants
								.stream()
								.map(segment -> System.lineSeparator() + "\t" + segment + typeArguments + " " + segment.toLowerCase() +
																";")
								.collect(new Collectors.Joiner(""));

						final var unionType = beforeContent + "Data";
						final var generatedUnion =
								templateString + "union " + unionType + " {" + unionFields + System.lineSeparator() + "}" +
								System.lineSeparator();

						beforeStruct += generatedEnum + generatedUnion;

						generatedFields = List.of(new CDefinition(new CIdentifier(tagType), "_tag"),
																			new CDefinition(new CIdentifier(unionType + typeArguments), "_data"));
					}

					scope = scope.enter().withStructureName(beforeContent).defineAll(recordFields);

					final var recordFieldsStream = recordFields.stream().map(JDefinition::toCDefinition);
					final var structureFields = recordFieldsStream
							.concat(generatedFields.stream().map(item -> item))
							.map(CDefinable::generate)
							.map(Main::generateStatement)
							.collect(new Collectors.Joiner(""));

					final var inputSegments = divide(content).map(Main::parseClassSegment).toList();
					final var incompleteSegments = inputSegments.stream().toList();

					final var methodDefinitions = inputSegments
							.stream()
							.map(JIncompleteClassSegment::createDefinition)
							.flatMap(Stream::fromOptional)
							.toList();

					final var outputContent = incompleteSegments
							.stream()
							.map(JIncompleteClassSegment::complete)
							.map(JClassSegment::toCStructureSegment)
							.map(CStructureSegment::generate)
							.collect(new Collectors.Joiner(""));

					final var generated =
							beforeStruct + templateString + "struct " + beforeContent + " {" + structureFields + outputContent +
							System.lineSeparator() + "};" + System.lineSeparator();

					structures = structures.addLast(generated);

					final var thisType = scope
							.getThisType()
							.<JType>map(classType -> classType.attachMembers(methodDefinitions))
							.orElse(JPrimitiveType.Void);

					scope = scope.exit().defineType(beforeContent, thisType);
					return Optional.of(new JClassSegmentWrapper(""));
				}
			}
		}

		return Optional.empty();
	}

	private static String generateDereferenceThis(String thisType) {
		return generateStatement(thisType + " _this = *((" + thisType + "*) _ref)");
	}

	private static String generateStatement(String content) {
		return System.lineSeparator() + "\t" + content + ";";
	}

	private static boolean isIdentifier(String input) {
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (Character.isLetter(c) || (i != 0 && Character.isDigit(c))) {continue;}
			return false;
		}

		return true;
	}

	private static JIncompleteClassSegment parseClassSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return new JClassSegmentWrapper("");
		}

		final var maybeInterface = compileStructure(stripped, "interface");
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final var maybeRecord = compileStructure(stripped, "record");
		if (maybeRecord.isPresent()) {
			return maybeRecord.get();
		}

		final var maybeEnum = compileStructure(stripped, "enum");
		if (maybeEnum.isPresent()) {
			return maybeEnum.get();
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var maybeClassStatement = compileClassStatement(substring);
			if (maybeClassStatement.isPresent()) {
				return maybeClassStatement.get();
			}
		}

		return compileMethod(stripped).orElseGet(() -> new JPlaceholder(stripped));
	}

	private static Optional<JIncompleteClassSegment> compileMethod(String stripped) {
		final var i = stripped.indexOf("(");
		if (i >= 0) {
			final var substring = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var paramString = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();
				final var header = parseDefinition(substring)
						.<JMethodHeader>map(definition -> definition)
						.or(() -> parseConstructor(substring))
						.orElseGet(() -> new JPlaceholder(substring));

				final var jParameters = Stream
						.fromArray(paramString.split(Pattern.quote(",")))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(Main::parseDefinition)
						.flatMap(Stream::fromOptional)
						.toList();

				return Optional.of(new JIncompleteMethod(header, jParameters, withBraces));
			}
		}

		return Optional.empty();
	}

	private static Optional<JMethodHeader> parseConstructor(String input) {
		final var stripped = input.strip();
		return Optional.of(new JConstructor(stripped));
	}

	private static Optional<JClassSegmentWrapper> compileClassStatement(String input) {
		return compileDefinition(input).map(Main::generateStatement).map(JClassSegmentWrapper::new).or(() -> {
			final var enumValues = Stream
					.fromArray(input.split(Pattern.quote(",")))
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.toList();

			final var name = scope.getCurrentStructName();
			if (!enumValues.stream().collect(new AllMatch<String>(segment -> compileEnumValue(segment, name)))) {
				return Optional.empty();
			}
			return Optional.of(new JClassSegmentWrapper(""));
		});
	}

	private static boolean compileEnumValue(String segment, String enumName) {
		final var stripped = segment.strip();
		if (stripped.endsWith(")")) {
			final var substring = stripped.substring(0, stripped.length() - 1);

			final var i = substring.indexOf("(");
			if (i >= 0) {
				final var memberName = substring.substring(0, i);
				final var substring2 = substring.substring(i + 1);

				if (isIdentifier(memberName)) {
					globals = globals.addLast(enumName + " " + enumName + "_" + memberName + " = new_" + enumName + "(" +
																		compileExpression(substring2) + ");" + System.lineSeparator());
					return true;
				}
			}
		}

		return false;
	}

	private static String compileExpression(String input) {
		return parseExpression(input).toCExpression().generate();
	}

	private static JExpression parseExpression(String input) {
		final var stripped = input.strip();
		if (isIdentifier(stripped)) {
			return new JIdentifier(stripped);
		}

		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var substring = stripped.substring(0, i).strip();
			final var name = stripped.substring(i + 1).strip();
			if (isIdentifier(name)) {
				final var child = parseExpression(substring);
				return new JMemberAccess(child, name);
			}
		}

		if (stripped.endsWith(")")) {
			final var slice = stripped.substring(0, stripped.length() - 1);
			final var i1 = slice.indexOf("(");
			if (i1 >= 0) {
				final var callerString = slice.substring(0, i1);
				final var arguments = Stream
						.fromArray(slice.substring(i1 + 1).split(Pattern.quote(",")))
						.map(String::strip)
						.filter(segment -> !segment.isEmpty())
						.map(Main::parseExpression)
						.toList();

				return new JInvocation(parseExpression(callerString), arguments);
			}
		}

		return new JPlaceholder(stripped);
	}

	private static String compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			return generateStatement(compileMethodSegmentValue(substring));
		}

		return CPlaceholder.wrap(stripped);
	}

	private static String compileMethodSegmentValue(String input) {
		if (input.startsWith("return ")) {
			final var slice = input.substring("return ".length());
			return "return " + compileExpression(slice);
		}

		final var i = input.indexOf("=");
		if (i >= 0) {
			final var destination = input.substring(0, i);
			final var substring1 = input.substring(i + 1);
			final var source = parseExpression(substring1);
			final var sourceString = source.toCExpression().generate();

			return parseDefinition(destination).map(definition -> {
				final var jDefinition = withResolvedType(definition, source);
				scope = scope.define(jDefinition);
				return jDefinition.toCDefinition().generate() + " = " + sourceString;
			}).orElseGet(() -> compileExpression(destination) + " = " + sourceString);
		}

		return CPlaceholder.wrap(input);
	}

	private static JDefinition withResolvedType(JDefinition definition, JExpression source) {
		return definition.mapType(type -> {
			if (type instanceof JIdentifier(var value) && value.equals("var")) {
				return resolveExpression(source);
			}

			return type;
		});
	}

	private static JType resolveExpression(JExpression expression) {
		if (expression instanceof JIdentifier(var input)) {
			return scope.resolveExpression(input).orElseGet(() -> new JPlaceholder("Unresolved identifier: " + input));
		}

		if (expression instanceof JMemberAccess(var child, var name)) {
			final var resolved = resolveExpression(child);
			if (resolved instanceof JClassType type0) {
				return type0.resolve(name).orElseGet(() -> new JPlaceholder("Property not present: " + name));
			}
			return new JPlaceholder("Not a structure type: " + resolved);
		}

		if (expression instanceof JInvocation invocation) {
			final var caller = invocation.caller;
			final var callerType = resolveExpression(caller);
			if (callerType instanceof JMethodType methodType) {
				return methodType.returnType;
			} else {
				return new JPlaceholder("Failed to resolve caller: " + callerType);
			}
		}

		return new JPlaceholder(expression.toString());
	}

	private static Optional<String> compileDefinition(String input) {
		return parseDefinition(input).map(JDefinition::toCDefinition).map(CDefinable::generate);
	}

	private static Optional<JDefinition> parseDefinition(String input) {
		final var stripped = input.strip();
		final var i = stripped.lastIndexOf(" ");
		if (i < 0) {return Optional.empty();}
		final var beforeName = stripped.substring(0, i).strip();
		final var name = stripped.substring(i + 1).strip();

		if (!isIdentifier(name)) {
			return Optional.empty();
		}

		final var i1 = beforeName.lastIndexOf(" ");
		if (i1 >= 0) {
			final var beforeType = beforeName.substring(0, i1);
			final var type = beforeName.substring(i1 + 1);
			return Optional.of(new JDefinition(Optional.of(beforeType), parseType(type), name));
		} else {
			return Optional.of(new JDefinition(Optional.empty(), parseType(beforeName), name));
		}
	}

	private static String compileTypeToString(String input) {
		return parseType(input).toCType().generate();
	}

	private static JType parseType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return JPrimitiveType.Void;
		}

		if (stripped.endsWith("[]")) {
			final var cType = parseType(stripped.substring(0, stripped.length() - 2));
			return new JArrayType(cType);
		}

		if (stripped.equals("String")) {
			return JPrimitiveType.String;
		}

		if (stripped.endsWith(">")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i = substring.indexOf("<");
			if (i >= 0) {
				final var base = substring.substring(0, i);
				final var typeArgumentsArray = substring.substring(i + 1).split(Pattern.quote(","));

				final var typeArguments = Stream
						.fromArray(typeArgumentsArray)
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(Main::parseType)
						.toList();

				return new JGenericType(base, typeArguments);
			}
		}

		if (isIdentifier(stripped)) {
			return new JIdentifier(stripped);
		}

		return new JPlaceholder(stripped);
	}
}
