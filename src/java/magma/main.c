#include "./org/jetbrains/annotations/NotNull"
#include "./java/io/IOException"
#include "./java/nio/file/Path"
#include "./java/nio/file/Paths"
#include "./java/util/ArrayList"
#include "./java/util/Arrays"
#include "./java/util/Collections"
#include "./java/util/Deque"
#include "./java/util/LinkedList"
#include "./java/util/List"
#include "./java/util/Optional"
#include "./java/util/function/BiFunction"
#include "./java/util/function/Function"
#include "./java/util/regex/Pattern"
#include "./java/util/stream/Collectors"
#include "./java/util/stream/IntStream"
struct Result<T, X> {
<R> R match(Function<struct T, struct R> whenOk, Function<struct X, struct R> whenErr);};
struct Err<T, X>(X error) implements Result<T, X> {
};
struct Ok<T, X>(T value) implements Result<T, X> {
};
struct State {
	Deque<char> queue;
	List<struct String> segments;
	struct StringBuilder buffer;
	int depth;
};
struct Tuple<A, B>(A left, B right) {
};
struct Main {
/* 

    private static @NotNull Optional<String> compileElse(String input, List<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.startsWith("else ")) {
            String withoutKeyword = stripped.substring("else ".length()).strip();
            if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
                String indent = createIndent(depth);
                return compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1),
                        statement -> compileStatementOrBlock(statement, typeParams, depth + 1))
                        .map(result -> indent + "else {" + result + indent + "}");
            } else {
                return compileStatement(withoutKeyword, typeParams, depth).map(result -> "else " + result);
            }
        }

        return Optional.empty();
    } */};
List<struct String> imports = ArrayList<>();
List<struct String> structs = ArrayList<>();
List<struct String> globals = ArrayList<>();
List<struct String> methods = ArrayList<>();
int counter = 0;
<R> R match(Function<struct T, struct R> whenOk, Function<struct X, struct R> whenErr) {
	return whenErr.apply(this.error);
}
<R> R match(Function<struct T, struct R> whenOk, Function<struct X, struct R> whenErr) {
	return whenOk.apply(this.value);
}
struct private State(Deque<char> queue, List<struct String> segments, struct StringBuilder buffer, int depth) {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
struct public State(Deque<char> queue) {
	this(queue, ArrayList<>(), struct StringBuilder(), 0);
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
List<struct String> segments() {
	return this.segments;
}
char peek() {
	return this.queue.peek();
}
auto __lambda0__(auto input) {
	return compileAndWrite(input, source);
}
auto __lambda1__() {
	return struct Optional.of()
}
auto __lambda2__() {
	return struct Throwable.printStackTrace()
}
void main(struct String* args) {
	struct Path source = Paths.get(".", "src", "java", "magma", "Main.java");
	magma.Files.readString(source).match(__lambda0__, __lambda1__).ifPresent(__lambda2__);
}
Optional<struct IOException> compileAndWrite(struct String input, struct Path source) {
	struct Path target = source.resolveSibling("main.c");
	struct String output = compile(input);
	return magma.Files.writeString(target, output);
}
auto __lambda3__() {
	return struct Main.divideStatementChar()
}
auto __lambda4__() {
	return struct Main.compileRootSegment()
}
auto __lambda5__(auto list) {
	List<struct String> copy = ArrayList<struct String>();
	copy.addAll(imports);
	copy.addAll(structs);
	copy.addAll(globals);
	copy.addAll(methods);
	copy.addAll(list);
	return copy;
}
auto __lambda6__() {
	return struct Main.mergeStatements()
}
auto __lambda7__(auto compiled) {
	return mergeAll(compiled, __lambda6__);
}
auto __lambda8__() {
	return generatePlaceholder(input);
}
struct String compile(struct String input) {
	List<struct String> segments = divide(input, __lambda3__);
	return parseAll(segments, __lambda4__).map(__lambda5__).map(__lambda7__).or(__lambda8__).orElse("");
}
auto __lambda9__() {
	return struct Main.divideStatementChar()
}
auto __lambda10__() {
	return struct Main.mergeStatements()
}
Optional<struct String> compileStatements(struct String input, Function<struct String, Optional<struct String>> compiler) {
	return compileAndMerge(divide(input, __lambda9__), compiler, __lambda10__);
}
auto __lambda11__(auto compiled) {
	return mergeAll(compiled, merger);
}
Optional<struct String> compileAndMerge(List<struct String> segments, Function<struct String, Optional<struct String>> compiler, BiFunction<struct StringBuilder, struct String, struct StringBuilder> merger) {
	return parseAll(segments, compiler).map(__lambda11__);
}
auto __lambda12__(auto _, auto next) {
	return next;
}
struct String mergeAll(List<struct String> compiled, BiFunction<struct StringBuilder, struct String, struct StringBuilder> merger) {
	return compiled.stream().reduce(struct StringBuilder(), merger, __lambda12__).toString();
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
auto __lambda16__(auto _, auto next) {
	return next;
}
Optional<List<struct String>> parseAll(List<struct String> segments, Function<struct String, Optional<struct String>> compiler) {
	return segments.stream().reduce(Optional.of(ArrayList<struct String>()), __lambda15__, __lambda16__);
}
struct StringBuilder mergeStatements(struct StringBuilder output, struct String compiled) {
	return output.append(compiled);
}
auto __lambda17__() {
	return struct input.charAt()
}
auto __lambda18__() {
	return struct LinkedList.new()
}
List<struct String> divide(struct String input, BiFunction<struct State, struct Character, struct State> divider) {
	LinkedList<char> queue = IntStream.range(0, input.length()).mapToObj(__lambda17__).collect(Collectors.toCollection(__lambda18__));
	struct State state = struct State(queue);
	while (state.hasElements()) {
		char c = state.pop();
		if (c == '\'') {
			state.append(c);
			char maybeSlash = state.pop();
			state.append(maybeSlash);
			if (maybeSlash == '\\') state.append(state.pop())
			state.append(state.pop());
			continue;
		}
		if (c == '\"') {
			state.append(c);
			while (state.hasElements()) {
				char next = state.pop();
				state.append(next);
				if (next == '\\') state.append(state.pop())
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
	if (c == ';' && appended.isLevel()) return appended.advance()
	if (c == '}' && isShallow(appended)) return appended.advance().exit()
	if (c == '{' || c == '(') return appended.enter()
	if (c == '}' || c == ')') return appended.exit()
	return appended;
}
int isShallow(struct State state) {
	return state.depth == 1;
}
Optional<struct String> compileRootSegment(struct String input) {
	if (input.startsWith("package ")) return Optional.of("")
	struct String stripped = input.strip();
	if (stripped.startsWith("import ")) {
		struct String right = stripped.substring("import ".length());
		if (right.endsWith(";")) {
			struct String content = right.substring(0, right.length() - ";".length());
			struct String joined = String.join("/", content.split(Pattern.quote(".")));
			imports.add("#include \"./" + joined + "\"\n");
			return Optional.of("");
		}
	}
	Optional<struct String> maybeClass = compileToStruct(input, "class ", ArrayList<>());
	if (maybeClass.isPresent()) return maybeClass
	return generatePlaceholder(input);
}
auto __lambda19__(auto input1) {
	return compileClassMember(input1, typeParams);
}
auto __lambda20__(auto outputContent) {
			structs.add("struct " + name + " {\n" + outputContent + "};\n");
			return "";
}
Optional<struct String> compileToStruct(struct String input, struct String infix, List<struct String> typeParams) {
	int classIndex = input.indexOf(infix);
	if (classIndex < 0) return Optional.empty()
	struct String afterKeyword = input.substring(classIndex + infix.length());
	int contentStart = afterKeyword.indexOf("{");
	if (contentStart >= 0) {
		struct String name = afterKeyword.substring(0, contentStart).strip();
		struct String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
		if (withEnd.endsWith("}")) {
			struct String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
			return compileStatements(inputContent, __lambda19__).map(__lambda20__);
		}
	}
	return Optional.empty();
}
auto __lambda21__() {
	return compileToStruct(input, "interface ", typeParams);
}
auto __lambda22__() {
	return compileToStruct(input, "record ", typeParams);
}
auto __lambda23__() {
	return compileToStruct(input, "class ", typeParams);
}
auto __lambda24__() {
	return compileGlobalInitialization(input, typeParams);
}
auto __lambda25__() {
	return compileDefinitionStatement(input);
}
auto __lambda26__() {
	return compileMethod(input, typeParams);
}
auto __lambda27__() {
	return generatePlaceholder(input);
}
Optional<struct String> compileClassMember(struct String input, List<struct String> typeParams) {
	return compileWhitespace(input).or(__lambda21__).or(__lambda22__).or(__lambda23__).or(__lambda24__).or(__lambda25__).or(__lambda26__).or(__lambda27__);
}
auto __lambda28__(auto result) {
	return "\t" + result + ";\n";
}
Optional<struct String> compileDefinitionStatement(struct String input) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String content = stripped.substring(0, stripped.length() - ";".length());
		return compileDefinition(content).map(__lambda28__);
	}
	return Optional.empty();
}
auto __lambda29__(auto generated) {
	globals.add(generated + ";\n");
	return "";
}
Optional<struct String> compileGlobalInitialization(struct String input, List<struct String> typeParams) {
	return compileInitialization(input, typeParams, 0).map(__lambda29__);
}
auto __lambda30__(auto outputValue) {
	return outputDefinition + " = " + outputValue;
}
auto __lambda31__(auto outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda30__);
}
Optional<struct String> compileInitialization(struct String input, List<struct String> typeParams, int depth) {
	if (!input.endsWith(";")) return Optional.empty()
	struct String withoutEnd = input.substring(0, input.length() - ";".length());
	int valueSeparator = withoutEnd.indexOf("=");
	if (valueSeparator < 0) return Optional.empty()
	struct String definition = withoutEnd.substring(0, valueSeparator).strip();
	struct String value = withoutEnd.substring(valueSeparator + "=".length()).strip();
	return compileDefinition(definition).flatMap(__lambda31__);
}
Optional<struct String> compileWhitespace(struct String input) {
	if (input.isBlank()) return Optional.of("")
	return Optional.empty();
}
auto __lambda32__() {
	return struct Main.compileParameter()
}
Optional<struct String> compileMethod(struct String input, List<struct String> typeParams) {
	int paramStart = input.indexOf("(");
	if (paramStart < 0) return Optional.empty()
	struct String inputDefinition = input.substring(0, paramStart).strip();
	struct String withParams = input.substring(paramStart + "(".length());
	return compileDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) return Optional.empty();

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, __lambda32__).flatMap(outputParams -> assembleMethodBody(typeParams, outputDefinition, outputParams, withParams.substring(paramEnd + ")".length()).strip()));
        });
}
auto __lambda33__(auto input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
auto __lambda34__(auto outputContent) {
		methods.add(header + " {" + outputContent + "\n}\n");
		return Optional.of("");
}
Optional<struct String> assembleMethodBody(List<struct String> typeParams, struct String definition, struct String params, struct String body) {
	struct String header = "\t".repeat(0) + definition + "(" + params + ")";
	if (body.startsWith("{") && body.endsWith("}")) {
		struct String inputContent = body.substring("{".length(), body.length() - "}".length());
		return compileStatements(inputContent, __lambda33__).flatMap(__lambda34__);
	}
	return Optional.of(header + ";");
}
auto __lambda35__() {
	return compileDefinition(definition);
}
auto __lambda36__() {
	return generatePlaceholder(definition);
}
Optional<struct String> compileParameter(struct String definition) {
	return compileWhitespace(definition).or(__lambda35__).or(__lambda36__);
}
auto __lambda37__() {
	return struct Main.divideValueChar()
}
Optional<struct String> compileValues(struct String input, Function<struct String, Optional<struct String>> compiler) {
	List<struct String> divided = divide(input, __lambda37__);
	return compileValues(divided, compiler);
}
struct State divideValueChar(struct State state, char c) {
	if (c == '-') {
		if (state.peek() == '>') {
			state.pop();
			return state.append('-').append('>');
		}
	}
	if (c == ',' && state.isLevel()) return state.advance()
	struct State appended = state.append(c);
	if (c == ' < ' || c == '(') return appended.enter()
	if (c == '>' || c == ')') return appended.exit()
	return appended;
}
auto __lambda38__() {
	return struct Main.mergeValues()
}
Optional<struct String> compileValues(List<struct String> params, Function<struct String, Optional<struct String>> compiler) {
	return compileAndMerge(params, compiler, __lambda38__);
}
auto __lambda39__() {
	return compileConditional(input, typeParams, "if ", depth);
}
auto __lambda40__() {
	return compileConditional(input, typeParams, "while ", depth);
}
auto __lambda41__() {
	return compileElse(input, typeParams, depth);
}
auto __lambda42__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda43__() {
	return compileInitialization(input, typeParams, depth).map(__lambda42__);
}
auto __lambda44__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda45__() {
	return compileStatement(input, typeParams, depth).map(__lambda44__);
}
auto __lambda46__() {
	return compileKeywordStatement(input, depth, "continue");
}
auto __lambda47__() {
	return compileKeywordStatement(input, depth, "break");
}
auto __lambda48__() {
	return generatePlaceholder(input);
}
Optional<struct String> compileStatementOrBlock(struct String input, List<struct String> typeParams, int depth) {
	return compileWhitespace(input).or(__lambda39__).or(__lambda40__).or(__lambda41__).or(__lambda43__).or(__lambda45__).or(__lambda46__).or(__lambda47__).or(__lambda48__);
}
Optional<struct String> compileKeywordStatement(struct String input, int depth, struct String keyword) {
	if (input.strip().equals(keyword + ";")) {
		return Optional.of(formatStatement(depth, keyword));
	}
	else {
		return Optional.empty();
	}
}
struct String formatStatement(int depth, struct String value) {
	return createIndent(depth) + value + ";";
}
struct String createIndent(int depth) {
	return "\n" + "\t".repeat(depth);
}
auto __lambda49__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda50__(auto statements) {
		return withCondition + " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
}
auto __lambda51__(auto result) {
		return withCondition + " " + result;
}
auto __lambda52__(auto newCondition) {
	struct String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		struct String content = withBraces.substring(1, withBraces.length() - 1);
		return compileStatements(content, __lambda49__).map(__lambda50__);
	}
	else {
		return compileStatement(withBraces, typeParams, depth).map(__lambda51__);
	}
}
Optional<struct String> compileConditional(struct String input, List<struct String> typeParams, struct String prefix, int depth) {
	struct String stripped = input.strip();
	if (!stripped.startsWith(prefix)) return Optional.empty()
	struct String afterKeyword = stripped.substring(prefix.length()).strip();
	/* if (!afterKeyword */.startsWith("(")) return Optional.empty();
	struct String withoutConditionStart = afterKeyword.substring(1);
	int conditionEnd = findConditionEnd(withoutConditionStart);
	if (conditionEnd < 0) return Optional.empty()
	struct String oldCondition = withoutConditionStart.substring(0, conditionEnd).strip();
	struct String withBraces = withoutConditionStart.substring(conditionEnd + ")".length()).strip();
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda52__);
}
auto __lambda53__(auto index) {
	return Tuple<>(index, input.charAt(index));
}
auto __lambda54__() {
	return struct LinkedList.new()
}
int findConditionEnd(struct String input) {
	int conditionEnd = /* -1 */;
	int depth0 = 0;
	LinkedList<Tuple<int, struct Character>> queue = IntStream.range(0, input.length()).mapToObj(__lambda53__).collect(Collectors.toCollection(__lambda54__));
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
		if (c == ')' && depth0 == 0) {
			conditionEnd = i;
			break;
		}
		/* if (c */ = /* = '(') depth0 */ +  + ;
		/* if (c */ = /* = ')') depth0-- */;
	}
	return conditionEnd;
}
auto __lambda55__(auto result) {
	return "return " + result;
}
auto __lambda56__(auto newSource) {
			return newDest + " = " + newSource;
}
auto __lambda57__(auto newDest) {
			return compileValue(source, typeParams, depth).map(__lambda56__);
}
Optional<struct String> compileStatement(struct String input, List<struct String> typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda55__);
		}
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			struct String destination = withoutEnd.substring(0, valueSeparator).strip();
			struct String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda57__);
		}
		Optional<struct String> maybeInvocation = compileInvocation(withoutEnd, typeParams, depth);
		if (maybeInvocation.isPresent()) return maybeInvocation
	}
	return Optional.empty();
}
auto __lambda58__(auto result) {
	return "!" + result;
}
auto __lambda59__(auto compiled) {
			return generateLambdaWithReturn(Collections.emptyList(), "\n\treturn " + compiled + "." + property + "()");
}
auto __lambda60__(auto compiled) {
	return compiled + "." + property;
}
auto __lambda61__() {
	return compileOperator(input, typeParams, depth, "<");
}
auto __lambda62__() {
	return compileOperator(input, typeParams, depth, "+");
}
auto __lambda63__() {
	return compileOperator(input, typeParams, depth, ">=");
}
auto __lambda64__() {
	return compileOperator(input, typeParams, depth, "&&");
}
auto __lambda65__() {
	return compileOperator(input, typeParams, depth, "==");
}
auto __lambda66__() {
	return compileOperator(input, typeParams, depth, "!=");
}
auto __lambda67__() {
	return generatePlaceholder(input);
}
Optional<struct String> compileValue(struct String input, List<struct String> typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) return Optional.of(stripped)
	/* if (stripped */.startsWith("'") && stripped.endsWith("'")) return Optional.of(stripped);
	if (isSymbol(stripped) || isNumber(stripped)) {
		return Optional.of(stripped);
	}
	if (stripped.startsWith("new ")) {
		struct String slice = stripped.substring("new ".length());
		int argsStart = slice.indexOf("(");
		if (argsStart >= 0) {
			struct String type = slice.substring(0, argsStart);
			struct String withEnd = slice.substring(argsStart + "(".length()).strip();/* 
                if (withEnd.endsWith(")")) {
                    String argsString = withEnd.substring(0, withEnd.length() - ")".length());
                    return compileType(type, typeParams, depth).flatMap(outputType -> compileArgs(argsString, typeParams, depth).map(value -> outputType + value));
                } */
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda58__);
	}
	Optional<struct String> value = compileLambda(stripped, typeParams, depth);
	if (value.isPresent()) return value
	Optional<struct String> invocation = compileInvocation(input, typeParams, depth);
	if (invocation.isPresent()) return invocation
	int methodIndex = stripped.lastIndexOf("::");
	if (methodIndex >= 0) {
		struct String type = stripped.substring(0, methodIndex).strip();
		struct String property = stripped.substring(methodIndex + "::".length()).strip();
		if (isSymbol(property)) {
			return compileType(type, typeParams, depth).flatMap(__lambda59__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		struct String object = input.substring(0, separator).strip();
		struct String property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda60__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda61__).or(__lambda62__).or(__lambda63__).or(__lambda64__).or(__lambda65__).or(__lambda66__).or(__lambda67__);
}
auto __lambda68__(auto rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
auto __lambda69__(auto leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda68__);
}
Optional<struct String> compileOperator(struct String input, List<struct String> typeParams, int depth, struct String operator) {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) return Optional.empty()
	struct String left = input.substring(0, operatorIndex);
	struct String right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda69__);
}
auto __lambda70__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
auto __lambda71__(auto result) {
		return generateLambdaWithReturn(paramNames, result);
}
auto __lambda72__(auto newValue) {
	return generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";");
}
Optional<struct String> compileLambda(struct String input, List<struct String> typeParams, int depth) {
	int arrowIndex = input.indexOf("->");
	if (arrowIndex < 0) return Optional.empty()
	struct String beforeArrow = input.substring(0, arrowIndex).strip();/* 
        List<String> paramNames; */
	if (isSymbol(beforeArrow)) {
		paramNames = Collections.singletonList(beforeArrow);
	}/*  else if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
            String inner = beforeArrow.substring(1, beforeArrow.length() - 1);
            paramNames = Arrays.stream(inner.split(Pattern.quote(",")))
                    .map(String::strip)
                    .filter(value -> !value.isEmpty())
                    .toList();
        } */
	else {
		return Optional.empty();
	}
	struct String value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		struct String slice = value.substring(1, value.length() - 1);
		return compileStatements(slice, __lambda70__).flatMap(__lambda71__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda72__);
}
auto __lambda73__(auto name) {
	return "auto " + name;
}
Optional<struct String> generateLambdaWithReturn(List<struct String> paramNames, struct String returnValue) {
	int current = counter;/* 
        counter++; */
	struct String lambdaName = "__lambda" + current + "__";
	struct String joined = paramNames.stream().map(__lambda73__).collect(Collectors.joining(", ", "(", ")"));
	methods.add("auto " + lambdaName + joined + " {" + returnValue + "\n}\n");
	return Optional.of(lambdaName);
}
auto __lambda74__() {
	return struct input.charAt()
}
auto __lambda75__() {
	return char.isDigit()
}
int isNumber(struct String input) {
	return IntStream.range(0, input.length()).map(__lambda74__).allMatch(__lambda75__);
}
Optional<struct String> compileInvocation(struct String input, List<struct String> typeParams, int depth) {
	struct String stripped = input.strip();/* 
        if (stripped.endsWith(")")) {
            String sliced = stripped.substring(0, stripped.length() - ")".length());

            int argsStart = findInvocationStart(sliced);

            if (argsStart >= 0) {
                String type = sliced.substring(0, argsStart);
                String withEnd = sliced.substring(argsStart + "(".length()).strip();
                return compileValue(type, typeParams, depth).flatMap(caller -> {
                    return compileArgs(withEnd, typeParams, depth).map(value -> caller + value);
                });
            }
        } */
	return Optional.empty();
}
int findInvocationStart(struct String sliced) {
	int argsStart = /* -1 */;
	int depth0 = 0;
	int i = sliced.length() - 1;
	while (i >= 0) {
		char c = sliced.charAt(i);
		if (c == '(' && depth0 == 0) {
			argsStart = i;
			break;
		}
		/* if (c */ = /* = ')') depth0 */ +  + ;
		/* if (c */ = /* = '(') depth0-- */;/* 
            i--; */
	}
	return argsStart;
}
auto __lambda76__() {
	return compileValue(arg, typeParams, depth);
}
auto __lambda77__(auto arg) {
	return compileWhitespace(arg).or(__lambda76__);
}
auto __lambda78__(auto args) {
	return "(" + args + ")";
}
Optional<struct String> compileArgs(struct String argsString, List<struct String> typeParams, int depth) {
	return compileValues(argsString, __lambda77__).map(__lambda78__);
}
struct StringBuilder mergeValues(struct StringBuilder cache, struct String element) {
	if (cache.isEmpty()) return cache.append(element)
	return cache.append(", ").append(element);
}
auto __lambda79__() {
	return struct String.strip()
}
auto __lambda80__(auto value) {
	return !value.isEmpty();
}
auto __lambda81__() {
	return struct Main.isSymbol()
}
auto __lambda82__(auto outputType) {
	return Optional.of(generateDefinition(typeParams, outputType, name));
}
auto __lambda83__(auto outputType) {
	return Optional.of(generateDefinition(Collections.emptyList(), outputType, name));
}
Optional<struct String> compileDefinition(struct String definition) {
	struct String stripped = definition.strip();
	int nameSeparator = stripped.lastIndexOf(" ");
	if (nameSeparator < 0) return Optional.empty()
	struct String beforeName = stripped.substring(0, nameSeparator).strip();
	struct String name = stripped.substring(nameSeparator + " ".length()).strip();
	if (!isSymbol(name)) return Optional.empty()
	int typeSeparator = /* -1 */;
	int depth = 0;
	int i = beforeName.length() - 1;
	while (i >= 0) {
		char c = beforeName.charAt(i);
		if (c == ' ' && depth == 0) {
			typeSeparator = i;
			break;
		}
		else {
			/* if (c */ = /* = '>') depth */ +  + ;
			/* if (c */ = /* = ' */ < /* ') depth-- */;
		}/* 
            i--; */
	}
	if (typeSeparator >= 0) {
		struct String beforeType = beforeName.substring(0, typeSeparator).strip();
		struct String beforeTypeParams = beforeType;/* 
            List<String> typeParams; */
		if (beforeType.endsWith(">")) {
			struct String withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
			int typeParamStart = withoutEnd.indexOf("<");
			if (typeParamStart >= 0) {
				beforeTypeParams = withoutEnd.substring(0, typeParamStart);
				struct String substring = withoutEnd.substring(typeParamStart + 1);
				typeParams = splitValues(substring);
			}
			else {
				typeParams = Collections.emptyList();
			}
		}
		else {
			typeParams = Collections.emptyList();
		}
		struct String strippedBeforeTypeParams = beforeTypeParams.strip();/* 

            String modifiersString; */
		int annotationSeparator = strippedBeforeTypeParams.lastIndexOf("\n");
		if (annotationSeparator >= 0) {
			modifiersString = strippedBeforeTypeParams.substring(annotationSeparator + "\n".length());
		}
		else {
			modifiersString = strippedBeforeTypeParams;
		}
		int allSymbols = Arrays.stream(modifiersString.split(Pattern.quote(" "))).map(__lambda79__).filter(__lambda80__).allMatch(__lambda81__);
		if (!allSymbols) {
			return Optional.empty();
		}
		struct String inputType = beforeName.substring(typeSeparator + " ".length());
		return compileType(inputType, typeParams, depth).flatMap(__lambda82__);
	}
	else {
		return compileType(beforeName, Collections.emptyList(), depth).flatMap(__lambda83__);
	}
}
auto __lambda84__() {
	return struct String.strip()
}
auto __lambda85__(auto param) {
	return !param.isEmpty();
}
List<struct String> splitValues(struct String substring) {
	struct String* paramsArrays = substring.strip().split(Pattern.quote(","));
	return Arrays.stream(paramsArrays).map(__lambda84__).filter(__lambda85__).toList();
}
struct String generateDefinition(List<struct String> maybeTypeParams, struct String type, struct String name) {/* 
        String typeParamsString; */
	if (maybeTypeParams.isEmpty()) {
		typeParamsString = "";
	}
	else {
		typeParamsString = "<" + String.join(", ", maybeTypeParams) + "> ";
	}
	return typeParamsString + type + " " + name;
}
auto __lambda86__(auto value) {
	return value + "*";
}
auto __lambda87__() {
	return compileType(type, typeParams, depth);
}
auto __lambda88__(auto type) {
			return compileWhitespace(type).or(__lambda87__);
}
auto __lambda89__(auto compiled) {
			return base + " < " + compiled + ">";
}
Optional<struct String> compileType(struct String input, List<struct String> typeParams, int depth) {
	if (input.equals("void")) return Optional.of("void")
	if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
		return Optional.of("int");
	}
	if (input.equals("char") || input.equals("Character")) {
		return Optional.of("char");
	}
	if (input.endsWith("[]")) {
		return compileType(input.substring(0, input.length() - "[]".length()), typeParams, depth).map(__lambda86__);
	}
	struct String stripped = input.strip();
	if (isSymbol(stripped)) {
		if (typeParams.contains(stripped)) {
			return Optional.of(stripped);
		}
		else {
			return Optional.of("struct " + stripped);
		}
	}
	if (stripped.endsWith(">")) {
		struct String slice = stripped.substring(0, stripped.length() - ">".length());
		int argsStart = slice.indexOf("<");
		if (argsStart >= 0) {
			struct String base = slice.substring(0, argsStart).strip();
			struct String params = slice.substring(argsStart + " < ".length()).strip();
			return compileValues(params, __lambda88__).map(__lambda89__);
		}
	}
	return generatePlaceholder(input);
}
auto __lambda90__(auto index) {
	char c = input.charAt(index);
	struct return c = /* = '_'  */ || Character.isLetter(c) ||(index != 0 && Character.isDigit(c));
}
int isSymbol(struct String input) {
	if (input.isBlank()) return false
	return IntStream.range(0, input.length()).allMatch(__lambda90__);
}
Optional<struct String> generatePlaceholder(struct String input) {
	return Optional.of("/* " + input + " */");
}
/* 
 */