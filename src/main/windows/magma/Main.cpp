// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
#include "Main.h"
char* generate_Definable(void* _ref){
	Definable this = *((Definable*) _ref);
	return this.vtable.apply(this.data);
}
char* generate_CExpression(void* _ref){
	CExpression this = *((CExpression*) _ref);
	return this.vtable.apply(this.data);
}
char* generate_CRootSegment(void* _ref){
	CRootSegment this = *((CRootSegment*) _ref);
	return this.vtable.apply(this.data);
}
ParseState new_ParseState(void* _ref){
	ParseState this;
	this.functions = new_ArrayList<char*>();
	this.rootSegments = new_HashMap<char*, ArrayList<CRootSegment>>();
	this.beforeStatements = new_Stack<ArrayList<char*>>();
	this.beforeStatements.add(new_ArrayList<char*>());
	this.afterStatements = new_ArrayList<char*>();
	this.counter =  - 1;
	this.includes = new_ArrayList<char*>();
	this.beforeStructs = new_ArrayList<char*>();
	this.structFields = new_ArrayList<char*>();
	this.functionDeclarations = new_ArrayList<char*>();
	this.usesBoolean = false;
	this.typeUsages = new_ArrayList<char*>();
	this.maybeCurrentStructName = new_None<char*>();
	this.structDependencies = new_HashMap<char*, ArrayList<char*>>();
	return this;
}
ParseState addFunction_ParseState(void* _ref, char* func){
	this.functions == this.functions.addLast(func);
	return this;
}
ParseState addAllRootSegments_ParseState(void* _ref, char* name, ArrayList<CRootSegment> generated){
	this.rootSegments.put(name, generated);
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
ParseState addStructForwardDeclaration_ParseState(void* _ref, char* beforeStruct){
	this.beforeStructs == this.beforeStructs.addLast(beforeStruct);
	return this;
}
ParseState addStructField_ParseState(void* _ref, char* field){
	this.structFields == this.structFields.addLast(field);
	return this;
}
ArrayList<char*> popStructFields_ParseState(void* _ref){
	ArrayList<char*> copy = this.structFields.copy();
	this.structFields == this.structFields.clear();
	return copy;
}
ParseState addFunctionDeclaration_ParseState(void* _ref, char* functionDeclaration){
	this.functionDeclarations == this.functionDeclarations.addLast(functionDeclaration);
	return this;
}
ParseState toggleBoolean_ParseState(void* _ref){
	this.usesBoolean = true;
	return this;
}
ParseState withStructName_ParseState(void* _ref, char* name){
	this.maybeCurrentStructName = new_Some<char*>(name);
	return this;
}
ParseState addTypeUsage_ParseState(void* _ref, char* identifier){
	if (!this.typeUsages.contains(identifier)) {
		this.typeUsages == this.typeUsages.addLast(identifier);
	}
	return this;
}
ParseState completeStructure_ParseState(void* _ref){
	??? _temp = this.maybeCurrentStructName;
	if (_temp.tag == Some) {
		Some<String> _cast = _temp.data.some;
		char* oldName = _cast.oldName;
		ArrayList<char*> copy = this.typeUsages.copy();
		this.typeUsages = new_ArrayList<char*>();
		this.structDependencies.put(oldName, copy);
	}
	return this;
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
bool isShallow_DivideState(void* _ref){
	return this.depth == 1;
}
bool isLevel_DivideState(void* _ref){
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
	return new_Tuple<DivideState, Character>(tuple.left().append(tuple.right()), tuple.right());
}
Option<Tuple<DivideState, Character>> popAndAppendToTuple_DivideState(void* _ref){
	return this.pop().map(__lambda0__);
}
Option<DivideState> popAndAppendToOption_DivideState(void* _ref){
	return this.popAndAppendToTuple().map(left_Tuple);
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
auto __lambda1__(auto fields) {
	return " {" + fields + System.lineSeparator() + "}";
}
char* generate_CStruct(void* _ref){
	char* s = this.maybeFields.map(__lambda1__).orElse("");
	return generateTemplateString(this.typeParameters) + "struct " + this.name() + s + ";" + System.lineSeparator();
}
auto __lambda2__(auto slice) {
	return generateIndent(1) + slice;
}
char* generate_EnumNode(void* _ref){
	char* enumFields = this.variants().stream().map(__lambda2__).collect(new_Joiner(","));
	return "enum " + this.name() + "Tag {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();
}
char* generate_Union(void* _ref){
	return generateTemplateString(this.typeParameters()) + "union " + this.name() + "Data {" + this.fields() + System.lineSeparator() + "};" + System.lineSeparator();
}
CStruct toStruct_JStructure(void* _ref){
	return new_CStruct(this.typeParameters(), this.name(), new_Some<char*>(this.fields().toString()));
}
CStruct toStructForwardDeclaration_JStructure(void* _ref){
	return new_CStruct(this.typeParameters(), this.name(), new_None<char*>());
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
auto __lambda3__(auto slice) {
	return slice + "^" + System.lineSeparator() + "\t";
}
Result<Path, IOError> writeBuildFile_Main(void* _ref, Ok<ArrayList<ArrayList<Path>>, IOError> v1, Path targetDirectory){
	ArrayList<Path> list = v1.value().stream().flatMap(stream_ArrayList).collect(new_ListCollector<Path>());
	Path path = targetDirectory.resolveByString("build.bat");
	char* joined = list.stream().map(relativize_targetDirectory).map(asString_Path).map(__lambda3__).collect(new_Joiner(" "));
	return /*switch (path.writeString("clang " + joined + " -o magmac.exe")) {
			case None<IOError> _ -> new Ok<Path, IOError>(path);
			case Some<IOError> v -> new Err<Path, IOError>(v.value());
		}*/;
}
Result<ArrayList<ArrayList<Path>>, IOError> runWithSources_Main(void* _ref, ArrayList<Path> sources, Path sourceDirectory, Path targetDirectory){
	return compileSources(sources, sourceDirectory, targetDirectory);
}
auto __lambda4__(auto path) {
	return path.asString().endsWith(".java");
}
auto __lambda5__(auto source) {
	return compileSource(source, sourceDirectory, targetDirectory);
}
Result<ArrayList<ArrayList<Path>>, IOError> compileSources_Main(void* _ref, ArrayList<Path> sources, Path sourceDirectory, Path targetDirectory){
	return sources.stream().filter(__lambda4__).map(__lambda5__).collect(new_ResultCollector<ArrayList<Path>, IOError, ArrayList<ArrayList<Path>>>(new_ListCollector<ArrayList<Path>>()));
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
auto __lambda6__() {
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
	char* headerOutput = prefix + "#ifndef " + defined + System.lineSeparator() + "#define " + defined + System.lineSeparator() + compiled.left() + "#endif";
	char* targetOutput = prefix + "#include \"Main.h\"" + System.lineSeparator() + compiled.right();
	Path header = targetParent.resolveByString(name + ".h");
	Path target = targetParent.resolveByString(name + ".cpp");
	Option<IOError> maybeError = header.writeString(headerOutput).or(__lambda6__);
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
		joiner.add(s.left());
		state == s.right();
		i++;
	}
	char* joined = joiner.toString();
	ParseState withBoolean = attachBoolean(state);
	ParseState current = withBoolean.withStructName("?");
	char* joinedIncludes = current.includes.stream().collect(new_Joiner(""));
	HashMap<char*, ArrayList<char*>> copy = new_HashMap<char*, ArrayList<char*>>(current.structDependencies);
	Map<char*, ArrayList<char*>> adjacency = removeItemFromValueWhenNotPresentInKey(copy);
	ArrayList<char*> structOrder = computeStructOrder(adjacency);
	char* joinedBeforeStructs = current.beforeStructs.stream().collect(new_Joiner(""));
	char* joinedStructs = structOrder.stream().map(get_/*current.rootSegments*/).flatMap(stream_ArrayList).map(generate_CRootSegment).collect(new_Joiner(""));
	char* joinedFunctionDeclarations = current.functionDeclarations.stream().collect(new_Joiner(""));
	char* joinedFunctions = current.functions.stream().collect(new_Joiner(""));
	char* generatedHeaderContent = joinedIncludes + joinedBeforeStructs + joinedStructs + joinedFunctionDeclarations;
	char* generatedSourceContent = joinedFunctions + joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}";
	return new_Tuple<char*, char*>(generatedHeaderContent, generatedSourceContent);
}
ArrayList<char*> computeStructOrder_Main(void* _ref, Map<char*, ArrayList<char*>> adjacencies){
	ArrayList<char*> structOrder = new_ArrayList<char*>();
	while (!adjacencies.isEmpty()) {
		dependencies
			List<char*> structsToRemove = new_LinkedList<char*>();
		/*for (Entry<String, ArrayList<String>> entry : adjacencies.entrySet()) {
				final String structName = entry.getKey();
				final ArrayList<String> adjacentList = entry.getValue();

				if (adjacentList.isEmpty()) {
					structsToRemove.add(structName);
				}
			}*/
		if (structsToRemove.isEmpty()) {
			/*break*/;
		}
		/*// Process the structs with no dependencies
			for (String structName : structsToRemove) {
				structOrder = structOrder.addFirst(structName);
				adjacencies.remove(structName);

				// Remove this struct from all other adjacency lists
				Map<String, ArrayList<String>> updates = new HashMap<String, ArrayList<String>>();
				for (Entry<String, ArrayList<String>> otherEntry : adjacencies.entrySet()) {
					final ArrayList<String> otherAdjacencies = otherEntry.getValue();
					if (otherAdjacencies.contains(structName)) {
						final ArrayList<String> filtered =
								otherAdjacencies.stream().filter(dep -> !dep.equals(structName)).collect(new ListCollector<String>());
						updates.put(otherEntry.getKey(), filtered);
					}
				}
				// Apply updates after iteration
				adjacencies.putAll(updates);
			}*/
	}
	return structOrder;
}
Map<char*, ArrayList<char*>> removeItemFromValueWhenNotPresentInKey_Main(void* _ref, HashMap<char*, ArrayList<char*>> copy){
	Map<char*, ArrayList<char*>> result = new_HashMap<char*, ArrayList<char*>>();
	/*for (Entry<String, ArrayList<String>> entry : copy.entrySet()) {
			final String key = entry.getKey();
			ArrayList<String> values = entry
					.getValue()
					.stream()
					.filter(copy::containsKey)
					.filter(element -> !element.equals(key))
					.collect(new ListCollector<String>());

			result.put(key, values);
		}*/
	return result;
}
ParseState attachBoolean_Main(void* _ref, ParseState state){
	ParseState current;
	if (state.usesBoolean) {
		current == state.addIncludes("#include <stdbool.h>" + System.lineSeparator());
	}
	else {
		current = state;
	}
	return current;
}
Stream<char*> divide_Main(void* _ref, char* input, BiFunction<DivideState, Character, DivideState> folder){
	Tuple<DivideState, Boolean> current = new_Tuple<DivideState, Boolean>(new_DivideState(input), true);
	while (current.right()) {
		current == foldCycle(current.left(), folder);
	}
	return current.left().advance().stream();
}
Tuple<DivideState, Boolean> foldCycle_Main(void* _ref, DivideState state, BiFunction<DivideState, Character, DivideState> folder){
	Option<Tuple<DivideState, Character>> maybeNext = state.pop();
	if (maybeNext.tag == Some) {
		Some<Tuple<DivideState, Character>> _cast = maybeNext.data.some;
		Tuple<DivideState, Character> value = _cast.value;
		return new_Tuple<DivideState, Boolean>(foldEscaped(value.left(), value.right(), folder), true);
	}
	return new_Tuple<DivideState, Boolean>(state, false);
}
auto __lambda7__() {
	return foldDoubleQuotes(state, next);
}
auto __lambda8__() {
	return folder.apply(state, next);
}
DivideState foldEscaped_Main(void* _ref, DivideState state, char next, BiFunction<DivideState, Character, DivideState> folder){
	return foldSingleQuotes(state, next).or(__lambda7__).orElseGet(__lambda8__);
}
Option<DivideState> foldSingleQuotes_Main(void* _ref, DivideState state, char next){
	if (next != '\'') {
		return new_None<DivideState>();
	}
	DivideState appended = state.append(next);
	return appended.popAndAppendToTuple().flatMap(foldEscaped_Main).flatMap(popAndAppendToOption_DivideState);
}
Option<DivideState> foldEscaped_Main(void* _ref, Tuple<DivideState, Character> tuple){
	if (tuple.right() == '\\') {
		return tuple.left().popAndAppendToOption();
	}
	else {
		return new_Some<DivideState>(tuple.left());
	}
}
Option<DivideState> foldDoubleQuotes_Main(void* _ref, DivideState state, char next){
	if (next != '\"') {
		return new_None<DivideState>();
	}
	Tuple<DivideState, Boolean> current = new_Tuple<DivideState, Boolean>(state.append(next), true);
	while (current.right()) {
		current == foldUntilDoubleQuotes(current.left());
	}
	return new_Some<DivideState>(current.left());
}
Tuple<DivideState, Boolean> foldUntilDoubleQuotes_Main(void* _ref, DivideState state){
	Option<Tuple<DivideState, Character>> maybeNext = state.popAndAppendToTuple();
	if (!(maybeNext.tag == Some)) {
		Some<Tuple<DivideState, Character>> _cast = maybeNext.data.some;
		Tuple<DivideState, Character> value = _cast.value;
		return new_Tuple<DivideState, Boolean>(state, false);
	}
	DivideState nextState = value.left();
	char nextChar = value.right();
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
auto __lambda9__(auto string, auto string2) {
	return string + "/" + string2;
}
auto __lambda10__() {
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
			char* folded = segments.stream().foldWithInitial(joined, __lambda9__);
			newState == state.addIncludes("#include \"" + folded + ".h\"" + System.lineSeparator());
		}
		return new_Tuple<char*, ParseState>("", newState);
	}
	return compileStructure(stripped, "class", state).orElseGet(__lambda10__);
}
Option<Tuple<char*, ParseState>> compileStructure_Main(void* _ref, char* input, char* type, ParseState state){
	Option<Tuple<JStructure, ParseState>> maybeStructure = parseStructure(input, type, state);
	if (maybeStructure.tag == Some) {
		Option<ParseState> maybeCompleted = completeStructure(tuple.left(), tuple.right());
		if (maybeCompleted.tag == Some) {
		Some<Tuple<JStructure, ParseState>> _cast = maybeStructure.data.some;
		Tuple<JStructure, ParseState> tuple = _cast.tuple;
		Some<ParseState> _cast = maybeCompleted.data.some;
		ParseState completed = _cast.completed;
			return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("", completed));
		}
		else {
			return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("", tuple.right()));
		}
	}
	return new_None<Tuple<char*, ParseState>>();
}
Option<Tuple<JStructure, ParseState>> parseStructure_Main(void* _ref, char* input, char* type, ParseState state){
	int keywordIndex = input.indexOf(type + " ");
	if (keywordIndex < 0) {
		return new_None<Tuple<JStructure, ParseState>>();
	}
	ArrayList<char*> annotations = findAnnotations(input.substring(0, keywordIndex));
	char* afterKeyword = input.substring(keywordIndex + (type + " ").length());
	int contentStart = afterKeyword.indexOf("{");
	if (contentStart < 0) {
		return new_None<Tuple<JStructure, ParseState>>();
	}
	char* beforeContent = afterKeyword.substring(0, contentStart).strip();
	char* withoutPermits = beforeContent;
	ArrayList<char*> variants = new_ArrayList<char*>();
	int permitsIndex = beforeContent.indexOf("permits");
	if (permitsIndex >= 0) {
		char* slice = beforeContent.substring(permitsIndex + "permits".length());
		variants == splitVariants(slice);
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
	StringBuilder recordFields = new_StringBuilder();
	if (maybeWithImplements.endsWith(")")) {
		char* slice = maybeWithImplements.substring(0, maybeWithImplements.length() - 1);
		int beforeParams = slice.indexOf("(");
		if (beforeParams >= 0) {
			beforeMaybeParams == slice.substring(0, beforeParams).strip();
			char* substring = slice.substring(beforeParams + 1);
			ArrayList<char*> parameters = divide(substring, foldValue_Main).collect(new_ListCollector<char*>());
			ParseState current = state;
			/*for (int i = 0; i < parameters.size(); i++) {
					if (parameters.get(i) instanceof Some<String>(String parameter)) {
						final Tuple<String, ParseState> tuple = compileParameter(parameter, current);
						recordFields.append(tuple.left());
						current = tuple.right();
					}
				}*/
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
	if (!isIdentifier(name)) {
		return new_None<Tuple<JStructure, ParseState>>();
	}
	char* afterContent = afterKeyword.substring(contentStart + "{".length()).strip();
	if (!afterContent.endsWith("}")) {
		return new_None<Tuple<JStructure, ParseState>>();
	}
	char* content = afterContent.substring(0, afterContent.length() - "}".length());
	Tuple<char*, ParseState> parseState1 = compileStructureSegments(content, name, state, typeParameters);
	ParseState outer = parseState1.right().withStructName(name);
	recordFields.append(parseState1.left());
	JStructure jStructure = new_JStructure(annotations, type, name, typeParameters, variants, recordFields);
	return new_Some<Tuple<JStructure, ParseState>>(new_Tuple<JStructure, ParseState>(jStructure, outer));
}
Option<ParseState> completeStructure_Main(void* _ref, JStructure structure, ParseState state){
	if (structure.annotations.contains("Actual")) {
		return new_None<ParseState>();
	}
	CStruct struct = structure.toStruct();
	ArrayList < CRootSegment >= emittedRootSegments == pullOutDependentTypes(structure, state).addLast(struct);
	ParseState registerVariantsAsTypeUsages = structure.variants().stream().foldWithInitial(state, addTypeUsage_ParseState);
	CStruct structForwardDeclaration = structure.toStructForwardDeclaration();
	ParseState registered = registerVariantsAsTypeUsages.completeStructure().addStructForwardDeclaration(structForwardDeclaration.generate()).addAllRootSegments(structForwardDeclaration.name, emittedRootSegments);
	return new_Some<ParseState>(registered);
}
auto __lambda11__(auto variant) {
	return variant + "Type";
}
ArrayList<CRootSegment> pullOutDependentTypes_Main(void* _ref, JStructure JStructure, ParseState state){
	StringBuilder fields = JStructure.fields();
	char* joinedTypeParameters = joinTypeParameters(JStructure.typeParameters());
	if (!JStructure.variants().isEmpty()) {
		char* unionFields = joinUnionFields(JStructure.variants(), joinedTypeParameters);
		ArrayList<char*> collect = JStructure.variants().stream().map(__lambda11__).collect(new_ListCollector<char*>());
		fields.append(generateStatement(JStructure.name() + "Tag tag", 1));
		fields.append(generateStatement(JStructure.name() + "Data" + joinedTypeParameters + " data", 1));
		return new_/*ArrayList<CRootSegment>()
					.addLast(new EnumNode(JStructure.name(), collect))
					.addLast*/(new_Union(JStructure.typeParameters(), JStructure.name(), unionFields));
	}
	if (JStructure.type().equals("interface")) {
		char* vTableName = JStructure.name() + "VTable";
		char* functionDeclarations = state.popStructFields().stream().collect(new_Joiner());
		fields.append(generateStatement("void* data", 1));
		fields.append(generateStatement(vTableName + joinedTypeParameters + " vtable", 1));
		return new_/*ArrayList<CRootSegment>().addLast*/(new_CStruct(JStructure.typeParameters(), vTableName, new_Some<char*>(functionDeclarations)));
	}
	return new_ArrayList<CRootSegment>();
}
auto __lambda12__(auto slice) {
	return slice + joinedTypeParameters + " " + slice.toLowerCase();
}
auto __lambda13__(auto content1) {
	return generateStatement(content1, 1);
}
char* joinUnionFields_Main(void* _ref, ArrayList<char*> variants, char* joinedTypeParameters){
	return variants.stream().map(__lambda12__).map(__lambda13__).collect(new_Joiner());
}
char* joinTypeParameters_Main(void* _ref, ArrayList<char*> typeParameters){
	char* joinedTypeParameters;
	if (typeParameters.isEmpty()) {
		joinedTypeParameters = "";
	}
	else {
		joinedTypeParameters = "<" + typeParameters.stream().collect(new_Joiner(", ")) + ">";
	}
	return joinedTypeParameters;
}
Tuple<char*, ParseState> compileStructureSegments_Main(void* _ref, char* content, char* name, ParseState state, ArrayList<char*> typeParameters){
	ArrayList<char*> segments = divide(content, foldStatement_Main).collect(new_ListCollector<char*>());
	StringBuilder inner = new_StringBuilder();
	int j = 0;
	while (j < segments.size()) {
		char* segment = segments.get(j).orElse(null);
		Tuple<char*, ParseState> compiled = compileClassSegment(segment, name, state, typeParameters);
		inner.append(compiled.left());
		state == compiled.right();
		j++;
	}
	return new_Tuple<char*, ParseState>(inner.toString(), state);
}
auto __lambda14__(auto segment) {
	return !segment.isEmpty();
}
auto __lambda15__(auto variant) {
	return variant;
}
ArrayList<char*> splitVariants_Main(void* _ref, char* slice){
	return divide(slice, foldValue_Main).map(strip_char*).filter(__lambda14__).map(__lambda15__).collect(new_ListCollector<char*>());
}
char* generateTemplateString_Main(void* _ref, ArrayList<char*> typeParameters){
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
	return templateString;
}
char* compileValues_Main(void* _ref, char* input, Function<char*, char*> mapper){
	return divide(input, foldValue_Main).map(mapper).collect(new_Joiner(", "));
}
auto __lambda16__() {
	return new_Tuple<char*, ParseState>(wrap(input1), state);
}
Tuple<char*, ParseState> compileParameter_Main(void* _ref, char* input1, ParseState state){
	if (input1.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	return generateField(input1, state).orElseGet(__lambda16__);
}
auto __lambda17__(auto content) {
	return generateStatement(content, 1);
}
Option<Tuple<char*, ParseState>> generateField_Main(void* _ref, char* input, ParseState state){
	return compileDefinition(input, state).map(Tuple.mapLeft(generate_Definable)).map(Tuple.mapLeft(__lambda17__));
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
Tuple<char*, ParseState> compileClassSegment_Main(void* _ref, char* input, char* name, ParseState state, ArrayList<char*> typeParams){
	char* stripped = input.strip();
	if (stripped.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	return compileClassSegmentValue(stripped, name, state, typeParams);
}
auto __lambda18__() {
	return compileStructure(input, "record", state);
}
auto __lambda19__() {
	return compileStructure(input, "interface", state);
}
auto __lambda20__() {
	return compileField(input, state);
}
auto __lambda21__() {
	return compileMethod(input, name, state, typeParams);
}
auto __lambda22__() {
	char* generated = generateSegment(wrap(input), 1);
	return new_Tuple<char*, ParseState>(generated, state);
}
Tuple<char*, ParseState> compileClassSegmentValue_Main(void* _ref, char* input, char* name, ParseState state, ArrayList<char*> typeParams){
	if (input.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	return compileStructure(input, "class", state).or(__lambda18__).or(__lambda19__).or(__lambda20__).or(__lambda21__).orElseGet(__lambda22__);
}
Option<Tuple<char*, ParseState>> compileMethod_Main(void* _ref, char* input, char* structName, ParseState state, ArrayList<char*> typeParams){
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
	JMethodHeader methodHeader = compileMethodHeader(beforeParams, state).left();
	char* inputParamString = withParams.substring(0, paramEnd);
	char* withBraces = withParams.substring(paramEnd + 1).strip();
	ArrayList<Definition> params = new_ArrayList<Definition>();
	if (!inputParamString.isEmpty()) {
		ArrayList<char*> segments = divide(inputParamString, foldValue_Main).collect(new_ListCollector<char*>());
		ParseState current = state;
		/*for (int i = 0; i < segments.size(); i++) {
				if (segments.get(i) instanceof Some<String>(String segment)) {
					final Option<Tuple<Definition, ParseState>> maybeDefinition = compileDefinition(segment, current);
					if (maybeDefinition instanceof Some<Tuple<Definition, ParseState>>(Tuple<Definition, ParseState> tuple)) {
						params = params.addLast(tuple.left());
						current = tuple.right();
					}
				}
			}*/
	}
	char* templateString = generateTemplateString(typeParams);
	ArrayList<Definition> outputParams = params.addFirst(new_Definition("void*", "_ref"));
	char* joinedOutputParams = outputParams.stream().map(generate_Definition).collect(new_Joiner(", "));
	char* field = /* switch (methodHeader) {
			case Definable definable -> switch (definable) {
				case Definition definition -> definition.type + " (*" + definition.name + ")";
				case Placeholder placeholder -> placeholder.generate();
			};
			case JConstructor _ -> "start Constructors not allowed as interface methods end";
		}*/;
	char* outputParamsString = "(" + joinedOutputParams + ")";
	char* outputMethodHeader = templateString + transformMethodHeader(methodHeader, structName).generate() + outputParamsString;
	if (withBraces.equals(";") || isPlatformDependentMethod(methodHeader)) {
		char* joinedTypes = outputParams.stream().map(type_Definition).collect(new_Joiner(", "));
		char* functionDeclaration = generateStatement(field + "(" + joinedTypes + ")", 1);
		ArrayList<char*> paramNames = params.stream().map(name_Definition).collect(new_ListCollector<char*>());
		ArrayList<char*> stringArrayList = paramNames.subList(1, paramNames.size()).orElse(new_ArrayList<char*>()).addFirst("this.data");
		char* joinedArgs = stringArrayList.stream().collect(new_Joiner(", "));
		ParseState withFunctionDeclaration = state.addStructField(functionDeclaration).addFunctionDeclaration(outputMethodHeader + ";" + System.lineSeparator()).addFunction(/*
							outputMethodHeader + "{" + generateStatement(structName + " this = *((" + structName + "*) _ref)", 1) +
							generateStatement("return this.vtable.apply(" + joinedArgs + ")", 1) + System.lineSeparator() + "}" +
							System.lineSeparator()*/);
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("", withFunctionDeclaration));
	}
	else if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		char* inputBody = withBraces.substring(1, withBraces.length() - 1);
		Tuple<ArrayList<char*>, ParseState> compiledBody = compileMethodStatements(state, 0, inputBody);
		ArrayList < String >= statements == compiledBody.left();
	??? _temp = Objects.requireNonNull(methodHeader);
		if (_temp.tag == JConstructor) {
		JConstructor _cast = _temp.data.jconstructor;
			ArrayList < String >= stringArrayList == statements.addFirst(generateStatement(structName + " this", 1));
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
bool isPlatformDependentMethod_Main(void* _ref, JMethodHeader methodHeader){
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
auto __lambda23__() {
	return compileConstructor(beforeParams, state);
}
auto __lambda24__() {
	return new_Tuple<JMethodHeader, ParseState>(new_Placeholder(beforeParams), state);
}
Tuple<JMethodHeader, ParseState> compileMethodHeader_Main(void* _ref, char* beforeParams, ParseState state){
	return compileDefinition(beforeParams, state).map(/*Tuple.<Definition, ParseState, JMethodHeader>mapLeft(value -> value)*/).or(__lambda23__).orElseGet(__lambda24__);
}
Tuple<char*, ParseState> compileMethodSegment_Main(void* _ref, char* input, int depth, ParseState state){
	char* stripped = input.strip();
	if (stripped.isEmpty()) {
		return new_Tuple<char*, ParseState>("", state);
	}
	Tuple<char*, ParseState> tuple = compileMethodSegmentValue(stripped, depth, state);
	return new_Tuple<char*, ParseState>(generateSegment(tuple.left(), depth), tuple.right());
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
		return new_Tuple<char*, ParseState>("else " + result.left(), result.right());
	}
	if (stripped.endsWith(";")) {
		char* slice = stripped.substring(0, stripped.length() - 1);
		Tuple<char*, ParseState> result = compileMethodStatementValue(slice, state);
		return new_Tuple<char*, ParseState>(result.left() + ";", result.right());
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
	Tuple<char*, ParseState> compiledBody = compileMethodSegmentValue(body, depth, condition.right());
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(type + " (" + condition.left() + ") " + compiledBody.left(), compiledBody.right()));
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
		compiled == compiled.addAllLast(string.right().popBeforeStatements()).addLast(string.left());
		current == string.right();
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
auto __lambda25__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left().generate(), tuple.right());
}
auto __lambda26__(auto generated) {
	return new_Tuple<char*, ParseState>(generated.left().generate(), generated.right());
}
auto __lambda27__() {
	return compileExpression(destinationString, state);
}
auto __lambda28__(auto value) {
	return new_Tuple<char*, ParseState>(value.left().generate(), value.right());
}
auto __lambda29__() {
	return new_Tuple<char*, ParseState>(wrap(input), state);
}
Tuple<char*, ParseState> compileMethodStatementValue_Main(void* _ref, char* input, ParseState state){
	if (input.startsWith("return ")) {
		char* substring = input.substring("return ".length());
		Tuple<char*, ParseState> result = compileExpression(substring, state);
		return new_Tuple<char*, ParseState>("return " + result.left(), result.right());
	}
	if (input.endsWith("++")) {
		char* slice = input.substring(0, input.length() - 2);
		Option<Tuple<char*, ParseState>> temp = tryCompileExpression(slice, state).map(__lambda25__);
		if (temp.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = temp.data.some;
		Tuple<char*, ParseState> value = _cast.value;
			return new_Tuple<char*, ParseState>(value.left() + "++", value.right());
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
		Tuple<char*, ParseState> destinationResult = compileDefinition(destinationString, state).map(__lambda26__).orElseGet(__lambda27__);
		Tuple<char*, ParseState> sourceResult = compileExpression(source, destinationResult.right());
		return new_Tuple<char*, ParseState>(destinationResult.left() + " = " + sourceResult.left(), sourceResult.right());
	}
	return compileDefinition(input, state).map(__lambda28__).orElseGet(__lambda29__);
}
auto __lambda30__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left().generate(), tuple.right());
}
auto __lambda31__() {
	return new_Tuple<char*, ParseState>(wrap(input), state);
}
Tuple<char*, ParseState> compileExpression_Main(void* _ref, char* input, ParseState state){
	return tryCompileExpression(input, state).map(__lambda30__).orElseGet(__lambda31__);
}
auto __lambda32__() {
	return compileIdentifier(stripped, state);
}
auto __lambda33__() {
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
	return getOr(state, stripped).map(wrapInContent_Main).or(__lambda32__).or(__lambda33__);
}
auto __lambda34__() {
	return compileOperator(stripped, "-", state);
}
auto __lambda35__() {
	return compileOperator(stripped, ">=", state);
}
auto __lambda36__() {
	return compileOperator(stripped, "<", state);
}
auto __lambda37__() {
	return compileOperator(stripped, "!=", state);
}
auto __lambda38__() {
	return compileOperator(stripped, "==", state);
}
auto __lambda39__() {
	return compileOperator(stripped, "&&", state);
}
auto __lambda40__() {
	return compileOperator(stripped, "||", state);
}
Option<Tuple<char*, ParseState>> getOr_Main(void* _ref, ParseState state, char* stripped){
	return compileOperator(stripped, "+", state).or(__lambda34__).or(__lambda35__).or(__lambda36__).or(__lambda37__).or(__lambda38__).or(__lambda39__).or(__lambda40__);
}
Tuple<CExpression, ParseState> wrapInContent_Main(void* _ref, Tuple<char*, ParseState> tuple){
	return new_Tuple<CExpression, ParseState>(new_Content(tuple.left()), tuple.right());
}
Option<Tuple<char*, ParseState>> compileString_Main(void* _ref, char* stripped, ParseState state){
	if (isString(stripped)) {
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(stripped, state));
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda41__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left().generate(), tuple.right());
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
	Option<Tuple<char*, ParseState>> maybeResult = tryCompileExpression(substring, state).map(__lambda41__);
	if (!(maybeResult.tag == Some)) {
		Some<Tuple<String, ParseState>> _cast = maybeResult.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return new_None<Tuple<char*, ParseState>>();
	}
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(value.left() + "." + name, value.right()));
}
Option<Tuple<char*, ParseState>> compileMethodReference_Main(void* _ref, ParseState state, char* stripped){
	int separator = stripped.lastIndexOf("::");
	if (separator >= 0) {
		char* substring = stripped.substring(0, separator);
		char* name = stripped.substring(separator + 2).strip();
		if (isIdentifier(name)) {
			Option<Tuple<char*, ParseState>> maybeResult = compileType(state, substring);
			if (maybeResult.tag == Some(Tuple) {
		Some _cast = maybeResult.data.some(tuple;
		Tuple<char*, ParseState> value = _cast.value;
				return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(name + "_" + value.left(), value.right()));
			}
		}
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda42__(auto result) {
	Definition definition = result.left();
	char* generated = definition.generate();
	return generated + " = _cast." + definition.name;
}
auto __lambda43__(auto destructMember) {
	return generateStatement(destructMember, 2);
}
auto __lambda44__(auto slice1) {
	return compileDefinition(slice1, state).map(__lambda42__).map(__lambda43__).orElse("");
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
				result1 == compileValues(paramString, __lambda44__);
			}
			parameters = result1;
			afterOperator == afterOperator.substring(0, paramStart);
		}
	}
	CExpression target = value.left();
	ParseState maybeWithBeforeStatement = value.right();
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
bool isABoolean_Main(void* _ref, char* stripped){
	return stripped.startsWith("'") && stripped.endsWith("'") && stripped.length() <  == 4;
}
auto __lambda45__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left().generate(), tuple.right());
}
Option<Tuple<char*, ParseState>> compileNot_Main(void* _ref, ParseState state, char* stripped){
	if (stripped.startsWith("!")) {
		char* slice = stripped.substring(1);
		Option<Tuple<char*, ParseState>> maybeResult = tryCompileExpression(slice, state).map(__lambda45__);
		if (maybeResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = maybeResult.data.some;
		Tuple<char*, ParseState> value = _cast.value;
			return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("!" + value.left(), value.right()));
		}
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda46__(auto tuple, auto s) {
	return mergeExpression(tuple.left(), tuple.right(), s);
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
	Tuple<StringJoiner, ParseState> reduce = divide(arguments, foldValue_Main).collect(new_ListCollector<char*>()).stream().foldWithInitial(new_Tuple<StringJoiner, ParseState>(new_StringJoiner(", "), value.right()), __lambda46__);
	char* collect = reduce.left().toString();
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(value.left() + "(" + collect + ")", reduce.right()));
}
Tuple<StringJoiner, ParseState> mergeExpression_Main(void* _ref, StringJoiner joiner, ParseState state, char* segment){
	Tuple<char*, ParseState> result = compileExpression(segment, state);
	StringJoiner add = joiner.add(result.left());
	return new_Tuple<StringJoiner, ParseState>(add, result.right());
}
auto __lambda47__(auto state, auto c) {
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
	return divide(input, __lambda47__);
}
auto __lambda48__(auto slice) {
	return !slice.isEmpty();
}
auto __lambda49__(auto slice) {
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
		outputParams == Streams.fromRef(array).map(strip_char*).filter(__lambda48__).map(__lambda49__).collect(new_Joiner(", "));
	}
	else {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* body = stripped.substring(i1 + 2).strip();
	Tuple<char*, ParseState> bodyResult = compileLambdaBody(state, body);
	char* generatedName = bodyResult.right().generateAnonymousFunctionName();
	char* s1 = "auto " + generatedName + "(" + outputParams + ") " + bodyResult.left() + System.lineSeparator();
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(generatedName, bodyResult.right().addFunction(s1)));
}
Tuple<char*, ParseState> compileLambdaBody_Main(void* _ref, ParseState state, char* body){
	Option<Tuple<char*, ParseState>> maybeBlock = compileBlock(state, body, 0);
	if (maybeBlock.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = maybeBlock.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return value;
	}
	Tuple<char*, ParseState> result = compileExpression(body, state);
	char* s = generateStatement("return " + result.left(), 1);
	char* s2 = "{" + s + generateIndent(0) + "}";
	return new_Tuple<char*, ParseState>(s2, result.right());
}
auto __lambda50__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left().generate(), tuple.right());
}
Option<Tuple<char*, ParseState>> compileCaller_Main(void* _ref, ParseState state, char* caller){
	if (caller.startsWith("new ")) {
		Option<Tuple<char*, ParseState>> newType = compileType(state, caller.substring("new ".length()));
		if (newType.tag == Some(Tuple) {
		Some _cast = newType.data.some(tuple;
		Tuple<char*, ParseState> value = _cast.value;
			return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>("new_" + value.left(), value.right()));
		}
	}
	return tryCompileExpression(caller, state).map(__lambda50__);
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
auto __lambda51__(auto state1, auto next) {
	return foldOperator(operator, state1, next);
}
auto __lambda52__(auto tuple1) {
	return new_Tuple<char*, ParseState>(tuple1.left().generate(), tuple1.right());
}
auto __lambda53__(auto tuple) {
	return new_Tuple<char*, ParseState>(tuple.left().generate(), tuple.right());
}
Option<Tuple<char*, ParseState>> compileOperator_Main(void* _ref, char* input, char* operator, ParseState state){
	ArrayList<char*> segments = divide(input, __lambda51__).collect(new_ListCollector<char*>());
	if (segments.size() < 2) {
		return new_None<Tuple<char*, ParseState>>();
	}
	char* left = segments.getFirst().orElse(null);
	char* right = segments.subList(1, segments.size()).orElse(new_ArrayList<char*>()).stream().collect(new_Joiner(operator));
	Option<Tuple<char*, ParseState>> maybeLeftResult = tryCompileExpression(left, state).map(__lambda52__);
	if (!(maybeLeftResult.tag == Some)) {
		Some<Tuple<String, ParseState>> _cast = maybeLeftResult.data.some;
		Tuple<char*, ParseState> value = _cast.value;
		return new_None<Tuple<char*, ParseState>>();
	}
	Option<Tuple<char*, ParseState>> maybeRightResult = tryCompileExpression(right, value.right()).map(__lambda53__);
	if (maybeRightResult.tag == Some) {
		Some<Tuple<String, ParseState>> _cast = maybeRightResult.data.some;
		Tuple<char*, ParseState> rightResult = _cast.rightResult;
		char* generated = value.left() + " " + operator + " " + rightResult.left();
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(generated, rightResult.right()));
	}
	return new_None<Tuple<char*, ParseState>>();
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
			return state1.pop().map(left_Tuple).orElse(state1).advance();
		}
	}
	return state1.advance();
}
bool isString_Main(void* _ref, char* stripped){
	if (stripped.length() < 2) {
		return false;
	}
	bool hasDoubleQuotes = stripped.startsWith("\"") && stripped.endsWith("\"");
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
bool areAllDoubleQuotesEscaped_Main(void* _ref, char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda54__);
}
auto __lambda55__(auto i) {
	return Character.isDigit(input.charAt(i));
}
bool isNumber_Main(void* _ref, char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda55__);
}
auto __lambda56__(auto i) {
	char next = input.charAt(i);
	bool isValidDigit = i != 0 && Character.isDigit(next);
	return Character.isLetter(next) || isValidDigit;
}
bool isIdentifier_Main(void* _ref, char* input){
	return IntStream.range(0, input.length()).allMatch(__lambda56__);
}
Option<Tuple<JMethodHeader, ParseState>> compileConstructor_Main(void* _ref, char* beforeParams, ParseState state){
	int separator = beforeParams.lastIndexOf(" ");
	if (separator < 0) {
		return new_None<Tuple<JMethodHeader, ParseState>>();
	}
	char* name = beforeParams.substring(separator + " ".length());
	return new_Some<Tuple<JMethodHeader, ParseState>>(new_Tuple<JMethodHeader, ParseState>(new_JConstructor(name), state));
}
Option<Tuple<char*, ParseState>> compileField_Main(void* _ref, char* input, ParseState state){
	if (input.endsWith(";")) {
		char* substring = input.substring(0, input.length() - ";".length()).strip();
		Option<Tuple<char*, ParseState>> s = generateField(substring, state);
		if (s.tag == Some(Tuple) {
		Some _cast = s.data.some(tuple;
		Tuple<char*, ParseState> value = _cast.value;
			return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(value.left(), value.right()));
		}
	}
	return new_None<Tuple<char*, ParseState>>();
}
auto __lambda57__(auto type) {
	return new_Tuple<Definition, ParseState>(new_Definition(new_ArrayList<char*>(), type.left(), name), type.right());
}
auto __lambda58__(auto type) {
	return new_Tuple<Definition, ParseState>(new_Definition(annotations, type.left(), name), type.right());
}
Option<Tuple<Definition, ParseState>> compileDefinition_Main(void* _ref, char* input, ParseState state){
	char* stripped = input.strip();
	int index = stripped.lastIndexOf(" ");
	if (index < 0) {
		return new_None<Tuple<Definition, ParseState>>();
	}
	char* beforeName = stripped.substring(0, index).strip();
	char* name = stripped.substring(index + " ".length()).strip();
	if (!isIdentifier(name)) {
		return new_None<Tuple<Definition, ParseState>>();
	}
	ArrayList<char*> segments = divide(beforeName, foldTypeSeparator_Main).collect(new_ListCollector<char*>());
	if (segments.size() < 2) {
		return compileType(state, beforeName).map(__lambda57__);
	}
	char* beforeType = segments.subList(0, segments.size() - 1).orElse(new_ArrayList<char*>()).stream().collect(new_Joiner(" "));
	ArrayList<char*> annotations = findAnnotations(beforeType);
	char* typeString = segments.getLast().orElse(null);
	return compileType(state, typeString).map(__lambda58__);
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
Option<Tuple<char*, ParseState>> compileType_Main(void* _ref, ParseState state, char* input){
	char* stripped = input.strip();
	/*switch (stripped) {
			case "int" -> {
				return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>("int", state));
			}
			case "String" -> {
				return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>("char*", state));
			}
			case "boolean" -> {
				return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>("bool", state.toggleBoolean()));
			}
			case "public" -> {
				return new None<Tuple<String, ParseState>>();
			}
		}*/
	if (stripped.endsWith(">")) {
		char* withoutEnd = stripped.substring(0, stripped.length() - 1);
		int argumentStart = withoutEnd.indexOf("<");
		if (argumentStart >= 0) {
			char* base = withoutEnd.substring(0, argumentStart);
			char* argumentsString = withoutEnd.substring(argumentStart + "<".length());
			ArrayList<char*> segments = divide(argumentsString, foldValue_Main).collect(new_ListCollector<char*>());
			ArrayList<char*> arguments = new_ArrayList<char*>();
			ParseState current = state;
			/*for (int i = 0; i < segments.size(); i++) {
					if (segments.get(i) instanceof Some<String>(String impl)) {
						final Tuple<String, ParseState> tuple = compileTypeOrPlaceholder(impl, current);
						arguments = arguments.addLast(tuple.left());
						current = tuple.right();
					}
				}*/
			char* outputArguments = arguments.stream().collect(new_Joiner(", "));
			return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(base + "<" + outputArguments + ">", current.addTypeUsage(base)));
		}
	}
	if (stripped.endsWith("[]")) {
		char* slice = stripped.substring(0, stripped.length() - 2);
		return compileType(state, slice).map(Tuple.mapLeft(__lambda61__));
	}
	if (isIdentifier(stripped)) {
		return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(stripped, state.addTypeUsage((stripped))));
	}
	return new_Some<Tuple<char*, ParseState>>(new_Tuple<char*, ParseState>(wrap(stripped), state));
}
auto __lambda62__() {
	return new_Tuple<char*, ParseState>(wrap(slice), state);
}
Tuple<char*, ParseState> compileTypeOrPlaceholder_Main(void* _ref, char* slice, ParseState state){
	return compileType(state, slice).orElseGet(__lambda62__);
}
char* wrap_Main(void* _ref, char* input){
	char* replaced = input.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
int main(){
	main_Main();
	return 0;
}