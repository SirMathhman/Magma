JPrimitiveType JPrimitiveType_Char = new_JPrimitiveType(/*Undefined identifier: CPrimitiveType*/.Char);
JPrimitiveType JPrimitiveType_Void = new_JPrimitiveType(/*Undefined identifier: CPrimitiveType*/.Void);
JPrimitiveType JPrimitiveType_String = new_JPrimitiveType(/*new CPointerType(CPrimitiveType.Char)*/);
CPrimitiveType CPrimitiveType_Char = new_CPrimitiveType(/*"char"*/);
CPrimitiveType CPrimitiveType_Void = new_CPrimitiveType(/*"void"*/);
struct JPrimitiveType {
	CType cType;
};
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
struct Result {ResultTag _tagResultData<T, X> _data
};
enum CTypeTag {
	CIdentifierType,
	CPlaceholderType,
	CPointerTypeType,
	CPrimitiveTypeType,
	CStructureTypeType,
	CTemplateTypeType
};
union CTypeData {
	CIdentifier cidentifier;
	CPlaceholder cplaceholder;
	CPointerType cpointertype;
	CPrimitiveType cprimitivetype;
	CStructureType cstructuretype;
	CTemplateType ctemplatetype;
}
struct CType {CTypeTag _tagCTypeData _data
};
enum CDefinableTag {
	CDefinitionType,
	CPlaceholderType
};
union CDefinableData {
	CDefinition cdefinition;
	CPlaceholder cplaceholder;
}
struct CDefinable {CDefinableTag _tagCDefinableData _data
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
struct JMethodHeader {JMethodHeaderTag _tagJMethodHeaderData _data
};
struct CFunctionHeader {
};
struct JType {
};
enum JExpressionTag {
	JIdentifierType,
	JMemberAccessType,
	JPlaceholderType
};
union JExpressionData {
	JIdentifier jidentifier;
	JMemberAccess jmemberaccess;
	JPlaceholder jplaceholder;
}
struct JExpression {JExpressionTag _tagJExpressionData _data
};
enum CExpressionTag {
	CFieldAccessType,
	CIdentifierType,
	CPlaceholderType
};
union CExpressionData {
	CFieldAccess cfieldaccess;
	CIdentifier cidentifier;
	CPlaceholder cplaceholder;
}
struct CExpression {CExpressionTag _tagCExpressionData _data
};
template <typename T, typename X>
struct Err {X error
};
template <typename T, typename X>
struct Ok {T value
};
struct CPointerType {CType child
};
struct CTemplateType {char* baseList<CType> typeArguments
};
struct CIdentifier {char* input
};
struct CPlaceholder {char* input
};
struct CDefinition {CType typechar* name
};
struct JDefinition {Optional<char*> beforeTypeJType typechar* name
};
struct JConstructor {char* input
};
struct JPlaceholder {char* input
};
struct JArrayType {JType type
};
struct JGenericType {char* baseList<JType> typeArguments
};
struct JIdentifier {char* value
};
struct CFieldAccess {CExpression childchar* name
};
struct JMemberAccess {JExpression childchar* name
};
struct Frame {Optional<char*> maybeStructureNameList<JDefinition> definitions
};
struct CStructureType {char* nameList<CDefinition> fields
};
struct JClassType {char* nameList<JDefinition> definitions
};
struct Scope {List<Frame> frames
};
struct Main {
};
JType toJType_JPrimitiveType(void* _ref){
	JPrimitiveType _this = *((JPrimitiveType*) _ref);
	JTypeData data;
	data.err = this;
	return JType { JPrimitiveTypeType, data };
}
JPrimitiveType new_JPrimitiveType(CType cType) {
	JPrimitiveType this;
	this.cType = cType;
	return this;
}
CType toCType_JPrimitiveType(void* _ref) {
	JPrimitiveType _this = *((JPrimitiveType*) _ref);
	return this.cType;
}
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
char* generate_CPrimitiveType(void* _ref) {
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	return this.content;
}
char* generate_CType(void* _ref);
char* generate_CDefinable(void* _ref);
CDefinable toCDefinition_JMethodHeader(void* _ref);
CType toCType_JType(void* _ref);
CExpression toCExpression_JExpression(void* _ref);
char* generate_CExpression(void* _ref);
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
char* generate_CPointerType(void* _ref) {
	CPointerType _this = *((CPointerType*) _ref);
	return /*this.child.generate() + "*"*/;
}
CType toCType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CTypeData data;
	data.err = this;
	return CType { CTemplateTypeType, data };
}
char* generate_CTemplateType(void* _ref) {
	CTemplateType _this = *((CTemplateType*) _ref);
	CTemplateType cTemplateType = this;
	List<CType> typeArguments1 = cTemplateType.typeArguments;
	/*JPlaceholder[input=typeArguments1.stream()]*/ stream = /*typeArguments1.stream()*/;
	/*JPlaceholder[input=stream.map(CType::generate)]*/ stringStream = /*stream.map(CType::generate)*/;
	/*JPlaceholder[input=stringStream.collect(Collectors.joining(", "))]*/ joined = /*stringStream.collect(Collectors.joining(", "))*/;
	return /*this.base + "<" + joined + ">"*/;
}
/*CType, CExpression*/ to/*CType, CExpression*/_CIdentifier(void* _ref){
	CIdentifier _this = *((CIdentifier*) _ref);
	/*CType, CExpression*/Data data;
	data.err = this;
	return /*CType, CExpression*/ { CIdentifierType, data };
}
char* generate_CIdentifier(void* _ref) {
	CIdentifier _this = *((CIdentifier*) _ref);
	return this.input;
}
/*CType, CDefinable, CExpression*/ to/*CType, CDefinable, CExpression*/_CPlaceholder(void* _ref){
	CPlaceholder _this = *((CPlaceholder*) _ref);
	/*CType, CDefinable, CExpression*/Data data;
	data.err = this;
	return /*CType, CDefinable, CExpression*/ { CPlaceholderType, data };
}
char* wrap_CPlaceholder(void* _ref, char* input) {
	CPlaceholder _this = *((CPlaceholder*) _ref);
	/*JPlaceholder[input=input.replace("start", "start").replace("end", "end")]*/ replaced = /*input.replace("start", "start").replace("end", "end")*/;
	return /*"start" + replaced + "end"*/;
}
char* generate_CPlaceholder(void* _ref) {
	CPlaceholder _this = *((CPlaceholder*) _ref);
	return /*wrap(this.input)*/;
}
CDefinable toCDefinable_CDefinition(void* _ref){
	CDefinition _this = *((CDefinition*) _ref);
	CDefinableData data;
	data.err = this;
	return CDefinable { CDefinitionType, data };
}
char* generate_CDefinition(void* _ref) {
	CDefinition _this = *((CDefinition*) _ref);
	return /*this.type.generate() + " " + this*/.name;
}
JMethodHeader toJMethodHeader_JDefinition(void* _ref){
	JDefinition _this = *((JDefinition*) _ref);
	JMethodHeaderData data;
	data.err = this;
	return JMethodHeader { JDefinitionType, data };
}
CDefinable toCDefinition_JDefinition(void* _ref) {
	JDefinition _this = *((JDefinition*) _ref);
	return /*new CDefinition(this.type.toCType(), this.name)*/;
}
JDefinition mapType_JDefinition(void* _ref, /*JType>*/ mapper) {
	JDefinition _this = *((JDefinition*) _ref);
	return /*new JDefinition(this.beforeType, mapper.apply(this.type), this.name)*/;
}
JMethodHeader toJMethodHeader_JConstructor(void* _ref){
	JConstructor _this = *((JConstructor*) _ref);
	JMethodHeaderData data;
	data.err = this;
	return JMethodHeader { JConstructorType, data };
}
CDefinable toCDefinition_JConstructor(void* _ref) {
	JConstructor _this = *((JConstructor*) _ref);
	/*JPlaceholder[input=new CIdentifier(this.input)]*/ type = /*new CIdentifier(this.input)*/;
	return /*new CDefinition(type, "new_" + this.input)*/;
}
/*JMethodHeader, JType, JExpression*/ to/*JMethodHeader, JType, JExpression*/_JPlaceholder(void* _ref){
	JPlaceholder _this = *((JPlaceholder*) _ref);
	/*JMethodHeader, JType, JExpression*/Data data;
	data.err = this;
	return /*JMethodHeader, JType, JExpression*/ { JPlaceholderType, data };
}
CDefinable toCDefinition_JPlaceholder(void* _ref) {
	JPlaceholder _this = *((JPlaceholder*) _ref);
	return /*new CPlaceholder(this.input)*/;
}
CType toCType_JPlaceholder(void* _ref) {
	JPlaceholder _this = *((JPlaceholder*) _ref);
	return /*new CPlaceholder(this.input)*/;
}
CExpression toCExpression_JPlaceholder(void* _ref) {
	JPlaceholder _this = *((JPlaceholder*) _ref);
	return /*new CPlaceholder(this.input)*/;
}
JType toJType_JArrayType(void* _ref){
	JArrayType _this = *((JArrayType*) _ref);
	JTypeData data;
	data.err = this;
	return JType { JArrayTypeType, data };
}
CType toCType_JArrayType(void* _ref) {
	JArrayType _this = *((JArrayType*) _ref);
	return /*new CPointerType(this.type.toCType())*/;
}
JType toJType_JGenericType(void* _ref){
	JGenericType _this = *((JGenericType*) _ref);
	JTypeData data;
	data.err = this;
	return JType { JGenericTypeType, data };
}
CType toCType_JGenericType(void* _ref) {
	JGenericType _this = *((JGenericType*) _ref);
	return /*new CTemplateType(this.base, this.typeArguments.stream().map(JType::toCType).toList())*/;
}
/*JType, JExpression*/ to/*JType, JExpression*/_JIdentifier(void* _ref){
	JIdentifier _this = *((JIdentifier*) _ref);
	/*JType, JExpression*/Data data;
	data.err = this;
	return /*JType, JExpression*/ { JIdentifierType, data };
}
CType toCType_JIdentifier(void* _ref) {
	JIdentifier _this = *((JIdentifier*) _ref);
	return /*new CIdentifier(this.value)*/;
}
CExpression toCExpression_JIdentifier(void* _ref) {
	JIdentifier _this = *((JIdentifier*) _ref);
	return /*new CIdentifier(this.value)*/;
}
CExpression toCExpression_CFieldAccess(void* _ref){
	CFieldAccess _this = *((CFieldAccess*) _ref);
	CExpressionData data;
	data.err = this;
	return CExpression { CFieldAccessType, data };
}
char* generate_CFieldAccess(void* _ref) {
	CFieldAccess _this = *((CFieldAccess*) _ref);
	return /*this.child.generate() + "." + this*/.name;
}
JExpression toJExpression_JMemberAccess(void* _ref){
	JMemberAccess _this = *((JMemberAccess*) _ref);
	JExpressionData data;
	data.err = this;
	return JExpression { JMemberAccessType, data };
}
CExpression toCExpression_JMemberAccess(void* _ref) {
	JMemberAccess _this = *((JMemberAccess*) _ref);
	return /*new CFieldAccess(this.child.toCExpression(), this.name)*/;
}
public Frame_Frame(void* _ref) {
	Frame _this = *((Frame*) _ref);
	/*this(Optional.empty(), new ArrayList<JDefinition>())*/;
}
Optional<JClassType> toClassType_Frame(void* _ref) {
	Frame _this = *((Frame*) _ref);
	return /*this.maybeStructureName.map(structureName -> new JClassType(structureName, this.definitions))*/;
}
void defineAll_Frame(void* _ref, List<JDefinition> definitions) {
	Frame _this = *((Frame*) _ref);
	/*definitions.forEach(this::define)*/;
}
void define_Frame(void* _ref, JDefinition definition) {
	Frame _this = *((Frame*) _ref);
	/*assert !this.isVar(definition)*/;
	/*this.definitions.addLast(definition)*/;
}
boolean isVar_Frame(void* _ref, JDefinition definition) {
	Frame _this = *((Frame*) _ref);
	/*Not a structure type: JIdentifier[value=JDefinition]*/ type = definition.type;
	return /*type instanceof JIdentifier(var value) && value.equals("var")*/;
}
Frame withStructureName_Frame(void* _ref, char* structureName) {
	Frame _this = *((Frame*) _ref);
	return /*new Frame(Optional.of(structureName), this.definitions)*/;
}
CType toCType_CStructureType(void* _ref){
	CStructureType _this = *((CStructureType*) _ref);
	CTypeData data;
	data.err = this;
	return CType { CStructureTypeType, data };
}
char* generate_CStructureType(void* _ref) {
	CStructureType _this = *((CStructureType*) _ref);
	return this.name;
}
JType toJType_JClassType(void* _ref){
	JClassType _this = *((JClassType*) _ref);
	JTypeData data;
	data.err = this;
	return JType { JClassTypeType, data };
}
CType toCType_JClassType(void* _ref) {
	JClassType _this = *((JClassType*) _ref);
	return /*new CStructureType(this.name,
																this.definitions
																		.stream()
																		.map(definition -> new CDefinition(definition.type.toCType(), definition.name))
																		.toList())*/;
}
Optional<JType> resolve_JClassType(void* _ref, char* name) {
	JClassType _this = *((JClassType*) _ref);
	return /*this.definitions
					.stream()
					.filter(definition -> definition.name.equals(name))
					.map(definition -> definition.type)
					.findFirst()*/;
}
public Scope_Scope(void* _ref) {
	Scope _this = *((Scope*) _ref);
	/*this(new ArrayList<Frame>())*/;
	/*this.frames.addLast(new Frame())*/;
}
Optional<JType> resolveIdentifier_Scope(void* _ref, char* input) {
	Scope _this = *((Scope*) _ref);/*if (input.equals("this")) {
				return this.frames
						.reversed()
						.stream()
						.map(Frame::toClassType)
						.flatMap(Optional::stream)
						.findFirst()
						.map(value -> value);
			}*/
	return /*this.frames
					.reversed()
					.stream()
					.map(frame -> frame.definitions.stream().filter(definition -> definition.name.equals(input)).findFirst())
					.flatMap(Optional::stream)
					.map(JDefinition::type)
					.findFirst()*/;
}
Scope enter_Scope(void* _ref) {
	Scope _this = *((Scope*) _ref);
	/*this.frames.addLast(new Frame())*/;
	return this;
}
Scope defineAll_Scope(void* _ref, List<JDefinition> definitions) {
	Scope _this = *((Scope*) _ref);
	/*this.frames.getLast().defineAll(definitions)*/;
	return this;
}
Scope exit_Scope(void* _ref) {
	Scope _this = *((Scope*) _ref);
	/*this.frames.removeLast()*/;
	return this;
}
Scope define_Scope(void* _ref, JDefinition definition) {
	Scope _this = *((Scope*) _ref);
	/*this.frames.getLast().define(definition)*/;
	return this;
}
Scope withStructureName_Scope(void* _ref, char* name) {
	Scope _this = *((Scope*) _ref);
	/*this.frames.set(this.frames.size() - 1, this.frames.getLast().withStructureName(name))*/;
	return this;
}
char* getCurrentStructName_Scope(void* _ref) {
	Scope _this = *((Scope*) _ref);
	return /*this.frames
					.reversed()
					.stream()
					.map(frame -> frame.maybeStructureName)
					.flatMap(Optional::stream)
					.findFirst()
					.orElse("?")*/;
}
public static final List<String> functions = new ArrayList<String> new_public static final List<String> functions = new ArrayList<String>();
public static final List<String> structures = new ArrayList<String> new_public static final List<String> structures = new ArrayList<String>();
private static final List<String> globals = new ArrayList<String> new_private static final List<String> globals = new ArrayList<String>();
new Scope_Main(void* _ref);
void main_Main(void* _ref, char** args) {
	Main _this = *((Main*) _ref);
	/*run().ifPresent(Throwable::printStackTrace)*/;
}
Optional<IOException> run_Main(void* _ref) {
	Main _this = *((Main*) _ref);
	/*JPlaceholder[input=Paths.get(".", "src", "main", "java", "magma", "Main.java")]*/ source = /*Paths.get(".", "src", "main", "java", "magma", "Main.java")*/;/*return switch (readString(source)) {
			case Ok(var input) -> {
				final var target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");
				final var output = compile(input);
				yield writeString(target, output);
			}
			case Err<String, IOException> v -> Optional.of(v.error);
		}*/
	/**/;
}
Optional<IOException> writeString_Main(void* _ref, Path target, char* output) {
	Main _this = *((Main*) _ref);/*try {
			Files.writeString(target, output);
			return Optional.empty();
		}*//*catch (IOException e) {
			return Optional.of(e);
		}*/
}
/*IOException>*/ readString_Main(void* _ref, Path source) {
	Main _this = *((Main*) _ref);/*try {
			return new Ok<String, IOException>(Files.readString(source));
		}*//*catch (IOException e) {
			return new Err<String, IOException>(e);
		}*/
}
char* compile_Main(void* _ref, char* input) {
	Main _this = *((Main*) _ref);
	/*Undefined identifier: scope*/ = /*scope.enter()*/;
	/*JPlaceholder[input=compileStatements(input, Main::compileRootSegment)]*/ compiled = /*compileStatements(input, Main::compileRootSegment)*/;
	/*JPlaceholder[input=String.join("", globals)]*/ joinedGlobals = /*String.join("", globals)*/;
	/*JPlaceholder[input=String.join("", structures)]*/ joinedStructures = /*String.join("", structures)*/;
	/*JPlaceholder[input=String.join("", functions)]*/ joinedFunctions = /*String.join("", functions)*/;
	return /*joinedGlobals + joinedStructures + joinedFunctions + compiled*/;
}
char* compileStatements_Main(void* _ref, char* input, /*String>*/ mapper) {
	Main _this = *((Main*) _ref);
	/*JPlaceholder[input=new ArrayList<String>()]*/ segments = /*new ArrayList<String>()*/;
	/*JPlaceholder[input=new StringBuilder()]*/ buffer = /*new StringBuilder()*/;
	/*JPlaceholder[input=0]*/ depth = /*0*/;
	/*(var*/ i = /*0*/;
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
segments.add new_segments.add();
return segments.stream new_return segments.stream();
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

					List<JDefinition> recordFields = new ArrayList<JDefinition>();
					if (beforeContent.endsWith(")")) {
						final var substring2 = beforeContent.substring(0, beforeContent.length() - 1);
						final var i3 = substring2.indexOf("(");
						if (i3 >= 0) {
							final var substring4 = substring2.substring(i3 + 1);
							recordFields = Arrays
									.stream(substring4.split(Pattern.quote(",")))
									.map(String::strip)
									.filter(slice -> !slice.isEmpty())
									.map(Main::parseDefinition)
									.flatMap(Optional::stream)
									.toList();

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
						functions.add(generateFunction(thisType,
																					 superType,
																					 "to" + superType + "_" + beforeContent,
																					 "void* _ref",
																					 generateStatement(superType + "Data data") +
																					 generateStatement("data.err = this") + generateStatement(
																							 "return " + superType + " { " + beforeContent + "Type, data }")));
					}

					List<CDefinition> generatedFields = new ArrayList<CDefinition>();
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

						recordFields
								.stream()
								.map(JDefinition::toCDefinition)
								.map(CDefinable::generate)
								.map(Main::generateStatement)
								.collect(Collectors.joining());

						generatedFields = List.of(new CDefinition(new CIdentifier(tagType), "_tag"),
																			new CDefinition(new CIdentifier(unionType + typeArguments), "_data"));
					}

					scope = scope.enter().withStructureName(beforeContent).defineAll(recordFields);

					final var recordFieldsStream = recordFields.stream().map(JDefinition::toCDefinition);
					final var structureFields = Stream
							.concat(recordFieldsStream, generatedFields.stream())
							.map(CDefinable::generate)
							.collect(Collectors.joining());

					final var generated = beforeStruct + templateString + "struct " + beforeContent + " {" + structureFields +
																compileStatements(content, Main::compileClassSegment) + System.lineSeparator() + "};" +
																System.lineSeparator();
					structures.add(generated);
					scope = scope.exit();
					return Optional.of("");
				}
			}
		}

		return Optional.empty();
	}*//*private static String generateFunction(String thisType,
																				 String returnType,
																				 String name,
																				 String params,
																				 String content) {
		return returnType + " " + name + "(" + params + "){" + generateDereferenceThis(thisType) + content +
					 System.lineSeparator() + "}" + System.lineSeparator();
	}*//*private static String generateDereferenceThis(String thisType) {
		return generateStatement(thisType + " _this = *((" + thisType + "*) _ref)");
	}*//*private static String generateStatement(String content) {
		return System.lineSeparator() + "\t" + content + ";";
	}*//*private static boolean isIdentifier(String input) {
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (Character.isLetter(c) || (i != 0 && Character.isDigit(c))) {continue;}
			return false;
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
				final var paramString = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();
				final var header = parseDefinition(substring)
						.<JMethodHeader>map(definition -> definition)
						.or(() -> parseConstructor(substring))
						.orElseGet(() -> new JPlaceholder(substring));

				final var jParameters = Arrays
						.stream(paramString.split(Pattern.quote(",")))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(Main::parseDefinition)
						.flatMap(Optional::stream)
						.toList();

				final var cParameters =
						new ArrayList<CDefinable>(jParameters.stream().map(JDefinition::toCDefinition).toList());

				CDefinable outputDefinition;
				switch (header) {
					case JDefinition jDefinition:
						cParameters.addFirst(new CDefinition(new CPointerType(CPrimitiveType.Void), "_ref"));
						outputDefinition =
								new CDefinition(jDefinition.type.toCType(), jDefinition.name + "_" + scope.getCurrentStructName());
						break;
					default:
						outputDefinition = header.toCDefinition();
						break;
				}

				final var joinedParameters = cParameters.stream().map(CDefinable::generate).collect(Collectors.joining(", "));
				final var headerWithString = outputDefinition.generate() + "(" + joinedParameters + ")";

				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var content = withBraces.substring(1, withBraces.length() - 1);

					scope = scope.enter().defineAll(jParameters).enter();
					final var compiledContent = compileStatements(content, Main::compileMethodSegment);
					scope = scope.exit().exit();

					final String outputContent;
					if (header instanceof JConstructor(var name)) {
						outputContent = generateStatement(name + " this") + compiledContent + generateStatement("return this");
					} else if (header instanceof JDefinition) {
						outputContent = generateDereferenceThis(scope.getCurrentStructName()) + compiledContent;
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
	}*//*private static Optional<JMethodHeader> parseConstructor(String input) {
		final var stripped = input.strip();
		return Optional.of(new JConstructor(stripped));
	}*//*private static Optional<String> compileClassStatement(String input) {
		return compileDefinition(input).map(Main::generateStatement).or(() -> {
			final var list =
					Arrays.stream(input.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).toList();

			final var name = scope.getCurrentStructName();
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
		return parseExpression(input).toCExpression().generate();
	}*//*private static JExpression parseExpression(String input) {
		final var stripped = input.strip();
		if (isIdentifier(stripped)) {
			if (isDefined(stripped)) {
				return new JIdentifier(stripped);
			} else {
				return new JPlaceholder("Undefined identifier: " + stripped);
			}
		}

		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var substring = stripped.substring(0, i).strip();
			final var name = stripped.substring(i + 1).strip();
			if (isIdentifier(name)) {
				final var child = parseExpression(substring);
				return new JMemberAccess(child, name);
			}
		}

		return new JPlaceholder(stripped);
	}*//*private static boolean isDefined(String input) {
		if (input.equals("this")) {
			return true;
		}

		return scope.resolveIdentifier(input).isPresent();
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
		if (input.startsWith("return ")) {
			final var slice = input.substring("return ".length());
			return "return " + compileExpression(slice);
		}

		final var i = input.indexOf("=");
		if (i >= 0) {
			final var destination = input.substring(0, i);
			final var substring1 = input.substring(i + 1);
			final var source = parseExpression(substring1);
			final var sourceString = source.toCExpression().generate();

			return parseDefinition(destination).map(definition -> {
				final var jDefinition = withResolvedType(definition, source);
				scope = scope.define(jDefinition);
				return jDefinition.toCDefinition().generate() + " = " + sourceString;
			}).orElseGet(() -> compileExpression(destination) + " = " + sourceString);
		}

		return CPlaceholder.wrap(input);
	}*//*private static JDefinition withResolvedType(JDefinition definition, JExpression source) {
		return definition.mapType(type -> {
			if (type instanceof JIdentifier(var value) && value.equals("var")) {
				return resolveExpression(source);
			}

			return type;
		});
	}*//*private static JType resolveExpression(JExpression type) {
		if (type instanceof JIdentifier(var input)) {
			return scope.resolveIdentifier(input).orElseGet(() -> new JPlaceholder("Unresolved identifier: " + input));
		}

		if (type instanceof JMemberAccess(var child, var name)) {
			final var resolved = resolveExpression(child);
			if (resolved instanceof JClassType type0) {
				return type0.resolve(name).orElseGet(() -> new JPlaceholder("Property not present: " + name));
			}
			return new JPlaceholder("Not a structure type: " + resolved);
		}

		return new JPlaceholder(type.toString());
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
			return Optional.of(new JDefinition(Optional.of(beforeType), parseType(type), name));
		} else {
			return Optional.of(new JDefinition(Optional.empty(), parseType(beforeName), name));
		}
	}*//*private static String compileTypeToString(String input) {
		return parseType(input).toCType().generate();
	}*//*private static JType parseType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return JPrimitiveType.Void;
		}

		if (stripped.endsWith("[]")) {
			final var cType = parseType(stripped.substring(0, stripped.length() - 2));
			return new JArrayType(cType);
		}

		if (stripped.equals("String")) {
			return JPrimitiveType.String;
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
						.map(Main::parseType)
						.toList();

				return new JGenericType(base, typeArguments);
			}
		}

		if (isIdentifier(stripped)) {
			return new JIdentifier(stripped);
		}

		return new JPlaceholder(stripped);
	}*//*}*/