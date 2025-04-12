struct IOError {
	struct String (*display)();
};
struct Path_ {
	struct Path_ (*resolveSibling)(struct String);
	List_<struct String> (*listNames)();
};
struct State {
	List_<char> queue;
	List_<struct String> segments;
	struct StringBuilder buffer;
	int depth;
	struct private (*State)(List_<char>, List_<struct String>, struct StringBuilder, int);
	struct public (*State)(List_<char>);
	struct State (*advance)();
	struct State (*append)(char);
	int (*isLevel)();
	char (*pop)();
	int (*hasElements)();
	struct State (*exit)();
	struct State (*enter)();
	List_<struct String> (*segments)();
	char (*peek)();
};
struct Joiner {
	Option<struct String> (*createInitial)();
	Option<struct String> (*fold)(Option<struct String>, struct String);
};
struct RangeHead {
	int length;
	struct public (*RangeHead)(int);
	Option<int> (*next)();
};
struct Iterators {
	Iterator<T> (*empty)();
	Iterator<char> (*fromString)(struct String);
	Iterator<Tuple<int, struct Character>> (*fromStringWithIndices)(struct String);
	Iterator<T> (*fromOption)(Option<struct T>);
};
struct Node {
	struct public (*Node)();
	struct Node (*withString)(struct String, struct String);
	struct Node (*withNodeList)(struct String, List_<struct Node>);
	Option<List_<struct Node>> (*findNodeList)(struct String);
	Option<struct String> (*findString)(struct String);
	struct Node (*withNode)(struct String, struct Node);
	Option<struct Node> (*findNode)(struct String);
	int (*is)(struct String);
	struct Node (*retype)(struct String);
	int (*equalsTo)(struct Node);
	int (*isABoolean)(List_<struct Node>, List_<struct Node>);
};
struct Lists {
	int (*contains)(List_<struct T>, struct T, BiFunction<struct T, struct T, struct Boolean>);
	int (*equalsTo)(List_<struct T>, List_<struct T>, BiFunction<struct T, struct T, struct Boolean>);
};
struct Options {
	int (*equalsTo)(Option<struct T>, Option<struct T>, BiFunction<struct T, struct T, struct Boolean>);
};
struct Maps {
	int (*equalsTo)(Map_<struct K, struct V>, Map_<struct K, struct V>, BiFunction<struct K, struct K, struct Boolean>, BiFunction<struct V, struct V, struct Boolean>);
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
	int (*entryEqualsTo)(struct K, Map_<struct K, struct V>, Map_<struct K, struct V>, BiFunction<struct V, struct V, struct Boolean>);
	List_<K> (*foldUniquely)(List_<struct K>, struct K, BiFunction<struct K, struct K, struct Boolean>);
	void (*main)(struct String*);
	Option<struct IOError> (*compileAndWrite)(struct String, struct Path_);
	struct String (*compile)(struct String);
	struct String (*mergeAllStatements)(List_<struct Node>);
	Option<List_<struct Node>> (*parseAllStatements)(struct String, Function<struct String, Option<struct Node>>);
	List_<struct String> (*divideAllStatements)(struct String);
	struct String (*generateAll)(List_<struct Node>, Function<struct Node, struct String>, BiFunction<struct StringBuilder, struct String, struct StringBuilder>);
	struct String (*mergeAll)(List_<struct String>, BiFunction<struct StringBuilder, struct String, struct StringBuilder>);
	Option<List_<struct Node>> (*parseAll)(List_<struct String>, Function<struct String, Option<struct Node>>);
	struct StringBuilder (*mergeStatements)(struct StringBuilder, struct String);
	List_<struct String> (*divide)(struct String, BiFunction<struct State, struct Character, struct State>);
	struct State (*divideStatementChar)(struct State, char);
	int (*isShallow)(struct State);
	Option<struct String> (*compileRootSegment)(struct String);
	List_<struct String> (*splitByDelimiter)(struct String, char);
	Option<struct String> (*compileToStruct)(struct String, struct String, List_<struct String>);
	Option<struct String> (*generateStruct)(List_<struct String>, struct Node);
	Option<struct String> (*compileClassMember)(struct String, List_<struct String>);
	Option<struct String> (*compileDefinitionStatement)(struct String);
	Option<struct String> (*compileGlobalInitialization)(struct String, List_<struct String>);
	Option<struct String> (*compileInitialization)(struct String, List_<struct String>, int);
	Option<struct String> (*compileWhitespace)(struct String);
	Option<struct String> (*compileMethod)(struct String, List_<struct String>);
	Function<struct String, Option<struct Node>> (*createParamRule)();
	Option<struct String> (*getStringOption)(List_<struct String>, struct Node, List_<struct Node>, struct String);
	Option<List_<struct Node>> (*parseAllValues)(struct String, Function<struct String, Option<struct Node>>);
	struct State (*divideValueChar)(struct State, char);
	struct String (*mergeAllValues)(List_<struct Node>, Function<struct Node, struct String>);
	Option<struct String> (*compileStatementOrBlock)(struct String, List_<struct String>, int);
	Option<struct String> (*compilePostOperator)(struct String, List_<struct String>, int, struct String);
	Option<struct String> (*compileElse)(struct String, List_<struct String>, int);
	Option<struct String> (*compileKeywordStatement)(struct String, int, struct String);
	struct String (*formatStatement)(int, struct String);
	struct String (*createIndent)(int);
	Option<struct String> (*compileConditional)(struct String, List_<struct String>, struct String, int);
	int (*findConditionEnd)(struct String);
	Option<struct String> (*compileInvocationStatement)(struct String, List_<struct String>, int);
	Option<struct String> (*compileAssignment)(struct String, List_<struct String>, int);
	Option<struct String> (*compileReturn)(struct String, List_<struct String>, int);
	Option<struct String> (*compileValue)(struct String, List_<struct String>, int);
	Option<struct String> (*compileOperator)(struct String, List_<struct String>, int, struct String);
	Option<struct String> (*compileLambda)(struct String, List_<struct String>, int);
	Option<struct String> (*generateLambdaWithReturn)(List_<struct String>, struct String);
	int (*isNumber)(struct String);
	Option<struct String> (*compileInvocation)(struct String, List_<struct String>, int);
	int (*findInvocationStart)(struct String);
	Option<struct String> (*compileArgs)(struct String, List_<struct String>, int);
	struct StringBuilder (*mergeValues)(struct StringBuilder, struct String);
	Option<struct Node> (*parseDefinition)(struct String);
	Option<struct Node> (*parseDefinitionWithName)(struct String, struct Node);
	Option<struct Node> (*parseDefinitionWithTypeSeparator)(struct Node, struct String, struct String);
	Option<struct Node> (*parseDefinitionTypeProperty)(struct Node, struct String, List_<struct String>);
	Option<struct Node> (*parseDefinitionWithNoTypeParams)(struct Node, struct String, struct String);
	int (*validateLeft)(struct String);
	Option<struct String> (*generateDefinition)(struct Node);
	struct String (*unwrapDefault)(struct Node);
	struct Node (*wrapDefault)(struct String);
	Option<int> (*findTypeSeparator)(struct String);
	List_<struct String> (*splitValues)(struct String);
	struct String (*generateType)(struct Node);
	Option<struct Node> (*parseType)(struct String, List_<struct String>);
	Option<struct Node> (*parseOr)(struct String, List_<Function<struct String, Option<struct Node>>>);
	List_<Function<struct String, Option<struct Node>>> (*listTypeRules)(List_<struct String>);
	Function<struct String, Option<struct Node>> (*parseGeneric)(List_<struct String>);
	struct String (*generateGeneric)(struct Node);
	Function<struct String, Option<struct Node>> (*wrapDefaultFunction)(Function<struct String, Option<struct String>>);
	Option<struct String> (*compilePrimitive)(struct String);
	Option<struct String> (*compileArray)(struct String, List_<struct String>);
	Option<struct String> (*compileSymbol)(struct String, List_<struct String>);
	int (*isSymbol)(struct String);
	Option<struct String> (*generatePlaceholder)(struct String);
};
// List_<struct String>
// List_<char>
// Option<struct String>
// None<>
// Some<>
// Option<int>
// Iterator<T>
// HeadedIterator<>
// EmptyHead<>
// Iterator<char>
// Iterator<Tuple<int, struct Character>>
// Tuple<int, struct Character>
// Option<struct T>
// List_<struct Node>
// Option<List_<struct Node>>
// Option<struct Node>
// List_<struct T>
// BiFunction<struct T, struct T, struct Boolean>
// Map_<struct K, struct V>
// BiFunction<struct K, struct K, struct Boolean>
// BiFunction<struct V, struct V, struct Boolean>
// Map_<struct String, Function<struct Node, Option<struct String>>>
// Function<struct Node, Option<struct String>>
// Option<struct V>
// List_<K>
// List_<struct K>
// Option<struct IOError>
// ListCollector<>
// Function<struct String, Option<struct Node>>
// Function<struct Node, struct String>
// BiFunction<struct StringBuilder, struct String, struct StringBuilder>
// BiFunction<struct State, struct Character, struct State>
// List_<Tuple<int, struct Character>>
// List_<Function<struct String, Option<struct Node>>>
// Function<struct String, Option<struct String>>
int counter = 0;
List_<struct String> imports = Impl.listEmpty();
List_<struct String> structs = Impl.listEmpty();
List_<struct String> globals = Impl.listEmpty();
List_<struct String> methods = Impl.listEmpty();
List_<struct Node> expansions = Impl.listEmpty();
int counter = 0;
Map_<struct String, Function<struct Node, Option<struct String>>> structGenerators = Impl.mapEmpty();
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
List_<struct String> segments() {
	return this.segments;
}
char peek() {
	return this.queue.peek();
}
Option<struct String> createInitial() {
	return None<>();
}
auto __lambda0__(auto inner) {
	return inner + this.delimiter + element;
}
Option<struct String> fold() {
	return Some<>(current.map(__lambda0__).orElse(element));
}
struct public RangeHead() {
	this.length = length;
}
Option<int> next() {
	if (this.counter >= this.length) {
		return None<>();
	}
	int value = this.counter;this.counter++;
	return Some<>(value);
}
<T> Iterator<T> empty() {
	return HeadedIterator<>(EmptyHead<>());
}
auto __lambda1__() {
	return struct Tuple.right()
}
Iterator<char> fromString() {
	return fromStringWithIndices(string).map(__lambda1__);
}
Iterator<Tuple<int, struct Character>> fromStringWithIndices() {
	return HeadedIterator<>(struct RangeHead(string.length())).map(index -> new Tuple<>(index, string.charAt(index)));
}
auto __lambda2__() {
	return struct SingleHead.new()
}
auto __lambda3__() {
	return struct EmptyHead.new()
}
<T> Iterator<T> fromOption() {
	return HeadedIterator<>(option.<Head<T>>map(__lambda2__).orElseGet(__lambda3__));
}
struct public Node() {
	this(None<>(), Impl.mapEmpty(), Impl.mapEmpty(), Impl.mapEmpty());
}
struct Node withString() {
	return struct Node(this.type, this.strings.with(propertyKey, propertyValue), this.nodes, this.nodeLists);
}
struct Node withNodeList() {
	return struct Node(this.type, this.strings, this.nodes, this.nodeLists.with(propertyKey, propertyValues));
}
Option<List_<struct Node>> findNodeList() {
	return this.nodeLists.find(propertyKey);
}
Option<struct String> findString() {
	return this.strings.find(propertyKey);
}
struct Node withNode() {
	return struct Node(this.type, this.strings, this.nodes.with(propertyKey, propertyValue), this.nodeLists);
}
Option<struct Node> findNode() {
	return this.nodes.find(propertyKey);
}
auto __lambda4__(auto inner) {
	return inner.equals(type);
}
int is() {
	return this.type.filter(__lambda4__).isPresent();
}
struct Node retype() {
	return struct Node(Some<>(type), this.strings, this.nodes, this.nodeLists);
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
	return HeadedIterator<>(struct RangeHead(first.size())).allMatch(index -> {
                return equator.apply(first.get(index), second.get(index));
            });
}
auto __lambda14__() {
	return second;
}
auto __lambda15__(auto tuple) {
	return equator.apply(tuple.left, tuple.right);
}
<T> int equalsTo() {/* 
            if(first.isEmpty() && second.isEmpty()) return true; */
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
	Option<struct V> firstOption = first.find(key);
	Option<struct V> secondOption = second.find(key);
	return Options.equalsTo(firstOption, secondOption, valueEquator);
}
<K> List_<K> foldUniquely() {
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
Option<struct IOError> compileAndWrite() {
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
	return list1.iter().map(__lambda22__).collect(ListCollector<>());
}
auto __lambda24__() {
	return struct Main.generateGeneric()
}
auto __lambda25__(auto expansion) {
	return "// " + expansion + "\n";
}
auto __lambda26__(auto list) {
	List_<struct String> collect = expansions.iter().map(__lambda24__).map(__lambda25__).collect(ListCollector<>());
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
	List_<struct String> segments = divideAllStatements(input);
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
Option<List_<struct Node>> parseAllStatements() {
	return parseAll(divideAllStatements(input), rule);
}
auto __lambda32__() {
	return struct Main.divideStatementChar()
}
List_<struct String> divideAllStatements() {
	return divide(input, __lambda32__);
}
struct String generateAll() {
	return mergeAll(compiled.iter().map(generator).collect(ListCollector<>()), merger);
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
Option<List_<struct Node>> parseAll() {
	return segments.iter().<Option<List_<Node>>>fold(Some<>(Impl.listEmpty()), __lambda35__);
}
struct StringBuilder mergeStatements() {
	return output.append(compiled);
}
List_<struct String> divide() {
	List_<char> queue = Iterators.fromString(input).collect(ListCollector<>());
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
Option<struct String> compileRootSegment() {
	Option<struct String> whitespace = compileWhitespace(input);
	if (whitespace.isPresent()) {
		return whitespace;
	}
	if (input.startsWith("package ")) {
		return Some<>("");
	}
	struct String stripped = input.strip();
	if (stripped.startsWith("import ")) {
		struct String right = stripped.substring("import ".length());
		if (right.endsWith(";")) {
			struct String content = right.substring(0, right.length() - ";".length());
			List_<struct String> split = splitByDelimiter(content, '.');
			if (split.size() >= 3 && Lists.equalsTo(split.slice(0, 3), Impl.listOf("java", "util", "function"), __lambda36__)) {
				return Some<>("");
			}
			struct String joined = split.iter().collect(struct Joiner("/")).orElse("");
			imports.add("#include \"./" + joined + "\"\n");
			return Some<>("");
		}
	}
	Option<struct String> maybeClass = compileToStruct(input, "class ", Impl.listEmpty());
	if (maybeClass.isPresent()) {
		return maybeClass;
	}
	return generatePlaceholder(input);
}
List_<struct String> splitByDelimiter() {
	List_<struct String> segments = Impl.listEmpty();
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
auto __lambda37__(auto child) {
	return generateStruct(typeParams, child);
}
Option<struct String> compileToStruct() {
	int classIndex = input.indexOf(infix);
	if (classIndex < 0) {
		return None<>();
	}
	struct String afterKeyword = input.substring(classIndex + infix.length());
	int contentStart = afterKeyword.indexOf("{");
	if (contentStart < 0) {
		return None<>();
	}
	struct String beforeContent = afterKeyword.substring(0, contentStart).strip();
	int implementsIndex = beforeContent.indexOf(" implements ");
	struct String beforeContent1 = implementsIndex >= /*  0
                ? beforeContent */.substring(0, implementsIndex)
                : beforeContent;
	int paramStart = beforeContent1.indexOf("(");
	struct String name1 = paramStart >= /*  0
                ? beforeContent1 */.substring(0, paramStart)
                : beforeContent1;
	int typeParamStart = name1.indexOf("<");
	struct String name = name1.strip();
	struct String body = afterKeyword.substring(contentStart + "{".length());
	struct Node node = struct Node(/* ) */.withString("name", /* name) */.withString("body", body);
	if (typeParamStart >= 0) {
		structGenerators = structGenerators.with(name, __lambda37__);
		return Some<>("// " + name1 + "\n");
	}
	return generateStruct(typeParams, node);
}
auto __lambda38__(auto input1) {
	return compileClassMember(input1, typeParams);
}
auto __lambda39__() {
	return struct Main.mergeAllStatements()
}
auto __lambda40__(auto outputContent) {
	structs.add("struct " + name + " {\n" + outputContent + "};\n");
	return "";
}
Option<struct String> generateStruct() {
	struct String name = node.findString("name").orElse("");
	struct String body = node.findString("body").orElse("");
	if (!isSymbol(name)) {
		return None<>();
	}
	struct String withEnd = body.strip();
	if (!withEnd.endsWith("}")) {
		return None<>();
	}
	struct String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
	return parseAllStatements(inputContent, wrapDefaultFunction(__lambda38__)).map(__lambda39__).map(__lambda40__);
}
auto __lambda41__() {
	return compileToStruct(input, "interface ", typeParams);
}
auto __lambda42__() {
	return compileToStruct(input, "record ", typeParams);
}
auto __lambda43__() {
	return compileToStruct(input, "class ", typeParams);
}
auto __lambda44__() {
	return compileGlobalInitialization(input, typeParams);
}
auto __lambda45__() {
	return compileDefinitionStatement(input);
}
auto __lambda46__() {
	return compileMethod(input, typeParams);
}
auto __lambda47__() {
	return generatePlaceholder(input);
}
Option<struct String> compileClassMember() {
	return compileWhitespace(input).or(__lambda41__).or(__lambda42__).or(__lambda43__).or(__lambda44__).or(__lambda45__).or(__lambda46__).or(__lambda47__);
}
auto __lambda48__() {
	return struct Main.generateDefinition()
}
auto __lambda49__(auto result) {
	return "\t" + result + ";\n";
}
Option<struct String> compileDefinitionStatement() {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String content = stripped.substring(0, stripped.length() - ";".length());
		return parseDefinition(content).flatMap(__lambda48__).map(__lambda49__);
	}
	return None<>();
}
auto __lambda50__(auto generated) {
	globals.add(generated + ";\n");
	return "";
}
Option<struct String> compileGlobalInitialization() {
	return compileInitialization(input, typeParams, 0).map(__lambda50__);
}
auto __lambda51__() {
	return struct Main.generateDefinition()
}
auto __lambda52__(auto outputValue) {
	return outputDefinition + " = " + outputValue;
}
auto __lambda53__(auto outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda52__);
}
Option<struct String> compileInitialization() {
	if (!input.endsWith(";")) {
		return None<>();
	}
	struct String withoutEnd = input.substring(0, input.length() - ";".length());
	int valueSeparator = withoutEnd.indexOf("=");
	if (valueSeparator < 0) {
		return None<>();
	}
	struct String definition = withoutEnd.substring(0, valueSeparator).strip();
	struct String value = withoutEnd.substring(valueSeparator + "=".length()).strip();
	return parseDefinition(definition).flatMap(__lambda51__).flatMap(__lambda53__);
}
Option<struct String> compileWhitespace() {
	if (input.isBlank()) {
		return Some<>("");
	}
	return None<>();
}
Option<struct String> compileMethod() {
	int paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return None<>();
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
auto __lambda54__() {
	return struct Main.compileWhitespace()
}
auto __lambda55__() {
	return struct Main.parseDefinition()
}
auto __lambda56__(auto definition) {
	return parseOr(definition, Impl.listOf(wrapDefaultFunction(__lambda54__), __lambda55__));
}
Function<struct String, Option<struct Node>> createParamRule() {
	return __lambda56__;
}
auto __lambda57__(auto param) {
	return param.findNode("type");
}
auto __lambda58__() {
	return struct Iterators.fromOption()
}
auto __lambda59__() {
	return generateDefinition(functionalDefinition);
}
auto __lambda60__(auto input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
auto __lambda61__() {
	return struct Main.mergeAllStatements()
}
auto __lambda62__(auto outputContent) {
	methods.add("\t".repeat(0) + asContent + "(" + mergeAllValues(params, Main::unwrapDefault) + ")" + " {" + outputContent + "\n}\n");
	return Some<>(entry);
}
auto __lambda63__(auto output) {
	struct String asContent = output.left;
	struct String asType = output.right;
	struct String entry = "\t" + asType + ";\n";
	if (!body.startsWith("{") || !body.endsWith("}")) {
		return Some<>(entry);
	}
	struct String inputContent = body.substring("{".length(), body.length() - "}".length());
	return parseAllStatements(inputContent, wrapDefaultFunction(__lambda60__)).map(__lambda61__).flatMap(__lambda62__);
}
Option<struct String> getStringOption() {
	List_<struct Node> paramTypes = params.iter().map(__lambda57__).flatMap(__lambda58__).collect(ListCollector<>());
	struct String name = definition.findString("name").orElse("");
	struct Node returns = definition.findNode("type").orElse(struct Node());
	struct Node functionalDefinition = struct Node(/* ) */.retype("functional-definition").withString("name", /* name) */.withNode("returns", /* returns) */.withNodeList("params", paramTypes);
	return generateDefinition(definition).and(__lambda59__).flatMap(__lambda63__);
}
auto __lambda64__() {
	return struct Main.divideValueChar()
}
Option<List_<struct Node>> parseAllValues() {
	return parseAll(divide(input, __lambda64__), rule);
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
auto __lambda65__() {
	return struct Main.mergeValues()
}
struct String mergeAllValues() {
	return generateAll(compiled, generator, __lambda65__);
}
auto __lambda66__() {
	return compileKeywordStatement(input, depth, "continue");
}
auto __lambda67__() {
	return compileKeywordStatement(input, depth, "break");
}
auto __lambda68__() {
	return compileConditional(input, typeParams, "if ", depth);
}
auto __lambda69__() {
	return compileConditional(input, typeParams, "while ", depth);
}
auto __lambda70__() {
	return compileElse(input, typeParams, depth);
}
auto __lambda71__() {
	return compilePostOperator(input, typeParams, depth, "++");
}
auto __lambda72__() {
	return compilePostOperator(input, typeParams, depth, "--");
}
auto __lambda73__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda74__() {
	return compileReturn(input, typeParams, depth).map(__lambda73__);
}
auto __lambda75__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda76__() {
	return compileInitialization(input, typeParams, depth).map(__lambda75__);
}
auto __lambda77__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda78__() {
	return compileAssignment(input, typeParams, depth).map(__lambda77__);
}
auto __lambda79__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda80__() {
	return compileInvocationStatement(input, typeParams, depth).map(__lambda79__);
}
auto __lambda81__() {
	return compileDefinitionStatement(input);
}
auto __lambda82__() {
	return generatePlaceholder(input);
}
Option<struct String> compileStatementOrBlock() {
	return compileWhitespace(input).or(__lambda66__).or(__lambda67__).or(__lambda68__).or(__lambda69__).or(__lambda70__).or(__lambda71__).or(__lambda72__).or(__lambda74__).or(__lambda76__).or(__lambda78__).or(__lambda80__).or(__lambda81__).or(__lambda82__);
}
auto __lambda83__(auto value) {
	return value + operator + ";";
}
Option<struct String> compilePostOperator() {
	struct String stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		struct String slice = stripped.substring(0, stripped.length() -(operator + ";").length());
		return compileValue(slice, typeParams, depth).map(__lambda83__);
	}
	else {
		return None<>();
	}
}
auto __lambda84__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda85__() {
	return struct Main.mergeAllStatements()
}
auto __lambda86__(auto result) {
	return indent + "else {" + result + indent + "}";
}
auto __lambda87__(auto result) {
	return "else " + result;
}
Option<struct String> compileElse() {
	struct String stripped = input.strip();
	if (stripped.startsWith("else ")) {
		struct String withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			struct String indent = createIndent(depth);
			return parseAllStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), wrapDefaultFunction(__lambda84__)).map(__lambda85__).map(__lambda86__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda87__);
		}
	}
	return None<>();
}
Option<struct String> compileKeywordStatement() {
	if (input.strip().equals(keyword + ";")) {
		return Some<>(formatStatement(depth, keyword));
	}
	else {
		return None<>();
	}
}
struct String formatStatement() {
	return createIndent(depth) + value + ";";
}
struct String createIndent() {
	return "\n" + "\t".repeat(depth);
}
auto __lambda88__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda89__() {
	return struct Main.mergeAllStatements()
}
auto __lambda90__(auto statements) {
		return withCondition + " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
}
auto __lambda91__(auto result) {
		return withCondition + " " + result;
}
auto __lambda92__(auto newCondition) {
	struct String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		struct String content = withBraces.substring(1, withBraces.length() - 1);
		return parseAllStatements(content, wrapDefaultFunction(__lambda88__)).map(__lambda89__).map(__lambda90__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda91__);
	}
}
Option<struct String> compileConditional() {
	struct String stripped = input.strip();
	if (!stripped.startsWith(prefix)) {
		return None<>();
	}
	struct String afterKeyword = stripped.substring(prefix.length()).strip();
	if (!afterKeyword.startsWith("(")) {
		return None<>();
	}
	struct String withoutConditionStart = afterKeyword.substring(1);
	int conditionEnd = findConditionEnd(withoutConditionStart);
	if (conditionEnd < 0) {
		return None<>();
	}
	struct String oldCondition = withoutConditionStart.substring(0, conditionEnd).strip();
	struct String withBraces = withoutConditionStart.substring(conditionEnd + ")".length()).strip();
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda92__);
}
int findConditionEnd() {
	int conditionEnd = -1;
	int depth0 = 0;
	List_<Tuple<int, struct Character>> queue = Iterators.fromStringWithIndices(input).collect(ListCollector<>());
	while (!queue.isEmpty()) {
		Tuple<int, struct Character> pair = queue.pop();
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
				Tuple<int, struct Character> next = queue.pop();
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
Option<struct String> compileInvocationStatement() {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		Option<struct String> maybeInvocation = compileInvocation(withoutEnd, typeParams, depth);
		if (maybeInvocation.isPresent()) {
			return maybeInvocation;
		}
	}
	return None<>();
}
auto __lambda93__(auto newSource) {
			return newDest + " = " + newSource;
}
auto __lambda94__(auto newDest) {
			return compileValue(source, typeParams, depth).map(__lambda93__);
}
Option<struct String> compileAssignment() {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			struct String destination = withoutEnd.substring(0, valueSeparator).strip();
			struct String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda94__);
		}
	}
	return None<>();
}
auto __lambda95__(auto result) {
	return "return " + result;
}
Option<struct String> compileReturn() {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda95__);
		}
	}
	return None<>();
}
auto __lambda96__() {
	return struct Main.generateType()
}
auto __lambda97__(auto value) {
	return outputType + value;
}
auto __lambda98__(auto outputType) {
	return compileArgs(argsString, typeParams, depth).map(__lambda97__);
}
auto __lambda99__(auto result) {
	return "!" + result;
}
auto __lambda100__() {
	return struct Main.generateType()
}
auto __lambda101__(auto compiled) {
			return generateLambdaWithReturn(Impl.listEmpty(), "\n\treturn " + compiled + "." + property + "()");
}
auto __lambda102__(auto compiled) {
	return compiled + "." + property;
}
auto __lambda103__() {
	return compileOperator(input, typeParams, depth, "<");
}
auto __lambda104__() {
	return compileOperator(input, typeParams, depth, "+");
}
auto __lambda105__() {
	return compileOperator(input, typeParams, depth, ">=");
}
auto __lambda106__() {
	return compileOperator(input, typeParams, depth, "&&");
}
auto __lambda107__() {
	return compileOperator(input, typeParams, depth, "==");
}
auto __lambda108__() {
	return compileOperator(input, typeParams, depth, "!=");
}
auto __lambda109__() {
	return generatePlaceholder(input);
}
Option<struct String> compileValue() {
	struct String stripped = input.strip();
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return Some<>(stripped);
	}
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return Some<>(stripped);
	}
	if (isSymbol(stripped) || isNumber(stripped)) {
		return Some<>(stripped);
	}
	if (stripped.startsWith("new ")) {
		struct String slice = stripped.substring("new ".length());
		int argsStart = slice.indexOf("(");
		if (argsStart >= 0) {
			struct String type = slice.substring(0, argsStart);
			struct String withEnd = slice.substring(argsStart + "(".length()).strip();
			if (withEnd.endsWith(")")) {
				struct String argsString = withEnd.substring(0, withEnd.length() - ")".length());
				return parseType(type, typeParams).map(__lambda96__).flatMap(__lambda98__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda99__);
	}
	Option<struct String> value = compileLambda(stripped, typeParams, depth);
	if (value.isPresent()) {
		return value;
	}
	Option<struct String> invocation = compileInvocation(input, typeParams, depth);
	if (invocation.isPresent()) {
		return invocation;
	}
	int methodIndex = stripped.lastIndexOf("::");
	if (methodIndex >= 0) {
		struct String type = stripped.substring(0, methodIndex).strip();
		struct String property = stripped.substring(methodIndex + "::".length()).strip();
		if (isSymbol(property)) {
			return parseType(type, typeParams).map(__lambda100__).flatMap(__lambda101__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		struct String object = input.substring(0, separator).strip();
		struct String property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda102__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda103__).or(__lambda104__).or(__lambda105__).or(__lambda106__).or(__lambda107__).or(__lambda108__).or(__lambda109__);
}
auto __lambda110__(auto rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
auto __lambda111__(auto leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda110__);
}
Option<struct String> compileOperator() {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return None<>();
	}
	struct String left = input.substring(0, operatorIndex);
	struct String right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda111__);
}
auto __lambda112__() {
	return struct String.strip()
}
auto __lambda113__(auto value) {
	return !value.isEmpty();
}
auto __lambda114__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
auto __lambda115__() {
	return struct Main.mergeAllStatements()
}
auto __lambda116__(auto result) {
		return generateLambdaWithReturn(paramNames, result);
}
auto __lambda117__(auto newValue) {
	return generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";");
}
Option<struct String> compileLambda() {
	int arrowIndex = input.indexOf("->");
	if (arrowIndex < 0) {
		return None<>();
	}
	struct String beforeArrow = input.substring(0, arrowIndex).strip();	List_<struct String> paramNames;

	if (isSymbol(beforeArrow)) {
		paramNames = Impl.listOf(beforeArrow);
	}else 
	if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
		struct String inner = beforeArrow.substring(1, beforeArrow.length() - 1);
		paramNames = splitByDelimiter(inner, ',').iter().map(__lambda112__).filter(__lambda113__).collect(ListCollector<>());
	}
	else {
		return None<>();
	}
	struct String value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		struct String slice = value.substring(1, value.length() - 1);
		return parseAllStatements(slice, wrapDefaultFunction(__lambda114__)).map(__lambda115__).flatMap(__lambda116__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda117__);
}
auto __lambda118__(auto name) {
	return "auto " + name;
}
Option<struct String> generateLambdaWithReturn() {
	int current = counter;counter++;
	struct String lambdaName = "__lambda" + current + "__";
	struct String joinedLambdaParams = paramNames.iter().map(__lambda118__).collect(struct Joiner(", ")).orElse("");
	methods.add("auto " + lambdaName + "(" + joinedLambdaParams + ")" + " {" + returnValue + "\n}\n");
	return Some<>(lambdaName);
}
auto __lambda119__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return (index == 0 && c == '-') || Character.isDigit(c);
}
int isNumber() {
	return Iterators.fromStringWithIndices(input).allMatch(__lambda119__);
}
auto __lambda120__(auto value) {
	return caller + value;
}
auto __lambda121__(auto caller) {
			return compileArgs(withEnd, typeParams, depth).map(__lambda120__);
}
Option<struct String> compileInvocation() {
	struct String stripped = input.strip();
	if (stripped.endsWith(")")) {
		struct String sliced = stripped.substring(0, stripped.length() - ")".length());
		int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			struct String type = sliced.substring(0, argsStart);
			struct String withEnd = sliced.substring(argsStart + "(".length()).strip();
			return compileValue(type, typeParams, depth).flatMap(__lambda121__);
		}
	}
	return None<>();
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
auto __lambda122__() {
	return compileValue(arg, typeParams, depth);
}
auto __lambda123__(auto arg) {
	return compileWhitespace(arg).or(__lambda122__);
}
auto __lambda124__() {
	return struct Main.unwrapDefault()
}
auto __lambda125__(auto compiled) {
	return mergeAllValues(compiled, __lambda124__);
}
auto __lambda126__(auto args) {
	return "(" + args + ")";
}
Option<struct String> compileArgs() {
	return parseAllValues(argsString, wrapDefaultFunction(__lambda123__)).map(__lambda125__).map(__lambda126__);
}
struct StringBuilder mergeValues() {
	if (cache.isEmpty()) {
		return cache.append(element);
	}
	return cache.append(", ").append(element);
}
Option<struct Node> parseDefinition() {
	struct String stripped = definition.strip();
	int nameSeparator = stripped.lastIndexOf(" ");
	if (nameSeparator < 0) {
		return None<>();
	}
	struct String beforeName = stripped.substring(0, nameSeparator).strip();
	struct String name = stripped.substring(nameSeparator + " ".length()).strip();
	if (!isSymbol(name)) {
		return None<>();
	}
	struct Node withName = struct Node(/* ) */.withString("name", name);
	return parseDefinitionWithName(beforeName, withName);
}
auto __lambda127__(auto typeSeparator) {
	struct String beforeType = beforeName.substring(0, typeSeparator).strip();
	struct String type = beforeName.substring(typeSeparator + " ".length());
	return parseDefinitionWithTypeSeparator(withName, beforeType, type);
}
auto __lambda128__() {
	return parseDefinitionTypeProperty(withName, beforeName, Impl.listEmpty());
}
Option<struct Node> parseDefinitionWithName() {
	return findTypeSeparator(beforeName).map(__lambda127__).orElseGet(__lambda128__);
}
auto __lambda129__() {
	return struct Main.wrapDefault()
}
auto __lambda130__(auto node) {
	return node.withNodeList("type-params", typeParamsNodes);
}
Option<struct Node> parseDefinitionWithTypeSeparator() {
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
	List_<struct String> typeParamsStrings = splitValues(substring);
	List_<struct Node> typeParamsNodes = typeParamsStrings.iter().map(__lambda129__).collect(ListCollector<>());
	int hasValidBeforeParams = validateLeft(beforeTypeParams);
	if (!hasValidBeforeParams) {
		return None<>();
	}
	return parseDefinitionTypeProperty(withName, type, typeParamsStrings).map(__lambda130__);
}
auto __lambda131__(auto outputType) {
	return withName.withNode("type", outputType);
}
Option<struct Node> parseDefinitionTypeProperty() {
	return parseType(type, typeParams).map(__lambda131__);
}
auto __lambda132__(auto node) {
	return node.withNodeList("type-params", typeParamsList);
}
Option<struct Node> parseDefinitionWithNoTypeParams() {
	int hasValidBeforeParams = validateLeft(beforeType);
	List_<struct Node> typeParamsList = Impl.listEmpty();
	if (!hasValidBeforeParams) {
		return None<>();
	}
	return parseDefinitionTypeProperty(withName, type, Impl.listEmpty()).map(__lambda132__);
}
auto __lambda133__() {
	return struct String.strip()
}
auto __lambda134__(auto value) {
	return !value.isEmpty();
}
auto __lambda135__() {
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
	return splitByDelimiter(modifiersString, ' ').iter().map(__lambda133__).filter(__lambda134__).allMatch(__lambda135__);
}
auto __lambda136__() {
	return struct Impl.listEmpty()
}
auto __lambda137__() {
	return struct Main.generateType()
}
auto __lambda138__() {
	return struct Impl.listEmpty()
}
auto __lambda139__() {
	return struct Main.unwrapDefault()
}
auto __lambda140__(auto inner) {
	return "<" + inner + "> ";
}
auto __lambda141__() {
	return struct Main.generateType()
}
Option<struct String> generateDefinition() {
	if (node.is("functional-definition")) {
		struct String name = node.findString("name").orElse("");
		struct String returns = generateType(node.findNode("returns").orElse(struct Node()));
		struct String params = node.findNodeList("params").orElseGet(__lambda136__).iter().map(__lambda137__).collect(struct Joiner(", ")).orElse("");
		return Some<>(returns + " (*" + name + ")(" + params + ")");
	}
	struct String typeParamsString = node.findNodeList("type-params").orElseGet(__lambda138__).iter().map(__lambda139__).collect(struct Joiner(", ")).map(__lambda140__).orElse("");
	struct String type = node.findNode("type").map(__lambda141__).orElse("");
	struct String name = node.findString("name").orElse("name");
	return Some<>(typeParamsString + type + " " + name);
}
struct String unwrapDefault() {
	return value.findString("value").orElse("");
}
struct Node wrapDefault() {
	return struct Node(/* ) */.withString("value", typeParam);
}
Option<int> findTypeSeparator() {
	int depth = 0;
	int index = beforeName.length() - 1;
	while (index >= 0) {
		char c = beforeName.charAt(index);
		if (c == ' ' && depth == 0) {
			return Some<>(index);
		}
		else {
			if (c == '>') {depth++;
			}
			if (c == ' < ') {depth--;
			}
		}index--;
	}
	return None<>();
}
auto __lambda142__() {
	return struct String.strip()
}
auto __lambda143__(auto param) {
	return !param.isEmpty();
}
List_<struct String> splitValues() {
	return splitByDelimiter(substring.strip(), ',').iter().map(__lambda142__).filter(__lambda143__).collect(ListCollector<>());
}
auto __lambda144__() {
	return struct Node.equalsTo()
}
struct String generateType() {
	if (node.is("generic")) {
		if (!Lists.contains(expansions, node, __lambda144__)) {
			expansions = expansions.add(node);
		}
		return generateGeneric(node);
	}
	return unwrapDefault(node);
}
Option<struct Node> parseType() {
	return parseOr(input, listTypeRules(typeParams));
}
auto __lambda145__(auto function) {
	return function.apply(input);
}
auto __lambda146__() {
	return struct Iterators.fromOption()
}
Option<struct Node> parseOr() {
	return rules.iter().map(__lambda145__).flatMap(__lambda146__).next();
}
auto __lambda147__() {
	return struct Main.compilePrimitive()
}
auto __lambda148__(auto input) {
	return compileArray(input, typeParams);
}
auto __lambda149__(auto input) {
	return compileSymbol(input, typeParams);
}
List_<Function<struct String, Option<struct Node>>> listTypeRules() {
	return Impl.listOf(wrapDefaultFunction(__lambda147__), wrapDefaultFunction(__lambda148__), wrapDefaultFunction(__lambda149__), parseGeneric(typeParams));
}
Function<struct String, Option<struct Node>> parseGeneric() {/* 
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
                        wrapDefaultFunction(Main::compileWhitespace),
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
struct String generateGeneric() {
	List_<struct Node> typeParams = node.findNodeList("type-params").orElse(Impl.listEmpty());
	struct String base = node.findString("base").orElse("");
	return base + " < " + mergeAllValues(typeParams, Main::generateType) + ">";
}
auto __lambda150__() {
	return struct Main.wrapDefault()
}
auto __lambda151__(auto input) {
	return mapper.apply(input).map(__lambda150__);
}
Function<struct String, Option<struct Node>> wrapDefaultFunction() {
	return __lambda151__;
}
Option<struct String> compilePrimitive() {
	if (input.equals("void")) {
		return Some<>("void");
	}
	if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
		return Some<>("int");
	}
	if (input.equals("char") || input.equals("Character")) {
		return Some<>("char");
	}
	return None<>();
}
auto __lambda152__() {
	return struct Main.generateType()
}
auto __lambda153__(auto value) {
	return value + "*";
}
Option<struct String> compileArray() {
	if (input.endsWith("[]")) {
		return parseType(input.substring(0, input.length() - "[]".length()), typeParams).map(__lambda152__).map(__lambda153__);
	}
	return None<>();
}
auto __lambda154__() {
	return struct String.equals()
}
Option<struct String> compileSymbol() {
	struct String stripped = input.strip();
	if (!isSymbol(stripped)) {
		return None<>();
	}
	if (Lists.contains(typeParams, stripped, __lambda154__)) {
		return Some<>(stripped);
	}
	else {
		return Some<>("struct " + stripped);
	}
}
auto __lambda155__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return c == '_' || Character.isLetter(c) ||(index != 0 && Character.isDigit(c));
}
int isSymbol() {
	if (input.isBlank()) {
		return false;
	}
	return Iterators.fromStringWithIndices(input).allMatch(__lambda155__);
}
Option<struct String> generatePlaceholder() {
	return Some<>("/* " + input + " */");
}
