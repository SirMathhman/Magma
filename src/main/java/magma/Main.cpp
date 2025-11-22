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
struct StructMemberTable {
	char* (*generate)(void*);
};
struct StructMember {
	StructMemberTable table;
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
/*Void*/(){?
}
PrimitiveType new_PrimitiveType(char* content){
	PrimitiveType this;
	this.content = content;
	return this;
}
char* generate_PrimitiveType(void* _this){
	PrimitiveType this = *((PrimitiveType*) _this);
	return this.content;
}
char* toBaseName_PrimitiveType(void* _this){
	PrimitiveType this = *((PrimitiveType*) _this);
	return this.content;
}
/*private enum PrimitiveType implements Type {
		Void*/(){?
}
R apply_F1R(void* _this, T0 value){
	F1R<T0, R> this = *((F1R<T0, R>*) _this);
	R _ret;
	switch (this.variant) {
	}
	return _ret;
}
template <typename T0, typename R>
/*{
		R*/ apply_Main(void* _this, T0 value){
	Main this = *((Main*) _this);
	/*{
		R*/ _ret;
	switch (this.variant) {
	}
	return _ret;
}
template <typename T, typename X, typename R>
Result<R, X> mapValue_Result(void* _this, F1R<T, R> mapper){
	Result<T, X> this = *((Result<T, X>*) _this);
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
template <typename T, typename X> permits Err, typename Ok {
		<R>
Result<R, X> mapValue_Main(void* _this, F1R<T, R> mapper){
	Main this = *((Main*) _this);
	Result<R, X> _ret;
	switch (this.variant) {
	}
	return _ret;
}
char* generate_Type(void* _this){
	Type this = *((Type*) _this);
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
	Type this = *((Type*) _this);
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
/*{
		String*/ generate_Main(void* _this){
	Main this = *((Main*) _this);
	/*{
		String*/ _ret;
	switch (this.variant) {
	}
	return _ret;
}
char* generate_MethodDeclaration(void* _this){
	MethodDeclaration this = *((MethodDeclaration*) _this);
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
/*{
		String*/ generate_Main(void* _this){
	Main this = *((Main*) _this);
	/*{
		String*/ _ret;
	switch (this.variant) {
	}
	return _ret;
}
char* generate_StructMember(void* _this){
	StructMember this = *((StructMember*) _this);
	char* _ret;
	switch (this.variant) {
	}
	return _ret;
}
/*{
		String*/ generate_Main(void* _this){
	Main this = *((Main*) _this);
	/*{
		String*/ _ret;
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
	Err<T, X> this = *((Err<T, X>*) _this);
	return new_Err<R, X>(this.error);
}
/*private record Err<T, X>*/(X error){?
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
	Ok<T, X> this = *((Ok<T, X>*) _this);
	return new_Ok<R, X>(mapper.apply(this.value));
}
/*private record Ok<T, X>*/(T value){?
}
public State_State(void* _this, char* input){
	State this = *((State*) _this);
	this.input = input;
	this.index = /*0*/;
	this.buffer = new_StringBuilder();
	this.depth = /*0*/;
	this.segments = new_ArrayList<char*>();
}
boolean isShallow_State(void* _this){
	State this = *((State*) _this);
	return /*this.depth == 1*/;
}
boolean isLevel_State(void* _this){
	State this = *((State*) _this);
	return /*this.depth == 0*/;
}
State append_State(void* _this, Character next){
	State this = *((State*) _this);
	/*this.buffer.append(next)*/;
	return this;
}
Optional<Character> pop_State(void* _this){
	State this = *((State*) _this);
	/*if (this.index < this.input.length()) {
				final var value = this.input.charAt(this.index);
				this.index++;
				return Optional.of(value);
			}*/
	/*else {
				return Optional.empty();
			}*/
}
State advance_State(void* _this){
	State this = *((State*) _this);
	/*this.segments.add(this.buffer.toString())*/;
	/*this.buffer.setLength(0)*/;
	return this;
}
State enter_State(void* _this){
	State this = *((State*) _this);
	this.depth = /*this.depth + 1*/;
	return this;
}
State exit_State(void* _this){
	State this = *((State*) _this);
	this.depth = /*this.depth - 1*/;
	return this;
}
Stream<char*> stream_State(void* _this){
	State this = *((State*) _this);
	return this.segments.stream();
}
/*depth;

		public*/ State_Main(void* _this, char* input){
	Main this = *((Main*) _this);
	this.input = input;
	this.index = /*0*/;
	this.buffer = new_StringBuilder();
	this.depth = /*0*/;
	this.segments = new_ArrayList<char*>();
	/*}

		private boolean isShallow() {
			return this*/.depth = /*= 1*/;
	/*}

		private boolean isLevel() {
			return this*/.depth = /*= 0*/;
	/*}

		private State append(Character next) {
			this.buffer.append(next)*/;
	return this;
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
	return this;
	/*}

		private State enter() {
			this*/.depth = /*this.depth + 1*/;
	return this;
	/*}

		private State exit() {
			this*/.depth = /*this.depth - 1*/;
	return this;
	/*}

		private Stream<String> stream() {
			return this.segments.stream()*/;
	/*}*/
}
Type toType_PointerType(void* _this){
	PointerType this = *((PointerType*) _this);
	TypeData data;
	data.PointerType = this;
	return { TypeVariant.PointerTypeVariant, data };
}
char* generate_PointerType(void* _this){
	PointerType this = *((PointerType*) _this);
	return /*this.type.generate() + "*"*/;
}
char* toBaseName_PointerType(void* _this){
	PointerType this = *((PointerType*) _this);
	return /*this.type.toBaseName() + "_ptr"*/;
}
record PointerType_Main(void* _this, Type type){
	Main this = *((Main*) _this);
	record _ret;
	switch (this.variant) {
	}
	return _ret;
}
Type toType_TemplateType(void* _this){
	TemplateType this = *((TemplateType*) _this);
	TypeData data;
	data.TemplateType = this;
	return { TypeVariant.TemplateTypeVariant, data };
}
char* generate_TemplateType(void* _this){
	TemplateType this = *((TemplateType*) _this);
	/*final var typeArguments*/ = this.list.stream(/*).map(Type::generate).collect(Collectors.joining("*/, /*")*/);
	return /*this.base + "<" + typeArguments + ">"*/;
}
char* toBaseName_TemplateType(void* _this){
	TemplateType this = *((TemplateType*) _this);
	return this.base;
}
record TemplateType_Main(void* _this, char* base, List<Type> list){
	Main this = *((Main*) _this);
	record _ret;
	switch (this.variant) {
	}
	return _ret;
}
Type toType_Identifier(void* _this){
	Identifier this = *((Identifier*) _this);
	TypeData data;
	data.Identifier = this;
	return { TypeVariant.IdentifierVariant, data };
}
char* generate_Identifier(void* _this){
	Identifier this = *((Identifier*) _this);
	return this.value;
}
char* toBaseName_Identifier(void* _this){
	Identifier this = *((Identifier*) _this);
	return this.value;
}
record Identifier_Main(void* _this, char* value){
	Main this = *((Main*) _this);
	record _ret;
	switch (this.variant) {
	}
	return _ret;
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
	Placeholder this = *((Placeholder*) _this);
	return wrap(this.input);
}
char* toBaseName_Placeholder(void* _this){
	Placeholder this = *((Placeholder*) _this);
	return wrap(this.input);
}
record Placeholder_Main(void* _this, char* input){
	Main this = *((Main*) _this);
	record _ret;
	switch (this.variant) {
	}
	return _ret;
}
MethodDeclaration toMethodDeclaration_Constructor(void* _this){
	Constructor this = *((Constructor*) _this);
	MethodDeclarationData data;
	data.Constructor = this;
	return { MethodDeclarationVariant.ConstructorVariant, data };
}
char* generate_Constructor(void* _this){
	Constructor this = *((Constructor*) _this);
	return /*this.structName + " new_" + this*/.structName;
}
record Constructor_Main(void* _this, char* structName){
	Main this = *((Main*) _this);
	record _ret;
	switch (this.variant) {
	}
	return _ret;
}
MethodDeclaration toMethodDeclaration_Declaration(void* _this){
	Declaration this = *((Declaration*) _this);
	MethodDeclarationData data;
	data.Declaration = this;
	return { MethodDeclarationVariant.DeclarationVariant, data };
}
public Declaration_Declaration(void* _this, char* type, char* name){
	Declaration this = *((Declaration*) _this);
	/*this(Collections.emptyList(), Optional.empty(), type, name)*/;
}
char* generate_Declaration(void* _this){
	Declaration this = *((Declaration*) _this);
	/*var beforeDeclaration*/ = generateTemplateString(this.typeParameters());
	return /*beforeDeclaration + this.type + " " + this*/.name;
}
Declaration mapName_Declaration(void* _this, F1R<char*, char*> mapper){
	Declaration this = *((Declaration*) _this);
	return new_Declaration(this.typeParameters, this.maybeBeforeType, this.type, mapper.apply(this.name));
}
record Declaration_Main(void* _this, List<char*> typeParameters, Optional<char*> maybeBeforeType, char* type, char* name){
	Main this = *((Main*) _this);
	record _ret;
	switch (this.variant) {
	}
	return _ret;
}
StructMember toStructMember_FunctionDeclaration(void* _this){
	FunctionDeclaration this = *((FunctionDeclaration*) _this);
	StructMemberData data;
	data.FunctionDeclaration = this;
	return { StructMemberVariant.FunctionDeclarationVariant, data };
}
char* generate_FunctionDeclaration(void* _this){
	FunctionDeclaration this = *((FunctionDeclaration*) _this);
	/*final var joinedParameterTypes*/ = this.parameterTypes.stream(/*).collect(Collectors.joining("*/, /*"*/, /*"("*/, /*")")*/);
	return /*this.type + " (*" + this.name + ")" + joinedParameterTypes*/;
}
record FunctionDeclaration_Main(void* _this, char* type, char* name, List<char*> parameterTypes){
	Main this = *((Main*) _this);
	record _ret;
	switch (this.variant) {
	}
	return _ret;
}
/*public static final List<String> structures = new ArrayList<String>*/(){?
}
/*public static final List<String> functions = new ArrayList<String>*/(){?
}
/*public static final List<String> globals = new ArrayList<String>*/(){?
}
void main_Main(void* _this, char** args){
	Main this = *((Main*) _this);
	/*run().ifPresent(Throwable::printStackTrace)*/;
}
Optional<IOException> run_Main(void* _this){
	Main this = *((Main*) _this);
	/*final var source*/ = Paths.get(/*"."*/, /*"src"*/, /*"main"*/, /*"java"*/, /*"magma"*/, /*"Main.java"*/);
	/*final var target*/ = source.resolveSibling(/*"Main.cpp"*/);
	/*final var input*/ = readString(/*source).mapValue(Main::compile*/);
	/*return switch (input) {
			case Err<String, IOException> v -> Optional.of(v.error);
			case Ok<String, IOException> v -> writeString(target, v.value);
		}*/
	/**/;
}
Optional<IOException> writeString_Main(void* _this, Path target, char* output){
	Main this = *((Main*) _this);
	/*try {
			Files.writeString(target, output);
			return Optional.empty();
		}*/
	/*catch (IOException e) {
			return Optional.of(e);
		}*/
}
Result<char*, IOException> readString_Main(void* _this, Path source){
	Main this = *((Main*) _this);
	/*try {
			return new Ok<String, IOException>(Files.readString(source));
		}*/
	/*catch (IOException e) {
			return new Err<String, IOException>(e);
		}*/
}
char* compile_Main(void* _this, char* input){
	Main this = *((Main*) _this);
	/*final var all*/ = compileStatements(input, /*Main::compileRootSegment*/);
	/*final var joinedStructures*/ = String.join(/*""*/, structures);
	/*final var joinedGlobals*/ = String.join(/*""*/, globals);
	/*final var joinedFunctions*/ = String.join(/*""*/, functions);
	return /*joinedStructures + joinedGlobals + joinedFunctions + all*/;
}
char* compileStatements_Main(void* _this, char* input, F1R<char*, char*> mapper){
	Main this = *((Main*) _this);
	return compileAll(input, mapper, /*Main::foldStatement*/);
}
char* compileAll_Main(void* _this, char* input, F1R<char*, char*> mapper, BiFunction<State, Character, State> folder){
	Main this = *((Main*) _this);
	return divide(input, /*folder)*/.map(/*mapper::apply).collect(Collectors.joining(""*/));
}
Stream<char*> divide_Main(void* _this, char* input, BiFunction<State, Character, State> folder){
	Main this = *((Main*) _this);
	/*var current*/ = new_State(input);
	/*while (true) {
			final var maybeNext = current.pop();
			if (maybeNext.isEmpty()) {
				break;
			}

			final var next = maybeNext.get();
			current = folder.apply(current, next);
		}*/
	return current.advance(/*).stream(*/);
}
State foldStatement_Main(void* _this, State current, Character next){
	Main this = *((Main*) _this);
	/*final var appended*/ = current.append(next);
	/*if (next*/ = /*= '*/;
	/*' && appended.isLevel()) {
			return appended.advance();
		}*/
	/*if (next == '*/
}
/*' && appended.isShallow*/(){?
}
/*if */(){?
}
/*') {
			return appended.exit*/(){?
}
/*public class Main {
	private enum PrimitiveType implements Type {
		Void("void"), Char("char");

		private final String content;

		PrimitiveType(String content) {this.content = content;}

		@Override
		public String generate() {
			return this.content;
		}

		@Override
		public String toBaseName() {
			return this.content;
		}
	}

	private interface F1R<T0, R> {
		R apply(T0 value);
	}

	private sealed interface Result<T, X> permits Err, Ok {
		<R> Result<R, X> mapValue(F1R<T, R> mapper);
	}

	private sealed interface Type permits Identifier, Placeholder, PointerType, PrimitiveType, TemplateType {
		String generate();

		String toBaseName();
	}

	private sealed interface MethodDeclaration permits Constructor, Declaration, Placeholder {
		String generate();
	}

	private interface StructMember {
		String generate();
	}

	private record Err<T, X>(X error) implements Result<T, X> {
		@Override
		public <R> Result<R, X> mapValue(F1R<T, R> mapper) {
			return new Err<R, X>(this.error);
		}
	}

	private record Ok<T, X>(T value) implements Result<T, X> {
		@Override
		public <R> Result<R, X> mapValue(F1R<T, R> mapper) {
			return new Ok<R, X>(mapper.apply(this.value));
		}
	}

	private static class State {
		private final String input;
		private final ArrayList<String> segments;
		private final StringBuilder buffer;
		private int index;
		private int depth;

		public State(String input) {
			this.input = input;
			this.index = 0;
			this.buffer = new StringBuilder();
			this.depth = 0;
			this.segments = new ArrayList<String>();
		}

		private boolean isShallow() {
			return this.depth == 1;
		}

		private boolean isLevel() {
			return this.depth == 0;
		}

		private State append(Character next) {
			this.buffer.append(next);
			return this;
		}

		private Optional<Character> pop() {
			if (this.index < this.input.length()) {
				final var value = this.input.charAt(this.index);
				this.index++;
				return Optional.of(value);
			} else {
				return Optional.empty();
			}
		}

		private State advance() {
			this.segments.add(this.buffer.toString());
			this.buffer.setLength(0);
			return this;
		}

		private State enter() {
			this.depth = this.depth + 1;
			return this;
		}

		private State exit() {
			this.depth = this.depth - 1;
			return this;
		}

		private Stream<String> stream() {
			return this.segments.stream();
		}
	}

	private record PointerType(Type type) implements Type {
		@Override
		public String generate() {
			return this.type.generate() + "*";
		}

		@Override
		public String toBaseName() {
			return this.type.toBaseName() + "_ptr";
		}
	}

	private record TemplateType(String base, List<Type> list) implements Type {

		@Override
		public String generate() {
			final var typeArguments = this.list.stream().map(Type::generate).collect(Collectors.joining(", "));

			return this.base + "<" + typeArguments + ">";
		}

		@Override
		public String toBaseName() {
			return this.base;
		}
	}

	private record Identifier(String value) implements Type {
		@Override
		public String generate() {
			return this.value;
		}

		@Override
		public String toBaseName() {
			return this.value;
		}
	}

	private record Placeholder(String input) implements Type, MethodDeclaration, StructMember {
		@Override
		public String generate() {
			return wrap(this.input);
		}

		@Override
		public String toBaseName() {
			return wrap(this.input);
		}
	}

	private record Constructor(String structName) implements MethodDeclaration {
		@Override
		public String generate() {
			return this.structName + " new_" + this.structName;
		}
	}

	private record Declaration(List<String> typeParameters, Optional<String> maybeBeforeType, String type, String name)
			implements MethodDeclaration {
		public Declaration(String type, String name) {
			this(Collections.emptyList(), Optional.empty(), type, name);
		}

		@Override
		public String generate() {
			var beforeDeclaration = generateTemplateString(this.typeParameters());
			return beforeDeclaration + this.type + " " + this.name;
		}

		public Declaration mapName(F1R<String, String> mapper) {
			return new Declaration(this.typeParameters, this.maybeBeforeType, this.type, mapper.apply(this.name));
		}
	}

	private record FunctionDeclaration(String type, String name, List<String> parameterTypes) implements StructMember {
		@Override
		public String generate() {
			final var joinedParameterTypes = this.parameterTypes.stream().collect(Collectors.joining(", ", "(", ")"));
			return this.type + " (*" + this.name + ")" + joinedParameterTypes;
		}
	}


	public static final List<String> structures = new ArrayList<String>();
	public static final List<String> functions = new ArrayList<String>();
	public static final List<String> globals = new ArrayList<String>();

	public static void main(String[] args) {
		run().ifPresent(Throwable::printStackTrace);
	}

	private static Optional<IOException> run() {
		final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		final var target = source.resolveSibling("Main.cpp");
		final var input = readString(source).mapValue(Main::compile);

		return switch (input) {
			case Err<String, IOException> v -> Optional.of(v.error);
			case Ok<String, IOException> v -> writeString(target, v.value);
		};
	}

	private static Optional<IOException> writeString(Path target, String output) {
		try {
			Files.writeString(target, output);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(e);
		}
	}

	private static Result<String, IOException> readString(Path source) {
		try {
			return new Ok<String, IOException>(Files.readString(source));
		} catch (IOException e) {
			return new Err<String, IOException>(e);
		}
	}

	private static String compile(String input) {
		final var all = compileStatements(input, Main::compileRootSegment);

		final var joinedStructures = String.join("", structures);
		final var joinedGlobals = String.join("", globals);
		final var joinedFunctions = String.join("", functions);
		return joinedStructures + joinedGlobals + joinedFunctions + all;
	}

	private static String compileStatements(String input, F1R<String, String> mapper) {
		return compileAll(input, mapper, Main::foldStatement);
	}

	private static String compileAll(String input,
																	 F1R<String, String> mapper,
																	 BiFunction<State, Character, State> folder) {
		return divide(input, folder).map(mapper::apply).collect(Collectors.joining(""));
	}

	private static Stream<String> divide(String input, BiFunction<State, Character, State> folder) {
		var current = new State(input);
		while (true) {
			final var maybeNext = current.pop();
			if (maybeNext.isEmpty()) {
				break;
			}

			final var next = maybeNext.get();
			current = folder.apply(current, next);
		}

		return current.advance().stream();
	}

	private static State foldStatement(State current, Character next) {
		final var appended = current.append(next);
		if (next == ';' && appended.isLevel()) {
			return appended.advance();
		}

		if (next == '}' && appended.isShallow()) {
			return appended.advance().exit();
		}

		if (next == '{') {
			return appended.enter();
		}

		if (next == '}') {
			return appended.exit();
		}

		return appended;
	}*//*private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return compileStructure("class", stripped).map(StructMember::generate).orElseGet(() -> wrap(stripped));
	}*//*private static Optional<StructMember> compileStructure(String type, String stripped) {
		final var i = stripped.indexOf(type + " ");
		if (i < 0) {return Optional.empty();}
		final var modifiers = stripped.substring(0, i).strip();
		final var afterKeyword = stripped.substring(i + (type + " ").length()).strip();

		final var i1 = afterKeyword.indexOf("{");
		if (i1 < 0) {return Optional.empty();}
		var beforeContent = afterKeyword.substring(0, i1).strip();

		final var withEnd = afterKeyword.substring(i1 + 1).strip();
		if (!withEnd.endsWith("}")) {
			return Optional.empty();
		}
		final var inputContent = withEnd.substring(0, withEnd.length() - 1);

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
			beforeContent = beforeContent.substring(0, i4).strip();
			implementees = divide(implementeesString, Main::foldValue)
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.map(Main::parseType)
					.toList();
		}

		List<Declaration> recordFields = Collections.emptyList();
		if (beforeContent.endsWith(")")) {
			final var substring = beforeContent.substring(0, beforeContent.length() - 1);
			final var i3 = substring.indexOf("(");
			if (i3 >= 0) {
				beforeContent = substring.substring(0, i3);
				recordFields = divide(substring.substring(i3 + 1), Main::foldValue)
						.map(slice -> parseDeclaration(slice, Collections.emptyList()))
						.flatMap(Optional::stream)
						.toList();
			}
		}

		List<String> typeParameters = new ArrayList<String>();
		final var i3 = beforeContent.indexOf("<");
		if (i3 >= 0) {
			final var substring1 = beforeContent.substring(i3 + 1).strip();
			if (substring1.endsWith(">")) {
				beforeContent = beforeContent.substring(0, i3);
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
		final var joinedTypeParameters = joinTypeParameters(typeParameters);

		String fields = "";
		var dependencies = new StringBuilder();
		for (var implementee : implementees) {
			final var identifier = implementee.toBaseName();

			final var variant = identifier + "Variant" + "." + name + "Variant";
			final var thisType = name + joinedTypeParameters;
			final var conversionFunctionContent = generateStatement(thisType + " this = *((" + thisType + "*) _this)") +
																						generateStatement(identifier + "Data" + joinedTypeParameters + " data") +
																						generateStatement("data." + name + " = this") +
																						generateStatement("return { " + variant + ", data }");

			final var conversionFunction =
					templateString + implementee.generate() + " to" + identifier + "_" + name + "(void* _this){" +
					conversionFunctionContent + System.lineSeparator() + "}" + System.lineSeparator();

			functions.add(conversionFunction);
		}

		final var joinedRecordFields =
				recordFields.stream().map(Declaration::generate).map(Main::generateStatement).collect(Collectors.joining());

		var finalTypeParameters = typeParameters;
		List<String> finalVariants = variants;
		final var members = divide(inputContent, Main::foldStatement)
				.map(slice -> compileClassSegment(slice, name, finalTypeParameters, finalVariants))
				.flatMap(Optional::stream)
				.toList();

		if (modifiersList.contains("sealed")) {
			modifiersList.remove("sealed");

			final var enumFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + "Variant")
					.collect(Collectors.joining(", "));

			final var generatedEnum =
					"enum " + name + "Variant {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();

			final var unionFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + "Data" + joinedTypeParameters + " " + variant +
													";")
					.collect(Collectors.joining());

			final var generatedUnion =
					templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" +
					System.lineSeparator();

			fields += System.lineSeparator() + "\t" + name + "Variant variant;" + System.lineSeparator() + "\t" + name +
								"Data data;";

			dependencies.append(generatedEnum).append(generatedUnion);
		} else if (type.equals("interface")) {
			final var table = generateStatement(name + "Table" + joinedTypeParameters + " table");
			final var data = generateStatement("void* data");

			final var tableMembers =
					members.stream().map(StructMember::generate).map(Main::generateStatement).collect(Collectors.joining(""));
			final var vTable = templateString + "struct " + name + "Table" + joinedTypeParameters + " {" + tableMembers +
												 System.lineSeparator() + "};" + System.lineSeparator();

			dependencies.append(vTable);
			fields += table + data;
		}
		
		final var generated =
				dependencies + templateString + "struct " + name + " {" + joinedRecordFields + fields + System.lineSeparator() +
				"};" + System.lineSeparator();
		structures.add(generated);

		return Optional.empty();
	}*//*private static String joinTypeParameters(List<String> typeParameters) {
		final String joinedTypeParameters;
		if (typeParameters.isEmpty()) {
			joinedTypeParameters = "";
		} else {
			joinedTypeParameters = typeParameters.stream().collect(Collectors.joining(", ", "<", ">"));
		}
		return joinedTypeParameters;
	}*//*private static String generateStatement(String content) {return generateStatement(1, content);}*//*private static String generateStatement(int depth, String content) {
		return generateIndent(depth) + content + ";";
	}*//*private static String generateIndent(int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}*//*private static List<String> splitValues(String input) {
		return Arrays.stream(input.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
	}*//*private static String generateTemplateString(List<String> typeParameters) {
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
	}*//*private static boolean isIdentifier(String input) {
		final var stripped = input.strip();
		for (var i = 0; i < stripped.length(); i++) {
			final var c = stripped.charAt(i);
			if (Character.isLetter(c) || (i != 0 && Character.isDigit(c))) {continue;}
			return false;
		}

		return true;
	}*//*private static Optional<StructMember> compileClassSegment(String input,
																														String structName,
																														List<String> typeParameters,
																														List<String> variants) {
		final var stripped = input.strip();

		if (stripped.isEmpty()) {
			return Optional.empty();
		}

		final var maybeEnum = compileStructure("enum", input);
		if (maybeEnum.isPresent()) {
			return maybeEnum;
		}

		final var maybeInterface = compileStructure("interface", input);
		if (maybeInterface.isPresent()) {
			return maybeInterface;
		}

		final var maybeRecord = compileStructure("record", input);
		if (maybeRecord.isPresent()) {
			return maybeRecord;
		}

		final var maybeClass = compileStructure("class", input);
		if (maybeClass.isPresent()) {
			return maybeClass;
		}

		final var maybeEnumValues = compileEnumValues(input, structName);
		if (maybeEnumValues.isPresent()) {
			return maybeEnumValues;
		}

		final var i = stripped.indexOf("(");
		if (i >= 0) {
			final var declarationString = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var parametersString = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();

				final var parameters = divide(parametersString, Main::foldValue)
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.toList()
						.stream()
						.map(param -> parseDeclaration(param, typeParameters))
						.flatMap(Optional::stream)
						.collect(Collectors.toCollection(ArrayList::new));

				final var methodDeclaration = parseMethodDeclaration(declarationString, structName, typeParameters);

				Optional<String> maybeCompiled = Optional.empty();
				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var inputContent = withBraces.substring(1, withBraces.length() - 1);
					maybeCompiled = Optional.of(compileStatements(inputContent, Main::compileMethodSegment));
				}

				String outputContent;
				if (methodDeclaration instanceof Constructor) {
					final var compiled = maybeCompiled.orElse("?");
					outputContent = generateStatement(structName + " this") + compiled + generateStatement("return this");
				} else if (methodDeclaration instanceof Declaration declaration) {
					parameters.addFirst(new Declaration("void*", "_this"));

					final var joinedTypeParameters = joinTypeParameters(typeParameters);

					final var thisInitialization = generateStatement(
							structName + joinedTypeParameters + " this = *((" + structName + joinedTypeParameters + "*) _this)");

					outputContent = thisInitialization + maybeCompiled.orElseGet(() -> {
						final var returnValueDefinition = generateStatement(declaration.type + " _ret");

						final var cases = variants
								.stream()
								.map(variant -> generateCase(structName, declaration, variant))
								.collect(Collectors.joining());

						return returnValueDefinition + generateIndent(1) + "switch (" + "this.variant" + ") {" + cases +
									 generateIndent(1) + "}" + generateStatement("return _ret");
					});
				} else {
					outputContent = "?";
				}

				final var compiledParameters = parameters.stream().map(Declaration::generate).collect(Collectors.joining(", "
				));

				final var modifiedMethodDeclaration = switch (methodDeclaration) {
					case Constructor constructor -> constructor;
					case Declaration declaration -> declaration.mapName(name -> name + "_" + structName);
					case Placeholder placeholder -> placeholder;
				};

				final var header = modifiedMethodDeclaration.generate() + "(" + compiledParameters + ")";
				final var generated = header + "{" + outputContent + System.lineSeparator() + "}" + System.lineSeparator();
				functions.add(generated);

				final var parameterTypes = parameters.stream().map(Declaration::type).toList();

				return switch (methodDeclaration) {
					case Constructor _ -> Optional.empty();
					case Declaration member -> Optional.of(new FunctionDeclaration(member.type, member.name, parameterTypes));
					case Placeholder placeholder -> Optional.of(placeholder);
				};
			}
		}

		return Optional.of(new Placeholder(stripped));
	}*//*private static String generateCase(String structName, Declaration declaration, String variant) {
		return generateIndent(2) + "case " + structName + "Variant." + variant + "Variant:" +
					 generateStatement(3, "_ret = " + declaration.name + "_" + variant + "(&this.data." + variant + ")") +
					 generateStatement(3, "break");
	}*//*private static MethodDeclaration parseMethodDeclaration(String declaration,
																													String structName,
																													List<String> typeParameters) {
		return parseDeclaration(declaration, typeParameters)
				.<MethodDeclaration>map(value -> value)
				.or(() -> parseConstructor(declaration, structName))
				.orElseGet(() -> new Placeholder(declaration));
	}*//*private static Optional<MethodDeclaration> parseConstructor(String declaration, String structName) {
		if (declaration.strip().equals(structName)) {
			return Optional.of(new Constructor(structName));
		} else {
			return Optional.empty();
		}
	}*//*private static Optional<StructMember> compileEnumValues(String input, String structName) {
		final var stripped = input.strip();
		if (!stripped.endsWith(";")) {
			return Optional.empty();
		}

		final var enumValues = divide(stripped.substring(0, stripped.length() - 1), Main::foldValue)
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList();

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
						final var generated =
								structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" +
								System.lineSeparator();

						globals.add(generated);
					}
				}
			}
		}

		return Optional.empty();
	}*//*private static State foldValue(State state, Character next) {
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
	}*//*private static String compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			return System.lineSeparator() + "\t" + compileMethodStatement(substring) + ";";
		}

		return System.lineSeparator() + "\t" + wrap(stripped);
	}*//*private static String compileMethodStatement(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("return ")) {
			return "return " + compileExpression(stripped.substring("return ".length()));
		}

		final var i = stripped.indexOf("=");
		if (i >= 0) {
			final var substring = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			return compileExpression(substring) + " = " + compileExpression(substring1);
		}

		return wrap(stripped);
	}*//*private static String compileExpression(String input) {
		final var stripped = input.strip();
		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var instance = stripped.substring(0, i);
			final var memberName = stripped.substring(i + 1).strip();
			if (isIdentifier(memberName)) {
				return compileExpression(instance) + "." + memberName;
			}
		}

		if (isIdentifier(stripped)) {
			return stripped;
		}

		if (stripped.endsWith(")")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i1 = substring.indexOf("(");
			if (i1 >= 0) {
				final var caller = substring.substring(0, i1);
				final var arguments = substring.substring(i1 + 1);
				final var joinedArguments =
						divide(arguments, Main::foldValue).map(Main::compileExpression).collect(Collectors.joining(", "));

				return compileCaller(caller) + "(" + joinedArguments + ")";
			}
		}

		return wrap(stripped);
	}*//*private static String compileCaller(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("new ")) {
			final var type = stripped.substring("new ".length());
			return "new_" + compileType(type);
		}

		return compileExpression(stripped);
	}*//*private static Optional<Declaration> parseDeclaration(String input, List<String> typeParameters) {
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
																					 name));
			}
		}

		return Optional.empty();
	}*//*private static String compileType(String input) {
		return parseType(input).generate();
	}*//*private static Type parseType(String input) {
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
	}*//*private static String wrap(String input) {
		final var replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}*//*}*/