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
	var joined = this.list.stream().map(generate_CPPType).collect(Collectors.joining(", "));
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
	var replaced = input.replace("/*", "start").replace("*/", "end");
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
		var counter = this.index;
		this.index++;
		var element = this.input.charAt(counter);
		return Optional.of(element);
	}else {
		return Optional.empty();
	}
}
Stream<char*> stream() {
	return this.segments.stream();
}
auto _lambda0_(auto next) {
		var appended = this.append(next);
		return new_Tuple<Character, State>(next, appended);
	}Optional<Tuple<Character, State>> popAndAppendToTuple() {
	return this.pop().map(_lambda0_);
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
	var source = Paths.get(".", "src", "main", "java", "magma", "App.java");
	var input = this.readString(source);
	return _switch1_;
}
auto _lambda2_(auto ()) thisOptional<IOException> compilePath(char* input) {
	var target = source.resolveSibling("App.cpp");
	var output = this.compile(input);
	return this.writeString(target, output).or(_lambda2_.compileNative(target));
}
Optional<> compileNative(Path target) {
	var clang = this.startCommand(List.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));
	return _switch3_;
}
Optional<IOException> waitForProcess(Process process) {
	return _switch4_;
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
	var compiled = this.compileStatements(input, compileRootSegment_this);
	var joinedForwardDeclarations = String.join("", this.forwardDeclarations);
	var joinedFunctions = String.join("", this.functions);
	var joinedStructures = String.join("", this.structures);
	var joinedSealedStructures = String.join("", this.sealedStructures);
	var joinedGlobals = String.join("", this.globals);
	return joinedForwardDeclarations + compiled + joinedStructures + joinedSealedStructures + joinedGlobals + joinedFunctions + "int main(){" + System.lineSeparator() + "\treturn " + "0;" + System.lineSeparator() +
					 "}";
}
char* compileStatements(Function<char*, char*> mapper) {
	return this.divide(input, foldStatement_this).map(mapper).collect(Collectors.joining());
}
Stream<char*> divide(BiFunction<State, Character, State> folder) {
	var current = new_State(input);
	while (true) {
		var maybeNext = current.pop();
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
		var current0 = current.append(next);
		while (true) {
			var maybeTuple = current0.popAndAppendToTuple();
			if (maybeTuple.isEmpty()) {
				break;
			}
			var tuple = maybeTuple.get();
			current0 = tuple.right;
			var nextInQuotes = tuple.left;
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
	var appended = state.append(c);
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
auto _lambda5_(auto ()) Placeholderchar* compileRootSegment(char* input) {
	var stripped = input.strip();
	if (/*stripped.startsWith("package ") || stripped*/.startsWith("import ")) {
		return "";
	}
	return this.compileStructure("class", stripped).orElseGet(_lambda5_.wrap(input));
}
auto _lambda6_(auto slice) /*!slice*/auto _lambda7_(auto slice) /*!slice*/auto _lambda8_(auto slice) "typename " + sliceauto _lambda9_(auto content1) thisauto _lambda10_(auto slice) slice + "Tag"auto _lambda11_(auto slice) System.lineSeparator() + "\t" + slice + typeArguments + " " + slice.toLowerCase() + ";"Optional<char*> compileStructure(char* input) {
	var classIndex = input.indexOf(type);
	if (/*classIndex >= 0*/) {
		var afterKeyword = input.substring(classIndex + type.length());
		var contentStart = afterKeyword.indexOf("{");
		if (/*contentStart >= 0*/) {
			var beforeContent = afterKeyword.substring(0, contentStart).strip();
			var withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
			if (withEnd.endsWith("}")) {
				var content = withEnd.substring(0, withEnd.length() - 1);
				var permitsIndex = beforeContent.indexOf("permits");
				List<char*> variants = Collections.emptyList();
				if (/*permitsIndex >= 0*/) {
					var variantsArray = beforeContent.substring(permitsIndex + "permits".length()).split(Pattern.quote(","));
					beforeContent = beforeContent.substring(0, permitsIndex).strip();
					variants = Arrays.stream(variantsArray).map(strip_char*).filter(_lambda6_.isEmpty()).toList();
				}
				var implementsIndex = beforeContent.indexOf("implements");
				Optional<CPPType> maybeInterfaceType = Optional.empty();
				if (/*implementsIndex >= 0*/) {
					var slice = beforeContent.substring(implementsIndex + "implements".length()).strip();
					maybeInterfaceType = this.compileType(slice);
					beforeContent = beforeContent.substring(0, implementsIndex).strip();
				}
				if (/*beforeContent.endsWith(")"*/) /*) {
						final var slice = beforeContent.substring(0, beforeContent.length() - 1);
						final var i = slice.indexOf("(");
						if (i >= 0) {
							final var params = slice.substring(i + 1);
							beforeContent = slice.substring(0, i).strip();
						}
					}*/
				List<char*> typeParameters = new_ArrayList<char*>();
				if (beforeContent.endsWith(">")) {
					var withoutEnd = beforeContent.substring(0, beforeContent.length() - 1);
					var typeParamStart = withoutEnd.indexOf("<");
					if (/*typeParamStart >= 0*/) {
						beforeContent = withoutEnd.substring(0, typeParamStart);
						var typeParamsArray = withoutEnd.substring(typeParamStart + 1).split(Pattern.quote(","));
						typeParameters = Arrays.stream(typeParamsArray).map(strip_char*).filter(_lambda7_.isEmpty()).toList();
					}
				}
				if (/*!this*/.isIdentifier(beforeContent)) {
					return Optional.empty();
				}
				char* templateString;
				if (typeParameters.isEmpty()) {
					templateString = "";
				}else {
					var collect = typeParameters.stream().map(_lambda8_).collect(Collectors.joining(", "));
					templateString = "template <" + collect + ">" + System.lineSeparator();
				}
				char* dependencies;
				if (variants.isEmpty()) {
					dependencies = "";
				}else {
					var enumFields = variants.stream().map(_lambda10_).map(_lambda9_.generateWithIndent(content1, 1)).collect(Collectors.joining(","));
					var typeArguments = this.joinTypeArguments(typeParameters);
					var unionFields = variants.stream().map(_lambda11_).collect(Collectors.joining());
					dependencies = "enum " + beforeContent + "Tag {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator() + templateString + "union " + beforeContent + "Data {" + unionFields + System.lineSeparator() + "};" + System.lineSeparator();
				}
				char* fields;
				if (variants.isEmpty()) {
					fields = "";
				}else {
					fields = /*this.generateStatement(beforeContent*/ + /*"Tag tag", 1)*/ + this.generateStatement(beforeContent + "Data " + "data", 1);
				}
				if (maybeInterfaceType.isPresent()) {
					var interfaceType = maybeInterfaceType.get();
					var joinedTypeArguments = this.joinTypeArguments(typeParameters);
					var thisType = beforeContent + joinedTypeArguments;
					/*this.functions.add(templateString*/ + interfaceType.generate() + " to" + interfaceType.getSimpleName() + "_" + beforeContent + "(void* _ref" + "){" + /*this.generateStatement(thisType*/ + /*" _this*/ = /**(("*/ + thisType + /*"*) _ref)", 1)*/ + this.generateStatement(/*interfaceType.getSimpleName(*/) + "Data" + joinedTypeArguments + /*" data", 1)*/ + /*this.generateStatement("data."*/ + beforeContent.toLowerCase() + /*" = _this", 1)*/ + /*this.generateStatement(
																	 "return "*/ + interfaceType.generate(/*)*/ + " { " + beforeContent + "Tag, " + /*"data }",
																	 1)*/ + System.lineSeparator() + "}" + System.lineSeparator());
				}
				this.forwardDeclarations.add(templateString + "struct " + beforeContent + ";" + System.lineSeparator());
				this.structureNames.push(beforeContent);
				var generated = dependencies + templateString + "struct " + beforeContent + " {" + fields + System.lineSeparator() + this.compileStatements(content, compileClassSegment_this) + "};" + System.lineSeparator();
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
		for (var i = 0; i < input.length(); i++) {
			final var next = input.charAt(i);
			if (Character.isLetter(next) || (i != 0 && Character.isDigit(next))) {continue;}
			return false;
		}*/
	return true;
}
auto _lambda12_(auto ()) thisauto _lambda13_(auto ()) Placeholderauto _lambda14_(auto ()) thischar* compileClassSegment(char* input) {
	if (input.isBlank()) {
		return "";
	}
	var maybeClass = this.compileStructure("class", input);
	if (maybeClass.isPresent()) {
		return maybeClass.get();
	}
	var maybeInterface = this.compileStructure("interface", input);
	if (maybeInterface.isPresent()) {
		return maybeInterface.get();
	}
	var maybeRecord = this.compileStructure("record", input);
	if (maybeRecord.isPresent()) {
		return maybeRecord.get();
	}
	var maybeEnum = this.compileStructure("enum", input);
	if (maybeEnum.isPresent()) {
		return maybeEnum.get();
	}
	if (input.endsWith(";")) {
		var slice = input.substring(0, input.length() - 1);
		var maybeClassStatement = this.compileEnumValues(slice).or(_lambda12_.compileDefinitionToField(slice));
		if (maybeClassStatement.isPresent()) {
			return maybeClassStatement.get();
		}
	}
	var paramStart = input.indexOf("(");
	if (/*paramStart >= 0*/) {
		var definition = input.substring(0, paramStart).strip();
		var withParams = input.substring(paramStart + 1);
		var paramEnd = withParams.indexOf(")");
		if (/*paramEnd >= 0*/) {
			var params = withParams.substring(0, paramEnd).strip();
			var withBraces = withParams.substring(paramEnd + 1).strip();
			var header = this.compileDefinition(definition).or(_lambda14_.compileConstructor(definition)).orElseGet(_lambda13_.wrap(definition));
			var s = header + "(" + this.compileParameters(params) + ")";
			char* generated;
			if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
				var content = withBraces.substring(1, withBraces.length() - 1);
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
auto _lambda15_(auto content) thisOptional<char*> compileDefinitionToField(char* slice) {
	return this.compileDefinition(slice).map(_lambda15_.generateStatement(content, 1));
}
char* compileMethodSegments(char* content) {
	return this.compileStatements(content, compileMethodSegmentOrPlaceholder_this);
}
Optional<char*> compileConstructor(char* input) {
	var i = input.lastIndexOf(" ");
	if (/*i >= 0*/) {
		var name = input.substring(i + 1).strip();
		if (this.isIdentifier(name)) {
			var structName = this.structureNames.peek();
			return Optional.of(structName + " new_" + structName);
		}
	}else {
		if (this.isIdentifier(input)) {
			var structName = this.structureNames.peek();
			return Optional.of(structName + " new_" + structName);
		}
	}
	return Optional.empty();
}
auto _lambda16_(auto slice) /*!slice*/Optional<char*> compileEnumValues(char* input) {
	var segments = Arrays.stream(input.split(Pattern.quote(","))).map(strip_char*).filter(_lambda16_.isEmpty()).toList();/*

		for (var segment : segments) {
			final var stripped = segment.strip();
			final var maybeEnumValue = this.compileEnumValue(stripped);
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
			final var slice = stripped.substring(0, stripped.length() - 1);
			final var i = slice.indexOf("(");
			if (i >= 0) {
				final var name = slice.substring(0, i).strip();
				final var arguments = slice.substring(i + 1);
				if (this.isIdentifier(name)) {
					final var structureName = this.structureNames.peek();
					return Optional.of(structureName + " " + name + "Value = " + structureName + " { " + arguments + " };" +
														 System.lineSeparator());
				}
			}
		}*/
	return Optional.empty();
}
auto _lambda17_(auto ()) Placeholderchar* compileMethodSegmentOrPlaceholder(char* input) {
	return this.compileMethodSegment(input).orElseGet(_lambda17_.wrap(input));
}
Optional<char*> compileMethodSegment(char* input) {
	var stripped = input.strip();
	if (/*stripped.isEmpty() || stripped.startsWith("try ") || stripped*/.startsWith("catch ")) {
		return Optional.of("");
	}
	if (stripped.startsWith("{") && stripped.endsWith("}")) {
		var content = stripped.substring(1, stripped.length() - 1);
		this.depth++;
		var compiled = this.compileMethodSegments(content);
		/*this.depth--*/;
		return Optional.of("{" + compiled + this.generateIndent(this.depth) + "}");
	}
	var maybeIf = this.compileConditional(stripped, "if");
	if (maybeIf.isPresent()) {
		return maybeIf;
	}
	var maybeWhile = this.compileConditional(stripped, "while");
	if (maybeWhile.isPresent()) {
		return maybeWhile;
	}
	if (stripped.endsWith(";")) {
		var slice = stripped.substring(0, stripped.length() - 1);
		return Optional.of(this.generateStatement(this.compileMethodStatement(slice), this.depth));
	}
	if (stripped.startsWith("else ")) {
		var substring = stripped.substring(5);
		return Optional.of("else " + this.compileMethodSegmentOrPlaceholder(substring));
	}
	return Optional.empty();
}
Optional<char*> compileConditional(char* type) {
	if (input.startsWith(type)) {
		var substring = input.substring(type.length()).strip();/*
			if (substring.startsWith("(")) {
				final var withCondition = substring.substring(1);
				final var conditionEnd = this.findConditionEnd(withCondition);

				if (conditionEnd >= 0) {
					final var condition = withCondition.substring(0, conditionEnd).strip();
					final var substring2 = withCondition.substring(conditionEnd + 1).strip();
					return Optional.of(this.generateIndent(this.depth) + type + " (" + this.compileExpression(condition) + ") " +
														 this.compileMethodSegmentOrPlaceholder(substring2));
				}
			}*/
	}
	return Optional.empty();
}
int findConditionEnd(char* withCondition) {
	int conditionEnd =  - 1;
	var depth = 0;/*
		for (var i = 0; i < withCondition.length(); i++) {
			final var c = withCondition.charAt(i);
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
auto _lambda18_(auto ()) thisauto _lambda19_(auto ()) Placeholderauto _lambda20_(auto ()) thischar* compileMethodStatement(char* input) {
	var stripped = input.strip();
	if (stripped.startsWith("return ")) {
		var slice = stripped.substring("return ".length()).strip();
		return "return " + this.compileExpression(slice);
	}
	var separator = stripped.indexOf('=');
	if (/*separator >= 0*/) {
		var substring = stripped.substring(0, separator).strip();
		var substring1 = stripped.substring(separator + 1).strip();
		var s = this.compileDefinition(substring).orElseGet(_lambda18_.compileExpression(substring));
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
	return this.compileInvocation(stripped).or(_lambda20_.compileDefinition(input)).orElseGet(_lambda19_.wrap(stripped));
}
auto _lambda21_(auto ()) thisauto _lambda22_(auto ()) thisauto _lambda23_(auto ()) thisauto _lambda24_(auto ()) thisauto _lambda25_(auto ()) thischar* compileExpression(char* input) {
	var stripped = input.strip();
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return stripped;
	}
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return stripped;
	}
	var maybeInvocation = this.compileInvocation(stripped);
	if (maybeInvocation.isPresent()) {
		return maybeInvocation.get();
	}
	var i = stripped.lastIndexOf(".");
	if (/*i >= 0*/) {
		var child = stripped.substring(0, i).strip();
		var name = stripped.substring(i + 1).strip();
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
	var arrowIndex = stripped.indexOf("->");
	if (/*arrowIndex >= 0*/) {
		var name = stripped.substring(0, arrowIndex).strip();
		var content = stripped.substring(arrowIndex + 2);
		var functionName = this.createName("lambda");
		this.functions.add("auto " + functionName + "(auto " + name + ") " + this.compileMethodSegment(content).orElseGet(_lambda21_.compileExpression(content)));
		return functionName;
	}
	var maybeOperator = this.compileOperator(stripped, "+").or(_lambda25_.compileOperator(stripped, "-")).or(_lambda24_.compileOperator(stripped, "&&")).or(_lambda23_.compileOperator(stripped, "==")).or(_lambda22_.compileOperator(stripped, "<"));
	if (maybeOperator.isPresent()) {
		return maybeOperator.get();
	}
	var i2 = stripped.lastIndexOf("::");
	if (/*i2 >= 0*/) {
		var substring = stripped.substring(0, i2);
		var substring1 = stripped.substring(i2 + 2);
		return substring1 + "_" + this.compileType(substring).map(generate_CPPType).orElse("?");
	}
	if (this.isNumber(stripped)) {
		return stripped;
	}
	return Placeholder.wrap(stripped);
}
char* createName(char* type) {
	var s = "_" + type + this.counter + "_";
	this.counter++;
	return s;
}
Optional<char*> compileInvocation(char* stripped) {
	if (/*stripped.endsWith(")"*/) /*) {
			final var slice = stripped.substring(0, stripped.length() - 1);
			int argStart = -1;
			var depth = 0;
			for (int i = 0; i < slice.length(); i++) {
				final var next = slice.charAt(i);
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
				final var caller = slice.substring(0, argStart).strip();
				final var arguments = this
						.divide(slice.substring(argStart + 1), this::foldValue)
						.map(String::strip)
						.filter(segment -> !segment.isEmpty())
						.map(this::compileExpression)
						.toList();

				final var maybeCaller = this.compileCaller(caller);
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
		var substring = caller.substring("new ".length());
		var maybeType = this.compileType(substring);
		if (maybeType.isPresent()) {
			return Optional.of("new_" + maybeType.get().generate());
		}
	}
	return Optional.of(this.compileExpression(caller));
}
Optional<char*> compileOperator(char* separator) {
	var i1 = stripped.indexOf(separator);
	if (/*i1 >= 0*/) {
		var substring = stripped.substring(0, i1);
		var substring1 = stripped.substring(i1 + separator.length());
		return Optional.of(this.compileExpression(substring) + " " + separator + " " + this.compileExpression(substring1));
	}
	return Optional.empty();
}
boolean isNumber(char* input) {/*
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
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
auto _lambda26_(auto ()) Placeholderchar* compileDefinitionOrPlaceholder(char* input) {
	return this.compileDefinition(input).orElseGet(_lambda26_.wrap(input));
}
auto _lambda27_(auto cppType) cppType.generate() + " " + nameauto _lambda28_(auto cppType) cppType.generate() + " " + nameOptional<char*> compileDefinition(char* input) {
	var nameSeparator = input.lastIndexOf(" ");
	if (nameSeparator < 0) {
		return Optional.empty();
	}
	var beforeName = input.substring(0, nameSeparator);
	var name = input.substring(nameSeparator + 1).strip();
	if (/*!this*/.isIdentifier(name)) {
		return Optional.empty();
	}
	int typeSeparator =  - 1;
	var depth = 0;/*
		for (var i = 0; i < beforeName.length(); i++) {
			final var c = beforeName.charAt(i);
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
	if (/*typeSeparator >= 0*/) {
		var type = beforeName.substring(typeSeparator + 1).strip();
		return this.compileType(type).map(_lambda27_);
	}
	return this.compileType(beforeName).map(_lambda28_);
}
auto _lambda29_(auto slice) /*!slice*/Optional<CPPType> compileType(char* input) {
	var stripped = input.strip();
	if (stripped.equals("void")) {
		return Optional.of(CPPPrimitiveType.Void);
	}
	if (stripped.endsWith("[]")) {
		var slice = stripped.substring(0, stripped.length() - 2);
		return this.compileType(slice).map(new_CPointerType);
	}
	if (stripped.equals("String")) {
		return Optional.of(new_CPointerType(CPPPrimitiveType.Char));
	}
	if (stripped.endsWith(">")) {
		var withoutEnd = stripped.substring(0, stripped.length() - 1);
		var i = withoutEnd.indexOf("<");
		if (/*i >= 0*/) {
			var base = withoutEnd.substring(0, i);
			var typeArguments = withoutEnd.substring(i + 1);
			var list = this.divide(typeArguments, foldValue_this).map(strip_char*).filter(_lambda29_.isEmpty()).map(compileType_this).flatMap(stream_Optional).toList();
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
	var appended = state.append(next);
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