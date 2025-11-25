package magma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Main {
	private enum CPrimitiveType implements CType {
		Void("void"), Char("char"), Int("int");

		private final String content;

		CPrimitiveType(String content) {
			this.content = content;
		}

		@Override
		public String generate() {
			return this.content;
		}

		@Override
		public String toBaseName() {
			return this.content;
		}

		@Override
		public List<String> extractIdentifiers() {
			return Lists.empty();
		}
	}

	private enum JPrimitiveType implements JType {
		Int("int"), Void("void"), Boolean("bool"), Char("char"), Var("var");

		private final String name;

		JPrimitiveType(String name) {this.name = name;}

		@Override
		public String stringify() {
			return this.name;
		}
	}

	private enum Operator {
		Equals("==", JPrimitiveType.Boolean), NotEquals("!=", JPrimitiveType.Boolean),
		LessThan("<", JPrimitiveType.Boolean), Add("+", JPrimitiveType.Int), Subtract("-", JPrimitiveType.Int),
		And("&&", JPrimitiveType.Boolean), Or("||", JPrimitiveType.Boolean),
		GreaterThanOrEquals(">=", JPrimitiveType.Boolean);

		private final String value;
		private final JType type;

		Operator(String value, JType type) {
			this.value = value;
			this.type = type;
		}

		public String generate() {
			return this.value;
		}

		public JType getType() {
			return this.type;
		}
	}

	private interface Head<T> {
		Option<T> next();
	}

	private interface List<T> {
		Iter<T> iter();

		boolean isEmpty();

		List<T> addLast(T element);

		boolean contains(T element);

		List<T> addFirst(T element);

		List<T> addAllLast(List<T> elements);

		int size();

		T getFirst();

		List<T> subList(int start, int end);

		List<T> clear();

		List<T> removeLast();

		List<T> mapLast(F1R<T, T> mapper);

		Iter<T> iterReversed();

		List<T> copy();

		List<T> removeElement(T element);
	}

	private interface Path {
		Option<IOError> writeString(String output);

		Result<String, IOError> readString();

		Option<Path> getParent();

		Option<IOError> createDirectories();

		Path resolve(String child);
	}

	private interface FR<T> {
		T apply();
	}

	private sealed interface Option<T> permits None, Some {
		<R> Option<R> map(F1R<T, R> mapper);

		T orElse(T other);

		<R> Option<R> flatMap(F1R<T, Option<R>> mapper);

		T orElseGet(FR<T> other);

		Iter<T> iter();

		Option<T> or(FR<Option<T>> other);

		Tuple<Boolean, T> toTuple(FR<T> other);
	}

	private interface F1R<T0, R> {
		R apply(T0 value);
	}

	private sealed interface Result<T, X> permits Err, Ok {
		<R> Result<R, X> mapValue(F1R<T, R> mapper);
	}

	private sealed interface CNamedType extends CType permits CTemplateType, Identifier {
		String getName();
	}

	private interface CType {
		String generate();

		String toBaseName();

		List<String> extractIdentifiers();
	}

	private sealed interface JMethodDeclaration permits JConstructor, JDeclaration, Placeholder {}

	private sealed interface CStructMember permits EmptyStructMember, CField, CFunctionDeclaration, Placeholder {
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

	private interface IOError {
		String display();
	}

	private interface CAssignable {
		String generate();
	}

	private interface JType {
		String stringify();
	}

	private interface JAssignable {}

	private interface JExpression extends JCaller, JAssignable {}

	private interface CExpression extends CAssignable {}

	private interface JCaller {}

	private sealed interface CDefinable permits CDeclaration, CFunctionDeclaration, Placeholder {
		String generate();

		List<String> extractIdentifiers();

		CDefinable mapTypeParameters(F1R<List<String>, List<String>> mapper);

		CDefinable mapName(F1R<String, String> mapper);
	}

	private sealed interface CStructureOrUnion permits CStructure, CUnion {
		String generate();

		List<String> findDependencies();

		String findName();
	}

	private interface JObjectMember {}

	private interface F1<T> {
		void apply(T element);
	}

	@Actual
	private record JavaIOError(IOException e) implements IOError {
		@Override
		public String display() {
			final var writer = new StringWriter();
			this.e.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}

	private static class StringBuilders {
		public static StringBuilder empty() {
			return new StringBuilder(Lists.empty());
		}
	}

	private record StringBuilder(List<Character> list) {
		public StringBuilder appendChar(char next) {
			return new StringBuilder(this.list.addLast(next));
		}

		public StringBuilder clear() {
			return new StringBuilder(this.list.clear());
		}

		@Override
		public String toString() {
			return this.list.iter().map(String::valueOf).collect(new Joiner());
		}
	}

	private record Iter<T>(Head<T> head) {
		public static <T> Iter<T> of(T value) {
			return new Iter<T>(new SingleHead<T>(value));
		}

		public static <T> Iter<T> empty() {
			return new Iter<T>(new EmptyHead<T>());
		}

		public <R> Iter<R> map(F1R<T, R> mapper) {
			return new Iter<R>(new MapHead<T, R>(this.head, mapper));
		}

		public <R> R fold(R initial, F2R<R, T, R> folder) {
			var current = initial;
			while (true) {
				var finalCurrent = current;
				final var tuple =
						this.head.next().map(element -> folder.apply(finalCurrent, element)).toTuple(() -> finalCurrent);
				if (tuple.left) current = tuple.right;
				else return current;
			}
		}

		public <C> C collect(Collector<T, C> collector) {
			return this.fold(collector.createInitial(), collector::fold);
		}

		public List<T> toList() {
			return this.collect(new ListCollector<T>());
		}

		public Iter<T> filter(F1R<T, Boolean> predicate) {
			return this.flatMap(element -> {
				if (predicate.apply(element)) return new Iter<T>(new SingleHead<T>(element));
				return new Iter<T>(new EmptyHead<T>());
			});
		}

		private <R> Iter<R> flatMap(F1R<T, Iter<R>> mapper) {
			return new Iter<R>(new FlatMapHead<T, R>(this.head, mapper));
		}

		public Option<T> next() {
			return this.head.next();
		}

		public void forEach(F1<T> consumer) {
			while (true) {
				final var next = this.head.next();
				if (next instanceof Some<T>(var next0)) consumer.apply(next0);
				else break;
			}
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
			} else return new None<Integer>();
		}
	}

	private static class Lists {
		@Actual
		public static <T> List<T> empty() {
			return new JavaList<T>();
		}
	}

	@Actual
	private static final class JavaList<T> implements List<T> {
		private final java.util.List<T> nativeList;

		private JavaList(java.util.List<T> nativeList) {
			this.nativeList = new ArrayList<T>(nativeList);
		}

		private JavaList() {
			this(new ArrayList<T>());
		}

		@Override
		public String toString() {
			return this.nativeList.toString();
		}

		public JavaList<T> addLast(T element) {
			this.nativeList.add(element);
			return this;
		}

		@Override
		public Iter<T> iter() {
			return new Iter<Integer>(new RangeHead(this.nativeList.size())).map(this.nativeList::get);
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
		public List<T> addAllLast(List<T> elements) {
			return elements.iter().fold(this, JavaList::addLast);
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

		@Override
		public List<T> removeLast() {
			this.nativeList.removeLast();
			return this;
		}

		@Override
		public List<T> mapLast(F1R<T, T> mapper) {
			if (!this.nativeList.isEmpty()) {
				final var last = this.nativeList.getLast();
				final var newLast = mapper.apply(last);
				this.nativeList.set(this.nativeList.size() - 1, newLast);
			}

			return this;
		}

		@Override
		public Iter<T> iterReversed() {
			return new Iter<Integer>(new RangeHead(this.nativeList.size()))
					.map(index -> this.nativeList.size() - index - 1)
					.map(this.nativeList::get);
		}

		@Override
		public List<T> copy() {
			return new JavaList<T>(new ArrayList<T>(this.nativeList));
		}

		@Override
		public List<T> removeElement(T element) {
			this.nativeList.remove(element);
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
			this.buffer = StringBuilders.empty();
			this.depth = 0;
			this.segments = Lists.empty();
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
			} else return new None<Character>();
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

		private Iter<String> stream() {
			return this.segments.iter();
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
			if (this.index < this.input.length()) return new Some<Character>(this.input.charAt(this.index));

			return new None<Character>();
		}
	}

	private record CPointerType(CType type) implements CType {
		@Override
		public String generate() {
			return this.type.generate() + "*";
		}

		@Override
		public String toBaseName() {
			return this.type.toBaseName() + "_ptr";
		}

		@Override
		public List<String> extractIdentifiers() {
			return this.type.extractIdentifiers();
		}
	}

	private record CTemplateType(String base, List<CType> typeArguments) implements CNamedType {
		private CTemplateType(String base, List<CType> typeArguments) {
			this.base = base;
			this.typeArguments = typeArguments;

			assert !typeArguments.isEmpty();
		}

		@Override
		public String generate() {
			final var typeArguments = this.typeArguments.iter().map(CType::generate).collect(new Joiner(", "));
			return this.base + "<" + typeArguments + ">";
		}

		@Override
		public String toBaseName() {
			return this.base;
		}

		@Override
		public List<String> extractIdentifiers() {
			return this.typeArguments.iter().map(CType::extractIdentifiers).flatMap(List::iter).toList().addLast(this.base);
		}

		@Override
		public String getName() {
			return this.base;
		}
	}

	private record CQuantity(CExpression expression) implements CExpression {
		@Override
		public String generate() {
			return "(" + this.expression.generate() + ")";
		}
	}

	private record CDereference(CExpression expression) implements CExpression {
		@Override
		public String generate() {
			return "*" + this.expression.generate();
		}
	}

	private record Identifier(String value) implements CNamedType, JType, JExpression, CExpression {
		private static boolean isIdentifier(String input) {
			final var stripped = input.strip();
			if (stripped.isEmpty() || stripped.equals("return")) return false;

			return IntStream.range(0, stripped.length()).allMatch(i -> {
				final var c = stripped.charAt(i);
				return c == '_' || isLetter(c) || (i != 0 && isDigit(c));
			});
		}

		@Override
		public String generate() {
			return this.value;
		}

		@Override
		public String toBaseName() {
			return this.value;
		}

		@Override
		public List<String> extractIdentifiers() {
			return Lists.<String>empty().addLast(this.value);
		}

		@Override
		public String stringify() {
			return this.value;
		}

		@Override
		public String getName() {
			return this.value;
		}
	}

	private record Placeholder(String input)
			implements CType, JMethodDeclaration, CStructMember, CAssignable, JAssignable, JType, JObjectMember, CExpression,
			CDefinable, JExpression {
		private static String wrap(String input) {
			final var replaced = input.replace("/*", "start").replace("*/", "end");
			return "/*" + replaced + "*/";
		}

		@Override
		public String generate() {
			return wrap(this.input);
		}

		@Override
		public String toBaseName() {
			return wrap(this.input);
		}

		@Override
		public List<String> extractIdentifiers() {
			return Lists.empty();
		}

		@Override
		public CDefinable mapTypeParameters(F1R<List<String>, List<String>> mapper) {
			return this;
		}

		@Override
		public CDefinable mapName(F1R<String, String> mapper) {
			return this;
		}

		@Override
		public String stringify() {
			return wrap(this.input);
		}
	}

	private record JConstructor(String type) implements JMethodDeclaration {}

	private record JDeclaration(List<String> annotations, List<String> typeParameters, Option<String> maybeBeforeType,
															JType type, String name) implements JMethodDeclaration, JAssignable {
		public JDeclaration(String name, JType type) {
			this(Lists.empty(), Lists.empty(), new None<String>(), type, name);
		}

		@Override
		public String toString() {
			final String annotationsString;
			if (this.annotations.isEmpty()) annotationsString = "";
			else annotationsString = "annotations=" + this.annotations + ", ";

			final String typeParametersString;
			if (this.typeParameters.isEmpty()) typeParametersString = "";
			else typeParametersString = "typeParameters=" + this.typeParameters + ", ";

			final String maybeBeforeTypeString;
			if (this.maybeBeforeType instanceof Some<String>(var result))
				maybeBeforeTypeString = "maybeBeforeType=" + result + ", ";
			else maybeBeforeTypeString = "";

			return "JDeclaration {" + annotationsString + typeParametersString + maybeBeforeTypeString + "type=" + this.type +
						 ", name='" + this.name + '\'' + '}';
		}

		public JDeclaration mapName(F1R<String, String> mapper) {
			return new JDeclaration(this.annotations,
															this.typeParameters,
															this.maybeBeforeType,
															this.type,
															mapper.apply(this.name));
		}

		public JDeclaration withType(JType type) {
			return new JDeclaration(this.annotations, this.typeParameters, this.maybeBeforeType, type, this.name);
		}
	}

	private record CFunctionDeclaration(CType type, String name, List<CType> parameterTypes)
			implements CDefinable, CStructMember {
		@Override
		public String generate() {
			final var joinedParameterTypes =
					"(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", ")) + ")";

			return this.type.generate() + " (*" + this.name + ")" + joinedParameterTypes;
		}

		@Override
		public List<String> extractIdentifiers() {
			return this.type
					.extractIdentifiers()
					.addAllLast(this.parameterTypes.iter().map(CType::extractIdentifiers).flatMap(List::iter).toList());
		}

		@Override
		public CDefinable mapTypeParameters(F1R<List<String>, List<String>> mapper) {
			return this;
		}

		@Override
		public CDefinable mapName(F1R<String, String> mapper) {
			return new CFunctionDeclaration(this.type, mapper.apply(this.name), this.parameterTypes);
		}
	}

	private static final class EmptyStructMember implements CStructMember, JObjectMember {
		@Override
		public String generate() {
			return "";
		}
	}

	private record EscapedFolder(Folder folder) implements Folder {
		@Override
		public State apply(State state, Character next) {
			if (next == '/') {
				final var peek = state.peek();
				if (peek instanceof Some<Character>(var afterNext) && afterNext == '*') {
					final var withNext = state.append(next);
					var current = withNext.popAndAppendToOption().orElse(withNext);

					while (true) {
						final var tupleOption = current.popAndAppendToTuple();
						if (tupleOption instanceof Some<Tuple<State, Character>>(var pair)) {
							current = pair.left;

							if (pair.right == '*') {
								final var peekAgain = current.peek();
								if (peekAgain instanceof Some<Character>(var maybeEnd) && maybeEnd == '/') {
									current = current.popAndAppendToOption().orElse(current);
									break;
								}
							}
						} else break;
					}

					return current;
				}
			}

			if (next == '\'') {
				final var appended = state.append(next);
				return appended.popAndAppendToTuple().map(tuple -> {
					if (tuple.right == '\\') return tuple.left.popAndAppendToOption().orElse(tuple.left);
					return tuple.left;
				}).flatMap(State::popAndAppendToOption).orElse(appended);
			}

			if (next == '\"') {
				var current = state.append(next);
				while (true) {
					final var maybeTuple = current.popAndAppendToTuple();
					if (!(maybeTuple instanceof Some<Tuple<State, Character>>(var value))) break;

					current = value.left;

					final var right = value.right;
					if (right == '\\') current = current.popAndAppendToOption().orElse(current);

					if (right == '\"') break;
				}

				return current;
			}

			return this.folder.apply(state, next);
		}
	}

	private static class ValueFolder implements Folder {
		@Override
		public State apply(State state, Character next) {
			if (next == ',' && state.isLevel()) return state.advance();

			final var appended = state.append(next);
			if (next == '-') {
				final var peeked = appended.peek();
				if (peeked instanceof Some<Character>(var value) && value == '>')
					return appended.popAndAppendToOption().orElse(appended);
				else return appended;
			}

			if (next == '<' || next == '(') return appended.enter();

			if (next == '>' || next == ')') return appended.exit();
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
		public Iter<T> iter() {
			return Iter.of(this.value);
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
		public Iter<T> iter() {
			return Iter.empty();
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
			if (c == '(') return appended.enter();

			if (c == ')') {
				if (appended.isLevel()) return appended.advance();

				return appended.exit();
			}

			return appended;
		}
	}

	private record CField(CDefinable declaration) implements CStructMember {
		@Override
		public String generate() {
			return Main.generateStatement(1, this.declaration.generate());
		}
	}

	private static class Streams {
		public static <T> Iter<T> fromObjArray(T[] elements) {
			return new Iter<Integer>(new RangeHead(elements.length)).map(index -> elements[index]);
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
			if (this.retrieved) return new None<T>();
			this.retrieved = true;
			return new Some<T>(this.value);
		}
	}

	private static final class FlatMapHead<T, R> implements Head<R> {
		private final Head<T> head;
		private final F1R<T, Iter<R>> mapper;
		private Option<Iter<R>> maybeCurrent;

		public FlatMapHead(Head<T> head, F1R<T, Iter<R>> mapper) {
			this.head = head;
			this.mapper = mapper;
			this.maybeCurrent = new None<Iter<R>>();
		}

		@Override
		public Option<R> next() {
			while (true) {
				if (this.maybeCurrent instanceof Some<Iter<R>>(var current)) {
					final var next = current.head.next();
					if (next instanceof Some<R>) return next;
				}

				final var maybeNext = this.head.next();
				if (maybeNext instanceof None<T>) return new None<R>();
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

	private record AnyMatch<T>(F1R<T, Boolean> predicate) implements Collector<T, Boolean> {
		@Override
		public Boolean createInitial() {
			return false;
		}

		@Override
		public Boolean fold(Boolean aBoolean, T t) {
			return aBoolean || this.predicate.apply(t);
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
			if (current.isEmpty()) return element;
			return current + this.delimiter + element;
		}
	}

	private static class ListCollector<T> implements Collector<T, List<T>> {
		@Override
		public List<T> createInitial() {
			return Lists.empty();
		}

		@Override
		public List<T> fold(List<T> tList, T t) {
			return tList.addLast(t);
		}
	}

	private static class Paths {
		@Actual
		public static Path get(String first) {
			return new JavaPath(java.nio.file.Paths.get(first));
		}
	}

	@Actual
	private record JavaPath(java.nio.file.Path path) implements Path {
		@Override
		public Option<Path> getParent() {
			final var parent = this.path.getParent();
			if (parent instanceof java.nio.file.Path p) return new Some<Path>(new JavaPath(p));
			return new None<Path>();
		}

		@Override
		public Option<IOError> createDirectories() {
			try {
				Files.createDirectories(this.path);
				return new None<IOError>();
			} catch (IOException e) {
				return new Some<IOError>(new JavaIOError(e));
			}
		}

		@Override
		public Path resolve(String child) {
			return new JavaPath(this.path.resolve(child));
		}

		@Override
		public Option<IOError> writeString(String output) {
			try {
				Files.writeString(this.path, output);
				return new None<IOError>();
			} catch (IOException e) {
				return new Some<IOError>(new JavaIOError(e));
			}
		}

		@Override
		public Result<String, IOError> readString() {
			try {
				return new Ok<String, IOError>(Files.readString(this.path));
			} catch (IOException e) {
				return new Err<String, IOError>(new JavaIOError(e));
			}
		}
	}

	private record CDeclaration(List<String> typeParameters, CType type, String name) implements CAssignable,
			CDefinable {
		public CDeclaration(CType type, String name) {
			this(Lists.empty(), type, name);
		}

		@Override
		public CDefinable mapName(F1R<String, String> mapper) {
			return new CDeclaration(this.typeParameters, this.type, mapper.apply(this.name));
		}

		@Override
		public CDefinable mapTypeParameters(F1R<List<String>, List<String>> mapper) {
			return new CDeclaration(mapper.apply(this.typeParameters), this.type, this.name);
		}

		@Override
		public String generate() {
			final var template = generateTemplateString(this.typeParameters);
			return template + this.type.generate() + " " + this.name;
		}

		@Override
		public List<String> extractIdentifiers() {
			return this.type.extractIdentifiers();
		}
	}

	private record JArrayType(JType type) implements JType {

		@Override
		public String stringify() {
			return this.type.stringify() + "_array";
		}
	}

	private record JGenericType(String base, List<JType> typeArguments) implements JType {

		@Override
		public String stringify() {
			final var joined =
					this.typeArguments.iter().map(JType::stringify).map(slice -> "_" + slice).collect(new Joiner());
			return this.base + joined;
		}
	}

	private record CPointerAccess(CExpression instance, String fieldName) implements CExpression {
		@Override
		public String generate() {
			return this.instance.generate() + "->" + this.fieldName;
		}
	}

	private record CFieldAccess(CExpression instance, String fieldName) implements CExpression {
		@Override
		public String generate() {
			return this.instance.generate() + "." + this.fieldName;
		}
	}

	private record JMemberAccess(JExpression instance, String memberName) implements JExpression {}

	private record JConstruction(JType jType) implements JCaller {}

	private record CInvocation(CExpression expression, List<CExpression> cArguments) implements CExpression {
		@Override
		public String generate() {
			final var joinedArguments = this.cArguments().iter().map(CAssignable::generate).collect(new Joiner(", "));
			return this.expression().generate() + "(" + joinedArguments + ")";
		}
	}

	private record JInvokable(JCaller caller, List<JExpression> arguments) implements JExpression {}

	private record JFunctionalType(List<JType> parameterTypes, JType returnType) implements JType {
		public JFunctionalType(JType returnType) {
			this(new JavaList<JType>(), returnType);
		}

		@Override
		public String stringify() {
			final var joined =
					this.parameterTypes.iter().map(JType::stringify).map(slice -> "_" + slice).collect(new Joiner());
			return "func_" + this.returnType.stringify() + joined;
		}
	}

	private static class Environment {
		private final List<Frame> frames;

		private Environment() {
			this(new JavaList<Frame>());
		}

		private Environment(List<Frame> frames) {
			this.frames = frames;
		}

		private Option<JDeclaration> resolveExpression(String identifier) {
			return this.frames.iter().map(frame -> frame.resolveExpression(identifier)).flatMap(Option::iter).next();
		}

		public <T> Tuple<Environment, T> within(F1R<Environment, Tuple<Environment, T>> supplier) {
			final var withLastEnv = this.enter();
			final var result = supplier.apply(withLastEnv);
			var exited = result.left.exit();
			return new Tuple<Environment, T>(exited, result.right);
		}

		private Environment exit() {
			return new Environment(this.frames.removeLast());
		}

		private Environment enter() {
			return new Environment(this.frames.addLast(new Frame()));
		}

		public Environment defineAllExpressions(List<JDeclaration> declarations) {
			return new Environment(this.frames.mapLast(last -> last.defineAllExpressions(declarations)));
		}

		public Environment defineExpression(JDeclaration declaration) {
			return new Environment(this.frames.mapLast(last -> last.defineExpression(declaration)));
		}

		public Option<JObjectType> resolveCurrent() {
			return this.frames.iterReversed().map(Frame::toStructureType).flatMap(Option::iter).next();
		}

		public Environment withObject(JObject object) {
			return new Environment(this.frames.mapLast(last -> last.withObject(object)));
		}

		public Option<JObjectType> resolveType(String name) {
			return this.frames.iterReversed().map(frame -> frame.resolveType(name)).flatMap(Option::iter).next();
		}

		public Environment defineAllTypes(List<JObjectType> types) {
			return new Environment(this.frames.mapLast(last -> last.defineAllTypes(types)));
		}
	}

	private static class Frame {
		private final Option<JObject> maybeObject;
		private final List<JDeclaration> definedExpressions;
		private final List<JObjectType> definedTypes;

		private Frame(Option<JObject> maybeName, List<JObjectType> definedTypes, List<JDeclaration> definedExpressions) {
			this.maybeObject = maybeName;
			this.definedTypes = definedTypes;
			this.definedExpressions = definedExpressions;
		}

		public Frame() {
			this(new None<JObject>(), new JavaList<JObjectType>(), new JavaList<JDeclaration>());
		}

		public Frame defineAllExpressions(List<JDeclaration> definitions) {
			return new Frame(this.maybeObject, this.definedTypes, this.definedExpressions.addAllLast(definitions));
		}

		public Option<JDeclaration> resolveExpression(String identifier) {
			return this.definedExpressions.iter().filter(define -> define.name.equals(identifier)).next();
		}

		public Frame defineExpression(JDeclaration declaration) {
			return new Frame(this.maybeObject, this.definedTypes, this.definedExpressions.addLast(declaration));
		}

		public Option<JObjectType> toStructureType() {
			return this.maybeObject.map(obj -> new JObjectType(obj.name, obj.variants, this.definedExpressions));
		}

		public Frame withObject(JObject name) {
			return new Frame(new Some<JObject>(name), this.definedTypes, this.definedExpressions);
		}

		public Option<JObjectType> resolveType(String name) {
			return this.definedTypes.iter().filter(type -> type.name.equals(name)).next();
		}

		public Frame defineAllTypes(List<JObjectType> types) {
			return new Frame(this.maybeObject, this.definedTypes.addAllLast(types), this.definedExpressions);
		}
	}

	private record JObjectType(String name, List<String> variants, List<JDeclaration> members) implements JType {
		private Option<JType> resolve(String name) {
			return this.members.iter().filter(member -> member.name.equals(name)).next().map(JDeclaration::type);
		}

		@Override
		public String stringify() {
			return this.name;
		}
	}

	private static final class JRecursiveType implements JType {
		private Option<JType> maybeInternal;

		private JRecursiveType() {
			this.maybeInternal = new None<JType>();
		}

		public static JType create(F1R<JType, JType> mapper) {
			final var created = new JRecursiveType();
			final var apply = mapper.apply(created);
			created.set(apply);
			return created;
		}

		private void set(JType created) {
			this.maybeInternal = new Some<JType>(created);
		}

		@Override
		public String stringify() {
			return this.maybeInternal.map(JType::stringify).orElse("?");
		}
	}

	private record CStructure(List<String> typeParameters, String name, List<CDefinable> fields)
			implements CStructureOrUnion {
		@Override
		public String findName() {
			return this.name;
		}

		@Override
		public String generate() {
			final var joinedFields = this.fields.iter().map(CField::new).map(CField::generate).collect(new Joiner());

			return generateTemplateString(this.typeParameters) + "struct " + this.name + " {" + joinedFields +
						 System.lineSeparator() + "};" + System.lineSeparator();
		}

		@Override
		public List<String> findDependencies() {
			return this.fields
					.iter()
					.map(CDefinable::extractIdentifiers)
					.flatMap(List::iter)
					.filter(value -> !this.typeParameters.contains(value))
					.collect(new ListCollector<String>());
		}
	}

	private record CEnum(String name, List<String> variants) {
		public String generate() {
			final var enumFields = this
					.variants()
					.iter()
					.map(variant -> variant + "Variant")
					.map(variant -> generateIndent(1) + variant)
					.collect(new Joiner(","));

			return "enum class " + this.name + " {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();
		}
	}

	private record CUnion(List<String> typeParameters, String name, List<CDefinable> members)
			implements CStructureOrUnion {
		@Override
		public String findName() {
			return this.name + "Data";
		}

		@Override
		public String generate() {
			final var unionFields =
					this.members.iter().map(CDefinable::generate).map(Main::generateStatement).collect(new Joiner());

			return generateTemplateString(this.typeParameters()) + "union " + this.name + "Data {" + unionFields +
						 System.lineSeparator() + "};" + System.lineSeparator();
		}

		@Override
		public List<String> findDependencies() {
			return this.members.iter().map(CDefinable::extractIdentifiers).flatMap(List::iter).toList();
		}
	}

	private record JObject(String type, List<String> annotations, List<String> modifiersList, String name,
												 List<String> typeParameters, List<JDeclaration> recordFields, List<CType> implementees,
												 List<String> variants, List<JObjectMember> children) implements JObjectMember {

		private List<CFunction> createConversionFunctions(Environment environment1) {
			return this
					.implementees()
					.iter()
					.map(implementee -> this.createConversionType(implementee, environment1))
					.toList();
		}

		// TODO: This return type needs to be a CFunction
		private CFunction createConversionType(CType implementee, Environment environment) {
			var joinedTypeParameters = Main.joinTypeParameters(this.typeParameters);
			// TODO: turn this into proper AST generation

			final String implementeeName = implementee.toBaseName();
			final var thisType = this.name + joinedTypeParameters;
			final var thisPtr = Main.generateStatement(thisType + "* _this = (" + thisType + "*) _ref");

			final var jObjectType = environment.resolveType(implementeeName).orElse(null);
			assert jObjectType != null;

			final String content;
			/*
				HeadTable<int> table = HeadTable<int>{
					next_RangeHead,
				};
				void *data = moveToHeap(*_this);
				return Head<int>{ data, table };
				 */
			if (jObjectType.variants.isEmpty()) content = Main.generateStatement("return _impl");
			else {
				final var s1 = Main.generateStatement(implementeeName + "Data" + joinedTypeParameters + " data");
				final var s2 = Main.generateStatement("data." + this.name + " = *_this");
				final var s3 = Main.generateStatement("return { " + implementeeName + "Tag::" + this.name + "Variant, data }");
				content = s1 + s2 + s3;
			}

			final var conversionFunctionName = "to" + implementeeName + "_" + this.name;
			final var parameters =
					Lists.<CDeclaration>empty().addLast(new CDeclaration(new CPointerType(CPrimitiveType.Void), "_ref"));
			final var header =
					new CFunctionHeader(new CDeclaration(this.typeParameters, implementee, conversionFunctionName), parameters);

			return new CFunction(header, thisPtr + content);
		}

		public JObjectType toType() {
			final var memberDefinitions = this.children.iter().map(this::extractDefinition).flatMap(Option::iter).toList();
			return new JObjectType(this.name, this.variants, memberDefinitions);
		}

		private Option<JDeclaration> extractDefinition(JObjectMember child) {
			return switch (child) {
				case JField jField -> new Some<JDeclaration>(jField.declaration);
				case JMethod jMethod -> jMethod.toDeclaration();
				case EmptyStructMember _, JObject _, Placeholder _ -> new None<JDeclaration>();
				default -> throw new IllegalStateException("Unexpected value: " + child);
			};
		}
	}

	public record CFunctionHeader(CDefinable definition, List<CDeclaration> parameters) {
		private String generate() {
			final var compiledParameters = this.parameters().iter().map(CDeclaration::generate).collect(new Joiner(", "));
			return this.definition().generate() + "(" + compiledParameters + ")";
		}
	}

	public record CFunction(CFunctionHeader header, String content) {
		private String generate() {
			return this.header().generate() + "{" + this.content() + System.lineSeparator() + "}" + System.lineSeparator();
		}
	}

	private record JMethod(List<String> typeParameters, List<JDeclaration> parameters,
												 JMethodDeclaration methodDeclaration, String content) implements JObjectMember {
		private Option<JDeclaration> toDeclaration() {
			if (this.methodDeclaration instanceof JDeclaration declaration) {
				final var returnType = declaration.type;
				final var paramTypes = this.parameters.iter().map(JDeclaration::type).toList();
				final var functionalType = new JFunctionalType(paramTypes, returnType);
				return new Some<JDeclaration>(new JDeclaration(declaration.name, functionalType));
			}

			return new None<JDeclaration>();
		}
	}

	private record JField(JDeclaration declaration) implements JObjectMember {}

	private record JNumber(String value) implements JExpression {
		private JNumber(String value) {
			this.value = value;

			assert !value.isEmpty();
		}
	}

	private record CNumber(String value) implements CExpression {
		private CNumber(String value) {
			this.value = value;

			assert !value.isEmpty();
		}

		@Override
		public String generate() {
			return this.value;
		}
	}

	private record JNot(CExpression instance) implements JExpression {}

	private record CNot(CExpression instance) implements CExpression {
		@Override
		public String generate() {
			return "!" + this.instance.generate();
		}
	}

	private record MapCollector<K, V>() implements Collector<Tuple<K, V>, Map<K, V>> {
		@Override
		public Map<K, V> createInitial() {
			return new HashMap<K, V>();
		}

		@Override
		public Map<K, V> fold(Map<K, V> kvMap, Tuple<K, V> kvTuple) {
			kvMap.put(kvTuple.left, kvTuple.right);
			return kvMap;
		}
	}

	private record CReference(CExpression instance) implements CExpression {
		@Override
		public String generate() {
			return "&" + this.instance.generate();
		}
	}

	private record Char(String value) implements JExpression, CExpression {
		@Override
		public String generate() {
			return "'" + this.value + "'";
		}
	}

	private record JOperator(JExpression left, Operator operator, JExpression rightCompiled) implements JExpression {}

	private record COperator(CExpression left, Operator operator, CExpression right) implements CExpression {
		@Override
		public String generate() {
			return this.left.generate() + " " + this.operator.generate() + " " + this.right.generate();
		}
	}

	private record StringNode(String slice) implements JExpression, CExpression {
		@Override
		public String generate() {
			return "\"" + this.slice + "\"";
		}
	}

	private record JMethodAccess(JExpression instance, String methodName) implements JExpression {}

	private record JInstanceOf() implements JExpression {}

	private record JQuantity(JExpression instance) implements JExpression {}

	private static Environment environment = new Environment();
	private final JType StringType;
	private List<String> functionDeclarations;
	private List<String> globals;
	private List<String> structureForwardDeclarations;
	private List<CStructureOrUnion> structuresOrUnions;
	private List<CFunction> functions;
	private int counter;
	private List<CEnum> enums;

	public Main() {
		this.structuresOrUnions = Lists.empty();

		this.enums = Lists.empty();
		this.structureForwardDeclarations = Lists.empty();

		this.functionDeclarations = Lists.empty();
		this.functions = Lists.empty();

		this.globals = Lists.empty();
		this.counter = 0;

		this.StringType = JRecursiveType.create(StringType -> {
			// We don't need parameter types for now, we don't validate them yet
			final var methods = Lists
					.<JDeclaration>empty()
					.addLast(new JDeclaration("charAt", new JFunctionalType(JPrimitiveType.Char)))
					.addLast(new JDeclaration("indexOf", new JFunctionalType(JPrimitiveType.Int)))
					.addLast(new JDeclaration("lastIndexOf", new JFunctionalType(JPrimitiveType.Int)))
					.addLast(new JDeclaration("length", new JFunctionalType(JPrimitiveType.Int)))
					.addLast(new JDeclaration("strip", new JFunctionalType(StringType)))
					.addLast(new JDeclaration("substring", new JFunctionalType(StringType)))
					.addLast(new JDeclaration("replace", new JFunctionalType(StringType)));

			return new JObjectType("String", Lists.empty(), methods);
		});
	}

	private static boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private static String generateTemplateString(List<String> typeParameters) {
		if (typeParameters.isEmpty()) return "";
		final var typeNames = typeParameters.iter().map(typeParam -> "typename " + typeParam).collect(new Joiner(", "));
		return "template <" + typeNames + ">" + System.lineSeparator();
	}

	public static void main(String[] args) {
		if (new Main().run() instanceof Some<IOError>(var value)) System.err.println(value.display());
	}

	private static String generateStatement(int depth, String content) {
		return generateIndent(depth) + content + ";";
	}

	private static String generateIndent(int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}

	private static CType transformPrimitiveType(JPrimitiveType type) {
		return switch (type) {
			case Int, Boolean -> CPrimitiveType.Int;
			case Void -> CPrimitiveType.Void;
			case Char -> CPrimitiveType.Char;
			case Var -> new Placeholder("var");
		};
	}

	private static String generateStatement(String content) {
		return generateStatement(1, content);
	}

	private static String joinTypeParameters(List<String> typeParameters) {
		final String joinedTypeParameters;
		if (typeParameters.isEmpty()) joinedTypeParameters = "";
		else joinedTypeParameters = "<" + typeParameters.iter().collect(new Joiner(", ")) + ">";

		return joinedTypeParameters;
	}

	private static Option<JObjectType> extractType(JObjectMember jObjectMember) {
		return switch (jObjectMember) {
			case JObject jObject -> new Some<JObjectType>(jObject.toType());
			case EmptyStructMember _, JField _, JMethod _, Placeholder _ -> new None<JObjectType>();
			default -> throw new IllegalStateException("Unexpected value: " + jObjectMember);
		};
	}

	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	public CDeclaration toCDeclaration(JDeclaration declaration) {
		return new CDeclaration(declaration.typeParameters, this.transformType(declaration.type), declaration.name);
	}

	private CType transformType(JType jType) {
		return switch (jType) {
			case Identifier identifier -> identifier;
			case JArrayType jArrayType -> new CPointerType(this.transformType(jArrayType.type));
			case JGenericType jGenericType -> {
				final var newTypeArguments = jGenericType.typeArguments.iter().map(this::transformType).toList();
				yield new CTemplateType(jGenericType.base, newTypeArguments);
			}
			case JPrimitiveType jPrimitiveType -> transformPrimitiveType(jPrimitiveType);
			case Placeholder placeholder -> placeholder;
			case JFunctionalType jFunctionalType -> new Placeholder(jFunctionalType.toString());
			case JObjectType jStructureType -> new Identifier(jStructureType.name);
			case JRecursiveType jRecursiveType -> {
				if (jRecursiveType == this.StringType) yield new CPointerType(CPrimitiveType.Char);
				else yield new Placeholder("Unknown built-in type");
			}
			default -> throw new IllegalStateException("Unexpected value: " + jType);
		};
	}

	private Tuple<CExpression, List<CType>> transformCaller(JCaller jCaller) {
		return switch (jCaller) {
			case JConstruction jConstruction -> this.destroyConstruction(jConstruction);
			case JExpression jExpression ->
					new Tuple<CExpression, List<CType>>(this.transformExpression(jExpression), Lists.empty());
			default -> throw new IllegalStateException("Unexpected value: " + jCaller);
		};
	}

	private Tuple<CExpression, List<CType>> destroyConstruction(JConstruction jConstruction) {
		final var cType = this.transformType(jConstruction.jType);
		if (cType instanceof Identifier(var value))
			return new Tuple<CExpression, List<CType>>(new Identifier("new_" + value), Lists.empty());

		if (cType instanceof CTemplateType(var base, var typeArguments))
			return new Tuple<CExpression, List<CType>>(new Identifier("new_" + base), typeArguments);

		return new Tuple<CExpression, List<CType>>(new Identifier("new_" + cType.generate()), Lists.empty());
	}

	private CExpression transformExpression(JExpression expression) {
		return switch (expression) {
			case Identifier identifier -> {
				if (identifier.value.equals("this")) yield new CQuantity(new CDereference(new Identifier("_this")));

				yield identifier;
			}
			case JInvokable jInvokable -> this.transformInvocation(jInvokable);
			case JMemberAccess jMemberAccess -> {
				final var instance = jMemberAccess.instance;
				final var memberName = jMemberAccess.memberName;
				final var transformed = this.transformExpression(instance);
				if (transformed instanceof CQuantity(var expression1))
					if (expression1 instanceof CDereference(var expression2)) yield new CPointerAccess(expression2, memberName);

				yield new CFieldAccess(transformed, memberName);
			}
			case JNumber jNumber -> new CNumber(jNumber.value);
			case JNot jNot -> new CNot(jNot.instance);
			case Placeholder placeholder -> placeholder;
			case Char aChar -> aChar;
			case JOperator jOperator -> new COperator(this.transformExpression(jOperator.left),
																								jOperator.operator,
																								this.transformExpression(jOperator.rightCompiled));
			case StringNode stringNode -> stringNode;
			case JMethodAccess access -> new Identifier("???");
			case JInstanceOf instance -> new Identifier("???");
			case JQuantity quantity -> new CQuantity(this.transformExpression(quantity.instance));
			default -> throw new IllegalStateException("Unexpected value: " + expression);
		};
	}

	private CExpression transformInvocation(JInvokable jInvokable) {
		final var arguments = jInvokable.arguments().iter().map(this::transformExpression).toList();
		final var caller = jInvokable.caller();
		if (caller instanceof JMemberAccess(var instance, var memberName)) {
			final var jType = this.resolveExpression(instance);
			final var baseName = jType.stringify();

			final var tuple = this.transformCaller(instance);
			final var left = tuple.left;

			CExpression element;
			if (left instanceof CDereference(var expression)) element = expression;
			else element = new CReference(new CQuantity(left));

			if (element instanceof CQuantity(var expression)) element = expression;

			final var newArguments = arguments.addFirst(element);
			return new CInvocation(new Identifier(memberName + "_" + baseName), newArguments);
		}

		/*
		TODO: support explicit type arguments on invocations
		*/
		final var tuple = this.transformCaller(caller);
		return new CInvocation(tuple.left, arguments);
	}

	private Option<IOError> run() {
		final var source =
				Paths.get(".").resolve("src").resolve("main").resolve("java").resolve("magma").resolve("Main.java");
		final var target =
				Paths.get(".").resolve("src").resolve("main").resolve("windows").resolve("magma").resolve("Main.cpp");

		final var input = source.readString().mapValue(this::compile);

		return switch (input) {
			case Err<String, IOError> v -> new Some<IOError>(v.error);
			case Ok<String, IOError> v -> {
				final var maybeParent = target.getParent();
				if (maybeParent instanceof Some<Path>(var parent)) {
					final var maybeError = parent.createDirectories();
					if (maybeError instanceof Some<IOError>(var error)) yield new Some<IOError>(error);
				}
				yield target.writeString(v.value);
			}
		};
	}

	private String compile(String input) {
		final var all = this.compileStatements(input, this::compileRootSegment);

		final var joinedStructureForwardDeclarations = this.joinStrings(this.structureForwardDeclarations);
		final var joinedStructures =
				this.createTopologicallySortedList().iter().map(CStructureOrUnion::generate).collect(new Joiner());
		final var joinedGlobals = this.joinStrings(this.globals);

		final var joinedFunctionDeclarations = this.joinStrings(this.functionDeclarations);
		final var joinedFunctions = this.functions.iter().map(CFunction::generate).collect(new Joiner());

		final var joinedEnums = this.enums.iter().map(CEnum::generate).collect(new Joiner());
		return "#include \"intrinsics.h\"" + System.lineSeparator() + joinedStructureForwardDeclarations + joinedEnums +
					 joinedStructures + joinedFunctionDeclarations + joinedGlobals + joinedFunctions + all;
	}

	private List<CStructureOrUnion> createTopologicallySortedList() {
		var dependencyMap = this.structuresOrUnions.iter().map(value -> {
			final var withoutDuplicates = this.removeDuplicates(value.findDependencies());
			return new Tuple<String, List<String>>(value.findName(), withoutDuplicates);
		}).collect(new MapCollector<String, List<String>>());

		for (var entry : dependencyMap.entrySet()) {
			var newValues = entry.getValue().iter().filter(dependencyMap::containsKey).toList();
			final var oldKey = entry.getKey();

			/*
			This is to prevent circular dependencies with VTables.

			Technically List -> ListTable -> List, but ListTable -> List is invalid
			ListTable does not have any fields, only dynamic members,
			and we can safely assume that any mention of List in ListTable is a pointer or in a function pointer.
			Therefore, we remove it.
			 */
			if (oldKey.endsWith("Table")) {
				final var keyWithoutTable = oldKey.substring(0, oldKey.length() - "Table".length());
				if (newValues.contains(keyWithoutTable)) newValues = newValues.removeElement(keyWithoutTable);
			}

			dependencyMap.put(oldKey, newValues);
		}

		var dependencyOrder = Lists.<String>empty();
		while (!dependencyMap.isEmpty()) {
			var dependencyMapKeysToRemove =
					dependencyMap.entrySet().stream().filter(entry -> entry.getValue().isEmpty()).map(Entry::getKey).toList();

			final var oldLength = dependencyMap.size();
			for (var dependencyMapKeyToRemove : dependencyMapKeysToRemove) {
				dependencyMap.remove(dependencyMapKeyToRemove);
				dependencyOrder = dependencyOrder.addLast(dependencyMapKeyToRemove);

				this.removeKeyFromValues(dependencyMap, dependencyMapKeyToRemove);
			}

			final var newLength = dependencyMap.size();
			assert oldLength != newLength;
		}

		final var mapping = this.structuresOrUnions
				.iter()
				.map(rootSegment -> new Tuple<String, CStructureOrUnion>(rootSegment.findName(), rootSegment))
				.collect(new MapCollector<String, CStructureOrUnion>());

		final List<CStructureOrUnion>[] toReturn = new List[]{Lists.<CStructureOrUnion>empty()};
		dependencyOrder.iter().forEach(element -> {
			final var cStructureOrUnion = mapping.get(element);
			toReturn[0] = toReturn[0].addLast(cStructureOrUnion);
		});

		return toReturn[0];
	}

	private void removeKeyFromValues(Map<String, List<String>> dependencyMap, String dependencyMapKeyToRemove) {
		for (var anyDependencyMapKey : dependencyMap.keySet()) {
			final var values = dependencyMap.get(anyDependencyMapKey);
			if (values.contains(dependencyMapKeyToRemove)) {
				var newValues = values.removeElement(dependencyMapKeyToRemove);
				dependencyMap.put(anyDependencyMapKey, newValues);
			}
		}
	}

	private List<String> removeDuplicates(List<String> list) {
		return list.iter().fold(Lists.empty(), (copy, element) -> {
			if (copy.contains(element)) return copy;
			return copy.addLast(element);
		});
	}

	private String joinStrings(List<String> structures) {
		return structures.iter().collect(new Joiner(""));
	}

	private String compileStatements(String input, F1R<String, String> mapper) {
		return this.compileAll(input, mapper, new EscapedFolder(this::foldStatement));
	}

	private String compileAll(String input, F1R<String, String> mapper, Folder folder) {
		return this.divide(input, folder).map(mapper).collect(new Joiner(""));
	}

	private Iter<String> divide(String input, Folder folder) {
		var current = new State(input);
		while (true) {
			final var maybeNext = current.pop();
			if (!(maybeNext instanceof Some<Character>(var value))) break;

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
						if (right == '\n') return withoutLineCommentPrefix.advance();
					} else return withoutLineCommentPrefix;
				}
			}
		}

		final var appended = current.append(next);
		if (next == ';' && appended.isLevel()) return appended.advance();

		if (next == '}' && appended.isShallow()) {
			final State appended1;
			if (appended.peek() instanceof Some<Character>(var peek) && peek == ';')
				appended1 = appended.popAndAppendToOption().orElse(appended);
			else appended1 = appended;

			return appended1.advance().exit();
		}

		if (next == '{' || next == '(') return appended.enter();

		if (next == '}' || next == ')') return appended.exit();

		return appended;
	}

	private String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) return "";

		if (stripped.startsWith("package ") || stripped.startsWith("import ")) return "";

		return this
				.parseObject("class", stripped)
				.flatMap(this::transformObject)
				.map(CStructMember::generate)
				.orElseGet(() -> Placeholder.wrap(stripped));
	}

	private Option<JObject> parseObject(String type, String stripped) {
		final var i = stripped.indexOf(type + " ");
		if (i < 0) return new None<JObject>();
		final var beforeType = stripped.substring(0, i).strip();

		final String modifiers;
		List<String> annotations = Lists.empty();

		final var i5 = beforeType.lastIndexOf("\n");
		if (i5 >= 0) {
			final var substring = beforeType.substring(0, i5);
			final var substring1 = beforeType.substring(i5 + 1);
			annotations = this.collectAnnotations(substring);
			modifiers = substring1;
		} else modifiers = beforeType;

		final var afterKeyword = stripped.substring(i + (type + " ").length()).strip();

		final var i1 = afterKeyword.indexOf("{");
		if (i1 < 0) return new None<JObject>();
		var beforeContent = afterKeyword.substring(0, i1).strip();

		final var withEnd = afterKeyword.substring(i1 + 1).strip();
		if (!withEnd.endsWith("}")) return new None<JObject>();
		final var inputContent = withEnd.substring(0, withEnd.length() - 1);

		List<String> variants = Lists.empty();
		final var i2 = beforeContent.indexOf("permits ");
		if (i2 >= 0) {
			final var substring1 = beforeContent.substring(i2 + "permits ".length());
			beforeContent = beforeContent.substring(0, i2);

			variants = this.splitValues(substring1);
		}

		// TODO: generate conversion methods
		List<CType> extensions = Lists.empty();
		final var extendsIndex = beforeContent.indexOf("extends ");
		if (extendsIndex >= 0) {
			final var extensionsString = beforeContent.substring(extendsIndex + "extends ".length());
			beforeContent = beforeContent.substring(0, extendsIndex).strip();/*
			extensions = this
					.divide(extensionsString, new ValueFolder())
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.map(input -> transformType(this.parseType(input)))
					.toList();*/
		}

		List<CType> implementees = Lists.empty();
		final var i4 = beforeContent.indexOf("implements ");
		if (i4 >= 0) {
			final var implementeesString = beforeContent.substring(i4 + "implements ".length());
			beforeContent = beforeContent.substring(0, i4).strip();
			implementees = this
					.divide(implementeesString, new ValueFolder())
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.map(input -> this.transformType(this.parseType(input)))
					.toList();
		}

		List<JDeclaration> recordFields = Lists.empty();
		if (beforeContent.endsWith(")")) {
			final var substring = beforeContent.substring(0, beforeContent.length() - 1);
			final var i3 = substring.indexOf("(");
			if (i3 >= 0) {
				beforeContent = substring.substring(0, i3);
				recordFields = this
						.divide(substring.substring(i3 + 1), new ValueFolder())
						.map(this::parseDeclaration)
						.flatMap(Option::iter)
						.toList();
			}
		}

		List<String> typeParameters = Lists.empty();
		final var i3 = beforeContent.indexOf("<");
		if (i3 >= 0) {
			final var substring1 = beforeContent.substring(i3 + 1).strip();
			if (substring1.endsWith(">")) {
				beforeContent = beforeContent.substring(0, i3);
				final var substring = substring1.substring(0, substring1.length() - 1);
				typeParameters = this.splitValues(substring);
			}
		}

		if (!Identifier.isIdentifier(beforeContent)) return new None<JObject>();

		var modifiersList = Streams
				.fromObjArray(modifiers.split(Pattern.quote(" ")))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList();

		var name = beforeContent.strip();
		var finalTypeParameters = typeParameters;
		final var divisions = this.divide(inputContent, new EscapedFolder(this::foldStatement)).toList();
		final var children = divisions
				.iter()
				.map(slice -> this.parseObjectMember(slice, name, finalTypeParameters))
				.flatMap(Option::iter)
				.toList();

		final var prototype = new JObject(type,
																			annotations,
																			modifiersList,
																			name,
																			typeParameters,
																			recordFields,
																			implementees,
																			variants,
																			children);

		return new Some<JObject>(prototype);
	}

	private Option<CStructMember> transformObject(JObject object) {
		if (object.annotations.contains("Actual")) return new Some<CStructMember>(new EmptyStructMember());

		this.functions = object.createConversionFunctions(environment).iter().fold(this.functions, List::addLast);
		final var within = environment.within(env -> {
			environment = env.withObject(object);

			final var types = object.children.iter().map(Main::extractType).flatMap(Option::iter).toList();
			final var declarations =
					object.children.iter().map(this::extractField).flatMap(Option::iter).toList().addAllLast(object.recordFields);

			environment = environment.defineAllTypes(types);
			environment = environment.defineAllExpressions(declarations);

			final var members = object.children
					.iter()
					.map(wrapper -> this.transformObjectMemberPrototype(object, wrapper))
					.flatMap(Option::iter)
					.toList();

			return new Tuple<Environment, List<CStructMember>>(environment, members);
		});

		environment = within.left;
		var members = within.right;

		var fields = object.recordFields.iter().map(this::toCDeclaration).<CDefinable>map(value -> value).toList();
		if (object.type().equals("interface"))
			if (object.modifiersList().contains("sealed")) fields = this.handleSealedInterface(object, fields);
			else fields = this.handleUnsealedInterface(object, members, fields);
		else {
			final var retained = this.retainFields(members);
			fields = fields.addAllLast(retained);
		}

		if (object.type.equals("record")) {
			final var recordFields = object.recordFields.iter().map(this::toCDeclaration).toList();

			final var structureType = this.createStructureType(object.name, object.typeParameters);
			final var definition = new CDeclaration(object.typeParameters, structureType, "new_" + structureType.getName());

			final var joinedAssignments = recordFields
					.iter()
					.map(field -> "_this->" + field.name + " = " + field.name)
					.map(Main::generateStatement)
					.collect(new Joiner());

			final var content = generateStatement(structureType.generate() + " _thisInstance") +
													generateStatement(structureType.generate() + "* _this = &_thisInstance") + joinedAssignments +
													generateStatement("return _thisInstance");

			this.functions = this.functions.addLast(new CFunction(new CFunctionHeader(definition, recordFields), content));
		}

		this.structuresOrUnions =
				this.structuresOrUnions.addLast(new CStructure(object.typeParameters(), object.name, fields));

		this.structureForwardDeclarations = this.structureForwardDeclarations.addLast(
				generateTemplateString(object.typeParameters) + "struct " + object.name + ";" + System.lineSeparator());

		return new Some<CStructMember>(new EmptyStructMember());
	}

	private List<CDefinable> handleUnsealedInterface(JObject object,
																									 List<CStructMember> members,
																									 List<CDefinable> fields) {
		final var list = members.iter().map(this::retainDefinables).flatMap(Option::iter).toList();
		final var cStructure = new CStructure(object.typeParameters(), object.name + "Table", list);
		this.structuresOrUnions = this.structuresOrUnions.addLast(cStructure);

		final var tableType = this.createStructureType(object.name + "Table", object.typeParameters);

		fields = fields
				.addLast(new CDeclaration(tableType, "table"))
				.addFirst(new CDeclaration(new CPointerType(CPrimitiveType.Void), "data"));

		return fields;
	}

	private CNamedType createStructureType(String name, List<String> typeArguments) {
		if (typeArguments.isEmpty()) return new Identifier(name);
		else return new CTemplateType(name, typeArguments.iter().<CType>map(Identifier::new).toList());
	}

	private List<CDefinable> handleSealedInterface(JObject object, List<CDefinable> fields) {
		var name = object.name;
		var typeParameters = object.typeParameters();
		var variants = object.variants();
		final var jEnum = new CEnum(name + "Tag", variants);

		final var typeArguments = typeParameters.iter().<CType>map(Identifier::new).toList();
		final var unionMembers = variants.iter().map(variant -> this.createUnionField(variant, typeArguments)).toList();
		final var union = new CUnion(typeParameters, name, unionMembers);

		fields = fields
				.addLast(new CDeclaration(new Identifier(object.name + "Tag"), "variant"))
				.addLast(new CDeclaration(this.createStructureType(name + "Data", object.typeParameters), "data"));

		this.enums = this.enums.addLast(jEnum);
		this.structuresOrUnions = this.structuresOrUnions.addLast(union);
		return fields;
	}

	private CDefinable createUnionField(String variant, List<CType> typeArguments) {
		final var type = typeArguments.isEmpty() ? new Identifier(variant) : new CTemplateType(variant, typeArguments);
		return new CDeclaration(type, variant);
	}

	private Option<JDeclaration> extractField(JObjectMember prototype) {
		return switch (prototype) {
			case JMethod methodPrototype -> methodPrototype.toDeclaration();
			case JField field -> new Some<JDeclaration>(field.declaration);
			default -> new None<JDeclaration>();
		};
	}

	private Option<CStructMember> transformObjectMemberPrototype(JObject object, JObjectMember wrapper) {
		return switch (wrapper) {
			case JObject objectPrototype -> this.transformObject(objectPrototype);
			case JMethod methodPrototype -> new Some<CStructMember>(this.completeMethodProto(methodPrototype, object));
			case Placeholder placeholder -> new Some<CStructMember>(placeholder);
			case EmptyStructMember _ -> new None<CStructMember>();
			case JField jField -> new Some<CStructMember>(new CField(this.toCDeclaration(jField.declaration)));
			default -> throw new IllegalStateException("Unexpected value: " + wrapper);
		};
	}

	private CStructMember completeMethodProto(JMethod jFunctionProto, JObject object) {
		Option<String> maybeCompiled = new None<String>();
		if (jFunctionProto.methodDeclaration() instanceof JDeclaration declaration &&
				declaration.annotations.contains("Actual")) {
			var cParameters = jFunctionProto.parameters().iter().map(this::toCDeclaration).toList();
			final var compiledParameters = cParameters.iter().map(CDeclaration::generate).collect(new Joiner(", "));

			final var modifiedMethodDeclaration = this.toCDeclaration(declaration.mapName(name -> name + "_" + object.name));

			this.functionDeclarations = this.functionDeclarations.addLast(
					modifiedMethodDeclaration.generate() + "(" + compiledParameters + ");" + System.lineSeparator());

			return new EmptyStructMember();
		}

		if (jFunctionProto.content.startsWith("{") && jFunctionProto.content.endsWith("}")) {
			final var inputContent = jFunctionProto.content.substring(1, jFunctionProto.content().length() - 1);

			final var within = environment.within((env) -> {
				var self = env.defineAllExpressions(jFunctionProto.parameters());
				return self.within(value -> new Tuple<Environment, String>(value,
																																	 this.compileMethodsSegments(inputContent, 1)));
			});

			environment = within.left;
			maybeCompiled = new Some<String>(within.right);
		}

		var cParameters = jFunctionProto.parameters().iter().map(this::toCDeclaration).toList();
		if (jFunctionProto.methodDeclaration() instanceof JDeclaration)
			cParameters = cParameters.addFirst(new CDeclaration(new CPointerType(CPrimitiveType.Void), "_ref"));

		final var outputContent = this.computeMethodBody(jFunctionProto.typeParameters(),
																										 jFunctionProto.methodDeclaration(),
																										 cParameters,
																										 maybeCompiled,
																										 object.name,
																										 object.variants);

		final var mapped = this.transformMethodDeclaration(object.name,
																											 jFunctionProto.typeParameters(),
																											 jFunctionProto.methodDeclaration());
		final var header = new CFunctionHeader(mapped, cParameters);
		final var cFunction = new CFunction(header, outputContent);

		this.functionDeclarations = this.functionDeclarations.addLast(header.generate() + ";" + System.lineSeparator());
		this.functions = this.functions.addLast(cFunction);

		final var parameterTypes = cParameters.iter().map(CDeclaration::type).toList();
		return switch (jFunctionProto.methodDeclaration) {
			case JConstructor _ -> new EmptyStructMember();
			case JDeclaration member -> {
				final var cDeclaration = this.toCDeclaration(member);
				final var f1RDeclaration = new CFunctionDeclaration(cDeclaration.type, cDeclaration.name, parameterTypes);
				yield new CField(f1RDeclaration);
			}
			case Placeholder placeholder -> placeholder;
		};
	}

	private Option<JObjectMember> parseObjectMember(String input, String name, List<String> typeParameters) {
		final var stripped = input.strip();
		if (stripped.startsWith("//")) return new Some<JObjectMember>(new EmptyStructMember());
		if (stripped.isEmpty()) return new None<JObjectMember>();

		final var maybeEnum = this.parseObject("enum", input);
		if (maybeEnum instanceof Some<JObject>(var enum0)) return new Some<JObjectMember>(enum0);

		final var maybeInterface = this.parseObject("interface", input);
		if (maybeInterface instanceof Some<JObject>(var interface0)) return new Some<JObjectMember>(interface0);

		final var maybeRecord = this.parseObject("record", input);
		if (maybeRecord instanceof Some<JObject>(var record0)) return new Some<JObjectMember>(record0);

		final var maybeClass = this.parseObject("class", input);
		if (maybeClass instanceof Some<JObject>(var class0)) return new Some<JObjectMember>(class0);

		final var maybeEnumValues = this.parseEnumValuesStatement(input, name);
		if (maybeEnumValues instanceof Some<JObjectMember>(var enumValues)) return new Some<JObjectMember>(enumValues);

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var maybeDeclaration = this.parseDeclaration(substring);
			if (maybeDeclaration instanceof Some<JDeclaration>(var declaration)) {
				environment = environment.defineExpression(declaration);
				return new Some<JObjectMember>(new JField(declaration));
			}
		}

		final var maybeMethod = this.parseMethod(stripped, name, typeParameters);
		if (maybeMethod instanceof Some<JObjectMember>(var temp)) return new Some<JObjectMember>(temp);
		return new Some<JObjectMember>(new Placeholder(stripped));
	}

	private Option<JObjectMember> parseMethod(String stripped, String name, List<String> typeParameters) {
		final var i = stripped.indexOf("(");
		if (i < 0) return new None<JObjectMember>();

		final var declarationString = stripped.substring(0, i);
		final var substring1 = stripped.substring(i + 1);
		final var i1 = substring1.indexOf(")");
		if (i1 < 0) return new None<JObjectMember>();
		final var parametersString = substring1.substring(0, i1);
		final var withBraces = substring1.substring(i1 + 1).strip();

		final var parameters = this
				.divide(parametersString, new ValueFolder())
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList()
				.iter()
				.map(this::parseDeclaration)
				.flatMap(Option::iter)
				.toList();

		final var declaration = this.parseMethodDeclaration(declarationString, name);
		final var proto = new JMethod(typeParameters, parameters, declaration, withBraces);
		return new Some<JObjectMember>(proto);
	}

	private List<CDefinable> retainFields(List<CStructMember> members) {
		final var list = members.iter().map(member1 -> switch (member1) {
			case CField(var declaration) -> new Some<CDefinable>(declaration);
			case CFunctionDeclaration _, EmptyStructMember _, Placeholder _ -> new None<CDefinable>();
		}).flatMap(Option::iter).toList();

		return list.iter().filter(member -> !(member instanceof CFunctionDeclaration)).toList();
	}

	private Option<CDefinable> retainDefinables(CStructMember member) {
		return switch (member) {
			case CField(var declaration) -> new Some<CDefinable>(declaration);
			case CFunctionDeclaration functionDeclaration -> new Some<CDefinable>(functionDeclaration);
			case EmptyStructMember _, Placeholder _ -> new None<CDefinable>();
		};
	}

	private List<String> splitValues(String input) {
		final var segments = input.split(Pattern.quote(","));
		final var list = Arrays.stream(segments).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
		return new JavaList<String>(list);
	}

	private String computeMethodBody(List<String> typeParameters,
																	 JMethodDeclaration methodDeclaration,
																	 List<CDeclaration> cParameters,
																	 Option<String> maybeContent,
																	 String structName,
																	 List<String> structureVariants) {
		if (methodDeclaration instanceof JConstructor) {
			final var compiled = maybeContent.orElse("?");
			return Main.generateStatement(structName + " _thisInstance") +
						 generateStatement(structName + "* _this = &_thisInstance") + compiled +
						 Main.generateStatement("return " + "_thisInstance");
		}

		if (methodDeclaration instanceof JDeclaration declaration) {
			final var joinedTypeParameters = Main.joinTypeParameters(typeParameters);

			final var thisInitialization = Main.generateStatement(
					structName + joinedTypeParameters + "* _this = (" + structName + joinedTypeParameters + "*) _ref");

			final var body = maybeContent.orElseGet(() -> {
				final var type = this.transformType(declaration.type);
				final var list = cParameters.subList(1, cParameters.size()).iter().map(parameter -> parameter.name).toList();
				return this.createBodyForAbstractMethod(structureVariants, type, declaration.name, list, structName);
			});

			return thisInitialization + body;
		}

		return "?";
	}

	private String createBodyForAbstractMethod(List<String> variants,
																						 CType type,
																						 String name,
																						 List<String> parameterNames,
																						 String structName) {
		if (variants.isEmpty()) {
			final var joinedParameters = parameterNames.addFirst("_this->data").iter().collect(new Joiner(", "));

			return Main.generateStatement("return _this->table." + name + "(" + joinedParameters + ")");
		} else {
			final var returnValueDefinition = Main.generateStatement(type.generate() + " _ret");

			final var cases = variants
					.iter()
					.map(variant -> this.generateCase(variant, name, parameterNames, structName))
					.collect(new Joiner());

			return returnValueDefinition + generateIndent(1) + "switch (" + "_this->variant" + ") {" + cases +
						 generateIndent(1) + "}" + Main.generateStatement("return _ret");
		}
	}

	private CDefinable transformMethodDeclaration(String structName,
																								List<String> typeParameters,
																								JMethodDeclaration methodDeclaration) {
		return this
				.convertToFunctionDeclarations(typeParameters, methodDeclaration)
				.mapTypeParameters(typeParameters0 -> typeParameters0.addAllLast(typeParameters))
				.mapName(name -> name + "_" + structName);
	}

	private CDefinable convertToFunctionDeclarations(List<String> typeParameters, JMethodDeclaration methodDeclaration) {
		return switch (methodDeclaration) {
			case JConstructor constructor -> {
				final var type = this.toConstructorReturnType(constructor.type, typeParameters);
				yield new CDeclaration(type, "new");
			}
			case JDeclaration declaration -> this.toCDeclaration(declaration);
			case Placeholder placeholder -> placeholder;
		};
	}

	private CType toConstructorReturnType(String base, List<String> typeParameters) {
		if (base.isEmpty()) return new Identifier(base);

		final var typeArguments = typeParameters.iter().<CType>map(Identifier::new).toList();
		if (typeArguments.isEmpty()) return new Identifier(base);
		return new CTemplateType(base, typeArguments);
	}

	private String compileMethodsSegments(String inputContent, int indent) {
		return this.compileStatements(inputContent, input -> this.compileMethodSegment(input, indent));
	}

	private String generateCase(String variant, String name, List<String> parameterNames, String baseName) {
		final var s = "&(_this->data." + variant + ")";
		final var joined = parameterNames.copy().addFirst(s).iter().collect(new Joiner(", "));

		return generateIndent(2) + "case " + baseName + "Tag::" + variant + "Variant:" +
					 generateStatement(3, "_ret = " + name + "_" + variant + "(" + joined + ")") + generateStatement(3, "break");
	}

	private JMethodDeclaration parseMethodDeclaration(String declaration, String structName) {
		return this
				.parseConstructor(declaration, structName)
				.or(() -> this.parseDeclaration(declaration).map(this::toInterface))
				.orElseGet(() -> new Placeholder(declaration));
	}

	private JMethodDeclaration toInterface(JDeclaration value) {
		return value;
	}

	private Option<JMethodDeclaration> parseConstructor(String declaration, String structName) {
		final var stripped = declaration.strip();
		if (stripped.equals(structName)) return new Some<JMethodDeclaration>(new JConstructor(structName));

		final var i = stripped.lastIndexOf(" ");
		if (i >= 0) {
			final var substring = stripped.substring(i + 1).strip();
			if (substring.equals(structName)) return new Some<JMethodDeclaration>(new JConstructor(structName));
		}

		return new None<JMethodDeclaration>();
	}

	private Option<JObjectMember> parseEnumValuesStatement(String input, String structName) {
		final var stripped = input.strip();
		if (stripped.endsWith(";")) return this.parseEnumValues(structName, stripped.substring(0, stripped.length() - 1));
		return this.parseEnumValues(structName, stripped);
	}

	private Option<JObjectMember> parseEnumValues(String structName, String input) {
		final var enumValues = this
				.divide(input, (state, character) -> new ValueFolder().apply(state, character))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList();

		if (!enumValues.isEmpty()) {
			var optionStream = enumValues.iter().map(enumValue -> this.compileEnumValue(structName, enumValue));
			final var areAnyInvalid =
					(boolean) optionStream.collect(new AnyMatch<Option<CStructMember>>(option -> option instanceof None<CStructMember>));

			if (areAnyInvalid) return new None<JObjectMember>();
		}

		return new Some<JObjectMember>(new EmptyStructMember());
	}

	private Option<CStructMember> compileEnumValue(String structName, String input) {
		final var stripped = input.strip();
		if (stripped.endsWith(")")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i = substring.indexOf("(");
			if (i >= 0) {
				final var name = substring.substring(0, i);
				if (!Identifier.isIdentifier(name)) return new None<CStructMember>();

				final var substring2 = substring.substring(i + 1);
				final var generated =
						structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" +
						System.lineSeparator();

				this.globals = this.globals.addLast(generated);
				return new Some<CStructMember>(new EmptyStructMember());
			}
		}

		if (Identifier.isIdentifier(stripped)) {
			final var generated =
					structName + " " + structName + stripped + " = " + "new_" + structName + "()" + ";" + System.lineSeparator();

			this.globals = this.globals.addLast(generated);
			return new Some<CStructMember>(new EmptyStructMember());
		}

		return new None<CStructMember>();
	}

	private String compileMethodSegment(String input, int indent) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) return "";

		final var maybeIf = this.compileConditional("if", indent, stripped);
		if (maybeIf instanceof Some<String>(var result)) return result;

		final var maybeWhile = this.compileConditional("while", indent, stripped);
		if (maybeWhile instanceof Some<String>(var result)) return result;

		if (stripped.startsWith("else ")) {
			final var substring = stripped.substring("else ".length()).strip();
			if (substring.startsWith("{") && substring.endsWith("}")) {
				final var substring1 = substring.substring(1, substring.length() - 1);
				return generateIndent(indent) + "else {" + this.compileMethodsSegments(substring1, indent + 1) +
							 generateIndent(indent) + "}";
			} else return generateIndent(indent) + "else " + this.compileMethodSegment(substring, indent + 1);
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			return generateIndent(indent) + this.compileMethodStatement(substring) + ";";
		}

		if (stripped.startsWith("//")) return generateIndent(indent) + stripped;

		return System.lineSeparator() + "\t" + Placeholder.wrap(stripped);
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

				if (divisions.size() < 2) return new None<String>();

				final var first = divisions.getFirst();
				final var maybeWithBraces = this.joinStrings(divisions.subList(1, divisions.size()));

				if (!first.endsWith(")")) return new None<String>();
				final var condition = first.substring(0, first.length() - 1);

				if (maybeWithBraces.startsWith("{") && maybeWithBraces.endsWith("}")) {
					final var content = maybeWithBraces.substring(1, maybeWithBraces.length() - 1);
					return new Some<String>(
							generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" +
							this.compileMethodsSegments(content, indent + 1) + generateIndent(indent) + "}");
				}

				return new Some<String>(
						generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") " +
						this.compileMethodSegment(maybeWithBraces, indent + 1));
			}
		}

		return new None<String>();
	}

	private String compileMethodStatement(String input) {
		final var stripped = input.strip();
		if (stripped.equals("break")) return "break";

		if (stripped.startsWith("return "))
			return "return " + this.compileExpressionOrPlaceholder(stripped.substring("return ".length()));

		final var maybeAssignment = this.compileAssignment(stripped);
		if (maybeAssignment instanceof Some<String>(var assignment)) return assignment;

		final var maybeInvokable = this.parseInvokable(stripped);
		if (maybeInvokable instanceof Some(var value)) return this.transformExpression(value).generate();

		final var instance = this.post(stripped, "++");
		if (instance instanceof Some<String>(var x)) return x;

		final var instance0 = this.post(stripped, "--");
		if (instance0 instanceof Some<String>(var x)) return x;

		final var maybeDeclaration = this.parseDeclaration(input);
		if (maybeDeclaration instanceof Some<JDeclaration>(var declaration))
			return this.toCDeclaration(declaration).generate();

		if (stripped.startsWith("assert ")) return "";

		return Placeholder.wrap(stripped);
	}

	private Option<String> compileAssignment(String stripped) {
		final var index = stripped.indexOf("=");
		if (index >= 0) {
			final var destination = stripped.substring(0, index);
			final var substring1 = stripped.substring(index + 1);
			final var assignable = this.parseAssignable(destination);
			final var maybeSource = this.parseExpression(substring1);

			if (maybeSource instanceof Some<JExpression>(var source)) {
				final var cAssignable = this.transformAssignable(assignable, source);
				return new Some<String>(cAssignable.generate() + " = " + this.transformExpression(source).generate());
			}
		}

		return new None<String>();
	}

	private CAssignable transformAssignable(JAssignable assignable, JExpression source) {
		return switch (assignable) {
			case JDeclaration local -> {
				final var newType = this.resolveType(source, local.type);
				final var jDeclaration = local.withType(newType);
				environment = environment.defineExpression(jDeclaration);
				yield this.toCDeclaration(jDeclaration);
			}

			case JExpression jExpression -> this.transformExpression(jExpression);
			default -> throw new IllegalStateException("Unexpected value: " + assignable);
		};
	}

	private JType resolveType(JExpression source, JType type) {
		if (type.equals(JPrimitiveType.Var)) return this.resolveExpression(source);
		return type;
	}

	private JType resolveExpression(JExpression source) {
		return this.cleanupType(this.resolveUncleanedExpression(source));
	}

	private JType resolveUncleanedExpression(JExpression source) {
		return switch (source) {
			case Identifier(var value) -> this.resolveIdentifier(value);
			case JMemberAccess access -> {
				final var instance = access.instance;
				final var instanceType = this.resolveExpression(instance);

				if (instanceType instanceof JRecursiveType recursiveType)
					if (recursiveType.maybeInternal instanceof Some<JType>(var internal) &&
							internal instanceof JObjectType objectType)
						yield this.resolveMember(objectType, instanceType, access.memberName);

				if (instanceType instanceof JObjectType type) yield this.resolveMember(type, instanceType, access.memberName);

				assert !(instanceType instanceof JGenericType);
				yield new Placeholder(
						"Cannot access member '" + access.memberName + "' in '" + instanceType + "', not an object.");
			}
			case JInvokable jInvokable -> this.resolveCaller(jInvokable.caller);
			case JNumber _ -> JPrimitiveType.Int;
			case JNot _ -> JPrimitiveType.Boolean;
			case Placeholder placeholder -> placeholder;
			case Char _ -> JPrimitiveType.Char;
			case JOperator jOperator -> jOperator.operator.getType();
			case StringNode _ -> this.StringType;
			case JQuantity quantity -> this.resolveExpression(quantity.instance);
			default -> throw new IllegalStateException("Unexpected value: " + source);
		};
	}

	private JType resolveIdentifier(String value) {
		if (value.equals("this")) return environment
				.resolveCurrent()
				.<JType>map(thisType -> thisType)
				.orElseGet(() -> new Placeholder("Not within a struct"));

		final var maybeFound = environment.resolveExpression(value).map(JDeclaration::type);
		if (maybeFound instanceof Some<JType>(var found)) return found;

		if (environment.resolveType(value) instanceof Some<JObjectType>(var resolved)) return resolved;

		return new Placeholder("Undefined identifier: " + value);
	}

	private JType cleanupType(JType found) {
		if (found instanceof JGenericType genericType) {
			final var resolved = environment.resolveType(genericType.base);
			if (resolved instanceof Some<JObjectType>(var objType)) return objType;
			else return new Placeholder("Generic type '" + genericType.base + "' has not been defined");
		}

		return found;
	}

	private JType resolveMember(JObjectType type, JType instanceType, String name) {
		return type
				.resolve(name)
				.orElseGet(() -> new Placeholder("Member '" + name + "' not defined in '" + instanceType + "'"));
	}

	private JType resolveCaller(JCaller caller) {
		return switch (caller) {
			case JConstruction jConstruction -> jConstruction.jType;
			case JExpression jExpression -> {
				final var jType = this.resolveExpression(jExpression);
				if (jType instanceof JFunctionalType functionalType) yield functionalType.returnType;
				yield new Placeholder("Not a functional type: " + jType);
			}
			default -> throw new IllegalStateException("Unexpected value: " + caller);
		};
	}

	private JAssignable parseAssignable(String input) {
		return this
				.parseExpression(input)
				.<JAssignable>map(value -> value)
				.or(() -> this.parseDeclaration(input).map(value -> value))
				.orElseGet(() -> new Placeholder(input));
	}

	private Option<String> post(String stripped, String slice) {
		if (stripped.endsWith(slice)) {
			final var instance = stripped.substring(0, stripped.length() - 2);
			return new Some<String>(this.compileExpressionOrPlaceholder(instance) + slice);
		}

		return new None<String>();
	}

	private String compileExpressionOrPlaceholder(String input) {
		return this.parseCExpression(input).map(CExpression::generate).orElseGet(() -> Placeholder.wrap(input));
	}

	private Option<CExpression> parseCExpression(String input) {
		return this.parseExpression(input).map(this::transformExpression);
	}

	private Option<JExpression> parseExpression(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("(") && stripped.endsWith(")")) {
			final var content = stripped.substring(1, stripped.length() - 1);
			final var maybeParsed = this.parseExpression(content);
			if (maybeParsed instanceof Some<JExpression>(var parsed)) return new Some<JExpression>(new JQuantity(parsed));
		}

		if (stripped.startsWith("switch ")) return new Some<JExpression>(new Placeholder("TODO: switch"));

		final var i2 = stripped.lastIndexOf("::");
		if (i2 >= 0) {
			final var substring = stripped.substring(0, i2);
			final var methodName = stripped.substring(i2 + 2).strip();
			if (Identifier.isIdentifier(methodName)) {
				final var maybeInstance = this.parseExpression(substring);
				if (maybeInstance instanceof Some<JExpression>(var instance))
					return new Some<JExpression>(new JMethodAccess(instance, methodName));
			}
		}

		if (stripped.startsWith("'") && stripped.endsWith("'")) {
			final var content = stripped.substring(1, stripped.length() - 1);
			return new Some<JExpression>(new Char(content));
		}

		final var maybeLambda = this.compileLambda(stripped);
		if (maybeLambda instanceof Some<JExpression>) return maybeLambda;

		final var i3 = stripped.indexOf("instanceof");
		if (i3 >= 0) {
			final var substring = stripped.substring(0, i3);
			final var substring1 = stripped.substring(i3 + "instanceof".length()).strip();
			final var maybeInstance = this.parseCExpression(substring).map(CExpression::generate);
			if (maybeInstance instanceof Some<String>) {
				final var i4 = substring1.indexOf("<");
				return new Some<JExpression>(new JInstanceOf());
			}
		}

		final var maybeOperator = this
				.compileOperator(stripped, Operator.Equals)
				.or(() -> this.compileOperator(stripped, Operator.NotEquals))
				.or(() -> this.compileOperator(stripped, Operator.LessThan))
				.or(() -> this.compileOperator(stripped, Operator.Add))
				.or(() -> this.compileOperator(stripped, Operator.Subtract))
				.or(() -> this.compileOperator(stripped, Operator.And))
				.or(() -> this.compileOperator(stripped, Operator.Or))
				.or(() -> this.compileOperator(stripped, Operator.GreaterThanOrEquals));
		if (maybeOperator instanceof Some<JExpression>) return maybeOperator;

		final var maybeInvokable = this.parseInvokable(stripped);
		if (maybeInvokable instanceof Some<JExpression>) return maybeInvokable;

		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var instanceString = stripped.substring(0, i);
			final var memberName = stripped.substring(i + 1).strip();
			if (Identifier.isIdentifier(memberName)) {
				final var maybeInstance = this.parseExpression(instanceString);
				if (maybeInstance instanceof Some(var value))
					return new Some<JExpression>(new JMemberAccess(value, memberName));
			}
		}

		if (Identifier.isIdentifier(stripped)) return new Some<JExpression>(new Identifier(stripped));

		if (stripped.startsWith("!")) {
			final var substring = stripped.substring(1);
			final var maybeInstance = this.parseCExpression(substring);
			if (maybeInstance instanceof Some(var instance)) return new Some<JExpression>(new JNot(instance));
		}

		if (this.isNumber(stripped)) return new Some<JExpression>(new JNumber(stripped));

		if (stripped.startsWith("\"") && stripped.endsWith("\"") && stripped.length() >= 2)
			return new Some<JExpression>(new StringNode(stripped.substring(1, stripped.length() - 1)));

		return new None<JExpression>();
	}

	private Option<JExpression> compileLambda(String input) {
		final var index = input.indexOf("->");
		if (index < 0) return new None<JExpression>();

		final var beforeContent = input.substring(0, index).strip();
		final var maybeWithBraces = input.substring(index + 2).strip();

		final var maybeParams = this.parseLambdaParams(beforeContent);
		if (!(maybeParams instanceof Some<List<String>>(var params))) return new None<JExpression>();
		var paramList = params
				.iter()
				.map(param -> new CDeclaration(new Placeholder("TODO: resolve type of lambda param"), param))
				.toList()
				.addFirst(new CDeclaration(new CPointerType(CPrimitiveType.Void), "_ref"));

		final String output;
		if (maybeWithBraces.startsWith("{") && maybeWithBraces.endsWith("}")) {
			final var content = maybeWithBraces.substring(1, maybeWithBraces.length() - 1);
			output = this.compileMethodsSegments(content, 1);
		} else output = Main.generateStatement("return " + this.compileExpressionOrPlaceholder(maybeWithBraces));

		final var generatedName = this.generateName();
		final var cFunction =
				new CFunction(new CFunctionHeader(new CDeclaration(new Placeholder("TODO: resolve lambda return type"),
																													 generatedName), paramList), output);

		this.functions = this.functions.addLast(cFunction);
		return new Some<JExpression>(new Identifier(generatedName));
	}

	private Option<List<String>> parseLambdaParams(String input) {
		if (Identifier.isIdentifier(input)) return new Some<List<String>>(Lists.<String>empty().addLast(input));
		else if (input.startsWith("(") && input.endsWith(")")) {
			final var substring = input.substring(1, input.length() - 1);
			final var list =
					this.divide(substring, new ValueFolder()).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
			return new Some<List<String>>(list);
		} else return new None<List<String>>();
	}

	private String generateName() {
		final var generatedName = "lambda" + this.counter;
		this.counter++;
		return generatedName;
	}

	private Option<JExpression> compileOperator(String input, Operator operator) {
		final var operatorValue = operator.value;
		if (!input.contains(operatorValue)) return new None<JExpression>();

		var i1 = -1;
		var depth = 0;
		var i = 0;
		while (i < input.length() - 1) {
			final var c = input.charAt(i);
			if (c == operatorValue.charAt(0)) if (depth == 0) {
				i1 = i;
				break;
			}

			if (c == '(') depth++;
			if (c == ')') depth--;
			i++;
		}

		if (i1 >= 0) {
			final var leftString = input.substring(0, i1);
			final var rightString = input.substring(i1 + operatorValue.length());
			if (this.parseExpression(leftString) instanceof Some(var leftCompiled))
				if (this.parseExpression(rightString) instanceof Some(var rightCompiled))
					return new Some<JExpression>(new JOperator(leftCompiled, operator, rightCompiled));
		}

		return new None<JExpression>();
	}

	private Option<JExpression> parseInvokable(String stripped) {
		if (!stripped.endsWith(")")) return new None<JExpression>();

		final var length = stripped.length();
		final var withoutEnd = stripped.substring(0, length - 1);

		final var callerStart = this.findCallerStart(withoutEnd);

		if (callerStart < 0) return new None<JExpression>();
		final var callerString = withoutEnd.substring(0, callerStart);
		final var argumentsString = withoutEnd.substring(callerStart + 1);

		final var maybeCaller = this.parseCaller(callerString);

		if (!(maybeCaller instanceof Some(var value))) return new None<JExpression>();
		final var arguments = this
				.divide(argumentsString, new EscapedFolder(new ValueFolder()))
				.map(this::parseExpression)
				.flatMap(Option::iter)
				.toList();

		return new Some<JExpression>(new JInvokable(value, arguments));
	}

	private int findCallerStart(String withoutEnd) {
		var callerStart = -1;
		var depth = 0;
		var i = 0;
		while (i < withoutEnd.length()) {
			final var c = withoutEnd.charAt(i);
			if (c == '(') {
				if (depth == 0) callerStart = i;

				depth++;
			}
			if (c == ')') depth--;
			i++;
		}
		return callerStart;
	}

	private boolean isNumber(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) return false;
		if (stripped.startsWith("-")) return this.allDigits(stripped.substring(1));
		return this.allDigits(stripped);
	}

	private boolean allDigits(String input) {
		return IntStream.range(0, input.length()).mapToObj(input::charAt).allMatch(Character::isDigit);
	}

	private Option<JCaller> parseCaller(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("new ")) {
			final var type = stripped.substring("new ".length());
			final var jType = this.parseType(type);
			return new Some<JCaller>(new JConstruction(jType));
		}

		final var maybeExpression = this.parseExpression(stripped);
		if (maybeExpression instanceof Some<JExpression>(var expression)) return new Some<JCaller>(expression);

		return new None<JCaller>();
	}

	private Option<JDeclaration> parseDeclaration(String input) {
		final var stripped = input.strip();
		final var nameSeparator = stripped.lastIndexOf(" ");
		if (nameSeparator >= 0) {
			final var beforeName = stripped.substring(0, nameSeparator).strip();
			final var name = stripped.substring(nameSeparator + 1).strip();

			final var typeSeparator = this.findTypeSeparator(beforeName);

			if (!Identifier.isIdentifier(name)) return new None<JDeclaration>();

			if (typeSeparator < 0) {
				final var type = this.parseType(beforeName);
				return new Some<JDeclaration>(new JDeclaration(name, type));
			}

			var beforeType = beforeName.substring(0, typeSeparator).strip();

			List<String> copy = Lists.empty();
			if (beforeType.endsWith(">")) {
				final var substring = beforeType.substring(0, beforeType.length() - 1);
				final var i = substring.indexOf("<");
				if (i >= 0) {
					final var substring2 = substring.substring(i + 1);
					copy = this.splitValues(substring2);
					beforeType = substring.substring(0, i);
				}
			}

			List<String> annotations = Lists.empty();
			final var i = beforeType.lastIndexOf("\n");
			if (i >= 0) {
				annotations = this.collectAnnotations(beforeType.substring(0, i));

				beforeType = beforeType.substring(i + 1).strip();
			}

			if (Identifier.isIdentifier(name)) {
				final var type = this.parseType(beforeName.substring(typeSeparator + 1));
				final var jDeclaration = new JDeclaration(annotations, copy, new Some<String>(beforeType), type, name);
				return new Some<JDeclaration>(jDeclaration);
			}
		}

		return new None<JDeclaration>();
	}

	private List<String> collectAnnotations(String input) {
		return Streams
				.fromObjArray(input.split(Pattern.quote("\n")))
				.filter(slice -> !slice.isEmpty())
				.map(slice -> slice.substring(1))
				.map(String::strip)
				.toList();
	}

	private int findTypeSeparator(String beforeName) {
		var typeSeparator = -1;
		var depth = 0;
		var i = 0;
		while (i < beforeName.length()) {
			final var c = beforeName.charAt(i);
			if (c == ' ' && depth == 0) typeSeparator = i;
			if (c == '<') depth++;
			if (c == '>') depth--;
			i++;
		}
		return typeSeparator;
	}

	private JType parseType(String input) {
		final var stripped = input.strip();
		switch (stripped) {
			case "boolean", "Boolean" -> {
				return JPrimitiveType.Boolean;
			}
			case "int", "Integer" -> {
				return JPrimitiveType.Int;
			}
			case "void" -> {
				return JPrimitiveType.Void;
			}
			case "String" -> {
				return this.StringType;
			}
			case "Character" -> {
				return JPrimitiveType.Char;
			}
			case "var" -> {
				return JPrimitiveType.Var;
			}
		}

		if (stripped.endsWith("[]")) {
			final var slice = stripped.substring(0, stripped.length() - 2);
			final var type = this.parseType(slice);
			return new JArrayType(type);
		}

		if (stripped.endsWith(">")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i = substring.indexOf("<");
			if (i >= 0) {
				final var base = substring.substring(0, i);
				final var parameters = substring.substring(i + 1);

				final var list = this.divide(parameters, new ValueFolder()).map(this::parseType).toList();

				return new JGenericType(base, list);
			}
		}

		if (Identifier.isIdentifier(stripped)) return new Identifier(stripped);

		// TODO: handle varargs through monomorphization

		return new Placeholder(stripped);
	}
}
