// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
#include "Main.h"
ParseState new_ParseState(void* _ref){
	ParseState this;
	this.functions = new_ArrayList<char*>();
	this.structs = new_ArrayList<char*>();
	this.beforeStatements = new_Stack<ArrayList<char*>>();
	this.beforeStatements.add(new_ArrayList<char*>());
	this.afterStatements = new_ArrayList<char*>();
	this.counter =  - 1;
	this.includes = new_ArrayList<char*>();
	this.beforeStructs = new_ArrayList<char*>();
	this.functionDeclarations = new_ArrayList<char*>();
	return this;
}
ParseState addFunction_ParseState(void* _ref, char* func){
	this.functions == this.functions.addLast(func);
	return this;
}
ParseState addStruct_ParseState(void* _ref, char* struct){
	this.structs == this.structs.addLast(struct);
	return this;
}
char* generateAnonymousFunctionName_ParseState(void* _ref){
	this.counter++;
	return "__lambda" + this.counter + "__";
}
ParseState addAfterStatement_ParseState(void* _ref, char* statement){
	this.afterStatements == this.afterStatements.addLast(statement);
	return this;
}
ArrayList<char*> popAfterStatements_ParseState(void* _ref){
	ArrayList<char*> copy = this.afterStatements.copy();
	this.afterStatements == this.afterStatements.clear();
	return copy;
}
void addBeforeStatement_ParseState(void* _ref, char* beforeStatement){
	ArrayList<char*> peek = this.beforeStatements.pop();
	ArrayList<char*> added = peek.addLast(beforeStatement);
	this.beforeStatements.push(added);
}
ArrayList<char*> popBeforeStatements_ParseState(void* _ref){
	return this.beforeStatements.pop();
}
ParseState pushBeforeStatements_ParseState(void* _ref){
	this.beforeStatements.push(new_ArrayList<char*>());
	return this;
}
ParseState addIncludes_ParseState(void* _ref, char* include){
	if (!this.includes.contains(include)) {
		this.includes == this.includes.addLast(include);
	}
	return this;
}
ParseState addBeforeStruct_ParseState(void* _ref, char* beforeStruct){
	this.beforeStructs == this.beforeStructs.addLast(beforeStruct);
	return this;
}
ParseState addFunctionDeclaration_ParseState(void* _ref, char* functionDeclaration){
	this.functionDeclarations == this.functionDeclarations.addLast(functionDeclaration);
	return this;
}
ArrayList<char*> popFunctionDeclarations_ParseState(void* _ref){
	ArrayList<char*> copy = this.functionDeclarations.copy();
	this.functionDeclarations == this.functionDeclarations.clear();
	return copy;
}
DivideState new_DivideState(void* _ref, char* input){
	DivideState this;
	this.input = input;
	this.buffer = new_StringBuilder();
	this.depth = 0;
	this.segments = new_ArrayList<char*>();
	this.index = 0;
	return this;
}
Stream<char*> stream_DivideState(void* _ref){
	return this.segments.stream();
}
DivideState enter_DivideState(void* _ref){
	this.depth = this.depth + 1;
	return this;
}
DivideState exit_DivideState(void* _ref){
	this.depth = this.depth - 1;
	return this;
}
boolean isShallow_DivideState(void* _ref){
	return this.depth == 1;
}
boolean isLevel_DivideState(void* _ref){
	return this.depth == 0;
}
DivideState append_DivideState(void* _ref, char c){
	this.buffer.append(c);
	return this;
}
DivideState advance_DivideState(void* _ref){
	this.segments == this.segments.addLast(this.buffer.toString());
	this.buffer = new_StringBuilder();
	return this;
}
Option<Tuple<DivideState, Character>> pop_DivideState(void* _ref){
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
Option<Tuple<DivideState, Character>> popAndAppendToTuple_DivideState(void* _ref){
	return this.pop().map(__lambda0__);
}
auto __lambda1__(auto tuple) {
	return tuple.left;
}
Option<DivideState> popAndAppendToOption_DivideState(void* _ref){
	return this.popAndAppendToTuple().map(__lambda1__);
}
Option<Character> peek_DivideState(void* _ref){
	if (this.index < this.input.length()) {
		return new_Some<Character>(this.input.charAt(this.index));
	}
	else {
		return new_None<Character>();
	}
}
Definition new_Definition(void* _ref, char* type, char* name){
	Definition this;
	this(new_ArrayList<char*>(), type, name);
	return this;
}
char* generate_Definition(void* _ref){
	return this.type + " " + this.name;
}
char* generate_Placeholder(void* _ref){
	return wrap(this.input);
}
char* generate_Content(void* _ref){
	return this.value;
}
char* generate_CIdentifier(void* _ref){
	return this.value;
}
void main_Main(void* _ref, char** args){
	??? _temp = run();
	if (_temp.tag == Some) {
		Some<IOError> _cast = _temp.data.some;
		IOError value = _cast.value;
		System.out.println(value.display());
	}
}
Option<IOError> run_Main(void* _ref){
	Path sourceDirectory = Paths.get(".", "src", "main", "java");
	Path targetDirectory = Paths.get(".", "src", "main", "windows");
	Result<ArrayList<Path>, IOError> walked = sourceDirectory.walk();
	return /*switch (walked) {
			case Err<ArrayList<Path>, IOError> v -> new Some<IOError>(v.error());
			case Ok<ArrayList<Path>, IOError> v -> {
				final Result<ArrayList<ArrayList<Path>>, IOError> result =
						runWithSources(v.value(), sourceDirectory, targetDirectory);
				yield switch (result) {
					case Err<ArrayList<ArrayList<Path>>, IOError> v1 -> new Some<IOError>(v1.error());
					case Ok<ArrayList<ArrayList<Path>>, IOError> v1 -> switch (writeBuildFile(v1, targetDirectory)) {
						case Err<Path, IOError> v2 -> new Some<IOError>(v2.error());
						case Ok<Path, IOError> v2 -> runBuildFile(targetDirectory, v2.value());
					};
				};
			}
		}*/;
}
Option<IOError> runBuildFile_Main(void* _ref, Path targetDirectory, Path path){
	/*try {
			final ProcessBuilder builder =
					new ProcessBuilder("cmd.exe", "/c", targetDirectory.relativize(path).asString()).directory(JavaPath
																																																				 .unwrap(
																																																						 targetDirectory)
																																																				 .toFile());

			// Use the parent's console so the spawned process' stdout/stderr show up
			builder.inheritIO();
			final Process proc = builder.start();
			proc.waitFor();

			return new None<IOError>();
		}*/
	/*catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return new Some<IOError>(new JIOError(new IOException(e)));
		}*/
	/*catch (IOException e) {
			return new Some<IOError>(new JIOError(e));
		}*/
}
auto __lambda2__(auto slice) {
	return slice + "^" + System.lineSeparator() + "\t";
}
Result<Path, IOError> writeBuildFile_Main(void* _ref, Ok<ArrayList<ArrayList<Path>>, IOError> v1, Path targetDirectory){
	ArrayList<Path> list = v1.value().stream().flatMap(stream_ArrayList).collect(new_ListCollector<Path>());
	Path path = targetDirectory.resolveByString("build.bat");
	char* joined = list.stream().map(relativize_targetDirectory).map(asString_Path).map(__lambda2__).collect(new_Joiner(" "));
	return /*switch (path.writeString("clang " + joined + " -o magmac.exe")) {
			case None<IOError> _ -> new Ok<Path, IOError>(path);
			case Some<IOError> v -> new Err<Path, IOError>(v.value());
		}*/;
}
Result<ArrayList<ArrayList<Path>>, IOError> runWithSources_Main(void* _ref, ArrayList<Path> sources, Path sourceDirectory, Path targetDirectory){
	return compileSources(sources, sourceDirectory, targetDirectory);
}
auto __lambda3__(auto path) {
	return path.asString().endsWith(".java");
}
auto __lambda4__(auto source) {
	return compileSource(source, sourceDirectory, targetDirectory);
}
Result<ArrayList<ArrayList<Path>>, IOError> compileSources_Main(void* _ref, ArrayList<Path> sources, Path sourceDirectory, Path targetDirectory){
	return sources.stream().filter(__lambda3__).map(__lambda4__).collect(new_ResultCollector<ArrayList<Path>, IOError, ArrayList<ArrayList<Path>>>(new_ListCollector<ArrayList<Path>>()));
}
Result<ArrayList<Path>, IOError> compileSource_Main(void* _ref, Path source, Path sourceDirectory, Path targetDirectory){
	Path relativeParent = sourceDirectory.relativize(source.getParent());
	Path targetParent = targetDirectory.resolveByPath(relativeParent);
	ArrayList<char*> namespace = relativeParent.stream().collect(new_ListCollector<char*>());
	return /*switch (source.readString()) {
			case Ok<String, IOError>(String input) -> compileInput(source, input, targetParent, namespace);
			case Err<String, IOError>(IOError error) -> new Err<ArrayList<Path>, IOError>(error);
		}*/;
}
auto __lambda5__() {
	return target.writeString(targetOutput);
}
Result<ArrayList<Path>, IOError> compileInput_Main(void* _ref, Path source, char* input, Path targetParent, ArrayList<char*> namespace){
	if (!targetParent.exists()) {
		Option<IOError> result = targetParent.createDirectories();
		if (result.tag == Some) {
		Some<IOError> _cast = result.data.some;
		IOError error = _cast.error;
			return new_Err<ArrayList<Path>, IOError>(error);
		}
	}
	char* fileName = source.getFileName().asString();
	int separator = fileName.lastIndexOf(".");
	char* name = fileName.substring(0, separator);
	Tuple<char*, char*> compiled = compile(input, new_Location(namespace, name));
	char* prefix = "// File generated from '" + source.asString() + "'. This is not source code!" + System.lineSeparator();
	char* defined = name.toUpperCase() + "_H";
	char* headerOutput = prefix + "#ifndef " + defined + System.lineSeparator() + "#define " + defined + System.lineSeparator() + compiled.left + "#endif";
	char* targetOutput = prefix + "#include \"Main.h\"" + System.lineSeparator() + compiled.right;
	Path header = targetParent.resolveByString(name + ".h");
	Path target = targetParent.resolveByString(name + ".cpp");
	Option<IOError> maybeError = header.writeString(headerOutput).or(__lambda5__);
	if (maybeError.tag == Some) {
		Some<IOError> _cast = maybeError.data.some;
		IOError error = _cast.error;
		return new_Err<ArrayList<Path>, IOError>(error);
	}
	return new_Ok<ArrayList<Path>, IOError>(new_/*ArrayList<Path>().addLast*/(target));
}
Tuple<char*, char*> compile_Main(void* _ref, char* input, Location location){
	StringJoiner joiner = new_StringJoiner("");
	ParseState state = new_ParseState();
	ArrayList < String >= list == divide(input, foldStatement_Main).collect(new_ListCollector<char*>());
	int i = 0;
	while (i < list.size()) {
		char* input1 = list.get(i).orElse(null);
		Tuple<char*, ParseState> s = compileRootSegment(input1, state, location);
		joiner.add(s.left);
		state = s.right;
		i++;
	}
	char* joined = joiner.toString();
	char* joinedIncludes = state.includes.stream().collect(new_Joiner(""));
	char* joinedBeforeStructs = state.beforeStructs.stream().collect(new_Joiner(""));
	char* joinedStructs = state.structs.stream().collect(new_Joiner(""));
	char* joinedFunctions = state.functions.stream().collect(new_Joiner(""));
	char* generatedHeaderContent = joinedIncludes + joinedBeforeStructs + joinedStructs;
	char* generatedSourceContent = joinedFunctions + joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}";
	return new_Tuple<char*, char*>(generatedHeaderContent, generatedSourceContent);
}
Stream<char*> divide_Main(void* _ref, char* input, BiFunction<DivideState, Character, DivideState> folder){
	Tuple<DivideState, Boolean> current = new_Tuple<DivideState, Boolean>(new_DivideState(input), true);
	while (current.right) {
		current == foldCycle(current.left, folder);
	}
	return current.left.advance().stream();
}
Tuple<DivideState, Boolean> foldCycle_Main(void* _ref, DivideState state, BiFunction<DivideState, Character, DivideState> folder){
	Option<Tuple<DivideState, Character>> maybeNext = state.pop();
	if (maybeNext.tag == Some) {
		Some<Tuple<DivideState, Character>> _cast = maybeNext.data.some;
		Tuple<DivideState, Character> value = _cast.value;
		return new_Tuple<DivideState, Boolean>(foldEscaped(value.left, value.right, folder), true);
	}
	return new_Tuple<DivideState, Boolean>(state, false);
}
auto __lambda6__() {
	return foldDoubleQuotes(state, next);
}
auto __lambda7__() {
	return folder.apply(state, next);
}
DivideState foldEscaped_Main(void* _ref, DivideState state, char next, BiFunction<DivideState, Character, DivideState> folder){
	return foldSingleQuotes(state, next).or(__lambda6__).orElseGet(__lambda7__);
}
Option<DivideState> foldSingleQuotes_Main(void* _ref, DivideState state, char next){
	if (next != '\'') {
		return new_None<DivideState>();
	}
	DivideState appended = state.append(next);
	return appended.popAndAppendToTuple().flatMap(foldEscaped_Main).flatMap(popAndAppendToOption_DivideState);
}
Option<DivideState> foldEscaped_Main(void* _ref, Tuple<DivideState, Character> tuple){
	if (tuple.right == '\\') {
		return tuple.left.popAndAppendToOption();
	}
	else {
		return new_Some<DivideState>(tuple.left);
	}
}
Option<DivideState> foldDoubleQuotes_Main(void* _ref, DivideState state, char next){
	if (next != '\"') {
		return new_None<DivideState>();
	}
	Tuple<DivideState, Boolean> current = new_Tuple<DivideState, Boolean>(state.append(next), true);
	while (current.right) {
		current == foldUntilDoubleQuotes(current.left);
	}
	return new_Some<DivideState>(current.left);
}
Tuple<DivideState, Boolean> foldUntilDoubleQuotes_Main(void* _ref, DivideState state){
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
DivideState foldStatement_Main(void* _ref, DivideState state, char c){
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
auto __lambda8__(auto string, auto string2) {
	return string + "/" + string2;
}
auto __lambda9__() {
	return new_Tuple<char*, ParseState>(wrap(stripped), state);
}
Tuple<char*, ParseState> compileRootSegment_Main(void* _ref, char* input, ParseState state, Location location){
	char* stripped = input.strip();
	if (stripped.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	if (stripped.startsWith("package ")) {
		return new_Tuple<char*, ParseState>("", state);
	}
	if (stripped.startsWith("import ") && stripped.endsWith(";")) {
		char* slice = stripped.substring("import ".length(), stripped.length() - 1);
		char** divisionArray = slice.split(Pattern.quote("."));
		ArrayList<char*> divisions = Streams.fromRef(divisionArray).collect(new_ListCollector<char*>());
		char* joined = Streams.fromLength(location.namespace.size()).map(/*_ -> ".."*/).collect(new_Joiner("/"));
		ArrayList<char*> segments = divisions.subList(0, divisions.size() - 1).orElse(new_ArrayList<char*>());
		ParseState newState;
	??? _temp = segments.getFirst();
		if (_temp.tag == Some) {
		Some<String> _cast = _temp.data.some;
			newState = state;
		}
		else {
			char* folded = segments.stream().foldWithInitial(joined, __lambda8__);
			newState == state.addIncludes("#include \"" + folded + ".h\"" + System.lineSeparator());
		}
		return new_Tuple<char*, ParseState>("", newState);
	}
	return compileStructure(stripped, "class", state).orElseGet(__lambda9__);
}
auto __lambda10__(auto segment) {
	return !segment.isEmpty();
}
auto __lambda11__(auto slice) {
	return generateIndent(1) + slice + "Tag";
}
auto __lambda12__(auto slice) {
	return slice + joinedTypeParameters + " " + slice.toLowerCase();
}
auto __lambda13__(auto content1) {
	return generateStatement(content1, 1);
}
Option<Tuple<char*, ParseState>> compileStructure_Main(void* _ref, char* input, char* type, ParseState state){
	int keywordIndex = input.indexOf(type + " ");
	if (keywordIndex < 0) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* afterKeyword = input.substring(keywordIndex + (type + " ").length());
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
		variants == divide(slice, foldValue_Main).map(strip_char*).filter(__lambda10__).collect(new_ListCollector<char*>());
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
					"<" + typeParameters.stream().map(slice -> "typename " + slice).collect(new Joiner(", ")) + ">"*/;
		char* templateValues = collect + System.lineSeparator();
		templateString = "template " + templateValues;
	}
	char* joinedTypeParameters;
	if (typeParameters.isEmpty()) {
		joinedTypeParameters = "";
	}
	else {
		joinedTypeParameters = "<" + typeParameters.stream().collect(new_Joiner(", ")) + ">";
	}
	char* generatedSubStructs = "";
	if (!variants.isEmpty()) {
		char* enumFields = variants.stream().map(__lambda11__).collect(new_Joiner(","));
		char* unionFields = variants.stream().map(__lambda12__).map(__lambda13__).collect(new_Joiner());
		generatedSubStructs == "enum " + name + "Tag {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator() + templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" + System.lineSeparator();
		recordFields +  == generateStatement(name + "Tag tag", 1);
		recordFields +  == generateStatement(name + "Data" + joinedTypeParameters + " data", 1);
	}
	else if (type.equals("interface")) {
		char* vTableName = name + "VTable";
		char* functionDeclarations = outer.popFunctionDeclarations().stream().collect(new_Joiner());
		generatedSubStructs == templateString + "struct " + vTableName + " {" + functionDeclarations + System.lineSeparator() + "};" + System.lineSeparator();
		recordFields +  == generateStatement("void* data", 1);
		recordFields +  == generateStatement(vTableName + joinedTypeParameters + " vtable", 1);
	}
	char* generated = generatedSubStructs + templateString + "struct " + name + " {" + recordFields + inner + System.lineSeparator() + "};" + System.lineSeparator();
	ParseState parseState = outer.addBeforeStruct(templateString + "struct " + name + ";" + System.lineSeparator()).addStruct(generated);
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("", parseState));
}
char* compileValues_Main(void* _ref, char* input, Function<char*, char*> mapper){
	return compileValues(input, mapper, ", ");
}
char* compileValues_Main(void* _ref, char* input, Function<char*, char*> mapper, char* delimiter){
	return divide(input, foldValue_Main).map(mapper).collect(new_Joiner(delimiter));
}
auto __lambda14__() {
	return wrap(input1);
}
char* compileParameter_Main(void* _ref, char* input1){
	if (input1.isEmpty()) {
		return "";
	}
	return generateField(input1).orElseGet(__lambda14__);
}
auto __lambda15__(auto content) {
	return generateStatement(content, 1);
}
Option<char*> generateField_Main(void* _ref, char* input){
	return compileDefinition(input).map(generate_Definable).map(__lambda15__);
}
char* generateStatement_Main(void* _ref, char* content, int depth){
	return generateSegment(content + ";", depth);
}
char* generateSegment_Main(void* _ref, char* content, int depth){
	return generateIndent(depth) + content;
}
char* generateIndent_Main(void* _ref, int depth){
	return System.lineSeparator() + "\t".repeat(depth);
}
DivideState foldValue_Main(void* _ref, DivideState state, char next){
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
Tuple<char*, ParseState> compileClassSegment_Main(void* _ref, char* input, char* name, ParseState state){
	char* stripped = input.strip();
	if (stripped.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	return compileClassSegmentValue(stripped, name, state);
}
auto __lambda16__() {
	return compileStructure(input, "record", state);
}
auto __lambda17__() {
	return compileStructure(input, "interface", state);
}
auto __lambda18__() {
	return compileField(input, state);
}
auto __lambda19__() {
	return compileMethod(input, name, state);
}
auto __lambda20__() {
	char* generated = generateSegment(wrap(input), 1);
	return new_Tuple<char*, ParseState>(generated, state);
}
Tuple<char*, ParseState> compileClassSegmentValue_Main(void* _ref, char* input, char* name, ParseState state){
	if (input.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	return compileStructure(input, "class", state).or(__lambda16__).or(__lambda17__).or(__lambda18__).or(__lambda19__).orElseGet(__lambda20__);
}
Option<Tuple<char*, ParseState>> compileMethod_Main(void* _ref, char* input, char* name, ParseState state){
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
	char* inputParamString = withParams.substring(0, paramEnd);
	char* withBraces = withParams.substring(paramEnd + 1).strip();
	ArrayList<Definition> params;
	if (!inputParamString.isEmpty()) {
		params == divide(inputParamString, foldValue_Main).map(compileDefinition_Main).flatMap(fromOption_Streams).collect(new_ListCollector<Definition>());
	}
	else {
		params = new_ArrayList<Definition>();
	}
	char* outputParams = params.addFirst(new_Definition("void*", "_ref")).stream().map(generate_Definition).collect(new_Joiner(", "));
	char* outputMethodHeader = transformMethodHeader(methodHeader, name).generate() + "(" + outputParams + ")";
	if (withBraces.equals(";") || isPlatformDependentMethod(methodHeader)) {
		ParseState withFunctionDeclaration = state.addFunctionDeclaration(generateStatement(outputMethodHeader, 1));
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("", withFunctionDeclaration));
	}
	else if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		char* inputBody = withBraces.substring(1, withBraces.length() - 1);
		Tuple<ArrayList<char*>, ParseState> compiledBody = compileMethodStatements(state, 0, inputBody);
		ArrayList<char*> statements = compiledBody.left;
	??? _temp = Objects.requireNonNull(methodHeader);
		if (_temp.tag == JConstructor) {
		JConstructor _cast = _temp.data.jconstructor;
			ArrayList < String >= stringArrayList == statements.addFirst(generateStatement(name + " this", 1));
			statements == stringArrayList.addLast(generateStatement("return this", 1));
		}
		char* joined = statements.stream().collect(new_Joiner(""));
		char* outputBodyWithBraces = "{" + joined + System.lineSeparator() + "}";
		char* generated = outputMethodHeader + outputBodyWithBraces + System.lineSeparator();
		ParseState parseState = state.addFunction(generated);
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("", parseState));
	}
	else {
		return new_None<Tuple<char*, ParseState>>();
	}
}
boolean isPlatformDependentMethod_Main(void* _ref, JMethodHeader methodHeader){
		Definition definition && definition.annotations.contains _cast = methodHeader.data.definition definition && definition.annotations.contains("actual");
	return methodHeader.tag == Definition definition && definition.annotations.contains("Actual");
}
Definable transformMethodHeader_Main(void* _ref, JMethodHeader methodHeader, char* name){
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
auto __lambda21__(auto definable) {
	return definable;
}
auto __lambda22__() {
	return compileConstructor(beforeParams);
}
auto __lambda23__() {
	return new_Placeholder(beforeParams);
}
JMethodHeader compileMethodHeader_Main(void* _ref, char* beforeParams){
	return compileDefinition(beforeParams). < JMethodHeader >= map(__lambda21__).or(__lambda22__).orElseGet(__lambda23__);
}
Tuple<char*, ParseState> compileMethodSegment_Main(void* _ref, char* input, int depth, ParseState state){
	char* stripped = input.strip();
	if (stripped.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	Tuple<char*, ParseState> tuple = compileMethodSegmentValue(stripped, depth, state);
	return new_Tuple<char*, ParseState>(generateSegment(tuple.left, depth), tuple.right);
}
Tuple<char*, ParseState> compileMethodSegmentValue_Main(void* _ref, char* input, int depth, ParseState state){
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
Option<Tuple<char*, ParseState>> compileConditional_Main(void* _ref, char* type, int depth, ParseState state, char* stripped){
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
Option<Tuple<char*, ParseState>> compileBlock_Main(void* _ref, ParseState state, char* input, int depth){
	if (!input.startsWith("{") ||  != input.endsWith("}")) {
		return new_None<Tuple<char*, ParseState>>();
	}
	Tuple<ArrayList<char*>, ParseState> result = compileMethodStatements(state, depth, input.substring(1, input.length() - 1));
	char* generated = "{" + result.left().stream().collect(new_Joiner("")) + generateIndent(depth) + "}";
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(generated, result.right()));
}
Tuple<ArrayList<char*>, ParseState> compileMethodStatements_Main(void* _ref, ParseState state, int depth, char* content){
	ArrayList<char*> compiled = new_ArrayList<char*>();
	ParseState current = state;
	ArrayList < String >= list == divide(content, foldStatement_Main).collect(new_ListCollector<char*>());
	int i = 0;
	while (i < list.size()) {
		char* s = list.get(i).orElse(null);
		Tuple<char*, ParseState> string = compileMethodSegment(s, depth + 1, current.pushBeforeStatements());
		compiled == compiled.addAll(string.right.popBeforeStatements()).addLast(string.left);
		current = string.right;
		i++;
	}
	ArrayList<char*> removed = current.popAfterStatements();
	compiled == compiled.addAllAt(0, removed).orElse(new_ArrayList<char*>());
	return new_Tuple<ArrayList<char*>, ParseState>(compiled, current);
}
DivideState foldConditionEnd_Main(void* _ref, DivideState state, char c){
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
auto __lambda24__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
auto __lambda25__(auto generated) {
	return new_Tuple<char*, ParseState>(generated, state);
}
auto __lambda26__() {
	return compileExpression(destinationString, state);
}
auto __lambda27__(auto value) {
	return new_Tuple<char*, ParseState>(value.generate(), state);
}
auto __lambda28__() {
	return new_Tuple<char*, ParseState>(wrap(input), state);
}
Tuple<char*, ParseState> compileMethodStatementValue_Main(void* _ref, char* input, ParseState state){
	if (input.startsWith("return ")) {
		char* substring = input.substring("return ".length());
		Tuple<char*, ParseState> result = compileExpression(substring, state);
		return new_Tuple<char*, ParseState>("return " + result.left, result.right);
	}
	if (input.endsWith("++")) {
		char* slice = input.substring(0, input.length() - 2);
		Option<Tuple<char*, ParseState>> temp = tryCompileExpression(slice, state).map(__lambda24__);
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
		Tuple<char*, ParseState> destinationResult = compileDefinition(destinationString).map(generate_Definition).map(__lambda25__).orElseGet(__lambda26__);
		Tuple<char*, ParseState> sourceResult = compileExpression(source, destinationResult.right);
		return new_Tuple<char*, ParseState>(destinationResult.left + " = " + sourceResult.left, sourceResult.right);
	}
	return compileDefinition(input).map(__lambda27__).orElseGet(__lambda28__);
}
auto __lambda29__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
auto __lambda30__() {
	return new_Tuple<char*, ParseState>(wrap(input), state);
}
Tuple<char*, ParseState> compileExpression_Main(void* _ref, char* input, ParseState state){
	return tryCompileExpression(input, state).map(__lambda29__).orElseGet(__lambda30__);
}
auto __lambda31__() {
	return compileIdentifier(stripped, state);
}
auto __lambda32__() {
	return compileNumber(stripped, state).map(wrapInContent_Main);
}
Option<Tuple<CExpression, ParseState>> tryCompileExpression_Main(void* _ref, char* input, ParseState state){
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
	return getOr(state, stripped).map(wrapInContent_Main).or(__lambda31__).or(__lambda32__);
}
auto __lambda33__() {
	return compileOperator(stripped, "-", state);
}
auto __lambda34__() {
	return compileOperator(stripped, ">=", state);
}
auto __lambda35__() {
	return compileOperator(stripped, "<", state);
}
auto __lambda36__() {
	return compileOperator(stripped, "!=", state);
}
auto __lambda37__() {
	return compileOperator(stripped, "==", state);
}
auto __lambda38__() {
	return compileOperator(stripped, "&&", state);
}
auto __lambda39__() {
	return compileOperator(stripped, "||", state);
}
Option<Tuple<char*, ParseState>> getOr_Main(void* _ref, ParseState state, char* stripped){
	return compileOperator(stripped, "+", state).or(__lambda33__).or(__lambda34__).or(__lambda35__).or(__lambda36__).or(__lambda37__).or(__lambda38__).or(__lambda39__);
}
Tuple<CExpression, ParseState> wrapInContent_Main(void* _ref, Tuple<char*, ParseState> tuple){
	return new_Tuple<CExpression, ParseState>(new_Content(tuple.left), tuple.right);
}
Option<Tuple<char*, ParseState>> compileString_Main(void* _ref, char* stripped, ParseState state){
	if (isString(stripped)) {
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(stripped, state));
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda40__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
Option<Tuple<char*, ParseState>> compileFieldAccess_Main(void* _ref, ParseState state, char* stripped){
	int separator = stripped.lastIndexOf(".");
	if (separator < 0) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* substring = stripped.substring(0, separator);
	char* name = stripped.substring(separator + 1).strip();
	if (!isIdentifier(name)) {
		return new_None<Tuple<char*, ParseState>>();
	}
	Option<Tuple<char*, ParseState>> maybeResult = tryCompileExpression(substring, state).map(__lambda40__);
	if (!(maybeResult.tag == Some)) {
		Some<Tuple<String, ParseState>> _cast = maybeResult.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return new_None<Tuple<char*, ParseState>>();
	}
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(value.left + "." + name, value.right));
}
Option<Tuple<char*, ParseState>> compileMethodReference_Main(void* _ref, ParseState state, char* stripped){
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
auto __lambda41__(auto definition) {
	char* generated = definition.generate();
	return generated + " = _cast." + definition.name;
}
auto __lambda42__(auto destructMember) {
	return generateStatement(destructMember, 2);
}
auto __lambda43__(auto slice1) {
	return compileDefinition(slice1).map(__lambda41__).map(__lambda42__).orElse("");
}
Option<Tuple<char*, ParseState>> compileInstanceOf_Main(void* _ref, ParseState state, char* stripped){
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
				result1 == compileValues(paramString, __lambda43__);
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
Option<Tuple<char*, ParseState>> compileChar_Main(void* _ref, char* stripped, ParseState state){
	if (isABoolean(stripped)) {
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(stripped, state));
	}
	return new_None<Tuple<char*, ParseState>>();
}
boolean isABoolean_Main(void* _ref, char* stripped){
	return stripped.startsWith("'") && stripped.endsWith("'") && stripped.length() <  == 4;
}
auto __lambda44__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
Option<Tuple<char*, ParseState>> compileNot_Main(void* _ref, ParseState state, char* stripped){
	if (stripped.startsWith("!")) {
		char* slice = stripped.substring(1);
		Option<Tuple<char*, ParseState>> maybeResult = tryCompileExpression(slice, state).map(__lambda44__);
		if (maybeResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = maybeResult.data.some;
		Tuple<char*, ParseState> value = _cast.value;
			return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("!" + value.left, value.right));
		}
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda45__(auto tuple, auto s) {
	return mergeExpression(tuple.left, tuple.right, s);
}
Option<Tuple<char*, ParseState>> compileInvokable_Main(void* _ref, ParseState state, char* stripped){
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
	Tuple<StringJoiner, ParseState> reduce = divide(arguments, foldValue_Main).collect(new_ListCollector<char*>()).stream().foldWithInitial(new_Tuple<StringJoiner, ParseState>(new_StringJoiner(", "), value.right), __lambda45__);
	char* collect = reduce.left.toString();
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(value.left + "(" + collect + ")", reduce.right));
}
Tuple<StringJoiner, ParseState> mergeExpression_Main(void* _ref, StringJoiner joiner, ParseState state, char* segment){
	Tuple<char*, ParseState> result = compileExpression(segment, state);
	StringJoiner add = joiner.add(result.left);
	return new_Tuple<StringJoiner, ParseState>(add, result.right);
}
auto __lambda46__(auto state, auto c) {
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
Stream<char*> findArgStart_Main(void* _ref, char* input){
	return divide(input, __lambda46__);
}
auto __lambda47__(auto slice) {
	return !slice.isEmpty();
}
auto __lambda48__(auto slice) {
	return "auto " + slice;
}
Option<Tuple<char*, ParseState>> compileLambda_Main(void* _ref, ParseState state, char* stripped){
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
		outputParams == Streams.fromRef(array).map(strip_char*).filter(__lambda47__).map(__lambda48__).collect(new_Joiner(", "));
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
Tuple<char*, ParseState> compileLambdaBody_Main(void* _ref, ParseState state, char* body){
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
auto __lambda49__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
Option<Tuple<char*, ParseState>> compileCaller_Main(void* _ref, ParseState state, char* caller){
	if (caller.startsWith("new ")) {
		Option<char*> newType = compileType(caller.substring("new ".length()));
		if (newType.tag == Some) {
		Some<String> _cast = newType.data.some;
		char* value = _cast.value;
			return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("new_" + value, state));
		}
	}
	return tryCompileExpression(caller, state).map(__lambda49__);
}
Option<Tuple<CExpression, ParseState>> compileIdentifier_Main(void* _ref, char* input, ParseState state){
	if (isIdentifier(input)) {
		return new_Some<Tuple<CExpression, ParseState>>(new_Tuple<CExpression, ParseState>(new_CIdentifier(input), state));
	}
	return new_None<Tuple<CExpression, ParseState>>();
}
Option<Tuple<char*, ParseState>> compileNumber_Main(void* _ref, char* stripped, ParseState state){
	if (isNumber(stripped)) {
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(stripped, state));
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda50__(auto state1, auto next) {
	return foldOperator(operator, state1, next);
}
auto __lambda51__(auto tuple1) {
	return new_Tuple<char*, ParseState>(tuple1.left.generate(), tuple1.right);
}
auto __lambda52__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left.generate(), tuple.right);
}
Option<Tuple<char*, ParseState>> compileOperator_Main(void* _ref, char* input, char* operator, ParseState state){
	ArrayList<char*> segments = divide(input, __lambda50__).collect(new_ListCollector<char*>());
	if (segments.size() < 2) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* left = segments.getFirst().orElse(null);
	char* right = segments.subList(1, segments.size()).orElse(new_ArrayList<char*>()).stream().collect(new_Joiner(operator));
	Option<Tuple<char*, ParseState>> maybeLeftResult = tryCompileExpression(left, state).map(__lambda51__);
	if (!(maybeLeftResult.tag == Some)) {
		Some<Tuple<String, ParseState>> _cast = maybeLeftResult.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return new_None<Tuple<char*, ParseState>>();
	}
	Option<Tuple<char*, ParseState>> maybeRightResult = tryCompileExpression(right, value.right).map(__lambda52__);
	if (maybeRightResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = maybeRightResult.data.some;
		Tuple<char*, ParseState> rightResult = _cast.rightResult;
		char* generated = value.left + " " + operator + " " + rightResult.left;
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(generated, rightResult.right));
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda53__(auto inner) {
	return inner.left;
}
DivideState foldOperator_Main(void* _ref, char* operator, DivideState state1, Character next){
	if (next != operator.charAt(0)) {
		return state1.append(next);
	}
	Option<Character> peeked = state1.peek();
	??? _temp = operator.length() >= 2 && peeked;
	if (_temp.tag == Some) {
		if (value == operator.charAt(1)) {
		Some<Character> _cast = _temp.data.some;
		Character value = _cast.value;
			return state1.pop().map(__lambda53__).orElse(state1).advance();
		}
	}
	return state1.advance();
}
boolean isString_Main(void* _ref, char* stripped){
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
auto __lambda54__(auto i) {
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
boolean areAllDoubleQuotesEscaped_Main(void* _ref, char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda54__);
}
auto __lambda55__(auto i) {
	return Character.isDigit(input.charAt(i));
}
boolean isNumber_Main(void* _ref, char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda55__);
}
auto __lambda56__(auto i) {
	char next = input.charAt(i);
	boolean isValidDigit = i != 0 && Character.isDigit(next);
	return Character.isLetter(next) || isValidDigit;
}
boolean isIdentifier_Main(void* _ref, char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda56__);
}
Option<JMethodHeader> compileConstructor_Main(void* _ref, char* beforeParams){
	int separator = beforeParams.lastIndexOf(" ");
	if (separator < 0) {
		return new_None<JMethodHeader>();
	}
	char* name = beforeParams.substring(separator + " ".length());
	return new_Some<JMethodHeader>(new_JConstructor(name));
}
Option<Tuple<char*, ParseState>> compileField_Main(void* _ref, char* input, ParseState state){
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
auto __lambda57__(auto type) {
	return new_Definition(new_ArrayList<char*>(), type, name);
}
auto __lambda58__(auto type) {
	return new_Definition(annotations, type, name);
}
Option<Definition> compileDefinition_Main(void* _ref, char* input){
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
		return compileType(beforeName).map(__lambda57__);
	}
	char* withoutLast = segments.subList(0, segments.size() - 1).orElse(new_ArrayList<char*>()).stream().collect(new_Joiner(" "));
	ArrayList<char*> annotations = findAnnotations(withoutLast);
	char* typeString = segments.getLast().orElse(null);
	return compileType(typeString).map(__lambda58__);
}
auto __lambda59__(auto slice) {
	return slice.startsWith("@");
}
auto __lambda60__(auto slice) {
	return slice.substring(1);
}
ArrayList<char*> findAnnotations_Main(void* _ref, char* withoutLast){
	int i = withoutLast.lastIndexOf("\n");
	if (i < 0) {
		return new_ArrayList<char*>();
	}
	char** slices = withoutLast.substring(0, i).strip().split(Pattern.quote("\n"));
	return Streams.fromRef(slices).map(strip_char*).filter(__lambda59__).map(__lambda60__).collect(new_ListCollector<char*>());
}
DivideState foldTypeSeparator_Main(void* _ref, DivideState state, Character c){
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
auto __lambda61__(auto result) {
	return result + "*";
}
Option<char*> compileType_Main(void* _ref, char* input){
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
			ArrayList<char*> arguments = divide(argumentsString, foldValue_Main).map(compileTypeOrPlaceholder_Main).collect(new_ListCollector<char*>());
			char* outputArguments = arguments.stream().collect(new_Joiner(", "));
			return new_Some<char*>(base + "<" + outputArguments + ">");
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
auto __lambda62__() {
	return wrap(slice);
}
char* compileTypeOrPlaceholder_Main(void* _ref, char* slice){
	return compileType(slice).orElseGet(__lambda62__);
}
char* wrap_Main(void* _ref, char* input){
	char* replaced = input.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
int main(){
	main_Main();
	return 0;
}