#include "./java/util/HashMap"
#include "./java/util/Map"
struct IOError {
	struct String display();
};
struct Path_ {
	struct Path_ resolveSibling(struct String sibling);
	List_<struct String> listNames();
};
struct State {
	List_<char> queue;
	List_<struct String> segments;
	struct StringBuilder buffer;
	int depth;
};
struct Iterators {
};
struct ", Impl.emptyList());
        if (maybeClass.isPresent()) {
	struct return maybeClass;
/* 
        }

        return generatePlaceholder(input);
     */};
struct Main {
};
List_<struct String> imports = Impl.emptyList();
List_<struct String> structs = Impl.emptyList();
List_<struct String> globals = Impl.emptyList();
List_<struct String> methods = Impl.emptyList();
int counter = 0;
struct private State(List_<char> queue, List_<struct String> segments, struct StringBuilder buffer, int depth) {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
struct public State(List_<char> queue) {
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
List_<struct String> segments() {
	return this.segments;
}
char peek() {
	return this.queue.peek();
}
<T> Iterator<T> fromArray(struct T* array) {
	return HeadedIterator<>(struct RangeHead(array.length)).map(index -> array[index]);
}
<T> Iterator<T> empty() {
	return HeadedIterator<>(EmptyHead<>());
}
auto __lambda0__() {
	return struct Tuple.right()
}
Iterator<char> fromString(struct String string) {
	return fromStringWithIndices(string).map(__lambda0__);
}
Iterator<Tuple<int, struct Character>> fromStringWithIndices(struct String string) {
	return HeadedIterator<>(struct RangeHead(string.length())).map(index -> new Tuple<>(index, string.charAt(index)));
}
auto __lambda1__(auto input) {
	return compileAndWrite(input, source);
}
auto __lambda2__() {
	return struct Some.new()
}
auto __lambda3__() {
	return struct IOError.display()
}
void main(struct String* args) {
	struct Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
	Impl.readString(source).match(__lambda1__, __lambda2__).ifPresent(__lambda3__);
}
Option<struct IOError> compileAndWrite(struct String input, struct Path_ source) {
	struct Path_ target = source.resolveSibling("main.c");
	struct String output = compile(input);
	return Impl.writeString(target, output);
}
auto __lambda4__() {
	return struct Main.divideStatementChar()
}
auto __lambda5__() {
	return struct Main.compileRootSegment()
}
auto __lambda6__(auto list) {
	List_<struct String> copy = Impl.emptyList();
	copy.addAll(imports);
	copy.addAll(structs);
	copy.addAll(globals);
	copy.addAll(methods);
	copy.addAll(list);
	return copy;
}
auto __lambda7__() {
	return struct Main.mergeStatements()
}
auto __lambda8__(auto compiled) {
	return mergeAll(compiled, __lambda7__);
}
auto __lambda9__() {
	return generatePlaceholder(input);
}
struct String compile(struct String input) {
	List_<struct String> segments = divide(input, __lambda4__);
	return parseAll(segments, __lambda5__).map(__lambda6__).map(__lambda8__).or(__lambda9__).orElse("");
}
auto __lambda10__() {
	return struct Main.divideStatementChar()
}
auto __lambda11__() {
	return struct Main.mergeStatements()
}
Option<struct String> compileStatements(struct String input, Function<struct String, Option<struct String>> compiler) {
	return compileAndMerge(divide(input, __lambda10__), compiler, __lambda11__);
}
auto __lambda12__(auto compiled) {
	return mergeAll(compiled, merger);
}
Option<struct String> compileAndMerge(List_<struct String> segments, Function<struct String, Option<struct String>> compiler, BiFunction<struct StringBuilder, struct String, struct StringBuilder> merger) {
	return parseAll(segments, compiler).map(__lambda12__);
}
struct String mergeAll(List_<struct String> compiled, BiFunction<struct StringBuilder, struct String, struct StringBuilder> merger) {
	return compiled.iter().fold(struct StringBuilder(), merger).toString();
}
auto __lambda13__(auto compiledSegment) {
	allCompiled.add(compiledSegment);
	return allCompiled;
}
auto __lambda14__(auto allCompiled) {
	return compiler.apply(segment).map(__lambda13__);
}
auto __lambda15__(auto maybeCompiled, auto segment) {
	return maybeCompiled.flatMap(__lambda14__);
}
Option<List_<struct String>> parseAll(List_<struct String> segments, Function<struct String, Option<struct String>> compiler) {
	return segments.iter().<Option<List_<String>>>fold(Some<>(Impl.emptyList()), __lambda15__);
}
struct StringBuilder mergeStatements(struct StringBuilder output, struct String compiled) {
	return output.append(compiled);
}
List_<struct String> divide(struct String input, BiFunction<struct State, struct Character, struct State> divider) {
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
List_<struct String> splitByDelimiter(struct String content, char delimiter) {
	List_<struct String> segments = Impl.emptyList();
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
auto __lambda16__(auto input1) {
	return compileClassMember(input1, typeParams);
}
auto __lambda17__(auto outputContent) {
	structs.add("struct " + name + " {\n" + outputContent + "};\n");
	return "";
}
Option<struct String> compileToStruct(struct String input, struct String infix, List_<struct String> typeParams) {
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
	int typeParamStart = beforeContent.indexOf("<");	struct String name;

	if (typeParamStart >= 0) {
		return Some<>("");
	}
	else {
		name = beforeContent;
	}
	struct String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
	if (!withEnd.endsWith("}")) {
		return None<>();
	}
	struct String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
	return compileStatements(inputContent, __lambda16__).map(__lambda17__);
}
auto __lambda18__() {
	return compileToStruct(input, "interface ", typeParams);
}
auto __lambda19__() {
	return compileToStruct(input, "record ", typeParams);
}
auto __lambda20__() {
	return compileToStruct(input, "class ", typeParams);
}
auto __lambda21__() {
	return compileGlobalInitialization(input, typeParams);
}
auto __lambda22__() {
	return compileDefinitionStatement(input);
}
auto __lambda23__() {
	return compileMethod(input, typeParams);
}
auto __lambda24__() {
	return generatePlaceholder(input);
}
Option<struct String> compileClassMember(struct String input, List_<struct String> typeParams) {
	return compileWhitespace(input).or(__lambda18__).or(__lambda19__).or(__lambda20__).or(__lambda21__).or(__lambda22__).or(__lambda23__).or(__lambda24__);
}
auto __lambda25__(auto result) {
	return "\t" + result + ";\n";
}
Option<struct String> compileDefinitionStatement(struct String input) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String content = stripped.substring(0, stripped.length() - ";".length());
		return compileDefinition(content).map(__lambda25__);
	}
	return None<>();
}
auto __lambda26__(auto generated) {
	globals.add(generated + ";\n");
	return "";
}
Option<struct String> compileGlobalInitialization(struct String input, List_<struct String> typeParams) {
	return compileInitialization(input, typeParams, 0).map(__lambda26__);
}
auto __lambda27__(auto outputValue) {
	return outputDefinition + " = " + outputValue;
}
auto __lambda28__(auto outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda27__);
}
Option<struct String> compileInitialization(struct String input, List_<struct String> typeParams, int depth) {
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
	return compileDefinition(definition).flatMap(__lambda28__);
}
Option<struct String> compileWhitespace(struct String input) {
	if (input.isBlank()) {
		return Some<>("");
	}
	return None<>();
}
auto __lambda29__() {
	return struct Main.compileParameter()
}
Option<struct String> compileMethod(struct String input, List_<struct String> typeParams) {
	int paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return None<>();
	}
	struct String inputDefinition = input.substring(0, paramStart).strip();
	struct String withParams = input.substring(paramStart + "(".length());
	return compileDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return new None<>();
            }

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, __lambda29__).flatMap(outputParams -> assembleMethodBody(typeParams, outputDefinition, outputParams, withParams.substring(paramEnd + ")".length()).strip()));
        });
}
auto __lambda30__(auto input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
auto __lambda31__(auto outputContent) {
		methods.add(header + " {" + outputContent + "\n}\n");
		return Some<>("");
}
Option<struct String> assembleMethodBody(List_<struct String> typeParams, struct String definition, struct String params, struct String body) {
	struct String header = "\t".repeat(0) + definition + "(" + params + ")";
	if (body.startsWith("{") && body.endsWith("}")) {
		struct String inputContent = body.substring("{".length(), body.length() - "}".length());
		return compileStatements(inputContent, __lambda30__).flatMap(__lambda31__);
	}
	return Some<>("\t" + header + ";\n");
}
auto __lambda32__() {
	return compileDefinition(definition);
}
auto __lambda33__() {
	return generatePlaceholder(definition);
}
Option<struct String> compileParameter(struct String definition) {
	return compileWhitespace(definition).or(__lambda32__).or(__lambda33__);
}
auto __lambda34__() {
	return struct Main.divideValueChar()
}
Option<struct String> compileValues(struct String input, Function<struct String, Option<struct String>> compiler) {
	List_<struct String> divided = divide(input, __lambda34__);
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
auto __lambda35__() {
	return struct Main.mergeValues()
}
Option<struct String> compileValues(List_<struct String> params, Function<struct String, Option<struct String>> compiler) {
	return compileAndMerge(params, compiler, __lambda35__);
}
auto __lambda36__() {
	return compileKeywordStatement(input, depth, "continue");
}
auto __lambda37__() {
	return compileKeywordStatement(input, depth, "break");
}
auto __lambda38__() {
	return compileConditional(input, typeParams, "if ", depth);
}
auto __lambda39__() {
	return compileConditional(input, typeParams, "while ", depth);
}
auto __lambda40__() {
	return compileElse(input, typeParams, depth);
}
auto __lambda41__() {
	return compilePostOperator(input, typeParams, depth, "++");
}
auto __lambda42__() {
	return compilePostOperator(input, typeParams, depth, "--");
}
auto __lambda43__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda44__() {
	return compileReturn(input, typeParams, depth).map(__lambda43__);
}
auto __lambda45__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda46__() {
	return compileInitialization(input, typeParams, depth).map(__lambda45__);
}
auto __lambda47__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda48__() {
	return compileAssignment(input, typeParams, depth).map(__lambda47__);
}
auto __lambda49__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda50__() {
	return compileInvocationStatement(input, typeParams, depth).map(__lambda49__);
}
auto __lambda51__() {
	return compileDefinitionStatement(input);
}
auto __lambda52__() {
	return generatePlaceholder(input);
}
Option<struct String> compileStatementOrBlock(struct String input, List_<struct String> typeParams, int depth) {
	return compileWhitespace(input).or(__lambda36__).or(__lambda37__).or(__lambda38__).or(__lambda39__).or(__lambda40__).or(__lambda41__).or(__lambda42__).or(__lambda44__).or(__lambda46__).or(__lambda48__).or(__lambda50__).or(__lambda51__).or(__lambda52__);
}
auto __lambda53__(auto value) {
	return value + operator + ";";
}
Option<struct String> compilePostOperator(struct String input, List_<struct String> typeParams, int depth, struct String operator) {
	struct String stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		struct String slice = stripped.substring(0, stripped.length() -(operator + ";").length());
		return compileValue(slice, typeParams, depth).map(__lambda53__);
	}
	else {
		return None<>();
	}
}
auto __lambda54__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda55__(auto result) {
	return indent + "else {" + result + indent + "}";
}
auto __lambda56__(auto result) {
	return "else " + result;
}
Option<struct String> compileElse(struct String input, List_<struct String> typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.startsWith("else ")) {
		struct String withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			struct String indent = createIndent(depth);
			return compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), __lambda54__).map(__lambda55__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda56__);
		}
	}
	return None<>();
}
Option<struct String> compileKeywordStatement(struct String input, int depth, struct String keyword) {
	if (input.strip().equals(keyword + ";")) {
		return Some<>(formatStatement(depth, keyword));
	}
	else {
		return None<>();
	}
}
struct String formatStatement(int depth, struct String value) {
	return createIndent(depth) + value + ";";
}
struct String createIndent(int depth) {
	return "\n" + "\t".repeat(depth);
}
auto __lambda57__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda58__(auto statements) {
		return withCondition + " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
}
auto __lambda59__(auto result) {
		return withCondition + " " + result;
}
auto __lambda60__(auto newCondition) {
	struct String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		struct String content = withBraces.substring(1, withBraces.length() - 1);
		return compileStatements(content, __lambda57__).map(__lambda58__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda59__);
	}
}
Option<struct String> compileConditional(struct String input, List_<struct String> typeParams, struct String prefix, int depth) {
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
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda60__);
}
int findConditionEnd(struct String input) {
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
Option<struct String> compileInvocationStatement(struct String input, List_<struct String> typeParams, int depth) {
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
auto __lambda61__(auto newSource) {
			return newDest + " = " + newSource;
}
auto __lambda62__(auto newDest) {
			return compileValue(source, typeParams, depth).map(__lambda61__);
}
Option<struct String> compileAssignment(struct String input, List_<struct String> typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			struct String destination = withoutEnd.substring(0, valueSeparator).strip();
			struct String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda62__);
		}
	}
	return None<>();
}
auto __lambda63__(auto result) {
	return "return " + result;
}
Option<struct String> compileReturn(struct String input, List_<struct String> typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda63__);
		}
	}
	return None<>();
}
auto __lambda64__(auto value) {
	return outputType + value;
}
auto __lambda65__(auto outputType) {
	return compileArgs(argsString, typeParams, depth).map(__lambda64__);
}
auto __lambda66__(auto result) {
	return "!" + result;
}
auto __lambda67__(auto compiled) {
			return generateLambdaWithReturn(Impl.emptyList(), "\n\treturn " + compiled + "." + property + "()");
}
auto __lambda68__(auto compiled) {
	return compiled + "." + property;
}
auto __lambda69__() {
	return compileOperator(input, typeParams, depth, "<");
}
auto __lambda70__() {
	return compileOperator(input, typeParams, depth, "+");
}
auto __lambda71__() {
	return compileOperator(input, typeParams, depth, ">=");
}
auto __lambda72__() {
	return compileOperator(input, typeParams, depth, "&&");
}
auto __lambda73__() {
	return compileOperator(input, typeParams, depth, "==");
}
auto __lambda74__() {
	return compileOperator(input, typeParams, depth, "!=");
}
auto __lambda75__() {
	return generatePlaceholder(input);
}
Option<struct String> compileValue(struct String input, List_<struct String> typeParams, int depth) {
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
				return compileType(type, typeParams).flatMap(__lambda65__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda66__);
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
			return compileType(type, typeParams).flatMap(__lambda67__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		struct String object = input.substring(0, separator).strip();
		struct String property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda68__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda69__).or(__lambda70__).or(__lambda71__).or(__lambda72__).or(__lambda73__).or(__lambda74__).or(__lambda75__);
}
auto __lambda76__(auto rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
auto __lambda77__(auto leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda76__);
}
Option<struct String> compileOperator(struct String input, List_<struct String> typeParams, int depth, struct String operator) {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return None<>();
	}
	struct String left = input.substring(0, operatorIndex);
	struct String right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda77__);
}
auto __lambda78__() {
	return struct String.strip()
}
auto __lambda79__(auto value) {
	return !value.isEmpty();
}
auto __lambda80__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
auto __lambda81__(auto result) {
		return generateLambdaWithReturn(paramNames, result);
}
auto __lambda82__(auto newValue) {
	return generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";");
}
Option<struct String> compileLambda(struct String input, List_<struct String> typeParams, int depth) {
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
		paramNames = splitByDelimiter(inner, ',').iter().map(__lambda78__).filter(__lambda79__).collect(ListCollector<>());
	}
	else {
		return None<>();
	}
	struct String value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		struct String slice = value.substring(1, value.length() - 1);
		return compileStatements(slice, __lambda80__).flatMap(__lambda81__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda82__);
}
auto __lambda83__(auto name) {
	return "auto " + name;
}
Option<struct String> generateLambdaWithReturn(List_<struct String> paramNames, struct String returnValue) {
	int current = counter;counter++;
	struct String lambdaName = "__lambda" + current + "__";
	struct String joinedLambdaParams = paramNames.iter().map(__lambda83__).collect(struct Joiner(", ")).orElse("");
	methods.add("auto " + lambdaName + "(" + joinedLambdaParams + ")" + " {" + returnValue + "\n}\n");
	return Some<>(lambdaName);
}
auto __lambda84__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return (index == 0 && c == '-') || Character.isDigit(c);
}
int isNumber(struct String input) {
	return Iterators.fromStringWithIndices(input).allMatch(__lambda84__);
}
auto __lambda85__(auto value) {
	return caller + value;
}
auto __lambda86__(auto caller) {
			return compileArgs(withEnd, typeParams, depth).map(__lambda85__);
}
Option<struct String> compileInvocation(struct String input, List_<struct String> typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(")")) {
		struct String sliced = stripped.substring(0, stripped.length() - ")".length());
		int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			struct String type = sliced.substring(0, argsStart);
			struct String withEnd = sliced.substring(argsStart + "(".length()).strip();
			return compileValue(type, typeParams, depth).flatMap(__lambda86__);
		}
	}
	return None<>();
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
auto __lambda87__() {
	return compileValue(arg, typeParams, depth);
}
auto __lambda88__(auto arg) {
	return compileWhitespace(arg).or(__lambda87__);
}
auto __lambda89__(auto args) {
	return "(" + args + ")";
}
Option<struct String> compileArgs(struct String argsString, List_<struct String> typeParams, int depth) {
	return compileValues(argsString, __lambda88__).map(__lambda89__);
}
struct StringBuilder mergeValues(struct StringBuilder cache, struct String element) {
	if (cache.isEmpty()) {
		return cache.append(element);
	}
	return cache.append(", ").append(element);
}
auto __lambda90__(auto typeSeparator) {
	return compileDefinitionWithTypeSeparator(typeSeparator, beforeName, name);
}
auto __lambda91__() {
	return compileDefinitionWithoutTypeSeparator(beforeName, name);
}
Option<struct String> compileDefinition(struct String definition) {
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
	return findTypeSeparator(beforeName).map(__lambda90__).orElseGet(__lambda91__);
}
auto __lambda92__(auto outputType) {
	return generateDefinition(struct Node(/* ) */.withString("type", /* outputType) */.withString("name", name));
}
Option<struct String> compileDefinitionWithoutTypeSeparator(struct String beforeName, struct String name) {
	return compileType(beforeName, Impl.emptyList()).flatMap(__lambda92__);
}
auto __lambda93__() {
	return struct String.strip()
}
auto __lambda94__(auto value) {
	return !value.isEmpty();
}
auto __lambda95__() {
	return struct Main.isSymbol()
}
auto __lambda96__() {
	return struct Main.wrapDefault()
}
auto __lambda97__(auto outputType) {
	List_<struct Node> typeParamsList = typeParams.iter().map(__lambda96__).collect(ListCollector<>());
	return generateDefinition(struct Node(/* ) */.withNodeList("type-params", /* typeParamsList) */.withString("type", /* outputType) */.withString("name", name));
}
Option<struct String> compileDefinitionWithTypeSeparator(int typeSeparator, struct String beforeName, struct String name) {
	struct String beforeType = beforeName.substring(0, typeSeparator).strip();
	struct String beforeTypeParams = beforeType;	List_<struct String> typeParams;

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
	int allSymbols = splitByDelimiter(modifiersString, ' ').iter().map(__lambda93__).filter(__lambda94__).allMatch(__lambda95__);
	if (!allSymbols) {
		return None<>();
	}
	struct String inputType = beforeName.substring(typeSeparator + " ".length());
	return compileType(inputType, typeParams).flatMap(__lambda97__);
}
auto __lambda98__() {
	return struct Impl.emptyList()
}
auto __lambda99__() {
	return struct Main.unwrapDefault()
}
auto __lambda100__(auto inner) {
	return "<" + inner + "> ";
}
Option<struct String> generateDefinition(struct Node node) {
	struct String typeParamsString = node.findNodeList("type-params").orElseGet(__lambda98__).iter().map(__lambda99__).collect(struct Joiner(", ")).map(__lambda100__).orElse("");
	struct String type = node.findString("type").orElse("");
	struct String name = node.findString("name").orElse("name");
	return Some<>(typeParamsString + type + " " + name);
}
struct String unwrapDefault(struct Node value) {
	return value.findString("value").orElse("");
}
struct Node wrapDefault(struct String typeParam) {
	return struct Node(/* ) */.withString("value", typeParam);
}
Option<int> findTypeSeparator(struct String beforeName) {
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
auto __lambda101__() {
	return struct String.strip()
}
auto __lambda102__(auto param) {
	return !param.isEmpty();
}
List_<struct String> splitValues(struct String substring) {
	return splitByDelimiter(substring.strip(), ',').iter().map(__lambda101__).filter(__lambda102__).collect(ListCollector<>());
}
auto __lambda103__(auto value) {
	return value + "*";
}
auto __lambda104__() {
	return struct String.equals()
}
auto __lambda105__() {
	return compileType(type, typeParams);
}
auto __lambda106__(auto type) {
			return compileWhitespace(type).or(__lambda105__);
}
auto __lambda107__(auto compiled) {
			return base + " < " + compiled + ">";
}
Option<struct String> compileType(struct String input, List_<struct String> typeParams) {
	if (input.equals("void")) {
		return Some<>("void");
	}
	if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
		return Some<>("int");
	}
	if (input.equals("char") || input.equals("Character")) {
		return Some<>("char");
	}
	if (input.endsWith("[]")) {
		return compileType(input.substring(0, input.length() - "[]".length()), typeParams).map(__lambda103__);
	}
	struct String stripped = input.strip();
	if (isSymbol(stripped)) {
		if (Impl.contains(typeParams, stripped, __lambda104__)) {
			return Some<>(stripped);
		}
		else {
			return Some<>("struct " + stripped);
		}
	}
	if (stripped.endsWith(">")) {
		struct String slice = stripped.substring(0, stripped.length() - ">".length());
		int argsStart = slice.indexOf("<");
		if (argsStart >= 0) {
			struct String base = slice.substring(0, argsStart).strip();
			struct String params = slice.substring(argsStart + " < ".length()).strip();
			return compileValues(params, __lambda106__).map(__lambda107__);
		}
	}
	return generatePlaceholder(input);
}
auto __lambda108__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return c == '_' || Character.isLetter(c) ||(index != 0 && Character.isDigit(c));
}
int isSymbol(struct String input) {
	if (input.isBlank()) {
		return false;
	}
	return Iterators.fromStringWithIndices(input).allMatch(__lambda108__);
}
Option<struct String> generatePlaceholder(struct String input) {
	return Some<>("/* " + input + " */");
}
