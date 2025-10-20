// File generated from 'JavaPath[path=.\src\main\java\magma\Main.java]'. This is not source code!
#include "Main.h"
enum DefinableTag {
	Definition,
	Placeholder
};
union DefinableData {
	Definition definition;
	Placeholder placeholder;
};
struct Definable {
	DefinableTag tag;
	DefinableData data;
};
enum JMethodHeaderTag {
	JConstructor,
	Definable
};
union JMethodHeaderData {
	JConstructor jconstructor;
	Definable definable;
};
struct JMethodHeader {
	JMethodHeaderTag tag;
	JMethodHeaderData data;
};
enum CExpressionTag {
	CIdentifier,
	Content
};
union CExpressionData {
	CIdentifier cidentifier;
	Content content;
};
struct CExpression {
	CExpressionTag tag;
	CExpressionData data;
};
struct ParseState {
	Stack<ArrayList<char*>> beforeStatements;
	ArrayList<char*> afterStatements;
	ArrayList<char*> structs;
	ArrayList<char*> functions;
	int counter;
};
struct DivideState {
	char* input;
	ArrayList<char*> segments;
	StringBuilder buffer;
	int depth;
	int index;
};
template <typeparam A, typeparam B>
struct Tuple {
	A left;
	B right;
};
struct Definition {
	ArrayList<char*> annotations;
	char* type;
	char* name;
};
struct Placeholder {
	char* input;
};
struct JConstructor {
	char* name;
};
struct Content {
	char* value;
};
struct CIdentifier {
	char* value;
};
struct Main {
};
char* generate_Definable();
char* generate_CExpression();
ParseState new_ParseState(){
	ParseState this;
	this.functions = new_ArrayList<char*>();
	this.structs = new_ArrayList<char*>();
	this.beforeStatements = new_Stack<ArrayList<char*>>();
	this.beforeStatements.add(new_ArrayList<char*>());
	this.afterStatements = new_ArrayList<char*>();
	this.counter =  - 1;
	return this;
}
ParseState addFunction_ParseState(char* func){
	this.functions == this.functions.add(func);
	return this;
}
ParseState addStruct_ParseState(char* struct){
	this.structs == this.structs.add(struct);
	return this;
}
char* generateAnonymousFunctionName_ParseState(){
	this.counter++;
	return "__lambda" + this.counter + "__";
}
ParseState addAfterStatement_ParseState(char* statement){
	this.afterStatements == this.afterStatements.add(statement);
	return this;
}
ArrayList<char*> popAfterStatements_ParseState(){
	ArrayList<char*> copy = this.afterStatements.copy();
	this.afterStatements == this.afterStatements.clear();
	return copy;
}
void addBeforeStatement_ParseState(char* beforeStatement){
	ArrayList<char*> peek = this.beforeStatements.pop();
	ArrayList<char*> added = peek.add(beforeStatement);
	this.beforeStatements.push(added);
}
ArrayList<char*> popBeforeStatements_ParseState(){
	return this.beforeStatements.pop();
}
ParseState pushBeforeStatements_ParseState(){
	this.beforeStatements.push(new_ArrayList<char*>());
	return this;
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
	this.segments == this.segments.add(this.buffer.toString());
	this.buffer = new_StringBuilder();
	return this;
}
Option<Tuple<DivideState, Character>> pop_DivideState(){
	if (this.index >= this.input.length()) {
		return new_None<Tuple<DivideState, Character>>();
	}
	char next = this.input.charAt(this.index);
	this.index++;
	return new_Some<Tuple<DivideState, Character>>(new_Tuple<DivideState, Character>(this, next));
}
auto __lambda0__(auto tuple) {
	return new_Tuple<DivideState, Character>(tuple.left.append(tuple.right), tuple.right);
}
Option<Tuple<DivideState, Character>> popAndAppendToTuple_DivideState(){
	return this.pop().map(__lambda0__);
}
auto __lambda1__(auto tuple) {
	return tuple.left;
}
Option<DivideState> popAndAppendToOption_DivideState(){
	return this.popAndAppendToTuple().map(__lambda1__);
}
Option<Character> peek_DivideState(){
	if (this.index < this.input.length()) {
		return new_Some<Character>(this.input.charAt(this.index));
	}
	else {
		return new_None<Character>();
	}
}
char* generate_Definition(){
	return this.type + " " + this.name;
}
char* generate_Placeholder(){
	return wrap(this.input);
}
char* generate_Content(){
	return this.value;
}
char* generate_CIdentifier(){
	return this.value;
}
void main_Main(char** args){
	??? _temp = run();
	if (_temp.tag == Some) {
		Some<IOError> _cast = _temp.data.some;
		IOError value = _cast.value;
		System.out.println(value.display());
	}
}
Option<IOError> run_Main(){
	Path sourceDirectory = Paths.get(".", "src", "main", "java");
	Path targetDirectory = Paths.get(".", "src", "main", "windows");
	Result<ArrayList<Path>, IOError> walked = sourceDirectory.walk();
	return /*switch (walked) {
			case Err<ArrayList<Path>, IOError> v -> new Some<IOError>(v.error());
			case Ok<ArrayList<Path>, IOError> v -> runWithSources(v.value(), sourceDirectory, targetDirectory);
		}*/;
}
auto __lambda2__(auto path) {
	return path.asString().endsWith(".java");
}
auto __lambda3__(auto source) {
	return runWithSource(source, sourceDirectory, targetDirectory);
}
Option<IOError> runWithSources_Main(ArrayList<Path> sources, Path sourceDirectory, Path targetDirectory){
	return sources.stream().filter(__lambda2__).map(__lambda3__).flatMap(fromOption_Streams).next();
}
Option<IOError> runWithSource_Main(Path source, Path sourceDirectory, Path targetDirectory){
	Path relativeParent = sourceDirectory.relativize(source.getParent());
	Path targetParent = targetDirectory.resolveByPath(relativeParent);
	??? _temp = source.readString();
	if (_temp.tag == Ok) {
		if (!targetParent.exists()) {
			Option<IOError> result = targetParent.createDirectories();
			if (result.tag == Some) {
		Ok<String, IOError> _cast = _temp.data.ok;
		char* input = _cast.input;
		Some<IOError> _cast = result.data.some;
				return result;
			}
		}
		char* output = "// File generated from '" + source + "'. This is not source code!" + System.lineSeparator() + "#include \"Main.h\"" + System.lineSeparator() + compile(input);
		char* fileName = source.getFileName().asString();
		int separator = fileName.lastIndexOf(".");
		char* name = fileName.substring(0, separator);
		Path target = targetParent.resolveByString(name + ".cpp");
		return target.writeString(output);
	}
	return new_None<IOError>();
}
char* compile_Main(char* input){
	StringJoiner joiner = new_StringJoiner("");
	ParseState state = new_ParseState();
	ArrayList < String >= list == divide(input, foldStatement_Main).collect(new_ListCollector<char*>());
	int i = 0;
	while (i < list.size()) {
		char* input1 = list.get(i).orElse(null);
		Tuple<char*, ParseState> s = compileRootSegment(input1, state);
		joiner.add(s.left);
		state = s.right;
		i++;
	}
	char* joined = joiner.toString();
	char* joinedStructs = state.structs.stream().collect(new_Joiner(""));
	char* joinedFunctions = state.functions.stream().collect(new_Joiner(""));
	return joinedStructs + joinedFunctions + joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}";
}
Stream<char*> divide_Main(char* input, BiFunction<DivideState, Character, DivideState> folder){
	Tuple<DivideState, Boolean> current = new_Tuple<DivideState, Boolean>(new_DivideState(input), true);
	while (current.right) {
		current == foldCycle(current.left, folder);
	}
	return current.left.advance().stream();
}
Tuple<DivideState, Boolean> foldCycle_Main(DivideState state, BiFunction<DivideState, Character, DivideState> folder){
	Option<Tuple<DivideState, Character>> maybeNext = state.pop();
	if (maybeNext.tag == Some) {
		Some<Tuple<DivideState, Character>> _cast = maybeNext.data.some;
		Tuple<DivideState, Character> value = _cast.value;
		return new_Tuple<DivideState, Boolean>(foldEscaped(value.left, value.right, folder), true);
	}
	return new_Tuple<DivideState, Boolean>(state, false);
}
auto __lambda4__() {
	return foldDoubleQuotes(state, next);
}
auto __lambda5__() {
	return folder.apply(state, next);
}
DivideState foldEscaped_Main(DivideState state, char next, BiFunction<DivideState, Character, DivideState> folder){
	return foldSingleQuotes(state, next).or(__lambda4__).orElseGet(__lambda5__);
}
Option<DivideState> foldSingleQuotes_Main(DivideState state, char next){
	if (next != '\'') {
		return new_None<DivideState>();
	}
	DivideState appended = state.append(next);
	return appended.popAndAppendToTuple().flatMap(foldEscaped_Main).flatMap(popAndAppendToOption_DivideState);
}
Option<DivideState> foldEscaped_Main(Tuple<DivideState, Character> tuple){
	if (tuple.right == '\\') {
		return tuple.left.popAndAppendToOption();
	}
	else {
		return new_Some<DivideState>(tuple.left);
	}
}
Option<DivideState> foldDoubleQuotes_Main(DivideState state, char next){
	if (next != '\"') {
		return new_None<DivideState>();
	}
	Tuple<DivideState, Boolean> current = new_Tuple<DivideState, Boolean>(state.append(next), true);
	while (current.right) {
		current == foldUntilDoubleQuotes(current.left);
	}
	return new_Some<DivideState>(current.left);
}
Tuple<DivideState, Boolean> foldUntilDoubleQuotes_Main(DivideState state){
	Option<Tuple<DivideState, Character>> maybeNext = state.popAndAppendToTuple();
	if (!(maybeNext.tag == Some)) {
		Some<Tuple<DivideState, Character>> _cast = maybeNext.data.some;
		Tuple<DivideState, Character> value = _cast.value;
		return new_Tuple<DivideState, Boolean>(state, false);
	}
	DivideState nextState = value.left;
	char nextChar = value.right;
	if (nextChar == '\\') {
		return new_Tuple<DivideState, Boolean>(nextState.popAndAppendToOption().orElse(nextState), true);
	}
	if (nextChar == '\"') {
		return new_Tuple<DivideState, Boolean>(nextState, false);
	}
	return new_Tuple<DivideState, Boolean>(nextState, true);
}
DivideState foldStatement_Main(DivideState state, char c){
	DivideState appended = state.append(c);
	if (c == ';' && appended.isLevel()) {
		return appended.advance();
	}
	if (c == '}' && appended.isShallow()) {
		Option<Character> peeked = appended.peek();
		DivideState withPeeked;
		if (peeked.tag == Some) {
		Some<Character>(Character value) && value == ';' _cast = peeked.data.some;
			withPeeked == appended.popAndAppendToOption().orElse(appended);
		}
		else {
			withPeeked = appended;
		}
		return withPeeked.advance().exit();
	}
	if (c == '{' || c == '(') {
		return appended.enter();
	}
	if (c == '}' || c == ')') {
		return appended.exit();
	}
	return appended;
}
auto __lambda6__() {
	return new_Tuple<char*, ParseState>(wrap(stripped), state);
}
Tuple<char*, ParseState> compileRootSegment_Main(char* input, ParseState state){
	char* stripped = input.strip();
	if (stripped.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
		return new_Tuple<char*, ParseState>("", state);
	}
	return compileStructure(stripped, "class", state).orElseGet(__lambda6__);
}
auto __lambda7__(auto segment) {
	return !segment.isEmpty();
}
auto __lambda8__(auto slice) {
	return generateIndent(1) + slice;
}
auto __lambda9__(auto slice) {
	return slice + joinedTypeParameters + " " + slice.toLowerCase();
}
auto __lambda10__(auto content1) {
	return generateStatement(content1, 1);
}
Option<Tuple<char*, ParseState>> compileStructure_Main(char* input, char* type, ParseState state){
	int i = input.indexOf(type + " ");
	if (i < 0) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* afterKeyword = input.substring(i + (type + " ").length());
	int contentStart = afterKeyword.indexOf("{");
	if (contentStart < 0) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* beforeContent = afterKeyword.substring(0, contentStart).strip();
	char* withoutPermits = beforeContent;
	ArrayList<char*> variants = new_ArrayList<char*>();
	int permitsIndex = beforeContent.indexOf("permits");
	if (permitsIndex >= 0) {
		char* slice = beforeContent.substring(permitsIndex + "permits".length());
		variants == divide(slice, foldValue_Main).map(strip_char*).filter(__lambda7__).collect(new_ListCollector<char*>());
		withoutPermits == beforeContent.substring(0, permitsIndex);
	}
	char* maybeWithExtends = withoutPermits.strip();
	int extendsIndex = maybeWithExtends.indexOf("extends");
	if (extendsIndex >= 0) {
		maybeWithExtends == maybeWithExtends.substring(0, extendsIndex).strip();
	}
	char* maybeWithImplements = maybeWithExtends.strip();
	int implementsIndex = maybeWithImplements.indexOf("implements");
	if (implementsIndex >= 0) {
		maybeWithImplements == maybeWithImplements.substring(0, implementsIndex).strip();
	}
	char* beforeMaybeParams = maybeWithImplements.strip();
	char* recordFields = "";
	if (maybeWithImplements.endsWith(")")) {
		char* slice = maybeWithImplements.substring(0, maybeWithImplements.length() - 1);
		int beforeParams = slice.indexOf("(");
		if (beforeParams >= 0) {
			beforeMaybeParams == slice.substring(0, beforeParams).strip();
			char* substring = slice.substring(beforeParams + 1);
			recordFields == compileValues(substring, compileParameter_Main, "");
		}
	}
	char* name = beforeMaybeParams.strip();
	ArrayList<char*> typeParameters = new_ArrayList<char*>();
	if (beforeMaybeParams.endsWith(">")) {
		char* withoutEnd = beforeMaybeParams.substring(0, beforeMaybeParams.length() - 1);
		int i1 = withoutEnd.indexOf("<");
		if (i1 >= 0) {
			name == withoutEnd.substring(0, i1).strip();
			char* arguments = withoutEnd.substring(i1 + "<".length());
			typeParameters == divide(arguments, foldValue_Main).map(strip_char*).collect(new_ListCollector<char*>());
		}
	}
	char* afterContent = afterKeyword.substring(contentStart + "{".length()).strip();
	if (!afterContent.endsWith("}")) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* content = afterContent.substring(0, afterContent.length() - "}".length());
	ArrayList<char*> segments = divide(content, foldStatement_Main).collect(new_ListCollector<char*>());
	StringBuilder inner = new_StringBuilder();
	ParseState outer = state;
	int j = 0;
	while (j < segments.size()) {
		char* segment = segments.get(j).orElse(null);
		Tuple<char*, ParseState> compiled = compileClassSegment(segment, name, outer);
		inner.append(compiled.left);
		outer = compiled.right;
		j++;
	}
	char* templateString;
	if (typeParameters.isEmpty()) {
		templateString = "";
	}
	else {
		char* collect = /*
					"<" + typeParameters.stream().map(slice -> "typeparam " + slice).collect(new Joiner(", ")) + ">"*/;
		char* templateValues = collect + System.lineSeparator();
		templateString = "template " + templateValues;
	}
	char* generatedSubStructs = "";
	if (!variants.isEmpty()) {
		char* enumFields = variants.stream().map(__lambda8__).collect(new_Joiner(","));
		char* joinedTypeParameters;
		if (typeParameters.isEmpty()) {
			joinedTypeParameters = "";
		}
		else {
			joinedTypeParameters = "<" + typeParameters.stream().collect(new_Joiner(", ")) + ">";
		}
		char* unionFields = variants.stream().map(__lambda9__).map(__lambda10__).collect(new_Joiner());
		generatedSubStructs == "enum " + name + "Tag {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator() + templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" + System.lineSeparator();
		recordFields +  == generateStatement(name + "Tag tag", 1);
		recordFields +  == generateStatement(name + "Data" + joinedTypeParameters + " data", 1);
	}
	char* generated = generatedSubStructs + templateString + "struct " + name + " {" + recordFields + inner + System.lineSeparator() + "};" + System.lineSeparator();
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("", outer.addStruct(generated)));
}
char* compileValues_Main(char* input, Function<char*, char*> mapper){
	return compileValues(input, mapper, ", ");
}
char* compileValues_Main(char* input, Function<char*, char*> mapper, char* delimiter){
	return divide(input, foldValue_Main).map(mapper).collect(new_Joiner(delimiter));
}
auto __lambda11__() {
	return wrap(input1);
}
char* compileParameter_Main(char* input1){
	if (input1.isEmpty()) {
		return "";
	}
	return generateField(input1).orElseGet(__lambda11__);
}
auto __lambda12__(auto content) {
	return generateStatement(content, 1);
}
Option<char*> generateField_Main(char* input){
	return compileDefinition(input).map(generate_Definable).map(__lambda12__);
}
char* generateStatement_Main(char* content, int depth){
	return generateSegment(content + ";", depth);
}
char* generateSegment_Main(char* content, int depth){
	return generateIndent(depth) + content;
}
char* generateIndent_Main(int depth){
	return System.lineSeparator() + "\t".repeat(depth);
}
DivideState foldValue_Main(DivideState state, char next){
	if (next == ',' && state.isLevel()) {
		return state.advance();
	}
	DivideState appended = state.append(next);
	if (next == '-') {
		Option<Character> peeked = appended.peek();
		if (peeked.tag == Some) {
			if (value.equals('>')) {
		Some<Character> _cast = peeked.data.some;
		Character value = _cast.value;
				return appended.popAndAppendToOption().orElse(appended);
			}
		}
	}
	if (next == '(' || next == '<') {
		return appended.enter();
	}
	if (next == ')' || next == '>') {
		return appended.exit();
	}
	return appended;
}
Tuple<char*, ParseState> compileClassSegment_Main(char* input, char* name, ParseState state){
	char* stripped = input.strip();
	if (stripped.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	return compileClassSegmentValue(stripped, name, state);
}
auto __lambda13__() {
	return compileStructure(input, "record", state);
}
auto __lambda14__() {
	return compileStructure(input, "interface", state);
}
auto __lambda15__() {
	return compileField(input, state);
}
auto __lambda16__() {
	return compileMethod(input, name, state);
}
auto __lambda17__() {
	char* generated = generateSegment(wrap(input), 1);
	return new_Tuple<char*, ParseState>(generated, state);
}
Tuple<char*, ParseState> compileClassSegmentValue_Main(char* input, char* name, ParseState state){
	if (input.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	return compileStructure(input, "class", state).or(__lambda13__).or(__lambda14__).or(__lambda15__).or(__lambda16__).orElseGet(__lambda17__);
}
Option<Tuple<char*, ParseState>> compileMethod_Main(char* input, char* name, ParseState state){
	int paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* beforeParams = input.substring(0, paramStart).strip();
	char* withParams = input.substring(paramStart + 1);
	int paramEnd = withParams.indexOf(")");
	if (paramEnd < 0) {
		return new_None<Tuple<char*, ParseState>>();
	}
	JMethodHeader methodHeader = compileMethodHeader(beforeParams);
	char* inputParams = withParams.substring(0, paramEnd);
	char* withBraces = withParams.substring(paramEnd + 1).strip();
	char* outputParams = compileParameters(inputParams);
	char* outputMethodHeader = transformMethodHeader(methodHeader, name).generate() + "(" + outputParams + ")";
	char* outputBodyWithBraces;
	if (withBraces.equals(";") || isPlatformDependentMethod(methodHeader)) {
		outputBodyWithBraces = ";";
	}
	else if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		char* inputBody = withBraces.substring(1, withBraces.length() - 1);
		Tuple<ArrayList<char*>, ParseState> compiledBody = compileMethodStatements(state, 0, inputBody);
		ArrayList<char*> statements = compiledBody.left;
	??? _temp = Objects.requireNonNull(methodHeader);
		if (_temp.tag == JConstructor) {
		JConstructor _cast = _temp.data.jconstructor;
			statements == statements.addFirst(generateStatement(name + " this", 1)).addLast(generateStatement("return this", 1));
		}
		char* joined = statements.stream().collect(new_Joiner(""));
		outputBodyWithBraces = "{" + joined + System.lineSeparator() + "}";
	}
	else {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* generated = outputMethodHeader + outputBodyWithBraces + System.lineSeparator();
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("", state.addFunction(generated)));
}
boolean isPlatformDependentMethod_Main(JMethodHeader methodHeader){
		Definition definition && definition.annotations.contains _cast = methodHeader.data.definition definition && definition.annotations.contains("actual");
	return methodHeader.tag == Definition definition && definition.annotations.contains("Actual");
}
Definable transformMethodHeader_Main(JMethodHeader methodHeader, char* name){
	??? _temp = Objects.requireNonNull(methodHeader);
	if (_temp.tag == JConstructor(String name1)) {
		JConstructor _cast = _temp.data.jconstructor(string name1);
		char* name1 = _cast.name1;
		return new_Definition(new_ArrayList<char*>(), name1, "new_" + name1);
	}
	else if (methodHeader.tag == Definition definition) {
		Definition definition _cast = methodHeader.data.definition definition;
		return new_Definition(new_ArrayList<char*>(), definition.type, definition.name + "_" + name);
	}
	else if (methodHeader.tag == Placeholder placeholder) {
		Placeholder placeholder _cast = methodHeader.data.placeholder placeholder;
		return placeholder;
	}
	else {
		return new_Placeholder("?");
	}
}
auto __lambda18__(auto definable) {
	return definable;
}
auto __lambda19__() {
	return compileConstructor(beforeParams);
}
auto __lambda20__() {
	return new_Placeholder(beforeParams);
}
JMethodHeader compileMethodHeader_Main(char* beforeParams){
	return compileDefinition(beforeParams). < JMethodHeader >= map(__lambda18__).or(__lambda19__).orElseGet(__lambda20__);
}
auto __lambda21__(auto slice) {
	return compileDefinition(slice).map(generate_Definable).orElse("");
}
char* compileParameters_Main(char* input){
	if (input.isEmpty()) {
		return "";
	}
	return compileValues(input, __lambda21__);
}
Tuple<char*, ParseState> compileMethodSegment_Main(char* input, int depth, ParseState state){
	char* stripped = input.strip();
	if (stripped.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	Tuple<char*, ParseState> tuple = compileMethodSegmentValue(stripped, depth, state);
	return new_Tuple<char*, ParseState>(generateSegment(tuple.left, depth), tuple.right);
}
Tuple<char*, ParseState> compileMethodSegmentValue_Main(char* input, int depth, ParseState state){
	char* stripped = input.strip();
	Option<Tuple<char*, ParseState>> compiled = compileBlock(state, stripped, depth);
	if (compiled.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = compiled.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return value;
	}
	Option<Tuple<char*, ParseState>> maybeIf = compileConditional("if", depth, state, stripped);
	if (maybeIf.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = maybeIf.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return value;
	}
	Option<Tuple<char*, ParseState>> maybeWhile = compileConditional("while", depth, state, stripped);
	if (maybeWhile.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = maybeWhile.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return value;
	}
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
Option<Tuple<char*, ParseState>> compileConditional_Main(char* type, int depth, ParseState state, char* stripped){
	if (!stripped.startsWith(type)) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* withoutPrefix = stripped.substring(type.length());
	ArrayList<char*> conditionEnd = divide(withoutPrefix, foldConditionEnd_Main).collect(new_ListCollector<char*>());
	if (conditionEnd.size() < 2) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* withConditionEnd = conditionEnd.getFirst().orElse(null);
	char* substring1 = withConditionEnd.substring(0, withConditionEnd.length() - 1).strip();
	char* body = conditionEnd.subList(1, conditionEnd.size()).orElse(new_ArrayList<char*>()).stream().collect(new_Joiner(""));
	if (!substring1.startsWith("(")) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* expression = substring1.substring(1);
	Tuple<char*, ParseState> condition = compileExpression(expression, state);
	Tuple<char*, ParseState> compiledBody = compileMethodSegmentValue(body, depth, condition.right);
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(type + " (" + condition.left + ") " + compiledBody.left, compiledBody.right));
}
Option<Tuple<char*, ParseState>> compileBlock_Main(ParseState state, char* input, int depth){
	if (!input.startsWith("{") ||  != input.endsWith("}")) {
		return new_None<Tuple<char*, ParseState>>();
	}
	Tuple<ArrayList<char*>, ParseState> result = compileMethodStatements(state, depth, input.substring(1, input.length() - 1));
	char* generated = "{" + result.left().stream().collect(new_Joiner("")) + generateIndent(depth) + "}";
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(generated, result.right()));
}
Tuple<ArrayList<char*>, ParseState> compileMethodStatements_Main(ParseState state, int depth, char* content){
	ArrayList<char*> compiled = new_ArrayList<char*>();
	ParseState current = state;
	ArrayList < String >= list == divide(content, foldStatement_Main).collect(new_ListCollector<char*>());
	int i = 0;
	while (i < list.size()) {
		char* s = list.get(i).orElse(null);
		Tuple<char*, ParseState> string = compileMethodSegment(s, depth + 1, current.pushBeforeStatements());
		compiled == compiled.addAll(string.right.popBeforeStatements()).add(string.left);
		current = string.right;
		i++;
	}
	ArrayList<char*> removed = current.popAfterStatements();
	compiled == compiled.addAllAt(0, removed).orElse(new_ArrayList<char*>());
	return new_Tuple<ArrayList<char*>, ParseState>(compiled, current);
}
DivideState foldConditionEnd_Main(DivideState state, char c){
	DivideState appended = state.append(c);
	if (c == ')') {
		DivideState exited = appended.exit();
		if (exited.isLevel()) {
			return exited.advance();
		}
	}
	if (c == '(') {
		return appended.enter();
	}
	return appended;
}
auto __lambda22__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
auto __lambda23__(auto generated) {
	return new_Tuple<char*, ParseState>(generated, state);
}
auto __lambda24__() {
	return compileExpression(destinationString, state);
}
auto __lambda25__(auto value) {
	return new_Tuple<char*, ParseState>(value.generate(), state);
}
auto __lambda26__() {
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
		Option<Tuple<char*, ParseState>> temp = tryCompileExpression(slice, state).map(__lambda22__);
		if (temp.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = temp.data.some;
		Tuple<char*, ParseState> value = _cast.value;
			return new_Tuple<char*, ParseState>(value.left + "++", value.right);
		}
	}
	Option<Tuple<char*, ParseState>> invokableResult = compileInvokable(state, input);
	if (invokableResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = invokableResult.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return value;
	}
	int i = input.indexOf("=");
	if (i >= 0) {
		char* destinationString = input.substring(0, i);
		char* source = input.substring(i + 1);
		Tuple<char*, ParseState> destinationResult = compileDefinition(destinationString).map(generate_Definition).map(__lambda23__).orElseGet(__lambda24__);
		Tuple<char*, ParseState> sourceResult = compileExpression(source, destinationResult.right);
		return new_Tuple<char*, ParseState>(destinationResult.left + " = " + sourceResult.left, sourceResult.right);
	}
	return compileDefinition(input).map(__lambda25__).orElseGet(__lambda26__);
}
auto __lambda27__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
auto __lambda28__() {
	return new_Tuple<char*, ParseState>(wrap(input), state);
}
Tuple<char*, ParseState> compileExpression_Main(char* input, ParseState state){
	return tryCompileExpression(input, state).map(__lambda27__).orElseGet(__lambda28__);
}
auto __lambda29__() {
	return compileIdentifier(stripped, state);
}
auto __lambda30__() {
	return compileNumber(stripped, state).map(wrapInContent_Main);
}
Option<Tuple<CExpression, ParseState>> tryCompileExpression_Main(char* input, ParseState state){
	char* stripped = input.strip();
	Option<Tuple<char*, ParseState>> charResult = compileChar(stripped, state);
	if (charResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = charResult.data.some;
		return charResult.map(wrapInContent_Main);
	}
	Option<Tuple<char*, ParseState>> stringResult = compileString(stripped, state);
	if (stringResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = stringResult.data.some;
		return stringResult.map(wrapInContent_Main);
	}
	Option<Tuple<char*, ParseState>> notResult = compileNot(state, stripped);
	if (notResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = notResult.data.some;
		return notResult.map(wrapInContent_Main);
	}
	Option<Tuple<char*, ParseState>> lambdaResult = compileLambda(state, stripped);
	if (lambdaResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = lambdaResult.data.some;
		return lambdaResult.map(wrapInContent_Main);
	}
	Option<Tuple<char*, ParseState>> instanceOfResult = compileInstanceOf(state, stripped);
	if (instanceOfResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = instanceOfResult.data.some;
		return instanceOfResult.map(wrapInContent_Main);
	}
	Option<Tuple<char*, ParseState>> left = compileInvokable(state, stripped);
	if (left.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = left.data.some;
		return left.map(wrapInContent_Main);
	}
	Option<Tuple<char*, ParseState>> methodReferenceResult = compileMethodReference(state, stripped);
	if (methodReferenceResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = methodReferenceResult.data.some;
		return methodReferenceResult.map(wrapInContent_Main);
	}
	Option<Tuple<char*, ParseState>> fieldAccessResult = compileFieldAccess(state, stripped);
	if (fieldAccessResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = fieldAccessResult.data.some;
		return fieldAccessResult.map(wrapInContent_Main);
	}
	return getOr(state, stripped).map(wrapInContent_Main).or(__lambda29__).or(__lambda30__);
}
auto __lambda31__() {
	return compileOperator(stripped, "-", state);
}
auto __lambda32__() {
	return compileOperator(stripped, ">=", state);
}
auto __lambda33__() {
	return compileOperator(stripped, "<", state);
}
auto __lambda34__() {
	return compileOperator(stripped, "!=", state);
}
auto __lambda35__() {
	return compileOperator(stripped, "==", state);
}
auto __lambda36__() {
	return compileOperator(stripped, "&&", state);
}
auto __lambda37__() {
	return compileOperator(stripped, "||", state);
}
Option<Tuple<char*, ParseState>> getOr_Main(ParseState state, char* stripped){
	return compileOperator(stripped, "+", state).or(__lambda31__).or(__lambda32__).or(__lambda33__).or(__lambda34__).or(__lambda35__).or(__lambda36__).or(__lambda37__);
}
Tuple<CExpression, ParseState> wrapInContent_Main(Tuple<char*, ParseState> tuple){
	return new_Tuple<CExpression, ParseState>(new_Content(tuple.left), tuple.right);
}
Option<Tuple<char*, ParseState>> compileString_Main(char* stripped, ParseState state){
	if (isString(stripped)) {
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(stripped, state));
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda38__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
Option<Tuple<char*, ParseState>> compileFieldAccess_Main(ParseState state, char* stripped){
	int separator = stripped.lastIndexOf(".");
	if (separator < 0) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* substring = stripped.substring(0, separator);
	char* name = stripped.substring(separator + 1).strip();
	if (!isIdentifier(name)) {
		return new_None<Tuple<char*, ParseState>>();
	}
	Option<Tuple<char*, ParseState>> maybeResult = tryCompileExpression(substring, state).map(__lambda38__);
	if (!(maybeResult.tag == Some)) {
		Some<Tuple<String, ParseState>> _cast = maybeResult.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return new_None<Tuple<char*, ParseState>>();
	}
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(value.left + "." + name, value.right));
}
Option<Tuple<char*, ParseState>> compileMethodReference_Main(ParseState state, char* stripped){
	int separator = stripped.lastIndexOf("::");
	if (separator >= 0) {
		char* substring = stripped.substring(0, separator);
		char* name = stripped.substring(separator + 2).strip();
		if (isIdentifier(name)) {
			Option<char*> maybeResult = compileType(substring);
			if (maybeResult.tag == Some) {
		Some<String> _cast = maybeResult.data.some;
		char* value = _cast.value;
				return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(name + "_" + value, state));
			}
		}
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda39__(auto definition) {
	char* generated = definition.generate();
	return generated + " = _cast." + definition.name;
}
auto __lambda40__(auto destructMember) {
	return generateStatement(destructMember, 2);
}
auto __lambda41__(auto slice1) {
	return compileDefinition(slice1).map(__lambda39__).map(__lambda40__).orElse("");
}
Option<Tuple<char*, ParseState>> compileInstanceOf_Main(ParseState state, char* stripped){
	int instanceOfIndex = stripped.indexOf("instanceof");
	if (instanceOfIndex < 0) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* beforeOperator = stripped.substring(0, instanceOfIndex).strip();
	char* afterOperator = stripped.substring(instanceOfIndex + "instanceof".length()).strip();
	Option<Tuple<CExpression, ParseState>> maybeResult = tryCompileExpression(beforeOperator, state);
	if (!(maybeResult.tag == Some)) {
		Some<Tuple<CExpression, ParseState>> _cast = maybeResult.data.some;
		Tuple<CExpression, ParseState> value = _cast.value;
		return new_None<Tuple<char*, ParseState>>();
	}
	int typeArgumentsStart = afterOperator.indexOf("<");
	char* variantName;
	if (typeArgumentsStart >= 0) {
		variantName == afterOperator.substring(0, typeArgumentsStart);
	}
	else {
		variantName = afterOperator;
	}
	char* parameters = "";
	if (afterOperator.endsWith(")")) {
		char* slice = afterOperator.substring(0, afterOperator.length() - 1);
		int paramStart = slice.indexOf("(");
		if (paramStart >= 0) {
			char* paramString = slice.substring(paramStart + 1);
			char* result1 = "";
			if (!paramString.isEmpty()) {
				result1 == compileValues(paramString, __lambda41__);
			}
			parameters = result1;
			afterOperator == afterOperator.substring(0, paramStart);
		}
	}
	CExpression target = value.left;
	ParseState maybeWithBeforeStatement = value.right;
	char* targetAlias;
	if (!(target.tag == CIdentifier)) {
		CIdentifier _cast = target.data.cidentifier;
		char* alias = generateStatement("??? _temp = " + target.generate(), 1);
		maybeWithBeforeStatement.addBeforeStatement(alias);
		targetAlias = "_temp";
	}
	else {
		targetAlias == target.generate();
	}
	char* content = afterOperator + " _cast = " + targetAlias + ".data." + variantName.toLowerCase();
	char* statement = generateStatement(content, 2) + parameters;
	ParseState parseState = maybeWithBeforeStatement.addAfterStatement(statement);
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(targetAlias + ".tag == " + variantName, parseState));
}
Option<Tuple<char*, ParseState>> compileChar_Main(char* stripped, ParseState state){
	if (isABoolean(stripped)) {
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(stripped, state));
	}
	return new_None<Tuple<char*, ParseState>>();
}
boolean isABoolean_Main(char* stripped){
	return stripped.startsWith("'") && stripped.endsWith("'") && stripped.length() <  == 4;
}
auto __lambda42__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
Option<Tuple<char*, ParseState>> compileNot_Main(ParseState state, char* stripped){
	if (stripped.startsWith("!")) {
		char* slice = stripped.substring(1);
		Option<Tuple<char*, ParseState>> maybeResult = tryCompileExpression(slice, state).map(__lambda42__);
		if (maybeResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = maybeResult.data.some;
		Tuple<char*, ParseState> value = _cast.value;
			return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("!" + value.left, value.right));
		}
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda43__(auto tuple, auto s) {
	return mergeExpression(tuple.left, tuple.right, s);
}
Option<Tuple<char*, ParseState>> compileInvokable_Main(ParseState state, char* stripped){
	if (!stripped.endsWith(")")) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* slice = stripped.substring(0, stripped.length() - 1);
	ArrayList<char*> segments = findArgStart(slice).collect(new_ListCollector<char*>());
	if (segments.size() < 2) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* callerWithExt = segments.subList(0, segments.size() - 1).orElse(new_ArrayList<char*>()).stream().collect(new_Joiner(""));
	if (!callerWithExt.endsWith("(")) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* caller = callerWithExt.substring(0, callerWithExt.length() - 1);
	char* arguments = segments.getLast().orElse(null);
	Option<Tuple<char*, ParseState>> maybeCallerResult = compileCaller(state, caller);
	if (!(maybeCallerResult.tag == Some)) {
		Some<Tuple<String, ParseState>> _cast = maybeCallerResult.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return new_None<Tuple<char*, ParseState>>();
	}
	Tuple<StringJoiner, ParseState> reduce = divide(arguments, foldValue_Main).collect(new_ListCollector<char*>()).stream().foldWithInitial(new_Tuple<StringJoiner, ParseState>(new_StringJoiner(", "), value.right), __lambda43__);
	char* collect = reduce.left.toString();
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(value.left + "(" + collect + ")", reduce.right));
}
Tuple<StringJoiner, ParseState> mergeExpression_Main(StringJoiner joiner, ParseState state, char* segment){
	Tuple<char*, ParseState> result = compileExpression(segment, state);
	StringJoiner add = joiner.add(result.left);
	return new_Tuple<StringJoiner, ParseState>(add, result.right);
}
auto __lambda44__(auto state, auto c) {
	DivideState appended = state.append(c);
	if (c == '(') {
		DivideState entered = appended.enter();
		if (entered.isShallow()) {
			return entered.advance();
		}
		else {
			return entered;
		}
	}
	if (c == ')') {
		return appended.exit();
	}
	return appended;
}
Stream<char*> findArgStart_Main(char* input){
	return divide(input, __lambda44__);
}
auto __lambda45__(auto slice) {
	return !slice.isEmpty();
}
auto __lambda46__(auto slice) {
	return "auto " + slice;
}
Option<Tuple<char*, ParseState>> compileLambda_Main(ParseState state, char* stripped){
	int i1 = stripped.indexOf("->");
	if (i1 < 0) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* beforeArrow = stripped.substring(0, i1).strip();
	char* outputParams;
	if (isIdentifier(beforeArrow)) {
		outputParams = "auto " + beforeArrow;
	}
	else if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
		char* withoutParentheses = beforeArrow.substring(1, beforeArrow.length() - 1);
		char** array = withoutParentheses.split(Pattern.quote(","));
		outputParams == Streams.fromRef(array).map(strip_char*).filter(__lambda45__).map(__lambda46__).collect(new_Joiner(", "));
	}
	else {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* body = stripped.substring(i1 + 2).strip();
	Tuple<char*, ParseState> bodyResult = compileLambdaBody(state, body);
	char* generatedName = bodyResult.right.generateAnonymousFunctionName();
	char* s1 = "auto " + generatedName + "(" + outputParams + ") " + bodyResult.left + System.lineSeparator();
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(generatedName, bodyResult.right.addFunction(s1)));
}
Tuple<char*, ParseState> compileLambdaBody_Main(ParseState state, char* body){
	Option<Tuple<char*, ParseState>> maybeBlock = compileBlock(state, body, 0);
	if (maybeBlock.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = maybeBlock.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return value;
	}
	Tuple<char*, ParseState> result = compileExpression(body, state);
	char* s = generateStatement("return " + result.left, 1);
	char* s2 = "{" + s + generateIndent(0) + "}";
	return new_Tuple<char*, ParseState>(s2, result.right);
}
auto __lambda47__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
Option<Tuple<char*, ParseState>> compileCaller_Main(ParseState state, char* caller){
	if (caller.startsWith("new ")) {
		Option<char*> newType = compileType(caller.substring("new ".length()));
		if (newType.tag == Some) {
		Some<String> _cast = newType.data.some;
		char* value = _cast.value;
			return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("new_" + value, state));
		}
	}
	return tryCompileExpression(caller, state).map(__lambda47__);
}
Option<Tuple<CExpression, ParseState>> compileIdentifier_Main(char* input, ParseState state){
	if (isIdentifier(input)) {
		return new_Some<Tuple<CExpression, ParseState>>(new_Tuple<CExpression, ParseState>(new_CIdentifier(input), state));
	}
	return new_None<Tuple<CExpression, ParseState>>();
}
Option<Tuple<char*, ParseState>> compileNumber_Main(char* stripped, ParseState state){
	if (isNumber(stripped)) {
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(stripped, state));
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda48__(auto state1, auto next) {
	return foldOperator(operator, state1, next);
}
auto __lambda49__(auto tuple1) {
	return new_Tuple<char*, ParseState>(tuple1.left.generate(), tuple1.right);
}
auto __lambda50__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
Option<Tuple<char*, ParseState>> compileOperator_Main(char* input, char* operator, ParseState state){
	ArrayList<char*> segments = divide(input, __lambda48__).collect(new_ListCollector<char*>());
	if (segments.size() < 2) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* left = segments.getFirst().orElse(null);
	char* right = segments.subList(1, segments.size()).orElse(new_ArrayList<char*>()).stream().collect(new_Joiner(operator));
	Option<Tuple<char*, ParseState>> maybeLeftResult = tryCompileExpression(left, state).map(__lambda49__);
	if (!(maybeLeftResult.tag == Some)) {
		Some<Tuple<String, ParseState>> _cast = maybeLeftResult.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return new_None<Tuple<char*, ParseState>>();
	}
	Option<Tuple<char*, ParseState>> maybeRightResult = tryCompileExpression(right, value.right).map(__lambda50__);
	if (maybeRightResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = maybeRightResult.data.some;
		Tuple<char*, ParseState> rightResult = _cast.rightResult;
		char* generated = value.left + " " + operator + " " + rightResult.left;
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(generated, rightResult.right));
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda51__(auto inner) {
	return inner.left;
}
DivideState foldOperator_Main(char* operator, DivideState state1, Character next){
	if (next != operator.charAt(0)) {
		return state1.append(next);
	}
	Option<Character> peeked = state1.peek();
	??? _temp = operator.length() >= 2 && peeked;
	if (_temp.tag == Some) {
		if (value == operator.charAt(1)) {
		Some<Character> _cast = _temp.data.some;
		Character value = _cast.value;
			return state1.pop().map(__lambda51__).orElse(state1).advance();
		}
	}
	return state1.advance();
}
boolean isString_Main(char* stripped){
	if (stripped.length() < 2) {
		return false;
	}
	boolean hasDoubleQuotes = stripped.startsWith("\"") && stripped.endsWith("\"");
	if (!hasDoubleQuotes) {
		return false;
	}
	char* content = stripped.substring(1, stripped.length() - 1);
	return areAllDoubleQuotesEscaped(content);
}
auto __lambda52__(auto i) {
	char c = input.charAt(i);
	if (c != '\"') {
		return true;
	}
	if (i == 0) {
		return false;
	}
	char previous = input.charAt(i - 1);
	return previous == '\\';
}
boolean areAllDoubleQuotesEscaped_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda52__);
}
auto __lambda53__(auto i) {
	return Character.isDigit(input.charAt(i));
}
boolean isNumber_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda53__);
}
auto __lambda54__(auto i) {
	char next = input.charAt(i);
	boolean isValidDigit = i != 0 && Character.isDigit(next);
	return Character.isLetter(next) || isValidDigit;
}
boolean isIdentifier_Main(char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda54__);
}
Option<JMethodHeader> compileConstructor_Main(char* beforeParams){
	int separator = beforeParams.lastIndexOf(" ");
	if (separator < 0) {
		return new_None<JMethodHeader>();
	}
	char* name = beforeParams.substring(separator + " ".length());
	return new_Some<JMethodHeader>(new_JConstructor(name));
}
Option<Tuple<char*, ParseState>> compileField_Main(char* input, ParseState state){
	if (input.endsWith(";")) {
		char* substring = input.substring(0, input.length() - ";".length()).strip();
		Option<char*> s = generateField(substring);
		if (s.tag == Some) {
		Some<String> _cast = s.data.some;
		char* value = _cast.value;
			return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(value, state));
		}
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda55__(auto type) {
	return new_Definition(new_ArrayList<char*>(), type, name);
}
auto __lambda56__(auto type) {
	return new_Definition(annotations, type, name);
}
Option<Definition> compileDefinition_Main(char* input){
	char* stripped = input.strip();
	int index = stripped.lastIndexOf(" ");
	if (index < 0) {
		return new_None<Definition>();
	}
	char* beforeName = stripped.substring(0, index).strip();
	char* name = stripped.substring(index + " ".length()).strip();
	if (!isIdentifier(name)) {
		return new_None<Definition>();
	}
	ArrayList<char*> segments = divide(beforeName, foldTypeSeparator_Main).collect(new_ListCollector<char*>());
	if (segments.size() < 2) {
		return compileType(beforeName).map(__lambda55__);
	}
	char* withoutLast = segments.subList(0, segments.size() - 1).orElse(new_ArrayList<char*>()).stream().collect(new_Joiner(" "));
	ArrayList<char*> annotations = findAnnotations(withoutLast);
	char* typeString = segments.getLast().orElse(null);
	return compileType(typeString).map(__lambda56__);
}
auto __lambda57__(auto slice) {
	return slice.startsWith("@");
}
auto __lambda58__(auto slice) {
	return slice.substring(1);
}
ArrayList<char*> findAnnotations_Main(char* withoutLast){
	int i = withoutLast.lastIndexOf("\n");
	if (i < 0) {
		return new_ArrayList<char*>();
	}
	char** slices = withoutLast.substring(0, i).strip().split(Pattern.quote("\n"));
	return Streams.fromRef(slices).map(strip_char*).filter(__lambda57__).map(__lambda58__).collect(new_ListCollector<char*>());
}
DivideState foldTypeSeparator_Main(DivideState state, Character c){
	if (c == ' ' && state.isLevel()) {
		return state.advance();
	}
	DivideState appended = state.append(c);
	if (c == '<') {
		return appended.enter();
	}
	if (c == '>') {
		return appended.exit();
	}
	return appended;
}
auto __lambda59__() {
	return wrap(slice);
}
auto __lambda60__(auto slice) {
	return compileType(slice).orElseGet(__lambda59__);
}
auto __lambda61__(auto result) {
	return result + "*";
}
Option<char*> compileType_Main(char* input){
	char* stripped = input.strip();
	if (stripped.equals("public")) {
		return new_None<char*>();
	}
	if (stripped.endsWith(">")) {
		char* withoutEnd = stripped.substring(0, stripped.length() - 1);
		int argumentStart = withoutEnd.indexOf("<");
		if (argumentStart >= 0) {
			char* base = withoutEnd.substring(0, argumentStart);
			char* argumentsString = withoutEnd.substring(argumentStart + "<".length());
			char* arguments = compileValues(argumentsString, __lambda60__);
			return new_Some<char*>(base + "<" + arguments + ">");
		}
	}
	if (stripped.endsWith("[]")) {
		char* slice = stripped.substring(0, stripped.length() - 2);
		return compileType(slice).map(__lambda61__);
	}
	if (stripped.equals("String")) {
		return new_Some<char*>("char*");
	}
	if (stripped.equals("int")) {
		return new_Some<char*>("int");
	}
	if (isIdentifier(stripped)) {
		return new_Some<char*>(stripped);
	}
	return new_Some<char*>(wrap(stripped));
}
char* wrap_Main(char* input){
	char* replaced = input.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
int main(){
	main_Main();
	return 0;
}