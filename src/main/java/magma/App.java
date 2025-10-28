package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

		@Override
		public CType replaceIdentifiersWithMapping(HashMap<String, CType> mapping) {
			return this;
		}
	}

	private interface Function<T, R> {
		R apply(T arg);
	}

	private interface BiFunction<A, B, R> {
		R apply(A left, B right);
	}

	private interface Consumer<T> {
		void accept(T value);
	}

	private interface Head<T> {
		Option<T> next();
	}

	private interface Collector<T, C> {
		C createInitial();

		C fold(C current, T element);
	}

	private interface Supplier<T> {
		T get();
	}

	private sealed interface Result<T, X> permits Err, Ok {}

	private sealed interface CType
			permits CIdentifier, CPointerType, CPrimitiveType, CStructureType, CTemplateType, CFunctionType, Placeholder {
		String generate();

		String getSimpleName();

		CType replaceIdentifiersWithMapping(HashMap<String, CType> mapping);
	}

	private sealed interface CFunctionHeader permits CDefinition, Placeholder {
		String generate();
	}

	private sealed interface Option<T> permits None, Some {
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

		<R> Option<Tuple<T, R>> and(Supplier<Option<R>> other);
	}

	private interface Predicate<T> {
		boolean test(T element);
	}

	private sealed interface CStructureSegment extends CStructureMember
			permits CStatement, EmptyCStructureSegment, Placeholder {
		String generate();
	}

	private sealed interface CStructureMember permits CMethodMember, CStructureSegment {
		Option<CDefinition> toDefinition();
	}

	private interface CNode {
		String generate();
	}

	private sealed interface CExpression extends CCaller
			permits CContent, CFieldAccess, CIdentifier, CInvocation, CReference, Placeholder {
		String generate();
	}

	private sealed interface CCaller permits CConstruction, CExpression {
		String generate();
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

	private record ZipHead<T, R>(Head<T> head, Head<R> otherHead) implements Head<Tuple<T, R>> {
		@Override
		public Option<Tuple<T, R>> next() {
			return this.head.next().and(this.otherHead::next);
		}
	}

	private record Stream<T>(Head<T> head) {
		public <R> Stream<R> map(Function<T, R> mapper) {
			return new Stream<R>(new MapHead<T, R>(this.head, mapper));
		}

		public <R> R fold(R initial, BiFunction<R, T, R> folder) {
			var current = initial;
			while (true) {
				final var head = this.head;
				final var maybeNext = head.next();
				if (maybeNext instanceof Some<T>(var next)) {
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
			return this.flatMap(element -> this.applyFilter(predicate, element));
		}

		private Stream<T> applyFilter(Predicate<T> predicate, T element) {
			if (predicate.test(element)) {
				return new Stream<T>(new SingleHead<T>(element));
			}
			return new Stream<T>(new EmptyHead<T>());
		}

		public <R> Stream<R> flatMap(Function<T, Stream<R>> mapper) {
			return new Stream<R>(new FlatMapHead<T, R>(this.head, mapper));
		}

		public <R> Stream<Tuple<T, R>> zip(Stream<R> other) {
			return new Stream<Tuple<T, R>>(new ZipHead<T, R>(this.head, other.head));
		}
	}

	private static final class ArrayList<T> {
		private final List<T> inner;

		private ArrayList() {
			this.inner = new java.util.ArrayList<T>();
		}

		@SafeVarargs
		public static <T> ArrayList<T> of(T... elements) {
			var current = new ArrayList<T>();
			for (var i = 0; i < elements.length; i++) {
				current = current.addLast(elements[i]);
			}
			return current;
		}

		public static <T> ArrayList<T> empty() {
			return new ArrayList<T>();
		}

		@Override
		public String toString() {
			return this.inner.stream().map(Objects::toString).collect(Collectors.joining(", ", "[", "]"));
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
			return this.stream().collect(new ListCollector<T>());
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

		private ArrayList<T> setLast(T element) {
			this.inner.set(this.inner.size() - 1, element);
			return this;
		}

		public ArrayList<T> addAllLast(ArrayList<T> others) {
			return others.stream().fold(this, ArrayList::addLast);
		}

		public ArrayList<T> mapLast(Function<T, T> mapper) {
			if (this.isEmpty()) {
				return this;
			}
			return this.setLast(mapper.apply(this.getLast()));
		}

		public ArrayList<T> reverse() {
			var current = new ArrayList<T>();
			for (var i = 0; i < this.inner.size(); i++) {
				current = current.addLast(this.inner.get(this.inner.size() - i - 1));
			}
			return current;
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
		public String toString() {
			return this.value.toString();
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
			return new Stream<T>(new SingleHead<T>(this.value));
		}

		@Override
		public <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
			return other.get().map(otherValue -> new Tuple<T, R>(this.value, otherValue));
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
			return new Stream<T>(new EmptyHead<T>());
		}

		@Override
		public <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
			return new None<Tuple<T, R>>();
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

		@Override
		public CType replaceIdentifiersWithMapping(HashMap<String, CType> mapping) {
			return new CPointerType(this.type.replaceIdentifiersWithMapping(mapping));
		}
	}

	private record CTemplateType(String base, ArrayList<CType> typeArguments) implements CType {
		@Override
		public String generate() {
			final var stream = this.typeArguments.stream();
			final var map = stream.map(CType::generate);
			final var collector = new Joiner(", ");
			final var joined = map.collect(collector);
			final String s;
			if (this.typeArguments.isEmpty()) {
				s = "";
			} else {
				s = "<" + joined + ">";
			}
			return this.base + s;
		}

		@Override
		public String getSimpleName() {
			return this.base;
		}

		@Override
		public CType replaceIdentifiersWithMapping(HashMap<String, CType> mapping) {
			final var collect = this.typeArguments
					.stream()
					.map(arg -> arg.replaceIdentifiersWithMapping(mapping))
					.collect(new ListCollector<CType>());

			return new CTemplateType(this.base, collect);
		}
	}

	private record CIdentifier(String value) implements CType, CExpression {
		@Override
		public String generate() {
			return this.value;
		}

		@Override
		public String getSimpleName() {
			return this.value;
		}

		@Override
		public CType replaceIdentifiersWithMapping(HashMap<String, CType> mapping) {
			return mapping.get(this.value).orElse(this);
		}
	}

	private record Placeholder(String input) implements CType, CFunctionHeader, CExpression, CStructureSegment {
		private static String wrap(String input) {
			final var withoutStart = input.replace("/*", "start");
			final var withoutEnd = withoutStart.replace("*/", "end");
			return "/*" + withoutEnd + "*/";
		}

		@Override
		public String generate() {
			return wrap(this.input);
		}

		@Override
		public String getSimpleName() {
			return this.generate();
		}

		@Override
		public CType replaceIdentifiersWithMapping(HashMap<String, CType> mapping) {
			return this;
		}

		@Override
		public Option<CDefinition> toDefinition() {
			return new None<CDefinition>();
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
				var counter = this.index;
				this.index++;
				final var element = this.input.charAt(counter);
				return new Some<Character>(element);
			} else {
				return new None<Character>();
			}
		}

		public Stream<String> stream() {
			return this.segments.stream();
		}

		public Option<Tuple<Character, State>> popAndAppendToTuple() {
			return this.pop().map(next -> {
				final var appended = this.append(next);
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

	private record CDefinition(ArrayList<String> typeParameters, CType type, String name)
			implements CFunctionHeader, CNode {
		@Override
		public String generate() {
			return this.type().generate() + " " + this.name();
		}

		@Override
		public String toString() {
			return this.generate();
		}

		public CDefinition withType(CType type) {
			return new CDefinition(this.typeParameters, type, this.name);
		}

		public CDefinition mapType(Function<CType, CType> mapper) {
			return new CDefinition(this.typeParameters, mapper.apply(this.type), this.name);
		}
	}

	private record CStructureHeader(ArrayList<String> typeParameters, String name) {
		private CType toType() {
			if (this.typeParameters.isEmpty()) {
				return new CIdentifier(this.name);
			}

			final var list = this.typeParameters.stream().<CType>map(CIdentifier::new).toList();
			return new CTemplateType(this.name, list);
		}

		public String generate() {
			return App.createTemplateString(this.typeParameters()) + "struct " + this.name();
		}

		public CStructureType withFields(ArrayList<CDefinition> fields) {
			return new CStructureType(this.name, this.typeParameters, fields);
		}
	}

	private record CStructure(CStructureHeader CStructureHeader, String fields) {
		private String generate() {
			return this.CStructureHeader().generate() + " {" + this.fields() + System.lineSeparator() + "};";
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
				final var element = this.list.inner.get(this.counter);
				this.counter++;
				return new Some<T>(element);
			}

			return new None<T>();
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
				final var maybeNext = this.current.next();
				if (maybeNext.isPresent()) {
					return maybeNext;
				}

				final var maybeOuter = this.head.next();
				if (maybeOuter.isEmpty()) {
					return new None<R>();
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

	private static final class EmptyCStructureSegment implements CStructureSegment {
		@Override
		public String generate() {
			return "";
		}

		@Override
		public Option<CDefinition> toDefinition() {
			return new None<CDefinition>();
		}
	}

	private record CContent(String content) implements CNode, CExpression {
		@Override
		public String generate() {
			return this.content;
		}
	}

	private record CStatement(CNode content, int depth) implements CStructureSegment {
		@Override
		public String generate() {
			return App.generateWithIndent(this.content().generate(), this.depth()) + ";";
		}

		@Override
		public Option<CDefinition> toDefinition() {
			if (this.content instanceof CDefinition definition) {
				return new Some<CDefinition>(definition);
			} else {
				return new None<CDefinition>();
			}
		}
	}

	private record CFieldAccess(CExpression child, String name) implements CExpression {
		@Override
		public String generate() {
			return this.child.generate() + "." + this.name;
		}
	}

	private static class Frames {
		private ArrayList<Frame> frames;

		private Frames() {
			this.frames = ArrayList.empty();
		}

		private Frames defineAll(ArrayList<CDefinition> params) {
			this.frames = this.frames.mapLast(last -> last.defineAll(params));
			return this;
		}

		private <T> Tuple<T, Frames> within(Supplier<T> mapper) {
			this.frames = this.frames.addLast(new Frame());
			final var result = mapper.get();
			this.frames = this.frames.removeLast();

			return new Tuple<T, Frames>(result, this);
		}

		private Frames define(CDefinition definition) {
			this.frames = this.frames.mapLast(last -> last.define(definition));
			return this;
		}

		private Option<CDefinition> resolve(String name) {
			return this.frames.stream().map(frame -> frame.resolve(name)).flatMap(Option::stream).head.next();
		}

		private Frames withStructureHeader(CStructureHeader header) {
			this.frames = this.frames.mapLast(last -> last.withHeader(header));
			return this;
		}

		private ArrayList<String> collectTypeParameters() {
			return this
					.streamHeaders()
					.map(header -> header.typeParameters)
					.flatMap(ArrayList::stream)
					.collect(new ListCollector<String>());
		}

		public Option<CStructureHeader> findCurrentStructure() {
			return this.frames.copy().reverse().stream().map(frame -> frame.maybeHeader).flatMap(Option::stream).head.next();
		}

		private Stream<CStructureHeader> streamHeaders() {
			return this.frames.stream().map(frame -> frame.maybeHeader).flatMap(Option::stream);
		}

		public Option<Tuple<CStructureHeader, ArrayList<CDefinition>>> findCurrentScope() {
			return this.frames
					.copy()
					.reverse()
					.stream()
					.map(frame -> frame.maybeHeader.map(header -> new Tuple<CStructureHeader, ArrayList<CDefinition>>(header,
																																																						frame.definitions)))
					.flatMap(Option::stream).head.next();
		}

		public Option<CStructureType> findStructure(String structName) {
			return this.frames.stream().map(frame -> frame.findStructure(structName)).flatMap(Option::stream).head.next();
		}

		public Frames defineStructure(CStructureType type) {
			this.frames = this.frames.mapLast(last -> last.defineStructure(type));
			return this;
		}
	}

	private record HashMap<K, V>(java.util.HashMap<K, V> internal) {
		public HashMap() {
			this(new java.util.HashMap<K, V>());
		}

		public HashMap<K, V> with(K key, V value) {
			this.internal.put(key, value);
			return this;
		}

		public Option<V> get(K key) {
			if (this.internal.containsKey(key)) {
				return new Some<V>(this.internal.get(key));
			}
			return new None<V>();
		}
	}

	private static class MapCollector<K, V> implements Collector<Tuple<K, V>, HashMap<K, V>> {
		@Override
		public HashMap<K, V> createInitial() {
			return new HashMap<K, V>();
		}

		@Override
		public HashMap<K, V> fold(HashMap<K, V> current, Tuple<K, V> element) {
			return current.with(element.left, element.right);
		}
	}

	private record CStructureType(String name, ArrayList<String> typeParameters, ArrayList<CDefinition> fields)
			implements CType {

		@Override
		public String generate() {
			return this.name;
		}

		@Override
		public String getSimpleName() {
			return this.name;
		}

		@Override
		public CType replaceIdentifiersWithMapping(HashMap<String, CType> mapping) {
			final var list = this.fields
					.stream()
					.map(field -> field.mapType(type -> type.replaceIdentifiersWithMapping(mapping)))
					.toList();
			return new CStructureType(this.name, this.typeParameters, list);
		}

		public Option<CType> findField(String name) {
			return this.fields.stream().filter(field -> field.name.equals(name)).map(field -> field.type).head.next();
		}

		public CStructureType withTypeArguments(ArrayList<CType> typeArguments) {
			final var mapping =
					this.typeParameters.stream().zip(typeArguments.stream()).collect(new MapCollector<String, CType>());

			final var newFields = this.fields
					.stream()
					.map(field -> field.mapType(type -> type.replaceIdentifiersWithMapping(mapping)))
					.toList();

			return new CStructureType(this.name, ArrayList.empty(), newFields);
		}
	}

	private record CConstruction(CType type) implements CCaller {
		@Override
		public String generate() {
			return "new_" + this.type().generate();
		}
	}

	private record CInvocation(CCaller caller, ArrayList<CExpression> arguments) implements CExpression {
		@Override
		public String generate() {
			final var joinedArguments = this.arguments().stream().map(CExpression::generate).collect(new Joiner(", "));
			return this.caller().generate() + "(" + joinedArguments + ")";
		}
	}

	private record CMethodMember(CDefinition definition) implements CStructureMember {
		@Override
		public Option<CDefinition> toDefinition() {
			return new Some<CDefinition>(this.definition);
		}
	}

	private record CFunctionType(CType returnType, ArrayList<CType> paramTypes) implements CType {
		@Override
		public String generate() {
			return "???";
		}

		@Override
		public String getSimpleName() {
			return "???";
		}

		@Override
		public CType replaceIdentifiersWithMapping(HashMap<String, CType> mapping) {
			final var replacedParamTypes =
					this.paramTypes.stream().map(type -> type.replaceIdentifiersWithMapping(mapping)).toList();
			return new CFunctionType(this.returnType.replaceIdentifiersWithMapping(mapping), replacedParamTypes);
		}
	}

	private record Frame(Option<CStructureHeader> maybeHeader, ArrayList<CDefinition> definitions,
											 ArrayList<CStructureType> structures) {
		public Frame() {
			this(new None<CStructureHeader>(), ArrayList.empty(), ArrayList.empty());
		}

		public Frame defineAll(ArrayList<CDefinition> params) {
			return params.stream().fold(this, Frame::define);
		}

		public Frame define(CDefinition definition) {
			if (definition.type instanceof CIdentifier(var value) && value.equals("var")) {
				throw new RuntimeException();
			}

			return new Frame(this.maybeHeader, this.definitions.addLast(definition), this.structures);
		}

		public Frame withHeader(CStructureHeader header) {
			return new Frame(new Some<CStructureHeader>(header), this.definitions, this.structures);
		}

		public Option<CDefinition> resolve(String name) {
			return this.definitions.stream().filter(definition -> definition.name.equals(name)).head.next();
		}

		public Option<CStructureType> findStructure(String name) {
			return this.structures.stream().filter(type -> type.name.equals(name)).head.next();
		}

		public Frame defineStructure(CStructureType type) {
			return new Frame(this.maybeHeader, this.definitions, this.structures.addLast(type));
		}
	}

	private static class ArrayHead<T> implements Head<T> {
		private final T[] array;
		private int counter = 0;

		public ArrayHead(T[] array) {this.array = array;}

		@Override
		public Option<T> next() {
			if (this.counter >= this.array.length) {
				return new None<T>();
			}

			final var element = this.array[this.counter];
			this.counter++;
			return new Some<T>(element);
		}
	}

	private record CReference(CExpression child) implements CExpression {
		@Override
		public String generate() {
			return "&" + this.child.generate();
		}
	}

	private Frames frames;
	private ArrayList<String> globals;
	private ArrayList<String> forwardDeclarations;
	private ArrayList<String> structures;
	private ArrayList<String> sealedStructures;
	private ArrayList<String> functions;
	private int counter;
	private int depth;

	public App() {
		this.globals = ArrayList.empty();
		this.frames = new Frames();
		this.functions = ArrayList.empty();
		this.forwardDeclarations = ArrayList.empty();
		this.structures = ArrayList.empty();
		this.sealedStructures = ArrayList.empty();
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
			final var collect = typeParameters.stream().map(slice -> "typename " + slice).collect(new Joiner(", "));
			templateString = "template <" + collect + ">" + System.lineSeparator();
		}
		return templateString;
	}

	private static String generateWithIndent(String content, int depth) {
		return App.generateIndent(depth) + content;
	}

	private static String generateIndent(int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}

	private Option<IOException> run() {
		final var source = Paths.get(".", "src", "main", "java", "magma", "App.java");
		final var input = this.readString(source);
		return switch (input) {
			case Err<String, IOException> v -> new Some<IOException>(v.error);
			case Ok<String, IOException> v -> this.compilePath(source, v.value);
		};
	}

	private Option<IOException> compilePath(Path source, String input) {
		final var target = source.resolveSibling("App.cpp");
		final var output = this.compile(input);
		return this.writeString(target, output).or(() -> this.compileNative(target));
	}

	private Option<IOException> compileNative(Path target) {
		final var clang = this.startCommand(ArrayList.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));
		return switch (clang) {
			case Err<Process, IOException> v1 -> new Some<IOException>(v1.error);
			case Ok<Process, IOException> v1 -> this.waitForProcess(v1.value);
		};
	}

	private Option<IOException> waitForProcess(Process process) {
		return switch (this.waitFor(process)) {
			case Err<Integer, IOException> v2 -> new Some<IOException>(v2.error);
			case Ok<Integer, IOException> v2 -> {
				System.out.println("Compilation failed with exit code: " + v2.value);
				yield new None<IOException>();
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
			return new None<IOException>();
		} catch (IOException e) {
			return new Some<IOException>(e);
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
		final var compiled = this.compileStatements(input, this::compileRootSegment);

		final var joinedForwardDeclarations = String.join("", this.forwardDeclarations.inner);
		final var joinedFunctions = String.join("", this.functions.inner);

		final var joinedStructures = String.join("", this.structures.inner);
		final var joinedSealedStructures = String.join("", this.sealedStructures.inner);
		final var joinedGlobals = String.join("", this.globals.inner);

		return "#include \"Native.h\"" + System.lineSeparator() + joinedForwardDeclarations + compiled + joinedStructures +
					 joinedSealedStructures + joinedGlobals + joinedFunctions + "int main(){" + System.lineSeparator() +
					 "\treturn " + "0;" + System.lineSeparator() + "}";
	}

	private String compileStatements(String input, Function<String, String> mapper) {
		return this.divide(input, this::foldStatement).map(mapper).collect(new Joiner(""));
	}

	private Stream<String> divide(String input, BiFunction<State, Character, State> folder) {
		var current = new State(input);
		while (true) {
			final var maybeNext = current.pop();
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
			var current0 = current.append(next);
			while (true) {
				final var maybeTuple = current0.popAndAppendToTuple();
				if (maybeTuple.isEmpty()) {
					break;
				}

				final var tuple = maybeTuple.get();
				current0 = tuple.right;

				final var nextInQuotes = tuple.left;
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
		final var appended = state.append(c);
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
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return this.parseStructure("class", stripped).map(member -> {
			if (member instanceof CStructureSegment segment) {
				return segment.generate();
			} else {
				return "???";
			}
		}).orElseGet(() -> Placeholder.wrap(input));
	}

	private Option<CStructureMember> parseStructure(String type, String input) {
		final var classIndex = input.indexOf(type);
		if (classIndex >= 0) {
			final var afterKeyword = input.substring(classIndex + type.length());
			final var contentStart = afterKeyword.indexOf("{");
			if (contentStart >= 0) {
				var beforeContent = afterKeyword.substring(0, contentStart).strip();
				final var withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
				if (withEnd.endsWith("}")) {
					final var content = withEnd.substring(0, withEnd.length() - 1);

					final var permitsIndex = beforeContent.indexOf("permits");
					var variants = ArrayList.<String>empty();
					if (permitsIndex >= 0) {
						final var variantsArray =
								beforeContent.substring(permitsIndex + "permits".length()).split(Pattern.quote(","));
						beforeContent = beforeContent.substring(0, permitsIndex).strip();
						variants = new Stream<String>(new ArrayHead<String>(variantsArray))
								.map(String::strip)
								.filter(slice -> !slice.isEmpty())
								.toList();
					}

					final var implementsIndex = beforeContent.indexOf("implements");
					Option<CType> maybeInterfaceType = new None<CType>();
					if (implementsIndex >= 0) {
						final var slice = beforeContent.substring(implementsIndex + "implements".length()).strip();
						maybeInterfaceType = this.compileType(slice);
						beforeContent = beforeContent.substring(0, implementsIndex).strip();
					}

					final var i1 = beforeContent.indexOf("extends ");
					if (i1 >= 0) {
						beforeContent = beforeContent.substring(0, i1).strip();
					}

					var recordParameters = ArrayList.<CDefinition>empty();
					if (beforeContent.endsWith(")")) {
						final var slice = beforeContent.substring(0, beforeContent.length() - 1);
						final var i = slice.indexOf("(");
						if (i >= 0) {
							final var params = slice.substring(i + 1);
							beforeContent = slice.substring(0, i).strip();

							recordParameters = this.compileParametersToList(params);
						}
					}

					var typeParameters = ArrayList.<String>empty();
					if (beforeContent.endsWith(">")) {
						final var withoutEnd = beforeContent.substring(0, beforeContent.length() - 1);
						final var typeParamStart = withoutEnd.indexOf("<");
						if (typeParamStart >= 0) {
							beforeContent = withoutEnd.substring(0, typeParamStart);
							final var typeParamsArray = withoutEnd.substring(typeParamStart + 1).split(Pattern.quote(","));
							typeParameters = new Stream<String>(new ArrayHead<String>(typeParamsArray))
									.map(String::strip)
									.filter(slice -> !slice.isEmpty())
									.toList();
						}
					}

					if (!this.isIdentifier(beforeContent)) {
						return new None<CStructureMember>();
					}

					final var templateString = App.createTemplateString(typeParameters);

					String dependencies;
					if (!variants.isEmpty()) {
						final var enumFields = variants
								.stream()
								.map(slice -> slice + "Type")
								.map(content1 -> App.generateWithIndent(content1, 1))
								.collect(new Joiner(","));

						final var typeArguments = this.joinTypeArguments(typeParameters);
						final var unionFields = variants
								.stream()
								.map(slice -> System.lineSeparator() + "\t" + slice + typeArguments + " " + slice.toLowerCase() + ";")
								.collect(new Joiner(""));

						dependencies = "enum " + beforeContent + "Tag {" + enumFields + System.lineSeparator() + "};" +
													 System.lineSeparator() + templateString + "union " + beforeContent + "Data {" + unionFields +
													 System.lineSeparator() + "};" + System.lineSeparator();
					} else {
						dependencies = "";
					}

					final var types = typeParameters.stream().<CType>map(CIdentifier::new).toList();
					final var thisType = new CTemplateType(beforeContent, types);
					final var constructorHeader = new CDefinition(ArrayList.empty(), thisType, "new_" + beforeContent);

					final var assignments = recordParameters
							.stream()
							.map(parameter -> System.lineSeparator() + "\t_this." + parameter.name + " = " + parameter.name + ";")
							.collect(new Joiner(""));

					final var constructorContent1 =
							System.lineSeparator() + "\t" + thisType.generate() + " _this;" + assignments + System.lineSeparator() +
							"\treturn _this;" + System.lineSeparator();

					this.functions = this.functions.addLast(this.generateMethod(typeParameters,
																																			constructorHeader,
																																			constructorContent1,
																																			recordParameters));

					final String generatedFields;
					if (variants.isEmpty()) {
						generatedFields = recordParameters
								.stream()
								.map(CDefinition::generate)
								.map(slice -> new CStatement(new CContent(slice), 1).generate())
								.collect(new Joiner(""));
					} else {
						generatedFields = new CStatement(new CContent(beforeContent + "Tag tag"), 1).generate() +
															new CStatement(new CContent(
																	beforeContent + "Data" + this.joinTypeArguments(typeParameters) + " " + "data"),
																						 1).generate();
					}

					if (maybeInterfaceType.isPresent()) {
						final var interfaceType = maybeInterfaceType.get();
						final var joinedTypeArguments = this.joinTypeArguments(typeParameters);

						final var thisTypeString = beforeContent + joinedTypeArguments;
						this.functions = this.functions.addLast(
								templateString + interfaceType.generate() + " to" + interfaceType.getSimpleName() + "_" +
								beforeContent + "(void* _ref" + "){" +
								new CStatement(new CContent(thisTypeString + " _this = *((" + thisTypeString + "*) _ref)"),
															 1).generate() +
								new CStatement(new CContent(interfaceType.getSimpleName() + "Data" + joinedTypeArguments + " data"),
															 1).generate() +
								new CStatement(new CContent("data." + beforeContent.toLowerCase() + " = _this"), 1).generate() +
								new CStatement(new CContent(
										"return " + interfaceType.generate() + " { " + beforeContent + "Tag, " + "data }"), 1).generate() +
								System.lineSeparator() + "}" + System.lineSeparator());
					}

					this.forwardDeclarations = this.forwardDeclarations.addLast(
							templateString + "struct " + beforeContent + ";" + System.lineSeparator());

					final var header = new CStructureHeader(typeParameters, beforeContent);
					var finalRecordFields = recordParameters;
					var finalDependencies = dependencies;
					final var within1 = this.frames.within(() -> {
						this.frames = this.frames.withStructureHeader(header).defineAll(finalRecordFields);

						final var members = this
								.divide(content, this::foldStatement)
								.map(this::compileClassSegment)
								.collect(new ListCollector<CStructureMember>());

						final var joinedFields =
								members.stream().map(this::generateField).flatMap(Option::stream).collect(new Joiner(""));

						final var outputContent = generatedFields + joinedFields;

						final var generated =
								finalDependencies + new CStructure(header, outputContent).generate() + System.lineSeparator();
						return new Tuple<ArrayList<CStructureMember>, String>(members, generated);
					});

					final var result = within1.left;
					final var members = result.left;
					final var generated = result.right;

					final var memberDefinitions =
							members.stream().map(CStructureMember::toDefinition).flatMap(Option::stream).toList();

					final var allMembers = finalRecordFields.addAllLast(memberDefinitions);
					this.frames = within1.right.defineStructure(header.withFields(allMembers));

					if (variants.isEmpty()) {
						this.structures = this.structures.addLast(generated);
					} else {
						this.sealedStructures = this.sealedStructures.addLast(generated);
					}

					return new Some<CStructureMember>(new EmptyCStructureSegment());
				}
			}
		}

		return new None<CStructureMember>();
	}

	private Option<String> generateField(CStructureMember member) {
		if (member instanceof CStructureSegment segment) {
			return new Some<String>(segment.generate());
		} else {
			return new None<String>();
		}
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

	private boolean isIdentifier(String input) {
		for (var i = 0; i < input.length(); i++) {
			final var next = input.charAt(i);
			if (Character.isLetter(next) || (i != 0 && Character.isDigit(next))) {continue;}
			return false;
		}

		return true;
	}

	private CStructureMember compileClassSegment(String input) {
		if (input.isBlank()) {
			return new EmptyCStructureSegment();
		}

		final var maybeClass = this.parseStructure("class", input);
		if (maybeClass.isPresent()) {
			return maybeClass.get();
		}

		final var maybeInterface = this.parseStructure("interface", input);
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final var maybeRecord = this.parseStructure("record", input);
		if (maybeRecord.isPresent()) {
			return maybeRecord.get();
		}

		final var maybeEnum = this.parseStructure("enum", input);
		if (maybeEnum.isPresent()) {
			return maybeEnum.get();
		}

		if (input.endsWith(";")) {
			final var slice = input.substring(0, input.length() - 1);
			final var maybeClassStatement = this.compileEnumValues(slice).or(() -> this.compileDefinitionToField0(slice));
			if (maybeClassStatement.isPresent()) {
				return maybeClassStatement.get();
			}
		}

		return this.parseMethod(input).orElseGet(() -> new Placeholder(input));
	}

	private Option<CStructureMember> parseMethod(String input) {
		final var paramStart = input.indexOf("(");
		if (paramStart < 0) {
			return new None<CStructureMember>();
		}
		final var definition = input.substring(0, paramStart).strip();
		final var withParams = input.substring(paramStart + 1);

		final var paramEnd = withParams.indexOf(")");
		if (paramEnd < 0) {
			return new None<CStructureMember>();
		}
		final var inputParams = withParams.substring(0, paramEnd).strip();
		final var withBraces = withParams.substring(paramEnd + 1).strip();

		final var header = this.parseFunctionHeader(definition);
		final var params = this.compileParametersToList(inputParams);

		final ArrayList<String> typeParameters;
		if (header instanceof CDefinition definition1) {
			typeParameters = this.frames.collectTypeParameters().copy().addAllLast(definition1.typeParameters);
		} else {
			typeParameters = this.frames.collectTypeParameters().copy().addAllLast(ArrayList.empty());
		}

		final var paramsWithThis =
				params.copy().addFirst(new CDefinition(ArrayList.empty(), new CPointerType(CPrimitiveType.Void), "_ref"));

		var generated =
				createTemplateString(typeParameters) + this.generateHeaderWithParameters(header, paramsWithThis) + ";" +
				System.lineSeparator();

		if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
			final var content = withBraces.substring(1, withBraces.length() - 1);

			final var maybeCurrentStructure = this.frames.findCurrentStructure();
			if (maybeCurrentStructure instanceof Some<CStructureHeader>(var currentStructure)) {
				final var thisDefinition = new CStatement(new CContent(
						currentStructure.toType().generate() + " _this = *((" + currentStructure.name() + "*) _ref)"),
																									1).generate();

				final var framesWithParams = this.frames.within(() -> {
					this.frames = this.frames.defineAll(params);

					final var withBlock = this.frames.within(() -> {
						final var outputContent = thisDefinition + this.compileMethodSegments(content) + System.lineSeparator();
						return this.generateMethod(typeParameters, header, outputContent, paramsWithThis);
					});

					this.frames = withBlock.right;
					return withBlock.left;
				});

				generated = framesWithParams.left;
				this.frames = framesWithParams.right;
			}
		}

		this.functions = this.functions.addLast(generated);

		if (header instanceof CDefinition definition1) {
			final var paramTypes = params.stream().map(CDefinition::type).collect(new ListCollector<CType>());
			return new Some<CStructureMember>(new CMethodMember(definition1.mapType(type -> new CFunctionType(type,
																																																				paramTypes))));
		} else {
			return new Some<CStructureMember>(new EmptyCStructureSegment());
		}
	}

	private String generateMethod(ArrayList<String> typeParameters,
																CFunctionHeader header,
																String content,
																ArrayList<CDefinition> params) {
		return createTemplateString(typeParameters) + this.generateHeaderWithParameters(header, params) + " {" + content +
					 "}" + System.lineSeparator();
	}

	private String generateHeaderWithParameters(CFunctionHeader header, ArrayList<CDefinition> params) {
		final var outputParams = params.stream().map(CDefinition::generate).collect(new Joiner(", "));

		return header.generate() + "(" + outputParams + ")";
	}

	private CFunctionHeader parseFunctionHeader(String input) {
		return this.compileDefinition(input).<CFunctionHeader>map(item -> {
			final var currentStructureName = this.frames.findCurrentStructure().map(header -> header.name).orElse("???");
			return new CDefinition(item.typeParameters, item.type, item.name + "_" + currentStructureName);
		}).or(() -> this.compileConstructor(input)).orElseGet(() -> new Placeholder(input));
	}

	private Option<CStructureMember> compileDefinitionToField0(String slice) {
		final var maybeDefinition = this.compileDefinition(slice);
		if (!maybeDefinition.isPresent()) {
			return new None<CStructureMember>();
		}

		final var definition = maybeDefinition.get();
		this.frames = this.frames.define(definition);
		return new Some<CStructureMember>(new CStatement(definition, 1));
	}

	private String compileMethodSegments(String content) {
		return this.compileStatements(content, this::compileMethodSegmentOrPlaceholder);
	}

	private Option<CFunctionHeader> compileConstructor(String input) {
		final var i = input.lastIndexOf(" ");
		if (i >= 0) {
			final var name = input.substring(i + 1).strip();
			if (this.isIdentifier(name)) {
				final var peek0 = this.frames.findCurrentStructure();
				if (peek0 instanceof Some<CStructureHeader>(var peek)) {
					return new Some<CFunctionHeader>(new CDefinition(ArrayList.empty(), peek.toType(), "new_" + peek.name));
				}
			}
		} else {
			if (this.isIdentifier(input)) {
				final var structName = this.frames.findCurrentStructure().map(header -> header.name).orElse("???");
				return new Some<CFunctionHeader>(new CDefinition(ArrayList.empty(),
																												 new CIdentifier(structName),
																												 "new_" + structName));
			}
		}

		return new None<CFunctionHeader>();
	}

	private Option<CStructureMember> compileEnumValues(String input) {
		final var segments = new Stream<String>(new ArrayHead<String>(input.split(Pattern.quote(","))))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList();

		for (var segment : segments.inner) {
			final var stripped = segment.strip();
			final var maybeEnumValue = this.compileEnumValue(stripped);
			if (maybeEnumValue.isPresent()) {
				this.globals = this.globals.addLast(maybeEnumValue.get());
			} else {
				return new None<CStructureMember>();
			}
		}

		return new Some<CStructureMember>(new EmptyCStructureSegment());
	}

	private Option<String> compileEnumValue(String stripped) {
		if (stripped.endsWith(")")) {
			final var slice = stripped.substring(0, stripped.length() - 1);
			final var i = slice.indexOf("(");
			if (i >= 0) {
				final var name = slice.substring(0, i).strip();
				final var arguments = slice.substring(i + 1);
				if (this.isIdentifier(name)) {
					final var structureName = this.frames.findCurrentStructure().map(header -> header.name).orElse("???");
					return new Some<String>(structureName + " " + name + "Value = " + structureName + " { " + arguments + " };" +
																	System.lineSeparator());
				}
			}
		}

		return new None<String>();
	}

	private String compileMethodSegmentOrPlaceholder(String input) {
		return this.compileMethodSegment(input).orElseGet(() -> Placeholder.wrap(input));
	}

	private Option<String> compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty() || stripped.startsWith("try ") || stripped.startsWith("catch ")) {
			return new Some<String>("");
		}

		if (stripped.startsWith("{") && stripped.endsWith("}")) {
			final var content = stripped.substring(1, stripped.length() - 1);

			this.depth++;

			final var within = this.frames.within(() -> this.compileMethodSegments(content));

			final var compiled = within.left;
			this.frames = within.right;

			this.depth--;

			return new Some<String>("{" + compiled + App.generateIndent(this.depth) + "}");
		}

		final var maybeIf = this.compileConditional(stripped, "if");
		if (maybeIf.isPresent()) {
			return maybeIf;
		}

		final var maybeWhile = this.compileConditional(stripped, "while");
		if (maybeWhile.isPresent()) {
			return maybeWhile;
		}

		if (stripped.endsWith(";")) {
			final var slice = stripped.substring(0, stripped.length() - 1);
			return new Some<String>(new CStatement(new CContent(this.compileMethodStatement(slice)), this.depth).generate());
		}

		if (stripped.startsWith("else ")) {
			final var substring = stripped.substring(5);
			return new Some<String>(
					App.generateIndent(this.depth) + "else " + this.compileMethodSegmentOrPlaceholder(substring));
		}

		return new None<String>();
	}

	private Option<String> compileConditional(String input, String type) {
		if (input.startsWith(type)) {
			final var substring = input.substring(type.length()).strip();
			if (substring.startsWith("(")) {
				final var withCondition = substring.substring(1);
				final var conditionEnd = this.findConditionEnd(withCondition);

				if (conditionEnd >= 0) {
					final var condition = withCondition.substring(0, conditionEnd).strip();
					final var substring2 = withCondition.substring(conditionEnd + 1).strip();
					return new Some<String>(
							App.generateIndent(this.depth) + type + " (" + this.compileExpression(condition) + ") " +
							this.compileMethodSegmentOrPlaceholder(substring2));
				}
			}
		}

		return new None<String>();
	}

	private int findConditionEnd(String withCondition) {
		var conditionEnd = -1;
		var depth = 0;
		for (var i = 0; i < withCondition.length(); i++) {
			final var c = withCondition.charAt(i);
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
		final var stripped = input.strip();

		if (stripped.startsWith("return ")) {
			final var slice = stripped.substring("return ".length()).strip();
			return "return " + this.compileExpression(slice);
		}

		final var maybeAssignment = this.compileAssignment(stripped);
		if (maybeAssignment.isPresent()) {
			return maybeAssignment.get();
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
				.map(CExpression::generate)
				.or(() -> this.parseAndDefineDefinitionAsStatement(input))
				.orElseGet(() -> Placeholder.wrap(stripped));
	}

	private Option<String> compileAssignment(String stripped) {
		final var separator = stripped.indexOf('=');
		if (separator < 0) {return new None<String>();}

		final var destinationString = stripped.substring(0, separator).strip();
		final var sourceString = stripped.substring(separator + 1).strip();
		final var source = this.parseExpression(sourceString);

		final var generated = this.compileAssignmentContent(destinationString, source) + " = " + source.generate();
		return new Some<String>(generated);
	}

	private String compileAssignmentContent(String destinationString, CExpression source) {
		final var maybeDefinition = this.compileDefinition(destinationString);
		if (!maybeDefinition.isPresent()) {
			return this.compileExpression(destinationString);
		}

		final var definition = maybeDefinition.get();
		if (definition.type instanceof CIdentifier(var name) && name.equals("var")) {
			final var newType = this.resolveExpression(source);
			var withNewType = definition.withType(newType);
			this.frames = this.frames.define(withNewType);
			return withNewType.generate();
		}

		return definition.generate();

	}

	private CType resolveExpression(CExpression expression) {
		return switch (expression) {
			case CIdentifier identifier -> this.resolveIdentifier(identifier);
			case CContent cContent -> new Placeholder("typeof(" + cContent.content + ")");
			case CFieldAccess fieldAccess -> {
				final var childType = this.resolveExpression(fieldAccess.child);
				if (childType instanceof CStructureType structureType) {
					if (structureType.findField(fieldAccess.name) instanceof Some<CType>(var memberType)) {
						yield memberType;
					}

					if (structureType.findField(fieldAccess.name + "_" + structureType.name) instanceof Some<CType>(
							var memberType
					)) {
						yield memberType;
					}

					yield new Placeholder(
							"Undefined field '" + fieldAccess.name + "' in '" + structureType.name + "' of type '" + childType +
							"'");
				}

				yield new Placeholder("Does not have a type of structure: " + childType.generate());
			}
			case Placeholder placeholder -> placeholder;
			case CInvocation cInvocation -> {
				final var callerType = this.resolveCaller(cInvocation.caller);
				if (callerType instanceof CFunctionType functionType) {
					yield functionType.returnType;
				}

				yield new Placeholder("Not a function type: " + callerType.toString());
			}
			case CReference cReference -> new CPointerType(this.resolveExpression(expression));
		};
	}

	private CType resolveCaller(CCaller caller) {
		return switch (caller) {
			case CConstruction cConstruction -> new Placeholder(cConstruction.generate());
			case CExpression cExpression -> this.resolveExpression(cExpression);
		};
	}

	private CType resolveIdentifier(CIdentifier identifier) {
		if (identifier.value.equals("_this")) {
			final var maybeCurrentScope = this.frames.findCurrentScope();
			if (maybeCurrentScope instanceof Some(var currentScope)) {
				return new CStructureType(currentScope.left.name, currentScope.left.typeParameters, currentScope.right);
			}
		}

		final var maybeDefinition = this.frames.resolve(identifier.value);

		if (maybeDefinition instanceof Some<CDefinition>(var found)) {
			final var foundType = found.type;
			if (foundType instanceof CTemplateType(var base, var typeArguments)) {
				final var maybeStructureType = this.frames.findStructure(base);
				if (maybeStructureType instanceof Some<CStructureType>(var structureType)) {
					return structureType.withTypeArguments(typeArguments);
				}
			}

			return foundType;
		}

		return new Placeholder(identifier.value);
	}

	private Option<String> parseAndDefineDefinitionAsStatement(String input) {
		return this.parseAndDefineDefinition(input).map(CDefinition::generate);
	}

	private Option<CDefinition> parseAndDefineDefinition(String input) {
		final var maybeDefinition = this.compileDefinition(input);
		if (!maybeDefinition.isPresent()) {
			return new None<CDefinition>();
		}

		final var definition = maybeDefinition.get();
		this.frames = this.frames.define(definition);
		return new Some<CDefinition>(definition);
	}

	private String compileExpression(String input) {
		return this.parseExpression(input).generate();
	}

	private CExpression parseExpression(String input) {
		final var stripped = input.strip();
		if (stripped.equals("false")) {
			return new CContent("0");
		}

		if (stripped.equals("true")) {
			return new CContent("1");
		}

		if (stripped.startsWith("'") && stripped.endsWith("'")) {
			return new CContent(stripped);
		}

		if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
			return new CContent(stripped);
		}

		final var maybeLambda = this.compileLambda(stripped);
		if (maybeLambda.isPresent()) {
			return new CContent(maybeLambda.get());
		}

		final var maybeInvocation = this.compileInvocation(stripped);
		if (maybeInvocation.isPresent()) {
			return maybeInvocation.get();
		}

		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var child = stripped.substring(0, i).strip();
			final var name = stripped.substring(i + 1).strip();
			if (this.isIdentifier(name)) {
				final var newChild = this.parseExpression(child);
				return new CFieldAccess(newChild, name);
			}
		}

		if (this.isIdentifier(stripped)) {
			if (stripped.equals("this")) {
				return new CIdentifier("_this");
			}

			if (this.frames.resolve(stripped).isPresent()) {
				return new CIdentifier(stripped);
			}
		}

		if (stripped.startsWith("switch")) {
			return new CContent(this.createName("switch"));
		}

		final var maybeOperator = this
				.compileOperator(stripped, "+")
				.or(() -> this.compileOperator(stripped, "-"))
				.or(() -> this.compileOperator(stripped, "&&"))
				.or(() -> this.compileOperator(stripped, "||"))
				.or(() -> this.compileOperator(stripped, "=="))
				.or(() -> this.compileOperator(stripped, ">="))
				.or(() -> this.compileOperator(stripped, "<"));

		if (maybeOperator.isPresent()) {
			return new CContent(maybeOperator.get());
		}

		final var i2 = stripped.lastIndexOf("::");
		if (i2 >= 0) {
			final var substring = stripped.substring(0, i2);
			final var substring1 = stripped.substring(i2 + 2);
			final var maybeType = this.compileType(substring).map(CType::generate).orElse("?");
			return new CContent(substring1 + "_" + maybeType);
		}

		if (this.isNumber(stripped)) {
			return new CContent(stripped);
		}

		return new Placeholder(stripped);
	}

	private Option<String> compileLambda(String stripped) {
		final var arrowIndex = stripped.indexOf("->");
		if (arrowIndex >= 0) {
			final var names = stripped.substring(0, arrowIndex).strip();
			final var content = stripped.substring(arrowIndex + 2);

			final var functionName = this.createName("lambda");

			final ArrayList<String> parameters;
			if (this.isIdentifier(names)) {
				parameters = ArrayList.of("auto " + names);
			} else if (names.startsWith("(") && names.endsWith(")")) {
				final var slice = names.substring(1, names.length() - 1);
				parameters = this
						.divide(slice, this::foldValue)
						.map(String::strip)
						.filter(segment -> !segment.isEmpty())
						.map(segment -> "auto " + segment)
						.toList();
			} else {
				return new None<String>();
			}

			final var copy = parameters.copy().addFirst("auto _ref");
			this.functions = this.functions.addLast("auto " + functionName + "(" + String.join(", ", copy.inner) + ") " +
																							this.compileMethodSegment(content).orElseGet(() -> {
																								final var expression = this.compileExpression(content);
																								return "{" +
																											 new CStatement(new CContent("auto _this = _ref"), 1).generate() +
																											 new CStatement(new CContent("return " + expression),
																																			1).generate() + System.lineSeparator() + "}" +
																											 System.lineSeparator();
																							}));

			return new Some<String>(functionName);
		}

		return new None<String>();
	}

	private String createName(String type) {
		final var s = "_" + type + this.counter + "_";
		this.counter++;
		return s;
	}

	private Option<CExpression> compileInvocation(String stripped) {
		if (stripped.endsWith(")")) {
			final var slice = stripped.substring(0, stripped.length() - 1);
			var argStart = -1;
			var depth = 0;
			for (var i = 0; i < slice.length(); i++) {
				final var next = slice.charAt(i);
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
				final var callerString = slice.substring(0, argStart).strip();
				final var arguments = this
						.divide(slice.substring(argStart + 1), this::foldValue)
						.map(String::strip)
						.filter(segment -> !segment.isEmpty())
						.map(this::parseExpression)
						.toList();

				final var maybeCaller = this.parseCaller(callerString);
				if (maybeCaller.isPresent()) {
					final var caller = maybeCaller.get();
					if (caller instanceof CFieldAccess(var child, var name)) {
						final var childType = this.resolveExpression(child);
						final var newCallerAlias = name + "_" + childType.getSimpleName();
						return new Some<CExpression>(new CInvocation(new CIdentifier(newCallerAlias),
																												 arguments.addFirst(new CReference(child))));
					}

					return new Some<CExpression>(new CInvocation(caller, arguments));
				}
			}
		}

		return new None<CExpression>();
	}

	private Option<CCaller> parseCaller(String caller) {
		if (caller.startsWith("new ")) {
			final var substring = caller.substring("new ".length());
			final var maybeType = this.compileType(substring);
			if (maybeType.isPresent()) {
				final var type = maybeType.get();
				return new Some<CCaller>(new CConstruction(type));
			}
		}

		return new Some<CCaller>(this.parseExpression(caller));
	}

	private Option<String> compileOperator(String stripped, String separator) {
		final var i1 = stripped.indexOf(separator);
		if (i1 >= 0) {
			final var substring = stripped.substring(0, i1);
			final var substring1 = stripped.substring(i1 + separator.length());
			return new Some<String>(
					this.compileExpression(substring) + " " + separator + " " + this.compileExpression(substring1));
		}

		return new None<String>();
	}

	private boolean isNumber(String input) {
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (!Character.isDigit(c)) {
				return false;
			}
		}

		return true;
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
		final var nameSeparator = input.lastIndexOf(" ");
		if (nameSeparator < 0) {
			return new None<CDefinition>();
		}

		final var beforeName = input.substring(0, nameSeparator);
		final var name = input.substring(nameSeparator + 1).strip();
		if (!this.isIdentifier(name)) {
			return new None<CDefinition>();
		}

		var typeSeparator = -1;
		var depth = 0;
		for (var i = 0; i < beforeName.length(); i++) {
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
		}

		if (typeSeparator >= 0) {
			final var beforeType = beforeName.substring(0, typeSeparator).strip();
			var typeParameters = ArrayList.<String>empty();
			if (beforeType.endsWith(">")) {
				final var slice = beforeType.substring(0, beforeType.length() - 1);
				final var i = slice.indexOf("<");
				if (i >= 0) {
					final var typeParametersString = slice.substring(i + 1);
					typeParameters = this
							.divide(typeParametersString, this::foldValue)
							.map(String::strip)
							.filter(segment -> !segment.isEmpty())
							.collect(new ListCollector<String>());
				}
			}

			final var type = beforeName.substring(typeSeparator + 1).strip();
			var finalTypeParameters = typeParameters;
			return this.compileType(type).map(cType -> new CDefinition(finalTypeParameters, cType, name));
		}

		return this.compileType(beforeName).map(cType -> new CDefinition(ArrayList.empty(), cType, name));
	}

	private Option<CType> compileType(String input) {
		final var stripped = input.strip();

		switch (stripped) {
			case "Character" -> {
				return new Some<CType>(CPrimitiveType.Char);
			}
			case "boolean" -> {
				return new Some<CType>(CPrimitiveType.Int);
			}
			case "void" -> {
				return new Some<CType>(CPrimitiveType.Void);
			}
		}

		if (stripped.endsWith("[]")) {
			final var slice = stripped.substring(0, stripped.length() - 2);
			return this.compileType(slice).map(CPointerType::new);
		}

		if (stripped.equals("String")) {
			return new Some<CType>(new CPointerType(CPrimitiveType.Char));
		}

		if (stripped.endsWith(">")) {
			final var withoutEnd = stripped.substring(0, stripped.length() - 1);
			final var i = withoutEnd.indexOf("<");
			if (i >= 0) {
				final var base = withoutEnd.substring(0, i);
				final var typeArguments = withoutEnd.substring(i + 1);

				final var list = this
						.divide(typeArguments, this::foldValue)
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(this::compileType)
						.flatMap(Option::stream)
						.toList();

				return new Some<CType>(new CTemplateType(base, list));
			}
		}

		if (this.isIdentifier(stripped)) {
			if (stripped.equals("public") || stripped.equals("private")) {
				return new None<CType>();
			}

			return new Some<CType>(new CIdentifier(stripped));
		}

		return new None<CType>();
	}

	private State foldValue(State state, char next) {
		if (next == ',' && state.isLevel()) {
			return state.advance();
		}

		final var appended = state.append(next);
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
