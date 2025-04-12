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
typedef struct Map__K_V Map__K_V;
typedef struct Option_V Option_V;
typedef struct List__K List__K;
typedef struct Option_IOError Option_IOError;
typedef struct List__Tuple_int_Character List__Tuple_int_Character;
typedef struct Tuple_int_Character Tuple_int_Character;
typedef struct Option_int Option_int;
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
// Map__char*_Function_Node_String
/* 

    public interface Map_<K, V> {
        Map_<K, V> with(K key, V value);

        Option<V> find(K key);

        Iterator<K> iterKeys();
    } */// Map__K_V
// BiFunction_V_V_Boolean
// Option_V
// List__K
// BiFunction_K_K_Boolean
// Option_IOError
// Function_char*_Option_Node
// Function_Node_String
// BiFunction_StringBuilder_String_StringBuilder
// BiFunction_State_Character_State
// List__Tuple_int_Character
// Tuple_int_Character
// Option_int
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
/* 

        public State(List_<Character> queue) {
            this(queue, Impl.listEmpty(), new StringBuilder(), 0);
        } */	State (*advance)();
	State (*append)(char);
	int (*isLevel)();
	char (*pop)();
	int (*hasElements)();
	State (*exit)();
	State (*enter)();
/* 

        public List_<String> segments() {
            return this.segments;
        } *//* 

        public char peek() {
            return this.queue.peek();
        } */};
struct Joiner {
/* @Override
        public Option<String> createInitial() {
            return new None<>();
        } *//* 

        @Override
        public Option<String> fold(Option<String> current, String element) {
            return new Some<>(current.map(inner -> inner + this.delimiter + element).orElse(element));
        } */};
struct RangeHead {
	int length;
/* 

        public RangeHead(int length) {
            this.length = length;
        } *//* 

        @Override
        public Option<Integer> next() {
            if (this.counter >= this.length) {
                return new None<>();
            }

            int value = this.counter;
            this.counter++;
            return new Some<>(value);
        } */};
struct Iterators {
/* public static <T> Iterator<T> empty() {
            return new HeadedIterator<>(new EmptyHead<>());
        } *//* 

        public static Iterator<Character> fromString(String string) {
            return fromStringWithIndices(string).map(Tuple::right);
        } *//* 

        public static Iterator<Tuple<Integer, Character>> fromStringWithIndices(String string) {
            return new HeadedIterator<>(new RangeHead(string.length()))
                    .map(index -> new Tuple<>(index, string.charAt(index)));
        } *//* 

        public static <T> Iterator<T> fromOption(Option<T> option) {
            return new HeadedIterator<>(option.<Head<T>>map(SingleHead::new).orElseGet(EmptyHead::new));
        } */};
struct MapNode {
/* public MapNode() {
            this(new None<>(), Impl.mapEmpty(), Impl.mapEmpty(), Impl.mapEmpty());
        } */	int (*isABoolean)(List__Node, List__Node);
/* 

        @Override
        public Node withString(String propertyKey, String propertyValue) {
            return new MapNode(this.type, this.strings.with(propertyKey, propertyValue), this.nodes, this.nodeLists);
        } *//* 

        @Override
        public Node withNodeList(String propertyKey, List_<Node> propertyValues) {
            return new MapNode(this.type, this.strings, this.nodes, this.nodeLists.with(propertyKey, propertyValues));
        } *//* 

        @Override
        public Option<List_<Node>> findNodeList(String propertyKey) {
            return this.nodeLists.find(propertyKey);
        } *//* 

        @Override
        public Option<String> findString(String propertyKey) {
            return this.strings.find(propertyKey);
        } *//* 

        @Override
        public Node withNode(String propertyKey, Node propertyValue) {
            return new MapNode(this.type, this.strings, this.nodes.with(propertyKey, propertyValue), this.nodeLists);
        } *//* 

        @Override
        public Option<Node> findNode(String propertyKey) {
            return this.nodes.find(propertyKey);
        } *//* 

        @Override
        public boolean is(String type) {
            return this.type.filter(inner -> inner.equals(type)).isPresent();
        } *//* 

        @Override
        public Node retype(String type) {
            return new MapNode(new Some<>(type), this.strings, this.nodes, this.nodeLists);
        } *//* 

        @Override
        public boolean equalsTo(Node other) {
            boolean hasSameType = other.hasSameTypes(this.type);
            boolean hasSameStrings = other.hasSameStrings(this.strings);
            boolean hasSameNodes = other.hasSameNodes(this.nodes);
            boolean hasSameNodeLists = other.hasSameNodeLists(this, this.nodeLists);
            return hasSameType && hasSameStrings && hasSameNodes && hasSameNodeLists;
        } *//* 

        @Override
        public boolean hasSameNodeLists(Node node, Map_<String, List_<Node>> nodeLists) {
            return Maps.equalsTo(this.nodeLists, nodeLists, String::equals, MapNode::isABoolean);
        } *//* 

        @Override
        public boolean hasSameNodes(Map_<String, Node> nodes) {
            return Maps.equalsTo(this.nodes, nodes, String::equals, Node::equals);
        } *//* 

        @Override
        public boolean hasSameStrings(Map_<String, String> strings) {
            return Maps.equalsTo(this.strings, strings, String::equals, String::equals);
        } *//* 

        @Override
        public boolean hasSameTypes(Option<String> type) {
            return Options.equalsTo(this.type, type, String::equals);
        } */};
struct Lists {
/* public static <T> boolean contains(
                List_<T> list,
                T element,
                BiFunction<T, T, Boolean> equator
        ) {
            return list.iter().anyMatch(child -> equator.apply(child, element));
        } *//* 

        public static <T> boolean equalsTo(List_<T> first, List_<T> second, BiFunction<T, T, Boolean> equator) {
            if (first.size() != second.size()) {
                return false;
            }

            return new HeadedIterator<>(new RangeHead(first.size())).allMatch(index -> {
                return equator.apply(first.get(index), second.get(index));
            });
        } */};
struct Options {
/* public static <T> boolean equalsTo(Option<T> first, Option<T> second, BiFunction<T, T, Boolean> equator) {
            if (first.isEmpty() && second.isEmpty()) {
                return true;
            }

            return first.and(() -> second)
                    .filter(tuple -> equator.apply(tuple.left, tuple.right))
                    .isPresent();
        } */};
struct Maps {
/* public static <K, V> boolean equalsTo(
                Map_<K, V> first,
                Map_<K, V> second,
                BiFunction<K, K, Boolean> keyEquator,
                BiFunction<V, V, Boolean> valueEquator
        ) {
            return first.iterKeys()
                    .concat(second.iterKeys())
                    .fold(Impl.<K>listEmpty(), (kList, key) -> foldUniquely(kList, key, keyEquator))
                    .iter()
                    .allMatch(key -> entryEqualsTo(key, first, second, valueEquator));
        } */};
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
/* 

    public static void main(String[] args) {
        Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
        Impl.readString(source)
                .match(input -> compileAndWrite(input, source), Some::new)
                .ifPresent(ioError -> System.err.println(Impl.toNativeString(ioError.display())));
    } */	Option_IOError (*compileAndWrite)(char*, Path_);
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
auto __lambda0__() {
	return Node.equalsTo()
}
int isABoolean() {
	return Lists.equalsTo(nodeList, nodeList2, __lambda0__);
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
Option_IOError compileAndWrite() {
	Path_ target = source.resolveSibling(Impl.fromNativeString("main.c"));
	char* output = compile(input);
	return Impl.writeString(target, output);
}
auto __lambda1__() {
	return Main.compileRootSegment()
}
auto __lambda2__() {
	return Main.unwrapDefault()
}
auto __lambda3__(auto list1) {
	return list1.iter().map(__lambda2__).collect(ListCollector_());
}
auto __lambda4__() {
	return Main.getStringList()
}
auto __lambda5__() {
	return Main.mergeStatements()
}
auto __lambda6__(auto compiled) {
	return mergeAll(compiled, __lambda5__);
}
auto __lambda7__() {
	return generatePlaceholder(input);
}
char* compile() {
	List__char* segments = divideAllStatements(input);
	return parseAll(segments, wrapDefaultFunction(__lambda1__)).map(__lambda3__).map(__lambda4__).map(__lambda6__).or(__lambda7__).orElse("");
}
auto __lambda8__() {
	return Main.getString()
}
List__char* getStringList() {
	List__char* expandedStructs = expansions.iter().map(__lambda8__).collect(ListCollector_());
	return imports.addAll(structsForwarders).addAll(expandedStructs).addAll(structs).addAll(globals).addAll(methods).addAll(list);
}
auto __lambda9__(auto nodeOptionFunction) {
	return nodeOptionFunction.apply(expansion);
}
char* getString() {
	char* comment = "// " + generateGeneric(expansion) + "\n";
	char* base = generators.find(expansion.findString("base").orElse("")).map(__lambda9__).orElse("");
	return comment + base;
}
auto __lambda10__() {
	return Main.unwrapDefault()
}
auto __lambda11__() {
	return Main.mergeStatements()
}
char* mergeAllStatements() {
	return generateAll(compiled, __lambda10__, __lambda11__);
}
Option_List__Node parseAllStatements() {
	return parseAll(divideAllStatements(input), rule);
}
auto __lambda12__() {
	return Main.divideStatementChar()
}
List__char* divideAllStatements() {
	return divide(input, __lambda12__);
}
char* generateAll() {
	return mergeAll(compiled.iter().map(generator).collect(ListCollector_()), merger);
}
char* mergeAll() {
	return compiled.iter().fold(StringBuilder(), merger).toString();
}
auto __lambda13__() {
	return allCompiled.add()
}
auto __lambda14__(auto allCompiled) {
	return rule.apply(segment).map(__lambda13__);
}
auto __lambda15__(auto maybeCompiled, auto segment) {
	return maybeCompiled.flatMap(__lambda14__);
}
Option_List__Node parseAll() {
	return segments.iter().<Option<List_<Node>>>fold(Some_(Impl.listEmpty()), __lambda15__);
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
auto __lambda16__() {
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
			if (split.size() >= 3 && Lists.equalsTo(split.slice(0, 3), Impl.listOf("java", "util", "function"), __lambda16__)) {
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
auto __lambda17__(auto expansion) {
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
		generators = generators.with(name, __lambda17__);
		return Some_("// " + withoutParams + "\n");
	}
	return generateStruct(typeParams, withBody.withString("name", strippedWithoutParams));
}
auto __lambda18__() {
	return generatePlaceholder(input);
}
char* expand() {
	char* stringify = stringify(expansion);
	return generateStruct(typeParams, withName.withString("name", stringify)).or(__lambda18__).orElse("");
}
auto __lambda19__(auto node) {
	return !node.is("whitespace");
}
auto __lambda20__() {
	return Main.stringify()
}
char* stringify() {
	if (expansion.is("generic")) {
		char* base = expansion.findString("base").orElse("");
		char* typeParams = expansion.findNodeList("type-params").orElse(Impl.listEmpty()).iter().filter(__lambda19__).map(__lambda20__).collect(Joiner("_")).orElse("");
		return base + "_" + typeParams;
	}
	else {
		return expansion.findString("value").orElse("");
	}
}
auto __lambda21__(auto input1) {
	return compileClassMember(input1, typeParams);
}
auto __lambda22__() {
	return Main.mergeAllStatements()
}
auto __lambda23__(auto outputContent) {
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
	return parseAllStatements(inputContent, wrapDefaultFunction(__lambda21__)).map(__lambda22__).map(__lambda23__);
}
auto __lambda24__() {
	return compileToStruct(input, "interface ", typeParams);
}
auto __lambda25__() {
	return compileToStruct(input, "record ", typeParams);
}
auto __lambda26__() {
	return compileToStruct(input, "class ", typeParams);
}
auto __lambda27__() {
	return compileGlobalInitialization(input, typeParams);
}
auto __lambda28__() {
	return compileDefinitionStatement(input);
}
auto __lambda29__() {
	return compileMethod(input, typeParams);
}
auto __lambda30__() {
	return generatePlaceholder(input);
}
Option_char* compileClassMember() {
	return compileWhitespace(input).or(__lambda24__).or(__lambda25__).or(__lambda26__).or(__lambda27__).or(__lambda28__).or(__lambda29__).or(__lambda30__);
}
auto __lambda31__() {
	return Main.generateDefinition()
}
auto __lambda32__(auto result) {
	return "\t" + result + ";\n";
}
Option_char* compileDefinitionStatement() {
	char* stripped = input.strip();
	if (stripped.endsWith(";")) {
		char* content = stripped.substring(0, stripped.length() - ";".length());
		return parseDefinition(content).flatMap(__lambda31__).map(__lambda32__);
	}
	return None_();
}
auto __lambda33__(auto generated) {
	globals.add(generated + ";\n");
	return "";
}
Option_char* compileGlobalInitialization() {
	return compileInitialization(input, typeParams, 0).map(__lambda33__);
}
auto __lambda34__() {
	return Main.generateDefinition()
}
auto __lambda35__(auto outputValue) {
	return outputDefinition + " = " + outputValue;
}
auto __lambda36__(auto outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda35__);
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
	return parseDefinition(definition).flatMap(__lambda34__).flatMap(__lambda36__);
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
auto __lambda37__() {
	return Main.compileWhitespace()
}
auto __lambda38__() {
	return Main.parseDefinition()
}
auto __lambda39__(auto definition) {
	return parseOr(definition, Impl.listOf(wrapDefaultFunction(__lambda37__), __lambda38__));
}
Function_char*_Option_Node createParamRule() {
	return __lambda39__;
}
auto __lambda40__(auto param) {
	return param.findNode("type");
}
auto __lambda41__() {
	return Iterators.fromOption()
}
auto __lambda42__() {
	return generateDefinition(functionalDefinition);
}
auto __lambda43__(auto input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
auto __lambda44__() {
	return Main.mergeAllStatements()
}
auto __lambda45__(auto outputContent) {
	methods.add("\t".repeat(0) + asContent + "(" + mergeAllValues(params, Main::unwrapDefault) + ")" + " {" + outputContent + "\n}\n");
	return Some_(entry);
}
auto __lambda46__(auto output) {
	char* asContent = output.left;
	char* asType = output.right;
	char* entry = "\t" + asType + ";\n";
	if (!body.startsWith("{") || !body.endsWith("}")) {
		return Some_(entry);
	}
	char* inputContent = body.substring("{".length(), body.length() - "}".length());
	return parseAllStatements(inputContent, wrapDefaultFunction(__lambda43__)).map(__lambda44__).flatMap(__lambda45__);
}
Option_char* getStringOption() {
	List__Node paramTypes = params.iter().map(__lambda40__).flatMap(__lambda41__).collect(ListCollector_());
	char* name = definition.findString("name").orElse("");
	Node returns = definition.findNode("type").orElse(MapNode());
	Node functionalDefinition = MapNode(/* ) */.retype("functional-definition").withString("name", /* name) */.withNode("returns", /* returns) */.withNodeList("params", paramTypes);
	return generateDefinition(definition).and(__lambda42__).flatMap(__lambda46__);
}
auto __lambda47__() {
	return Main.divideValueChar()
}
Option_List__Node parseAllValues() {
	return parseAll(divide(input, __lambda47__), rule);
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
auto __lambda48__() {
	return Main.mergeValues()
}
char* mergeAllValues() {
	return generateAll(compiled, generator, __lambda48__);
}
auto __lambda49__() {
	return compileKeywordStatement(input, depth, "continue");
}
auto __lambda50__() {
	return compileKeywordStatement(input, depth, "break");
}
auto __lambda51__() {
	return compileConditional(input, typeParams, "if ", depth);
}
auto __lambda52__() {
	return compileConditional(input, typeParams, "while ", depth);
}
auto __lambda53__() {
	return compileElse(input, typeParams, depth);
}
auto __lambda54__() {
	return compilePostOperator(input, typeParams, depth, "++");
}
auto __lambda55__() {
	return compilePostOperator(input, typeParams, depth, "--");
}
auto __lambda56__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda57__() {
	return compileReturn(input, typeParams, depth).map(__lambda56__);
}
auto __lambda58__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda59__() {
	return compileInitialization(input, typeParams, depth).map(__lambda58__);
}
auto __lambda60__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda61__() {
	return compileAssignment(input, typeParams, depth).map(__lambda60__);
}
auto __lambda62__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda63__() {
	return compileInvocationStatement(input, typeParams, depth).map(__lambda62__);
}
auto __lambda64__() {
	return compileDefinitionStatement(input);
}
auto __lambda65__() {
	return generatePlaceholder(input);
}
Option_char* compileStatementOrBlock() {
	return compileWhitespace(input).or(__lambda49__).or(__lambda50__).or(__lambda51__).or(__lambda52__).or(__lambda53__).or(__lambda54__).or(__lambda55__).or(__lambda57__).or(__lambda59__).or(__lambda61__).or(__lambda63__).or(__lambda64__).or(__lambda65__);
}
auto __lambda66__(auto value) {
	return value + operator + ";";
}
Option_char* compilePostOperator() {
	char* stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		char* slice = stripped.substring(0, stripped.length() -(operator + ";").length());
		return compileValue(slice, typeParams, depth).map(__lambda66__);
	}
	else {
		return None_();
	}
}
auto __lambda67__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda68__() {
	return Main.mergeAllStatements()
}
auto __lambda69__(auto result) {
	return indent + "else {" + result + indent + "}";
}
auto __lambda70__(auto result) {
	return "else " + result;
}
Option_char* compileElse() {
	char* stripped = input.strip();
	if (stripped.startsWith("else ")) {
		char* withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			char* indent = createIndent(depth);
			return parseAllStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), wrapDefaultFunction(__lambda67__)).map(__lambda68__).map(__lambda69__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda70__);
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
auto __lambda71__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda72__() {
	return Main.mergeAllStatements()
}
auto __lambda73__(auto statements) {
		return withCondition + " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
}
auto __lambda74__(auto result) {
		return withCondition + " " + result;
}
auto __lambda75__(auto newCondition) {
	char* withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		char* content = withBraces.substring(1, withBraces.length() - 1);
		return parseAllStatements(content, wrapDefaultFunction(__lambda71__)).map(__lambda72__).map(__lambda73__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda74__);
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
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda75__);
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
auto __lambda76__(auto newSource) {
			return newDest + " = " + newSource;
}
auto __lambda77__(auto newDest) {
			return compileValue(source, typeParams, depth).map(__lambda76__);
}
Option_char* compileAssignment() {
	char* stripped = input.strip();
	if (stripped.endsWith(";")) {
		char* withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			char* destination = withoutEnd.substring(0, valueSeparator).strip();
			char* source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda77__);
		}
	}
	return None_();
}
auto __lambda78__(auto result) {
	return "return " + result;
}
Option_char* compileReturn() {
	char* stripped = input.strip();
	if (stripped.endsWith(";")) {
		char* withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda78__);
		}
	}
	return None_();
}
auto __lambda79__() {
	return Main.generateType()
}
auto __lambda80__(auto value) {
	return outputType + value;
}
auto __lambda81__(auto outputType) {
	return compileArgs(argsString, typeParams, depth).map(__lambda80__);
}
auto __lambda82__(auto result) {
	return "!" + result;
}
auto __lambda83__() {
	return Main.generateType()
}
auto __lambda84__(auto compiled) {
			return generateLambdaWithReturn(Impl.listEmpty(), "\n\treturn " + compiled + "." + property + "()");
}
auto __lambda85__(auto compiled) {
	return compiled + "." + property;
}
auto __lambda86__() {
	return compileOperator(input, typeParams, depth, "<");
}
auto __lambda87__() {
	return compileOperator(input, typeParams, depth, "+");
}
auto __lambda88__() {
	return compileOperator(input, typeParams, depth, ">=");
}
auto __lambda89__() {
	return compileOperator(input, typeParams, depth, "&&");
}
auto __lambda90__() {
	return compileOperator(input, typeParams, depth, "==");
}
auto __lambda91__() {
	return compileOperator(input, typeParams, depth, "!=");
}
auto __lambda92__() {
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
				return parseType(type, typeParams).map(__lambda79__).flatMap(__lambda81__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda82__);
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
			return parseType(type, typeParams).map(__lambda83__).flatMap(__lambda84__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		char* object = input.substring(0, separator).strip();
		char* property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda85__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda86__).or(__lambda87__).or(__lambda88__).or(__lambda89__).or(__lambda90__).or(__lambda91__).or(__lambda92__);
}
auto __lambda93__(auto rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
auto __lambda94__(auto leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda93__);
}
Option_char* compileOperator() {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return None_();
	}
	char* left = input.substring(0, operatorIndex);
	char* right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda94__);
}
auto __lambda95__() {
	return char*.strip()
}
auto __lambda96__(auto value) {
	return !value.isEmpty();
}
auto __lambda97__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
auto __lambda98__() {
	return Main.mergeAllStatements()
}
auto __lambda99__(auto result) {
		return generateLambdaWithReturn(paramNames, result);
}
auto __lambda100__(auto newValue) {
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
		paramNames = splitByDelimiter(inner, ',').iter().map(__lambda95__).filter(__lambda96__).collect(ListCollector_());
	}
	else {
		return None_();
	}
	char* value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		char* slice = value.substring(1, value.length() - 1);
		return parseAllStatements(slice, wrapDefaultFunction(__lambda97__)).map(__lambda98__).flatMap(__lambda99__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda100__);
}
auto __lambda101__(auto name) {
	return "auto " + name;
}
Option_char* generateLambdaWithReturn() {
	int current = counter;counter++;
	char* lambdaName = "__lambda" + current + "__";
	char* joinedLambdaParams = paramNames.iter().map(__lambda101__).collect(Joiner(", ")).orElse("");
	methods.add("auto " + lambdaName + "(" + joinedLambdaParams + ")" + " {" + returnValue + "\n}\n");
	return Some_(lambdaName);
}
auto __lambda102__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return (index == 0 && c == '-') || Character.isDigit(c);
}
int isNumber() {
	return Iterators.fromStringWithIndices(input).allMatch(__lambda102__);
}
auto __lambda103__(auto value) {
	return caller + value;
}
auto __lambda104__(auto caller) {
			return compileArgs(withEnd, typeParams, depth).map(__lambda103__);
}
Option_char* compileInvocation() {
	char* stripped = input.strip();
	if (stripped.endsWith(")")) {
		char* sliced = stripped.substring(0, stripped.length() - ")".length());
		int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			char* type = sliced.substring(0, argsStart);
			char* withEnd = sliced.substring(argsStart + "(".length()).strip();
			return compileValue(type, typeParams, depth).flatMap(__lambda104__);
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
auto __lambda105__() {
	return compileValue(arg, typeParams, depth);
}
auto __lambda106__(auto arg) {
	return compileWhitespace(arg).or(__lambda105__);
}
auto __lambda107__() {
	return Main.unwrapDefault()
}
auto __lambda108__(auto compiled) {
	return mergeAllValues(compiled, __lambda107__);
}
auto __lambda109__(auto args) {
	return "(" + args + ")";
}
Option_char* compileArgs() {
	return parseAllValues(argsString, wrapDefaultFunction(__lambda106__)).map(__lambda108__).map(__lambda109__);
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
auto __lambda110__(auto typeSeparator) {
	char* beforeType = beforeName.substring(0, typeSeparator).strip();
	char* type = beforeName.substring(typeSeparator + " ".length());
	return parseDefinitionWithTypeSeparator(withName, beforeType, type);
}
auto __lambda111__() {
	return parseDefinitionTypeProperty(withName, beforeName, Impl.listEmpty());
}
Option_Node parseDefinitionWithName() {
	return findTypeSeparator(beforeName).map(__lambda110__).orElseGet(__lambda111__);
}
auto __lambda112__() {
	return Main.wrapDefault()
}
auto __lambda113__(auto node) {
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
	List__Node typeParamsNodes = typeParamsStrings.iter().map(__lambda112__).collect(ListCollector_());
	int hasValidBeforeParams = validateLeft(beforeTypeParams);
	if (!hasValidBeforeParams) {
		return None_();
	}
	return parseDefinitionTypeProperty(withName, type, typeParamsStrings).map(__lambda113__);
}
auto __lambda114__(auto outputType) {
	return withName.withNode("type", outputType);
}
Option_Node parseDefinitionTypeProperty() {
	return parseType(type, typeParams).map(__lambda114__);
}
auto __lambda115__(auto node) {
	return node.withNodeList("type-params", typeParamsList);
}
Option_Node parseDefinitionWithNoTypeParams() {
	int hasValidBeforeParams = validateLeft(beforeType);
	List__Node typeParamsList = Impl.listEmpty();
	if (!hasValidBeforeParams) {
		return None_();
	}
	return parseDefinitionTypeProperty(withName, type, Impl.listEmpty()).map(__lambda115__);
}
auto __lambda116__() {
	return char*.strip()
}
auto __lambda117__(auto value) {
	return !value.isEmpty();
}
auto __lambda118__() {
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
	return splitByDelimiter(modifiersString, ' ').iter().map(__lambda116__).filter(__lambda117__).allMatch(__lambda118__);
}
auto __lambda119__() {
	return Impl.listEmpty()
}
auto __lambda120__() {
	return Main.generateType()
}
auto __lambda121__() {
	return Impl.listEmpty()
}
auto __lambda122__() {
	return Main.unwrapDefault()
}
auto __lambda123__(auto inner) {
	return "<" + inner + "> ";
}
auto __lambda124__() {
	return Main.generateType()
}
Option_char* generateDefinition() {
	if (node.is("functional-definition")) {
		char* name = node.findString("name").orElse("");
		char* returns = generateType(node.findNode("returns").orElse(MapNode()));
		char* params = node.findNodeList("params").orElseGet(__lambda119__).iter().map(__lambda120__).collect(Joiner(", ")).orElse("");
		return Some_(returns + " (*" + name + ")(" + params + ")");
	}
	char* typeParamsString = node.findNodeList("type-params").orElseGet(__lambda121__).iter().map(__lambda122__).collect(Joiner(", ")).map(__lambda123__).orElse("");
	char* type = node.findNode("type").map(__lambda124__).orElse("");
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
auto __lambda125__() {
	return char*.strip()
}
auto __lambda126__(auto param) {
	return !param.isEmpty();
}
List__char* splitValues() {
	return splitByDelimiter(substring.strip(), ',').iter().map(__lambda125__).filter(__lambda126__).collect(ListCollector_());
}
auto __lambda127__() {
	return Node.equalsTo()
}
auto __lambda128__(auto param) {
	return !param.is("whitespace");
}
char* generateType() {
	if (node.is("generic")) {
		if (!Lists.contains(expansions, node, __lambda127__)) {
			List__Node params = node.findNodeList("type-params").orElse(Impl.listEmpty()).iter().filter(__lambda128__).collect(ListCollector_());
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
auto __lambda129__(auto function) {
	return function.apply(input);
}
auto __lambda130__() {
	return Iterators.fromOption()
}
Option_Node parseOr() {
	return rules.iter().map(__lambda129__).flatMap(__lambda130__).next();
}
auto __lambda131__() {
	return Main.compilePrimitive()
}
auto __lambda132__(auto input) {
	return compileArray(input, typeParams);
}
auto __lambda133__(auto input) {
	return compileSymbol(input, typeParams);
}
List__Function_char*_Option_Node listTypeRules() {
	return Impl.listOf(wrapDefaultFunction(__lambda131__), wrapDefaultFunction(__lambda132__), wrapDefaultFunction(__lambda133__), parseGeneric(typeParams));
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
auto __lambda134__(auto value) {
	return value.retype(type);
}
auto __lambda135__(auto input) {
	return mapper.apply(input).map(__lambda134__);
}
Function_char*_Option_Node parseWithType() {
	return __lambda135__;
}
char* generateGeneric() {
	return stringify(node);
}
auto __lambda136__() {
	return Main.wrapDefault()
}
auto __lambda137__(auto input) {
	return mapper.apply(input).map(__lambda136__);
}
Function_char*_Option_Node wrapDefaultFunction() {
	return __lambda137__;
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
auto __lambda138__() {
	return Main.generateType()
}
auto __lambda139__(auto value) {
	return value + "*";
}
Option_char* compileArray() {
	if (input.endsWith("[]")) {
		return parseType(input.substring(0, input.length() - "[]".length()), typeParams).map(__lambda138__).map(__lambda139__);
	}
	return None_();
}
auto __lambda140__() {
	return char*.equals()
}
Option_char* compileSymbol() {
	char* stripped = input.strip();
	if (!isSymbol(stripped)) {
		return None_();
	}
	if (Lists.contains(typeParams, stripped, __lambda140__)) {
		return Some_(stripped);
	}
	else {
		return Some_(stripped);
	}
}
auto __lambda141__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return c == '_' || Character.isLetter(c) ||(index != 0 && Character.isDigit(c));
}
int isSymbol() {
	if (input.isBlank()) {
		return false;
	}/* 

        if(input.equals("public")) return false; */
	return Iterators.fromStringWithIndices(input).allMatch(__lambda141__);
}
Option_char* generatePlaceholder() {
	return Some_("/* " + input + " */");
}
