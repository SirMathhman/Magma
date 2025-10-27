struct App;
struct CPPPrimitiveType;
template <typename T, typename X>
struct Result;
struct CPPType;
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
/*
*/struct CPPPrimitiveType {

	char* content;};
template <typename T, typename X>
struct Err {
};
template <typename T, typename X>
struct Ok {
};
struct CPointerType {
};
struct CTemplateType {
};
struct CIdentifier {
};
struct Placeholder {
};
template <typename A, typename B>
struct Tuple {
};
struct State {

	char* input;
	ArrayList<char*> segments;
	StringBuilder buffer;
	int depth;
	int index;};
struct App {

	List<char*> globals;
	List<char*> forwardDeclarations;
	List<char*> functions;
	List<char*> structures;
	List<char*> sealedStructures;
	Stack<char*> structureNames;
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
	ResultData data;
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
CPPPrimitiveType VoidValue = CPPPrimitiveType { "void" };
CPPPrimitiveType CharValue = CPPPrimitiveType { "char" };
CPPType toCPPType_CPPPrimitiveType(void* _ref){
	CPPPrimitiveType _this = *((CPPPrimitiveType*) _ref);
	CPPTypeData data;
	data.cppprimitivetype = _this;
	return CPPType { CPPPrimitiveTypeTag, data };
}
CPPPrimitiveType new_CPPPrimitiveType(char* content) {
	this.content = content;
}
char* generate() {
	return this.content;
}
char* getSimpleName() {
	return this.content;
}
char* generate();
char* getSimpleName();
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
char* generate() {
	return this.type.generate() + "*";
}
char* getSimpleName() {
	return this.type.getSimpleName() + "_ref";
}
CPPType toCPPType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CPPTypeData data;
	data.ctemplatetype = _this;
	return CPPType { CTemplateTypeTag, data };
}
char* generate() {
	char* joined = this.list.stream().map(generate_CPPType).collect(Collectors.joining(", "));
	return this.base + "<" + joined + ">";
}
char* getSimpleName() {
	return this.base;
}
CPPType toCPPType_CIdentifier(void* _ref){
	CIdentifier _this = *((CIdentifier*) _ref);
	CPPTypeData data;
	data.cidentifier = _this;
	return CPPType { CIdentifierTag, data };
}
char* generate() {
	return this.input;
}
char* getSimpleName() {
	return this.input;
}
CPPType toCPPType_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CPPTypeData data;
	data.placeholder = _this;
	return CPPType { PlaceholderTag, data };
}
char* wrap(char* input) {
	char* replaced = input.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
char* generate() {
	return wrap(this.input);
}
char* getSimpleName() {
	return this.generate();
}
State new_State(char* input) {
	this.input = input;
	this.buffer = new_StringBuilder();
	this.depth = 0;
	this.segments = new_ArrayList<char*>();
	this.index = 0;
}
State enter() {
	this.depth = this.depth + 1;
	return this;
}
State exit() {
	this.depth = this.depth - 1;
	return this;
}
State advance() {
	this.segments.add(this.buffer.toString());
	this.buffer = new_StringBuilder();
	return this;
}
boolean isShallow() {
	return this.depth == 1;
}
State append(char c) {
	this.buffer.append(c);
	return this;
}
boolean isLevel() {
	return this.depth == 0;
}
Optional<Character> pop() {
	if (this.index < this.input.length()) {
		int counter = this.index;
		this.index++;
		char element = this.input.charAt(counter);
		return Optional.of(element);
	}else {
		return Optional.empty();
	}
}
Stream<char*> stream() {
	return this.segments.stream();
}
auto _lambda1_(auto _ref, auto next) {
		State appended = this.append(next);
		return new_Tuple<Character, State>(next, appended);
	}Optional<Tuple<Character, State>> popAndAppendToTuple() {
	return this.pop().map(_lambda1_);
}
Optional<State> popAndAppendToOption() {
	return this.popAndAppendToTuple().map(right_Tuple);
}
char peek() {
	return this.input.charAt(this.index);
}
App new_App() {
	this.globals = new_ArrayList<char*>();
	this.structureNames = new_Stack<char*>();
	this.functions = new_ArrayList<char*>();
	this.forwardDeclarations = new_ArrayList<char*>();
	this.structures = new_ArrayList<char*>();
	this.sealedStructures = new_ArrayList<char*>();
	this.depth = 1;
	this.counter = 0;
}
void main(char** args) {
	new_App().run().ifPresent(printStackTrace_Throwable);
}
Optional<IOException> run() {
	Path source = Paths.get(".", "src", "main", "java", "magma", "App.java");
	Result<char*, IOException> input = this.readString(source);
	return _switch3_;
}
auto _lambda5_(auto _ref) {
	return this.compileNative(target);
};
Optional<IOException> compilePath(char* input) {
	Path target = source.resolveSibling("App.cpp");
	char* output = this.compile(input);
	return this.writeString(target, output).or(_lambda5_);
}
Optional<> compileNative(Path target) {
	Result<Process, IOException> clang = this.startCommand(List.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));
	return _switch7_;
}
Optional<IOException> waitForProcess(Process process) {
	return _switch9_;
}
Result<Integer, IOException> waitFor(Process process) {
}
Result<Process, IOException> startCommand(List<char*> command) {
}
Optional<IOException> writeString(char* output) {
}
Result<char*, IOException> readString(Path source) {
}
char* compile(char* input) {
	char* compiled = this.compileStatements(input, compileRootSegment_this);
	char* joinedForwardDeclarations = String.join("", this.forwardDeclarations);
	char* joinedFunctions = String.join("", this.functions);
	char* joinedStructures = String.join("", this.structures);
	char* joinedSealedStructures = String.join("", this.sealedStructures);
	char* joinedGlobals = String.join("", this.globals);
	return joinedForwardDeclarations + compiled + joinedStructures + joinedSealedStructures + joinedGlobals + joinedFunctions + "int main(){" + System.lineSeparator() + "\treturn " + "0;" + System.lineSeparator() +
					 "}";
}
char* compileStatements(Function<char*, char*> mapper) {
	return this.divide(input, foldStatement_this).map(mapper).collect(Collectors.joining());
}
Stream<char*> divide(BiFunction<State, Character, State> folder) {
	State current = new_State(input);
	while (true) {
		Optional<Character> maybeNext = current.pop();
		if (maybeNext.isEmpty()) {
			break;
		}
		current = this.foldEscaped(current, maybeNext.get(), folder);
	}
	return current.advance().stream();
}
State foldEscaped(BiFunction<State, Character, State> folder) {
	if (next == '\'') {
		return current.append(next).popAndAppendToTuple().map(foldSingleEscapeChar_this).flatMap(popAndAppendToOption_State).orElse(current);
	}
	if (next == '\"') {
		State current0 = current.append(next);
		while (true) {
			Optional<Tuple<Character, State>> maybeTuple = current0.popAndAppendToTuple();
			if (maybeTuple.isEmpty()) {
				break;
			}
			Tuple<Character, State> tuple = maybeTuple.get();
			current0 = tuple.right;
			Character nextInQuotes = tuple.left;
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
State foldSingleEscapeChar(Tuple<Character, State> tuple) {
	if (tuple.left == '\\') {
		return tuple.right.popAndAppendToOption().orElse(tuple.right);
	}
	return tuple.right;
}
State foldStatement(Character c) {
	State appended = state.append(c);
	if (c == ';' && appended.isLevel()) {
		return appended.advance();
	}
	if (c == '}' && appended.isShallow()) {
		State state1;
		if (appended.peek() == ';') {
			state1 = appended.popAndAppendToOption().orElse(appended);
		}else {
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
	return Placeholder.wrap(input);
};
char* compileRootSegment(char* input) {
	char* stripped = input.strip();
	if (/*stripped.startsWith("package ") || stripped*/.startsWith("import ")) {
		return "";
	}
	return this.compileStructure("class", stripped).orElseGet(_lambda11_);
}
auto _lambda15_(auto _ref, auto slice) {
	return /*!slice*/.isEmpty();
};
auto _lambda19_(auto _ref, auto slice) {
	return /*!slice*/.isEmpty();
};
auto _lambda23_(auto _ref, auto slice) {
	return "typename " + slice;
};
auto _lambda27_(auto _ref, auto content1) {
	return this.generateWithIndent(content1, 1);
};
auto _lambda30_(auto _ref, auto slice) {
	return slice + "Tag";
};
auto _lambda34_(auto _ref, auto slice) {
	return System.lineSeparator() + "\t" + slice + typeArguments + " " + slice.toLowerCase() + ";";
};
Optional<char*> compileStructure(char* input) {
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
				List<char*> variants = Collections.emptyList();
				if (permitsIndex >= 0) {
					char** variantsArray = beforeContent.substring(permitsIndex + "permits".length()).split(Pattern.quote(","));
					beforeContent = beforeContent.substring(0, permitsIndex).strip();
					variants = Arrays.stream(variantsArray).map(strip_char*).filter(_lambda15_).toList();
				}
				int implementsIndex = beforeContent.indexOf("implements");
				Optional<CPPType> maybeInterfaceType = Optional.empty();
				if (implementsIndex >= 0) {
					char* slice = beforeContent.substring(implementsIndex + "implements".length()).strip();
					maybeInterfaceType = this.compileType(slice);
					beforeContent = beforeContent.substring(0, implementsIndex).strip();
				}
				if (/*beforeContent.endsWith(")"*/) /*) {
						final String slice = beforeContent.substring(0, beforeContent.length() - 1);
						final int i = slice.indexOf("(");
						if (i >= 0) {
							final String params = slice.substring(i + 1);
							beforeContent = slice.substring(0, i).strip();
						}
					}*/
				List<char*> typeParameters = new_ArrayList<char*>();
				if (beforeContent.endsWith(">")) {
					char* withoutEnd = beforeContent.substring(0, beforeContent.length() - 1);
					int typeParamStart = withoutEnd.indexOf("<");
					if (typeParamStart >= 0) {
						beforeContent = withoutEnd.substring(0, typeParamStart);
						char** typeParamsArray = withoutEnd.substring(typeParamStart + 1).split(Pattern.quote(","));
						typeParameters = Arrays.stream(typeParamsArray).map(strip_char*).filter(_lambda19_).toList();
					}
				}
				if (/*!this*/.isIdentifier(beforeContent)) {
					return Optional.empty();
				}
				char* templateString;
				if (typeParameters.isEmpty()) {
					templateString = "";
				}else {
					char* collect = typeParameters.stream().map(_lambda23_).collect(Collectors.joining(", "));
					templateString = "template <" + collect + ">" + System.lineSeparator();
				}
				char* dependencies;
				if (variants.isEmpty()) {
					dependencies = "";
				}else {
					char* enumFields = variants.stream().map(_lambda30_).map(_lambda27_).collect(Collectors.joining(","));
					char* typeArguments = this.joinTypeArguments(typeParameters);
					char* unionFields = variants.stream().map(_lambda34_).collect(Collectors.joining());
					dependencies = "enum " + beforeContent + "Tag {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator() + templateString + "union " + beforeContent + "Data {" + unionFields + System.lineSeparator() + "};" + System.lineSeparator();
				}
				char* fields;
				if (variants.isEmpty()) {
					fields = "";
				}else {
					fields = /*this.generateStatement(beforeContent*/ + /*"Tag tag", 1)*/ + this.generateStatement(beforeContent + "Data " + "data", 1);
				}
				if (maybeInterfaceType.isPresent()) {
					CPPType interfaceType = maybeInterfaceType.get();
					char* joinedTypeArguments = this.joinTypeArguments(typeParameters);
					char* thisType = beforeContent + joinedTypeArguments;
					/*this.functions.add(templateString*/ + interfaceType.generate() + " to" + interfaceType.getSimpleName() + "_" + beforeContent + "(void* _ref" + "){" + /*this.generateStatement(thisType*/ + /*" _this*/ = /**(("*/ + thisType + /*"*) _ref)", 1)*/ + this.generateStatement(/*interfaceType.getSimpleName(*/) + "Data" + joinedTypeArguments + /*" data", 1)*/ + /*this.generateStatement("data."*/ + beforeContent.toLowerCase() + /*" = _this", 1)*/ + /*this.generateStatement(
																	 "return "*/ + interfaceType.generate(/*)*/ + " { " + beforeContent + "Tag, " + /*"data }",
																	 1)*/ + System.lineSeparator() + "}" + System.lineSeparator());
				}
				this.forwardDeclarations.add(templateString + "struct " + beforeContent + ";" + System.lineSeparator());
				this.structureNames.push(beforeContent);
				char* generated = dependencies + templateString + "struct " + beforeContent + " {" + fields + System.lineSeparator() + this.compileStatements(content, compileClassSegment_this) + "};" + System.lineSeparator();
				this.structureNames.pop();
				if (variants.isEmpty()) {
					this.structures.add(generated);
				}else {
					this.sealedStructures.add(generated);
				}
				return Optional.of("");
			}
		}
	}
	return Optional.empty();
}
char* joinTypeArguments(List<char*> typeParameters) {
	char* joinedTypeArguments;
	if (typeParameters.isEmpty()) {
		joinedTypeArguments = "";
	}else {
		joinedTypeArguments = "<" + String.join(", ", typeParameters) + ">";
	}
	return joinedTypeArguments;
}
char* generateStatement(int depth) {
	return this.generateWithIndent(content, depth) + ";";
}
char* generateWithIndent(int depth) {
	return this.generateIndent(depth) + content;
}
char* generateIndent(int depth) {
	return System.lineSeparator() + "\t".repeat(depth);
}
boolean isIdentifier(char* input) {/*
		for (int i = 0; i < input.length(); i++) {
			final char next = input.charAt(i);
			if (Character.isLetter(next) || (i != 0 && Character.isDigit(next))) {continue;}
			return false;
		}*/
	return true;
}
auto _lambda36_(auto _ref) {
	return this.compileDefinitionToField(slice);
};
auto _lambda38_(auto _ref) {
	return Placeholder.wrap(definition);
};
auto _lambda41_(auto _ref) {
	return this.compileConstructor(definition);
};
char* compileClassSegment(char* input) {
	if (input.isBlank()) {
		return "";
	}
	Optional<char*> maybeClass = this.compileStructure("class", input);
	if (maybeClass.isPresent()) {
		return maybeClass.get();
	}
	Optional<char*> maybeInterface = this.compileStructure("interface", input);
	if (maybeInterface.isPresent()) {
		return maybeInterface.get();
	}
	Optional<char*> maybeRecord = this.compileStructure("record", input);
	if (maybeRecord.isPresent()) {
		return maybeRecord.get();
	}
	Optional<char*> maybeEnum = this.compileStructure("enum", input);
	if (maybeEnum.isPresent()) {
		return maybeEnum.get();
	}
	if (input.endsWith(";")) {
		char* slice = input.substring(0, input.length() - 1);
		Optional<char*> maybeClassStatement = this.compileEnumValues(slice).or(_lambda36_);
		if (maybeClassStatement.isPresent()) {
			return maybeClassStatement.get();
		}
	}
	int paramStart = input.indexOf("(");
	if (paramStart >= 0) {
		char* definition = input.substring(0, paramStart).strip();
		char* withParams = input.substring(paramStart + 1);
		int paramEnd = withParams.indexOf(")");
		if (paramEnd >= 0) {
			char* params = withParams.substring(0, paramEnd).strip();
			char* withBraces = withParams.substring(paramEnd + 1).strip();
			char* header = this.compileDefinition(definition).or(_lambda41_).orElseGet(_lambda38_);
			char* s = header + "(" + this.compileParameters(params) + ")";
			char* generated;
			if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
				char* content = withBraces.substring(1, withBraces.length() - 1);
				generated = s + " {" + this.compileMethodSegments(content) + System.lineSeparator() + "}" + System.lineSeparator();
			}else {
				generated = s + ";" + System.lineSeparator();
			}
			this.functions.add(generated);
			return "";
		}
	}
	return Placeholder.wrap(input);
}
auto _lambda43_(auto _ref, auto content) {
	return this.generateStatement(content, 1);
};
Optional<char*> compileDefinitionToField(char* slice) {
	return this.compileDefinition(slice).map(_lambda43_);
}
char* compileMethodSegments(char* content) {
	return this.compileStatements(content, compileMethodSegmentOrPlaceholder_this);
}
Optional<char*> compileConstructor(char* input) {
	int i = input.lastIndexOf(" ");
	if (i >= 0) {
		char* name = input.substring(i + 1).strip();
		if (this.isIdentifier(name)) {
			char* structName = this.structureNames.peek();
			return Optional.of(structName + " new_" + structName);
		}
	}else {
		if (this.isIdentifier(input)) {
			char* structName = this.structureNames.peek();
			return Optional.of(structName + " new_" + structName);
		}
	}
	return Optional.empty();
}
auto _lambda47_(auto _ref, auto slice) {
	return /*!slice*/.isEmpty();
};
Optional<char*> compileEnumValues(char* input) {
	List<char*> segments = Arrays.stream(input.split(Pattern.quote(","))).map(strip_char*).filter(_lambda47_).toList();/*

		for (String segment : segments) {
			final String stripped = segment.strip();
			final Optional<String> maybeEnumValue = this.compileEnumValue(stripped);
			if (maybeEnumValue.isPresent()) {
				this.globals.add(maybeEnumValue.get());
			} else {
				return Optional.empty();
			}
		}*/
	return Optional.of("");
}
Optional<char*> compileEnumValue(char* stripped) {
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
auto _lambda49_(auto _ref) {
	return Placeholder.wrap(input);
};
char* compileMethodSegmentOrPlaceholder(char* input) {
	return this.compileMethodSegment(input).orElseGet(_lambda49_);
}
Optional<char*> compileMethodSegment(char* input) {
	char* stripped = input.strip();
	if (/*stripped.isEmpty() || stripped.startsWith("try ") || stripped*/.startsWith("catch ")) {
		return Optional.of("");
	}
	if (stripped.startsWith("{") && stripped.endsWith("}")) {
		char* content = stripped.substring(1, stripped.length() - 1);
		this.depth++;
		char* compiled = this.compileMethodSegments(content);
		/*this.depth--*/;
		return Optional.of("{" + compiled + this.generateIndent(this.depth) + "}");
	}
	Optional<char*> maybeIf = this.compileConditional(stripped, "if");
	if (maybeIf.isPresent()) {
		return maybeIf;
	}
	Optional<char*> maybeWhile = this.compileConditional(stripped, "while");
	if (maybeWhile.isPresent()) {
		return maybeWhile;
	}
	if (stripped.endsWith(";")) {
		char* slice = stripped.substring(0, stripped.length() - 1);
		return Optional.of(this.generateStatement(this.compileMethodStatement(slice), this.depth));
	}
	if (stripped.startsWith("else ")) {
		char* substring = stripped.substring(5);
		return Optional.of("else " + this.compileMethodSegmentOrPlaceholder(substring));
	}
	return Optional.empty();
}
Optional<char*> compileConditional(char* type) {
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
int findConditionEnd(char* withCondition) {
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
auto _lambda51_(auto _ref) {
	return this.compileExpression(substring);
};
auto _lambda53_(auto _ref) {
	return Placeholder.wrap(stripped);
};
auto _lambda56_(auto _ref) {
	return this.compileDefinition(input);
};
char* compileMethodStatement(char* input) {
	char* stripped = input.strip();
	if (stripped.startsWith("return ")) {
		char* slice = stripped.substring("return ".length()).strip();
		return "return " + this.compileExpression(slice);
	}
	int separator = stripped.indexOf('=');
	if (separator >= 0) {
		char* substring = stripped.substring(0, separator).strip();
		char* substring1 = stripped.substring(separator + 1).strip();
		char* s = this.compileDefinition(substring).orElseGet(_lambda51_);
		return s + " = " + this.compileExpression(substring1);
	}
	if (stripped.endsWith("++")) {
		return this.compileExpression(stripped.substring(0, stripped.length() - 2)) + "++";
	}
	if (stripped.equals("break")) {
		return "break";
	}
	if (stripped.equals("continue")) {
		return "continue";
	}
	return this.compileInvocation(stripped).or(_lambda56_).orElseGet(_lambda53_);
}
auto _lambda58_(auto _ref) {
	return this.compileOperator(stripped, "<");
};
auto _lambda61_(auto _ref) {
	return this.compileOperator(stripped, ">=");
};
auto _lambda64_(auto _ref) {
	return this.compileOperator(stripped, "==");
};
auto _lambda67_(auto _ref) {
	return this.compileOperator(stripped, "&&");
};
auto _lambda70_(auto _ref) {
	return this.compileOperator(stripped, "-");
};
char* compileExpression(char* input) {
	char* stripped = input.strip();
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return stripped;
	}
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return stripped;
	}
	Optional<char*> maybeLambda = this.compileLambda(stripped);
	if (maybeLambda.isPresent()) {
		return maybeLambda.get();
	}
	Optional<char*> maybeInvocation = this.compileInvocation(stripped);
	if (maybeInvocation.isPresent()) {
		return maybeInvocation.get();
	}
	int i = stripped.lastIndexOf(".");
	if (i >= 0) {
		char* child = stripped.substring(0, i).strip();
		char* name = stripped.substring(i + 1).strip();
		if (this.isIdentifier(name)) {
			return this.compileExpression(child) + "." + name;
		}
	}
	if (this.isIdentifier(stripped)) {
		return stripped;
	}
	if (stripped.startsWith("switch")) {
		return this.createName("switch");
	}
	Optional<char*> maybeOperator = this.compileOperator(stripped, "+").or(_lambda70_).or(_lambda67_).or(_lambda64_).or(_lambda61_).or(_lambda58_);
	if (maybeOperator.isPresent()) {
		return maybeOperator.get();
	}
	int i2 = stripped.lastIndexOf("::");
	if (i2 >= 0) {
		char* substring = stripped.substring(0, i2);
		char* substring1 = stripped.substring(i2 + 2);
		return substring1 + "_" + this.compileType(substring).map(generate_CPPType).orElse("?");
	}
	if (this.isNumber(stripped)) {
		return stripped;
	}
	return Placeholder.wrap(stripped);
}
auto _lambda75_(auto _ref, auto segment) {
	return "auto " + segment;
};
auto _lambda78_(auto _ref, auto segment) {
	return /*!segment*/.isEmpty();
};
Optional<char*> compileLambda(char* stripped) {
	int arrowIndex = stripped.indexOf("->");
	if (arrowIndex >= 0) {
		char* names = stripped.substring(0, arrowIndex).strip();
		char* content = stripped.substring(arrowIndex + 2);
		char* functionName = this.createName("lambda");
		List<char*> parameters;
		if (this.isIdentifier(names)) {
			parameters = List.of("auto " + names);
		}else 
		if (names.startsWith("(") && names.endsWith(")")) {
			char* slice = names.substring(1, names.length() - 1);
			parameters = this.divide(slice, foldValue_this).map(strip_char*).filter(_lambda78_).map(_lambda75_).toList();
		}else {
			return Optional.empty();
		}
		ArrayList<char*> copy = new_ArrayList<char*>(parameters);
		copy.addFirst("auto _ref");
		/*this.functions.add("auto "*/ + functionName + "(" + String.join(", ", copy) + ") " + this.compileMethodSegment(content).orElseGet(/*(*/) - /*> {
													 final String expression*/ = /*this.compileExpression(content);
													 return "{"*/ + /*this.generateStatement("return "*/ + /*expression, 1)*/ + System.lineSeparator() + "};" + System.lineSeparator(/*);
												 })*/);
		return Optional.of(functionName);
	}
	return Optional.empty();
}
char* createName(char* type) {
	char* s = "_" + type + this.counter + "_";
	this.counter++;
	return s;
}
Optional<char*> compileInvocation(char* stripped) {
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
				final List<String> arguments = this
						.divide(slice.substring(argStart + 1), this::foldValue)
						.map(String::strip)
						.filter(segment -> !segment.isEmpty())
						.map(this::compileExpression)
						.toList();

				final Optional<String> maybeCaller = this.compileCaller(caller);
				if (maybeCaller.isPresent()) {
					return Optional.of(maybeCaller.get() + "(" + String.join(", ", arguments) + ")");
				}
			}
		}*/
	return Optional.empty();
}
Optional<char*> compileCaller(char* caller) {
	char* newCaller;
	if (caller.startsWith("new ")) {
		char* substring = caller.substring("new ".length());
		Optional<CPPType> maybeType = this.compileType(substring);
		if (maybeType.isPresent()) {
			return Optional.of("new_" + maybeType.get().generate());
		}
	}
	return Optional.of(this.compileExpression(caller));
}
Optional<char*> compileOperator(char* separator) {
	int i1 = stripped.indexOf(separator);
	if (i1 >= 0) {
		char* substring = stripped.substring(0, i1);
		char* substring1 = stripped.substring(i1 + separator.length());
		return Optional.of(this.compileExpression(substring) + " " + separator + " " + this.compileExpression(substring1));
	}
	return Optional.empty();
}
boolean isNumber(char* input) {/*
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			if (!Character.isDigit(c)) {
				return false;
			}
		}*/
	return true;
}
char* compileParameters(char* input) {
	if (input.isEmpty()) {
		return "";
	}
	return this.compileDefinitionOrPlaceholder(input);
}
auto _lambda86_(auto _ref) {
	return Placeholder.wrap(input);
};
char* compileDefinitionOrPlaceholder(char* input) {
	return this.compileDefinition(input).orElseGet(_lambda86_);
}
auto _lambda88_(auto _ref, auto cppType) {
	return cppType.generate() + " " + name;
};
auto _lambda90_(auto _ref, auto cppType) {
	return cppType.generate() + " " + name;
};
Optional<char*> compileDefinition(char* input) {
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
		return this.compileType(type).map(_lambda88_);
	}
	return this.compileType(beforeName).map(_lambda90_);
}
auto _lambda98_(auto _ref, auto slice) {
	return /*!slice*/.isEmpty();
};
Optional<CPPType> compileType(char* input) {
	char* stripped = input.strip();
	if (stripped.equals("void")) {
		return Optional.of(CPPPrimitiveType.Void);
	}
	if (stripped.endsWith("[]")) {
		char* slice = stripped.substring(0, stripped.length() - 2);
		return this.compileType(slice).map(new_CPointerType);
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
			List<CPPType> list = this.divide(typeArguments, foldValue_this).map(strip_char*).filter(_lambda98_).map(compileType_this).flatMap(stream_Optional).toList();
			return Optional.of(new_CTemplateType(base, list));
		}
	}
	if (this.isIdentifier(stripped)) {
		if (stripped.equals("public")) {
			return Optional.empty();
		}
		return Optional.of(new_CIdentifier(stripped));
	}
	return Optional.empty();
}
State foldValue(char next) {
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