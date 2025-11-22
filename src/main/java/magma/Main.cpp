struct PrimitiveType {
	char* content;/*PrimitiveType(String content) {this.content = content;}*/
};
enum HeadVariant {
	RangeHeadVariant,
	EmptyHeadVariant,
	FlatMapHeadVariant,
	MapHeadVariant,
	SingleHeadVariant
};
template <typename T>
union HeadData {
	RangeHead<T> RangeHead;
	EmptyHead<T> EmptyHead;
	FlatMapHead<T> FlatMapHead;
	MapHead<T> MapHead;
	SingleHead<T> SingleHead;
};
template <typename T>
struct Head {
	HeadVariant variant;
	HeadData<T> data;
};
template <typename T>
struct ListTable {
	Stream<T> (*stream)(void*);
	int (*isEmpty)(void*);
	List<T> (*addLast)(void*, T);
	int (*contains)(void*, T);
	List<T> (*addFirst)(void*, T);
	List<T> (*addAll)(void*, List<T>);
	int (*size)(void*);
	T (*getFirst)(void*);
	List<T> (*subList)(void*, int, int);
	List<T> (*clear)(void*);
};
template <typename T>
struct List {
	ListTable<T> table;
	void* data;
};
struct PathTable {
	Path (*resolveSibling)(void*, char*);
	Option<IOError> (*writeString)(void*, char*);
	Result<char*, IOError> (*readString)(void*);
};
struct Path {
	PathTable table;
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
	OptionData<T> data;
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
	ResultData<T, X> data;
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
	F1RDeclarationVariant,
	PlaceholderVariant
};
union StructMemberData {
	Declaration Declaration;
	EmptyStructMember EmptyStructMember;
	Field Field;
	F1RDeclaration F1RDeclaration;
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
template <typename A, typename B, typename R>
struct F2RTable {
	R (*apply)(void*, A, B);
};
template <typename A, typename B, typename R>
struct F2R {
	F2RTable<A, B, R> table;
	void* data;
};
struct ActualTable {
};
struct Actual {
	ActualTable table;
	void* data;
};
template <typename T, typename C>
struct CollectorTable {
	C (*createInitial)(void*);
	C (*fold)(void*, C, T);
};
template <typename T, typename C>
struct Collector {
	CollectorTable<T, C> table;
	void* data;
};
struct IOError {
	IOException e;
};
struct StringBuilder {
	List<char> list;
};
template <typename T>
struct Stream {
	Head<T> head;
};
struct RangeHead {
	int length;
	int counter;
};
template <typename T>
struct JavaList {
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
struct F1RDeclaration {
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
struct Streams {
};
template <typename T, typename R>
struct MapHead {
	Head<T> head;
	F1R<T, R> mapper;
};
template <typename T>
struct SingleHead {
	T value;
	int retrieved;
};
template <typename T, typename R>
struct FlatMapHead {
	Head<T> head;
	F1R<T, Stream<R>> mapper;
	Option<Stream<R>> maybeCurrent;
};
template <typename T>
struct EmptyHead {
};
template <typename T>
struct AnyMatch {
	Predicate<T> predicate;
};
struct Joiner {
	char* delimiter;
};
template <typename T>
struct ListCollector {
};
struct Paths {
};
struct JavaPath {
	/*java.nio.file.Path*/ path;
};
struct Main {
	List<char*> functionDeclarations;
	List<char*> globals;
	List<char*> structures;
	List<char*> functions;
	int counter;
};
PrimitiveType PrimitiveTypeVoid = new_PrimitiveType("void");
PrimitiveType PrimitiveTypeChar = new_PrimitiveType("char");
PrimitiveType PrimitiveTypeInt = new_PrimitiveType("int");
PrimitiveType new_PrimitiveType(char* content);char* generate_PrimitiveType(void* _ref);char* toBaseName_PrimitiveType(void* _ref);template <typename T>
Option<T> next_Head(void* _ref);template <typename T>
Stream<T> stream_List(void* _ref);template <typename T>
int isEmpty_List(void* _ref);template <typename T>
List<T> addLast_List(void* _ref, T element);template <typename T>
int contains_List(void* _ref, T element);template <typename T>
List<T> addFirst_List(void* _ref, T element);template <typename T>
List<T> addAll_List(void* _ref, List<T> elements);template <typename T>
int size_List(void* _ref);template <typename T>
T getFirst_List(void* _ref);template <typename T>
List<T> subList_List(void* _ref, int start, int end);template <typename T>
List<T> clear_List(void* _ref);Path resolveSibling_Path(void* _ref, char* sibling);Option<IOError> writeString_Path(void* _ref, char* output);Result<char*, IOError> readString_Path(void* _ref);template <typename T>
T apply_FR(void* _ref);template <typename R, typename T>
Option<R> map_Option(void* _ref, F1R<T, R> mapper);template <typename T>
T orElse_Option(void* _ref, T other);template <typename R, typename T>
Option<R> flatMap_Option(void* _ref, F1R<T, Option<R>> mapper);template <typename T>
T orElseGet_Option(void* _ref, FR<T> other);template <typename T>
Stream<T> stream_Option(void* _ref);template <typename T>
Option<T> or_Option(void* _ref, FR<Option<T>> other);template <typename T>
Tuple<int, T> toTuple_Option(void* _ref, FR<T> other);template <typename T0, typename R>
R apply_F1R(void* _ref, T0 value);template <typename R, typename T, typename X>
Result<R, X> mapValue_Result(void* _ref, F1R<T, R> mapper);char* generate_Type(void* _ref);char* toBaseName_Type(void* _ref);char* generate_MethodDeclaration(void* _ref);char* generate_StructMember(void* _ref);State apply_Folder(void* _ref, State state, char character);template <typename A, typename B, typename R>
R apply_F2R(void* _ref, A a, B b);template <typename T, typename C>
C createInitial_Collector(void* _ref);template <typename T, typename C>
C fold_Collector(void* _ref, C c, T t);char* display_IOError(void* _ref);public StringBuilder_StringBuilder(void* _ref);StringBuilder appendChar_StringBuilder(void* _ref, char next);StringBuilder clear_StringBuilder(void* _ref);StringBuilder appendString_StringBuilder(void* _ref, char* chars);char* toString_StringBuilder(void* _ref);template <typename T, typename T>
Stream<T> of_Stream(void* _ref, T value);template <typename T, typename T>
Stream<T> empty_Stream(void* _ref);template <typename R, typename T>
Stream<R> map_Stream(void* _ref, F1R<T, R> mapper);template <typename R, typename T>
R fold_Stream(void* _ref, R initial, F2R<R, T, R> folder);template <typename C, typename T>
C collect_Stream(void* _ref, Collector<T, C> collector);template <typename T>
List<T> toList_Stream(void* _ref);template <typename T>
Stream<T> filter_Stream(void* _ref, Predicate<T> predicate);template <typename R, typename T>
Stream<R> flatMap_Stream(void* _ref, F1R<T, Stream<R>> mapper);public RangeHead_RangeHead(void* _ref, int length);Option<Integer> next_RangeHead(void* _ref);template <typename T>
private JavaList_JavaList(void* _ref, java.util.List<T> nativeList);template <typename T>
public JavaList_JavaList(void* _ref);template <typename T>
JavaList<T> addLast_JavaList(void* _ref, T element);template <typename T>
Stream<T> stream_JavaList(void* _ref);template <typename T>
int isEmpty_JavaList(void* _ref);template <typename T>
int contains_JavaList(void* _ref, T element);template <typename T>
List<T> addFirst_JavaList(void* _ref, T element);template <typename T>
List<T> addAll_JavaList(void* _ref, List<T> elements);template <typename T>
int size_JavaList(void* _ref);template <typename T>
T getFirst_JavaList(void* _ref);template <typename T>
List<T> subList_JavaList(void* _ref, int start, int end);template <typename T>
List<T> clear_JavaList(void* _ref);template <typename R, typename T, typename X>
Result<R, X> mapValue_Err(void* _ref, F1R<T, R> mapper);template <typename R, typename T, typename X>
Result<R, X> mapValue_Ok(void* _ref, F1R<T, R> mapper);public State_State(void* _ref, char* input);int isShallow_State(void* _ref);int isLevel_State(void* _ref);State append_State(void* _ref, char next);Option<char> pop_State(void* _ref);State advance_State(void* _ref);State enter_State(void* _ref);State exit_State(void* _ref);Stream<char*> stream_State(void* _ref);Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref);Option<State> popAndAppendToOption_State(void* _ref);Option<char> peek_State(void* _ref);char* generate_PointerType(void* _ref);char* toBaseName_PointerType(void* _ref);char* generate_TemplateType(void* _ref);char* toBaseName_TemplateType(void* _ref);char* generate_Identifier(void* _ref);char* toBaseName_Identifier(void* _ref);char* generate_Placeholder(void* _ref);char* toBaseName_Placeholder(void* _ref);char* generate_Constructor(void* _ref);public Declaration_Declaration(void* _ref, char* type, char* name);char* generate_Declaration(void* _ref);Declaration mapName_Declaration(void* _ref, F1R<char*, char*> mapper);Declaration mapTypeParameters_Declaration(void* _ref, F1R<List<char*>, List<char*>> mapper);char* generate_F1RDeclaration(void* _ref);char* generate_EmptyStructMember(void* _ref);State apply_EscapedFolder(void* _ref, State state, char next);State apply_ValueFolder(void* _ref, State state, char next);template <typename R, typename T>
Option<R> map_Some(void* _ref, F1R<T, R> mapper);template <typename T>
T orElse_Some(void* _ref, T other);template <typename R, typename T>
Option<R> flatMap_Some(void* _ref, F1R<T, Option<R>> mapper);template <typename T>
T orElseGet_Some(void* _ref, FR<T> other);template <typename T>
Stream<T> stream_Some(void* _ref);template <typename T>
Option<T> or_Some(void* _ref, FR<Option<T>> other);template <typename T>
Tuple<int, T> toTuple_Some(void* _ref, FR<T> other);template <typename R, typename T>
Option<R> map_None(void* _ref, F1R<T, R> mapper);template <typename T>
T orElse_None(void* _ref, T other);template <typename R, typename T>
Option<R> flatMap_None(void* _ref, F1R<T, Option<R>> mapper);template <typename T>
T orElseGet_None(void* _ref, FR<T> other);template <typename T>
Stream<T> stream_None(void* _ref);template <typename T>
Option<T> or_None(void* _ref, FR<Option<T>> other);template <typename T>
Tuple<int, T> toTuple_None(void* _ref, FR<T> other);State apply_ConditionEndLocator(void* _ref, State state, char c);char* generate_Field(void* _ref);template <typename T>
Stream<T> fromObjArray_Streams(void* _ref, T* elements);Stream<char> fromCharArray_Streams(void* _ref, char* array);template <typename T, typename R>
Option<R> next_MapHead(void* _ref);template <typename T>
public SingleHead_SingleHead(void* _ref, T value);template <typename T>
Option<T> next_SingleHead(void* _ref);template <typename T, typename R>
public FlatMapHead_FlatMapHead(void* _ref, Head<T> head, F1R<T, Stream<R>> mapper);template <typename T, typename R>
Option<R> next_FlatMapHead(void* _ref);template <typename T>
Option<T> next_EmptyHead(void* _ref);template <typename T>
int createInitial_AnyMatch(void* _ref);template <typename T>
int fold_AnyMatch(void* _ref, int aBoolean, T t);public Joiner_Joiner(void* _ref);char* createInitial_Joiner(void* _ref);char* fold_Joiner(void* _ref, char* current, char* element);template <typename T>
List<T> createInitial_ListCollector(void* _ref);template <typename T>
List<T> fold_ListCollector(void* _ref, List<T> tList, T t);Path get_Paths(char* first, /*String...*/ more);
Path resolveSibling_JavaPath(void* _ref, char* sibling);Option<IOError> writeString_JavaPath(void* _ref, char* output);Result<char*, IOError> readString_JavaPath(void* _ref);public Main_Main(void* _ref);char* generateTemplateString_Main(void* _ref, List<char*> typeParameters);char* wrap_Main(void* _ref, char* input);void main_Main(void* _ref, char** args);char* generateStatement_Main(void* _ref, int depth, char* content);char* generateIndent_Main(void* _ref, int depth);Option<IOError> run_Main(void* _ref);char* compile_Main(void* _ref, char* input);char* joinStrings_Main(void* _ref, char* delimiter, List<char*> structures);char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper);char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder);Stream<char*> divide_Main(void* _ref, char* input, Folder folder);State foldStatement_Main(void* _ref, State current, char next);char* compileRootSegment_Main(void* _ref, char* input);Option<StructMember> compileStructure_Main(void* _ref, char* type, char* stripped);char* getString_Main(void* _ref, Type implementee, char* name, char* joinedTypeParameters, char* templateString);char* joinTypeParameters_Main(void* _ref, List<char*> typeParameters);char* generateStatement_Main(void* _ref, char* content);List<char*> splitValues_Main(void* _ref, char* input);int isIdentifier_Main(void* _ref, char* input);Option<StructMember> compileClassSegment_Main(void* _ref, char* input, char* structName, List<char*> typeParameters, List<char*> variants);Option<StructMember> compileMethod_Main(void* _ref, char* structName, List<char*> typeParameters, List<char*> variants, char* input);char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent);char* generateCase_Main(void* _ref, char* structName, Declaration declaration, char* variant);MethodDeclaration parseMethodDeclaration_Main(void* _ref, char* declaration, char* structName);MethodDeclaration toInterface_Main(void* _ref, Declaration value);Option<MethodDeclaration> parseConstructor_Main(void* _ref, char* declaration, char* structName);Option<StructMember> compileEnumValues_Main(void* _ref, char* input, char* structName);Option<StructMember> compileEnumValue_Main(void* _ref, char* structName, char* enumValue);char* compileMethodSegment_Main(void* _ref, char* input, int indent);Option<char*> compileConditional_Main(void* _ref, char* type, int indent, char* input);char* compileMethodStatement_Main(void* _ref, char* input);Option<char*> post_Main(void* _ref, char* stripped, char* slice);char* compileExpressionOrPlaceholder_Main(void* _ref, char* input);Option<char*> compileExpression_Main(void* _ref, char* input);Option<char*> compileLambda_Main(void* _ref, char* stripped);char* generateName_Main(void* _ref);Option<char*> compileOperator_Main(void* _ref, char* input, char* operator);Option<char*> compileInvokable_Main(void* _ref, char* stripped);int findCallerStart_Main(void* _ref, char* withoutEnd);int isNumber_Main(void* _ref, char* input);int allDigits_Main(void* _ref, char* input);Option<char*> compileCaller_Main(void* _ref, char* input);Option<Declaration> parseDeclaration_Main(void* _ref, char* input);int findTypeSeparator_Main(void* _ref, char* beforeName);char* compileType_Main(void* _ref, char* input);Type parseType_Main(void* _ref, char* input);Type toType_PrimitiveType(void* _ref){
	PrimitiveType _this = *((PrimitiveType*) _ref);
	TypeData data;
	data.PrimitiveType = _this;
	return { PrimitiveTypeVariant, data };
}
PrimitiveType new_PrimitiveType(char* content){
	PrimitiveType _this;
	_this->content = content;
	return _this;
}
char* generate_PrimitiveType(void* _ref){
	PrimitiveType* _this = (PrimitiveType*) _ref;
	return _this->content;
}
char* toBaseName_PrimitiveType(void* _ref){
	PrimitiveType* _this = (PrimitiveType*) _ref;
	return _this->content;
}
template <typename T>
Option<T> next_Head(void* _ref){
	Head<T>* _this = (Head<T>*) _ref;
	Option<T> _ret;
	switch (_this->variant) {
		case RangeHeadVariant:
			_ret = next_RangeHead(&(_this->data.RangeHead));
			break;
		case EmptyHeadVariant:
			_ret = next_EmptyHead(&(_this->data.EmptyHead));
			break;
		case FlatMapHeadVariant:
			_ret = next_FlatMapHead(&(_this->data.FlatMapHead));
			break;
		case MapHeadVariant:
			_ret = next_MapHead(&(_this->data.MapHead));
			break;
		case SingleHeadVariant:
			_ret = next_SingleHead(&(_this->data.SingleHead));
			break;
	}
	return _ret;
}
template <typename T>
Stream<T> stream_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	Stream<T> _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T>
int isEmpty_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	int _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T>
List<T> addLast_List(void* _ref, T element){
	List<T>* _this = (List<T>*) _ref;
	List<T> _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T>
int contains_List(void* _ref, T element){
	List<T>* _this = (List<T>*) _ref;
	int _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T>
List<T> addFirst_List(void* _ref, T element){
	List<T>* _this = (List<T>*) _ref;
	List<T> _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T>
List<T> addAll_List(void* _ref, List<T> elements){
	List<T>* _this = (List<T>*) _ref;
	List<T> _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T>
int size_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	int _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T>
T getFirst_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	T _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T>
List<T> subList_List(void* _ref, int start, int end){
	List<T>* _this = (List<T>*) _ref;
	List<T> _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T>
List<T> clear_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	List<T> _ret;
	switch (_this->variant) {
	}
	return _ret;
}
Path resolveSibling_Path(void* _ref, char* sibling){
	Path* _this = (Path*) _ref;
	Path _ret;
	switch (_this->variant) {
	}
	return _ret;
}
Option<IOError> writeString_Path(void* _ref, char* output){
	Path* _this = (Path*) _ref;
	Option<IOError> _ret;
	switch (_this->variant) {
	}
	return _ret;
}
Result<char*, IOError> readString_Path(void* _ref){
	Path* _this = (Path*) _ref;
	Result<char*, IOError> _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T>
T apply_FR(void* _ref){
	FR<T>* _this = (FR<T>*) _ref;
	T _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename R, typename T>
Option<R> map_Option(void* _ref, F1R<T, R> mapper){
	Option<T>* _this = (Option<T>*) _ref;
	Option<R> _ret;
	switch (_this->variant) {
		case NoneVariant:
			_ret = map_None(&(_this->data.None));
			break;
		case SomeVariant:
			_ret = map_Some(&(_this->data.Some));
			break;
	}
	return _ret;
}
template <typename T>
T orElse_Option(void* _ref, T other){
	Option<T>* _this = (Option<T>*) _ref;
	T _ret;
	switch (_this->variant) {
		case NoneVariant:
			_ret = orElse_None(&(_this->data.None));
			break;
		case SomeVariant:
			_ret = orElse_Some(&(_this->data.Some));
			break;
	}
	return _ret;
}
template <typename R, typename T>
Option<R> flatMap_Option(void* _ref, F1R<T, Option<R>> mapper){
	Option<T>* _this = (Option<T>*) _ref;
	Option<R> _ret;
	switch (_this->variant) {
		case NoneVariant:
			_ret = flatMap_None(&(_this->data.None));
			break;
		case SomeVariant:
			_ret = flatMap_Some(&(_this->data.Some));
			break;
	}
	return _ret;
}
template <typename T>
T orElseGet_Option(void* _ref, FR<T> other){
	Option<T>* _this = (Option<T>*) _ref;
	T _ret;
	switch (_this->variant) {
		case NoneVariant:
			_ret = orElseGet_None(&(_this->data.None));
			break;
		case SomeVariant:
			_ret = orElseGet_Some(&(_this->data.Some));
			break;
	}
	return _ret;
}
template <typename T>
Stream<T> stream_Option(void* _ref){
	Option<T>* _this = (Option<T>*) _ref;
	Stream<T> _ret;
	switch (_this->variant) {
		case NoneVariant:
			_ret = stream_None(&(_this->data.None));
			break;
		case SomeVariant:
			_ret = stream_Some(&(_this->data.Some));
			break;
	}
	return _ret;
}
template <typename T>
Option<T> or_Option(void* _ref, FR<Option<T>> other){
	Option<T>* _this = (Option<T>*) _ref;
	Option<T> _ret;
	switch (_this->variant) {
		case NoneVariant:
			_ret = or_None(&(_this->data.None));
			break;
		case SomeVariant:
			_ret = or_Some(&(_this->data.Some));
			break;
	}
	return _ret;
}
template <typename T>
Tuple<int, T> toTuple_Option(void* _ref, FR<T> other){
	Option<T>* _this = (Option<T>*) _ref;
	Tuple<int, T> _ret;
	switch (_this->variant) {
		case NoneVariant:
			_ret = toTuple_None(&(_this->data.None));
			break;
		case SomeVariant:
			_ret = toTuple_Some(&(_this->data.Some));
			break;
	}
	return _ret;
}
template <typename T0, typename R>
R apply_F1R(void* _ref, T0 value){
	F1R<T0, R>* _this = (F1R<T0, R>*) _ref;
	R _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename R, typename T, typename X>
Result<R, X> mapValue_Result(void* _ref, F1R<T, R> mapper){
	Result<T, X>* _this = (Result<T, X>*) _ref;
	Result<R, X> _ret;
	switch (_this->variant) {
		case ErrVariant:
			_ret = mapValue_Err(&(_this->data.Err));
			break;
		case OkVariant:
			_ret = mapValue_Ok(&(_this->data.Ok));
			break;
	}
	return _ret;
}
char* generate_Type(void* _ref){
	Type* _this = (Type*) _ref;
	char* _ret;
	switch (_this->variant) {
		case IdentifierVariant:
			_ret = generate_Identifier(&(_this->data.Identifier));
			break;
		case PlaceholderVariant:
			_ret = generate_Placeholder(&(_this->data.Placeholder));
			break;
		case PointerTypeVariant:
			_ret = generate_PointerType(&(_this->data.PointerType));
			break;
		case PrimitiveTypeVariant:
			_ret = generate_PrimitiveType(&(_this->data.PrimitiveType));
			break;
		case TemplateTypeVariant:
			_ret = generate_TemplateType(&(_this->data.TemplateType));
			break;
	}
	return _ret;
}
char* toBaseName_Type(void* _ref){
	Type* _this = (Type*) _ref;
	char* _ret;
	switch (_this->variant) {
		case IdentifierVariant:
			_ret = toBaseName_Identifier(&(_this->data.Identifier));
			break;
		case PlaceholderVariant:
			_ret = toBaseName_Placeholder(&(_this->data.Placeholder));
			break;
		case PointerTypeVariant:
			_ret = toBaseName_PointerType(&(_this->data.PointerType));
			break;
		case PrimitiveTypeVariant:
			_ret = toBaseName_PrimitiveType(&(_this->data.PrimitiveType));
			break;
		case TemplateTypeVariant:
			_ret = toBaseName_TemplateType(&(_this->data.TemplateType));
			break;
	}
	return _ret;
}
char* generate_MethodDeclaration(void* _ref){
	MethodDeclaration* _this = (MethodDeclaration*) _ref;
	char* _ret;
	switch (_this->variant) {
		case ConstructorVariant:
			_ret = generate_Constructor(&(_this->data.Constructor));
			break;
		case DeclarationVariant:
			_ret = generate_Declaration(&(_this->data.Declaration));
			break;
		case PlaceholderVariant:
			_ret = generate_Placeholder(&(_this->data.Placeholder));
			break;
	}
	return _ret;
}
char* generate_StructMember(void* _ref){
	StructMember* _this = (StructMember*) _ref;
	char* _ret;
	switch (_this->variant) {
		case DeclarationVariant:
			_ret = generate_Declaration(&(_this->data.Declaration));
			break;
		case EmptyStructMemberVariant:
			_ret = generate_EmptyStructMember(&(_this->data.EmptyStructMember));
			break;
		case FieldVariant:
			_ret = generate_Field(&(_this->data.Field));
			break;
		case F1RDeclarationVariant:
			_ret = generate_F1RDeclaration(&(_this->data.F1RDeclaration));
			break;
		case PlaceholderVariant:
			_ret = generate_Placeholder(&(_this->data.Placeholder));
			break;
	}
	return _ret;
}
State apply_Folder(void* _ref, State state, char character){
	Folder* _this = (Folder*) _ref;
	State _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename A, typename B, typename R>
R apply_F2R(void* _ref, A a, B b){
	F2R<A, B, R>* _this = (F2R<A, B, R>*) _ref;
	R _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T, typename C>
C createInitial_Collector(void* _ref){
	Collector<T, C>* _this = (Collector<T, C>*) _ref;
	C _ret;
	switch (_this->variant) {
	}
	return _ret;
}
template <typename T, typename C>
C fold_Collector(void* _ref, C c, T t){
	Collector<T, C>* _this = (Collector<T, C>*) _ref;
	C _ret;
	switch (_this->variant) {
	}
	return _ret;
}
char* display_IOError(void* _ref){
	IOError* _this = (IOError*) _ref;
	var writer = new_StringWriter();
	_this->e.printStackTrace(new_PrintWriter(writer));
	return writer.toString();
}
public StringBuilder_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	this(new_JavaList<char>());
}
StringBuilder appendChar_StringBuilder(void* _ref, char next){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder(_this->list.addLast(next));
}
StringBuilder clear_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder(_this->list.clear());
}
StringBuilder appendString_StringBuilder(void* _ref, char* chars){
	StringBuilder* _this = (StringBuilder*) _ref;
	return Streams.fromCharArray(chars.toCharArray()).fold(this, F? { alloc(StringBuilder), F?Table { appendChar }});
}
char* toString_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return _this->list.stream().map(F? { alloc(String), F?Table { valueOf }}).collect(new_Joiner());
}
template <typename T, typename T>
Stream<T> of_Stream(void* _ref, T value){
	Stream<T>* _this = (Stream<T>*) _ref;
	return new_Stream<T>(new_SingleHead<T>(value));
}
template <typename T, typename T>
Stream<T> empty_Stream(void* _ref){
	Stream<T>* _this = (Stream<T>*) _ref;
	return new_Stream<T>(new_EmptyHead<T>());
}
template <typename R, typename T>
Stream<R> map_Stream(void* _ref, F1R<T, R> mapper){
	Stream<T>* _this = (Stream<T>*) _ref;
	return new_Stream<R>(new_MapHead<T, R>(_this->head, mapper));
}
auto lambda0(void* _ref, auto ()){
	return finalCurrent;
}
auto lambda1(void* _ref, auto element){
	return folder.apply(finalCurrent, element);
}
template <typename R, typename T>
R fold_Stream(void* _ref, R initial, F2R<R, T, R> folder){
	Stream<T>* _this = (Stream<T>*) _ref;
	var current = initial;
	while (true) {
		var finalCurrent = current;
		var tuple = _this->head.next().map(lambda1).toTuple(lambda0);
		if (tuple.left) {
			current = tuple.right;
		}
		else {
			return current;
		}
	}
}
template <typename C, typename T>
C collect_Stream(void* _ref, Collector<T, C> collector){
	Stream<T>* _this = (Stream<T>*) _ref;
	return _this->fold(collector.createInitial(), F? { alloc(collector), F?Table { fold }});
}
template <typename T>
List<T> toList_Stream(void* _ref){
	Stream<T>* _this = (Stream<T>*) _ref;
	return _this->collect(new_ListCollector<T>());
}
auto lambda2(void* _ref, auto element){
	if (predicate.test(element)) {
		return new_Stream<T>(new_SingleHead<T>(element));
	}
	return new_Stream<T>(new_EmptyHead<T>());
}
template <typename T>
Stream<T> filter_Stream(void* _ref, Predicate<T> predicate){
	Stream<T>* _this = (Stream<T>*) _ref;
	return _this->flatMap(lambda2);
}
template <typename R, typename T>
Stream<R> flatMap_Stream(void* _ref, F1R<T, Stream<R>> mapper){
	Stream<T>* _this = (Stream<T>*) _ref;
	return new_Stream<R>(new_FlatMapHead<T, R>(_this->head, mapper));
}
Head<Integer> toHead_RangeHead(void* _ref){
	RangeHead _this = *((RangeHead*) _ref);
	HeadData data;
	data.RangeHead = _this;
	return { RangeHeadVariant, data };
}
public RangeHead_RangeHead(void* _ref, int length){
	RangeHead* _this = (RangeHead*) _ref;
	_this->length = length;
	_this->counter = 0;
}
Option<Integer> next_RangeHead(void* _ref){
	RangeHead* _this = (RangeHead*) _ref;
	if (_this->counter < this.length) {
		var value = _this->counter;
		_this->counter++;
		return new_Some<Integer>(value);
	}
	else {
		return new_None<Integer>();
	}
}
template <typename T>
List<T> toList_JavaList(void* _ref){
	JavaList<T> _this = *((JavaList<T>*) _ref);
	ListData<T> data;
	data.JavaList = _this;
	return { JavaListVariant, data };
}
template <typename T>
private JavaList_JavaList(void* _ref, java.util.List<T> nativeList){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	_this->nativeList = new_ArrayList<T>(nativeList);
}
template <typename T>
public JavaList_JavaList(void* _ref){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	this(new_ArrayList<T>());
}
template <typename T>
JavaList<T> addLast_JavaList(void* _ref, T element){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	_this->nativeList.add(element);
	return this;
}
template <typename T>
Stream<T> stream_JavaList(void* _ref){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	return new_Stream<Integer>(new_RangeHead(_this->nativeList.size())).map(F? { alloc(_this->nativeList), F?Table { get }});
}
template <typename T>
int isEmpty_JavaList(void* _ref){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	return _this->nativeList.isEmpty();
}
template <typename T>
int contains_JavaList(void* _ref, T element){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	return _this->nativeList.contains(element);
}
template <typename T>
List<T> addFirst_JavaList(void* _ref, T element){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	_this->nativeList.addFirst(element);
	return this;
}
template <typename T>
List<T> addAll_JavaList(void* _ref, List<T> elements){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	return elements.stream().fold(this, F? { alloc(JavaList), F?Table { addLast }});
}
template <typename T>
int size_JavaList(void* _ref){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	return _this->nativeList.size();
}
template <typename T>
T getFirst_JavaList(void* _ref){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	return _this->nativeList.getFirst();
}
template <typename T>
List<T> subList_JavaList(void* _ref, int start, int end){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	return new_JavaList<T>(_this->nativeList.subList(start, end));
}
template <typename T>
List<T> clear_JavaList(void* _ref){
	JavaList<T>* _this = (JavaList<T>*) _ref;
	_this->nativeList.clear();
	return this;
}
template <typename T, typename X>
Result<T, X> toResult_Err(void* _ref){
	Err<T, X> _this = *((Err<T, X>*) _ref);
	ResultData<T, X> data;
	data.Err = _this;
	return { ErrVariant, data };
}
template <typename R, typename T, typename X>
Result<R, X> mapValue_Err(void* _ref, F1R<T, R> mapper){
	Err<T, X>* _this = (Err<T, X>*) _ref;
	return new_Err<R, X>(_this->error);
}
template <typename T, typename X>
Result<T, X> toResult_Ok(void* _ref){
	Ok<T, X> _this = *((Ok<T, X>*) _ref);
	ResultData<T, X> data;
	data.Ok = _this;
	return { OkVariant, data };
}
template <typename R, typename T, typename X>
Result<R, X> mapValue_Ok(void* _ref, F1R<T, R> mapper){
	Ok<T, X>* _this = (Ok<T, X>*) _ref;
	return new_Ok<R, X>(mapper.apply(_this->value));
}
public State_State(void* _ref, char* input){
	State* _this = (State*) _ref;
	_this->input = input;
	_this->index = 0;
	_this->buffer = new_StringBuilder();
	_this->depth = 0;
	_this->segments = new_JavaList<char*>();
}
int isShallow_State(void* _ref){
	State* _this = (State*) _ref;
	return _this->depth == 1;
}
int isLevel_State(void* _ref){
	State* _this = (State*) _ref;
	return _this->depth == 0;
}
State append_State(void* _ref, char next){
	State* _this = (State*) _ref;
	_this->buffer = _this->buffer.appendChar(next);
	return this;
}
Option<char> pop_State(void* _ref){
	State* _this = (State*) _ref;
	if (_this->index < this.input.length()) {
		var value = _this->input.charAt(_this->index);
		_this->index++;
		return new_Some<char>(value);
	}
	else {
		return new_None<char>();
	}
}
State advance_State(void* _ref){
	State* _this = (State*) _ref;
	_this->segments = _this->segments.addLast(_this->buffer.toString());
	_this->buffer = _this->buffer.clear();
	return this;
}
State enter_State(void* _ref){
	State* _this = (State*) _ref;
	_this->depth = _this->depth + 1;
	return this;
}
State exit_State(void* _ref){
	State* _this = (State*) _ref;
	_this->depth = _this->depth - 1;
	return this;
}
Stream<char*> stream_State(void* _ref){
	State* _this = (State*) _ref;
	return _this->segments.stream();
}
auto lambda3(void* _ref, auto popped){
	var appended = _this->append(popped);
	return new_Tuple<State, char>(appended, popped);
}
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref){
	State* _this = (State*) _ref;
	return _this->pop().map(lambda3);
}
auto lambda4(void* _ref, auto tuple){
	return tuple.left;
}
Option<State> popAndAppendToOption_State(void* _ref){
	State* _this = (State*) _ref;
	return _this->popAndAppendToTuple().map(lambda4);
}
Option<char> peek_State(void* _ref){
	State* _this = (State*) _ref;
	if (_this->index < this.input.length()) {
		return new_Some<char>(_this->input.charAt(_this->index));
	}
	return new_None<char>();
}
Type toType_PointerType(void* _ref){
	PointerType _this = *((PointerType*) _ref);
	TypeData data;
	data.PointerType = _this;
	return { PointerTypeVariant, data };
}
char* generate_PointerType(void* _ref){
	PointerType* _this = (PointerType*) _ref;
	return _this->type.generate() + "*";
}
char* toBaseName_PointerType(void* _ref){
	PointerType* _this = (PointerType*) _ref;
	return _this->type.toBaseName() + "_ptr";
}
Type toType_TemplateType(void* _ref){
	TemplateType _this = *((TemplateType*) _ref);
	TypeData data;
	data.TemplateType = _this;
	return { TemplateTypeVariant, data };
}
char* generate_TemplateType(void* _ref){
	TemplateType* _this = (TemplateType*) _ref;
	var typeArguments = _this->list.stream().map(F? { alloc(Type), F?Table { generate }}).collect(new_Joiner(", "));
	return _this->base + " < " + typeArguments + ">";
}
char* toBaseName_TemplateType(void* _ref){
	TemplateType* _this = (TemplateType*) _ref;
	return _this->base;
}
Type toType_Identifier(void* _ref){
	Identifier _this = *((Identifier*) _ref);
	TypeData data;
	data.Identifier = _this;
	return { IdentifierVariant, data };
}
char* generate_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
char* toBaseName_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
Type toType_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	TypeData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
MethodDeclaration toMethodDeclaration_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	MethodDeclarationData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
StructMember toStructMember_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	StructMemberData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
char* generate_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return wrap(_this->input);
}
char* toBaseName_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return wrap(_this->input);
}
MethodDeclaration toMethodDeclaration_Constructor(void* _ref){
	Constructor _this = *((Constructor*) _ref);
	MethodDeclarationData data;
	data.Constructor = _this;
	return { ConstructorVariant, data };
}
char* generate_Constructor(void* _ref){
	Constructor* _this = (Constructor*) _ref;
	return _this->structName + " new_" + this.structName;
}
MethodDeclaration toMethodDeclaration_Declaration(void* _ref){
	Declaration _this = *((Declaration*) _ref);
	MethodDeclarationData data;
	data.Declaration = _this;
	return { DeclarationVariant, data };
}
StructMember toStructMember_Declaration(void* _ref){
	Declaration _this = *((Declaration*) _ref);
	StructMemberData data;
	data.Declaration = _this;
	return { DeclarationVariant, data };
}
public Declaration_Declaration(void* _ref, char* type, char* name){
	Declaration* _this = (Declaration*) _ref;
	this(new_JavaList<char*>(), new_JavaList<char*>(), new_None<char*>(), type, name);
}
char* generate_Declaration(void* _ref){
	Declaration* _this = (Declaration*) _ref;
	var beforeDeclaration = generateTemplateString(_this->typeParameters());
	return beforeDeclaration + _this->type + " " + this.name;
}
Declaration mapName_Declaration(void* _ref, F1R<char*, char*> mapper){
	Declaration* _this = (Declaration*) _ref;
	return new_Declaration(_this->annotations, _this->typeParameters, _this->maybeBeforeType, _this->type, mapper.apply(_this->name));
}
Declaration mapTypeParameters_Declaration(void* _ref, F1R<List<char*>, List<char*>> mapper){
	Declaration* _this = (Declaration*) _ref;
	return new_Declaration(_this->annotations, mapper.apply(_this->typeParameters), _this->maybeBeforeType, _this->type, _this->name);
}
StructMember toStructMember_F1RDeclaration(void* _ref){
	F1RDeclaration _this = *((F1RDeclaration*) _ref);
	StructMemberData data;
	data.F1RDeclaration = _this;
	return { F1RDeclarationVariant, data };
}
char* generate_F1RDeclaration(void* _ref){
	F1RDeclaration* _this = (F1RDeclaration*) _ref;
	var joinedParameterTypes = "(" + this.parameterTypes.stream().collect(new Joiner(", ")) + ")";
	return _this->type + " (*" + this.name + ")" + joinedParameterTypes;
}
StructMember toStructMember_EmptyStructMember(void* _ref){
	EmptyStructMember _this = *((EmptyStructMember*) _ref);
	StructMemberData data;
	data.EmptyStructMember = _this;
	return { EmptyStructMemberVariant, data };
}
char* generate_EmptyStructMember(void* _ref){
	EmptyStructMember* _this = (EmptyStructMember*) _ref;
	return "";
}
Folder toFolder_EscapedFolder(void* _ref){
	EscapedFolder _this = *((EscapedFolder*) _ref);
	FolderData data;
	data.EscapedFolder = _this;
	return { EscapedFolderVariant, data };
}
auto lambda5(void* _ref, auto tuple){
	if (tuple.right == '\\') {
		return tuple.left.popAndAppendToOption().orElse(tuple.left);
	}
	return tuple.left;
}
State apply_EscapedFolder(void* _ref, State state, char next){
	EscapedFolder* _this = (EscapedFolder*) _ref;
	if (next == '\'') {
		var appended = state.append(next);
		return appended.popAndAppendToTuple().map(lambda5).flatMap(F? { alloc(State), F?Table { popAndAppendToOption }}).orElse(appended);
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
	return _this->folder.apply(state, next);
}
Folder toFolder_ValueFolder(void* _ref){
	ValueFolder _this = *((ValueFolder*) _ref);
	FolderData data;
	data.ValueFolder = _this;
	return { ValueFolderVariant, data };
}
State apply_ValueFolder(void* _ref, State state, char next){
	ValueFolder* _this = (ValueFolder*) _ref;
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
Option<T> toOption_Some(void* _ref){
	Some<T> _this = *((Some<T>*) _ref);
	OptionData<T> data;
	data.Some = _this;
	return { SomeVariant, data };
}
template <typename R, typename T>
Option<R> map_Some(void* _ref, F1R<T, R> mapper){
	Some<T>* _this = (Some<T>*) _ref;
	return new_Some<R>(mapper.apply(_this->value));
}
template <typename T>
T orElse_Some(void* _ref, T other){
	Some<T>* _this = (Some<T>*) _ref;
	return _this->value;
}
template <typename R, typename T>
Option<R> flatMap_Some(void* _ref, F1R<T, Option<R>> mapper){
	Some<T>* _this = (Some<T>*) _ref;
	return mapper.apply(_this->value);
}
template <typename T>
T orElseGet_Some(void* _ref, FR<T> other){
	Some<T>* _this = (Some<T>*) _ref;
	return _this->value;
}
template <typename T>
Stream<T> stream_Some(void* _ref){
	Some<T>* _this = (Some<T>*) _ref;
	return Stream.of(_this->value);
}
template <typename T>
Option<T> or_Some(void* _ref, FR<Option<T>> other){
	Some<T>* _this = (Some<T>*) _ref;
	return this;
}
template <typename T>
Tuple<int, T> toTuple_Some(void* _ref, FR<T> other){
	Some<T>* _this = (Some<T>*) _ref;
	return new_Tuple<int, T>(true, _this->value);
}
template <typename T>
Option<T> toOption_None(void* _ref){
	None<T> _this = *((None<T>*) _ref);
	OptionData<T> data;
	data.None = _this;
	return { NoneVariant, data };
}
template <typename R, typename T>
Option<R> map_None(void* _ref, F1R<T, R> mapper){
	None<T>* _this = (None<T>*) _ref;
	return new_None<R>();
}
template <typename T>
T orElse_None(void* _ref, T other){
	None<T>* _this = (None<T>*) _ref;
	return other;
}
template <typename R, typename T>
Option<R> flatMap_None(void* _ref, F1R<T, Option<R>> mapper){
	None<T>* _this = (None<T>*) _ref;
	return new_None<R>();
}
template <typename T>
T orElseGet_None(void* _ref, FR<T> other){
	None<T>* _this = (None<T>*) _ref;
	return other.apply();
}
template <typename T>
Stream<T> stream_None(void* _ref){
	None<T>* _this = (None<T>*) _ref;
	return Stream.empty();
}
template <typename T>
Option<T> or_None(void* _ref, FR<Option<T>> other){
	None<T>* _this = (None<T>*) _ref;
	return other.apply();
}
template <typename T>
Tuple<int, T> toTuple_None(void* _ref, FR<T> other){
	None<T>* _this = (None<T>*) _ref;
	return new_Tuple<int, T>(false, other.apply());
}
Folder toFolder_ConditionEndLocator(void* _ref){
	ConditionEndLocator _this = *((ConditionEndLocator*) _ref);
	FolderData data;
	data.ConditionEndLocator = _this;
	return { ConditionEndLocatorVariant, data };
}
State apply_ConditionEndLocator(void* _ref, State state, char c){
	ConditionEndLocator* _this = (ConditionEndLocator*) _ref;
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
StructMember toStructMember_Field(void* _ref){
	Field _this = *((Field*) _ref);
	StructMemberData data;
	data.Field = _this;
	return { FieldVariant, data };
}
char* generate_Field(void* _ref){
	Field* _this = (Field*) _ref;
	return Main.generateStatement(1, _this->declaration.generate());
}
auto lambda6(void* _ref, auto index){
	return /*elements[index]*/;
}
template <typename T>
Stream<T> fromObjArray_Streams(void* _ref, T* elements){
	Streams* _this = (Streams*) _ref;
	return new_Stream<Integer>(new_RangeHead(elements.length)).map(lambda6);
}
auto lambda7(void* _ref, auto index){
	return /*array[index]*/;
}
Stream<char> fromCharArray_Streams(void* _ref, char* array){
	Streams* _this = (Streams*) _ref;
	return new_Stream<Integer>(new_RangeHead(array.length)).map(lambda7);
}
template <typename T, typename R>
Head<R> toHead_MapHead(void* _ref){
	MapHead<T, R> _this = *((MapHead<T, R>*) _ref);
	HeadData<T, R> data;
	data.MapHead = _this;
	return { MapHeadVariant, data };
}
template <typename T, typename R>
Option<R> next_MapHead(void* _ref){
	MapHead<T, R>* _this = (MapHead<T, R>*) _ref;
	return _this->head.next().map(_this->mapper);
}
template <typename T>
Head<T> toHead_SingleHead(void* _ref){
	SingleHead<T> _this = *((SingleHead<T>*) _ref);
	HeadData<T> data;
	data.SingleHead = _this;
	return { SingleHeadVariant, data };
}
template <typename T>
public SingleHead_SingleHead(void* _ref, T value){
	SingleHead<T>* _this = (SingleHead<T>*) _ref;
	_this->value = value;
	_this->retrieved = false;
}
template <typename T>
Option<T> next_SingleHead(void* _ref){
	SingleHead<T>* _this = (SingleHead<T>*) _ref;
	if (_this->retrieved) {
		return new_None<T>();
	}
	_this->retrieved = true;
	return new_Some<T>(_this->value);
}
template <typename T, typename R>
Head<R> toHead_FlatMapHead(void* _ref){
	FlatMapHead<T, R> _this = *((FlatMapHead<T, R>*) _ref);
	HeadData<T, R> data;
	data.FlatMapHead = _this;
	return { FlatMapHeadVariant, data };
}
template <typename T, typename R>
public FlatMapHead_FlatMapHead(void* _ref, Head<T> head, F1R<T, Stream<R>> mapper){
	FlatMapHead<T, R>* _this = (FlatMapHead<T, R>*) _ref;
	_this->head = head;
	_this->mapper = mapper;
	_this->maybeCurrent = new_None<Stream<R>>();
}
template <typename T, typename R>
Option<R> next_FlatMapHead(void* _ref){
	FlatMapHead<T, R>* _this = (FlatMapHead<T, R>*) _ref;
	while (true) {
		if (_this->maybeCurrent.variant = ?.SomeVariant) {
			var next = current.head.next();
			if (next.variant = ?.SomeVariant) {
				return next;
			}
		}
		var maybeNext = _this->head.next();
		if (maybeNext.variant = ?.NoneVariant) {
			return new_None<R>();
		}
		_this->maybeCurrent = maybeNext.map(_this->mapper);
	}
}
template <typename T>
Head<T> toHead_EmptyHead(void* _ref){
	EmptyHead<T> _this = *((EmptyHead<T>*) _ref);
	HeadData<T> data;
	data.EmptyHead = _this;
	return { EmptyHeadVariant, data };
}
template <typename T>
Option<T> next_EmptyHead(void* _ref){
	EmptyHead<T>* _this = (EmptyHead<T>*) _ref;
	return new_None<T>();
}
template <typename T>
Collector<T, int> toCollector_AnyMatch(void* _ref){
	AnyMatch<T> _this = *((AnyMatch<T>*) _ref);
	CollectorData<T> data;
	data.AnyMatch = _this;
	return { AnyMatchVariant, data };
}
template <typename T>
int createInitial_AnyMatch(void* _ref){
	AnyMatch<T>* _this = (AnyMatch<T>*) _ref;
	return false;
}
template <typename T>
int fold_AnyMatch(void* _ref, int aBoolean, T t){
	AnyMatch<T>* _this = (AnyMatch<T>*) _ref;
	return aBoolean || this.predicate.test(t);
}
Collector<char*, char*> toCollector_Joiner(void* _ref){
	Joiner _this = *((Joiner*) _ref);
	CollectorData data;
	data.Joiner = _this;
	return { JoinerVariant, data };
}
public Joiner_Joiner(void* _ref){
	Joiner* _this = (Joiner*) _ref;
	this("");
}
char* createInitial_Joiner(void* _ref){
	Joiner* _this = (Joiner*) _ref;
	return "";
}
char* fold_Joiner(void* _ref, char* current, char* element){
	Joiner* _this = (Joiner*) _ref;
	if (current.isEmpty()) {
		return element;
	}
	return current + _this->delimiter + element;
}
template <typename T>
Collector<T, List<T>> toCollector_ListCollector(void* _ref){
	ListCollector<T> _this = *((ListCollector<T>*) _ref);
	CollectorData<T> data;
	data.ListCollector = _this;
	return { ListCollectorVariant, data };
}
template <typename T>
List<T> createInitial_ListCollector(void* _ref){
	ListCollector<T>* _this = (ListCollector<T>*) _ref;
	return new_JavaList<T>();
}
template <typename T>
List<T> fold_ListCollector(void* _ref, List<T> tList, T t){
	ListCollector<T>* _this = (ListCollector<T>*) _ref;
	return tList.addLast(t);
}
Path toPath_JavaPath(void* _ref){
	JavaPath _this = *((JavaPath*) _ref);
	PathData data;
	data.JavaPath = _this;
	return { JavaPathVariant, data };
}
Path resolveSibling_JavaPath(void* _ref, char* sibling){
	JavaPath* _this = (JavaPath*) _ref;
	return new_JavaPath(_this->path.resolveSibling(sibling));
}
Option<IOError> writeString_JavaPath(void* _ref, char* output){
	JavaPath* _this = (JavaPath*) _ref;
	/*try {
				Files.writeString(this.path, output);
				return new None<IOError>();
			}*/
	/*catch (IOException e) {
				return new Some<IOError>(new IOError(e));
			}*/
}
Result<char*, IOError> readString_JavaPath(void* _ref){
	JavaPath* _this = (JavaPath*) _ref;
	/*try {
				return new Ok<String, IOError>(Files.readString(this.path));
			}*/
	/*catch (IOException e) {
				return new Err<String, IOError>(new IOError(e));
			}*/
}
public Main_Main(void* _ref){
	Main* _this = (Main*) _ref;
	_this->structures = new_JavaList<char*>();
	_this->functionDeclarations = new_JavaList<char*>();
	_this->functions = new_JavaList<char*>();
	_this->globals = new_JavaList<char*>();
	_this->counter = 0;
}
auto lambda8(void* _ref, auto typeParam){
	return "typename " + typeParam;
}
char* generateTemplateString_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* templateString;
	if (typeParameters.isEmpty()) {
		templateString = "";
	}
	else {
		var typeNames = typeParameters.stream().map(lambda8).collect(new_Joiner(", "));
		templateString = "template <" + typeNames + ">" + System.lineSeparator();
	}
	return templateString;
}
char* wrap_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	var replaced = input.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
void main_Main(void* _ref, char** args){
	Main* _this = (Main*) _ref;
	var ioExceptionOption = new_Main().run();
	if (ioExceptionOption.variant = ?.SomeVariant) {
		//noinspection CallToPrintStackTrace
		System.err.println(value.display());
	}
}
char* generateStatement_Main(void* _ref, int depth, char* content){
	Main* _this = (Main*) _ref;
	return generateIndent(depth) + content + ";";
}
char* generateIndent_Main(void* _ref, int depth){
	Main* _this = (Main*) _ref;
	return System.lineSeparator() + "\t".repeat(depth);
}
Option<IOError> run_Main(void* _ref){
	Main* _this = (Main*) _ref;
	var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
	var target = source.resolveSibling("Main.cpp");
	var input = source.readString().mapValue(F? { alloc(this), F?Table { compile }});
	return _switch;
}
char* compile_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	var all = _this->compileStatements(input, F? { alloc(this), F?Table { compileRootSegment }});
	var joinedStructures = _this->joinStrings("", _this->structures);
	var joinedGlobals = _this->joinStrings("", _this->globals);
	var joinedFunctionDeclarations = _this->joinStrings("", _this->functionDeclarations);
	var joinedFunctions = _this->joinStrings("", _this->functions);
	return joinedStructures + joinedGlobals + joinedFunctionDeclarations + joinedFunctions + all;
}
char* joinStrings_Main(void* _ref, char* delimiter, List<char*> structures){
	Main* _this = (Main*) _ref;
	return structures.stream().collect(new_Joiner(delimiter));
}
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper){
	Main* _this = (Main*) _ref;
	return _this->compileAll(input, mapper, new_EscapedFolder(F? { alloc(this), F?Table { foldStatement }}));
}
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder){
	Main* _this = (Main*) _ref;
	return _this->divide(input, folder).map(mapper).collect(new_Joiner(""));
}
Stream<char*> divide_Main(void* _ref, char* input, Folder folder){
	Main* _this = (Main*) _ref;
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
State foldStatement_Main(void* _ref, State current, char next){
	Main* _this = (Main*) _ref;
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
auto lambda9(void* _ref, auto ()){
	return wrap(stripped);
}
char* compileRootSegment_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	var stripped = input.strip();
	if (stripped.isEmpty()) {
		return "";
	}
	if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
		return "";
	}
	return _this->compileStructure("class", stripped).map(F? { alloc(StructMember), F?Table { generate }}).orElseGet(lambda9);
}
auto lambda10(void* _ref, auto slice){
	return !slice.isEmpty();
}
auto lambda11(void* _ref, auto (state, character)){
	return new_ValueFolder().apply(state, character);
}
auto lambda12(void* _ref, auto (state, character)){
	return new_ValueFolder().apply(state, character);
}
auto lambda13(void* _ref, auto slice){
	return !slice.isEmpty();
}
auto lambda14(void* _ref, auto implementee){
	return _this->getString(implementee, name, joinedTypeParameters, templateString);
}
auto lambda15(void* _ref, auto slice){
	return _this->compileClassSegment(slice, name, finalTypeParameters, finalVariants);
}
auto lambda16(void* _ref, auto variant){
	return System.lineSeparator() + "\t" + variant + "Variant";
}
auto lambda17(void* _ref, auto variant){
	return System.lineSeparator() + "\t" + variant + joinedTypeParameters + " " + variant + ";";
}
auto lambda18(void* _ref, auto member){
	return !(member.variant = ?.F1RDeclarationVariant);
}
Option<StructMember> compileStructure_Main(void* _ref, char* type, char* stripped){
	Main* _this = (Main*) _ref;
	var i = stripped.indexOf(type + " ");
	if (i < 0) {
		return new_None<StructMember>();
	}
	var modifiers = stripped.substring(0, i).strip();
	var afterKeyword = stripped.substring(i + (type + " ").length()).strip();
	var i1 = afterKeyword.indexOf("{");
	if (i1 < 0) {
		return new_None<StructMember>();
	}
	var beforeContent = afterKeyword.substring(0, i1).strip();
	var withEnd = afterKeyword.substring(i1 + 1).strip();
	if (!withEnd.endsWith("}")) {
		return new_None<StructMember>();
	}
	var inputContent = withEnd.substring(0, withEnd.length() - 1);
	List<char*> variants = new_JavaList<char*>();
	var i2 = beforeContent.indexOf("permits ");
	if (i2 >= 0) {
		var substring1 = beforeContent.substring(i2 + "permits ".length());
		beforeContent = beforeContent.substring(0, i2);
		variants = _this->splitValues(substring1);
	}
	List<Type> implementees = new_JavaList<Type>();
	var i4 = beforeContent.indexOf("implements ");
	if (i4 >= 0) {
		var implementeesString = beforeContent.substring(i4 + "implements ".length());
		beforeContent = beforeContent.substring(0, i4).strip();
		implementees = _this->divide(implementeesString, lambda11).map(F? { alloc(String), F?Table { strip }}).filter(lambda10).map(F? { alloc(this), F?Table { parseType }}).toList();
	}
	List<Declaration> recordFields = new_JavaList<Declaration>();
	if (beforeContent.endsWith(")")) {
		var substring = beforeContent.substring(0, beforeContent.length() - 1);
		var i3 = substring.indexOf("(");
		if (i3 >= 0) {
			beforeContent = substring.substring(0, i3);
			recordFields = _this->divide(substring.substring(i3 + 1), lambda12).map(F? { alloc(this), F?Table { parseDeclaration }}).flatMap(F? { alloc(Option), F?Table { stream }}).toList();
		}
	}
	List<char*> typeParameters = new_JavaList<char*>();
	var i3 = beforeContent.indexOf(" < ");
	if (i3 >= 0) {
		var substring1 = beforeContent.substring(i3 + 1).strip();
		if (substring1.endsWith(">")) {
			beforeContent = beforeContent.substring(0, i3);
			var substring = substring1.substring(0, substring1.length() - 1);
			typeParameters = _this->splitValues(substring);
		}
	}
	if (!this.isIdentifier(beforeContent)) {
		return new_None<StructMember>();
	}
	var modifiersList = Streams.fromObjArray(modifiers.split(Pattern.quote(" "))).map(F? { alloc(String), F?Table { strip }}).filter(lambda13).toList();
	var name = beforeContent.strip();
	var templateString = generateTemplateString(typeParameters);
	var joinedTypeParameters = _this->joinTypeParameters(typeParameters);
	var fields = new_StringBuilder();
	var dependencies = new_StringBuilder();
	_this->functions = implementees.stream().map(lambda14).fold(_this->functions, F? { alloc(List), F?Table { addLast }});
	var joinedRecordFields = recordFields.stream().map(F? { alloc(Declaration), F?Table { generate }}).map(F? { alloc(this), F?Table { generateStatement }}).collect(new_Joiner());
	var finalTypeParameters = typeParameters;
	var finalVariants = variants;
	var members = _this->divide(inputContent, new_EscapedFolder(F? { alloc(this), F?Table { foldStatement }})).map(lambda15).flatMap(F? { alloc(Option), F?Table { stream }}).toList();
	if (modifiersList.contains("sealed")) {
		var enumFields = variants.stream().map(lambda16).collect(new_Joiner(","));
		var generatedEnum = "enum " + name + "Variant {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();
		var unionFields = variants.stream().map(lambda17).collect(new_Joiner());
		var generatedUnion = templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" + System.lineSeparator();
		var s = name + "Variant variant";
		var s1 = name + "Data" + joinedTypeParameters + " data";
		var generatedFields = _this->generateStatement(s) + this.generateStatement(s1);
		fields = fields.appendString(generatedFields);
		dependencies = dependencies.appendString(generatedEnum).appendString(generatedUnion);
	}
	else 
	if (type.equals("interface")) {
		var table = _this->generateStatement(name + "Table" + joinedTypeParameters + " table");
		var data = _this->generateStatement("void* data");
		var tableMembers = members.stream().map(F? { alloc(StructMember), F?Table { generate }}).map(F? { alloc(this), F?Table { generateStatement }}).collect(new_Joiner(""));
		var vTable = templateString + "struct " + name + "Table {" + tableMembers + System.lineSeparator() + "};" + System.lineSeparator();
		dependencies = dependencies.appendString(vTable);
		fields = fields.appendString(table).appendString(data);
	}
	else {
		var joinedMembers = members.stream().filter(lambda18).map(F? { alloc(StructMember), F?Table { generate }}).collect(new_Joiner());
		fields = fields.appendString(joinedMembers);
	}
	var generated = dependencies + templateString + "struct " + name + " {" + joinedRecordFields + fields + System.lineSeparator() + "};" + System.lineSeparator();
	_this->structures = _this->structures.addLast(generated);
	return new_Some<StructMember>(new_EmptyStructMember());
}
char* getString_Main(void* _ref, Type implementee, char* name, char* joinedTypeParameters, char* templateString){
	Main* _this = (Main*) _ref;
	var identifier = implementee.toBaseName();
	var thisType = name + joinedTypeParameters;
	var s = _this->generateStatement(thisType + " _this = *((" + thisType + "*) _ref)");
	var s1 = _this->generateStatement(identifier + "Data" + joinedTypeParameters + " data");
	var s2 = _this->generateStatement("data." + name + " = _this");
	var s3 = _this->generateStatement("return { " + name + "Variant, data }");
	var conversionF1RContent = s + s1 + s2 + s3;
	return templateString + implementee.generate() + " to" + identifier + "_" + name + "(void* _ref){" + conversionF1RContent + System.lineSeparator() + "}" + System.lineSeparator();
}
char* joinTypeParameters_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* joinedTypeParameters;
	if (typeParameters.isEmpty()) {
		joinedTypeParameters = "";
	}
	else {
		joinedTypeParameters = " < " + typeParameters.stream().collect(new_Joiner(", ")) + ">";
	}
	return joinedTypeParameters;
}
char* generateStatement_Main(void* _ref, char* content){
	Main* _this = (Main*) _ref;
	return generateStatement(1, content);
}
auto lambda19(void* _ref, auto slice){
	return !slice.isEmpty();
}
List<char*> splitValues_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	var segments = input.split(Pattern.quote(","));
	var list = Arrays.stream(segments).map(F? { alloc(String), F?Table { strip }}).filter(lambda19).toList();
	return new_JavaList<char*>(list);
}
auto lambda20(void* _ref, auto i){
	var c = stripped.charAt(i);
	return Character.isLetter(c) || (i != 0 && Character.isDigit(c));
}
int isIdentifier_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	var stripped = input.strip();
	return IntStream.range(0, stripped.length()).allMatch(lambda20);
}
Option<StructMember> compileClassSegment_Main(void* _ref, char* input, char* structName, List<char*> typeParameters, List<char*> variants){
	Main* _this = (Main*) _ref;
	var stripped = input.strip();
	if (stripped.isEmpty()) {
		return new_None<StructMember>();
	}
	var maybeEnum = _this->compileStructure("enum", input);
	if (maybeEnum.variant = ?.SomeVariant) {
		return maybeEnum;
	}
	var maybeInterface = _this->compileStructure("interface", input);
	if (maybeInterface.variant = ?.SomeVariant) {
		return maybeInterface;
	}
	var maybeRecord = _this->compileStructure("record", input);
	if (maybeRecord.variant = ?.SomeVariant) {
		return maybeRecord;
	}
	var maybeClass = _this->compileStructure("class", input);
	if (maybeClass.variant = ?.SomeVariant) {
		return maybeClass;
	}
	var maybeEnumValues = _this->compileEnumValues(input, structName);
	if (maybeEnumValues.variant = ?.SomeVariant) {
		return maybeEnumValues;
	}
	if (stripped.endsWith(";")) {
		var substring = stripped.substring(0, stripped.length() - 1);
		var maybeDeclaration = _this->parseDeclaration(substring);
		if (maybeDeclaration.variant = ?.SomeVariant) {
			return new_Some<StructMember>(new_Field(declaration));
		}
	}
	var maybeMethod = _this->compileMethod(structName, typeParameters, variants, stripped);
	if (maybeMethod.variant = ?.SomeVariant) {
		return maybeMethod;
	}
	return new_Some<StructMember>(new_Placeholder(stripped));
}
auto lambda21(void* _ref, auto slice){
	return !slice.isEmpty();
}
auto lambda22(void* _ref, auto (state, character)){
	return new_ValueFolder().apply(state, character);
}
auto lambda23(void* _ref, auto name){
	return name + "_" + structName;
}
auto lambda24(void* _ref, auto variant){
	return _this->generateCase(structName, declaration, variant);
}
auto lambda25(void* _ref){
	var returnValueDefinition = _this->generateStatement(declaration.type + " _ret");
	var cases = variants.stream().map(lambda24).collect(new_Joiner());
	return returnValueDefinition + generateIndent(1) + "switch (" + "_this->variant" + ") {" + cases + generateIndent(1) + "}" + this.generateStatement("return _ret");
}
Option<StructMember> compileMethod_Main(void* _ref, char* structName, List<char*> typeParameters, List<char*> variants, char* input){
	Main* _this = (Main*) _ref;
	var i = input.indexOf("(");
	if (i < 0) {
		return new_None<StructMember>();
	}
	var declarationString = input.substring(0, i);
	var substring1 = input.substring(i + 1);
	var i1 = substring1.indexOf(")");
	if (i1 < 0) {
		return new_None<StructMember>();
	}
	var parametersString = substring1.substring(0, i1);
	var withBraces = substring1.substring(i1 + 1).strip();
	var parameters = _this->divide(parametersString, lambda22).map(F? { alloc(String), F?Table { strip }}).filter(lambda21).toList().stream().map(F? { alloc(this), F?Table { parseDeclaration }}).flatMap(F? { alloc(Option), F?Table { stream }}).toList();
	var methodDeclaration = _this->parseMethodDeclaration(declarationString, structName);
	Option<char*> maybeCompiled = new_None<char*>();
	if (methodDeclaration.variant = ?.Declaration declaration && declaration.annotations.contains("Actual")Variant) {
		var compiledParameters = parameters.stream().map(F? { alloc(Declaration), F?Table { generate }}).collect(new_Joiner(", "));
		var modifiedMethodDeclaration = declaration.mapName(lambda23);
		_this->functionDeclarations = _this->functionDeclarations.addLast(modifiedMethodDeclaration.generate() + "(" + compiledParameters + ");" + System.lineSeparator());
		return new_Some<StructMember>(new_EmptyStructMember());
	}
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		var inputContent = withBraces.substring(1, withBraces.length() - 1);
		maybeCompiled = new_Some<char*>(_this->compileMethodsSegments(inputContent, 1));
	}
	char* outputContent;
	if (methodDeclaration.variant = ?.ConstructorVariant) {
		var compiled = maybeCompiled.orElse("?");
		outputContent = _this->generateStatement(structName + " _this") + compiled + this.generateStatement("return " + "_this");
	}
	else 
	if (methodDeclaration.variant = ?.Declaration declarationVariant) {
		parameters = parameters.addFirst(new_Declaration("void*", "_ref"));
		var joinedTypeParameters = _this->joinTypeParameters(typeParameters);
		var thisInitialization = _this->generateStatement(structName + joinedTypeParameters + "* _this = (" + structName + joinedTypeParameters + "*) _ref");
		outputContent = thisInitialization + maybeCompiled.orElseGet(lambda25);
	}
	else {
		outputContent = "?";
	}
	var compiledParameters = parameters.stream().map(F? { alloc(Declaration), F?Table { generate }}).collect(new_Joiner(", "));
	var modifiedMethodDeclaration = _switch;
	var header = modifiedMethodDeclaration.generate() + "(" + compiledParameters + ")";
	var generated = header + "{" + outputContent + System.lineSeparator() + "}" + System.lineSeparator();
	_this->functionDeclarations = _this->functionDeclarations.addLast(header + ";");
	_this->functions = _this->functions.addLast(generated);
	var parameterTypes = parameters.stream().map(F? { alloc(Declaration), F?Table { type }}).toList();
	return _switch;
}
auto lambda26(void* _ref, auto input){
	return _this->compileMethodSegment(input, indent);
}
char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent){
	Main* _this = (Main*) _ref;
	return _this->compileStatements(inputContent, lambda26);
}
char* generateCase_Main(void* _ref, char* structName, Declaration declaration, char* variant){
	Main* _this = (Main*) _ref;
	return generateIndent(2) + "case " + variant + "Variant:" + generateStatement(3, "_ret = " + declaration.name + "_" + variant + "(&(_this->data." + variant + "))") + generateStatement(3, "break");
}
auto lambda27(void* _ref, auto ()){
	return new_Placeholder(declaration);
}
auto lambda28(void* _ref, auto ()){
	return _this->parseConstructor(declaration, structName);
}
MethodDeclaration parseMethodDeclaration_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	return _this->parseDeclaration(declaration).map(F? { alloc(this), F?Table { toInterface }}).or(lambda28).orElseGet(lambda27);
}
MethodDeclaration toInterface_Main(void* _ref, Declaration value){
	Main* _this = (Main*) _ref;
	return value;
}
Option<MethodDeclaration> parseConstructor_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	if (declaration.strip().equals(structName)) {
		return new_Some<MethodDeclaration>(new_Constructor(structName));
	}
	else {
		return new_None<MethodDeclaration>();
	}
}
auto lambda29(void* _ref, auto slice){
	return !slice.isEmpty();
}
auto lambda30(void* _ref, auto (state, character)){
	return new_ValueFolder().apply(state, character);
}
auto lambda31(void* _ref, auto enumValue){
	return _this->compileEnumValue(structName, enumValue);
}
auto lambda32(void* _ref, auto option){
	return option.variant = ?.NoneVariant;
}
Option<StructMember> compileEnumValues_Main(void* _ref, char* input, char* structName){
	Main* _this = (Main*) _ref;
	var stripped = input.strip();
	if (!stripped.endsWith(";")) {
		return new_None<StructMember>();
	}
	var enumValues = _this->divide(stripped.substring(0, stripped.length() - 1), lambda30).map(F? { alloc(String), F?Table { strip }}).filter(lambda29).toList();
	if (!enumValues.isEmpty()) {
		var optionStream = enumValues.stream().map(lambda31);
		var areAnyInvalid = /*
					(boolean) optionStream.collect(new AnyMatch<Option<StructMember>>(option -> option instanceof None<StructMember>))*/;
		if (areAnyInvalid) {
			return new_None<StructMember>();
		}
	}
	return new_Some<StructMember>(new_EmptyStructMember());
}
Option<StructMember> compileEnumValue_Main(void* _ref, char* structName, char* enumValue){
	Main* _this = (Main*) _ref;
	if (enumValue.endsWith(")")) {
		var substring = enumValue.substring(0, enumValue.length() - 1);
		var i = substring.indexOf("(");
		if (i >= 0) {
			var name = substring.substring(0, i);
			if (!this.isIdentifier(name)) {
				return new_None<StructMember>();
			}
			var substring2 = substring.substring(i + 1);
			var generated = structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" + System.lineSeparator();
			_this->globals = _this->globals.addLast(generated);
			return new_Some<StructMember>(new_EmptyStructMember());
		}
	}
	return new_None<StructMember>();
}
char* compileMethodSegment_Main(void* _ref, char* input, int indent){
	Main* _this = (Main*) _ref;
	var stripped = input.strip();
	if (stripped.isEmpty()) {
		return "";
	}
	if (stripped.endsWith(";")) {
		var substring = stripped.substring(0, stripped.length() - 1);
		return generateIndent(indent) + _this->compileMethodStatement(substring) + ";";
	}
	var maybeIf = _this->compileConditional("if", indent, stripped);
	if (maybeIf.variant = ?.SomeVariant) {
		return result;
	}
	var maybeWhile = _this->compileConditional("while", indent, stripped);
	if (maybeWhile.variant = ?.SomeVariant) {
		return result;
	}
	if (stripped.startsWith("else ")) {
		var substring = stripped.substring("else ".length()).strip();
		if (substring.startsWith("{") && substring.endsWith("}")) {
			var substring1 = substring.substring(1, substring.length() - 1);
			return generateIndent(indent) + "else {" + _this->compileMethodsSegments(substring1, indent + 1) + generateIndent(indent) + "}";
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
auto lambda33(void* _ref, auto slice){
	return !slice.isEmpty();
}
Option<char*> compileConditional_Main(void* _ref, char* type, int indent, char* input){
	Main* _this = (Main*) _ref;
	if (input.startsWith(type)) {
		var substring = input.substring(type.length()).strip();
		if (substring.startsWith("(")) {
			var afterConditionStart = substring.substring(1).strip();
			var divisions = _this->divide(afterConditionStart, new_EscapedFolder(new_ConditionEndLocator())).map(F? { alloc(String), F?Table { strip }}).filter(lambda33).toList();
			if (divisions.size() < 2) {
				return new_None<char*>();
			}
			var first = divisions.getFirst();
			var last = _this->joinStrings("", divisions.subList(1, divisions.size()));
			if (!first.endsWith(")")) {
				return new_None<char*>();
			}
			var condition = first.substring(0, first.length() - 1);
			if (last.startsWith("{") && last.endsWith("}")) {
				var content = last.substring(1, last.length() - 1);
				return new_Some<char*>(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" + _this->compileMethodsSegments(content, indent + 1) + generateIndent(indent) + "}");
			}
		}
	}
	return new_None<char*>();
}
auto lambda34(void* _ref, auto ()){
	return wrap(destination);
}
auto lambda35(void* _ref, auto ()){
	return _this->parseDeclaration(destination).map(F? { alloc(Declaration), F?Table { generate }});
}
char* compileMethodStatement_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
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
		return _this->compileExpression(destination).or(lambda35).orElseGet(lambda34) + " = " + this.compileExpressionOrPlaceholder(substring1);
	}
	var maybeInvokable = _this->compileInvokable(stripped);
	if (maybeInvokable.variant = ?.SomeVariant) {
		return value;
	}
	var instance = _this->post(stripped, "++");
	if (instance.variant = ?.SomeVariant) {
		return x;
	}
	var instance0 = _this->post(stripped, "--");
	if (instance0.variant = ?.SomeVariant) {
		return x;
	}
	var maybeDeclaration = _this->parseDeclaration(input);
	if (maybeDeclaration.variant = ?.SomeVariant) {
		return declaration.generate();
	}
	return wrap(stripped);
}
Option<char*> post_Main(void* _ref, char* stripped, char* slice){
	Main* _this = (Main*) _ref;
	if (stripped.endsWith(slice)) {
		var instance = stripped.substring(0, stripped.length() - 2);
		return new_Some<char*>(_this->compileExpressionOrPlaceholder(instance) + slice);
	}
	return new_None<char*>();
}
auto lambda36(void* _ref, auto ()){
	return wrap(input);
}
char* compileExpressionOrPlaceholder_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return _this->compileExpression(input).orElseGet(lambda36);
}
auto lambda37(void* _ref, auto ()){
	return _this->compileOperator(stripped, " >= ");
}
auto lambda38(void* _ref, auto ()){
	return _this->compileOperator(stripped, " || ");
}
auto lambda39(void* _ref, auto ()){
	return _this->compileOperator(stripped, " && ");
}
auto lambda40(void* _ref, auto ()){
	return _this->compileOperator(stripped, " - ");
}
auto lambda41(void* _ref, auto ()){
	return _this->compileOperator(stripped, " + ");
}
auto lambda42(void* _ref, auto ()){
	return _this->compileOperator(stripped, " < ");
}
auto lambda43(void* _ref, auto ()){
	return _this->compileOperator(stripped, " != ");
}
Option<char*> compileExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	var stripped = input.strip();
	if (stripped.startsWith("switch ")) {
		return new_Some<char*>("_switch");
	}
	var i2 = stripped.lastIndexOf("::");
	if (i2 >= 0) {
		var substring = stripped.substring(0, i2);
		var name = stripped.substring(i2 + 2).strip();
		if (_this->isIdentifier(name)) {
			var compiled = _this->compileExpressionOrPlaceholder(substring);
			var functionalInterfaceName = "F?";
			return new_Some<char*>(functionalInterfaceName + " { alloc(" + compiled + "), " + functionalInterfaceName + "Table { " + name + " }}");
		}
	}
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return new_Some<char*>(stripped);
	}
	var maybeLambda = _this->compileLambda(stripped);
	if (maybeLambda.variant = ?.SomeVariant) {
		return maybeLambda;
	}
	var i3 = stripped.indexOf(".variant = ?."Variant);
	if (i3 >= 0) {
		var substring = stripped.substring(0, i3);
		var substring1 = stripped.substring(i3 + ".variant = ?.".length()Variant).strip();
		var maybeInstance = _this->compileExpression(substring);
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
		if (_this->isIdentifier(memberName)) {
			var maybeInstance = _this->compileExpression(instanceString);
			if (maybeInstance.variant = ?.SomeVariant) {
				char* instance;
				instance = value;
				char* generated;
				if (instance.equals("this")) {
					generated = "_this->" + memberName;
				}
				else {
					generated = instance + "." + memberName;
				}
				return new_Some<char*>(generated);
			}
		}
	}
	var maybeInvokable = _this->compileInvokable(stripped);
	if (maybeInvokable.variant = ?.SomeVariant) {
		return maybeInvokable;
	}
	var maybeOperator = _this->compileOperator(stripped, " == ").or(lambda43).or(lambda42).or(lambda41).or(lambda40).or(lambda39).or(lambda38).or(lambda37);
	if (maybeOperator.variant = ?.SomeVariant) {
		return maybeOperator;
	}
	if (_this->isIdentifier(stripped)) {
		return new_Some<char*>(stripped);
	}
	if (stripped.startsWith("!")) {
		var substring = stripped.substring(1);
		var maybeInstance = _this->compileExpression(substring);
		if (maybeInstance.variant = ?.SomeVariant) {
			return new_Some<char*>("!" + instance);
		}
	}
	if (_this->isNumber(stripped)) {
		return new_Some<char*>(stripped);
	}
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return new_Some<char*>(stripped);
	}
	return new_None<char*>();
}
auto lambda44(void* _ref, auto slice){
	return !slice.isEmpty();
}
auto lambda45(void* _ref, auto param){
	return "auto " + param;
}
Option<char*> compileLambda_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	var i1 = stripped.indexOf("->");
	if (i1 >= 0) {
		var beforeContent = stripped.substring(0, i1).strip();
		var maybeWithBraces = stripped.substring(i1 + 2).strip();
		List<char*> params;
		if (_this->isIdentifier(beforeContent)) {
			params = new_JavaList<char*>().addLast(beforeContent);
		}
		else 
		if (beforeContent.startsWith("(") && beforeContent.endsWith(")")) {
			var substring = beforeContent.substring(1, beforeContent.length() - 1);
			params = _this->divide(substring, new_ValueFolder()).map(F? { alloc(String), F?Table { strip }}).filter(lambda44).toList();
		}
		else {
			return new_None<char*>();
		}
		if (maybeWithBraces.startsWith("{") && maybeWithBraces.endsWith("}")) {
			var content = maybeWithBraces.substring(1, maybeWithBraces.length() - 1);
			var compiled = _this->compileMethodsSegments(content, 1);
			var generatedName = _this->generateName();
			var paramList = params.stream().map(lambda45).toList().addFirst("void* _ref");
			var joined = _this->joinStrings(", ", paramList);
			_this->functions = _this->functions.addLast("auto " + generatedName + "(" + joined + "){" + compiled + System.lineSeparator() + "}" + System.lineSeparator());
			return new_Some<char*>(generatedName);
		}
		else {
			var generatedName = _this->generateName();
			_this->functions = _this->functions.addLast("auto " + generatedName + "(void* _ref, auto " + beforeContent + ")" + "{" + _this->generateStatement("return " + this.compileExpressionOrPlaceholder(maybeWithBraces)) + System.lineSeparator() + "}" + System.lineSeparator());
			return new_Some<char*>(generatedName);
		}
	}
	return new_None<char*>();
}
char* generateName_Main(void* _ref){
	Main* _this = (Main*) _ref;
	var generatedName = "lambda" + this.counter;
	_this->counter++;
	return generatedName;
}
Option<char*> compileOperator_Main(void* _ref, char* input, char* operator){
	Main* _this = (Main*) _ref;
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
		if (_this->compileExpression(leftString).variant = ?.SomeVariant) {
			if (_this->compileExpression(right).variant = ?.SomeVariant) {
				return new_Some<char*>(leftCompiled + " " + operator + " " + rightCompiled);
			}
		}
	}
	return new_None<char*>();
}
Option<char*> compileInvokable_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	if (stripped.endsWith(")")) {
		var withoutEnd = stripped.substring(0, stripped.length() - 1);
		var callerStart = _this->findCallerStart(withoutEnd);
		if (callerStart >= 0) {
			var callerString = withoutEnd.substring(0, callerStart);
			var arguments = withoutEnd.substring(callerStart + 1);
			var joinedArguments = _this->divide(arguments, new_EscapedFolder(new_ValueFolder())).map(F? { alloc(this), F?Table { compileExpressionOrPlaceholder }}).collect(new_Joiner(", "));
			var maybeCaller = _this->compileCaller(callerString);
			if (maybeCaller.variant = ?.SomeVariant) {
				return new_Some<char*>(value + "(" + joinedArguments + ")");
			}
		}
	}
	return new_None<char*>();
}
int findCallerStart_Main(void* _ref, char* withoutEnd){
	Main* _this = (Main*) _ref;
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
int isNumber_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	if (input.startsWith(" - ")) {
		return _this->allDigits(input.substring(1));
	}
	return _this->allDigits(input);
}
int allDigits_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return IntStream.range(0, input.length()).mapToObj(F? { alloc(input), F?Table { charAt }}).allMatch(F? { alloc(Character), F?Table { isDigit }});
}
Option<char*> compileCaller_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	var stripped = input.strip();
	var maybeExpression = _this->compileExpression(stripped);
	if (maybeExpression.variant = ?.SomeVariant) {
		return maybeExpression;
	}
	if (stripped.startsWith("new ")) {
		var type = stripped.substring("new ".length());
		return new_Some<char*>("new_" + this.compileType(type));
	}
	return new_None<char*>();
}
auto lambda46(void* _ref, auto slice){
	return slice.substring(1);
}
auto lambda47(void* _ref, auto slice){
	return !slice.isEmpty();
}
Option<Declaration> parseDeclaration_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	var stripped = input.strip();
	var nameSeparator = stripped.lastIndexOf(" ");
	if (nameSeparator >= 0) {
		var beforeName = stripped.substring(0, nameSeparator).strip();
		var name = stripped.substring(nameSeparator + 1).strip();
		var typeSeparator = _this->findTypeSeparator(beforeName);
		if (!this.isIdentifier(name)) {
			return new_None<Declaration>();
		}
		if (typeSeparator < 0) {
			var type = _this->compileType(beforeName);
			return new_Some<Declaration>(new_Declaration(type, name));
		}
		var beforeType = beforeName.substring(0, typeSeparator).strip();
		List<char*> copy = new_JavaList<char*>();
		if (beforeType.endsWith(">")) {
			var substring = beforeType.substring(0, beforeType.length() - 1);
			var i = substring.indexOf(" < ");
			if (i >= 0) {
				var substring2 = substring.substring(i + 1);
				copy = _this->splitValues(substring2);
				beforeType = substring.substring(0, i);
			}
		}
		List<char*> annotations = new_JavaList<char*>();
		var i = beforeType.lastIndexOf("\n");
		if (i >= 0) {
			annotations = new_JavaList<char*>(Arrays.stream(beforeType.substring(0, i).split(Pattern.quote("\n"))).filter(lambda47).map(lambda46).map(F? { alloc(String), F?Table { strip }}).toList());
			beforeType = beforeType.substring(i + 1).strip();
		}
		if (_this->isIdentifier(name)) {
			return new_Some<Declaration>(new_Declaration(annotations, copy, new_Some<char*>(beforeType), _this->compileType(beforeName.substring(typeSeparator + 1)), name));
		}
	}
	return new_None<Declaration>();
}
int findTypeSeparator_Main(void* _ref, char* beforeName){
	Main* _this = (Main*) _ref;
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
char* compileType_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return _this->parseType(input).generate();
}
Type parseType_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	var stripped = input.strip();
	if (stripped.equals("boolean") || stripped.equals("Boolean")) {
		return PrimitiveType.Int;
	}
	if (stripped.equals("void")) {
		return PrimitiveType.Void;
	}
	if (stripped.endsWith("[]")) {
		var slice = stripped.substring(0, stripped.length() - 2);
		var type = _this->parseType(slice);
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
			var list = _this->divide(parameters, new_ValueFolder()).map(F? { alloc(this), F?Table { parseType }}).toList();
			return new_TemplateType(base, list);
		}
	}
	if (stripped.equals("Character")) {
		return PrimitiveType.Char;
	}
	if (_this->isIdentifier(stripped)) {
		return new_Identifier(stripped);
	}
	return new_Placeholder(stripped);
}
