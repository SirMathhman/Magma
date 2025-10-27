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

	/*

		private final*/ char* content;/*
	*/};
template <typename T, typename X>
struct Err {
};
template <typename T, typename X>
struct Ok {
};
struct CPointerType {
/*
	*/};
struct CTemplateType {
/*
	*/};
struct CIdentifier {
/*
	*/};
struct Placeholder {
/*
	*/};
template <typename A, typename B>
struct Tuple {
};
struct App {

	/*

	public static final List<String> globals =*/ new ArrayList<>();
	/*
	private static final List<String> forwardDeclarations =*/ new ArrayList<String>();
	/*
	private static final List<String> functions =*/ new ArrayList<String>();
	/*
	private static final List<String> structures =*/ new ArrayList<String>();
	/*
	private static final List<String> sealedStructures =*/ new ArrayList<String>();
	/*
	private static final Stack<String> structureNames =*/ new Stack<>();/*
*/};
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
	/*

		String*/ getSimpleName();/*
	*/};
/*name*/
/*name*/
CPPType toCPPType_CPPPrimitiveType(void* _ref){
	CPPPrimitiveType _this = *((CPPPrimitiveType*) _ref);
	CPPTypeData data;
	data.cppprimitivetype = _this;
	return CPPType { CPPPrimitiveTypeTag, data };
}
/*CPPPrimitiveType*/(char* content) {/*this.content = content;*//**/}
/*@Override
		public*/ char* generate() {/*
			return this.content;*//*
		*/}
/*@Override
		public*/ char* getSimpleName() {/*
			return this.content;*//*
		*/}
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
/*@Override
		public*/ char* generate() {/*
			return this.type.generate() + "*";*//*
		*/}
/*@Override
		public*/ char* getSimpleName() {/*
			return this.type.getSimpleName() + "_ref";*//*
		*/}
CPPType toCPPType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CPPTypeData data;
	data.ctemplatetype = _this;
	return CPPType { CTemplateTypeTag, data };
}
/*@Override
		public*/ char* generate() {/*
			final var joined = String.join(", ", this.list);*//*
			return this.base + "<" + joined + ">";*//*
		*/}
/*@Override
		public*/ char* getSimpleName() {/*
			return this.base;*//*
		*/}
CPPType toCPPType_CIdentifier(void* _ref){
	CIdentifier _this = *((CIdentifier*) _ref);
	CPPTypeData data;
	data.cidentifier = _this;
	return CPPType { CIdentifierTag, data };
}
/*@Override
		public*/ char* generate() {/*
			return this.input;*//*
		*/}
/*@Override
		public*/ char* getSimpleName() {/*
			return this.input;*//*
		*/}
CPPType toCPPType_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CPPTypeData data;
	data.placeholder = _this;
	return CPPType { PlaceholderTag, data };
}
/*@Override
		public*/ char* generate() {/*
			return wrap(this.input);*//*
		*/}
/*@Override
		public*/ char* getSimpleName() {/*
			return this.generate();*//*
		*/}
/*private static class State {
		public final String input;
		public final ArrayList<String> segments;
		private StringBuilder buffer;
		private int depth;
		private int index =*/ /*0;

		public*/ State(char* input) {/*
			this.input = input;*//*
			this.buffer = new StringBuilder();*//*
			this.depth = 0;*//*
			this.segments = new ArrayList<String>();*//*
		}

		State enter() {
			this.depth = this.depth + 1;*//*
			return this;*//*
		}

		State exit() {
			this.depth = this.depth - 1;*//*
			return this;*//*
		}

		State advance() {
			this.segments.add(this.buffer.toString());*//*
			this.buffer = new StringBuilder();*//*
			return this;*//*
		}

		boolean isShallow() {
			return this.depth == 1;*//*
		}

		State append(char c) {
			this.buffer.append(c);*//*
			return this;*//*
		}

		boolean isLevel() {
			return this.depth == 0;*//*
		}

		public Optional<Character> pop() {
			if (this.index < this.input.length()) {
				var counter = this.index;
				this.index++;
				final var element = this.input.charAt(counter);
				return Optional.of(element);
			}*//* else {
				return Optional.empty();
			}*//*
		}

		public Stream<String> stream() {
			return this.segments.stream();*//*
		}

		public Optional<Tuple<Character, State>> popAndAppendToTuple() {
			return this.pop().map(next -> {
				final var appended = this.append(next);
				return new Tuple<Character, State>(next, appended);
			}*//*);*//*
		}

		public Optional<State> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(Tuple::right);*//*
		}
	*/}
/*public static*/ void main(char** args) {/*
		run().ifPresent(Throwable::printStackTrace);*//*
	*/}
/*private static*/ Optional<IOException> run() {/*
		final var source = Paths.get(".", "src", "main", "java", "magma", "App.java");*//*
		final var input = readString(source);*//*
		return switch (input) {
			case Err<String, IOException> v -> Optional.of(v.error);
			case Ok<String, IOException> v -> compilePath(source, v.value);
		}*//*;*//*
	*/}
/*private static*/ Optional<IOException> compilePath(/*Path source,*/ char* input) {/*
		final var target = source.resolveSibling("App.cpp");*//*
		final var output = compile(input);*//*
		return writeString(target, output).or(() -> compileNative(target));*//*
	*/}
/*private static Optional<? extends*/ /*IOException>*/ compileNative(Path target) {/*
		final var clang = startCommand(List.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));*//*
		return switch (clang) {
			case Err<Process, IOException> v1 -> Optional.of(v1.error);
			case Ok<Process, IOException> v1 -> waitForProcess(v1.value);
		}*//*;*//*
	*/}
/*private static*/ Optional<IOException> waitForProcess(Process process) {/*
		return switch (waitFor(process)) {
			case Err<Integer, IOException> v2 -> Optional.of(v2.error);
			case Ok<Integer, IOException> v2 -> {
				System.out.println("Compilation failed with exit code: " + v2.value);
				yield Optional.empty();
			}
		}*//*;*//*
	*/}
/*private static Result<Integer,*/ /*IOException>*/ waitFor(Process process) {/*
		try {
			return new Ok<Integer, IOException>(process.waitFor());
		}*//* catch (InterruptedException e) {
			return new Err<Integer, IOException>(new IOException(e));
		}*//*
	*/}
/*private static Result<Process,*/ /*IOException>*/ startCommand(List<char*> command) {/*
		try {
			return new Ok<Process, IOException>(new ProcessBuilder(command).inheritIO().start());
		}*//* catch (IOException e) {
			return new Err<Process, IOException>(e);
		}*//*
	*/}
/*private static*/ Optional<IOException> writeString(/*Path target,*/ char* output) {/*
		try {
			Files.writeString(target, output);
			return Optional.empty();
		}*//* catch (IOException e) {
			return Optional.of(e);
		}*//*
	*/}
/*private static Result<String,*/ /*IOException>*/ readString(Path source) {/*
		try {
			return new Ok<String, IOException>(Files.readString(source));
		}*//* catch (IOException e) {
			return new Err<String, IOException>(e);
		}*//*
	*/}
/*private static*/ char* compile(char* input) {/*
		final var compiled = compileStatements(input, App::compileRootSegment);*//*

		final var joinedForwardDeclarations = String.join("", forwardDeclarations);*//*
		final var joinedFunctions = String.join("", functions);*//*

		final var joinedStructures = String.join("", structures);*//*
		final var joinedSealedStructures = String.join("", sealedStructures);*//*
		final var joinedGlobals = String.join("", globals);*//*

		return joinedForwardDeclarations + compiled + joinedStructures + joinedSealedStructures + joinedGlobals +
					 joinedFunctions + "int main(){" + System.lineSeparator() + "\treturn " + "0;" + System.lineSeparator() +
					 "}";*//*
	*/}
/*private static*/ char* compileStatements(/*String input, Function<String,*/ /*String>*/ mapper) {/*
		return divide(new State(input)).map(mapper).collect(Collectors.joining());*//*
	*/}
/*private static*/ Stream<char*> divide(State state) {/*
		var current = state;*//*
		while (true) {
			final var maybeNext = current.pop();
			if (maybeNext.isEmpty()) {
				break;
			}

			current = foldEscaped(current, maybeNext.get());
		}*//*

		return current.advance().stream();*//*
	*/}
/*private static*/ State foldEscaped(/*State current,*/ char next) {/*
		if (next == '\'') {
			return current
					.append(next)
					.popAndAppendToTuple()
					.map(App::foldSingleEscapeChar)
					.flatMap(State::popAndAppendToOption)
					.orElse(current);
		}*//*

		if (next == '\"') {
			var current0 = current.append(next);
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
		}*//*

		return fold(current, next);*//*
	*/}
/*private static*/ State foldSingleEscapeChar(/*Tuple<Character,*/ /*State>*/ tuple) {/*
		if (tuple.left == '\\') {
			return tuple.right.popAndAppendToOption().orElse(tuple.right);
		}*//*
		return tuple.right;*//*
	*/}
/*private static*/ State fold(/*State state,*/ Character c) {/*
		final var appended = state.append(c);*//*
		if (c == ';' && appended.isLevel()) {
			return appended.advance();
		}*//* else if (c == '}' && appended.isShallow()) {
			return appended.advance().exit();
		}*//*
		if (c == '{') {
			return appended.enter();
		}*//*
		if (c == '}') {
			return appended.exit();
		}*//*
		return appended;*//*
	*/}
/*private static*/ char* compileRootSegment(char* input) {/*
		final var stripped = input.strip();*//*
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}*//*

		return compileStructure("class", stripped).orElseGet(() -> wrap(input));*//*
	*/}
/*private static*/ Optional<char*> compileStructure(/*String type,*/ char* input) {/*
		final var classIndex = input.indexOf(type);*//*
		if (classIndex >= 0) {
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
						maybeInterfaceType = Optional.of(compileType(slice));
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

					if (!isIdentifier(beforeContent)) {
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
								.map(App::generateWithIndent)
								.collect(Collectors.joining(","));

						final var typeArguments = joinTypeArguments(typeParameters);
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
						fields = generateStatement(beforeContent + "Tag tag") + generateStatement(beforeContent + "Data data");
					}

					if (maybeInterfaceType.isPresent()) {
						final var interfaceType = maybeInterfaceType.get();
						final var joinedTypeArguments = joinTypeArguments(typeParameters);

						final var thisType = beforeContent + joinedTypeArguments;
						functions.add(templateString + interfaceType.generate() + " to" + interfaceType.getSimpleName() + "_" +
													beforeContent + "(void* _ref" + "){" +
													generateStatement(thisType + " _this = *((" + thisType + "*) _ref)") +
													generateStatement(interfaceType.getSimpleName() + "Data" + joinedTypeArguments + " data") +
													generateStatement("data." + beforeContent.toLowerCase() + " = _this") + generateStatement(
								"return " + interfaceType.generate() + " { " + beforeContent + "Tag, " + "data }") +
													System.lineSeparator() + "}" + System.lineSeparator());
					}

					forwardDeclarations.add(templateString + "struct " + beforeContent + ";" + System.lineSeparator());

					structureNames.push(beforeContent);
					final var generated =
							dependencies + templateString + "struct " + beforeContent + " {" + fields + System.lineSeparator() +
							compileStatements(content, App::compileClassSegment) + "};" + System.lineSeparator();
					structureNames.pop();

					if (variants.isEmpty()) {
						structures.add(generated);
					} else {
						sealedStructures.add(generated);
					}

					return Optional.of("");
				}
			}
		}*//*

		return Optional.empty();*//*
	*/}
/*private static*/ char* joinTypeArguments(List<char*> typeParameters) {/*
		String joinedTypeArguments;*//*
		if (typeParameters.isEmpty()) {
			joinedTypeArguments = "";
		}*//* else {
			joinedTypeArguments = "<" + String.join(", ", typeParameters) + ">";
		}*//*
		return joinedTypeArguments;*//*
	*/}
/*private static*/ char* generateStatement(char* content) {/*
		return generateWithIndent(content) + ";";*//*
	*/}
/*private static*/ char* generateWithIndent(char* content) {/*
		return System.lineSeparator() + "\t" + content;*//*
	*/}
/*private static*/ boolean isIdentifier(char* input) {/*
		for (var i = 0;*//* i < input.length();*//* i++) {
			if (!Character.isLetter(input.charAt(i))) {
				return false;
			}
		}*//*

		return true;*//*
	*/}
/*private static*/ char* compileClassSegment(char* input) {/*
		if (input.isEmpty()) {
			return "";
		}*//*

		final var maybeInterface = compileStructure("interface", input);*//*
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}*//*

		final var maybeRecord = compileStructure("record", input);*//*
		if (maybeRecord.isPresent()) {
			return maybeRecord.get();
		}*//*

		final var maybeEnum = compileStructure("enum", input);*//*
		if (maybeEnum.isPresent()) {
			return maybeEnum.get();
		}*//*

		final var paramStart = input.indexOf("(");*//*
		if (paramStart >= 0) {
			final var definition = input.substring(0, paramStart).strip();
			final var withParams = input.substring(paramStart + 1);
			final var paramEnd = withParams.indexOf(")");
			if (paramEnd >= 0) {
				final var params = withParams.substring(0, paramEnd).strip();
				final var withBraces = withParams.substring(paramEnd + 1).strip();
				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var content = withBraces.substring(1, withBraces.length() - 1);

					final var generated = compileDefinitionOrPlaceholder(definition) + "(" + compileParameters(params) + ") {" +
																compileStatements(content, App::compileMethodSegment) + "}" + System.lineSeparator();

					functions.add(generated);
					return "";
				}
			}
		}*//*

		if (input.endsWith(";")) {
			final var slice = input.substring(0, input.length() - 1);
			return compileEnumValues(slice).orElseGet(() -> generateStatement(compileDefinition(slice).orElseGet(() -> wrap(
					slice))));

		}*//*

		return wrap(input);*//*
	*/}
/*private static*/ Optional<char*> compileEnumValues(char* input) {/*
		final var segments =
				Arrays.stream(input.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).toList();*//*

		for (var segment : segments) {
			final var stripped = segment.strip();
			final var maybeEnumValue = compileEnumValue(stripped);
			if (maybeEnumValue.isPresent()) {
				globals.add(maybeEnumValue.get() + System.lineSeparator());
			} else {
				return Optional.empty();
			}
		}*//*

		return Optional.of("");*//*
	*/}
/*private static*/ Optional<char*> compileEnumValue(char* stripped) {/*
		if (stripped.endsWith(")")) {
			final var slice = stripped.substring(0, stripped.length() - 1);
			final var i = slice.indexOf("(");
			if (i >= 0) {
				final var name = slice.substring(0, i).strip();
				final var arguments = slice.substring(i + 1);
				if (isIdentifier(name)) {
					return Optional.of(wrap("name"));
				}
			}
		}*//*

		return Optional.empty();*//*
	*/}
/*private static*/ char* compileMethodSegment(char* input) {/*
		return wrap(input);*//*
	*/}
/*private static*/ char* compileParameters(char* input) {/*
		if (input.isEmpty()) {
			return "";
		}*//*
		return compileDefinitionOrPlaceholder(input);*//*
	*/}
/*private static*/ char* compileDefinitionOrPlaceholder(char* input) {/*
		return compileDefinition(input).orElseGet(() -> wrap(input));*//*
	*/}
/*private static*/ Optional<char*> compileDefinition(char* input) {/*
		final var nameSeparator = input.lastIndexOf(" ");*//*
		if (nameSeparator < 0) {
			return Optional.empty();
		}*//*

		final var beforeName = input.substring(0, nameSeparator);*//*
		final var name = input.substring(nameSeparator + 1).strip();*//*
		final var typeSeparator = beforeName.lastIndexOf(" ");*//*
		if (typeSeparator >= 0) {
			final var beforeType = beforeName.substring(0, typeSeparator);
			final var type = beforeName.substring(typeSeparator + 1).strip();
			return Optional.of(wrap(beforeType) + " " + compileType(type).generate() + " " + name);
		}*//* else {
			return Optional.of(compileType(beforeName).generate() + " " + name);
		}*//*
	*/}
/*private static*/ CPPType compileType(char* input) {/*
		if (input.equals("void")) {
			return CPPPrimitiveType.Void;
		}*//*

		if (input.endsWith("[]")) {
			final var slice = input.substring(0, input.length() - 2);
			return new CPointerType(compileType(slice));
		}*//*

		if (input.equals("String")) {
			return new CPointerType(CPPPrimitiveType.Char);
		}*//*

		if (input.endsWith(">")) {
			final var withoutEnd = input.substring(0, input.length() - 1);
			final var i = withoutEnd.indexOf("<");
			if (i >= 0) {
				final var base = withoutEnd.substring(0, i);
				final var typeArguments = withoutEnd.substring(i + 1);

				final var list = Arrays
						.stream(typeArguments.split(Pattern.quote(",")))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(input1 -> compileType(input1).generate())
						.toList();

				return new CTemplateType(base, list);
			}
		}*//*

		if (isIdentifier(input)) {
			return new CIdentifier(input);
		}*//*

		return new Placeholder(input);*//*
	*/}
/*private static*/ char* wrap(char* input) {/*
		return "start" + input.replace("start", "start").replace("end", "end") + "end";*//*
	*/}
int main(){
	return 0;
}