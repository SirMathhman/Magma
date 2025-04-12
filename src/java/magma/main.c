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
auto __lambda1__() {
	return struct SingleHead.new()
}
auto __lambda2__() {
	return struct EmptyHead.new()
}
<T> Iterator<T> fromOption(Option<struct T> option) {
	return HeadedIterator<>(option.<Head<T>>map(__lambda1__).orElseGet(__lambda2__));
}
auto __lambda3__(auto input) {
	return compileAndWrite(input, source);
}
auto __lambda4__() {
	return struct Some.new()
}
auto __lambda5__() {
	return struct IOError.display()
}
void main(struct String* args) {
	struct Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
	Impl.readString(source).match(__lambda3__, __lambda4__).ifPresent(__lambda5__);
}
Option<struct IOError> compileAndWrite(struct String input, struct Path_ source) {
	struct Path_ target = source.resolveSibling("main.c");
	struct String output = compile(input);
	return Impl.writeString(target, output);
}
auto __lambda6__() {
	return struct Main.divideStatementChar()
}
auto __lambda7__() {
	return struct Main.compileRootSegment()
}
auto __lambda8__(auto list) {
	List_<struct String> copy = Impl.emptyList();
	copy.addAll(imports);
	copy.addAll(structs);
	copy.addAll(globals);
	copy.addAll(methods);
	copy.addAll(list);
	return copy;
}
auto __lambda9__() {
	return struct Main.mergeStatements()
}
auto __lambda10__(auto compiled) {
	return mergeAll(compiled, __lambda9__);
}
auto __lambda11__() {
	return generatePlaceholder(input);
}
struct String compile(struct String input) {
	List_<struct String> segments = divide(input, __lambda6__);
	return parseAll(segments, __lambda7__).map(__lambda8__).map(__lambda10__).or(__lambda11__).orElse("");
}
auto __lambda12__() {
	return struct Main.divideStatementChar()
}
auto __lambda13__() {
	return struct Main.mergeStatements()
}
Option<struct String> compileStatements(struct String input, Function<struct String, Option<struct String>> compiler) {
	return compileAndMerge(divide(input, __lambda12__), compiler, __lambda13__);
}
auto __lambda14__(auto compiled) {
	return mergeAll(compiled, merger);
}
Option<struct String> compileAndMerge(List_<struct String> segments, Function<struct String, Option<struct String>> compiler, BiFunction<struct StringBuilder, struct String, struct StringBuilder> merger) {
	return parseAll(segments, compiler).map(__lambda14__);
}
struct String mergeAll(List_<struct String> compiled, BiFunction<struct StringBuilder, struct String, struct StringBuilder> merger) {
	return compiled.iter().fold(struct StringBuilder(), merger).toString();
}
auto __lambda15__(auto compiledSegment) {
	allCompiled.add(compiledSegment);
	return allCompiled;
}
auto __lambda16__(auto allCompiled) {
	return compiler.apply(segment).map(__lambda15__);
}
auto __lambda17__(auto maybeCompiled, auto segment) {
	return maybeCompiled.flatMap(__lambda16__);
}
Option<List_<struct String>> parseAll(List_<struct String> segments, Function<struct String, Option<struct String>> compiler) {
	return segments.iter().<Option<List_<String>>>fold(Some<>(Impl.emptyList()), __lambda17__);
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
auto __lambda18__(auto input1) {
	return compileClassMember(input1, typeParams);
}
auto __lambda19__(auto outputContent) {
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
	return compileStatements(inputContent, __lambda18__).map(__lambda19__);
}
auto __lambda20__() {
	return compileToStruct(input, "interface ", typeParams);
}
auto __lambda21__() {
	return compileToStruct(input, "record ", typeParams);
}
auto __lambda22__() {
	return compileToStruct(input, "class ", typeParams);
}
auto __lambda23__() {
	return compileGlobalInitialization(input, typeParams);
}
auto __lambda24__() {
	return compileDefinitionStatement(input);
}
auto __lambda25__() {
	return compileMethod(input, typeParams);
}
auto __lambda26__() {
	return generatePlaceholder(input);
}
Option<struct String> compileClassMember(struct String input, List_<struct String> typeParams) {
	return compileWhitespace(input).or(__lambda20__).or(__lambda21__).or(__lambda22__).or(__lambda23__).or(__lambda24__).or(__lambda25__).or(__lambda26__);
}
auto __lambda27__() {
	return struct Main.generateDefinition()
}
auto __lambda28__(auto result) {
	return "\t" + result + ";\n";
}
Option<struct String> compileDefinitionStatement(struct String input) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String content = stripped.substring(0, stripped.length() - ";".length());
		return parseDefinition(content).flatMap(__lambda27__).map(__lambda28__);
	}
	return None<>();
}
auto __lambda29__(auto generated) {
	globals.add(generated + ";\n");
	return "";
}
Option<struct String> compileGlobalInitialization(struct String input, List_<struct String> typeParams) {
	return compileInitialization(input, typeParams, 0).map(__lambda29__);
}
auto __lambda30__() {
	return struct Main.generateDefinition()
}
auto __lambda31__(auto outputValue) {
	return outputDefinition + " = " + outputValue;
}
auto __lambda32__(auto outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda31__);
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
	return parseDefinition(definition).flatMap(__lambda30__).flatMap(__lambda32__);
}
Option<struct String> compileWhitespace(struct String input) {
	if (input.isBlank()) {
		return Some<>("");
	}
	return None<>();
}
auto __lambda33__() {
	return struct Main.generateDefinition()
}
auto __lambda34__() {
	return struct Main.compileParameter()
}
Option<struct String> compileMethod(struct String input, List_<struct String> typeParams) {
	int paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return None<>();
	}
	struct String inputDefinition = input.substring(0, paramStart).strip();
	struct String withParams = input.substring(paramStart + "(".length());
	return parseDefinition(inputDefinition).flatMap(__lambda33__).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return new None<>();
            }

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, __lambda34__).flatMap(outputParams -> assembleMethodBody(typeParams, outputDefinition, outputParams, withParams.substring(paramEnd + ")".length()).strip()));
        });
}
auto __lambda35__(auto input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
auto __lambda36__(auto outputContent) {
		methods.add(header + " {" + outputContent + "\n}\n");
		return Some<>("");
}
Option<struct String> assembleMethodBody(List_<struct String> typeParams, struct String definition, struct String params, struct String body) {
	struct String header = "\t".repeat(0) + definition + "(" + params + ")";
	if (body.startsWith("{") && body.endsWith("}")) {
		struct String inputContent = body.substring("{".length(), body.length() - "}".length());
		return compileStatements(inputContent, __lambda35__).flatMap(__lambda36__);
	}
	return Some<>("\t" + header + ";\n");
}
auto __lambda37__() {
	return struct Main.generateDefinition()
}
auto __lambda38__() {
	return parseDefinition(definition).flatMap(__lambda37__);
}
auto __lambda39__() {
	return generatePlaceholder(definition);
}
Option<struct String> compileParameter(struct String definition) {
	return compileWhitespace(definition).or(__lambda38__).or(__lambda39__);
}
auto __lambda40__() {
	return struct Main.divideValueChar()
}
Option<struct String> compileValues(struct String input, Function<struct String, Option<struct String>> compiler) {
	List_<struct String> divided = divide(input, __lambda40__);
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
auto __lambda41__() {
	return struct Main.mergeValues()
}
Option<struct String> compileValues(List_<struct String> params, Function<struct String, Option<struct String>> compiler) {
	return compileAndMerge(params, compiler, __lambda41__);
}
auto __lambda42__() {
	return compileKeywordStatement(input, depth, "continue");
}
auto __lambda43__() {
	return compileKeywordStatement(input, depth, "break");
}
auto __lambda44__() {
	return compileConditional(input, typeParams, "if ", depth);
}
auto __lambda45__() {
	return compileConditional(input, typeParams, "while ", depth);
}
auto __lambda46__() {
	return compileElse(input, typeParams, depth);
}
auto __lambda47__() {
	return compilePostOperator(input, typeParams, depth, "++");
}
auto __lambda48__() {
	return compilePostOperator(input, typeParams, depth, "--");
}
auto __lambda49__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda50__() {
	return compileReturn(input, typeParams, depth).map(__lambda49__);
}
auto __lambda51__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda52__() {
	return compileInitialization(input, typeParams, depth).map(__lambda51__);
}
auto __lambda53__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda54__() {
	return compileAssignment(input, typeParams, depth).map(__lambda53__);
}
auto __lambda55__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda56__() {
	return compileInvocationStatement(input, typeParams, depth).map(__lambda55__);
}
auto __lambda57__() {
	return compileDefinitionStatement(input);
}
auto __lambda58__() {
	return generatePlaceholder(input);
}
Option<struct String> compileStatementOrBlock(struct String input, List_<struct String> typeParams, int depth) {
	return compileWhitespace(input).or(__lambda42__).or(__lambda43__).or(__lambda44__).or(__lambda45__).or(__lambda46__).or(__lambda47__).or(__lambda48__).or(__lambda50__).or(__lambda52__).or(__lambda54__).or(__lambda56__).or(__lambda57__).or(__lambda58__);
}
auto __lambda59__(auto value) {
	return value + operator + ";";
}
Option<struct String> compilePostOperator(struct String input, List_<struct String> typeParams, int depth, struct String operator) {
	struct String stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		struct String slice = stripped.substring(0, stripped.length() -(operator + ";").length());
		return compileValue(slice, typeParams, depth).map(__lambda59__);
	}
	else {
		return None<>();
	}
}
auto __lambda60__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda61__(auto result) {
	return indent + "else {" + result + indent + "}";
}
auto __lambda62__(auto result) {
	return "else " + result;
}
Option<struct String> compileElse(struct String input, List_<struct String> typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.startsWith("else ")) {
		struct String withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			struct String indent = createIndent(depth);
			return compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), __lambda60__).map(__lambda61__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda62__);
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
auto __lambda63__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda64__(auto statements) {
		return withCondition + " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
}
auto __lambda65__(auto result) {
		return withCondition + " " + result;
}
auto __lambda66__(auto newCondition) {
	struct String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		struct String content = withBraces.substring(1, withBraces.length() - 1);
		return compileStatements(content, __lambda63__).map(__lambda64__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda65__);
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
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda66__);
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
auto __lambda67__(auto newSource) {
			return newDest + " = " + newSource;
}
auto __lambda68__(auto newDest) {
			return compileValue(source, typeParams, depth).map(__lambda67__);
}
Option<struct String> compileAssignment(struct String input, List_<struct String> typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			struct String destination = withoutEnd.substring(0, valueSeparator).strip();
			struct String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda68__);
		}
	}
	return None<>();
}
auto __lambda69__(auto result) {
	return "return " + result;
}
Option<struct String> compileReturn(struct String input, List_<struct String> typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda69__);
		}
	}
	return None<>();
}
auto __lambda70__() {
	return struct Main.generateType()
}
auto __lambda71__(auto value) {
	return outputType + value;
}
auto __lambda72__(auto outputType) {
	return compileArgs(argsString, typeParams, depth).map(__lambda71__);
}
auto __lambda73__(auto result) {
	return "!" + result;
}
auto __lambda74__() {
	return struct Main.generateType()
}
auto __lambda75__(auto compiled) {
			return generateLambdaWithReturn(Impl.emptyList(), "\n\treturn " + compiled + "." + property + "()");
}
auto __lambda76__(auto compiled) {
	return compiled + "." + property;
}
auto __lambda77__() {
	return compileOperator(input, typeParams, depth, "<");
}
auto __lambda78__() {
	return compileOperator(input, typeParams, depth, "+");
}
auto __lambda79__() {
	return compileOperator(input, typeParams, depth, ">=");
}
auto __lambda80__() {
	return compileOperator(input, typeParams, depth, "&&");
}
auto __lambda81__() {
	return compileOperator(input, typeParams, depth, "==");
}
auto __lambda82__() {
	return compileOperator(input, typeParams, depth, "!=");
}
auto __lambda83__() {
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
				return parseType(type, typeParams).map(__lambda70__).flatMap(__lambda72__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda73__);
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
			return parseType(type, typeParams).map(__lambda74__).flatMap(__lambda75__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		struct String object = input.substring(0, separator).strip();
		struct String property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda76__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda77__).or(__lambda78__).or(__lambda79__).or(__lambda80__).or(__lambda81__).or(__lambda82__).or(__lambda83__);
}
auto __lambda84__(auto rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
auto __lambda85__(auto leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda84__);
}
Option<struct String> compileOperator(struct String input, List_<struct String> typeParams, int depth, struct String operator) {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return None<>();
	}
	struct String left = input.substring(0, operatorIndex);
	struct String right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda85__);
}
auto __lambda86__() {
	return struct String.strip()
}
auto __lambda87__(auto value) {
	return !value.isEmpty();
}
auto __lambda88__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
auto __lambda89__(auto result) {
		return generateLambdaWithReturn(paramNames, result);
}
auto __lambda90__(auto newValue) {
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
		paramNames = splitByDelimiter(inner, ',').iter().map(__lambda86__).filter(__lambda87__).collect(ListCollector<>());
	}
	else {
		return None<>();
	}
	struct String value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		struct String slice = value.substring(1, value.length() - 1);
		return compileStatements(slice, __lambda88__).flatMap(__lambda89__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda90__);
}
auto __lambda91__(auto name) {
	return "auto " + name;
}
Option<struct String> generateLambdaWithReturn(List_<struct String> paramNames, struct String returnValue) {
	int current = counter;counter++;
	struct String lambdaName = "__lambda" + current + "__";
	struct String joinedLambdaParams = paramNames.iter().map(__lambda91__).collect(struct Joiner(", ")).orElse("");
	methods.add("auto " + lambdaName + "(" + joinedLambdaParams + ")" + " {" + returnValue + "\n}\n");
	return Some<>(lambdaName);
}
auto __lambda92__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return (index == 0 && c == '-') || Character.isDigit(c);
}
int isNumber(struct String input) {
	return Iterators.fromStringWithIndices(input).allMatch(__lambda92__);
}
auto __lambda93__(auto value) {
	return caller + value;
}
auto __lambda94__(auto caller) {
			return compileArgs(withEnd, typeParams, depth).map(__lambda93__);
}
Option<struct String> compileInvocation(struct String input, List_<struct String> typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(")")) {
		struct String sliced = stripped.substring(0, stripped.length() - ")".length());
		int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			struct String type = sliced.substring(0, argsStart);
			struct String withEnd = sliced.substring(argsStart + "(".length()).strip();
			return compileValue(type, typeParams, depth).flatMap(__lambda94__);
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
auto __lambda95__() {
	return compileValue(arg, typeParams, depth);
}
auto __lambda96__(auto arg) {
	return compileWhitespace(arg).or(__lambda95__);
}
auto __lambda97__(auto args) {
	return "(" + args + ")";
}
Option<struct String> compileArgs(struct String argsString, List_<struct String> typeParams, int depth) {
	return compileValues(argsString, __lambda96__).map(__lambda97__);
}
struct StringBuilder mergeValues(struct StringBuilder cache, struct String element) {
	if (cache.isEmpty()) {
		return cache.append(element);
	}
	return cache.append(", ").append(element);
}
Option<struct Node> parseDefinition(struct String definition) {
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
auto __lambda98__(auto typeSeparator) {
	struct String beforeType = beforeName.substring(0, typeSeparator).strip();
	struct String type = beforeName.substring(typeSeparator + " ".length());
	return parseDefinitionWithTypeSeparator(withName, beforeType, type);
}
auto __lambda99__() {
	return parseDefinitionTypeProperty(withName, beforeName, Impl.emptyList());
}
Option<struct Node> parseDefinitionWithName(struct String beforeName, struct Node withName) {
	return findTypeSeparator(beforeName).map(__lambda98__).orElseGet(__lambda99__);
}
auto __lambda100__() {
	return struct Main.wrapDefault()
}
auto __lambda101__(auto node) {
	return node.withNodeList("type-params", typeParamsNodes);
}
Option<struct Node> parseDefinitionWithTypeSeparator(struct Node withName, struct String beforeType, struct String type) {
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
	List_<struct Node> typeParamsNodes = typeParamsStrings.iter().map(__lambda100__).collect(ListCollector<>());
	int hasValidBeforeParams = validateLeft(beforeTypeParams);
	if (!hasValidBeforeParams) {
		return None<>();
	}
	return parseDefinitionTypeProperty(withName, type, typeParamsStrings).map(__lambda101__);
}
auto __lambda102__(auto outputType) {
	return withName.withNode("type", outputType);
}
Option<struct Node> parseDefinitionTypeProperty(struct Node withName, struct String type, List_<struct String> typeParams) {
	return parseType(type, typeParams).map(__lambda102__);
}
auto __lambda103__(auto node) {
	return node.withNodeList("type-params", typeParamsList);
}
Option<struct Node> parseDefinitionWithNoTypeParams(struct Node withName, struct String beforeType, struct String type) {
	int hasValidBeforeParams = validateLeft(beforeType);
	List_<struct Node> typeParamsList = Impl.emptyList();
	if (!hasValidBeforeParams) {
		return None<>();
	}
	return parseDefinitionTypeProperty(withName, type, Impl.emptyList()).map(__lambda103__);
}
auto __lambda104__() {
	return struct String.strip()
}
auto __lambda105__(auto value) {
	return !value.isEmpty();
}
auto __lambda106__() {
	return struct Main.isSymbol()
}
int validateLeft(struct String beforeTypeParams) {
	struct String strippedBeforeTypeParams = beforeTypeParams.strip();	struct String modifiersString;

	int annotationSeparator = strippedBeforeTypeParams.lastIndexOf("\n");
	if (annotationSeparator >= 0) {
		modifiersString = strippedBeforeTypeParams.substring(annotationSeparator + "\n".length());
	}
	else {
		modifiersString = strippedBeforeTypeParams;
	}
	return splitByDelimiter(modifiersString, ' ').iter().map(__lambda104__).filter(__lambda105__).allMatch(__lambda106__);
}
auto __lambda107__() {
	return struct Impl.emptyList()
}
auto __lambda108__() {
	return struct Main.unwrapDefault()
}
auto __lambda109__(auto inner) {
	return "<" + inner + "> ";
}
auto __lambda110__() {
	return struct Main.generateType()
}
Option<struct String> generateDefinition(struct Node node) {
	struct String typeParamsString = node.findNodeList("type-params").orElseGet(__lambda107__).iter().map(__lambda108__).collect(struct Joiner(", ")).map(__lambda109__).orElse("");
	struct String type = node.findNode("type").map(__lambda110__).orElse("");
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
auto __lambda111__() {
	return struct String.strip()
}
auto __lambda112__(auto param) {
	return !param.isEmpty();
}
List_<struct String> splitValues(struct String substring) {
	return splitByDelimiter(substring.strip(), ',').iter().map(__lambda111__).filter(__lambda112__).collect(ListCollector<>());
}
struct String generateType(struct Node value) {
	return unwrapDefault(value);
}
auto __lambda113__(auto function) {
	return function.apply(input);
}
auto __lambda114__() {
	return struct Iterators.fromOption()
}
Option<struct Node> parseType(struct String input, List_<struct String> typeParams) {
	return listTypeRules(typeParams).iter().map(__lambda113__).flatMap(__lambda114__).next();
}
auto __lambda115__() {
	return struct Main.compilePrimitive()
}
auto __lambda116__(auto input) {
	return compileArray(input, typeParams);
}
auto __lambda117__(auto input) {
	return compileSymbol(input, typeParams);
}
auto __lambda118__(auto input) {
	return compileGeneric(input, typeParams);
}
List_<Function<struct String, Option<struct Node>>> listTypeRules(List_<struct String> typeParams) {
	return Impl.listOf(wrapDefaultFunction(__lambda115__), wrapDefaultFunction(__lambda116__), wrapDefaultFunction(__lambda117__), wrapDefaultFunction(__lambda118__));
}
auto __lambda119__() {
	return struct Main.wrapDefault()
}
auto __lambda120__(auto s) {
	return mapper.apply(s).map(__lambda119__);
}
Function<struct String, Option<struct Node>> wrapDefaultFunction(Function<struct String, Option<struct String>> mapper) {
	return __lambda120__;
}
Option<struct String> compilePrimitive(struct String input) {
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
auto __lambda121__() {
	return struct Main.generateType()
}
auto __lambda122__(auto value) {
	return value + "*";
}
Option<struct String> compileArray(struct String input, List_<struct String> typeParams) {
	if (input.endsWith("[]")) {
		return parseType(input.substring(0, input.length() - "[]".length()), typeParams).map(__lambda121__).map(__lambda122__);
	}
	return None<>();
}
auto __lambda123__() {
	return struct String.equals()
}
Option<struct String> compileSymbol(struct String input, List_<struct String> typeParams) {
	struct String stripped = input.strip();
	if (!isSymbol(stripped)) {
		return None<>();
	}
	if (Impl.contains(typeParams, stripped, __lambda123__)) {
		return Some<>(stripped);
	}
	else {
		return Some<>("struct " + stripped);
	}
}
auto __lambda124__() {
	return struct Main.generateType()
}
auto __lambda125__() {
	return parseType(type, typeParams).map(__lambda124__);
}
auto __lambda126__(auto type) {
	return compileWhitespace(type).or(__lambda125__);
}
auto __lambda127__(auto compiled) {
	return base + " < " + compiled + ">";
}
Option<struct String> compileGeneric(struct String input, List_<struct String> typeParams) {
	struct String stripped = input.strip();
	if (!stripped.endsWith(">")) {
		return None<>();
	}
	struct String slice = stripped.substring(0, stripped.length() - ">".length());
	int argsStart = slice.indexOf("<");
	if (argsStart < 0) {
		return None<>();
	}
	struct String base = slice.substring(0, argsStart).strip();
	struct String params = slice.substring(argsStart + " < ".length()).strip();
	return compileValues(params, __lambda126__).map(__lambda127__);
}
auto __lambda128__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return c == '_' || Character.isLetter(c) ||(index != 0 && Character.isDigit(c));
}
int isSymbol(struct String input) {
	if (input.isBlank()) {
		return false;
	}
	return Iterators.fromStringWithIndices(input).allMatch(__lambda128__);
}
Option<struct String> generatePlaceholder(struct String input) {
	return Some<>("/* " + input + " */");
}
