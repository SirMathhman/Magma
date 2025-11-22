/*public*/ struct Main {
};
Type toType_PrimitiveType(void* _this){
	PrimitiveType this = *((PrimitiveType*) _this);
	TypeData data;
	data.primitivetype = this;
	return { TypeVariant.PrimitiveTypeVariant, data };
}
/*private*/ struct PrimitiveType {
};
PrimitiveType PrimitiveTypeVoid = new_PrimitiveType("void");
PrimitiveType PrimitiveTypeChar = new_PrimitiveType("char");
PrimitiveType new_PrimitiveType(char* content){
	PrimitiveType this;
	this.content = content;
	return this;
}
/*@Override
		public*/ char* generate_PrimitiveType(){
	/*return this.content*/;
}
/*@Override
		public*/ char* toIdentifier_PrimitiveType(){
	/*return this.name().toLowerCase()*/;
}
/*}*/enum ResultVariant {
	ErrVariant, 
	OkVariant
};
template <typename T, typename X>
union ResultData {
	ErrData<T, X> err;
	OkData<T, X> ok;
};
template <typename T, typename X>
/*private*/ struct Result {
	ResultVariant variant;
	ResultData data;
};
template <typename T, typename X, typename R>
/**/ Result<R, X> mapValue_Result(Function<T, R> mapper);
/*}*/enum TypeVariant {
	IdentifierVariant, 
	PlaceholderVariant, 
	PointerTypeVariant, 
	PrimitiveTypeVariant, 
	TemplateTypeVariant
};
union TypeData {
	IdentifierData identifier;
	PlaceholderData placeholder;
	PointerTypeData pointertype;
	PrimitiveTypeData primitivetype;
	TemplateTypeData templatetype;
};
/*private*/ struct Type {
	TypeVariant variant;
	TypeData data;
};
char* generate();
char* toIdentifier();
/*}*/enum MethodDeclarationVariant {
	ConstructorVariant, 
	DeclarationVariant, 
	PlaceholderVariant
};
union MethodDeclarationData {
	ConstructorData constructor;
	DeclarationData declaration;
	PlaceholderData placeholder;
};
/*private*/ struct MethodDeclaration {
	MethodDeclarationVariant variant;
	MethodDeclarationData data;
};
char* generate();
/*}*//*private record Err<T, X>*/(X error);
/*private record Ok<T, X>*/(T value);
/*private static class State {
		private final String input;
		private final ArrayList<String> segments;
		private final StringBuilder buffer;
		private int index;
		private int*/ /*depth;

		public*/ State_Main(char* input){
	this.input = input;
	this.index = /*0*/;
	this.buffer = /*new StringBuilder()*/;
	this.depth = /*0*/;
	this.segments = /*new ArrayList<String>()*/;
	/*}

		private boolean isShallow() {
			return this*/.depth = /*= 1*/;
	/*}

		private boolean isLevel() {
			return this*/.depth = /*= 0*/;
	/*}

		private State append(Character next) {
			this.buffer.append(next)*/;
	/*return this*/;
	/*}

		private Optional<Character> pop() {
			if (this.index < this.input.length()) {
				final var value = this.input.charAt(this.index);
				this.index++;
				return Optional.of(value);
			}*/
	/*else {
				return Optional.empty();
			}*/
	/*}

		private State advance() {
			this.segments.add(this.buffer.toString())*/;
	/*this.buffer.setLength(0)*/;
	/*return this*/;
	/*}

		private State enter() {
			this*/.depth = this.depth + 1;
	/*return this*/;
	/*}

		private State exit() {
			this*/.depth = this.depth - 1;
	/*return this*/;
	/*}

		private Stream<String> stream() {
			return this.segments.stream()*/;
	/*}*/
}
/*private*/ record PointerType_Main(Type type);
/*private*/ record TemplateType_Main(char* base, List<Type> list);
/*private*/ record Identifier_Main(char* value);
/*private*/ record Placeholder_Main(char* input);
/*private*/ record Constructor_Main(char* structName);
/*private*/ record Declaration_Main(List<char*> typeParameter, Optional<char*> beforeType, char* type, char* name);
/*public static*/ void main_Main(char** args){
	/*run().ifPresent(Throwable::printStackTrace)*/;
}
/*private static*/ Optional<IOException> run_Main(){
	/*final var source*/ = Paths.get(".", "src", "main", "java", "magma", "Main.java");
	/*final var target*/ = source.resolveSibling("Main.cpp");
	/*final var input*/ = /*readString(source)*/.mapValue(Main::compile);
	/*return switch (input) {
			case Err<String, IOException> v -> Optional.of(v.error);
			case Ok<String, IOException> v -> writeString(target, v.value);
		}*/
	/**/;
}
/*private static*/ Optional<IOException> writeString_Main(Path target, char* output){
	/*try {
			Files.writeString(target, output);
			return Optional.empty();
		}*/
	/*catch (IOException e) {
			return Optional.of(e);
		}*/
}
/*private static*/ Result<char*, IOException> readString_Main(Path source){
	/*try {
			return new Ok<String, IOException>(Files.readString(source));
		}*/
	/*catch (IOException e) {
			return new Err<String, IOException>(e);
		}*/
}
/*private static*/ char* compile_Main(char* input){
	/*return compileStatements(input, Main::compileRootSegment)*/;
}
/*private static*/ char* compileStatements_Main(char* input, Function<char*, char*> mapper){
	/*return compileAll(input, mapper, Main::foldStatement)*/;
}
/*private static*/ char* compileAll_Main(char* input, Function<char*, char*> mapper, BiFunction<State, Character, State> folder){
	/*return divide(input, folder).map(mapper).collect(Collectors.joining(""))*/;
}
/*private static*/ Stream<char*> divide_Main(char* input, BiFunction<State, Character, State> folder){
	/*var current*/ = /*new State(input)*/;
	/*while (true) {
			final var maybeNext = current.pop();
			if (maybeNext.isEmpty()) {
				break;
			}

			final var next = maybeNext.get();
			current = folder.apply(current, next);
		}*/
	/*return current.advance().stream()*/;
}
/*private static*/ State foldStatement_Main(State current, Character next){
	/*final var appended*/ = current.append(next);
	/*if (next*/ = /*= '*/;
	/*' && appended.isLevel()) {
			return appended.advance();
		}*/
	/*if (next == '*/
}
/*' && appended.isShallow*/();
/*if */(/*next == '{'*/){
	/*return appended.enter()*/;
	/*}

		if (next == '*/
}
/*') {
			return appended.exit*/();
/*}*//*private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return compileStructure("class", stripped).orElseGet(() -> wrap(stripped));
	}*//*private static Optional<String> compileStructure(String type, String stripped) {
		final var i = stripped.indexOf(type + " ");
		if (i < 0) {return Optional.empty();}
		final var modifiers = stripped.substring(0, i).strip();
		final var afterKeyword = stripped.substring(i + (type + " ").length()).strip();

		final var i1 = afterKeyword.indexOf("{");
		if (i1 < 0) {return Optional.empty();}
		var beforeContent = afterKeyword.substring(0, i1).strip();
		final var content = afterKeyword.substring(i1 + 1);

		List<String> variants = new ArrayList<String>();
		final var i2 = beforeContent.indexOf("permits ");
		if (i2 >= 0) {
			final var substring1 = beforeContent.substring(i2 + "permits ".length());
			beforeContent = beforeContent.substring(0, i2);

			variants = splitValues(substring1);
		}

		List<Type> implementees = new ArrayList<Type>();
		final var i4 = beforeContent.indexOf("implements ");
		if (i4 >= 0) {
			final var implementeesString = beforeContent.substring(i4 + "implements ".length());
			beforeContent = beforeContent.substring(0, i4);

			implementees = divide(implementeesString, Main::foldValue)
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.map(Main::parseType)
					.toList();
		}

		List<String> typeParameters = new ArrayList<String>();
		final var i3 = beforeContent.indexOf("<");
		if (i3 >= 0) {
			final var substring1 = beforeContent.substring(i3 + 1).strip();
			beforeContent = beforeContent.substring(0, i3);
			if (substring1.endsWith(">")) {
				final var substring = substring1.substring(0, substring1.length() - 1);
				typeParameters = splitValues(substring);
			}
		}

		if (!isIdentifier(beforeContent)) {return Optional.empty();}

		final var modifiersList = Arrays
				.stream(modifiers.split(Pattern.quote(" ")))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.collect(Collectors.toCollection(ArrayList::new));

		var name = beforeContent.strip();

		final var templateString = generateTemplateString(typeParameters);

		final String fields;
		var dependencies = new StringBuilder();
		for (var implementee : implementees) {
			final var identifier = implementee.toIdentifier();

			final var variant = identifier + "Variant" + "." + name + "Variant";
			final var conversionFunctionContent =
					generateStatement(name + " this = *((" + name + "*) _this)") + generateStatement(identifier + "Data data") +
					generateStatement("data." + name.toLowerCase() + " = this") +
					generateStatement("return { " + variant + ", data }");

			final var conversionFunction =
					implementee.generate() + " to" + identifier + "_" + name + "(void* _this){" + conversionFunctionContent +
					System.lineSeparator() + "}" + System.lineSeparator();

			dependencies.append(conversionFunction);
		}

		if (!variants.isEmpty() && modifiersList.contains("sealed")) {
			modifiersList.remove("sealed");

			final var enumFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + "Variant")
					.collect(Collectors.joining(", "));

			final var generatedEnum =
					"enum " + name + "Variant {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();

			final String joinedTypeParameters;
			if (typeParameters.isEmpty()) {
				joinedTypeParameters = "";
			} else {
				joinedTypeParameters = typeParameters.stream().collect(Collectors.joining(", ", "<", ">"));
			}

			final var unionFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + "Data" + joinedTypeParameters + " " +
													variant.toLowerCase() + ";")
					.collect(Collectors.joining());

			final var generatedUnion =
					templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" +
					System.lineSeparator();

			fields = System.lineSeparator() + "\t" + name + "Variant variant;" + System.lineSeparator() + "\t" + name +
							 "Data data;";

			dependencies.append(generatedEnum).append(generatedUnion);
		} else {
			fields = "";
		}

		final String joinedModifiers;
		if (modifiersList.isEmpty()) {
			joinedModifiers = "";
		} else {
			joinedModifiers =
					modifiersList.stream().map(Main::wrap).map(modifier -> modifier + " ").collect(Collectors.joining());
		}

		var finalTypeParameters = typeParameters;
		return Optional.of(
				dependencies + templateString + joinedModifiers + "struct " + name + " {" + fields + System.lineSeparator() +
				"};" + System.lineSeparator() +
				compileStatements(content, input1 -> compileClassSegment(input1, name, finalTypeParameters)));
	}

	private static String generateStatement(String content) {
		return System.lineSeparator() + "\t" + content + ";";
	}

	private static List<String> splitValues(String input) {
		return Arrays.stream(input.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
	}

	private static String generateTemplateString(List<String> typeParameters) {
		final String templateString;
		if (typeParameters.isEmpty()) {
			templateString = "";
		} else {
			templateString = "template " + typeParameters
					.stream()
					.map(typeParam -> "typename " + typeParam)
					.collect(Collectors.joining(", ", "<", ">")) + System.lineSeparator();
		}
		return templateString;
	}

	private static boolean isIdentifier(String input) {
		final var stripped = input.strip();
		for (var i = 0; i < stripped.length(); i++) {
			final var c = stripped.charAt(i);
			if (!Character.isLetter(c)) {
				return false;
			}
		}

		return true;
	}

	private static String compileClassSegment(String input, String structName, List<String> typeParameters) {
		final var stripped = input.strip();

		final var maybeEnum = compileStructure("enum", input);
		if (maybeEnum.isPresent()) {
			return maybeEnum.get();
		}

		final var maybeInterface = compileStructure("interface", input);
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final var maybeEnumValues = compileEnumValues(input, structName);
		if (maybeEnumValues.isPresent()) {
			return maybeEnumValues.get();
		}

		final var i = stripped.indexOf("(");
		if (i >= 0) {
			final var declarationString = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var parameters = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();

				final var compiledParameters = divide(parameters, Main::foldValue)
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.toList()
						.stream()
						.map(param -> compileDeclarationOrPlaceholder(param, structName, typeParameters))
						.collect(Collectors.joining(", "));

				final var declaration = parseMethodDeclaration(declarationString, structName, typeParameters);
				final var header = declaration.generate() + "(" + compiledParameters + ")";

				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var inputContent = withBraces.substring(1, withBraces.length() - 1);

					final var compiled = compileStatements(inputContent, Main::compileMethodSegment);
					final String outputContent;
					if (declaration instanceof Constructor) {
						outputContent = generateStatement(structName + " this") + compiled + generateStatement("return this");
					} else {
						outputContent = compiled;
					}

					return header + "{" + outputContent + System.lineSeparator() + "}" + System.lineSeparator();
				}

				return header + ";" + System.lineSeparator();
			}
		}

		return wrap(stripped);
	}

	private static MethodDeclaration parseMethodDeclaration(String declaration,
																													String structName,
																													List<String> typeParameters) {
		return parseDeclaration(declaration, structName, typeParameters)
				.<MethodDeclaration>map(value -> value)
				.or(() -> parseConstructor(declaration, structName))
				.orElseGet(() -> new Placeholder(declaration));
	}

	private static Optional<MethodDeclaration> parseConstructor(String declaration, String structName) {
		if (declaration.strip().equals(structName)) {
			return Optional.of(new Constructor(structName));
		} else {
			return Optional.empty();
		}
	}

	private static Optional<String> compileEnumValues(String input, String structName) {
		final var stripped = input.strip();
		if (!stripped.endsWith(";")) {
			return Optional.empty();
		}

		final var enumValues = divide(stripped.substring(0, stripped.length() - 1), Main::foldValue)
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList();

		final var buffer = new StringBuilder();
		if (!enumValues.isEmpty()) {
			for (var enumValue : enumValues) {
				if (enumValue.endsWith(")")) {
					final var substring = enumValue.substring(0, enumValue.length() - 1);
					final var i = substring.indexOf("(");
					if (i >= 0) {
						final var name = substring.substring(0, i);
						if (!isIdentifier(name)) {
							return Optional.empty();
						}

						final var substring2 = substring.substring(i + 1);
						buffer.append(
								structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" +
								System.lineSeparator());
					}
				}
			}
		}

		return Optional.of(buffer.toString());
	}

	private static State foldValue(State state, Character next) {
		if (next == ',' && state.isLevel()) {
			return state.advance();
		}

		final var appended = state.append(next);
		if (next == '<') {
			return appended.enter();
		}
		if (next == '>') {
			return appended.exit();
		}
		return appended;
	}

	private static String compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			return System.lineSeparator() + "\t" + compileMethodStatement(substring) + ";";
		}

		return System.lineSeparator() + "\t" + wrap(stripped);
	}

	private static String compileMethodStatement(String input) {
		final var i = input.indexOf("=");
		if (i >= 0) {
			final var substring = input.substring(0, i);
			final var substring1 = input.substring(i + 1);
			return compileExpression(substring) + " = " + compileExpression(substring1);
		}

		return wrap(input);
	}

	private static String compileExpression(String input) {
		final var stripped = input.strip();
		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var substring = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			return compileExpression(substring) + "." + substring1;
		}

		if (isIdentifier(stripped)) {
			return stripped;
		}

		return wrap(stripped);
	}

	private static String compileDeclarationOrPlaceholder(String input, String structName, List<String> typeParameters) {
		return compileDeclaration(input, structName, typeParameters).orElseGet(() -> wrap(input));
	}

	private static Optional<String> compileDeclaration(String input, String structName, List<String> typeParameters) {
		return parseDeclaration(input, structName, typeParameters).map(Declaration::generate);
	}

	private static Optional<Declaration> parseDeclaration(String input, String structName, List<String> typeParameters) {
		final var stripped = input.strip();
		final var nameSeparator = stripped.lastIndexOf(" ");
		if (nameSeparator >= 0) {
			final var beforeName = stripped.substring(0, nameSeparator).strip();
			final var name = stripped.substring(nameSeparator + 1).strip();

			var typeSeparator = -1;
			var depth = 0;
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
			}

			if (typeSeparator < 0 && isIdentifier(name)) {
				final var type = compileType(beforeName);
				return Optional.of(new Declaration(type, name));
			}

			var beforeType = beforeName.substring(0, typeSeparator).strip();

			final var copy = new ArrayList<String>(typeParameters);
			if (beforeType.endsWith(">")) {
				final var substring = beforeType.substring(0, beforeType.length() - 1);
				final var i = substring.indexOf("<");
				if (i >= 0) {
					final var substring2 = substring.substring(i + 1);
					copy.addAll(splitValues(substring2));

					beforeType = substring.substring(0, i);
				}
			}

			if (isIdentifier(name)) {
				return Optional.of(new Declaration(copy,
																					 Optional.of(beforeType),
																					 compileType(beforeName.substring(typeSeparator + 1)),
																					 name + "_" + structName));
			}
		}

		return Optional.empty();
	}

	private static String compileType(String input) {
		return parseType(input).generate();
	}

	private static Type parseType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return PrimitiveType.Void;
		}

		if (stripped.endsWith("[]")) {
			final var slice = stripped.substring(0, stripped.length() - 2);
			final var type = parseType(slice);
			return new PointerType(type);
		}

		if (stripped.equals("String")) {
			return new PointerType(PrimitiveType.Char);
		}

		if (stripped.endsWith(">")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i = substring.indexOf("<");
			if (i >= 0) {
				final var base = substring.substring(0, i);
				final var parameters = substring.substring(i + 1);

				final var list = divide(parameters, Main::foldValue).map(Main::parseType).toList();

				return new TemplateType(base, list);
			}
		}

		if (isIdentifier(stripped)) {
			return new Identifier(stripped);
		}

		return new Placeholder(stripped);
	}

	private static String wrap(String input) {
		final var replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}
}*//**/