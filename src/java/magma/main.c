struct Result<T, X> {
	<T, X, R> struct R match(R (*)(T) whenOk, R (*)(X) whenErr);
	<T, X, R, R> Result<struct T, struct R> mapErr(R (*)(X) mapper);
	<T, X, R, R, R> Result<struct R, struct X> flatMapValue(Result<R, X> (*)(T) mapper);
	<T, X, R, R, R, R> Result<struct R, struct X> mapValue(R (*)(T) mapper);
	Option<T> findValue();
};
struct Option<T> {
	<T, X, R, R, R, R, T, X> {
        <R, T, R> Option<struct R> map(R (*)(T) mapper);
	int isPresent();
	T orElse(T other);
	int isEmpty();
	void ifPresent(Consumer<T> consumer);
	Option<T> or(Supplier<Option<T>> other);
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R> Option<struct R> flatMap(Option<R> (*)(T) mapper);
	Tuple<int, T> toTuple(T other);
	T orElseGet(Supplier<T> supplier);
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R> struct R match(R (*)(T) whenPresent, Supplier<R> whenEmpty);
};
struct Error {
	String display();
};
struct IOError {
};
struct List_<T> {
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
};
struct Path_ {
	Path_ resolveSibling(String sibling);
	List_<String> asList();
};
struct Iterator<T> {
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R> struct R foldWithInitial(R initial, BiFunction<R, T, R> folder);
	void forEach(Consumer<T> consumer);
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R> Iterator<struct R> map(R (*)(T) mapper);
	Iterator<T> filter(Predicate<T> predicate);
	Option<T> next();
	Iterator<T> concat(Iterator<T> other);
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C> struct C collect(Collector<T, C> collector);
	int allMatch(Predicate<T> predicate);
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R> Option<struct R> foldWithMapper(R (*)(T) mapper, BiFunction<R, T, R> folder);
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X> Result<struct R, struct X> foldToResult(R initial, BiFunction<R, T, Result<R, X>> mapper);
};
struct Collector<T, C> {
	C createInitial();
	C fold(C current, T element);
};
struct Head<T> {
	Option<T> next();
};
struct Divider {
	State fold(State state, char c);
};
struct Rule {
};
struct ApplicationError {
};
struct HeadedIterator<T> {
};
struct None<T> {
};
struct EmptyHead<T> {
};
struct SingleHead<T> {
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T> struct T value;
};
struct Some<T> {
};
struct Err<T, X> {
};
struct Ok<T, X> {
};
struct State {
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> List_<char> queue;
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> List_<struct String> segments;
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> struct StringBuilder buffer;
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> int depth;
};
struct Tuple<A, B> {
};
struct Iterators {
};
struct RangeHead {
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T> int length;
};
struct ListCollector<T> {
};
struct Joiner {
};
struct DelimitedDivider {
};
struct CompileError {
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T, T> struct String format(int depth);
};
struct Max {
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T, T> Result<struct String, struct CompileError> apply(String input);
};
struct Main {
	<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T, T> struct record DecoratedDivider(Divider divider);
};
int retrieved = false;
int counter = 0;
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T> struct String display() {
	return this.error.display();
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
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R> struct R foldWithInitial(R initial, BiFunction<R, T, R> folder) {
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
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R> void forEach(Consumer<T> consumer) {
	while (true) {
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
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R> Iterator<struct R> map(R (*)(T) mapper) {
	return HeadedIterator<>(__lambda4__);
}
auto __lambda21__(auto value) {
	return HeadedIterator<>(predicate.test(value)
                    ? new SingleHead<>(value)
                    : new EmptyHead<T>());
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R> Iterator<struct T> filter(Predicate<T> predicate) {
	return this.flatMap(__lambda21__);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R> Option<struct T> next() {
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
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R> Iterator<struct T> concat(Iterator<T> other) {
	return HeadedIterator<>(__lambda23__);
}
auto __lambda41__ {
	return struct collector.fold()
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C> struct C collect(Collector<T, C> collector) {
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
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C> int allMatch(Predicate<T> predicate) {
	return this.foldWithInitial(true, __lambda42__);
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
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R> Option<struct R> foldWithMapper(R (*)(T) mapper, BiFunction<R, T, R> folder) {
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
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X> Result<struct R, struct X> foldToResult(R initial, BiFunction<R, T, Result<R, X>> mapper) {
	return this.<Result<R, X>>foldWithInitial(Ok<>(initial), __lambda58__);
}
auto __lambda67__ {
	return struct Iterator.concat()
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R> Iterator<struct R> flatMap(Iterator<R> (*)(T) mapper) {
	return this.map(mapper).foldWithInitial(Iterators.empty(), __lambda67__);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R> Option<struct R> map(R (*)(T) mapper) {
	return None<>();
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R> int isPresent() {
	return false;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R> struct T orElse(T other) {
	return other;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R> int isEmpty() {
	return true;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R> void ifPresent(Consumer<T> consumer) {
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R> Option<struct T> or(Supplier<Option<T>> other) {
	return other.get();
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R> Option<struct R> flatMap(Option<R> (*)(T) mapper) {
	return None<>();
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R> Tuple<int, struct T> toTuple(T other) {
	return Tuple<>(false, other);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R> struct T orElseGet(Supplier<T> supplier) {
	return supplier.get();
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R> struct R match(R (*)(T) whenPresent, Supplier<R> whenEmpty) {
	return whenEmpty.get();
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T> Option<struct T> next() {
	return None<>();
}
private SingleHead(T value) {
	this.value = value;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T> Option<struct T> next() {
	if (this.retrieved) {
		return None<>();
	}
	this.retrieved = true;
	return Some<>(this.value);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R> Option<struct R> map(R (*)(T) mapper) {
	return Some<>(mapper.apply(this.value));
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R> int isPresent() {
	return true;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R> struct T orElse(T other) {
	return this.value;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R> int isEmpty() {
	return false;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R> void ifPresent(Consumer<T> consumer) {
	consumer.accept(this.value);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R> Option<struct T> or(Supplier<Option<T>> other) {
	return this;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R> Option<struct R> flatMap(Option<R> (*)(T) mapper) {
	return mapper.apply(this.value);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R> Tuple<int, struct T> toTuple(T other) {
	return Tuple<>(true, this.value);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R> struct T orElseGet(Supplier<T> supplier) {
	return this.value;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R> struct R match(R (*)(T) whenPresent, Supplier<R> whenEmpty) {
	return whenPresent.apply(this.value);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R> struct R match(R (*)(T) whenOk, R (*)(X) whenErr) {
	return whenErr.apply(this.error);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R> Result<struct T, struct R> mapErr(R (*)(X) mapper) {
	return Err<>(mapper.apply(this.error));
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R> Result<struct R, struct X> flatMapValue(Result<R, X> (*)(T) mapper) {
	return Err<>(this.error);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R> Result<struct R, struct X> mapValue(R (*)(T) mapper) {
	return Err<>(this.error);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R> Option<struct T> findValue() {
	return None<>();
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R> struct R match(R (*)(T) whenOk, R (*)(X) whenErr) {
	return whenOk.apply(this.value);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R> Result<struct T, struct R> mapErr(R (*)(X) mapper) {
	return Ok<>(this.value);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R> Result<struct R, struct X> flatMapValue(Result<R, X> (*)(T) mapper) {
	return mapper.apply(this.value);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> Result<struct R, struct X> mapValue(R (*)(T) mapper) {
	return Ok<>(mapper.apply(this.value));
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> Option<struct T> findValue() {
	return Some<>(this.value);
}
private State(List_<char> queue, List_<String> segments, StringBuilder buffer, int depth) {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
public State(List_<char> queue) {
	this(queue, Lists.empty(), struct StringBuilder(), 0);
}
auto __lambda68__(auto tuple) {
	return tuple.right.append(tuple.left);
}
auto __lambda69__(auto tuple) {
	return tuple.right.append;
}
auto __lambda70__(auto tuple) {
	return tuple.right;
}
auto __lambda71__(auto tuple) {
	return tuple;
}
auto __lambda72__(auto tuple) {
	return tuple.right.append(tuple;
}
auto __lambda73__(auto tuple) {
	return tuple.right;
}
auto __lambda74__(auto tuple) {
	return tuple;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> Option<struct State> popAndAppend() {
	return this.pop().map(__lambda68__);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> struct State advance() {
	return struct State(this.queue, this.segments.add(this.buffer.toString()), struct StringBuilder(), this.depth);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> struct State append(char c) {
	return struct State(this.queue, this.segments, this.buffer.append(c), this.depth);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> int isLevel() {
	return this.depth == 0;
}
auto __lambda75__(auto tuple) {
	return Tuple<>(tuple.left, struct State(tuple.right, this.segments, this.buffer, this.depth));
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> Option<Tuple<char, struct State>> pop() {
	return this.queue.popFirst().map(__lambda75__);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> int hasElements() {
	return !this.queue.isEmpty();
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> struct State exit() {
	return struct State(this.queue, this.segments, this.buffer, this.depth - 1);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> struct State enter() {
	return struct State(this.queue, this.segments, this.buffer, this.depth + 1);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> List_<struct String> segments() {
	return this.segments;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R> Option<char> peek() {
	return this.queue.peekFirst();
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T> Iterator<struct T> empty() {
	return HeadedIterator<>(EmptyHead<>());
}
auto __lambda76__(auto tuple) {
	return tuple.right;
}
auto __lambda77__(auto tuple) {
	return tuple;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T> Iterator<char> fromString(String input) {
	return fromStringWithIndices(input).map(__lambda76__);
}
auto __lambda78__(auto index) {
	return Tuple<>(index, input.charAt(index));
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T> Iterator<Tuple<int, char>> fromStringWithIndices(String input) {
	return HeadedIterator<>(struct RangeHead(input.length())).map(index -> new Tuple<>(index, input.charAt(index)));
}
public RangeHead(int length) {
	this.length = length;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T> Option<int> next() {
	if (this.counter < this.length) {
		int next = this.counter;this.counter++;
		return Some<>(next);
	}
	return None<>();
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T, T> List_<struct T> createInitial() {
	return Lists.empty();
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T, T> List_<struct T> fold(List_<T> current, T element) {
	return current.add(element);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T, T> Option<struct String> createInitial() {
	return None<>();
}
auto __lambda79__(auto inner) {
	return inner + this.delimiter + element;
}
auto __lambda80__(auto inner) {
	return inner + this;
}
auto __lambda81__(auto inner) {
	return inner;
}
auto __lambda82__(auto inner) {
	return inner;
}
auto __lambda83__(auto inner) {
	return inner + this.delimiter + element;
}
auto __lambda84__(auto inner) {
	return inner + this;
}
auto __lambda85__(auto inner) {
	return inner;
}
auto __lambda86__(auto inner) {
	return inner;
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T, T> Option<struct String> fold(Option<String> current, String element) {
	return Some<>(current.map(__lambda79__).orElse(element));
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T, T> struct State fold(State state, char c) {
	if (c == this.delimiter) {
		return state.advance();
	}
	return state.append(c);
}
public CompileError(String message, String context) {
	this(message, context, Lists.empty());
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T, T> struct String display() {
	return this.format(0);
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T, T> Option<int> createInitial() {
	return None<>();
}
<T, X, R, R, R, R, T, X> {
        <R, T, R, R, R, T> {
        <R, T, T, R, R, C, R, R, X, T> {
        <R, T, C, T, T, R, R, C, R, R, X, R, T, R, R, R, T> implements Option<T> {
        @Override
        public <R, T, T, T, R, R, R, T, X, R, R, R, R, T, X, R, R, R, R, A, B, T, T, T> Option<int> fold(Option<int> current, int element) {
	return Some<>(current.map(inner -> inner > element ? inner : element).orElse(element));
}
