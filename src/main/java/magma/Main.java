package magma;

import magma.JavaImpl.Paths;
import magma.Lib.IOError;
import magma.Lib.None;
import magma.Lib.Ok;
import magma.Lib.Optional;
import magma.Lib.Path;
import magma.Lib.Some;

import java.io.Closeable;
import java.util.Objects;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Main {
	private interface Collector<T, C> {
		C createInitial();

		C fold(C current, T element);
	}

	private interface List<T> {
		List<T> add(T element);

		List<T> clear();

		int size();

		Stream<T> stream();

		Optional<T> get(int index);

		boolean isEmpty();

		List<T> addFirst(T element);

		List<T> addLast(T element);

		boolean contains(T element);

		Optional<T> getFirst();

		Optional<List<T>> subList(int start, int end);

		List<T> addAll(List<T> elements);

		Optional<List<T>> addAllAt(int index, List<T> elements);

		Optional<T> getLast();

		List<T> copy();
	}

	private sealed interface Definable extends JMethodHeader permits Definition, Placeholder {
		String generate();
	}

	private sealed interface JMethodHeader permits JConstructor, Definable {}

	@interface Actual {}

	private sealed interface CExpression permits CIdentifier, Content {
		String generate();
	}

	private interface Head<T> {
		Optional<T> next();
	}

	private record Stream<T>(Head<T> head) {
		<R> Stream<R> map(Function<T, R> mapper) {
			return new Stream<R>(() -> this.head.next().map(mapper));
		}

		public Stream<T> filter(Predicate<T> predicate) {
			final Head<T> sourceHead = this.head;
			return new Stream<T>(() -> {
				while (true) {
					Optional<T> nextValue = sourceHead.next();
					if (nextValue instanceof Some<T>(T value)) {
						if (predicate.test(value)) {
							return Optional.of(value);
						}
						// Continue to next element
					} else {
						return Optional.empty();
					}
				}
			});
		}

		public <C> C collect(Collector<T, C> collector) {
			return this.reduce(collector.createInitial(), collector::fold);
		}

		public <C> C reduce(C initial, BiFunction<C, T, C> folder) {
			C accumulator = initial;
			Optional<T> current = this.head.next();
			while (current instanceof Some<T>(T value)) {
				accumulator = folder.apply(accumulator, value);
				current = this.head.next();
			}
			return accumulator;
		}
	}

	private static final class Array<T> implements Closeable {
		private final T[] elements;

		private Array(T[] elements) {this.elements = elements;}

		@Override
		public void close() {
			MemUtils.free(this.elements);
		}

		public int length() {
			return this.elements.length;
		}

		public void set(int index, T element) {
			if (index < this.elements.length) {
				this.elements[index] = element;
			}
		}

		public Optional<T> get(int index) {
			if (index < this.elements.length) {
				return new Some<T>(this.elements[index]);
			} else {
				return new None<T>();
			}
		}

		public T[] elements() {return this.elements;}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null || obj.getClass() != this.getClass()) {
				return false;
			}
			Array that = (Array) obj;
			return Objects.equals(this.elements, that.elements);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.elements);
		}

		@Override
		public String toString() {
			return "Array[" + "elements=" + this.elements + ']';
		}

	}

	private static class MemUtils {
		@SuppressWarnings("unchecked")
		private static <T> Array<T> alloc(int length) {
			return new Array<T>(malloc(length));
		}

		@Actual
		private static <T> T[] malloc(int length) {
			return (T[]) new Object[length];
		}

		@Actual
		public static <T> void free(T[] elements) {
		}

		@Actual
		public static <T> void memCopy(Array<T> src, int srcPos, Array<T> dest, int destPos, int length) {
			System.arraycopy(src.elements, srcPos, dest.elements, destPos, length);
		}
	}

	private static final class ArrayList<T> implements List<T> {
		private Array<T> elements;
		private int size;

		private ArrayList(Array<T> elements, int size) {
			this.elements = elements;
			this.size = size;
		}

		public ArrayList() {
			this(MemUtils.alloc(10), 0);
		}

		private void ensureCapacity(int minCapacity) {
			if (minCapacity <= this.elements.length()) {
				return;
			}

			int newCapacity = this.elements.length() * 2;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}

			Array<T> newElements = MemUtils.alloc(newCapacity);
			MemUtils.memCopy(this.elements, 0, newElements, 0, this.size);
			this.elements = newElements;
		}

		@Override
		public List<T> add(T element) {
			this.ensureCapacity(this.size + 1);
			this.elements.set(this.size, element);
			this.size++;
			return this;
		}

		@Override
		public List<T> clear() {
			this.elements = MemUtils.alloc(10);
			this.size = 0;
			return this;
		}

		@Override
		public int size() {
			return this.size;
		}

		@Override
		public Stream<T> stream() {
			return new Stream<T>(new ListHead<T>(this));
		}

		@Override
		public Optional<T> get(int index) {
			if (index < 0 || index >= this.size) {
				return Optional.empty();
			}

			return this.elements.get(index);
		}

		@Override
		public boolean isEmpty() {
			return this.size() == 0;
		}

		@Override
		public List<T> addFirst(T element) {
			this.ensureCapacity(this.size + 1);
			// Shift all elements one position to the right
			MemUtils.memCopy(this.elements, 0, this.elements, 1, this.size);
			this.elements.set(0, element);
			this.size++;
			return this;
		}

		@Override
		public List<T> addLast(T element) {
			return this.add(element);
		}

		@Override
		public boolean contains(T element) {
			for (int i = 0; i < this.size; i++) {
				if (Objects.equals(this.elements.elements[i], element)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public Optional<T> getFirst() {
			return this.get(0);
		}

		@Override
		public Optional<List<T>> subList(int start, int end) {
			if (start < 0 || end > this.size || start > end) {
				return Optional.empty();
			}
			int subSize = end - start;
			Array<T> newElements = MemUtils.alloc(Math.max(10, subSize));
			MemUtils.memCopy(this.elements, start, newElements, 0, subSize);
			return Optional.of(new ArrayList<T>(newElements, subSize));
		}

		@Override
		public List<T> addAll(List<T> elements) {
			int elementsSize = elements.size();
			this.ensureCapacity(this.size + elementsSize);

			for (int i = 0; i < elementsSize; i++) {
				this.elements.set(this.size + i, elements.get(i).orElse(null));
			}

			this.size += elementsSize;
			return this;
		}

		@Override
		public Optional<List<T>> addAllAt(int index, List<T> elements) {
			if (index < 0 || index > this.size) {
				return Optional.empty();
			}

			int elementsSize = elements.size();
			this.ensureCapacity(this.size + elementsSize);

			// Shift elements to the right to make room
			MemUtils.memCopy(this.elements, index, this.elements, index + elementsSize, this.size - index);

			// Copy inserted elements
			for (int i = 0; i < elementsSize; i++) {
				this.elements.set(index + i, elements.get(i).orElse(null));
			}

			this.size += elementsSize;
			return Optional.of(this);
		}

		@Override
		public Optional<T> getLast() {
			return this.get(this.size - 1);
		}

		@Override
		public List<T> copy() {
			final ArrayList<T> list = new ArrayList<T>();
			if (this.size >= 0) {
				MemUtils.memCopy(this.elements, 0, list.elements, 0, this.size);
			}
			return list;
		}
	}

	private static class ParseState {
		private final Stack<List<String>> beforeStatements;
		private List<String> structs;
		private List<String> afterStatements;
		private List<String> functions;
		private int counter;

		public ParseState() {
			this.functions = new ArrayList<String>();
			this.structs = new ArrayList<String>();

			this.beforeStatements = new Stack<List<String>>();
			this.beforeStatements.add(new ArrayList<String>());

			this.afterStatements = new ArrayList<String>();
			this.counter = -1;
		}

		public ParseState addFunction(String func) {
			this.functions = this.functions.add(func);
			return this;
		}

		public ParseState addStruct(String struct) {
			this.structs = this.structs.add(struct);
			return this;
		}

		public String generateAnonymousFunctionName() {
			this.counter++;
			return "__lambda" + this.counter + "__";
		}

		public ParseState addAfterStatement(String statement) {
			this.afterStatements = this.afterStatements.add(statement);
			return this;
		}

		public List<String> popAfterStatements() {
			final List<String> copy = this.afterStatements.copy();
			this.afterStatements = this.afterStatements.clear();
			return copy;
		}

		public void addBeforeStatement(String beforeStatement) {
			final List<String> peek = this.beforeStatements.pop();
			final List<String> added = peek.add(beforeStatement);
			this.beforeStatements.push(added);
		}

		public List<String> popBeforeStatements() {
			return this.beforeStatements.pop();
		}

		public ParseState pushBeforeStatements() {
			this.beforeStatements.push(new ArrayList<String>());
			return this;
		}
	}

	private static class DivideState {
		private final String input;
		private List<String> segments;
		private StringBuilder buffer;
		private int depth;
		private int index;

		public DivideState(String input) {
			this.input = input;
			this.buffer = new StringBuilder();
			this.depth = 0;
			this.segments = new ArrayList<String>();
			this.index = 0;
		}

		private Stream<String> stream() {
			return this.segments.stream();
		}

		private DivideState enter() {
			this.depth = this.depth + 1;
			return this;
		}

		private DivideState exit() {
			this.depth = this.depth - 1;
			return this;
		}

		private boolean isShallow() {
			return this.depth == 1;
		}

		private boolean isLevel() {
			return this.depth == 0;
		}

		private DivideState append(char c) {
			this.buffer.append(c);
			return this;
		}

		private DivideState advance() {
			this.segments = this.segments.add(this.buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		public Optional<Tuple<DivideState, Character>> pop() {
			if (this.index >= this.input.length()) {
				return Optional.empty();
			}
			final char next = this.input.charAt(this.index);
			this.index++;
			return Optional.of(new Tuple<DivideState, Character>(this, next));
		}

		public Optional<Tuple<DivideState, Character>> popAndAppendToTuple() {
			return this.pop().map(tuple -> new Tuple<DivideState, Character>(tuple.left.append(tuple.right), tuple.right));
		}

		public Optional<DivideState> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(tuple -> tuple.left);
		}

		public Optional<Character> peek() {
			if (this.index < this.input.length()) {
				return Optional.of(this.input.charAt(this.index));
			} else {
				return Optional.empty();
			}
		}
	}

	public record Tuple<A, B>(A left, B right) {}

	private record Definition(List<String> annotations, String type, String name) implements Definable {
		@Override
		public String generate() {
			return this.type + " " + this.name;
		}
	}

	private record Placeholder(String input) implements Definable {
		@Override
		public String generate() {
			return wrap(this.input);
		}
	}

	private record JConstructor(String name) implements JMethodHeader {}

	private record Content(String value) implements CExpression {
		@Override
		public String generate() {
			return this.value;
		}
	}

	private record CIdentifier(String value) implements CExpression {
		@Override
		public String generate() {
			return this.value;
		}
	}

	private static class Streams {
		public static <T> Stream<T> from(T[] elements) {
			return new Stream<T>(new ArrayHead<T>(elements));
		}
	}

	private static class ListHead<T> implements Head<T> {
		private final List<T> self;
		private int index;

		public ListHead(List<T> self) {
			this.self = self;
			this.index = 0;
		}

		@Override
		public Optional<T> next() {
			if (this.index < this.self.size()) {
				return this.self.get(this.index++);
			}

			return Optional.empty();
		}
	}

	private static class ListCollector<T> implements Collector<T, List<T>> {
		public ListCollector() {}

		@Override
		public List<T> createInitial() {
			return new ArrayList<T>();
		}

		@Override
		public List<T> fold(List<T> current, T element) {
			return current.add(element);
		}
	}

	private static class Joiner implements Collector<String, String> {
		private final String delimiter;

		public Joiner(String delimiter) {this.delimiter = delimiter;}

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

	private static class ArrayHead<T> implements Head<T> {
		private final T[] elements;
		private int counter;

		public ArrayHead(T[] elements) {
			this.elements = elements;
			this.counter = 0;
		}

		@Override
		public Optional<T> next() {
			if (this.counter < this.elements.length) {
				final T element = this.elements[this.counter];
				this.counter++;
				return new Some<T>(element);
			} else {
				return new None<T>();
			}
		}
	}

	public static void main(String[] args) {
		if (run() instanceof Some<IOError>(IOError value)) {
			System.out.println(value.display());
		}
	}

	private static Optional<IOError> run() {
		final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		final Path target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");

		if (source.readString() instanceof Ok<String, IOError>(String input)) {
			final Path targetParent = target.getParent();

			if (!targetParent.exists()) {
				return targetParent.createDirectories();
			}

			final String output = "// File generated from '" + source + "'. This is not source code!\n" + compile(input);
			return target.writeString(output);
		}

		return Optional.empty();
	}

	private static String compile(String input) {
		StringJoiner joiner = new StringJoiner("");
		ParseState state = new ParseState();
		List<String> list = divide(input, Main::foldStatement).collect(new ListCollector<String>());
		int i = 0;
		while (i < list.size()) {
			String input1 = list.get(i).orElse(null);
			Tuple<String, ParseState> s = compileRootSegment(input1, state);
			joiner.add(s.left);
			state = s.right;
			i++;
		}

		final String joined = joiner.toString();
		final String joinedStructs = state.structs.stream().collect(new Joiner(""));
		final String joinedFunctions = state.functions.stream().collect(new Joiner(""));

		return joinedStructs + joinedFunctions + joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" +
					 System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}";
	}

	private static Stream<String> divide(String input, BiFunction<DivideState, Character, DivideState> folder) {
		Tuple<DivideState, Boolean> current = new Tuple<DivideState, Boolean>(new DivideState(input), true);
		while (current.right) {
			current = foldCycle(current.left, folder);
		}
		return current.left.advance().stream();
	}

	private static Tuple<DivideState, Boolean> foldCycle(DivideState state,
																											 BiFunction<DivideState, Character, DivideState> folder) {
		final Optional<Tuple<DivideState, Character>> maybeNext = state.pop();
		if (maybeNext instanceof Some<Tuple<DivideState, Character>>(Tuple<DivideState, Character> value)) {
			return new Tuple<DivideState, Boolean>(foldEscaped(value.left, value.right, folder), true);
		}

		return new Tuple<DivideState, Boolean>(state, false);
	}

	private static DivideState foldEscaped(DivideState state,
																				 char next,
																				 BiFunction<DivideState, Character, DivideState> folder) {
		return foldSingleQuotes(state, next).or(() -> foldDoubleQuotes(state, next)).orElseGet(() -> folder.apply(state,
																																																							next));
	}

	private static Optional<DivideState> foldSingleQuotes(DivideState state, char next) {
		if (next != '\'') {
			return Optional.empty();
		}

		final DivideState appended = state.append(next);
		return appended.popAndAppendToTuple().flatMap(Main::foldEscaped).flatMap(DivideState::popAndAppendToOption);
	}

	private static Optional<DivideState> foldEscaped(Tuple<DivideState, Character> tuple) {
		if (tuple.right == '\\') {
			return tuple.left.popAndAppendToOption();
		} else {
			return Optional.of(tuple.left);
		}
	}

	private static Optional<DivideState> foldDoubleQuotes(DivideState state, char next) {
		if (next != '\"') {
			return Optional.empty();
		}

		Tuple<DivideState, Boolean> current = new Tuple<DivideState, Boolean>(state.append(next), true);
		while (current.right) {
			current = foldUntilDoubleQuotes(current.left);
		}
		return Optional.of(current.left);
	}

	private static Tuple<DivideState, Boolean> foldUntilDoubleQuotes(DivideState state) {
		final Optional<Tuple<DivideState, Character>> maybeNext = state.popAndAppendToTuple();
		if (!(maybeNext instanceof Some<Tuple<DivideState, Character>>(Tuple<DivideState, Character> value))) {
			return new Tuple<DivideState, Boolean>(state, false);
		}

		final DivideState nextState = value.left;
		final char nextChar = value.right;

		if (nextChar == '\\') {
			return new Tuple<DivideState, Boolean>(nextState.popAndAppendToOption().orElse(nextState), true);
		}
		if (nextChar == '\"') {
			return new Tuple<DivideState, Boolean>(nextState, false);
		}
		return new Tuple<DivideState, Boolean>(nextState, true);
	}

	private static DivideState foldStatement(DivideState state, char c) {
		final DivideState appended = state.append(c);
		if (c == ';' && appended.isLevel()) {
			return appended.advance();
		}
		if (c == '}' && appended.isShallow()) {
			final Optional<Character> peeked = appended.peek();
			final DivideState withPeeked;
			if (peeked instanceof Some<Character>(Character value) && value == ';') {
				withPeeked = appended.popAndAppendToOption().orElse(appended);
			} else {
				withPeeked = appended;
			}
			return withPeeked.advance().exit();
		}
		if (c == '{' || c == '(') {
			return appended.enter();
		}
		if (c == '}' || c == ')') {
			return appended.exit();
		}
		return appended;
	}

	private static Tuple<String, ParseState> compileRootSegment(String input, ParseState state) {
		final String stripped = input.strip();
		if (stripped.isEmpty()) {
			return new Tuple<String, ParseState>("", state);
		}
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return new Tuple<String, ParseState>("", state);
		}

		return compileStructure(stripped, "class", state).orElseGet(() -> new Tuple<String, ParseState>(wrap(stripped),
																																																		state));
	}

	private static Optional<Tuple<String, ParseState>> compileStructure(String input, String type, ParseState state) {
		final int i = input.indexOf(type + " ");
		if (i < 0) {
			return Optional.empty();
		}

		final String afterKeyword = input.substring(i + (type + " ").length());
		final int contentStart = afterKeyword.indexOf("{");

		if (contentStart < 0) {
			return Optional.empty();
		}

		final String beforeContent = afterKeyword.substring(0, contentStart).strip();
		String withoutPermits = beforeContent;
		List<String> variants = new ArrayList<String>();

		final int permitsIndex = beforeContent.indexOf("permits");
		if (permitsIndex >= 0) {
			final String slice = beforeContent.substring(permitsIndex + "permits".length());
			variants = divide(slice,
												Main::foldValue).map(String::strip).filter(segment -> !segment.isEmpty()).collect(new ListCollector<String>());
			withoutPermits = beforeContent.substring(0, permitsIndex);
		}

		String maybeWithExtends = withoutPermits.strip();
		final int extendsIndex = maybeWithExtends.indexOf("extends");
		if (extendsIndex >= 0) {
			maybeWithExtends = maybeWithExtends.substring(0, extendsIndex).strip();
		}

		String maybeWithImplements = maybeWithExtends.strip();
		final int implementsIndex = maybeWithImplements.indexOf("implements");
		if (implementsIndex >= 0) {
			maybeWithImplements = maybeWithImplements.substring(0, implementsIndex).strip();
		}

		String beforeMaybeParams = maybeWithImplements.strip();
		String recordFields = "";
		if (maybeWithImplements.endsWith(")")) {
			final String slice = maybeWithImplements.substring(0, maybeWithImplements.length() - 1);
			final int beforeParams = slice.indexOf("(");
			if (beforeParams >= 0) {
				beforeMaybeParams = slice.substring(0, beforeParams).strip();
				final String substring = slice.substring(beforeParams + 1);
				recordFields = compileValues(substring, Main::compileParameter, "");
			}
		}

		String name = beforeMaybeParams.strip();
		List<String> typeParameters = new ArrayList<String>();
		if (beforeMaybeParams.endsWith(">")) {
			final String withoutEnd = beforeMaybeParams.substring(0, beforeMaybeParams.length() - 1);
			final int i1 = withoutEnd.indexOf("<");
			if (i1 >= 0) {
				name = withoutEnd.substring(0, i1).strip();
				final String arguments = withoutEnd.substring(i1 + "<".length());
				typeParameters = divide(arguments, Main::foldValue).map(String::strip).collect(new ListCollector<String>());
			}
		}

		final String afterContent = afterKeyword.substring(contentStart + "{".length()).strip();

		if (!afterContent.endsWith("}")) {
			return Optional.empty();
		}
		final String content = afterContent.substring(0, afterContent.length() - "}".length());

		final List<String> segments = divide(content, Main::foldStatement).collect(new ListCollector<String>());

		StringBuilder inner = new StringBuilder();
		ParseState outer = state;
		int j = 0;
		while (j < segments.size()) {
			String segment = segments.get(j).orElse(null);
			Tuple<String, ParseState> compiled = compileClassSegment(segment, name, outer);
			inner.append(compiled.left);
			outer = compiled.right;
			j++;
		}

		String templateString;
		if (typeParameters.isEmpty()) {
			templateString = "";
		} else {
			final String collect =
					"<" + typeParameters.stream().map(slice -> "typeparam " + slice).collect(new Joiner(", ")) + ">";

			final String templateValues = collect + System.lineSeparator();
			templateString = "template " + templateValues;
		}

		String generatedSubStructs = "";
		if (!variants.isEmpty()) {
			final String enumFields = variants.stream().map(slice -> generateIndent(1) + slice).collect(new Joiner(","));

			final String joinedTypeParameters;
			if (typeParameters.isEmpty()) {
				joinedTypeParameters = "";
			} else {
				joinedTypeParameters = "<" + typeParameters.stream().collect(new Joiner(", ")) + ">";
			}

			final String unionFields = variants.stream().map(slice -> slice + joinedTypeParameters + " " +
																																slice.toLowerCase()).map(content1 -> generateStatement(
					content1,
					1)).collect(new Joiner());

			generatedSubStructs =
					"enum " + name + "Tag {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator() +
					templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" +
					System.lineSeparator();

			recordFields += generateStatement(name + "Tag tag", 1);
			recordFields += generateStatement(name + "Data" + joinedTypeParameters + " data", 1);
		}

		final String generated =
				generatedSubStructs + templateString + "struct " + name + " {" + recordFields + inner + System.lineSeparator() +
				"};" + System.lineSeparator();
		return Optional.of(new Tuple<String, ParseState>("", outer.addStruct(generated)));
	}

	private static String compileValues(String input, Function<String, String> mapper) {
		return compileValues(input, mapper, ", ");
	}

	private static String compileValues(String input, Function<String, String> mapper, String delimiter) {
		return divide(input, Main::foldValue).map(mapper).collect(new Joiner(delimiter));
	}

	private static String compileParameter(String input1) {
		if (input1.isEmpty()) {
			return "";
		}
		return generateField(input1).orElseGet(() -> wrap(input1));
	}

	private static Optional<String> generateField(String input) {
		return compileDefinition(input).map(Definable::generate).map(content -> generateStatement(content, 1));
	}

	private static String generateStatement(String content, int depth) {
		return generateSegment(content + ";", depth);
	}

	private static String generateSegment(String content, int depth) {
		return generateIndent(depth) + content;
	}

	private static String generateIndent(int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}

	private static DivideState foldValue(DivideState state, char next) {
		if (next == ',' && state.isLevel()) {
			return state.advance();
		}
		final DivideState appended = state.append(next);
		if (next == '-') {
			final Optional<Character> peeked = appended.peek();
			if (peeked instanceof Some<Character>(Character value)) {
				if (value.equals('>')) {
					return appended.popAndAppendToOption().orElse(appended);
				}
			}
		}

		if (next == '(' || next == '<') {
			return appended.enter();
		}
		if (next == ')' || next == '>') {
			return appended.exit();
		}
		return appended;
	}

	private static Tuple<String, ParseState> compileClassSegment(String input, String name, ParseState state) {
		final String stripped = input.strip();
		if (stripped.isEmpty()) {
			return new Tuple<String, ParseState>("", state);
		}
		return compileClassSegmentValue(stripped, name, state);
	}

	private static Tuple<String, ParseState> compileClassSegmentValue(String input, String name, ParseState state) {
		if (input.isEmpty()) {
			return new Tuple<String, ParseState>("", state);
		}

		return compileStructure(input, "class", state).or(() -> compileStructure(input,
																																						 "record",
																																						 state)).or(() -> compileStructure(input,
																																																							 "interface",
																																																							 state)).or(
				() -> compileField(input, state)).or(() -> compileMethod(input, name, state)).orElseGet(() -> {
			final String generated = generateSegment(wrap(input), 1);
			return new Tuple<String, ParseState>(generated, state);
		});
	}

	private static Optional<Tuple<String, ParseState>> compileMethod(String input, String name, ParseState state) {
		final int paramStart = input.indexOf("(");
		if (paramStart < 0) {
			return Optional.empty();
		}

		final String beforeParams = input.substring(0, paramStart).strip();
		final String withParams = input.substring(paramStart + 1);

		final int paramEnd = withParams.indexOf(")");
		if (paramEnd < 0) {
			return Optional.empty();
		}

		final JMethodHeader methodHeader = compileMethodHeader(beforeParams);
		final String inputParams = withParams.substring(0, paramEnd);
		final String withBraces = withParams.substring(paramEnd + 1).strip();

		final String outputParams = compileParameters(inputParams);
		final String outputMethodHeader = transformMethodHeader(methodHeader, name).generate() + "(" + outputParams + ")";

		final String outputBodyWithBraces;
		if (withBraces.equals(";") || isPlatformDependentMethod(methodHeader)) {
			outputBodyWithBraces = ";";
		} else if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
			final String inputBody = withBraces.substring(1, withBraces.length() - 1);
			final Tuple<List<String>, ParseState> compiledBody = compileMethodStatements(state, 0, inputBody);

			List<String> statements = compiledBody.left;
			if (Objects.requireNonNull(methodHeader) instanceof JConstructor) {
				statements =
						statements.addFirst(generateStatement(name + " this", 1)).addLast(generateStatement("return this", 1));
			}

			final String joined = statements.stream().collect(new Joiner(""));
			outputBodyWithBraces = "{" + joined + System.lineSeparator() + "}";
		} else {
			return Optional.empty();
		}

		final String generated = outputMethodHeader + outputBodyWithBraces + System.lineSeparator();
		return Optional.of(new Tuple<String, ParseState>("", state.addFunction(generated)));
	}

	private static boolean isPlatformDependentMethod(JMethodHeader methodHeader) {
		return methodHeader instanceof Definition definition && definition.annotations.contains("Actual");
	}

	private static Definable transformMethodHeader(JMethodHeader methodHeader, String name) {
		if (Objects.requireNonNull(methodHeader) instanceof JConstructor(String name1)) {
			return new Definition(new ArrayList<String>(), name1, "new_" + name1);
		} else if (methodHeader instanceof Definition definition) {
			return new Definition(new ArrayList<String>(), definition.type, definition.name + "_" + name);
		} else if (methodHeader instanceof Placeholder placeholder) {
			return placeholder;
		} else {
			return new Placeholder("?");
		}
	}

	private static JMethodHeader compileMethodHeader(String beforeParams) {
		return compileDefinition(beforeParams).<JMethodHeader>map(definable -> definable).or(() -> compileConstructor(
				beforeParams)).orElseGet(() -> new Placeholder(beforeParams));
	}

	private static String compileParameters(String input) {
		if (input.isEmpty()) {
			return "";
		}
		return compileValues(input, slice -> compileDefinition(slice).map(Definable::generate).orElse(""));
	}

	private static Tuple<String, ParseState> compileMethodSegment(String input, int depth, ParseState state) {
		final String stripped = input.strip();
		if (stripped.isEmpty()) {
			return new Tuple<String, ParseState>("", state);
		}

		final Tuple<String, ParseState> tuple = compileMethodSegmentValue(stripped, depth, state);
		return new Tuple<String, ParseState>(generateSegment(tuple.left, depth), tuple.right);
	}

	private static Tuple<String, ParseState> compileMethodSegmentValue(String input, int depth, ParseState state) {
		final String stripped = input.strip();
		final Optional<Tuple<String, ParseState>> compiled = compileBlock(state, stripped, depth);
		if (compiled instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
			return value;
		}

		final Optional<Tuple<String, ParseState>> maybeIf = compileConditional("if", depth, state, stripped);
		if (maybeIf instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
			return value;
		}

		final Optional<Tuple<String, ParseState>> maybeWhile = compileConditional("while", depth, state, stripped);
		if (maybeWhile instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
			return value;
		}

		if (stripped.startsWith("else")) {
			final String substring = stripped.substring("else".length());
			final Tuple<String, ParseState> result = compileMethodSegmentValue(substring, depth, state);
			return new Tuple<String, ParseState>("else " + result.left, result.right);
		}

		if (stripped.endsWith(";")) {
			final String slice = stripped.substring(0, stripped.length() - 1);
			final Tuple<String, ParseState> result = compileMethodStatementValue(slice, state);
			return new Tuple<String, ParseState>(result.left + ";", result.right);
		}

		return new Tuple<String, ParseState>(wrap(stripped), state);
	}

	private static Optional<Tuple<String, ParseState>> compileConditional(String type,
																																				int depth,
																																				ParseState state,
																																				String stripped) {
		if (!stripped.startsWith(type)) {
			return Optional.empty();
		}
		final String withoutPrefix = stripped.substring(type.length());

		final List<String> conditionEnd =
				divide(withoutPrefix, Main::foldConditionEnd).collect(new ListCollector<String>());
		if (conditionEnd.size() < 2) {
			return Optional.empty();
		}
		final String withConditionEnd = conditionEnd.getFirst().orElse(null);
		final String substring1 = withConditionEnd.substring(0, withConditionEnd.length() - 1).strip();
		final String body =
				conditionEnd.subList(1, conditionEnd.size()).orElse(new ArrayList<String>()).stream().collect(new Joiner(""));

		if (!substring1.startsWith("(")) {
			return Optional.empty();
		}
		final String expression = substring1.substring(1);

		final Tuple<String, ParseState> condition = compileExpression(expression, state);
		final Tuple<String, ParseState> compiledBody = compileMethodSegmentValue(body, depth, condition.right);
		return Optional.of(new Tuple<String, ParseState>(type + " (" + condition.left + ") " + compiledBody.left,
																										 compiledBody.right));
	}

	private static Optional<Tuple<String, ParseState>> compileBlock(ParseState state, String input, int depth) {
		if (!input.startsWith("{") || !input.endsWith("}")) {
			return Optional.empty();
		}
		final Tuple<List<String>, ParseState> result =
				compileMethodStatements(state, depth, input.substring(1, input.length() - 1));
		final String generated = "{" + result.left().stream().collect(new Joiner("")) + generateIndent(depth) + "}";
		return Optional.of(new Tuple<String, ParseState>(generated, result.right()));
	}

	private static Tuple<List<String>, ParseState> compileMethodStatements(ParseState state, int depth, String content) {
		List<String> compiled = new ArrayList<String>();
		ParseState current = state;
		List<String> list = divide(content, Main::foldStatement).collect(new ListCollector<String>());
		int i = 0;
		while (i < list.size()) {
			String s = list.get(i).orElse(null);

			Tuple<String, ParseState> string = compileMethodSegment(s, depth + 1, current.pushBeforeStatements());
			compiled = compiled.addAll(string.right.popBeforeStatements()).add(string.left);

			current = string.right;
			i++;
		}

		final List<String> removed = current.popAfterStatements();
		compiled = compiled.addAllAt(0, removed).orElse(new ArrayList<String>());

		return new Tuple<List<String>, ParseState>(compiled, current);
	}

	private static DivideState foldConditionEnd(DivideState state, char c) {
		final DivideState appended = state.append(c);
		if (c == ')') {
			final DivideState exited = appended.exit();
			if (exited.isLevel()) {
				return exited.advance();
			}
		}

		if (c == '(') {
			return appended.enter();
		}
		return appended;
	}

	private static Tuple<String, ParseState> compileMethodStatementValue(String input, ParseState state) {
		if (input.startsWith("return ")) {
			final String substring = input.substring("return ".length());
			final Tuple<String, ParseState> result = compileExpression(substring, state);
			return new Tuple<String, ParseState>("return " + result.left, result.right);
		}

		if (input.endsWith("++")) {
			final String slice = input.substring(0, input.length() - 2);
			final Optional<Tuple<String, ParseState>> temp =
					tryCompileExpression(slice, state).map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(),
																																												tuple.right));
			if (temp instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
				return new Tuple<String, ParseState>(value.left + "++", value.right);
			}
		}

		final Optional<Tuple<String, ParseState>> invokableResult = compileInvokable(state, input);
		if (invokableResult instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
			return value;
		}

		final int i = input.indexOf("=");
		if (i >= 0) {
			final String destinationString = input.substring(0, i);
			final String source = input.substring(i + 1);
			final Tuple<String, ParseState> destinationResult =
					compileDefinition(destinationString).map(Definition::generate).map(generated -> new Tuple<String, ParseState>(
							generated,
							state)).orElseGet(() -> compileExpression(destinationString, state));

			final Tuple<String, ParseState> sourceResult = compileExpression(source, destinationResult.right);
			return new Tuple<String, ParseState>(destinationResult.left + " = " + sourceResult.left, sourceResult.right);
		}

		return compileDefinition(input).map(value -> new Tuple<String, ParseState>(value.generate(),
																																							 state)).orElseGet(() -> new Tuple<String, ParseState>(
				wrap(input),
				state));
	}

	private static Tuple<String, ParseState> compileExpression(String input, ParseState state) {
		return tryCompileExpression(input, state).map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(),
																																												 tuple.right)).orElseGet(() -> new Tuple<String, ParseState>(
				wrap(input),
				state));
	}

	private static Optional<Tuple<CExpression, ParseState>> tryCompileExpression(String input, ParseState state) {
		final String stripped = input.strip();
		final Optional<Tuple<String, ParseState>> charResult = compileChar(stripped, state);
		if (charResult instanceof Some<Tuple<String, ParseState>>) {
			return charResult.map(Main::wrapInContent);
		}

		final Optional<Tuple<String, ParseState>> stringResult = compileString(stripped, state);
		if (stringResult instanceof Some<Tuple<String, ParseState>>) {
			return stringResult.map(Main::wrapInContent);
		}

		final Optional<Tuple<String, ParseState>> notResult = compileNot(state, stripped);
		if (notResult instanceof Some<Tuple<String, ParseState>>) {
			return notResult.map(Main::wrapInContent);
		}

		final Optional<Tuple<String, ParseState>> lambdaResult = compileLambda(state, stripped);
		if (lambdaResult instanceof Some<Tuple<String, ParseState>>) {
			return lambdaResult.map(Main::wrapInContent);
		}

		final Optional<Tuple<String, ParseState>> instanceOfResult = compileInstanceOf(state, stripped);
		if (instanceOfResult instanceof Some<Tuple<String, ParseState>>) {
			return instanceOfResult.map(Main::wrapInContent);
		}

		final Optional<Tuple<String, ParseState>> left = compileInvokable(state, stripped);
		if (left instanceof Some<Tuple<String, ParseState>>) {
			return left.map(Main::wrapInContent);
		}

		final Optional<Tuple<String, ParseState>> methodReferenceResult = compileMethodReference(state, stripped);
		if (methodReferenceResult instanceof Some<Tuple<String, ParseState>>) {
			return methodReferenceResult.map(Main::wrapInContent);
		}

		final Optional<Tuple<String, ParseState>> fieldAccessResult = compileFieldAccess(state, stripped);
		if (fieldAccessResult instanceof Some<Tuple<String, ParseState>>) {
			return fieldAccessResult.map(Main::wrapInContent);
		}

		return getOr(state, stripped).map(Main::wrapInContent).or(() -> compileIdentifier(stripped,
																																											state)).or(() -> compileNumber(
				stripped,
				state).map(Main::wrapInContent));
	}

	private static Optional<Tuple<String, ParseState>> getOr(ParseState state, String stripped) {
		return compileOperator(stripped, "+", state).or(() -> compileOperator(stripped,
																																					"-",
																																					state)).or(() -> compileOperator(stripped,
																																																					 ">=",
																																																					 state)).or(() -> compileOperator(
				stripped,
				"<",
				state)).or(() -> compileOperator(stripped, "!=", state)).or(() -> compileOperator(stripped,
																																													"==",
																																													state)).or(() -> compileOperator(
				stripped,
				"&&",
				state)).or(() -> compileOperator(stripped, "||", state));
	}

	private static Tuple<CExpression, ParseState> wrapInContent(Tuple<String, ParseState> tuple) {
		return new Tuple<CExpression, ParseState>(new Content(tuple.left), tuple.right);
	}

	private static Optional<Tuple<String, ParseState>> compileString(String stripped, ParseState state) {
		if (isString(stripped)) {
			return Optional.of(new Tuple<String, ParseState>(stripped, state));
		}
		return Optional.empty();
	}

	private static Optional<Tuple<String, ParseState>> compileFieldAccess(ParseState state, String stripped) {
		final int separator = stripped.lastIndexOf(".");
		if (separator < 0) {
			return Optional.empty();
		}

		final String substring = stripped.substring(0, separator);
		final String name = stripped.substring(separator + 1).strip();

		if (!isIdentifier(name)) {
			return Optional.empty();
		}
		final Optional<Tuple<String, ParseState>> maybeResult =
				tryCompileExpression(substring, state).map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(),
																																													tuple.right));
		if (!(maybeResult instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value))) {
			return Optional.empty();
		}
		return Optional.of(new Tuple<String, ParseState>(value.left + "." + name, value.right));
	}

	private static Optional<Tuple<String, ParseState>> compileMethodReference(ParseState state, String stripped) {
		final int separator = stripped.lastIndexOf("::");
		if (separator >= 0) {
			final String substring = stripped.substring(0, separator);
			final String name = stripped.substring(separator + 2).strip();
			if (isIdentifier(name)) {
				final Optional<String> maybeResult = compileType(substring);
				if (maybeResult instanceof Some<String>(String value)) {
					return Optional.of(new Tuple<String, ParseState>(name + "_" + value, state));
				}
			}
		}
		return Optional.empty();
	}

	private static Optional<Tuple<String, ParseState>> compileInstanceOf(ParseState state, String stripped) {
		final int instanceOfIndex = stripped.indexOf("instanceof");
		if (instanceOfIndex < 0) {
			return Optional.empty();
		}
		final String beforeOperator = stripped.substring(0, instanceOfIndex).strip();
		String afterOperator = stripped.substring(instanceOfIndex + "instanceof".length()).strip();

		final Optional<Tuple<CExpression, ParseState>> maybeResult = tryCompileExpression(beforeOperator, state);

		if (!(maybeResult instanceof Some<Tuple<CExpression, ParseState>>(
				Tuple<CExpression, ParseState> value
		))) {
			return Optional.empty();
		}

		final int typeArgumentsStart = afterOperator.indexOf("<");
		String variantName;
		if (typeArgumentsStart >= 0) {
			variantName = afterOperator.substring(0, typeArgumentsStart);
		} else {
			variantName = afterOperator;
		}

		String parameters = "";
		if (afterOperator.endsWith(")")) {
			final String slice = afterOperator.substring(0, afterOperator.length() - 1);
			final int paramStart = slice.indexOf("(");
			if (paramStart >= 0) {
				final String paramString = slice.substring(paramStart + 1);
				String result1 = "";
				if (!paramString.isEmpty()) {
					result1 = compileValues(paramString, slice1 -> compileDefinition(slice1).map(definition -> {
						final String generated = definition.generate();
						return generated + " = _cast." + definition.name;
					}).map(destructMember -> generateStatement(destructMember, 2)).orElse(""));
				}

				parameters = result1;
				afterOperator = afterOperator.substring(0, paramStart);
			}
		}

		final CExpression target = value.left;
		ParseState maybeWithBeforeStatement = value.right;
		final String targetAlias;

		if (!(target instanceof CIdentifier)) {
			final String alias = generateStatement("??? _temp = " + target.generate(), 1);
			maybeWithBeforeStatement.addBeforeStatement(alias);
			targetAlias = "_temp";
		} else {
			targetAlias = target.generate();
		}


		final String content = afterOperator + " _cast = " + targetAlias + ".data." + variantName.toLowerCase();
		final String statement = generateStatement(content, 2) + parameters;
		final ParseState parseState = maybeWithBeforeStatement.addAfterStatement(statement);
		return Optional.of(new Tuple<String, ParseState>(targetAlias + ".tag == " + variantName, parseState));
	}

	private static Optional<Tuple<String, ParseState>> compileChar(String stripped, ParseState state) {
		if (isABoolean(stripped)) {
			return Optional.of(new Tuple<String, ParseState>(stripped, state));
		}
		return Optional.empty();
	}

	private static boolean isABoolean(String stripped) {
		return stripped.startsWith("'") && stripped.endsWith("'") && stripped.length() <= 4;
	}

	private static Optional<Tuple<String, ParseState>> compileNot(ParseState state, String stripped) {
		if (stripped.startsWith("!")) {
			final String slice = stripped.substring(1);
			final Optional<Tuple<String, ParseState>> maybeResult =
					tryCompileExpression(slice, state).map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(),
																																												tuple.right));
			if (maybeResult instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
				return Optional.of(new Tuple<String, ParseState>("!" + value.left, value.right));
			}
		}
		return Optional.empty();
	}

	private static Optional<Tuple<String, ParseState>> compileInvokable(ParseState state, String stripped) {
		if (!stripped.endsWith(")")) {
			return Optional.empty();
		}
		final String slice = stripped.substring(0, stripped.length() - 1);

		final List<String> segments = findArgStart(slice).collect(new ListCollector<String>());
		if (segments.size() < 2) {
			return Optional.empty();
		}

		final String callerWithExt =
				segments.subList(0, segments.size() - 1).orElse(new ArrayList<String>()).stream().collect(new Joiner(""));
		if (!callerWithExt.endsWith("(")) {
			return Optional.empty();
		}
		final String caller = callerWithExt.substring(0, callerWithExt.length() - 1);
		final String arguments = segments.getLast().orElse(null);

		final Optional<Tuple<String, ParseState>> maybeCallerResult = compileCaller(state, caller);
		if (!(maybeCallerResult instanceof Some<Tuple<String, ParseState>>(
				Tuple<String, ParseState> value
		))) {
			return Optional.empty();
		}

		final Tuple<StringJoiner, ParseState> reduce = divide(arguments,
																													Main::foldValue).collect(new ListCollector<String>()).stream().reduce(
				new Tuple<StringJoiner, ParseState>(new StringJoiner(", "), value.right),
				(tuple, s) -> mergeExpression(tuple.left, tuple.right, s));
		final String collect = reduce.left.toString();
		return Optional.of(new Tuple<String, ParseState>(value.left + "(" + collect + ")", reduce.right));
	}

	private static Tuple<StringJoiner, ParseState> mergeExpression(StringJoiner joiner,
																																 ParseState state,
																																 String segment) {
		Tuple<String, ParseState> result = compileExpression(segment, state);
		final StringJoiner add = joiner.add(result.left);
		return new Tuple<StringJoiner, ParseState>(add, result.right);
	}

	private static Stream<String> findArgStart(String input) {
		return divide(input, (state, c) -> {
			final DivideState appended = state.append(c);
			if (c == '(') {
				final DivideState entered = appended.enter();
				if (entered.isShallow()) {
					return entered.advance();
				} else {
					return entered;
				}
			}
			if (c == ')') {
				return appended.exit();
			}
			return appended;
		});
	}

	private static Optional<Tuple<String, ParseState>> compileLambda(ParseState state, String stripped) {
		final int i1 = stripped.indexOf("->");
		if (i1 < 0) {
			return Optional.empty();
		}

		final String beforeArrow = stripped.substring(0, i1).strip();

		final String outputParams;
		if (isIdentifier(beforeArrow)) {
			outputParams = "auto " + beforeArrow;
		} else if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
			final String withoutParentheses = beforeArrow.substring(1, beforeArrow.length() - 1);
			final String[] array = withoutParentheses.split(Pattern.quote(","));
			outputParams = Streams.from(array).map(String::strip).filter(slice -> !slice.isEmpty()).map(slice -> "auto " +
																																																					 slice).collect(
					new Joiner(", "));

		} else {
			return Optional.empty();
		}

		final String body = stripped.substring(i1 + 2).strip();
		final Tuple<String, ParseState> bodyResult = compileLambdaBody(state, body);

		final String generatedName = bodyResult.right.generateAnonymousFunctionName();
		final String s1 = "auto " + generatedName + "(" + outputParams + ") " + bodyResult.left + System.lineSeparator();
		return Optional.of(new Tuple<String, ParseState>(generatedName, bodyResult.right.addFunction(s1)));
	}

	private static Tuple<String, ParseState> compileLambdaBody(ParseState state, String body) {
		final Optional<Tuple<String, ParseState>> maybeBlock = compileBlock(state, body, 0);
		if (maybeBlock instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
			return value;
		}

		final Tuple<String, ParseState> result = compileExpression(body, state);
		final String s = generateStatement("return " + result.left, 1);
		final String s2 = "{" + s + generateIndent(0) + "}";
		return new Tuple<String, ParseState>(s2, result.right);
	}

	private static Optional<Tuple<String, ParseState>> compileCaller(ParseState state, String caller) {
		if (caller.startsWith("new ")) {
			final Optional<String> newType = compileType(caller.substring("new ".length()));
			if (newType instanceof Some<String>(String value)) {
				return Optional.of(new Tuple<String, ParseState>("new_" + value, state));
			}
		}

		return tryCompileExpression(caller, state).map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(),
																																													tuple.right));
	}

	private static Optional<Tuple<CExpression, ParseState>> compileIdentifier(String input, ParseState state) {
		if (isIdentifier(input)) {
			return Optional.of(new Tuple<CExpression, ParseState>(new CIdentifier(input), state));
		}
		return Optional.empty();
	}

	private static Optional<Tuple<String, ParseState>> compileNumber(String stripped, ParseState state) {
		if (isNumber(stripped)) {
			return Optional.of(new Tuple<String, ParseState>(stripped, state));
		}
		return Optional.empty();
	}

	private static Optional<Tuple<String, ParseState>> compileOperator(String input, String operator, ParseState state) {
		final List<String> segments =
				divide(input, (state1, next) -> foldOperator(operator, state1, next)).collect(new ListCollector<String>());

		if (segments.size() < 2) {
			return Optional.empty();
		}

		final String left = segments.getFirst().orElse(null);
		final String right =
				segments.subList(1, segments.size()).orElse(new ArrayList<String>()).stream().collect(new Joiner(operator));

		final Optional<Tuple<String, ParseState>> maybeLeftResult =
				tryCompileExpression(left, state).map(tuple1 -> new Tuple<String, ParseState>(tuple1.left.generate(),
																																											tuple1.right));

		if (!(maybeLeftResult instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value))) {
			return Optional.empty();
		}

		final Optional<Tuple<String, ParseState>> maybeRightResult =
				tryCompileExpression(right, value.right).map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(),
																																														tuple.right));
		if (maybeRightResult instanceof Some<Tuple<String, ParseState>>(
				Tuple<String, ParseState> rightResult
		)) {
			final String generated = value.left + " " + operator + " " + rightResult.left;
			return Optional.of(new Tuple<String, ParseState>(generated, rightResult.right));
		}

		return Optional.empty();
	}

	private static DivideState foldOperator(String operator, DivideState state1, Character next) {
		if (next != operator.charAt(0)) {
			return state1.append(next);
		}

		final Optional<Character> peeked = state1.peek();
		if (operator.length() >= 2 && peeked instanceof Some<Character>(Character value)) {
			if (value == operator.charAt(1)) {
				return state1.pop().map(inner -> inner.left).orElse(state1).advance();
			}
		}

		return state1.advance();
	}

	private static boolean isString(String stripped) {
		if (stripped.length() < 2) {
			return false;
		}

		final boolean hasDoubleQuotes = stripped.startsWith("\"") && stripped.endsWith("\"");
		if (!hasDoubleQuotes) {
			return false;
		}

		final String content = stripped.substring(1, stripped.length() - 1);
		return areAllDoubleQuotesEscaped(content);
	}

	private static boolean areAllDoubleQuotesEscaped(String input) {
		return IntStream.range(0, input.length()).allMatch(i -> {
			final char c = input.charAt(i);
			if (c != '\"') {
				return true;
			}
			if (i == 0) {
				return false;
			}
			char previous = input.charAt(i - 1);
			return previous == '\\';
		});
	}

	private static boolean isNumber(String input) {
		return IntStream.range(0, input.length()).allMatch(i -> Character.isDigit(input.charAt(i)));
	}

	private static boolean isIdentifier(String input) {
		return IntStream.range(0, input.length()).allMatch(i -> {
			final char next = input.charAt(i);
			final boolean isValidDigit = i != 0 && Character.isDigit(next);
			return Character.isLetter(next) || isValidDigit;
		});
	}

	private static Optional<JMethodHeader> compileConstructor(String beforeParams) {
		final int separator = beforeParams.lastIndexOf(" ");
		if (separator < 0) {
			return Optional.empty();
		}

		final String name = beforeParams.substring(separator + " ".length());
		return Optional.of(new JConstructor(name));
	}

	private static Optional<Tuple<String, ParseState>> compileField(String input, ParseState state) {
		if (input.endsWith(";")) {
			final String substring = input.substring(0, input.length() - ";".length()).strip();
			final Optional<String> s = generateField(substring);
			if (s instanceof Some<String>(String value)) {
				return Optional.of(new Tuple<String, ParseState>(value, state));
			}
		}

		return Optional.empty();
	}

	private static Optional<Definition> compileDefinition(String input) {
		final String stripped = input.strip();
		final int index = stripped.lastIndexOf(" ");
		if (index < 0) {
			return Optional.empty();
		}

		final String beforeName = stripped.substring(0, index).strip();
		final String name = stripped.substring(index + " ".length()).strip();
		if (!isIdentifier(name)) {
			return Optional.empty();
		}

		final List<String> segments = divide(beforeName, Main::foldTypeSeparator).collect(new ListCollector<String>());
		if (segments.size() < 2) {
			return compileType(beforeName).map(type -> new Definition(new ArrayList<String>(), type, name));
		}

		final String withoutLast =
				segments.subList(0, segments.size() - 1).orElse(new ArrayList<String>()).stream().collect(new Joiner(" "));
		final List<String> annotations = findAnnotations(withoutLast);

		final String typeString = segments.getLast().orElse(null);
		return compileType(typeString).map(type -> new Definition(annotations, type, name));
	}

	private static List<String> findAnnotations(String withoutLast) {
		final int i = withoutLast.lastIndexOf("\n");
		if (i < 0) {
			return new ArrayList<String>();
		}

		final String[] slices = withoutLast.substring(0, i).strip().split(Pattern.quote("\n"));
		return Streams.from(slices).map(String::strip).filter(slice -> slice.startsWith("@")).map(slice -> slice.substring(1)).collect(
				new ListCollector<String>());
	}

	private static DivideState foldTypeSeparator(DivideState state, Character c) {
		if (c == ' ' && state.isLevel()) {
			return state.advance();
		}

		final DivideState appended = state.append(c);
		if (c == '<') {
			return appended.enter();
		}
		if (c == '>') {
			return appended.exit();
		}
		return appended;
	}

	private static Optional<String> compileType(String input) {
		final String stripped = input.strip();
		if (stripped.equals("public")) {
			return Optional.empty();
		}

		if (stripped.endsWith(">")) {
			final String withoutEnd = stripped.substring(0, stripped.length() - 1);
			final int argumentStart = withoutEnd.indexOf("<");
			if (argumentStart >= 0) {
				final String base = withoutEnd.substring(0, argumentStart);
				final String argumentsString = withoutEnd.substring(argumentStart + "<".length());

				final String arguments =
						compileValues(argumentsString, slice -> compileType(slice).orElseGet(() -> wrap(slice)));
				return Optional.of(base + "<" + arguments + ">");
			}
		}

		if (stripped.endsWith("[]")) {
			final String slice = stripped.substring(0, stripped.length() - 2);
			return compileType(slice).map(result -> result + "*");
		}

		if (stripped.equals("String")) {
			return Optional.of("char*");
		}
		if (stripped.equals("int")) {
			return Optional.of("int");
		}

		if (isIdentifier(stripped)) {
			return Optional.of(stripped);
		}
		return Optional.of(wrap(stripped));
	}

	private static String wrap(String input) {
		final String replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}
