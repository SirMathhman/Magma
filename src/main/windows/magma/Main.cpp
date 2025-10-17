// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
struct Definable extends JMethodHeader permits Definition, Placeholder {
};
struct JMethodHeader permits JConstructor, Definable {
};
struct Result<T, X> permits Err, Ok {
};
struct Actual {
};
struct ParseState {
	List<char*> functions;
	List<char*> structs;
	int counter;
};
struct DivideState {
	ArrayList<char*> segments;
	char* input;
	StringBuilder buffer;
	int depth;
	int index;
};
template <typeparam A, typeparam B>
struct Tuple {
	A left;, 
	B right;
};
struct Definition(List<String> annotations, String type, String name) implements Definable {
};
struct Placeholder(String input) implements Definable {
};
struct JConstructor(String name) implements JMethodHeader {
};
template <typeparam T, typeparam X>(T value) implements Result<T, typeparam X>
struct Ok {
};
template <typeparam T, typeparam X>(X error) implements Result<T, typeparam X>
struct Err {
};
struct Main {
};
char* generate_Definable extends JMethodHeader permits Definition, Placeholder();
ParseState new_ParseState(){
	ParseState this;
	this.functions = new_ArrayList<char*>();
	this.structs = new_ArrayList<char*>();
	this.counter =  - 1;
	return this;
}
ParseState addFunction_ParseState(char* func){
	this.functions.add(func);
	return this;
}
ParseState addStruct_ParseState(char* struct){
	this.structs.add(struct);
	return this;
}
char* generateAnonymousFunctionName_ParseState(){
	this.counter++;
	return "__lambda" + this.counter + "__";
}
DivideState new_DivideState(char* input){
	DivideState this;
	this.input = input;
	this.buffer = new_StringBuilder();
	this.depth = 0;
	this.segments = new_ArrayList<char*>();
	this.index = 0;
	return this;
}
Stream<char*> stream_DivideState(){
	return this.segments.stream();
}
DivideState enter_DivideState(){
	this.depth = this.depth + 1;
	return this;
}
DivideState exit_DivideState(){
	this.depth = this.depth - 1;
	return this;
}
boolean isShallow_DivideState(){
	return this.depth == 1;
}
boolean isLevel_DivideState(){
	return this.depth == 0;
}
DivideState append_DivideState(char c){
	this.buffer.append(c);
	return this;
}
DivideState advance_DivideState(){
	this.segments.add(this.buffer.toString());
	this.buffer = new_StringBuilder();
	return this;
}
Optional<Tuple<DivideState, Character>> pop_DivideState(){
	if (this.index >= this.input.length()) return Optional.empty();
	char next = this.input.charAt(this.index);
	this.index++;
	return Optional.of(new_Tuple<DivideState, Character>(this, next));
}
auto __lambda0__(auto tuple) {
	return new_Tuple<DivideState, Character>(tuple.left.append(tuple.right), tuple.right);
}
Optional<Tuple<DivideState, Character>> popAndAppendToTuple_DivideState(){
	return this.pop().map(__lambda0__);
}
auto __lambda1__(auto tuple) {
	return tuple.left;
}
Optional<DivideState> popAndAppendToOption_DivideState(){
	return this.popAndAppendToTuple().map(__lambda1__);
}
Optional<Character> peek_DivideState(){
	if (this.index < this.input.length()) return Optional.of(this.input.charAt(this.index));
	else return Optional.empty();
}
char* generate_Definition(List<String> annotations, String type, String name) implements Definable(){
	return this.type + " " + this.name;
}
char* generate_Placeholder(String input) implements Definable(){
	return wrap(this.input);
}
void main_Main(char** args){
	run().ifPresent(printStackTrace_Throwable);
}
Optional<IOException> run_Main(){
	Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
	Path target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");
	if (!(/*readString(source) instanceof Ok<String, IOException>(String input)*/)) return Optional.empty();
	Path targetParent = target.getParent();
	if (!Files.exists(targetParent)) return createDirectories(targetParent);
	char* output = "// File generated from '" + source + "'. This is not source code!\n" + compile(input);
	return writeString(target, output);
}
Optional<IOException> writeString_Main(Path target, char* output);
Optional<IOException> createDirectories_Main(Path targetParent);
Result<char*, IOException> readString_Main(Path source);
char* compile_Main(char* input){
	StringJoiner joiner = new_StringJoiner("");
	ParseState state = new_ParseState();
	List < String >= list == divide(input, foldStatement_Main).toList();
	int i = 0;
	while (i < list.size()) {
		char* input1 = list.get(i);
		Tuple<char*, ParseState> s = compileRootSegment(input1, state);
		joiner.add(s.left);
		state = s.right;
		i++;
	}
	char* joined = joiner.toString();
	char* joinedStructs = String.join("", state.structs);
	char* joinedFunctions = String.join("", state.functions);
	return joinedStructs + joinedFunctions + joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}";
}
char* compileAll_Main(char* input, BiFunction<DivideState, Character, DivideState> folder, Function<char*, char*> mapper){
	return divide(input, folder).map(mapper).collect(Collectors.joining(", "));
}
Stream<char*> divide_Main(char* input, BiFunction<DivideState, Character, DivideState> folder){
	Tuple<DivideState, Boolean> current = new_Tuple<DivideState, Boolean>(new_DivideState(input), true);
	while (current.right) current == foldCycle(current.left, folder);
	return current.left.advance().stream();
}
Tuple<DivideState, Boolean> foldCycle_Main(DivideState state, BiFunction<DivideState, Character, DivideState> folder){
	Optional<Tuple<DivideState, Character>> maybeNext = state.pop();
	if (maybeNext.isEmpty()) return new_Tuple<DivideState, Boolean>(state, false);
	Tuple<DivideState, Character> tuple = maybeNext.get();
	return new_Tuple<DivideState, Boolean>(foldEscaped(tuple.left, tuple.right, folder), true);
}
auto __lambda2__() {
	return foldDoubleQuotes(state, next);
}
auto __lambda3__() {
	return folder.apply(state, next);
}
DivideState foldEscaped_Main(DivideState state, char next, BiFunction<DivideState, Character, DivideState> folder){
	return foldSingleQuotes(state, next).or(__lambda2__).orElseGet(__lambda3__);
}
Optional<DivideState> foldSingleQuotes_Main(DivideState state, char next){
	if (next != '\'') return Optional.empty();
	DivideState appended = state.append(next);
	return appended.popAndAppendToTuple().flatMap(foldEscaped_Main).flatMap(popAndAppendToOption_DivideState);
}
Optional<DivideState> foldEscaped_Main(Tuple<DivideState, Character> tuple){
	if (tuple.right == '\\') return tuple.left.popAndAppendToOption();
	else return Optional.of(tuple.left);
}
Optional<DivideState> foldDoubleQuotes_Main(DivideState state, char next){
	if (next != '\"') return Optional.empty();
	Tuple<DivideState, Boolean> current = new_Tuple<DivideState, Boolean>(state.append(next), true);
	while (current.right) current == foldUntilDoubleQuotes(current.left);
	return Optional.of(current.left);
}
Tuple<DivideState, Boolean> foldUntilDoubleQuotes_Main(DivideState state){
	Optional<Tuple<DivideState, Character>> maybeNext = state.popAndAppendToTuple();
	if (maybeNext.isEmpty()) return new_Tuple<DivideState, Boolean>(state, false);
	Tuple<DivideState, Character> tuple = maybeNext.get();
	DivideState nextState = tuple.left;
	char nextChar = tuple.right;
	if (nextChar == '\\') return new_Tuple<DivideState, Boolean>(nextState.popAndAppendToOption().orElse(nextState), true);
	if (nextChar == '\"') return new_Tuple<DivideState, Boolean>(nextState, false);
	return new_Tuple<DivideState, Boolean>(nextState, true);
}
DivideState foldStatement_Main(DivideState state, char c){
	DivideState appended = state.append(c);
	if (c == ';' && appended.isLevel()) return appended.advance();
	if (c == '}' && appended.isShallow()) return appended.advance().exit();
	if (c == '{' || c == '(') return appended.enter();
	if (c == '}' || c == ')') return appended.exit();
	return appended;
}
auto __lambda4__() {
	return new_Tuple<char*, ParseState>(wrap(stripped), state);
}
Tuple<char*, ParseState> compileRootSegment_Main(char* input, ParseState state){
	char* stripped = input.strip();
	if (stripped.startsWith("package ") || stripped.startsWith("import ")) return new_Tuple<char*, ParseState>("", state);
	return compileStructure(stripped, "class", state).orElseGet(__lambda4__);
}
auto __lambda5__(auto slice) {
	return "typeparam " + slice;
}
Optional<Tuple<char*, ParseState>> compileStructure_Main(char* input, char* type, ParseState state){
	int i = input.indexOf(type + " ");
	if (i < 0) return Optional.empty();
	char* afterKeyword = input.substring(i + (type + " ").length());
	int contentStart = afterKeyword.indexOf("{");
	if (contentStart < 0) return Optional.empty();
	char* beforeContent = afterKeyword.substring(0, contentStart).strip();
	char* beforeMaybeParams = beforeContent;
	char* recordFields = "";
	if (beforeContent.endsWith(")")) {
		char* slice = beforeContent.substring(0, beforeContent.length() - 1);
		int beforeParams = slice.indexOf("(");
		if (beforeParams >= 0) {
			beforeMaybeParams == slice.substring(0, beforeParams).strip();
			char* substring = slice.substring(beforeParams + 1);
			recordFields == compileValues(substring, compileParameter_Main);
		}
	}
	char* name = beforeMaybeParams;
	List < String >= typeArguments == Collections.emptyList();
	if (beforeMaybeParams.endsWith(">")) {
		char* withoutEnd = beforeMaybeParams.substring(0, beforeMaybeParams.length() - 1);
		int i1 = withoutEnd.indexOf("<");
		if (i1 >= 0) {
			name == withoutEnd.substring(0, i1);
			char* arguments = withoutEnd.substring(i1 + "<".length());
			typeArguments == divide(arguments, foldValue_Main).map(strip_char*).toList();
		}
	}
	char* afterContent = afterKeyword.substring(contentStart + "{".length()).strip();
	if (!afterContent.endsWith("}")) return Optional.empty();
	char* content = afterContent.substring(0, afterContent.length() - "}".length());
	List<char*> segments = divide(content, foldStatement_Main).toList();
	StringBuilder inner = new_StringBuilder();
	ParseState outer = state;
	int j = 0;
	while (j < segments.size()) {
		char* segment = segments.get(j);
		Tuple<char*, ParseState> compiled = compileClassSegment(segment, name, outer);
		inner.append(compiled.left);
		outer = compiled.right;
		j++;
	}
	char* beforeStruct;
	if (typeArguments.isEmpty()) beforeStruct = "";
	else {
		char* collect = typeArguments.stream().map(__lambda5__).collect(Collectors.joining(", ", "<", ">"));
		char* templateValues = collect + System.lineSeparator();
		beforeStruct = "template " + templateValues;
	}
	char* generated = beforeStruct + "struct " + name + " {" + recordFields + inner + System.lineSeparator() + "};" + System.lineSeparator();
	return Optional.of(new_Tuple<char*, ParseState>("", outer.addStruct(generated)));
}
char* compileValues_Main(char* input, Function<char*, char*> mapper){
	return compileAll(input, foldValue_Main, mapper);
}
auto __lambda6__() {
	return wrap(input1);
}
char* compileParameter_Main(char* input1){
	if (input1.isEmpty()) return "";
	return generateField(input1).orElseGet(__lambda6__);
}
Optional<char*> generateField_Main(char* input){
	return compileDefinition(input).map(generate_Definable).map(generateStatement_Main);
}
char* generateStatement_Main(char* content){
	return generateSegment(content + ";", 1);
}
char* generateSegment_Main(char* content, int depth){
	return generateIndent(depth) + content;
}
char* generateIndent_Main(int depth){
	return System.lineSeparator() + "\t".repeat(depth);
}
DivideState foldValue_Main(DivideState state, char next){
	if (next == ',' && state.isLevel()) return state.advance();
	DivideState appended = state.append(next);
	if (next == '-') {
		Optional<Character> peeked = appended.peek();
		if (peeked.isPresent() && peeked.get().equals('>')) return appended.popAndAppendToOption().orElse(appended);
	}
	if (next == '(' || next == '<') return appended.enter();
	if (next == ')' || next == '>') return appended.exit();
	return appended;
}
Tuple<char*, ParseState> compileClassSegment_Main(char* input, char* name, ParseState state){
	char* stripped = input.strip();
	if (stripped.isEmpty()) return new_Tuple<char*, ParseState>("", state);
	return compileClassSegmentValue(stripped, name, state);
}
auto __lambda7__() {
	return compileStructure(input, "record", state);
}
auto __lambda8__() {
	return compileStructure(input, "interface", state);
}
auto __lambda9__() {
	return compileField(input, state);
}
auto __lambda10__() {
	return compileMethod(input, name, state);
}
auto __lambda11__() {
	char* generated = generateSegment(wrap(input), 1);
	return new_Tuple<char*, ParseState>(generated, state);
}
Tuple<char*, ParseState> compileClassSegmentValue_Main(char* input, char* name, ParseState state){
	if (input.isEmpty()) return new_Tuple<char*, ParseState>("", state);
	return compileStructure(input, "class", state).or(__lambda7__).or(__lambda8__).or(__lambda9__).or(__lambda10__).orElseGet(__lambda11__);
}
Optional<Tuple<char*, ParseState>> compileMethod_Main(char* input, char* name, ParseState state){
	int paramStart = input.indexOf("(");
	if (paramStart < 0) return Optional.empty();
	char* beforeParams = input.substring(0, paramStart).strip();
	char* withParams = input.substring(paramStart + 1);
	int paramEnd = withParams.indexOf(")");
	if (paramEnd < 0) return Optional.empty();
	JMethodHeader methodHeader = compileMethodHeader(beforeParams);
	char* inputParams = withParams.substring(0, paramEnd);
	char* withBraces = withParams.substring(paramEnd + 1).strip();
	char* outputParams = compileParameters(inputParams);
	char* outputMethodHeader = transformMethodHeader(methodHeader, name).generate() + "(" + outputParams + ")";
	char* outputBodyWithBraces;
	ParseState current = state;
	if (withBraces.equals(";") || isPlatformDependentMethod(methodHeader)) outputBodyWithBraces = ";";
	else if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		char* inputBody = withBraces.substring(1, withBraces.length() - 1);
		StringJoiner joiner = new_StringJoiner("");
		List < String >= list == divide(inputBody, foldStatement_Main).toList();
		int i = 0;
		while (i < list.size()) {
			char* s = list.get(i);
			Tuple<char*, ParseState> string = compileMethodSegment(s, 1, current);
			joiner.add(string.left);
			current = string.right;
			i++;
		}
		char* compiledBody = joiner.toString();
		char* outputBody;
		if (/*Objects.requireNonNull(methodHeader) instanceof JConstructor*/) outputBody = /* generateStatement(name + " this") + compiledBody + generateStatement("return this")*/;
		else outputBody = compiledBody;
		outputBodyWithBraces = "{" + outputBody + System.lineSeparator() + "}";
	}
	else return Optional.empty();
	char* generated = outputMethodHeader + outputBodyWithBraces + System.lineSeparator();
	return Optional.of(new_Tuple<char*, ParseState>("", current.addFunction(generated)));
}
boolean isPlatformDependentMethod_Main(JMethodHeader methodHeader){
	return /*methodHeader instanceof Definition definition && definition.annotations.contains("Actual")*/;
}
Definable transformMethodHeader_Main(JMethodHeader methodHeader, char* name){
	/*return switch (methodHeader) {
			case JConstructor constructor ->
					new Definition(Collections.emptyList(), constructor.name, "new_" + constructor.name);
			case Definition definition ->
					new Definition(Collections.emptyList(), definition.type, definition.name + "_" + name);
			case Placeholder placeholder -> placeholder;
		}*/
	/**/;
}
auto __lambda12__(auto definable) {
	return definable;
}
auto __lambda13__() {
	return compileConstructor(beforeParams);
}
auto __lambda14__() {
	return new_Placeholder(beforeParams);
}
JMethodHeader compileMethodHeader_Main(char* beforeParams){
	return compileDefinition(beforeParams). < JMethodHeader >= map(__lambda12__).or(__lambda13__).orElseGet(__lambda14__);
}
auto __lambda15__(auto slice) {
	return compileDefinition(slice).map(generate_Definable).orElse("");
}
char* compileParameters_Main(char* input){
	if (input.isEmpty()) return "";
	return compileValues(input, __lambda15__);
}
Tuple<char*, ParseState> compileMethodSegment_Main(char* input, int depth, ParseState state){
	char* stripped = input.strip();
	if (stripped.isEmpty()) return new_Tuple<char*, ParseState>("", state);
	Tuple<char*, ParseState> tuple = compileMethodSegmentValue(stripped, depth, state);
	return new_Tuple<char*, ParseState>(generateSegment(tuple.left, depth), tuple.right);
}
Tuple<char*, ParseState> compileMethodSegmentValue_Main(char* input, int depth, ParseState state){
	char* stripped = input.strip();
	Optional<Tuple<char*, ParseState>> compiled = compileBlock(state, stripped, depth);
	if (compiled.isPresent()) return compiled.get();
	Optional<Tuple<char*, ParseState>> maybeIf = compileConditional("if", depth, state, stripped);
	if (maybeIf.isPresent()) return maybeIf.get();
	Optional<Tuple<char*, ParseState>> maybeWhile = compileConditional("while", depth, state, stripped);
	if (maybeWhile.isPresent()) return maybeWhile.get();
	if (stripped.startsWith("else")) {
		char* substring = stripped.substring("else".length());
		Tuple<char*, ParseState> result = compileMethodSegmentValue(substring, depth, state);
		return new_Tuple<char*, ParseState>("else " + result.left, result.right);
	}
	if (stripped.endsWith(";")) {
		char* slice = stripped.substring(0, stripped.length() - 1);
		Tuple<char*, ParseState> result = compileMethodStatementValue(slice, state);
		return new_Tuple<char*, ParseState>(result.left + ";", result.right);
	}
	return new_Tuple<char*, ParseState>(wrap(stripped), state);
}
Optional<Tuple<char*, ParseState>> compileConditional_Main(char* type, int depth, ParseState state, char* stripped){
	if (!stripped.startsWith(type)) return Optional.empty();
	char* withoutPrefix = stripped.substring(type.length());
	List<char*> conditionEnd = divide(withoutPrefix, foldConditionEnd_Main).toList();
	if (conditionEnd.size() < 2) return Optional.empty();
	char* withConditionEnd = conditionEnd.getFirst();
	char* substring1 = withConditionEnd.substring(0, withConditionEnd.length() - 1).strip();
	char* body = String.join("", conditionEnd.subList(1, conditionEnd.size()));
	if (!substring1.startsWith("(")) return Optional.empty();
	char* expression = substring1.substring(1);
	Tuple<char*, ParseState> condition = compileExpression(expression, state);
	Tuple<char*, ParseState> compiledBody = compileMethodSegmentValue(body, depth, condition.right);
	return Optional.of(new_Tuple<char*, ParseState>(type + " (" + condition.left + ") " + compiledBody.left, compiledBody.right));
}
Optional<Tuple<char*, ParseState>> compileBlock_Main(ParseState state, char* input, int depth){
	if (!input.startsWith("{") ||  != input.endsWith("}")) return Optional.empty();
	char* substring = input.substring(1, input.length() - 1);
	StringJoiner joiner = new_StringJoiner("");
	ParseState current = state;
	List < String >= list == divide(substring, foldStatement_Main).toList();
	int i = 0;
	while (i < list.size()) {
		char* s = list.get(i);
		Tuple<char*, ParseState> string = compileMethodSegment(s, depth + 1, current);
		joiner.add(string.left);
		current = string.right;
		i++;
	}
	char* compiled = joiner.toString();
	return Optional.of(new_Tuple<char*, ParseState>("{" + compiled + generateIndent(depth) + "}", current));
}
DivideState foldConditionEnd_Main(DivideState state, char c){
	DivideState appended = state.append(c);
	if (c == ')') {
		DivideState exited = appended.exit();
		if (exited.isLevel()) return exited.advance();
	}
	if (c == '(') return appended.enter();
	return appended;
}
auto __lambda16__(auto generated) {
	return new_Tuple<char*, ParseState>(generated, state);
}
auto __lambda17__() {
	return compileExpression(destinationString, state);
}
auto __lambda18__(auto value) {
	return new_Tuple<char*, ParseState>(value.generate(), state);
}
auto __lambda19__() {
	return new_Tuple<char*, ParseState>(wrap(input), state);
}
Tuple<char*, ParseState> compileMethodStatementValue_Main(char* input, ParseState state){
	if (input.startsWith("return ")) {
		char* substring = input.substring("return ".length());
		Tuple<char*, ParseState> result = compileExpression(substring, state);
		return new_Tuple<char*, ParseState>("return " + result.left, result.right);
	}
	if (input.endsWith("++")) {
		char* slice = input.substring(0, input.length() - 2);
		Optional<Tuple<char*, ParseState>> temp = tryCompileExpression(slice, state);
		if (temp.isPresent()) {
			Tuple<char*, ParseState> result = temp.get();
			return new_Tuple<char*, ParseState>(result.left + "++", result.right);
		}
	}
	Optional<Tuple<char*, ParseState>> invokableResult = compileInvokable(state, input);
	if (invokableResult.isPresent()) return invokableResult.get();
	int i = input.indexOf("=");
	if (i >= 0) {
		char* destinationString = input.substring(0, i);
		char* source = input.substring(i + 1);
		Tuple<char*, ParseState> destinationResult = compileDefinition(destinationString).map(generate_Definition).map(__lambda16__).orElseGet(__lambda17__);
		Tuple<char*, ParseState> sourceResult = compileExpression(source, destinationResult.right);
		return new_Tuple<char*, ParseState>(destinationResult.left + " = " + sourceResult.left, sourceResult.right);
	}
	return compileDefinition(input).map(__lambda18__).orElseGet(__lambda19__);
}
auto __lambda20__() {
	return new_Tuple<char*, ParseState>(wrap(input), state);
}
Tuple<char*, ParseState> compileExpression_Main(char* input, ParseState state){
	return tryCompileExpression(input, state).orElseGet(__lambda20__);
}
auto __lambda21__() {
	return compileOperator(stripped, "-", state);
}
auto __lambda22__() {
	return compileOperator(stripped, ">=", state);
}
auto __lambda23__() {
	return compileOperator(stripped, "<", state);
}
auto __lambda24__() {
	return compileOperator(stripped, "!=", state);
}
auto __lambda25__() {
	return compileOperator(stripped, "==", state);
}
auto __lambda26__() {
	return compileOperator(stripped, "&&", state);
}
auto __lambda27__() {
	return compileOperator(stripped, "||", state);
}
auto __lambda28__() {
	return compileIdentifier(stripped, state);
}
auto __lambda29__() {
	return compileNumber(stripped, state);
}
Optional<Tuple<char*, ParseState>> tryCompileExpression_Main(char* input, ParseState state){
	char* stripped = input.strip();
	if (stripped.startsWith("'") && stripped.endsWith("'") && stripped.length() <  == 4) return Optional.of(new_Tuple<char*, ParseState>(stripped, state));
	if (isString(stripped)) return Optional.of(new_Tuple<char*, ParseState>(stripped, state));
	if (stripped.startsWith("!")) {
		char* slice = stripped.substring(1);
		Optional<Tuple<char*, ParseState>> maybeResult = tryCompileExpression(slice, state);
		if (maybeResult.isPresent()) {
			Tuple<char*, ParseState> result = maybeResult.get();
			return Optional.of(new_Tuple<char*, ParseState>("!" + result.left, result.right));
		}
	}
	Optional<Tuple<char*, ParseState>> lambdaResult = compileLambda(state, stripped);
	if (lambdaResult.isPresent()) return lambdaResult;
	Optional<Tuple<char*, ParseState>> left = compileInvokable(state, stripped);
	if (left.isPresent()) return left;
	int separator = stripped.lastIndexOf("::");
	if (separator >= 0) {
		char* substring = stripped.substring(0, separator);
		char* name = stripped.substring(separator + 2).strip();
		if (isIdentifier(name)) {
			Optional<char*> maybeResult = compileType(substring);
			if (maybeResult.isPresent()) {
				char* result = maybeResult.get();
				return Optional.of(new_Tuple<char*, ParseState>(name + "_" + result, state));
			}
		}
	}
	int i = stripped.lastIndexOf(".");
	if (i >= 0) {
		char* substring = stripped.substring(0, i);
		char* name = stripped.substring(i + 1).strip();
		if (isIdentifier(name)) {
			Optional<Tuple<char*, ParseState>> maybeResult = tryCompileExpression(substring, state);
			if (maybeResult.isPresent()) {
				Tuple<char*, ParseState> result = maybeResult.get();
				return Optional.of(new_Tuple<char*, ParseState>(result.left + "." + name, result.right));
			}
		}
	}
	return compileOperator(stripped, "+", state).or(__lambda21__).or(__lambda22__).or(__lambda23__).or(__lambda24__).or(__lambda25__).or(__lambda26__).or(__lambda27__).or(__lambda28__).or(__lambda29__);
}
auto __lambda30__(auto tuple, auto s) {
	return mergeExpression(tuple.left, tuple.right, s);
}
auto __lambda31__(auto _, auto next) {
	return next;
}
Optional<Tuple<char*, ParseState>> compileInvokable_Main(ParseState state, char* stripped){
	if (!stripped.endsWith(")")) return Optional.empty();
	char* slice = stripped.substring(0, stripped.length() - 1);
	List<char*> segments = findArgStart(slice).toList();
	if (segments.size() < 2) return Optional.empty();
	char* callerWithExt = String.join("", segments.subList(0, segments.size() - 1));
	if (!callerWithExt.endsWith("(")) return Optional.empty();
	char* caller = callerWithExt.substring(0, callerWithExt.length() - 1);
	char* arguments = segments.getLast();
	Optional<Tuple<char*, ParseState>> maybeCallerResult = compileCaller(state, caller);
	if (maybeCallerResult.isEmpty()) return Optional.empty();
	Tuple<char*, ParseState> callerResult = maybeCallerResult.get();
	Tuple<StringJoiner, ParseState> reduce = divide(arguments, foldValue_Main).toList().stream().reduce(new_Tuple<StringJoiner, ParseState>(new_StringJoiner(", "), callerResult.right), __lambda30__, __lambda31__);
	char* collect = reduce.left.toString();
	return Optional.of(new_Tuple<char*, ParseState>(callerResult.left + "(" + collect + ")", reduce.right));
}
Tuple<StringJoiner, ParseState> mergeExpression_Main(StringJoiner joiner, ParseState state, char* segment){
	Tuple<char*, ParseState> result = compileExpression(segment, state);
	StringJoiner add = joiner.add(result.left);
	return new_Tuple<StringJoiner, ParseState>(add, result.right);
}
auto __lambda32__(auto state, auto c) {
	DivideState appended = state.append(c);
	if (c == '(') {
		DivideState entered = appended.enter();
		if (entered.isShallow()) return entered.advance();
		else return entered;
	}
	if (c == ')') return appended.exit();
	return appended;
}
Stream<char*> findArgStart_Main(char* input){
	return divide(input, __lambda32__);
}
auto __lambda33__(auto slice) {
	return !slice.isEmpty();
}
auto __lambda34__(auto slice) {
	return "auto " + slice;
}
Optional<Tuple<char*, ParseState>> compileLambda_Main(ParseState state, char* stripped){
	int i1 = stripped.indexOf("->");
	if (i1 < 0) return Optional.empty();
	char* beforeArrow = stripped.substring(0, i1).strip();
	char* outputParams;
	if (isIdentifier(beforeArrow)) outputParams = "auto " + beforeArrow;
	else if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
		char* withoutParentheses = beforeArrow.substring(1, beforeArrow.length() - 1);
		outputParams == Arrays.stream(withoutParentheses.split(Pattern.quote(","))).map(strip_char*).filter(__lambda33__).map(__lambda34__).collect(Collectors.joining(", "));
	}
	else return Optional.empty();
	char* body = stripped.substring(i1 + 2).strip();
	Tuple<char*, ParseState> bodyResult = compileLambdaBody(state, body);
	char* generatedName = bodyResult.right.generateAnonymousFunctionName();
	char* s1 = "auto " + generatedName + "(" + outputParams + ") " + bodyResult.left + System.lineSeparator();
	return Optional.of(new_Tuple<char*, ParseState>(generatedName, bodyResult.right.addFunction(s1)));
}
Tuple<char*, ParseState> compileLambdaBody_Main(ParseState state, char* body){
	Optional<Tuple<char*, ParseState>> maybeBlock = compileBlock(state, body, 0);
	if (maybeBlock.isPresent()) return maybeBlock.get();
	Tuple<char*, ParseState> result = compileExpression(body, state);
	char* s = generateStatement("return " + result.left);
	char* s2 = "{" + s + generateIndent(0) + "}";
	return new_Tuple<char*, ParseState>(s2, result.right);
}
Optional<Tuple<char*, ParseState>> compileCaller_Main(ParseState state, char* caller){
	if (caller.startsWith("new ")) {
		Optional<char*> newType = compileType(caller.substring("new ".length()));
		if (newType.isPresent()) return Optional.of(new_Tuple<char*, ParseState>("new_" + newType.get(), state));
	}
	return tryCompileExpression(caller, state);
}
Optional<Tuple<char*, ParseState>> compileIdentifier_Main(char* stripped, ParseState state){
	if (isIdentifier(stripped)) return Optional.of(new_Tuple<char*, ParseState>(stripped, state));
	return Optional.empty();
}
Optional<Tuple<char*, ParseState>> compileNumber_Main(char* stripped, ParseState state){
	if (isNumber(stripped)) return Optional.of(new_Tuple<char*, ParseState>(stripped, state));
	return Optional.empty();
}
auto __lambda35__(auto state1, auto next) {
	return foldOperator(operator, state1, next);
}
Optional<Tuple<char*, ParseState>> compileOperator_Main(char* input, char* operator, ParseState state){
	List<char*> segments = divide(input, __lambda35__).toList();
	if (segments.size() < 2) return Optional.empty();
	char* left = segments.getFirst();
	char* right = String.join(operator, segments.subList(1, segments.size()));
	Optional<Tuple<char*, ParseState>> maybeLeftResult = tryCompileExpression(left, state);
	if (maybeLeftResult.isEmpty()) return Optional.empty();
	Tuple<char*, ParseState> leftResult = maybeLeftResult.get();
	Optional<Tuple<char*, ParseState>> maybeRightResult = tryCompileExpression(right, leftResult.right);
	if (maybeRightResult.isEmpty()) return Optional.empty();
	Tuple<char*, ParseState> rightResult = maybeRightResult.get();
	char* generated = leftResult.left + " " + operator + " " + rightResult.left;
	return Optional.of(new_Tuple<char*, ParseState>(generated, rightResult.right));
}
auto __lambda36__(auto inner) {
	return inner.left;
}
DivideState foldOperator_Main(char* operator, DivideState state1, Character next){
	if (next != operator.charAt(0)) return state1.append(next);
	Optional<Character> peeked = state1.peek();
	if (operator.length() >= 2 && peeked.isPresent() && peeked.get() == operator.charAt(1)) return state1.pop().map(__lambda36__).orElse(state1).advance();
	return state1.advance();
}
boolean isString_Main(char* stripped){
	if (stripped.length() < 2) return false;
	boolean hasDoubleQuotes = stripped.startsWith("\"") && stripped.endsWith("\"");
	if (!hasDoubleQuotes) return false;
	char* content = stripped.substring(1, stripped.length() - 1);
	return areAllDoubleQuotesEscaped(content);
}
auto __lambda37__(auto i) {
	char c = input.charAt(i);
	if (c != '\"') return true;
	if (i == 0) return false;
	char previous = input.charAt(i - 1);
	return previous == '\\';
}
boolean areAllDoubleQuotesEscaped_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda37__);
}
auto __lambda38__(auto i) {
	return Character.isDigit(input.charAt(i));
}
boolean isNumber_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda38__);
}
auto __lambda39__(auto i) {
	char next = input.charAt(i);
	boolean isValidDigit = i != 0 && Character.isDigit(next);
	return Character.isLetter(next) || isValidDigit;
}
boolean isIdentifier_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda39__);
}
Optional<JMethodHeader> compileConstructor_Main(char* beforeParams){
	int separator = beforeParams.lastIndexOf(" ");
	if (separator < 0) return Optional.empty();
	char* name = beforeParams.substring(separator + " ".length());
	return Optional.of(new_JConstructor(name));
}
Optional<Tuple<char*, ParseState>> compileField_Main(char* input, ParseState state){
	if (input.endsWith(";")) {
		char* substring = input.substring(0, input.length() - ";".length()).strip();
		Optional<char*> s = generateField(substring);
		if (s.isPresent()) return Optional.of(new_Tuple<char*, ParseState>(s.get(), state));
	}
	return Optional.empty();
}
auto __lambda40__(auto type) {
	return new_Definition(Collections.emptyList(), type, name);
}
auto __lambda41__(auto type) {
	return new_Definition(annotations, type, name);
}
Optional<Definition> compileDefinition_Main(char* input){
	char* stripped = input.strip();
	int index = stripped.lastIndexOf(" ");
	if (index < 0) return Optional.empty();
	char* beforeName = stripped.substring(0, index).strip();
	char* name = stripped.substring(index + " ".length()).strip();
	if (!isIdentifier(name)) return Optional.empty();
	List<char*> segments = divide(beforeName, foldTypeSeparator_Main).toList();
	if (segments.size() < 2) return compileType(beforeName).map(__lambda40__);
	char* withoutLast = String.join(" ", segments.subList(0, segments.size() - 1));
	List<char*> annotations = findAnnotations(withoutLast);
	char* typeString = segments.getLast();
	return compileType(typeString).map(__lambda41__);
}
auto __lambda42__(auto slice) {
	return slice.startsWith("@");
}
auto __lambda43__(auto slice) {
	return slice.substring(1);
}
List<char*> findAnnotations_Main(char* withoutLast){
	int i = withoutLast.lastIndexOf("\n");
	if (i < 0) return Collections.emptyList();
	char** slices = withoutLast.substring(0, i).strip().split(Pattern.quote("\n"));
	return Arrays.stream(slices).map(strip_char*).filter(__lambda42__).map(__lambda43__).toList();
}
DivideState foldTypeSeparator_Main(DivideState state, Character c){
	if (c == ' ' && state.isLevel()) return state.advance();
	DivideState appended = state.append(c);
	if (c == '<') return appended.enter();
	if (c == '>') return appended.exit();
	return appended;
}
auto __lambda44__() {
	return wrap(slice);
}
auto __lambda45__(auto slice) {
	return compileType(slice).orElseGet(__lambda44__);
}
auto __lambda46__(auto result) {
	return result + "*";
}
Optional<char*> compileType_Main(char* input){
	char* stripped = input.strip();
	if (stripped.equals("public")) return Optional.empty();
	if (stripped.endsWith(">")) {
		char* withoutEnd = stripped.substring(0, stripped.length() - 1);
		int argumentStart = withoutEnd.indexOf("<");
		if (argumentStart >= 0) {
			char* base = withoutEnd.substring(0, argumentStart);
			char* argumentsString = withoutEnd.substring(argumentStart + "<".length());
			char* arguments = compileValues(argumentsString, __lambda45__);
			return Optional.of(base + "<" + arguments + ">");
		}
	}
	if (stripped.endsWith("[]")) {
		char* slice = stripped.substring(0, stripped.length() - 2);
		return compileType(slice).map(__lambda46__);
	}
	if (stripped.equals("String")) return Optional.of("char*");
	if (stripped.equals("int")) return Optional.of("int");
	if (isIdentifier(stripped)) return Optional.of(stripped);
	return Optional.of(wrap(stripped));
}
char* wrap_Main(char* input){
	char* replaced = input.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
/**/int main(){
	main_Main();
	return 0;
}