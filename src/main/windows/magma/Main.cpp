CPrimitiveType CPrimitiveType_Char = new_CPrimitiveType("char");
CPrimitiveType CPrimitiveType_Void = new_CPrimitiveType("void");
struct CPrimitiveType {
	char* content;
};
enum ResultTag {
	ErrType,
	OkType
};
template <typename T, typename X>
union ResultData {
	Err<T, X> err;
	Ok<T, X> ok;
}
template <typename T, typename X>
struct Result {
	ResultTag _tag;
	ResultData<T, X> _data;
};
enum CTypeTag {
	CIdentifierType,
	CPlaceholderType,
	CPointerTypeType,
	CPrimitiveTypeType,
	CTemplateTypeType
};
union CTypeData {
	CIdentifier cidentifier;
	CPlaceholder cplaceholder;
	CPointerType cpointertype;
	CPrimitiveType cprimitivetype;
	CTemplateType ctemplatetype;
}
struct CType {
	CTypeTag _tag;
	CTypeData _data;
};
enum CDefinableTag {
	CDefinitionType,
	CPlaceholderType
};
union CDefinableData {
	CDefinition cdefinition;
	CPlaceholder cplaceholder;
}
struct CDefinable {
	CDefinableTag _tag;
	CDefinableData _data;
};
enum JMethodHeaderTag {
	JConstructorType,
	JDefinitionType,
	JPlaceholderType
};
union JMethodHeaderData {
	JConstructor jconstructor;
	JDefinition jdefinition;
	JPlaceholder jplaceholder;
}
struct JMethodHeader {
	JMethodHeaderTag _tag;
	JMethodHeaderData _data;
};
struct CFunctionHeader {
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
	CType child;
};
struct CTemplateType {
	char* base;
	List<CType> typeArguments;
};
struct CIdentifier {
	char* input;
};
struct CPlaceholder {
	char* input;
};
struct CDefinition {
	CType type;
	char* name;
};
struct JDefinition {
	Optional<char*> beforeType;
	CType type;
	char* name;
};
struct JConstructor {
	char* input;
};
struct JPlaceholder {
	char* input;
};
struct Main {
};
CType toCType_CPrimitiveType(void* _ref){
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	CTypeData data;
	data.err = this;
	return CType { CPrimitiveTypeType, data };
}
CPrimitiveType new_CPrimitiveType(char* content) {
	CPrimitiveType this;
	this.content = content;
	return this;
}
char* generate_CPrimitiveType(/**/) {
	/*return this.content*/;
}
char* generate_CType(/**/);
char* generate_CDefinable(/**/);
CDefinable toCDefinition_JMethodHeader(/**/);
Result<T, X> toResult<T, X>_Err(void* _ref){
	Err<T, X> _this = *((Err<T, X>*) _ref);
	Result<T, X>Data data;
	data.err = this;
	return Result<T, X> { ErrType, data };
}
Result<T, X> toResult<T, X>_Ok(void* _ref){
	Ok<T, X> _this = *((Ok<T, X>*) _ref);
	Result<T, X>Data data;
	data.err = this;
	return Result<T, X> { OkType, data };
}
CType toCType_CPointerType(void* _ref){
	CPointerType _this = *((CPointerType*) _ref);
	CTypeData data;
	data.err = this;
	return CType { CPointerTypeType, data };
}
char* generate_CPointerType(/**/) {
	/*return this.child.generate() + "*"*/;
}
CType toCType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CTypeData data;
	data.err = this;
	return CType { CTemplateTypeType, data };
}
char* generate_CTemplateType(/**/) {
	final var joined = this.typeArguments.stream().map(CType::generate).collect(Collectors.joining(", "));
	/*return this.base + "<" + joined + ">"*/;
}
CType toCType_CIdentifier(void* _ref){
	CIdentifier _this = *((CIdentifier*) _ref);
	CTypeData data;
	data.err = this;
	return CType { CIdentifierType, data };
}
char* generate_CIdentifier(/**/) {
	/*return this.input*/;
}
/*CType, CDefinable*/ to/*CType, CDefinable*/_CPlaceholder(void* _ref){
	CPlaceholder _this = *((CPlaceholder*) _ref);
	/*CType, CDefinable*/Data data;
	data.err = this;
	return /*CType, CDefinable*/ { CPlaceholderType, data };
}
char* wrap_CPlaceholder(char* input) {
	final var replaced = input.replace("/*", "start").replace("*/", "end");
	/*return "start" + replaced + "end"*/;
}
char* generate_CPlaceholder(/**/) {
	/*return wrap(this.input)*/;
}
CDefinable toCDefinable_CDefinition(void* _ref){
	CDefinition _this = *((CDefinition*) _ref);
	CDefinableData data;
	data.err = this;
	return CDefinable { CDefinitionType, data };
}
char* generate_CDefinition(/**/) {
	/*return this.type.generate() + " " + this.name*/;
}
JMethodHeader toJMethodHeader_JDefinition(void* _ref){
	JDefinition _this = *((JDefinition*) _ref);
	JMethodHeaderData data;
	data.err = this;
	return JMethodHeader { JDefinitionType, data };
}
CDefinable toCDefinition_JDefinition(/**/) {
	/*return new CDefinition(this.type, this.name)*/;
}
JMethodHeader toJMethodHeader_JConstructor(void* _ref){
	JConstructor _this = *((JConstructor*) _ref);
	JMethodHeaderData data;
	data.err = this;
	return JMethodHeader { JConstructorType, data };
}
CDefinable toCDefinition_JConstructor(/**/) {
	final var type = new CIdentifier(this.input);
	/*return new CDefinition(type, "new_" + this.input)*/;
}
JMethodHeader toJMethodHeader_JPlaceholder(void* _ref){
	JPlaceholder _this = *((JPlaceholder*) _ref);
	JMethodHeaderData data;
	data.err = this;
	return JMethodHeader { JPlaceholderType, data };
}
CDefinable toCDefinition_JPlaceholder(/**/) {
	/*return new CPlaceholder(this.input)*/;
}
public static final List<String> functions = new ArrayList<String> new_public static final List<String> functions = new ArrayList<String>(/**/);
public static final List<String> structures = new ArrayList<String> new_public static final List<String> structures = new ArrayList<String>(/**/);
private static final List<String> globals = new ArrayList<String> new_private static final List<String> globals = new ArrayList<String>(/**/);
private static final Stack<String> structureNames = new Stack<String> new_private static final Stack<String> structureNames = new Stack<String>(/**/);
void main_Main(char** args) {
	/*run().ifPresent(Throwable::printStackTrace)*/;
}
Optional<IOException> run_Main(/**/) {
	final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");/*return switch (readString(source)) {
			case Ok(var input) -> {
				final var target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");
				final var output = compile(input);
				yield writeString(target, output);
			}
			case Err<String, IOException> v -> Optional.of(v.error);
		}*/
	/**/;
}
Optional<IOException> writeString_Main(char* output) {/*try {
			Files.writeString(target, output);
			return Optional.empty();
		}*//*catch (IOException e) {
			return Optional.of(e);
		}*/
}
/*IOException>*/ readString_Main(Path source) {/*try {
			return new Ok<String, IOException>(Files.readString(source));
		}*//*catch (IOException e) {
			return new Err<String, IOException>(e);
		}*/
}
char* compile_Main(char* input) {
	final var compiled = compileStatements(input, Main::compileRootSegment);
	final var joinedGlobals = String.join("", globals);
	final var joinedStructures = String.join("", structures);
	final var joinedFunctions = String.join("", functions);
	/*return joinedGlobals + joinedStructures + joinedFunctions + compiled*/;
}
char* compileStatements_Main(/*String>*/ mapper) {
	final var segments = new ArrayList<String>();
	var buffer = new StringBuilder();
	var depth = 0;
	for (var i = 0;
	/*i < input.length()*/;/*i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
			} else if (c == '}*//*' && depth == 1) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
				depth--;
			}*//*else if (c == '{') {
				depth++;
			} else if (c == '}*//*') {
				depth--;
			}*/
}
segments.add new_segments.add(/*buffer.toString(*/);
return segments.stream new_return segments.stream(/**/);
/*private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return compileStructure(stripped, "class").orElseGet(() -> CPlaceholder.wrap(stripped));
	}*//*private static Optional<String> compileStructure(String stripped, String type) {
		final var i = stripped.indexOf(type + " ");
		if (i >= 0) {
			final var substring = stripped.substring(i + (type + " ").length()).strip();
			if (substring.endsWith("}")) {
				final var substring1 = substring.substring(0, substring.length() - 1);
				final var i1 = substring1.indexOf("{");
				if (i1 >= 0) {
					var beforeContent = substring1.substring(0, i1).strip();
					final var content = substring1.substring(i1 + 1).strip();

					final var i2 = beforeContent.indexOf("permits");
					List<String> variants = new ArrayList<String>();
					if (i2 >= 0) {
						final var stripped1 = beforeContent.substring(i2 + "permits".length()).strip().split(Pattern.quote(","));

						variants = Arrays.stream(stripped1).map(String::strip).filter(segment -> !segment.isEmpty()).toList();

						beforeContent = beforeContent.substring(0, i2).strip();
					}

					Optional<String> maybeImplements = Optional.empty();
					final var i4 = beforeContent.indexOf("implements ");
					if (i4 >= 0) {
						final var substring2 = beforeContent.substring(i4 + "implements ".length());
						maybeImplements = Optional.of(compileTypeToString(substring2.strip()));

						beforeContent = beforeContent.substring(0, i4).strip();
					}

					var structureFields = "";
					if (beforeContent.endsWith(")")) {
						final var substring2 = beforeContent.substring(0, beforeContent.length() - 1);
						final var i3 = substring2.indexOf("(");
						if (i3 >= 0) {
							final var substring4 = substring2.substring(i3 + 1);
							structureFields += Arrays
									.stream(substring4.split(Pattern.quote(",")))
									.map(String::strip)
									.filter(slice -> !slice.isEmpty())
									.map(Main::compileDefinition)
									.flatMap(Optional::stream)
									.map(Main::generateStatement)
									.collect(Collectors.joining());

							beforeContent = substring2.substring(0, i3);
						}
					}

					List<String> typeParameters = new ArrayList<String>();
					if (beforeContent.endsWith(">")) {
						final var substring2 = beforeContent.substring(0, beforeContent.length() - 1);
						final var i3 = substring2.indexOf("<");
						if (i3 >= 0) {
							final var substring3 = substring2.substring(i3 + 1).strip().split(Pattern.quote(","));
							typeParameters = Arrays.stream(substring3).map(String::strip).filter(slice -> !slice.isEmpty()).toList();

							beforeContent = beforeContent.substring(0, i3).strip();
						}
					}

					var templateString = "";
					if (!typeParameters.isEmpty()) {
						final var joined =
								typeParameters.stream().map(slice -> "typename " + slice).collect(Collectors.joining(", "));

						templateString = "template <" + joined + ">" + System.lineSeparator();
					}

					final String typeArguments;
					if (typeParameters.isEmpty()) {
						typeArguments = "";
					} else {
						typeArguments = "<" + String.join(", ", typeParameters) + ">";
					}

					var beforeStruct = "";
					if (maybeImplements.isPresent()) {
						final var superType = maybeImplements.get();
						final var thisType = beforeContent + typeArguments;
						functions.add(superType + " to" + superType + "_" + beforeContent + "(void* _ref){" +
													generateStatement(thisType + " _this = *((" + thisType + "*) _ref)") +
													generateStatement(superType + "Data data") + generateStatement("data.err = this") +
													generateStatement("return " + superType + " { " + beforeContent + "Type, data }") +
													System.lineSeparator() + "}" + System.lineSeparator());
					}

					if (!variants.isEmpty()) {
						final var enumFields = variants
								.stream()
								.map(segment -> System.lineSeparator() + "\t" + segment + "Type")
								.collect(Collectors.joining(","));

						final var tagType = beforeContent + "Tag";
						final var generatedEnum =
								"enum " + tagType + " {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();

						final var unionFields = variants
								.stream()
								.map(segment -> System.lineSeparator() + "\t" + segment + typeArguments + " " + segment.toLowerCase() +
																";")
								.collect(Collectors.joining());

						final var unionType = beforeContent + "Data";
						final var generatedUnion =
								templateString + "union " + unionType + " {" + unionFields + System.lineSeparator() + "}" +
								System.lineSeparator();

						beforeStruct += generatedEnum + generatedUnion;
						structureFields +=
								generateStatement(tagType + " _tag") + generateStatement(unionType + typeArguments + " _data");
					}

					structureNames.push(beforeContent);
					final var generated = beforeStruct + templateString + "struct " + beforeContent + " {" + structureFields +
																compileStatements(content, Main::compileClassSegment) + System.lineSeparator() + "};" +
																System.lineSeparator();
					structureNames.pop();

					structures.add(generated);
					return Optional.of("");
				}
			}
		}

		return Optional.empty();
	}*//*private static String generateStatement(String content) {
		return System.lineSeparator() + "\t" + content + ";";
	}*//*private static boolean isIdentifier(String input) {
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (!Character.isLetter(c)) {
				return false;
			}
		}

		return true;
	}*//*private static String compileClassSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		final var maybeInterface = compileStructure(stripped, "interface");
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final var maybeRecord = compileStructure(stripped, "record");
		if (maybeRecord.isPresent()) {
			return maybeRecord.get();
		}

		final var maybeEnum = compileStructure(stripped, "enum");
		if (maybeEnum.isPresent()) {
			return maybeEnum.get();
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var maybeClassStatement = compileClassStatement(substring);
			if (maybeClassStatement.isPresent()) {
				return maybeClassStatement.get();
			}
		}

		return compileMethod(stripped).orElseGet(() -> CPlaceholder.wrap(stripped));
	}*//*private static Optional<String> compileMethod(String stripped) {
		final var i = stripped.indexOf("(");
		if (i >= 0) {
			final var substring = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var substring2 = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();
				final var header = parseDefinition(substring)
						.<JMethodHeader>map(definition -> definition)
						.or(() -> parseConstructor(substring))
						.orElseGet(() -> new JPlaceholder(substring));

				final var headerWithString =
						transformHeader(header).generate() + "(" + compileDefinitionOrPlaceholder(substring2) + ")";

				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var content = withBraces.substring(1, withBraces.length() - 1);
					final var compiledContent = compileStatements(content, Main::compileMethodSegment);
					final String outputContent;
					if (header instanceof JConstructor(var name)) {
						outputContent = generateStatement(name + " this") + compiledContent + generateStatement("return this");
					} else {
						outputContent = compiledContent;
					}

					final var generated =
							headerWithString + " {" + outputContent + System.lineSeparator() + "}" + System.lineSeparator();
					functions.add(generated);
					return Optional.of("");
				} else {
					final var generated = headerWithString + ";" + System.lineSeparator();
					functions.add(generated);
					return Optional.of("");
				}
			}
		}

		return Optional.empty();
	}*//*private static CDefinable transformHeader(JMethodHeader header) {
		return switch (header) {
			case JDefinition jDefinition -> new CDefinition(jDefinition.type,
																											jDefinition.name + "_" + structureNames.peek());
			default -> header.toCDefinition();
		};
	}*//*private static Optional<JMethodHeader> parseConstructor(String input) {
		final var stripped = input.strip();
		return Optional.of(new JConstructor(stripped));
	}*//*private static Optional<String> compileClassStatement(String input) {
		return compileDefinition(input).map(Main::generateStatement).or(() -> {
			final var list =
					Arrays.stream(input.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).toList();

			final var name = structureNames.peek();
			for (var segment : list) {
				if (!compileEnumValue(segment, name)) {
					return Optional.empty();
				}
			}

			return Optional.of("");
		});
	}*//*private static boolean compileEnumValue(String segment, String enumName) {
		final var stripped = segment.strip();
		if (stripped.endsWith(")")) {
			final var substring = stripped.substring(0, stripped.length() - 1);

			final var i = substring.indexOf("(");
			if (i >= 0) {
				final var memberName = substring.substring(0, i);
				final var substring2 = substring.substring(i + 1);

				if (isIdentifier(memberName)) {
					globals.add(enumName + " " + enumName + "_" + memberName + " = new_" + enumName + "(" +
											compileExpression(substring2) + ");" + System.lineSeparator());
					return true;
				}
			}
		}

		return false;
	}*//*private static String compileExpression(String input) {
		return input.strip();
	}*//*private static String compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			return generateStatement(compileMethodSegmentValue(substring));
		}

		return CPlaceholder.wrap(stripped);
	}*//*private static String compileMethodSegmentValue(String input) {
		final var i = input.indexOf("=");
		if (i >= 0) {
			final var substring = input.substring(0, i);
			final var substring1 = input.substring(i + 1);
			return compileExpression(substring) + " = " + compileExpression(substring1);
		}

		return CPlaceholder.wrap(input);
	}*//*private static String compileDefinitionOrPlaceholder(String input) {
		return compileDefinition(input).orElseGet(() -> CPlaceholder.wrap(input));
	}*//*private static Optional<String> compileDefinition(String input) {
		return parseDefinition(input).map(JDefinition::toCDefinition).map(CDefinable::generate);
	}*//*private static Optional<JDefinition> parseDefinition(String input) {
		final var stripped = input.strip();
		final var i = stripped.lastIndexOf(" ");
		if (i < 0) {return Optional.empty();}
		final var beforeName = stripped.substring(0, i).strip();
		final var name = stripped.substring(i + 1).strip();

		if (!isIdentifier(name)) {
			return Optional.empty();
		}

		final var i1 = beforeName.lastIndexOf(" ");
		if (i1 >= 0) {
			final var beforeType = beforeName.substring(0, i1);
			final var type = beforeName.substring(i1 + 1);
			return Optional.of(new JDefinition(Optional.of(beforeType), compileType(type), name));
		} else {
			return Optional.of(new JDefinition(Optional.empty(), compileType(beforeName), name));
		}
	}*//*private static String compileTypeToString(String input) {
		return compileType(input).generate();
	}*//*private static CType compileType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return CPrimitiveType.Void;
		}

		if (stripped.endsWith("[]")) {
			final var cType = compileType(stripped.substring(0, stripped.length() - 2));
			return new CPointerType(cType);
		}

		if (stripped.equals("String")) {
			return new CPointerType(CPrimitiveType.Char);
		}

		if (stripped.endsWith(">")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i = substring.indexOf("<");
			if (i >= 0) {
				final var base = substring.substring(0, i);
				final var typeArgumentsArray = substring.substring(i + 1).split(Pattern.quote(","));

				final var typeArguments = Arrays
						.stream(typeArgumentsArray)
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(Main::compileType)
						.toList();

				return new CTemplateType(base, typeArguments);
			}
		}

		if (isIdentifier(stripped)) {
			return new CIdentifier(stripped);
		}

		return new CPlaceholder(stripped);
	}*//*}*/