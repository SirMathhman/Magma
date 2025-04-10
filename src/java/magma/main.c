struct Result {
	<R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
	<R> Result<struct T, R> mapErr(Function<X, R> mapper);
	<R> Result<R, struct X> flatMapValue(Function<T, Result<R, X>> mapper);
	<R> Result<R, struct X> mapValue(Function<T, R> mapper);
	Option<T> findValue();
};
struct Option {
	<R> Option<R> map(Function<T, R> mapper);
	int isPresent();
	T orElse(T other);
	int isEmpty();
	void ifPresent(Consumer<T> consumer);
	Option<T> or(Supplier<Option<T>> other);
	<R> Option<R> flatMap(Function<T, Option<R>> mapper);
	Tuple<int, T> toTuple(T other);
	T orElseGet(Supplier<T> supplier);
	<R> R match(Function<T, R> whenPresent, Supplier<R> whenEmpty);
};
struct Error {
	String display();
};
struct IOError {
};
struct List_ {
	List_<T> add(T element);
	List_<T> addAll(List_<T> others);
	Iterator<T> iter();
	int isEmpty();
	int size();
	List_<T> slice(int startInclusive, int endExclusive);
	Option<Tuple<T, List_<T>>> popFirst();
	Option<T> peekFirst();
	T get(int index);
};
struct Path_ {
	Path_ resolveSibling(String sibling);
	List_<String> asList();
};
struct Iterator {
	<R> R foldWithInitial(R initial, BiFunction<R, T, R> folder);
	void forEach(Consumer<T> consumer);
	<R> Iterator<R> map(Function<T, R> mapper);
	Iterator<T> filter(Predicate<T> predicate);
	Option<T> next();
	Iterator<T> concat(Iterator<T> other);
	<C> C collect(Collector<T, C> collector);
	int allMatch(Predicate<T> predicate);
	<R> Option<R> foldWithMapper(Function<T, R> mapper, BiFunction<R, T, R> folder);
	<R, X> Result<struct R, struct X> foldToResult(R initial, BiFunction<R, T, Result<R, X>> mapper);
};
struct Collector {
	C createInitial();
	C fold(C current, T element);
};
struct Head {
	Option<T> next();
};
struct Divider {
	State fold(State state, char c);
};
struct Rule {
};
struct ApplicationError {
};
struct HeadedIterator {
};
struct None {
};
struct EmptyHead {
};
struct SingleHead {
	T value;
};
struct Some {
};
struct Err {
};
struct Ok {
};
struct State {
	List_<char> queue;
	List_<String> segments;
	StringBuilder buffer;
	int depth;
};
struct Tuple {
};
struct Iterators {
};
struct RangeHead {
	int length;
};
struct ListCollector {
};
struct Joiner {
};
struct DelimitedDivider {
};
struct CompileError {
	String display();
};
struct Main {
	<T, X> {
        <R> struct R match(Function<T, R> whenOk, Function<X, R> whenErr);
	<T> {
        <R> Option<struct R> map(Function<T, R> mapper);
	<T> {
        <R> struct R foldWithInitial(R initial, BiFunction<R, T, R> folder);
	record ApplicationError(Error error);
	record Joiner(String delimiter);
	record DecoratedDivider(Divider divider);
	record DelimitedDivider(char delimiter);
	record CompileError(String message, String context, List_<CompileError> errors);
};
int retrieved = false;
int counter = 0;
String display() {
	return this.error.display();
}
auto __lambda0__(auto next) {
	return folder.apply(finalCurrent, next);
}
<R> R foldWithInitial(R initial, BiFunction<R, T, R> folder) {
	R current = initial;
	while (true) {
		R finalCurrent = current;
		Option<R> option = this.head.next().map(__lambda0__);
		if (option.isPresent()) {
			current = option.orElse(finalCurrent);
		}
		else {
			return current;
		}
	}
}
void forEach(Consumer<T> consumer) {
	while (true) {
		Option<T> next = this.head.next();
		if (next.isEmpty()) {
			break;
		}
		next.ifPresent(consumer);
	}
}
auto __lambda1__ {
	return this.head.next().map(mapper);
}
<R> Iterator<R> map(Function<T, R> mapper) {
	return HeadedIterator<>(__lambda1__);
}
auto __lambda2__(auto value) {
	return HeadedIterator<>(predicate.test(value)
                    ? new SingleHead<>(value)
                    : new EmptyHead<T>());
}
Iterator<T> filter(Predicate<T> predicate) {
	return this.flatMap(__lambda2__);
}
Option<T> next() {
	return this.head.next();
}
auto __lambda3__ {
	return other.next()
}
auto __lambda4__ {
	return this.head.next().or(__lambda3__);
}
Iterator<T> concat(Iterator<T> other) {
	return HeadedIterator<>(__lambda4__);
}
auto __lambda5__ {
	return collector.fold()
}
<C> C collect(Collector<T, C> collector) {
	return this.foldWithInitial(collector.createInitial(), __lambda5__);
}
auto __lambda6__(auto aBoolean, t) {
	return aBoolean && predicate.test(t);
}
int allMatch(Predicate<T> predicate) {
	return this.foldWithInitial(true, __lambda6__);
}
auto __lambda7__(auto next) {
	return this.foldWithInitial(next, folder);
}
<R> Option<R> foldWithMapper(Function<T, R> mapper, BiFunction<R, T, R> folder) {
	return this.head.next().map(mapper).map(__lambda7__);
}
auto __lambda8__(auto current) {
	return mapper.apply(current, t);
}
auto __lambda9__(auto result, t) {
	return result.flatMapValue(__lambda8__);
}
<R, X> Result<struct R, struct X> foldToResult(R initial, BiFunction<R, T, Result<R, X>> mapper) {
	return this.<Result<R, X>>foldWithInitial(Ok<>(initial), __lambda9__);
}
auto __lambda10__ {
	return Iterator.concat()
}
<R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper) {
	return this.map(mapper).foldWithInitial(Iterators.empty(), __lambda10__);
}
<R> Option<R> map(Function<T, R> mapper) {
	return None<>();
}
int isPresent() {
	return false;
}
T orElse(T other) {
	return other;
}
int isEmpty() {
	return true;
}
void ifPresent(Consumer<T> consumer) {
}
Option<T> or(Supplier<Option<T>> other) {
	return other.get();
}
<R> Option<R> flatMap(Function<T, Option<R>> mapper) {
	return None<>();
}
Tuple<int, T> toTuple(T other) {
	return Tuple<>(false, other);
}
T orElseGet(Supplier<T> supplier) {
	return supplier.get();
}
<R> R match(Function<T, R> whenPresent, Supplier<R> whenEmpty) {
	return whenEmpty.get();
}
Option<T> next() {
	return None<>();
}
private SingleHead(T value) {
	this.value = value;
}
Option<T> next() {
	if (this.retrieved) {
		return None<>();
	}
	this.retrieved = true;
	return Some<>(this.value);
}
<R> Option<R> map(Function<T, R> mapper) {
	return Some<>(mapper.apply(this.value));
}
int isPresent() {
	return true;
}
T orElse(T other) {
	return this.value;
}
int isEmpty() {
	return false;
}
void ifPresent(Consumer<T> consumer) {
	consumer.accept(this.value);
}
Option<T> or(Supplier<Option<T>> other) {
	return this;
}
<R> Option<R> flatMap(Function<T, Option<R>> mapper) {
	return mapper.apply(this.value);
}
Tuple<int, T> toTuple(T other) {
	return Tuple<>(true, this.value);
}
T orElseGet(Supplier<T> supplier) {
	return this.value;
}
<R> R match(Function<T, R> whenPresent, Supplier<R> whenEmpty) {
	return whenPresent.apply(this.value);
}
<R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
	return whenErr.apply(this.error);
}
<R> Result<struct T, R> mapErr(Function<X, R> mapper) {
	return Err<>(mapper.apply(this.error));
}
<R> Result<R, struct X> flatMapValue(Function<T, Result<R, X>> mapper) {
	return Err<>(this.error);
}
<R> Result<R, struct X> mapValue(Function<T, R> mapper) {
	return Err<>(this.error);
}
Option<T> findValue() {
	return None<>();
}
<R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
	return whenOk.apply(this.value);
}
<R> Result<struct T, R> mapErr(Function<X, R> mapper) {
	return Ok<>(this.value);
}
<R> Result<R, struct X> flatMapValue(Function<T, Result<R, X>> mapper) {
	return mapper.apply(this.value);
}
<R> Result<R, struct X> mapValue(Function<T, R> mapper) {
	return Ok<>(mapper.apply(this.value));
}
Option<T> findValue() {
	return Some<>(this.value);
}
private State(List_<char> queue, List_<String> segments, StringBuilder buffer, int depth) {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
public State(List_<char> queue) {
	this(queue, Lists.empty(), StringBuilder(), 0);
}
auto __lambda11__(auto tuple) {
	return tuple.right.append(tuple.left);
}
Option<State> popAndAppend() {
	return this.pop().map(__lambda11__);
}
State advance() {
	return State(this.queue, this.segments.add(this.buffer.toString()), StringBuilder(), this.depth);
}
State append(char c) {
	return State(this.queue, this.segments, this.buffer.append(c), this.depth);
}
int isLevel() {
	return this.depth == 0;
}
auto __lambda12__(auto tuple) {
	return Tuple<>(tuple.left, State(tuple.right, this.segments, this.buffer, this.depth));
}
Option<Tuple<char, State>> pop() {
	return this.queue.popFirst().map(__lambda12__);
}
int hasElements() {
	return !this.queue.isEmpty();
}
State exit() {
	return State(this.queue, this.segments, this.buffer, this.depth - 1);
}
State enter() {
	return State(this.queue, this.segments, this.buffer, this.depth + 1);
}
List_<String> segments() {
	return this.segments;
}
Option<char> peek() {
	return this.queue.peekFirst();
}
<T> Iterator<T> empty() {
	return HeadedIterator<>(EmptyHead<>());
}
auto __lambda13__(auto tuple) {
	return tuple.right;
}
Iterator<char> fromString(String input) {
	return fromStringWithIndices(input).map(__lambda13__);
}
Iterator<Tuple<int, Character>> fromStringWithIndices(String input) {
	return HeadedIterator<>(RangeHead(input.length())).map(index -> new Tuple<>(index, input.charAt(index)));
}
public RangeHead(int length) {
	this.length = length;
}
Option<int> next() {
	if (this.counter < this.length) {
		int next = this.counter;this.counter++;
		return Some<>(next);
	}
	return None<>();
}
List_<T> createInitial() {
	return Lists.empty();
}
List_<T> fold(List_<T> current, T element) {
	return current.add(element);
}
Option<String> createInitial() {
	return None<>();
}
auto __lambda14__(auto inner) {
	return inner + this.delimiter + element;
}
Option<String> fold(Option<String> current, String element) {
	return Some<>(current.map(__lambda14__).orElse(element));
}
auto __lambda15__ {
	return State.popAndAppend()
}
Option<State> divideSingleQuotes(State state, char c) {
	if (c != '\'') {
		return None<>();
	}
	State appended = state.append(c);
	Option<Tuple<char, State>> maybeSlashTuple = appended.pop();
	if (maybeSlashTuple.isEmpty()) {
		return None<>();
	}
	Tuple<char, State> slashTuple = maybeSlashTuple.orElse(Tuple<>('\0', appended));
	var withMaybeSlash = slashTuple.right.append(slashTuple.left);
	Option<State> withSlash = slashTuple.left == '\\' ? withMaybeSlash.popAndAppend() : new Some<>(withMaybeSlash);
	return withSlash.flatMap(__lambda15__);
}
State fold(State state, char c) {
	if (c == this.delimiter) {
		return state.advance();
	}
	return state.append(c);
}
public CompileError(String message, String context) {
	this(message, context, Lists.empty());
}
