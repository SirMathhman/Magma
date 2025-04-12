typedef struct IOError IOError;
typedef struct Path_ Path_;
typedef struct String_ String_;
typedef struct Node Node;
typedef struct State State;
typedef struct Joiner Joiner;
typedef struct RangeHead RangeHead;
typedef struct Iterators Iterators;
typedef struct MapNode MapNode;
typedef struct Lists Lists;
typedef struct Options Options;
typedef struct Maps Maps;
typedef struct Main Main;
typedef struct List__Node List__Node;
typedef struct Option_List__Node Option_List__Node;
typedef struct Option_Node Option_Node;
typedef struct List__char List__char;
typedef struct Option_int Option_int;
typedef struct Iterator_T Iterator_T;
typedef struct Iterator_char Iterator_char;
typedef struct Iterator_Tuple_int_Character Iterator_Tuple_int_Character;
typedef struct Option_T Option_T;
typedef struct List__T List__T;
typedef struct Map__K_V Map__K_V;
typedef struct Option_V Option_V;
typedef struct List__K List__K;
typedef struct Option_IOError Option_IOError;
typedef struct List__Tuple_int_Character List__Tuple_int_Character;
typedef struct Tuple_int_Character Tuple_int_Character;
// List__char*
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
    } */// List__Node
// Option_List__Node
// Option_char*
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
    } */// Option_Node
// Map__char*_List__Node
/* 

    public interface Map_<K, V> {
        Map_<K, V> with(K key, V value);

        Option<V> find(K key);

        Iterator<K> iterKeys();
    } */// Map__char*_Node
/* 

    public interface Map_<K, V> {
        Map_<K, V> with(K key, V value);

        Option<V> find(K key);

        Iterator<K> iterKeys();
    } */// Map__char*_String
/* 

    public interface Map_<K, V> {
        Map_<K, V> with(K key, V value);

        Option<V> find(K key);

        Iterator<K> iterKeys();
    } */// List__char
// Option_int
// Iterator_T
// Iterator_char
// Iterator_Tuple_int_Character
// Option_T
// List__T
// BiFunction_T_T_Boolean
// Map__K_V
// BiFunction_K_K_Boolean
// BiFunction_V_V_Boolean
// Map__char*_Function_Node_String
/* 

    public interface Map_<K, V> {
        Map_<K, V> with(K key, V value);

        Option<V> find(K key);

        Iterator<K> iterKeys();
    } */// Option_V
// List__K
// Option_IOError
// Function_char*_Option_Node
// Function_Node_String
// BiFunction_StringBuilder_String_StringBuilder
// BiFunction_State_Character_State
// List__Tuple_int_Character
// Tuple_int_Character
// List__Function_char*_Option_Node
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
    } */// Function_char*_Option_char*
struct IOError {
	String_ (*display)();
};
struct Path_ {
	Path_ (*resolveSibling)(String_);
	List__char* (*listNames)();
};
struct String_ {
	char* (*toCharArray)();
};
struct Node {
	Node (*withString)(char*, char*);
	Node (*withNodeList)(char*, List__Node);
	Option_List__Node (*findNodeList)(char*);
	Option_char* (*findString)(char*);
	Node (*withNode)(char*, Node);
	Option_Node (*findNode)(char*);
	int (*is)(char*);
	Node (*retype)(char*);
	int (*equalsTo)(Node);
	int (*hasSameNodeLists)(Node, Map__char*_List__Node);
	int (*hasSameNodes)(Map__char*_Node);
	int (*hasSameStrings)(Map__char*_String);
	int (*hasSameTypes)(Option_char*);
};
struct State {
	List__char queue;
	List__char* segments;
	StringBuilder buffer;
	int depth;
	private (*State)(List__char, List__char*, StringBuilder, int);
	public (*State)(List__char);
	State (*advance)();
	State (*append)(char);
	int (*isLevel)();
	char (*pop)();
	int (*hasElements)();
	State (*exit)();
	State (*enter)();
	List__char* (*segments)();
	char (*peek)();
};
struct Joiner {
	Option_char* (*createInitial)();
	Option_char* (*fold)(Option_char*, char*);
};
struct RangeHead {
	int length;
	public (*RangeHead)(int);
	Option_int (*next)();
};
struct Iterators {
	Iterator_T (*empty)();
	Iterator_char (*fromString)(char*);
	Iterator_Tuple_int_Character (*fromStringWithIndices)(char*);
	Iterator_T (*fromOption)(Option_T);
};
struct MapNode {
	public (*MapNode)();
	int (*isABoolean)(List__Node, List__Node);
	Node (*withString)(char*, char*);
	Node (*withNodeList)(char*, List__Node);
	Option_List__Node (*findNodeList)(char*);
	Option_char* (*findString)(char*);
	Node (*withNode)(char*, Node);
	Option_Node (*findNode)(char*);
	int (*is)(char*);
	Node (*retype)(char*);
	int (*equalsTo)(Node);
	int (*hasSameNodeLists)(Node, Map__char*_List__Node);
	int (*hasSameNodes)(Map__char*_Node);
	int (*hasSameStrings)(Map__char*_String);
	int (*hasSameTypes)(Option_char*);
};
struct Lists {
	int (*contains)(List__T, T, BiFunction_T_T_Boolean);
	int (*equalsTo)(List__T, List__T, BiFunction_T_T_Boolean);
};
struct Options {
	int (*equalsTo)(Option_T, Option_T, BiFunction_T_T_Boolean);
};
struct Maps {
	int (*equalsTo)(Map__K_V, Map__K_V, BiFunction_K_K_Boolean, BiFunction_V_V_Boolean);
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
	int (*entryEqualsTo)(K, Map__K_V, Map__K_V, BiFunction_V_V_Boolean);
	List__K (*foldUniquely)(List__K, K, BiFunction_K_K_Boolean);
	void (*main)(char**);
	Option_IOError (*compileAndWrite)(char*, Path_);
	char* (*compile)(char*);
	List__char* (*getStringList)(List__char*);
	char* (*getString)(Node);
	char* (*mergeAllStatements)(List__Node);
	Option_List__Node (*parseAllStatements)(char*, Function_char*_Option_Node);
	List__char* (*divideAllStatements)(char*);
	char* (*generateAll)(List__Node, Function_Node_String, BiFunction_StringBuilder_String_StringBuilder);
	char* (*mergeAll)(List__char*, BiFunction_StringBuilder_String_StringBuilder);
	Option_List__Node (*parseAll)(List__char*, Function_char*_Option_Node);
	StringBuilder (*mergeStatements)(StringBuilder, char*);
	List__char* (*divide)(char*, BiFunction_State_Character_State);
	State (*divideStatementChar)(State, char);
	int (*isShallow)(State);
	Option_char* (*compileRootSegment)(char*);
	List__char* (*splitByDelimiter)(char*, char);
	Option_char* (*compileToStruct)(char*, char*, List__char*);
	char* (*expand)(char*, List__char*, Node, Node);
	char* (*stringify)(Node);
	Option_char* (*generateStruct)(List__char*, Node);
	Option_char* (*compileClassMember)(char*, List__char*);
	Option_char* (*compileDefinitionStatement)(char*);
	Option_char* (*compileGlobalInitialization)(char*, List__char*);
	Option_char* (*compileInitialization)(char*, List__char*, int);
	Option_char* (*compileWhitespace)(char*);
	Option_char* (*compileMethod)(char*, List__char*);
	Function_char*_Option_Node (*createParamRule)();
	Option_char* (*getStringOption)(List__char*, Node, List__Node, char*);
	Option_List__Node (*parseAllValues)(char*, Function_char*_Option_Node);
	State (*divideValueChar)(State, char);
	char* (*mergeAllValues)(List__Node, Function_Node_String);
	Option_char* (*compileStatementOrBlock)(char*, List__char*, int);
	Option_char* (*compilePostOperator)(char*, List__char*, int, char*);
	Option_char* (*compileElse)(char*, List__char*, int);
	Option_char* (*compileKeywordStatement)(char*, int, char*);
	char* (*formatStatement)(int, char*);
	char* (*createIndent)(int);
	Option_char* (*compileConditional)(char*, List__char*, char*, int);
	int (*findConditionEnd)(char*);
	Option_char* (*compileInvocationStatement)(char*, List__char*, int);
	Option_char* (*compileAssignment)(char*, List__char*, int);
	Option_char* (*compileReturn)(char*, List__char*, int);
	Option_char* (*compileValue)(char*, List__char*, int);
	Option_char* (*compileOperator)(char*, List__char*, int, char*);
	Option_char* (*compileLambda)(char*, List__char*, int);
	Option_char* (*generateLambdaWithReturn)(List__char*, char*);
	int (*isNumber)(char*);
	Option_char* (*compileInvocation)(char*, List__char*, int);
	int (*findInvocationStart)(char*);
	Option_char* (*compileArgs)(char*, List__char*, int);
	StringBuilder (*mergeValues)(StringBuilder, char*);
	Option_Node (*parseDefinition)(char*);
	Option_Node (*parseDefinitionWithName)(char*, Node);
	Option_Node (*parseDefinitionWithTypeSeparator)(Node, char*, char*);
	Option_Node (*parseDefinitionTypeProperty)(Node, char*, List__char*);
	Option_Node (*parseDefinitionWithNoTypeParams)(Node, char*, char*);
	int (*validateLeft)(char*);
	Option_char* (*generateDefinition)(Node);
	char* (*unwrapDefault)(Node);
	Node (*wrapDefault)(char*);
	Option_int (*findTypeSeparator)(char*);
	List__char* (*splitValues)(char*);
	char* (*generateType)(Node);
	Option_Node (*parseType)(char*, List__char*);
	Option_Node (*parseOr)(char*, List__Function_char*_Option_Node);
	List__Function_char*_Option_Node (*listTypeRules)(List__char*);
	Function_char*_Option_Node (*parseGeneric)(List__char*);
	Function_char*_Option_Node (*parseWithType)(char*, Function_char*_Option_Node);
	char* (*generateGeneric)(Node);
	Function_char*_Option_Node (*wrapDefaultFunction)(Function_char*_Option_char*);
	Option_char* (*compilePrimitive)(char*);
	Option_char* (*compileArray)(char*, List__char*);
	Option_char* (*compileSymbol)(char*, List__char*);
	int (*isSymbol)(char*);
	Option_char* (*generatePlaceholder)(char*);
};
struct List__Node {
	List__T (*add)(T);
	List__T (*addAll)(List__T);
	Iterator_T (*iter)();
	Option_Tuple_T_List__T (*popFirst)();
	T (*pop)();
	int (*isEmpty)();
	T (*peek)();
	int (*size)();
	List__T (*slice)(int, int);
	T (*get)(int);
};
struct Option_List__Node {
	Option_R (*map)(Function_T_R);
	T (*orElse)(T);
	int (*isPresent)();
	int (*isEmpty)();
	void (*ifPresent)(Consumer_T);
	Option_T (*or)(Supplier_Option_T);
	Option_R (*flatMap)(Function_T_Option_R);
	T (*orElseGet)(Supplier_T);
	Option_T (*filter)(Predicate_T);
	Option_Tuple_T_R (*and)(Supplier_Option_R);
};
struct Option_Node {
	Option_R (*map)(Function_T_R);
	T (*orElse)(T);
	int (*isPresent)();
	int (*isEmpty)();
	void (*ifPresent)(Consumer_T);
	Option_T (*or)(Supplier_Option_T);
	Option_R (*flatMap)(Function_T_Option_R);
	T (*orElseGet)(Supplier_T);
	Option_T (*filter)(Predicate_T);
	Option_Tuple_T_R (*and)(Supplier_Option_R);
};
struct List__char {
	List__T (*add)(T);
	List__T (*addAll)(List__T);
	Iterator_T (*iter)();
	Option_Tuple_T_List__T (*popFirst)();
	T (*pop)();
	int (*isEmpty)();
	T (*peek)();
	int (*size)();
	List__T (*slice)(int, int);
	T (*get)(int);
};
struct Option_int {
	Option_R (*map)(Function_T_R);
	T (*orElse)(T);
	int (*isPresent)();
	int (*isEmpty)();
	void (*ifPresent)(Consumer_T);
	Option_T (*or)(Supplier_Option_T);
	Option_R (*flatMap)(Function_T_Option_R);
	T (*orElseGet)(Supplier_T);
	Option_T (*filter)(Predicate_T);
	Option_Tuple_T_R (*and)(Supplier_Option_R);
};
struct Iterator_T {
	R (*fold)(R, BiFunction_R_T_R);
	Iterator_R (*map)(Function_T_R);
	C (*collect)(Collector_T_C);
	int (*anyMatch)(Predicate_T);
	void (*forEach)(Consumer_T);
	Iterator_T (*filter)(Predicate_T);
	int (*allMatch)(Predicate_T);
	Iterator_T (*concat)(Iterator_T);
	Option_T (*next)();
	Iterator_R (*flatMap)(Function_T_Iterator_R);
};
struct Iterator_char {
	R (*fold)(R, BiFunction_R_T_R);
	Iterator_R (*map)(Function_T_R);
	C (*collect)(Collector_T_C);
	int (*anyMatch)(Predicate_T);
	void (*forEach)(Consumer_T);
	Iterator_T (*filter)(Predicate_T);
	int (*allMatch)(Predicate_T);
	Iterator_T (*concat)(Iterator_T);
	Option_T (*next)();
	Iterator_R (*flatMap)(Function_T_Iterator_R);
};
struct Iterator_Tuple_int_Character {
	R (*fold)(R, BiFunction_R_T_R);
	Iterator_R (*map)(Function_T_R);
	C (*collect)(Collector_T_C);
	int (*anyMatch)(Predicate_T);
	void (*forEach)(Consumer_T);
	Iterator_T (*filter)(Predicate_T);
	int (*allMatch)(Predicate_T);
	Iterator_T (*concat)(Iterator_T);
	Option_T (*next)();
	Iterator_R (*flatMap)(Function_T_Iterator_R);
};
struct Option_T {
	Option_R (*map)(Function_T_R);
	T (*orElse)(T);
	int (*isPresent)();
	int (*isEmpty)();
	void (*ifPresent)(Consumer_T);
	Option_T (*or)(Supplier_Option_T);
	Option_R (*flatMap)(Function_T_Option_R);
	T (*orElseGet)(Supplier_T);
	Option_T (*filter)(Predicate_T);
	Option_Tuple_T_R (*and)(Supplier_Option_R);
};
struct List__T {
	List__T (*add)(T);
	List__T (*addAll)(List__T);
	Iterator_T (*iter)();
	Option_Tuple_T_List__T (*popFirst)();
	T (*pop)();
	int (*isEmpty)();
	T (*peek)();
	int (*size)();
	List__T (*slice)(int, int);
	T (*get)(int);
};
struct Map__K_V {
	Map__K_V (*with)(K, V);
	Option_V (*find)(K);
	Iterator_K (*iterKeys)();
};
struct Option_V {
	Option_R (*map)(Function_T_R);
	T (*orElse)(T);
	int (*isPresent)();
	int (*isEmpty)();
	void (*ifPresent)(Consumer_T);
	Option_T (*or)(Supplier_Option_T);
	Option_R (*flatMap)(Function_T_Option_R);
	T (*orElseGet)(Supplier_T);
	Option_T (*filter)(Predicate_T);
	Option_Tuple_T_R (*and)(Supplier_Option_R);
};
struct List__K {
	List__T (*add)(T);
	List__T (*addAll)(List__T);
	Iterator_T (*iter)();
	Option_Tuple_T_List__T (*popFirst)();
	T (*pop)();
	int (*isEmpty)();
	T (*peek)();
	int (*size)();
	List__T (*slice)(int, int);
	T (*get)(int);
};
struct Option_IOError {
	Option_R (*map)(Function_T_R);
	T (*orElse)(T);
	int (*isPresent)();
	int (*isEmpty)();
	void (*ifPresent)(Consumer_T);
	Option_T (*or)(Supplier_Option_T);
	Option_R (*flatMap)(Function_T_Option_R);
	T (*orElseGet)(Supplier_T);
	Option_T (*filter)(Predicate_T);
	Option_Tuple_T_R (*and)(Supplier_Option_R);
};
struct List__Tuple_int_Character {
	List__T (*add)(T);
	List__T (*addAll)(List__T);
	Iterator_T (*iter)();
	Option_Tuple_T_List__T (*popFirst)();
	T (*pop)();
	int (*isEmpty)();
	T (*peek)();
	int (*size)();
	List__T (*slice)(int, int);
	T (*get)(int);
};
struct Tuple_int_Character {
};
int counter = 0;
List__char* imports = Impl.listEmpty();
List__char* globals = Impl.listEmpty();
List__char* methods = Impl.listEmpty();
List__char* structsForwarders = Impl.listEmpty();
List__char* structs = Impl.listEmpty();
List__Node expansions = Impl.listEmpty();
int counter = 0;
Map__char*_Function_Node_String generators = Impl.mapEmpty();
private State() {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
public State() {
	this(queue, Impl.listEmpty(), StringBuilder(), 0);
}
State advance() {
	this.segments.add(this.buffer.toString());
	this.buffer = StringBuilder();
	return this;
}
State append() {
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
State exit() {
	this.depth = this.depth - 1;
	return this;
}
State enter() {
	this.depth = this.depth + 1;
	return this;
}
List__char* segments() {
	return this.segments;
}
char peek() {
	return this.queue.peek();
}
Option_char* createInitial() {
	return None_();
}
auto __lambda0__(auto inner) {
	return inner + this.delimiter + element;
}
Option_char* fold() {
	return Some_(current.map(__lambda0__).orElse(element));
}
public RangeHead() {
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
	return Tuple.right()
}
Iterator_char fromString() {
	return fromStringWithIndices(string).map(__lambda1__);
}
Iterator_Tuple_int_Character fromStringWithIndices() {
	return HeadedIterator_(RangeHead(string.length())).map(index -> new Tuple<>(index, string.charAt(index)));
}
auto __lambda2__() {
	return SingleHead.new()
}
auto __lambda3__() {
	return EmptyHead.new()
}
<T> Iterator_T fromOption() {
	return HeadedIterator_(option.<Head<T>>map(__lambda2__).orElseGet(__lambda3__));
}
public MapNode() {
	this(None_(), Impl.mapEmpty(), Impl.mapEmpty(), Impl.mapEmpty());
}
auto __lambda4__() {
	return Node.equalsTo()
}
int isABoolean() {
	return Lists.equalsTo(nodeList, nodeList2, __lambda4__);
}
Node withString() {
	return MapNode(this.type, this.strings.with(propertyKey, propertyValue), this.nodes, this.nodeLists);
}
Node withNodeList() {
	return MapNode(this.type, this.strings, this.nodes, this.nodeLists.with(propertyKey, propertyValues));
}
Option_List__Node findNodeList() {
	return this.nodeLists.find(propertyKey);
}
Option_char* findString() {
	return this.strings.find(propertyKey);
}
Node withNode() {
	return MapNode(this.type, this.strings, this.nodes.with(propertyKey, propertyValue), this.nodeLists);
}
Option_Node findNode() {
	return this.nodes.find(propertyKey);
}
auto __lambda5__(auto inner) {
	return inner.equals(type);
}
int is() {
	return this.type.filter(__lambda5__).isPresent();
}
Node retype() {
	return MapNode(Some_(type), this.strings, this.nodes, this.nodeLists);
}
int equalsTo() {
	int hasSameType = other.hasSameTypes(this.type);
	int hasSameStrings = other.hasSameStrings(this.strings);
	int hasSameNodes = other.hasSameNodes(this.nodes);
	int hasSameNodeLists = other.hasSameNodeLists(this, this.nodeLists);
	return hasSameType && hasSameStrings && hasSameNodes && hasSameNodeLists;
}
auto __lambda6__() {
	return char*.equals()
}
auto __lambda7__() {
	return MapNode.isABoolean()
}
int hasSameNodeLists() {
	return Maps.equalsTo(this.nodeLists, nodeLists, __lambda6__, __lambda7__);
}
auto __lambda8__() {
	return char*.equals()
}
auto __lambda9__() {
	return Node.equals()
}
int hasSameNodes() {
	return Maps.equalsTo(this.nodes, nodes, __lambda8__, __lambda9__);
}
auto __lambda10__() {
	return char*.equals()
}
auto __lambda11__() {
	return char*.equals()
}
int hasSameStrings() {
	return Maps.equalsTo(this.strings, strings, __lambda10__, __lambda11__);
}
auto __lambda12__() {
	return char*.equals()
}
int hasSameTypes() {
	return Options.equalsTo(this.type, type, __lambda12__);
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
	return HeadedIterator_(RangeHead(first.size())).allMatch(index -> {
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
	Option_V firstOption = first.find(key);
	Option_V secondOption = second.find(key);
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
	return Some.new()
}
auto __lambda20__(auto ioError) {
	return System.err.println(Impl.toNativeString(ioError.display()));
}
void main() {
	Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
	Impl.readString(source).match(__lambda18__, __lambda19__).ifPresent(__lambda20__);
}
Option_IOError compileAndWrite() {
	Path_ target = source.resolveSibling(Impl.fromNativeString("main.c"));
	char* output = compile(input);
	return Impl.writeString(target, output);
}
auto __lambda21__() {
	return Main.compileRootSegment()
}
auto __lambda22__() {
	return Main.unwrapDefault()
}
auto __lambda23__(auto list1) {
	return list1.iter().map(__lambda22__).collect(ListCollector_());
}
auto __lambda24__() {
	return Main.getStringList()
}
auto __lambda25__() {
	return Main.mergeStatements()
}
auto __lambda26__(auto compiled) {
	return mergeAll(compiled, __lambda25__);
}
auto __lambda27__() {
	return generatePlaceholder(input);
}
char* compile() {
	List__char* segments = divideAllStatements(input);
	return parseAll(segments, wrapDefaultFunction(__lambda21__)).map(__lambda23__).map(__lambda24__).map(__lambda26__).or(__lambda27__).orElse("");
}
auto __lambda28__() {
	return Main.getString()
}
List__char* getStringList() {
	List__char* expandedStructs = expansions.iter().map(__lambda28__).collect(ListCollector_());
	return imports.addAll(structsForwarders).addAll(expandedStructs).addAll(structs).addAll(globals).addAll(methods).addAll(list);
}
auto __lambda29__(auto nodeOptionFunction) {
	return nodeOptionFunction.apply(expansion);
}
char* getString() {
	char* comment = "// " + generateGeneric(expansion) + "\n";
	char* base = generators.find(expansion.findString("base").orElse("")).map(__lambda29__).orElse("");
	return comment + base;
}
auto __lambda30__() {
	return Main.unwrapDefault()
}
auto __lambda31__() {
	return Main.mergeStatements()
}
char* mergeAllStatements() {
	return generateAll(compiled, __lambda30__, __lambda31__);
}
Option_List__Node parseAllStatements() {
	return parseAll(divideAllStatements(input), rule);
}
auto __lambda32__() {
	return Main.divideStatementChar()
}
List__char* divideAllStatements() {
	return divide(input, __lambda32__);
}
char* generateAll() {
	return mergeAll(compiled.iter().map(generator).collect(ListCollector_()), merger);
}
char* mergeAll() {
	return compiled.iter().fold(StringBuilder(), merger).toString();
}
auto __lambda33__() {
	return allCompiled.add()
}
auto __lambda34__(auto allCompiled) {
	return rule.apply(segment).map(__lambda33__);
}
auto __lambda35__(auto maybeCompiled, auto segment) {
	return maybeCompiled.flatMap(__lambda34__);
}
Option_List__Node parseAll() {
	return segments.iter().<Option<List_<Node>>>fold(Some_(Impl.listEmpty()), __lambda35__);
}
StringBuilder mergeStatements() {
	return output.append(compiled);
}
List__char* divide() {
	List__char queue = Iterators.fromString(input).collect(ListCollector_());
	State state = State(queue);
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
State divideStatementChar() {
	State appended = state.append(c);
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
	return char*.equals()
}
Option_char* compileRootSegment() {
	Option_char* whitespace = compileWhitespace(input);
	if (whitespace.isPresent()) {
		return whitespace;
	}
	if (input.startsWith("package ")) {
		return Some_("");
	}
	char* stripped = input.strip();
	if (stripped.startsWith("import ")) {
		char* right = stripped.substring("import ".length());
		if (right.endsWith(";")) {
			char* content = right.substring(0, right.length() - ";".length());
			List__char* split = splitByDelimiter(content, '.');
			if (split.size() >= 3 && Lists.equalsTo(split.slice(0, 3), Impl.listOf("java", "util", "function"), __lambda36__)) {
				return Some_("");
			}
			char* joined = split.iter().collect(Joiner("/")).orElse("");
			imports.add("#include \"./" + joined + "\"\n");
			return Some_("");
		}
	}
	Option_char* maybeClass = compileToStruct(input, "class ", Impl.listEmpty());
	if (maybeClass.isPresent()) {
		return maybeClass;
	}
	return generatePlaceholder(input);
}
List__char* splitByDelimiter() {
	List__char* segments = Impl.listEmpty();
	StringBuilder buffer = StringBuilder();/* 
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
Option_char* compileToStruct() {
	int classIndex = input.indexOf(infix);
	if (classIndex < 0) {
		return None_();
	}
	char* afterKeyword = input.substring(classIndex + infix.length());
	int contentStart = afterKeyword.indexOf("{");
	if (contentStart < 0) {
		return None_();
	}
	char* beforeContent = afterKeyword.substring(0, contentStart).strip();
	int implementsIndex = beforeContent.indexOf(" implements ");
	char* beforeContent1 = implementsIndex >= /*  0
                ? beforeContent */.substring(0, implementsIndex)
                : beforeContent;
	int paramStart = beforeContent1.indexOf("(");
	char* withoutParams = paramStart >= /*  0
                ? beforeContent1 */.substring(0, paramStart)
                : beforeContent1;
	char* strippedWithoutParams = withoutParams.strip();
	int typeParamStart = withoutParams.indexOf("<");
	char* body = afterKeyword.substring(contentStart + "{".length());
	Node withBody = MapNode(/* ) */.withString("body", body);
	if (typeParamStart >= 0) {
		char* name = strippedWithoutParams.substring(0, typeParamStart).strip();
		Node withName = withBody.withString("name", name);
		generators = generators.with(name, __lambda37__);
		return Some_("// " + withoutParams + "\n");
	}
	return generateStruct(typeParams, withBody.withString("name", strippedWithoutParams));
}
auto __lambda38__() {
	return generatePlaceholder(input);
}
char* expand() {
	char* stringify = stringify(expansion);
	return generateStruct(typeParams, withName.withString("name", stringify)).or(__lambda38__).orElse("");
}
auto __lambda39__(auto node) {
	return !node.is("whitespace");
}
auto __lambda40__() {
	return Main.stringify()
}
char* stringify() {
	if (expansion.is("generic")) {
		char* base = expansion.findString("base").orElse("");
		char* typeParams = expansion.findNodeList("type-params").orElse(Impl.listEmpty()).iter().filter(__lambda39__).map(__lambda40__).collect(Joiner("_")).orElse("");
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
	return Main.mergeAllStatements()
}
auto __lambda43__(auto outputContent) {
	structsForwarders = structsForwarders.add("typedef struct " + name + " " + name + ";\n");
	structs = structs.add("struct %s {\n%s};\n".formatted(name, outputContent));
	return "";
}
Option_char* generateStruct() {
	char* name = node.findString("name").orElse("");
	char* body = node.findString("body").orElse("");
	if (!isSymbol(name)) {
		return None_();
	}
	char* withEnd = body.strip();
	if (!withEnd.endsWith("}")) {
		return None_();
	}
	char* inputContent = withEnd.substring(0, withEnd.length() - "}".length());
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
Option_char* compileClassMember() {
	return compileWhitespace(input).or(__lambda44__).or(__lambda45__).or(__lambda46__).or(__lambda47__).or(__lambda48__).or(__lambda49__).or(__lambda50__);
}
auto __lambda51__() {
	return Main.generateDefinition()
}
auto __lambda52__(auto result) {
	return "\t" + result + ";\n";
}
Option_char* compileDefinitionStatement() {
	char* stripped = input.strip();
	if (stripped.endsWith(";")) {
		char* content = stripped.substring(0, stripped.length() - ";".length());
		return parseDefinition(content).flatMap(__lambda51__).map(__lambda52__);
	}
	return None_();
}
auto __lambda53__(auto generated) {
	globals.add(generated + ";\n");
	return "";
}
Option_char* compileGlobalInitialization() {
	return compileInitialization(input, typeParams, 0).map(__lambda53__);
}
auto __lambda54__() {
	return Main.generateDefinition()
}
auto __lambda55__(auto outputValue) {
	return outputDefinition + " = " + outputValue;
}
auto __lambda56__(auto outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda55__);
}
Option_char* compileInitialization() {
	if (!input.endsWith(";")) {
		return None_();
	}
	char* withoutEnd = input.substring(0, input.length() - ";".length());
	int valueSeparator = withoutEnd.indexOf("=");
	if (valueSeparator < 0) {
		return None_();
	}
	char* definition = withoutEnd.substring(0, valueSeparator).strip();
	char* value = withoutEnd.substring(valueSeparator + "=".length()).strip();
	return parseDefinition(definition).flatMap(__lambda54__).flatMap(__lambda56__);
}
Option_char* compileWhitespace() {
	if (input.isBlank()) {
		return Some_("");
	}
	return None_();
}
Option_char* compileMethod() {
	int paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return None_();
	}
	char* inputDefinition = input.substring(0, paramStart).strip();
	char* withParams = input.substring(paramStart + "(".length());
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
	return Main.compileWhitespace()
}
auto __lambda58__() {
	return Main.parseDefinition()
}
auto __lambda59__(auto definition) {
	return parseOr(definition, Impl.listOf(wrapDefaultFunction(__lambda57__), __lambda58__));
}
Function_char*_Option_Node createParamRule() {
	return __lambda59__;
}
auto __lambda60__(auto param) {
	return param.findNode("type");
}
auto __lambda61__() {
	return Iterators.fromOption()
}
auto __lambda62__() {
	return generateDefinition(functionalDefinition);
}
auto __lambda63__(auto input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
auto __lambda64__() {
	return Main.mergeAllStatements()
}
auto __lambda65__(auto outputContent) {
	methods.add("\t".repeat(0) + asContent + "(" + mergeAllValues(params, Main::unwrapDefault) + ")" + " {" + outputContent + "\n}\n");
	return Some_(entry);
}
auto __lambda66__(auto output) {
	char* asContent = output.left;
	char* asType = output.right;
	char* entry = "\t" + asType + ";\n";
	if (!body.startsWith("{") || !body.endsWith("}")) {
		return Some_(entry);
	}
	char* inputContent = body.substring("{".length(), body.length() - "}".length());
	return parseAllStatements(inputContent, wrapDefaultFunction(__lambda63__)).map(__lambda64__).flatMap(__lambda65__);
}
Option_char* getStringOption() {
	List__Node paramTypes = params.iter().map(__lambda60__).flatMap(__lambda61__).collect(ListCollector_());
	char* name = definition.findString("name").orElse("");
	Node returns = definition.findNode("type").orElse(MapNode());
	Node functionalDefinition = MapNode(/* ) */.retype("functional-definition").withString("name", /* name) */.withNode("returns", /* returns) */.withNodeList("params", paramTypes);
	return generateDefinition(definition).and(__lambda62__).flatMap(__lambda66__);
}
auto __lambda67__() {
	return Main.divideValueChar()
}
Option_List__Node parseAllValues() {
	return parseAll(divide(input, __lambda67__), rule);
}
State divideValueChar() {
	if (c == '-') {
		if (state.peek() == '>') {
			state.pop();
			return state.append('-').append('>');
		}
	}
	if (c == ',' && state.isLevel()) {
		return state.advance();
	}
	State appended = state.append(c);
	if (c == ' < ' || c == '(') {
		return appended.enter();
	}
	if (c == '>' || c == ')') {
		return appended.exit();
	}
	return appended;
}
auto __lambda68__() {
	return Main.mergeValues()
}
char* mergeAllValues() {
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
Option_char* compileStatementOrBlock() {
	return compileWhitespace(input).or(__lambda69__).or(__lambda70__).or(__lambda71__).or(__lambda72__).or(__lambda73__).or(__lambda74__).or(__lambda75__).or(__lambda77__).or(__lambda79__).or(__lambda81__).or(__lambda83__).or(__lambda84__).or(__lambda85__);
}
auto __lambda86__(auto value) {
	return value + operator + ";";
}
Option_char* compilePostOperator() {
	char* stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		char* slice = stripped.substring(0, stripped.length() -(operator + ";").length());
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
	return Main.mergeAllStatements()
}
auto __lambda89__(auto result) {
	return indent + "else {" + result + indent + "}";
}
auto __lambda90__(auto result) {
	return "else " + result;
}
Option_char* compileElse() {
	char* stripped = input.strip();
	if (stripped.startsWith("else ")) {
		char* withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			char* indent = createIndent(depth);
			return parseAllStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), wrapDefaultFunction(__lambda87__)).map(__lambda88__).map(__lambda89__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda90__);
		}
	}
	return None_();
}
Option_char* compileKeywordStatement() {
	if (input.strip().equals(keyword + ";")) {
		return Some_(formatStatement(depth, keyword));
	}
	else {
		return None_();
	}
}
char* formatStatement() {
	return createIndent(depth) + value + ";";
}
char* createIndent() {
	return "\n" + "\t".repeat(depth);
}
auto __lambda91__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda92__() {
	return Main.mergeAllStatements()
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
	char* withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		char* content = withBraces.substring(1, withBraces.length() - 1);
		return parseAllStatements(content, wrapDefaultFunction(__lambda91__)).map(__lambda92__).map(__lambda93__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda94__);
	}
}
Option_char* compileConditional() {
	char* stripped = input.strip();
	if (!stripped.startsWith(prefix)) {
		return None_();
	}
	char* afterKeyword = stripped.substring(prefix.length()).strip();
	if (!afterKeyword.startsWith("(")) {
		return None_();
	}
	char* withoutConditionStart = afterKeyword.substring(1);
	int conditionEnd = findConditionEnd(withoutConditionStart);
	if (conditionEnd < 0) {
		return None_();
	}
	char* oldCondition = withoutConditionStart.substring(0, conditionEnd).strip();
	char* withBraces = withoutConditionStart.substring(conditionEnd + ")".length()).strip();
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda95__);
}
int findConditionEnd() {
	int conditionEnd = -1;
	int depth0 = 0;
	List__Tuple_int_Character queue = Iterators.fromStringWithIndices(input).collect(ListCollector_());
	while (!queue.isEmpty()) {
		Tuple_int_Character pair = queue.pop();
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
				Tuple_int_Character next = queue.pop();
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
Option_char* compileInvocationStatement() {
	char* stripped = input.strip();
	if (stripped.endsWith(";")) {
		char* withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		Option_char* maybeInvocation = compileInvocation(withoutEnd, typeParams, depth);
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
Option_char* compileAssignment() {
	char* stripped = input.strip();
	if (stripped.endsWith(";")) {
		char* withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			char* destination = withoutEnd.substring(0, valueSeparator).strip();
			char* source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda97__);
		}
	}
	return None_();
}
auto __lambda98__(auto result) {
	return "return " + result;
}
Option_char* compileReturn() {
	char* stripped = input.strip();
	if (stripped.endsWith(";")) {
		char* withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda98__);
		}
	}
	return None_();
}
auto __lambda99__() {
	return Main.generateType()
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
	return Main.generateType()
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
Option_char* compileValue() {
	char* stripped = input.strip();
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
		char* slice = stripped.substring("new ".length());
		int argsStart = slice.indexOf("(");
		if (argsStart >= 0) {
			char* type = slice.substring(0, argsStart);
			char* withEnd = slice.substring(argsStart + "(".length()).strip();
			if (withEnd.endsWith(")")) {
				char* argsString = withEnd.substring(0, withEnd.length() - ")".length());
				return parseType(type, typeParams).map(__lambda99__).flatMap(__lambda101__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda102__);
	}
	Option_char* value = compileLambda(stripped, typeParams, depth);
	if (value.isPresent()) {
		return value;
	}
	Option_char* invocation = compileInvocation(input, typeParams, depth);
	if (invocation.isPresent()) {
		return invocation;
	}
	int methodIndex = stripped.lastIndexOf("::");
	if (methodIndex >= 0) {
		char* type = stripped.substring(0, methodIndex).strip();
		char* property = stripped.substring(methodIndex + "::".length()).strip();
		if (isSymbol(property)) {
			return parseType(type, typeParams).map(__lambda103__).flatMap(__lambda104__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		char* object = input.substring(0, separator).strip();
		char* property = input.substring(separator + ".".length()).strip();
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
Option_char* compileOperator() {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return None_();
	}
	char* left = input.substring(0, operatorIndex);
	char* right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda114__);
}
auto __lambda115__() {
	return char*.strip()
}
auto __lambda116__(auto value) {
	return !value.isEmpty();
}
auto __lambda117__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
auto __lambda118__() {
	return Main.mergeAllStatements()
}
auto __lambda119__(auto result) {
		return generateLambdaWithReturn(paramNames, result);
}
auto __lambda120__(auto newValue) {
	return generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";");
}
Option_char* compileLambda() {
	int arrowIndex = input.indexOf("->");
	if (arrowIndex < 0) {
		return None_();
	}
	char* beforeArrow = input.substring(0, arrowIndex).strip();	List__char* paramNames;

	if (isSymbol(beforeArrow)) {
		paramNames = Impl.listOf(beforeArrow);
	}else 
	if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
		char* inner = beforeArrow.substring(1, beforeArrow.length() - 1);
		paramNames = splitByDelimiter(inner, ',').iter().map(__lambda115__).filter(__lambda116__).collect(ListCollector_());
	}
	else {
		return None_();
	}
	char* value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		char* slice = value.substring(1, value.length() - 1);
		return parseAllStatements(slice, wrapDefaultFunction(__lambda117__)).map(__lambda118__).flatMap(__lambda119__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda120__);
}
auto __lambda121__(auto name) {
	return "auto " + name;
}
Option_char* generateLambdaWithReturn() {
	int current = counter;counter++;
	char* lambdaName = "__lambda" + current + "__";
	char* joinedLambdaParams = paramNames.iter().map(__lambda121__).collect(Joiner(", ")).orElse("");
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
Option_char* compileInvocation() {
	char* stripped = input.strip();
	if (stripped.endsWith(")")) {
		char* sliced = stripped.substring(0, stripped.length() - ")".length());
		int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			char* type = sliced.substring(0, argsStart);
			char* withEnd = sliced.substring(argsStart + "(".length()).strip();
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
	return Main.unwrapDefault()
}
auto __lambda128__(auto compiled) {
	return mergeAllValues(compiled, __lambda127__);
}
auto __lambda129__(auto args) {
	return "(" + args + ")";
}
Option_char* compileArgs() {
	return parseAllValues(argsString, wrapDefaultFunction(__lambda126__)).map(__lambda128__).map(__lambda129__);
}
StringBuilder mergeValues() {
	if (cache.isEmpty()) {
		return cache.append(element);
	}
	return cache.append(", ").append(element);
}
Option_Node parseDefinition() {
	char* stripped = definition.strip();
	int nameSeparator = stripped.lastIndexOf(" ");
	if (nameSeparator < 0) {
		return None_();
	}
	char* beforeName = stripped.substring(0, nameSeparator).strip();
	char* name = stripped.substring(nameSeparator + " ".length()).strip();
	if (!isSymbol(name)) {
		return None_();
	}
	Node withName = MapNode(/* ) */.withString("name", name);
	return parseDefinitionWithName(beforeName, withName);
}
auto __lambda130__(auto typeSeparator) {
	char* beforeType = beforeName.substring(0, typeSeparator).strip();
	char* type = beforeName.substring(typeSeparator + " ".length());
	return parseDefinitionWithTypeSeparator(withName, beforeType, type);
}
auto __lambda131__() {
	return parseDefinitionTypeProperty(withName, beforeName, Impl.listEmpty());
}
Option_Node parseDefinitionWithName() {
	return findTypeSeparator(beforeName).map(__lambda130__).orElseGet(__lambda131__);
}
auto __lambda132__() {
	return Main.wrapDefault()
}
auto __lambda133__(auto node) {
	return node.withNodeList("type-params", typeParamsNodes);
}
Option_Node parseDefinitionWithTypeSeparator() {
	if (!beforeType.endsWith(">")) {
		return parseDefinitionWithNoTypeParams(withName, beforeType, type);
	}
	char* withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
	int typeParamStart = withoutEnd.indexOf("<");
	if (typeParamStart < 0) {
		return parseDefinitionWithNoTypeParams(withName, beforeType, type);
	}
	char* beforeTypeParams = withoutEnd.substring(0, typeParamStart);
	char* substring = withoutEnd.substring(typeParamStart + 1);
	List__char* typeParamsStrings = splitValues(substring);
	List__Node typeParamsNodes = typeParamsStrings.iter().map(__lambda132__).collect(ListCollector_());
	int hasValidBeforeParams = validateLeft(beforeTypeParams);
	if (!hasValidBeforeParams) {
		return None_();
	}
	return parseDefinitionTypeProperty(withName, type, typeParamsStrings).map(__lambda133__);
}
auto __lambda134__(auto outputType) {
	return withName.withNode("type", outputType);
}
Option_Node parseDefinitionTypeProperty() {
	return parseType(type, typeParams).map(__lambda134__);
}
auto __lambda135__(auto node) {
	return node.withNodeList("type-params", typeParamsList);
}
Option_Node parseDefinitionWithNoTypeParams() {
	int hasValidBeforeParams = validateLeft(beforeType);
	List__Node typeParamsList = Impl.listEmpty();
	if (!hasValidBeforeParams) {
		return None_();
	}
	return parseDefinitionTypeProperty(withName, type, Impl.listEmpty()).map(__lambda135__);
}
auto __lambda136__() {
	return char*.strip()
}
auto __lambda137__(auto value) {
	return !value.isEmpty();
}
auto __lambda138__() {
	return Main.isSymbol()
}
int validateLeft() {
	char* strippedBeforeTypeParams = beforeTypeParams.strip();	char* modifiersString;

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
	return Impl.listEmpty()
}
auto __lambda140__() {
	return Main.generateType()
}
auto __lambda141__() {
	return Impl.listEmpty()
}
auto __lambda142__() {
	return Main.unwrapDefault()
}
auto __lambda143__(auto inner) {
	return "<" + inner + "> ";
}
auto __lambda144__() {
	return Main.generateType()
}
Option_char* generateDefinition() {
	if (node.is("functional-definition")) {
		char* name = node.findString("name").orElse("");
		char* returns = generateType(node.findNode("returns").orElse(MapNode()));
		char* params = node.findNodeList("params").orElseGet(__lambda139__).iter().map(__lambda140__).collect(Joiner(", ")).orElse("");
		return Some_(returns + " (*" + name + ")(" + params + ")");
	}
	char* typeParamsString = node.findNodeList("type-params").orElseGet(__lambda141__).iter().map(__lambda142__).collect(Joiner(", ")).map(__lambda143__).orElse("");
	char* type = node.findNode("type").map(__lambda144__).orElse("");
	char* name = node.findString("name").orElse("name");
	return Some_(typeParamsString + type + " " + name);
}
char* unwrapDefault() {
	return value.findString("value").orElse("");
}
Node wrapDefault() {
	return MapNode(/* ) */.withString("value", typeParam);
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
	return char*.strip()
}
auto __lambda146__(auto param) {
	return !param.isEmpty();
}
List__char* splitValues() {
	return splitByDelimiter(substring.strip(), ',').iter().map(__lambda145__).filter(__lambda146__).collect(ListCollector_());
}
auto __lambda147__() {
	return Node.equalsTo()
}
auto __lambda148__(auto param) {
	return !param.is("whitespace");
}
char* generateType() {
	if (node.is("generic")) {
		if (!Lists.contains(expansions, node, __lambda147__)) {
			List__Node params = node.findNodeList("type-params").orElse(Impl.listEmpty()).iter().filter(__lambda148__).collect(ListCollector_());
			if (!params.isEmpty()) {
				expansions = expansions.add(node);
			}
		}
		return generateGeneric(node);
	}
	return unwrapDefault(node);
}
Option_Node parseType() {
	return parseOr(input, listTypeRules(typeParams));
}
auto __lambda149__(auto function) {
	return function.apply(input);
}
auto __lambda150__() {
	return Iterators.fromOption()
}
Option_Node parseOr() {
	return rules.iter().map(__lambda149__).flatMap(__lambda150__).next();
}
auto __lambda151__() {
	return Main.compilePrimitive()
}
auto __lambda152__(auto input) {
	return compileArray(input, typeParams);
}
auto __lambda153__(auto input) {
	return compileSymbol(input, typeParams);
}
List__Function_char*_Option_Node listTypeRules() {
	return Impl.listOf(wrapDefaultFunction(__lambda151__), wrapDefaultFunction(__lambda152__), wrapDefaultFunction(__lambda153__), parseGeneric(typeParams));
}
Function_char*_Option_Node parseGeneric() {/* 
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
                return new MapNode()
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
Function_char*_Option_Node parseWithType() {
	return __lambda155__;
}
char* generateGeneric() {
	return stringify(node);
}
auto __lambda156__() {
	return Main.wrapDefault()
}
auto __lambda157__(auto input) {
	return mapper.apply(input).map(__lambda156__);
}
Function_char*_Option_Node wrapDefaultFunction() {
	return __lambda157__;
}
Option_char* compilePrimitive() {
	if (input.equals("void")) {
		return Some_("void");
	}
	if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
		return Some_("int");
	}
	if (input.equals("char") || input.equals("Character")) {
		return Some_("char");
	}
	if (input.equals("String")) {
		return Some_("char*");
	}
	return None_();
}
auto __lambda158__() {
	return Main.generateType()
}
auto __lambda159__(auto value) {
	return value + "*";
}
Option_char* compileArray() {
	if (input.endsWith("[]")) {
		return parseType(input.substring(0, input.length() - "[]".length()), typeParams).map(__lambda158__).map(__lambda159__);
	}
	return None_();
}
auto __lambda160__() {
	return char*.equals()
}
Option_char* compileSymbol() {
	char* stripped = input.strip();
	if (!isSymbol(stripped)) {
		return None_();
	}
	if (Lists.contains(typeParams, stripped, __lambda160__)) {
		return Some_(stripped);
	}
	else {
		return Some_(stripped);
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
Option_char* generatePlaceholder() {
	return Some_("/* " + input + " */");
}
