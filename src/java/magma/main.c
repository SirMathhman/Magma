struct Option {
	<R> Option_R map(Function_struct T, struct R mapper);
	struct T orElse(struct T other);
	int isPresent();
	int isEmpty();
	void ifPresent(Consumer_struct T consumer);
	Option_struct T or(Supplier_Option_struct T supplier);
	<R> Option_R flatMap(Function_struct T, Option_struct R mapper);
};
struct List_ {
	List__struct T add(struct T element);
	void addAll(List__struct T elements);
	Iterator_struct T iter();
	Option_Tuple_struct T, List__struct T popFirst();
	struct T pop();
	int isEmpty();
	struct T peek();
	int size();
	List__struct T slice(int startInclusive, int endExclusive);
	struct T get(int index);
};
struct Iterator {
	<R> R fold(struct R initial, BiFunction_struct R, struct T, struct R folder);
	<R> Iterator_R map(Function_struct T, struct R mapper);
	<C> C collect(Collector_struct T, struct C collector);
	int anyMatch(Predicate_struct T predicate);
	void forEach(Consumer_struct T consumer);
	Iterator_struct T filter(Predicate_struct T predicate);
	int allMatch(Predicate_struct T predicate);
	Iterator_struct T concat(Iterator_struct T other);
	Option_struct T next();
};
struct Head {
	Option_struct T next();
};
struct Collector {
	struct C createInitial();
	struct C fold(struct C current, struct T element);
};
struct Result {
	<R> R match(Function_struct T, struct R whenOk, Function_struct X, struct R whenErr);
};
struct IOError {
	struct String display();
};
struct Path_ {
	struct Path_ resolveSibling(struct String sibling);
	List__struct String listNames();
};
struct Err {
};
struct Ok {
};
struct State {	List__char queue;
	List__struct String segments;
	struct StringBuilder buffer;
	int depth;

};
struct Tuple {
};
struct None {
};
struct Some {
};
struct Joiner(String delimiter) implements Collector {
};
struct RangeHead implements Head {	int length;

};
struct HeadedIterator {
};
struct EmptyHead {
};
struct Iterators {
};
struct ListCollector {
};
struct SingleHead {	struct T value;

};
struct ", Impl.emptyList());
        if (maybeClass.isPresent()) {	struct return maybeClass;
/* 
        }

        return generatePlaceholder(input);
     */
};
struct Main {
};
int counter = 0;
int retrieved = false;
List__struct String imports = Impl.emptyList();
List__struct String structs = Impl.emptyList();
List__struct String globals = Impl.emptyList();
List__struct String methods = Impl.emptyList();
int counter = 0;
<R> R match(Function_struct T, struct R whenOk, Function_struct X, struct R whenErr) {
	return whenErr.apply(this.error);
}
<R> R match(Function_struct T, struct R whenOk, Function_struct X, struct R whenErr) {
	return whenOk.apply(this.value);
}
struct private State(List__char queue, List__struct String segments, struct StringBuilder buffer, int depth) {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
struct public State(List__char queue) {
	this(queue, Impl.emptyList(), struct StringBuilder(), 0);
}
struct State advance() {
	this.segments.add(this.buffer.toString());
	this.buffer = struct StringBuilder();
	return this;
}
struct State append(char c) {
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
<R> Option_R map(Function_struct T, struct R mapper) {
	return None_();
}
struct T orElse(struct T other) {
	return other;
}
int isPresent() {
	return false;
}
int isEmpty() {
	return true;
}
void ifPresent(Consumer_struct T consumer) {
}
Option_struct T or(Supplier_Option_struct T supplier) {
	return supplier.get();
}
<R> Option_R flatMap(Function_struct T, Option_struct R mapper) {
	return None_();
}
<R> Option_R map(Function_struct T, struct R mapper) {
	return Some_(mapper.apply(this.value));
}
struct T orElse(struct T other) {
	return this.value;
}
int isPresent() {
	return true;
}
int isEmpty() {
	return false;
}
void ifPresent(Consumer_struct T consumer) {
	consumer.accept(this.value);
}
Option_struct T or(Supplier_Option_struct T supplier) {
	return this;
}
<R> Option_R flatMap(Function_struct T, Option_struct R mapper) {
	return mapper.apply(this.value);
}
Option_struct String createInitial() {
	return None_();
}
auto __lambda0__(auto inner) {
	return inner + this.delimiter + element;
}
Option_struct String fold(Option_struct String current, struct String element) {
	return Some_(current.map(__lambda0__).orElse(element));
}
struct public RangeHead(int length) {
	this.length = length;
}
Option_int next() {
	if (this.counter >= this.length) {
		return None_();
	}
	int value = this.counter;this.counter++;
	return Some_(value);
}
auto __lambda1__(auto next) {
	return folder.apply(finalCurrent, next);
}
<R> R fold(struct R initial, BiFunction_struct R, struct T, struct R folder) {
	struct R current = initial;
	while (true) {
		struct R finalCurrent = current;
		Option_struct R maybeCurrent = this.head.next().map(__lambda1__);
		if (maybeCurrent.isPresent()) {
			current = maybeCurrent.orElse(null);
		}
		else {
			return current;
		}
	}
}
auto __lambda2__() {
	return this.head.next().map(mapper);
}
<R> Iterator_R map(Function_struct T, struct R mapper) {
	return HeadedIterator_(__lambda2__);
}
auto __lambda3__() {
	return struct collector.fold()
}
<C> C collect(Collector_struct T, struct C collector) {
	return this.fold(collector.createInitial(), __lambda3__);
}
auto __lambda4__(auto aBoolean, auto t) {
	return aBoolean || predicate.test(t);
}
int anyMatch(Predicate_struct T predicate) {
	return this.fold(false, __lambda4__);
}
void forEach(Consumer_struct T consumer) {
	while (true) {
		Option_struct T next = this.head.next();
		if (next.isEmpty()) {
			break;
		}
		next.ifPresent(consumer);
	}
}
auto __lambda5__(auto value) {
	return HeadedIterator_(predicate.test(value)
                    ? new SingleHead<>(value)
                    : new EmptyHead<>());
}
Iterator_struct T filter(Predicate_struct T predicate) {
	return this.flatMap(__lambda5__);
}
auto __lambda6__(auto aBoolean, auto t) {
	return aBoolean && predicate.test(t);
}
int allMatch(Predicate_struct T predicate) {
	return this.fold(true, __lambda6__);
}
auto __lambda7__() {
	return struct other.next()
}
auto __lambda8__() {
	return this.head.next().or(__lambda7__);
}
Iterator_struct T concat(Iterator_struct T other) {
	return HeadedIterator_(__lambda8__);
}
Option_struct T next() {
	return this.head.next();
}
auto __lambda9__() {
	return struct Iterator.concat()
}
<R> Iterator_R flatMap(Function_struct T, Iterator_struct R mapper) {
	return this.map(mapper).fold(Iterators.empty(), __lambda9__);
}
Option_struct T next() {
	return None_();
}
<T> Iterator_T fromArray(struct T* array) {
	return HeadedIterator_(struct RangeHead(array.length)).map(index -> array[index]);
}
<T> Iterator_T empty() {
	return HeadedIterator_(EmptyHead_());
}
auto __lambda10__() {
	return struct Tuple.right()
}
Iterator_char fromString(struct String string) {
	return fromStringWithIndices(string).map(__lambda10__);
}
Iterator_Tuple_int, struct Character fromStringWithIndices(struct String string) {
	return HeadedIterator_(struct RangeHead(string.length())).map(index -> new Tuple<>(index, string.charAt(index)));
}
List__struct T createInitial() {
	return Impl.emptyList();
}
List__struct T fold(List__struct T current, struct T element) {
	return current.add(element);
}
struct public SingleHead(struct T value) {
	this.value = value;
}
Option_struct T next() {
	if (this.retrieved) {
		return None_();
	}
	this.retrieved = true;
	return Some_(this.value);
}
auto __lambda11__(auto input) {
	return compileAndWrite(input, source);
}
auto __lambda12__() {
	return struct Some.new()
}
auto __lambda13__() {
	return struct IOError.display()
}
void main(struct String* args) {
	struct Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
	Impl.readString(source).match(__lambda11__, __lambda12__).ifPresent(__lambda13__);
}
Option_struct IOError compileAndWrite(struct String input, struct Path_ source) {
	struct Path_ target = source.resolveSibling("main.c");
	struct String output = compile(input);
	return Impl.writeString(target, output);
}
auto __lambda14__() {
	return struct Main.divideStatementChar()
}
auto __lambda15__() {
	return struct Main.compileRootSegment()
}
auto __lambda16__(auto list) {
	List__struct String copy = Impl.emptyList();
	copy.addAll(imports);
	copy.addAll(structs);
	copy.addAll(globals);
	copy.addAll(methods);
	copy.addAll(list);
	return copy;
}
auto __lambda17__() {
	return struct Main.mergeStatements()
}
auto __lambda18__(auto compiled) {
	return mergeAll(compiled, __lambda17__);
}
auto __lambda19__() {
	return generatePlaceholder(input);
}
struct String compile(struct String input) {
	List__struct String segments = divide(input, __lambda14__);
	return parseAll(segments, __lambda15__).map(__lambda16__).map(__lambda18__).or(__lambda19__).orElse("");
}
auto __lambda20__() {
	return struct Main.divideStatementChar()
}
auto __lambda21__() {
	return struct Main.mergeStatements()
}
Option_struct String compileStatements(struct String input, Function_struct String, Option_struct String compiler) {
	return compileAndMerge(divide(input, __lambda20__), compiler, __lambda21__);
}
auto __lambda22__(auto compiled) {
	return mergeAll(compiled, merger);
}
Option_struct String compileAndMerge(List__struct String segments, Function_struct String, Option_struct String compiler, BiFunction_struct StringBuilder, struct String, struct StringBuilder merger) {
	return parseAll(segments, compiler).map(__lambda22__);
}
struct String mergeAll(List__struct String compiled, BiFunction_struct StringBuilder, struct String, struct StringBuilder merger) {
	return compiled.iter().fold(struct StringBuilder(), merger).toString();
}
auto __lambda23__(auto compiledSegment) {
	allCompiled.add(compiledSegment);
	return allCompiled;
}
auto __lambda24__(auto allCompiled) {
	return compiler.apply(segment).map(__lambda23__);
}
auto __lambda25__(auto maybeCompiled, auto segment) {
	return maybeCompiled.flatMap(__lambda24__);
}
Option_List__struct String parseAll(List__struct String segments, Function_struct String, Option_struct String compiler) {
	return segments.iter().<Option<List_<String>>>fold(Some_(Impl.emptyList()), __lambda25__);
}
struct StringBuilder mergeStatements(struct StringBuilder output, struct String compiled) {
	return output.append(compiled);
}
List__struct String divide(struct String input, BiFunction_struct State, struct Character, struct State divider) {
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
struct State divideStatementChar(struct State state, char c) {
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
int isShallow(struct State state) {
	return state.depth == 1;
}
List__struct String splitByDelimiter(struct String content, char delimiter) {
	List__struct String segments = Impl.emptyList();
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
auto __lambda26__(auto input1) {
	return compileClassMember(input1, typeParams);
}
auto __lambda27__(auto outputContent) {
	structs.add("struct " + name + " {" + outputContent + "\n};\n");
	return "";
}
Option_struct String compileToStruct(struct String input, struct String infix, List__struct String typeParams) {
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
	int typeStartIndex = beforeContent.indexOf("<");
	struct String name = typeStartIndex >= /*  0
                ? beforeContent */.substring(0, typeStartIndex)
                : beforeContent;
	struct String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
	if (!withEnd.endsWith("}")) {
		return None_();
	}
	struct String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
	return compileStatements(inputContent, __lambda26__).map(__lambda27__);
}
auto __lambda28__() {
	return compileToStruct(input, "interface ", typeParams);
}
auto __lambda29__() {
	return compileToStruct(input, "record ", typeParams);
}
auto __lambda30__() {
	return compileToStruct(input, "class ", typeParams);
}
auto __lambda31__() {
	return compileGlobalInitialization(input, typeParams);
}
auto __lambda32__() {
	return compileDefinitionStatement(input);
}
auto __lambda33__() {
	return compileMethod(input, typeParams);
}
auto __lambda34__() {
	return generatePlaceholder(input);
}
Option_struct String compileClassMember(struct String input, List__struct String typeParams) {
	return compileWhitespace(input).or(__lambda28__).or(__lambda29__).or(__lambda30__).or(__lambda31__).or(__lambda32__).or(__lambda33__).or(__lambda34__);
}
auto __lambda35__(auto result) {
	return "\t" + result + ";\n";
}
Option_struct String compileDefinitionStatement(struct String input) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String content = stripped.substring(0, stripped.length() - ";".length());
		return compileDefinition(content).map(__lambda35__);
	}
	return None_();
}
auto __lambda36__(auto generated) {
	globals.add(generated + ";\n");
	return "";
}
Option_struct String compileGlobalInitialization(struct String input, List__struct String typeParams) {
	return compileInitialization(input, typeParams, 0).map(__lambda36__);
}
auto __lambda37__(auto outputValue) {
	return outputDefinition + " = " + outputValue;
}
auto __lambda38__(auto outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda37__);
}
Option_struct String compileInitialization(struct String input, List__struct String typeParams, int depth) {
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
	return compileDefinition(definition).flatMap(__lambda38__);
}
Option_struct String compileWhitespace(struct String input) {
	if (input.isBlank()) {
		return Some_("");
	}
	return None_();
}
auto __lambda39__() {
	return struct Main.compileParameter()
}
Option_struct String compileMethod(struct String input, List__struct String typeParams) {
	int paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return None_();
	}
	struct String inputDefinition = input.substring(0, paramStart).strip();
	struct String withParams = input.substring(paramStart + "(".length());
	return compileDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return new None<>();
            }

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, __lambda39__).flatMap(outputParams -> assembleMethodBody(typeParams, outputDefinition, outputParams, withParams.substring(paramEnd + ")".length()).strip()));
        });
}
auto __lambda40__(auto input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
auto __lambda41__(auto outputContent) {
		methods.add(header + " {" + outputContent + "\n}\n");
		return Some_("");
}
Option_struct String assembleMethodBody(List__struct String typeParams, struct String definition, struct String params, struct String body) {
	struct String header = "\t".repeat(0) + definition + "(" + params + ")";
	if (body.startsWith("{") && body.endsWith("}")) {
		struct String inputContent = body.substring("{".length(), body.length() - "}".length());
		return compileStatements(inputContent, __lambda40__).flatMap(__lambda41__);
	}
	return Some_("\n\t" + header + ";");
}
auto __lambda42__() {
	return compileDefinition(definition);
}
auto __lambda43__() {
	return generatePlaceholder(definition);
}
Option_struct String compileParameter(struct String definition) {
	return compileWhitespace(definition).or(__lambda42__).or(__lambda43__);
}
auto __lambda44__() {
	return struct Main.divideValueChar()
}
Option_struct String compileValues(struct String input, Function_struct String, Option_struct String compiler) {
	List__struct String divided = divide(input, __lambda44__);
	return compileValues(divided, compiler);
}
struct State divideValueChar(struct State state, char c) {
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
auto __lambda45__() {
	return struct Main.mergeValues()
}
Option_struct String compileValues(List__struct String params, Function_struct String, Option_struct String compiler) {
	return compileAndMerge(params, compiler, __lambda45__);
}
auto __lambda46__() {
	return compileKeywordStatement(input, depth, "continue");
}
auto __lambda47__() {
	return compileKeywordStatement(input, depth, "break");
}
auto __lambda48__() {
	return compileConditional(input, typeParams, "if ", depth);
}
auto __lambda49__() {
	return compileConditional(input, typeParams, "while ", depth);
}
auto __lambda50__() {
	return compileElse(input, typeParams, depth);
}
auto __lambda51__() {
	return compilePostOperator(input, typeParams, depth, "++");
}
auto __lambda52__() {
	return compilePostOperator(input, typeParams, depth, "--");
}
auto __lambda53__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda54__() {
	return compileReturn(input, typeParams, depth).map(__lambda53__);
}
auto __lambda55__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda56__() {
	return compileInitialization(input, typeParams, depth).map(__lambda55__);
}
auto __lambda57__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda58__() {
	return compileAssignment(input, typeParams, depth).map(__lambda57__);
}
auto __lambda59__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda60__() {
	return compileInvocationStatement(input, typeParams, depth).map(__lambda59__);
}
auto __lambda61__() {
	return compileDefinitionStatement(input);
}
auto __lambda62__() {
	return generatePlaceholder(input);
}
Option_struct String compileStatementOrBlock(struct String input, List__struct String typeParams, int depth) {
	return compileWhitespace(input).or(__lambda46__).or(__lambda47__).or(__lambda48__).or(__lambda49__).or(__lambda50__).or(__lambda51__).or(__lambda52__).or(__lambda54__).or(__lambda56__).or(__lambda58__).or(__lambda60__).or(__lambda61__).or(__lambda62__);
}
auto __lambda63__(auto value) {
	return value + operator + ";";
}
Option_struct String compilePostOperator(struct String input, List__struct String typeParams, int depth, struct String operator) {
	struct String stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		struct String slice = stripped.substring(0, stripped.length() -(operator + ";").length());
		return compileValue(slice, typeParams, depth).map(__lambda63__);
	}
	else {
		return None_();
	}
}
auto __lambda64__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda65__(auto result) {
	return indent + "else {" + result + indent + "}";
}
auto __lambda66__(auto result) {
	return "else " + result;
}
Option_struct String compileElse(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.startsWith("else ")) {
		struct String withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			struct String indent = createIndent(depth);
			return compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), __lambda64__).map(__lambda65__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda66__);
		}
	}
	return None_();
}
Option_struct String compileKeywordStatement(struct String input, int depth, struct String keyword) {
	if (input.strip().equals(keyword + ";")) {
		return Some_(formatStatement(depth, keyword));
	}
	else {
		return None_();
	}
}
struct String formatStatement(int depth, struct String value) {
	return createIndent(depth) + value + ";";
}
struct String createIndent(int depth) {
	return "\n" + "\t".repeat(depth);
}
auto __lambda67__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda68__(auto statements) {
		return withCondition + " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
}
auto __lambda69__(auto result) {
		return withCondition + " " + result;
}
auto __lambda70__(auto newCondition) {
	struct String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		struct String content = withBraces.substring(1, withBraces.length() - 1);
		return compileStatements(content, __lambda67__).map(__lambda68__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda69__);
	}
}
Option_struct String compileConditional(struct String input, List__struct String typeParams, struct String prefix, int depth) {
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
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda70__);
}
int findConditionEnd(struct String input) {
	int conditionEnd = -1;
	int depth0 = 0;
	List__Tuple_int, struct Character queue = Iterators.fromStringWithIndices(input).collect(ListCollector_());
	while (!queue.isEmpty()) {
		Tuple_int, struct Character pair = queue.pop();
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
				Tuple_int, struct Character next = queue.pop();
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
Option_struct String compileInvocationStatement(struct String input, List__struct String typeParams, int depth) {
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
auto __lambda71__(auto newSource) {
			return newDest + " = " + newSource;
}
auto __lambda72__(auto newDest) {
			return compileValue(source, typeParams, depth).map(__lambda71__);
}
Option_struct String compileAssignment(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			struct String destination = withoutEnd.substring(0, valueSeparator).strip();
			struct String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda72__);
		}
	}
	return None_();
}
auto __lambda73__(auto result) {
	return "return " + result;
}
Option_struct String compileReturn(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda73__);
		}
	}
	return None_();
}
auto __lambda74__(auto value) {
	return outputType + value;
}
auto __lambda75__(auto outputType) {
	return compileArgs(argsString, typeParams, depth).map(__lambda74__);
}
auto __lambda76__(auto result) {
	return "!" + result;
}
auto __lambda77__(auto compiled) {
			return generateLambdaWithReturn(Impl.emptyList(), "\n\treturn " + compiled + "." + property + "()");
}
auto __lambda78__(auto compiled) {
	return compiled + "." + property;
}
auto __lambda79__() {
	return compileOperator(input, typeParams, depth, "<");
}
auto __lambda80__() {
	return compileOperator(input, typeParams, depth, "+");
}
auto __lambda81__() {
	return compileOperator(input, typeParams, depth, ">=");
}
auto __lambda82__() {
	return compileOperator(input, typeParams, depth, "&&");
}
auto __lambda83__() {
	return compileOperator(input, typeParams, depth, "==");
}
auto __lambda84__() {
	return compileOperator(input, typeParams, depth, "!=");
}
auto __lambda85__() {
	return generatePlaceholder(input);
}
Option_struct String compileValue(struct String input, List__struct String typeParams, int depth) {
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
				return compileType(type, typeParams).flatMap(__lambda75__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda76__);
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
			return compileType(type, typeParams).flatMap(__lambda77__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		struct String object = input.substring(0, separator).strip();
		struct String property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda78__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda79__).or(__lambda80__).or(__lambda81__).or(__lambda82__).or(__lambda83__).or(__lambda84__).or(__lambda85__);
}
auto __lambda86__(auto rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
auto __lambda87__(auto leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda86__);
}
Option_struct String compileOperator(struct String input, List__struct String typeParams, int depth, struct String operator) {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return None_();
	}
	struct String left = input.substring(0, operatorIndex);
	struct String right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda87__);
}
auto __lambda88__() {
	return struct String.strip()
}
auto __lambda89__(auto value) {
	return !value.isEmpty();
}
auto __lambda90__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
auto __lambda91__(auto result) {
		return generateLambdaWithReturn(paramNames, result);
}
auto __lambda92__(auto newValue) {
	return generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";");
}
Option_struct String compileLambda(struct String input, List__struct String typeParams, int depth) {
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
		paramNames = splitByDelimiter(inner, ',').iter().map(__lambda88__).filter(__lambda89__).collect(ListCollector_());
	}
	else {
		return None_();
	}
	struct String value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		struct String slice = value.substring(1, value.length() - 1);
		return compileStatements(slice, __lambda90__).flatMap(__lambda91__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda92__);
}
auto __lambda93__(auto name) {
	return "auto " + name;
}
Option_struct String generateLambdaWithReturn(List__struct String paramNames, struct String returnValue) {
	int current = counter;counter++;
	struct String lambdaName = "__lambda" + current + "__";
	struct String joinedLambdaParams = paramNames.iter().map(__lambda93__).collect(struct Joiner(", ")).orElse("");
	methods.add("auto " + lambdaName + "(" + joinedLambdaParams + ")" + " {" + returnValue + "\n}\n");
	return Some_(lambdaName);
}
auto __lambda94__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return (index == 0 && c == '-') || Character.isDigit(c);
}
int isNumber(struct String input) {
	return Iterators.fromStringWithIndices(input).allMatch(__lambda94__);
}
auto __lambda95__(auto value) {
	return caller + value;
}
auto __lambda96__(auto caller) {
			return compileArgs(withEnd, typeParams, depth).map(__lambda95__);
}
Option_struct String compileInvocation(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(")")) {
		struct String sliced = stripped.substring(0, stripped.length() - ")".length());
		int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			struct String type = sliced.substring(0, argsStart);
			struct String withEnd = sliced.substring(argsStart + "(".length()).strip();
			return compileValue(type, typeParams, depth).flatMap(__lambda96__);
		}
	}
	return None_();
}
int findInvocationStart(struct String sliced) {
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
auto __lambda97__() {
	return compileValue(arg, typeParams, depth);
}
auto __lambda98__(auto arg) {
	return compileWhitespace(arg).or(__lambda97__);
}
auto __lambda99__(auto args) {
	return "(" + args + ")";
}
Option_struct String compileArgs(struct String argsString, List__struct String typeParams, int depth) {
	return compileValues(argsString, __lambda98__).map(__lambda99__);
}
struct StringBuilder mergeValues(struct StringBuilder cache, struct String element) {
	if (cache.isEmpty()) {
		return cache.append(element);
	}
	return cache.append(", ").append(element);
}
auto __lambda100__() {
	return struct String.strip()
}
auto __lambda101__(auto value) {
	return !value.isEmpty();
}
auto __lambda102__() {
	return struct Main.isSymbol()
}
auto __lambda103__(auto outputType) {
	return Some_(generateDefinition(typeParams, outputType, name));
}
auto __lambda104__(auto outputType) {
	return Some_(generateDefinition(Impl.emptyList(), outputType, name));
}
Option_struct String compileDefinition(struct String definition) {
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
		struct String beforeType = beforeName.substring(0, typeSeparator).strip();
		struct String beforeTypeParams = beforeType;	List__struct String typeParams;

		if (beforeType.endsWith(">")) {
			struct String withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
			int typeParamStart = withoutEnd.indexOf("<");
			if (typeParamStart >= 0) {
				beforeTypeParams = withoutEnd.substring(0, typeParamStart);
				struct String substring = withoutEnd.substring(typeParamStart + 1);
				typeParams = splitValues(substring);
			}
			else {
				typeParams = Impl.emptyList();
			}
		}
		else {
			typeParams = Impl.emptyList();
		}
		struct String strippedBeforeTypeParams = beforeTypeParams.strip();	struct String modifiersString;

		int annotationSeparator = strippedBeforeTypeParams.lastIndexOf("\n");
		if (annotationSeparator >= 0) {
			modifiersString = strippedBeforeTypeParams.substring(annotationSeparator + "\n".length());
		}
		else {
			modifiersString = strippedBeforeTypeParams;
		}
		int allSymbols = splitByDelimiter(modifiersString, ' ').iter().map(__lambda100__).filter(__lambda101__).allMatch(__lambda102__);
		if (!allSymbols) {
			return None_();
		}
		struct String inputType = beforeName.substring(typeSeparator + " ".length());
		return compileType(inputType, typeParams).flatMap(__lambda103__);
	}
	else {
		return compileType(beforeName, Impl.emptyList()).flatMap(__lambda104__);
	}
}
auto __lambda105__() {
	return struct String.strip()
}
auto __lambda106__(auto param) {
	return !param.isEmpty();
}
List__struct String splitValues(struct String substring) {
	return splitByDelimiter(substring.strip(), ',').iter().map(__lambda105__).filter(__lambda106__).collect(ListCollector_());
}
auto __lambda107__(auto inner) {
	return "<" + inner + "> ";
}
struct String generateDefinition(List__struct String maybeTypeParams, struct String type, struct String name) {
	struct String typeParamsString = maybeTypeParams.iter().collect(struct Joiner(", ")).map(__lambda107__).orElse("");
	return typeParamsString + type + " " + name;
}
auto __lambda108__(auto value) {
	return value + "*";
}
auto __lambda109__() {
	return struct String.equals()
}
auto __lambda110__() {
	return compileType(type, typeParams);
}
auto __lambda111__(auto type) {
			return compileWhitespace(type).or(__lambda110__);
}
auto __lambda112__(auto compiled) {
			return base + "_" + compiled;
}
Option_struct String compileType(struct String input, List__struct String typeParams) {
	if (input.equals("void")) {
		return Some_("void");
	}
	if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
		return Some_("int");
	}
	if (input.equals("char") || input.equals("Character")) {
		return Some_("char");
	}
	if (input.endsWith("[]")) {
		return compileType(input.substring(0, input.length() - "[]".length()), typeParams).map(__lambda108__);
	}
	struct String stripped = input.strip();
	if (isSymbol(stripped)) {
		if (Impl.contains(typeParams, stripped, __lambda109__)) {
			return Some_(stripped);
		}
		else {
			return Some_("struct " + stripped);
		}
	}
	if (stripped.endsWith(">")) {
		struct String slice = stripped.substring(0, stripped.length() - ">".length());
		int argsStart = slice.indexOf("<");
		if (argsStart >= 0) {
			struct String base = slice.substring(0, argsStart).strip();
			struct String params = slice.substring(argsStart + " < ".length()).strip();
			return compileValues(params, __lambda111__).map(__lambda112__);
		}
	}
	return generatePlaceholder(input);
}
auto __lambda113__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return c == '_' || Character.isLetter(c) ||(index != 0 && Character.isDigit(c));
}
int isSymbol(struct String input) {
	if (input.isBlank()) {
		return false;
	}
	return Iterators.fromStringWithIndices(input).allMatch(__lambda113__);
}
Option_struct String generatePlaceholder(struct String input) {
	return Some_("/* " + input + " */");
}
