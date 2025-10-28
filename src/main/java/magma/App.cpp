struct App;
struct CPPPrimitiveType;
template <typename T, typename X>
struct Result;
struct CPPType;
struct CFunctionHeader;
template <typename T>
struct ArrayList;
template <typename T, typename X>
struct Err;
template <typename T, typename X>
struct Ok;
struct CPointerType;
struct CTemplateType;
struct CIdentifier;
struct Placeholder;
template <typename A, typename B>
struct Tuple;
struct State;
struct CDefinition;
/*
*/struct CPPPrimitiveType {

	char* content;};
template <typename T>
struct ArrayList {
	List<T> inner;
};
template <typename T, typename X>
struct Err {
	X error;
};
template <typename T, typename X>
struct Ok {
	T value;
};
struct CPointerType {
	CPPType type;
};
struct CTemplateType {
	char* base;
	ArrayList<CPPType> list;
};
struct CIdentifier {
	char* input;
};
struct Placeholder {
	char* input;
};
template <typename A, typename B>
struct Tuple {
	A left;
	B right;
};
struct State {

	char* input;
	ArrayList<char*> segments;
	StringBuilder buffer;
	int depth;
	int index;};
struct CDefinition {
	CPPType cppType;
	char* name;
};
struct App {

	Stack<char*> structureNames;
	ArrayList<char*> globals;
	ArrayList<char*> forwardDeclarations;
	ArrayList<char*> structures;
	ArrayList<char*> sealedStructures;
	ArrayList<char*> functions;
	int counter;
	int depth;};
enum ResultTag {
	ErrTag,
	OkTag
};
template <typename T, typename X>
union ResultData {
	Err<T, X> err;
	Ok<T, X> ok;
};
template <typename T, typename X>
struct Result {
	ResultTag tag;
	ResultData<T, X> data;
};
enum CPPTypeTag {
	CIdentifierTag,
	CPPPrimitiveTypeTag,
	CPointerTypeTag,
	CTemplateTypeTag,
	PlaceholderTag
};
union CPPTypeData {
	CIdentifier cidentifier;
	CPPPrimitiveType cppprimitivetype;
	CPointerType cpointertype;
	CTemplateType ctemplatetype;
	Placeholder placeholder;
};
struct CPPType {
	CPPTypeTag tag;
	CPPTypeData data;
};
enum CFunctionHeaderTag {
	CDefinitionTag,
	PlaceholderTag
};
union CFunctionHeaderData {
	CDefinition cdefinition;
	Placeholder placeholder;
};
struct CFunctionHeader {
	CFunctionHeaderTag tag;
	CFunctionHeaderData data;
};
CPPPrimitiveType VoidValue = CPPPrimitiveType { "void" };
CPPPrimitiveType CharValue = CPPPrimitiveType { "char" };
CPPPrimitiveType IntValue = CPPPrimitiveType { "int" };
CPPType toCPPType_CPPPrimitiveType(void* _ref){
	CPPPrimitiveType _this = *((CPPPrimitiveType*) _ref);
	CPPTypeData data;
	data.cppprimitivetype = _this;
	return CPPType { CPPPrimitiveTypeTag, data };
}
CPPPrimitiveType new_CPPPrimitiveType(void* _ref, char* content) {
	CPPPrimitiveType _this = *((CPPPrimitiveType*) _ref);
	_this.content = content;
}
char* generate_CPPPrimitiveType(void* _ref) {
	CPPPrimitiveType _this = *((CPPPrimitiveType*) _ref);
	return _this.content;
}
char* getSimpleName_CPPPrimitiveType(void* _ref) {
	CPPPrimitiveType _this = *((CPPPrimitiveType*) _ref);
	return _this.content;
}
char* generate_CPPType(void* _ref);
char* getSimpleName_CPPType(void* _ref);
char* generate_CFunctionHeader(void* _ref);
ArrayList new_ArrayList(void* _ref) {
	ArrayList _this = *((ArrayList*) _ref);
	_this(new_java.util.ArrayList<T>());
}
ArrayList<T> of_ArrayList(void* _ref) {
	ArrayList _this = *((ArrayList*) _ref);
	return new_ArrayList<T>(new_java.util.ArrayList<T>(Arrays.asList(elements)));
}
Stream<T> stream_ArrayList(void* _ref) {
	ArrayList _this = *((ArrayList*) _ref);
	return _this.inner.stream();
}
ArrayList<T> add_ArrayList(void* _ref, T element) {
	ArrayList _this = *((ArrayList*) _ref);
	_this.inner.add(element);
	return _this;
}
int isEmpty_ArrayList(void* _ref) {
	ArrayList _this = *((ArrayList*) _ref);
	return _this.inner.isEmpty();
}
ArrayList<T> copy_ArrayList(void* _ref) {
	ArrayList _this = *((ArrayList*) _ref);
	return new_ArrayList<T>(new_java.util.ArrayList<T>(_this.inner));
}
ArrayList<T> addFirst_ArrayList(void* _ref, T element) {
	ArrayList _this = *((ArrayList*) _ref);
	_this.inner.addFirst(element);
	return _this;
}
template <typename T, typename X>
Result<T, X> toResult_Err(void* _ref){
	Err<T, X> _this = *((Err<T, X>*) _ref);
	ResultData<T, X> data;
	data.err = _this;
	return Result<T, X> { ErrTag, data };
}
template <typename T, typename X>
Result<T, X> toResult_Ok(void* _ref){
	Ok<T, X> _this = *((Ok<T, X>*) _ref);
	ResultData<T, X> data;
	data.ok = _this;
	return Result<T, X> { OkTag, data };
}
CPPType toCPPType_CPointerType(void* _ref){
	CPointerType _this = *((CPointerType*) _ref);
	CPPTypeData data;
	data.cpointertype = _this;
	return CPPType { CPointerTypeTag, data };
}
char* generate_CPointerType(void* _ref) {
	CPointerType _this = *((CPointerType*) _ref);
	return _this.type.generate() + "*";
}
char* getSimpleName_CPointerType(void* _ref) {
	CPointerType _this = *((CPointerType*) _ref);
	return _this.type.getSimpleName() + "_ref";
}
CPPType toCPPType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CPPTypeData data;
	data.ctemplatetype = _this;
	return CPPType { CTemplateTypeTag, data };
}
char* generate_CTemplateType(void* _ref) {
	CTemplateType _this = *((CTemplateType*) _ref);
	char* joined = _this.list.stream().map(generate_CPPType).collect(Collectors.joining(", "));
	return _this.base + "<" + joined + ">";
}
char* getSimpleName_CTemplateType(void* _ref) {
	CTemplateType _this = *((CTemplateType*) _ref);
	return _this.base;
}
CPPType toCPPType_CIdentifier(void* _ref){
	CIdentifier _this = *((CIdentifier*) _ref);
	CPPTypeData data;
	data.cidentifier = _this;
	return CPPType { CIdentifierTag, data };
}
char* generate_CIdentifier(void* _ref) {
	CIdentifier _this = *((CIdentifier*) _ref);
	return _this.input;
}
char* getSimpleName_CIdentifier(void* _ref) {
	CIdentifier _this = *((CIdentifier*) _ref);
	return _this.input;
}
char* wrap_Placeholder(void* _ref, char* input) {
	Placeholder _this = *((Placeholder*) _ref);
	char* replaced = input.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
char* generate_Placeholder(void* _ref) {
	Placeholder _this = *((Placeholder*) _ref);
	return wrap(_this.input);
}
char* getSimpleName_Placeholder(void* _ref) {
	Placeholder _this = *((Placeholder*) _ref);
	return _this.generate();
}
State new_State(void* _ref, char* input) {
	State _this = *((State*) _ref);
	_this.input = input;
	_this.buffer = new_StringBuilder();
	_this.depth = 0;
	_this.segments = new_ArrayList<char*>();
	_this.index = 0;
}
State enter_State(void* _ref) {
	State _this = *((State*) _ref);
	_this.depth = _this.depth + 1;
	return _this;
}
State exit_State(void* _ref) {
	State _this = *((State*) _ref);
	_this.depth = _this.depth - 1;
	return _this;
}
State advance_State(void* _ref) {
	State _this = *((State*) _ref);
	_this.segments = _this.segments.add(_this.buffer.toString());
	_this.buffer = new_StringBuilder();
	return _this;
}
int isShallow_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.depth == 1;
}
State append_State(void* _ref, char c) {
	State _this = *((State*) _ref);
	_this.buffer.append(c);
	return _this;
}
int isLevel_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.depth == 0;
}
Optional<char> pop_State(void* _ref) {
	State _this = *((State*) _ref);
	if (_this.index < _this.input.length()) {
		int counter = _this.index;
		_this.index++;
		char element = _this.input.charAt(counter);
		return Optional.of(element);
	}
	else {
		return Optional.empty();
	}
}
Stream<char*> stream_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.segments.stream();
}
auto _lambda1_(auto _ref, auto next) {
		State appended = _this.append(next);
		return new_Tuple<char, State>(next, appended);
	}Optional<Tuple<char, State>> popAndAppendToTuple_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.pop().map(_lambda1_);
}
Optional<State> popAndAppendToOption_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.popAndAppendToTuple().map(right_Tuple);
}
char peek_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.input.charAt(_this.index);
}
CFunctionHeader toCFunctionHeader_CDefinition(void* _ref){
	CDefinition _this = *((CDefinition*) _ref);
	CFunctionHeaderData data;
	data.cdefinition = _this;
	return CFunctionHeader { CDefinitionTag, data };
}
char* generate_CDefinition(void* _ref) {
	CDefinition _this = *((CDefinition*) _ref);
	return _this.cppType().generate() + " " + _this.name();
}
App new_App(void* _ref) {
	App _this = *((App*) _ref);
	_this.globals = new_ArrayList<char*>();
	_this.structureNames = new_Stack<char*>();
	_this.functions = new_ArrayList<char*>();
	_this.forwardDeclarations = new_ArrayList<char*>();
	_this.structures = new_ArrayList<char*>();
	_this.sealedStructures = new_ArrayList<char*>();
	_this.depth = 1;
	_this.counter = 0;
}
void main_App(void* _ref, char** args) {
	App _this = *((App*) _ref);
	new_App().run().ifPresent(printStackTrace_Throwable);
}
Optional<IOException> run_App(void* _ref) {
	App _this = *((App*) _ref);
	Path source = Paths.get(".", "src", "main", "java", "magma", "App.java");
	Result<char*, IOException> input = _this.readString(source);
	return _switch3_;
}
auto _lambda5_(auto _ref) {
	auto _this = _ref;
	return _this.compileNative(target);
};
Optional<IOException> compilePath_App(void* _ref, Path source, char* input) {
	App _this = *((App*) _ref);
	Path target = source.resolveSibling("App.cpp");
	char* output = _this.compile(input);
	return _this.writeString(target, output).or(_lambda5_);
}
Optional<> compileNative_App(void* _ref, Path target) {
	App _this = *((App*) _ref);
	Result<Process, IOException> clang = _this.startCommand(ArrayList.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));
	return _switch7_;
}
Optional<IOException> waitForProcess_App(void* _ref, Process process) {
	App _this = *((App*) _ref);
	return _switch9_;
}
Result<Integer, IOException> waitFor_App(void* _ref, Process process) {
	App _this = *((App*) _ref);
}
Result<Process, IOException> startCommand_App(void* _ref, ArrayList<char*> command) {
	App _this = *((App*) _ref);
}
Optional<IOException> writeString_App(void* _ref, Path target, char* output) {
	App _this = *((App*) _ref);
}
Result<char*, IOException> readString_App(void* _ref, Path source) {
	App _this = *((App*) _ref);
}
char* compile_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	char* compiled = _this.compileStatements(input, compileRootSegment_this);
	char* joinedForwardDeclarations = String.join("", _this.forwardDeclarations.inner);
	char* joinedFunctions = String.join("", _this.functions.inner);
	char* joinedStructures = String.join("", _this.structures.inner);
	char* joinedSealedStructures = String.join("", _this.sealedStructures.inner);
	char* joinedGlobals = String.join("", _this.globals.inner);
	return joinedForwardDeclarations + compiled + joinedStructures + joinedSealedStructures + joinedGlobals + joinedFunctions + "int main(){" + System.lineSeparator() + "\treturn " + "0;" + System.lineSeparator() +
					 "}";
}
char* compileStatements_App(void* _ref, char* input, Function<char*, char*> mapper) {
	App _this = *((App*) _ref);
	return _this.divide(input, foldStatement_this).map(mapper).collect(Collectors.joining());
}
Stream<char*> divide_App(void* _ref, char* input, BiFunction<State, char, State> folder) {
	App _this = *((App*) _ref);
	State current = new_State(input);
	while (true) {
		Optional<char> maybeNext = current.pop();
		if (maybeNext.isEmpty()) {
			break;
		}
		current = _this.foldEscaped(current, maybeNext.get(), folder);
	}
	return current.advance().stream();
}
State foldEscaped_App(void* _ref, State current, char next, BiFunction<State, char, State> folder) {
	App _this = *((App*) _ref);
	if (next == '\'') {
		return current.append(next).popAndAppendToTuple().map(foldSingleEscapeChar_this).flatMap(popAndAppendToOption_State).orElse(current);
	}
	if (next == '\"') {
		State current0 = current.append(next);
		while (true) {
			Optional<Tuple<char, State>> maybeTuple = current0.popAndAppendToTuple();
			if (maybeTuple.isEmpty()) {
				break;
			}
			Tuple<char, State> tuple = maybeTuple.get();
			current0 = tuple.right;
			char nextInQuotes = tuple.left;
			if (nextInQuotes == '\\') {
				current0 = current0.popAndAppendToOption().orElse(current0);
				continue;
			}
			if (nextInQuotes == '\"') {
				break;
			}
		}
		return current0;
	}
	return folder.apply(current, next);
}
State foldSingleEscapeChar_App(void* _ref, Tuple<char, State> tuple) {
	App _this = *((App*) _ref);
	if (tuple.left == '\\') {
		return tuple.right.popAndAppendToOption().orElse(tuple.right);
	}
	return tuple.right;
}
State foldStatement_App(void* _ref, State state, char c) {
	App _this = *((App*) _ref);
	State appended = state.append(c);
	if (c == ';' && appended.isLevel()) {
		return appended.advance();
	}
	if (c == '}' && appended.isShallow()) {
		State state1;
		if (appended.peek() == ';') {
			state1 = appended.popAndAppendToOption().orElse(appended);
		}
		else {
			state1 = appended;
		}
		return state1.advance().exit();
	}/*

		if (c == '{' || c == '(') {
			return appended.enter();
		}*/
	if (c == '}' || c == ') /*') {
			return appended.exit();
		}*/
	return appended;
}
auto _lambda11_(auto _ref) {
	auto _this = _ref;
	return Placeholder.wrap(input);
};
char* compileRootSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	char* stripped = input.strip();
	if (/*stripped.startsWith("package ") || stripped*/.startsWith("import ")) {
		return "";
	}
	return _this.compileStructure("class", stripped).orElseGet(_lambda11_);
}
auto _lambda16_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
auto _lambda21_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
auto _lambda25_(auto _ref, auto slice) {
	auto _this = _ref;
	return "typename " + slice;
};
auto _lambda29_(auto _ref, auto content1) {
	auto _this = _ref;
	return _this.generateWithIndent(content1, 1);
};
auto _lambda32_(auto _ref, auto slice) {
	auto _this = _ref;
	return slice + "Tag";
};
auto _lambda36_(auto _ref, auto slice) {
	auto _this = _ref;
	return System.lineSeparator() + "\t" + slice + typeArguments + " " + slice.toLowerCase() + ";";
};
auto _lambda40_(auto _ref, auto slice) {
	auto _this = _ref;
	return _this.generateStatement(slice, 1);
};
Optional<char*> compileStructure_App(void* _ref, char* type, char* input) {
	App _this = *((App*) _ref);
	int classIndex = input.indexOf(type);
	if (classIndex >= 0) {
		char* afterKeyword = input.substring(classIndex + type.length());
		int contentStart = afterKeyword.indexOf("{");
		if (contentStart >= 0) {
			char* beforeContent = afterKeyword.substring(0, contentStart).strip();
			char* withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
			if (withEnd.endsWith("}")) {
				char* content = withEnd.substring(0, withEnd.length() - 1);
				int permitsIndex = beforeContent.indexOf("permits");
				ArrayList<char*> variants = new_ArrayList<char*>();
				if (permitsIndex >= 0) {
					char** variantsArray = beforeContent.substring(permitsIndex + "permits".length()).split(Pattern.quote(","));
					beforeContent = beforeContent.substring(0, permitsIndex).strip();
					variants = new_ArrayList<char*>(Arrays.stream(variantsArray).map(strip_char*).filter(_lambda16_).toList());
				}
				int implementsIndex = beforeContent.indexOf("implements");
				Optional<CPPType> maybeInterfaceType = Optional.empty();
				if (implementsIndex >= 0) {
					char* slice = beforeContent.substring(implementsIndex + "implements".length()).strip();
					maybeInterfaceType = _this.compileType(slice);
					beforeContent = beforeContent.substring(0, implementsIndex).strip();
				}
				ArrayList<CDefinition> recordFields = new_ArrayList<CDefinition>();
				if (/*beforeContent.endsWith(")"*/) /*) {
						final String slice = beforeContent.substring(0, beforeContent.length() - 1);
						final int i = slice.indexOf("(");
						if (i >= 0) {
							final String params = slice.substring(i + 1);
							beforeContent = slice.substring(0, i).strip();

							recordFields = this.compileParametersToList(params);
						}
					}*/
				ArrayList<char*> typeParameters = new_ArrayList<char*>();
				if (beforeContent.endsWith(">")) {
					char* withoutEnd = beforeContent.substring(0, beforeContent.length() - 1);
					int typeParamStart = withoutEnd.indexOf("<");
					if (typeParamStart >= 0) {
						beforeContent = withoutEnd.substring(0, typeParamStart);
						char** typeParamsArray = withoutEnd.substring(typeParamStart + 1).split(Pattern.quote(","));
						typeParameters = new_ArrayList<char*>(Arrays.stream(typeParamsArray).map(strip_char*).filter(_lambda21_).toList());
					}
				}
				if (/*!this*/.isIdentifier(beforeContent)) {
					return Optional.empty();
				}
				char* templateString;
				if (typeParameters.isEmpty()) {
					templateString = "";
				}
				else {
					char* collect = typeParameters.stream().map(_lambda25_).collect(Collectors.joining(", "));
					templateString = "template <" + collect + ">" + System.lineSeparator();
				}
				char* dependencies;
				if (variants.isEmpty()) {
					dependencies = "";
				}
				else {
					char* enumFields = variants.stream().map(_lambda32_).map(_lambda29_).collect(Collectors.joining(","));
					char* typeArguments = _this.joinTypeArguments(typeParameters);
					char* unionFields = variants.stream().map(_lambda36_).collect(Collectors.joining());
					dependencies = "enum " + beforeContent + "Tag {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator() + templateString + "union " + beforeContent + "Data {" + unionFields + System.lineSeparator() + "};" + System.lineSeparator();
				}
				char* fields;
				if (variants.isEmpty()) {
					fields = recordFields.stream().map(generate_CDefinition).map(_lambda40_).collect(Collectors.joining(""));
				}
				else {
					fields = /*this.generateStatement(beforeContent*/ + /*"Tag tag", 1)*/ + _this.generateStatement(beforeContent + "Data" + this.joinTypeArguments(typeParameters) + " " + "data", 1);
				}
				if (maybeInterfaceType.isPresent()) {
					CPPType interfaceType = maybeInterfaceType.get();
					char* joinedTypeArguments = _this.joinTypeArguments(typeParameters);
					char* thisType = beforeContent + joinedTypeArguments;
					_this.functions = _this.functions.add(templateString + interfaceType.generate() + " to" + interfaceType.getSimpleName() + "_" + beforeContent + "(void* _ref" + "){" + /*this.generateStatement(thisType*/ + " _this = *((" + thisType + /*"*) _ref)", 1)*/ + _this.generateStatement(/*interfaceType.getSimpleName(*/) + "Data" + joinedTypeArguments + /*" data", 1)*/ + /*this.generateStatement("data."*/ + beforeContent.toLowerCase() + /*" = _this", 1)*/ + /*this.generateStatement(
										"return "*/ + interfaceType.generate() + " { " + beforeContent + "Tag, " + /*"data }",
										1)*/ + System.lineSeparator() + "}" + System.lineSeparator());
				}
				_this.forwardDeclarations = _this.forwardDeclarations.add(templateString + "struct " + beforeContent + ";" + System.lineSeparator());
				_this.structureNames.push(beforeContent);
				char* generated = dependencies + templateString + "struct " + beforeContent + " {" + fields + System.lineSeparator() + _this.compileStatements(content, compileClassSegment_this) + "};" + System.lineSeparator();
				_this.structureNames.pop();
				if (variants.isEmpty()) {
					_this.structures = _this.structures.add(generated);
				}
				else {
					_this.sealedStructures = _this.sealedStructures.add(generated);
				}
				return Optional.of("");
			}
		}
	}
	return Optional.empty();
}
char* joinTypeArguments_App(void* _ref, ArrayList<char*> typeParameters) {
	App _this = *((App*) _ref);
	char* joinedTypeArguments;
	if (typeParameters.isEmpty()) {
		joinedTypeArguments = "";
	}
	else {
		joinedTypeArguments = "<" + String.join(", ", typeParameters.inner) + ">";
	}
	return joinedTypeArguments;
}
char* generateStatement_App(void* _ref, char* content, int depth) {
	App _this = *((App*) _ref);
	return _this.generateWithIndent(content, depth) + ";";
}
char* generateWithIndent_App(void* _ref, char* content, int depth) {
	App _this = *((App*) _ref);
	return _this.generateIndent(depth) + content;
}
char* generateIndent_App(void* _ref, int depth) {
	App _this = *((App*) _ref);
	return System.lineSeparator() + "\t".repeat(depth);
}
int isIdentifier_App(void* _ref, char* input) {
	App _this = *((App*) _ref);/*
		for (int i = 0; i < input.length(); i++) {
			final char next = input.charAt(i);
			if (Character.isLetter(next) || (i != 0 && Character.isDigit(next))) {continue;}
			return false;
		}*/
	return true;
}
auto _lambda42_(auto _ref) {
	auto _this = _ref;
	return _this.compileDefinitionToField(slice);
};
auto _lambda44_(auto _ref) {
	auto _this = _ref;
	return Placeholder.wrap(input);
};
char* compileClassSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	if (input.isBlank()) {
		return "";
	}
	Optional<char*> maybeClass = _this.compileStructure("class", input);
	if (maybeClass.isPresent()) {
		return maybeClass.get();
	}
	Optional<char*> maybeInterface = _this.compileStructure("interface", input);
	if (maybeInterface.isPresent()) {
		return maybeInterface.get();
	}
	Optional<char*> maybeRecord = _this.compileStructure("record", input);
	if (maybeRecord.isPresent()) {
		return maybeRecord.get();
	}
	Optional<char*> maybeEnum = _this.compileStructure("enum", input);
	if (maybeEnum.isPresent()) {
		return maybeEnum.get();
	}
	if (input.endsWith(";")) {
		char* slice = input.substring(0, input.length() - 1);
		Optional<char*> maybeClassStatement = _this.compileEnumValues(slice).or(_lambda42_);
		if (maybeClassStatement.isPresent()) {
			return maybeClassStatement.get();
		}
	}
	return _this.compileMethod(input).orElseGet(_lambda44_);
}
Optional<char*> compileMethod_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	int paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return Optional.empty();
	}
	char* definition = input.substring(0, paramStart).strip();
	char* withParams = input.substring(paramStart + 1);
	int paramEnd = withParams.indexOf(")");
	if (paramEnd < 0) {
		return Optional.empty();
	}
	char* params = withParams.substring(0, paramEnd).strip();
	char* withBraces = withParams.substring(paramEnd + 1).strip();
	CFunctionHeader header = _this.compileFunctionHeader(definition);
	char* beforeContent = header.generate() + "(" + this.compileParameters(params) + ")";
	char* generated;
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		char* content = withBraces.substring(1, withBraces.length() - 1);
		char* currentStructureName = _this.structureNames.peek();
		char* thisDefinition = _this.generateStatement(currentStructureName + " _this = *((" + currentStructureName + "*) _ref)", 1);
		generated = beforeContent + " {" + thisDefinition + _this.compileMethodSegments(content) + System.lineSeparator() + "}" + System.lineSeparator();
	}
	else {
		generated = beforeContent + ";" + System.lineSeparator();
	}
	_this.functions = _this.functions.add(generated);
	return Optional.of("");
}
auto _lambda46_(auto _ref) {
	auto _this = _ref;
	return new_Placeholder(input);
};
auto _lambda49_(auto _ref) {
	auto _this = _ref;
	return _this.compileConstructor(input);
};
auto _lambda52_(auto _ref, auto item) {
	auto _this = _ref;
	return new_CDefinition(item.cppType, item.name + "_" + _this.structureNames.peek());
};
CFunctionHeader compileFunctionHeader_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.compileDefinition(input). < /*CFunctionHeader>map*/(_lambda52_).or(_lambda49_).orElseGet(_lambda46_);
}
auto _lambda54_(auto _ref, auto content) {
	auto _this = _ref;
	return _this.generateStatement(content, 1);
};
Optional<char*> compileDefinitionToField_App(void* _ref, char* slice) {
	App _this = *((App*) _ref);
	return _this.compileDefinition(slice).map(generate_CDefinition).map(_lambda54_);
}
char* compileMethodSegments_App(void* _ref, char* content) {
	App _this = *((App*) _ref);
	return _this.compileStatements(content, compileMethodSegmentOrPlaceholder_this);
}
Optional<CFunctionHeader> compileConstructor_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	int i = input.lastIndexOf(" ");
	if (i >= 0) {
		char* name = input.substring(i + 1).strip();
		if (_this.isIdentifier(name)) {
			char* structName = _this.structureNames.peek();
			return Optional.of(new_CDefinition(new_CIdentifier(structName), "new_" + structName));
		}
	}
	else {
		if (_this.isIdentifier(input)) {
			char* structName = _this.structureNames.peek();
			return Optional.of(new_CDefinition(new_CIdentifier(structName), "new_" + structName));
		}
	}
	return Optional.empty();
}
auto _lambda59_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
Optional<char*> compileEnumValues_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	ArrayList<char*> segments = new_ArrayList<char*>(Arrays.stream(input.split(Pattern.quote(","))).map(strip_char*).filter(_lambda59_).toList());/*

		for (String segment : segments.inner) {
			final String stripped = segment.strip();
			final Optional<String> maybeEnumValue = this.compileEnumValue(stripped);
			if (maybeEnumValue.isPresent()) {
				this.globals = this.globals.add(maybeEnumValue.get());
			} else {
				return Optional.empty();
			}
		}*/
	return Optional.of("");
}
Optional<char*> compileEnumValue_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	if (/*stripped.endsWith(")"*/) /*) {
			final String slice = stripped.substring(0, stripped.length() - 1);
			final int i = slice.indexOf("(");
			if (i >= 0) {
				final String name = slice.substring(0, i).strip();
				final String arguments = slice.substring(i + 1);
				if (this.isIdentifier(name)) {
					final String structureName = this.structureNames.peek();
					return Optional.of(structureName + " " + name + "Value = " + structureName + " { " + arguments + " };" +
														 System.lineSeparator());
				}
			}
		}*/
	return Optional.empty();
}
auto _lambda61_(auto _ref) {
	auto _this = _ref;
	return Placeholder.wrap(input);
};
char* compileMethodSegmentOrPlaceholder_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.compileMethodSegment(input).orElseGet(_lambda61_);
}
Optional<char*> compileMethodSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	char* stripped = input.strip();
	if (/*stripped.isEmpty() || stripped.startsWith("try ") || stripped*/.startsWith("catch ")) {
		return Optional.of("");
	}
	if (stripped.startsWith("{") && stripped.endsWith("}")) {
		char* content = stripped.substring(1, stripped.length() - 1);
		_this.depth++;
		char* compiled = _this.compileMethodSegments(content);
		/*this.depth--*/;
		return Optional.of("{" + compiled + this.generateIndent(this.depth) + "}");
	}
	Optional<char*> maybeIf = _this.compileConditional(stripped, "if");
	if (maybeIf.isPresent()) {
		return maybeIf;
	}
	Optional<char*> maybeWhile = _this.compileConditional(stripped, "while");
	if (maybeWhile.isPresent()) {
		return maybeWhile;
	}
	if (stripped.endsWith(";")) {
		char* slice = stripped.substring(0, stripped.length() - 1);
		return Optional.of(_this.generateStatement(_this.compileMethodStatement(slice), _this.depth));
	}
	if (stripped.startsWith("else ")) {
		char* substring = stripped.substring(5);
		return Optional.of(_this.generateIndent(_this.depth) + "else " + _this.compileMethodSegmentOrPlaceholder(substring));
	}
	return Optional.empty();
}
Optional<char*> compileConditional_App(void* _ref, char* input, char* type) {
	App _this = *((App*) _ref);
	if (input.startsWith(type)) {
		char* substring = input.substring(type.length()).strip();/*
			if (substring.startsWith("(")) {
				final String withCondition = substring.substring(1);
				final int conditionEnd = this.findConditionEnd(withCondition);

				if (conditionEnd >= 0) {
					final String condition = withCondition.substring(0, conditionEnd).strip();
					final String substring2 = withCondition.substring(conditionEnd + 1).strip();
					return Optional.of(this.generateIndent(this.depth) + type + " (" + this.compileExpression(condition) + ") " +
														 this.compileMethodSegmentOrPlaceholder(substring2));
				}
			}*/
	}
	return Optional.empty();
}
int findConditionEnd_App(void* _ref, char* withCondition) {
	App _this = *((App*) _ref);
	int conditionEnd =  - 1;
	int depth = 0;/*
		for (int i = 0; i < withCondition.length(); i++) {
			final char c = withCondition.charAt(i);
			if (c == ')') {
				depth--;
				if (depth == -1) {
					conditionEnd = i;
					break;
				}
			}

			if (c == '(') {
				depth++;
			}
		}*/
	return conditionEnd;
}
auto _lambda63_(auto _ref) {
	auto _this = _ref;
	return _this.compileExpression(substring);
};
auto _lambda65_(auto _ref) {
	auto _this = _ref;
	return Placeholder.wrap(stripped);
};
auto _lambda68_(auto _ref) {
	auto _this = _ref;
	return _this.compileDefinition(input).map(generate_CDefinition);
};
char* compileMethodStatement_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	char* stripped = input.strip();
	if (stripped.startsWith("return ")) {
		char* slice = stripped.substring("return ".length()).strip();
		return "return " + _this.compileExpression(slice);
	}
	int separator = stripped.indexOf('=');
	if (separator >= 0) {
		char* substring = stripped.substring(0, separator).strip();
		char* substring1 = stripped.substring(separator + 1).strip();
		char* s = _this.compileDefinition(substring).map(generate_CDefinition).orElseGet(_lambda63_);
		return s + " = " + _this.compileExpression(substring1);
	}
	if (stripped.endsWith("++")) {
		return _this.compileExpression(stripped.substring(0, stripped.length() - 2)) + "++";
	}
	if (stripped.equals("break")) {
		return "break";
	}
	if (stripped.equals("continue")) {
		return "continue";
	}
	return _this.compileInvocation(stripped).or(_lambda68_).orElseGet(_lambda65_);
}
auto _lambda70_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "<");
};
auto _lambda73_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, ">=");
};
auto _lambda76_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "==");
};
auto _lambda79_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "&&");
};
auto _lambda82_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "-");
};
char* compileExpression_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	char* stripped = input.strip();
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return stripped;
	}
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return stripped;
	}
	Optional<char*> maybeLambda = _this.compileLambda(stripped);
	if (maybeLambda.isPresent()) {
		return maybeLambda.get();
	}
	Optional<char*> maybeInvocation = _this.compileInvocation(stripped);
	if (maybeInvocation.isPresent()) {
		return maybeInvocation.get();
	}
	int i = stripped.lastIndexOf(".");
	if (i >= 0) {
		char* child = stripped.substring(0, i).strip();
		char* name = stripped.substring(i + 1).strip();
		if (_this.isIdentifier(name)) {
			return _this.compileExpression(child) + "." + name;
		}
	}
	if (_this.isIdentifier(stripped)) {
		if (stripped.equals("this")) {
			return "_this";
		}
		return stripped;
	}
	if (stripped.startsWith("switch")) {
		return _this.createName("switch");
	}
	Optional<char*> maybeOperator = _this.compileOperator(stripped, "+").or(_lambda82_).or(_lambda79_).or(_lambda76_).or(_lambda73_).or(_lambda70_);
	if (maybeOperator.isPresent()) {
		return maybeOperator.get();
	}
	int i2 = stripped.lastIndexOf("::");
	if (i2 >= 0) {
		char* substring = stripped.substring(0, i2);
		char* substring1 = stripped.substring(i2 + 2);
		return substring1 + "_" + _this.compileType(substring).map(generate_CPPType).orElse("?");
	}
	if (_this.isNumber(stripped)) {
		return stripped;
	}
	return Placeholder.wrap(stripped);
}
auto _lambda88_(auto _ref, auto segment) {
	auto _this = _ref;
	return "auto " + segment;
};
auto _lambda91_(auto _ref, auto segment) {
	auto _this = _ref;
	return /*!segment*/.isEmpty();
};
auto _lambda94_(auto _ref) {
			char* expression = _this.compileExpression(content);
			return "{" + _this.generateStatement("auto _this = _ref", 1) + /*this.generateStatement("return "*/ + /*expression, 1)*/ + System.lineSeparator() + "};" + System.lineSeparator();
		}Optional<char*> compileLambda_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	int arrowIndex = stripped.indexOf("->");
	if (arrowIndex >= 0) {
		char* names = stripped.substring(0, arrowIndex).strip();
		char* content = stripped.substring(arrowIndex + 2);
		char* functionName = _this.createName("lambda");
		ArrayList<char*> parameters;
		if (_this.isIdentifier(names)) {
			parameters = ArrayList.of("auto " + names);
		}
		else 
		if (names.startsWith("(") && names.endsWith(")")) {
			char* slice = names.substring(1, names.length() - 1);
			parameters = new_ArrayList<char*>(_this.divide(slice, foldValue_this).map(strip_char*).filter(_lambda91_).map(_lambda88_).toList());
		}
		else {
			return Optional.empty();
		}
		ArrayList<char*> copy = parameters.copy().addFirst("auto _ref");
		_this.functions = _this.functions.add("auto " + functionName + "(" + String.join(", ", copy.inner) + ") " + _this.compileMethodSegment(content).orElseGet(_lambda94_));
		return Optional.of(functionName);
	}
	return Optional.empty();
}
char* createName_App(void* _ref, char* type) {
	App _this = *((App*) _ref);
	char* s = "_" + type + this.counter + "_";
	_this.counter++;
	return s;
}
Optional<char*> compileInvocation_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	if (/*stripped.endsWith(")"*/) /*) {
			final String slice = stripped.substring(0, stripped.length() - 1);
			int argStart = -1;
			int depth = 0;
			for (int i = 0; i < slice.length(); i++) {
				final char next = slice.charAt(i);
				if (next == '(') {
					if (depth == 0) {
						argStart = i;
					}

					depth++;
				}
				if (next == ')') {
					depth--;
				}
			}

			if (argStart >= 0) {
				final String caller = slice.substring(0, argStart).strip();
				final ArrayList<String> arguments = new ArrayList<String>(this
																																			.divide(slice.substring(argStart + 1),
																																							this::foldValue)
																																			.map(String::strip)
																																			.filter(segment -> !segment.isEmpty())
																																			.map(this::compileExpression)
																																			.toList());

				final Optional<String> maybeCaller = this.compileCaller(caller);
				if (maybeCaller.isPresent()) {
					return Optional.of(maybeCaller.get() + "(" + String.join(", ", arguments.inner) + ")");
				}
			}
		}*/
	return Optional.empty();
}
Optional<char*> compileCaller_App(void* _ref, char* caller) {
	App _this = *((App*) _ref);
	if (caller.startsWith("new ")) {
		char* substring = caller.substring("new ".length());
		Optional<CPPType> maybeType = _this.compileType(substring);
		if (maybeType.isPresent()) {
			return Optional.of("new_" + maybeType.get().generate());
		}
	}
	return Optional.of(_this.compileExpression(caller));
}
Optional<char*> compileOperator_App(void* _ref, char* stripped, char* separator) {
	App _this = *((App*) _ref);
	int i1 = stripped.indexOf(separator);
	if (i1 >= 0) {
		char* substring = stripped.substring(0, i1);
		char* substring1 = stripped.substring(i1 + separator.length());
		return Optional.of(_this.compileExpression(substring) + " " + separator + " " + _this.compileExpression(substring1));
	}
	return Optional.empty();
}
int isNumber_App(void* _ref, char* input) {
	App _this = *((App*) _ref);/*
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			if (!Character.isDigit(c)) {
				return false;
			}
		}*/
	return true;
}
char* compileParameters_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.compileParametersToList(input).copy().addFirst(new_CDefinition(new_CPointerType(CPPPrimitiveType.Void), "_ref")).stream().map(generate_CDefinition).collect(Collectors.joining(", "));
}
auto _lambda103_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
ArrayList<CDefinition> compileParametersToList_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return new_ArrayList<CDefinition>(_this.divide(input, foldValue_this).map(strip_char*).filter(_lambda103_).map(compileDefinition_this).flatMap(stream_Optional).toList());
}
auto _lambda105_(auto _ref, auto cppType) {
	auto _this = _ref;
	return new_CDefinition(cppType, name);
};
auto _lambda107_(auto _ref, auto cppType) {
	auto _this = _ref;
	return new_CDefinition(cppType, name);
};
Optional<CDefinition> compileDefinition_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	int nameSeparator = input.lastIndexOf(" ");
	if (nameSeparator < 0) {
		return Optional.empty();
	}
	char* beforeName = input.substring(0, nameSeparator);
	char* name = input.substring(nameSeparator + 1).strip();
	if (/*!this*/.isIdentifier(name)) {
		return Optional.empty();
	}
	int typeSeparator =  - 1;
	int depth = 0;/*
		for (int i = 0; i < beforeName.length(); i++) {
			final char c = beforeName.charAt(i);
			if (c == ' ' && depth == 0) {
				typeSeparator = i;
			}
			if (c == '<') {
				depth++;
			}
			if (c == '>') {
				depth--;
			}
		}*/
	if (typeSeparator >= 0) {
		char* type = beforeName.substring(typeSeparator + 1).strip();
		return _this.compileType(type).map(_lambda105_);
	}
	return _this.compileType(beforeName).map(_lambda107_);
}
auto _lambda116_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
Optional<CPPType> compileType_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	char* stripped = input.strip();/*

		switch (stripped) {
			case "Character" -> {
				return Optional.of(CPPPrimitiveType.Char);
			}
			case "boolean" -> {
				return Optional.of(CPPPrimitiveType.Int);
			}
			case "void" -> {
				return Optional.of(CPPPrimitiveType.Void);
			}
		}*/
	if (stripped.endsWith("[]")) {
		char* slice = stripped.substring(0, stripped.length() - 2);
		return _this.compileType(slice).map(new_CPointerType);
	}
	if (stripped.equals("String")) {
		return Optional.of(new_CPointerType(CPPPrimitiveType.Char));
	}
	if (stripped.endsWith(">")) {
		char* withoutEnd = stripped.substring(0, stripped.length() - 1);
		int i = withoutEnd.indexOf("<");
		if (i >= 0) {
			char* base = withoutEnd.substring(0, i);
			char* typeArguments = withoutEnd.substring(i + 1);
			ArrayList<CPPType> list = new_ArrayList<CPPType>(_this.divide(typeArguments, foldValue_this).map(strip_char*).filter(_lambda116_).map(compileType_this).flatMap(stream_Optional).toList());
			return Optional.of(new_CTemplateType(base, list));
		}
	}
	if (_this.isIdentifier(stripped)) {
		if (stripped.equals("public")) {
			return Optional.empty();
		}
		return Optional.of(new_CIdentifier(stripped));
	}
	return Optional.empty();
}
State foldValue_App(void* _ref, State state, char next) {
	App _this = *((App*) _ref);
	if (next == ',' && state.isLevel()) {
		return state.advance();
	}
	State appended = state.append(next);
	if (next == ' - ') {
		if (appended.peek() == '>') {
			return appended.popAndAppendToOption().orElse(appended);
		}
	}/*

		if (next == '<' || next == '(') {
			return appended.enter();
		}*/
	if (next == '>' || next == ') /*') {
			return appended.exit();
		}*/
	return appended;
}
int main(){
	return 0;
}