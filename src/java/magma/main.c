struct IOError {
	struct String (*display)();
};
struct Path_ {
	struct Path_ (*resolveSibling)(struct String);
	List__struct String (*listNames)();
};
struct State {
	List__char queue;
	List__struct String segments;
	struct StringBuilder buffer;
	int depth;
	struct private (*State)(List__char, List__struct String, struct StringBuilder, int);
	struct public (*State)(List__char);
	struct State (*advance)();
	struct State (*append)(char);
	int (*isLevel)();
	char (*pop)();
	int (*hasElements)();
	struct State (*exit)();
	struct State (*enter)();
	List__struct String (*segments)();
	char (*peek)();
};
struct Joiner {
	Option_struct String (*createInitial)();
	Option_struct String (*fold)(Option_struct String, struct String);
};
struct RangeHead {
	int length;
	struct public (*RangeHead)(int);
	Option_int (*next)();
};
struct Iterators {
	Iterator_T (*empty)();
	Iterator_char (*fromString)(struct String);
	Iterator_Tuple_int_struct Character (*fromStringWithIndices)(struct String);
	Iterator_T (*fromOption)(Option_struct T);
};
struct Node {
	struct public (*Node)();
	struct Node (*withString)(struct String, struct String);
	struct Node (*withNodeList)(struct String, List__struct Node);
	Option_List__struct Node (*findNodeList)(struct String);
	Option_struct String (*findString)(struct String);
	struct Node (*withNode)(struct String, struct Node);
	Option_struct Node (*findNode)(struct String);
	int (*is)(struct String);
	struct Node (*retype)(struct String);
	int (*equalsTo)(struct Node);
	int (*isABoolean)(List__struct Node, List__struct Node);
};
struct Lists {
	int (*contains)(List__struct T, struct T, BiFunction_struct T_struct T_struct Boolean);
	int (*equalsTo)(List__struct T, List__struct T, BiFunction_struct T_struct T_struct Boolean);
};
struct Options {
	int (*equalsTo)(Option_struct T, Option_struct T, BiFunction_struct T_struct T_struct Boolean);
};
struct Maps {
	int (*equalsTo)(Map__struct K_struct V, Map__struct K_struct V, BiFunction_struct K_struct K_struct Boolean, BiFunction_struct V_struct V_struct Boolean);
};
struct Main {
// Option<T>
// List_<T>
// Iterator<T>
// Head<T>
// Collector<T, C>
// Result<T, X>
// Map_<K, V>
// Err<T, X>
// Ok<T, X>
// Tuple<A, B>
// None<T>
// Some<T>
// HeadedIterator<T>
// EmptyHead<T>
// ListCollector<T>
// SingleHead<T>
	int (*entryEqualsTo)(struct K, Map__struct K_struct V, Map__struct K_struct V, BiFunction_struct V_struct V_struct Boolean);
	List__K (*foldUniquely)(List__struct K, struct K, BiFunction_struct K_struct K_struct Boolean);
	void (*main)(struct String*);
	Option_struct IOError (*compileAndWrite)(struct String, struct Path_);
	struct String (*compile)(struct String);
	struct String (*mergeAllStatements)(List__struct Node);
	Option_List__struct Node (*parseAllStatements)(struct String, Function_struct String_Option_struct Node);
	List__struct String (*divideAllStatements)(struct String);
	struct String (*generateAll)(List__struct Node, Function_struct Node_struct String, BiFunction_struct StringBuilder_struct String_struct StringBuilder);
	struct String (*mergeAll)(List__struct String, BiFunction_struct StringBuilder_struct String_struct StringBuilder);
	Option_List__struct Node (*parseAll)(List__struct String, Function_struct String_Option_struct Node);
	struct StringBuilder (*mergeStatements)(struct StringBuilder, struct String);
	List__struct String (*divide)(struct String, BiFunction_struct State_struct Character_struct State);
	struct State (*divideStatementChar)(struct State, char);
	int (*isShallow)(struct State);
	Option_struct String (*compileRootSegment)(struct String);
	List__struct String (*splitByDelimiter)(struct String, char);
	Option_struct String (*compileToStruct)(struct String, struct String, List__struct String);
	struct String (*expand)(struct String, List__struct String, struct Node, struct Node);
	struct String (*generateGeneric)(struct Node);
	Option_struct String (*generateStruct)(List__struct String, struct Node);
	Option_struct String (*compileClassMember)(struct String, List__struct String);
	Option_struct String (*compileDefinitionStatement)(struct String);
	Option_struct String (*compileGlobalInitialization)(struct String, List__struct String);
	Option_struct String (*compileInitialization)(struct String, List__struct String, int);
	Option_struct String (*compileWhitespace)(struct String);
	Option_struct String (*compileMethod)(struct String, List__struct String);
	Function_struct String_Option_struct Node (*createParamRule)();
	Option_struct String (*getStringOption)(List__struct String, struct Node, List__struct Node, struct String);
	Option_List__struct Node (*parseAllValues)(struct String, Function_struct String_Option_struct Node);
	struct State (*divideValueChar)(struct State, char);
	struct String (*mergeAllValues)(List__struct Node, Function_struct Node_struct String);
	Option_struct String (*compileStatementOrBlock)(struct String, List__struct String, int);
	Option_struct String (*compilePostOperator)(struct String, List__struct String, int, struct String);
	Option_struct String (*compileElse)(struct String, List__struct String, int);
	Option_struct String (*compileKeywordStatement)(struct String, int, struct String);
	struct String (*formatStatement)(int, struct String);
	struct String (*createIndent)(int);
	Option_struct String (*compileConditional)(struct String, List__struct String, struct String, int);
	int (*findConditionEnd)(struct String);
	Option_struct String (*compileInvocationStatement)(struct String, List__struct String, int);
	Option_struct String (*compileAssignment)(struct String, List__struct String, int);
	Option_struct String (*compileReturn)(struct String, List__struct String, int);
	Option_struct String (*compileValue)(struct String, List__struct String, int);
	Option_struct String (*compileOperator)(struct String, List__struct String, int, struct String);
	Option_struct String (*compileLambda)(struct String, List__struct String, int);
	Option_struct String (*generateLambdaWithReturn)(List__struct String, struct String);
	int (*isNumber)(struct String);
	Option_struct String (*compileInvocation)(struct String, List__struct String, int);
	int (*findInvocationStart)(struct String);
	Option_struct String (*compileArgs)(struct String, List__struct String, int);
	struct StringBuilder (*mergeValues)(struct StringBuilder, struct String);
	Option_struct Node (*parseDefinition)(struct String);
	Option_struct Node (*parseDefinitionWithName)(struct String, struct Node);
	Option_struct Node (*parseDefinitionWithTypeSeparator)(struct Node, struct String, struct String);
	Option_struct Node (*parseDefinitionTypeProperty)(struct Node, struct String, List__struct String);
	Option_struct Node (*parseDefinitionWithNoTypeParams)(struct Node, struct String, struct String);
	int (*validateLeft)(struct String);
	Option_struct String (*generateDefinition)(struct Node);
	struct String (*unwrapDefault)(struct Node);
	struct Node (*wrapDefault)(struct String);
	Option_int (*findTypeSeparator)(struct String);
	List__struct String (*splitValues)(struct String);
	struct String (*generateType)(struct Node);
	Option_struct Node (*parseType)(struct String, List__struct String);
	Option_struct Node (*parseOr)(struct String, List__Function_struct String_Option_struct Node);
	List__Function_struct String_Option_struct Node (*listTypeRules)(List__struct String);
	Function_struct String_Option_struct Node (*parseGeneric)(List__struct String);
	Function_struct String_Option_struct Node (*parseWithType)(struct String, Function_struct String_Option_struct Node);
	Function_struct String_Option_struct Node (*wrapDefaultFunction)(Function_struct String_Option_struct String);
	Option_struct String (*compilePrimitive)(struct String);
	Option_struct String (*compileArray)(struct String, List__struct String);
	Option_struct String (*compileSymbol)(struct String, List__struct String);
	int (*isSymbol)(struct String);
	Option_struct String (*generatePlaceholder)(struct String);
};
struct List__char {
	List__struct T (*add)(struct T);
	List__struct T (*addAll)(List__struct T);
	Iterator_struct T (*iter)();
	Option_Tuple_struct T_List__struct T (*popFirst)();
	struct T (*pop)();
	int (*isEmpty)();
	struct T (*peek)();
	int (*size)();
	List__struct T (*slice)(int, int);
	struct T (*get)(int);
};
struct Option_int {
	Option_R (*map)(Function_struct T_struct R);
	struct T (*orElse)(struct T);
	int (*isPresent)();
	int (*isEmpty)();
	void (*ifPresent)(Consumer_struct T);
	Option_struct T (*or)(Supplier_Option_struct T);
	Option_R (*flatMap)(Function_struct T_Option_struct R);
	struct T (*orElseGet)(Supplier_struct T);
	Option_struct T (*filter)(Predicate_struct T);
	Option_Tuple_struct T_R (*and)(Supplier_Option_struct R);
};
struct Iterator_T {
	R (*fold)(struct R, BiFunction_struct R_struct T_struct R);
	Iterator_R (*map)(Function_struct T_struct R);
	C (*collect)(Collector_struct T_struct C);
	int (*anyMatch)(Predicate_struct T);
	void (*forEach)(Consumer_struct T);
	Iterator_struct T (*filter)(Predicate_struct T);
	int (*allMatch)(Predicate_struct T);
	Iterator_struct T (*concat)(Iterator_struct T);
	Option_struct T (*next)();
	Iterator_R (*flatMap)(Function_struct T_Iterator_struct R);
};
struct Iterator_char {
	R (*fold)(struct R, BiFunction_struct R_struct T_struct R);
	Iterator_R (*map)(Function_struct T_struct R);
	C (*collect)(Collector_struct T_struct C);
	int (*anyMatch)(Predicate_struct T);
	void (*forEach)(Consumer_struct T);
	Iterator_struct T (*filter)(Predicate_struct T);
	int (*allMatch)(Predicate_struct T);
	Iterator_struct T (*concat)(Iterator_struct T);
	Option_struct T (*next)();
	Iterator_R (*flatMap)(Function_struct T_Iterator_struct R);
};
struct List__K {
	List__struct T (*add)(struct T);
	List__struct T (*addAll)(List__struct T);
	Iterator_struct T (*iter)();
	Option_Tuple_struct T_List__struct T (*popFirst)();
	struct T (*pop)();
	int (*isEmpty)();
	struct T (*peek)();
	int (*size)();
	List__struct T (*slice)(int, int);
	struct T (*get)(int);
};
// List__struct String
/* 

    public interface List_<T> {
        List_<T> add(T element);

        List_<T> addAll(List_<T> elements);

        Iterator<T> iter();

        Option<Tuple<T, List_<T>>> popFirst();

        T pop();

        boolean isEmpty();

        T peek();

        int size();

        List_<T> slice(int startInclusive, int endExclusive);

        T get(int index);
    } */// List__char
// Option_struct String
/* public interface Option<T> {
        <R> Option<R> map(Function<T, R> mapper);

        T orElse(T other);

        boolean isPresent();

        boolean isEmpty();

        void ifPresent(Consumer<T> consumer);

        Option<T> or(Supplier<Option<T>> supplier);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);

        T orElseGet(Supplier<T> other);

        Option<T> filter(Predicate<T> predicate);

        <R> Option<Tuple<T, R>> and(Supplier<Option<R>> supplier);
    } */// Option_int
// Iterator_T
// Iterator_char
// Iterator_Tuple_int_struct Character
/* 

    public interface Iterator<T> {
        <R> R fold(R initial, BiFunction<R, T, R> folder);

        <R> Iterator<R> map(Function<T, R> mapper);

        <C> C collect(Collector<T, C> collector);

        boolean anyMatch(Predicate<T> predicate);

        void forEach(Consumer<T> consumer);

        Iterator<T> filter(Predicate<T> predicate);

        boolean allMatch(Predicate<T> predicate);

        Iterator<T> concat(Iterator<T> other);

        Option<T> next();

        <R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper);
    } */// Option_struct T
/* public interface Option<T> {
        <R> Option<R> map(Function<T, R> mapper);

        T orElse(T other);

        boolean isPresent();

        boolean isEmpty();

        void ifPresent(Consumer<T> consumer);

        Option<T> or(Supplier<Option<T>> supplier);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);

        T orElseGet(Supplier<T> other);

        Option<T> filter(Predicate<T> predicate);

        <R> Option<Tuple<T, R>> and(Supplier<Option<R>> supplier);
    } */// List__struct Node
/* 

    public interface List_<T> {
        List_<T> add(T element);

        List_<T> addAll(List_<T> elements);

        Iterator<T> iter();

        Option<Tuple<T, List_<T>>> popFirst();

        T pop();

        boolean isEmpty();

        T peek();

        int size();

        List_<T> slice(int startInclusive, int endExclusive);

        T get(int index);
    } */// Option_List__struct Node
/* public interface Option<T> {
        <R> Option<R> map(Function<T, R> mapper);

        T orElse(T other);

        boolean isPresent();

        boolean isEmpty();

        void ifPresent(Consumer<T> consumer);

        Option<T> or(Supplier<Option<T>> supplier);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);

        T orElseGet(Supplier<T> other);

        Option<T> filter(Predicate<T> predicate);

        <R> Option<Tuple<T, R>> and(Supplier<Option<R>> supplier);
    } */// Option_struct Node
/* public interface Option<T> {
        <R> Option<R> map(Function<T, R> mapper);

        T orElse(T other);

        boolean isPresent();

        boolean isEmpty();

        void ifPresent(Consumer<T> consumer);

        Option<T> or(Supplier<Option<T>> supplier);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);

        T orElseGet(Supplier<T> other);

        Option<T> filter(Predicate<T> predicate);

        <R> Option<Tuple<T, R>> and(Supplier<Option<R>> supplier);
    } */// List__struct T
/* 

    public interface List_<T> {
        List_<T> add(T element);

        List_<T> addAll(List_<T> elements);

        Iterator<T> iter();

        Option<Tuple<T, List_<T>>> popFirst();

        T pop();

        boolean isEmpty();

        T peek();

        int size();

        List_<T> slice(int startInclusive, int endExclusive);

        T get(int index);
    } */// BiFunction_struct T_struct T_struct Boolean
// Map__struct K_struct V
/* 

    public interface Map_<K, V> {
        Map_<K, V> with(K key, V value);

        Option<V> find(K key);

        Iterator<K> iterKeys();
    } */// BiFunction_struct K_struct K_struct Boolean
// BiFunction_struct V_struct V_struct Boolean
// Map__struct String_Function_struct Node_struct String
/* 

    public interface Map_<K, V> {
        Map_<K, V> with(K key, V value);

        Option<V> find(K key);

        Iterator<K> iterKeys();
    } */// Option_struct V
/* public interface Option<T> {
        <R> Option<R> map(Function<T, R> mapper);

        T orElse(T other);

        boolean isPresent();

        boolean isEmpty();

        void ifPresent(Consumer<T> consumer);

        Option<T> or(Supplier<Option<T>> supplier);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);

        T orElseGet(Supplier<T> other);

        Option<T> filter(Predicate<T> predicate);

        <R> Option<Tuple<T, R>> and(Supplier<Option<R>> supplier);
    } */// List__K
// List__struct K
/* 

    public interface List_<T> {
        List_<T> add(T element);

        List_<T> addAll(List_<T> elements);

        Iterator<T> iter();

        Option<Tuple<T, List_<T>>> popFirst();

        T pop();

        boolean isEmpty();

        T peek();

        int size();

        List_<T> slice(int startInclusive, int endExclusive);

        T get(int index);
    } */// Option_struct IOError
/* public interface Option<T> {
        <R> Option<R> map(Function<T, R> mapper);

        T orElse(T other);

        boolean isPresent();

        boolean isEmpty();

        void ifPresent(Consumer<T> consumer);

        Option<T> or(Supplier<Option<T>> supplier);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);

        T orElseGet(Supplier<T> other);

        Option<T> filter(Predicate<T> predicate);

        <R> Option<Tuple<T, R>> and(Supplier<Option<R>> supplier);
    } */// Function_struct String_Option_struct Node
// Function_struct Node_struct String
// BiFunction_struct StringBuilder_struct String_struct StringBuilder
// BiFunction_struct State_struct Character_struct State
// List__Tuple_int_struct Character
/* 

    public interface List_<T> {
        List_<T> add(T element);

        List_<T> addAll(List_<T> elements);

        Iterator<T> iter();

        Option<Tuple<T, List_<T>>> popFirst();

        T pop();

        boolean isEmpty();

        T peek();

        int size();

        List_<T> slice(int startInclusive, int endExclusive);

        T get(int index);
    } */// Tuple_int_struct Character
/* 

    public record Tuple<A, B>(A left, B right) {
    } */// List__Function_struct String_Option_struct Node
/* 

    public interface List_<T> {
        List_<T> add(T element);

        List_<T> addAll(List_<T> elements);

        Iterator<T> iter();

        Option<Tuple<T, List_<T>>> popFirst();

        T pop();

        boolean isEmpty();

        T peek();

        int size();

        List_<T> slice(int startInclusive, int endExclusive);

        T get(int index);
    } */// Function_struct String_Option_struct String
int counter = 0;
List__struct String imports = Impl.listEmpty();
List__struct String structs = Impl.listEmpty();
List__struct String globals = Impl.listEmpty();
List__struct String methods = Impl.listEmpty();
List__struct Node expansions = Impl.listEmpty();
int counter = 0;
Map__struct String_Function_struct Node_struct String generators = Impl.mapEmpty();
struct private State() {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
struct public State() {
	this(queue, Impl.listEmpty(), struct StringBuilder(), 0);
}
struct State advance() {
	this.segments.add(this.buffer.toString());
	this.buffer = struct StringBuilder();
	return this;
}
struct State append() {
	this.buffer.append(c);
	return this;
}
int isLevel() {
	return this.depth == 0;
}
char pop() {
	return this.queue.pop();
}
int hasElements() {
	return !this.queue.isEmpty();
}
struct State exit() {
	this.depth = this.depth - 1;
	return this;
}
struct State enter() {
	this.depth = this.depth + 1;
	return this;
}
List__struct String segments() {
	return this.segments;
}
char peek() {
	return this.queue.peek();
}
Option_struct String createInitial() {
	return None_();
}
auto __lambda0__(auto inner) {
	return inner + this.delimiter + element;
}
Option_struct String fold() {
	return Some_(current.map(__lambda0__).orElse(element));
}
struct public RangeHead() {
	this.length = length;
}
Option_int next() {
	if (this.counter >= this.length) {
		return None_();
	}
	int value = this.counter;this.counter++;
	return Some_(value);
}
<T> Iterator_T empty() {
	return HeadedIterator_(EmptyHead_());
}
auto __lambda1__() {
	return struct Tuple.right()
}
Iterator_char fromString() {
	return fromStringWithIndices(string).map(__lambda1__);
}
Iterator_Tuple_int_struct Character fromStringWithIndices() {
	return HeadedIterator_(struct RangeHead(string.length())).map(index -> new Tuple<>(index, string.charAt(index)));
}
auto __lambda2__() {
	return struct SingleHead.new()
}
auto __lambda3__() {
	return struct EmptyHead.new()
}
<T> Iterator_T fromOption() {
	return HeadedIterator_(option.<Head<T>>map(__lambda2__).orElseGet(__lambda3__));
}
struct public Node() {
	this(None_(), Impl.mapEmpty(), Impl.mapEmpty(), Impl.mapEmpty());
}
struct Node withString() {
	return struct Node(this.type, this.strings.with(propertyKey, propertyValue), this.nodes, this.nodeLists);
}
struct Node withNodeList() {
	return struct Node(this.type, this.strings, this.nodes, this.nodeLists.with(propertyKey, propertyValues));
}
Option_List__struct Node findNodeList() {
	return this.nodeLists.find(propertyKey);
}
Option_struct String findString() {
	return this.strings.find(propertyKey);
}
struct Node withNode() {
	return struct Node(this.type, this.strings, this.nodes.with(propertyKey, propertyValue), this.nodeLists);
}
Option_struct Node findNode() {
	return this.nodes.find(propertyKey);
}
auto __lambda4__(auto inner) {
	return inner.equals(type);
}
int is() {
	return this.type.filter(__lambda4__).isPresent();
}
struct Node retype() {
	return struct Node(Some_(type), this.strings, this.nodes, this.nodeLists);
}
auto __lambda5__() {
	return struct String.equals()
}
auto __lambda6__() {
	return struct String.equals()
}
auto __lambda7__() {
	return struct String.equals()
}
auto __lambda8__() {
	return struct String.equals()
}
auto __lambda9__() {
	return struct Node.equals()
}
auto __lambda10__() {
	return struct String.equals()
}
auto __lambda11__() {
	return struct this.isABoolean()
}
int equalsTo() {
	int hasSameType = Options.equalsTo(this.type, other.type, __lambda5__);
	int hasSameStrings = Maps.equalsTo(this.strings, other.strings, __lambda6__, __lambda7__);
	int hasSameNodes = Maps.equalsTo(this.nodes, other.nodes, __lambda8__, __lambda9__);
	int hasSameNodeLists = Maps.equalsTo(this.nodeLists, other.nodeLists, __lambda10__, __lambda11__);
	return hasSameType && hasSameStrings && hasSameNodes && hasSameNodeLists;
}
auto __lambda12__() {
	return struct Node.equalsTo()
}
int isABoolean() {
	return Lists.equalsTo(nodeList, nodeList2, __lambda12__);
}
auto __lambda13__(auto child) {
	return equator.apply(child, element);
}
<T> int contains() {
	return list.iter().anyMatch(__lambda13__);
}
<T> int equalsTo() {
	if (first.size() != second.size()) {
		return false;
	}
	return HeadedIterator_(struct RangeHead(first.size())).allMatch(index -> {
                return equator.apply(first.get(index), second.get(index));
            });
}
auto __lambda14__() {
	return second;
}
auto __lambda15__(auto tuple) {
	return equator.apply(tuple.left, tuple.right);
}
<T> int equalsTo() {
	if (first.isEmpty() && second.isEmpty()) {
		return true;
	}
	return first.and(__lambda14__).filter(__lambda15__).isPresent();
}
auto __lambda16__(auto kList, auto key) {
	return foldUniquely(kList, key, keyEquator);
}
auto __lambda17__(auto key) {
	return entryEqualsTo(key, first, second, valueEquator);
}
<K, V> int equalsTo() {
	return first.iterKeys().concat(second.iterKeys()).fold(Impl.<K>listEmpty(), __lambda16__).iter().allMatch(__lambda17__);
}
<K, V> int entryEqualsTo() {
	Option_struct V firstOption = first.find(key);
	Option_struct V secondOption = second.find(key);
	return Options.equalsTo(firstOption, secondOption, valueEquator);
}
<K> List__K foldUniquely() {
	if (Lists.contains(kList, key, keyEquator)) {
		return kList;
	}
	else {
		return kList.add(key);
	}
}
auto __lambda18__(auto input) {
	return compileAndWrite(input, source);
}
auto __lambda19__() {
	return struct Some.new()
}
auto __lambda20__() {
	return struct IOError.display()
}
void main() {
	struct Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
	Impl.readString(source).match(__lambda18__, __lambda19__).ifPresent(__lambda20__);
}
Option_struct IOError compileAndWrite() {
	struct Path_ target = source.resolveSibling("main.c");
	struct String output = compile(input);
	return Impl.writeString(target, output);
}
auto __lambda21__() {
	return struct Main.compileRootSegment()
}
auto __lambda22__() {
	return struct Main.unwrapDefault()
}
auto __lambda23__(auto list1) {
	return list1.iter().map(__lambda22__).collect(ListCollector_());
}
auto __lambda24__(auto nodeOptionFunction) {
	return nodeOptionFunction.apply(expansion);
}
auto __lambda25__(auto expansion) {
	struct String comment = "// " + generateGeneric(expansion) + "\n";
	struct String base = generators.find(expansion.findString("base").orElse("")).map(__lambda24__).orElse("");
	return comment + base;
}
auto __lambda26__(auto list) {
	List__struct String collect = expansions.iter().map(__lambda25__).collect(ListCollector_());
	return imports.addAll(structs).addAll(collect).addAll(globals).addAll(methods).addAll(list);
}
auto __lambda27__() {
	return struct Main.mergeStatements()
}
auto __lambda28__(auto compiled) {
	return mergeAll(compiled, __lambda27__);
}
auto __lambda29__() {
	return generatePlaceholder(input);
}
struct String compile() {
	List__struct String segments = divideAllStatements(input);
	return parseAll(segments, wrapDefaultFunction(__lambda21__)).map(__lambda23__).map(__lambda26__).map(__lambda28__).or(__lambda29__).orElse("");
}
auto __lambda30__() {
	return struct Main.unwrapDefault()
}
auto __lambda31__() {
	return struct Main.mergeStatements()
}
struct String mergeAllStatements() {
	return generateAll(compiled, __lambda30__, __lambda31__);
}
Option_List__struct Node parseAllStatements() {
	return parseAll(divideAllStatements(input), rule);
}
auto __lambda32__() {
	return struct Main.divideStatementChar()
}
List__struct String divideAllStatements() {
	return divide(input, __lambda32__);
}
struct String generateAll() {
	return mergeAll(compiled.iter().map(generator).collect(ListCollector_()), merger);
}
struct String mergeAll() {
	return compiled.iter().fold(struct StringBuilder(), merger).toString();
}
auto __lambda33__() {
	return struct allCompiled.add()
}
auto __lambda34__(auto allCompiled) {
	return rule.apply(segment).map(__lambda33__);
}
auto __lambda35__(auto maybeCompiled, auto segment) {
	return maybeCompiled.flatMap(__lambda34__);
}
Option_List__struct Node parseAll() {
	return segments.iter().<Option<List_<Node>>>fold(Some_(Impl.listEmpty()), __lambda35__);
}
struct StringBuilder mergeStatements() {
	return output.append(compiled);
}
List__struct String divide() {
	List__char queue = Iterators.fromString(input).collect(ListCollector_());
	struct State state = struct State(queue);
	while (state.hasElements()) {
		char c = state.pop();
		if (c == '\'') {
			state.append(c);
			char maybeSlash = state.pop();
			state.append(maybeSlash);
			if (maybeSlash == '\\') {
				state.append(state.pop());
			}
			state.append(state.pop());
			continue;
		}
		if (c == '\"') {
			state.append(c);
			while (state.hasElements()) {
				char next = state.pop();
				state.append(next);
				if (next == '\\') {
					state.append(state.pop());
				}
				if (next == '"') {
					break;
				}
			}
			continue;
		}
		state = divider.apply(state, c);
	}
	return state.advance().segments();
}
struct State divideStatementChar() {
	struct State appended = state.append(c);
	if (c == ';' && appended.isLevel()) {
		return appended.advance();
	}
	if (c == '}' && isShallow(appended)) {
		return appended.advance().exit();
	}
	if (c == '{' || c == '(') {
		return appended.enter();
	}
	if (c == '}' || c == ')') {
		return appended.exit();
	}
	return appended;
}
int isShallow() {
	return state.depth == 1;
}
auto __lambda36__() {
	return struct String.equals()
}
Option_struct String compileRootSegment() {
	Option_struct String whitespace = compileWhitespace(input);
	if (whitespace.isPresent()) {
		return whitespace;
	}
	if (input.startsWith("package ")) {
		return Some_("");
	}
	struct String stripped = input.strip();
	if (stripped.startsWith("import ")) {
		struct String right = stripped.substring("import ".length());
		if (right.endsWith(";")) {
			struct String content = right.substring(0, right.length() - ";".length());
			List__struct String split = splitByDelimiter(content, '.');
			if (split.size() >= 3 && Lists.equalsTo(split.slice(0, 3), Impl.listOf("java", "util", "function"), __lambda36__)) {
				return Some_("");
			}
			struct String joined = split.iter().collect(struct Joiner("/")).orElse("");
			imports.add("#include \"./" + joined + "\"\n");
			return Some_("");
		}
	}
	Option_struct String maybeClass = compileToStruct(input, "class ", Impl.listEmpty());
	if (maybeClass.isPresent()) {
		return maybeClass;
	}
	return generatePlaceholder(input);
}
List__struct String splitByDelimiter() {
	List__struct String segments = Impl.listEmpty();
	struct StringBuilder buffer = struct StringBuilder();/* 
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == delimiter) {
                segments = segments.add(buffer.toString());
                buffer = new StringBuilder();
            }
            else {
                buffer.append(c);
            }
        } */
	return segments.add(buffer.toString());
}
auto __lambda37__(auto expansion) {
	return expand(input, typeParams, withName, expansion);
}
Option_struct String compileToStruct() {
	int classIndex = input.indexOf(infix);
	if (classIndex < 0) {
		return None_();
	}
	struct String afterKeyword = input.substring(classIndex + infix.length());
	int contentStart = afterKeyword.indexOf("{");
	if (contentStart < 0) {
		return None_();
	}
	struct String beforeContent = afterKeyword.substring(0, contentStart).strip();
	int implementsIndex = beforeContent.indexOf(" implements ");
	struct String beforeContent1 = implementsIndex >= /*  0
                ? beforeContent */.substring(0, implementsIndex)
                : beforeContent;
	int paramStart = beforeContent1.indexOf("(");
	struct String withoutParams = paramStart >= /*  0
                ? beforeContent1 */.substring(0, paramStart)
                : beforeContent1;
	struct String strippedWithoutParams = withoutParams.strip();
	int typeParamStart = withoutParams.indexOf("<");
	struct String body = afterKeyword.substring(contentStart + "{".length());
	struct Node withBody = struct Node(/* ) */.withString("body", body);
	if (typeParamStart >= 0) {
		struct String name = strippedWithoutParams.substring(0, typeParamStart).strip();
		struct Node withName = withBody.withString("name", name);
		generators = generators.with(name, __lambda37__);
		return Some_("// " + withoutParams + "\n");
	}
	return generateStruct(typeParams, withBody.withString("name", strippedWithoutParams));
}
auto __lambda38__() {
	return generatePlaceholder(input);
}
struct String expand() {
	struct String stringify = generateGeneric(expansion);
	return generateStruct(typeParams, withName.withString("name", stringify)).or(__lambda38__).orElse("");
}
auto __lambda39__(auto node) {
	return !node.is("whitespace");
}
auto __lambda40__() {
	return struct Main.generateGeneric()
}
struct String generateGeneric() {
	if (expansion.is("generic")) {
		struct String base = expansion.findString("base").orElse("");
		struct String typeParams = expansion.findNodeList("type-params").orElse(Impl.listEmpty()).iter().filter(__lambda39__).map(__lambda40__).collect(struct Joiner("_")).orElse("");
		return base + "_" + typeParams;
	}
	else {
		return expansion.findString("value").orElse("");
	}
}
auto __lambda41__(auto input1) {
	return compileClassMember(input1, typeParams);
}
auto __lambda42__() {
	return struct Main.mergeAllStatements()
}
auto __lambda43__(auto outputContent) {
	structs.add("struct " + name + " {\n" + outputContent + "};\n");
	return "";
}
Option_struct String generateStruct() {
	struct String name = node.findString("name").orElse("");
	struct String body = node.findString("body").orElse("");
	if (!isSymbol(name)) {
		return None_();
	}
	struct String withEnd = body.strip();
	if (!withEnd.endsWith("}")) {
		return None_();
	}
	struct String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
	return parseAllStatements(inputContent, wrapDefaultFunction(__lambda41__)).map(__lambda42__).map(__lambda43__);
}
auto __lambda44__() {
	return compileToStruct(input, "interface ", typeParams);
}
auto __lambda45__() {
	return compileToStruct(input, "record ", typeParams);
}
auto __lambda46__() {
	return compileToStruct(input, "class ", typeParams);
}
auto __lambda47__() {
	return compileGlobalInitialization(input, typeParams);
}
auto __lambda48__() {
	return compileDefinitionStatement(input);
}
auto __lambda49__() {
	return compileMethod(input, typeParams);
}
auto __lambda50__() {
	return generatePlaceholder(input);
}
Option_struct String compileClassMember() {
	return compileWhitespace(input).or(__lambda44__).or(__lambda45__).or(__lambda46__).or(__lambda47__).or(__lambda48__).or(__lambda49__).or(__lambda50__);
}
auto __lambda51__() {
	return struct Main.generateDefinition()
}
auto __lambda52__(auto result) {
	return "\t" + result + ";\n";
}
Option_struct String compileDefinitionStatement() {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String content = stripped.substring(0, stripped.length() - ";".length());
		return parseDefinition(content).flatMap(__lambda51__).map(__lambda52__);
	}
	return None_();
}
auto __lambda53__(auto generated) {
	globals.add(generated + ";\n");
	return "";
}
Option_struct String compileGlobalInitialization() {
	return compileInitialization(input, typeParams, 0).map(__lambda53__);
}
auto __lambda54__() {
	return struct Main.generateDefinition()
}
auto __lambda55__(auto outputValue) {
	return outputDefinition + " = " + outputValue;
}
auto __lambda56__(auto outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda55__);
}
Option_struct String compileInitialization() {
	if (!input.endsWith(";")) {
		return None_();
	}
	struct String withoutEnd = input.substring(0, input.length() - ";".length());
	int valueSeparator = withoutEnd.indexOf("=");
	if (valueSeparator < 0) {
		return None_();
	}
	struct String definition = withoutEnd.substring(0, valueSeparator).strip();
	struct String value = withoutEnd.substring(valueSeparator + "=".length()).strip();
	return parseDefinition(definition).flatMap(__lambda54__).flatMap(__lambda56__);
}
Option_struct String compileWhitespace() {
	if (input.isBlank()) {
		return Some_("");
	}
	return None_();
}
Option_struct String compileMethod() {
	int paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return None_();
	}
	struct String inputDefinition = input.substring(0, paramStart).strip();
	struct String withParams = input.substring(paramStart + "(".length());
	return parseDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return new None<>();
            }

            String params = withParams.substring(0, paramEnd);
            String body = withParams.substring(paramEnd + ")".length()).strip();
            return parseAllValues(params, createParamRule()).flatMap(outputParams -> getStringOption(typeParams, outputDefinition, outputParams, body));
        });
}
auto __lambda57__() {
	return struct Main.compileWhitespace()
}
auto __lambda58__() {
	return struct Main.parseDefinition()
}
auto __lambda59__(auto definition) {
	return parseOr(definition, Impl.listOf(wrapDefaultFunction(__lambda57__), __lambda58__));
}
Function_struct String_Option_struct Node createParamRule() {
	return __lambda59__;
}
auto __lambda60__(auto param) {
	return param.findNode("type");
}
auto __lambda61__() {
	return struct Iterators.fromOption()
}
auto __lambda62__() {
	return generateDefinition(functionalDefinition);
}
auto __lambda63__(auto input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
auto __lambda64__() {
	return struct Main.mergeAllStatements()
}
auto __lambda65__(auto outputContent) {
	methods.add("\t".repeat(0) + asContent + "(" + mergeAllValues(params, Main::unwrapDefault) + ")" + " {" + outputContent + "\n}\n");
	return Some_(entry);
}
auto __lambda66__(auto output) {
	struct String asContent = output.left;
	struct String asType = output.right;
	struct String entry = "\t" + asType + ";\n";
	if (!body.startsWith("{") || !body.endsWith("}")) {
		return Some_(entry);
	}
	struct String inputContent = body.substring("{".length(), body.length() - "}".length());
	return parseAllStatements(inputContent, wrapDefaultFunction(__lambda63__)).map(__lambda64__).flatMap(__lambda65__);
}
Option_struct String getStringOption() {
	List__struct Node paramTypes = params.iter().map(__lambda60__).flatMap(__lambda61__).collect(ListCollector_());
	struct String name = definition.findString("name").orElse("");
	struct Node returns = definition.findNode("type").orElse(struct Node());
	struct Node functionalDefinition = struct Node(/* ) */.retype("functional-definition").withString("name", /* name) */.withNode("returns", /* returns) */.withNodeList("params", paramTypes);
	return generateDefinition(definition).and(__lambda62__).flatMap(__lambda66__);
}
auto __lambda67__() {
	return struct Main.divideValueChar()
}
Option_List__struct Node parseAllValues() {
	return parseAll(divide(input, __lambda67__), rule);
}
struct State divideValueChar() {
	if (c == '-') {
		if (state.peek() == '>') {
			state.pop();
			return state.append('-').append('>');
		}
	}
	if (c == ',' && state.isLevel()) {
		return state.advance();
	}
	struct State appended = state.append(c);
	if (c == ' < ' || c == '(') {
		return appended.enter();
	}
	if (c == '>' || c == ')') {
		return appended.exit();
	}
	return appended;
}
auto __lambda68__() {
	return struct Main.mergeValues()
}
struct String mergeAllValues() {
	return generateAll(compiled, generator, __lambda68__);
}
auto __lambda69__() {
	return compileKeywordStatement(input, depth, "continue");
}
auto __lambda70__() {
	return compileKeywordStatement(input, depth, "break");
}
auto __lambda71__() {
	return compileConditional(input, typeParams, "if ", depth);
}
auto __lambda72__() {
	return compileConditional(input, typeParams, "while ", depth);
}
auto __lambda73__() {
	return compileElse(input, typeParams, depth);
}
auto __lambda74__() {
	return compilePostOperator(input, typeParams, depth, "++");
}
auto __lambda75__() {
	return compilePostOperator(input, typeParams, depth, "--");
}
auto __lambda76__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda77__() {
	return compileReturn(input, typeParams, depth).map(__lambda76__);
}
auto __lambda78__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda79__() {
	return compileInitialization(input, typeParams, depth).map(__lambda78__);
}
auto __lambda80__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda81__() {
	return compileAssignment(input, typeParams, depth).map(__lambda80__);
}
auto __lambda82__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda83__() {
	return compileInvocationStatement(input, typeParams, depth).map(__lambda82__);
}
auto __lambda84__() {
	return compileDefinitionStatement(input);
}
auto __lambda85__() {
	return generatePlaceholder(input);
}
Option_struct String compileStatementOrBlock() {
	return compileWhitespace(input).or(__lambda69__).or(__lambda70__).or(__lambda71__).or(__lambda72__).or(__lambda73__).or(__lambda74__).or(__lambda75__).or(__lambda77__).or(__lambda79__).or(__lambda81__).or(__lambda83__).or(__lambda84__).or(__lambda85__);
}
auto __lambda86__(auto value) {
	return value + operator + ";";
}
Option_struct String compilePostOperator() {
	struct String stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		struct String slice = stripped.substring(0, stripped.length() -(operator + ";").length());
		return compileValue(slice, typeParams, depth).map(__lambda86__);
	}
	else {
		return None_();
	}
}
auto __lambda87__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda88__() {
	return struct Main.mergeAllStatements()
}
auto __lambda89__(auto result) {
	return indent + "else {" + result + indent + "}";
}
auto __lambda90__(auto result) {
	return "else " + result;
}
Option_struct String compileElse() {
	struct String stripped = input.strip();
	if (stripped.startsWith("else ")) {
		struct String withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			struct String indent = createIndent(depth);
			return parseAllStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), wrapDefaultFunction(__lambda87__)).map(__lambda88__).map(__lambda89__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda90__);
		}
	}
	return None_();
}
Option_struct String compileKeywordStatement() {
	if (input.strip().equals(keyword + ";")) {
		return Some_(formatStatement(depth, keyword));
	}
	else {
		return None_();
	}
}
struct String formatStatement() {
	return createIndent(depth) + value + ";";
}
struct String createIndent() {
	return "\n" + "\t".repeat(depth);
}
auto __lambda91__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda92__() {
	return struct Main.mergeAllStatements()
}
auto __lambda93__(auto statements) {
		return withCondition + " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
}
auto __lambda94__(auto result) {
		return withCondition + " " + result;
}
auto __lambda95__(auto newCondition) {
	struct String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		struct String content = withBraces.substring(1, withBraces.length() - 1);
		return parseAllStatements(content, wrapDefaultFunction(__lambda91__)).map(__lambda92__).map(__lambda93__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda94__);
	}
}
Option_struct String compileConditional() {
	struct String stripped = input.strip();
	if (!stripped.startsWith(prefix)) {
		return None_();
	}
	struct String afterKeyword = stripped.substring(prefix.length()).strip();
	if (!afterKeyword.startsWith("(")) {
		return None_();
	}
	struct String withoutConditionStart = afterKeyword.substring(1);
	int conditionEnd = findConditionEnd(withoutConditionStart);
	if (conditionEnd < 0) {
		return None_();
	}
	struct String oldCondition = withoutConditionStart.substring(0, conditionEnd).strip();
	struct String withBraces = withoutConditionStart.substring(conditionEnd + ")".length()).strip();
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda95__);
}
int findConditionEnd() {
	int conditionEnd = -1;
	int depth0 = 0;
	List__Tuple_int_struct Character queue = Iterators.fromStringWithIndices(input).collect(ListCollector_());
	while (!queue.isEmpty()) {
		Tuple_int_struct Character pair = queue.pop();
		int i = pair.left;
		char c = pair.right;
		if (c == '\'') {
			if (queue.pop().right == '\\') {
				queue.pop();
			}
			queue.pop();
			continue;
		}
		if (c == '"') {
			while (!queue.isEmpty()) {
				Tuple_int_struct Character next = queue.pop();
				if (next.right == '\\') {
					queue.pop();
				}
				if (next.right == '"') {
					break;
				}
			}
			continue;
		}
		if (c == ')' && depth0 == 0) {
			conditionEnd = i;
			break;
		}
		if (c == '(') {depth0++;
		}
		if (c == ')') {depth0--;
		}
	}
	return conditionEnd;
}
Option_struct String compileInvocationStatement() {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		Option_struct String maybeInvocation = compileInvocation(withoutEnd, typeParams, depth);
		if (maybeInvocation.isPresent()) {
			return maybeInvocation;
		}
	}
	return None_();
}
auto __lambda96__(auto newSource) {
			return newDest + " = " + newSource;
}
auto __lambda97__(auto newDest) {
			return compileValue(source, typeParams, depth).map(__lambda96__);
}
Option_struct String compileAssignment() {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			struct String destination = withoutEnd.substring(0, valueSeparator).strip();
			struct String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda97__);
		}
	}
	return None_();
}
auto __lambda98__(auto result) {
	return "return " + result;
}
Option_struct String compileReturn() {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda98__);
		}
	}
	return None_();
}
auto __lambda99__() {
	return struct Main.generateType()
}
auto __lambda100__(auto value) {
	return outputType + value;
}
auto __lambda101__(auto outputType) {
	return compileArgs(argsString, typeParams, depth).map(__lambda100__);
}
auto __lambda102__(auto result) {
	return "!" + result;
}
auto __lambda103__() {
	return struct Main.generateType()
}
auto __lambda104__(auto compiled) {
			return generateLambdaWithReturn(Impl.listEmpty(), "\n\treturn " + compiled + "." + property + "()");
}
auto __lambda105__(auto compiled) {
	return compiled + "." + property;
}
auto __lambda106__() {
	return compileOperator(input, typeParams, depth, "<");
}
auto __lambda107__() {
	return compileOperator(input, typeParams, depth, "+");
}
auto __lambda108__() {
	return compileOperator(input, typeParams, depth, ">=");
}
auto __lambda109__() {
	return compileOperator(input, typeParams, depth, "&&");
}
auto __lambda110__() {
	return compileOperator(input, typeParams, depth, "==");
}
auto __lambda111__() {
	return compileOperator(input, typeParams, depth, "!=");
}
auto __lambda112__() {
	return generatePlaceholder(input);
}
Option_struct String compileValue() {
	struct String stripped = input.strip();
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return Some_(stripped);
	}
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return Some_(stripped);
	}
	if (isSymbol(stripped) || isNumber(stripped)) {
		return Some_(stripped);
	}
	if (stripped.startsWith("new ")) {
		struct String slice = stripped.substring("new ".length());
		int argsStart = slice.indexOf("(");
		if (argsStart >= 0) {
			struct String type = slice.substring(0, argsStart);
			struct String withEnd = slice.substring(argsStart + "(".length()).strip();
			if (withEnd.endsWith(")")) {
				struct String argsString = withEnd.substring(0, withEnd.length() - ")".length());
				return parseType(type, typeParams).map(__lambda99__).flatMap(__lambda101__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda102__);
	}
	Option_struct String value = compileLambda(stripped, typeParams, depth);
	if (value.isPresent()) {
		return value;
	}
	Option_struct String invocation = compileInvocation(input, typeParams, depth);
	if (invocation.isPresent()) {
		return invocation;
	}
	int methodIndex = stripped.lastIndexOf("::");
	if (methodIndex >= 0) {
		struct String type = stripped.substring(0, methodIndex).strip();
		struct String property = stripped.substring(methodIndex + "::".length()).strip();
		if (isSymbol(property)) {
			return parseType(type, typeParams).map(__lambda103__).flatMap(__lambda104__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		struct String object = input.substring(0, separator).strip();
		struct String property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda105__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda106__).or(__lambda107__).or(__lambda108__).or(__lambda109__).or(__lambda110__).or(__lambda111__).or(__lambda112__);
}
auto __lambda113__(auto rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
auto __lambda114__(auto leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda113__);
}
Option_struct String compileOperator() {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return None_();
	}
	struct String left = input.substring(0, operatorIndex);
	struct String right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda114__);
}
auto __lambda115__() {
	return struct String.strip()
}
auto __lambda116__(auto value) {
	return !value.isEmpty();
}
auto __lambda117__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
auto __lambda118__() {
	return struct Main.mergeAllStatements()
}
auto __lambda119__(auto result) {
		return generateLambdaWithReturn(paramNames, result);
}
auto __lambda120__(auto newValue) {
	return generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";");
}
Option_struct String compileLambda() {
	int arrowIndex = input.indexOf("->");
	if (arrowIndex < 0) {
		return None_();
	}
	struct String beforeArrow = input.substring(0, arrowIndex).strip();	List__struct String paramNames;

	if (isSymbol(beforeArrow)) {
		paramNames = Impl.listOf(beforeArrow);
	}else 
	if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
		struct String inner = beforeArrow.substring(1, beforeArrow.length() - 1);
		paramNames = splitByDelimiter(inner, ',').iter().map(__lambda115__).filter(__lambda116__).collect(ListCollector_());
	}
	else {
		return None_();
	}
	struct String value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		struct String slice = value.substring(1, value.length() - 1);
		return parseAllStatements(slice, wrapDefaultFunction(__lambda117__)).map(__lambda118__).flatMap(__lambda119__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda120__);
}
auto __lambda121__(auto name) {
	return "auto " + name;
}
Option_struct String generateLambdaWithReturn() {
	int current = counter;counter++;
	struct String lambdaName = "__lambda" + current + "__";
	struct String joinedLambdaParams = paramNames.iter().map(__lambda121__).collect(struct Joiner(", ")).orElse("");
	methods.add("auto " + lambdaName + "(" + joinedLambdaParams + ")" + " {" + returnValue + "\n}\n");
	return Some_(lambdaName);
}
auto __lambda122__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return (index == 0 && c == '-') || Character.isDigit(c);
}
int isNumber() {
	return Iterators.fromStringWithIndices(input).allMatch(__lambda122__);
}
auto __lambda123__(auto value) {
	return caller + value;
}
auto __lambda124__(auto caller) {
			return compileArgs(withEnd, typeParams, depth).map(__lambda123__);
}
Option_struct String compileInvocation() {
	struct String stripped = input.strip();
	if (stripped.endsWith(")")) {
		struct String sliced = stripped.substring(0, stripped.length() - ")".length());
		int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			struct String type = sliced.substring(0, argsStart);
			struct String withEnd = sliced.substring(argsStart + "(".length()).strip();
			return compileValue(type, typeParams, depth).flatMap(__lambda124__);
		}
	}
	return None_();
}
int findInvocationStart() {
	int argsStart = -1;
	int depth0 = 0;
	int i = sliced.length() - 1;
	while (i >= 0) {
		char c = sliced.charAt(i);
		if (c == '(' && depth0 == 0) {
			argsStart = i;
			break;
		}
		if (c == ')') {depth0++;
		}
		if (c == '(') {depth0--;
		}i--;
	}
	return argsStart;
}
auto __lambda125__() {
	return compileValue(arg, typeParams, depth);
}
auto __lambda126__(auto arg) {
	return compileWhitespace(arg).or(__lambda125__);
}
auto __lambda127__() {
	return struct Main.unwrapDefault()
}
auto __lambda128__(auto compiled) {
	return mergeAllValues(compiled, __lambda127__);
}
auto __lambda129__(auto args) {
	return "(" + args + ")";
}
Option_struct String compileArgs() {
	return parseAllValues(argsString, wrapDefaultFunction(__lambda126__)).map(__lambda128__).map(__lambda129__);
}
struct StringBuilder mergeValues() {
	if (cache.isEmpty()) {
		return cache.append(element);
	}
	return cache.append(", ").append(element);
}
Option_struct Node parseDefinition() {
	struct String stripped = definition.strip();
	int nameSeparator = stripped.lastIndexOf(" ");
	if (nameSeparator < 0) {
		return None_();
	}
	struct String beforeName = stripped.substring(0, nameSeparator).strip();
	struct String name = stripped.substring(nameSeparator + " ".length()).strip();
	if (!isSymbol(name)) {
		return None_();
	}
	struct Node withName = struct Node(/* ) */.withString("name", name);
	return parseDefinitionWithName(beforeName, withName);
}
auto __lambda130__(auto typeSeparator) {
	struct String beforeType = beforeName.substring(0, typeSeparator).strip();
	struct String type = beforeName.substring(typeSeparator + " ".length());
	return parseDefinitionWithTypeSeparator(withName, beforeType, type);
}
auto __lambda131__() {
	return parseDefinitionTypeProperty(withName, beforeName, Impl.listEmpty());
}
Option_struct Node parseDefinitionWithName() {
	return findTypeSeparator(beforeName).map(__lambda130__).orElseGet(__lambda131__);
}
auto __lambda132__() {
	return struct Main.wrapDefault()
}
auto __lambda133__(auto node) {
	return node.withNodeList("type-params", typeParamsNodes);
}
Option_struct Node parseDefinitionWithTypeSeparator() {
	if (!beforeType.endsWith(">")) {
		return parseDefinitionWithNoTypeParams(withName, beforeType, type);
	}
	struct String withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
	int typeParamStart = withoutEnd.indexOf("<");
	if (typeParamStart < 0) {
		return parseDefinitionWithNoTypeParams(withName, beforeType, type);
	}
	struct String beforeTypeParams = withoutEnd.substring(0, typeParamStart);
	struct String substring = withoutEnd.substring(typeParamStart + 1);
	List__struct String typeParamsStrings = splitValues(substring);
	List__struct Node typeParamsNodes = typeParamsStrings.iter().map(__lambda132__).collect(ListCollector_());
	int hasValidBeforeParams = validateLeft(beforeTypeParams);
	if (!hasValidBeforeParams) {
		return None_();
	}
	return parseDefinitionTypeProperty(withName, type, typeParamsStrings).map(__lambda133__);
}
auto __lambda134__(auto outputType) {
	return withName.withNode("type", outputType);
}
Option_struct Node parseDefinitionTypeProperty() {
	return parseType(type, typeParams).map(__lambda134__);
}
auto __lambda135__(auto node) {
	return node.withNodeList("type-params", typeParamsList);
}
Option_struct Node parseDefinitionWithNoTypeParams() {
	int hasValidBeforeParams = validateLeft(beforeType);
	List__struct Node typeParamsList = Impl.listEmpty();
	if (!hasValidBeforeParams) {
		return None_();
	}
	return parseDefinitionTypeProperty(withName, type, Impl.listEmpty()).map(__lambda135__);
}
auto __lambda136__() {
	return struct String.strip()
}
auto __lambda137__(auto value) {
	return !value.isEmpty();
}
auto __lambda138__() {
	return struct Main.isSymbol()
}
int validateLeft() {
	struct String strippedBeforeTypeParams = beforeTypeParams.strip();	struct String modifiersString;

	int annotationSeparator = strippedBeforeTypeParams.lastIndexOf("\n");
	if (annotationSeparator >= 0) {
		modifiersString = strippedBeforeTypeParams.substring(annotationSeparator + "\n".length());
	}
	else {
		modifiersString = strippedBeforeTypeParams;
	}
	return splitByDelimiter(modifiersString, ' ').iter().map(__lambda136__).filter(__lambda137__).allMatch(__lambda138__);
}
auto __lambda139__() {
	return struct Impl.listEmpty()
}
auto __lambda140__() {
	return struct Main.generateType()
}
auto __lambda141__() {
	return struct Impl.listEmpty()
}
auto __lambda142__() {
	return struct Main.unwrapDefault()
}
auto __lambda143__(auto inner) {
	return "<" + inner + "> ";
}
auto __lambda144__() {
	return struct Main.generateType()
}
Option_struct String generateDefinition() {
	if (node.is("functional-definition")) {
		struct String name = node.findString("name").orElse("");
		struct String returns = generateType(node.findNode("returns").orElse(struct Node()));
		struct String params = node.findNodeList("params").orElseGet(__lambda139__).iter().map(__lambda140__).collect(struct Joiner(", ")).orElse("");
		return Some_(returns + " (*" + name + ")(" + params + ")");
	}
	struct String typeParamsString = node.findNodeList("type-params").orElseGet(__lambda141__).iter().map(__lambda142__).collect(struct Joiner(", ")).map(__lambda143__).orElse("");
	struct String type = node.findNode("type").map(__lambda144__).orElse("");
	struct String name = node.findString("name").orElse("name");
	return Some_(typeParamsString + type + " " + name);
}
struct String unwrapDefault() {
	return value.findString("value").orElse("");
}
struct Node wrapDefault() {
	return struct Node(/* ) */.withString("value", typeParam);
}
Option_int findTypeSeparator() {
	int depth = 0;
	int index = beforeName.length() - 1;
	while (index >= 0) {
		char c = beforeName.charAt(index);
		if (c == ' ' && depth == 0) {
			return Some_(index);
		}
		else {
			if (c == '>') {depth++;
			}
			if (c == ' < ') {depth--;
			}
		}index--;
	}
	return None_();
}
auto __lambda145__() {
	return struct String.strip()
}
auto __lambda146__(auto param) {
	return !param.isEmpty();
}
List__struct String splitValues() {
	return splitByDelimiter(substring.strip(), ',').iter().map(__lambda145__).filter(__lambda146__).collect(ListCollector_());
}
auto __lambda147__() {
	return struct Node.equalsTo()
}
auto __lambda148__(auto param) {
	return !param.is("whitespace");
}
struct String generateType() {
	if (node.is("generic")) {
		if (!Lists.contains(expansions, node, __lambda147__)) {
			List__struct Node params = node.findNodeList("type-params").orElse(Impl.listEmpty()).iter().filter(__lambda148__).collect(ListCollector_());
			if (!params.isEmpty()) {
				expansions = expansions.add(node);
			}
		}
		return generateGeneric(node);
	}
	return unwrapDefault(node);
}
Option_struct Node parseType() {
	return parseOr(input, listTypeRules(typeParams));
}
auto __lambda149__(auto function) {
	return function.apply(input);
}
auto __lambda150__() {
	return struct Iterators.fromOption()
}
Option_struct Node parseOr() {
	return rules.iter().map(__lambda149__).flatMap(__lambda150__).next();
}
auto __lambda151__() {
	return struct Main.compilePrimitive()
}
auto __lambda152__(auto input) {
	return compileArray(input, typeParams);
}
auto __lambda153__(auto input) {
	return compileSymbol(input, typeParams);
}
List__Function_struct String_Option_struct Node listTypeRules() {
	return Impl.listOf(wrapDefaultFunction(__lambda151__), wrapDefaultFunction(__lambda152__), wrapDefaultFunction(__lambda153__), parseGeneric(typeParams));
}
Function_struct String_Option_struct Node parseGeneric() {/* 
        return input -> {
            String stripped = input.strip();
            if (!stripped.endsWith(">")) {
                return new None<>();
            }

            String slice = stripped.substring(0, stripped.length() - ">".length());
            int argsStart = slice.indexOf("<");
            if (argsStart < 0) {
                return new None<>();
            }

            String base = slice.substring(0, argsStart).strip();
            String params = slice.substring(argsStart + "<".length()).strip();

            Option<List_<Node>> listOption = parseAllValues(params, inner -> {
                return parseOr(inner, Impl.listOf(
                        parseWithType("whitespace", wrapDefaultFunction(Main::compileWhitespace)),
                        input0 -> parseType(input0, typeParams)
                ));
            });

            return listOption.map(compiled -> {
                return new Node()
                        .retype("generic")
                        .withNodeList("type-params", compiled).withString("base", base);
            });
        } *//* ; */
}
auto __lambda154__(auto value) {
	return value.retype(type);
}
auto __lambda155__(auto input) {
	return mapper.apply(input).map(__lambda154__);
}
Function_struct String_Option_struct Node parseWithType() {
	return __lambda155__;
}
auto __lambda156__() {
	return struct Main.wrapDefault()
}
auto __lambda157__(auto input) {
	return mapper.apply(input).map(__lambda156__);
}
Function_struct String_Option_struct Node wrapDefaultFunction() {
	return __lambda157__;
}
Option_struct String compilePrimitive() {
	if (input.equals("void")) {
		return Some_("void");
	}
	if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
		return Some_("int");
	}
	if (input.equals("char") || input.equals("Character")) {
		return Some_("char");
	}
	return None_();
}
auto __lambda158__() {
	return struct Main.generateType()
}
auto __lambda159__(auto value) {
	return value + "*";
}
Option_struct String compileArray() {
	if (input.endsWith("[]")) {
		return parseType(input.substring(0, input.length() - "[]".length()), typeParams).map(__lambda158__).map(__lambda159__);
	}
	return None_();
}
auto __lambda160__() {
	return struct String.equals()
}
Option_struct String compileSymbol() {
	struct String stripped = input.strip();
	if (!isSymbol(stripped)) {
		return None_();
	}
	if (Lists.contains(typeParams, stripped, __lambda160__)) {
		return Some_(stripped);
	}
	else {
		return Some_("struct " + stripped);
	}
}
auto __lambda161__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return c == '_' || Character.isLetter(c) ||(index != 0 && Character.isDigit(c));
}
int isSymbol() {
	if (input.isBlank()) {
		return false;
	}
	return Iterators.fromStringWithIndices(input).allMatch(__lambda161__);
}
Option_struct String generatePlaceholder() {
	return Some_("/* " + input + " */");
}
