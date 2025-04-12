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
struct Iterators {
	Iterator<T> (*empty)();
	Iterator<char> (*fromString)(struct String);
	Iterator<Tuple<int, struct Character>> (*fromStringWithIndices)(struct String);
	Iterator<T> (*fromOption)(Option<struct T>);
};
struct ", Impl.listEmpty());
        if (maybeClass.isPresent()) {
	struct return maybeClass;
/* 
        }

        return generatePlaceholder(input);
     */};
struct Main {
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
	List_<struct String> (*splitByDelimiter)(struct String, char);
	Option<struct String> (*compileToStruct)(struct String, struct String, List_<struct String>);
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
List_<struct String> imports = Impl.listEmpty();
List_<struct String> structs = Impl.listEmpty();
List_<struct String> globals = Impl.listEmpty();
List_<struct String> methods = Impl.listEmpty();
int counter = 0;
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
<T> Iterator<T> empty() {
	return HeadedIterator<>(EmptyHead<>());
}
auto __lambda0__() {
	return struct Tuple.right()
}
Iterator<char> fromString() {
	return fromStringWithIndices(string).map(__lambda0__);
}
Iterator<Tuple<int, struct Character>> fromStringWithIndices() {
	return HeadedIterator<>(struct RangeHead(string.length())).map(index -> new Tuple<>(index, string.charAt(index)));
}
auto __lambda1__() {
	return struct SingleHead.new()
}
auto __lambda2__() {
	return struct EmptyHead.new()
}
<T> Iterator<T> fromOption() {
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
void main() {
	struct Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
	Impl.readString(source).match(__lambda3__, __lambda4__).ifPresent(__lambda5__);
}
Option<struct IOError> compileAndWrite() {
	struct Path_ target = source.resolveSibling("main.c");
	struct String output = compile(input);
	return Impl.writeString(target, output);
}
auto __lambda6__() {
	return struct Main.compileRootSegment()
}
auto __lambda7__() {
	return struct Main.unwrapDefault()
}
auto __lambda8__(auto list1) {
	return list1.iter().map(__lambda7__).collect(ListCollector<>());
}
auto __lambda9__(auto list) {
	List_<struct String> copy = Impl.listEmpty();
	copy.addAll(imports);
	copy.addAll(structs);
	copy.addAll(globals);
	copy.addAll(methods);
	copy.addAll(list);
	return copy;
}
auto __lambda10__() {
	return struct Main.mergeStatements()
}
auto __lambda11__(auto compiled) {
	return mergeAll(compiled, __lambda10__);
}
auto __lambda12__() {
	return generatePlaceholder(input);
}
struct String compile() {
	List_<struct String> segments = divideAllStatements(input);
	return parseAll(segments, wrapDefaultFunction(__lambda6__)).map(__lambda8__).map(__lambda9__).map(__lambda11__).or(__lambda12__).orElse("");
}
auto __lambda13__() {
	return struct Main.unwrapDefault()
}
auto __lambda14__() {
	return struct Main.mergeStatements()
}
struct String mergeAllStatements() {
	return generateAll(compiled, __lambda13__, __lambda14__);
}
Option<List_<struct Node>> parseAllStatements() {
	return parseAll(divideAllStatements(input), rule);
}
auto __lambda15__() {
	return struct Main.divideStatementChar()
}
List_<struct String> divideAllStatements() {
	return divide(input, __lambda15__);
}
struct String generateAll() {
	return mergeAll(compiled.iter().map(generator).collect(ListCollector<>()), merger);
}
struct String mergeAll() {
	return compiled.iter().fold(struct StringBuilder(), merger).toString();
}
auto __lambda16__() {
	return struct allCompiled.add()
}
auto __lambda17__(auto allCompiled) {
	return rule.apply(segment).map(__lambda16__);
}
auto __lambda18__(auto maybeCompiled, auto segment) {
	return maybeCompiled.flatMap(__lambda17__);
}
Option<List_<struct Node>> parseAll() {
	return segments.iter().<Option<List_<Node>>>fold(Some<>(Impl.listEmpty()), __lambda18__);
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
auto __lambda19__(auto input1) {
	return compileClassMember(input1, typeParams);
}
auto __lambda20__() {
	return struct Main.mergeAllStatements()
}
auto __lambda21__(auto outputContent) {
	structs.add("struct " + name + " {\n" + outputContent + "};\n");
	return "";
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
	return parseAllStatements(inputContent, wrapDefaultFunction(__lambda19__)).map(__lambda20__).map(__lambda21__);
}
auto __lambda22__() {
	return compileToStruct(input, "interface ", typeParams);
}
auto __lambda23__() {
	return compileToStruct(input, "record ", typeParams);
}
auto __lambda24__() {
	return compileToStruct(input, "class ", typeParams);
}
auto __lambda25__() {
	return compileGlobalInitialization(input, typeParams);
}
auto __lambda26__() {
	return compileDefinitionStatement(input);
}
auto __lambda27__() {
	return compileMethod(input, typeParams);
}
auto __lambda28__() {
	return generatePlaceholder(input);
}
Option<struct String> compileClassMember() {
	return compileWhitespace(input).or(__lambda22__).or(__lambda23__).or(__lambda24__).or(__lambda25__).or(__lambda26__).or(__lambda27__).or(__lambda28__);
}
auto __lambda29__() {
	return struct Main.generateDefinition()
}
auto __lambda30__(auto result) {
	return "\t" + result + ";\n";
}
Option<struct String> compileDefinitionStatement() {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String content = stripped.substring(0, stripped.length() - ";".length());
		return parseDefinition(content).flatMap(__lambda29__).map(__lambda30__);
	}
	return None<>();
}
auto __lambda31__(auto generated) {
	globals.add(generated + ";\n");
	return "";
}
Option<struct String> compileGlobalInitialization() {
	return compileInitialization(input, typeParams, 0).map(__lambda31__);
}
auto __lambda32__() {
	return struct Main.generateDefinition()
}
auto __lambda33__(auto outputValue) {
	return outputDefinition + " = " + outputValue;
}
auto __lambda34__(auto outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda33__);
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
	return parseDefinition(definition).flatMap(__lambda32__).flatMap(__lambda34__);
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
auto __lambda35__() {
	return struct Main.compileWhitespace()
}
auto __lambda36__() {
	return struct Main.parseDefinition()
}
auto __lambda37__(auto definition) {
	return parseOr(definition, Impl.listOf(wrapDefaultFunction(__lambda35__), __lambda36__));
}
Function<struct String, Option<struct Node>> createParamRule() {
	return __lambda37__;
}
auto __lambda38__(auto param) {
	return param.findNode("type");
}
auto __lambda39__() {
	return struct Iterators.fromOption()
}
auto __lambda40__() {
	return generateDefinition(functionalDefinition);
}
auto __lambda41__(auto input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
auto __lambda42__() {
	return struct Main.mergeAllStatements()
}
auto __lambda43__(auto outputContent) {
	methods.add("\t".repeat(0) + asContent + "(" + mergeAllValues(params, Main::unwrapDefault) + ")" + " {" + outputContent + "\n}\n");
	return Some<>(entry);
}
auto __lambda44__(auto output) {
	struct String asContent = output.left;
	struct String asType = output.right;
	struct String entry = "\t" + asType + ";\n";
	if (!body.startsWith("{") || !body.endsWith("}")) {
		return Some<>(entry);
	}
	struct String inputContent = body.substring("{".length(), body.length() - "}".length());
	return parseAllStatements(inputContent, wrapDefaultFunction(__lambda41__)).map(__lambda42__).flatMap(__lambda43__);
}
Option<struct String> getStringOption() {
	List_<struct Node> paramTypes = params.iter().map(__lambda38__).flatMap(__lambda39__).collect(ListCollector<>());
	struct String name = definition.findString("name").orElse("");
	struct Node returns = definition.findNode("type").orElse(struct Node());
	struct Node functionalDefinition = struct Node(/* ) */.retype("functional-definition").withString("name", /* name) */.withNode("returns", /* returns) */.withNodeList("params", paramTypes);
	return generateDefinition(definition).and(__lambda40__).flatMap(__lambda44__);
}
auto __lambda45__() {
	return struct Main.divideValueChar()
}
Option<List_<struct Node>> parseAllValues() {
	return parseAll(divide(input, __lambda45__), rule);
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
auto __lambda46__() {
	return struct Main.mergeValues()
}
struct String mergeAllValues() {
	return generateAll(compiled, generator, __lambda46__);
}
auto __lambda47__() {
	return compileKeywordStatement(input, depth, "continue");
}
auto __lambda48__() {
	return compileKeywordStatement(input, depth, "break");
}
auto __lambda49__() {
	return compileConditional(input, typeParams, "if ", depth);
}
auto __lambda50__() {
	return compileConditional(input, typeParams, "while ", depth);
}
auto __lambda51__() {
	return compileElse(input, typeParams, depth);
}
auto __lambda52__() {
	return compilePostOperator(input, typeParams, depth, "++");
}
auto __lambda53__() {
	return compilePostOperator(input, typeParams, depth, "--");
}
auto __lambda54__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda55__() {
	return compileReturn(input, typeParams, depth).map(__lambda54__);
}
auto __lambda56__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda57__() {
	return compileInitialization(input, typeParams, depth).map(__lambda56__);
}
auto __lambda58__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda59__() {
	return compileAssignment(input, typeParams, depth).map(__lambda58__);
}
auto __lambda60__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda61__() {
	return compileInvocationStatement(input, typeParams, depth).map(__lambda60__);
}
auto __lambda62__() {
	return compileDefinitionStatement(input);
}
auto __lambda63__() {
	return generatePlaceholder(input);
}
Option<struct String> compileStatementOrBlock() {
	return compileWhitespace(input).or(__lambda47__).or(__lambda48__).or(__lambda49__).or(__lambda50__).or(__lambda51__).or(__lambda52__).or(__lambda53__).or(__lambda55__).or(__lambda57__).or(__lambda59__).or(__lambda61__).or(__lambda62__).or(__lambda63__);
}
auto __lambda64__(auto value) {
	return value + operator + ";";
}
Option<struct String> compilePostOperator() {
	struct String stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		struct String slice = stripped.substring(0, stripped.length() -(operator + ";").length());
		return compileValue(slice, typeParams, depth).map(__lambda64__);
	}
	else {
		return None<>();
	}
}
auto __lambda65__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda66__() {
	return struct Main.mergeAllStatements()
}
auto __lambda67__(auto result) {
	return indent + "else {" + result + indent + "}";
}
auto __lambda68__(auto result) {
	return "else " + result;
}
Option<struct String> compileElse() {
	struct String stripped = input.strip();
	if (stripped.startsWith("else ")) {
		struct String withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			struct String indent = createIndent(depth);
			return parseAllStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), wrapDefaultFunction(__lambda65__)).map(__lambda66__).map(__lambda67__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda68__);
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
auto __lambda69__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda70__() {
	return struct Main.mergeAllStatements()
}
auto __lambda71__(auto statements) {
		return withCondition + " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
}
auto __lambda72__(auto result) {
		return withCondition + " " + result;
}
auto __lambda73__(auto newCondition) {
	struct String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		struct String content = withBraces.substring(1, withBraces.length() - 1);
		return parseAllStatements(content, wrapDefaultFunction(__lambda69__)).map(__lambda70__).map(__lambda71__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda72__);
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
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda73__);
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
auto __lambda74__(auto newSource) {
			return newDest + " = " + newSource;
}
auto __lambda75__(auto newDest) {
			return compileValue(source, typeParams, depth).map(__lambda74__);
}
Option<struct String> compileAssignment() {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			struct String destination = withoutEnd.substring(0, valueSeparator).strip();
			struct String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda75__);
		}
	}
	return None<>();
}
auto __lambda76__(auto result) {
	return "return " + result;
}
Option<struct String> compileReturn() {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda76__);
		}
	}
	return None<>();
}
auto __lambda77__() {
	return struct Main.generateType()
}
auto __lambda78__(auto value) {
	return outputType + value;
}
auto __lambda79__(auto outputType) {
	return compileArgs(argsString, typeParams, depth).map(__lambda78__);
}
auto __lambda80__(auto result) {
	return "!" + result;
}
auto __lambda81__() {
	return struct Main.generateType()
}
auto __lambda82__(auto compiled) {
			return generateLambdaWithReturn(Impl.listEmpty(), "\n\treturn " + compiled + "." + property + "()");
}
auto __lambda83__(auto compiled) {
	return compiled + "." + property;
}
auto __lambda84__() {
	return compileOperator(input, typeParams, depth, "<");
}
auto __lambda85__() {
	return compileOperator(input, typeParams, depth, "+");
}
auto __lambda86__() {
	return compileOperator(input, typeParams, depth, ">=");
}
auto __lambda87__() {
	return compileOperator(input, typeParams, depth, "&&");
}
auto __lambda88__() {
	return compileOperator(input, typeParams, depth, "==");
}
auto __lambda89__() {
	return compileOperator(input, typeParams, depth, "!=");
}
auto __lambda90__() {
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
				return parseType(type, typeParams).map(__lambda77__).flatMap(__lambda79__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda80__);
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
			return parseType(type, typeParams).map(__lambda81__).flatMap(__lambda82__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		struct String object = input.substring(0, separator).strip();
		struct String property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda83__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda84__).or(__lambda85__).or(__lambda86__).or(__lambda87__).or(__lambda88__).or(__lambda89__).or(__lambda90__);
}
auto __lambda91__(auto rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
auto __lambda92__(auto leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda91__);
}
Option<struct String> compileOperator() {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return None<>();
	}
	struct String left = input.substring(0, operatorIndex);
	struct String right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda92__);
}
auto __lambda93__() {
	return struct String.strip()
}
auto __lambda94__(auto value) {
	return !value.isEmpty();
}
auto __lambda95__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
auto __lambda96__() {
	return struct Main.mergeAllStatements()
}
auto __lambda97__(auto result) {
		return generateLambdaWithReturn(paramNames, result);
}
auto __lambda98__(auto newValue) {
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
		paramNames = splitByDelimiter(inner, ',').iter().map(__lambda93__).filter(__lambda94__).collect(ListCollector<>());
	}
	else {
		return None<>();
	}
	struct String value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		struct String slice = value.substring(1, value.length() - 1);
		return parseAllStatements(slice, wrapDefaultFunction(__lambda95__)).map(__lambda96__).flatMap(__lambda97__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda98__);
}
auto __lambda99__(auto name) {
	return "auto " + name;
}
Option<struct String> generateLambdaWithReturn() {
	int current = counter;counter++;
	struct String lambdaName = "__lambda" + current + "__";
	struct String joinedLambdaParams = paramNames.iter().map(__lambda99__).collect(struct Joiner(", ")).orElse("");
	methods.add("auto " + lambdaName + "(" + joinedLambdaParams + ")" + " {" + returnValue + "\n}\n");
	return Some<>(lambdaName);
}
auto __lambda100__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return (index == 0 && c == '-') || Character.isDigit(c);
}
int isNumber() {
	return Iterators.fromStringWithIndices(input).allMatch(__lambda100__);
}
auto __lambda101__(auto value) {
	return caller + value;
}
auto __lambda102__(auto caller) {
			return compileArgs(withEnd, typeParams, depth).map(__lambda101__);
}
Option<struct String> compileInvocation() {
	struct String stripped = input.strip();
	if (stripped.endsWith(")")) {
		struct String sliced = stripped.substring(0, stripped.length() - ")".length());
		int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			struct String type = sliced.substring(0, argsStart);
			struct String withEnd = sliced.substring(argsStart + "(".length()).strip();
			return compileValue(type, typeParams, depth).flatMap(__lambda102__);
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
auto __lambda103__() {
	return compileValue(arg, typeParams, depth);
}
auto __lambda104__(auto arg) {
	return compileWhitespace(arg).or(__lambda103__);
}
auto __lambda105__() {
	return struct Main.unwrapDefault()
}
auto __lambda106__(auto compiled) {
	return mergeAllValues(compiled, __lambda105__);
}
auto __lambda107__(auto args) {
	return "(" + args + ")";
}
Option<struct String> compileArgs() {
	return parseAllValues(argsString, wrapDefaultFunction(__lambda104__)).map(__lambda106__).map(__lambda107__);
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
auto __lambda108__(auto typeSeparator) {
	struct String beforeType = beforeName.substring(0, typeSeparator).strip();
	struct String type = beforeName.substring(typeSeparator + " ".length());
	return parseDefinitionWithTypeSeparator(withName, beforeType, type);
}
auto __lambda109__() {
	return parseDefinitionTypeProperty(withName, beforeName, Impl.listEmpty());
}
Option<struct Node> parseDefinitionWithName() {
	return findTypeSeparator(beforeName).map(__lambda108__).orElseGet(__lambda109__);
}
auto __lambda110__() {
	return struct Main.wrapDefault()
}
auto __lambda111__(auto node) {
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
	List_<struct Node> typeParamsNodes = typeParamsStrings.iter().map(__lambda110__).collect(ListCollector<>());
	int hasValidBeforeParams = validateLeft(beforeTypeParams);
	if (!hasValidBeforeParams) {
		return None<>();
	}
	return parseDefinitionTypeProperty(withName, type, typeParamsStrings).map(__lambda111__);
}
auto __lambda112__(auto outputType) {
	return withName.withNode("type", outputType);
}
Option<struct Node> parseDefinitionTypeProperty() {
	return parseType(type, typeParams).map(__lambda112__);
}
auto __lambda113__(auto node) {
	return node.withNodeList("type-params", typeParamsList);
}
Option<struct Node> parseDefinitionWithNoTypeParams() {
	int hasValidBeforeParams = validateLeft(beforeType);
	List_<struct Node> typeParamsList = Impl.listEmpty();
	if (!hasValidBeforeParams) {
		return None<>();
	}
	return parseDefinitionTypeProperty(withName, type, Impl.listEmpty()).map(__lambda113__);
}
auto __lambda114__() {
	return struct String.strip()
}
auto __lambda115__(auto value) {
	return !value.isEmpty();
}
auto __lambda116__() {
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
	return splitByDelimiter(modifiersString, ' ').iter().map(__lambda114__).filter(__lambda115__).allMatch(__lambda116__);
}
auto __lambda117__() {
	return struct Impl.listEmpty()
}
auto __lambda118__() {
	return struct Main.generateType()
}
auto __lambda119__() {
	return struct Impl.listEmpty()
}
auto __lambda120__() {
	return struct Main.unwrapDefault()
}
auto __lambda121__(auto inner) {
	return "<" + inner + "> ";
}
auto __lambda122__() {
	return struct Main.generateType()
}
Option<struct String> generateDefinition() {
	if (node.is("functional-definition")) {
		struct String name = node.findString("name").orElse("");
		struct String returns = generateType(node.findNode("returns").orElse(struct Node()));
		struct String params = node.findNodeList("params").orElseGet(__lambda117__).iter().map(__lambda118__).collect(struct Joiner(", ")).orElse("");
		return Some<>(returns + " (*" + name + ")(" + params + ")");
	}
	struct String typeParamsString = node.findNodeList("type-params").orElseGet(__lambda119__).iter().map(__lambda120__).collect(struct Joiner(", ")).map(__lambda121__).orElse("");
	struct String type = node.findNode("type").map(__lambda122__).orElse("");
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
auto __lambda123__() {
	return struct String.strip()
}
auto __lambda124__(auto param) {
	return !param.isEmpty();
}
List_<struct String> splitValues() {
	return splitByDelimiter(substring.strip(), ',').iter().map(__lambda123__).filter(__lambda124__).collect(ListCollector<>());
}
struct String generateType() {
	if (node.is("generic")) {
		return generateGeneric(node);
	}
	return unwrapDefault(node);
}
Option<struct Node> parseType() {
	return parseOr(input, listTypeRules(typeParams));
}
auto __lambda125__(auto function) {
	return function.apply(input);
}
auto __lambda126__() {
	return struct Iterators.fromOption()
}
Option<struct Node> parseOr() {
	return rules.iter().map(__lambda125__).flatMap(__lambda126__).next();
}
auto __lambda127__() {
	return struct Main.compilePrimitive()
}
auto __lambda128__(auto input) {
	return compileArray(input, typeParams);
}
auto __lambda129__(auto input) {
	return compileSymbol(input, typeParams);
}
List_<Function<struct String, Option<struct Node>>> listTypeRules() {
	return Impl.listOf(wrapDefaultFunction(__lambda127__), wrapDefaultFunction(__lambda128__), wrapDefaultFunction(__lambda129__), parseGeneric(typeParams));
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
auto __lambda130__() {
	return struct Main.wrapDefault()
}
auto __lambda131__(auto input) {
	return mapper.apply(input).map(__lambda130__);
}
Function<struct String, Option<struct Node>> wrapDefaultFunction() {
	return __lambda131__;
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
auto __lambda132__() {
	return struct Main.generateType()
}
auto __lambda133__(auto value) {
	return value + "*";
}
Option<struct String> compileArray() {
	if (input.endsWith("[]")) {
		return parseType(input.substring(0, input.length() - "[]".length()), typeParams).map(__lambda132__).map(__lambda133__);
	}
	return None<>();
}
auto __lambda134__() {
	return struct String.equals()
}
Option<struct String> compileSymbol() {
	struct String stripped = input.strip();
	if (!isSymbol(stripped)) {
		return None<>();
	}
	if (Impl.contains(typeParams, stripped, __lambda134__)) {
		return Some<>(stripped);
	}
	else {
		return Some<>("struct " + stripped);
	}
}
auto __lambda135__(auto tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return c == '_' || Character.isLetter(c) ||(index != 0 && Character.isDigit(c));
}
int isSymbol() {
	if (input.isBlank()) {
		return false;
	}
	return Iterators.fromStringWithIndices(input).allMatch(__lambda135__);
}
Option<struct String> generatePlaceholder() {
	return Some<>("/* " + input + " */");
}
