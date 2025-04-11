/*struct Result<T, X> {
	<R> R match(R (*)(T) whenOk, R (*)(X) whenErr);
	<R> Result<T, R> mapErr(R (*)(X) mapper);
	<R> Result<R, X> flatMapValue(Result<R, X> (*)(T) mapper);
	<R> Result<R, X> mapValue(R (*)(T) mapper);
	Option<T> findValue();
};
*//*struct Option<T> {
	<R> Option<R> map(R (*)(T) mapper);
	int isPresent();
	T orElse(T other);
	int isEmpty();
	void ifPresent(Consumer<T> consumer);
	Option<T> or(Supplier<Option<T>> other);
	<R> Option<R> flatMap(Option<R> (*)(T) mapper);
	Tuple<int, T> toTuple(T other);
	T orElseGet(Supplier<T> supplier);
	<R> R match(R (*)(T) whenPresent, Supplier<R> whenEmpty);
	<R> Option<Tuple<T, R>> and(Supplier<Option<R>> other);
};
*/struct Error {
	struct String_ display();
};
struct IOError {
};
/*struct List_<T> {
	List_<T> add(T element);
	List_<T> addAll(List_<T> others);
	Iterator<T> iter();
	int isEmpty();
	int size();
	List_<T> slice(int startInclusive, int endExclusive);
	Option<Tuple<T, List_<T>>> popFirst();
	Option<T> peekFirst();
	T get(int index);
	List_<T> sort(BiFunction<T, T, int> comparator);
	T last();
	List_<T> set(int index, T element);
};
*/struct Path_ {
	struct Path_ resolveSibling(struct String sibling);
	List_<struct String> asList();
};
/*struct Iterator<T> {
	<R> R foldWithInitial(R initial, BiFunction<R, T, R> folder);
	void forEach(Consumer<T> consumer);
	<R> Iterator<R> map(R (*)(T) mapper);
	Iterator<T> filter(Predicate<T> predicate);
	Option<T> next();
	Iterator<T> concat(Iterator<T> other);
	<C> C collect(Collector<T, C> collector);
	int allMatch(Predicate<T> predicate);
	<R> Option<R> foldWithMapper(R (*)(T) mapper, BiFunction<R, T, R> folder);
	<R, X> Result<R, X> foldToResult(R initial, BiFunction<R, T, Result<R, X>> mapper);
	int anyMatch(Predicate<T> filter);
};
*//*struct Collector<T, C> {
	C createInitial();
	C fold(C current, T element);
};
*//*struct Head<T> {
	Option<T> next();
};
*/struct Divider {
	struct State fold(struct State state, char c);
};
struct Rule {
};
/*struct Map_<K, V> {
	Map_<K, V> with(K propertyKey, V propertyValue);
	Option<V> find(K propertyKey);
	Map_<K, V> withAll(Map_<K, V> other);
	Main.Iterator<Main.Tuple<K, V>> iter();
	Iterator<K> keys();
};
*/struct String_ {
};
struct ApplicationError {
};
/*struct HeadedIterator<T> {
};
*//*struct None<T> {
};
*//*struct EmptyHead<T> {
};
*//*struct SingleHead<T> {
	T value;
};
*//*struct Some<T> {
};
*//*struct Err<T, X> {
};
*//*struct Ok<T, X> {
};
*/struct State {
	List_<char> queue;
	List_<struct String> segments;
	struct StringBuilder buffer;
	int depth;
};
/*struct Tuple<A, B> {
};
*/struct Iterators {
};
struct RangeHead {
	int length;
};
/*struct ListCollector<T> {
};
*/struct Joiner {
};
struct DelimitedDivider {
};
struct Options {
};
struct CompileError {
	struct String format(int depth);
};
struct Max {
	Result<struct String, struct CompileError> apply(struct String input);
};
struct Main {
	struct record DecoratedDivider(struct Divider divider);
};
// Result<T, R>
// Result<R, X>
// Option<T>
// Option<R>
// Consumer<T>
// Supplier<Option<T>>
// Tuple<int, T>
// Supplier<T>
// Supplier<R>
// Tuple<T, R>
// Option<Tuple<T, R>>
// Supplier<Option<R>>
// List_<T>
// Iterator<T>
// Tuple<T, List_<T>>
// Option<Tuple<T, List_<T>>>
// BiFunction<T, T, int>
// List_<struct String>
// BiFunction<R, T, R>
// Iterator<R>
// Predicate<T>
// Collector<T, C>
// BiFunction<R, T, Result<R, X>>
// Map_<K, V>
// Option<V>
// Main.Tuple<K, V>
// Main.Iterator<Main.Tuple<K, V>>
// Iterator<K>
// HeadedIterator<>
// Ok<>
// None<>
// Tuple<>
// Some<>
// Err<>
// List_<char>
// Option<struct State>
// Tuple<char, struct State>
// Option<Tuple<char, struct State>>
// Option<char>
// EmptyHead<>
// Iterator<char>
// Tuple<int, char>
// Iterator<Tuple<int, char>>
// Option<int>
// Option<struct String>
// BiFunction<V, V, int>
// Result<struct String, struct CompileError>
// List_<struct CompileError>
int retrieved = 0;
int counter = 0;
struct record String_(char* array) {
}
struct String display0() {
	return Strings.unwrap(this.error.display());
}
struct String_ display() {
	return Strings.from(this.display0().toCharArray());
}
auto __lambda0__(auto next) {
	return folder.apply(finalCurrent, next);
}
auto __lambda1__(auto next) {
	return folder.apply;
}
auto __lambda2__(auto next) {
	return folder;
}
auto __lambda3__(auto next) {
	return folder;
}
<R> R foldWithInitial(R initial, BiFunction<R, T, R> folder) {
	R current = initial;
	while (1) {
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
	while (1) {
		Option<T> next = this.head.next();
		if (next.isEmpty()) {
			break;
		}
		next.ifPresent(consumer);
	}
}
auto __lambda4__ {
	return this.head.next().map(mapper);
}
auto __lambda5__ {
	return this.head.next().map;
}
auto __lambda6__ {
	return this.head.next();
}
auto __lambda7__ {
	return this.head.next;
}
auto __lambda8__ {
	return this.head;
}
auto __lambda9__ {
	return this;
}
auto __lambda10__ {
	return this.head;
}
auto __lambda11__ {
	return this;
}
auto __lambda12__ {
	return this.head.next();
}
auto __lambda13__ {
	return this.head.next;
}
auto __lambda14__ {
	return this.head;
}
auto __lambda15__ {
	return this;
}
auto __lambda16__ {
	return this.head;
}
auto __lambda17__ {
	return this;
}
auto __lambda18__ {
	return this.head.next(;
}
auto __lambda19__ {
	return this.head;
}
auto __lambda20__ {
	return this;
}
<R> Iterator<R> map(R (*)(T) mapper) {
	return HeadedIterator<>(__lambda4__);
}
auto __lambda21__(auto value) {
	return HeadedIterator<>(predicate.test(value)
                    ? new SingleHead<>(value)
                    : new EmptyHead<T>());
}
Iterator<T> filter(Predicate<T> predicate) {
	return this.flatMap(__lambda21__);
}
Option<T> next() {
	return this.head.next();
}
auto __lambda22__ {
	return struct other.next()
}
auto __lambda23__ {
	return this.head.next().or(__lambda22__);
}
auto __lambda24__ {
	return this.head.next().or;
}
auto __lambda25__ {
	return this.head.next();
}
auto __lambda26__ {
	return this.head.next;
}
auto __lambda27__ {
	return this.head;
}
auto __lambda28__ {
	return this;
}
auto __lambda29__ {
	return this.head;
}
auto __lambda30__ {
	return this;
}
auto __lambda31__ {
	return struct other.next()
}
auto __lambda32__ {
	return this.head.next();
}
auto __lambda33__ {
	return this.head.next;
}
auto __lambda34__ {
	return this.head;
}
auto __lambda35__ {
	return this;
}
auto __lambda36__ {
	return this.head;
}
auto __lambda37__ {
	return this;
}
auto __lambda38__ {
	return this.head.next(;
}
auto __lambda39__ {
	return this.head;
}
auto __lambda40__ {
	return this;
}
Iterator<T> concat(Iterator<T> other) {
	return HeadedIterator<>(__lambda23__);
}
auto __lambda41__ {
	return struct collector.fold()
}
<C> C collect(Collector<T, C> collector) {
	return this.foldWithInitial(collector.createInitial(), __lambda41__);
}
auto __lambda42__(auto aBoolean, t) {
	return aBoolean && predicate.test(t);
}
auto __lambda43__(auto aBoolean, t) {
	return aBoolean && predicate.test;
}
auto __lambda44__(auto aBoolean, t) {
	return aBoolean && predicate;
}
auto __lambda45__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda46__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda47__(auto aBoolean, t) {
	return aBoolean && predicate;
}
auto __lambda48__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda49__(auto aBoolean, t) {
	return aBoolean;
}
int allMatch(Predicate<T> predicate) {
	return this.foldWithInitial(1, __lambda42__);
}
auto __lambda50__(auto next) {
	return this.foldWithInitial(next, folder);
}
auto __lambda51__(auto next) {
	return this.foldWithInitial;
}
auto __lambda52__(auto next) {
	return this;
}
auto __lambda53__(auto next) {
	return this;
}
<R> Option<R> foldWithMapper(R (*)(T) mapper, BiFunction<R, T, R> folder) {
	return this.head.next().map(mapper).map(__lambda50__);
}
auto __lambda54__(auto current) {
	return mapper.apply(current, t);
}
auto __lambda55__(auto current) {
	return mapper.apply;
}
auto __lambda56__(auto current) {
	return mapper;
}
auto __lambda57__(auto current) {
	return mapper;
}
auto __lambda58__(auto result, t) {
	return result.flatMapValue(__lambda54__);
}
auto __lambda59__(auto result, t) {
	return result.flatMapValue;
}
auto __lambda60__(auto result, t) {
	return result;
}
auto __lambda61__(auto current) {
	return mapper.apply(current, t);
}
auto __lambda62__(auto current) {
	return mapper.apply;
}
auto __lambda63__(auto current) {
	return mapper;
}
auto __lambda64__(auto current) {
	return mapper;
}
auto __lambda65__(auto result, t) {
	return result.flatMapValue(
                            current -> mapper;
}
auto __lambda66__(auto result, t) {
	return result;
}
<R, X> Result<R, X> foldToResult(R initial, BiFunction<R, T, Result<R, X>> mapper) {
	return this.<Result<R, X>>foldWithInitial(Ok<>(initial), __lambda58__);
}
auto __lambda67__(auto aBoolean, t) {
	return aBoolean || filter.test(t);
}
auto __lambda68__(auto aBoolean, t) {
	return aBoolean || filter.test;
}
auto __lambda69__(auto aBoolean, t) {
	return aBoolean || filter;
}
auto __lambda70__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda71__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda72__(auto aBoolean, t) {
	return aBoolean || filter;
}
auto __lambda73__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda74__(auto aBoolean, t) {
	return aBoolean;
}
int anyMatch(Predicate<T> filter) {
	return this.foldWithInitial(0, __lambda67__);
}
auto __lambda75__ {
	return struct Iterator.concat()
}
<R> Iterator<R> flatMap(Iterator<R> (*)(T) mapper) {
	return this.map(mapper).foldWithInitial(Iterators.empty(), __lambda75__);
}
<R> Option<R> map(R (*)(T) mapper) {
	return None<>();
}
int isPresent() {
	return 0;
}
T orElse(T other) {
	return other;
}
int isEmpty() {
	return 1;
}
void ifPresent(Consumer<T> consumer) {
}
Option<T> or(Supplier<Option<T>> other) {
	return other.get();
}
<R> Option<R> flatMap(Option<R> (*)(T) mapper) {
	return None<>();
}
Tuple<int, T> toTuple(T other) {
	return Tuple<>(0, other);
}
T orElseGet(Supplier<T> supplier) {
	return supplier.get();
}
<R> R match(R (*)(T) whenPresent, Supplier<R> whenEmpty) {
	return whenEmpty.get();
}
<R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
	return None<>();
}
Option<T> next() {
	return None<>();
}
struct private SingleHead(T value) {
	this.value = value;
}
Option<T> next() {
	if (this.retrieved) {
		return None<>();
	}
	this.retrieved = 1;
	return Some<>(this.value);
}
<R> Option<R> map(R (*)(T) mapper) {
	return Some<>(mapper.apply(this.value));
}
int isPresent() {
	return 1;
}
T orElse(T other) {
	return this.value;
}
int isEmpty() {
	return 0;
}
void ifPresent(Consumer<T> consumer) {
	consumer.accept(this.value);
}
Option<T> or(Supplier<Option<T>> other) {
	return this;
}
<R> Option<R> flatMap(Option<R> (*)(T) mapper) {
	return mapper.apply(this.value);
}
Tuple<int, T> toTuple(T other) {
	return Tuple<>(1, this.value);
}
T orElseGet(Supplier<T> supplier) {
	return this.value;
}
<R> R match(R (*)(T) whenPresent, Supplier<R> whenEmpty) {
	return whenPresent.apply(this.value);
}
auto __lambda76__(auto otherValue) {
	return Tuple<>(this.value, otherValue);
}
<R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
	return other.get().map(__lambda76__);
}
<R> R match(R (*)(T) whenOk, R (*)(X) whenErr) {
	return whenErr.apply(this.error);
}
<R> Result<T, R> mapErr(R (*)(X) mapper) {
	return Err<>(mapper.apply(this.error));
}
<R> Result<R, X> flatMapValue(Result<R, X> (*)(T) mapper) {
	return Err<>(this.error);
}
<R> Result<R, X> mapValue(R (*)(T) mapper) {
	return Err<>(this.error);
}
Option<T> findValue() {
	return None<>();
}
<R> R match(R (*)(T) whenOk, R (*)(X) whenErr) {
	return whenOk.apply(this.value);
}
<R> Result<T, R> mapErr(R (*)(X) mapper) {
	return Ok<>(this.value);
}
<R> Result<R, X> flatMapValue(Result<R, X> (*)(T) mapper) {
	return mapper.apply(this.value);
}
<R> Result<R, X> mapValue(R (*)(T) mapper) {
	return Ok<>(mapper.apply(this.value));
}
Option<T> findValue() {
	return Some<>(this.value);
}
struct private State(List_<char> queue, List_<struct String> segments, struct StringBuilder buffer, int depth) {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
struct public State(List_<char> queue) {
	this(queue, Lists.empty(), struct StringBuilder(), 0);
}
auto __lambda77__(auto tuple) {
	return tuple.right.append(tuple.left);
}
auto __lambda78__(auto tuple) {
	return tuple.right.append;
}
auto __lambda79__(auto tuple) {
	return tuple.right;
}
auto __lambda80__(auto tuple) {
	return tuple;
}
auto __lambda81__(auto tuple) {
	return tuple.right.append(tuple;
}
auto __lambda82__(auto tuple) {
	return tuple.right;
}
auto __lambda83__(auto tuple) {
	return tuple;
}
Option<struct State> popAndAppend() {
	return this.pop().map(__lambda77__);
}
struct State advance() {
	return struct State(this.queue, this.segments.add(this.buffer.toString()), struct StringBuilder(), this.depth);
}
struct State append(char c) {
	return struct State(this.queue, this.segments, this.buffer.append(c), this.depth);
}
int isLevel() {
	return this.depth == 0;
}
auto __lambda84__(auto tuple) {
	return Tuple<>(tuple.left, struct State(tuple.right, this.segments, this.buffer, this.depth));
}
Option<Tuple<char, struct State>> pop() {
	return this.queue.popFirst().map(__lambda84__);
}
int hasElements() {
	return !this.queue.isEmpty();
}
struct State exit() {
	return struct State(this.queue, this.segments, this.buffer, this.depth - 1);
}
struct State enter() {
	return struct State(this.queue, this.segments, this.buffer, this.depth + 1);
}
List_<struct String> segments() {
	return this.segments;
}
Option<char> peek() {
	return this.queue.peekFirst();
}
<T> Iterator<T> empty() {
	return HeadedIterator<>(EmptyHead<>());
}
auto __lambda85__(auto tuple) {
	return tuple.right;
}
auto __lambda86__(auto tuple) {
	return tuple;
}
Iterator<char> fromString(struct String input) {
	return fromStringWithIndices(input).map(__lambda85__);
}
auto __lambda87__(auto index) {
	return Tuple<>(index, input.charAt(index));
}
Iterator<Tuple<int, char>> fromStringWithIndices(struct String input) {
	return HeadedIterator<>(struct RangeHead(input.length())).map(index -> new Tuple<>(index, input.charAt(index)));
}
struct public RangeHead(int length) {
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
Option<struct String> createInitial() {
	return None<>();
}
auto __lambda88__(auto inner) {
	return inner + this.delimiter + element;
}
auto __lambda89__(auto inner) {
	return inner + this;
}
auto __lambda90__(auto inner) {
	return inner;
}
auto __lambda91__(auto inner) {
	return inner;
}
auto __lambda92__(auto inner) {
	return inner + this.delimiter + element;
}
auto __lambda93__(auto inner) {
	return inner + this;
}
auto __lambda94__(auto inner) {
	return inner;
}
auto __lambda95__(auto inner) {
	return inner;
}
Option<struct String> fold(Option<struct String> current, struct String element) {
	return Some<>(current.map(__lambda88__).orElse(element));
}
struct State fold(struct State state, char c) {
	if (c == this.delimiter) {
		return state.advance();
	}
	return state.append(c);
}
auto __lambda96__ {
	return second;
}
auto __lambda97__(auto tuple) {
	return equator.apply(tuple.left, tuple.right);
}
auto __lambda98__(auto tuple) {
	return equator.apply;
}
auto __lambda99__(auto tuple) {
	return equator;
}
auto __lambda100__(auto tuple) {
	return equator.apply(tuple.left, tuple;
}
auto __lambda101__(auto tuple) {
	return equator.apply(tuple;
}
auto __lambda102__(auto tuple) {
	return equator;
}
auto __lambda103__ {
	return second;
}
auto __lambda104__ {
	return second;
}
auto __lambda105__(auto tuple) {
	return equator.apply(tuple.left, tuple.right);
}
auto __lambda106__(auto tuple) {
	return equator.apply;
}
auto __lambda107__(auto tuple) {
	return equator;
}
auto __lambda108__(auto tuple) {
	return equator.apply(tuple.left, tuple;
}
auto __lambda109__(auto tuple) {
	return equator.apply(tuple;
}
auto __lambda110__(auto tuple) {
	return equator;
}
auto __lambda111__ {
	return second;
}
<V> int equalsTo(Option<V> first, Option<V> second, BiFunction<V, V, int> equator) {
	if (first.isEmpty() && second.isEmpty()) {
		return 1;
	}
	return first.and(__lambda96__).map(__lambda97__).orElse(0);
}
auto __lambda112__ {
	return second;
}
auto __lambda113__(auto tuple) {
	return equator.apply(tuple.left, tuple.right);
}
auto __lambda114__(auto tuple) {
	return equator.apply;
}
auto __lambda115__(auto tuple) {
	return equator;
}
auto __lambda116__(auto tuple) {
	return equator.apply(tuple.left, tuple;
}
auto __lambda117__(auto tuple) {
	return equator.apply(tuple;
}
auto __lambda118__(auto tuple) {
	return equator;
}
auto __lambda119__ {
	return second;
}
auto __lambda120__ {
	return second;
}
auto __lambda121__(auto tuple) {
	return equator.apply(tuple.left, tuple.right);
}
auto __lambda122__(auto tuple) {
	return equator.apply;
}
auto __lambda123__(auto tuple) {
	return equator;
}
auto __lambda124__(auto tuple) {
	return equator.apply(tuple.left, tuple;
}
auto __lambda125__(auto tuple) {
	return equator.apply(tuple;
}
auto __lambda126__(auto tuple) {
	return equator;
}
auto __lambda127__ {
	return second;
}
struct public CompileError(struct String message, struct String context) {
	this(message, context, Lists.empty());
}
struct String display0() {
	return this.format(0);
}
Option<int> createInitial() {
	return None<>();
}
Option<int> fold(Option<int> current, int element) {
	return Some<>(current.map(inner -> inner > element ? inner : element).orElse(element));
}
/*struct Result<T, X> {
	<R> R match(R (*)(T) whenOk, R (*)(X) whenErr);
	<R> Result<T, R> mapErr(R (*)(X) mapper);
	<R> Result<R, X> flatMapValue(Result<R, X> (*)(T) mapper);
	<R> Result<R, X> mapValue(R (*)(T) mapper);
	Option<T> findValue();
};
*//*struct Option<T> {
	<R> Option<R> map(R (*)(T) mapper);
	int isPresent();
	T orElse(T other);
	int isEmpty();
	void ifPresent(Consumer<T> consumer);
	Option<T> or(Supplier<Option<T>> other);
	<R> Option<R> flatMap(Option<R> (*)(T) mapper);
	Tuple<int, T> toTuple(T other);
	T orElseGet(Supplier<T> supplier);
	<R> R match(R (*)(T) whenPresent, Supplier<R> whenEmpty);
	<R> Option<Tuple<T, R>> and(Supplier<Option<R>> other);
};
*/struct Error {
	struct String_ display();
};
struct IOError {
};
/*struct List_<T> {
	List_<T> add(T element);
	List_<T> addAll(List_<T> others);
	Iterator<T> iter();
	int isEmpty();
	int size();
	List_<T> slice(int startInclusive, int endExclusive);
	Option<Tuple<T, List_<T>>> popFirst();
	Option<T> peekFirst();
	T get(int index);
	List_<T> sort(BiFunction<T, T, int> comparator);
	T last();
	List_<T> set(int index, T element);
};
*/struct Path_ {
	struct Path_ resolveSibling(struct String sibling);
	List_<struct String> asList();
};
/*struct Iterator<T> {
	<R> R foldWithInitial(R initial, BiFunction<R, T, R> folder);
	void forEach(Consumer<T> consumer);
	<R> Iterator<R> map(R (*)(T) mapper);
	Iterator<T> filter(Predicate<T> predicate);
	Option<T> next();
	Iterator<T> concat(Iterator<T> other);
	<C> C collect(Collector<T, C> collector);
	int allMatch(Predicate<T> predicate);
	<R> Option<R> foldWithMapper(R (*)(T) mapper, BiFunction<R, T, R> folder);
	<R, X> Result<R, X> foldToResult(R initial, BiFunction<R, T, Result<R, X>> mapper);
	int anyMatch(Predicate<T> filter);
};
*//*struct Collector<T, C> {
	C createInitial();
	C fold(C current, T element);
};
*//*struct Head<T> {
	Option<T> next();
};
*/struct Divider {
	struct State fold(struct State state, char c);
};
struct Rule {
};
/*struct Map_<K, V> {
	Map_<K, V> with(K propertyKey, V propertyValue);
	Option<V> find(K propertyKey);
	Map_<K, V> withAll(Map_<K, V> other);
	Main.Iterator<Main.Tuple<K, V>> iter();
	Iterator<K> keys();
};
*/struct String_ {
};
struct ApplicationError {
};
/*struct HeadedIterator<T> {
};
*//*struct None<T> {
};
*//*struct EmptyHead<T> {
};
*//*struct SingleHead<T> {
	T value;
};
*//*struct Some<T> {
};
*//*struct Err<T, X> {
};
*//*struct Ok<T, X> {
};
*/struct State {
	List_<char> queue;
	List_<struct String> segments;
	struct StringBuilder buffer;
	int depth;
};
/*struct Tuple<A, B> {
};
*/struct Iterators {
};
struct RangeHead {
	int length;
};
/*struct ListCollector<T> {
};
*/struct Joiner {
};
struct DelimitedDivider {
};
struct Options {
};
struct CompileError {
	struct String format(int depth);
};
struct Max {
	Result<struct String, struct CompileError> apply(struct String input);
};
struct Main {
	struct record DecoratedDivider(struct Divider divider);
};
// Result<T, R>
// Result<R, X>
// Option<T>
// Option<R>
// Consumer<T>
// Supplier<Option<T>>
// Tuple<int, T>
// Supplier<T>
// Supplier<R>
// Tuple<T, R>
// Option<Tuple<T, R>>
// Supplier<Option<R>>
// List_<T>
// Iterator<T>
// Tuple<T, List_<T>>
// Option<Tuple<T, List_<T>>>
// BiFunction<T, T, int>
// List_<struct String>
// BiFunction<R, T, R>
// Iterator<R>
// Predicate<T>
// Collector<T, C>
// BiFunction<R, T, Result<R, X>>
// Map_<K, V>
// Option<V>
// Main.Tuple<K, V>
// Main.Iterator<Main.Tuple<K, V>>
// Iterator<K>
// HeadedIterator<>
// Ok<>
// None<>
// Tuple<>
// Some<>
// Err<>
// List_<char>
// Option<struct State>
// Tuple<char, struct State>
// Option<Tuple<char, struct State>>
// Option<char>
// EmptyHead<>
// Iterator<char>
// Tuple<int, char>
// Iterator<Tuple<int, char>>
// Option<int>
// Option<struct String>
// BiFunction<V, V, int>
// Result<struct String, struct CompileError>
// List_<struct CompileError>
int retrieved = 0;
int counter = 0;
struct record String_(char* array) {
}
struct String display0() {
	return Strings.unwrap(this.error.display());
}
struct String_ display() {
	return Strings.from(this.display0().toCharArray());
}
auto __lambda0__(auto next) {
	return folder.apply(finalCurrent, next);
}
auto __lambda1__(auto next) {
	return folder.apply;
}
auto __lambda2__(auto next) {
	return folder;
}
auto __lambda3__(auto next) {
	return folder;
}
<R> R foldWithInitial(R initial, BiFunction<R, T, R> folder) {
	R current = initial;
	while (1) {
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
	while (1) {
		Option<T> next = this.head.next();
		if (next.isEmpty()) {
			break;
		}
		next.ifPresent(consumer);
	}
}
auto __lambda4__ {
	return this.head.next().map(mapper);
}
auto __lambda5__ {
	return this.head.next().map;
}
auto __lambda6__ {
	return this.head.next();
}
auto __lambda7__ {
	return this.head.next;
}
auto __lambda8__ {
	return this.head;
}
auto __lambda9__ {
	return this;
}
auto __lambda10__ {
	return this.head;
}
auto __lambda11__ {
	return this;
}
auto __lambda12__ {
	return this.head.next();
}
auto __lambda13__ {
	return this.head.next;
}
auto __lambda14__ {
	return this.head;
}
auto __lambda15__ {
	return this;
}
auto __lambda16__ {
	return this.head;
}
auto __lambda17__ {
	return this;
}
auto __lambda18__ {
	return this.head.next(;
}
auto __lambda19__ {
	return this.head;
}
auto __lambda20__ {
	return this;
}
<R> Iterator<R> map(R (*)(T) mapper) {
	return HeadedIterator<>(__lambda4__);
}
auto __lambda21__(auto value) {
	return HeadedIterator<>(predicate.test(value)
                    ? new SingleHead<>(value)
                    : new EmptyHead<T>());
}
Iterator<T> filter(Predicate<T> predicate) {
	return this.flatMap(__lambda21__);
}
Option<T> next() {
	return this.head.next();
}
auto __lambda22__ {
	return struct other.next()
}
auto __lambda23__ {
	return this.head.next().or(__lambda22__);
}
auto __lambda24__ {
	return this.head.next().or;
}
auto __lambda25__ {
	return this.head.next();
}
auto __lambda26__ {
	return this.head.next;
}
auto __lambda27__ {
	return this.head;
}
auto __lambda28__ {
	return this;
}
auto __lambda29__ {
	return this.head;
}
auto __lambda30__ {
	return this;
}
auto __lambda31__ {
	return struct other.next()
}
auto __lambda32__ {
	return this.head.next();
}
auto __lambda33__ {
	return this.head.next;
}
auto __lambda34__ {
	return this.head;
}
auto __lambda35__ {
	return this;
}
auto __lambda36__ {
	return this.head;
}
auto __lambda37__ {
	return this;
}
auto __lambda38__ {
	return this.head.next(;
}
auto __lambda39__ {
	return this.head;
}
auto __lambda40__ {
	return this;
}
Iterator<T> concat(Iterator<T> other) {
	return HeadedIterator<>(__lambda23__);
}
auto __lambda41__ {
	return struct collector.fold()
}
<C> C collect(Collector<T, C> collector) {
	return this.foldWithInitial(collector.createInitial(), __lambda41__);
}
auto __lambda42__(auto aBoolean, t) {
	return aBoolean && predicate.test(t);
}
auto __lambda43__(auto aBoolean, t) {
	return aBoolean && predicate.test;
}
auto __lambda44__(auto aBoolean, t) {
	return aBoolean && predicate;
}
auto __lambda45__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda46__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda47__(auto aBoolean, t) {
	return aBoolean && predicate;
}
auto __lambda48__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda49__(auto aBoolean, t) {
	return aBoolean;
}
int allMatch(Predicate<T> predicate) {
	return this.foldWithInitial(1, __lambda42__);
}
auto __lambda50__(auto next) {
	return this.foldWithInitial(next, folder);
}
auto __lambda51__(auto next) {
	return this.foldWithInitial;
}
auto __lambda52__(auto next) {
	return this;
}
auto __lambda53__(auto next) {
	return this;
}
<R> Option<R> foldWithMapper(R (*)(T) mapper, BiFunction<R, T, R> folder) {
	return this.head.next().map(mapper).map(__lambda50__);
}
auto __lambda54__(auto current) {
	return mapper.apply(current, t);
}
auto __lambda55__(auto current) {
	return mapper.apply;
}
auto __lambda56__(auto current) {
	return mapper;
}
auto __lambda57__(auto current) {
	return mapper;
}
auto __lambda58__(auto result, t) {
	return result.flatMapValue(__lambda54__);
}
auto __lambda59__(auto result, t) {
	return result.flatMapValue;
}
auto __lambda60__(auto result, t) {
	return result;
}
auto __lambda61__(auto current) {
	return mapper.apply(current, t);
}
auto __lambda62__(auto current) {
	return mapper.apply;
}
auto __lambda63__(auto current) {
	return mapper;
}
auto __lambda64__(auto current) {
	return mapper;
}
auto __lambda65__(auto result, t) {
	return result.flatMapValue(
                            current -> mapper;
}
auto __lambda66__(auto result, t) {
	return result;
}
<R, X> Result<R, X> foldToResult(R initial, BiFunction<R, T, Result<R, X>> mapper) {
	return this.<Result<R, X>>foldWithInitial(Ok<>(initial), __lambda58__);
}
auto __lambda67__(auto aBoolean, t) {
	return aBoolean || filter.test(t);
}
auto __lambda68__(auto aBoolean, t) {
	return aBoolean || filter.test;
}
auto __lambda69__(auto aBoolean, t) {
	return aBoolean || filter;
}
auto __lambda70__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda71__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda72__(auto aBoolean, t) {
	return aBoolean || filter;
}
auto __lambda73__(auto aBoolean, t) {
	return aBoolean;
}
auto __lambda74__(auto aBoolean, t) {
	return aBoolean;
}
int anyMatch(Predicate<T> filter) {
	return this.foldWithInitial(0, __lambda67__);
}
auto __lambda75__ {
	return struct Iterator.concat()
}
<R> Iterator<R> flatMap(Iterator<R> (*)(T) mapper) {
	return this.map(mapper).foldWithInitial(Iterators.empty(), __lambda75__);
}
<R> Option<R> map(R (*)(T) mapper) {
	return None<>();
}
int isPresent() {
	return 0;
}
T orElse(T other) {
	return other;
}
int isEmpty() {
	return 1;
}
void ifPresent(Consumer<T> consumer) {
}
Option<T> or(Supplier<Option<T>> other) {
	return other.get();
}
<R> Option<R> flatMap(Option<R> (*)(T) mapper) {
	return None<>();
}
Tuple<int, T> toTuple(T other) {
	return Tuple<>(0, other);
}
T orElseGet(Supplier<T> supplier) {
	return supplier.get();
}
<R> R match(R (*)(T) whenPresent, Supplier<R> whenEmpty) {
	return whenEmpty.get();
}
<R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
	return None<>();
}
Option<T> next() {
	return None<>();
}
struct private SingleHead(T value) {
	this.value = value;
}
Option<T> next() {
	if (this.retrieved) {
		return None<>();
	}
	this.retrieved = 1;
	return Some<>(this.value);
}
<R> Option<R> map(R (*)(T) mapper) {
	return Some<>(mapper.apply(this.value));
}
int isPresent() {
	return 1;
}
T orElse(T other) {
	return this.value;
}
int isEmpty() {
	return 0;
}
void ifPresent(Consumer<T> consumer) {
	consumer.accept(this.value);
}
Option<T> or(Supplier<Option<T>> other) {
	return this;
}
<R> Option<R> flatMap(Option<R> (*)(T) mapper) {
	return mapper.apply(this.value);
}
Tuple<int, T> toTuple(T other) {
	return Tuple<>(1, this.value);
}
T orElseGet(Supplier<T> supplier) {
	return this.value;
}
<R> R match(R (*)(T) whenPresent, Supplier<R> whenEmpty) {
	return whenPresent.apply(this.value);
}
auto __lambda76__(auto otherValue) {
	return Tuple<>(this.value, otherValue);
}
<R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
	return other.get().map(__lambda76__);
}
<R> R match(R (*)(T) whenOk, R (*)(X) whenErr) {
	return whenErr.apply(this.error);
}
<R> Result<T, R> mapErr(R (*)(X) mapper) {
	return Err<>(mapper.apply(this.error));
}
<R> Result<R, X> flatMapValue(Result<R, X> (*)(T) mapper) {
	return Err<>(this.error);
}
<R> Result<R, X> mapValue(R (*)(T) mapper) {
	return Err<>(this.error);
}
Option<T> findValue() {
	return None<>();
}
<R> R match(R (*)(T) whenOk, R (*)(X) whenErr) {
	return whenOk.apply(this.value);
}
<R> Result<T, R> mapErr(R (*)(X) mapper) {
	return Ok<>(this.value);
}
<R> Result<R, X> flatMapValue(Result<R, X> (*)(T) mapper) {
	return mapper.apply(this.value);
}
<R> Result<R, X> mapValue(R (*)(T) mapper) {
	return Ok<>(mapper.apply(this.value));
}
Option<T> findValue() {
	return Some<>(this.value);
}
struct private State(List_<char> queue, List_<struct String> segments, struct StringBuilder buffer, int depth) {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
struct public State(List_<char> queue) {
	this(queue, Lists.empty(), struct StringBuilder(), 0);
}
auto __lambda77__(auto tuple) {
	return tuple.right.append(tuple.left);
}
auto __lambda78__(auto tuple) {
	return tuple.right.append;
}
auto __lambda79__(auto tuple) {
	return tuple.right;
}
auto __lambda80__(auto tuple) {
	return tuple;
}
auto __lambda81__(auto tuple) {
	return tuple.right.append(tuple;
}
auto __lambda82__(auto tuple) {
	return tuple.right;
}
auto __lambda83__(auto tuple) {
	return tuple;
}
Option<struct State> popAndAppend() {
	return this.pop().map(__lambda77__);
}
struct State advance() {
	return struct State(this.queue, this.segments.add(this.buffer.toString()), struct StringBuilder(), this.depth);
}
struct State append(char c) {
	return struct State(this.queue, this.segments, this.buffer.append(c), this.depth);
}
int isLevel() {
	return this.depth == 0;
}
auto __lambda84__(auto tuple) {
	return Tuple<>(tuple.left, struct State(tuple.right, this.segments, this.buffer, this.depth));
}
Option<Tuple<char, struct State>> pop() {
	return this.queue.popFirst().map(__lambda84__);
}
int hasElements() {
	return !this.queue.isEmpty();
}
struct State exit() {
	return struct State(this.queue, this.segments, this.buffer, this.depth - 1);
}
struct State enter() {
	return struct State(this.queue, this.segments, this.buffer, this.depth + 1);
}
List_<struct String> segments() {
	return this.segments;
}
Option<char> peek() {
	return this.queue.peekFirst();
}
<T> Iterator<T> empty() {
	return HeadedIterator<>(EmptyHead<>());
}
auto __lambda85__(auto tuple) {
	return tuple.right;
}
auto __lambda86__(auto tuple) {
	return tuple;
}
Iterator<char> fromString(struct String input) {
	return fromStringWithIndices(input).map(__lambda85__);
}
auto __lambda87__(auto index) {
	return Tuple<>(index, input.charAt(index));
}
Iterator<Tuple<int, char>> fromStringWithIndices(struct String input) {
	return HeadedIterator<>(struct RangeHead(input.length())).map(index -> new Tuple<>(index, input.charAt(index)));
}
struct public RangeHead(int length) {
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
Option<struct String> createInitial() {
	return None<>();
}
auto __lambda88__(auto inner) {
	return inner + this.delimiter + element;
}
auto __lambda89__(auto inner) {
	return inner + this;
}
auto __lambda90__(auto inner) {
	return inner;
}
auto __lambda91__(auto inner) {
	return inner;
}
auto __lambda92__(auto inner) {
	return inner + this.delimiter + element;
}
auto __lambda93__(auto inner) {
	return inner + this;
}
auto __lambda94__(auto inner) {
	return inner;
}
auto __lambda95__(auto inner) {
	return inner;
}
Option<struct String> fold(Option<struct String> current, struct String element) {
	return Some<>(current.map(__lambda88__).orElse(element));
}
struct State fold(struct State state, char c) {
	if (c == this.delimiter) {
		return state.advance();
	}
	return state.append(c);
}
auto __lambda96__ {
	return second;
}
auto __lambda97__(auto tuple) {
	return equator.apply(tuple.left, tuple.right);
}
auto __lambda98__(auto tuple) {
	return equator.apply;
}
auto __lambda99__(auto tuple) {
	return equator;
}
auto __lambda100__(auto tuple) {
	return equator.apply(tuple.left, tuple;
}
auto __lambda101__(auto tuple) {
	return equator.apply(tuple;
}
auto __lambda102__(auto tuple) {
	return equator;
}
auto __lambda103__ {
	return second;
}
auto __lambda104__ {
	return second;
}
auto __lambda105__(auto tuple) {
	return equator.apply(tuple.left, tuple.right);
}
auto __lambda106__(auto tuple) {
	return equator.apply;
}
auto __lambda107__(auto tuple) {
	return equator;
}
auto __lambda108__(auto tuple) {
	return equator.apply(tuple.left, tuple;
}
auto __lambda109__(auto tuple) {
	return equator.apply(tuple;
}
auto __lambda110__(auto tuple) {
	return equator;
}
auto __lambda111__ {
	return second;
}
<V> int equalsTo(Option<V> first, Option<V> second, BiFunction<V, V, int> equator) {
	if (first.isEmpty() && second.isEmpty()) {
		return 1;
	}
	return first.and(__lambda96__).map(__lambda97__).orElse(0);
}
auto __lambda112__ {
	return second;
}
auto __lambda113__(auto tuple) {
	return equator.apply(tuple.left, tuple.right);
}
auto __lambda114__(auto tuple) {
	return equator.apply;
}
auto __lambda115__(auto tuple) {
	return equator;
}
auto __lambda116__(auto tuple) {
	return equator.apply(tuple.left, tuple;
}
auto __lambda117__(auto tuple) {
	return equator.apply(tuple;
}
auto __lambda118__(auto tuple) {
	return equator;
}
auto __lambda119__ {
	return second;
}
auto __lambda120__ {
	return second;
}
auto __lambda121__(auto tuple) {
	return equator.apply(tuple.left, tuple.right);
}
auto __lambda122__(auto tuple) {
	return equator.apply;
}
auto __lambda123__(auto tuple) {
	return equator;
}
auto __lambda124__(auto tuple) {
	return equator.apply(tuple.left, tuple;
}
auto __lambda125__(auto tuple) {
	return equator.apply(tuple;
}
auto __lambda126__(auto tuple) {
	return equator;
}
auto __lambda127__ {
	return second;
}
struct public CompileError(struct String message, struct String context) {
	this(message, context, Lists.empty());
}
struct String display0() {
	return this.format(0);
}
Option<int> createInitial() {
	return None<>();
}
Option<int> fold(Option<int> current, int element) {
	return Some<>(current.map(inner -> inner > element ? inner : element).orElse(element));
}
