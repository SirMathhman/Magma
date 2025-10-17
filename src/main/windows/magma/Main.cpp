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
	this.functions = new_ArrayList<char*>();
	this.structs = new_ArrayList<char*>();
	return this;
}
ParseState addFunction_ParseState(char* func){
	/*this.functions.add(func)*/;
	return this;
}
ParseState addStruct_ParseState(char* struct){
	/*this.structs.add(struct)*/;
	return this;
}
char* generateAnonymousFunctionName_ParseState(){
	/*this.counter++*/;
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
	/*this.buffer.append(c)*/;
	return this;
}
DivideState advance_DivideState(){
	/*this.segments.add(this.buffer.toString())*/;
	this.buffer = new_StringBuilder();
	return this;
}
Optional<Tuple<DivideState, Character>> pop_DivideState(){
	if (this.index >= this.input.length()) return Optional.empty();
	char next = this.input.charAt(this.index);
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
	/*else return Optional.empty()*/;
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
	StringJoiner joiner = new_StringJoiner("");
	ParseState state = new_ParseState();
	/*for (String input1 : divide(input, Main::foldStatement).toList()) {
			Tuple<String, ParseState> s = compileRootSegment(input1, state);
			joiner.add(s.left);
			state = s.right;
		}*/
	char* joined = joiner.toString();
	char* joinedStructs = String.join("", state.structs);
	char* joinedFunctions = String.join("", state.functions);
	return joinedStructs + joinedFunctions + joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}";
}
char* compileAll_Main(char* input, BiFunction<DivideState, Character, DivideState> folder, Function<char*, char*> mapper){
	return divide(input, folder).map(mapper).collect(Collectors.joining(", "));
}
Stream<char*> divide_Main(char* input, BiFunction<DivideState, Character, DivideState> folder){
	DivideState current = new_DivideState(input);
	/*while (true) {
			final Optional<Tuple<DivideState, Character>> maybeNext = current.pop();
			if (maybeNext.isEmpty()) break;
			final Tuple<DivideState, Character> tuple = maybeNext.get();
			current = foldEscaped(tuple.left, tuple.right, folder);
		}*/
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
	DivideState appended = state.append(next);
	return appended.popAndAppendToTuple().flatMap(/*Main::foldEscaped*/).flatMap(/*DivideState::popAndAppendToOption*/);
}
Optional<DivideState> foldEscaped_Main(Tuple<DivideState, Character> tuple){
	if (tuple.right == '\\') return tuple.left.popAndAppendToOption();
	/*else return Optional.of(tuple.left)*/;
}
Optional<DivideState> foldDoubleQuotes_Main(DivideState state, char next){
	if (next != '\"') return Optional.empty();
	DivideState appended = state.append(next);
	/*while (true) {
			final Optional<Tuple<DivideState, Character>> maybeNext = appended.popAndAppendToTuple();
			if (maybeNext.isPresent()) {
				final Tuple<DivideState, Character> tuple = maybeNext.get();
				appended = tuple.left;

				final char c = tuple.right;
				if (c == '\\') appended = appended.popAndAppendToOption().orElse(appended);
				if (c == '\"') break;
			} else break;
		}*/
	return Optional.of(appended);
}
DivideState foldStatement_Main(DivideState state, char c){
	DivideState appended = state.append(c);
	if (c == ';' && appended.isLevel()) return appended.advance();
	if (c == '}' && appended.isShallow()) return appended.advance().exit();
	/*if (c */ = /*= '{' || c == '(') return appended*/.enter();
	if (c == '}' || c == ''') /*') return appended.exit()*/;
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
Optional<Tuple<char*, ParseState>> compileStructure_Main(char* input, char* type, ParseState state){
	int i = input.indexOf(type + " ");
	if (i < 0) return Optional.empty();
	char* afterKeyword = input.substring(i + (type + " ").length());
	int contentStart = afterKeyword.indexOf("{");
	if (contentStart < 0) return Optional.empty();
	char* beforeContent = afterKeyword.substring(0, contentStart).strip();
	/*// if (!isIdentifier(beforeContent)) return Optional.empty()*/;
	char* beforeMaybeParams = beforeContent;
	char* recordFields = "";
	if (/*beforeContent.endsWith(")"*/) /*) {
			final String slice = beforeContent.substring(0, beforeContent.length() - 1);
			final int beforeParams = slice.indexOf("(");
			if (beforeParams >= 0) {
				beforeMaybeParams = slice.substring(0, beforeParams).strip();
				final String substring = slice.substring(beforeParams + 1);
				recordFields = compileValues(substring, Main::compileParameter);
			}
		}*/
	char* name = beforeMaybeParams;
	List<char*> typeArguments = Collections.emptyList();
	if (beforeMaybeParams.endsWith(">")) {
		char* withoutEnd = beforeMaybeParams.substring(0, beforeMaybeParams.length() - 1);
		int i1 = withoutEnd.indexOf("<");
		if (i1 >= 0) {
			name = withoutEnd.substring(0, i1);
			char* arguments = withoutEnd.substring(i1 + "<".length());
			typeArguments = divide(arguments, /* Main::foldValue*/).map(/*String::strip*/).toList();
		}
	}
	char* afterContent = afterKeyword.substring(contentStart + "{".length()).strip();
	if (!afterContent.endsWith("}")) return Optional.empty();
	char* content = afterContent.substring(0, afterContent.length() - "}".length());
	List<char*> segments = divide(content, /* Main::foldStatement*/).toList();
	StringBuilder inner = new_StringBuilder();
	ParseState outer = state;
	/*for (String segment : segments) {
			Tuple<String, ParseState> compiled = compileClassSegment(segment, name, outer);
			inner.append(compiled.left);
			outer = compiled.right;
		}*/
	/*String beforeStruct*/;
	if (typeArguments.isEmpty()) beforeStruct = "";
	/*else {
			final String templateValues =
					typeArguments.stream().map(slice -> "typeparam " + slice).collect(Collectors.joining(", ", "<", ">")) +
					System.lineSeparator();

			beforeStruct = "template " + templateValues;
		}*/
	char* generated = beforeStruct + "struct " + name + " {" + recordFields + inner + System.lineSeparator() + "};" + System.lineSeparator();
	return Optional.of(new_Tuple<char*, ParseState>("", outer.addStruct(generated)));
}
char* compileValues_Main(char* input, Function<char*, char*> mapper){
	return compileAll(input, /* Main::foldValue*/, mapper);
}
auto __lambda5__() {
	return wrap(input1);
}
char* compileParameter_Main(char* input1){
	if (input1.isEmpty()) return "";
	return generateField(input1).orElseGet(__lambda5__);
}
Optional<char*> generateField_Main(char* input){
	return compileDefinition(input).map(/*Definable::generate*/).map(/*Main::generateStatement*/);
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
	/*if (next */ = /*= '(' || next == '<') return appended*/.enter();
	if (next == '') /*' ||*/ next = /*= '>') return appended.exit()*/;
	return appended;
}
Tuple<char*, ParseState> compileClassSegment_Main(char* input, char* name, ParseState state){
	char* stripped = input.strip();
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
	char* generated = generateSegment(wrap(input), 1);
	return new_Tuple<char*, ParseState>(generated, state);
}
Tuple<char*, ParseState> compileClassSegmentValue_Main(char* input, char* name, ParseState state){
	if (input.isEmpty()) return new_Tuple<char*, ParseState>("", state);
	return compileStructure(input, "class", state).or(__lambda6__).or(__lambda7__).or(__lambda8__).or(__lambda9__).orElseGet(__lambda10__);
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
	/*final String outputBodyWithBraces*/;
	ParseState current = state;
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		char* inputBody = withBraces.substring(1, withBraces.length() - 1);
		StringJoiner joiner = new_StringJoiner("");
		/*for (String s : divide(inputBody, Main::foldStatement).toList()) {
				Tuple<String, ParseState> string = compileMethodSegment(s, 1, current);
				joiner.add(string.left);
				current = string.right;
			}*/
		char* compiledBody = joiner.toString();
		/*String outputBody*/;
		if (/*Objects.requireNonNull(methodHeader) instanceof JConstructor*/) outputBody = /*generateStatement(name + " this") + compiledBody + generateStatement*/("return this");
		else outputBody = compiledBody;
		outputBodyWithBraces = "{" + outputBody + System.lineSeparator() + "}";
	}
	/*(withBraces.equals(";"))*/ outputBodyWithBraces = ";";
	/*else return Optional.empty()*/;
	char* generated = outputMethodHeader + outputBodyWithBraces + System.lineSeparator();
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
	return compileDefinition(slice).map(/*Definable::generate*/).orElse("");
}
char* compileParameters_Main(char* input){
	if (input.isEmpty()) return "";
	return compileValues(input, __lambda14__);
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
	if (stripped.startsWith("if")) {
		char* withoutPrefix = stripped.substring(2);
		int conditionEnd = findConditionEnd(withoutPrefix);
		if (conditionEnd >= 0) {
			char* substring1 = withoutPrefix.substring(0, conditionEnd).strip();
			char* body = withoutPrefix.substring(conditionEnd + 1);
			/*if (substring1.startsWith("(")) {
					final String expression = substring1.substring(1);
					final Tuple<String, ParseState> condition = compileExpression(expression, state);
					final Tuple<String, ParseState> compiledBody = compileMethodSegmentValue(body, depth, condition.right);
					return new Tuple<String, ParseState>("if (" + condition.left + ") " + compiledBody.left, compiledBody.right);
				}*/
		}
	}
	if (stripped.endsWith(";")) {
		char* slice = stripped.substring(0, stripped.length() - 1);
		Tuple<char*, ParseState> result = compileMethodStatementValue(slice, state);
		return new_Tuple<char*, ParseState>(result.left + ";", result.right);
	}
	return new_Tuple<char*, ParseState>(wrap(stripped), state);
}
Optional<Tuple<char*, ParseState>> compileBlock_Main(ParseState state, char* input, int depth){
	if (!input.startsWith("{") ||  != input.endsWith("}")) return Optional.empty();
	char* substring = input.substring(1, input.length() - 1);
	StringJoiner joiner = new_StringJoiner("");
	ParseState current = state;
	/*for (String s : divide(substring, Main::foldStatement).toList()) {
			Tuple<String, ParseState> string = compileMethodSegment(s, depth + 1, current);
			joiner.add(string.left);
			current = string.right;
		}*/
	char* compiled = joiner.toString();
	return Optional.of(new_Tuple<char*, ParseState>("{" + compiled + generateIndent(depth) + "}", current));
}
int findConditionEnd_Main(char* withoutPrefix){
	int conditionEnd =  - 1;
	int depth0 = 0;
	/*for (int i = 0; i < withoutPrefix.length(); i++) {
			final char c = withoutPrefix.charAt(i);
			if (c == ')') {
				depth0--;
				if (depth0 == 0) {
					conditionEnd = i;
					break;
				}
			}
			if (c == '(') depth0++;
		}*/
	return conditionEnd;
}
auto __lambda15__(auto generated) {
	return new_Tuple<char*, ParseState>(generated, state);
}
auto __lambda16__() {
	return compileExpression(destinationString, state);
}
Tuple<char*, ParseState> compileMethodStatementValue_Main(char* input, ParseState state){
	if (input.startsWith("return ")) {
		char* substring = input.substring("return ".length());
		Tuple<char*, ParseState> result = compileExpression(substring, state);
		return new_Tuple<char*, ParseState>("return " + result.left, result.right);
	}
	int i = input.indexOf("=");
	if (i >= 0) {
		char* destinationString = input.substring(0, i);
		char* source = input.substring(i + 1);
		Tuple<char*, ParseState> destinationResult = compileDefinition(destinationString).map(/*Definition::generate*/).map(__lambda15__).orElseGet(__lambda16__);
		Tuple<char*, ParseState> sourceResult = compileExpression(source, destinationResult.right);
		return new_Tuple<char*, ParseState>(destinationResult.left + " = " + sourceResult.left, sourceResult.right);
	}
	return new_Tuple<char*, ParseState>(wrap(input), state);
}
auto __lambda17__() {
	return new_Tuple<char*, ParseState>(wrap(input), state);
}
Tuple<char*, ParseState> compileExpression_Main(char* input, ParseState state){
	return tryCompileExpression(input, state).orElseGet(__lambda17__);
}
auto __lambda18__() {
	return compileOperator(stripped, "-", state);
}
auto __lambda19__() {
	return compileOperator(stripped, ">=", state);
}
auto __lambda20__() {
	return compileOperator(stripped, "<", state);
}
auto __lambda21__() {
	return compileOperator(stripped, "!=", state);
}
auto __lambda22__() {
	return compileOperator(stripped, "==", state);
}
auto __lambda23__() {
	return compileOperator(stripped, "&&", state);
}
auto __lambda24__() {
	return compileOperator(stripped, "||", state);
}
auto __lambda25__() {
	return compileIdentifier(stripped, state);
}
auto __lambda26__() {
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
	if (/*stripped.endsWith(")"*/) /*) {
			final String slice = stripped.substring(0, stripped.length() - 1);

			int index = -1;
			int depth = 0;
			for (int i = 0; i < slice.length(); i++) {
				final char c = slice.charAt(i);
				if (c == '(') {
					depth++;
					if (depth == 1) index = i;
				}
				if (c == ')') depth--;
			}

			if (index >= 0) {
				final String caller = slice.substring(0, index).strip();
				final String arguments = slice.substring(index + 1);

				final Tuple<String, ParseState> callerResult = compileCaller(state, caller);

				StringJoiner joiner = new StringJoiner(", ");
				ParseState current = callerResult.right;
				for (String s : divide(arguments, Main::foldValue).toList()) {
					Tuple<String, ParseState> result = compileExpression(s, current);
					joiner.add(result.left);
					current = result.right;
				}

				final String collect = joiner.toString();
				return Optional.of(new Tuple<String, ParseState>(callerResult.left + "(" + collect + ")", current));
			}
		}*/
	int i = stripped.lastIndexOf(".");
	if (i >= 0) {
		char* substring = stripped.substring(0, i);
		char* name = stripped.substring(i + 1).strip();
		if (isIdentifier(name)) {
			Tuple<char*, ParseState> result = compileExpression(substring, state);
			return Optional.of(new_Tuple<char*, ParseState>(result.left + "." + name, result.right));
		}
	}
	return compileOperator(stripped, "+", state).or(__lambda18__).or(__lambda19__).or(__lambda20__).or(__lambda21__).or(__lambda22__).or(__lambda23__).or(__lambda24__).or(__lambda25__).or(__lambda26__);
}
Optional<Tuple<char*, ParseState>> compileLambda_Main(ParseState state, char* stripped){
	int i1 = stripped.indexOf("->");
	if (i1 < 0) return Optional.empty();
	char* beforeArrow = stripped.substring(0, i1).strip();
	/*final String outputParams*/;
	if (isIdentifier(beforeArrow)) outputParams = "auto " + beforeArrow;
	/*else if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
			final String withoutParentheses = beforeArrow.substring(1, beforeArrow.length() - 1);
			outputParams = Arrays.stream(withoutParentheses.split(Pattern.quote(",")))
													 .map(String::strip)
													 .filter(slice -> !slice.isEmpty())
													 .map(slice -> "auto " + slice)
													 .collect(Collectors.joining(", "));

		}*/
	/*else return Optional.empty()*/;
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
Tuple<char*, ParseState> compileCaller_Main(ParseState state, char* caller){
	if (caller.startsWith("new ")) {
		Optional<char*> newType = compileType(caller.substring("new ".length()));
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
auto __lambda27__(auto state1, auto next) {
	if (next == operator.charAt(0)) {
		Optional<Character> peeked = state1.peek();
		if (operator.length() >= 2 && peeked.isPresent() && peeked.get() == operator.charAt(1)) return state1.pop().map(/*Tuple::left*/).orElse(state1).advance();
		return state1.advance();
	}
	return state1.append(next);
}
Optional<Tuple<char*, ParseState>> compileOperator_Main(char* input, char* operator, ParseState state){
	List<char*> segments = divide(input, __lambda27__).toList();
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
boolean isString_Main(char* stripped){
	if (stripped.length() < 2) return false;
	boolean hasDoubleQuotes = stripped.startsWith("\"") && stripped.endsWith("\"");
	if (!hasDoubleQuotes) return false;
	char* content = stripped.substring(1, stripped.length() - 1);
	return areAllDoubleQuotesEscaped(content);
}
auto __lambda28__(auto i) {
	char c = input.charAt(i);
	if (c == '\"') {
		if (i == 0) return false;
		char previous = input.charAt(i - 1);
		return previous == '\\';
	}
	return true;
}
boolean areAllDoubleQuotesEscaped_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda28__);
}
auto __lambda29__(auto i) {
	return Character.isDigit(input.charAt(i));
}
boolean isNumber_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda29__);
}
auto __lambda30__(auto i) {
	char next = input.charAt(i);
	boolean isValidDigit = i != 0 && Character.isDigit(next);
	return Character.isLetter(next) || isValidDigit;
}
boolean isIdentifier_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda30__);
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
auto __lambda31__(auto type) {
	return new_Definition(type, name);
}
auto __lambda32__(auto type) {
	return new_Definition(type, name);
}
Optional<Definition> compileDefinition_Main(char* input){
	char* stripped = input.strip();
	int index = stripped.lastIndexOf(" ");
	if (index < 0) return Optional.empty();
	char* beforeName = stripped.substring(0, index).strip();
	char* name = stripped.substring(index + " ".length()).strip();
	if (!isIdentifier(name)) return Optional.empty();
	List<char*> typeSeparator = findTypeSeparator(beforeName).toList();
	if (typeSeparator.isEmpty()) return compileType(beforeName).map(__lambda31__);
	char* typeString = typeSeparator.getLast();
	return compileType(typeString).map(__lambda32__);
}
auto __lambda33__(auto state, auto c) {
	if (c == ' ' && state.isLevel()) return state.advance();
	DivideState appended = state.append(c);
	if (c == '<') return appended.enter();
	if (c == '>') return appended.exit();
	return appended;
}
Stream<char*> findTypeSeparator_Main(char* beforeName){
	return divide(beforeName, __lambda33__);
}
auto __lambda34__() {
	return wrap(slice);
}
auto __lambda35__(auto slice) {
	return compileType(slice).orElseGet(__lambda34__);
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
			char* arguments = compileValues(argumentsString, __lambda35__);
			return Optional.of(base + "<" + arguments + ">");
		}
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