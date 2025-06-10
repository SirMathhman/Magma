struct Path {
	struct Result readString();
	Option<struct IOError> write(Array<char> content);
	struct Path resolveSibling(Array<char> name);
};
struct IOError {
	Array<char> display();
};
struct Result {
};
struct Actual {
};
struct Type extends Node {
	Array<char> generate();
};
struct Node {
	Array<char> generate();
};
struct CDefinition {
	Array<char> generate();
};
struct Lists {
};
struct private DivideState(Array<char> input, List<Array<char>> segments, Array<char> buffer, int depth, int index) {
	this.input = input;
	this.index = index;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
struct public DivideState(Array<char> input) {
	this(input, Lists.empty(), "", 0, 0);
}
int isLevel() {
	return this.depth == 0;
}
struct DivideState append(char c) {
	this.buffer = this.buffer + c;
	return this;
}
struct DivideState advance() {
	this.segments = this.segments.addLast(this.buffer);
	this.buffer = "";
	return this;
}
struct DivideState enter() {
	this.depth = this.depth + 1;
	return this;
}
struct DivideState exit() {
	this.depth = this.depth - 1;
	return this;
}
int isShallow() {
	return this.depth == 1;
}
Option<Tuple<struct DivideState, char>> pop() {
	if (this.index < this.input.length()) {
		auto c = this.input.charAt(this.index);
		auto next = struct DivideState(this.input, this.segments, this.buffer, this.depth, this.index + 1);
		return Some<struct >(Tuple<struct >(next, c));
	}
	else {
		return None<struct >();
	}
}
Option<char> peek() {
	if (this.index < this.input.length()) {
		return Some<struct >(this.input.charAt(this.index));
	}
	return None<struct >();
}
Option<struct DivideState> append() {
	return this.pop().map(auto lambda(auto tuple){
	return tuple.left.append(tuple.right);
}
);
}
Option<Tuple<struct DivideState, char>> popAndAppendToTuple() {
	return this.pop().map(auto lambda(auto tuple){
	return Tuple<struct >(tuple.left.append(tuple.right), tuple.right);
}
);
}
Option<struct DivideState> popAndAppendToOption() {
	return this.popAndAppendToTuple().map(left);
}
struct DivideState {
	Array<char> input;
	int index;
	List<Array<char>> segments;
	Array<char> buffer;
	int depth;
	struct private DivideState(Array<char> input, List<Array<char>> segments, Array<char> buffer, int depth, int index);
	struct public DivideState(Array<char> input);
	int isLevel();
	struct DivideState append(char c);
	struct DivideState advance();
	struct DivideState enter();
	struct DivideState exit();
	int isShallow();
	Option<Tuple<struct DivideState, char>> pop();
	Option<char> peek();
	Option<struct DivideState> append();
	Option<Tuple<struct DivideState, char>> popAndAppendToTuple();
	Option<struct DivideState> popAndAppendToOption();
};
Array<char> generate() {
	return "struct " + this.name;
}
struct ClassDefinition {
	Array<char> generate();
};
struct JavaDefinition {
};
struct Ok(String value) implements Result {
};
struct Err(IOError error) implements Result {
};
struct Path get(Array<char> first, Array<Array<char>> more);
struct Paths {
};
Array<char> generate() {
	return this.generateWithName("");
}
Array<char> generateWithName(Array<char> name) {
	auto joined = generateValueNodes(this.argumentTypes);
	return this.returnType.generate() + " (*" + name + ")(" + joined + ")";
}
struct FunctionType(List<Type> argumentTypes, Type returnType) implements Type {
	Array<char> generate();
	Array<char> generateWithName(Array<char> name);
};
Array<char> generate() {
	auto outputArguments = generateValueNodes(this.elements);
	return this.base + " < " + outputArguments + ">";
}
struct TemplateType(String base, List<Type> elements) implements Type {
	Array<char> generate();
};
Array<char> generate() {
	return generatePlaceholder(this.input);
}
struct Placeholder(String input) implements Type {
	Array<char> generate();
};
Array<char> generate() {
	return "struct " + this.name;
}
struct StructType(String name) implements Type {
	Array<char> generate();
};
Array<char> generate() {
	return this.type().generate() + " " + this.name();
}
struct SimpleCDefinition(Type type, String name) implements CDefinition {
	Array<char> generate();
};
struct public CFunctionDefinition(struct FunctionType type, struct JavaDefinition definition) {
	this.type = type;
	this.definition = definition;
}
Array<char> generate() {
	return this.type.generateWithName(this.definition.name);
}
struct CFunctionDefinition implements CDefinition {
	struct FunctionType type;
	struct JavaDefinition definition;
	struct public CFunctionDefinition(struct FunctionType type, struct JavaDefinition definition);
	Array<char> generate();
};
void main(Array<Array<char>> args) {
	auto source = Paths.get(".", "src", "magma", "Main.java");
	source.readString().match(auto lambda(auto input){
	return compileAndWrite(input, source);
}
, new).ifPresent(auto lambda(auto error){
	return printErroneousLine(error.display());
}
);
}
void printErroneousLine(Array<char> content);
Option<struct IOError> compileAndWrite(Array<char> input, struct Path source) {
	auto target = source.resolveSibling("Main.c");
	auto string = compile(input);
	return target.write(string);
}
Array<char> compile(Array<char> input) {
	return compileStatements(input, compileRootSegment);
}
Array<char> compileStatements(Array<char> input, Array<char> (*mapper)(Array<char>)) {
	return compileAll(input, foldStatements, mapper, mergeStatements);
}
Array<char> compileAll(Array<char> input, struct DivideState (*folder)(struct DivideState, char), Array<char> (*mapper)(Array<char>), Array<char> (*merger)(Array<char>, Array<char>)) {
	return generateAll(merger, parseAll(input, folder, mapper));
}
Array<char> generateAll(Array<char> (*merger)(Array<char>, Array<char>), List<Array<char>> stringList) {
	return stringList.iter().fold("", merger);
}
Array<char> mergeStatements(Array<char> buffer, Array<char> element) {
	return buffer + element;
}
List<Array<char>> divideStatements(Array<char> input) {
	return divide(input, foldStatements);
}
List<Array<char>> divide(Array<char> input, struct DivideState (*folder)(struct DivideState, char)) {
	auto current = struct DivideState(input);
	while (true) {
		auto maybeNext = current.pop().map(auto lambda(auto tuple){
	return getObject(folder, tuple);
}
);
		if (maybeNext.isPresent()) {
			current = maybeNext.get();
		}
		else {
			break;
		}
	}
	return current.advance().segments;
}
struct DivideState getObject(struct DivideState (*folder)(struct DivideState, char), Tuple<struct DivideState, char> tuple) {
	auto currentState = tuple.left;
	auto c = tuple.right;
	return foldSingleQuotes(currentState, c).or(auto lambda(auto ){
	return foldDoubleQuotes(currentState, c);
}
).orElseGet(auto lambda(auto ){
	return folder.apply(currentState, c);
}
);
}
Option<struct DivideState> foldDoubleQuotes(struct DivideState state, char c) {
	if (c != '\"') {
		return None<struct >();
	}
	auto current = state.append(c);
	while (true) {
		auto tuple = current.popAndAppendToTuple().orElse(Tuple<struct >(state, '\0'));
		current = tuple.left;
		auto next = tuple.right;
		if (next == '\\') {
			current = current.popAndAppendToOption().orElse(current);
		}
		if (next == '\"') {
			break;
		}
	}
	return Some<struct >(current);
}
Option<struct DivideState> foldSingleQuotes(struct DivideState currentState, char c) {
	if (c != '\'') {
		return None<struct >();
	}
	auto appended = currentState.append(c);
	return appended.popAndAppendToTuple().flatMap(foldEscaped).flatMap(popAndAppendToOption);
}
Option<struct DivideState> foldEscaped(Tuple<struct DivideState, char> tuple) {
	if (tuple.right == '\\') {
		return tuple.left.popAndAppendToOption();
	}
	return Some<struct >(tuple.left);
}
struct DivideState foldStatements(struct DivideState state, char c) {
	auto appended = state.append(c);
	if (c == ';' && appended.isLevel()) {
		return appended.advance();
	}
	if (c == '}' && appended.isShallow()) {
		return appended.advance().exit();
	}
	if (c == '{' || c == '(') {
		return appended.enter();
	}
	if (c == '}' || c == ')') {
		return appended.exit();
	}
	return appended;
}
Array<char> compileRootSegment(Array<char> input) {
	auto stripped = input.strip();
	if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
		return "";
	}
	return compileClass(input).map(auto lambda(auto tuple){
	auto joined = tuple.left.iter().collect(struct Joiner()).orElse("");
	return joined + tuple.right;
}
).orElseGet(auto lambda(auto ){
	return generatePlaceholder(input);
}
);
}
Option<Tuple<List<Array<char>>, Array<char>>> compileClass(Array<char> input) {
	auto contentStart = input.indexOf('{');
	if (contentStart >= 0) {
		auto beforeContent = input.substring(0, contentStart);
		auto withEnd = input.substring(contentStart + "{".length()).strip();
		if (withEnd.endsWith("}")) {
			auto maybeHeader = compileClassDefinition(beforeContent);
			if (maybeHeader.isPresent()) {
				auto definition = maybeHeader.get();
				auto others = compileClassWithDefinition(definition, withEnd);
				return Some<struct >(Tuple<struct >(others, ""));
			}
		}
	}
	return None<struct >();
}
List<Array<char>> compileClassWithDefinition(struct ClassDefinition definition, Array<char> withEnd) {
	if (definition.typeParameters.containsElements() || definition.annotations.contains("Actual")) {
		return Lists.empty();
	}
	auto inputContent = withEnd.substring(0, withEnd.length() - "}".length());
	auto segments = divideStatements(inputContent);
	auto tuple = segments.iter().map(compileClassSegment).collect(TupleCollector<struct >(ListBulkCollector<struct >(), struct Joiner()));
	auto others = tuple.left;
	auto output = tuple.right.orElse("");
	auto generatedHeader = definition.generate();
	auto generated = generatedHeader + " {" + output + "\n};\n";
	return others.addLast(generated);
}
Tuple<List<Array<char>>, Array<char>> compileClassSegment(Array<char> input) {
	return compileWhitespace(input). < Tuple < List < /*String>, String>>map*/(auto lambda(auto result){
	return Tuple<struct >(Lists.empty(), result);
}
).or(auto lambda(auto ){
	return compileField(input);
}
).or(auto lambda(auto ){
	return compileClass(input);
}
).or(auto lambda(auto ){
	return compileMethod(input);
}
).orElseGet(auto lambda(auto ){
	return Tuple<struct >(Lists.empty(), generatePlaceholder(input));
}
);
}
Option<Tuple<List<Array<char>>, Array<char>>> compileMethod(Array<char> input) {
	auto paramStart = input.indexOf("(");
	if (paramStart >= 0) {
		auto beforeParams = input.substring(0, paramStart);
		auto withParams = input.substring(paramStart + "(".length());
		auto paramEnd = withParams.indexOf(")");
		if (paramEnd >= 0) {
			auto params = withParams.substring(0, paramEnd);
			auto withBraces = withParams.substring(paramEnd + ")".length()).strip();
			auto maybeDefinition = parseMethodDefinition(beforeParams);
			if (maybeDefinition.isPresent()) {
				auto definition = maybeDefinition.get();
				if (definition.typeParameters.containsElements()) {
					return Some<struct >(Tuple<struct >(Lists.empty(), ""));
				}
				auto compiledParameters = compileValues(params, compileParameter);
				auto header = transformDefinition(definition).generate() + "(" + compiledParameters + ")";
				if (withBraces.equals(";")) {
					auto generated = header + ";";
					return Some<struct >(Tuple<struct >(Lists.empty(), "\n\t" + generated));
				}
				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					return Some<struct >(compileMethodWithBody(definition, header, withBraces));
				}
				return None<struct >();
			}
		}
	}
	return None<struct >();
}
struct CDefinition transformDefinition(struct JavaDefinition definition) {
	if (/*definition.type instanceof FunctionType type*/) {
		return struct CFunctionDefinition(type, definition);
	}
	return struct SimpleCDefinition(definition.type, definition.name);
}
Tuple<List<Array<char>>, Array<char>> compileMethodWithBody(struct JavaDefinition definition, Array<char> header, Array<char> withBraces) {
	if (definition.annotations.contains("Actual")) {
		return Tuple<struct >(Lists.of(header + ";\n"), "");
	}
	auto inputContent = withBraces.substring(1, withBraces.length() - 1).strip();
	auto outputContent = compileFunctionSegments(inputContent, 1);
	auto withinStructure = /* definition.modifiers.contains("static") ? "" : "\n\t" */ + header + ";";
	return Tuple<struct >(Lists.of(header + " {" + outputContent + "\n}" + "\n"), withinStructure);
}
Array<char> compileFunctionSegments(Array<char> input, int depth) {
	return compileStatements(input, /*input1 */ - /*> compileFunctionSegment*/(/*input1*/, depth));
}
Option<struct JavaDefinition> parseMethodDefinition(Array<char> input) {
	return parseDefinition(input).or(auto lambda(auto ){
	return parseConstructor(input);
}
);
}
Option<struct JavaDefinition> parseConstructor(Array<char> input) {
	auto separator = input.lastIndexOf(" ");
	if (separator >= 0) {
		auto name = input.substring(separator + " ".length());
		return Some<struct >(struct JavaDefinition(Lists.empty(), Lists.of("static"), Lists.empty(), struct StructType(name), "new"));
	}
	else {
		return None<struct >();
	}
}
Array<char> compileValues(Array<char> input, Array<char> (*mapper)(Array<char>)) {
	return generateValues(parseValues(input, mapper));
}
Array<char> generateValues(List<Array<char>> elements) {
	return generateAll(mergeValues, elements);
}
Array<char> compileFunctionSegment(Array<char> input, int depth) {
	return compileWhitespace(input).or(auto lambda(auto ){
	return compileFunctionStatement(input, depth);
}
).or(auto lambda(auto ){
	return compileBlock(input, depth);
}
).orElseGet(auto lambda(auto ){
	return generatePlaceholder(input);
}
);
}
Option<Array<char>> compileBlock(Array<char> input, int depth) {
	auto stripped = input.strip();
	if (stripped.endsWith("}")) {
		auto withoutEnd = stripped.substring(0, stripped.length() - "}".length());
		return divide(withoutEnd, foldBlockStart).popFirst().flatMap(auto lambda(auto divisions){
	auto left = divisions.left;
	if (left.endsWith("{")) {
		auto header = left.substring(0, left.length() - "{".length());
		auto inputContent = divisions.right.iter().collect(struct Joiner()).orElse("");
		auto outputContent = compileFunctionSegments(inputContent, depth + 1);
		auto indent = "\n" + "\t".repeat(depth);
		return Some<struct >(indent + compileBlockHeader(header) + " {" + outputContent + indent + "}");
	}
	else {
		return None<struct >();
	}
}
);
	}
	return None<struct >();
}
struct DivideState foldBlockStart(struct DivideState state, char c) {
	auto appended = state.append(c);
	if (c == '{') {
		auto entered = appended.enter();
		if (entered.isShallow()) {
			return entered.advance();
		}
		return entered;
	}
	if (c == '}') {
		return appended.exit();
	}
	return appended;
}
Array<char> compileBlockHeader(Array<char> input) {
	auto stripped = input.strip();
	if (stripped.equals("else")) {
		return "else";
	}
	if (stripped.endsWith(")")) {
		auto withoutEnd = stripped.substring(0, stripped.length() - ")".length());
		auto conditionStart = withoutEnd.indexOf("(");
		if (conditionStart >= 0) {
			auto beforeCondition = withoutEnd.substring(0, conditionStart);
			auto conditionString = withoutEnd.substring(conditionStart + "(".length());
			auto compiled = compileValue(conditionString);
			auto strippedCompiled = beforeCondition.strip();
			/*final var beforeContent = switch*/ (strippedCompiled) {
				/*case "if", "while" -> strippedCompiled*/;
				auto lambda(auto default){
	return generatePlaceholder;
}
(strippedCompiled);
			}
			/**/;
			return beforeContent + " (" + compiled + ")";
		}
	}
	return generatePlaceholder(stripped);
}
Option<Array<char>> compileFunctionStatement(Array<char> input, int depth) {
	auto stripped = input.strip();
	if (stripped.endsWith(";")) {
		auto withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		return Some<struct >("\n" + "\t".repeat(depth) + compileFunctionStatementValue(withoutEnd) + ";");
	}
	return None<struct >();
}
Array<char> compileFunctionStatementValue(Array<char> input) {
	auto stripped = input.strip();
	if (stripped.equals("break")) {
		return "break";
	}
	if (stripped.startsWith("return ")) {
		auto value = stripped.substring("return ".length());
		return "return " + compileValue(value);
	}
	auto i = stripped.indexOf("=");
	if (i >= 0) {
		auto destinationString = stripped.substring(0, i);
		/*final var substring1 */ = stripped.substring(i + "=".length());
		auto destination = parseDefinition(destinationString).map(auto lambda(auto javaDefinition){
	return transformDefinition(javaDefinition).generate();
}
).orElseGet(auto lambda(auto ){
	return compileValue(destinationString);
}
);
		return destination + " = " + compileValue(/*substring1*/);
	}
	return compileInvokable(stripped).orElseGet(auto lambda(auto ){
	return generatePlaceholder(input);
}
);
}
Option<Array<char>> compileInvokable(Array<char> input) {
	auto stripped = input.strip();
	if (stripped.endsWith(")")) {
		auto withoutEnd = stripped.substring(0, stripped.length() - ")".length());
		auto divisions = divide(withoutEnd, foldInvocationStart);
		return divisions.popLast().flatMap(auto lambda(auto tuple){
	auto joined = tuple.left.iter().collect(struct Joiner()).orElse("");
	auto arguments = tuple.right;
	if (joined.endsWith("(")) {
		auto oldCaller = joined.substring(0, joined.length() - 1);
		auto newCaller = /*oldCaller.startsWith("new ")
                            ? compileConstruction(oldCaller)
                            : compileValue*/(oldCaller);
		return Some<struct >(newCaller + "(" + compileValues(arguments, compileValue) + ")");
	}
	else {
		return None<struct >();
	}
}
);
	}
	return None<struct >();
}
struct DivideState foldInvocationStart(struct DivideState state, char c) {
	auto appended = state.append(c);
	if (c == '(') {
		auto entered = appended.enter();
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
Array<char> compileConstruction(Array<char> caller) {
	auto type = caller.substring("new ".length());
	return compileTypeOrPlaceholder(type);
}
Array<char> compileValue(Array<char> input) {
	return compileLambda(input).or(auto lambda(auto ){
	return compileInvokable(input);
}
).or(auto lambda(auto ){
	return compileAccess(input);
}
).or(auto lambda(auto ){
	return compileOperator(input, " != ");
}
).or(auto lambda(auto ){
	return compileOperator(input, " == ");
}
).or(auto lambda(auto ){
	return compileOperator(input, " + ");
}
).or(auto lambda(auto ){
	return compileOperator(input, " - ");
}
).or(auto lambda(auto ){
	return compileOperator(input, " && ");
}
).or(auto lambda(auto ){
	return compileOperator(input, " || ");
}
).or(auto lambda(auto ){
	return compileOperator(input, " < ");
}
).or(auto lambda(auto ){
	return compileOperator(input, " >= ");
}
).or(auto lambda(auto ){
	return compileSymbol(input);
}
).or(auto lambda(auto ){
	return compileNumber(input);
}
).or(auto lambda(auto ){
	return compileChar(input);
}
).or(auto lambda(auto ){
	return compileString(input);
}
).or(auto lambda(auto ){
	return compileMethodReference(input);
}
).orElseGet(auto lambda(auto ){
	return generatePlaceholder(input);
}
);
}
Option<Array<char>> compileMethodReference(Array<char> input) {
	auto i = input.lastIndexOf("::");
	if (i >= 0) {
		auto substring = input.substring(i + "::".length());
		return Some<struct >(substring);
	}
	else {
		return None<struct >();
	}
}
Option<Array<char>> compileChar(Array<char> input) {
	auto stripped = input.strip();
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return Some<struct >(stripped);
	}
	else {
		return None<struct >();
	}
}
Option<Array<char>> compileLambda(Array<char> input) {
	auto arrowIndex = input.indexOf(" - /*>"*/);
	if (arrowIndex >= 0) {
		auto beforeArrow = input.substring(0, arrowIndex).strip();
		auto right = input.substring(arrowIndex + " - /*>"*/.length()).strip();
		/*final String parameters*/;
		if (isSymbol(beforeArrow)) {
			parameters = "auto " + beforeArrow;
		}
		/*else if*/ (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
			auto content = beforeArrow.substring(1, beforeArrow.length() - 1);
			parameters = divide(content, foldByDelimiter(',')).iter().map(strip).map(auto lambda(auto inner){
	return "auto " + inner;
}
).collect(struct Joiner(", ")).orElse("");
		}
		else {
			return None<struct >();
		}
		/*final String s*/;
		if (right.startsWith("{") && right.endsWith("}")) {
			auto substring = right.substring(1, right.length() - 1);
			s = compileFunctionSegments(substring, 1);
		}
		else {
			auto value = compileValue(right);
			s = "\n\treturn " + value + ";";
		}
		return Some<struct >("auto lambda(" + parameters + "){" + s + "\n}\n");
	}
	return None<struct >();
}
Option<Array<char>> compileString(Array<char> input) {
	auto stripped = input.strip();
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return Some<struct >(stripped);
	}
	else {
		return None<struct >();
	}
}
Option<Array<char>> compileNumber(Array<char> input) {
	auto stripped = input.strip();
	if (isNumber(stripped)) {
		return Some<struct >(stripped);
	}
	else {
		return None<struct >();
	}
}
Option<Array<char>> compileSymbol(Array<char> input) {
	auto stripped = input.strip();
	if (isSymbol(stripped)) {
		return Some<struct >(stripped);
	}
	else {
		return None<struct >();
	}
}
Option<Array<char>> compileAccess(Array<char> input) {
	auto separator = input.lastIndexOf(".");
	if (separator >= 0) {
		auto substring = input.substring(0, separator);
		auto property = input.substring(separator + ".".length()).strip();
		if (isSymbol(property)) {
			return Some<struct >(compileValue(substring) + "." + property);
		}
	}
	return None<struct >();
}
Option<Array<char>> compileOperator(Array<char> input, Array<char> infix) {
	auto index = input.indexOf(infix);
	if (index >= 0) {
		auto leftString = input.substring(0, index);
		auto rightString = input.substring(index + infix.length());
		return Some<struct >(compileValue(leftString) + " " + infix + " " + compileValue(rightString));
	}
	return None<struct >();
}
int isNumber(Array<char> input) {
	/*for*/ (/*int i = 0; i */ < /* input.length(); i*/ +  + ) {
		auto c = input.charAt(i);
		if (Character.isDigit(c)) {
			/*continue*/;
		}
		return false;
	}
	return true;
}
Array<char> mergeValues(Array<char> buffer, Array<char> element) {
	if (buffer.isEmpty()) {
		return element;
	}
	return buffer + ", " + element;
}
Array<char> compileParameter(Array<char> input) {
	return compileWhitespace(input).or(auto lambda(auto ){
	return parseDefinition(input).map(auto lambda(auto javaDefinition){
	return transformDefinition(javaDefinition).generate();
}
);
}
).orElseGet(auto lambda(auto ){
	return generatePlaceholder(input);
}
);
}
Option<Array<char>> compileWhitespace(Array<char> input) {
	if (input.isBlank()) {
		return Some<struct >("");
	}
	else {
		return None<struct >();
	}
}
Option<Tuple<List<Array<char>>, Array<char>>> compileField(Array<char> input) {
	auto stripped = input.strip();
	if (stripped.endsWith(";")) {
		auto withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		return parseDefinition(withoutEnd).map(auto lambda(auto javaDefinition){
	return transformDefinition(javaDefinition).generate();
}
).map(auto lambda(auto generated){
	return Tuple<struct >(Lists.empty(), "\n\t" + generated + ";");
}
);
	}
	return None<struct >();
}
Option<struct JavaDefinition> parseDefinition(Array<char> input) {
	auto stripped = input.strip();
	auto nameSeparator = stripped.lastIndexOf(" ");
	if (nameSeparator >= 0) {
		auto beforeName = stripped.substring(0, nameSeparator).strip();
		auto name = stripped.substring(nameSeparator + " ".length()).strip();
		if (isSymbol(name)) {
			return parseDefinitionWithType(beforeName, name);
		}
	}
	return None<struct >();
}
int isSymbol(Array<char> input) {
	/*for*/ (/*var i = 0; i */ < /* input.length(); i*/ +  + ) {
		auto c = input.charAt(i);
		if (Character.isLetter(c)) {
			/*continue*/;
		}
		return false;
	}
	return true;
}
Option<struct JavaDefinition> parseDefinitionWithType(Array<char> beforeName, Array<char> name) {
	auto maybeTuple = divide(beforeName, foldTypeSeparator).popLast();
	if (maybeTuple.isEmpty()) {
		return parseType(beforeName).map(auto lambda(auto type){
	return struct JavaDefinition(Lists.empty(), Lists.empty(), Lists.empty(), type, name);
}
);
	}
	auto tuple = maybeTuple.get();
	auto beforeType = tuple.left.iter().collect(struct Joiner(" ")).orElse("");
	auto type = tuple.right;
	return parseType(type).map(auto lambda(auto compiledType){
	return parseDefinitionWithTypeParameters(name, compiledType, beforeType);
}
);
}
struct DivideState foldTypeSeparator(struct DivideState state, char c) {
	if (c == ' ' && state.isLevel()) {
		return state.advance();
	}
	auto appended = state.append(c);
	if (c == ' < ') {
		return appended.enter();
	}
	if (c == '>') {
		return appended.exit();
	}
	return appended;
}
struct JavaDefinition parseDefinitionWithTypeParameters(Array<char> name, struct Type compiledType, Array<char> input) {
	auto beforeType = input.strip();
	if (beforeType.endsWith(">")) {
		auto withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
		auto typeParametersStart = withoutEnd.indexOf(" < ");
		if (typeParametersStart >= 0) {
			auto beforeTypeParameters = withoutEnd.substring(0, typeParametersStart);
			auto typeParametersString = withoutEnd.substring(typeParametersStart + " < ".length());
			auto typeParameters = parseTypeParameters(typeParametersString);
			return parseDefinitionWithModifiers(beforeTypeParameters, typeParameters, compiledType, name);
		}
	}
	return parseDefinitionWithModifiers(beforeType, Lists.empty(), compiledType, name);
}
struct JavaDefinition parseDefinitionWithModifiers(Array<char> beforeTypeParameters, List<Array<char>> typeParameters, struct Type type, Array<char> name) {
	auto separator = beforeTypeParameters.lastIndexOf("\n");
	if (separator >= 0) {
		auto annotationsString = beforeTypeParameters.substring(0, separator);
		auto annotations = parseAnnotations(annotationsString);
		auto substring = beforeTypeParameters.substring(separator + "\n".length());
		auto modifiers = parseModifiers(substring);
		return struct JavaDefinition(annotations, modifiers, typeParameters, type, name);
	}
	else {
		auto modifiers = parseModifiers(beforeTypeParameters);
		return struct JavaDefinition(Lists.empty(), modifiers, typeParameters, type, name);
	}
}
List<Array<char>> parseAnnotations(Array<char> annotationsString) {
	return divide(annotationsString, foldByDelimiter('\n')).iter().map(strip).map(auto lambda(auto value){
	return value.substring(1);
}
).collect(ListCollector<struct >());
}
List<Array<char>> parseModifiers(Array<char> beforeTypeParameters) {
	return parseAll(beforeTypeParameters, foldByDelimiter(' '), strip);
}
struct DivideState (*foldByDelimiter)(struct DivideState, char)(char delimiter) {
	/*return (state, c) ->*/ {
		if (c == delimiter) {
			return state.advance();
		}
		return state.append(c);
	}
	/**/;
}
Option<struct Type> parseType(Array<char> input) {
	return compileTemplateType(input).or(auto lambda(auto ){
	return compilePrimitiveType(input);
}
).or(auto lambda(auto ){
	return compileSymbolType(input);
}
).or(auto lambda(auto ){
	return compileArrayType(input).map(auto lambda(auto type){
	return type;
}
);
}
).or(auto lambda(auto ){
	return compileVariadicType(input);
}
);
}
Option<struct Type> compileVariadicType(Array<char> input) {
	auto stripped = input.strip();
	if (stripped.endsWith("...")) {
		auto substring = stripped.substring(0, stripped.length() - "...".length());
		auto child = parseTypeOrPlaceholder(substring);
		return Some<struct >(wrapInArray(child));
	}
	else {
		return None<struct >();
	}
}
struct TemplateType wrapInArray(struct Type child) {
	return struct TemplateType("Array", Lists.of(child));
}
Option<struct TemplateType> compileArrayType(Array<char> input) {
	auto stripped = input.strip();
	if (stripped.endsWith("[]")) {
		auto slice = stripped.substring(0, stripped.length() - "[]".length());
		return parseType(slice).map(wrapInArray);
	}
	return None<struct >();
}
Option<struct Type> compileSymbolType(Array<char> input) {
	if (isSymbol(input.strip())) {
		return Some<struct >(struct StructType(input.strip()));
	}
	return None<struct >();
}
Option<struct Type> compilePrimitiveType(Array<char> input) {
	/*return switch*/ (input.strip()) {
		/*case "char", "Character" */ - /*> new Some*/ < /*>*/(Primitive.Char);
		/*case "boolean", "Boolean", "int", "Integer" */ - /*> new Some*/ < /*>*/(Primitive.Int);
		/*case "var" */ - /*> new Some*/ < /*>*/(Primitive.Auto);
		/*case "void" */ - /*> new Some*/ < /*>*/(Primitive.Void);
		/*case "String" */ - /*> new Some*/ < /*>*/(wrapInArray(Primitive.Char));
		auto lambda(auto default){
	return /*new None*/ < /*>*/;
}
();
	}
	/**/;
}
Option<struct Type> compileTemplateType(Array<char> input) {
	if (/*!input*/.strip().endsWith(">")) {
		return None<struct >();
	}
	auto withoutEnd = input.strip().substring(0, input.strip().length() - ">".length());
	auto typeArgumentsStart = withoutEnd.indexOf(" < ");
	if (typeArgumentsStart < 0) {
		return None<struct >();
	}
	auto base = withoutEnd.substring(0, typeArgumentsStart);
	auto arguments = withoutEnd.substring(typeArgumentsStart + " < ".length());
	return Some<struct >(assembleTemplateType(base, arguments));
}
struct Type assembleTemplateType(Array<char> base, Array<char> inputArguments) {
	auto elements = parseValues(inputArguments, parseTypeOrPlaceholder);
	/*return switch*/ (base) {
		/*case "Function" ->*/ {
			auto first = elements.getFirst();
			auto last = elements.getLast();
			/*yield new FunctionType*/(Lists.of(first), last);
		}
		/*case "BiFunction" ->*/ {
			/*final var arg0 */ = elements.getFirst();
			/*final var arg1 */ = elements.get(1);
			auto returnType = elements.getLast();
			/*yield new FunctionType*/(Lists.of(/*arg0*/, /* arg1*/), returnType);
		}
		auto lambda(auto default){
	return /*new TemplateType*/;
}
(base, elements);
	}
	/**/;
}
Array<char> compileTypeOrPlaceholder(Array<char> input) {
	return parseTypeOrPlaceholder(input).generate();
}
struct Type parseTypeOrPlaceholder(Array<char> input) {
	return parseType(input).orElseGet(auto lambda(auto ){
	return struct Placeholder(input);
}
);
}
Option<struct ClassDefinition> compileClassDefinition(Array<char> input) {
	return compileClassDefinitionWithKeyword(input, "class ").or(auto lambda(auto ){
	return compileClassDefinitionWithKeyword(input, "interface ");
}
).or(auto lambda(auto ){
	return compileClassDefinitionWithKeyword(input, "record ");
}
).or(auto lambda(auto ){
	return compileClassDefinitionWithKeyword(input, "enum ");
}
);
}
Option<struct ClassDefinition> compileClassDefinitionWithKeyword(Array<char> input, Array<char> keyword) {
	auto classIndex = input.indexOf(keyword);
	if (classIndex < 0) {
		return None<struct >();
	}
	auto beforeKeyword = input.substring(0, classIndex).strip();
	auto afterKeyword = input.substring(classIndex + keyword.length()).strip();
	return Some<struct >(parseClassDefinitionWithParameters(beforeKeyword, afterKeyword));
}
struct ClassDefinition parseClassDefinitionWithParameters(Array<char> beforeKeyword, Array<char> afterKeyword) {
	if (afterKeyword.endsWith(")")) {
		auto withoutEnd = afterKeyword.substring(0, afterKeyword.length() - ")".length());
		auto paramStart = withoutEnd.indexOf("(");
		if (paramStart >= 0) {
			auto beforeParameters = withoutEnd.substring(0, paramStart);
			auto parameters = withoutEnd.substring(paramStart + "(".length());
			return parseClassDefinitionWithTypeParameters(beforeKeyword, beforeParameters);
		}
	}
	return parseClassDefinitionWithTypeParameters(beforeKeyword, afterKeyword);
}
struct ClassDefinition parseClassDefinitionWithTypeParameters(Array<char> beforeKeyword, Array<char> input) {
	auto stripped = input.strip();
	if (stripped.endsWith(">")) {
		auto withoutEnd = stripped.substring(0, stripped.length() - ">".length());
		auto typeParamsStart = withoutEnd.indexOf(" < ");
		if (typeParamsStart >= 0) {
			auto base = withoutEnd.substring(0, typeParamsStart);
			auto typeParametersString = withoutEnd.substring(typeParamsStart + " < ".length());
			auto typeParameters = parseTypeParameters(typeParametersString);
			return parseClassDefinitionWithModifiers(beforeKeyword, base, typeParameters);
		}
	}
	return parseClassDefinitionWithModifiers(beforeKeyword, stripped, Lists.empty());
}
struct ClassDefinition parseClassDefinitionWithModifiers(Array<char> beforeKeyword, Array<char> base, List<Array<char>> typeParameters) {
	auto i = beforeKeyword.lastIndexOf("\n");
	if (i >= 0) {
		auto annotationsString = beforeKeyword.substring(0, i);
		auto modifiersString = beforeKeyword.substring(i + "\n".length());
		auto annotations = parseAnnotations(annotationsString);
		auto modifiers = parseModifiers(modifiersString);
		return struct ClassDefinition(annotations, modifiers, base, typeParameters);
	}
	auto modifiers = parseModifiers(beforeKeyword);
	return struct ClassDefinition(Lists.empty(), modifiers, base, typeParameters);
}
List<Array<char>> parseTypeParameters(Array<char> typeParameters) {
	return mapAll(divideValues(typeParameters), strip);
}
List<Array<char>> divideValues(Array<char> input) {
	return divide(input, foldValues);
}
struct DivideState foldValues(struct DivideState state, char c) {
	if (c == ',' && state.isLevel()) {
		return state.advance();
	}
	auto appended = state.append(c);
	if (c == ' - ') {
		auto maybe = appended.peek();
		if (/*maybe instanceof Some*/(/*var peek*/) && peek == '>') {
			return appended.append().orElse(appended);
		}
	}
	if (c == ' < ' || c == '(') {
		return appended.enter();
	}
	if (c == '>' || c == ')') {
		return appended.exit();
	}
	return appended;
}
Array<char> generatePlaceholder(Array<char> input) {
	return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
}
struct Primitive new(Array<char> value) {
	this.value = value;
}
Array<char> generate() {
	return this.value;
}
struct Primitive implements Type {/*Char("char"),
        Int("int"),
        Auto("auto"),
        Void("void");*/
	Array<char> value;
	Array<char> generate();
};
struct Main {
};
/*
*/