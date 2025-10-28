package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class App {
	private enum CPrimitiveType implements CType {
		Void("void"), Char("char"), Int("int");

		private final String content;

		CPrimitiveType(String content) {this.content = content;}

		@Override
		public String generate() {
			return this.content;
		}

		@Override
		public String getSimpleName() {
			return this.content;
		}
	}

	private sealed interface Head<T> permits EmptyHead, ListHead, MapHead, SingleHead, FlatMapHead {
		Option<T> next();
	}

	private interface Collector<T, C> {
		C createInitial();

		C fold(C current, T element);
	}

	private sealed interface Result<T, X> permits Err, Ok {}

	private sealed interface CType permits CIdentifier, CPrimitiveType, CPointerType, CTemplateType, Placeholder {
		String generate();

		String getSimpleName();
	}

	private sealed interface CFunctionHeader permits CDefinition, Placeholder {
		String generate();
	}

	private sealed interface Option<T> permits None, Some {
		static <T> Option<T> of(T element) {
			return new Some<T>(element);
		}

		static <T> Option<T> empty() {
			return new None<T>();
		}

		<R> Option<R> map(Function<T, R> mapper);

		void ifPresent(Consumer<T> consumer);

		Option<T> or(Supplier<Option<T>> other);

		boolean isEmpty();

		T get();

		<R> Option<R> flatMap(Function<T, Option<R>> mapper);

		T orElse(T other);

		T orElseGet(Supplier<T> other);

		boolean isPresent();

		Stream<T> stream();
	}

	private static final class SingleHead<T> implements Head<T> {
		private final T element;
		private boolean retrieved;

		private SingleHead(T element) {
			this.element = element;
			this.retrieved = false;
		}

		@Override
		public Option<T> next() {
			if (this.retrieved) {
				return new None<T>();
			}
			this.retrieved = true;
			return new Some<T>(this.element);
		}
	}

	private static final class EmptyHead<T> implements Head<T> {
		@Override
		public Option<T> next() {
			return new None<T>();
		}
	}

	private record Stream<T>(Head<T> head) {
		public static <T> Stream<T> of(T element) {
			return new Stream<T>(new SingleHead<T>(element));
		}

		public static <T> Stream<T> empty() {
			return new Stream<T>(new EmptyHead<T>());
		}

		public <R> Stream<R> map(Function<T, R> mapper) {
			return new Stream<R>(new MapHead<T, R>(this.head, mapper));
		}

		public <R> R fold(R initial, BiFunction<R, T, R> folder) {
			R current = initial;
			while (true) {
				final Option<T> maybeNext = this.head.next();
				if (maybeNext instanceof Some<T>(T next)) {
					current = folder.apply(current, next);
				} else {
					return current;
				}
			}
		}

		public <C> C collect(Collector<T, C> collector) {
			return this.fold(collector.createInitial(), collector::fold);
		}

		public ArrayList<T> toList() {
			return this.collect(new ListCollector<T>());
		}

		public Stream<T> filter(Predicate<T> predicate) {
			return this.flatMap(element -> {
				if (predicate.test(element)) {
					return Stream.of(element);
				}
				return Stream.empty();
			});
		}

		public <R> Stream<R> flatMap(Function<T, Stream<R>> mapper) {
			return new Stream<R>(new FlatMapHead<T, R>(this.head, mapper));
		}
	}

	private record ArrayList<T>(List<T> inner) {

		public ArrayList() {
			this(new java.util.ArrayList<T>());
		}

		@SafeVarargs
		public static <T> ArrayList<T> of(T... elements) {
			return new ArrayList<T>(new java.util.ArrayList<T>(Arrays.asList(elements)));
		}

		private int size() {
			return this.inner.size();
		}

		public Stream<T> stream() {
			return new Stream<T>(new ListHead<T>(this));
		}

		public ArrayList<T> addLast(T element) {
			this.inner.add(element);
			return this;
		}

		public boolean isEmpty() {
			return this.inner.isEmpty();
		}

		public ArrayList<T> copy() {
			return new ArrayList<T>(new java.util.ArrayList<T>(this.inner));
		}

		public ArrayList<T> addFirst(T element) {
			this.inner.addFirst(element);
			return this;
		}

		public ArrayList<T> removeLast() {
			this.inner.removeLast();
			return this;
		}

		public T getLast() {
			return this.inner.getLast();
		}
	}

	private record Err<T, X>(X error) implements Result<T, X> {}

	private record Ok<T, X>(T value) implements Result<T, X> {}

	private record Some<T>(T value) implements Option<T> {
		@Override
		public <R> Option<R> map(Function<T, R> mapper) {
			return new Some<R>(mapper.apply(this.value));
		}

		@Override
		public void ifPresent(Consumer<T> consumer) {
			consumer.accept(this.value);
		}

		@Override
		public Option<T> or(Supplier<Option<T>> other) {
			return this;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public T get() {
			return this.value;
		}

		@Override
		public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
			return mapper.apply(this.value);
		}

		@Override
		public T orElse(T other) {
			return this.value;
		}

		@Override
		public T orElseGet(Supplier<T> other) {
			return this.value;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public Stream<T> stream() {
			return Stream.of(this.value);
		}
	}

	private static final class None<T> implements Option<T> {
		@Override
		public <R> Option<R> map(Function<T, R> mapper) {
			return new None<R>();
		}

		@Override
		public void ifPresent(Consumer<T> consumer) {
		}

		@Override
		public Option<T> or(Supplier<Option<T>> other) {
			return other.get();
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public T get() {
			return null;
		}

		@Override
		public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
			return new None<R>();
		}

		@Override
		public T orElse(T other) {
			return other;
		}

		@Override
		public T orElseGet(Supplier<T> other) {
			return other.get();
		}

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public Stream<T> stream() {
			return Stream.empty();
		}
	}

	private record CPointerType(CType type) implements CType {
		@Override
		public String generate() {
			return this.type.generate() + "*";
		}

		@Override
		public String getSimpleName() {
			return this.type.getSimpleName() + "_ref";
		}
	}

	public record CTemplateType(String base, ArrayList<CType> list) implements CType {
		@Override
		public String generate() {
			final String joined = this.list.stream().map(CType::generate).collect(new Joiner(", "));

			return this.base + "<" + joined + ">";
		}

		@Override
		public String getSimpleName() {
			return this.base;
		}
	}

	private record CIdentifier(String input) implements CType {
		@Override
		public String generate() {
			return this.input;
		}

		@Override
		public String toString() {
			return "";
		}

		@Override
		public String getSimpleName() {
			return this.input;
		}
	}

	private record Placeholder(String input) implements CType, CFunctionHeader {
		private static String wrap(String input) {
			final String replaced = input.replace("/*", "start").replace("*/", "end");
			return "/*" + replaced + "*/";
		}

		@Override
		public String generate() {
			return wrap(this.input);
		}

		@Override
		public String getSimpleName() {
			return this.generate();
		}
	}

	private record Tuple<A, B>(A left, B right) {}

	private static class State {
		public final String input;
		public ArrayList<String> segments;
		private String buffer;
		private int depth;
		private int index;

		public State(String input) {
			this.input = input;
			this.buffer = "";
			this.depth = 0;
			this.segments = new ArrayList<String>();
			this.index = 0;
		}

		State enter() {
			this.depth = this.depth + 1;
			return this;
		}

		State exit() {
			this.depth = this.depth - 1;
			return this;
		}

		State advance() {
			this.segments = this.segments.addLast(this.buffer);
			this.buffer = "";
			return this;
		}

		boolean isShallow() {
			return this.depth == 1;
		}

		State append(char c) {
			this.buffer += c;
			return this;
		}

		boolean isLevel() {
			return this.depth == 0;
		}

		public Option<Character> pop() {
			if (this.index < this.input.length()) {
				int counter = this.index;
				this.index++;
				final char element = this.input.charAt(counter);
				return Option.of(element);
			} else {
				return Option.empty();
			}
		}

		public Stream<String> stream() {
			return this.segments.stream();
		}

		public Option<Tuple<Character, State>> popAndAppendToTuple() {
			return this.pop().map(next -> {
				final State appended = this.append(next);
				return new Tuple<Character, State>(next, appended);
			});
		}

		public Option<State> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(Tuple::right);
		}

		public char peek() {
			return this.input.charAt(this.index);
		}
	}

	private record CDefinition(CType cType, String name) implements CFunctionHeader {
		@Override
		public String generate() {
			return this.cType().generate() + " " + this.name();
		}
	}

	private record CStructureHeader(ArrayList<String> typeParameters, String name) {
		private CType toType() {
			if (this.typeParameters.isEmpty()) {
				return new CIdentifier(this.name);
			}

			final ArrayList<CType> list = this.typeParameters.stream().<CType>map(CIdentifier::new).toList();
			return new CTemplateType(this.name, list);
		}

		public String generate() {
			return App.createTemplateString(this.typeParameters()) + "struct " + this.name();
		}

		public String createTemplateString() {
			return App.createTemplateString(this.typeParameters);
		}
	}

	private record CStructure(CStructureHeader CStructureHeader, String fields) {
		private String generate() {
			return this.CStructureHeader().generate() + " {" + this.fields() + "};";
		}
	}

	private static final class MapHead<T, R> implements Head<R> {
		private final Function<T, R> mapper;
		private final Head<T> head;

		public MapHead(Head<T> head, Function<T, R> mapper) {
			this.mapper = mapper;
			this.head = head;
		}

		@Override
		public Option<R> next() {
			return this.head.next().map(this.mapper);
		}
	}

	private static final class ListHead<T> implements Head<T> {
		private final ArrayList<T> list;
		private int counter;

		public ListHead(ArrayList<T> list) {
			this.list = list;
			this.counter = 0;
		}

		@Override
		public Option<T> next() {
			if (this.counter < this.list.size()) {
				final T element = this.list.inner.get(this.counter);
				this.counter++;
				return Option.of(element);
			}

			return Option.empty();
		}
	}

	private static class ListCollector<T> implements Collector<T, ArrayList<T>> {
		@Override
		public ArrayList<T> createInitial() {
			return new ArrayList<T>();
		}

		@Override
		public ArrayList<T> fold(ArrayList<T> current, T element) {
			return current.addLast(element);
		}
	}

	private static final class FlatMapHead<T, R> implements Head<R> {
		private final Head<T> head;
		private final Function<T, Stream<R>> mapper;
		private Head<R> current;

		private FlatMapHead(Head<T> head, Function<T, Stream<R>> mapper) {
			this.head = head;
			this.mapper = mapper;
			this.current = new EmptyHead<R>();
		}

		@Override
		public Option<R> next() {
			while (true) {
				final Option<R> maybeNext = this.current.next();
				if (maybeNext.isPresent()) {
					return maybeNext;
				}

				final Option<T> maybeOuter = this.head.next();
				if (maybeOuter.isEmpty()) {
					return Option.empty();
				}

				this.current = this.mapper.apply(maybeOuter.get()).head;
			}
		}
	}

	private record Joiner(String delimiter) implements Collector<String, String> {

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

	private ArrayList<CStructureHeader> structureHeaders;
	private ArrayList<String> globals;
	private ArrayList<String> forwardDeclarations;
	private ArrayList<String> structures;
	private ArrayList<String> sealedStructures;
	private ArrayList<String> functions;
	private int counter;
	private int depth;

	public App() {
		this.globals = new ArrayList<String>();
		this.structureHeaders = new ArrayList<CStructureHeader>();
		this.functions = new ArrayList<String>();
		this.forwardDeclarations = new ArrayList<String>();
		this.structures = new ArrayList<String>();
		this.sealedStructures = new ArrayList<String>();
		this.depth = 1;
		this.counter = 0;
	}

	public static void main(String[] args) {
		new App().run().ifPresent(Throwable::printStackTrace);
	}

	private static String createTemplateString(ArrayList<String> typeParameters) {
		String templateString;
		if (typeParameters.isEmpty()) {
			templateString = "";
		} else {
			final String collect = typeParameters.stream().map(slice -> "typename " + slice).collect(new Joiner(", "));
			templateString = "template <" + collect + ">" + System.lineSeparator();
		}
		return templateString;
	}

	private Option<IOException> run() {
		final Path source = Paths.get(".", "src", "main", "java", "magma", "App.java");
		final Result<String, IOException> input = this.readString(source);
		return switch (input) {
			case Err<String, IOException> v -> Option.of(v.error);
			case Ok<String, IOException> v -> this.compilePath(source, v.value);
		};
	}

	private Option<IOException> compilePath(Path source, String input) {
		final Path target = source.resolveSibling("App.cpp");
		final String output = this.compile(input);
		return this.writeString(target, output).or(() -> this.compileNative(target));
	}

	private Option<IOException> compileNative(Path target) {
		final Result<Process, IOException> clang =
				this.startCommand(ArrayList.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));
		return switch (clang) {
			case Err<Process, IOException> v1 -> Option.of(v1.error);
			case Ok<Process, IOException> v1 -> this.waitForProcess(v1.value);
		};
	}

	private Option<IOException> waitForProcess(Process process) {
		return switch (this.waitFor(process)) {
			case Err<Integer, IOException> v2 -> Option.of(v2.error);
			case Ok<Integer, IOException> v2 -> {
				System.out.println("Compilation failed with exit code: " + v2.value);
				yield Option.empty();
			}
		};
	}

	private Result<Integer, IOException> waitFor(Process process) {
		try {
			return new Ok<Integer, IOException>(process.waitFor());
		} catch (InterruptedException e) {
			return new Err<Integer, IOException>(new IOException(e));
		}
	}

	private Result<Process, IOException> startCommand(ArrayList<String> command) {
		try {
			return new Ok<Process, IOException>(new ProcessBuilder(command.inner).inheritIO().start());
		} catch (IOException e) {
			return new Err<Process, IOException>(e);
		}
	}

	private Option<IOException> writeString(Path target, String output) {
		try {
			Files.writeString(target, output);
			return Option.empty();
		} catch (IOException e) {
			return Option.of(e);
		}
	}

	private Result<String, IOException> readString(Path source) {
		try {
			return new Ok<String, IOException>(Files.readString(source));
		} catch (IOException e) {
			return new Err<String, IOException>(e);
		}
	}

	private String compile(String input) {
		final String compiled = this.compileStatements(input, this::compileRootSegment);

		final String joinedForwardDeclarations = String.join("", this.forwardDeclarations.inner);
		final String joinedFunctions = String.join("", this.functions.inner);

		final String joinedStructures = String.join("", this.structures.inner);
		final String joinedSealedStructures = String.join("", this.sealedStructures.inner);
		final String joinedGlobals = String.join("", this.globals.inner);

		return joinedForwardDeclarations + compiled + joinedStructures + joinedSealedStructures + joinedGlobals +
					 joinedFunctions + "int main(){" + System.lineSeparator() + "\treturn " + "0;" + System.lineSeparator() +
					 "}";
	}

	private String compileStatements(String input, Function<String, String> mapper) {
		return this.divide(input, this::foldStatement).map(mapper).collect(new Joiner(""));
	}

	private Stream<String> divide(String input, BiFunction<State, Character, State> folder) {
		State current = new State(input);
		while (true) {
			final Option<Character> maybeNext = current.pop();
			if (maybeNext.isEmpty()) {
				break;
			}

			current = this.foldEscaped(current, maybeNext.get(), folder);
		}

		return current.advance().stream();
	}

	private State foldEscaped(State current, char next, BiFunction<State, Character, State> folder) {
		if (next == '\'') {
			return current
					.append(next)
					.popAndAppendToTuple()
					.map(this::foldSingleEscapeChar)
					.flatMap(State::popAndAppendToOption)
					.orElse(current);
		}

		if (next == '\"') {
			State current0 = current.append(next);
			while (true) {
				final Option<Tuple<Character, State>> maybeTuple = current0.popAndAppendToTuple();
				if (maybeTuple.isEmpty()) {
					break;
				}

				final Tuple<Character, State> tuple = maybeTuple.get();
				current0 = tuple.right;

				final Character nextInQuotes = tuple.left;
				if (nextInQuotes == '\\') {
					current0 = current0.popAndAppendToOption().orElse(current0);
					continue;
				}

				if (nextInQuotes == '\"') {
					break;
				}
			}

			return current0;
		}

		return folder.apply(current, next);
	}

	private State foldSingleEscapeChar(Tuple<Character, State> tuple) {
		if (tuple.left == '\\') {
			return tuple.right.popAndAppendToOption().orElse(tuple.right);
		}
		return tuple.right;
	}

	private State foldStatement(State state, Character c) {
		final State appended = state.append(c);
		if (c == ';' && appended.isLevel()) {
			return appended.advance();
		}

		if (c == '}' && appended.isShallow()) {
			final State state1;
			if (appended.peek() == ';') {
				state1 = appended.popAndAppendToOption().orElse(appended);
			} else {
				state1 = appended;
			}

			return state1.advance().exit();
		}

		if (c == '{' || c == '(') {
			return appended.enter();
		}

		if (c == '}' || c == ')') {
			return appended.exit();
		}

		return appended;
	}

	private String compileRootSegment(String input) {
		final String stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return this.compileStructure("class", stripped).orElseGet(() -> Placeholder.wrap(input));
	}

	private Option<String> compileStructure(String type, String input) {
		final int classIndex = input.indexOf(type);
		if (classIndex >= 0) {
			final String afterKeyword = input.substring(classIndex + type.length());
			final int contentStart = afterKeyword.indexOf("{");
			if (contentStart >= 0) {
				String beforeContent = afterKeyword.substring(0, contentStart).strip();
				final String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
				if (withEnd.endsWith("}")) {
					final String content = withEnd.substring(0, withEnd.length() - 1);

					final int permitsIndex = beforeContent.indexOf("permits");
					ArrayList<String> variants = new ArrayList<String>();
					if (permitsIndex >= 0) {
						final String[] variantsArray =
								beforeContent.substring(permitsIndex + "permits".length()).split(Pattern.quote(","));
						beforeContent = beforeContent.substring(0, permitsIndex).strip();
						variants = new ArrayList<String>(Arrays
																								 .stream(variantsArray)
																								 .map(String::strip)
																								 .filter(slice -> !slice.isEmpty())
																								 .toList());
					}

					final int implementsIndex = beforeContent.indexOf("implements");
					Option<CType> maybeInterfaceType = Option.empty();
					if (implementsIndex >= 0) {
						final String slice = beforeContent.substring(implementsIndex + "implements".length()).strip();
						maybeInterfaceType = this.compileType(slice);
						beforeContent = beforeContent.substring(0, implementsIndex).strip();
					}

					ArrayList<CDefinition> recordFields = new ArrayList<CDefinition>();
					if (beforeContent.endsWith(")")) {
						final String slice = beforeContent.substring(0, beforeContent.length() - 1);
						final int i = slice.indexOf("(");
						if (i >= 0) {
							final String params = slice.substring(i + 1);
							beforeContent = slice.substring(0, i).strip();

							recordFields = this.compileParametersToList(params);
						}
					}

					ArrayList<String> typeParameters = new ArrayList<String>();
					if (beforeContent.endsWith(">")) {
						final String withoutEnd = beforeContent.substring(0, beforeContent.length() - 1);
						final int typeParamStart = withoutEnd.indexOf("<");
						if (typeParamStart >= 0) {
							beforeContent = withoutEnd.substring(0, typeParamStart);
							final String[] typeParamsArray = withoutEnd.substring(typeParamStart + 1).split(Pattern.quote(","));
							typeParameters = new ArrayList<String>(Arrays
																												 .stream(typeParamsArray)
																												 .map(String::strip)
																												 .filter(slice -> !slice.isEmpty())
																												 .toList());
						}
					}

					if (!this.isIdentifier(beforeContent)) {
						return Option.empty();
					}

					final String templateString = App.createTemplateString(typeParameters);

					String dependencies;
					if (variants.isEmpty()) {
						dependencies = "";
					} else {
						final String enumFields = variants
								.stream()
								.map(slice -> slice + "Tag")
								.map(content1 -> this.generateWithIndent(content1, 1))
								.collect(new Joiner(","));

						final String typeArguments = this.joinTypeArguments(typeParameters);
						final String unionFields = variants
								.stream()
								.map(slice -> System.lineSeparator() + "\t" + slice + typeArguments + " " + slice.toLowerCase() + ";")
								.collect(new Joiner(""));

						dependencies = "enum " + beforeContent + "Tag {" + enumFields + System.lineSeparator() + "};" +
													 System.lineSeparator() + templateString + "union " + beforeContent + "Data {" + unionFields +
													 System.lineSeparator() + "};" + System.lineSeparator();
					}

					final String fields;
					if (variants.isEmpty()) {
						fields = recordFields
								.stream()
								.map(CDefinition::generate)
								.map(slice -> this.generateStatement(slice, 1))
								.collect(new Joiner(""));
					} else {
						fields = this.generateStatement(beforeContent + "Tag tag", 1) + this.generateStatement(
								beforeContent + "Data" + this.joinTypeArguments(typeParameters) + " " + "data", 1);
					}

					if (maybeInterfaceType.isPresent()) {
						final CType interfaceType = maybeInterfaceType.get();
						final String joinedTypeArguments = this.joinTypeArguments(typeParameters);

						final String thisType = beforeContent + joinedTypeArguments;
						this.functions = this.functions.addLast(
								templateString + interfaceType.generate() + " to" + interfaceType.getSimpleName() + "_" +
								beforeContent + "(void* _ref" + "){" +
								this.generateStatement(thisType + " _this = *((" + thisType + "*) _ref)", 1) +
								this.generateStatement(interfaceType.getSimpleName() + "Data" + joinedTypeArguments + " data", 1) +
								this.generateStatement("data." + beforeContent.toLowerCase() + " = _this", 1) + this.generateStatement(
										"return " + interfaceType.generate() + " { " + beforeContent + "Tag, " + "data }",
										1) + System.lineSeparator() + "}" + System.lineSeparator());
					}

					this.forwardDeclarations = this.forwardDeclarations.addLast(
							templateString + "struct " + beforeContent + ";" + System.lineSeparator());

					final CStructureHeader header = new CStructureHeader(typeParameters, beforeContent);
					this.structureHeaders = this.structureHeaders.addLast(header);

					final String outputContent =
							fields + System.lineSeparator() + this.compileStatements(content, this::compileClassSegment);

					final String generated =
							dependencies + new CStructure(header, outputContent).generate() + System.lineSeparator();

					this.structureHeaders = this.structureHeaders.removeLast();

					if (variants.isEmpty()) {
						this.structures = this.structures.addLast(generated);
					} else {
						this.sealedStructures = this.sealedStructures.addLast(generated);
					}

					return Option.of("");
				}
			}
		}

		return Option.empty();
	}

	private String joinTypeArguments(ArrayList<String> typeParameters) {
		String joinedTypeArguments;
		if (typeParameters.isEmpty()) {
			joinedTypeArguments = "";
		} else {
			joinedTypeArguments = "<" + String.join(", ", typeParameters.inner) + ">";
		}
		return joinedTypeArguments;
	}

	private String generateStatement(String content, int depth) {
		return this.generateWithIndent(content, depth) + ";";
	}

	private String generateWithIndent(String content, int depth) {
		return this.generateIndent(depth) + content;
	}

	private String generateIndent(int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}

	private boolean isIdentifier(String input) {
		for (int i = 0; i < input.length(); i++) {
			final char next = input.charAt(i);
			if (Character.isLetter(next) || (i != 0 && Character.isDigit(next))) {continue;}
			return false;
		}

		return true;
	}

	private String compileClassSegment(String input) {
		if (input.isBlank()) {
			return "";
		}

		final Option<String> maybeClass = this.compileStructure("class", input);
		if (maybeClass.isPresent()) {
			return maybeClass.get();
		}

		final Option<String> maybeInterface = this.compileStructure("interface", input);
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final Option<String> maybeRecord = this.compileStructure("record", input);
		if (maybeRecord.isPresent()) {
			return maybeRecord.get();
		}

		final Option<String> maybeEnum = this.compileStructure("enum", input);
		if (maybeEnum.isPresent()) {
			return maybeEnum.get();
		}

		if (input.endsWith(";")) {
			final String slice = input.substring(0, input.length() - 1);
			final Option<String> maybeClassStatement =
					this.compileEnumValues(slice).or(() -> this.compileDefinitionToField(slice));
			if (maybeClassStatement.isPresent()) {
				return maybeClassStatement.get();
			}
		}

		return this.compileMethod(input).orElseGet(() -> Placeholder.wrap(input));
	}

	private Option<String> compileMethod(String input) {
		final int paramStart = input.indexOf("(");
		if (paramStart < 0) {return Option.empty();}
		final String definition = input.substring(0, paramStart).strip();
		final String withParams = input.substring(paramStart + 1);

		final int paramEnd = withParams.indexOf(")");
		if (paramEnd < 0) {return Option.empty();}
		final String params = withParams.substring(0, paramEnd).strip();
		final String withBraces = withParams.substring(paramEnd + 1).strip();

		final CFunctionHeader header = this.compileFunctionHeader(definition);

		final String headerWithParameters = header.generate() + "(" + this.compileParameters(params) + ")";
		final String templateString = this.structureHeaders.getLast().createTemplateString();

		final String generated;
		if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
			final String content = withBraces.substring(1, withBraces.length() - 1);

			final CStructureHeader currentStructureType = this.structureHeaders.getLast();
			final String thisDefinition = this.generateStatement(
					currentStructureType.toType().generate() + " _this = *((" + currentStructureType.name() + "*) _ref)", 1);

			generated = templateString + headerWithParameters + " {" + thisDefinition + this.compileMethodSegments(content) +
									System.lineSeparator() + "}" + System.lineSeparator();
		} else {
			generated = templateString + headerWithParameters + ";" + System.lineSeparator();
		}

		this.functions = this.functions.addLast(generated);
		return Option.of("");
	}

	private CFunctionHeader compileFunctionHeader(String input) {
		return this
				.compileDefinition(input)
				.<CFunctionHeader>map(item -> new CDefinition(item.cType,
																											item.name + "_" + this.structureHeaders.getLast().name))
				.or(() -> this.compileConstructor(input))
				.orElseGet(() -> new Placeholder(input));
	}

	private Option<String> compileDefinitionToField(String slice) {
		return this.compileDefinition(slice).map(CDefinition::generate).map(content -> this.generateStatement(content, 1));
	}

	private String compileMethodSegments(String content) {
		return this.compileStatements(content, this::compileMethodSegmentOrPlaceholder);
	}

	private Option<CFunctionHeader> compileConstructor(String input) {
		final int i = input.lastIndexOf(" ");
		if (i >= 0) {
			final String name = input.substring(i + 1).strip();
			if (this.isIdentifier(name)) {
				final CStructureHeader peek = this.structureHeaders.getLast();
				return Option.of(new CDefinition(peek.toType(), "new_" + peek.name));
			}
		} else {
			if (this.isIdentifier(input)) {
				final String structName = this.structureHeaders.getLast().name;
				return Option.of(new CDefinition(new CIdentifier(structName), "new_" + structName));
			}
		}

		return Option.empty();
	}

	private Option<String> compileEnumValues(String input) {
		final ArrayList<String> segments = new ArrayList<String>(Arrays
																																 .stream(input.split(Pattern.quote(",")))
																																 .map(String::strip)
																																 .filter(slice -> !slice.isEmpty())
																																 .toList());

		for (String segment : segments.inner) {
			final String stripped = segment.strip();
			final Option<String> maybeEnumValue = this.compileEnumValue(stripped);
			if (maybeEnumValue.isPresent()) {
				this.globals = this.globals.addLast(maybeEnumValue.get());
			} else {
				return Option.empty();
			}
		}

		return Option.of("");
	}

	private Option<String> compileEnumValue(String stripped) {
		if (stripped.endsWith(")")) {
			final String slice = stripped.substring(0, stripped.length() - 1);
			final int i = slice.indexOf("(");
			if (i >= 0) {
				final String name = slice.substring(0, i).strip();
				final String arguments = slice.substring(i + 1);
				if (this.isIdentifier(name)) {
					final String structureName = this.structureHeaders.getLast().name;
					return Option.of(structureName + " " + name + "Value = " + structureName + " { " + arguments + " };" +
													 System.lineSeparator());
				}
			}
		}

		return Option.empty();
	}

	private String compileMethodSegmentOrPlaceholder(String input) {
		return this.compileMethodSegment(input).orElseGet(() -> Placeholder.wrap(input));
	}

	private Option<String> compileMethodSegment(String input) {
		final String stripped = input.strip();
		if (stripped.isEmpty() || stripped.startsWith("try ") || stripped.startsWith("catch ")) {
			return Option.of("");
		}

		if (stripped.startsWith("{") && stripped.endsWith("}")) {
			final String content = stripped.substring(1, stripped.length() - 1);

			this.depth++;
			final String compiled = this.compileMethodSegments(content);
			this.depth--;

			return Option.of("{" + compiled + this.generateIndent(this.depth) + "}");
		}

		final Option<String> maybeIf = this.compileConditional(stripped, "if");
		if (maybeIf.isPresent()) {
			return maybeIf;
		}

		final Option<String> maybeWhile = this.compileConditional(stripped, "while");
		if (maybeWhile.isPresent()) {
			return maybeWhile;
		}

		if (stripped.endsWith(";")) {
			final String slice = stripped.substring(0, stripped.length() - 1);
			return Option.of(this.generateStatement(this.compileMethodStatement(slice), this.depth));
		}

		if (stripped.startsWith("else ")) {
			final String substring = stripped.substring(5);
			return Option.of(this.generateIndent(this.depth) + "else " + this.compileMethodSegmentOrPlaceholder(substring));
		}

		return Option.empty();
	}

	private Option<String> compileConditional(String input, String type) {
		if (input.startsWith(type)) {
			final String substring = input.substring(type.length()).strip();
			if (substring.startsWith("(")) {
				final String withCondition = substring.substring(1);
				final int conditionEnd = this.findConditionEnd(withCondition);

				if (conditionEnd >= 0) {
					final String condition = withCondition.substring(0, conditionEnd).strip();
					final String substring2 = withCondition.substring(conditionEnd + 1).strip();
					return Option.of(this.generateIndent(this.depth) + type + " (" + this.compileExpression(condition) + ") " +
													 this.compileMethodSegmentOrPlaceholder(substring2));
				}
			}
		}

		return Option.empty();
	}

	private int findConditionEnd(String withCondition) {
		int conditionEnd = -1;
		int depth = 0;
		for (int i = 0; i < withCondition.length(); i++) {
			final char c = withCondition.charAt(i);
			if (c == ')') {
				depth--;
				if (depth == -1) {
					conditionEnd = i;
					break;
				}
			}

			if (c == '(') {
				depth++;
			}
		}

		return conditionEnd;
	}

	private String compileMethodStatement(String input) {
		final String stripped = input.strip();

		if (stripped.startsWith("return ")) {
			final String slice = stripped.substring("return ".length()).strip();
			return "return " + this.compileExpression(slice);
		}

		final int separator = stripped.indexOf('=');
		if (separator >= 0) {
			final String substring = stripped.substring(0, separator).strip();
			final String substring1 = stripped.substring(separator + 1).strip();
			final String s = this
					.compileDefinition(substring)
					.map(CDefinition::generate)
					.orElseGet(() -> this.compileExpression(substring));

			return s + " = " + this.compileExpression(substring1);
		}

		if (stripped.endsWith("++")) {
			return this.compileExpression(stripped.substring(0, stripped.length() - 2)) + "++";
		}

		if (stripped.equals("break")) {
			return "break";
		}

		if (stripped.equals("continue")) {
			return "continue";
		}

		return this
				.compileInvocation(stripped)
				.or(() -> this.compileDefinition(input).map(CDefinition::generate))
				.orElseGet(() -> Placeholder.wrap(stripped));
	}

	private String compileExpression(String input) {
		final String stripped = input.strip();

		if (stripped.startsWith("'") && stripped.endsWith("'")) {
			return stripped;
		}

		if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
			return stripped;
		}

		final Option<String> maybeLambda = this.compileLambda(stripped);
		if (maybeLambda.isPresent()) {
			return maybeLambda.get();
		}

		final Option<String> maybeInvocation = this.compileInvocation(stripped);
		if (maybeInvocation.isPresent()) {
			return maybeInvocation.get();
		}

		final int i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final String child = stripped.substring(0, i).strip();
			final String name = stripped.substring(i + 1).strip();
			if (this.isIdentifier(name)) {
				return this.compileExpression(child) + "." + name;
			}
		}

		if (this.isIdentifier(stripped)) {
			if (stripped.equals("this")) {
				return "_this";
			}
			return stripped;
		}

		if (stripped.startsWith("switch")) {
			return this.createName("switch");
		}

		final Option<String> maybeOperator = this
				.compileOperator(stripped, "+")
				.or(() -> this.compileOperator(stripped, "-"))
				.or(() -> this.compileOperator(stripped, "&&"))
				.or(() -> this.compileOperator(stripped, "=="))
				.or(() -> this.compileOperator(stripped, ">="))
				.or(() -> this.compileOperator(stripped, "<"));

		if (maybeOperator.isPresent()) {
			return maybeOperator.get();
		}

		final int i2 = stripped.lastIndexOf("::");
		if (i2 >= 0) {
			final String substring = stripped.substring(0, i2);
			final String substring1 = stripped.substring(i2 + 2);
			return substring1 + "_" + this.compileType(substring).map(CType::generate).orElse("?");
		}

		if (this.isNumber(stripped)) {
			return stripped;
		}

		return Placeholder.wrap(stripped);
	}

	private Option<String> compileLambda(String stripped) {
		final int arrowIndex = stripped.indexOf("->");
		if (arrowIndex >= 0) {
			final String names = stripped.substring(0, arrowIndex).strip();
			final String content = stripped.substring(arrowIndex + 2);

			final String functionName = this.createName("lambda");

			final ArrayList<String> parameters;
			if (this.isIdentifier(names)) {
				parameters = ArrayList.of("auto " + names);
			} else if (names.startsWith("(") && names.endsWith(")")) {
				final String slice = names.substring(1, names.length() - 1);
				parameters = this
						.divide(slice, this::foldValue)
						.map(String::strip)
						.filter(segment -> !segment.isEmpty())
						.map(segment -> "auto " + segment)
						.toList();
			} else {
				return Option.empty();
			}

			final ArrayList<String> copy = parameters.copy().addFirst("auto _ref");
			this.functions = this.functions.addLast("auto " + functionName + "(" + String.join(", ", copy.inner) + ") " +
																							this.compileMethodSegment(content).orElseGet(() -> {
																								final String expression = this.compileExpression(content);
																								return "{" + this.generateStatement("auto _this = _ref", 1) +
																											 this.generateStatement("return " + expression, 1) +
																											 System.lineSeparator() + "};" + System.lineSeparator();
																							}));

			return Option.of(functionName);
		}

		return Option.empty();
	}

	private String createName(String type) {
		final String s = "_" + type + this.counter + "_";
		this.counter++;
		return s;
	}

	private Option<String> compileInvocation(String stripped) {
		if (stripped.endsWith(")")) {
			final String slice = stripped.substring(0, stripped.length() - 1);
			int argStart = -1;
			int depth = 0;
			for (int i = 0; i < slice.length(); i++) {
				final char next = slice.charAt(i);
				if (next == '(') {
					if (depth == 0) {
						argStart = i;
					}

					depth++;
				}
				if (next == ')') {
					depth--;
				}
			}

			if (argStart >= 0) {
				final String caller = slice.substring(0, argStart).strip();
				final ArrayList<String> arguments = this
						.divide(slice.substring(argStart + 1), this::foldValue)
						.map(String::strip)
						.filter(segment -> !segment.isEmpty())
						.map(this::compileExpression)
						.toList();

				final Option<String> maybeCaller = this.compileCaller(caller);
				if (maybeCaller.isPresent()) {
					return Option.of(maybeCaller.get() + "(" + String.join(", ", arguments.inner) + ")");
				}
			}
		}

		return Option.empty();
	}

	private Option<String> compileCaller(String caller) {
		if (caller.startsWith("new ")) {
			final String substring = caller.substring("new ".length());
			final Option<CType> maybeType = this.compileType(substring);
			if (maybeType.isPresent()) {
				return Option.of("new_" + maybeType.get().generate());
			}
		}

		return Option.of(this.compileExpression(caller));
	}

	private Option<String> compileOperator(String stripped, String separator) {
		final int i1 = stripped.indexOf(separator);
		if (i1 >= 0) {
			final String substring = stripped.substring(0, i1);
			final String substring1 = stripped.substring(i1 + separator.length());
			return Option.of(this.compileExpression(substring) + " " + separator + " " + this.compileExpression(substring1));
		}

		return Option.empty();
	}

	private boolean isNumber(String input) {
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			if (!Character.isDigit(c)) {
				return false;
			}
		}

		return true;
	}

	private String compileParameters(String input) {
		return this
				.compileParametersToList(input)
				.copy()
				.addFirst(new CDefinition(new CPointerType(CPrimitiveType.Void), "_ref"))
				.stream()
				.map(CDefinition::generate)
				.collect(new Joiner(", "));
	}

	private ArrayList<CDefinition> compileParametersToList(String input) {
		return this
				.divide(input, this::foldValue)
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.map(this::compileDefinition)
				.flatMap(Option::stream)
				.toList();
	}

	private Option<CDefinition> compileDefinition(String input) {
		final int nameSeparator = input.lastIndexOf(" ");
		if (nameSeparator < 0) {
			return Option.empty();
		}

		final String beforeName = input.substring(0, nameSeparator);
		final String name = input.substring(nameSeparator + 1).strip();
		if (!this.isIdentifier(name)) {
			return Option.empty();
		}

		int typeSeparator = -1;
		int depth = 0;
		for (int i = 0; i < beforeName.length(); i++) {
			final char c = beforeName.charAt(i);
			if (c == ' ' && depth == 0) {
				typeSeparator = i;
			}
			if (c == '<') {
				depth++;
			}
			if (c == '>') {
				depth--;
			}
		}

		if (typeSeparator >= 0) {
			final String type = beforeName.substring(typeSeparator + 1).strip();
			return this.compileType(type).map(cType -> new CDefinition(cType, name));
		}

		return this.compileType(beforeName).map(cType -> new CDefinition(cType, name));
	}

	private Option<CType> compileType(String input) {
		final String stripped = input.strip();

		switch (stripped) {
			case "Character" -> {
				return Option.of(CPrimitiveType.Char);
			}
			case "boolean" -> {
				return Option.of(CPrimitiveType.Int);
			}
			case "void" -> {
				return Option.of(CPrimitiveType.Void);
			}
		}

		if (stripped.endsWith("[]")) {
			final String slice = stripped.substring(0, stripped.length() - 2);
			return this.compileType(slice).map(CPointerType::new);
		}

		if (stripped.equals("String")) {
			return Option.of(new CPointerType(CPrimitiveType.Char));
		}

		if (stripped.endsWith(">")) {
			final String withoutEnd = stripped.substring(0, stripped.length() - 1);
			final int i = withoutEnd.indexOf("<");
			if (i >= 0) {
				final String base = withoutEnd.substring(0, i);
				final String typeArguments = withoutEnd.substring(i + 1);

				final ArrayList<CType> list = this
						.divide(typeArguments, this::foldValue)
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(this::compileType)
						.flatMap(Option::stream)
						.toList();

				return Option.of(new CTemplateType(base, list));
			}
		}

		if (this.isIdentifier(stripped)) {
			if (stripped.equals("public")) {
				return Option.empty();
			}

			return Option.of(new CIdentifier(stripped));
		}

		return Option.empty();
	}

	private State foldValue(State state, char next) {
		if (next == ',' && state.isLevel()) {
			return state.advance();
		}

		final State appended = state.append(next);
		if (next == '-') {
			if (appended.peek() == '>') {
				return appended.popAndAppendToOption().orElse(appended);
			}
		}

		if (next == '<' || next == '(') {
			return appended.enter();
		}

		if (next == '>' || next == ')') {
			return appended.exit();
		}

		return appended;
	}
}
