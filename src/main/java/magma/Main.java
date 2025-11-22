package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Main {
	private enum PrimitiveType implements Type {
		Void("void"), Char("char"), Int("int");

		private final String content;

		PrimitiveType(String content) {this.content = content;}

		@Override
		public String generate() {
			return this.content;
		}

		@Override
		public String toBaseName() {
			return this.content;
		}
	}

	private sealed interface Head<T> permits RangeHead, EmptyHead, FlatMapHead, MapHead, SingleHead {
		Option<T> next();
	}

	private interface List<T> {
		Stream<T> stream();

		boolean isEmpty();

		List<T> addLast(T element);

		boolean contains(T element);

		List<T> addFirst(T element);

		List<T> addAll(List<T> elements);

		int size();

		T getFirst();

		List<T> subList(int start, int end);

		List<T> clear();
	}

	private interface FR<T> {
		T apply();
	}

	private sealed interface Option<T> permits None, Some {
		<R> Option<R> map(F1R<T, R> mapper);

		T orElse(T other);

		<R> Option<R> flatMap(F1R<T, Option<R>> mapper);

		T orElseGet(FR<T> other);

		Stream<T> stream();

		Option<T> or(FR<Option<T>> other);

		Tuple<Boolean, T> toTuple(FR<T> other);
	}

	private interface F1R<T0, R> {
		R apply(T0 value);
	}

	private sealed interface Result<T, X> permits Err, Ok {
		<R> Result<R, X> mapValue(F1R<T, R> mapper);
	}

	private sealed interface Type permits Identifier, Placeholder, PointerType, PrimitiveType, TemplateType {
		String generate();

		String toBaseName();
	}

	private sealed interface MethodDeclaration permits Constructor, Declaration, Placeholder {
		String generate();
	}

	private sealed interface StructMember permits Declaration, EmptyStructMember, Field, F1RDeclaration, Placeholder {
		String generate();
	}

	private interface Folder {
		State apply(State state, Character character);
	}

	private interface F2R<A, B, R> {
		R apply(A a, B b);
	}

	private @interface Actual {}

	private interface Collector<T, C> {
		C createInitial();

		C fold(C c, T t);
	}

	private record StringBuilder(List<Character> list) {
		public StringBuilder() {
			this(new JavaList<Character>());
		}

		public StringBuilder appendChar(char next) {
			return new StringBuilder(this.list.addLast(next));
		}

		public StringBuilder clear() {
			return new StringBuilder(this.list.clear());
		}

		public StringBuilder appendString(String chars) {
			return Streams.fromCharArray(chars.toCharArray()).fold(this, StringBuilder::appendChar);
		}

		@Override
		public String toString() {
			return this.list.stream().map(String::valueOf).collect(new Joiner());
		}
	}

	private record Stream<T>(Head<T> head) {
		public static <T> Stream<T> of(T value) {
			return new Stream<T>(new SingleHead<T>(value));
		}

		public static <T> Stream<T> empty() {
			return new Stream<T>(new EmptyHead<T>());
		}

		public <R> Stream<R> map(F1R<T, R> mapper) {
			return new Stream<R>(new MapHead<T, R>(this.head, mapper));
		}

		public <R> R fold(R initial, F2R<R, T, R> folder) {
			var current = initial;
			while (true) {
				var finalCurrent = current;
				final var tuple =
						this.head.next().map(element -> folder.apply(finalCurrent, element)).toTuple(() -> finalCurrent);
				if (tuple.left) {
					current = tuple.right;
				} else {
					return current;
				}
			}
		}

		public <C> C collect(Collector<T, C> collector) {
			return this.fold(collector.createInitial(), collector::fold);
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

		private <R> Stream<R> flatMap(F1R<T, Stream<R>> mapper) {
			return new Stream<R>(new FlatMapHead<T, R>(this.head, mapper));
		}
	}

	private static final class RangeHead implements Head<Integer> {
		private final int length;
		private int counter;

		public RangeHead(int length) {
			this.length = length;
			this.counter = 0;
		}

		@Override
		public Option<Integer> next() {
			if (this.counter < this.length) {
				final var value = this.counter;
				this.counter++;
				return new Some<Integer>(value);
			} else {
				return new None<Integer>();
			}
		}
	}

	private record JavaList<T>(java.util.List<T> nativeList) implements List<T> {

		private JavaList(java.util.List<T> nativeList) {
			this.nativeList = new ArrayList<T>(nativeList);
		}

		public JavaList() {
			this(new ArrayList<T>());
		}

		public JavaList<T> addLast(T element) {
			this.nativeList.add(element);
			return this;
		}

		@Override
		public Stream<T> stream() {
			return new Stream<Integer>(new RangeHead(this.nativeList.size())).map(this.nativeList::get);
		}

		@Override
		public boolean isEmpty() {
			return this.nativeList.isEmpty();
		}

		public boolean contains(T element) {
			return this.nativeList.contains(element);
		}

		@Override
		public List<T> addFirst(T element) {
			this.nativeList.addFirst(element);
			return this;
		}

		@Override
		public List<T> addAll(List<T> elements) {
			return elements.stream().fold(this, JavaList::addLast);
		}

		@Override
		public int size() {
			return this.nativeList.size();
		}

		@Override
		public T getFirst() {
			return this.nativeList.getFirst();
		}

		@Override
		public List<T> subList(int start, int end) {
			return new JavaList<T>(this.nativeList.subList(start, end));
		}

		@Override
		public List<T> clear() {
			this.nativeList.clear();
			return this;
		}

	}

	private record Err<T, X>(X error) implements Result<T, X> {
		@Override
		public <R> Result<R, X> mapValue(F1R<T, R> mapper) {
			return new Err<R, X>(this.error);
		}
	}

	private record Ok<T, X>(T value) implements Result<T, X> {
		@Override
		public <R> Result<R, X> mapValue(F1R<T, R> mapper) {
			return new Ok<R, X>(mapper.apply(this.value));
		}
	}

	private record Tuple<A, B>(A left, B right) {}

	private static class State {
		private final String input;
		private StringBuilder buffer;
		private List<String> segments;
		private int index;
		private int depth;

		public State(String input) {
			this.input = input;
			this.index = 0;
			this.buffer = new StringBuilder();
			this.depth = 0;
			this.segments = new JavaList<String>();
		}

		private boolean isShallow() {
			return this.depth == 1;
		}

		private boolean isLevel() {
			return this.depth == 0;
		}

		private State append(Character next) {
			this.buffer = this.buffer.appendChar(next);
			return this;
		}

		private Option<Character> pop() {
			if (this.index < this.input.length()) {
				final var value = this.input.charAt(this.index);
				this.index++;
				return new Some<Character>(value);
			} else {
				return new None<Character>();
			}
		}

		private State advance() {
			this.segments = this.segments.addLast(this.buffer.toString());
			this.buffer = this.buffer.clear();
			return this;
		}

		private State enter() {
			this.depth = this.depth + 1;
			return this;
		}

		private State exit() {
			this.depth = this.depth - 1;
			return this;
		}

		private Stream<String> stream() {
			return this.segments.stream();
		}

		public Option<Tuple<State, Character>> popAndAppendToTuple() {
			return this.pop().map(popped -> {
				final var appended = this.append(popped);
				return new Tuple<State, Character>(appended, popped);
			});
		}

		public Option<State> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(tuple -> tuple.left);
		}

		public Option<Character> peek() {
			if (this.index < this.input.length()) {
				return new Some<Character>(this.input.charAt(this.index));
			}

			return new None<Character>();
		}
	}

	private record PointerType(Type type) implements Type {
		@Override
		public String generate() {
			return this.type.generate() + "*";
		}

		@Override
		public String toBaseName() {
			return this.type.toBaseName() + "_ptr";
		}
	}

	private record TemplateType(String base, List<Type> list) implements Type {
		@Override
		public String generate() {
			final var typeArguments = this.list.stream().map(Type::generate).collect(new Joiner(", "));

			return this.base + "<" + typeArguments + ">";
		}

		@Override
		public String toBaseName() {
			return this.base;
		}
	}

	private record Identifier(String value) implements Type {
		@Override
		public String generate() {
			return this.value;
		}

		@Override
		public String toBaseName() {
			return this.value;
		}
	}

	private record Placeholder(String input) implements Type, MethodDeclaration, StructMember {
		@Override
		public String generate() {
			return wrap(this.input);
		}

		@Override
		public String toBaseName() {
			return wrap(this.input);
		}
	}

	private record Constructor(String structName) implements MethodDeclaration {
		@Override
		public String generate() {
			return this.structName + " new_" + this.structName;
		}
	}

	private record Declaration(List<String> annotations, List<String> typeParameters, Option<String> maybeBeforeType,
														 String type, String name) implements MethodDeclaration, StructMember {
		public Declaration(String type, String name) {
			this(new JavaList<String>(), new JavaList<String>(), new None<String>(), type, name);
		}

		@Override
		public String generate() {
			var beforeDeclaration = generateTemplateString(this.typeParameters());
			return beforeDeclaration + this.type + " " + this.name;
		}

		public Declaration mapName(F1R<String, String> mapper) {
			return new Declaration(this.annotations,
														 this.typeParameters,
														 this.maybeBeforeType,
														 this.type,
														 mapper.apply(this.name));
		}

		public Declaration mapTypeParameters(F1R<List<String>, List<String>> mapper) {
			return new Declaration(this.annotations,
														 mapper.apply(this.typeParameters),
														 this.maybeBeforeType,
														 this.type,
														 this.name);
		}
	}

	private record F1RDeclaration(String type, String name, List<String> parameterTypes) implements StructMember {
		@Override
		public String generate() {
			final var joinedParameterTypes = "(" + this.parameterTypes.stream().collect(new Joiner(", ")) + ")";
			return this.type + " (*" + this.name + ")" + joinedParameterTypes;
		}
	}

	private static final class EmptyStructMember implements StructMember {
		@Override
		public String generate() {
			return "";
		}
	}

	private record EscapedFolder(Folder folder) implements Folder {
		@Override
		public State apply(State state, Character next) {
			if (next == '\'') {
				final var appended = state.append(next);
				return appended.popAndAppendToTuple().map(tuple -> {
					if (tuple.right == '\\') {
						return tuple.left.popAndAppendToOption().orElse(tuple.left);
					}
					return tuple.left;
				}).flatMap(State::popAndAppendToOption).orElse(appended);
			}

			if (next == '\"') {
				var current = state.append(next);
				while (true) {
					final var maybeTuple = current.popAndAppendToTuple();
					if (!(maybeTuple instanceof Some<Tuple<State, Character>>(var value))) {
						break;
					}

					current = value.left;

					final var right = value.right;
					if (right == '\\') {
						current = current.popAndAppendToOption().orElse(current);
					}

					if (right == '\"') {
						break;
					}
				}

				return current;
			}

			return this.folder.apply(state, next);
		}
	}

	private static class ValueFolder implements Folder {
		@Override
		public State apply(State state, Character next) {
			if (next == ',' && state.isLevel()) {
				return state.advance();
			}

			final var appended = state.append(next);
			if (next == '-') {
				final var peeked = appended.peek();
				if (peeked instanceof Some<Character>(var value) && value == '>') {
					return appended.popAndAppendToOption().orElse(appended);
				} else {
					return appended;
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

	private record Some<T>(T value) implements Option<T> {
		@Override
		public <R> Option<R> map(F1R<T, R> mapper) {
			return new Some<R>(mapper.apply(this.value));
		}

		@Override
		public T orElse(T other) {
			return this.value;
		}

		@Override
		public <R> Option<R> flatMap(F1R<T, Option<R>> mapper) {
			return mapper.apply(this.value);
		}

		@Override
		public T orElseGet(FR<T> other) {
			return this.value;
		}

		@Override
		public Stream<T> stream() {
			return Stream.of(this.value);
		}

		@Override
		public Option<T> or(FR<Option<T>> other) {
			return this;
		}

		@Override
		public Tuple<Boolean, T> toTuple(FR<T> other) {
			return new Tuple<Boolean, T>(true, this.value);
		}
	}

	private static final class None<T> implements Option<T> {
		@Override
		public <R> Option<R> map(F1R<T, R> mapper) {
			return new None<R>();
		}

		@Override
		public T orElse(T other) {
			return other;
		}

		@Override
		public <R> Option<R> flatMap(F1R<T, Option<R>> mapper) {
			return new None<R>();
		}

		@Override
		public T orElseGet(FR<T> other) {
			return other.apply();
		}

		@Override
		public Stream<T> stream() {
			return Stream.empty();
		}

		@Override
		public Option<T> or(FR<Option<T>> other) {
			return other.apply();
		}

		@Override
		public Tuple<Boolean, T> toTuple(FR<T> other) {
			return new Tuple<Boolean, T>(false, other.apply());
		}
	}

	private static class ConditionEndLocator implements Folder {
		@Override
		public State apply(State state, Character c) {
			final var appended = state.append(c);
			if (c == '(') {
				return appended.enter();
			}

			if (c == ')') {
				if (appended.isLevel()) {
					return appended.advance();
				}

				return appended.exit();
			}

			return appended;
		}
	}

	private record Field(Declaration declaration) implements StructMember {
		@Override
		public String generate() {
			return Main.generateStatement(1, this.declaration.generate());
		}
	}

	private static class Streams {
		public static <T> Stream<T> fromObjArray(T[] elements) {
			return new Stream<Integer>(new RangeHead(elements.length)).map(index -> elements[index]);
		}

		public static Stream<Character> fromCharArray(char[] array) {
			return new Stream<Integer>(new RangeHead(array.length)).map(index -> array[index]);
		}
	}

	private record MapHead<T, R>(Head<T> head, F1R<T, R> mapper) implements Head<R> {
		@Override
		public Option<R> next() {
			return this.head.next().map(this.mapper);
		}
	}

	private static final class SingleHead<T> implements Head<T> {
		private final T value;
		private boolean retrieved;

		public SingleHead(T value) {
			this.value = value;
			this.retrieved = false;
		}

		@Override
		public Option<T> next() {
			if (this.retrieved) {
				return new None<T>();
			}
			this.retrieved = true;
			return new Some<T>(this.value);
		}
	}

	private static final class FlatMapHead<T, R> implements Head<R> {
		private final Head<T> head;
		private final F1R<T, Stream<R>> mapper;
		private Option<Stream<R>> maybeCurrent;

		public FlatMapHead(Head<T> head, F1R<T, Stream<R>> mapper) {
			this.head = head;
			this.mapper = mapper;
			this.maybeCurrent = new None<Stream<R>>();
		}

		@Override
		public Option<R> next() {
			while (true) {
				if (this.maybeCurrent instanceof Some<Stream<R>>(var current)) {
					final var next = current.head.next();
					if (next instanceof Some<R>) {
						return next;
					}
				}

				final var maybeNext = this.head.next();
				if (maybeNext instanceof None<T>) {
					return new None<R>();
				}
				this.maybeCurrent = maybeNext.map(this.mapper);
			}
		}
	}

	private static final class EmptyHead<T> implements Head<T> {
		@Override
		public Option<T> next() {
			return new None<T>();
		}
	}

	private record AnyMatch<T>(Predicate<T> predicate) implements Collector<T, Boolean> {
		@Override
		public Boolean createInitial() {
			return false;
		}

		@Override
		public Boolean fold(Boolean aBoolean, T t) {
			return aBoolean || this.predicate.test(t);
		}
	}

	private record Joiner(String delimiter) implements Collector<String, String> {
		public Joiner() {
			this("");
		}

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

	private static class ListCollector<T> implements Collector<T, List<T>> {
		@Override
		public List<T> createInitial() {
			return new JavaList<T>();
		}

		@Override
		public List<T> fold(List<T> tList, T t) {
			return tList.addLast(t);
		}
	}
	private List<String> functionDeclarations;
	private List<String> globals;
	private List<String> structures;
	private List<String> functions;
	private int counter;

	public Main() {
		this.structures = new JavaList<String>();

		this.functionDeclarations = new JavaList<String>();
		this.functions = new JavaList<String>();

		this.globals = new JavaList<String>();
		this.counter = 0;
	}

	private static String generateTemplateString(List<String> typeParameters) {
		final String templateString;
		if (typeParameters.isEmpty()) {
			templateString = "";
		} else {
			final var typeNames = typeParameters.stream().map(typeParam -> "typename " + typeParam).collect(new Joiner(", "
			));

			templateString = "template <" + typeNames + ">" + System.lineSeparator();
		}
		return templateString;
	}

	private static String wrap(String input) {
		final var replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}

	public static void main(String[] args) {
		var ioExceptionOption = new Main().run();
		if (ioExceptionOption instanceof Some<IOException>(
				var value
		)) {
			//noinspection CallToPrintStackTrace
			value.printStackTrace();
		}
	}

	private static String generateStatement(int depth, String content) {
		return generateIndent(depth) + content + ";";
	}

	private static String generateIndent(int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}

	private Option<IOException> run() {
		final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		final var target = source.resolveSibling("Main.cpp");
		final var input = this.readString(source).mapValue(this::compile);

		return switch (input) {
			case Err<String, IOException> v -> new Some<IOException>(v.error);
			case Ok<String, IOException> v -> this.writeString(target, v.value);
		};
	}

	@Actual
	private Option<IOException> writeString(Path target, String output) {
		try {
			Files.writeString(target, output);
			return new None<IOException>();
		} catch (IOException e) {
			return new Some<IOException>(e);
		}
	}

	@Actual
	private Result<String, IOException> readString(Path source) {
		try {
			return new Ok<String, IOException>(Files.readString(source));
		} catch (IOException e) {
			return new Err<String, IOException>(e);
		}
	}

	private String compile(String input) {
		final var all = this.compileStatements(input, this::compileRootSegment);

		final var joinedStructures = this.joinStrings("", this.structures);
		final var joinedGlobals = this.joinStrings("", this.globals);

		final var joinedFunctionDeclarations = this.joinStrings("", this.functionDeclarations);
		final var joinedFunctions = this.joinStrings("", this.functions);
		return joinedStructures + joinedGlobals + joinedFunctionDeclarations + joinedFunctions + all;
	}

	private String joinStrings(String delimiter, List<String> structures) {
		return structures.stream().collect(new Joiner(delimiter));
	}

	private String compileStatements(String input, F1R<String, String> mapper) {
		return this.compileAll(input, mapper, new EscapedFolder(this::foldStatement));
	}

	private String compileAll(String input, F1R<String, String> mapper, Folder folder) {
		return this.divide(input, folder).map(mapper).collect(new Joiner(""));
	}

	private Stream<String> divide(String input, Folder folder) {
		var current = new State(input);
		while (true) {
			final var maybeNext = current.pop();
			if (!(maybeNext instanceof Some<Character>(var value))) {
				break;
			}

			final Character next;
			next = value;
			current = folder.apply(current, next);
		}

		return current.advance().stream();
	}

	private State foldStatement(State current, Character next) {
		if (next == '/' && current.isLevel()) {
			final var maybePeeked = current.peek();
			if (maybePeeked instanceof Some<Character>(var peek) && peek == '/') {
				var withoutLineCommentPrefix = current.append('/').popAndAppendToOption().orElse(current);
				while (true) {
					final var maybeTuple = withoutLineCommentPrefix.popAndAppendToTuple();
					if (maybeTuple instanceof Some<Tuple<State, Character>>(var tuple)) {
						withoutLineCommentPrefix = tuple.left;

						final var right = tuple.right;
						if (right == '\r' || right == '\n') {
							withoutLineCommentPrefix = withoutLineCommentPrefix.advance();
						}
					} else {
						return withoutLineCommentPrefix;
					}
				}
			}
		}

		final var appended = current.append(next);
		if (next == ';' && appended.isLevel()) {
			return appended.advance();
		}

		if (next == '}' && appended.isShallow()) {
			final State appended1;
			if (appended.peek() instanceof Some<Character>(var peek) && peek == ';') {
				appended1 = appended.popAndAppendToOption().orElse(appended);
			} else {
				appended1 = appended;
			}

			return appended1.advance().exit();
		}

		if (next == '{' || next == '(') {
			return appended.enter();
		}

		if (next == '}' || next == ')') {
			return appended.exit();
		}

		return appended;
	}

	private String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return this.compileStructure("class", stripped).map(StructMember::generate).orElseGet(() -> wrap(stripped));
	}

	private Option<StructMember> compileStructure(String type, String stripped) {
		final var i = stripped.indexOf(type + " ");
		if (i < 0) {
			return new None<StructMember>();
		}
		final var modifiers = stripped.substring(0, i).strip();
		final var afterKeyword = stripped.substring(i + (type + " ").length()).strip();

		final var i1 = afterKeyword.indexOf("{");
		if (i1 < 0) {
			return new None<StructMember>();
		}
		var beforeContent = afterKeyword.substring(0, i1).strip();

		final var withEnd = afterKeyword.substring(i1 + 1).strip();
		if (!withEnd.endsWith("}")) {
			return new None<StructMember>();
		}
		final var inputContent = withEnd.substring(0, withEnd.length() - 1);

		List<String> variants = new JavaList<String>();
		final var i2 = beforeContent.indexOf("permits ");
		if (i2 >= 0) {
			final var substring1 = beforeContent.substring(i2 + "permits ".length());
			beforeContent = beforeContent.substring(0, i2);

			variants = this.splitValues(substring1);
		}

		List<Type> implementees = new JavaList<Type>();
		final var i4 = beforeContent.indexOf("implements ");
		if (i4 >= 0) {
			final var implementeesString = beforeContent.substring(i4 + "implements ".length());
			beforeContent = beforeContent.substring(0, i4).strip();
			implementees = this
					.divide(implementeesString, (state, character) -> new ValueFolder().apply(state, character))
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.map(this::parseType)
					.toList();
		}

		List<Declaration> recordFields = new JavaList<Declaration>();
		if (beforeContent.endsWith(")")) {
			final var substring = beforeContent.substring(0, beforeContent.length() - 1);
			final var i3 = substring.indexOf("(");
			if (i3 >= 0) {
				beforeContent = substring.substring(0, i3);
				recordFields = this
						.divide(substring.substring(i3 + 1), (state, character) -> new ValueFolder().apply(state, character))
						.map(this::parseDeclaration)
						.flatMap(Option::stream)
						.toList();
			}
		}

		List<String> typeParameters = new JavaList<String>();
		final var i3 = beforeContent.indexOf("<");
		if (i3 >= 0) {
			final var substring1 = beforeContent.substring(i3 + 1).strip();
			if (substring1.endsWith(">")) {
				beforeContent = beforeContent.substring(0, i3);
				final var substring = substring1.substring(0, substring1.length() - 1);
				typeParameters = this.splitValues(substring);
			}
		}

		if (!this.isIdentifier(beforeContent)) {
			return new None<StructMember>();
		}

		var modifiersList = Streams
				.fromObjArray(modifiers.split(Pattern.quote(" ")))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList();

		var name = beforeContent.strip();

		final var templateString = generateTemplateString(typeParameters);
		final var joinedTypeParameters = this.joinTypeParameters(typeParameters);

		var fields = new StringBuilder();
		var dependencies = new StringBuilder();
		this.functions = implementees
				.stream()
				.map(implementee -> this.getString(implementee, name, joinedTypeParameters, templateString))
				.fold(this.functions, List::addLast);

		final var joinedRecordFields =
				recordFields.stream().map(Declaration::generate).map(this::generateStatement).collect(new Joiner());

		var finalTypeParameters = typeParameters;
		var finalVariants = variants;
		final var members = this
				.divide(inputContent, new EscapedFolder(this::foldStatement))
				.map(slice -> this.compileClassSegment(slice, name, finalTypeParameters, finalVariants))
				.flatMap(Option::stream)
				.toList();

		if (modifiersList.contains("sealed")) {
			final var enumFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + "Variant")
					.collect(new Joiner(","));

			final var generatedEnum =
					"enum " + name + "Variant {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();

			final var unionFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + joinedTypeParameters + " " + variant + ";")
					.collect(new Joiner());

			final var generatedUnion =
					templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" +
					System.lineSeparator();

			final var s = name + "Variant variant";
			final var s1 = name + "Data" + joinedTypeParameters + " data";
			final var generatedFields = this.generateStatement(s) + this.generateStatement(s1);
			fields = fields.appendString(generatedFields);

			dependencies = dependencies.appendString(generatedEnum).appendString(generatedUnion);
		} else if (type.equals("interface")) {
			final var table = this.generateStatement(name + "Table" + joinedTypeParameters + " table");
			final var data = this.generateStatement("void* data");

			final var tableMembers =
					members.stream().map(StructMember::generate).map(this::generateStatement).collect(new Joiner(""));
			final var vTable = templateString + "struct " + name + "Table {" + tableMembers + System.lineSeparator() + "};" +
												 System.lineSeparator();

			dependencies = dependencies.appendString(vTable);
			fields = fields.appendString(table).appendString(data);
		} else {
			final var joinedMembers = members
					.stream()
					.filter(member -> !(member instanceof F1RDeclaration))
					.map(StructMember::generate)
					.collect(new Joiner());

			fields = fields.appendString(joinedMembers);
		}

		final var generated =
				dependencies + templateString + "struct " + name + " {" + joinedRecordFields + fields + System.lineSeparator() +
				"};" + System.lineSeparator();
		this.structures = this.structures.addLast(generated);

		return new Some<StructMember>(new EmptyStructMember());
	}

	private String getString(Type implementee, String name, String joinedTypeParameters, String templateString) {
		final var identifier = implementee.toBaseName();
		final var thisType = name + joinedTypeParameters;
		final var s = this.generateStatement(thisType + " _this = *((" + thisType + "*) _ref)");
		final var s1 = this.generateStatement(identifier + "Data" + joinedTypeParameters + " data");
		final var s2 = this.generateStatement("data." + name + " = _this");
		final var s3 = this.generateStatement("return { " + name + "Variant, data }");
		final var conversionF1RContent = s + s1 + s2 + s3;
		return templateString + implementee.generate() + " to" + identifier + "_" + name + "(void* _ref){" +
					 conversionF1RContent + System.lineSeparator() + "}" + System.lineSeparator();
	}

	private String joinTypeParameters(List<String> typeParameters) {
		final String joinedTypeParameters;
		if (typeParameters.isEmpty()) {
			joinedTypeParameters = "";
		} else {
			joinedTypeParameters = "<" + typeParameters.stream().collect(new Joiner(", ")) + ">";
		}

		return joinedTypeParameters;
	}

	private String generateStatement(String content) {return generateStatement(1, content);}

	private List<String> splitValues(String input) {
		final var segments = input.split(Pattern.quote(","));
		final var list = Arrays.stream(segments).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
		return new JavaList<String>(list);
	}

	private boolean isIdentifier(String input) {
		final var stripped = input.strip();
		return IntStream.range(0, stripped.length()).allMatch(i -> {
			final var c = stripped.charAt(i);
			return Character.isLetter(c) || (i != 0 && Character.isDigit(c));
		});
	}

	private Option<StructMember> compileClassSegment(String input,
																									 String structName,
																									 List<String> typeParameters,
																									 List<String> variants) {
		final var stripped = input.strip();

		if (stripped.isEmpty()) {
			return new None<StructMember>();
		}

		final var maybeEnum = this.compileStructure("enum", input);
		if (maybeEnum instanceof Some<StructMember>) {
			return maybeEnum;
		}

		final var maybeInterface = this.compileStructure("interface", input);
		if (maybeInterface instanceof Some<StructMember>) {
			return maybeInterface;
		}

		final var maybeRecord = this.compileStructure("record", input);
		if (maybeRecord instanceof Some<StructMember>) {
			return maybeRecord;
		}

		final var maybeClass = this.compileStructure("class", input);
		if (maybeClass instanceof Some<StructMember>) {
			return maybeClass;
		}

		final var maybeEnumValues = this.compileEnumValues(input, structName);
		if (maybeEnumValues instanceof Some<StructMember>) {
			return maybeEnumValues;
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var maybeDeclaration = this.parseDeclaration(substring);
			if (maybeDeclaration instanceof Some<Declaration>(var declaration)) {
				return new Some<StructMember>(new Field(declaration));
			}
		}

		final var maybeMethod = this.compileMethod(structName, typeParameters, variants, stripped);
		if (maybeMethod instanceof Some<StructMember>) {
			return maybeMethod;
		}

		return new Some<StructMember>(new Placeholder(stripped));
	}

	private Option<StructMember> compileMethod(String structName,
																						 List<String> typeParameters,
																						 List<String> variants,
																						 String input) {
		final var i = input.indexOf("(");
		if (i < 0) {return new None<StructMember>();}

		final var declarationString = input.substring(0, i);
		final var substring1 = input.substring(i + 1);
		final var i1 = substring1.indexOf(")");
		if (i1 < 0) {return new None<StructMember>();}
		final var parametersString = substring1.substring(0, i1);
		final var withBraces = substring1.substring(i1 + 1).strip();

		var parameters = this
				.divide(parametersString, (state, character) -> new ValueFolder().apply(state, character))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList()
				.stream()
				.map(this::parseDeclaration)
				.flatMap(Option::stream)
				.toList();

		final var methodDeclaration = this.parseMethodDeclaration(declarationString, structName);

		Option<String> maybeCompiled = new None<String>();
		if (methodDeclaration instanceof Declaration declaration && declaration.annotations.contains("Actual")) {
			final var compiledParameters = parameters.stream().map(Declaration::generate).collect(new Joiner(", "));

			final var modifiedMethodDeclaration = declaration.mapName(name -> name + "_" + structName);
			this.functionDeclarations = this.functionDeclarations.addLast(
					modifiedMethodDeclaration.generate() + "(" + compiledParameters + ");" + System.lineSeparator());

			return new Some<StructMember>(new EmptyStructMember());
		}

		if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
			final var inputContent = withBraces.substring(1, withBraces.length() - 1);
			maybeCompiled = new Some<String>(this.compileMethodsSegments(inputContent, 1));
		}

		String outputContent;
		if (methodDeclaration instanceof Constructor) {
			final var compiled = maybeCompiled.orElse("?");
			outputContent =
					this.generateStatement(structName + " _this") + compiled + this.generateStatement("return " + "_this");
		} else if (methodDeclaration instanceof Declaration declaration) {
			parameters = parameters.addFirst(new Declaration("void*", "_ref"));

			final var joinedTypeParameters = this.joinTypeParameters(typeParameters);

			final var thisInitialization = this.generateStatement(
					structName + joinedTypeParameters + "* _this = (" + structName + joinedTypeParameters + "*) _ref");

			outputContent = thisInitialization + maybeCompiled.orElseGet(() -> {
				final var returnValueDefinition = this.generateStatement(declaration.type + " _ret");

				final var cases =
						variants.stream().map(variant -> this.generateCase(structName, declaration, variant)).collect(new Joiner());

				return returnValueDefinition + generateIndent(1) + "switch (" + "_this->variant" + ") {" + cases +
							 generateIndent(1) + "}" + this.generateStatement("return _ret");
			});
		} else {
			outputContent = "?";
		}

		final var compiledParameters = parameters.stream().map(Declaration::generate).collect(new Joiner(", "));

		final var modifiedMethodDeclaration = switch (methodDeclaration) {
			case Constructor constructor -> constructor;
			case Declaration declaration -> declaration
					.mapTypeParameters(typeParameters0 -> typeParameters0.addAll(typeParameters))
					.mapName(name -> name + "_" + structName);

			case Placeholder placeholder -> placeholder;
		};

		final var header = modifiedMethodDeclaration.generate() + "(" + compiledParameters + ")";
		final var generated = header + "{" + outputContent + System.lineSeparator() + "}" + System.lineSeparator();
		this.functions = this.functions.addLast(generated);

		final var parameterTypes = parameters.stream().map(Declaration::type).toList();

		return switch (methodDeclaration) {
			case Constructor _ -> new None<StructMember>();
			case Declaration member -> new Some<StructMember>(new F1RDeclaration(member.type, member.name, parameterTypes));
			case Placeholder placeholder -> new Some<StructMember>(placeholder);
		};

	}

	private String compileMethodsSegments(String inputContent, int indent) {
		return this.compileStatements(inputContent, input -> this.compileMethodSegment(input, indent));
	}

	private String generateCase(String structName, Declaration declaration, String variant) {
		return generateIndent(2) + "case " + variant + "Variant:" +
					 generateStatement(3, "_ret = " + declaration.name + "_" + variant + "(_this->data." + variant + ")") +
					 generateStatement(3, "break");
	}

	private MethodDeclaration parseMethodDeclaration(String declaration, String structName) {
		return this
				.parseDeclaration(declaration)
				.map(this::toInterface)
				.or(() -> this.parseConstructor(declaration, structName))
				.orElseGet(() -> new Placeholder(declaration));
	}

	private MethodDeclaration toInterface(Declaration value) {
		return value;
	}

	private Option<MethodDeclaration> parseConstructor(String declaration, String structName) {
		if (declaration.strip().equals(structName)) {
			return new Some<MethodDeclaration>(new Constructor(structName));
		} else {
			return new None<MethodDeclaration>();
		}
	}

	private Option<StructMember> compileEnumValues(String input, String structName) {
		final var stripped = input.strip();
		if (!stripped.endsWith(";")) {
			return new None<StructMember>();
		}

		final var enumValues = this
				.divide(stripped.substring(0, stripped.length() - 1),
								(state, character) -> new ValueFolder().apply(state, character))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList();

		if (!enumValues.isEmpty()) {
			var optionStream = enumValues.stream().map(enumValue -> this.compileEnumValue(structName, enumValue));
			final var areAnyInvalid =
					(boolean) optionStream.collect(new AnyMatch<Option<StructMember>>(option -> option instanceof None<StructMember>));

			if (areAnyInvalid) {
				return new None<StructMember>();
			}
		}

		return new Some<StructMember>(new EmptyStructMember());
	}

	private Option<StructMember> compileEnumValue(String structName, String enumValue) {
		if (enumValue.endsWith(")")) {
			final var substring = enumValue.substring(0, enumValue.length() - 1);
			final var i = substring.indexOf("(");
			if (i >= 0) {
				final var name = substring.substring(0, i);
				if (!this.isIdentifier(name)) {
					return new None<StructMember>();
				}

				final var substring2 = substring.substring(i + 1);
				final var generated =
						structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" +
						System.lineSeparator();

				this.globals = this.globals.addLast(generated);
				return new Some<StructMember>(new EmptyStructMember());
			}
		}

		return new None<StructMember>();
	}

	private String compileMethodSegment(String input, int indent) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			return generateIndent(indent) + this.compileMethodStatement(substring) + ";";
		}

		final var maybeIf = this.compileConditional("if", indent, stripped);
		if (maybeIf instanceof Some<String>(var result)) {
			return result;
		}

		final var maybeWhile = this.compileConditional("while", indent, stripped);
		if (maybeWhile instanceof Some<String>(var result)) {
			return result;
		}

		if (stripped.startsWith("else ")) {
			final var substring = stripped.substring("else ".length()).strip();
			if (substring.startsWith("{") && substring.endsWith("}")) {
				final var substring1 = substring.substring(1, substring.length() - 1);
				return generateIndent(indent) + "else {" + this.compileMethodsSegments(substring1, indent + 1) +
							 generateIndent(indent) + "}";
			} else {
				return generateIndent(indent) + "else " + this.compileMethodSegment(substring, indent);
			}
		}

		if (stripped.startsWith("//")) {
			return generateIndent(indent) + stripped;
		}

		return System.lineSeparator() + "\t" + wrap(stripped);
	}

	private Option<String> compileConditional(String type, int indent, String input) {
		if (input.startsWith(type)) {
			final var substring = input.substring(type.length()).strip();
			if (substring.startsWith("(")) {
				final var afterConditionStart = substring.substring(1).strip();

				final var divisions = this
						.divide(afterConditionStart, new EscapedFolder(new ConditionEndLocator()))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.toList();

				if (divisions.size() < 2) {
					return new None<String>();
				}

				final var first = divisions.getFirst();
				final var last = this.joinStrings("", divisions.subList(1, divisions.size()));

				if (!first.endsWith(")")) {
					return new None<String>();
				}
				final var condition = first.substring(0, first.length() - 1);

				if (last.startsWith("{") && last.endsWith("}")) {
					final var content = last.substring(1, last.length() - 1);
					return new Some<String>(
							generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" +
							this.compileMethodsSegments(content, indent + 1) + generateIndent(indent) + "}");
				}
			}
		}

		return new None<String>();
	}

	private String compileMethodStatement(String input) {
		final var stripped = input.strip();
		if (stripped.equals("break")) {
			return "break";
		}

		if (stripped.startsWith("return ")) {
			return "return " + this.compileExpressionOrPlaceholder(stripped.substring("return ".length()));
		}

		final var i = stripped.indexOf("=");
		if (i >= 0) {
			final var destination = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			return this
								 .compileExpression(destination)
								 .or(() -> this.parseDeclaration(destination).map(Declaration::generate))
								 .orElseGet(() -> wrap(destination)) + " = " + this.compileExpressionOrPlaceholder(substring1);
		}

		final var maybeInvokable = this.compileInvokable(stripped);
		if (maybeInvokable instanceof Some<String>(var value)) {
			return value;
		}

		final var instance = this.post(stripped, "++");
		if (instance instanceof Some<String>(var x)) {
			return x;
		}

		final var instance0 = this.post(stripped, "--");
		if (instance0 instanceof Some<String>(var x)) {
			return x;
		}

		final var maybeDeclaration = this.parseDeclaration(input);
		if (maybeDeclaration instanceof Some<Declaration>(var declaration)) {
			return declaration.generate();
		}

		return wrap(stripped);
	}

	private Option<String> post(String stripped, String slice) {
		if (stripped.endsWith(slice)) {
			final var instance = stripped.substring(0, stripped.length() - 2);
			return new Some<String>(this.compileExpressionOrPlaceholder(instance) + slice);
		}

		return new None<String>();
	}

	private String compileExpressionOrPlaceholder(String input) {
		return this.compileExpression(input).orElseGet(() -> wrap(input));
	}

	private Option<String> compileExpression(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("switch ")) {
			return new Some<String>("_switch");
		}

		final var i2 = stripped.lastIndexOf("::");
		if (i2 >= 0) {
			final var substring = stripped.substring(0, i2);
			final var name = stripped.substring(i2 + 2).strip();
			if (this.isIdentifier(name)) {
				final var compiled = this.compileExpressionOrPlaceholder(substring);
				final var functionalInterfaceName = "F?";
				return new Some<String>(
						functionalInterfaceName + " { alloc(" + compiled + "), " + functionalInterfaceName + "Table { " + name +
						" }}");
			}
		}

		if (stripped.startsWith("'") && stripped.endsWith("'")) {
			return new Some<String>(stripped);
		}

		final var maybeLambda = this.compileLambda(stripped);
		if (maybeLambda instanceof Some<String>) {
			return maybeLambda;
		}

		final var i3 = stripped.indexOf("instanceof");
		if (i3 >= 0) {
			final var substring = stripped.substring(0, i3);
			final var substring1 = stripped.substring(i3 + "instanceof".length()).strip();
			final var maybeInstance = this.compileExpression(substring);
			if (maybeInstance instanceof Some<String>(var instance)) {
				final var i4 = substring1.indexOf("<");
				final String substring2;
				if (i4 >= 0) {
					substring2 = substring1.substring(0, i4);
				} else {
					substring2 = substring1;
				}

				return new Some<String>(instance + ".variant = ?." + substring2 + "Variant");
			}
		}

		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var instanceString = stripped.substring(0, i);
			final var memberName = stripped.substring(i + 1).strip();
			if (this.isIdentifier(memberName)) {
				final var maybeInstance = this.compileExpression(instanceString);
				if (maybeInstance instanceof Some<String>(var value)) {
					final String instance;
					instance = value;
					final String generated;
					if (instance.equals("this")) {
						generated = "_this->" + memberName;
					} else {
						generated = instance + "." + memberName;
					}

					return new Some<String>(generated);
				}
			}
		}

		final var maybeInvokable = this.compileInvokable(stripped);
		if (maybeInvokable instanceof Some<String>) {
			return maybeInvokable;
		}

		final var maybeOperator = this
				.compileOperator(stripped, "==")
				.or(() -> this.compileOperator(stripped, "!="))
				.or(() -> this.compileOperator(stripped, "<"))
				.or(() -> this.compileOperator(stripped, "+"))
				.or(() -> this.compileOperator(stripped, "-"))
				.or(() -> this.compileOperator(stripped, "&&"))
				.or(() -> this.compileOperator(stripped, "||"))
				.or(() -> this.compileOperator(stripped, ">="));

		if (maybeOperator instanceof Some<String>) {
			return maybeOperator;
		}

		if (this.isIdentifier(stripped)) {
			return new Some<String>(stripped);
		}

		if (stripped.startsWith("!")) {
			final var substring = stripped.substring(1);
			final var maybeInstance = this.compileExpression(substring);
			if (maybeInstance instanceof Some<String>(var instance)) {
				return new Some<String>("!" + instance);
			}
		}

		if (this.isNumber(stripped)) {
			return new Some<String>(stripped);
		}

		if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
			return new Some<String>(stripped);
		}

		return new None<String>();
	}

	private Option<String> compileLambda(String stripped) {
		final var i1 = stripped.indexOf("->");
		if (i1 >= 0) {
			final var beforeContent = stripped.substring(0, i1).strip();
			final var maybeWithBraces = stripped.substring(i1 + 2).strip();

			List<String> params;
			if (this.isIdentifier(beforeContent)) {
				params = new JavaList<String>().addLast(beforeContent);
			} else if (beforeContent.startsWith("(") && beforeContent.endsWith(")")) {
				final var substring = beforeContent.substring(1, beforeContent.length() - 1);
				params =
						this.divide(substring, new ValueFolder()).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
			} else {
				return new None<String>();
			}

			if (maybeWithBraces.startsWith("{") && maybeWithBraces.endsWith("}")) {
				final var content = maybeWithBraces.substring(1, maybeWithBraces.length() - 1);
				final var compiled = this.compileMethodsSegments(content, 1);

				final var generatedName = this.generateName();

				var paramList = params.stream().map(param -> "auto " + param).toList().addFirst("void* _ref");

				final var joined = this.joinStrings(", ", paramList);

				this.functions = this.functions.addLast(
						"auto " + generatedName + "(" + joined + "){" + compiled + System.lineSeparator() + "}" +
						System.lineSeparator());

				return new Some<String>(generatedName);
			} else {
				final var generatedName = this.generateName();

				this.functions = this.functions.addLast(
						"auto " + generatedName + "(void* _ref, auto " + beforeContent + ")" + "{" +
						this.generateStatement("return " + this.compileExpressionOrPlaceholder(maybeWithBraces)) +
						System.lineSeparator() + "}" + System.lineSeparator());

				return new Some<String>(generatedName);
			}
		}

		return new None<String>();
	}

	private String generateName() {
		final var generatedName = "lambda" + this.counter;
		this.counter++;
		return generatedName;
	}

	private Option<String> compileOperator(String input, String operator) {
		if (input.length() < 3) {
			return new None<String>();
		}

		if (!input.contains(operator)) {
			return new None<String>();
		}

		var i1 = -1;
		var depth = 0;
		var i = 0;
		while (i < input.length() - 1) {
			final var c = input.charAt(i);
			if (c == operator.charAt(0)) {
				if (depth == 0) {
					i1 = i;
					break;
				}
			}

			if (c == '(') {
				depth++;
			}
			if (c == ')') {
				depth--;
			}
			i++;
		}

		if (i1 >= 0) {
			final var leftString = input.substring(0, i1);
			final var right = input.substring(i1 + operator.length());
			if (this.compileExpression(leftString) instanceof Some<String>(var leftCompiled)) {
				if (this.compileExpression(right) instanceof Some<String>(var rightCompiled)) {
					return new Some<String>(leftCompiled + " " + operator + " " + rightCompiled);
				}
			}
		}

		return new None<String>();
	}

	private Option<String> compileInvokable(String stripped) {
		if (stripped.endsWith(")")) {
			final var withoutEnd = stripped.substring(0, stripped.length() - 1);

			final var callerStart = this.findCallerStart(withoutEnd);

			if (callerStart >= 0) {
				final var callerString = withoutEnd.substring(0, callerStart);
				final var arguments = withoutEnd.substring(callerStart + 1);
				final var joinedArguments = this
						.divide(arguments, new EscapedFolder(new ValueFolder()))
						.map(this::compileExpressionOrPlaceholder)
						.collect(new Joiner(", "));

				final var maybeCaller = this.compileCaller(callerString);
				if (maybeCaller instanceof Some<String>(var value)) {
					return new Some<String>(value + "(" + joinedArguments + ")");
				}
			}
		}

		return new None<String>();
	}

	private int findCallerStart(String withoutEnd) {
		var callerStart = -1;
		var depth = 0;
		var i = 0;
		while (i < withoutEnd.length()) {
			final var c = withoutEnd.charAt(i);
			if (c == '(') {
				if (depth == 0) {
					callerStart = i;
				}

				depth++;
			}
			if (c == ')') {
				depth--;
			}
			i++;
		}
		return callerStart;
	}

	private boolean isNumber(String input) {
		if (input.startsWith("-")) {
			return this.allDigits(input.substring(1));
		}
		return this.allDigits(input);
	}

	private boolean allDigits(String input) {
		return IntStream.range(0, input.length()).mapToObj(input::charAt).allMatch(Character::isDigit);
	}

	private Option<String> compileCaller(String input) {
		final var stripped = input.strip();
		final var maybeExpression = this.compileExpression(stripped);
		if (maybeExpression instanceof Some<String>) {
			return maybeExpression;
		}

		if (stripped.startsWith("new ")) {
			final var type = stripped.substring("new ".length());
			return new Some<String>("new_" + this.compileType(type));
		}

		return new None<String>();
	}

	private Option<Declaration> parseDeclaration(String input) {
		final var stripped = input.strip();
		final var nameSeparator = stripped.lastIndexOf(" ");
		if (nameSeparator >= 0) {
			final var beforeName = stripped.substring(0, nameSeparator).strip();
			final var name = stripped.substring(nameSeparator + 1).strip();

			final var typeSeparator = this.findTypeSeparator(beforeName);

			if (!this.isIdentifier(name)) {
				return new None<Declaration>();
			}

			if (typeSeparator < 0) {
				final var type = this.compileType(beforeName);
				return new Some<Declaration>(new Declaration(type, name));
			}

			var beforeType = beforeName.substring(0, typeSeparator).strip();

			List<String> copy = new JavaList<String>();
			if (beforeType.endsWith(">")) {
				final var substring = beforeType.substring(0, beforeType.length() - 1);
				final var i = substring.indexOf("<");
				if (i >= 0) {
					final var substring2 = substring.substring(i + 1);
					copy = this.splitValues(substring2);
					beforeType = substring.substring(0, i);
				}
			}

			List<String> annotations = new JavaList<String>();
			final var i = beforeType.lastIndexOf("\n");
			if (i >= 0) {
				annotations = new JavaList<String>(Arrays
																							 .stream(beforeType.substring(0, i).split(Pattern.quote("\n")))
																							 .filter(slice -> !slice.isEmpty())
																							 .map(slice -> slice.substring(1))
																							 .map(String::strip)
																							 .toList());

				beforeType = beforeType.substring(i + 1).strip();
			}

			if (this.isIdentifier(name)) {
				return new Some<Declaration>(new Declaration(annotations,
																										 copy,
																										 new Some<String>(beforeType),
																										 this.compileType(beforeName.substring(typeSeparator + 1)),
																										 name));
			}
		}

		return new None<Declaration>();
	}

	private int findTypeSeparator(String beforeName) {
		var typeSeparator = -1;
		var depth = 0;
		var i = 0;
		while (i < beforeName.length()) {
			final var c = beforeName.charAt(i);
			if (c == ' ' && depth == 0) {
				typeSeparator = i;
			}
			if (c == '<') {
				depth++;
			}
			if (c == '>') {
				depth--;
			}
			i++;
		}
		return typeSeparator;
	}

	private String compileType(String input) {
		return this.parseType(input).generate();
	}

	private Type parseType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("boolean") || stripped.equals("Boolean")) {
			return PrimitiveType.Int;
		}

		if (stripped.equals("void")) {
			return PrimitiveType.Void;
		}

		if (stripped.endsWith("[]")) {
			final var slice = stripped.substring(0, stripped.length() - 2);
			final var type = this.parseType(slice);
			return new PointerType(type);
		}

		if (stripped.equals("String")) {
			return new PointerType(PrimitiveType.Char);
		}

		if (stripped.endsWith(">")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i = substring.indexOf("<");
			if (i >= 0) {
				final var base = substring.substring(0, i);
				final var parameters = substring.substring(i + 1);

				final var list = this.divide(parameters, new ValueFolder()).map(this::parseType).toList();
				return new TemplateType(base, list);
			}
		}

		if (stripped.equals("Character")) {
			return PrimitiveType.Char;
		}

		if (this.isIdentifier(stripped)) {
			return new Identifier(stripped);
		}

		return new Placeholder(stripped);
	}
}
