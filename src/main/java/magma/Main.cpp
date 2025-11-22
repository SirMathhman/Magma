struct PrimitiveType {
	char* content;
};
template <typename T>
struct ListTable {
	Stream<T> (*stream)(void*);
	boolean (*isEmpty)(void*);
	List<T> (*addLast)(void*, T);
	boolean (*contains)(void*, T);
	List<T> (*addFirst)(void*, T);
	List<T> (*addAll)(void*, List<T>);
};
template <typename T>
struct List {
	ListTable<T> table;
	void* data;
};
template <typename T>
struct FRTable {
	T (*apply)(void*);
};
template <typename T>
struct FR {
	FRTable<T> table;
	void* data;
};
enum OptionVariant {
	NoneVariant,
	SomeVariant
};
template <typename T>
union OptionData {
	None<T> None;
	Some<T> Some;
};
template <typename T>
struct Option {
	OptionVariant variant;
	OptionData data;
};
template <typename T0, typename R>
struct F1RTable {
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
	Err<T, X> Err;
	Ok<T, X> Ok;
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
	Identifier Identifier;
	Placeholder Placeholder;
	PointerType PointerType;
	PrimitiveType PrimitiveType;
	TemplateType TemplateType;
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
	Constructor Constructor;
	Declaration Declaration;
	Placeholder Placeholder;
};
struct MethodDeclaration {
	MethodDeclarationVariant variant;
	MethodDeclarationData data;
};
enum StructMemberVariant {
	DeclarationVariant,
	EmptyStructMemberVariant,
	FieldVariant,
	FunctionDeclarationVariant,
	PlaceholderVariant
};
union StructMemberData {
	Declaration Declaration;
	EmptyStructMember EmptyStructMember;
	Field Field;
	FunctionDeclaration FunctionDeclaration;
	Placeholder Placeholder;
};
struct StructMember {
	StructMemberVariant variant;
	StructMemberData data;
};
struct FolderTable {
	State (*apply)(void*, State, char);
};
struct Folder {
	FolderTable table;
	void* data;
};
struct ActualTable {
};
struct Actual {
	ActualTable table;
	void* data;
};
template <typename T>
struct ArrayList {
	java.util.List<T> nativeList;
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
	char* input;
	StringBuilder buffer;
	List<char*> segments;
	int index;
	int depth;
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
	List<char*> annotations;
	List<char*> typeParameters;
	Option<char*> maybeBeforeType;
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
template <typename T>
struct Some {
	T value;
};
template <typename T>
struct None {
};
struct ConditionEndLocator {
};
struct Field {
	Declaration declaration;
};
struct Main {
	List<char*> globals;
	List<char*> structures;
	List<char*> functions;
	int counter;
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
Stream<T> stream_List(void* _this){
	List<T>* this = (List<T>*) _this;
	Stream<T> _ret;
	switch (this.variant) {
	}
	return _ret;
}
boolean isEmpty_List(void* _this){
	List<T>* this = (List<T>*) _this;
	boolean _ret;
	switch (this.variant) {
	}
	return _ret;
}
List<T> addLast_List(void* _this, T element){
	List<T>* this = (List<T>*) _this;
	List<T> _ret;
	switch (this.variant) {
	}
	return _ret;
}
boolean contains_List(void* _this, T element){
	List<T>* this = (List<T>*) _this;
	boolean _ret;
	switch (this.variant) {
	}
	return _ret;
}
List<T> addFirst_List(void* _this, T element){
	List<T>* this = (List<T>*) _this;
	List<T> _ret;
	switch (this.variant) {
	}
	return _ret;
}
List<T> addAll_List(void* _this, List<T> elements){
	List<T>* this = (List<T>*) _this;
	List<T> _ret;
	switch (this.variant) {
	}
	return _ret;
}
T apply_FR(void* _this){
	FR<T>* this = (FR<T>*) _this;
	T _ret;
	switch (this.variant) {
	}
	return _ret;
}
template <typename T, typename T>
Option<T> of_Option(void* _this, T value){
	Option<T, T>* this = (Option<T, T>*) _this;
	return new_Some<T>(value);
}
template <typename T, typename T, typename T>
Option<T> empty_Option(void* _this){
	Option<T, T, T>* this = (Option<T, T, T>*) _this;
	return new_None<T>();
}
template <typename T, typename T, typename T, typename R>
Option<R> map_Option(void* _this, F1R<T, R> mapper){
	Option<T, T, T, R>* this = (Option<T, T, T, R>*) _this;
	Option<R> _ret;
	switch (this.variant) {
		case OptionVariant.NoneVariant:
			_ret = map_None(&this.data.None);
			break;
		case OptionVariant.SomeVariant:
			_ret = map_Some(&this.data.Some);
			break;
	}
	return _ret;
}
T orElse_Option(void* _this, T other){
	Option<T, T, T, R>* this = (Option<T, T, T, R>*) _this;
	T _ret;
	switch (this.variant) {
		case OptionVariant.NoneVariant:
			_ret = orElse_None(&this.data.None);
			break;
		case OptionVariant.SomeVariant:
			_ret = orElse_Some(&this.data.Some);
			break;
	}
	return _ret;
}
template <typename T, typename T, typename T, typename R, typename R>
Option<R> flatMap_Option(void* _this, F1R<T, Option<R>> mapper){
	Option<T, T, T, R, R>* this = (Option<T, T, T, R, R>*) _this;
	Option<R> _ret;
	switch (this.variant) {
		case OptionVariant.NoneVariant:
			_ret = flatMap_None(&this.data.None);
			break;
		case OptionVariant.SomeVariant:
			_ret = flatMap_Some(&this.data.Some);
			break;
	}
	return _ret;
}
T orElseGet_Option(void* _this, FR<T> other){
	Option<T, T, T, R, R>* this = (Option<T, T, T, R, R>*) _this;
	T _ret;
	switch (this.variant) {
		case OptionVariant.NoneVariant:
			_ret = orElseGet_None(&this.data.None);
			break;
		case OptionVariant.SomeVariant:
			_ret = orElseGet_Some(&this.data.Some);
			break;
	}
	return _ret;
}
Stream<T> stream_Option(void* _this){
	Option<T, T, T, R, R>* this = (Option<T, T, T, R, R>*) _this;
	Stream<T> _ret;
	switch (this.variant) {
		case OptionVariant.NoneVariant:
			_ret = stream_None(&this.data.None);
			break;
		case OptionVariant.SomeVariant:
			_ret = stream_Some(&this.data.Some);
			break;
	}
	return _ret;
}
Option<T> or_Option(void* _this, FR<Option<T>> other){
	Option<T, T, T, R, R>* this = (Option<T, T, T, R, R>*) _this;
	Option<T> _ret;
	switch (this.variant) {
		case OptionVariant.NoneVariant:
			_ret = or_None(&this.data.None);
			break;
		case OptionVariant.SomeVariant:
			_ret = or_Some(&this.data.Some);
			break;
	}
	return _ret;
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
	Result<T, X, R>* this = (Result<T, X, R>*) _this;
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
		case StructMemberVariant.DeclarationVariant:
			_ret = generate_Declaration(&this.data.Declaration);
			break;
		case StructMemberVariant.EmptyStructMemberVariant:
			_ret = generate_EmptyStructMember(&this.data.EmptyStructMember);
			break;
		case StructMemberVariant.FieldVariant:
			_ret = generate_Field(&this.data.Field);
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
State apply_Folder(void* _this, State state, char character){
	Folder* this = (Folder*) _this;
	State _ret;
	switch (this.variant) {
	}
	return _ret;
}
template <typename T>
List<T> toList_ArrayList(void* _this){
	ArrayList<T> this = *((ArrayList<T>*) _this);
	ListData<T> data;
	data.ArrayList = this;
	return { ListVariant.ArrayListVariant, data };
}
private ArrayList_ArrayList(void* _this, java.util.List<T> nativeList){
	ArrayList<T>* this = (ArrayList<T>*) _this;
	this->nativeList = new_java.util.ArrayList<T>(nativeList);
}
public ArrayList_ArrayList(void* _this){
	ArrayList<T>* this = (ArrayList<T>*) _this;
	this(new_java.util.ArrayList<T>());
}
template <typename T>
ArrayList<T> addLast_ArrayList(void* _this, T element){
	ArrayList<T>* this = (ArrayList<T>*) _this;
	this->nativeList.add(element);
	return this;
}
template <typename T>
Stream<T> stream_ArrayList(void* _this){
	ArrayList<T>* this = (ArrayList<T>*) _this;
	return this->nativeList.stream();
}
template <typename T>
boolean isEmpty_ArrayList(void* _this){
	ArrayList<T>* this = (ArrayList<T>*) _this;
	return this->nativeList.isEmpty();
}
template <typename T>
boolean contains_ArrayList(void* _this, T element){
	ArrayList<T>* this = (ArrayList<T>*) _this;
	return this->nativeList.contains(element);
}
template <typename T>
List<T> addFirst_ArrayList(void* _this, T element){
	ArrayList<T>* this = (ArrayList<T>*) _this;
	this->nativeList.addFirst(element);
	return this;
}
auto lambda0(void* _this, auto (_, next)){
	return next;
}
template <typename T>
List<T> addAll_ArrayList(void* _this, List<T> elements){
	ArrayList<T>* this = (ArrayList<T>*) _this;
	return elements.stream().reduce(this, F? { alloc(ArrayList), F?Table { addLast }}, lambda0);
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
	Err<T, X, R>* this = (Err<T, X, R>*) _this;
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
	Ok<T, X, R>* this = (Ok<T, X, R>*) _this;
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
State append_State(void* _this, char next){
	State* this = (State*) _this;
	this->buffer.append(next);
	return this;
}
Option<char> pop_State(void* _this){
	State* this = (State*) _this;
	if (this->index < this.input.length()) {
		var value = this->input.charAt(this->index);
		this->index++;
		return Option.of(value);
	}
	else {
		return Option.empty();
	}
}
State advance_State(void* _this){
	State* this = (State*) _this;
	this->segments = this->segments.addLast(this->buffer.toString());
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
auto lambda1(void* _this, auto popped){
	var appended = this->append(popped);
	return new_Tuple<State, char>(appended, popped);
}
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _this){
	State* this = (State*) _this;
	return this->pop().map(lambda1);
}
auto lambda2(void* _this, auto tuple){
	return tuple.left;
}
Option<State> popAndAppendToOption_State(void* _this){
	State* this = (State*) _this;
	return this->popAndAppendToTuple().map(lambda2);
}
Option<char> peek_State(void* _this){
	State* this = (State*) _this;
	if (this->index < this.input.length()) {
		return Option.of(this->input.charAt(this->index));
	}
	return Option.empty();
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
	var typeArguments = this->list.stream().map(F? { alloc(Type), F?Table { generate }}).collect(Collectors.joining(", "));
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
	return this->structName + " new_" + this.structName;
}
MethodDeclaration toMethodDeclaration_Declaration(void* _this){
	Declaration this = *((Declaration*) _this);
	MethodDeclarationData data;
	data.Declaration = this;
	return { MethodDeclarationVariant.DeclarationVariant, data };
}
StructMember toStructMember_Declaration(void* _this){
	Declaration this = *((Declaration*) _this);
	StructMemberData data;
	data.Declaration = this;
	return { StructMemberVariant.DeclarationVariant, data };
}
public Declaration_Declaration(void* _this, char* type, char* name){
	Declaration* this = (Declaration*) _this;
	this(new_ArrayList<char*>(), new_ArrayList<char*>(), Option.empty(), type, name);
}
char* generate_Declaration(void* _this){
	Declaration* this = (Declaration*) _this;
	var beforeDeclaration = generateTemplateString(this->typeParameters());
	return beforeDeclaration + this->type + " " + this.name;
}
Declaration mapName_Declaration(void* _this, F1R<char*, char*> mapper){
	Declaration* this = (Declaration*) _this;
	return new_Declaration(this->annotations, this->typeParameters, this->maybeBeforeType, this->type, mapper.apply(this->name));
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
	return this->type + " (*" + this.name + ")" + joinedParameterTypes;
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
auto lambda3(void* _this, auto tuple){
	if (tuple.right == '\\') {
		return tuple.left.popAndAppendToOption().orElse(tuple.left);
	}
	return tuple.left;
}
State apply_EscapedFolder(void* _this, State state, char next){
	EscapedFolder* this = (EscapedFolder*) _this;
	if (next == '\'') {
		var appended = state.append(next);
		return appended.popAndAppendToTuple().map(lambda3).flatMap(F? { alloc(State), F?Table { popAndAppendToOption }}).orElse(appended);
	}
	if (next == '\"') {
		var current = state.append(next);
		while (true) {
			var maybeTuple = current.popAndAppendToTuple();
			if (!(maybeTuple.variant = ?.SomeVariant)) {
				break;
			}
			current = value.left;
			var right = value.right;
			if (right == '\\') {
				current = current.popAndAppendToOption().orElse(current);
			}
			if (right == '\"') {
				break;
			}
		}
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
State apply_ValueFolder(void* _this, State state, char next){
	ValueFolder* this = (ValueFolder*) _this;
	if (next == ',' && state.isLevel()) {
		return state.advance();
	}
	var appended = state.append(next);
	if (next == '-') {
		var peeked = appended.peek();
		if (peeked.variant = ?.SomeVariant) {
			return appended.popAndAppendToOption().orElse(appended);
		}
		else {
			return appended;
		}
	}
	if (next == '<' || next == '(') {
		return appended.enter();
	}
	if (next == '>' || next == ')') {
		return appended.exit();
	}
	return appended;
}
template <typename T>
Option<T> toOption_Some(void* _this){
	Some<T> this = *((Some<T>*) _this);
	OptionData<T> data;
	data.Some = this;
	return { OptionVariant.SomeVariant, data };
}
template <typename T, typename R>
Option<R> map_Some(void* _this, F1R<T, R> mapper){
	Some<T, R>* this = (Some<T, R>*) _this;
	return new_Some<R>(mapper.apply(this->value));
}
template <typename T, typename R>
T orElse_Some(void* _this, T other){
	Some<T, R>* this = (Some<T, R>*) _this;
	return this->value;
}
template <typename T, typename R, typename R>
Option<R> flatMap_Some(void* _this, F1R<T, Option<R>> mapper){
	Some<T, R, R>* this = (Some<T, R, R>*) _this;
	return mapper.apply(this->value);
}
template <typename T, typename R, typename R>
T orElseGet_Some(void* _this, FR<T> other){
	Some<T, R, R>* this = (Some<T, R, R>*) _this;
	return this->value;
}
template <typename T, typename R, typename R>
Stream<T> stream_Some(void* _this){
	Some<T, R, R>* this = (Some<T, R, R>*) _this;
	return Stream.of(this->value);
}
template <typename T, typename R, typename R>
Option<T> or_Some(void* _this, FR<Option<T>> other){
	Some<T, R, R>* this = (Some<T, R, R>*) _this;
	return this;
}
template <typename T>
Option<T> toOption_None(void* _this){
	None<T> this = *((None<T>*) _this);
	OptionData<T> data;
	data.None = this;
	return { OptionVariant.NoneVariant, data };
}
template <typename T, typename R>
Option<R> map_None(void* _this, F1R<T, R> mapper){
	None<T, R>* this = (None<T, R>*) _this;
	return new_None<R>();
}
template <typename T, typename R>
T orElse_None(void* _this, T other){
	None<T, R>* this = (None<T, R>*) _this;
	return other;
}
template <typename T, typename R, typename R>
Option<R> flatMap_None(void* _this, F1R<T, Option<R>> mapper){
	None<T, R, R>* this = (None<T, R, R>*) _this;
	return new_None<R>();
}
template <typename T, typename R, typename R>
T orElseGet_None(void* _this, FR<T> other){
	None<T, R, R>* this = (None<T, R, R>*) _this;
	return other.apply();
}
template <typename T, typename R, typename R>
Stream<T> stream_None(void* _this){
	None<T, R, R>* this = (None<T, R, R>*) _this;
	return Stream.empty();
}
template <typename T, typename R, typename R>
Option<T> or_None(void* _this, FR<Option<T>> other){
	None<T, R, R>* this = (None<T, R, R>*) _this;
	return other.apply();
}
Folder toFolder_ConditionEndLocator(void* _this){
	ConditionEndLocator this = *((ConditionEndLocator*) _this);
	FolderData data;
	data.ConditionEndLocator = this;
	return { FolderVariant.ConditionEndLocatorVariant, data };
}
State apply_ConditionEndLocator(void* _this, State state, char c){
	ConditionEndLocator* this = (ConditionEndLocator*) _this;
	var appended = state.append(c);
	if (c == '(') {
		return appended.enter();
	}
	if (c == ')') {
		if (appended.isLevel()) {
			return appended.advance();
		}
		return appended.exit();
	}
	return appended;
}
StructMember toStructMember_Field(void* _this){
	Field this = *((Field*) _this);
	StructMemberData data;
	data.Field = this;
	return { StructMemberVariant.FieldVariant, data };
}
char* generate_Field(void* _this){
	Field* this = (Field*) _this;
	return Main.generateStatement(1, this->declaration.generate());
}
public Main_Main(void* _this){
	Main* this = (Main*) _this;
	this->structures = new_ArrayList<char*>();
	this->functions = new_ArrayList<char*>();
	this->globals = new_ArrayList<char*>();
	this->counter = 0;
}
auto lambda4(void* _this, auto typeParam){
	return "typename " + typeParam;
}
char* generateTemplateString_Main(void* _this, List<char*> typeParameters){
	Main* this = (Main*) _this;
	char* templateString;
	if (typeParameters.isEmpty()) {
		templateString = "";
	}
	else {
		var typeNames = typeParameters.stream().map(lambda4).collect(Collectors.joining(", ", " < ", ">"));
		templateString = "template " + typeNames + System.lineSeparator();
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
	var ioExceptionOption = new_Main().run();
	if (ioExceptionOption.variant = ?.SomeVariant) {
		//noinspection CallToPrintStackTrace
		value.printStackTrace();
	}
}
char* generateStatement_Main(void* _this, int depth, char* content){
	Main* this = (Main*) _this;
	return generateIndent(depth) + content + ";";
}
char* generateIndent_Main(void* _this, int depth){
	Main* this = (Main*) _this;
	return System.lineSeparator() + "\t".repeat(depth);
}
Option<IOException> run_Main(void* _this){
	Main* this = (Main*) _this;
	var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
	var target = source.resolveSibling("Main.cpp");
	var input = this->readString(source).mapValue(F? { alloc(this), F?Table { compile }});
	return _switch;
}
Option<IOException> writeString_Main(Path target, char* output);
Result<char*, IOException> readString_Main(Path source);
char* compile_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var all = this->compileStatements(input, F? { alloc(this), F?Table { compileRootSegment }});
	var joinedStructures = this->joinStrings("", this->structures);
	var joinedGlobals = this->joinStrings("", this->globals);
	var joinedFunctions = this->joinStrings("", this->functions);
	return joinedStructures + joinedGlobals + joinedFunctions + all;
}
char* joinStrings_Main(void* _this, char* delimiter, List<char*> structures){
	Main* this = (Main*) _this;
	return String.join(delimiter, structures.stream().toList());
}
char* compileStatements_Main(void* _this, char* input, F1R<char*, char*> mapper){
	Main* this = (Main*) _this;
	return this->compileAll(input, mapper, new_EscapedFolder(F? { alloc(this), F?Table { foldStatement }}));
}
char* compileAll_Main(void* _this, char* input, F1R<char*, char*> mapper, Folder folder){
	Main* this = (Main*) _this;
	return this->divide(input, folder).map(F? { alloc(mapper), F?Table { apply }}).collect(Collectors.joining(""));
}
Stream<char*> divide_Main(void* _this, char* input, Folder folder){
	Main* this = (Main*) _this;
	var current = new_State(input);
	while (true) {
		var maybeNext = current.pop();
		if (!(maybeNext.variant = ?.SomeVariant)) {
			break;
		}
		char next;
		next = value;
		current = folder.apply(current, next);
	}
	return current.advance().stream();
}
State foldStatement_Main(void* _this, State current, char next){
	Main* this = (Main*) _this;
	if (next == '/' && current.isLevel()) {
		var maybePeeked = current.peek();
		if (maybePeeked.variant = ?.SomeVariant) {
			var withoutLineCommentPrefix = current.append('/').popAndAppendToOption().orElse(current);
			while (true) {
				var maybeTuple = withoutLineCommentPrefix.popAndAppendToTuple();
				if (maybeTuple.variant = ?.SomeVariant) {
					withoutLineCommentPrefix = tuple.left;
					var right = tuple.right;
					if (right == '\r' || right == '\n') {
						withoutLineCommentPrefix = withoutLineCommentPrefix.advance();
					}
				}
				else {
					return withoutLineCommentPrefix;
				}
			}
		}
	}
	var appended = current.append(next);
	if (next == ';' && appended.isLevel()) {
		return appended.advance();
	}
	if (next == '}' && appended.isShallow()) {
		State appended1;
		if (appended.peek().variant = ?.SomeVariant) {
			appended1 = appended.popAndAppendToOption().orElse(appended);
		}
		else {
			appended1 = appended;
		}
		return appended1.advance().exit();
	}
	if (next == '{' || next == '(') {
		return appended.enter();
	}
	if (next == '}' || next == ')') {
		return appended.exit();
	}
	return appended;
}
auto lambda5(void* _this, auto ()){
	return wrap(stripped);
}
char* compileRootSegment_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (stripped.isEmpty()) {
		return "";
	}
	if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
		return "";
	}
	return this->compileStructure("class", stripped).map(F? { alloc(StructMember), F?Table { generate }}).orElseGet(lambda5);
}
auto lambda6(void* _this, auto slice){
	return !slice.isEmpty();
}
auto lambda7(void* _this, auto (state, character)){
	return new_ValueFolder().apply(state, character);
}
auto lambda8(void* _this, auto slice){
	return this->parseDeclaration(slice, new_ArrayList<char*>());
}
auto lambda9(void* _this, auto (state, character)){
	return new_ValueFolder().apply(state, character);
}
auto lambda10(void* _this, auto slice){
	return !slice.isEmpty();
}
auto lambda11(void* _this, auto (_, next)){
	return next;
}
auto lambda12(void* _this, auto implementee){
	return this->getString(implementee, name, joinedTypeParameters, templateString);
}
auto lambda13(void* _this, auto slice){
	return this->compileClassSegment(slice, name, finalTypeParameters, finalVariants);
}
auto lambda14(void* _this, auto variant){
	return System.lineSeparator() + "\t" + variant + "Variant";
}
auto lambda15(void* _this, auto variant){
	return System.lineSeparator() + "\t" + variant + joinedTypeParameters + " " + variant + ";";
}
auto lambda16(void* _this, auto member){
	return !(member.variant = ?.FunctionDeclarationVariant);
}
Option<StructMember> compileStructure_Main(void* _this, char* type, char* stripped){
	Main* this = (Main*) _this;
	var i = stripped.indexOf(type + " ");
	if (i < 0) {
		return Option.empty();
	}
	var modifiers = stripped.substring(0, i).strip();
	var afterKeyword = stripped.substring(i + (type + " ").length()).strip();
	var i1 = afterKeyword.indexOf("{");
	if (i1 < 0) {
		return Option.empty();
	}
	var beforeContent = afterKeyword.substring(0, i1).strip();
	var withEnd = afterKeyword.substring(i1 + 1).strip();
	if (!withEnd.endsWith("}")) {
		return Option.empty();
	}
	var inputContent = withEnd.substring(0, withEnd.length() - 1);
	List<char*> variants = new_ArrayList<char*>();
	var i2 = beforeContent.indexOf("permits ");
	if (i2 >= 0) {
		var substring1 = beforeContent.substring(i2 + "permits ".length());
		beforeContent = beforeContent.substring(0, i2);
		variants = this->splitValues(substring1);
	}
	List<Type> implementees = new_ArrayList<Type>();
	var i4 = beforeContent.indexOf("implements ");
	if (i4 >= 0) {
		var implementeesString = beforeContent.substring(i4 + "implements ".length());
		beforeContent = beforeContent.substring(0, i4).strip();
		implementees = new_ArrayList<Type>(this->divide(implementeesString, lambda7).map(F? { alloc(String), F?Table { strip }}).filter(lambda6).map(F? { alloc(this), F?Table { parseType }}).toList());
	}
	List<Declaration> recordFields = new_ArrayList<Declaration>();
	if (beforeContent.endsWith(")")) {
		var substring = beforeContent.substring(0, beforeContent.length() - 1);
		var i3 = substring.indexOf("(");
		if (i3 >= 0) {
			beforeContent = substring.substring(0, i3);
			var list = this->divide(substring.substring(i3 + 1), lambda9).map(lambda8).flatMap(F? { alloc(Option), F?Table { stream }}).toList();
			recordFields = new_ArrayList<Declaration>(list);
		}
	}
	List<char*> typeParameters = new_ArrayList<char*>();
	var i3 = beforeContent.indexOf(" < ");
	if (i3 >= 0) {
		var substring1 = beforeContent.substring(i3 + 1).strip();
		if (substring1.endsWith(">")) {
			beforeContent = beforeContent.substring(0, i3);
			var substring = substring1.substring(0, substring1.length() - 1);
			typeParameters = this->splitValues(substring);
		}
	}
	if (!this.isIdentifier(beforeContent)) {
		return Option.empty();
	}
	var modifiersList = new_ArrayList<char*>(Arrays.stream(modifiers.split(Pattern.quote(" "))).map(F? { alloc(String), F?Table { strip }}).filter(lambda10).collect(Collectors.toCollection(F? { alloc(java.util.ArrayList), F?Table { new }})));
	var name = beforeContent.strip();
	var templateString = generateTemplateString(typeParameters);
	var joinedTypeParameters = this->joinTypeParameters(typeParameters);
	var fields = new_StringBuilder();
	var dependencies = new_StringBuilder();
	this->functions = implementees.stream().map(lambda12).reduce(this->functions, F? { alloc(List), F?Table { addLast }}, lambda11);
	var joinedRecordFields = recordFields.stream().map(F? { alloc(Declaration), F?Table { generate }}).map(F? { alloc(this), F?Table { generateStatement }}).collect(Collectors.joining());
	var finalTypeParameters = typeParameters;
	var finalVariants = variants;
	var members = this->divide(inputContent, new_EscapedFolder(F? { alloc(this), F?Table { foldStatement }})).map(lambda13).flatMap(F? { alloc(Option), F?Table { stream }}).toList();
	if (modifiersList.contains("sealed")) {
		var enumFields = variants.stream().map(lambda14).collect(Collectors.joining(","));
		var generatedEnum = "enum " + name + "Variant {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();
		var unionFields = variants.stream().map(lambda15).collect(Collectors.joining());
		var generatedUnion = templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" + System.lineSeparator();
		var s = name + "Variant variant";
		var s1 = name + "Data data";
		var generatedFields = this->generateStatement(s) + this.generateStatement(s1);
		fields = fields.append(generatedFields);
		dependencies.append(generatedEnum).append(generatedUnion);
	}
	else 
	if (type.equals("interface")) {
		var table = this->generateStatement(name + "Table" + joinedTypeParameters + " table");
		var data = this->generateStatement("void* data");
		var tableMembers = members.stream().map(F? { alloc(StructMember), F?Table { generate }}).map(F? { alloc(this), F?Table { generateStatement }}).collect(Collectors.joining(""));
		var vTable = templateString + "struct " + name + "Table {" + tableMembers + System.lineSeparator() + "};" + System.lineSeparator();
		dependencies.append(vTable);
		fields = fields.append(table).append(data);
	}
	else {
		var joinedMembers = members.stream().filter(lambda16).map(F? { alloc(StructMember), F?Table { generate }}).collect(Collectors.joining());
		fields.append(joinedMembers);
	}
	var generated = dependencies + templateString + "struct " + name + " {" + joinedRecordFields + fields + System.lineSeparator() + "};" + System.lineSeparator();
	this->structures = this->structures.addLast(generated);
	return Option.of(new_EmptyStructMember());
}
char* getString_Main(void* _this, Type implementee, char* name, char* joinedTypeParameters, char* templateString){
	Main* this = (Main*) _this;
	var identifier = implementee.toBaseName();
	var variant = identifier + "Variant" + "." + name + "Variant";
	var thisType = name + joinedTypeParameters;
	var s = this->generateStatement(thisType + " this = *((" + thisType + "*) _this)");
	var s1 = this->generateStatement(identifier + "Data" + joinedTypeParameters + " data");
	var s2 = this->generateStatement("data." + name + " = this");
	var s3 = this->generateStatement("return { " + variant + ", data }");
	var conversionFunctionContent = s + s1 + s2 + s3;
	return templateString + implementee.generate() + " to" + identifier + "_" + name + "(void* _this){" + conversionFunctionContent + System.lineSeparator() + "}" + System.lineSeparator();
}
char* joinTypeParameters_Main(void* _this, List<char*> typeParameters){
	Main* this = (Main*) _this;
	char* joinedTypeParameters;
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
	return generateStatement(1, content);
}
auto lambda17(void* _this, auto slice){
	return !slice.isEmpty();
}
List<char*> splitValues_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var segments = input.split(Pattern.quote(","));
	var list = Arrays.stream(segments).map(F? { alloc(String), F?Table { strip }}).filter(lambda17).toList();
	return new_ArrayList<char*>(list);
}
auto lambda18(void* _this, auto i){
	var c = stripped.charAt(i);
	return Character.isLetter(c) || (i != 0 && Character.isDigit(c));
}
boolean isIdentifier_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	return IntStream.range(0, stripped.length()).allMatch(lambda18);
}
auto lambda19(void* _this, auto param){
	return this->parseDeclaration(param, typeParameters);
}
auto lambda20(void* _this, auto slice){
	return !slice.isEmpty();
}
auto lambda21(void* _this, auto (state, character)){
	return new_ValueFolder().apply(state, character);
}
auto lambda22(void* _this, auto name){
	return name + "_" + structName;
}
auto lambda23(void* _this, auto variant){
	return this->generateCase(structName, declaration, variant);
}
auto lambda24(void* _this){
	var returnValueDefinition = this->generateStatement(declaration.type + " _ret");
	var cases = variants.stream().map(lambda23).collect(Collectors.joining());
	return returnValueDefinition + generateIndent(1) + "switch (" + "this.variant" + ") {" + cases + generateIndent(1) + "}" + this.generateStatement("return _ret");
}
Option<StructMember> compileClassSegment_Main(void* _this, char* input, char* structName, List<char*> typeParameters, List<char*> variants){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (stripped.isEmpty()) {
		return Option.empty();
	}
	var maybeEnum = this->compileStructure("enum", input);
	if (maybeEnum.variant = ?.SomeVariant) {
		return maybeEnum;
	}
	var maybeInterface = this->compileStructure("interface", input);
	if (maybeInterface.variant = ?.SomeVariant) {
		return maybeInterface;
	}
	var maybeRecord = this->compileStructure("record", input);
	if (maybeRecord.variant = ?.SomeVariant) {
		return maybeRecord;
	}
	var maybeClass = this->compileStructure("class", input);
	if (maybeClass.variant = ?.SomeVariant) {
		return maybeClass;
	}
	var maybeEnumValues = this->compileEnumValues(input, structName);
	if (maybeEnumValues.variant = ?.SomeVariant) {
		return maybeEnumValues;
	}
	if (stripped.endsWith(";")) {
		var substring = stripped.substring(0, stripped.length() - 1);
		var maybeDeclaration = this->parseDeclaration(substring, new_ArrayList<char*>());
		if (maybeDeclaration.variant = ?.SomeVariant) {
			return new_Some<StructMember>(new_Field(declaration));
		}
	}
	var i = stripped.indexOf("(");
	if (i >= 0) {
		var declarationString = stripped.substring(0, i);
		var substring1 = stripped.substring(i + 1);
		var i1 = substring1.indexOf(")");
		if (i1 >= 0) {
			var parametersString = substring1.substring(0, i1);
			var withBraces = substring1.substring(i1 + 1).strip();
			var collect = this->divide(parametersString, lambda21).map(F? { alloc(String), F?Table { strip }}).filter(lambda20).toList().stream().map(lambda19).flatMap(F? { alloc(Option), F?Table { stream }}).collect(Collectors.toCollection(F? { alloc(java.util.ArrayList), F?Table { new }}));
			List<Declaration> parameters = new_ArrayList<Declaration>(collect);
			var methodDeclaration = this->parseMethodDeclaration(declarationString, structName, typeParameters);
			Option<char*> maybeCompiled = Option.empty();
			if (methodDeclaration.variant = ?.Declaration declaration && declaration.annotations.contains("Actual")Variant) {
				var compiledParameters = parameters.stream().map(F? { alloc(Declaration), F?Table { generate }}).collect(Collectors.joining(", "));
				var modifiedMethodDeclaration = declaration.mapName(lambda22);
				this->functions = this->functions.addLast(modifiedMethodDeclaration.generate() + "(" + compiledParameters + ");" + System.lineSeparator());
				return new_Some<StructMember>(new_EmptyStructMember());
			}
			if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
				var inputContent = withBraces.substring(1, withBraces.length() - 1);
				maybeCompiled = Option.of(this->compileMethodsSegments(inputContent, 1));
			}
			char* outputContent;
			if (methodDeclaration.variant = ?.ConstructorVariant) {
				var compiled = maybeCompiled.orElse("?");
				outputContent = this->generateStatement(structName + " this") + compiled + this.generateStatement("return this");
			}
			else 
			if (methodDeclaration.variant = ?.Declaration declarationVariant) {
				parameters = parameters.addFirst(new_Declaration("void*", "_this"));
				var joinedTypeParameters = this->joinTypeParameters(typeParameters);
				var thisInitialization = this->generateStatement(structName + joinedTypeParameters + "* this = (" + structName + joinedTypeParameters + "*) _this");
				outputContent = thisInitialization + maybeCompiled.orElseGet(lambda24);
			}
			else {
				outputContent = "?";
			}
			var compiledParameters = parameters.stream().map(F? { alloc(Declaration), F?Table { generate }}).collect(Collectors.joining(", "));
			var modifiedMethodDeclaration = _switch;
			var header = modifiedMethodDeclaration.generate() + "(" + compiledParameters + ")";
			var generated = header + "{" + outputContent + System.lineSeparator() + "}" + System.lineSeparator();
			this->functions = this->functions.addLast(generated);
			var parameterTypes = new_ArrayList<char*>(parameters.stream().map(F? { alloc(Declaration), F?Table { type }}).toList());
			return _switch;
		}
	}
	return Option.of(new_Placeholder(stripped));
}
auto lambda25(void* _this, auto input){
	return this->compileMethodSegment(input, indent);
}
char* compileMethodsSegments_Main(void* _this, char* inputContent, int indent){
	Main* this = (Main*) _this;
	return this->compileStatements(inputContent, lambda25);
}
char* generateCase_Main(void* _this, char* structName, Declaration declaration, char* variant){
	Main* this = (Main*) _this;
	return generateIndent(2) + "case " + structName + "Variant." + variant + "Variant:" + generateStatement(3, "_ret = " + declaration.name + "_" + variant + "(&this.data." + variant + ")") + generateStatement(3, "break");
}
auto lambda26(void* _this, auto ()){
	return new_Placeholder(declaration);
}
auto lambda27(void* _this, auto ()){
	return this->parseConstructor(declaration, structName);
}
MethodDeclaration parseMethodDeclaration_Main(void* _this, char* declaration, char* structName, List<char*> typeParameters){
	Main* this = (Main*) _this;
	return this->parseDeclaration(declaration, typeParameters).map(F? { alloc(this), F?Table { toInterface }}).or(lambda27).orElseGet(lambda26);
}
MethodDeclaration toInterface_Main(void* _this, Declaration value){
	Main* this = (Main*) _this;
	return value;
}
Option<MethodDeclaration> parseConstructor_Main(void* _this, char* declaration, char* structName){
	Main* this = (Main*) _this;
	if (declaration.strip().equals(structName)) {
		return Option.of(new_Constructor(structName));
	}
	else {
		return Option.empty();
	}
}
auto lambda28(void* _this, auto slice){
	return !slice.isEmpty();
}
auto lambda29(void* _this, auto (state, character)){
	return new_ValueFolder().apply(state, character);
}
auto lambda30(void* _this, auto option){
	return option.variant = ?.NoneVariant;
}
auto lambda31(void* _this, auto enumValue){
	return this->compileEnumValue(structName, enumValue);
}
Option<StructMember> compileEnumValues_Main(void* _this, char* input, char* structName){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (!stripped.endsWith(";")) {
		return Option.empty();
	}
	var enumValues = this->divide(stripped.substring(0, stripped.length() - 1), lambda29).map(F? { alloc(String), F?Table { strip }}).filter(lambda28).toList();
	if (!enumValues.isEmpty()) {
		var areAnyInvalid = enumValues.stream().map(lambda31).anyMatch(lambda30);
		if (areAnyInvalid) {
			return new_None<StructMember>();
		}
	}
	return Option.of(new_EmptyStructMember());
}
Option<StructMember> compileEnumValue_Main(void* _this, char* structName, char* enumValue){
	Main* this = (Main*) _this;
	if (enumValue.endsWith(")")) {
		var substring = enumValue.substring(0, enumValue.length() - 1);
		var i = substring.indexOf("(");
		if (i >= 0) {
			var name = substring.substring(0, i);
			if (!this.isIdentifier(name)) {
				return Option.empty();
			}
			var substring2 = substring.substring(i + 1);
			var generated = structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" + System.lineSeparator();
			this->globals = this->globals.addLast(generated);
			return Option.of(new_EmptyStructMember());
		}
	}
	return new_None<StructMember>();
}
char* compileMethodSegment_Main(void* _this, char* input, int indent){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (stripped.isEmpty()) {
		return "";
	}
	if (stripped.endsWith(";")) {
		var substring = stripped.substring(0, stripped.length() - 1);
		return generateIndent(indent) + this->compileMethodStatement(substring) + ";";
	}
	var maybeIf = this->compileConditional("if", indent, stripped);
	if (maybeIf.variant = ?.SomeVariant) {
		return result;
	}
	var maybeWhile = this->compileConditional("while", indent, stripped);
	if (maybeWhile.variant = ?.SomeVariant) {
		return result;
	}
	if (stripped.startsWith("else ")) {
		var substring = stripped.substring("else ".length()).strip();
		if (substring.startsWith("{") && substring.endsWith("}")) {
			var substring1 = substring.substring(1, substring.length() - 1);
			return generateIndent(indent) + "else {" + this->compileMethodsSegments(substring1, indent + 1) + generateIndent(indent) + "}";
		}
		else {
			return generateIndent(indent) + "else " + this.compileMethodSegment(substring, indent);
		}
	}
	if (stripped.startsWith("//")) {
		return generateIndent(indent) + stripped;
	}
	return System.lineSeparator() + "\t" + wrap(stripped);
}
auto lambda32(void* _this, auto slice){
	return !slice.isEmpty();
}
Option<char*> compileConditional_Main(void* _this, char* type, int indent, char* input){
	Main* this = (Main*) _this;
	if (input.startsWith(type)) {
		var substring = input.substring(type.length()).strip();
		if (substring.startsWith("(")) {
			var afterConditionStart = substring.substring(1).strip();
			var divisions = this->divide(afterConditionStart, new_EscapedFolder(new_ConditionEndLocator())).map(F? { alloc(String), F?Table { strip }}).filter(lambda32).toList();
			if (divisions.size() < 2) {
				return Option.empty();
			}
			var first = divisions.getFirst();
			var last = this->joinStrings("", new_ArrayList<char*>(divisions.subList(1, divisions.size())));
			if (!first.endsWith(")")) {
				return new_None<char*>();
			}
			var condition = first.substring(0, first.length() - 1);
			if (last.startsWith("{") && last.endsWith("}")) {
				var content = last.substring(1, last.length() - 1);
				return Option.of(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" + this->compileMethodsSegments(content, indent + 1) + generateIndent(indent) + "}");
			}
		}
	}
	return Option.empty();
}
auto lambda33(void* _this, auto ()){
	return wrap(destination);
}
auto lambda34(void* _this, auto ()){
	return this->parseDeclaration(destination, new_ArrayList<char*>()).map(F? { alloc(Declaration), F?Table { generate }});
}
char* compileMethodStatement_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (stripped.equals("break")) {
		return "break";
	}
	if (stripped.startsWith("return ")) {
		return "return " + this.compileExpressionOrPlaceholder(stripped.substring("return ".length()));
	}
	var i = stripped.indexOf("=");
	if (i >= 0) {
		var destination = stripped.substring(0, i);
		var substring1 = stripped.substring(i + 1);
		return this->compileExpression(destination).or(lambda34).orElseGet(lambda33) + " = " + this.compileExpressionOrPlaceholder(substring1);
	}
	var maybeInvokable = this->compileInvokable(stripped);
	if (maybeInvokable.variant = ?.SomeVariant) {
		return value;
	}
	var instance = this->post(stripped, "++");
	if (instance.variant = ?.SomeVariant) {
		return x;
	}
	var instance0 = this->post(stripped, "--");
	if (instance0.variant = ?.SomeVariant) {
		return x;
	}
	var maybeDeclaration = this->parseDeclaration(input, new_ArrayList<char*>());
	if (maybeDeclaration.variant = ?.SomeVariant) {
		return declaration.generate();
	}
	return wrap(stripped);
}
Option<char*> post_Main(void* _this, char* stripped, char* slice){
	Main* this = (Main*) _this;
	if (stripped.endsWith(slice)) {
		var instance = stripped.substring(0, stripped.length() - 2);
		return new_Some<char*>(this->compileExpressionOrPlaceholder(instance) + slice);
	}
	return new_None<char*>();
}
auto lambda35(void* _this, auto ()){
	return wrap(input);
}
char* compileExpressionOrPlaceholder_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	return this->compileExpression(input).orElseGet(lambda35);
}
auto lambda36(void* _this, auto ()){
	return this->compileOperator(stripped, " >= ");
}
auto lambda37(void* _this, auto ()){
	return this->compileOperator(stripped, " || ");
}
auto lambda38(void* _this, auto ()){
	return this->compileOperator(stripped, " && ");
}
auto lambda39(void* _this, auto ()){
	return this->compileOperator(stripped, " - ");
}
auto lambda40(void* _this, auto ()){
	return this->compileOperator(stripped, " + ");
}
auto lambda41(void* _this, auto ()){
	return this->compileOperator(stripped, " < ");
}
auto lambda42(void* _this, auto ()){
	return this->compileOperator(stripped, " != ");
}
Option<char*> compileExpression_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	if (stripped.startsWith("switch ")) {
		return new_Some<char*>("_switch");
	}
	var i2 = stripped.lastIndexOf("::");
	if (i2 >= 0) {
		var substring = stripped.substring(0, i2);
		var name = stripped.substring(i2 + 2).strip();
		if (this->isIdentifier(name)) {
			var compiled = this->compileExpressionOrPlaceholder(substring);
			var functionalInterfaceName = "F?";
			return Option.of(functionalInterfaceName + " { alloc(" + compiled + "), " + functionalInterfaceName + "Table { " + name + " }}");
		}
	}
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return Option.of(stripped);
	}
	var maybeLambda = this->compileLambda(stripped);
	if (maybeLambda.variant = ?.SomeVariant) {
		return maybeLambda;
	}
	var i3 = stripped.indexOf(".variant = ?."Variant);
	if (i3 >= 0) {
		var substring = stripped.substring(0, i3);
		var substring1 = stripped.substring(i3 + ".variant = ?.".length()Variant).strip();
		var maybeInstance = this->compileExpression(substring);
		if (maybeInstance.variant = ?.SomeVariant) {
			var i4 = substring1.indexOf(" < ");
			char* substring2;
			if (i4 >= 0) {
				substring2 = substring1.substring(0, i4);
			}
			else {
				substring2 = substring1;
			}
			return new_Some<char*>(instance + ".variant = ?." + substring2 + "Variant");
		}
	}
	var i = stripped.lastIndexOf(".");
	if (i >= 0) {
		var instanceString = stripped.substring(0, i);
		var memberName = stripped.substring(i + 1).strip();
		if (this->isIdentifier(memberName)) {
			var maybeInstance = this->compileExpression(instanceString);
			if (maybeInstance.variant = ?.SomeVariant) {
				char* instance;
				instance = value;
				char* generated;
				if (instance.equals("this")) {
					generated = "this->" + memberName;
				}
				else {
					generated = instance + "." + memberName;
				}
				return Option.of(generated);
			}
		}
	}
	var maybeInvokable = this->compileInvokable(stripped);
	if (maybeInvokable.variant = ?.SomeVariant) {
		return maybeInvokable;
	}
	var maybeOperator = this->compileOperator(stripped, " == ").or(lambda42).or(lambda41).or(lambda40).or(lambda39).or(lambda38).or(lambda37).or(lambda36);
	if (maybeOperator.variant = ?.SomeVariant) {
		return maybeOperator;
	}
	if (this->isIdentifier(stripped)) {
		return Option.of(stripped);
	}
	if (stripped.startsWith("!")) {
		var substring = stripped.substring(1);
		var maybeInstance = this->compileExpression(substring);
		if (maybeInstance.variant = ?.SomeVariant) {
			return new_Some<char*>("!" + instance);
		}
	}
	if (this->isNumber(stripped)) {
		return Option.of(stripped);
	}
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return Option.of(stripped);
	}
	return Option.empty();
}
auto lambda43(void* _this, auto slice){
	return !slice.isEmpty();
}
auto lambda44(void* _this, auto param){
	return "auto " + param;
}
Option<char*> compileLambda_Main(void* _this, char* stripped){
	Main* this = (Main*) _this;
	var i1 = stripped.indexOf("->");
	if (i1 >= 0) {
		var beforeContent = stripped.substring(0, i1).strip();
		var maybeWithBraces = stripped.substring(i1 + 2).strip();
		List<char*> params;
		if (this->isIdentifier(beforeContent)) {
			params = new_ArrayList<char*>().addLast(beforeContent);
		}
		else 
		if (beforeContent.startsWith("(") && beforeContent.endsWith(")")) {
			var substring = beforeContent.substring(1, beforeContent.length() - 1);
			params = new_ArrayList<char*>(this->divide(substring, new_ValueFolder()).map(F? { alloc(String), F?Table { strip }}).filter(lambda43).toList());
		}
		else {
			return new_None<char*>();
		}
		if (maybeWithBraces.startsWith("{") && maybeWithBraces.endsWith("}")) {
			var content = maybeWithBraces.substring(1, maybeWithBraces.length() - 1);
			var compiled = this->compileMethodsSegments(content, 1);
			var generatedName = this->generateName();
			List<char*> paramList = new_ArrayList<char*>(params.stream().map(lambda44).collect(Collectors.toCollection(F? { alloc(java.util.ArrayList), F?Table { new }})));
			paramList = paramList.addFirst("void* _this");
			var joined = this->joinStrings(", ", paramList);
			this->functions = this->functions.addLast("auto " + generatedName + "(" + joined + "){" + compiled + System.lineSeparator() + "}" + System.lineSeparator());
			return Option.of(generatedName);
		}
		else {
			var generatedName = this->generateName();
			this->functions = this->functions.addLast("auto " + generatedName + "(void* _this, auto " + beforeContent + ")" + "{" + this->generateStatement("return " + this.compileExpressionOrPlaceholder(maybeWithBraces)) + System.lineSeparator() + "}" + System.lineSeparator());
			return Option.of(generatedName);
		}
	}
	return Option.empty();
}
char* generateName_Main(void* _this){
	Main* this = (Main*) _this;
	var generatedName = "lambda" + this.counter;
	this->counter++;
	return generatedName;
}
Option<char*> compileOperator_Main(void* _this, char* input, char* operator){
	Main* this = (Main*) _this;
	if (input.length() < 3) {
		return new_None<char*>();
	}
	if (!input.contains(operator)) {
		return new_None<char*>();
	}
	var i1 = -1;
	var depth = 0;
	var i = 0;
	while (i < input.length() - 1) {
		var c = input.charAt(i);
		if (c == operator.charAt(0)) {
			if (depth == 0) {
				i1 = i;
				break;
			}
		}
		if (c == '(') {
			depth++;
		}
		if (c == ')') {
			depth--;
		}
		i++;
	}
	if (i1 >= 0) {
		var leftString = input.substring(0, i1);
		var right = input.substring(i1 + operator.length());
		if (this->compileExpression(leftString).variant = ?.SomeVariant) {
			if (this->compileExpression(right).variant = ?.SomeVariant) {
				return Option.of(leftCompiled + " " + operator + " " + rightCompiled);
			}
		}
	}
	return Option.empty();
}
Option<char*> compileInvokable_Main(void* _this, char* stripped){
	Main* this = (Main*) _this;
	if (stripped.endsWith(")")) {
		var withoutEnd = stripped.substring(0, stripped.length() - 1);
		var callerStart = this->findCallerStart(withoutEnd);
		if (callerStart >= 0) {
			var callerString = withoutEnd.substring(0, callerStart);
			var arguments = withoutEnd.substring(callerStart + 1);
			var joinedArguments = this->divide(arguments, new_EscapedFolder(new_ValueFolder())).map(F? { alloc(this), F?Table { compileExpressionOrPlaceholder }}).collect(Collectors.joining(", "));
			var maybeCaller = this->compileCaller(callerString);
			if (maybeCaller.variant = ?.SomeVariant) {
				return Option.of(value + "(" + joinedArguments + ")");
			}
		}
	}
	return Option.empty();
}
int findCallerStart_Main(void* _this, char* withoutEnd){
	Main* this = (Main*) _this;
	var callerStart = -1;
	var depth = 0;
	var i = 0;
	while (i < withoutEnd.length()) {
		var c = withoutEnd.charAt(i);
		if (c == '(') {
			if (depth == 0) {
				callerStart = i;
			}
			depth++;
		}
		if (c == ')') {
			depth--;
		}
		i++;
	}
	return callerStart;
}
boolean isNumber_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	if (input.startsWith(" - ")) {
		return this->allDigits(input.substring(1));
	}
	return this->allDigits(input);
}
boolean allDigits_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	return IntStream.range(0, input.length()).mapToObj(F? { alloc(input), F?Table { charAt }}).allMatch(F? { alloc(Character), F?Table { isDigit }});
}
Option<char*> compileCaller_Main(void* _this, char* input){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	var maybeExpression = this->compileExpression(stripped);
	if (maybeExpression.variant = ?.SomeVariant) {
		return maybeExpression;
	}
	if (stripped.startsWith("new ")) {
		var type = stripped.substring("new ".length());
		return Option.of("new_" + this.compileType(type));
	}
	return new_None<char*>();
}
auto lambda45(void* _this, auto slice){
	return slice.substring(1);
}
auto lambda46(void* _this, auto slice){
	return !slice.isEmpty();
}
Option<Declaration> parseDeclaration_Main(void* _this, char* input, List<char*> typeParameters){
	Main* this = (Main*) _this;
	var stripped = input.strip();
	var nameSeparator = stripped.lastIndexOf(" ");
	if (nameSeparator >= 0) {
		var beforeName = stripped.substring(0, nameSeparator).strip();
		var name = stripped.substring(nameSeparator + 1).strip();
		var typeSeparator = this->findTypeSeparator(beforeName);
		if (!this.isIdentifier(name)) {
			return Option.empty();
		}
		if (typeSeparator < 0) {
			var type = this->compileType(beforeName);
			return Option.of(new_Declaration(type, name));
		}
		var beforeType = beforeName.substring(0, typeSeparator).strip();
		var copy = typeParameters;
		if (beforeType.endsWith(">")) {
			var substring = beforeType.substring(0, beforeType.length() - 1);
			var i = substring.indexOf(" < ");
			if (i >= 0) {
				var substring2 = substring.substring(i + 1);
				copy = copy.addAll(this->splitValues(substring2));
				beforeType = substring.substring(0, i);
			}
		}
		List<char*> annotations = new_ArrayList<char*>();
		var i = beforeType.lastIndexOf("\n");
		if (i >= 0) {
			annotations = new_ArrayList<char*>(Arrays.stream(beforeType.substring(0, i).split(Pattern.quote("\n"))).filter(lambda46).map(lambda45).map(F? { alloc(String), F?Table { strip }}).toList());
			beforeType = beforeType.substring(i + 1).strip();
		}
		if (this->isIdentifier(name)) {
			return Option.of(new_Declaration(annotations, copy, Option.of(beforeType), this->compileType(beforeName.substring(typeSeparator + 1)), name));
		}
	}
	return Option.empty();
}
int findTypeSeparator_Main(void* _this, char* beforeName){
	Main* this = (Main*) _this;
	var typeSeparator = -1;
	var depth = 0;
	var i = 0;
	while (i < beforeName.length()) {
		var c = beforeName.charAt(i);
		if (c == ' ' && depth == 0) {
			typeSeparator = i;
		}
		if (c == '<') {
			depth++;
		}
		if (c == '>') {
			depth--;
		}
		i++;
	}
	return typeSeparator;
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
		if (i >= 0) {
			var base = substring.substring(0, i);
			var parameters = substring.substring(i + 1);
			var list = new_ArrayList<Type>(this->divide(parameters, new_ValueFolder()).map(F? { alloc(this), F?Table { parseType }}).toList());
			return new_TemplateType(base, list);
		}
	}
	if (stripped.equals("Character")) {
		return PrimitiveType.Char;
	}
	if (this->isIdentifier(stripped)) {
		return new_Identifier(stripped);
	}
	return new_Placeholder(stripped);
}
