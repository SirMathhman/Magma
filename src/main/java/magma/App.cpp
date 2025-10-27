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
struct App {

	List<char*> globals;
	List<char*> forwardDeclarations;
	List<char*> functions;
	List<char*> structures;
	List<char*> sealedStructures;
	Stack<char*> structureNames;
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

	char* generate();
	char* getSimpleName();};
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
	/*final var joined*/ = this.list.stream(/*)*/.map(/*CPPType::generate)*/.collect(Collectors.joining(", "));
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
	return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
}
char* generate() {
	return wrap(this.input);
}
char* getSimpleName() {
	return this.generate();
}
/*0;

		public*/ State(char* input) {
	this.input = input;
	this.buffer = new_StringBuilder();
	this.depth = /*0*/;
	this.segments = new_ArrayList<char*>();
	/*}

		State enter() {
			this*/.depth = this.depth + 1;
	return this;
	/*}

		State exit() {
			this*/.depth = this.depth - 1;
	return this;
	/*}

		State advance() {
			this.segments.add(this.buffer.toString())*/;
	this.buffer = new_StringBuilder();
	return this;
	/*}

		boolean isShallow() {
			return this*/.depth = /*= 1*/;
	/*}

		State append(char c) {
			this.buffer.append(c)*/;
	return this;
	/*}

		boolean isLevel() {
			return this*/.depth = /*= 0*/;/*
		}

		public Optional<Character> pop() {
			if (this.index < this.input.length()) {
				var counter = this.index;
				this.index++;
				final var element = this.input.charAt(counter);
				return Optional.of(element);
			}*//* else {
				return Optional.empty();
			}*/
	/*}

		public Stream<String> stream() {
			return this.segments.stream()*/;/*
		}

		public Optional<Tuple<Character, State>> popAndAppendToTuple() {
			return this.pop().map(next -> {
				final var appended = this.append(next);
				return new Tuple<Character, State>(next, appended);
			}*/
	/*)*/;
	/*}

		public Optional<State> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(Tuple::right)*/;/*
		}
	*/
}
App new_App() {
	this.globals = new_ArrayList<char*>();
	this.structureNames = new_Stack<char*>();
	this.functions = new_ArrayList<char*>();
	this.forwardDeclarations = new_ArrayList<char*>();
	this.structures = new_ArrayList<char*>();
	this.sealedStructures = new_ArrayList<char*>();
	this.depth = /*1*/;
}
void main(char** args) {
	/*new App().run().ifPresent(Throwable::printStackTrace)*/;
}
Optional<IOException> run() {
	/*final var source*/ = Paths.get(".", "src", "main", "java", "magma", "App.java");
	/*final var input*/ = this.readString(source);/*
		return switch (input) {
			case Err<String, IOException> v -> Optional.of(v.error);
			case Ok<String, IOException> v -> this.compilePath(source, v.value);
		}*/
	/**/;
}
Optional<IOException> compilePath(char* input) {
	/*final var target*/ = source.resolveSibling("App.cpp");
	/*final var output*/ = this.compile(input);
	return this.writeString(/*target, output)*/.or(/*() -> this*/.compileNative(target));
}
/*IOException>*/ compileNative(Path target) {
	/*final var clang*/ = this.startCommand(List.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));/*
		return switch (clang) {
			case Err<Process, IOException> v1 -> Optional.of(v1.error);
			case Ok<Process, IOException> v1 -> this.waitForProcess(v1.value);
		}*/
	/**/;
}
Optional<IOException> waitForProcess(Process process) {/*
		return switch (this.waitFor(process)) {
			case Err<Integer, IOException> v2 -> Optional.of(v2.error);
			case Ok<Integer, IOException> v2 -> {
				System.out.println("Compilation failed with exit code: " + v2.value);
				yield Optional.empty();
			}
		}*/
	/**/;
}
/*IOException>*/ waitFor(Process process) {/*
		try {
			return new Ok<Integer, IOException>(process.waitFor());
		}*//* catch (InterruptedException e) {
			return new Err<Integer, IOException>(new IOException(e));
		}*/
}
/*IOException>*/ startCommand(List<char*> command) {/*
		try {
			return new Ok<Process, IOException>(new ProcessBuilder(command).inheritIO().start());
		}*//* catch (IOException e) {
			return new Err<Process, IOException>(e);
		}*/
}
Optional<IOException> writeString(char* output) {/*
		try {
			Files.writeString(target, output);
			return Optional.empty();
		}*//* catch (IOException e) {
			return Optional.of(e);
		}*/
}
/*IOException>*/ readString(Path source) {/*
		try {
			return new Ok<String, IOException>(Files.readString(source));
		}*//* catch (IOException e) {
			return new Err<String, IOException>(e);
		}*/
}
char* compile(char* input) {
	/*final var compiled*/ = this.compileStatements(/*input, this::compileRootSegment*/);
	/*final var joinedForwardDeclarations*/ = String.join(/*"", this*/.forwardDeclarations);
	/*final var joinedFunctions*/ = String.join(/*"", this*/.functions);
	/*final var joinedStructures*/ = String.join(/*"", this*/.structures);
	/*final var joinedSealedStructures*/ = String.join(/*"", this*/.sealedStructures);
	/*final var joinedGlobals*/ = String.join(/*"", this*/.globals);
	return /*joinedForwardDeclarations + compiled + joinedStructures + joinedSealedStructures + joinedGlobals +
					 joinedFunctions + "int main(){" + System*/.lineSeparator() + "\treturn " + "0;" + System.lineSeparator() +
					 "}";
}
char* compileStatements(/*String>*/ mapper) {
	return this.divide(new_State(/*input))*/.map(mapper).collect(Collectors.joining());
}
Stream<char*> divide(State state) {
	/*var current*/ = state;/*
		while (true) {
			final var maybeNext = current.pop();
			if (maybeNext.isEmpty()) {
				break;
			}

			current = this.foldEscaped(current, maybeNext.get());
		}*/
	return current.advance(/*)*/.stream();
}
State foldEscaped(char next) {
	if (/*next == '\''*/) {
		return current.append(/*next)*/.popAndAppendToTuple()
					.map(this::foldSingleEscapeChar)
					.flatMap(State::popAndAppendToOption)
					.orElse(current);
	}
	if (/*next == '\"'*/) {
		/*var current0*/ = current.append(next);
			while (true) {
				final var maybeTuple = current0.popAndAppendToTuple();
				if (maybeTuple.isEmpty()) {
					break;
				}

				final var tuple = maybeTuple.get();
				current0 = tuple.right;

				final var nextInQuotes = tuple.left;
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
	return this.fold(/*current, next*/);
}
State foldSingleEscapeChar(/*State>*/ tuple) {
	if (tuple.left == '\\') {
		return tuple.right.popAndAppendToOption(/*)*/.orElse(tuple.right);
	}
	return tuple.right;
}
State fold(Character c) {
	/*final var appended*/ = state.append(c);
	if (/*c == ';' && appended*/.isLevel()) {
		return appended.advance();
	}/* else if (c == '}' && appended.isShallow()) {
			return appended.advance().exit();
		}*/
	if (/*c == '{'*/) {
		return appended.enter();
	}
	if (/*c == '}'*/) {
		return appended.exit();
	}
	return appended;
}
char* compileRootSegment(char* input) {
	/*final var stripped*/ = input.strip();
	if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
		return "";
	}
	return this.compileStructure(/*"class", stripped)*/.orElseGet(/*() -> Placeholder*/.wrap(input));
}
Optional<char*> compileStructure(char* input) {
	/*final var classIndex*/ = input.indexOf(type);
	if (/*classIndex >= 0*/) {/*
			final var afterKeyword = input.substring(classIndex + type.length());
			final var contentStart = afterKeyword.indexOf("{");
			if (contentStart >= 0) {
				var beforeContent = afterKeyword.substring(0, contentStart).strip();
				final var withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
				if (withEnd.endsWith("}")) {
					final var content = withEnd.substring(0, withEnd.length() - 1);

					final var permitsIndex = beforeContent.indexOf("permits");
					List<String> variants = Collections.emptyList();
					if (permitsIndex >= 0) {
						final var variantsArray =
								beforeContent.substring(permitsIndex + "permits".length()).split(Pattern.quote(","));
						beforeContent = beforeContent.substring(0, permitsIndex).strip();
						variants = Arrays.stream(variantsArray).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
					}

					final var implementsIndex = beforeContent.indexOf("implements");
					Optional<CPPType> maybeInterfaceType = Optional.empty();
					if (implementsIndex >= 0) {
						final var slice = beforeContent.substring(implementsIndex + "implements".length()).strip();
						maybeInterfaceType = this.compileType(slice);
						beforeContent = beforeContent.substring(0, implementsIndex).strip();
					}

					if (beforeContent.endsWith(")")) {
						final var slice = beforeContent.substring(0, beforeContent.length() - 1);
						final var i = slice.indexOf("(");
						if (i >= 0) {
							final var params = slice.substring(i + 1);
							beforeContent = slice.substring(0, i).strip();
						}
					}

					List<String> typeParameters = new ArrayList<String>();
					if (beforeContent.endsWith(">")) {
						final var withoutEnd = beforeContent.substring(0, beforeContent.length() - 1);
						final var typeParamStart = withoutEnd.indexOf("<");
						if (typeParamStart >= 0) {
							beforeContent = withoutEnd.substring(0, typeParamStart);
							final var typeParamsArray = withoutEnd.substring(typeParamStart + 1).split(Pattern.quote(","));
							typeParameters =
									Arrays.stream(typeParamsArray).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
						}
					}

					if (!this.isIdentifier(beforeContent)) {
						return Optional.empty();
					}

					String templateString;
					if (typeParameters.isEmpty()) {
						templateString = "";
					} else {
						final var collect =
								typeParameters.stream().map(slice -> "typename " + slice).collect(Collectors.joining(", "));
						templateString = "template <" + collect + ">" + System.lineSeparator();
					}

					String dependencies;
					if (variants.isEmpty()) {
						dependencies = "";
					} else {
						final var enumFields = variants
								.stream()
								.map(slice -> slice + "Tag")
								.map(content1 -> this.generateWithIndent(content1, 1))
								.collect(Collectors.joining(","));

						final var typeArguments = this.joinTypeArguments(typeParameters);
						final var unionFields = variants
								.stream()
								.map(slice -> System.lineSeparator() + "\t" + slice + typeArguments + " " + slice.toLowerCase() + ";")
								.collect(Collectors.joining());

						dependencies = "enum " + beforeContent + "Tag {" + enumFields + System.lineSeparator() + "};" +
													 System.lineSeparator() + templateString + "union " + beforeContent + "Data {" + unionFields +
													 System.lineSeparator() + "};" + System.lineSeparator();
					}

					final String fields;
					if (variants.isEmpty()) {
						fields = "";
					} else {
						fields = this.generateStatement(beforeContent + "Tag tag", 1) +
										 this.generateStatement(beforeContent + "Data " + "data", 1);
					}

					if (maybeInterfaceType.isPresent()) {
						final var interfaceType = maybeInterfaceType.get();
						final var joinedTypeArguments = this.joinTypeArguments(typeParameters);

						final var thisType = beforeContent + joinedTypeArguments;
						this.functions.add(templateString + interfaceType.generate() + " to" + interfaceType.getSimpleName() +
															 "_" +
															 beforeContent + "(void* _ref" + "){" +
															 this.generateStatement(thisType + " _this = *((" + thisType + "*) _ref)", 1) +
															 this.generateStatement(
																	 interfaceType.getSimpleName() + "Data" + joinedTypeArguments + " data", 1) +
															 this.generateStatement("data." + beforeContent.toLowerCase() + " = _this", 1) +
															 this.generateStatement(
																	 "return " + interfaceType.generate() + " { " + beforeContent + "Tag, " + "data }",
																	 1) + System.lineSeparator() + "}" + System.lineSeparator());
					}

					this.forwardDeclarations.add(templateString + "struct " + beforeContent + ";" + System.lineSeparator());

					this.structureNames.push(beforeContent);
					final var generated =
							dependencies + templateString + "struct " + beforeContent + " {" + fields + System.lineSeparator() +
							this.compileStatements(content, this::compileClassSegment) + "};" + System.lineSeparator();
					this.structureNames.pop();

					if (variants.isEmpty()) {
						this.structures.add(generated);
					} else {
						this.sealedStructures.add(generated);
					}

					return Optional.of("");
				}
			}
		*/
	}
	return Optional.empty();
}
char* joinTypeArguments(List<char*> typeParameters) {
	/*String joinedTypeArguments*/;
	if (typeParameters.isEmpty()) {
		joinedTypeArguments = "";
	}/* else {
			joinedTypeArguments = "<" + String.join(", ", typeParameters) + ">";
		}*/
	return joinedTypeArguments;
}
char* generateStatement(int depth) {
	return this.generateWithIndent(content, depth) + ";";
}
char* generateWithIndent(int depth) {
	return this.generateIndent(depth) + content;
}
char* generateIndent(int depth) {
	return System.lineSeparator(/*) + "\t"*/.repeat(depth);
}
boolean isIdentifier(char* input) {
	/*for (var i*/ = /*0*/;
	/*i < input.length()*/;/* i++) {
			if (!Character.isLetter(input.charAt(i))) {
				return false;
			}
		}*/
	return true;
}
char* compileClassSegment(char* input) {
	if (input.isBlank()) {
		return "";
	}
	/*final var maybeInterface*/ = this.compileStructure(/*"interface", input*/);
	if (maybeInterface.isPresent()) {
		return maybeInterface.get();
	}
	/*final var maybeRecord*/ = this.compileStructure(/*"record", input*/);
	if (maybeRecord.isPresent()) {
		return maybeRecord.get();
	}
	/*final var maybeEnum*/ = this.compileStructure(/*"enum", input*/);
	if (maybeEnum.isPresent()) {
		return maybeEnum.get();
	}
	/*final var paramStart*/ = input.indexOf("(");
	if (/*paramStart >= 0*/) {/*
			final var definition = input.substring(0, paramStart).strip();
			final var withParams = input.substring(paramStart + 1);
			final var paramEnd = withParams.indexOf(")");
			if (paramEnd >= 0) {
				final var params = withParams.substring(0, paramEnd).strip();
				final var withBraces = withParams.substring(paramEnd + 1).strip();
				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var content = withBraces.substring(1, withBraces.length() - 1);

					final var header = this
							.compileDefinition(definition)
							.or(() -> this.compileConstructor(definition))
							.orElseGet(() -> Placeholder.wrap(definition));

					final var generated =
							header + "(" + this.compileParameters(params) + ") {" + this.compileMethodStatements(content) +
							System.lineSeparator() + "}" + System.lineSeparator();

					this.functions.add(generated);
					return "";
				}
			}
		*/
	}
	if (input.endsWith(";")) {
		/*final var slice*/ = input.substring(/*0, input*/.length(/*) - 1);
			return this*/.compileEnumValues(slice)
					.orElseGet(() -> this.generateStatement(this
																											.compileDefinition(slice)
																											.orElseGet(() -> Placeholder.wrap(slice)), 1));
	}
	return Placeholder.wrap(input);
}
char* compileMethodStatements(char* content) {
	return this.compileStatements(/*content, this::compileMethodSegment*/);
}
Optional<char*> compileConstructor(char* input) {
	/*final var i*/ = input.lastIndexOf(" ");
	if (/*i >= 0*/) {/*
			final var name = input.substring(i + 1).strip();
			if (this.isIdentifier(name)) {
				final var structName = this.structureNames.peek();
				return Optional.of(structName + " new_" + structName);
			}
		*/
	}/* else {
			if (this.isIdentifier(input)) {
				final var structName = this.structureNames.peek();
				return Optional.of(structName + " new_" + structName);
			}
		}*/
	return Optional.empty();
}
Optional<char*> compileEnumValues(char* input) {
	/*final var segments*/ = Arrays.stream(input.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).toList();/*

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
	if (stripped.endsWith(")") /*) {
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
char* compileMethodSegment(char* input) {
	/*final var stripped*/ = input.strip();
	if (stripped.isEmpty()) {
		return "";
	}
	if (stripped.startsWith("{") && stripped.endsWith("}")) {
		/*final var content*/ = stripped.substring(1, stripped.length() - 1);

			this.depth++;
			final var compiled = this.compileMethodSegment(content);
			this.depth--;

			return "{" + compiled + this.generateIndent(this.depth) + "}";
	}
	if (stripped.startsWith("if")) {/*
			final var substring = stripped.substring(2).strip();
			if (substring.startsWith("(")) {
				final var withCondition = substring.substring(1);
				final var conditionEnd = this.findConditionEnd(withCondition);

				if (conditionEnd >= 0) {
					final var condition = withCondition.substring(0, conditionEnd).strip();
					final var substring2 = withCondition.substring(conditionEnd + 1).strip();
					return this.generateIndent(this.depth) + "if (" + this.compileExpression(condition) + ") " +
								 this.compileMethodSegment(substring2);
				}
			}
		*/
	}
	if (stripped.endsWith(";")) {
		/*final var slice*/ = stripped.substring(/*0, stripped*/.length() - 1);
			return this.generateStatement(this.compileMethodStatement(slice), this.depth);
	}
	return Placeholder.wrap(input);
}
int findConditionEnd(char* withCondition) {
	/*int conditionEnd*/ = /*-1*/;
	/*var depth*/ = /*0*/;
	/*for (var i*/ = /*0*/;
	/*i < withCondition.length()*/;/* i++) {
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
char* compileMethodStatement(char* input) {
	if (input.startsWith("return ")) {
		/*final var slice*/ = input.substring("return ".length()).strip();
			return "return " + this.compileExpression(slice);
	}
	/*final var separator*/ = input.indexOf(/*'='*/);
	if (/*separator >= 0*/) {
		/*final var substring*/ = input.substring(/*0, separator)*/.strip();
			final var substring1 = input.substring(separator + 1).strip();
			return this.compileExpression(substring) + " = " + this.compileExpression(substring1);
	}
	return Placeholder.wrap(input);
}
char* compileExpression(char* input) {
	if (input.startsWith("\"") && input.endsWith("\"")) {
		return input;
	}
	if (input.endsWith(")") /*) {
			final var slice = input.substring(0, input.length() - 1);
			final var i = slice.indexOf("(");
			if (i >= 0) {
				final var caller = slice.substring(0, i).strip();
				final var arguments = slice.substring(i + 1);
				final String newCaller;
				if (!caller.startsWith("new ")) {
					newCaller = this.compileExpression(caller);
				} else {
					final var substring = caller.substring("new ".length());
					newCaller = "new_" + this.compileType(substring).orElseGet(() -> new Placeholder(substring)).generate();
				}

				return newCaller + "(" + this.compileExpression(arguments) + ")";
			}
		}*/
	/*final var i*/ = input.indexOf(".");
	if (/*i >= 0*/) {
		/*final var child*/ = input.substring(0, i).strip();
			final var name = input.substring(i + 1);
			return this.compileExpression(child) + "." + name;
	}
	if (this.isIdentifier(input)) {
		return input;
	}
	return Placeholder.wrap(input);
}
char* compileParameters(char* input) {
	if (input.isEmpty()) {
		return "";
	}
	return this.compileDefinitionOrPlaceholder(input);
}
char* compileDefinitionOrPlaceholder(char* input) {
	return this.compileDefinition(/*input)*/.orElseGet(/*() -> Placeholder*/.wrap(input));
}
Optional<char*> compileDefinition(char* input) {
	/*final var nameSeparator*/ = input.lastIndexOf(" ");
	if (/*nameSeparator < 0*/) {
		return Optional.empty();
	}
	/*final var beforeName*/ = input.substring(/*0, nameSeparator*/);
	/*final var name*/ = input.substring(/*nameSeparator + 1)*/.strip();
	/*final var typeSeparator*/ = beforeName.lastIndexOf(" ");
	if (/*typeSeparator >= 0*/) {
		/*final var type*/ = beforeName.substring(/*typeSeparator + 1)*/.strip();
			return this.compileType(type).map(cppType -> cppType.generate() + " " + name);
	}
	return this.compileType(/*beforeName)*/.map(cppType -> cppType.generate() + " " + name);
}
Optional<CPPType> compileType(char* input) {
	/*final var stripped*/ = input.strip();
	if (stripped.equals("void")) {
		return Optional.of(CPPPrimitiveType.Void);
	}
	if (stripped.endsWith("[]")) {
		/*final var slice*/ = stripped.substring(/*0, stripped*/.length() - 2);
			return this.compileType(slice).map(CPointerType::new);
	}
	if (stripped.equals("String")) {
		return Optional.of(new_CPointerType(CPPPrimitiveType.Char));
	}
	if (stripped.endsWith(">")) {/*
			final var withoutEnd = stripped.substring(0, stripped.length() - 1);
			final var i = withoutEnd.indexOf("<");
			if (i >= 0) {
				final var base = withoutEnd.substring(0, i);
				final var typeArguments = withoutEnd.substring(i + 1);

				final var list = Arrays
						.stream(typeArguments.split(Pattern.quote(",")))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(this::compileType)
						.flatMap(Optional::stream)
						.toList();

				return Optional.of(new CTemplateType(base, list));
			}
		*/
	}
	if (this.isIdentifier(stripped)) {
		if (stripped.equals("public")) 
		/*{
				return Optional.empty();
			}

			return Optional.of(new CIdentifier(stripped))*/;
	}
	return Optional.of(new_Placeholder(stripped));
}
int main(){
	return 0;
}