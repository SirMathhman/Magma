#include "./java/util/Arrays"
#include "./java/util/Deque"
#include "./java/util/LinkedList"
#include "./java/util/Optional"
#include "./java/util/function/BiFunction"
#include "./java/util/function/Consumer"
#include "./java/util/function/Function"
#include "./java/util/function/Predicate"
#include "./java/util/regex/Pattern"
#include "./java/util/stream/Collectors"
#include "./java/util/stream/IntStream"
struct Result {
	<R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
};
struct IOError {
	String display();
};
struct List_ {
	List_<T> add(T element);
	List_<T> addAll(List_<T> others);
	Iterator<T> iter();
	int isEmpty();
	T getFirst();
	int size();
	List_<T> slice(int startInclusive, int endExclusive);
};
struct Path_ {
	Path_ resolveSibling(String sibling);
	List_<String> asList();
	Path_ resolveChild(String child);
};
struct Iterator {
	<R> R foldWithInitial(R initial, BiFunction<R, T, R> folder);
	void forEach(Consumer<T> consumer);
	<R> Iterator<R> map(Function<T, R> mapper);
	Iterator<T> filter(Predicate<T> predicate);
	Optional<T> next();
	Iterator<T> concat(Iterator<T> other);
	<C> C collect(Collector<T, C> collector);
	int allMatch(Predicate<T> predicate);
	<R> Optional<R> foldWithMapper(Function<T, R> mapper, BiFunction<R, T, R> folder);
};
struct Collector {
	C createInitial();
	C fold(C current, T element);
};
struct Head {
	Optional<T> next();
};
struct HeadedIterator {
};
struct EmptyHead {
};
struct SingleHead {
	T value;
};
struct Err {
};
struct Ok {
};
struct State {
	Deque<char> queue;
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
struct Main {
};
int retrieved = false;
int counter = 0;
List_<String> imports = Lists.empty();
List_<String> structs = Lists.empty();
List_<String> globals = Lists.empty();
List_<String> methods = Lists.empty();
int counter = 0;
auto __lambda0__(auto next) {
	return folder.apply(finalCurrent, next);
}
<R> R foldWithInitial(R initial, BiFunction<R, T, R> folder) {
	R current = initial;
	while (true) {
		R finalCurrent = current;
		Optional<R> option = this.head.next().map(__lambda0__);
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
		Optional<T> next = this.head.next();
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
Optional<T> next() {
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
auto __lambda6__(auto aBoolean, auto t) {
	return aBoolean && predicate.test(t);
}
int allMatch(Predicate<T> predicate) {
	return this.foldWithInitial(true, __lambda6__);
}
auto __lambda7__(auto next) {
	return this.foldWithInitial(next, folder);
}
<R> Optional<R> foldWithMapper(Function<T, R> mapper, BiFunction<R, T, R> folder) {
	return this.head.next().map(mapper).map(__lambda7__);
}
auto __lambda8__ {
	return Iterator.concat()
}
<R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper) {
	return this.map(mapper).foldWithInitial(Iterators.empty(), __lambda8__);
}
Optional<T> next() {
	return Optional.empty();
}
private SingleHead(T value) {
	this.value = value;
}
Optional<T> next() {
	if (this.retrieved) {
		return Optional.empty();
	}
	this.retrieved = true;
	return Optional.of(this.value);
}
<R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
	return whenErr.apply(this.error);
}
<R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
	return whenOk.apply(this.value);
}
private State(Deque<char> queue, List_<String> segments, StringBuilder buffer, int depth) {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
public State(Deque<char> queue) {
	this(queue, Lists.empty(), StringBuilder(), 0);
}
State advance() {
	this.segments = this.segments.add(this.buffer.toString());
	this.buffer = StringBuilder();
	return this;
}
State append(char c) {
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
List_<String> segments() {
	return this.segments;
}
Optional<char> peek() {
	if (!this.queue.isEmpty()) {
		return Optional.of(this.queue.peek());
	}
	else {
		return Optional.empty();
	}
}
<T> Iterator<T> fromArray(T* array) {
	return HeadedIterator<>(RangeHead(array.length)).map(index -> array[index]);
}
<T> Iterator<T> empty() {
	return HeadedIterator<>(EmptyHead<>());
}
<T> Iterator<T> from(/* T... */ array) {
	return HeadedIterator<>(RangeHead(array.length)).map(index -> array[index]);
}
public RangeHead(int length) {
	this.length = length;
}
Optional<int> next() {
	if (this.counter < this.length) {
		int next = this.counter;this.counter++;
		return Optional.of(next);
	}
	return Optional.empty();
}
List_<T> createInitial() {
	return Lists.empty();
}
List_<T> fold(List_<T> current, T element) {
	return current.add(element);
}
Optional<String> createInitial() {
	return Optional.empty();
}
auto __lambda9__(auto inner) {
	return inner + this.delimiter + element;
}
Optional<String> fold(Optional<String> current, String element) {
	return Optional.of(current.map(__lambda9__).orElse(element));
}
auto __lambda10__(auto input) {
	return compileAndWrite(source, input);
}
auto __lambda11__ {
	return Optional.of()
}
auto __lambda12__(auto error) {
	return System.err.println(error.display());
}
void main(String* args) {
	Path_ source = Paths.get(".", "src", "java", "magma", "Main.java");
	Files.readString(source).match(__lambda10__, __lambda11__).ifPresent(__lambda12__);
}
Optional<IOError> compileAndWrite(Path_ source, String input) {
	Path_ target = source.resolveSibling("main.c");
	String output = compile(input);
	return Files.writeString(target, output);
}
auto __lambda13__ {
	return Main.divideStatementChar()
}
auto __lambda14__ {
	return Main.compileRootSegment()
}
auto __lambda15__ {
	return Main.mergeStatics()
}
auto __lambda16__ {
	return Main.mergeStatements()
}
auto __lambda17__(auto compiled) {
	return mergeAll(compiled, __lambda16__);
}
auto __lambda18__ {
	return generatePlaceholder(input);
}
String compile(String input) {
	List_<String> segments = divide(input, __lambda13__);
	return parseAll(segments, __lambda14__).map(__lambda15__).map(__lambda17__).or(__lambda18__).orElse("");
}
List_<String> mergeStatics(List_<String> list) {
	return Lists.<String>empty().addAll(imports).addAll(structs).addAll(globals).addAll(methods).addAll(list);
}
auto __lambda19__ {
	return Main.divideStatementChar()
}
auto __lambda20__ {
	return Main.mergeStatements()
}
Optional<String> compileStatements(String input, Function<String, Optional<String>> compiler) {
	return compileAndMerge(divide(input, __lambda19__), compiler, __lambda20__);
}
auto __lambda21__(auto compiled) {
	return mergeAll(compiled, merger);
}
Optional<String> compileAndMerge(List_<String> segments, Function<String, Optional<String>> compiler, BiFunction<StringBuilder, String, StringBuilder> merger) {
	return parseAll(segments, compiler).map(__lambda21__);
}
String mergeAll(List_<String> compiled, BiFunction<StringBuilder, String, StringBuilder> merger) {
	return compiled.iter().foldWithInitial(StringBuilder(), merger).toString();
}
auto __lambda22__ {
	return allCompiled.add()
}
auto __lambda23__(auto allCompiled) {
	return compiler.apply(segment).map(__lambda22__);
}
auto __lambda24__(auto maybeCompiled, auto segment) {
	return maybeCompiled.flatMap(__lambda23__);
}
Optional<List_<String>> parseAll(List_<String> segments, Function<String, Optional<String>> compiler) {
	return segments.iter().foldWithInitial(Optional.of(Lists.empty()), __lambda24__);
}
StringBuilder mergeStatements(StringBuilder output, String compiled) {
	return output.append(compiled);
}
auto __lambda25__ {
	return input.charAt()
}
auto __lambda26__ {
	return LinkedList.new()
}
List_<String> divide(String input, BiFunction<State, Character, State> divider) {
	Deque<char> queue = IntStream.range(0, input.length()).mapToObj(__lambda25__).collect(Collectors.toCollection(__lambda26__));
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
State divideStatementChar(State state, char c) {
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
int isShallow(State state) {
	return state.depth == 1;
}
Optional<String> compileRootSegment(String input) {
	Optional<String> whitespace = compileWhitespace(input);
	if (whitespace.isPresent()) {
		return whitespace;
	}
	if (input.startsWith("package ")) {
		return Optional.of("");
	}
	String stripped = input.strip();
	if (stripped.startsWith("import ")) {
		String right = stripped.substring("import ".length());
		if (right.endsWith(";")) {
			String content = right.substring(0, right.length() - ";".length());
			String joined = String.join("/", content.split(Pattern.quote(".")));
			imports.add("#include \"./" + joined + "\"\n");
			return Optional.of("");
		}
	}
	Optional<String> maybeClass = compileToStruct(input, "class ", Lists.empty());
	if (maybeClass.isPresent()) {
		return maybeClass;
	}
	return generatePlaceholder(input);
}
auto __lambda27__(auto input1) {
	return compileClassMember(input1, typeParams);
}
auto __lambda28__(auto outputContent) {
				structs.add("struct " + name + " {\n" + outputContent + "};\n");
				return "";
}
Optional<String> compileToStruct(String input, String infix, List_<String> typeParams) {
	int classIndex = input.indexOf(infix);
	if (classIndex < 0) {
		return Optional.empty();
	}
	String afterKeyword = input.substring(classIndex + infix.length());
	int contentStart = afterKeyword.indexOf("{");
	if (contentStart >= 0) {
		String beforeContent = afterKeyword.substring(0, contentStart).strip();
		int implementsIndex = beforeContent.indexOf(" implements ");	String beforeImplements;

		if (implementsIndex >= 0) {
			beforeImplements = beforeContent.substring(0, implementsIndex);
		}
		else {
			beforeImplements = beforeContent;
		}
		String strippedBeforeImplements = beforeImplements.strip();	String withoutParams;

		if (strippedBeforeImplements.endsWith(")")) {
			String withoutEnd = strippedBeforeImplements.substring(0, strippedBeforeImplements.length() - ")".length());
			int paramStart = withoutEnd.indexOf("(");
			if (paramStart >= 0) {
				withoutParams = withoutEnd.substring(0, paramStart).strip();
			}
			else {
				withoutParams = strippedBeforeImplements;
			}
		}
		else {
			withoutParams = strippedBeforeImplements;
		}
		String strippedWithoutParams = withoutParams.strip();	String name;

		if (strippedWithoutParams.endsWith(">")) {
			int genStart = strippedWithoutParams.indexOf("<");
			if (genStart >= 0) {
				name = strippedWithoutParams.substring(0, genStart).strip();
			}
			else {
				name = strippedWithoutParams;
			}
		}
		else {
			name = strippedWithoutParams;
		}
		if (isSymbol(name)) {
			String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
			if (withEnd.endsWith("}")) {
				String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
				return compileStatements(inputContent, __lambda27__).map(__lambda28__);
			}
		}
	}
	return Optional.empty();
}
auto __lambda29__ {
	return compileToStruct(input, "interface ", typeParams);
}
auto __lambda30__ {
	return compileToStruct(input, "record ", typeParams);
}
auto __lambda31__ {
	return compileToStruct(input, "class ", typeParams);
}
auto __lambda32__ {
	return compileGlobalInitialization(input, typeParams);
}
auto __lambda33__ {
	return compileDefinitionStatement(input);
}
auto __lambda34__ {
	return compileMethod(input, typeParams);
}
auto __lambda35__ {
	return generatePlaceholder(input);
}
Optional<String> compileClassMember(String input, List_<String> typeParams) {
	return compileWhitespace(input).or(__lambda29__).or(__lambda30__).or(__lambda31__).or(__lambda32__).or(__lambda33__).or(__lambda34__).or(__lambda35__);
}
auto __lambda36__(auto result) {
	return "\t" + result + ";\n";
}
Optional<String> compileDefinitionStatement(String input) {
	String stripped = input.strip();
	if (stripped.endsWith(";")) {
		String content = stripped.substring(0, stripped.length() - ";".length());
		return compileDefinition(content).map(__lambda36__);
	}
	return Optional.empty();
}
auto __lambda37__(auto generated) {
	globals.add(generated + ";\n");
	return "";
}
Optional<String> compileGlobalInitialization(String input, List_<String> typeParams) {
	return compileInitialization(input, typeParams, 0).map(__lambda37__);
}
auto __lambda38__(auto outputValue) {
	return outputDefinition + " = " + outputValue;
}
auto __lambda39__(auto outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda38__);
}
Optional<String> compileInitialization(String input, List_<String> typeParams, int depth) {
	if (!input.endsWith(";")) {
		return Optional.empty();
	}
	String withoutEnd = input.substring(0, input.length() - ";".length());
	int valueSeparator = withoutEnd.indexOf("=");
	if (valueSeparator < 0) {
		return Optional.empty();
	}
	String definition = withoutEnd.substring(0, valueSeparator).strip();
	String value = withoutEnd.substring(valueSeparator + "=".length()).strip();
	return compileDefinition(definition).flatMap(__lambda39__);
}
Optional<String> compileWhitespace(String input) {
	if (input.isBlank()) {
		return Optional.of("");
	}
	return Optional.empty();
}
auto __lambda40__ {
	return Main.compileParameter()
}
Optional<String> compileMethod(String input, List_<String> typeParams) {
	int paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return Optional.empty();
	}
	String inputDefinition = input.substring(0, paramStart).strip();
	String withParams = input.substring(paramStart + "(".length());
	return compileDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return Optional.empty();
            }

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, __lambda40__).flatMap(outputParams -> assembleMethodBody(typeParams, outputDefinition, outputParams, withParams.substring(paramEnd + ")".length()).strip()));
        });
}
auto __lambda41__(auto input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
auto __lambda42__(auto outputContent) {
		methods.add(header + " {" + outputContent + "\n}\n");
		return Optional.of("");
}
Optional<String> assembleMethodBody(List_<String> typeParams, String definition, String params, String body) {
	String header = "\t".repeat(0) + definition + "(" + params + ")";
	if (body.startsWith("{") && body.endsWith("}")) {
		String inputContent = body.substring("{".length(), body.length() - "}".length());
		return compileStatements(inputContent, __lambda41__).flatMap(__lambda42__);
	}
	return Optional.of("\t" + header + ";\n");
}
auto __lambda43__ {
	return compileDefinition(definition);
}
auto __lambda44__ {
	return generatePlaceholder(definition);
}
Optional<String> compileParameter(String definition) {
	return compileWhitespace(definition).or(__lambda43__).or(__lambda44__);
}
auto __lambda45__ {
	return Main.divideValueChar()
}
Optional<String> compileValues(String input, Function<String, Optional<String>> compiler) {
	List_<String> divided = divide(input, __lambda45__);
	return compileValues(divided, compiler);
}
State divideValueChar(State state, char c) {
	if (c == '-') {
		if (state.peek().orElse('\0') == '>') {
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
auto __lambda46__ {
	return Main.mergeValues()
}
Optional<String> compileValues(List_<String> params, Function<String, Optional<String>> compiler) {
	return compileAndMerge(params, compiler, __lambda46__);
}
auto __lambda47__ {
	return compileKeywordStatement(input, depth, "continue");
}
auto __lambda48__ {
	return compileKeywordStatement(input, depth, "break");
}
auto __lambda49__ {
	return compileConditional(input, typeParams, "if ", depth);
}
auto __lambda50__ {
	return compileConditional(input, typeParams, "while ", depth);
}
auto __lambda51__ {
	return compileElse(input, typeParams, depth);
}
auto __lambda52__ {
	return compilePostOperator(input, typeParams, depth, "++");
}
auto __lambda53__ {
	return compilePostOperator(input, typeParams, depth, "--");
}
auto __lambda54__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda55__ {
	return compileReturn(input, typeParams, depth).map(__lambda54__);
}
auto __lambda56__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda57__ {
	return compileInitialization(input, typeParams, depth).map(__lambda56__);
}
auto __lambda58__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda59__ {
	return compileAssignment(input, typeParams, depth).map(__lambda58__);
}
auto __lambda60__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda61__ {
	return compileInvocationStatement(input, typeParams, depth).map(__lambda60__);
}
auto __lambda62__ {
	return compileDefinitionStatement(input);
}
auto __lambda63__ {
	return generatePlaceholder(input);
}
Optional<String> compileStatementOrBlock(String input, List_<String> typeParams, int depth) {
	return compileWhitespace(input).or(__lambda47__).or(__lambda48__).or(__lambda49__).or(__lambda50__).or(__lambda51__).or(__lambda52__).or(__lambda53__).or(__lambda55__).or(__lambda57__).or(__lambda59__).or(__lambda61__).or(__lambda62__).or(__lambda63__);
}
auto __lambda64__(auto value) {
	return value + operator + ";";
}
Optional<String> compilePostOperator(String input, List_<String> typeParams, int depth, String operator) {
	String stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		String slice = stripped.substring(0, stripped.length() -(operator + ";").length());
		return compileValue(slice, typeParams, depth).map(__lambda64__);
	}
	else {
		return Optional.empty();
	}
}
auto __lambda65__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda66__(auto result) {
	return indent + "else {" + result + indent + "}";
}
auto __lambda67__(auto result) {
	return "else " + result;
}
Optional<String> compileElse(String input, List_<String> typeParams, int depth) {
	String stripped = input.strip();
	if (stripped.startsWith("else ")) {
		String withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			String indent = createIndent(depth);
			return compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), __lambda65__).map(__lambda66__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda67__);
		}
	}
	return Optional.empty();
}
Optional<String> compileKeywordStatement(String input, int depth, String keyword) {
	if (input.strip().equals(keyword + ";")) {
		return Optional.of(formatStatement(depth, keyword));
	}
	else {
		return Optional.empty();
	}
}
String formatStatement(int depth, String value) {
	return createIndent(depth) + value + ";";
}
String createIndent(int depth) {
	return "\n" + "\t".repeat(depth);
}
auto __lambda68__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda69__(auto statements) {
	return withCondition + " {" + statements + "\n" +
                        "\t".repeat(depth) +
                        "}";
}
auto __lambda70__(auto result) {
	return withCondition + " " + result;
}
auto __lambda71__(auto newCondition) {
	String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		String content = withBraces.substring(1, withBraces.length() - 1);
		return compileStatements(content, __lambda68__).map(__lambda69__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda70__);
	}
}
Optional<String> compileConditional(String input, List_<String> typeParams, String prefix, int depth) {
	String stripped = input.strip();
	if (!stripped.startsWith(prefix)) {
		return Optional.empty();
	}
	String afterKeyword = stripped.substring(prefix.length()).strip();
	if (!afterKeyword.startsWith("(")) {
		return Optional.empty();
	}
	String withoutConditionStart = afterKeyword.substring(1);
	int conditionEnd = findConditionEnd(withoutConditionStart);
	if (conditionEnd < 0) {
		return Optional.empty();
	}
	String oldCondition = withoutConditionStart.substring(0, conditionEnd).strip();
	String withBraces = withoutConditionStart.substring(conditionEnd + ")".length()).strip();
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda71__);
}
auto __lambda72__(auto index) {
	return Tuple<>(index, input.charAt(index));
}
auto __lambda73__ {
	return LinkedList.new()
}
int findConditionEnd(String input) {
	int conditionEnd = -1;
	int depth0 = 0;
	Deque<Tuple<int, Character>> queue = IntStream.range(0, input.length()).mapToObj(__lambda72__).collect(Collectors.toCollection(__lambda73__));
	while (!queue.isEmpty()) {
		Tuple<int, Character> pair = queue.pop();
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
				Tuple<int, Character> next = queue.pop();
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
Optional<String> compileInvocationStatement(String input, List_<String> typeParams, int depth) {
	String stripped = input.strip();
	if (stripped.endsWith(";")) {
		String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		Optional<String> maybeInvocation = compileInvocation(withoutEnd, typeParams, depth);
		if (maybeInvocation.isPresent()) {
			return maybeInvocation;
		}
	}
	return Optional.empty();
}
auto __lambda74__(auto newSource) {
	return newDest + " = " + newSource;
}
auto __lambda75__(auto newDest) {
	return compileValue(source, typeParams, depth).map(__lambda74__);
}
Optional<String> compileAssignment(String input, List_<String> typeParams, int depth) {
	String stripped = input.strip();
	if (stripped.endsWith(";")) {
		String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			String destination = withoutEnd.substring(0, valueSeparator).strip();
			String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda75__);
		}
	}
	return Optional.empty();
}
auto __lambda76__(auto result) {
	return "return " + result;
}
Optional<String> compileReturn(String input, List_<String> typeParams, int depth) {
	String stripped = input.strip();
	if (stripped.endsWith(";")) {
		String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda76__);
		}
	}
	return Optional.empty();
}
auto __lambda77__(auto value) {
	return outputType + value;
}
auto __lambda78__(auto outputType) {
	return compileArgs(argsString, typeParams, depth).map(__lambda77__);
}
auto __lambda79__(auto result) {
	return "!" + result;
}
auto __lambda80__(auto compiled) {
	return generateLambdaWithReturn(Lists.empty(), "\n\treturn " + compiled + "." + property + "()");
}
auto __lambda81__(auto compiled) {
	return compiled + "." + property;
}
auto __lambda82__ {
	return compileOperator(input, typeParams, depth, "<");
}
auto __lambda83__ {
	return compileOperator(input, typeParams, depth, "+");
}
auto __lambda84__ {
	return compileOperator(input, typeParams, depth, ">=");
}
auto __lambda85__ {
	return compileOperator(input, typeParams, depth, "&&");
}
auto __lambda86__ {
	return compileOperator(input, typeParams, depth, "==");
}
auto __lambda87__ {
	return compileOperator(input, typeParams, depth, "!=");
}
auto __lambda88__ {
	return generatePlaceholder(input);
}
Optional<String> compileValue(String input, List_<String> typeParams, int depth) {
	String stripped = input.strip();
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return Optional.of(stripped);
	}
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return Optional.of(stripped);
	}
	if (isSymbol(stripped) || isNumber(stripped)) {
		return Optional.of(stripped);
	}
	if (stripped.startsWith("new ")) {
		String slice = stripped.substring("new ".length());
		int argsStart = slice.indexOf("(");
		if (argsStart >= 0) {
			String type = slice.substring(0, argsStart);
			String withEnd = slice.substring(argsStart + "(".length()).strip();
			if (withEnd.endsWith(")")) {
				String argsString = withEnd.substring(0, withEnd.length() - ")".length());
				return compileType(type, typeParams).flatMap(__lambda78__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda79__);
	}
	Optional<String> value = compileLambda(stripped, typeParams, depth);
	if (value.isPresent()) {
		return value;
	}
	Optional<String> invocation = compileInvocation(input, typeParams, depth);
	if (invocation.isPresent()) {
		return invocation;
	}
	int methodIndex = stripped.lastIndexOf("::");
	if (methodIndex >= 0) {
		String type = stripped.substring(0, methodIndex).strip();
		String property = stripped.substring(methodIndex + "::".length()).strip();
		if (isSymbol(property)) {
			return compileType(type, typeParams).flatMap(__lambda80__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		String object = input.substring(0, separator).strip();
		String property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda81__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda82__).or(__lambda83__).or(__lambda84__).or(__lambda85__).or(__lambda86__).or(__lambda87__).or(__lambda88__);
}
auto __lambda89__(auto rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
auto __lambda90__(auto leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda89__);
}
Optional<String> compileOperator(String input, List_<String> typeParams, int depth, String operator) {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return Optional.empty();
	}
	String left = input.substring(0, operatorIndex);
	String right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda90__);
}
auto __lambda91__ {
	return String.strip()
}
auto __lambda92__(auto value) {
	return !value.isEmpty();
}
auto __lambda93__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
auto __lambda94__(auto result) {
	return generateLambdaWithReturn(paramNames, result);
}
auto __lambda95__(auto newValue) {
	return generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";");
}
Optional<String> compileLambda(String input, List_<String> typeParams, int depth) {
	int arrowIndex = input.indexOf("->");
	if (arrowIndex < 0) {
		return Optional.empty();
	}
	String beforeArrow = input.substring(0, arrowIndex).strip();	List_<String> paramNames;

	if (isSymbol(beforeArrow)) {
		paramNames = Lists.of(beforeArrow);
	}else 
	if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
		String inner = beforeArrow.substring(1, beforeArrow.length() - 1);
		paramNames = Iterators.fromArray(inner.split(Pattern.quote(","))).map(__lambda91__).filter(__lambda92__).collect(ListCollector<>());
	}
	else {
		return Optional.empty();
	}
	String value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		String slice = value.substring(1, value.length() - 1);
		return compileStatements(slice, __lambda93__).flatMap(__lambda94__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda95__);
}
auto __lambda96__(auto name) {
	return "auto " + name;
}
auto __lambda97__(auto value) {
	return "(" + value + ")";
}
Optional<String> generateLambdaWithReturn(List_<String> paramNames, String returnValue) {
	int current = counter;counter++;
	String lambdaName = "__lambda" + current + "__";
	String joined = paramNames.iter().map(__lambda96__).collect(Joiner(", ")).map(__lambda97__).orElse("");
	methods.add("auto " + lambdaName + joined + " {" + returnValue + "\n}\n");
	return Optional.of(lambdaName);
}
auto __lambda98__(auto index) {
	char c = input.charAt(index);
	return (index == 0 && c == '-') || Character.isDigit(c);
}
int isNumber(String input) {
	return IntStream.range(0, input.length()).allMatch(__lambda98__);
}
auto __lambda99__(auto value) {
	return caller + value;
}
auto __lambda100__(auto caller) {
	return compileArgs(withEnd, typeParams, depth).map(__lambda99__);
}
Optional<String> compileInvocation(String input, List_<String> typeParams, int depth) {
	String stripped = input.strip();
	if (stripped.endsWith(")")) {
		String sliced = stripped.substring(0, stripped.length() - ")".length());
		int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			String type = sliced.substring(0, argsStart);
			String withEnd = sliced.substring(argsStart + "(".length()).strip();
			return compileValue(type, typeParams, depth).flatMap(__lambda100__);
		}
	}
	return Optional.empty();
}
int findInvocationStart(String sliced) {
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
auto __lambda101__ {
	return compileValue(arg, typeParams, depth);
}
auto __lambda102__(auto arg) {
	return compileWhitespace(arg).or(__lambda101__);
}
auto __lambda103__(auto args) {
	return "(" + args + ")";
}
Optional<String> compileArgs(String argsString, List_<String> typeParams, int depth) {
	return compileValues(argsString, __lambda102__).map(__lambda103__);
}
StringBuilder mergeValues(StringBuilder cache, String element) {
	if (cache.isEmpty()) {
		return cache.append(element);
	}
	return cache.append(", ").append(element);
}
auto __lambda104__ {
	return String.strip()
}
auto __lambda105__(auto value) {
	return !value.isEmpty();
}
auto __lambda106__ {
	return Main.isSymbol()
}
auto __lambda107__(auto outputType) {
	return Optional.of(generateDefinition(typeParams, outputType, name));
}
auto __lambda108__(auto outputType) {
	return Optional.of(generateDefinition(Lists.empty(), outputType, name));
}
Optional<String> compileDefinition(String definition) {
	String stripped = definition.strip();
	int nameSeparator = stripped.lastIndexOf(" ");
	if (nameSeparator < 0) {
		return Optional.empty();
	}
	String beforeName = stripped.substring(0, nameSeparator).strip();
	String name = stripped.substring(nameSeparator + " ".length()).strip();
	if (!isSymbol(name)) {
		return Optional.empty();
	}
	int typeSeparator = -1;
	int depth = 0;
	int i = beforeName.length() - 1;
	while (i >= 0) {
		char c = beforeName.charAt(i);
		if (c == ' ' && depth == 0) {
			typeSeparator = i;
			break;
		}
		else {
			if (c == '>') {depth++;
			}
			if (c == ' < ') {depth--;
			}
		}i--;
	}
	if (typeSeparator >= 0) {
		String beforeType = beforeName.substring(0, typeSeparator).strip();
		String beforeTypeParams = beforeType;	List_<String> typeParams;

		if (beforeType.endsWith(">")) {
			String withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
			int typeParamStart = withoutEnd.indexOf("<");
			if (typeParamStart >= 0) {
				beforeTypeParams = withoutEnd.substring(0, typeParamStart);
				String substring = withoutEnd.substring(typeParamStart + 1);
				typeParams = splitValues(substring);
			}
			else {
				typeParams = Lists.empty();
			}
		}
		else {
			typeParams = Lists.empty();
		}
		String strippedBeforeTypeParams = beforeTypeParams.strip();	String modifiersString;

		int annotationSeparator = strippedBeforeTypeParams.lastIndexOf("\n");
		if (annotationSeparator >= 0) {
			modifiersString = strippedBeforeTypeParams.substring(annotationSeparator + "\n".length());
		}
		else {
			modifiersString = strippedBeforeTypeParams;
		}
		int allSymbols = Arrays.stream(modifiersString.split(Pattern.quote(" "))).map(__lambda104__).filter(__lambda105__).allMatch(__lambda106__);
		if (!allSymbols) {
			return Optional.empty();
		}
		String inputType = beforeName.substring(typeSeparator + " ".length());
		return compileType(inputType, typeParams).flatMap(__lambda107__);
	}
	else {
		return compileType(beforeName, Lists.empty()).flatMap(__lambda108__);
	}
}
auto __lambda109__ {
	return String.strip()
}
auto __lambda110__(auto param) {
	return !param.isEmpty();
}
List_<String> splitValues(String substring) {
	String* paramsArrays = substring.strip().split(Pattern.quote(","));
	return Iterators.from(paramsArrays).map(__lambda109__).filter(__lambda110__).collect(ListCollector<>());
}
String generateDefinition(List_<String> maybeTypeParams, String type, String name) {
	return generateTypeParams(maybeTypeParams) + type + " " + name;
}
auto __lambda111__(auto result) {
	return "<" + result + "> ";
}
String generateTypeParams(List_<String> maybeTypeParams) {
	if (maybeTypeParams.isEmpty()) {
		return "";
	}
	return maybeTypeParams.iter().collect(Joiner(", ")).map(__lambda111__).orElse("");
}
auto __lambda112__(auto value) {
	return value + "*";
}
auto __lambda113__ {
	return String.equals()
}
auto __lambda114__ {
	return compileType(type, typeParams);
}
auto __lambda115__(auto type) {
	return compileWhitespace(type).or(__lambda114__);
}
auto __lambda116__(auto compiled) {
	return base + " < " + compiled + ">";
}
Optional<String> compileType(String input, List_<String> typeParams) {
	if (input.equals("void")) {
		return Optional.of("void");
	}
	if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
		return Optional.of("int");
	}
	if (input.equals("char") || input.equals("Character")) {
		return Optional.of("char");
	}
	if (input.endsWith("[]")) {
		return compileType(input.substring(0, input.length() - "[]".length()), typeParams).map(__lambda112__);
	}
	String stripped = input.strip();
	if (isSymbol(stripped)) {
		if (Lists.contains(typeParams, stripped, __lambda113__)) {
			return Optional.of(stripped);
		}
		else {
			return Optional.of("struct " + stripped);
		}
	}
	if (stripped.endsWith(">")) {
		String slice = stripped.substring(0, stripped.length() - ">".length());
		int argsStart = slice.indexOf("<");
		if (argsStart >= 0) {
			String base = slice.substring(0, argsStart).strip();
			String params = slice.substring(argsStart + " < ".length()).strip();
			return compileValues(params, __lambda115__).map(__lambda116__);
		}
	}
	return generatePlaceholder(input);
}
auto __lambda117__(auto index) {
	char c = input.charAt(index);
	return c == '_' || Character.isLetter(c) ||(index != 0 && Character.isDigit(c));
}
int isSymbol(String input) {
	if (input.isBlank()) {
		return false;
	}
	return IntStream.range(0, input.length()).allMatch(__lambda117__);
}
Optional<String> generatePlaceholder(String input) {
	return Optional.of("/* " + input + " */");
}
