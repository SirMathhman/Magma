struct PrimitiveType {
};
template <typename T0, typename R>
struct F1RTable<T0, R> {
	R (*apply)(void*, T0);
};
template <typename T0, typename R>
struct F1R {
	F1RTable<T0, R> table;
	void* data;
};
enum ResultVariant {
	ErrVariant,
	OkVariant
};
template <typename T, typename X>
union ResultData {
	ErrData<T, X> Err;
	OkData<T, X> Ok;
};
template <typename T, typename X>
struct Result {
	ResultVariant variant;
	ResultData data;
};
enum TypeVariant {
	IdentifierVariant,
	PlaceholderVariant,
	PointerTypeVariant,
	PrimitiveTypeVariant,
	TemplateTypeVariant
};
union TypeData {
	IdentifierData Identifier;
	PlaceholderData Placeholder;
	PointerTypeData PointerType;
	PrimitiveTypeData PrimitiveType;
	TemplateTypeData TemplateType;
};
struct Type {
	TypeVariant variant;
	TypeData data;
};
enum MethodDeclarationVariant {
	ConstructorVariant,
	DeclarationVariant,
	PlaceholderVariant
};
union MethodDeclarationData {
	ConstructorData Constructor;
	DeclarationData Declaration;
	PlaceholderData Placeholder;
};
struct MethodDeclaration {
	MethodDeclarationVariant variant;
	MethodDeclarationData data;
};
enum StructMemberVariant {
	EmptyStructMemberVariant,
	FunctionDeclarationVariant,
	PlaceholderVariant
};
union StructMemberData {
	EmptyStructMemberData EmptyStructMember;
	FunctionDeclarationData FunctionDeclaration;
	PlaceholderData Placeholder;
};
struct StructMember {
	StructMemberVariant variant;
	StructMemberData data;
};
struct FolderTable {
	State (*apply)(void*, State, Character);
};
struct Folder {
	FolderTable table;
	void* data;
};
template <typename T, typename X>
struct Err {
	X error;
};
template <typename T, typename X>
struct Ok {
	T value;
};
template <typename A, typename B>
struct Tuple {
	A left;
	B right;
};
struct State {
};
struct PointerType {
	Type type;
};
struct TemplateType {
	char* base;
	List<Type> list;
};
struct Identifier {
	char* value;
};
struct Placeholder {
	char* input;
};
struct Constructor {
	char* structName;
};
struct Declaration {
	List<char*> typeParameters;
	Optional<char*> maybeBeforeType;
	char* type;
	char* name;
};
struct FunctionDeclaration {
	char* type;
	char* name;
	List<char*> parameterTypes;
};
struct EmptyStructMember {
};
struct EscapedFolder {
	Folder folder;
};
struct ValueFolder {
};
struct Main {
};
PrimitiveType PrimitiveTypeVoid = new_PrimitiveType("void");
PrimitiveType PrimitiveTypeChar = new_PrimitiveType("char");
Type toType_PrimitiveType(void* _this){
	PrimitiveType this = *((PrimitiveType*) _this);
	TypeData data;
	data.PrimitiveType = this;
	return { TypeVariant.PrimitiveTypeVariant, data };
}
PrimitiveType new_PrimitiveType(char* content){
	PrimitiveType this;
	this->content = content;
	return this;
}
char* generate_PrimitiveType(void* _this){
	PrimitiveType* this = (PrimitiveType*) _this;
	return this->content;
}
char* toBaseName_PrimitiveType(void* _this){
	PrimitiveType* this = (PrimitiveType*) _this;
	return this->content;
}
R apply_F1R(void* _this, T0 value){
	F1R<T0, R>* this = (F1R<T0, R>*) _this;
	R _ret;
	switch (this.variant) {
	}
	return _ret;
}
template <typename T, typename X, typename R>
Result<R, X> mapValue_Result(void* _this, F1R<T, R> mapper){
	Result<T, X>* this = (Result<T, X>*) _this;
	Result<R, X> _ret;
	switch (this.variant) {
		case ResultVariant.ErrVariant:
			_ret = mapValue_Err(&this.data.Err);
			break;
		case ResultVariant.OkVariant:
			_ret = mapValue_Ok(&this.data.Ok);
			break;
	}
	return _ret;
}
char* generate_Type(void* _this){
	Type* this = (Type*) _this;
	char* _ret;
	switch (this.variant) {
		case TypeVariant.IdentifierVariant:
			_ret = generate_Identifier(&this.data.Identifier);
			break;
		case TypeVariant.PlaceholderVariant:
			_ret = generate_Placeholder(&this.data.Placeholder);
			break;
		case TypeVariant.PointerTypeVariant:
			_ret = generate_PointerType(&this.data.PointerType);
			break;
		case TypeVariant.PrimitiveTypeVariant:
			_ret = generate_PrimitiveType(&this.data.PrimitiveType);
			break;
		case TypeVariant.TemplateTypeVariant:
			_ret = generate_TemplateType(&this.data.TemplateType);
			break;
	}
	return _ret;
}
char* toBaseName_Type(void* _this){
	Type* this = (Type*) _this;
	char* _ret;
	switch (this.variant) {
		case TypeVariant.IdentifierVariant:
			_ret = toBaseName_Identifier(&this.data.Identifier);
			break;
		case TypeVariant.PlaceholderVariant:
			_ret = toBaseName_Placeholder(&this.data.Placeholder);
			break;
		case TypeVariant.PointerTypeVariant:
			_ret = toBaseName_PointerType(&this.data.PointerType);
			break;
		case TypeVariant.PrimitiveTypeVariant:
			_ret = toBaseName_PrimitiveType(&this.data.PrimitiveType);
			break;
		case TypeVariant.TemplateTypeVariant:
			_ret = toBaseName_TemplateType(&this.data.TemplateType);
			break;
	}
	return _ret;
}
char* generate_MethodDeclaration(void* _this){
	MethodDeclaration* this = (MethodDeclaration*) _this;
	char* _ret;
	switch (this.variant) {
		case MethodDeclarationVariant.ConstructorVariant:
			_ret = generate_Constructor(&this.data.Constructor);
			break;
		case MethodDeclarationVariant.DeclarationVariant:
			_ret = generate_Declaration(&this.data.Declaration);
			break;
		case MethodDeclarationVariant.PlaceholderVariant:
			_ret = generate_Placeholder(&this.data.Placeholder);
			break;
	}
	return _ret;
}
char* generate_StructMember(void* _this){
	StructMember* this = (StructMember*) _this;
	char* _ret;
	switch (this.variant) {
		case StructMemberVariant.EmptyStructMemberVariant:
			_ret = generate_EmptyStructMember(&this.data.EmptyStructMember);
			break;
		case StructMemberVariant.FunctionDeclarationVariant:
			_ret = generate_FunctionDeclaration(&this.data.FunctionDeclaration);
			break;
		case StructMemberVariant.PlaceholderVariant:
			_ret = generate_Placeholder(&this.data.Placeholder);
			break;
	}
	return _ret;
}
State apply_Folder(void* _this, State state, Character character){
	Folder* this = (Folder*) _this;
	State _ret;
	switch (this.variant) {
	}
	return _ret;
}
template <typename T, typename X>
Result<T, X> toResult_Err(void* _this){
	Err<T, X> this = *((Err<T, X>*) _this);
	ResultData<T, X> data;
	data.Err = this;
	return { ResultVariant.ErrVariant, data };
}
template <typename T, typename X, typename R>
Result<R, X> mapValue_Err(void* _this, F1R<T, R> mapper){
	Err<T, X>* this = (Err<T, X>*) _this;
	return new_Err<R, X>(this->error);
}
template <typename T, typename X>
Result<T, X> toResult_Ok(void* _this){
	Ok<T, X> this = *((Ok<T, X>*) _this);
	ResultData<T, X> data;
	data.Ok = this;
	return { ResultVariant.OkVariant, data };
}
template <typename T, typename X, typename R>
Result<R, X> mapValue_Ok(void* _this, F1R<T, R> mapper){
	Ok<T, X>* this = (Ok<T, X>*) _this;
	return new_Ok<R, X>(mapper.apply(this->value));
}
public State_State(void* _this, char* input){
	State* this = (State*) _this;
	this->input = input;
	this->index = 0;
	this->buffer = new_StringBuilder();
	this->depth = 0;
	this->segments = new_ArrayList<char*>();
}
boolean isShallow_State(void* _this){
	State* this = (State*) _this;
	return this->depth == 1;
}
boolean isLevel_State(void* _this){
	State* this = (State*) _this;
	return this->depth == 0;
}
State append_State(void* _this, Character next){
	State* this = (State*) _this;
	this->buffer.append(next);
	return this;
}
Optional<Character> pop_State(void* _this){
	State* this = (State*) _this;
	if (this->index < this->input.length()) {
		var value = this->input.charAt(this->index);
		this->index++;
		return Optional.of(value);
	}
	else {
		return Optional.empty();
	}
}
State advance_State(void* _this){
	State* this = (State*) _this;
	this->segments.add(this->buffer.toString());
	this->buffer.setLength(0);
	return this;
}
State enter_State(void* _this){
	State* this = (State*) _this;
	this->depth = this->depth + 1;
	return this;
}
State exit_State(void* _this){
	State* this = (State*) _this;
	this->depth = this->depth - 1;
	return this;
}
Stream<char*> stream_State(void* _this){
	State* this = (State*) _this;
	return this->segments.stream();
}
Optional<Tuple<State, Character>> popAndAppendToTuple_State(void* _this){
	State* this = (State*) _this;
	return this->pop().map(/*popped -> {
				final var appended = this.append(popped);
				return new Tuple<State, Character>(appended, popped);
			}*/);
}
Optional<State> popAndAppendToOption_State(void* _this){
	State* this = (State*) _this;
	return this->popAndAppendToTuple().map(/*tuple -> tuple.left*/);
}
Optional<Character> peek_State(void* _this){
	State* this = (State*) _this;
	if (this->index < this->input.length()) {
		return Optional.of(this->input.charAt(this->index));
	}
	return Optional.empty();
}
Type toType_PointerType(void* _this){
	PointerType this = *((PointerType*) _this);
	TypeData data;
	data.PointerType = this;
	return { TypeVariant.PointerTypeVariant, data };
}
char* generate_PointerType(void* _this){
	PointerType* this = (PointerType*) _this;
	return this->type.generate() + "*";
}
char* toBaseName_PointerType(void* _this){
	PointerType* this = (PointerType*) _this;
	return this->type.toBaseName() + "_ptr";
}
Type toType_TemplateType(void* _this){
	TemplateType this = *((TemplateType*) _this);
	TypeData data;
	data.TemplateType = this;
	return { TypeVariant.TemplateTypeVariant, data };
}
char* generate_TemplateType(void* _this){
	TemplateType* this = (TemplateType*) _this;
	var typeArguments = this->list.stream().map(/*Type::generate*/).collect(Collectors.joining(", "));
	return this->base + " < " + typeArguments + ">";
}
char* toBaseName_TemplateType(void* _this){
	TemplateType* this = (TemplateType*) _this;
	return this->base;
}
Type toType_Identifier(void* _this){
	Identifier this = *((Identifier*) _this);
	TypeData data;
	data.Identifier = this;
	return { TypeVariant.IdentifierVariant, data };
}
char* generate_Identifier(void* _this){
	Identifier* this = (Identifier*) _this;
	return this->value;
}
char* toBaseName_Identifier(void* _this){
	Identifier* this = (Identifier*) _this;
	return this->value;
}
Type toType_Placeholder(void* _this){
	Placeholder this = *((Placeholder*) _this);
	TypeData data;
	data.Placeholder = this;
	return { TypeVariant.PlaceholderVariant, data };
}
MethodDeclaration toMethodDeclaration_Placeholder(void* _this){
	Placeholder this = *((Placeholder*) _this);
	MethodDeclarationData data;
	data.Placeholder = this;
	return { MethodDeclarationVariant.PlaceholderVariant, data };
}
StructMember toStructMember_Placeholder(void* _this){
	Placeholder this = *((Placeholder*) _this);
	StructMemberData data;
	data.Placeholder = this;
	return { StructMemberVariant.PlaceholderVariant, data };
}
char* generate_Placeholder(void* _this){
	Placeholder* this = (Placeholder*) _this;
	return wrap(this->input);
}
char* toBaseName_Placeholder(void* _this){
	Placeholder* this = (Placeholder*) _this;
	return wrap(this->input);
}
MethodDeclaration toMethodDeclaration_Constructor(void* _this){
	Constructor this = *((Constructor*) _this);
	MethodDeclarationData data;
	data.Constructor = this;
	return { MethodDeclarationVariant.ConstructorVariant, data };
}
char* generate_Constructor(void* _this){
	Constructor* this = (Constructor*) _this;
	return this->structName + " new_" + this->structName;
}
MethodDeclaration toMethodDeclaration_Declaration(void* _this){
	Declaration this = *((Declaration*) _this);
	MethodDeclarationData data;
	data.Declaration = this;
	return { MethodDeclarationVariant.DeclarationVariant, data };
}
public Declaration_Declaration(void* _this, char* type, char* name){
	Declaration* this = (Declaration*) _this;
	this(Collections.emptyList(), Optional.empty(), type, name);
}
char* generate_Declaration(void* _this){
	Declaration* this = (Declaration*) _this;
	var beforeDeclaration = generateTemplateString(this->typeParameters());
	return beforeDeclaration + this->type + " " + this->name;
}
Declaration mapName_Declaration(void* _this, F1R<char*, char*> mapper){
	Declaration* this = (Declaration*) _this;
	return new_Declaration(this->typeParameters, this->maybeBeforeType, this->type, mapper.apply(this->name));
}
StructMember toStructMember_FunctionDeclaration(void* _this){
	FunctionDeclaration this = *((FunctionDeclaration*) _this);
	StructMemberData data;
	data.FunctionDeclaration = this;
	return { StructMemberVariant.FunctionDeclarationVariant, data };
}
char* generate_FunctionDeclaration(void* _this){
	FunctionDeclaration* this = (FunctionDeclaration*) _this;
	var joinedParameterTypes = this->parameterTypes.stream().collect(Collectors.joining(", ", "(", ")"));
	return this->type + " (*" + this->name + ")" + joinedParameterTypes;
}
StructMember toStructMember_EmptyStructMember(void* _this){
	EmptyStructMember this = *((EmptyStructMember*) _this);
	StructMemberData data;
	data.EmptyStructMember = this;
	return { StructMemberVariant.EmptyStructMemberVariant, data };
}
char* generate_EmptyStructMember(void* _this){
	EmptyStructMember* this = (EmptyStructMember*) _this;
	return "";
}
Folder toFolder_EscapedFolder(void* _this){
	EscapedFolder this = *((EscapedFolder*) _this);
	FolderData data;
	data.EscapedFolder = this;
	return { FolderVariant.EscapedFolderVariant, data };
}
State apply_EscapedFolder(void* _this, State state, Character next){
	EscapedFolder* this = (EscapedFolder*) _this;
	if (next == '\'') {
		var appended = state.append(next);
		return appended.popAndAppendToTuple().map(/*tuple -> {
					if (tuple.right == '\\') {
						return tuple.left.popAndAppendToOption().orElse(tuple.left);
					}
					return tuple.left;
				}*/).flatMap(/*State::popAndAppendToOption*/).orElse(appended);
	}
	if (next == '\"') {
		var current = state.append(next);
	/*while (true) {
					final var maybeTuple = current.popAndAppendToTuple();
					if (maybeTuple.isEmpty()) {
						break;
					}

					final var tuple = maybeTuple.get();
					current = tuple.left;

					final var right = tuple.right;
					if (right == '\\') {
						current = current.popAndAppendToOption().orElse(current);
					}

					if (right == '\"') {
						break;
					}
				}*/
		return current;
	}
	return this->folder.apply(state, next);
}
Folder toFolder_ValueFolder(void* _this){
	ValueFolder this = *((ValueFolder*) _this);
	FolderData data;
	data.ValueFolder = this;
	return { FolderVariant.ValueFolderVariant, data };
}
State apply_ValueFolder(void* _this, State state, Character next){
	ValueFolder* this = (ValueFolder*) _this;
	if (next == ',' && state.isLevel()) {
		return state.advance();
	}
	var appended = state.append(next);
	if (next == '-') {
		var peeked = appended.peek();
		if (peeked.isPresent() && peeked.get() == '>') {
			return appended.popAndAppendToOption().orElse(appended);
	}
		else {
			return appended;
		}
	}
	/*if (next == '<' || next == '(') {
				return appended.enter();
			}*/
	/*if (next == '>' || next == ')') {
				return appended.exit();
			}*/
	return appended;
}
public Main_Main(void* _this){
	Main* this = (Main*) _this;
	this->structures = new_ArrayList<char*>();
	this->functions = new_ArrayList<char*>();
	this->globals = new_ArrayList<char*>();
}
char* generateTemplateString_Main(void* _this, List<char*> typeParameters){
	Main* this = (Main*) _this;
	/*final String templateString*/;
	if (typeParameters.isEmpty()) {
		templateString = "";
	}
	else {
		templateString = /* "template " + typeParameters
					.stream()
					.map(typeParam -> "typename " + typeParam)
					.collect(Collectors.joining(", ", "<", ">")) + System.lineSeparator()*/;
	}
	return templateString;
}
char* wrap_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var replaced = input.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
void main_Main(void* _this, char** args){
	Main* this = (Main*) _this;
	new_/*Main().run().ifPresent*/(/*Throwable::printStackTrace*/);
}
Optional<IOException> run_Main(void* _this){
	Main* this = (Main*) _this;
	var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
	var target = source.resolveSibling("Main.cpp");
	var input = this->readString(source).mapValue(/*this::compile*/);
	/*return switch (input) {
			case Err<String, IOException> v -> Optional.of(v.error);
			case Ok<String, IOException> v -> this.writeString(target, v.value);
		}*/
	/**/;
}
Optional<IOException> writeString_Main(void* _this, Path target, char* output){
	Main* this = (Main*) _this;
	/*try {
			Files.writeString(target, output);
			return Optional.empty();
		}*/
	/*catch (IOException e) {
			return Optional.of(e);
		}*/
}
Result<char*, IOException> readString_Main(void* _this, Path source){
	Main* this = (Main*) _this;
	/*try {
			return new Ok<String, IOException>(Files.readString(source));
		}*/
	/*catch (IOException e) {
			return new Err<String, IOException>(e);
		}*/
}
char* compile_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var all = this->compileStatements(input, /* this::compileRootSegment*/);
	var joinedStructures = String.join("", this->structures);
	var joinedGlobals = String.join("", this->globals);
	var joinedFunctions = String.join("", this->functions);
	return joinedStructures + joinedGlobals + joinedFunctions + all;
}
char* compileStatements_Main(void* _this, char* input, F1R<char*, char*> mapper){
	Main* this = (Main*) _this;
	return this->compileAll(input, mapper, new_EscapedFolder(/*this::foldStatement*/));
}
char* compileAll_Main(void* _this, char* input, F1R<char*, char*> mapper, Folder folder){
	Main* this = (Main*) _this;
	return this->divide(input, folder).map(/*mapper::apply*/).collect(Collectors.joining(""));
}
Stream<char*> divide_Main(void* _this, char* input, Folder folder){
	Main* this = (Main*) _this;
	var current = new_State(input);
	/*while (true) {
			final var maybeNext = current.pop();
			if (maybeNext.isEmpty()) {
				break;
			}

			final var next = maybeNext.get();
			current = folder.apply(current, next);
		}*/
	return current.advance().stream();
}
State foldStatement_Main(void* _this, State current, Character next){
	Main* this = (Main*) _this;
	var appended = current.append(next);
	if (next == ';' && appended.isLevel()) {
		return appended.advance();
	}
	if (next == '}' && appended.isShallow()) {
		return appended.advance().exit();
	}
	/*if (next == '{' || next == '(') {
			return appended.enter();
		}*/
	/*if (next == '}' || next == ')') {
			return appended.exit();
		}*/
	return appended;
}
char* compileRootSegment_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (/*stripped.startsWith("package ") || stripped.startsWith("import ")*/) {
		return "";
	}
	return this->compileStructure("class", stripped).map(/*StructMember::generate*/).orElseGet(/*() -> wrap(stripped)*/);
}
Optional<StructMember> compileStructure_Main(void* _this, char* type, char* stripped){
	Main* this = (Main*) _this;
	var i = stripped.indexOf(type + " ");
	if (i < 0) {
		return Optional.empty();
	}
	var modifiers = stripped.substring(0, i).strip();
	var afterKeyword = stripped.substring(i + (type + " ").length()).strip();
	var i1 = afterKeyword.indexOf("{");
	if (i1 < 0) {
		return Optional.empty();
	}
	var beforeContent = afterKeyword.substring(0, i1).strip();
	var withEnd = afterKeyword.substring(i1 + 1).strip();
	if (/*!withEnd.endsWith("}")*/) {
		return Optional.empty();
	}
	var inputContent = withEnd.substring(0, withEnd.length() - 1);
	List<char*> variants = new_ArrayList<char*>();
	var i2 = beforeContent.indexOf("permits ");
	if (/*i2 >= 0*/) {
		var substring1 = beforeContent.substring(i2 + "permits ".length());
		beforeContent = beforeContent.substring(0, i2);
		variants = this->splitValues(substring1);
	}
	List<Type> implementees = new_ArrayList<Type>();
	var i4 = beforeContent.indexOf("implements ");
	if (/*i4 >= 0*/) {
		var implementeesString = beforeContent.substring(i4 + "implements ".length());
		beforeContent = beforeContent.substring(0, i4).strip();
		implementees = this->divide(implementeesString, /* (state, character) -> new ValueFolder().apply(state, character)*/).map(/*String::strip*/).filter(/*slice -> !slice.isEmpty()*/).map(/*this::parseType*/).toList();
	}
	List<Declaration> recordFields = Collections.emptyList();
	/*if (beforeContent.endsWith(")")) {
			final var substring = beforeContent.substring(0, beforeContent.length() - 1);
			final var i3 = substring.indexOf("(");
			if (i3 >= 0) {
				beforeContent = substring.substring(0, i3);
				recordFields = this
						.divide(substring.substring(i3 + 1), (state, character) -> new ValueFolder().apply(state, character))
						.map(slice -> this.parseDeclaration(slice, Collections.emptyList()))
						.flatMap(Optional::stream)
						.toList();
			}
		}*/
	List<char*> typeParameters = new_ArrayList<char*>();
	var i3 = beforeContent.indexOf(" < ");
	if (/*i3 >= 0*/) {
		var substring1 = beforeContent.substring(i3 + 1).strip();
		if (substring1.endsWith(">")) {
			beforeContent = beforeContent.substring(0, i3);
			var substring = substring1.substring(0, substring1.length() - 1);
			typeParameters = this->splitValues(substring);
	}
	}
	if (/*!this.isIdentifier(beforeContent)*/) {
		return Optional.empty();
	}
	var modifiersList = Arrays.stream(modifiers.split(Pattern.quote(" "))).map(/*String::strip*/).filter(/*slice -> !slice.isEmpty()*/).collect(Collectors.toCollection(/*ArrayList::new*/));
	var name = beforeContent.strip();
	var templateString = generateTemplateString(typeParameters);
	var joinedTypeParameters = this->joinTypeParameters(typeParameters);
	var fields = "";
	var dependencies = new_StringBuilder();
	/*for (var implementee : implementees) {
			final var identifier = implementee.toBaseName();

			final var variant = identifier + "Variant" + "." + name + "Variant";
			final var thisType = name + joinedTypeParameters;
			final var conversionFunctionContent = this.generateStatement(thisType + " this = *((" + thisType + "*) _this)") +
																						this.generateStatement(
																								identifier + "Data" + joinedTypeParameters + " data") +
																						this.generateStatement("data." + name + " = this") +
																						this.generateStatement("return { " + variant + ", data }");

			final var conversionFunction =
					templateString + implementee.generate() + " to" + identifier + "_" + name + "(void* _this){" +
					conversionFunctionContent + System.lineSeparator() + "}" + System.lineSeparator();

			this.functions.add(conversionFunction);
		}*/
	var joinedRecordFields = recordFields.stream().map(/*Declaration::generate*/).map(/*this::generateStatement*/).collect(Collectors.joining());
	var finalTypeParameters = typeParameters;
	var finalVariants = variants;
	var members = this->divide(inputContent, new_EscapedFolder(/*this::foldStatement*/)).map(/*slice -> this.compileClassSegment(slice, name, finalTypeParameters, finalVariants)*/).flatMap(/*Optional::stream*/).toList();
	if (modifiersList.contains("sealed")) {
		modifiersList.remove("sealed");
		var enumFields = variants.stream().map(/*variant -> System.lineSeparator() + "\t" + variant + "Variant"*/).collect(Collectors.joining(","));
		var generatedEnum = "enum " + name + "Variant {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();
		var unionFields = variants.stream().map(/*variant -> System.lineSeparator() + "\t" + variant + "Data" + joinedTypeParameters + " " + variant +
													";"*/).collect(Collectors.joining());
		var generatedUnion = templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" + System.lineSeparator();
		fields +  = System.lineSeparator() + "\t" + name + "Variant variant;" + System.lineSeparator() + "\t" + name + "Data data;";
		dependencies.append(generatedEnum).append(generatedUnion);
	}
	/*else if (type.equals("interface")) {
			final var table = this.generateStatement(name + "Table" + joinedTypeParameters + " table");
			final var data = this.generateStatement("void* data");

			final var tableMembers =
					members.stream().map(StructMember::generate).map(this::generateStatement).collect(Collectors.joining(""));
			final var vTable = templateString + "struct " + name + "Table" + joinedTypeParameters + " {" + tableMembers +
												 System.lineSeparator() + "};" + System.lineSeparator();

			dependencies.append(vTable);
			fields += table + data;
		}*/
	else {
		var joinedMembers = members.stream().filter(/*member -> !(member instanceof FunctionDeclaration)*/).map(/*StructMember::generate*/).collect(Collectors.joining());
		fields +  = joinedMembers;
	}
	var generated = dependencies + templateString + "struct " + name + " {" + joinedRecordFields + fields + System.lineSeparator() + "};" + System.lineSeparator();
	this->structures.add(generated);
	return Optional.of(new_EmptyStructMember());
}
char* joinTypeParameters_Main(void* _this, List<char*> typeParameters){
	Main* this = (Main*) _this;
	/*final String joinedTypeParameters*/;
	if (typeParameters.isEmpty()) {
		joinedTypeParameters = "";
	}
	else {
		joinedTypeParameters = typeParameters.stream().collect(Collectors.joining(", ", " < ", ">"));
	}
	return joinedTypeParameters;
}
char* generateStatement_Main(void* _this, char* content){
	Main* this = (Main*) _this;
	return this->generateStatement(1, content);
}
char* generateStatement_Main(void* _this, int depth, char* content){
	Main* this = (Main*) _this;
	return this->generateIndent(depth) + content + ";";
}
char* generateIndent_Main(void* _this, int depth){
	Main* this = (Main*) _this;
	return System.lineSeparator() + "\t".repeat(depth);
}
List<char*> splitValues_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	return Arrays.stream(input.split(Pattern.quote(","))).map(/*String::strip*/).filter(/*slice -> !slice.isEmpty()*/).toList();
}
boolean isIdentifier_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	/*for (var i = 0; i < stripped.length(); i++) {
			final var c = stripped.charAt(i);
			if (Character.isLetter(c) || (i != 0 && Character.isDigit(c))) {continue;}
			return false;
		}*/
	return true;
}
Optional<StructMember> compileClassSegment_Main(void* _this, char* input, char* structName, List<char*> typeParameters, List<char*> variants){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (stripped.isEmpty()) {
		return Optional.empty();
	}
	var maybeEnum = this->compileStructure("enum", input);
	if (maybeEnum.isPresent()) {
		return maybeEnum;
	}
	var maybeInterface = this->compileStructure("interface", input);
	if (maybeInterface.isPresent()) {
		return maybeInterface;
	}
	var maybeRecord = this->compileStructure("record", input);
	if (maybeRecord.isPresent()) {
		return maybeRecord;
	}
	var maybeClass = this->compileStructure("class", input);
	if (maybeClass.isPresent()) {
		return maybeClass;
	}
	var maybeEnumValues = this->compileEnumValues(input, structName);
	if (maybeEnumValues.isPresent()) {
		return maybeEnumValues;
	}
	var i = stripped.indexOf("(");
	if (/*i >= 0*/) {
		var declarationString = stripped.substring(0, i);
		var substring1 = stripped.substring(i + 1);
		var i1 = substring1.indexOf(")");
		if (/*i1 >= 0*/) {
			var parametersString = substring1.substring(0, i1);
			var withBraces = substring1.substring(i1 + 1).strip();
			var parameters = this->divide(parametersString, /* (state, character) -> new ValueFolder().apply(state, character)*/).map(/*String::strip*/).filter(/*slice -> !slice.isEmpty()*/).toList().stream().map(/*param -> this.parseDeclaration(param, typeParameters)*/).flatMap(/*Optional::stream*/).collect(Collectors.toCollection(/*ArrayList::new*/));
			var methodDeclaration = this->parseMethodDeclaration(declarationString, structName, typeParameters);
			Optional<char*> maybeCompiled = Optional.empty();
			if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
				var inputContent = withBraces.substring(1, withBraces.length() - 1);
				maybeCompiled = Optional.of(this->compileMethodsSegments(inputContent, 1));
	}
			/*String outputContent*/;
			if (/*methodDeclaration instanceof Constructor*/) {
				var compiled = maybeCompiled.orElse("?");
				outputContent = /*
							this.generateStatement(structName + " this") + compiled + this.generateStatement("return this")*/;
	}
	/*else if (methodDeclaration instanceof Declaration declaration) {
					parameters.addFirst(new Declaration("void*", "_this"));

					final var joinedTypeParameters = this.joinTypeParameters(typeParameters);

					final var thisInitialization = this.generateStatement(
							structName + joinedTypeParameters + "* this = (" + structName + joinedTypeParameters + "*) _this");

					outputContent = thisInitialization + maybeCompiled.orElseGet(() -> {
						final var returnValueDefinition = this.generateStatement(declaration.type + " _ret");

						final var cases = variants
								.stream()
								.map(variant -> this.generateCase(structName, declaration, variant))
								.collect(Collectors.joining());

						return returnValueDefinition + this.generateIndent(1) + "switch (" + "this.variant" + ") {" + cases +
									 this.generateIndent(1) + "}" + this.generateStatement("return _ret");
					});
				}*/
			else {
				outputContent = "?";
			}
			var compiledParameters = parameters.stream().map(/*Declaration::generate*/).collect(Collectors.joining(", "));
	/*final var modifiedMethodDeclaration = switch (methodDeclaration) {
					case Constructor constructor -> constructor;
					case Declaration declaration -> declaration.mapName(name -> name + "_" + structName);
					case Placeholder placeholder -> placeholder;
				}*/
			/**/;
			var header = modifiedMethodDeclaration.generate() + "(" + compiledParameters + ")";
			var generated = header + "{" + outputContent + System.lineSeparator() + "}" + System.lineSeparator();
			this->functions.add(generated);
			var parameterTypes = parameters.stream().map(/*Declaration::type*/).toList();
	/*return switch (methodDeclaration) {
					case Constructor _ -> Optional.empty();
					case Declaration member -> Optional.of(new FunctionDeclaration(member.type, member.name, parameterTypes));
					case Placeholder placeholder -> Optional.of(placeholder);
				}*/
			/**/;
	}
	}
	return Optional.of(new_Placeholder(stripped));
}
char* compileMethodsSegments_Main(void* _this, char* inputContent, int indent){
	Main* this = (Main*) _this;
	return this->compileStatements(inputContent, /* input -> this.compileMethodSegment(input, indent)*/);
}
char* generateCase_Main(void* _this, char* structName, Declaration declaration, char* variant){
	Main* this = (Main*) _this;
	return /*this.generateIndent(2) + "case " + structName + "Variant." + variant + "Variant:" +
					 this.generateStatement(3, "_ret = " + declaration.name + "_" + variant + "(&this.data." + variant + ")") +
					 this.generateStatement(3, "break")*/;
}
MethodDeclaration parseMethodDeclaration_Main(void* _this, char* declaration, char* structName, List<char*> typeParameters){
	Main* this = (Main*) _this;
	return /*this
				.parseDeclaration(declaration, typeParameters)
				.<MethodDeclaration>map(value -> value)
				.or(() -> this.parseConstructor(declaration, structName))
				.orElseGet(() -> new Placeholder(declaration))*/;
}
Optional<MethodDeclaration> parseConstructor_Main(void* _this, char* declaration, char* structName){
	Main* this = (Main*) _this;
	if (declaration.strip().equals(structName)) {
		return Optional.of(new_Constructor(structName));
	}
	else {
		return Optional.empty();
	}
}
Optional<StructMember> compileEnumValues_Main(void* _this, char* input, char* structName){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (/*!stripped.endsWith(";")*/) {
		return Optional.empty();
	}
	var enumValues = this->divide(stripped.substring(0, stripped.length() - 1), /*
								(state, character) -> new ValueFolder().apply(state, character)*/).map(/*String::strip*/).filter(/*slice -> !slice.isEmpty()*/).toList();
	if (/*!enumValues.isEmpty()*/) {
	/*for (var enumValue : enumValues) {
				if (enumValue.endsWith(")")) {
					final var substring = enumValue.substring(0, enumValue.length() - 1);
					final var i = substring.indexOf("(");
					if (i >= 0) {
						final var name = substring.substring(0, i);
						if (!this.isIdentifier(name)) {
							return Optional.empty();
						}

						final var substring2 = substring.substring(i + 1);
						final var generated =
								structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" +
								System.lineSeparator();

						this.globals.add(generated);
					}
				}
			}*/
	}
	return Optional.of(new_EmptyStructMember());
}
char* compileMethodSegment_Main(void* _this, char* input, int indent){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (stripped.isEmpty()) {
		return "";
	}
	if (stripped.endsWith(";")) {
		var substring = stripped.substring(0, stripped.length() - 1);
		return this->generateIndent(indent) + this->compileMethodStatement(substring) + ";";
	}
	if (stripped.startsWith("if")) {
		var substring = stripped.substring(2).strip();
	/*if (substring.startsWith("(")) {
				final var afterConditionStart = substring.substring(1).strip();
				var conditionEnd = -1;
				var depth = 0;
				for (var i = 0; i < afterConditionStart.length(); i++) {
					final var c = afterConditionStart.charAt(i);
					if (c == '(') {
						depth++;
					}
					if (c == ')') {
						if (depth == 0) {
							conditionEnd = i;
							break;
						}

						depth--;
					}
				}

				if (conditionEnd >= 0) {
					final var condition = afterConditionStart.substring(0, conditionEnd);
					final var withBraces = afterConditionStart.substring(conditionEnd + 1).strip();
					if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
						final var content = withBraces.substring(1, withBraces.length() - 1);
						return this.generateIndent(indent) + "if (" + this.compileExpressionOrPlaceholder(condition) + ") {" +
									 this.compileMethodsSegments(content, indent + 1) + this.generateIndent(1) + "}";
					}
				}
			}*/
	}
	if (stripped.startsWith("else ")) {
		var substring = stripped.substring("else ".length()).strip();
		if (substring.startsWith("{") && substring.endsWith("}")) {
			var substring1 = substring.substring(1, substring.length() - 1);
			return this->generateIndent(indent) + "else {" + this.compileMethodsSegments(substring1, indent + 1) +
							 this.generateIndent(indent) + "}";
	}
	}
	return System.lineSeparator() + "\t" + wrap(stripped);
}
char* compileMethodStatement_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (stripped.startsWith("return ")) {
		return "return " + this->compileExpressionOrPlaceholder(stripped.substring("return ".length()));
	}
	var maybeInvokable = this->compileInvokable(stripped);
	if (maybeInvokable.isPresent()) {
		return maybeInvokable.get();
	}
	var i = stripped.indexOf("=");
	if (/*i >= 0*/) {
		var destination = stripped.substring(0, i);
		var substring1 = stripped.substring(i + 1);
		return this->compileExpression(destination).or(/*() -> this.parseDeclaration(destination, Collections.emptyList()).map(Declaration::generate)*/).orElseGet(/*() -> wrap(destination)*/) + " = " + this->compileExpressionOrPlaceholder(substring1);
	}
	if (stripped.endsWith(" +  + ")) {
		var instance = stripped.substring(0, stripped.length() - 2);
		return this->compileExpressionOrPlaceholder(instance) + " +  + ";
	}
	return wrap(stripped);
}
char* compileExpressionOrPlaceholder_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	return this->compileExpression(input).orElseGet(/*() -> wrap(input)*/);
}
Optional<char*> compileExpression_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return Optional.of(stripped);
	}
	var maybeOperator = this->compileOperator(stripped, " == ").or(/*() -> this.compileOperator(stripped, "<")*/).or(/*() -> this.compileOperator(stripped, "+")*/).or(/*() -> this.compileOperator(stripped, "-")*/).or(/*() -> this.compileOperator(stripped, "&&")*/);
	if (maybeOperator.isPresent()) {
		return maybeOperator;
	}
	var i = stripped.lastIndexOf(".");
	if (/*i >= 0*/) {
		var instanceString = stripped.substring(0, i);
		var memberName = stripped.substring(i + 1).strip();
		if (this->isIdentifier(memberName)) {
			var maybeInstance = this->compileExpression(instanceString);
			if (maybeInstance.isPresent()) {
				var instance = maybeInstance.get();
				/*final String generated*/;
				if (instance.equals("this")) {
					generated = "this->" + memberName;
	}
				else {
					generated = instance + "." + memberName;
				}
				return Optional.of(generated);
	}
	}
	}
	if (this->isIdentifier(stripped)) {
		return Optional.of(stripped);
	}
	var maybeInvokable = this->compileInvokable(stripped);
	if (maybeInvokable.isPresent()) {
		return maybeInvokable;
	}
	if (this->isNumber(stripped)) {
		return Optional.of(stripped);
	}
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return Optional.of(stripped);
	}
	return Optional.empty();
}
Optional<char*> compileOperator_Main(void* _this, char* input, char* operator){
	Main* this = (Main*) _this;
	var i1 = input.indexOf(operator);
	if (/*i1 >= 0*/) {
		var leftString = input.substring(0, i1);
		var right = input.substring(i1 + operator.length());
		var leftResult = this->compileExpression(leftString);
		if (leftResult.isPresent()) {
			var rightResult = this->compileExpression(right);
			if (rightResult.isPresent()) {
				return Optional.of(/*leftResult.get(*/) + " " + operator + " " + rightResult.get(/*)*/);
	}
	}
	}
	return Optional.empty();
}
Optional<char*> compileInvokable_Main(void* _this, char* stripped){
	Main* this = (Main*) _this;
	/*if (stripped.endsWith(")")) {
			final var withoutEnd = stripped.substring(0, stripped.length() - 1);

			var callerStart = -1;
			var depth = 0;
			for (var i = 0; i < withoutEnd.length(); i++) {
				final var c = withoutEnd.charAt(i);
				if (c == '(') {
					if (depth == 0) {
						callerStart = i;
					}

					depth++;
				}
				if (c == ')') {
					depth--;
				}
			}

			if (callerStart >= 0) {
				final var callerString = withoutEnd.substring(0, callerStart);
				final var arguments = withoutEnd.substring(callerStart + 1);
				final var joinedArguments = this
						.divide(arguments, new EscapedFolder(new ValueFolder()))
						.map(this::compileExpressionOrPlaceholder)
						.collect(Collectors.joining(", "));

				final var maybeCaller = this.compileCaller(callerString);
				if (maybeCaller.isPresent()) {
					return Optional.of(maybeCaller.get() + "(" + joinedArguments + ")");
				}
			}
		}*/
	return Optional.empty();
}
boolean isNumber_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	/*for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (Character.isDigit(c)) {
				continue;
			}
			return false;
		}*/
	return true;
}
Optional<char*> compileCaller_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (stripped.startsWith("new ")) {
		var type = stripped.substring("new ".length());
		return Optional.of("new_" + this->compileType(type));
	}
	return this->compileExpression(stripped);
}
Optional<Declaration> parseDeclaration_Main(void* _this, char* input, List<char*> typeParameters){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	var nameSeparator = stripped.lastIndexOf(" ");
	if (/*nameSeparator >= 0*/) {
		var beforeName = stripped.substring(0, nameSeparator).strip();
		var name = stripped.substring(nameSeparator + 1).strip();
		var typeSeparator =  - 1;
		var depth = 0;
	/*for (var i = 0; i < beforeName.length(); i++) {
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
		if (/*!this.isIdentifier(name)*/) {
			return Optional.empty();
	}
		if (typeSeparator < 0) {
			var type = this->compileType(beforeName);
			return Optional.of(new_Declaration(type, name));
	}
		var beforeType = beforeName.substring(0, typeSeparator).strip();
		var copy = new_ArrayList<char*>(typeParameters);
		if (beforeType.endsWith(">")) {
			var substring = beforeType.substring(0, beforeType.length() - 1);
			var i = substring.indexOf(" < ");
			if (/*i >= 0*/) {
				var substring2 = substring.substring(i + 1);
				copy.addAll(this->splitValues(substring2));
				beforeType = substring.substring(0, i);
	}
	}
		if (this->isIdentifier(name)) {
			return Optional.of(new_Declaration(copy, Optional.of(beforeType), this->compileType(beforeName.substring(typeSeparator + 1)), name));
	}
	}
	return Optional.empty();
}
char* compileType_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	return this->parseType(input).generate();
}
Type parseType_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (stripped.equals("void")) {
		return PrimitiveType.Void;
	}
	if (stripped.endsWith("[]")) {
		var slice = stripped.substring(0, stripped.length() - 2);
		var type = this->parseType(slice);
		return new_PointerType(type);
	}
	if (stripped.equals("String")) {
		return new_PointerType(PrimitiveType.Char);
	}
	if (stripped.endsWith(">")) {
		var substring = stripped.substring(0, stripped.length() - 1);
		var i = substring.indexOf(" < ");
		if (/*i >= 0*/) {
			var base = substring.substring(0, i);
			var parameters = substring.substring(i + 1);
			var list = this->divide(parameters, /* (state, character) -> new ValueFolder().apply(state, character)*/).map(/*this::parseType*/).toList();
			return new_TemplateType(base, list);
	}
	}
	if (this->isIdentifier(stripped)) {
		return new_Identifier(stripped);
	}
	return new_Placeholder(stripped);
}
/**/