package magma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Main {
	private enum CPrimitiveType implements CType {
		Void("void"), Char("char"), Int("int");

		private final String content;

		CPrimitiveType(String content) {this.content = content;}

		@Override
		public String generate() {
			return this.content;
		}

		@Override
		public String toBaseName() {
			return this.content;
		}
	}

	private enum JPrimitiveType implements JType {
		Int, Void, Boolean, Char, Var
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

		List<T> addAll(List<T> elements);

		int size();

		T getFirst();

		List<T> subList(int start, int end);

		List<T> clear();

		List<T> removeLast();

		List<T> mapLast(Function<T, T> mapper);

		Iter<T> iterReversed();
	}

	private interface Path {
		Path resolveSibling(String sibling);

		Option<IOError> writeString(String output);

		Result<String, IOError> readString();
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

	private sealed interface CType permits Identifier, Placeholder, CPointerType, CPrimitiveType, CTemplateType {
		String generate();

		String toBaseName();
	}

	private sealed interface JMethodDeclaration permits JConstructor, JDeclaration, Placeholder {}

	private sealed interface CStructMember permits EmptyStructMember, CField, F1RDeclaration, Placeholder {
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

	private interface CFunctionDeclaration {
		default CFunctionDeclaration mapTypeParameters(F1R<List<String>, List<String>> mapper) {
			return this;
		}

		default CFunctionDeclaration mapName(F1R<String, String> mapper) {
			return this;
		}

		String generate();
	}

	private interface CAssignable {
		String generate();
	}

	private sealed interface JType
			permits Identifier, JArrayType, JFunctionalType, JGenericType, JObjectType, JPrimitiveType, JRecursiveType,
			Placeholder {}

	private sealed interface JAssignable permits JDeclaration, JExpression, JExpressionWrapper, Placeholder {}

	sealed private interface JExpression extends JCaller, JAssignable
			permits Identifier, JExpressionWrapper, JInvokable, JMemberAccess {
		default CAssignable toAssignable() {
			return this.toExpression();
		}

		CExpression toExpression();
	}

	private interface CExpression extends CAssignable {}

	private sealed interface JCaller permits JConstruction, JExpression {
		CExpression toExpression();
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

		public StringBuilder appendString(String chars) {
			return Streams.fromCharArray(chars.toCharArray()).fold(this, StringBuilder::appendChar);
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

		@SafeVarargs
		public static <T> List<T> of(T... elements) {
			return Streams.fromObjArray(elements).collect(new ListCollector<T>());
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
		public List<T> addAll(List<T> elements) {
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
		public List<T> mapLast(Function<T, T> mapper) {
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
	}

	private record CTemplateType(String base, List<CType> list) implements CType {
		@Override
		public String generate() {
			final var typeArguments = this.list.iter().map(CType::generate).collect(new Joiner(", "));
			return this.base + "<" + typeArguments + ">";
		}

		@Override
		public String toBaseName() {
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

	private record Identifier(String value) implements CType, JType, JExpression, CExpression {
		@Override
		public String generate() {
			return this.value;
		}

		@Override
		public String toBaseName() {
			return this.value;
		}

		@Override
		public CExpression toExpression() {
			return new CQuantity(new CDereference(new Identifier("_this")));
		}
	}

	private record Placeholder(String input)
			implements CType, JMethodDeclaration, CStructMember, CFunctionDeclaration, CAssignable, JAssignable, JType {
		@Override
		public String generate() {
			return wrap(this.input);
		}

		@Override
		public String toBaseName() {
			return wrap(this.input);
		}

		private CAssignable toCAssignable() {
			return this;
		}

		public CType toCType() {
			return this;
		}
	}

	private record JConstructor(String type) implements JMethodDeclaration {}

	private record JDeclaration(List<String> annotations, List<String> typeParameters, Option<String> maybeBeforeType,
															JType type, String name) implements JMethodDeclaration, JAssignable {
		public JDeclaration(JType type, String name) {
			this(Lists.empty(), Lists.empty(), new None<String>(), type, name);
		}

		public JDeclaration mapName(F1R<String, String> mapper) {
			return new JDeclaration(this.annotations,
															this.typeParameters,
															this.maybeBeforeType,
															this.type,
															mapper.apply(this.name));
		}

		public CDeclaration toCDeclaration() {
			return new CDeclaration(this.typeParameters, transformType(this.type), this.name);
		}

		private CAssignable toCAssignable() {
			return this.toCDeclaration();
		}

		public JDeclaration withType(JType type) {
			return new JDeclaration(this.annotations, this.typeParameters, this.maybeBeforeType, type, this.name);
		}
	}

	private record F1RDeclaration(CType type, String name, List<CType> parameterTypes) implements CStructMember {
		@Override
		public String generate() {
			final var joinedParameterTypes =
					"(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", ")) + ")";
			return this.type.generate() + " (*" + this.name + ")" + joinedParameterTypes;
		}
	}

	private static final class EmptyStructMember implements CStructMember {
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

	private record CField(CDeclaration declaration) implements CStructMember {
		@Override
		public String generate() {
			return Main.generateStatement(1, this.declaration.generate());
		}
	}

	private static class Streams {
		public static <T> Iter<T> fromObjArray(T[] elements) {
			return new Iter<Integer>(new RangeHead(elements.length)).map(index -> elements[index]);
		}

		public static Iter<Character> fromCharArray(char[] array) {
			return new Iter<Integer>(new RangeHead(array.length)).map(index -> array[index]);
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
		public static Path get(String first, String... more) {
			return new JavaPath(java.nio.file.Paths.get(first, more));
		}
	}

	@Actual
	private record JavaPath(java.nio.file.Path path) implements Path {
		@Override
		public Path resolveSibling(String sibling) {
			return new JavaPath(this.path.resolveSibling(sibling));
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

	private record CDeclaration(List<String> typeParameters, CType type, String name)
			implements CFunctionDeclaration, CAssignable {
		public CDeclaration(CType type, String name) {
			this(Lists.empty(), type, name);
		}

		@Override
		public CFunctionDeclaration mapName(F1R<String, String> mapper) {
			return new CDeclaration(this.typeParameters, this.type, mapper.apply(this.name));
		}

		@Override
		public CFunctionDeclaration mapTypeParameters(F1R<List<String>, List<String>> mapper) {
			return new CDeclaration(mapper.apply(this.typeParameters), this.type, this.name);
		}

		@Override
		public String generate() {
			final var template = generateTemplateString(this.typeParameters);
			return template + this.type.generate() + " " + this.name;
		}
	}

	private record JExpressionWrapper(String content) implements JExpression, JAssignable {
		@Override
		public CExpression toExpression() {
			return new CExpressionWrapper(this.content);
		}

		@Override
		public CAssignable toAssignable() {
			return new CExpressionWrapper(this.content);
		}
	}

	private record CExpressionWrapper(String content) implements CExpression {
		@Override
		public String generate() {
			return this.content;
		}
	}

	private record JArrayType(JType type) implements JType {
		public CType toCType() {
			return new CPointerType(transformType(this.type));
		}
	}

	private record JGenericType(String base, List<JType> typeArguments) implements JType {
		public CType toCType() {
			final var newTypeArguments = this.typeArguments.iter().map(Main::transformType).toList();
			return new CTemplateType(this.base, newTypeArguments);
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

	private record JMemberAccess(JExpression instance, String memberName) implements JExpression {
		@Override
		public CExpression toExpression() {
			final var cExpression = this.instance.toExpression();
			if (this.instance instanceof Identifier(var value) && value.equals("this"))
				return new CPointerAccess(new Identifier("_this"), this.memberName);
			else return new CFieldAccess(cExpression, this.memberName);
		}
	}

	private record JConstruction(JType jType) implements JCaller {
		@Override
		public CExpression toExpression() {
			return new Identifier("new_" + transformType(this.jType).generate());
		}
	}

	private record CInvocation(CExpression expression, List<CExpression> cArguments) implements CExpression {
		@Override
		public String generate() {
			final var joinedArguments = this.cArguments().iter().map(CAssignable::generate).collect(new Joiner(", "));
			return this.expression().generate() + "(" + joinedArguments + ")";
		}
	}

	private record JInvokable(JCaller caller, List<JExpression> arguments) implements JExpression {
		@Override
		public CExpression toExpression() {
			final var cArguments = this.arguments().iter().map(JExpression::toExpression).toList();
			final var expression = this.caller().toExpression();
			return new CInvocation(expression, cArguments);
		}
	}

	private record JFunctionalType(List<JType> parameterTypes, JType returnType) implements JType {
		public JFunctionalType(JType returnType) {
			this(new JavaList<JType>(), returnType);
		}
	}

	private static class Environment {
		private List<Frame> frames = new JavaList<Frame>();

		private Option<JDeclaration> resolveExpression(String identifier) {
			return this.frames.iter().map(frame -> frame.resolve(identifier)).flatMap(Option::iter).next();
		}

		public <T> Tuple<Environment, T> withinScoped(F1R<Environment, Tuple<Environment, T>> supplier) {
			this.frames = this.frames.addLast(new Frame());
			final var result = supplier.apply(this);
			this.frames = this.frames.removeLast();
			return result;
		}

		public Environment defineAll(List<JDeclaration> declarations) {
			this.frames = this.frames.mapLast(last -> last.defineAll(declarations));
			return this;
		}

		public <T> Tuple<Environment, T> within(Supplier<T> supplier) {
			this.frames = this.frames.addLast(new Frame());
			final var result = supplier.get();
			this.frames = this.frames.removeLast();
			return new Tuple<Environment, T>(this, result);
		}

		public Environment define(JDeclaration declaration) {
			this.frames = this.frames.mapLast(last -> last.define(declaration));
			return this;
		}

		public Option<JObjectType> resolveCurrent() {
			return this.frames.iterReversed().map(Frame::toStructureType).flatMap(Option::iter).next();
		}

		public Environment withName(String name) {
			this.frames = this.frames.mapLast(last -> last.withName(name));
			return this;
		}
	}

	private static class Frame {
		private final Option<String> maybeName;
		private List<JDeclaration> definitions;

		private Frame(Option<String> maybeName, List<JDeclaration> defined) {
			this.maybeName = maybeName;
			this.definitions = defined;
		}

		public Frame() {
			this(new None<String>(), new JavaList<JDeclaration>());
		}

		public Frame defineAll(List<JDeclaration> declarations) {
			return new Frame(this.maybeName, this.definitions.addAll(declarations));
		}

		public Option<JDeclaration> resolve(String identifier) {
			return this.definitions.iter().filter(define -> define.name.equals(identifier)).next();
		}

		public Frame define(JDeclaration declaration) {
			this.definitions = this.definitions.addLast(declaration);
			return this;
		}

		public Option<JObjectType> toStructureType() {
			return this.maybeName.map(name -> new JObjectType(name, this.definitions));
		}

		public Frame withName(String name) {
			return new Frame(new Some<String>(name), this.definitions);
		}
	}

	private record JObjectType(String name, List<JDeclaration> members) implements JType {
		private Option<JType> resolve(String name) {
			return this.members.iter().filter(member -> member.name.equals(name)).next().map(JDeclaration::type);
		}
	}

	private static final class JRecursiveType implements JType {
		private Option<JType> internal = new None<JType>();

		public static JType create(F1R<JType, JType> mapper) {
			final var created = new JRecursiveType();
			final var apply = mapper.apply(created);
			created.set(apply);
			return created;
		}

		private void set(JType created) {
			this.internal = new Some<JType>(created);
		}
	}

	private static final JType StringType = JRecursiveType.create(StringType -> {
		final var methods = Lists.of(new JDeclaration(new JFunctionalType(StringType), "strip"));
		return new JObjectType("String", methods);
	});

	private Environment environment = new Environment();
	private List<String> functionDeclarations;
	private List<String> globals;
	private List<String> structures;
	private List<String> functions;
	private int counter;

	public Main() {
		this.structures = Lists.empty();

		this.functionDeclarations = Lists.empty();
		this.functions = Lists.empty();

		this.globals = Lists.empty();
		this.counter = 0;
	}

	private static String generateTemplateString(List<String> typeParameters) {
		final String templateString;
		if (typeParameters.isEmpty()) templateString = "";
		else {
			final var typeNames = typeParameters.iter().map(typeParam -> "typename " + typeParam).collect(new Joiner(", "));

			templateString = "template <" + typeNames + ">" + System.lineSeparator();
		}
		return templateString;
	}

	private static String wrap(String input) {
		final var replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}

	public static void main(String[] args) {
		if (new Main().run() instanceof Some<IOError>(
				var value
		)) System.err.println(value.display());
	}

	private static String generateStatement(int depth, String content) {
		return generateIndent(depth) + content + ";";
	}

	private static String generateIndent(int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}

	private static CType transformType(JType jType) {
		return switch (jType) {
			case Identifier identifier -> identifier;
			case JArrayType jArrayType -> jArrayType.toCType();
			case JGenericType jGenericType -> jGenericType.toCType();
			case JPrimitiveType jPrimitiveType -> transformPrimitiveType(jPrimitiveType);
			case Placeholder placeholder -> placeholder.toCType();
			case JFunctionalType jFunctionalType -> new Placeholder(jFunctionalType.toString());
			case JObjectType jStructureType -> new Identifier(jStructureType.name);
			case JRecursiveType jRecursiveType -> new Placeholder("???");
		};
	}

	private static CType transformPrimitiveType(JPrimitiveType type) {
		return switch (type) {
			case Int, Boolean -> CPrimitiveType.Int;
			case Void -> CPrimitiveType.Void;
			case Char -> CPrimitiveType.Char;
			case Var -> new Placeholder("var");
		};
	}

	private Option<IOError> run() {
		final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		final var target = source.resolveSibling("Main.cpp");
		final var input = source.readString().mapValue(this::compile);

		return switch (input) {
			case Err<String, IOError> v -> new Some<IOError>(v.error);
			case Ok<String, IOError> v -> target.writeString(v.value);
		};
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
		return structures.iter().collect(new Joiner(delimiter));
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
						if (right == '\r' || right == '\n') withoutLineCommentPrefix = withoutLineCommentPrefix.advance();
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

		return this.compileStructure("class", stripped).map(CStructMember::generate).orElseGet(() -> wrap(stripped));
	}

	private Option<CStructMember> compileStructure(String type, String stripped) {
		final var i = stripped.indexOf(type + " ");
		if (i < 0) return new None<CStructMember>();
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

		if (annotations.contains("Actual")) return new Some<CStructMember>(new EmptyStructMember());

		final var afterKeyword = stripped.substring(i + (type + " ").length()).strip();

		final var i1 = afterKeyword.indexOf("{");
		if (i1 < 0) return new None<CStructMember>();
		var beforeContent = afterKeyword.substring(0, i1).strip();

		final var withEnd = afterKeyword.substring(i1 + 1).strip();
		if (!withEnd.endsWith("}")) return new None<CStructMember>();
		final var inputContent = withEnd.substring(0, withEnd.length() - 1);

		List<String> variants = Lists.empty();
		final var i2 = beforeContent.indexOf("permits ");
		if (i2 >= 0) {
			final var substring1 = beforeContent.substring(i2 + "permits ".length());
			beforeContent = beforeContent.substring(0, i2);

			variants = this.splitValues(substring1);
		}

		List<CType> implementees = Lists.empty();
		final var i4 = beforeContent.indexOf("implements ");
		if (i4 >= 0) {
			final var implementeesString = beforeContent.substring(i4 + "implements ".length());
			beforeContent = beforeContent.substring(0, i4).strip();
			implementees = this
					.divide(implementeesString, (state, character) -> new ValueFolder().apply(state, character))
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.map(input -> transformType(this.parseType(input)))
					.toList();
		}

		List<JDeclaration> recordFields = Lists.empty();
		if (beforeContent.endsWith(")")) {
			final var substring = beforeContent.substring(0, beforeContent.length() - 1);
			final var i3 = substring.indexOf("(");
			if (i3 >= 0) {
				beforeContent = substring.substring(0, i3);
				recordFields = this
						.divide(substring.substring(i3 + 1), (state, character) -> new ValueFolder().apply(state, character))
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

		if (!this.isIdentifier(beforeContent)) return new None<CStructMember>();

		var modifiersList = Streams
				.fromObjArray(modifiers.split(Pattern.quote(" ")))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList();

		var name = beforeContent.strip();

		final var templateString = generateTemplateString(typeParameters);
		final var joinedTypeParameters = this.joinTypeParameters(typeParameters);

		var fields = StringBuilders.empty();
		var dependencies = StringBuilders.empty();
		this.functions = implementees
				.iter()
				.map(implementee -> this.getString(implementee, name, joinedTypeParameters, templateString))
				.fold(this.functions, List::addLast);

		final var joinedRecordFields = recordFields
				.iter()
				.map(JDeclaration::toCDeclaration)
				.map(CDeclaration::generate)
				.map(this::generateStatement)
				.collect(new Joiner());

		var finalTypeParameters = typeParameters;
		var finalVariants = variants;

		final var within = this.environment.withinScoped((env) -> {
			final var withName = env.withName(name);

			// Note that withName is not used by members here, but should be accessible because Environment has a de facto
			// mutable implementation
			// But if environment becomes immutable, then we have to pass withName as a parameter here eventually
			final var members = this
					.divide(inputContent, new EscapedFolder(this::foldStatement))
					.map(slice -> this.compileClassSegment(slice, name, finalTypeParameters, finalVariants))
					.flatMap(Option::iter)
					.toList();

			return new Tuple<Environment, List<CStructMember>>(withName, members);
		});

		this.environment = within.left;
		var members = within.right;

		if (modifiersList.contains("sealed")) {
			final var enumFields =
					variants.iter().map(variant -> System.lineSeparator() + "\t" + variant + "Variant").collect(new Joiner(","));

			final var generatedEnum =
					"enum " + name + "Variant {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();

			final var unionFields = variants
					.iter()
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
					members.iter().map(CStructMember::generate).map(this::generateStatement).collect(new Joiner(""));
			final var vTable = templateString + "struct " + name + "Table {" + tableMembers + System.lineSeparator() + "};" +
												 System.lineSeparator();

			dependencies = dependencies.appendString(vTable);
			fields = fields.appendString(table).appendString(data);
		} else {
			final var joinedMembers = members
					.iter()
					.filter(member -> !(member instanceof F1RDeclaration))
					.map(CStructMember::generate)
					.collect(new Joiner());

			fields = fields.appendString(joinedMembers);
		}

		final var generated =
				dependencies + templateString + "struct " + name + " {" + joinedRecordFields + fields + System.lineSeparator() +
				"};" + System.lineSeparator();

		this.structures = this.structures.addLast(generated);
		return new Some<CStructMember>(new EmptyStructMember());
	}

	private String getString(CType implementee, String name, String joinedTypeParameters, String templateString) {
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
		if (typeParameters.isEmpty()) joinedTypeParameters = "";
		else joinedTypeParameters = "<" + typeParameters.iter().collect(new Joiner(", ")) + ">";

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

	private Option<CStructMember> compileClassSegment(String input,
																										String structName,
																										List<String> typeParameters,
																										List<String> variants) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) return new None<CStructMember>();

		final var maybeEnum = this.compileStructure("enum", input);
		if (maybeEnum instanceof Some<CStructMember>) return maybeEnum;

		final var maybeInterface = this.compileStructure("interface", input);
		if (maybeInterface instanceof Some<CStructMember>) return maybeInterface;

		final var maybeRecord = this.compileStructure("record", input);
		if (maybeRecord instanceof Some<CStructMember>) return maybeRecord;

		final var maybeClass = this.compileStructure("class", input);
		if (maybeClass instanceof Some<CStructMember>) return maybeClass;

		final var maybeEnumValues = this.compileEnumValues(input, structName);
		if (maybeEnumValues instanceof Some<CStructMember>) return maybeEnumValues;

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var maybeDeclaration = this.parseDeclaration(substring);
			if (maybeDeclaration instanceof Some<JDeclaration>(var declaration)) {
				this.environment = this.environment.define(declaration);
				return new Some<CStructMember>(new CField(declaration.toCDeclaration()));
			}
		}

		final var maybeMethod = this.compileMethod(structName, typeParameters, variants, stripped);
		if (maybeMethod instanceof Some<CStructMember>) return maybeMethod;

		return new Some<CStructMember>(new Placeholder(stripped));
	}

	private Option<CStructMember> compileMethod(String structName,
																							List<String> typeParameters,
																							List<String> variants,
																							String input) {
		final var i = input.indexOf("(");
		if (i < 0) return new None<CStructMember>();

		final var declarationString = input.substring(0, i);
		final var substring1 = input.substring(i + 1);
		final var i1 = substring1.indexOf(")");
		if (i1 < 0) return new None<CStructMember>();
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

		var cParameters = parameters.iter().map(JDeclaration::toCDeclaration).toList();

		final var methodDeclaration = this.parseMethodDeclaration(declarationString, structName);

		Option<String> maybeCompiled = new None<String>();
		if (methodDeclaration instanceof JDeclaration declaration && declaration.annotations.contains("Actual")) {
			final var compiledParameters = cParameters.iter().map(CDeclaration::generate).collect(new Joiner(", "));

			final var modifiedMethodDeclaration = declaration.mapName(name -> name + "_" + structName).toCDeclaration();

			this.functionDeclarations = this.functionDeclarations.addLast(
					modifiedMethodDeclaration.generate() + "(" + compiledParameters + ");" + System.lineSeparator());

			return new Some<CStructMember>(new EmptyStructMember());
		}

		if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
			final var inputContent = withBraces.substring(1, withBraces.length() - 1);

			final var within = this.environment.withinScoped((env) -> {
				return env.defineAll(parameters).within(() -> {
					return new Some<String>(this.compileMethodsSegments(inputContent, 1));
				});
			});

			this.environment = within.left;
			maybeCompiled = within.right;
		}

		String outputContent;
		if (methodDeclaration instanceof JConstructor) {
			final var compiled = maybeCompiled.orElse("?");
			outputContent =
					this.generateStatement(structName + " _this") + compiled + this.generateStatement("return " + "_this");
		} else if (methodDeclaration instanceof JDeclaration declaration) {
			cParameters = cParameters.addFirst(new CDeclaration(new CPointerType(CPrimitiveType.Void), "_ref"));

			final var joinedTypeParameters = this.joinTypeParameters(typeParameters);

			final var thisInitialization = this.generateStatement(
					structName + joinedTypeParameters + "* _this = (" + structName + joinedTypeParameters + "*) _ref");

			var finalParameters = cParameters;
			outputContent = thisInitialization + maybeCompiled.orElseGet(() -> {
				if (variants.isEmpty()) {
					final var joinedParameters = finalParameters
							.subList(1, finalParameters.size())
							.iter()
							.map(parameter -> parameter.name)
							.toList()
							.addFirst("_this->data")
							.iter()
							.collect(new Joiner(", "));

					return this.generateStatement("return _this->table." + declaration.name + "(" + joinedParameters + ")");
				} else {
					final var returnValueDefinition =
							this.generateStatement(transformType(declaration.type).generate() + " _ret");

					final var cases =
							variants.iter().map(variant -> this.generateCase(declaration, variant)).collect(new Joiner());

					return returnValueDefinition + generateIndent(1) + "switch (" + "_this->variant" + ") {" + cases +
								 generateIndent(1) + "}" + this.generateStatement("return _ret");
				}
			});
		} else outputContent = "?";

		final var compiledParameters = cParameters.iter().map(CDeclaration::generate).collect(new Joiner(", "));

		final var modifiedMethodDeclaration = switch (methodDeclaration) {
			case JConstructor constructor -> {
				final var type = this.toConstructorReturnType(constructor.type, typeParameters);
				yield (CFunctionDeclaration) new CDeclaration(type, "new");
			}
			case JDeclaration declaration -> declaration.toCDeclaration();
			case Placeholder placeholder -> placeholder;
		};

		final var mapped = modifiedMethodDeclaration
				.mapTypeParameters(typeParameters0 -> typeParameters0.addAll(typeParameters))
				.mapName(name -> name + "_" + structName);

		final var header = mapped.generate() + "(" + compiledParameters + ")";
		final var generated = header + "{" + outputContent + System.lineSeparator() + "}" + System.lineSeparator();

		this.functionDeclarations = this.functionDeclarations.addLast(header + ";" + System.lineSeparator());
		this.functions = this.functions.addLast(generated);

		final var parameterTypes = cParameters.iter().map(CDeclaration::type).toList();

		return switch (methodDeclaration) {
			case JConstructor _ -> new Some<CStructMember>(new EmptyStructMember());
			case JDeclaration member -> {
				final var cDeclaration = member.toCDeclaration();
				final var f1RDeclaration = new F1RDeclaration(cDeclaration.type, cDeclaration.name, parameterTypes);
				yield new Some<CStructMember>(f1RDeclaration);
			}
			case Placeholder placeholder -> new Some<CStructMember>(placeholder);
		};
	}

	private CType toConstructorReturnType(String base, List<String> typeParameters) {
		if (base.isEmpty()) return new Identifier(base);

		final var typeArguments = typeParameters.iter().<CType>map(Identifier::new).toList();
		return new CTemplateType(base, typeArguments);
	}

	private String compileMethodsSegments(String inputContent, int indent) {
		return this.compileStatements(inputContent, input -> this.compileMethodSegment(input, indent));
	}

	private String generateCase(JDeclaration declaration, String variant) {
		return generateIndent(2) + "case " + variant + "Variant:" +
					 generateStatement(3, "_ret = " + declaration.name + "_" + variant + "(&(_this->data." + variant + "))") +
					 generateStatement(3, "break");
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

	private Option<CStructMember> compileEnumValues(String input, String structName) {
		final var stripped = input.strip();
		if (!stripped.endsWith(";")) return new None<CStructMember>();

		final var enumValues = this
				.divide(stripped.substring(0, stripped.length() - 1),
								(state, character) -> new ValueFolder().apply(state, character))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList();

		if (!enumValues.isEmpty()) {
			var optionStream = enumValues.iter().map(enumValue -> this.compileEnumValue(structName, enumValue));
			final var areAnyInvalid =
					(boolean) optionStream.collect(new AnyMatch<Option<CStructMember>>(option -> option instanceof None<CStructMember>));

			if (areAnyInvalid) return new None<CStructMember>();
		}

		return new Some<CStructMember>(new EmptyStructMember());
	}

	private Option<CStructMember> compileEnumValue(String structName, String enumValue) {
		if (enumValue.endsWith(")")) {
			final var substring = enumValue.substring(0, enumValue.length() - 1);
			final var i = substring.indexOf("(");
			if (i >= 0) {
				final var name = substring.substring(0, i);
				if (!this.isIdentifier(name)) return new None<CStructMember>();

				final var substring2 = substring.substring(i + 1);
				final var generated =
						structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" +
						System.lineSeparator();

				this.globals = this.globals.addLast(generated);
				return new Some<CStructMember>(new EmptyStructMember());
			}
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

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			return generateIndent(indent) + this.compileMethodStatement(substring) + ";";
		}

		if (stripped.startsWith("else ")) {
			final var substring = stripped.substring("else ".length()).strip();
			if (substring.startsWith("{") && substring.endsWith("}")) {
				final var substring1 = substring.substring(1, substring.length() - 1);
				return generateIndent(indent) + "else {" + this.compileMethodsSegments(substring1, indent + 1) +
							 generateIndent(indent) + "}";
			} else return generateIndent(indent) + "else " + this.compileMethodSegment(substring, indent);
		}

		if (stripped.startsWith("//")) return generateIndent(indent) + stripped;

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

				if (divisions.size() < 2) return new None<String>();

				final var first = divisions.getFirst();
				final var maybeWithBraces = this.joinStrings("", divisions.subList(1, divisions.size()));

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
		if (maybeInvokable instanceof Some(var value)) return value.toExpression().generate();

		final var instance = this.post(stripped, "++");
		if (instance instanceof Some<String>(var x)) return x;

		final var instance0 = this.post(stripped, "--");
		if (instance0 instanceof Some<String>(var x)) return x;

		final var maybeDeclaration = this.parseDeclaration(input);
		if (maybeDeclaration instanceof Some<JDeclaration>(var declaration)) return declaration.toCDeclaration().generate();

		return wrap(stripped);
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
				return new Some<String>(cAssignable.generate() + " = " + source.toAssignable().generate());
			}
		}

		return new None<String>();
	}

	private CAssignable transformAssignable(JAssignable assignable, JExpression source) {
		return switch (assignable) {
			case JDeclaration local -> {
				final var newType = this.resolveType(source, local.type);
				final var jDeclaration = local.withType(newType);
				this.environment = this.environment.define(jDeclaration);
				yield jDeclaration.toCAssignable();
			}

			case JExpression jExpression -> jExpression.toAssignable();
			case Placeholder placeholder -> placeholder.toCAssignable();
		};
	}

	private JType resolveType(JExpression source, JType type) {
		if (type.equals(JPrimitiveType.Var)) return this.resolveExpression(source);
		return type;
	}

	private JType resolveExpression(JExpression source) {
		return switch (source) {
			case Identifier(var value) -> {
				if (value.equals("this")) yield this.environment
						.resolveCurrent()
						.<JType>map(thisType -> thisType)
						.orElseGet(() -> new Placeholder("Not within a struct"));

				final var maybeFound = this.environment.resolveExpression(value).map(JDeclaration::type);
				if (maybeFound instanceof Some<JType>(var found)) yield found;
				yield new Placeholder("Undefined identifier: " + value);
			}

			case JMemberAccess access -> {
				final var instanceType = this.resolveExpression(access.instance);
				if (instanceType instanceof JRecursiveType recursiveType)
					if (recursiveType.internal instanceof Some<JType>(var internal) && internal instanceof JObjectType objectType)
						yield this.getJType(access, objectType, instanceType);

				if (instanceType instanceof JObjectType type) yield this.getJType(access, type, instanceType);

				yield new Placeholder(
						"Cannot access member '" + access.memberName + "' in '" + instanceType + "', not an object.");
			}

			case JExpressionWrapper jExpressionWrapper ->
					new Placeholder("Unwrapped expression: " + jExpressionWrapper.content);
			case JInvokable jInvokable -> {
				yield this.resolveCaller(jInvokable.caller);
			}
		};
	}

	private JType getJType(JMemberAccess access, JObjectType type, JType instanceType) {
		return type
				.resolve(access.memberName)
				.orElseGet(() -> new Placeholder("Member '" + access.memberName + "' not defined in '" + instanceType + "'"));
	}

	private JType resolveCaller(JCaller caller) {
		return switch (caller) {
			case JConstruction jConstruction -> jConstruction.jType;
			case JExpression jExpression -> {
				final var jType = this.resolveExpression(jExpression);
				if (jType instanceof JFunctionalType functionalType) yield functionalType.returnType;
				yield new Placeholder("Not a functional type: " + jType);
			}
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
		return this.parseCExpression(input).map(CExpression::generate).orElseGet(() -> wrap(input));
	}

	private Option<CExpression> parseCExpression(String input) {
		return this.parseExpression(input).map(JExpression::toExpression);
	}

	private Option<JExpression> parseExpression(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("switch ")) return new Some<String>("_switch").map(JExpressionWrapper::new);

		final var i2 = stripped.lastIndexOf("::");
		if (i2 >= 0) {
			final var substring = stripped.substring(0, i2);
			final var name = stripped.substring(i2 + 2).strip();
			if (this.isIdentifier(name)) {
				final var compiled = this.compileExpressionOrPlaceholder(substring);
				final var functionalInterfaceName = "F?";
				return new Some<String>(
						functionalInterfaceName + " { alloc(" + compiled + "), " + functionalInterfaceName + "Table { " + name +
						" }}").map(JExpressionWrapper::new);
			}
		}

		if (stripped.startsWith("'") && stripped.endsWith("'"))
			return new Some<String>(stripped).map(JExpressionWrapper::new);

		final var maybeLambda = this.compileLambda(stripped);
		if (maybeLambda instanceof Some<String>) return maybeLambda.map(JExpressionWrapper::new);

		final var i3 = stripped.indexOf("instanceof");
		if (i3 >= 0) {
			final var substring = stripped.substring(0, i3);
			final var substring1 = stripped.substring(i3 + "instanceof".length()).strip();
			final var maybeInstance = this.parseCExpression(substring).map(CExpression::generate);
			if (maybeInstance instanceof Some<String>(var instance)) {
				final var i4 = substring1.indexOf("<");
				final String substring2;
				if (i4 >= 0) substring2 = substring1.substring(0, i4);
				else substring2 = substring1;

				return new Some<String>(instance + ".variant = ?." + substring2 + "Variant").map(JExpressionWrapper::new);
			}
		}

		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var instanceString = stripped.substring(0, i);
			final var memberName = stripped.substring(i + 1).strip();
			if (this.isIdentifier(memberName)) {
				final var maybeInstance = this.parseExpression(instanceString);
				if (maybeInstance instanceof Some(var value))
					return new Some<JExpression>(new JMemberAccess(value, memberName));
			}
		}

		final var maybeInvokable = this.parseInvokable(stripped);
		if (maybeInvokable instanceof Some<JExpression>) return maybeInvokable;

		final var maybeOperator = this
				.compileOperator(stripped, "==")
				.or(() -> this.compileOperator(stripped, "!="))
				.or(() -> this.compileOperator(stripped, "<"))
				.or(() -> this.compileOperator(stripped, "+"))
				.or(() -> this.compileOperator(stripped, "-"))
				.or(() -> this.compileOperator(stripped, "&&"))
				.or(() -> this.compileOperator(stripped, "||"))
				.or(() -> this.compileOperator(stripped, ">="));

		if (maybeOperator instanceof Some<String>) return maybeOperator.map(JExpressionWrapper::new);
		if (this.isIdentifier(stripped)) return new Some<JExpression>(new Identifier(stripped));

		if (stripped.startsWith("!")) {
			final var substring = stripped.substring(1);
			final var maybeInstance = this.parseCExpression(substring).map(CExpression::generate);
			if (maybeInstance instanceof Some<String>(var instance))
				return new Some<String>("!" + instance).map(JExpressionWrapper::new);
		}

		if (this.isNumber(stripped)) return new Some<String>(stripped).map(JExpressionWrapper::new);

		if (stripped.startsWith("\"") && stripped.endsWith("\""))
			return new Some<String>(stripped).map(JExpressionWrapper::new);

		return new None<JExpression>();
	}

	private Option<String> compileLambda(String input) {
		final var index = input.indexOf("->");
		if (index < 0) return new None<String>();

		final var beforeContent = input.substring(0, index).strip();
		final var maybeWithBraces = input.substring(index + 2).strip();

		List<String> params;
		if (this.isIdentifier(beforeContent)) params = Lists.of(beforeContent);
		else if (beforeContent.startsWith("(") && beforeContent.endsWith(")")) {
			final var substring = beforeContent.substring(1, beforeContent.length() - 1);
			params = this.divide(substring, new ValueFolder()).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
		} else return new None<String>();

		if (maybeWithBraces.startsWith("{") && maybeWithBraces.endsWith("}")) {
			final var content = maybeWithBraces.substring(1, maybeWithBraces.length() - 1);
			final var compiled = this.compileMethodsSegments(content, 1);

			final var generatedName = this.generateName();

			var paramList = params.iter().map(param -> "auto " + param).toList().addFirst("void* _ref");

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

	private String generateName() {
		final var generatedName = "lambda" + this.counter;
		this.counter++;
		return generatedName;
	}

	private Option<String> compileOperator(String input, String operator) {
		if (input.length() < 3) return new None<String>();
		if (!input.contains(operator)) return new None<String>();

		var i1 = -1;
		var depth = 0;
		var i = 0;
		while (i < input.length() - 1) {
			final var c = input.charAt(i);
			if (c == operator.charAt(0)) if (depth == 0) {
				i1 = i;
				break;
			}

			if (c == '(') depth++;
			if (c == ')') depth--;
			i++;
		}

		if (i1 >= 0) {
			final var leftString = input.substring(0, i1);
			final var right = input.substring(i1 + operator.length());
			if (this.parseCExpression(leftString).map(CExpression::generate) instanceof Some<String>(var leftCompiled))
				if (this.parseCExpression(right).map(CExpression::generate) instanceof Some<String>(var rightCompiled))
					return new Some<String>(leftCompiled + " " + operator + " " + rightCompiled);
		}

		return new None<String>();
	}

	private Option<JExpression> parseInvokable(String stripped) {
		if (!stripped.endsWith(")")) return new None<JExpression>();

		final var stripped1 = stripped;
		final var length = stripped1.length();
		final var withoutEnd = stripped1.substring(0, length - 1);

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
		if (input.startsWith("-")) return this.allDigits(input.substring(1));
		return this.allDigits(input);
	}

	private boolean allDigits(String input) {
		return IntStream.range(0, input.length()).mapToObj(input::charAt).allMatch(Character::isDigit);
	}

	private Option<JCaller> parseCaller(String input) {
		final var stripped = input.strip();
		final var maybeExpression = this.parseExpression(stripped);
		if (maybeExpression instanceof Some<JExpression>(var expression)) return new Some<JCaller>(expression);

		if (stripped.startsWith("new ")) {
			final var type = stripped.substring("new ".length());
			final var jType = this.parseType(type);
			return new Some<JCaller>(new JConstruction(jType));
		}

		return new None<JCaller>();
	}

	private Option<JDeclaration> parseDeclaration(String input) {
		final var stripped = input.strip();
		final var nameSeparator = stripped.lastIndexOf(" ");
		if (nameSeparator >= 0) {
			final var beforeName = stripped.substring(0, nameSeparator).strip();
			final var name = stripped.substring(nameSeparator + 1).strip();

			final var typeSeparator = this.findTypeSeparator(beforeName);

			if (!this.isIdentifier(name)) return new None<JDeclaration>();

			if (typeSeparator < 0) {
				final var type = this.parseType(beforeName);
				return new Some<JDeclaration>(new JDeclaration(type, name));
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

			if (this.isIdentifier(name)) {
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
			case "Integer" -> {
				return JPrimitiveType.Int;
			}
			case "void" -> {
				return JPrimitiveType.Void;
			}
			case "String" -> {
				return StringType;
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

		if (this.isIdentifier(stripped)) return new Identifier(stripped);
		return new Placeholder(stripped);
	}
}
