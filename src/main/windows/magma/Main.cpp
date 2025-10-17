// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
struct Definable extends JMethodHeader permits Definition, Placeholder {
};
struct JMethodHeader permits JConstructor, Definable {
};
struct ParseState {
	List<char*> functions;
	List<char*> structs;
	/*private int counter = -1;*/
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
struct Definition(String type, String name) implements Definable {
};
struct Placeholder(String input) implements Definable {
};
struct JConstructor(String name) implements JMethodHeader {
};
struct Main {
};
char* generate_Definable extends JMethodHeader permits Definition, Placeholder();
ParseState new_ParseState(){
	ParseState this;
	/*this.functions = new ArrayList<String>*/();
	/*this.structs = new ArrayList<String>*/();
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
	/*this.counter++*/;
	return "__lambda" + this.counter + "__";
}
DivideState new_DivideState(char* input){
	DivideState this;
	this.input = input;
	/*this.buffer = new StringBuilder*/();
	this.depth = 0;
	/*this.segments = new ArrayList<String>*/();
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
	/*this.buffer = new StringBuilder*/();
	return this;
}
Optional<Tuple<DivideState, Character>> pop_DivideState(){
	if (this.index >= this.input.length()) return Optional.empty();
	/*final char next = this*/.input.charAt(this.index);
	/*this.index++*/;
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
char* generate_Definition(String type, String name) implements Definable(){
	return this.type + " " + this.name;
}
char* generate_Placeholder(String input) implements Definable(){
	return wrap(this.input);
}
void main_Main(/*String[]*/ args){
	/*try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);

			final Path target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");
			final Path targetParent = target.getParent();

			if (!Files.exists(targetParent)) Files.createDirectories(targetParent);
			Files.writeString(target, "// File generated from '" + source + "'. This is not source code!\n" + compile(input));
		}*/
	/*catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}*/
}
char* compile_Main(char* input){
	/*StringJoiner joiner = new StringJoiner*/("");
	/*ParseState state = new ParseState*/();
	/*for (String input1 : divide(input, Main::foldStatement).toList()) {
			Tuple<String, ParseState> s = compileRootSegment(input1, state);
			joiner.add(s.left);
			state = s.right;
		}*/
	/*final String joined = joiner*/.toString();
	/*final String joinedStructs = String*/.join("", state.structs);
	/*final String joinedFunctions = String*/.join("", state.functions);
	return joinedStructs + joinedFunctions + joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}";
}
char* compileAll_Main(char* input, BiFunction<DivideState, Character, DivideState> folder, Function<char*, char*> mapper){
	return divide(input, folder).map(mapper).collect(Collectors.joining(", "));
}
Stream<char*> divide_Main(char* input, BiFunction<DivideState, Character, DivideState> folder){
	/*DivideState current = new DivideState*/(input);
	while (true) {
		/*final Optional<Tuple<DivideState, Character>> maybeNext = current*/.pop();
		if (maybeNext.isEmpty()) /*break*/;
		/*final Tuple<DivideState, Character> tuple = maybeNext*/.get();
		current == foldEscaped(tuple.left, tuple.right, folder);
	}
	return current.advance().stream();
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
	/*final DivideState appended = state*/.append(next);
	return appended.popAndAppendToTuple().flatMap(foldEscaped_Main).flatMap(popAndAppendToOption_DivideState);
}
Optional<DivideState> foldEscaped_Main(Tuple<DivideState, Character> tuple){
	if (tuple.right == '\\') return tuple.left.popAndAppendToOption();
	else return Optional.of(tuple.left);
}
Optional<DivideState> foldDoubleQuotes_Main(DivideState state, char next){
	if (next != '\"') return Optional.empty();
	/*DivideState appended = state*/.append(next);
	while (true) {
		/*final Optional<Tuple<DivideState, Character>> maybeNext = appended*/.popAndAppendToTuple();
		if (maybeNext.isPresent()) {
			/*final Tuple<DivideState, Character> tuple = maybeNext*/.get();
			appended = tuple.left;
			char c = tuple.right;
			if (c == '\\') appended == appended.popAndAppendToOption().orElse(appended);
			if (c == '\"') /*break*/;
		}
		else /*break*/;
	}
	return Optional.of(appended);
}
DivideState foldStatement_Main(DivideState state, char c){
	/*final DivideState appended = state*/.append(c);
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
	/*final String stripped = input*/.strip();
	if (stripped.startsWith("package ") || stripped.startsWith("import ")) return new_Tuple<char*, ParseState>("", state);
	return compileStructure(stripped, "class", state).orElseGet(__lambda4__);
}
Optional<Tuple<char*, ParseState>> compileStructure_Main(char* input, char* type, ParseState state){
	/*final int i = input*/.indexOf(type + " ");
	if (i < 0) return Optional.empty();
	/*final String afterKeyword = input*/.substring(i + (type + " ").length());
	/*final int contentStart = afterKeyword*/.indexOf("{");
	if (contentStart < 0) return Optional.empty();
	/*final String beforeContent = afterKeyword*/.substring(0, contentStart).strip();
	/*// if (!isIdentifier(beforeContent)) return Optional*/.empty();
	char* beforeMaybeParams = beforeContent;
	char* recordFields = "";
	if (beforeContent.endsWith(")")) {
		/*final String slice = beforeContent*/.substring(0, beforeContent.length() - 1);
		/*final int beforeParams = slice*/.indexOf("(");
		if (beforeParams >= 0) {
			beforeMaybeParams == slice.substring(0, beforeParams).strip();
			/*final String substring = slice*/.substring(beforeParams + 1);
			recordFields == compileValues(substring, compileParameter_Main);
		}
	}
	char* name = beforeMaybeParams;
	List < String >= typeArguments == Collections.emptyList();
	if (beforeMaybeParams.endsWith(">")) {
		/*final String withoutEnd = beforeMaybeParams*/.substring(0, beforeMaybeParams.length() - 1);
		/*final int i1 = withoutEnd*/.indexOf("<");
		if (i1 >= 0) {
			name == withoutEnd.substring(0, i1);
			/*final String arguments = withoutEnd*/.substring(i1 + "<".length());
			typeArguments == divide(arguments, foldValue_Main).map(strip_char*).toList();
		}
	}
	/*final String afterContent = afterKeyword*/.substring(contentStart + "{".length()).strip();
	if (!afterContent.endsWith("}")) return Optional.empty();
	/*final String content = afterContent*/.substring(0, afterContent.length() - "}".length());
	/*final List<String> segments = divide*/(content, foldStatement_Main).toList();
	/*StringBuilder inner = new StringBuilder*/();
	ParseState outer = state;
	/*for (String segment : segments) {
			Tuple<String, ParseState> compiled = compileClassSegment(segment, name, outer);
			inner.append(compiled.left);
			outer = compiled.right;
		}*/
	char* beforeStruct;
	if (typeArguments.isEmpty()) beforeStruct = "";
	else {
		/*final String templateValues =
					typeArguments.stream().map(slice -> "typeparam " + slice).collect(Collectors.joining(", ", "<", ">")) +
					System*/.lineSeparator();
		beforeStruct = "template " + templateValues;
	}
	/*final String generated =
				beforeStruct + "struct " + name + " {" + recordFields + inner + System.lineSeparator() + "};" +
				System*/.lineSeparator();
	return Optional.of(new_Tuple<char*, ParseState>("", outer.addStruct(generated)));
}
char* compileValues_Main(char* input, Function<char*, char*> mapper){
	return compileAll(input, foldValue_Main, mapper);
}
auto __lambda5__() {
	return wrap(input1);
}
char* compileParameter_Main(char* input1){
	if (input1.isEmpty()) return "";
	return generateField(input1).orElseGet(__lambda5__);
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
	/*final DivideState appended = state*/.append(next);
	if (next == '-') {
		/*final Optional<Character> peeked = appended*/.peek();
		if (peeked.isPresent() && peeked.get().equals('>')) return appended.popAndAppendToOption().orElse(appended);
	}
	if (next == '(' || next == '<') return appended.enter();
	if (next == ')' || next == '>') return appended.exit();
	return appended;
}
Tuple<char*, ParseState> compileClassSegment_Main(char* input, char* name, ParseState state){
	/*final String stripped = input*/.strip();
	if (stripped.isEmpty()) return new_Tuple<char*, ParseState>("", state);
	return compileClassSegmentValue(stripped, name, state);
}
auto __lambda6__() {
	return compileStructure(input, "record", state);
}
auto __lambda7__() {
	return compileStructure(input, "interface", state);
}
auto __lambda8__() {
	return compileField(input, state);
}
auto __lambda9__() {
	return compileMethod(input, name, state);
}
auto __lambda10__() {
	/*final String generated = generateSegment*/(wrap(input), 1);
	return new_Tuple<char*, ParseState>(generated, state);
}
Tuple<char*, ParseState> compileClassSegmentValue_Main(char* input, char* name, ParseState state){
	if (input.isEmpty()) return new_Tuple<char*, ParseState>("", state);
	return compileStructure(input, "class", state).or(__lambda6__).or(__lambda7__).or(__lambda8__).or(__lambda9__).orElseGet(__lambda10__);
}
Optional<Tuple<char*, ParseState>> compileMethod_Main(char* input, char* name, ParseState state){
	/*final int paramStart = input*/.indexOf("(");
	if (paramStart < 0) return Optional.empty();
	/*final String beforeParams = input*/.substring(0, paramStart).strip();
	/*final String withParams = input*/.substring(paramStart + 1);
	/*final int paramEnd = withParams*/.indexOf(")");
	if (paramEnd < 0) return Optional.empty();
	/*final JMethodHeader methodHeader = compileMethodHeader*/(beforeParams);
	/*final String inputParams = withParams*/.substring(0, paramEnd);
	/*final String withBraces = withParams*/.substring(paramEnd + 1).strip();
	/*final String outputParams = compileParameters*/(inputParams);
	char* outputMethodHeader = transformMethodHeader(methodHeader, name).generate() + "(" + outputParams + ")";
	char* outputBodyWithBraces;
	ParseState current = state;
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		/*final String inputBody = withBraces*/.substring(1, withBraces.length() - 1);
		/*StringJoiner joiner = new StringJoiner*/("");
		/*for (String s : divide(inputBody, Main::foldStatement).toList()) {
				Tuple<String, ParseState> string = compileMethodSegment(s, 1, current);
				joiner.add(string.left);
				current = string.right;
			}*/
		/*final String compiledBody = joiner*/.toString();
		char* outputBody;
		if (/*Objects.requireNonNull(methodHeader) instanceof JConstructor*/) /*outputBody = generateStatement(name + " this") + compiledBody + generateStatement*/("return this");
		else outputBody = compiledBody;
		outputBodyWithBraces = "{" + outputBody + System.lineSeparator() + "}";
	}
	else if (withBraces.equals(";")) outputBodyWithBraces = ";";
	else return Optional.empty();
	/*final String generated = outputMethodHeader + outputBodyWithBraces + System*/.lineSeparator();
	return Optional.of(new_Tuple<char*, ParseState>("", current.addFunction(generated)));
}
Definable transformMethodHeader_Main(JMethodHeader methodHeader, char* name){
	/*return switch (methodHeader) {
			case JConstructor constructor -> new Definition(constructor.name, "new_" + constructor.name);
			case Definition definition -> new Definition(definition.type, definition.name + "_" + name);
			case Placeholder placeholder -> placeholder;
		}*/
	/**/;
}
auto __lambda11__(auto definable) {
	return definable;
}
auto __lambda12__() {
	return compileConstructor(beforeParams);
}
auto __lambda13__() {
	return new_Placeholder(beforeParams);
}
JMethodHeader compileMethodHeader_Main(char* beforeParams){
	return compileDefinition(beforeParams). < JMethodHeader >= map(__lambda11__).or(__lambda12__).orElseGet(__lambda13__);
}
auto __lambda14__(auto slice) {
	return compileDefinition(slice).map(generate_Definable).orElse("");
}
char* compileParameters_Main(char* input){
	if (input.isEmpty()) return "";
	return compileValues(input, __lambda14__);
}
Tuple<char*, ParseState> compileMethodSegment_Main(char* input, int depth, ParseState state){
	/*final String stripped = input*/.strip();
	if (stripped.isEmpty()) return new_Tuple<char*, ParseState>("", state);
	/*final Tuple<String, ParseState> tuple = compileMethodSegmentValue*/(stripped, depth, state);
	return new_Tuple<char*, ParseState>(generateSegment(tuple.left, depth), tuple.right);
}
Tuple<char*, ParseState> compileMethodSegmentValue_Main(char* input, int depth, ParseState state){
	/*final String stripped = input*/.strip();
	/*final Optional<Tuple<String, ParseState>> compiled = compileBlock*/(state, stripped, depth);
	if (compiled.isPresent()) return compiled.get();
	/*final Optional<Tuple<String, ParseState>> maybeIf = compileConditional*/("if", depth, state, stripped);
	if (maybeIf.isPresent()) return maybeIf.get();
	/*final Optional<Tuple<String, ParseState>> maybeWhile = compileConditional*/("while", depth, state, stripped);
	if (maybeWhile.isPresent()) return maybeWhile.get();
	if (stripped.startsWith("else")) {
		/*final String substring = stripped*/.substring("else".length());
		/*final Tuple<String, ParseState> result = compileMethodSegmentValue*/(substring, depth, state);
		return new_Tuple<char*, ParseState>("else " + result.left, result.right);
	}
	if (stripped.endsWith(";")) {
		/*final String slice = stripped*/.substring(0, stripped.length() - 1);
		/*final Tuple<String, ParseState> result = compileMethodStatementValue*/(slice, state);
		return new_Tuple<char*, ParseState>(result.left + ";", result.right);
	}
	return new_Tuple<char*, ParseState>(wrap(stripped), state);
}
Optional<Tuple<char*, ParseState>> compileConditional_Main(char* type, int depth, ParseState state, char* stripped){
	if (!stripped.startsWith(type)) return Optional.empty();
	/*final String withoutPrefix = stripped*/.substring(type.length());
	/*final List<String> conditionEnd = divide*/(withoutPrefix, foldConditionEnd_Main).toList();
	if (conditionEnd.size() < 2) return Optional.empty();
	/*final String withConditionEnd = conditionEnd*/.getFirst();
	/*final String substring1 = withConditionEnd*/.substring(0, withConditionEnd.length() - 1).strip();
	/*final String body = String*/.join("", conditionEnd.subList(1, conditionEnd.size()));
	if (!substring1.startsWith("(")) return Optional.empty();
	/*final String expression = substring1*/.substring(1);
	/*final Tuple<String, ParseState> condition = compileExpression*/(expression, state);
	/*final Tuple<String, ParseState> compiledBody = compileMethodSegmentValue*/(body, depth, condition.right);
	return Optional.of(new_Tuple<char*, ParseState>(type + " (" + condition.left + ") " + compiledBody.left, compiledBody.right));
}
Optional<Tuple<char*, ParseState>> compileBlock_Main(ParseState state, char* input, int depth){
	if (!input.startsWith("{") ||  != input.endsWith("}")) return Optional.empty();
	/*final String substring = input*/.substring(1, input.length() - 1);
	/*StringJoiner joiner = new StringJoiner*/("");
	ParseState current = state;
	List < String >= list == divide(substring, foldStatement_Main).toList();
	int i = 0;
	while (i < list.size()) {
		/*String s = list*/.get(i);
		/*Tuple<String, ParseState> string = compileMethodSegment*/(s, depth + 1, current);
		joiner.add(string.left);
		current = string.right;
		/*i++*/;
	}
	/*final String compiled = joiner*/.toString();
	return Optional.of(new_Tuple<char*, ParseState>("{" + compiled + generateIndent(depth) + "}", current));
}
DivideState foldConditionEnd_Main(DivideState state, char c){
	/*final DivideState appended = state*/.append(c);
	if (c == ')') {
		/*final DivideState exited = appended*/.exit();
		if (exited.isLevel()) return exited.advance();
	}
	if (c == '(') return appended.enter();
	return appended;
}
auto __lambda15__(auto generated) {
	return new_Tuple<char*, ParseState>(generated, state);
}
auto __lambda16__() {
	return compileExpression(destinationString, state);
}
auto __lambda17__(auto value) {
	return new_Tuple<char*, ParseState>(value.generate(), state);
}
auto __lambda18__() {
	return new_Tuple<char*, ParseState>(wrap(input), state);
}
Tuple<char*, ParseState> compileMethodStatementValue_Main(char* input, ParseState state){
	if (input.startsWith("return ")) {
		/*final String substring = input*/.substring("return ".length());
		/*final Tuple<String, ParseState> result = compileExpression*/(substring, state);
		return new_Tuple<char*, ParseState>("return " + result.left, result.right);
	}
	/*final Optional<Tuple<String, ParseState>> invokableResult = compileInvokable*/(state, input);
	if (invokableResult.isPresent()) return invokableResult.get();
	/*final int i = input*/.indexOf("=");
	if (i >= 0) {
		/*final String destinationString = input*/.substring(0, i);
		/*final String source = input*/.substring(i + 1);
		/*final Tuple<String, ParseState> destinationResult =
					compileDefinition*/(destinationString).map(generate_Definition).map(__lambda15__).orElseGet(__lambda16__);
		/*final Tuple<String, ParseState> sourceResult = compileExpression*/(source, destinationResult.right);
		return new_Tuple<char*, ParseState>(destinationResult.left + " = " + sourceResult.left, sourceResult.right);
	}
	return compileDefinition(input).map(__lambda17__).orElseGet(__lambda18__);
}
auto __lambda19__() {
	return new_Tuple<char*, ParseState>(wrap(input), state);
}
Tuple<char*, ParseState> compileExpression_Main(char* input, ParseState state){
	return tryCompileExpression(input, state).orElseGet(__lambda19__);
}
auto __lambda20__() {
	return compileOperator(stripped, "-", state);
}
auto __lambda21__() {
	return compileOperator(stripped, ">=", state);
}
auto __lambda22__() {
	return compileOperator(stripped, "<", state);
}
auto __lambda23__() {
	return compileOperator(stripped, "!=", state);
}
auto __lambda24__() {
	return compileOperator(stripped, "==", state);
}
auto __lambda25__() {
	return compileOperator(stripped, "&&", state);
}
auto __lambda26__() {
	return compileOperator(stripped, "||", state);
}
auto __lambda27__() {
	return compileIdentifier(stripped, state);
}
auto __lambda28__() {
	return compileNumber(stripped, state);
}
Optional<Tuple<char*, ParseState>> tryCompileExpression_Main(char* input, ParseState state){
	/*final String stripped = input*/.strip();
	if (stripped.startsWith("'") && stripped.endsWith("'") && stripped.length() <  == 4) return Optional.of(new_Tuple<char*, ParseState>(stripped, state));
	if (isString(stripped)) return Optional.of(new_Tuple<char*, ParseState>(stripped, state));
	if (stripped.startsWith("!")) {
		/*final String slice = stripped*/.substring(1);
		/*final Optional<Tuple<String, ParseState>> maybeResult = tryCompileExpression*/(slice, state);
		if (maybeResult.isPresent()) {
			/*final Tuple<String, ParseState> result = maybeResult*/.get();
			return Optional.of(new_Tuple<char*, ParseState>("!" + result.left, result.right));
		}
	}
	/*final Optional<Tuple<String, ParseState>> lambdaResult = compileLambda*/(state, stripped);
	if (lambdaResult.isPresent()) return lambdaResult;
	/*final Optional<Tuple<String, ParseState>> left = compileInvokable*/(state, stripped);
	if (left.isPresent()) return left;
	/*final int separator = stripped*/.lastIndexOf("::");
	if (separator >= 0) {
		/*final String substring = stripped*/.substring(0, separator);
		/*final String name = stripped*/.substring(separator + 2).strip();
		if (isIdentifier(name)) {
			/*final Optional<String> maybeResult = compileType*/(substring);
			if (maybeResult.isPresent()) {
				/*final String result = maybeResult*/.get();
				return Optional.of(new_Tuple<char*, ParseState>(name + "_" + result, state));
			}
		}
	}
	/*final int i = stripped*/.lastIndexOf(".");
	if (i >= 0) {
		/*final String substring = stripped*/.substring(0, i);
		/*final String name = stripped*/.substring(i + 1).strip();
		if (isIdentifier(name)) {
			/*final Tuple<String, ParseState> result = compileExpression*/(substring, state);
			return Optional.of(new_Tuple<char*, ParseState>(result.left + "." + name, result.right));
		}
	}
	return compileOperator(stripped, "+", state).or(__lambda20__).or(__lambda21__).or(__lambda22__).or(__lambda23__).or(__lambda24__).or(__lambda25__).or(__lambda26__).or(__lambda27__).or(__lambda28__);
}
auto __lambda29__(auto tuple, auto s) {
	return mergeExpression(tuple.left, tuple.right, s);
}
auto __lambda30__(auto _, auto next) {
	return next;
}
Optional<Tuple<char*, ParseState>> compileInvokable_Main(ParseState state, char* stripped){
	if (!stripped.endsWith(")")) return Optional.empty();
	/*final String slice = stripped*/.substring(0, stripped.length() - 1);
	/*final List<String> segments = findArgStart*/(slice).toList();
	if (segments.size() < 2) return Optional.empty();
	/*final String callerWithExt = String*/.join("", segments.subList(0, segments.size() - 1));
	if (!callerWithExt.endsWith("(")) return Optional.empty();
	/*final String caller = callerWithExt*/.substring(0, callerWithExt.length() - 1);
	/*final String arguments = segments*/.getLast();
	/*final Tuple<String, ParseState> callerResult = compileCaller*/(state, caller);
	/*final Tuple<StringJoiner, ParseState> reduce = divide*/(arguments, foldValue_Main).toList().stream().reduce(new_Tuple<StringJoiner, ParseState>(new_StringJoiner(", "), callerResult.right), __lambda29__, __lambda30__);
	/*final String collect = reduce*/.left.toString();
	return Optional.of(new_Tuple<char*, ParseState>(callerResult.left + "(" + collect + ")", reduce.right));
}
Tuple<StringJoiner, ParseState> mergeExpression_Main(StringJoiner joiner, ParseState state, char* segment){
	/*Tuple<String, ParseState> result = compileExpression*/(segment, state);
	/*final StringJoiner add = joiner*/.add(result.left);
	return new_Tuple<StringJoiner, ParseState>(add, result.right);
}
auto __lambda31__(auto state, auto c) {
	/*final DivideState appended = state*/.append(c);
	if (c == '(') {
		/*final DivideState entered = appended*/.enter();
		if (entered.isShallow()) return entered.advance();
		else return entered;
	}
	if (c == ')') return appended.exit();
	return appended;
}
Stream<char*> findArgStart_Main(char* input){
	return divide(input, __lambda31__);
}
auto __lambda32__(auto slice) {
	return !slice.isEmpty();
}
auto __lambda33__(auto slice) {
	return "auto " + slice;
}
Optional<Tuple<char*, ParseState>> compileLambda_Main(ParseState state, char* stripped){
	/*final int i1 = stripped*/.indexOf("->");
	if (i1 < 0) return Optional.empty();
	/*final String beforeArrow = stripped*/.substring(0, i1).strip();
	char* outputParams;
	if (isIdentifier(beforeArrow)) outputParams = "auto " + beforeArrow;
	else if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
		/*final String withoutParentheses = beforeArrow*/.substring(1, beforeArrow.length() - 1);
		outputParams == Arrays.stream(withoutParentheses.split(Pattern.quote(","))).map(strip_char*).filter(__lambda32__).map(__lambda33__).collect(Collectors.joining(", "));
	}
	else return Optional.empty();
	/*final String body = stripped*/.substring(i1 + 2).strip();
	/*final Tuple<String, ParseState> bodyResult = compileLambdaBody*/(state, body);
	/*final String generatedName = bodyResult*/.right.generateAnonymousFunctionName();
	/*final String s1 = "auto " + generatedName + "(" + outputParams + ") " + bodyResult.left + System*/.lineSeparator();
	return Optional.of(new_Tuple<char*, ParseState>(generatedName, bodyResult.right.addFunction(s1)));
}
Tuple<char*, ParseState> compileLambdaBody_Main(ParseState state, char* body){
	/*final Optional<Tuple<String, ParseState>> maybeBlock = compileBlock*/(state, body, 0);
	if (maybeBlock.isPresent()) return maybeBlock.get();
	/*final Tuple<String, ParseState> result = compileExpression*/(body, state);
	/*final String s = generateStatement*/("return " + result.left);
	char* s2 = "{" + s + generateIndent(0) + "}";
	return new_Tuple<char*, ParseState>(s2, result.right);
}
Tuple<char*, ParseState> compileCaller_Main(ParseState state, char* caller){
	if (caller.startsWith("new ")) {
		/*final Optional<String> newType = compileType*/(caller.substring("new ".length()));
		if (newType.isPresent()) return new_Tuple<char*, ParseState>("new_" + newType.get(), state);
	}
	return compileExpression(caller, state);
}
Optional<Tuple<char*, ParseState>> compileIdentifier_Main(char* stripped, ParseState state){
	if (isIdentifier(stripped)) return Optional.of(new_Tuple<char*, ParseState>(stripped, state));
	return Optional.empty();
}
Optional<Tuple<char*, ParseState>> compileNumber_Main(char* stripped, ParseState state){
	if (isNumber(stripped)) return Optional.of(new_Tuple<char*, ParseState>(stripped, state));
	return Optional.empty();
}
auto __lambda34__(auto state1, auto next) {
	return foldOperator(operator, state1, next);
}
Optional<Tuple<char*, ParseState>> compileOperator_Main(char* input, char* operator, ParseState state){
	/*final List<String> segments = divide*/(input, __lambda34__).toList();
	if (segments.size() < 2) return Optional.empty();
	/*final String left = segments*/.getFirst();
	/*final String right = String*/.join(operator, segments.subList(1, segments.size()));
	/*final Optional<Tuple<String, ParseState>> maybeLeftResult = tryCompileExpression*/(left, state);
	if (maybeLeftResult.isEmpty()) return Optional.empty();
	/*final Tuple<String, ParseState> leftResult = maybeLeftResult*/.get();
	/*final Optional<Tuple<String, ParseState>> maybeRightResult = tryCompileExpression*/(right, leftResult.right);
	if (maybeRightResult.isEmpty()) return Optional.empty();
	/*final Tuple<String, ParseState> rightResult = maybeRightResult*/.get();
	char* generated = leftResult.left + " " + operator + " " + rightResult.left;
	return Optional.of(new_Tuple<char*, ParseState>(generated, rightResult.right));
}
auto __lambda35__(auto inner) {
	return inner.left;
}
DivideState foldOperator_Main(char* operator, DivideState state1, Character next){
	if (next != operator.charAt(0)) return state1.append(next);
	/*final Optional<Character> peeked = state1*/.peek();
	if (operator.length() >= 2 && peeked.isPresent() && peeked.get() == operator.charAt(1)) return state1.pop().map(__lambda35__).orElse(state1).advance();
	return state1.advance();
}
boolean isString_Main(char* stripped){
	if (stripped.length() < 2) return false;
	/*final boolean hasDoubleQuotes = stripped*/.startsWith("\"") && stripped.endsWith("\"");
	if (!hasDoubleQuotes) return false;
	/*final String content = stripped*/.substring(1, stripped.length() - 1);
	return areAllDoubleQuotesEscaped(content);
}
auto __lambda36__(auto i) {
	/*final char c = input*/.charAt(i);
	if (c != '\"') return true;
	if (i == 0) return false;
	/*char previous = input*/.charAt(i - 1);
	return previous == '\\';
}
boolean areAllDoubleQuotesEscaped_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda36__);
}
auto __lambda37__(auto i) {
	return Character.isDigit(input.charAt(i));
}
boolean isNumber_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda37__);
}
auto __lambda38__(auto i) {
	/*final char next = input*/.charAt(i);
	/*final boolean isValidDigit = i != 0 && Character*/.isDigit(next);
	return Character.isLetter(next) || isValidDigit;
}
boolean isIdentifier_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda38__);
}
Optional<JMethodHeader> compileConstructor_Main(char* beforeParams){
	/*final int separator = beforeParams*/.lastIndexOf(" ");
	if (separator < 0) return Optional.empty();
	/*final String name = beforeParams*/.substring(separator + " ".length());
	return Optional.of(new_JConstructor(name));
}
Optional<Tuple<char*, ParseState>> compileField_Main(char* input, ParseState state){
	if (input.endsWith(";")) {
		/*final String substring = input*/.substring(0, input.length() - ";".length()).strip();
		/*final Optional<String> s = generateField*/(substring);
		if (s.isPresent()) return Optional.of(new_Tuple<char*, ParseState>(s.get(), state));
	}
	return Optional.empty();
}
auto __lambda39__(auto type) {
	return new_Definition(type, name);
}
auto __lambda40__(auto type) {
	return new_Definition(type, name);
}
Optional<Definition> compileDefinition_Main(char* input){
	/*final String stripped = input*/.strip();
	/*final int index = stripped*/.lastIndexOf(" ");
	if (index < 0) return Optional.empty();
	/*final String beforeName = stripped*/.substring(0, index).strip();
	/*final String name = stripped*/.substring(index + " ".length()).strip();
	if (!isIdentifier(name)) return Optional.empty();
	/*final List<String> typeSeparator = findTypeSeparator*/(beforeName).toList();
	if (typeSeparator.isEmpty()) return compileType(beforeName).map(__lambda39__);
	/*final String typeString = typeSeparator*/.getLast();
	return compileType(typeString).map(__lambda40__);
}
auto __lambda41__(auto state, auto c) {
	if (c == ' ' && state.isLevel()) return state.advance();
	/*final DivideState appended = state*/.append(c);
	if (c == '<') return appended.enter();
	if (c == '>') return appended.exit();
	return appended;
}
Stream<char*> findTypeSeparator_Main(char* beforeName){
	return divide(beforeName, __lambda41__);
}
auto __lambda42__() {
	return wrap(slice);
}
auto __lambda43__(auto slice) {
	return compileType(slice).orElseGet(__lambda42__);
}
Optional<char*> compileType_Main(char* input){
	/*final String stripped = input*/.strip();
	if (stripped.equals("public")) return Optional.empty();
	if (stripped.endsWith(">")) {
		/*final String withoutEnd = stripped*/.substring(0, stripped.length() - 1);
		/*final int argumentStart = withoutEnd*/.indexOf("<");
		if (argumentStart >= 0) {
			/*final String base = withoutEnd*/.substring(0, argumentStart);
			/*final String argumentsString = withoutEnd*/.substring(argumentStart + "<".length());
			/*final String arguments =
						compileValues*/(argumentsString, __lambda43__);
			return Optional.of(base + "<" + arguments + ">");
		}
	}
	if (stripped.equals("String")) return Optional.of("char*");
	if (stripped.equals("int")) return Optional.of("int");
	if (isIdentifier(stripped)) return Optional.of(stripped);
	return Optional.of(wrap(stripped));
}
char* wrap_Main(char* input){
	/*final String replaced = input*/.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
/**/int main(){
	main_Main();
	return 0;
}