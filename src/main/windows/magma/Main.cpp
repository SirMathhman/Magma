// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
struct Definable extends JMethodHeader permits Definition, Placeholder {
};
struct JMethodHeader permits JConstructor, Definable {
};
struct ParseState {
	/*private final*/ List<char*> functions;
	/*private final*/ List<char*> structs;
	/*private int counter = -1;*/
};
struct DivideState {
	/*private final*/ ArrayList<char*> segments;
	/*private final*/ char* input;
	/*private*/ /*StringBuilder*/ buffer;
	/*private*/ int depth;
	/*private*/ int index;
};
template <typeparam A, typeparam B>
struct Tuple {
	/*A*/ left;, 
	/*B*/ right;
};
struct Definition(Optional<String> maybeBeforeType, String type, String name) implements Definable {
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
	this.functions = /*new ArrayList<String>*/();
	this.structs = /*new ArrayList<String>*/();
	return this;
}
/*public*/ /*ParseState*/ addFunction_ParseState(char* func){
	/*this.functions.add(func)*/;
	return this;
}
/*public*/ /*ParseState*/ addStruct_ParseState(char* struct){
	/*this.structs.add(struct)*/;
	return this;
}
/*public*/ char* generateAnonymousFunctionName_ParseState(){
	/*this.counter++*/;
	return "__lambda" + this.counter + "__";
}
/*public*/ List<char*> functions_ParseState(){
	return this.functions;
}
/*public*/ List<char*> structs_ParseState(){
	return this.structs;
}
/*@Override
		public*/ /*boolean*/ equals_ParseState(/*Object*/ obj){
	if (/*obj == this*/) return true;
	if (/*obj == null || obj.getClass() != this*/.getClass()) return false;
	/*ParseState*/ that = /* (ParseState) obj*/;
	return /*Objects.equals(this.functions, that.functions) && Objects*/.equals(this.structs, that.structs);
}
/*@Override
		public*/ int hashCode_ParseState(){
	return Objects.hash(this.functions, this.structs);
}
/*@Override
		public*/ char* toString_ParseState(){
	return /*"ParseState[" + "functions=" + this.functions + ", " + "structs=" + this.structs + ']'*/;
}
DivideState new_DivideState(char* input){
	DivideState this;
	this.input = input;
	this.buffer = /*new StringBuilder*/();
	this.depth = 0;
	this.segments = /*new ArrayList<String>*/();
	this.index = 0;
	return this;
}
/*private*/ Stream<char*> stream_DivideState(){
	return this.segments.stream();
}
/*private*/ /*DivideState*/ enter_DivideState(){
	this.depth = this.depth + 1;
	return this;
}
/*private*/ /*DivideState*/ exit_DivideState(){
	this.depth = this.depth - 1;
	return this;
}
/*private*/ /*boolean*/ isShallow_DivideState(){
	return /*this.depth == 1*/;
}
/*private*/ /*boolean*/ isLevel_DivideState(){
	return /*this.depth == 0*/;
}
/*private*/ /*DivideState*/ append_DivideState(/*char*/ c){
	/*this.buffer.append(c)*/;
	return this;
}
/*private*/ /*DivideState*/ advance_DivideState(){
	/*this.segments.add(this.buffer.toString())*/;
	this.buffer = /*new StringBuilder*/();
	return this;
}
/*public Optional<Tuple<DivideState,*/ /*Character>>*/ pop_DivideState(){
	if (this.index >= this.input.length()) return Optional.empty();
	/*final*/ /*char*/ next = this.input.charAt(this.index);
	/*this.index++*/;
	return Optional.of(/*new Tuple<DivideState*/, /*Character>*/(this, next));
}
auto __lambda0__(auto tuple) {
	return /* new Tuple<DivideState*/;
}
/*public Optional<Tuple<DivideState,*/ /*Character>>*/ popAndAppendToTuple_DivideState(){
	return this.pop().map(__lambda0__, /*Character>*/(tuple.left.append(tuple.right), tuple.right));
}
auto __lambda1__(auto tuple) {
	return tuple.left;
}
/*public*/ Optional</*DivideState*/> popAndAppendToOption_DivideState(){
	return this.popAndAppendToTuple().map(__lambda1__);
}
Definition new_Definition(char* type, char* name){
	Definition(Optional<String> maybeBeforeType, String type, String name) implements Definable this;
	/*this(Optional.empty(), type, name)*/;
	return this;
}
/*@Override
		public*/ char* generate_Definition(Optional<String> maybeBeforeType, String type, String name) implements Definable(){
	return /*this.maybeBeforeType.map(Main::wrap).map(value -> value + " ").orElse("") + this.type() + " " +
						 this*/.name();
}
/*@Override
		public*/ char* generate_Placeholder(String input) implements Definable(){
	return wrap(this.input);
}
/*public static*/ /*void*/ main_Main(/*String[]*/ args){
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
/*private static*/ char* compile_Main(char* input){
	/*StringJoiner*/ joiner = /*new StringJoiner*/("");
	/*ParseState*/ state = /*new ParseState*/();
	/*for (String input1 : divide(input, Main::foldStatement).toList()) {
			Tuple<String, ParseState> s = compileRootSegment(input1, state);
			joiner.add(s.left);
			state = s.right;
		}*/
	/*final*/ char* joined = joiner.toString();
	/*final*/ char* joinedStructs = String.join("", state.structs);
	/*final*/ char* joinedFunctions = String.join("", state.functions);
	return joinedStructs + joinedFunctions + joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}";
}
/*private static*/ char* compileAll_Main(char* input, , , /*DivideState>*/ folder, , /*String>*/ mapper){
	return divide(input, folder).map(mapper).collect(Collectors.joining(", "));
}
/*private static*/ Stream<char*> divide_Main(char* input, , , /*DivideState>*/ folder){
	/*DivideState*/ current = /*new DivideState*/(input);
	/*while (true) {
			final Optional<Tuple<DivideState, Character>> maybeNext = current.pop();
			if (maybeNext.isEmpty()) break;
			final Tuple<DivideState, Character> tuple = maybeNext.get();
			current = foldEscaped(tuple.left, tuple.right, folder);
		}*/
	return current.advance().stream();
}
/*private static*/ /*DivideState*/ foldEscaped_Main(/*DivideState*/ state, /*char*/ next, , , /*DivideState>*/ folder){
	return foldSingleQuotes(state, next).or(/*() -> foldDoubleQuotes*/(state, next)).orElseGet(/*() -> folder*/.apply(state, next));
}
/*private static*/ Optional</*DivideState*/> foldSingleQuotes_Main(/*DivideState*/ state, /*char*/ next){
	if (/*next != '\''*/) return Optional.empty();
	/*final*/ /*DivideState*/ appended = state.append(next);
	return appended.popAndAppendToTuple().flatMap(/*Main::foldEscaped*/).flatMap(/*DivideState::popAndAppendToOption*/);
}
/*private static*/ Optional</*DivideState*/> foldEscaped_Main(, /*Character>*/ tuple){
	if (/*tuple.right == '\\'*/) return tuple.left.popAndAppendToOption();
	/*else return Optional.of(tuple.left)*/;
}
/*private static*/ Optional</*DivideState*/> foldDoubleQuotes_Main(/*DivideState*/ state, /*char*/ next){
	if (/*next != '\"'*/) return Optional.empty();
	/*DivideState*/ appended = state.append(next);
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
/*private static*/ /*DivideState*/ foldStatement_Main(/*DivideState*/ state, /*char*/ c){
	/*final*/ /*DivideState*/ appended = state.append(c);
	if (/*c == ';' && appended*/.isLevel()) return appended.advance();
	if (/*c == '}' && appended*/.isShallow()) return appended.advance().exit();
	if (/*c == '{'*/) return appended.enter();
	if (/*c == '}'*/) return appended.exit();
	return appended;
}
/*private static Tuple<String,*/ /*ParseState>*/ compileRootSegment_Main(char* input, /*ParseState*/ state){
	/*final*/ char* stripped = input.strip();
	if (/*stripped.startsWith("package ") || stripped*/.startsWith("import ")) return /*new Tuple<String, ParseState>*/("", state);
	return compileStructure(stripped, "class", state).orElseGet(/*() -> new Tuple<String*/, /*ParseState>*/(wrap(stripped), state));
}
/*private static Optional<Tuple<String,*/ /*ParseState>>*/ compileStructure_Main(char* input, char* type, /*ParseState*/ state){
	/*final*/ int i = input.indexOf(type + " ");
	if (i < 0) return Optional.empty();
	/*final*/ char* afterKeyword = input.substring(i + (type + " ").length());
	/*final*/ int contentStart = afterKeyword.indexOf("{");
	if (contentStart < 0) return Optional.empty();
	/*final*/ char* beforeContent = afterKeyword.substring(0, contentStart).strip();
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
		/*final*/ char* withoutEnd = beforeMaybeParams.substring(0, beforeMaybeParams.length() - 1);
		/*final int i1 */ = withoutEnd.indexOf("<");
		if (/*i1 >= 0*/) {
			name = withoutEnd.substring(0, /* i1*/);
			/*final*/ char* arguments = withoutEnd.substring(/*i1 + "<"*/.length());
			typeArguments = divide(arguments, /* Main::foldValue*/).map(/*String::strip*/).toList();
		}
	}
	/*final*/ char* afterContent = afterKeyword.substring(contentStart + "{".length()).strip();
	if (/*!afterContent*/.endsWith("}")) return Optional.empty();
	/*final*/ char* content = afterContent.substring(0, afterContent.length() - "}".length());
	/*final*/ List<char*> segments = divide(content, /* Main::foldStatement*/).toList();
	/*StringBuilder*/ inner = /*new StringBuilder*/();
	/*ParseState*/ outer = state;
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
	/*final*/ char* generated = beforeStruct + "struct " + name + " {" + recordFields + inner + System.lineSeparator() + "};" + System.lineSeparator();
	return Optional.of(/*new Tuple<String*/, /*ParseState>*/("", outer.addStruct(generated)));
}
/*private static*/ char* compileValues_Main(char* input, , /*String>*/ mapper){
	return compileAll(input, /* Main::foldValue*/, mapper);
}
/*private static*/ char* compileParameter_Main(){
	if (/*input1*/.isEmpty()) return "";
	return generateField(/*input1*/).orElseGet(/*() -> wrap*/(/*input1*/));
}
/*private static*/ Optional<char*> generateField_Main(char* input){
	return compileDefinition(input).map(/*Definable::generate*/).map(/*Main::generateStatement*/);
}
/*private static*/ char* generateStatement_Main(char* content){
	return generateSegment(content + ";", 1);
}
/*private static*/ char* generateSegment_Main(char* content, int depth){
	return generateIndent(depth) + content;
}
/*private static*/ char* generateIndent_Main(int depth){
	return System.lineSeparator() + "\t".repeat(depth);
}
/*private static*/ /*DivideState*/ foldValue_Main(/*DivideState*/ state, /*char*/ next){
	if (/*next == ',' && state*/.isLevel()) return state.advance();
	/*final*/ /*DivideState*/ appended = state.append(next);
	/*if (next */ = /*= '(') return appended*/.enter();
	if (/*next == '*/) /*') return appended.exit()*/;
	return appended;
}
/*private static Tuple<String,*/ /*ParseState>*/ compileClassSegment_Main(char* input, char* name, /*ParseState*/ state){
	/*final*/ char* stripped = input.strip();
	if (stripped.isEmpty()) return /*new Tuple<String, ParseState>*/("", state);
	return compileClassSegmentValue(stripped, name, state);
}
/*private static Tuple<String,*/ /*ParseState>*/ compileClassSegmentValue_Main(char* input, char* name, /*ParseState*/ state){
	if (input.isEmpty()) return /*new Tuple<String, ParseState>*/("", state);
	/*return compileStructure(input, "class", state).or(() -> compileStructure(input, "record", state))
																									.or(() -> compileStructure(input, "interface", state))
																									.or(() -> compileField(input, state))
																									.or(() -> compileMethod(input, name, state))
																									.orElseGet(() -> {
																										final String generated = generateSegment(wrap(input), 1);
																										return new Tuple<String, ParseState>(generated, state);
																									}*/
	/*)*/;
}
/*private static Optional<Tuple<String,*/ /*ParseState>>*/ compileMethod_Main(char* input, char* name, /*ParseState*/ state){
	/*final*/ int paramStart = input.indexOf("(");
	if (paramStart < 0) return Optional.empty();
	/*final*/ char* beforeParams = input.substring(0, paramStart).strip();
	/*final*/ char* withParams = input.substring(paramStart + 1);
	/*final*/ int paramEnd = withParams.indexOf(")");
	if (paramEnd < 0) return Optional.empty();
	/*final*/ /*JMethodHeader*/ methodHeader = compileMethodHeader(beforeParams);
	/*final*/ char* inputParams = withParams.substring(0, paramEnd);
	/*final*/ char* withBraces = withParams.substring(paramEnd + 1).strip();
	/*final*/ char* outputParams = compileParameters(inputParams);
	/*final*/ char* outputMethodHeader = transformMethodHeader(methodHeader, name).generate() + "(" + outputParams + ")";
	/*final String outputBodyWithBraces*/;
	/*ParseState*/ current = state;
	if (/*withBraces.startsWith("{") && withBraces*/.endsWith("}")) {
		/*final*/ char* inputBody = withBraces.substring(1, withBraces.length() - 1);
		/*StringJoiner*/ joiner = /*new StringJoiner*/("");
		/*for (String s : divide(inputBody, Main::foldStatement).toList()) {
				Tuple<String, ParseState> string = compileMethodSegment(s, 1, current);
				joiner.add(string.left);
				current = string.right;
			}*/
		/*final*/ char* compiledBody = joiner.toString();
		/*String outputBody*/;
		if (/*Objects.requireNonNull(methodHeader) instanceof JConstructor*/) outputBody = /*generateStatement(name + " this") + compiledBody + generateStatement*/("return this");
		/*else*/ outputBody = compiledBody;
		outputBodyWithBraces = "{" + outputBody + System.lineSeparator() + "}";
	}
	/*else if*/ /*(withBraces.equals(";"))*/ outputBodyWithBraces = ";";
	/*else return Optional.empty()*/;
	/*final*/ char* generated = outputMethodHeader + outputBodyWithBraces + System.lineSeparator();
	return Optional.of(/*new Tuple<String*/, /*ParseState>*/("", current.addFunction(generated)));
}
/*private static*/ /*Definable*/ transformMethodHeader_Main(/*JMethodHeader*/ methodHeader, char* name){
	/*return switch (methodHeader) {
			case JConstructor constructor -> new Definition(constructor.name, "new_" + constructor.name);
			case Definition definition ->
					new Definition(definition.maybeBeforeType, definition.type, definition.name + "_" + name);
			case Placeholder placeholder -> placeholder;
		}*/
	/**/;
}
auto __lambda2__(auto definable) {
	return definable;
}
/*private static*/ /*JMethodHeader*/ compileMethodHeader_Main(char* beforeParams){
	return /*compileDefinition(beforeParams).<JMethodHeader>map*/(__lambda2__).or(/*() -> compileConstructor*/(beforeParams)).orElseGet(/*() -> new Placeholder*/(beforeParams));
}
auto __lambda3__(auto slice) {
	return compileDefinition(slice).map(/*Definable::generate*/).orElse("");
}
/*private static*/ char* compileParameters_Main(char* input){
	if (input.isEmpty()) return "";
	return compileValues(input, __lambda3__);
}
/*private static Tuple<String,*/ /*ParseState>*/ compileMethodSegment_Main(char* input, int depth, /*ParseState*/ state){
	/*final*/ char* stripped = input.strip();
	if (stripped.isEmpty()) return /*new Tuple<String, ParseState>*/("", state);
	/*final Tuple<String,*/ /*ParseState>*/ tuple = compileMethodSegmentValue(stripped, depth, state);
	return /*new Tuple<String, ParseState>*/(generateSegment(tuple.left, depth), tuple.right);
}
/*private static Tuple<String,*/ /*ParseState>*/ compileMethodSegmentValue_Main(char* input, int depth, /*ParseState*/ state){
	/*final*/ char* stripped = input.strip();
	if (/*stripped.startsWith("{") && stripped*/.endsWith("}")) {
		/*final*/ char* substring = stripped.substring(1, stripped.length() - 1);
		/*StringJoiner*/ joiner = /*new StringJoiner*/("");
		/*ParseState*/ current = state;
		/*for (String s : divide(substring, Main::foldStatement).toList()) {
				Tuple<String, ParseState> string = compileMethodSegment(s, depth + 1, current);
				joiner.add(string.left);
				current = string.right;
			}*/
		/*final*/ char* compiled = joiner.toString();
		return /*new Tuple<String, ParseState>*/("{" + compiled + generateIndent(depth) + "}", current);
	}
	if (stripped.startsWith("if")) {
		/*final*/ char* withoutPrefix = stripped.substring(2);
		/*final*/ int conditionEnd = findConditionEnd(withoutPrefix);
		if (conditionEnd >= 0) {
			/*final String substring1 */ = withoutPrefix.substring(0, conditionEnd).strip();
			/*final*/ char* body = withoutPrefix.substring(conditionEnd + 1);
			/*if (substring1.startsWith("(")) {
					final String expression = substring1.substring(1);
					final Tuple<String, ParseState> condition = compileExpression(expression, state);
					final Tuple<String, ParseState> compiledBody = compileMethodSegmentValue(body, depth, condition.right);
					return new Tuple<String, ParseState>("if (" + condition.left + ") " + compiledBody.left, compiledBody.right);
				}*/
		}
	}
	if (stripped.endsWith(";")) {
		/*final*/ char* slice = stripped.substring(0, stripped.length() - 1);
		/*final Tuple<String,*/ /*ParseState>*/ result = compileMethodStatementValue(slice, state);
		return /*new Tuple<String, ParseState>*/(result.left + ";", result.right);
	}
	return /*new Tuple<String, ParseState>*/(wrap(stripped), state);
}
/*private static*/ int findConditionEnd_Main(char* withoutPrefix){
	int conditionEnd =  - 1;
	/*int depth0 */ = 0;
	/*for*/ /*(int*/ i = 0;
	/*i < withoutPrefix.length()*/;
	/*i++) {
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
auto __lambda4__(auto generated) {
	return /* new Tuple<String*/;
}
/*private static Tuple<String,*/ /*ParseState>*/ compileMethodStatementValue_Main(char* input, /*ParseState*/ state){
	if (input.startsWith("return ")) {
		/*final*/ char* substring = input.substring("return ".length());
		/*final Tuple<String,*/ /*ParseState>*/ result = compileExpression(substring, state);
		return /*new Tuple<String, ParseState>*/("return " + result.left, result.right);
	}
	/*final*/ int i = input.indexOf("=");
	if (i >= 0) {
		/*final*/ char* destinationString = input.substring(0, i);
		/*final*/ char* source = input.substring(i + 1);
		/*final Tuple<String,*/ /*ParseState>*/ destinationResult = compileDefinition(destinationString).map(/*Definition::generate*/).map(__lambda4__, /*ParseState>*/(generated, state)).orElseGet(/*() -> compileExpression*/(destinationString, state));
		/*final Tuple<String,*/ /*ParseState>*/ sourceResult = compileExpression(source, destinationResult.right);
		return /*new Tuple<String, ParseState>*/(destinationResult.left + " = " + sourceResult.left, sourceResult.right);
	}
	return /*new Tuple<String, ParseState>*/(wrap(input), state);
}
/*private static Tuple<String,*/ /*ParseState>*/ compileExpression_Main(char* input, /*ParseState*/ state){
	return tryCompileExpression(input, state).orElseGet(/*() -> new Tuple<String*/, /*ParseState>*/(wrap(input), state));
}
/*private static Optional<Tuple<String,*/ /*ParseState>>*/ tryCompileExpression_Main(char* input, /*ParseState*/ state){
	/*final*/ char* stripped = input.strip();
	if (isString(stripped)) return Optional.of(/*new Tuple<String*/, /*ParseState>*/(stripped, state));
	/*final int i1 */ = stripped.indexOf("->");
	if (/*i1 >= 0*/) {
		/*final*/ char* name = stripped.substring(0, /* i1*/).strip();
		if (isIdentifier(name)) {
			/*final String substring1 */ = stripped.substring(/*i1 + 2*/);
			/*final Tuple<String,*/ /*ParseState>*/ result = compileExpression(/*substring1*/, state);
			/*final*/ char* s = generateStatement("return " + result.left);
			/*final*/ /*ParseState*/ right = result.right;
			/*final*/ char* generatedName = right.generateAnonymousFunctionName();
			/*final String s1 */ = "auto " + generatedName + "(auto " + name + ") {" + s + generateIndent(0) + "}" + System.lineSeparator();
			return Optional.of(/*new Tuple<String*/, /*ParseState>*/(generatedName, right.addFunction(/*s1*/)));
		}
	}
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
				final String caller = slice.substring(0, index);
				final String arguments = slice.substring(index + 1);

				final Tuple<String, ParseState> callerResult = compileExpression(caller, state);

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
	/*final*/ int i = stripped.lastIndexOf(".");
	if (i >= 0) {
		/*final*/ char* substring = stripped.substring(0, i);
		/*final*/ char* name = stripped.substring(i + 1).strip();
		if (isIdentifier(name)) {
			/*final Tuple<String,*/ /*ParseState>*/ result = compileExpression(substring, state);
			return Optional.of(/*new Tuple<String*/, /*ParseState>*/(result.left + "." + name, result.right));
		}
	}
	return compileOperator(stripped, "+", state).or(/*() -> compileOperator*/(stripped, "-", state)).or(/*() -> compileOperator*/(stripped, ">=", state)).or(/*() -> compileOperator*/(stripped, "<", state)).or(/*() -> compileIdentifier*/(stripped, state)).or(/*() -> compileNumber*/(stripped, state));
}
/*private static Optional<Tuple<String,*/ /*ParseState>>*/ compileIdentifier_Main(char* stripped, /*ParseState*/ state){
	if (isIdentifier(stripped)) return Optional.of(/*new Tuple<String*/, /*ParseState>*/(stripped, state));
	return Optional.empty();
}
/*private static Optional<Tuple<String,*/ /*ParseState>>*/ compileNumber_Main(char* stripped, /*ParseState*/ state){
	if (isNumber(stripped)) return Optional.of(/*new Tuple<String*/, /*ParseState>*/(stripped, state));
	return Optional.empty();
}
/*private static Optional<Tuple<String,*/ /*ParseState>>*/ compileOperator_Main(char* input, char* operator, /*ParseState*/ state){
	/*final*/ int index = input.indexOf(operator);
	if (index < 0) return Optional.empty();
	/*final*/ char* left = input.substring(0, index);
	/*final*/ char* right = input.substring(index + operator.length());
	/*final Optional<Tuple<String,*/ /*ParseState>>*/ maybeLeftResult = tryCompileExpression(left, state);
	if (maybeLeftResult.isEmpty()) return Optional.empty();
	/*final Tuple<String,*/ /*ParseState>*/ leftResult = maybeLeftResult.get();
	/*final Optional<Tuple<String,*/ /*ParseState>>*/ maybeRightResult = tryCompileExpression(right, leftResult.right);
	if (maybeRightResult.isEmpty()) return Optional.empty();
	/*final Tuple<String,*/ /*ParseState>*/ rightResult = maybeRightResult.get();
	/*final*/ char* generated = leftResult.left + " " + operator + " " + rightResult.left;
	return Optional.of(/*new Tuple<String*/, /*ParseState>*/(generated, rightResult.right));
}
/*private static*/ /*boolean*/ isString_Main(char* stripped){
	if (stripped.length() < 2) return false;
	/*final*/ /*boolean*/ hasDoubleQuotes = /*stripped.startsWith("\"") && stripped*/.endsWith(/*"\""*/);
	if (/*!hasDoubleQuotes*/) return false;
	return /*!stripped*/.substring(1, stripped.length() - 1).contains(/*"\""*/);
}
/*private static*/ /*boolean*/ isNumber_Main(char* input){
	/*for*/ /*(int*/ i = 0;
	/*i < input.length()*/;
	/*i++) {
			final char c = input.charAt(i);
			if (!Character.isDigit(c)) return false;
		}*/
	return true;
}
/*private static*/ /*boolean*/ isIdentifier_Main(char* input){
	/*for*/ /*(int*/ i = 0;
	/*i < input.length()*/;
	/*i++) if (!Character.isLetter(input.charAt(i))) return false*/;
	return true;
}
/*private static*/ Optional</*JMethodHeader*/> compileConstructor_Main(char* beforeParams){
	/*final*/ int separator = beforeParams.lastIndexOf(" ");
	if (separator < 0) return Optional.empty();
	/*final*/ char* name = beforeParams.substring(separator + " ".length());
	return Optional.of(/*new JConstructor*/(name));
}
/*private static Optional<Tuple<String,*/ /*ParseState>>*/ compileField_Main(char* input, /*ParseState*/ state){
	if (input.endsWith(";")) {
		/*final*/ char* substring = input.substring(0, input.length() - ";".length()).strip();
		/*final*/ Optional<char*> s = generateField(substring);
		if (s.isPresent()) return Optional.of(/*new Tuple<String*/, /*ParseState>*/(s.get(), state));
	}
	return Optional.empty();
}
auto __lambda5__(auto type) {
	return /*new Definition*/(type, name);
}
auto __lambda6__(auto type) {
	return /*new Definition*/(Optional.of(beforeType), type, name);
}
/*private static*/ Optional</*Definition*/> compileDefinition_Main(char* input){
	/*final*/ char* stripped = input.strip();
	/*final*/ int index = stripped.lastIndexOf(" ");
	if (index < 0) return Optional.empty();
	/*final*/ char* beforeName = stripped.substring(0, index).strip();
	/*final*/ char* name = stripped.substring(index + " ".length()).strip();
	if (/*!isIdentifier*/(name)) return Optional.empty();
	/*final*/ int typeSeparator = beforeName.lastIndexOf(" ");
	if (typeSeparator < 0) return compileType(beforeName).map(__lambda5__);
	/*final*/ char* beforeType = beforeName.substring(0, typeSeparator);
	/*final*/ char* typeString = beforeName.substring(typeSeparator + " ".length());
	return compileType(typeString).map(__lambda6__);
}
/*private static*/ Optional<char*> compileType_Main(char* input){
	/*final*/ char* stripped = input.strip();
	if (stripped.equals("public")) return Optional.empty();
	if (stripped.endsWith(">")) {
		/*final*/ char* withoutEnd = stripped.substring(0, stripped.length() - 1);
		/*final*/ int argumentStart = withoutEnd.indexOf("<");
		if (argumentStart >= 0) {
			/*final*/ char* base = withoutEnd.substring(0, argumentStart);
			/*final*/ char* arguments = withoutEnd.substring(argumentStart + "<".length());
			return Optional.of(base + "<" + compileType(arguments).orElse("") + ">");
		}
	}
	if (stripped.equals("String")) return Optional.of("char*");
	if (stripped.equals("int")) return Optional.of("int");
	return Optional.of(wrap(stripped));
}
/*private static*/ char* wrap_Main(char* input){
	/*final*/ char* replaced = input.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
/**/int main(){
	main_Main();
	return 0;
}