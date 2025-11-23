struct CPrimitiveType {
	char* content;
};
struct JPrimitiveType {/*Int, Void, Boolean, String, Char, Var*/
};
template <typename T>
struct HeadTable {
	Option<T> (*next)(void*);
};
template <typename T>
struct Head {
	HeadTable<T> table;
	void* data;
};
template <typename T>
struct ListTable {
	Stream<T> (*iter)(void*);
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
enum CTypeVariant {
	IdentifierVariant,
	PlaceholderVariant,
	CPointerTypeVariant,
	CPrimitiveTypeVariant,
	CTemplateTypeVariant
};
union CTypeData {
	Identifier Identifier;
	Placeholder Placeholder;
	CPointerType CPointerType;
	CPrimitiveType CPrimitiveType;
	CTemplateType CTemplateType;
};
struct CType {
	CTypeVariant variant;
	CTypeData data;
};
enum JMethodDeclarationVariant {
	JConstructorVariant,
	JDeclarationVariant,
	PlaceholderVariant
};
union JMethodDeclarationData {
	JConstructor JConstructor;
	JDeclaration JDeclaration;
	Placeholder Placeholder;
};
struct JMethodDeclaration {
	JMethodDeclarationVariant variant;
	JMethodDeclarationData data;
};
enum CStructMemberVariant {
	EmptyStructMemberVariant,
	CFieldVariant,
	F1RDeclarationVariant,
	PlaceholderVariant
};
union CStructMemberData {
	EmptyStructMember EmptyStructMember;
	CField CField;
	F1RDeclaration F1RDeclaration;
	Placeholder Placeholder;
};
struct CStructMember {
	CStructMemberVariant variant;
	CStructMemberData data;
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
struct IOErrorTable {
	char* (*display)(void*);
};
struct IOError {
	IOErrorTable table;
	void* data;
};
struct CFunctionDeclarationTable {
	CFunctionDeclaration (*mapTypeParameters)(void*, F1R<List<char*>, List<char*>>);
	CFunctionDeclaration (*mapName)(void*, F1R<char*, char*>);
	char* (*generate)(void*);
};
struct CFunctionDeclaration {
	CFunctionDeclarationTable table;
	void* data;
};
struct CAssignableTable {
	char* (*generate)(void*);
};
struct CAssignable {
	CAssignableTable table;
	void* data;
};
enum JTypeVariant {
	IdentifierVariant,
	JArrayTypeVariant,
	JGenericTypeVariant,
	JPrimitiveTypeVariant,
	PlaceholderVariant
};
union JTypeData {
	Identifier Identifier;
	JArrayType JArrayType;
	JGenericType JGenericType;
	JPrimitiveType JPrimitiveType;
	Placeholder Placeholder;
};
struct JType {
	JTypeVariant variant;
	JTypeData data;
};
enum JAssignableVariant {
	JDeclarationVariant,
	JExpressionVariant,
	JExpressionWrapperVariant,
	PlaceholderVariant
};
union JAssignableData {
	JDeclaration JDeclaration;
	JExpression JExpression;
	JExpressionWrapper JExpressionWrapper;
	Placeholder Placeholder;
};
struct JAssignable {
	JAssignableVariant variant;
	JAssignableData data;
};
struct StringBuilders {
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
struct Lists {
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
struct CPointerType {
	CType type;
};
struct CTemplateType {
	char* base;
	List<CType> list;
};
struct Identifier {
	char* value;
};
struct Placeholder {
	char* input;
};
struct JConstructor {
	char* type;
};
struct JDeclaration {
	List<char*> annotations;
	List<char*> typeParameters;
	Option<char*> maybeBeforeType;
	JType type;
	char* name;
};
struct F1RDeclaration {
	CType type;
	char* name;
	List<CType> parameterTypes;
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
struct CField {
	CDeclaration declaration;
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
	F1R<T, int> predicate;
};
struct Joiner {
	char* delimiter;
};
template <typename T>
struct ListCollector {
};
struct Paths {
};
struct CDeclaration {
	List<char*> typeParameters;
	CType type;
	char* name;
};
struct JExpressionWrapper {
	char* content;
};
struct CExpressionWrapper {
	char* content;
};
struct JArrayType {
	JType type;
};
struct JGenericType {
	char* base;
	List<JType> typeArguments;
};
struct CPointerAccess {
	CExpression instance;
	char* fieldName;
};
struct CFieldAccess {
	CExpression instance;
	char* fieldName;
};
struct JMemberAccess {
	JExpression instance;
	char* memberName;
};
struct Main {/*private interface CExpression extends CAssignable {}*//*private List<List<JDeclaration>> environment = new JavaList<List<JDeclaration>>*/
	List<char*> functionDeclarations;
	List<char*> globals;
	List<char*> structures;
	List<char*> functions;
	int counter;
};
CPrimitiveType CPrimitiveTypeVoid = new_CPrimitiveType("void");
CPrimitiveType CPrimitiveTypeChar = new_CPrimitiveType("char");
CPrimitiveType CPrimitiveTypeInt = new_CPrimitiveType("int");
CPrimitiveType<> new_CPrimitiveType(char* content);
char* generate_CPrimitiveType(void* _ref);
char* toBaseName_CPrimitiveType(void* _ref);
template <typename T>
Option<T> next_Head(void* _ref);
template <typename T>
Stream<T> iter_List(void* _ref);
template <typename T>
int isEmpty_List(void* _ref);
template <typename T>
List<T> addLast_List(void* _ref, T element);
template <typename T>
int contains_List(void* _ref, T element);
template <typename T>
List<T> addFirst_List(void* _ref, T element);
template <typename T>
List<T> addAll_List(void* _ref, List<T> elements);
template <typename T>
int size_List(void* _ref);
template <typename T>
T getFirst_List(void* _ref);
template <typename T>
List<T> subList_List(void* _ref, int start, int end);
template <typename T>
List<T> clear_List(void* _ref);
Path resolveSibling_Path(void* _ref, char* sibling);
Option<IOError> writeString_Path(void* _ref, char* output);
Result<char*, IOError> readString_Path(void* _ref);
template <typename T>
T apply_FR(void* _ref);
template <typename R, typename T>
Option<R> map_Option(void* _ref, F1R<T, R> mapper);
template <typename T>
T orElse_Option(void* _ref, T other);
template <typename R, typename T>
Option<R> flatMap_Option(void* _ref, F1R<T, Option<R>> mapper);
template <typename T>
T orElseGet_Option(void* _ref, FR<T> other);
template <typename T>
Stream<T> stream_Option(void* _ref);
template <typename T>
Option<T> or_Option(void* _ref, FR<Option<T>> other);
template <typename T>
Tuple<int, T> toTuple_Option(void* _ref, FR<T> other);
template <typename T0, typename R>
R apply_F1R(void* _ref, T0 value);
template <typename R, typename T, typename X>
Result<R, X> mapValue_Result(void* _ref, F1R<T, R> mapper);
char* generate_CType(void* _ref);
char* toBaseName_CType(void* _ref);
char* generate_CStructMember(void* _ref);
State apply_Folder(void* _ref, State state, char character);
template <typename A, typename B, typename R>
R apply_F2R(void* _ref, A a, B b);
template <typename T, typename C>
C createInitial_Collector(void* _ref);
template <typename T, typename C>
C fold_Collector(void* _ref, C c, T t);
char* display_IOError(void* _ref);
CFunctionDeclaration mapTypeParameters_CFunctionDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper);
CFunctionDeclaration mapName_CFunctionDeclaration(void* _ref, F1R<char*, char*> mapper);
char* generate_CFunctionDeclaration(void* _ref);
char* generate_CAssignable(void* _ref);
CAssignable toCAssignable_Main(void* _ref);
StringBuilder empty_StringBuilders(void* _ref);
StringBuilder appendChar_StringBuilder(void* _ref, char next);
StringBuilder clear_StringBuilder(void* _ref);
StringBuilder appendString_StringBuilder(void* _ref, char* chars);
char* toString_StringBuilder(void* _ref);
template <typename T, typename T>
Stream<T> of_Stream(void* _ref, T value);
template <typename T, typename T>
Stream<T> empty_Stream(void* _ref);
template <typename R, typename T>
Stream<R> map_Stream(void* _ref, F1R<T, R> mapper);
template <typename R, typename T>
R fold_Stream(void* _ref, R initial, F2R<R, T, R> folder);
template <typename C, typename T>
C collect_Stream(void* _ref, Collector<T, C> collector);
template <typename T>
List<T> toList_Stream(void* _ref);
template <typename T>
Stream<T> filter_Stream(void* _ref, F1R<T, int> predicate);
template <typename R, typename T>
Stream<R> flatMap_Stream(void* _ref, F1R<T, Stream<R>> mapper);
template <typename T>
Option<T> next_Stream(void* _ref);
RangeHead<> new_RangeHead(int length);
Option<int> next_RangeHead(void* _ref);
template <typename T>
List<T> empty_Lists();
template <typename T>
List<T> of_Lists(void* _ref, /*T...*/ elements);
template <typename R, typename T, typename X>
Result<R, X> mapValue_Err(void* _ref, F1R<T, R> mapper);
template <typename R, typename T, typename X>
Result<R, X> mapValue_Ok(void* _ref, F1R<T, R> mapper);
State<> new_State(char* input);
int isShallow_State(void* _ref);
int isLevel_State(void* _ref);
State append_State(void* _ref, char next);
Option<char> pop_State(void* _ref);
State advance_State(void* _ref);
State enter_State(void* _ref);
State exit_State(void* _ref);
Stream<char*> stream_State(void* _ref);
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref);
Option<State> popAndAppendToOption_State(void* _ref);
Option<char> peek_State(void* _ref);
char* generate_CPointerType(void* _ref);
char* toBaseName_CPointerType(void* _ref);
char* generate_CTemplateType(void* _ref);
char* toBaseName_CTemplateType(void* _ref);
char* generate_Identifier(void* _ref);
char* toString_Identifier(void* _ref);
char* toBaseName_Identifier(void* _ref);
CType toCType_Identifier(void* _ref);
CExpression toCExpression_Identifier(void* _ref);
char* generate_Placeholder(void* _ref);
char* toBaseName_Placeholder(void* _ref);
CAssignable toCAssignable_Placeholder(void* _ref);
CType toCType_Placeholder(void* _ref);
JDeclaration<> new_JDeclaration(JType type, char* name);
JDeclaration mapName_JDeclaration(void* _ref, F1R<char*, char*> mapper);
CDeclaration toCDeclaration_JDeclaration(void* _ref);
CAssignable toCAssignable_JDeclaration(void* _ref);
JDeclaration withType_JDeclaration(void* _ref, JType type);
char* generate_F1RDeclaration(void* _ref);
char* generate_EmptyStructMember(void* _ref);
State apply_EscapedFolder(void* _ref, State state, char next);
State apply_ValueFolder(void* _ref, State state, char next);
template <typename R, typename T>
Option<R> map_Some(void* _ref, F1R<T, R> mapper);
template <typename T>
T orElse_Some(void* _ref, T other);
template <typename R, typename T>
Option<R> flatMap_Some(void* _ref, F1R<T, Option<R>> mapper);
template <typename T>
T orElseGet_Some(void* _ref, FR<T> other);
template <typename T>
Stream<T> stream_Some(void* _ref);
template <typename T>
Option<T> or_Some(void* _ref, FR<Option<T>> other);
template <typename T>
Tuple<int, T> toTuple_Some(void* _ref, FR<T> other);
template <typename R, typename T>
Option<R> map_None(void* _ref, F1R<T, R> mapper);
template <typename T>
T orElse_None(void* _ref, T other);
template <typename R, typename T>
Option<R> flatMap_None(void* _ref, F1R<T, Option<R>> mapper);
template <typename T>
T orElseGet_None(void* _ref, FR<T> other);
template <typename T>
Stream<T> stream_None(void* _ref);
template <typename T>
Option<T> or_None(void* _ref, FR<Option<T>> other);
template <typename T>
Tuple<int, T> toTuple_None(void* _ref, FR<T> other);
State apply_ConditionEndLocator(void* _ref, State state, char c);
char* generate_CField(void* _ref);
template <typename T>
Stream<T> fromObjArray_Streams(void* _ref, T* elements);
Stream<char> fromCharArray_Streams(void* _ref, char* array);
template <typename T, typename R>
Option<R> next_MapHead(void* _ref);
template <typename T>
SingleHead<T> new_SingleHead(T value);
template <typename T>
Option<T> next_SingleHead(void* _ref);
template <typename T, typename R>
FlatMapHead<T, R> new_FlatMapHead(Head<T> head, F1R<T, Stream<R>> mapper);
template <typename T, typename R>
Option<R> next_FlatMapHead(void* _ref);
template <typename T>
Option<T> next_EmptyHead(void* _ref);
template <typename T>
int createInitial_AnyMatch(void* _ref);
template <typename T>
int fold_AnyMatch(void* _ref, int aBoolean, T t);
Joiner<> new_Joiner();
char* createInitial_Joiner(void* _ref);
char* fold_Joiner(void* _ref, char* current, char* element);
template <typename T>
List<T> createInitial_ListCollector(void* _ref);
template <typename T>
List<T> fold_ListCollector(void* _ref, List<T> tList, T t);
Path get_Paths(char* first, /*String...*/ more);
CDeclaration<> new_CDeclaration(CType type, char* name);
CFunctionDeclaration mapName_CDeclaration(void* _ref, F1R<char*, char*> mapper);
CFunctionDeclaration mapTypeParameters_CDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper);
char* generate_CDeclaration(void* _ref);
CExpression toCExpression_JExpressionWrapper(void* _ref);
CAssignable toCAssignable_JExpressionWrapper(void* _ref);
char* generate_CExpressionWrapper(void* _ref);
CType toCType_JArrayType(void* _ref);
CType toCType_JGenericType(void* _ref);
char* generate_CPointerAccess(void* _ref);
char* generate_CFieldAccess(void* _ref);
CExpression toCExpression_JMemberAccess(void* _ref);
/*private List<List<JDeclaration>> environment = new JavaList<List<JDeclaration>>*/();
Main<> new_Main();
char* generateTemplateString_Main(void* _ref, List<char*> typeParameters);
char* wrap_Main(void* _ref, char* input);
void main_Main(void* _ref, char** args);
char* generateStatement_Main(void* _ref, int depth, char* content);
char* generateIndent_Main(void* _ref, int depth);
CType transformType_Main(void* _ref, JType jType);
CType transformPrimitiveType_Main(void* _ref, JPrimitiveType type);
Option<IOError> run_Main(void* _ref);
char* compile_Main(void* _ref, char* input);
char* joinStrings_Main(void* _ref, char* delimiter, List<char*> structures);
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper);
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder);
Stream<char*> divide_Main(void* _ref, char* input, Folder folder);
State foldStatement_Main(void* _ref, State current, char next);
char* compileRootSegment_Main(void* _ref, char* input);
Option<CStructMember> compileStructure_Main(void* _ref, char* type, char* stripped);
char* getString_Main(void* _ref, CType implementee, char* name, char* joinedTypeParameters, char* templateString);
char* joinTypeParameters_Main(void* _ref, List<char*> typeParameters);
char* generateStatement_Main(void* _ref, char* content);
List<char*> splitValues_Main(void* _ref, char* input);
int isIdentifier_Main(void* _ref, char* input);
Option<CStructMember> compileClassSegment_Main(void* _ref, char* input, char* structName, List<char*> typeParameters, List<char*> variants);
Option<CStructMember> compileMethod_Main(void* _ref, char* structName, List<char*> typeParameters, List<char*> variants, char* input);
CType toConstructorReturnType_Main(void* _ref, char* base, List<char*> typeParameters);
char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent);
char* generateCase_Main(void* _ref, JDeclaration declaration, char* variant);
JMethodDeclaration parseMethodDeclaration_Main(void* _ref, char* declaration, char* structName);
JMethodDeclaration toInterface_Main(void* _ref, JDeclaration value);
Option<JMethodDeclaration> parseConstructor_Main(void* _ref, char* declaration, char* structName);
Option<CStructMember> compileEnumValues_Main(void* _ref, char* input, char* structName);
Option<CStructMember> compileEnumValue_Main(void* _ref, char* structName, char* enumValue);
char* compileMethodSegment_Main(void* _ref, char* input, int indent);
Option<char*> compileConditional_Main(void* _ref, char* type, int indent, char* input);
char* compileMethodStatement_Main(void* _ref, char* input);
Option<char*> compileAssignment_Main(void* _ref, char* stripped);
CAssignable transformAssignable_Main(void* _ref, JAssignable assignable, JExpression source);
JType resolveExpression_Main(void* _ref, JExpression source);
JAssignable parseAssignable_Main(void* _ref, char* input);
Option<char*> post_Main(void* _ref, char* stripped, char* slice);
char* compileExpressionOrPlaceholder_Main(void* _ref, char* input);
Option<CExpression> parseCExpression_Main(void* _ref, char* input);
Option<JExpression> parseExpression_Main(void* _ref, char* input);
Option<char*> compileLambda_Main(void* _ref, char* input);
char* generateName_Main(void* _ref);
Option<char*> compileOperator_Main(void* _ref, char* input, char* operator);
Option<char*> compileInvokable_Main(void* _ref, char* stripped);
int findCallerStart_Main(void* _ref, char* withoutEnd);
int isNumber_Main(void* _ref, char* input);
int allDigits_Main(void* _ref, char* input);
Option<char*> compileCaller_Main(void* _ref, char* input);
Option<JDeclaration> parseDeclaration_Main(void* _ref, char* input);
List<char*> collectAnnotations_Main(void* _ref, char* input);
int findTypeSeparator_Main(void* _ref, char* beforeName);
char* compileType_Main(void* _ref, char* input);
JType parseType_Main(void* _ref, char* input);
CType toCType_CPrimitiveType(void* _ref){
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	CTypeData data;
	data.CPrimitiveType = _this;
	return { CPrimitiveTypeVariant, data };
}
CPrimitiveType<> new_CPrimitiveType(char* content){
	CPrimitiveType _this;
	(*_this)content = content;
	return _this;
}
char* generate_CPrimitiveType(void* _ref){
	CPrimitiveType* _this = (CPrimitiveType*) _ref;
	return (*_this)content;
}
char* toBaseName_CPrimitiveType(void* _ref){
	CPrimitiveType* _this = (CPrimitiveType*) _ref;
	return (*_this)content;
}
JType toJType_JPrimitiveType(void* _ref){
	JPrimitiveType _this = *((JPrimitiveType*) _ref);
	JTypeData data;
	data.JPrimitiveType = _this;
	return { JPrimitiveTypeVariant, data };
}
template <typename T>
Option<T> next_Head(void* _ref){
	Head<T>* _this = (Head<T>*) _ref;
	return _this->table.next(_this->data);
}
template <typename T>
Stream<T> iter_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.iter(_this->data);
}
template <typename T>
int isEmpty_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.isEmpty(_this->data);
}
template <typename T>
List<T> addLast_List(void* _ref, T element){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.addLast(_this->data, element);
}
template <typename T>
int contains_List(void* _ref, T element){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.contains(_this->data, element);
}
template <typename T>
List<T> addFirst_List(void* _ref, T element){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.addFirst(_this->data, element);
}
template <typename T>
List<T> addAll_List(void* _ref, List<T> elements){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.addAll(_this->data, elements);
}
template <typename T>
int size_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.size(_this->data);
}
template <typename T>
T getFirst_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.getFirst(_this->data);
}
template <typename T>
List<T> subList_List(void* _ref, int start, int end){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.subList(_this->data, start, end);
}
template <typename T>
List<T> clear_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.clear(_this->data);
}
Path resolveSibling_Path(void* _ref, char* sibling){
	Path* _this = (Path*) _ref;
	return _this->table.resolveSibling(_this->data, sibling);
}
Option<IOError> writeString_Path(void* _ref, char* output){
	Path* _this = (Path*) _ref;
	return _this->table.writeString(_this->data, output);
}
Result<char*, IOError> readString_Path(void* _ref){
	Path* _this = (Path*) _ref;
	return _this->table.readString(_this->data);
}
template <typename T>
T apply_FR(void* _ref){
	FR<T>* _this = (FR<T>*) _ref;
	return _this->table.apply(_this->data);
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
	return _this->table.apply(_this->data, value);
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
char* generate_CType(void* _ref){
	CType* _this = (CType*) _ref;
	char* _ret;
	switch (_this->variant) {
		case IdentifierVariant:
			_ret = generate_Identifier(&(_this->data.Identifier));
			break;
		case PlaceholderVariant:
			_ret = generate_Placeholder(&(_this->data.Placeholder));
			break;
		case CPointerTypeVariant:
			_ret = generate_CPointerType(&(_this->data.CPointerType));
			break;
		case CPrimitiveTypeVariant:
			_ret = generate_CPrimitiveType(&(_this->data.CPrimitiveType));
			break;
		case CTemplateTypeVariant:
			_ret = generate_CTemplateType(&(_this->data.CTemplateType));
			break;
	}
	return _ret;
}
char* toBaseName_CType(void* _ref){
	CType* _this = (CType*) _ref;
	char* _ret;
	switch (_this->variant) {
		case IdentifierVariant:
			_ret = toBaseName_Identifier(&(_this->data.Identifier));
			break;
		case PlaceholderVariant:
			_ret = toBaseName_Placeholder(&(_this->data.Placeholder));
			break;
		case CPointerTypeVariant:
			_ret = toBaseName_CPointerType(&(_this->data.CPointerType));
			break;
		case CPrimitiveTypeVariant:
			_ret = toBaseName_CPrimitiveType(&(_this->data.CPrimitiveType));
			break;
		case CTemplateTypeVariant:
			_ret = toBaseName_CTemplateType(&(_this->data.CTemplateType));
			break;
	}
	return _ret;
}
char* generate_CStructMember(void* _ref){
	CStructMember* _this = (CStructMember*) _ref;
	char* _ret;
	switch (_this->variant) {
		case EmptyStructMemberVariant:
			_ret = generate_EmptyStructMember(&(_this->data.EmptyStructMember));
			break;
		case CFieldVariant:
			_ret = generate_CField(&(_this->data.CField));
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
	return _this->table.apply(_this->data, state, character);
}
template <typename A, typename B, typename R>
R apply_F2R(void* _ref, A a, B b){
	F2R<A, B, R>* _this = (F2R<A, B, R>*) _ref;
	return _this->table.apply(_this->data, a, b);
}
template <typename T, typename C>
C createInitial_Collector(void* _ref){
	Collector<T, C>* _this = (Collector<T, C>*) _ref;
	return _this->table.createInitial(_this->data);
}
template <typename T, typename C>
C fold_Collector(void* _ref, C c, T t){
	Collector<T, C>* _this = (Collector<T, C>*) _ref;
	return _this->table.fold(_this->data, c, t);
}
char* display_IOError(void* _ref){
	IOError* _this = (IOError*) _ref;
	return _this->table.display(_this->data);
}
CFunctionDeclaration mapTypeParameters_CFunctionDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return (*_this);
}
CFunctionDeclaration mapName_CFunctionDeclaration(void* _ref, F1R<char*, char*> mapper){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return (*_this);
}
char* generate_CFunctionDeclaration(void* _ref){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return _this->table.generate(_this->data);
}
char* generate_CAssignable(void* _ref){
	CAssignable* _this = (CAssignable*) _ref;
	return _this->table.generate(_this->data);
}
CAssignable toCAssignable_Main(void* _ref){
	Main* _this = (Main*) _ref;
	return (*_this)toCExpression();
	/*}

		CExpression toCExpression()*/;
}
StringBuilder empty_StringBuilders(void* _ref){
	StringBuilders* _this = (StringBuilders*) _ref;
	return new_StringBuilder(Listsempty());
}
StringBuilder appendChar_StringBuilder(void* _ref, char next){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder((*_this)listaddLast(next));
}
StringBuilder clear_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder((*_this)listclear());
}
StringBuilder appendString_StringBuilder(void* _ref, char* chars){
	StringBuilder* _this = (StringBuilder*) _ref;
	return StreamsfromCharArray(charstoCharArray())fold((*_this), F? { alloc(StringBuilder), F?Table { appendChar }});
}
char* toString_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return (*_this)listiter()map(F? { alloc(String), F?Table { valueOf }})collect(new_Joiner());
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
	return new_Stream<R>(new_MapHead<T, R>((*_this)head, mapper));
}
auto lambda0(void* _ref, auto ()){
	return finalCurrent;
}
auto lambda1(void* _ref, auto element){
	return folderapply(finalCurrent, element);
}
template <typename R, typename T>
R fold_Stream(void* _ref, R initial, F2R<R, T, R> folder){
	Stream<T>* _this = (Stream<T>*) _ref;
	R current = initial;
	while (true) {
		/**/ finalCurrent = current;
		/*JExpressionWrapper[content=(*_this)headnext()map(lambda1)toTuple(lambda0)]*/ tuple = (*_this)headnext()map(lambda1)toTuple(lambda0);
		if (tupleleft) 
			current = tupleright;
		return current;
	}
}
template <typename C, typename T>
C collect_Stream(void* _ref, Collector<T, C> collector){
	Stream<T>* _this = (Stream<T>*) _ref;
	return (*_this)fold(collectorcreateInitial(), F? { alloc(collector), F?Table { fold }});
}
template <typename T>
List<T> toList_Stream(void* _ref){
	Stream<T>* _this = (Stream<T>*) _ref;
	return (*_this)collect(new_ListCollector<T>());
}
auto lambda2(void* _ref, auto element){
	if (predicateapply(element)) 
		return new_Stream<T>(new_SingleHead<T>(element));
	return new_Stream<T>(new_EmptyHead<T>());
}
template <typename T>
Stream<T> filter_Stream(void* _ref, F1R<T, int> predicate){
	Stream<T>* _this = (Stream<T>*) _ref;
	return (*_this)flatMap(lambda2);
}
template <typename R, typename T>
Stream<R> flatMap_Stream(void* _ref, F1R<T, Stream<R>> mapper){
	Stream<T>* _this = (Stream<T>*) _ref;
	return new_Stream<R>(new_FlatMapHead<T, R>((*_this)head, mapper));
}
template <typename T>
Option<T> next_Stream(void* _ref){
	Stream<T>* _this = (Stream<T>*) _ref;
	return (*_this)headnext();
}
Head<int> toHead_RangeHead(void* _ref){
	RangeHead _this = *((RangeHead*) _ref);
	HeadData data;
	data.RangeHead = _this;
	return { RangeHeadVariant, data };
}
RangeHead<> new_RangeHead(int length){
	RangeHead _this;
	(*_this)length = length;
	(*_this)counter = 0;
	return _this;
}
Option<int> next_RangeHead(void* _ref){
	RangeHead* _this = (RangeHead*) _ref;
	if ((*_this)counter < (*_this)length) {
		/*JMemberAccess[instance=JExpressionWrapper[content=(*_this)], memberName=counter]*/ value = (*_this)counter;
		(*_this)counter++;
		return new_Some<int>(value);
	}
	/*else return new None<Integer>()*/;
}
template <typename T>
List<T> of_Lists(void* _ref, /*T...*/ elements){
	Lists* _this = (Lists*) _ref;
	return StreamsfromObjArray(elements)collect(new_ListCollector<T>());
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
	return new_Err<R, X>((*_this)error);
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
	return new_Ok<R, X>(mapperapply((*_this)value));
}
State<> new_State(char* input){
	State _this;
	(*_this)input = input;
	(*_this)index = 0;
	(*_this)buffer = StringBuildersempty();
	(*_this)depth = 0;
	(*_this)segments = Listsempty();
	return _this;
}
int isShallow_State(void* _ref){
	State* _this = (State*) _ref;
	return (*_this)depth == 1;
}
int isLevel_State(void* _ref){
	State* _this = (State*) _ref;
	return (*_this)depth == 0;
}
State append_State(void* _ref, char next){
	State* _this = (State*) _ref;
	(*_this)buffer = (*_this)bufferappendChar(next);
	return (*_this);
}
Option<char> pop_State(void* _ref){
	State* _this = (State*) _ref;
	if ((*_this)index < (*_this)inputlength()) {
		/*JExpressionWrapper[content=(*_this)inputcharAt((*_this)index)]*/ value = (*_this)inputcharAt((*_this)index);
		(*_this)index++;
		return new_Some<char>(value);
	}
	/*else return new None<Character>()*/;
}
State advance_State(void* _ref){
	State* _this = (State*) _ref;
	(*_this)segments = (*_this)segmentsaddLast((*_this)buffertoString());
	(*_this)buffer = (*_this)bufferclear();
	return (*_this);
}
State enter_State(void* _ref){
	State* _this = (State*) _ref;
	(*_this)depth = (*_this)depth + 1;
	return (*_this);
}
State exit_State(void* _ref){
	State* _this = (State*) _ref;
	(*_this)depth = (*_this)depth - 1;
	return (*_this);
}
Stream<char*> stream_State(void* _ref){
	State* _this = (State*) _ref;
	return (*_this)segmentsiter();
}
auto lambda3(void* _ref, auto popped){
	/*JExpressionWrapper[content=(*_this)append(popped)]*/ appended = (*_this)append(popped);
	return new_Tuple<State, char>(appended, popped);
}
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref){
	State* _this = (State*) _ref;
	return (*_this)pop()map(lambda3);
}
auto lambda4(void* _ref, auto tuple){
	return tupleleft;
}
Option<State> popAndAppendToOption_State(void* _ref){
	State* _this = (State*) _ref;
	return (*_this)popAndAppendToTuple()map(lambda4);
}
Option<char> peek_State(void* _ref){
	State* _this = (State*) _ref;
	if ((*_this)index < (*_this)inputlength()) 
		return new_Some<char>((*_this)inputcharAt((*_this)index));
	return new_None<char>();
}
CType toCType_CPointerType(void* _ref){
	CPointerType _this = *((CPointerType*) _ref);
	CTypeData data;
	data.CPointerType = _this;
	return { CPointerTypeVariant, data };
}
char* generate_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return (*_this)typegenerate() + "*";
}
char* toBaseName_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return (*_this)typetoBaseName() + "_ptr";
}
CType toCType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CTypeData data;
	data.CTemplateType = _this;
	return { CTemplateTypeVariant, data };
}
char* generate_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	/*JExpressionWrapper[content=(*_this)listiter()map(F? { alloc(CType), F?Table { generate }})collect(new_Joiner(", "))]*/ typeArguments = (*_this)listiter()map(F? { alloc(CType), F?Table { generate }})collect(new_Joiner(", "));
	return (*_this)base + " < " + typeArguments + ">";
}
char* toBaseName_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	return (*_this)base;
}
CType toCType_Identifier(void* _ref){
	Identifier _this = *((Identifier*) _ref);
	CTypeData data;
	data.Identifier = _this;
	return { IdentifierVariant, data };
}
JType toJType_Identifier(void* _ref){
	Identifier _this = *((Identifier*) _ref);
	JTypeData data;
	data.Identifier = _this;
	return { IdentifierVariant, data };
}
JExpression toJExpression_Identifier(void* _ref){
	Identifier _this = *((Identifier*) _ref);
	JExpressionData data;
	data.Identifier = _this;
	return { IdentifierVariant, data };
}
CExpression toCExpression_Identifier(void* _ref){
	Identifier _this = *((Identifier*) _ref);
	CExpressionData data;
	data.Identifier = _this;
	return { IdentifierVariant, data };
}
char* generate_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return (*_this)value;
}
char* toString_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return "";
}
char* toBaseName_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return (*_this)value;
}
CType toCType_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return (*_this);
}
CExpression toCExpression_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return (*_this);
}
CType toCType_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CTypeData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
JMethodDeclaration toJMethodDeclaration_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	JMethodDeclarationData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
CStructMember toCStructMember_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CStructMemberData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
CFunctionDeclaration toCFunctionDeclaration_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CFunctionDeclarationData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
CAssignable toCAssignable_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CAssignableData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
JAssignable toJAssignable_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	JAssignableData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
JType toJType_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	JTypeData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
char* generate_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return wrap((*_this)input);
}
char* toBaseName_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return wrap((*_this)input);
}
CAssignable toCAssignable_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return (*_this);
}
CType toCType_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return (*_this);
}
JMethodDeclaration toJMethodDeclaration_JConstructor(void* _ref){
	JConstructor _this = *((JConstructor*) _ref);
	JMethodDeclarationData data;
	data.JConstructor = _this;
	return { JConstructorVariant, data };
}
JMethodDeclaration toJMethodDeclaration_JDeclaration(void* _ref){
	JDeclaration _this = *((JDeclaration*) _ref);
	JMethodDeclarationData data;
	data.JDeclaration = _this;
	return { JDeclarationVariant, data };
}
JAssignable toJAssignable_JDeclaration(void* _ref){
	JDeclaration _this = *((JDeclaration*) _ref);
	JAssignableData data;
	data.JDeclaration = _this;
	return { JDeclarationVariant, data };
}
JDeclaration<> new_JDeclaration(JType type, char* name){
	JDeclaration _this;
	(*_this)(Listsempty(), Listsempty(), new_None<char*>(), type, name);
	return _this;
}
JDeclaration mapName_JDeclaration(void* _ref, F1R<char*, char*> mapper){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_JDeclaration((*_this)annotations, (*_this)typeParameters, (*_this)maybeBeforeType, (*_this)type, mapperapply((*_this)name));
}
CDeclaration toCDeclaration_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_CDeclaration((*_this)typeParameters, transformType((*_this)type), (*_this)name);
}
CAssignable toCAssignable_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	return (*_this)toCDeclaration();
}
JDeclaration withType_JDeclaration(void* _ref, JType type){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_JDeclaration((*_this)annotations, (*_this)typeParameters, (*_this)maybeBeforeType, type, (*_this)name);
}
CStructMember toCStructMember_F1RDeclaration(void* _ref){
	F1RDeclaration _this = *((F1RDeclaration*) _ref);
	CStructMemberData data;
	data.F1RDeclaration = _this;
	return { F1RDeclarationVariant, data };
}
char* generate_F1RDeclaration(void* _ref){
	F1RDeclaration* _this = (F1RDeclaration*) _ref;
	/*JExpressionWrapper[content="(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", ")) + ")"]*/ joinedParameterTypes = "(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", ")) + ")";
	return (*_this)typegenerate() + " (*" + this.name + ")" + joinedParameterTypes;
}
CStructMember toCStructMember_EmptyStructMember(void* _ref){
	EmptyStructMember _this = *((EmptyStructMember*) _ref);
	CStructMemberData data;
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
	if (tupleright == '\\') 
		return tupleleftpopAndAppendToOption()orElse(tupleleft);
	return tupleleft;
}
State apply_EscapedFolder(void* _ref, State state, char next){
	EscapedFolder* _this = (EscapedFolder*) _ref;
	if (next == '\'') {
		/*JExpressionWrapper[content=stateappend(next)]*/ appended = stateappend(next);
		return appendedpopAndAppendToTuple()map(lambda5)flatMap(F? { alloc(State), F?Table { popAndAppendToOption }})orElse(appended);
	}
	if (next == '\"') {
		/*JExpressionWrapper[content=stateappend(next)]*/ current = stateappend(next);
		while (true) {
			/*JExpressionWrapper[content=currentpopAndAppendToTuple()]*/ maybeTuple = currentpopAndAppendToTuple();
			if (!(maybeTuple.variant = ?.SomeVariant)) 
				break;
			current = valueleft;
			/*JMemberAccess[instance=, memberName=right]*/ right = valueright;
			if (right == '\\') 
				current = currentpopAndAppendToOption()orElse(current);
			if (right == '\"') 
				break;
		}
		return current;
	}
	return (*_this)folderapply(state, next);
}
Folder toFolder_ValueFolder(void* _ref){
	ValueFolder _this = *((ValueFolder*) _ref);
	FolderData data;
	data.ValueFolder = _this;
	return { ValueFolderVariant, data };
}
State apply_ValueFolder(void* _ref, State state, char next){
	ValueFolder* _this = (ValueFolder*) _ref;
	if (next == ',' && stateisLevel()) 
		return stateadvance();
	/*JExpressionWrapper[content=stateappend(next)]*/ appended = stateappend(next);
	if (next == '-') {
		/*JExpressionWrapper[content=appendedpeek()]*/ peeked = appendedpeek();
		if (peeked.variant = ?.SomeVariant) 
			return appendedpopAndAppendToOption()orElse(appended);
		return appended;
	}
	if (next == '<' || next == '(') 
		return appendedenter();
	if (next == '>' || next == ')') 
		return appendedexit();
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
	return new_Some<R>(mapperapply((*_this)value));
}
template <typename T>
T orElse_Some(void* _ref, T other){
	Some<T>* _this = (Some<T>*) _ref;
	return (*_this)value;
}
template <typename R, typename T>
Option<R> flatMap_Some(void* _ref, F1R<T, Option<R>> mapper){
	Some<T>* _this = (Some<T>*) _ref;
	return mapperapply((*_this)value);
}
template <typename T>
T orElseGet_Some(void* _ref, FR<T> other){
	Some<T>* _this = (Some<T>*) _ref;
	return (*_this)value;
}
template <typename T>
Stream<T> stream_Some(void* _ref){
	Some<T>* _this = (Some<T>*) _ref;
	return Streamof((*_this)value);
}
template <typename T>
Option<T> or_Some(void* _ref, FR<Option<T>> other){
	Some<T>* _this = (Some<T>*) _ref;
	return (*_this);
}
template <typename T>
Tuple<int, T> toTuple_Some(void* _ref, FR<T> other){
	Some<T>* _this = (Some<T>*) _ref;
	return new_Tuple<int, T>(true, (*_this)value);
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
	return otherapply();
}
template <typename T>
Stream<T> stream_None(void* _ref){
	None<T>* _this = (None<T>*) _ref;
	return Streamempty();
}
template <typename T>
Option<T> or_None(void* _ref, FR<Option<T>> other){
	None<T>* _this = (None<T>*) _ref;
	return otherapply();
}
template <typename T>
Tuple<int, T> toTuple_None(void* _ref, FR<T> other){
	None<T>* _this = (None<T>*) _ref;
	return new_Tuple<int, T>(false, otherapply());
}
Folder toFolder_ConditionEndLocator(void* _ref){
	ConditionEndLocator _this = *((ConditionEndLocator*) _ref);
	FolderData data;
	data.ConditionEndLocator = _this;
	return { ConditionEndLocatorVariant, data };
}
State apply_ConditionEndLocator(void* _ref, State state, char c){
	ConditionEndLocator* _this = (ConditionEndLocator*) _ref;
	/*JExpressionWrapper[content=stateappend(c)]*/ appended = stateappend(c);
	if (c == '(') 
		return appendedenter();
	if (c == ')') {
		if (appendedisLevel()) 
			return appendedadvance();
		return appendedexit();
	}
	return appended;
}
CStructMember toCStructMember_CField(void* _ref){
	CField _this = *((CField*) _ref);
	CStructMemberData data;
	data.CField = _this;
	return { CFieldVariant, data };
}
char* generate_CField(void* _ref){
	CField* _this = (CField*) _ref;
	return MaingenerateStatement(1, (*_this)declarationgenerate());
}
auto lambda6(void* _ref, auto index){
	return /*elements[index]*/;
}
template <typename T>
Stream<T> fromObjArray_Streams(void* _ref, T* elements){
	Streams* _this = (Streams*) _ref;
	return new_Stream<int>(new_RangeHead(elementslength))map(lambda6);
}
auto lambda7(void* _ref, auto index){
	return /*array[index]*/;
}
Stream<char> fromCharArray_Streams(void* _ref, char* array){
	Streams* _this = (Streams*) _ref;
	return new_Stream<int>(new_RangeHead(arraylength))map(lambda7);
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
	return (*_this)headnext()map((*_this)mapper);
}
template <typename T>
Head<T> toHead_SingleHead(void* _ref){
	SingleHead<T> _this = *((SingleHead<T>*) _ref);
	HeadData<T> data;
	data.SingleHead = _this;
	return { SingleHeadVariant, data };
}
template <typename T>
SingleHead<T> new_SingleHead(T value){
	SingleHead _this;
	(*_this)value = value;
	(*_this)retrieved = false;
	return _this;
}
template <typename T>
Option<T> next_SingleHead(void* _ref){
	SingleHead<T>* _this = (SingleHead<T>*) _ref;
	if ((*_this)retrieved) 
		return new_None<T>();
	(*_this)retrieved = true;
	return new_Some<T>((*_this)value);
}
template <typename T, typename R>
Head<R> toHead_FlatMapHead(void* _ref){
	FlatMapHead<T, R> _this = *((FlatMapHead<T, R>*) _ref);
	HeadData<T, R> data;
	data.FlatMapHead = _this;
	return { FlatMapHeadVariant, data };
}
template <typename T, typename R>
FlatMapHead<T, R> new_FlatMapHead(Head<T> head, F1R<T, Stream<R>> mapper){
	FlatMapHead _this;
	(*_this)head = head;
	(*_this)mapper = mapper;
	(*_this)maybeCurrent = new_None<Stream<R>>();
	return _this;
}
template <typename T, typename R>
Option<R> next_FlatMapHead(void* _ref){
	FlatMapHead<T, R>* _this = (FlatMapHead<T, R>*) _ref;
	while (true) {
		if ((*_this)maybeCurrent.variant = ?.SomeVariant) {
			/*JExpressionWrapper[content=currentheadnext()]*/ next = currentheadnext();
			if (next.variant = ?.SomeVariant) 
				return next;
		}
		/*JExpressionWrapper[content=(*_this)headnext()]*/ maybeNext = (*_this)headnext();
		if (maybeNext.variant = ?.NoneVariant) 
			return new_None<R>();
		(*_this)maybeCurrent = maybeNextmap((*_this)mapper);
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
	return aBoolean || (*_this)predicateapply(t);
}
Collector<char*, char*> toCollector_Joiner(void* _ref){
	Joiner _this = *((Joiner*) _ref);
	CollectorData data;
	data.Joiner = _this;
	return { JoinerVariant, data };
}
Joiner<> new_Joiner(){
	Joiner _this;
	(*_this)("");
	return _this;
}
char* createInitial_Joiner(void* _ref){
	Joiner* _this = (Joiner*) _ref;
	return "";
}
char* fold_Joiner(void* _ref, char* current, char* element){
	Joiner* _this = (Joiner*) _ref;
	if (currentisEmpty()) 
		return element;
	return current + (*_this)delimiter + element;
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
	return Listsempty();
}
template <typename T>
List<T> fold_ListCollector(void* _ref, List<T> tList, T t){
	ListCollector<T>* _this = (ListCollector<T>*) _ref;
	return tListaddLast(t);
}
CFunctionDeclaration toCFunctionDeclaration_CDeclaration(void* _ref){
	CDeclaration _this = *((CDeclaration*) _ref);
	CFunctionDeclarationData data;
	data.CDeclaration = _this;
	return { CDeclarationVariant, data };
}
CAssignable toCAssignable_CDeclaration(void* _ref){
	CDeclaration _this = *((CDeclaration*) _ref);
	CAssignableData data;
	data.CDeclaration = _this;
	return { CDeclarationVariant, data };
}
CDeclaration<> new_CDeclaration(CType type, char* name){
	CDeclaration _this;
	(*_this)(Listsempty(), type, name);
	return _this;
}
CFunctionDeclaration mapName_CDeclaration(void* _ref, F1R<char*, char*> mapper){
	CDeclaration* _this = (CDeclaration*) _ref;
	return new_CDeclaration((*_this)typeParameters, (*_this)type, mapperapply((*_this)name));
}
CFunctionDeclaration mapTypeParameters_CDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper){
	CDeclaration* _this = (CDeclaration*) _ref;
	return new_CDeclaration(mapperapply((*_this)typeParameters), (*_this)type, (*_this)name);
}
char* generate_CDeclaration(void* _ref){
	CDeclaration* _this = (CDeclaration*) _ref;
	/*JExpressionWrapper[content=generateTemplateString((*_this)typeParameters)]*/ template = generateTemplateString((*_this)typeParameters);
	return template + (*_this)typegenerate() + " " + (*_this)name;
}
JExpression toJExpression_JExpressionWrapper(void* _ref){
	JExpressionWrapper _this = *((JExpressionWrapper*) _ref);
	JExpressionData data;
	data.JExpressionWrapper = _this;
	return { JExpressionWrapperVariant, data };
}
JAssignable toJAssignable_JExpressionWrapper(void* _ref){
	JExpressionWrapper _this = *((JExpressionWrapper*) _ref);
	JAssignableData data;
	data.JExpressionWrapper = _this;
	return { JExpressionWrapperVariant, data };
}
CExpression toCExpression_JExpressionWrapper(void* _ref){
	JExpressionWrapper* _this = (JExpressionWrapper*) _ref;
	return new_CExpressionWrapper((*_this)content);
}
CAssignable toCAssignable_JExpressionWrapper(void* _ref){
	JExpressionWrapper* _this = (JExpressionWrapper*) _ref;
	return new_CExpressionWrapper((*_this)content);
}
CExpression toCExpression_CExpressionWrapper(void* _ref){
	CExpressionWrapper _this = *((CExpressionWrapper*) _ref);
	CExpressionData data;
	data.CExpressionWrapper = _this;
	return { CExpressionWrapperVariant, data };
}
char* generate_CExpressionWrapper(void* _ref){
	CExpressionWrapper* _this = (CExpressionWrapper*) _ref;
	return (*_this)content;
}
JType toJType_JArrayType(void* _ref){
	JArrayType _this = *((JArrayType*) _ref);
	JTypeData data;
	data.JArrayType = _this;
	return { JArrayTypeVariant, data };
}
CType toCType_JArrayType(void* _ref){
	JArrayType* _this = (JArrayType*) _ref;
	return new_CPointerType(transformType((*_this)type));
}
JType toJType_JGenericType(void* _ref){
	JGenericType _this = *((JGenericType*) _ref);
	JTypeData data;
	data.JGenericType = _this;
	return { JGenericTypeVariant, data };
}
CType toCType_JGenericType(void* _ref){
	JGenericType* _this = (JGenericType*) _ref;
	/*JExpressionWrapper[content=(*_this)typeArgumentsiter()map(F? { alloc(Main), F?Table { transformType }})toList()]*/ newTypeArguments = (*_this)typeArgumentsiter()map(F? { alloc(Main), F?Table { transformType }})toList();
	return new_CTemplateType((*_this)base, newTypeArguments);
}
CExpression toCExpression_CPointerAccess(void* _ref){
	CPointerAccess _this = *((CPointerAccess*) _ref);
	CExpressionData data;
	data.CPointerAccess = _this;
	return { CPointerAccessVariant, data };
}
char* generate_CPointerAccess(void* _ref){
	CPointerAccess* _this = (CPointerAccess*) _ref;
	return (*_this)instancegenerate() + "->" + (*_this)fieldName;
}
CExpression toCExpression_CFieldAccess(void* _ref){
	CFieldAccess _this = *((CFieldAccess*) _ref);
	CExpressionData data;
	data.CFieldAccess = _this;
	return { CFieldAccessVariant, data };
}
char* generate_CFieldAccess(void* _ref){
	CFieldAccess* _this = (CFieldAccess*) _ref;
	return (*_this)instancegenerate() + (*_this)fieldName;
}
JExpression toJExpression_JMemberAccess(void* _ref){
	JMemberAccess _this = *((JMemberAccess*) _ref);
	JExpressionData data;
	data.JMemberAccess = _this;
	return { JMemberAccessVariant, data };
}
CExpression toCExpression_JMemberAccess(void* _ref){
	JMemberAccess* _this = (JMemberAccess*) _ref;
	/*JExpressionWrapper[content=(*_this)instancetoCExpression()]*/ cExpression = (*_this)instancetoCExpression();
	if ((*_this)instance.variant = ?.Identifier(var value) && value.equals("this")Variant) 
		return new_CPointerAccess(new_Identifier("_this"), (*_this)memberName);
	/*else return new CFieldAccess(cExpression, this.memberName)*/;
}
/*private List<List<JDeclaration>> environment = new JavaList<List<JDeclaration>>*/(){?
}
Main<> new_Main(){
	Main _this;
	(*_this)structures = Listsempty();
	(*_this)functionDeclarations = Listsempty();
	(*_this)functions = Listsempty();
	(*_this)globals = Listsempty();
	(*_this)counter = 0;
	return _this;
}
auto lambda8(void* _ref, auto typeParam){
	return "typename " + typeParam;
}
char* generateTemplateString_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* templateString;
	if (typeParametersisEmpty()) 
		templateString = "";
	else {
		/*JExpressionWrapper[content=typeParametersiter()map(lambda8)collect(new_Joiner(", "))]*/ typeNames = typeParametersiter()map(lambda8)collect(new_Joiner(", "));
		templateString = "template <" + typeNames + ">" + SystemlineSeparator();
	}
	return templateString;
}
char* wrap_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputreplace("start", "start")replace("end", "end")]*/ replaced = inputreplace("/*", "start")replace("*/", "end");
	return "/*" + replaced + "*/";
}
void main_Main(void* _ref, char** args){
	Main* _this = (Main*) _ref;
	if (new_Main()run().variant = ?.SomeVariant) 
		Systemerrprintln(valuedisplay());
}
char* generateStatement_Main(void* _ref, int depth, char* content){
	Main* _this = (Main*) _ref;
	return generateIndent(depth) + content + ";";
}
char* generateIndent_Main(void* _ref, int depth){
	Main* _this = (Main*) _ref;
	return SystemlineSeparator() + "\t"repeat(depth);
}
CType transformType_Main(void* _ref, JType jType){
	Main* _this = (Main*) _ref;
	return _switch;
}
CType transformPrimitiveType_Main(void* _ref, JPrimitiveType type){
	Main* _this = (Main*) _ref;
	return _switch;
}
Option<IOError> run_Main(void* _ref){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=Pathsget(".", "src", "main", "java", "magma", "Main.java")]*/ source = Pathsget(".", "src", "main", "java", "magma", "Main.java");
	/*JExpressionWrapper[content=sourceresolveSibling("Main.cpp")]*/ target = sourceresolveSibling("Main.cpp");
	/*JExpressionWrapper[content=sourcereadString()mapValue(F? { alloc((*_this)), F?Table { compile }})]*/ input = sourcereadString()mapValue(F? { alloc((*_this)), F?Table { compile }});
	return _switch;
}
char* compile_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=(*_this)compileStatements(input, F? { alloc((*_this)), F?Table { compileRootSegment }})]*/ all = (*_this)compileStatements(input, F? { alloc((*_this)), F?Table { compileRootSegment }});
	/*JExpressionWrapper[content=(*_this)joinStrings("", (*_this)structures)]*/ joinedStructures = (*_this)joinStrings("", (*_this)structures);
	/*JExpressionWrapper[content=(*_this)joinStrings("", (*_this)globals)]*/ joinedGlobals = (*_this)joinStrings("", (*_this)globals);
	/*JExpressionWrapper[content=(*_this)joinStrings("", (*_this)functionDeclarations)]*/ joinedFunctionDeclarations = (*_this)joinStrings("", (*_this)functionDeclarations);
	/*JExpressionWrapper[content=(*_this)joinStrings("", (*_this)functions)]*/ joinedFunctions = (*_this)joinStrings("", (*_this)functions);
	return joinedStructures + joinedGlobals + joinedFunctionDeclarations + joinedFunctions + all;
}
char* joinStrings_Main(void* _ref, char* delimiter, List<char*> structures){
	Main* _this = (Main*) _ref;
	return structuresiter()collect(new_Joiner(delimiter));
}
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper){
	Main* _this = (Main*) _ref;
	return (*_this)compileAll(input, mapper, new_EscapedFolder(F? { alloc((*_this)), F?Table { foldStatement }}));
}
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder){
	Main* _this = (Main*) _ref;
	return (*_this)divide(input, folder)map(mapper)collect(new_Joiner(""));
}
Stream<char*> divide_Main(void* _ref, char* input, Folder folder){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=new_State(input)]*/ current = new_State(input);
	while (true) {
		/*JExpressionWrapper[content=currentpop()]*/ maybeNext = currentpop();
		if (!(maybeNext.variant = ?.SomeVariant)) 
			break;
		char next;
		next = value;
		current = folderapply(current, next);
	}
	return currentadvance()stream();
}
State foldStatement_Main(void* _ref, State current, char next){
	Main* _this = (Main*) _ref;
	if (next == '/' && currentisLevel()) {
		/*JExpressionWrapper[content=currentpeek()]*/ maybePeeked = currentpeek();
		if (maybePeeked.variant = ?.SomeVariant) {
			/*JExpressionWrapper[content=currentappend('/')popAndAppendToOption()orElse(current)]*/ withoutLineCommentPrefix = currentappend('/')popAndAppendToOption()orElse(current);
			while (true) {
				/*JExpressionWrapper[content=withoutLineCommentPrefixpopAndAppendToTuple()]*/ maybeTuple = withoutLineCommentPrefixpopAndAppendToTuple();
				if (maybeTuple.variant = ?.SomeVariant) {
					withoutLineCommentPrefix = tupleleft;
					/*JMemberAccess[instance=, memberName=right]*/ right = tupleright;
					if (right == '\r' || right == '\n') 
						withoutLineCommentPrefix = withoutLineCommentPrefixadvance();
				}
				return withoutLineCommentPrefix;
			}
		}
	}
	/*JExpressionWrapper[content=currentappend(next)]*/ appended = currentappend(next);
	if (next == ';' && appendedisLevel()) 
		return appendedadvance();
	if (next == '}' && appendedisShallow()) {
		State appended1;
		if (appendedpeek().variant = ?.SomeVariant) 
			appended1 = appendedpopAndAppendToOption()orElse(appended);
		else appended1 = appended;
		return appended1advance()exit();
	}
	if (next == '{' || next == '(') 
		return appendedenter();
	if (next == '}' || next == ')') 
		return appendedexit();
	return appended;
}
auto lambda9(void* _ref, auto ()){
	return wrap(stripped);
}
char* compileRootSegment_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputstrip()]*/ stripped = inputstrip();
	if (strippedisEmpty()) 
		return "";
	if (strippedstartsWith("package ") || strippedstartsWith("import ")) 
		return "";
	return (*_this)compileStructure("class", stripped)map(F? { alloc(CStructMember), F?Table { generate }})orElseGet(lambda9);
}
auto lambda10(void* _ref, auto input){
	return transformType((*_this)parseType(input));
}
auto lambda11(void* _ref, auto slice){
	return !sliceisEmpty();
}
auto lambda12(void* _ref, auto (state, character)){
	return new_ValueFolder()apply(state, character);
}
auto lambda13(void* _ref, auto (state, character)){
	return new_ValueFolder()apply(state, character);
}
auto lambda14(void* _ref, auto slice){
	return !sliceisEmpty();
}
auto lambda15(void* _ref, auto implementee){
	return (*_this)getString(implementee, name, joinedTypeParameters, templateString);
}
auto lambda16(void* _ref, auto slice){
	return (*_this)compileClassSegment(slice, name, finalTypeParameters, finalVariants);
}
auto lambda17(void* _ref, auto variant){
	return SystemlineSeparator() + "\t" + variant + "Variant";
}
auto lambda18(void* _ref, auto variant){
	return SystemlineSeparator() + "\t" + variant + joinedTypeParameters + " " + variant + ";";
}
auto lambda19(void* _ref, auto member){
	return !(member.variant = ?.F1RDeclarationVariant);
}
Option<CStructMember> compileStructure_Main(void* _ref, char* type, char* stripped){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=strippedindexOf(type + " ")]*/ i = strippedindexOf(type + " ");
	if (i < 0) 
		return new_None<CStructMember>();
	/*JExpressionWrapper[content=strippedsubstring(0, i)strip()]*/ beforeType = strippedsubstring(0, i)strip();
	char* modifiers;
	List<char*> annotations = Listsempty();
	/*JExpressionWrapper[content=beforeTypelastIndexOf("\n")]*/ i5 = beforeTypelastIndexOf("\n");
	if (i5 >= 0) {
		/*JExpressionWrapper[content=beforeTypesubstring(0, i5)]*/ substring = beforeTypesubstring(0, i5);
		/*JExpressionWrapper[content=beforeTypesubstring(i5 + 1)]*/ substring1 = beforeTypesubstring(i5 + 1);
		annotations = (*_this)collectAnnotations(substring);
		modifiers = substring1;
	}
	else modifiers = beforeType;
	if (annotationscontains("Actual")) 
		return new_Some<CStructMember>(new_EmptyStructMember());
	/*JExpressionWrapper[content=strippedsubstring(i + (type + " ")length())strip()]*/ afterKeyword = strippedsubstring(i + (type + " ")length())strip();
	/*JExpressionWrapper[content=afterKeywordindexOf("{")]*/ i1 = afterKeywordindexOf("{");
	if (i1 < 0) 
		return new_None<CStructMember>();
	/*JExpressionWrapper[content=afterKeywordsubstring(0, i1)strip()]*/ beforeContent = afterKeywordsubstring(0, i1)strip();
	/*JExpressionWrapper[content=afterKeywordsubstring(i1 + 1)strip()]*/ withEnd = afterKeywordsubstring(i1 + 1)strip();
	if (!withEndendsWith("}")) 
		return new_None<CStructMember>();
	/*JExpressionWrapper[content=withEndsubstring(0, withEndlength() - 1)]*/ inputContent = withEndsubstring(0, withEndlength() - 1);
	List<char*> variants = Listsempty();
	/*JExpressionWrapper[content=beforeContentindexOf("permits ")]*/ i2 = beforeContentindexOf("permits ");
	if (i2 >= 0) {
		/*JExpressionWrapper[content=beforeContentsubstring(i2 + "permits "length())]*/ substring1 = beforeContentsubstring(i2 + "permits "length());
		beforeContent = beforeContentsubstring(0, i2);
		variants = (*_this)splitValues(substring1);
	}
	List<CType> implementees = Listsempty();
	/*JExpressionWrapper[content=beforeContentindexOf("implements ")]*/ i4 = beforeContentindexOf("implements ");
	if (i4 >= 0) {
		/*JExpressionWrapper[content=beforeContentsubstring(i4 + "implements "length())]*/ implementeesString = beforeContentsubstring(i4 + "implements "length());
		beforeContent = beforeContentsubstring(0, i4)strip();
		implementees = (*_this)divide(implementeesString, lambda12)map(F? { alloc(String), F?Table { strip }})filter(lambda11)map(lambda10)toList();
	}
	List<JDeclaration> recordFields = Listsempty();
	if (beforeContentendsWith(")")) {
		/*JExpressionWrapper[content=beforeContentsubstring(0, beforeContentlength() - 1)]*/ substring = beforeContentsubstring(0, beforeContentlength() - 1);
		/*JExpressionWrapper[content=substringindexOf("(")]*/ i3 = substringindexOf("(");
		if (i3 >= 0) {
			beforeContent = substringsubstring(0, i3);
			recordFields = (*_this)divide(substringsubstring(i3 + 1), lambda13)map(F? { alloc((*_this)), F?Table { parseDeclaration }})flatMap(F? { alloc(Option), F?Table { stream }})toList();
		}
	}
	List<char*> typeParameters = Listsempty();
	/*JExpressionWrapper[content=beforeContentindexOf(" < ")]*/ i3 = beforeContentindexOf(" < ");
	if (i3 >= 0) {
		/*JExpressionWrapper[content=beforeContentsubstring(i3 + 1)strip()]*/ substring1 = beforeContentsubstring(i3 + 1)strip();
		if (substring1endsWith(">")) {
			beforeContent = beforeContentsubstring(0, i3);
			/*JExpressionWrapper[content=substring1substring(0, substring1length() - 1)]*/ substring = substring1substring(0, substring1length() - 1);
			typeParameters = (*_this)splitValues(substring);
		}
	}
	if (!(*_this)isIdentifier(beforeContent)) 
		return new_None<CStructMember>();
	/*JExpressionWrapper[content=StreamsfromObjArray(modifierssplit(Patternquote(" ")))map(F? { alloc(String), F?Table { strip }})filter(lambda14)toList()]*/ modifiersList = StreamsfromObjArray(modifierssplit(Patternquote(" ")))map(F? { alloc(String), F?Table { strip }})filter(lambda14)toList();
	/*JExpressionWrapper[content=beforeContentstrip()]*/ name = beforeContentstrip();
	/*JExpressionWrapper[content=generateTemplateString(typeParameters)]*/ templateString = generateTemplateString(typeParameters);
	/*JExpressionWrapper[content=(*_this)joinTypeParameters(typeParameters)]*/ joinedTypeParameters = (*_this)joinTypeParameters(typeParameters);
	/*JExpressionWrapper[content=StringBuildersempty()]*/ fields = StringBuildersempty();
	/*JExpressionWrapper[content=StringBuildersempty()]*/ dependencies = StringBuildersempty();
	(*_this)functions = implementeesiter()map(lambda15)fold((*_this)functions, F? { alloc(List), F?Table { addLast }});
	/*JExpressionWrapper[content=recordFieldsiter()map(F? { alloc(JDeclaration), F?Table { toCDeclaration }})map(F? { alloc(CDeclaration), F?Table { generate }})map(F? { alloc((*_this)), F?Table { generateStatement }})collect(new_Joiner())]*/ joinedRecordFields = recordFieldsiter()map(F? { alloc(JDeclaration), F?Table { toCDeclaration }})map(F? { alloc(CDeclaration), F?Table { generate }})map(F? { alloc((*_this)), F?Table { generateStatement }})collect(new_Joiner());
	List<char*> finalTypeParameters = typeParameters;
	/**/ finalVariants = variants;
	/*JExpressionWrapper[content=(*_this)divide(inputContent, new_EscapedFolder(F? { alloc((*_this)), F?Table { foldStatement }}))map(lambda16)flatMap(F? { alloc(Option), F?Table { stream }})toList()]*/ members = (*_this)divide(inputContent, new_EscapedFolder(F? { alloc((*_this)), F?Table { foldStatement }}))map(lambda16)flatMap(F? { alloc(Option), F?Table { stream }})toList();
	if (modifiersListcontains("sealed")) {
		/*JExpressionWrapper[content=variantsiter()map(lambda17)collect(new_Joiner(","))]*/ enumFields = variantsiter()map(lambda17)collect(new_Joiner(","));
		/*JExpressionWrapper[content="enum " + name + "Variant {" + enumFields + SystemlineSeparator() + "};" + SystemlineSeparator()]*/ generatedEnum = "enum " + name + "Variant {" + enumFields + SystemlineSeparator() + "};" + SystemlineSeparator();
		/*JExpressionWrapper[content=variantsiter()map(lambda18)collect(new_Joiner())]*/ unionFields = variantsiter()map(lambda18)collect(new_Joiner());
		/*JExpressionWrapper[content=templateString + "union " + name + "Data {" + unionFields + SystemlineSeparator() + "};" + SystemlineSeparator()]*/ generatedUnion = templateString + "union " + name + "Data {" + unionFields + SystemlineSeparator() + "};" + SystemlineSeparator();
		/*JExpressionWrapper[content=name + "Variant variant"]*/ s = name + "Variant variant";
		/*JExpressionWrapper[content=name + "Data" + joinedTypeParameters + " data"]*/ s1 = name + "Data" + joinedTypeParameters + " data";
		/*JExpressionWrapper[content=(*_this)generateStatement(s) + (*_this)generateStatement(s1)]*/ generatedFields = (*_this)generateStatement(s) + (*_this)generateStatement(s1);
		fields = fieldsappendString(generatedFields);
		dependencies = dependenciesappendString(generatedEnum)appendString(generatedUnion);
	}
	else 
	if (typeequals("interface")) {
		/*JExpressionWrapper[content=(*_this)generateStatement(name + "Table" + joinedTypeParameters + " table")]*/ table = (*_this)generateStatement(name + "Table" + joinedTypeParameters + " table");
		/*JExpressionWrapper[content=(*_this)generateStatement("void* data")]*/ data = (*_this)generateStatement("void* data");
		/*JExpressionWrapper[content=membersiter()map(F? { alloc(CStructMember), F?Table { generate }})map(F? { alloc((*_this)), F?Table { generateStatement }})collect(new_Joiner(""))]*/ tableMembers = membersiter()map(F? { alloc(CStructMember), F?Table { generate }})map(F? { alloc((*_this)), F?Table { generateStatement }})collect(new_Joiner(""));
		/*JExpressionWrapper[content=templateString + "struct " + name + "Table {" + tableMembers + SystemlineSeparator() + "};" + SystemlineSeparator()]*/ vTable = templateString + "struct " + name + "Table {" + tableMembers + SystemlineSeparator() + "};" + SystemlineSeparator();
		dependencies = dependenciesappendString(vTable);
		fields = fieldsappendString(table)appendString(data);
	}
	else {
		/*JExpressionWrapper[content=membersiter()filter(lambda19)map(F? { alloc(CStructMember), F?Table { generate }})collect(new_Joiner())]*/ joinedMembers = membersiter()filter(lambda19)map(F? { alloc(CStructMember), F?Table { generate }})collect(new_Joiner());
		fields = fieldsappendString(joinedMembers);
	}
	/*JExpressionWrapper[content=dependencies + templateString + "struct " + name + " {" + joinedRecordFields + fields + SystemlineSeparator() + "};" + SystemlineSeparator()]*/ generated = dependencies + templateString + "struct " + name + " {" + joinedRecordFields + fields + SystemlineSeparator() + "};" + SystemlineSeparator();
	(*_this)structures = (*_this)structuresaddLast(generated);
	return new_Some<CStructMember>(new_EmptyStructMember());
}
char* getString_Main(void* _ref, CType implementee, char* name, char* joinedTypeParameters, char* templateString){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=implementeetoBaseName()]*/ identifier = implementeetoBaseName();
	/*JExpressionWrapper[content=name + joinedTypeParameters]*/ thisType = name + joinedTypeParameters;
	/*JExpressionWrapper[content=(*_this)generateStatement(thisType + " _this = *((" + thisType + "*) _ref)")]*/ s = (*_this)generateStatement(thisType + " _this = *((" + thisType + "*) _ref)");
	/*JExpressionWrapper[content=(*_this)generateStatement(identifier + "Data" + joinedTypeParameters + " data")]*/ s1 = (*_this)generateStatement(identifier + "Data" + joinedTypeParameters + " data");
	/*JExpressionWrapper[content=(*_this)generateStatement("data." + name + " = _this")]*/ s2 = (*_this)generateStatement("data." + name + " = _this");
	/*JExpressionWrapper[content=(*_this)generateStatement("return { " + name + "Variant, data }")]*/ s3 = (*_this)generateStatement("return { " + name + "Variant, data }");
	/*JExpressionWrapper[content=s + s1 + s2 + s3]*/ conversionF1RContent = s + s1 + s2 + s3;
	return templateString + implementeegenerate() + " to" + identifier + "_" + name + "(void* _ref){" + conversionF1RContent + SystemlineSeparator() + "}" + SystemlineSeparator();
}
char* joinTypeParameters_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* joinedTypeParameters;
	if (typeParametersisEmpty()) 
		joinedTypeParameters = "";
	else joinedTypeParameters = " < " + typeParametersiter()collect(new_Joiner(", ")) + ">";
	return joinedTypeParameters;
}
char* generateStatement_Main(void* _ref, char* content){
	Main* _this = (Main*) _ref;
	return generateStatement(1, content);
}
auto lambda20(void* _ref, auto slice){
	return !sliceisEmpty();
}
List<char*> splitValues_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputsplit(Patternquote(","))]*/ segments = inputsplit(Patternquote(","));
	/*JExpressionWrapper[content=Arraysstream(segments)map(F? { alloc(String), F?Table { strip }})filter(lambda20)toList()]*/ list = Arraysstream(segments)map(F? { alloc(String), F?Table { strip }})filter(lambda20)toList();
	return new_JavaList<char*>(list);
}
auto lambda21(void* _ref, auto i){
	/*JExpressionWrapper[content=strippedcharAt(i)]*/ c = strippedcharAt(i);
	return CharacterisLetter(c) || (i != 0 && CharacterisDigit(c));
}
int isIdentifier_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputstrip()]*/ stripped = inputstrip();
	return IntStreamrange(0, strippedlength())allMatch(lambda21);
}
Option<CStructMember> compileClassSegment_Main(void* _ref, char* input, char* structName, List<char*> typeParameters, List<char*> variants){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputstrip()]*/ stripped = inputstrip();
	if (strippedisEmpty()) 
		return new_None<CStructMember>();
	/*JExpressionWrapper[content=(*_this)compileStructure("enum", input)]*/ maybeEnum = (*_this)compileStructure("enum", input);
	if (maybeEnum.variant = ?.SomeVariant) 
		return maybeEnum;
	/*JExpressionWrapper[content=(*_this)compileStructure("interface", input)]*/ maybeInterface = (*_this)compileStructure("interface", input);
	if (maybeInterface.variant = ?.SomeVariant) 
		return maybeInterface;
	/*JExpressionWrapper[content=(*_this)compileStructure("record", input)]*/ maybeRecord = (*_this)compileStructure("record", input);
	if (maybeRecord.variant = ?.SomeVariant) 
		return maybeRecord;
	/*JExpressionWrapper[content=(*_this)compileStructure("class", input)]*/ maybeClass = (*_this)compileStructure("class", input);
	if (maybeClass.variant = ?.SomeVariant) 
		return maybeClass;
	/*JExpressionWrapper[content=(*_this)compileEnumValues(input, structName)]*/ maybeEnumValues = (*_this)compileEnumValues(input, structName);
	if (maybeEnumValues.variant = ?.SomeVariant) 
		return maybeEnumValues;
	if (strippedendsWith(";")) {
		/*JExpressionWrapper[content=strippedsubstring(0, strippedlength() - 1)]*/ substring = strippedsubstring(0, strippedlength() - 1);
		/*JExpressionWrapper[content=(*_this)parseDeclaration(substring)]*/ maybeDeclaration = (*_this)parseDeclaration(substring);
		if (maybeDeclaration.variant = ?.SomeVariant) 
			return new_Some<CStructMember>(new_CField(declarationtoCDeclaration()));
	}
	/*JExpressionWrapper[content=(*_this)compileMethod(structName, typeParameters, variants, stripped)]*/ maybeMethod = (*_this)compileMethod(structName, typeParameters, variants, stripped);
	if (maybeMethod.variant = ?.SomeVariant) 
		return maybeMethod;
	return new_Some<CStructMember>(new_Placeholder(stripped));
}
auto lambda22(void* _ref, auto slice){
	return !sliceisEmpty();
}
auto lambda23(void* _ref, auto name){
	return name + "_" + structName;
}
auto lambda24(void* _ref, auto parameter){
	return parametername;
}
auto lambda25(void* _ref, auto variant){
	return (*_this)generateCase(declaration, variant);
}
auto lambda26(void* _ref){
	if (variantsisEmpty()) {
		/*JExpressionWrapper[content=finalParameterssubList(1, finalParameterssize())iter()map(lambda24)toList()addFirst("_this->data")iter()collect(new_Joiner(", "))]*/ joinedParameters = finalParameterssubList(1, finalParameterssize())iter()map(lambda24)toList()addFirst("_this->data")iter()collect(new_Joiner(", "));
		return (*_this)generateStatement("return _this->table." + declarationname + "(" + joinedParameters + ")");
	}
	else {
		/*JExpressionWrapper[content=(*_this)generateStatement(transformType(declarationtype)generate() + " _ret")]*/ returnValueDefinition = (*_this)generateStatement(transformType(declarationtype)generate() + " _ret");
		/*JExpressionWrapper[content=variantsiter()map(lambda25)collect(new_Joiner())]*/ cases = variantsiter()map(lambda25)collect(new_Joiner());
		return returnValueDefinition + generateIndent(1) + "switch (" + "_this->variant" + ") {" + cases + generateIndent(1) + "}" + (*_this)generateStatement("return _ret");
	}
}
auto lambda27(void* _ref, auto name){
	return name + "_" + structName;
}
auto lambda28(void* _ref, auto typeParameters0){
	return typeParameters0addAll(typeParameters);
}
Option<CStructMember> compileMethod_Main(void* _ref, char* structName, List<char*> typeParameters, List<char*> variants, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputindexOf("(")]*/ i = inputindexOf("(");
	if (i < 0) 
		return new_None<CStructMember>();
	/*JExpressionWrapper[content=inputsubstring(0, i)]*/ declarationString = inputsubstring(0, i);
	/*JExpressionWrapper[content=inputsubstring(i + 1)]*/ substring1 = inputsubstring(i + 1);
	/*JExpressionWrapper[content=substring1indexOf(")")]*/ i1 = substring1indexOf(")");
	if (i1 < 0) 
		return new_None<CStructMember>();
	/*JExpressionWrapper[content=substring1substring(0, i1)]*/ parametersString = substring1substring(0, i1);
	/*JExpressionWrapper[content=substring1substring(i1 + 1)strip()]*/ withBraces = substring1substring(i1 + 1)strip();
	/*JExpressionWrapper[content=(*_this)divide(parametersString, new_ValueFolder())map(F? { alloc(String), F?Table { strip }})filter(lambda22)toList()iter()map(F? { alloc((*_this)), F?Table { parseDeclaration }})flatMap(F? { alloc(Option), F?Table { stream }})toList()]*/ parameters = (*_this)divide(parametersString, new_ValueFolder())map(F? { alloc(String), F?Table { strip }})filter(lambda22)toList()iter()map(F? { alloc((*_this)), F?Table { parseDeclaration }})flatMap(F? { alloc(Option), F?Table { stream }})toList();
	(*_this)environment = (*_this)environmentaddLast(parameters);
	/*JExpressionWrapper[content=parametersiter()map(F? { alloc(JDeclaration), F?Table { toCDeclaration }})toList()]*/ cParameters = parametersiter()map(F? { alloc(JDeclaration), F?Table { toCDeclaration }})toList();
	/*JExpressionWrapper[content=(*_this)parseMethodDeclaration(declarationString, structName)]*/ methodDeclaration = (*_this)parseMethodDeclaration(declarationString, structName);
	Option<char*> maybeCompiled = new_None<char*>();
	if (methodDeclaration.variant = ?.JDeclaration declaration && declaration.annotations.contains("Actual")Variant) {
		/*JExpressionWrapper[content=cParametersiter()map(F? { alloc(CDeclaration), F?Table { generate }})collect(new_Joiner(", "))]*/ compiledParameters = cParametersiter()map(F? { alloc(CDeclaration), F?Table { generate }})collect(new_Joiner(", "));
		/*JExpressionWrapper[content=declarationmapName(lambda23)toCDeclaration()]*/ modifiedMethodDeclaration = declarationmapName(lambda23)toCDeclaration();
		(*_this)functionDeclarations = (*_this)functionDeclarationsaddLast(modifiedMethodDeclarationgenerate() + "(" + compiledParameters + ");" + SystemlineSeparator());
		return new_Some<CStructMember>(new_EmptyStructMember());
	}
	if (withBracesstartsWith("{") && withBracesendsWith("}")) {
		/*JExpressionWrapper[content=withBracessubstring(1, withBraceslength() - 1)]*/ inputContent = withBracessubstring(1, withBraceslength() - 1);
		maybeCompiled = new_Some<char*>((*_this)compileMethodsSegments(inputContent, 1));
	}
	char* outputContent;
	if (methodDeclaration.variant = ?.JConstructorVariant) {
		/*JExpressionWrapper[content=maybeCompiledorElse("?")]*/ compiled = maybeCompiledorElse("?");
		outputContent = (*_this)generateStatement(structName + " _this") + compiled + (*_this)generateStatement("return " + "_this");
	}
	else 
	if (methodDeclaration.variant = ?.JDeclaration declarationVariant) {
		cParameters = cParametersaddFirst(new_CDeclaration(new_CPointerType(CPrimitiveTypeVoid), "_ref"));
		/*JExpressionWrapper[content=(*_this)joinTypeParameters(typeParameters)]*/ joinedTypeParameters = (*_this)joinTypeParameters(typeParameters);
		/*JExpressionWrapper[content=(*_this)generateStatement(structName + joinedTypeParameters + "* _this = (" + structName + joinedTypeParameters + "*) _ref")]*/ thisInitialization = (*_this)generateStatement(structName + joinedTypeParameters + "* _this = (" + structName + joinedTypeParameters + "*) _ref");
		/**/ finalParameters = cParameters;
		outputContent = thisInitialization + maybeCompiledorElseGet(lambda26);
	}
	else outputContent = "?";
	/*JExpressionWrapper[content=cParametersiter()map(F? { alloc(CDeclaration), F?Table { generate }})collect(new_Joiner(", "))]*/ compiledParameters = cParametersiter()map(F? { alloc(CDeclaration), F?Table { generate }})collect(new_Joiner(", "));
	/*JExpressionWrapper[content=_switch]*/ modifiedMethodDeclaration = _switch;
	/*JExpressionWrapper[content=modifiedMethodDeclarationmapTypeParameters(lambda28)mapName(lambda27)]*/ mapped = modifiedMethodDeclarationmapTypeParameters(lambda28)mapName(lambda27);
	/*JExpressionWrapper[content=mappedgenerate() + "(" + compiledParameters + ")"]*/ header = mappedgenerate() + "(" + compiledParameters + ")";
	/*JExpressionWrapper[content=header + "{" + outputContent + SystemlineSeparator() + "}" + SystemlineSeparator()]*/ generated = header + "{" + outputContent + SystemlineSeparator() + "}" + SystemlineSeparator();
	(*_this)functionDeclarations = (*_this)functionDeclarationsaddLast(header + ";" + SystemlineSeparator());
	(*_this)functions = (*_this)functionsaddLast(generated);
	/*JExpressionWrapper[content=cParametersiter()map(F? { alloc(CDeclaration), F?Table { type }})toList()]*/ parameterTypes = cParametersiter()map(F? { alloc(CDeclaration), F?Table { type }})toList();
	return _switch;
}
CType toConstructorReturnType_Main(void* _ref, char* base, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	if (baseisEmpty()) 
		return new_Identifier(base);
	/*final var typeArguments = typeParameters.iter().<CType>map(Identifier::new).toList()*/;
	return new_CTemplateType(base, typeArguments);
}
auto lambda29(void* _ref, auto input){
	return (*_this)compileMethodSegment(input, indent);
}
char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent){
	Main* _this = (Main*) _ref;
	return (*_this)compileStatements(inputContent, lambda29);
}
char* generateCase_Main(void* _ref, JDeclaration declaration, char* variant){
	Main* _this = (Main*) _ref;
	return generateIndent(2) + "case " + variant + "Variant:" + generateStatement(3, "_ret = " + declarationname + "_" + variant + "(&(_this->data." + variant + "))") + generateStatement(3, "break");
}
auto lambda30(void* _ref, auto ()){
	return new_Placeholder(declaration);
}
auto lambda31(void* _ref, auto ()){
	return (*_this)parseDeclaration(declaration)map(F? { alloc((*_this)), F?Table { toInterface }});
}
JMethodDeclaration parseMethodDeclaration_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	return (*_this)parseConstructor(declaration, structName)or(lambda31)orElseGet(lambda30);
}
JMethodDeclaration toInterface_Main(void* _ref, JDeclaration value){
	Main* _this = (Main*) _ref;
	return value;
}
Option<JMethodDeclaration> parseConstructor_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=declarationstrip()]*/ stripped = declarationstrip();
	if (strippedequals(structName)) 
		return new_Some<JMethodDeclaration>(new_JConstructor(structName));
	/*JExpressionWrapper[content=strippedlastIndexOf(" ")]*/ i = strippedlastIndexOf(" ");
	if (i >= 0) {
		/*JExpressionWrapper[content=strippedsubstring(i + 1)strip()]*/ substring = strippedsubstring(i + 1)strip();
		if (substringequals(structName)) 
			return new_Some<JMethodDeclaration>(new_JConstructor(structName));
	}
	return new_None<JMethodDeclaration>();
}
auto lambda32(void* _ref, auto slice){
	return !sliceisEmpty();
}
auto lambda33(void* _ref, auto (state, character)){
	return new_ValueFolder()apply(state, character);
}
auto lambda34(void* _ref, auto enumValue){
	return (*_this)compileEnumValue(structName, enumValue);
}
auto lambda35(void* _ref, auto option){
	return option.variant = ?.NoneVariant;
}
auto lambda36(void* _ref, auto option){
	return option.variant = ?.NoneVariant;
}
Option<CStructMember> compileEnumValues_Main(void* _ref, char* input, char* structName){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputstrip()]*/ stripped = inputstrip();
	if (!strippedendsWith(";")) 
		return new_None<CStructMember>();
	/*JExpressionWrapper[content=(*_this)divide(strippedsubstring(0, strippedlength() - 1), lambda33)map(F? { alloc(String), F?Table { strip }})filter(lambda32)toList()]*/ enumValues = (*_this)divide(strippedsubstring(0, strippedlength() - 1), lambda33)map(F? { alloc(String), F?Table { strip }})filter(lambda32)toList();
	if (!enumValuesisEmpty()) {
		/*JExpressionWrapper[content=enumValuesiter()map(lambda34)]*/ optionStream = enumValuesiter()map(lambda34);
		/*final var areAnyInvalid =
					(boolean) optionStream.collect(new AnyMatch<Option<CStructMember>>(option -> option instanceof None<CStructMember>))*/;
		if (areAnyInvalid) 
			return new_None<CStructMember>();
	}
	return new_Some<CStructMember>(new_EmptyStructMember());
}
Option<CStructMember> compileEnumValue_Main(void* _ref, char* structName, char* enumValue){
	Main* _this = (Main*) _ref;
	if (enumValueendsWith(")")) {
		/*JExpressionWrapper[content=enumValuesubstring(0, enumValuelength() - 1)]*/ substring = enumValuesubstring(0, enumValuelength() - 1);
		/*JExpressionWrapper[content=substringindexOf("(")]*/ i = substringindexOf("(");
		if (i >= 0) {
			/*JExpressionWrapper[content=substringsubstring(0, i)]*/ name = substringsubstring(0, i);
			if (!(*_this)isIdentifier(name)) 
				return new_None<CStructMember>();
			/*JExpressionWrapper[content=substringsubstring(i + 1)]*/ substring2 = substringsubstring(i + 1);
			/*JExpressionWrapper[content=structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" + SystemlineSeparator()]*/ generated = structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" + SystemlineSeparator();
			(*_this)globals = (*_this)globalsaddLast(generated);
			return new_Some<CStructMember>(new_EmptyStructMember());
		}
	}
	return new_None<CStructMember>();
}
char* compileMethodSegment_Main(void* _ref, char* input, int indent){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputstrip()]*/ stripped = inputstrip();
	if (strippedisEmpty()) 
		return "";
	/*JExpressionWrapper[content=(*_this)compileConditional("if", indent, stripped)]*/ maybeIf = (*_this)compileConditional("if", indent, stripped);
	if (maybeIf.variant = ?.SomeVariant) 
		return result;
	/*JExpressionWrapper[content=(*_this)compileConditional("while", indent, stripped)]*/ maybeWhile = (*_this)compileConditional("while", indent, stripped);
	if (maybeWhile.variant = ?.SomeVariant) 
		return result;
	if (strippedendsWith(";")) {
		/*JExpressionWrapper[content=strippedsubstring(0, strippedlength() - 1)]*/ substring = strippedsubstring(0, strippedlength() - 1);
		return generateIndent(indent) + (*_this)compileMethodStatement(substring) + ";";
	}
	if (strippedstartsWith("else ")) {
		/*JExpressionWrapper[content=strippedsubstring("else "length())strip()]*/ substring = strippedsubstring("else "length())strip();
		if (substringstartsWith("{") && substringendsWith("}")) {
			/*JExpressionWrapper[content=substringsubstring(1, substringlength() - 1)]*/ substring1 = substringsubstring(1, substringlength() - 1);
			return generateIndent(indent) + "else {" + (*_this)compileMethodsSegments(substring1, indent + 1) + generateIndent(indent) + "}";
		}
		/*else return generateIndent(indent) + "else " + this.compileMethodSegment(substring, indent)*/;
	}
	if (strippedstartsWith("//")) 
		return generateIndent(indent) + stripped;
	return SystemlineSeparator() + "\t" + wrap(stripped);
}
auto lambda37(void* _ref, auto slice){
	return !sliceisEmpty();
}
Option<char*> compileConditional_Main(void* _ref, char* type, int indent, char* input){
	Main* _this = (Main*) _ref;
	if (inputstartsWith(type)) {
		/*JExpressionWrapper[content=inputsubstring(typelength())strip()]*/ substring = inputsubstring(typelength())strip();
		if (substringstartsWith("(")) {
			/*JExpressionWrapper[content=substringsubstring(1)strip()]*/ afterConditionStart = substringsubstring(1)strip();
			/*JExpressionWrapper[content=(*_this)divide(afterConditionStart, new_EscapedFolder(new_ConditionEndLocator()))map(F? { alloc(String), F?Table { strip }})filter(lambda37)toList()]*/ divisions = (*_this)divide(afterConditionStart, new_EscapedFolder(new_ConditionEndLocator()))map(F? { alloc(String), F?Table { strip }})filter(lambda37)toList();
			if (divisionssize() < 2) 
				return new_None<char*>();
			/*JExpressionWrapper[content=divisionsgetFirst()]*/ first = divisionsgetFirst();
			/*JExpressionWrapper[content=(*_this)joinStrings("", divisionssubList(1, divisionssize()))]*/ maybeWithBraces = (*_this)joinStrings("", divisionssubList(1, divisionssize()));
			if (!firstendsWith(")")) 
				return new_None<char*>();
			/*JExpressionWrapper[content=firstsubstring(0, firstlength() - 1)]*/ condition = firstsubstring(0, firstlength() - 1);
			if (maybeWithBracesstartsWith("{") && maybeWithBracesendsWith("}")) {
				/*JExpressionWrapper[content=maybeWithBracessubstring(1, maybeWithBraceslength() - 1)]*/ content = maybeWithBracessubstring(1, maybeWithBraceslength() - 1);
				return new_Some<char*>(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" + (*_this)compileMethodsSegments(content, indent + 1) + generateIndent(indent) + "}");
			}
			return new_Some<char*>(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") " + (*_this)compileMethodSegment(maybeWithBraces, indent + 1));
		}
	}
	return new_None<char*>();
}
char* compileMethodStatement_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputstrip()]*/ stripped = inputstrip();
	if (strippedequals("break")) 
		return "break";
	if (strippedstartsWith("return ")) 
		return "return " + (*_this)compileExpressionOrPlaceholder(strippedsubstring("return "length()));
	/*JExpressionWrapper[content=(*_this)compileAssignment(stripped)]*/ maybeAssignment = (*_this)compileAssignment(stripped);
	if (maybeAssignment.variant = ?.SomeVariant) 
		return assignment;
	/*JExpressionWrapper[content=(*_this)compileInvokable(stripped)]*/ maybeInvokable = (*_this)compileInvokable(stripped);
	if (maybeInvokable.variant = ?.SomeVariant) 
		return value;
	/*JExpressionWrapper[content=(*_this)post(stripped, "++")]*/ instance = (*_this)post(stripped, "++");
	if (instance.variant = ?.SomeVariant) 
		return x;
	/*JExpressionWrapper[content=(*_this)post(stripped, "--")]*/ instance0 = (*_this)post(stripped, "--");
	if (instance0.variant = ?.SomeVariant) 
		return x;
	/*JExpressionWrapper[content=(*_this)parseDeclaration(input)]*/ maybeDeclaration = (*_this)parseDeclaration(input);
	if (maybeDeclaration.variant = ?.SomeVariant) 
		return declarationtoCDeclaration()generate();
	return wrap(stripped);
}
Option<char*> compileAssignment_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=strippedindexOf("=")]*/ index = strippedindexOf("=");
	if (index >= 0) {
		/*JExpressionWrapper[content=strippedsubstring(0, index)]*/ destination = strippedsubstring(0, index);
		/*JExpressionWrapper[content=strippedsubstring(index + 1)]*/ substring1 = strippedsubstring(index + 1);
		/*JExpressionWrapper[content=(*_this)parseAssignable(destination)]*/ assignable = (*_this)parseAssignable(destination);
		/*JExpressionWrapper[content=(*_this)parseExpression(substring1)]*/ maybeSource = (*_this)parseExpression(substring1);
		if (maybeSource.variant = ?.SomeVariant) 
			return new_Some<char*>((*_this)transformAssignable(assignable, source)generate() + " = " + sourcetoCAssignable()generate());
	}
	return new_None<char*>();
}
CAssignable transformAssignable_Main(void* _ref, JAssignable assignable, JExpression source){
	Main* _this = (Main*) _ref;
	return _switch;
}
auto lambda38(void* _ref, auto def){
	return defnameequals(value);
}
JType resolveExpression_Main(void* _ref, JExpression source){
	Main* _this = (Main*) _ref;
	if (source.variant = ?.Identifier(var value)Variant) {
		/*JExpressionWrapper[content=(*_this)environmentiter()flatMap(F? { alloc(List), F?Table { iter }})filter(lambda38)next()]*/ maybeFound = (*_this)environmentiter()flatMap(F? { alloc(List), F?Table { iter }})filter(lambda38)next();
		if (maybeFound.variant = ?.SomeVariant) 
			return foundtype;
	}
	return new_Placeholder(sourcetoString());
}
auto lambda39(void* _ref, auto ()){
	return new_Placeholder(input);
}
auto lambda41(void* _ref, auto value){
	return value;
}
auto lambda40(void* _ref, auto ()){
	return (*_this)parseDeclaration(input)map(lambda41);
}
auto lambda42(void* _ref, auto value){
	return value;
}
auto lambda43(void* _ref, auto value){
	return value;
}
auto lambda44(void* _ref, auto value){
	return value;
}
auto lambda46(void* _ref, auto value){
	return value;
}
auto lambda45(void* _ref, auto ()){
	return (*_this)parseDeclaration(input)map(lambda46);
}
auto lambda47(void* _ref, auto value){
	return value;
}
auto lambda49(void* _ref, auto value){
	return value;
}
auto lambda48(void* _ref, auto ()){
	return (*_this)parseDeclaration(input)map(lambda49);
}
auto lambda50(void* _ref, auto value){
	return value;
}
auto lambda51(void* _ref, auto ()){
	return new_Placeholder(input);
}
auto lambda53(void* _ref, auto value){
	return value;
}
auto lambda52(void* _ref, auto ()){
	return (*_this)parseDeclaration(input)map(lambda53);
}
auto lambda54(void* _ref, auto value){
	return value;
}
JAssignable parseAssignable_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return /*this
				.parseExpression(input)
				.<JAssignable>map(value -> value)
				.or(() -> this.parseDeclaration(input).map(value -> value))
				.orElseGet(() -> new Placeholder(input))*/;
}
Option<char*> post_Main(void* _ref, char* stripped, char* slice){
	Main* _this = (Main*) _ref;
	if (strippedendsWith(slice)) {
		/*JExpressionWrapper[content=strippedsubstring(0, strippedlength() - 2)]*/ instance = strippedsubstring(0, strippedlength() - 2);
		return new_Some<char*>((*_this)compileExpressionOrPlaceholder(instance) + slice);
	}
	return new_None<char*>();
}
auto lambda55(void* _ref, auto ()){
	return wrap(input);
}
char* compileExpressionOrPlaceholder_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return (*_this)parseCExpression(input)map(F? { alloc(CExpression), F?Table { generate }})orElseGet(lambda55);
}
Option<CExpression> parseCExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return (*_this)parseExpression(input)map(F? { alloc(JExpression), F?Table { toCExpression }});
}
auto lambda56(void* _ref, auto ()){
	return (*_this)compileOperator(stripped, " >= ");
}
auto lambda57(void* _ref, auto ()){
	return (*_this)compileOperator(stripped, " || ");
}
auto lambda58(void* _ref, auto ()){
	return (*_this)compileOperator(stripped, " && ");
}
auto lambda59(void* _ref, auto ()){
	return (*_this)compileOperator(stripped, " - ");
}
auto lambda60(void* _ref, auto ()){
	return (*_this)compileOperator(stripped, " + ");
}
auto lambda61(void* _ref, auto ()){
	return (*_this)compileOperator(stripped, " < ");
}
auto lambda62(void* _ref, auto ()){
	return (*_this)compileOperator(stripped, " != ");
}
Option<JExpression> parseExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputstrip()]*/ stripped = inputstrip();
	if (strippedequals("this")) 
		return new_Some<char*>("(*_this)")map(F? { alloc(JExpressionWrapper), F?Table { new }});
	if (strippedstartsWith("switch ")) 
		return new_Some<char*>("_switch")map(F? { alloc(JExpressionWrapper), F?Table { new }});
	/*JExpressionWrapper[content=strippedlastIndexOf("::")]*/ i2 = strippedlastIndexOf("::");
	if (i2 >= 0) {
		/*JExpressionWrapper[content=strippedsubstring(0, i2)]*/ substring = strippedsubstring(0, i2);
		/*JExpressionWrapper[content=strippedsubstring(i2 + 2)strip()]*/ name = strippedsubstring(i2 + 2)strip();
		if ((*_this)isIdentifier(name)) {
			/*JExpressionWrapper[content=(*_this)compileExpressionOrPlaceholder(substring)]*/ compiled = (*_this)compileExpressionOrPlaceholder(substring);
			/*JExpressionWrapper[content="F?"]*/ functionalInterfaceName = "F?";
			return new_Some<char*>(functionalInterfaceName + " { alloc(" + compiled + "), " + functionalInterfaceName + "Table { " + name + " }}")map(F? { alloc(JExpressionWrapper), F?Table { new }});
		}
	}
	if (strippedstartsWith("'") && strippedendsWith("'")) 
		return new_Some<char*>(stripped)map(F? { alloc(JExpressionWrapper), F?Table { new }});
	/*JExpressionWrapper[content=(*_this)compileLambda(stripped)]*/ maybeLambda = (*_this)compileLambda(stripped);
	if (maybeLambda.variant = ?.SomeVariant) 
		return maybeLambdamap(F? { alloc(JExpressionWrapper), F?Table { new }});
	/*JExpressionWrapper[content=strippedindexOf(".variant = ?."Variant)]*/ i3 = strippedindexOf(".variant = ?."Variant);
	if (i3 >= 0) {
		/*JExpressionWrapper[content=strippedsubstring(0, i3)]*/ substring = strippedsubstring(0, i3);
		/*JExpressionWrapper[content=strippedsubstring(i3 + ".variant = ?.".length()Variant)strip()]*/ substring1 = strippedsubstring(i3 + ".variant = ?.".length()Variant)strip();
		/*JExpressionWrapper[content=(*_this)parseCExpression(substring)map(F? { alloc(CExpression), F?Table { generate }})]*/ maybeInstance = (*_this)parseCExpression(substring)map(F? { alloc(CExpression), F?Table { generate }});
		if (maybeInstance.variant = ?.SomeVariant) {
			/*JExpressionWrapper[content=substring1indexOf(" < ")]*/ i4 = substring1indexOf(" < ");
			char* substring2;
			if (i4 >= 0) 
				substring2 = substring1substring(0, i4);
			else substring2 = substring1;
			return new_Some<char*>(instance + ".variant = ?." + substring2 + "Variant")map(F? { alloc(JExpressionWrapper), F?Table { new }});
		}
	}
	/*JExpressionWrapper[content=strippedlastIndexOf(".")]*/ i = strippedlastIndexOf(".");
	if (i >= 0) {
		/*JExpressionWrapper[content=strippedsubstring(0, i)]*/ instanceString = strippedsubstring(0, i);
		/*JExpressionWrapper[content=strippedsubstring(i + 1)strip()]*/ memberName = strippedsubstring(i + 1)strip();
		if ((*_this)isIdentifier(memberName)) {
			/*JExpressionWrapper[content=(*_this)parseExpression(instanceString)]*/ maybeInstance = (*_this)parseExpression(instanceString);
			if (maybeInstance.variant = ?.Some(var value)Variant) 
				return new_Some<JExpression>(new_JMemberAccess(value, memberName));
		}
	}
	/*JExpressionWrapper[content=(*_this)compileInvokable(stripped)]*/ maybeInvokable = (*_this)compileInvokable(stripped);
	if (maybeInvokable.variant = ?.SomeVariant) 
		return maybeInvokablemap(F? { alloc(JExpressionWrapper), F?Table { new }});
	/*JExpressionWrapper[content=(*_this)compileOperator(stripped, " == ")or(lambda62)or(lambda61)or(lambda60)or(lambda59)or(lambda58)or(lambda57)or(lambda56)]*/ maybeOperator = (*_this)compileOperator(stripped, " == ")or(lambda62)or(lambda61)or(lambda60)or(lambda59)or(lambda58)or(lambda57)or(lambda56);
	if (maybeOperator.variant = ?.SomeVariant) 
		return maybeOperatormap(F? { alloc(JExpressionWrapper), F?Table { new }});
	if ((*_this)isIdentifier(stripped)) 
		return new_Some<JExpression>(new_Identifier(stripped));
	if (strippedstartsWith("!")) {
		/*JExpressionWrapper[content=strippedsubstring(1)]*/ substring = strippedsubstring(1);
		/*JExpressionWrapper[content=(*_this)parseCExpression(substring)map(F? { alloc(CExpression), F?Table { generate }})]*/ maybeInstance = (*_this)parseCExpression(substring)map(F? { alloc(CExpression), F?Table { generate }});
		if (maybeInstance.variant = ?.SomeVariant) 
			return new_Some<char*>("!" + instance)map(F? { alloc(JExpressionWrapper), F?Table { new }});
	}
	if ((*_this)isNumber(stripped)) 
		return new_Some<char*>(stripped)map(F? { alloc(JExpressionWrapper), F?Table { new }});
	if (strippedstartsWith("\"") && strippedendsWith("\"")) 
		return new_Some<char*>(stripped)map(F? { alloc(JExpressionWrapper), F?Table { new }});
	return new_None<JExpression>();
}
auto lambda63(void* _ref, auto slice){
	return !sliceisEmpty();
}
auto lambda64(void* _ref, auto param){
	return "auto " + param;
}
Option<char*> compileLambda_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputindexOf("->")]*/ index = inputindexOf("->");
	if (index < 0) 
		return new_None<char*>();
	/*JExpressionWrapper[content=inputsubstring(0, index)strip()]*/ beforeContent = inputsubstring(0, index)strip();
	/*JExpressionWrapper[content=inputsubstring(index + 2)strip()]*/ maybeWithBraces = inputsubstring(index + 2)strip();
	List<char*> params;
	if ((*_this)isIdentifier(beforeContent)) 
		params = Listsof(beforeContent);
	else 
	if (beforeContentstartsWith("(") && beforeContent.endsWith(")")) {
		/*JExpressionWrapper[content=beforeContentsubstring(1, beforeContentlength() - 1)]*/ substring = beforeContentsubstring(1, beforeContentlength() - 1);
		params = (*_this)divide(substring, new_ValueFolder())map(F? { alloc(String), F?Table { strip }})filter(lambda63)toList();
	}
	/*else return new None<String>()*/;
	if (maybeWithBracesstartsWith("{") && maybeWithBracesendsWith("}")) {
		/*JExpressionWrapper[content=maybeWithBracessubstring(1, maybeWithBraceslength() - 1)]*/ content = maybeWithBracessubstring(1, maybeWithBraceslength() - 1);
		/*JExpressionWrapper[content=(*_this)compileMethodsSegments(content, 1)]*/ compiled = (*_this)compileMethodsSegments(content, 1);
		/*JExpressionWrapper[content=(*_this)generateName()]*/ generatedName = (*_this)generateName();
		/*JExpressionWrapper[content=paramsiter()map(lambda64)toList()addFirst("void* _ref")]*/ paramList = paramsiter()map(lambda64)toList()addFirst("void* _ref");
		/*JExpressionWrapper[content=(*_this)joinStrings(", ", paramList)]*/ joined = (*_this)joinStrings(", ", paramList);
		(*_this)functions = (*_this)functionsaddLast("auto " + generatedName + "(" + joined + "){" + compiled + SystemlineSeparator() + "}" + SystemlineSeparator());
		return new_Some<char*>(generatedName);
	}
	else {
		/*JExpressionWrapper[content=(*_this)generateName()]*/ generatedName = (*_this)generateName();
		(*_this)functions = (*_this)functionsaddLast("auto " + generatedName + "(void* _ref, auto " + beforeContent + ")" + "{" + (*_this)generateStatement("return " + (*_this)compileExpressionOrPlaceholder(maybeWithBraces)) + SystemlineSeparator() + "}" + SystemlineSeparator());
		return new_Some<char*>(generatedName);
	}
}
char* generateName_Main(void* _ref){
	Main* _this = (Main*) _ref;
	/*JMemberAccess[instance=JExpressionWrapper[content="lambda" + (*_this)], memberName=counter]*/ generatedName = "lambda" + (*_this)counter;
	(*_this)counter++;
	return generatedName;
}
Option<char*> compileOperator_Main(void* _ref, char* input, char* operator){
	Main* _this = (Main*) _ref;
	if (inputlength() < 3) 
		return new_None<char*>();
	if (!inputcontains(operator)) 
		return new_None<char*>();
	/*JExpressionWrapper[content=-1]*/ i1 = -1;
	/*JExpressionWrapper[content=0]*/ depth = 0;
	/*JExpressionWrapper[content=0]*/ i = 0;
	while (i < inputlength() - 1) {
		/*JExpressionWrapper[content=inputcharAt(i)]*/ c = inputcharAt(i);
		if (c == operatorcharAt(0)) 
			if (depth == 0) {
				i1 = i;
				break;
			}
		if (c == '(') 
			depth++;
		if (c == ')') 
			depth--;
		i++;
	}
	if (i1 >= 0) {
		/*JExpressionWrapper[content=inputsubstring(0, i1)]*/ leftString = inputsubstring(0, i1);
		/*JExpressionWrapper[content=inputsubstring(i1 + operatorlength())]*/ right = inputsubstring(i1 + operatorlength());
		if ((*_this)parseCExpression(leftString)map(F? { alloc(CExpression), F?Table { generate }}).variant = ?.SomeVariant) 
			if ((*_this)parseCExpression(right)map(F? { alloc(CExpression), F?Table { generate }}).variant = ?.SomeVariant) 
				return new_Some<char*>(leftCompiled + " " + operator + " " + rightCompiled);
	}
	return new_None<char*>();
}
Option<char*> compileInvokable_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	if (strippedendsWith(")")) {
		char* stripped1 = stripped;
		/*JExpressionWrapper[content=strippedlength()]*/ length = strippedlength();
		/*JExpressionWrapper[content=stripped1substring(0, length - 1)]*/ withoutEnd = stripped1substring(0, length - 1);
		/*JExpressionWrapper[content=(*_this)findCallerStart(withoutEnd)]*/ callerStart = (*_this)findCallerStart(withoutEnd);
		if (callerStart >= 0) {
			/*JExpressionWrapper[content=withoutEndsubstring(0, callerStart)]*/ callerString = withoutEndsubstring(0, callerStart);
			/*JExpressionWrapper[content=withoutEndsubstring(callerStart + 1)]*/ arguments = withoutEndsubstring(callerStart + 1);
			/*JExpressionWrapper[content=(*_this)divide(arguments, new_EscapedFolder(new_ValueFolder()))map(F? { alloc((*_this)), F?Table { compileExpressionOrPlaceholder }})collect(new_Joiner(", "))]*/ joinedArguments = (*_this)divide(arguments, new_EscapedFolder(new_ValueFolder()))map(F? { alloc((*_this)), F?Table { compileExpressionOrPlaceholder }})collect(new_Joiner(", "));
			/*JExpressionWrapper[content=(*_this)compileCaller(callerString)]*/ maybeCaller = (*_this)compileCaller(callerString);
			if (maybeCaller.variant = ?.SomeVariant) 
				return new_Some<char*>(value + "(" + joinedArguments + ")");
		}
	}
	return new_None<char*>();
}
int findCallerStart_Main(void* _ref, char* withoutEnd){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=-1]*/ callerStart = -1;
	/*JExpressionWrapper[content=0]*/ depth = 0;
	/*JExpressionWrapper[content=0]*/ i = 0;
	while (i < withoutEndlength()) {
		/*JExpressionWrapper[content=withoutEndcharAt(i)]*/ c = withoutEndcharAt(i);
		if (c == '(') {
			if (depth == 0) 
				callerStart = i;
			depth++;
		}
		if (c == ')') 
			depth--;
		i++;
	}
	return callerStart;
}
int isNumber_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	if (inputstartsWith(" - ")) 
		return (*_this)allDigits(inputsubstring(1));
	return (*_this)allDigits(input);
}
int allDigits_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return IntStreamrange(0, inputlength())mapToObj(F? { alloc(input), F?Table { charAt }})allMatch(F? { alloc(Character), F?Table { isDigit }});
}
Option<char*> compileCaller_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputstrip()]*/ stripped = inputstrip();
	/*JExpressionWrapper[content=(*_this)parseCExpression(stripped)map(F? { alloc(CExpression), F?Table { generate }})]*/ maybeExpression = (*_this)parseCExpression(stripped)map(F? { alloc(CExpression), F?Table { generate }});
	if (maybeExpression.variant = ?.SomeVariant) 
		return maybeExpression;
	if (strippedstartsWith("new ")) {
		/*JExpressionWrapper[content=strippedsubstring("new "length())]*/ type = strippedsubstring("new "length());
		return new_Some<char*>("new_" + (*_this)compileType(type));
	}
	return new_None<char*>();
}
Option<JDeclaration> parseDeclaration_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputstrip()]*/ stripped = inputstrip();
	/*JExpressionWrapper[content=strippedlastIndexOf(" ")]*/ nameSeparator = strippedlastIndexOf(" ");
	if (nameSeparator >= 0) {
		/*JExpressionWrapper[content=strippedsubstring(0, nameSeparator)strip()]*/ beforeName = strippedsubstring(0, nameSeparator)strip();
		/*JExpressionWrapper[content=strippedsubstring(nameSeparator + 1)strip()]*/ name = strippedsubstring(nameSeparator + 1)strip();
		/*JExpressionWrapper[content=(*_this)findTypeSeparator(beforeName)]*/ typeSeparator = (*_this)findTypeSeparator(beforeName);
		if (!(*_this)isIdentifier(name)) 
			return new_None<JDeclaration>();
		if (typeSeparator < 0) {
			/*JExpressionWrapper[content=(*_this)parseType(beforeName)]*/ type = (*_this)parseType(beforeName);
			return new_Some<JDeclaration>(new_JDeclaration(type, name));
		}
		/*JExpressionWrapper[content=beforeNamesubstring(0, typeSeparator)strip()]*/ beforeType = beforeNamesubstring(0, typeSeparator)strip();
		List<char*> copy = Listsempty();
		if (beforeTypeendsWith(">")) {
			/*JExpressionWrapper[content=beforeTypesubstring(0, beforeTypelength() - 1)]*/ substring = beforeTypesubstring(0, beforeTypelength() - 1);
			/*JExpressionWrapper[content=substringindexOf(" < ")]*/ i = substringindexOf(" < ");
			if (i >= 0) {
				/*JExpressionWrapper[content=substringsubstring(i + 1)]*/ substring2 = substringsubstring(i + 1);
				copy = (*_this)splitValues(substring2);
				beforeType = substringsubstring(0, i);
			}
		}
		List<char*> annotations = Listsempty();
		/*JExpressionWrapper[content=beforeTypelastIndexOf("\n")]*/ i = beforeTypelastIndexOf("\n");
		if (i >= 0) {
			annotations = (*_this)collectAnnotations(beforeTypesubstring(0, i));
			beforeType = beforeTypesubstring(i + 1)strip();
		}
		if ((*_this)isIdentifier(name)) {
			/*JExpressionWrapper[content=(*_this)parseType(beforeNamesubstring(typeSeparator + 1))]*/ type = (*_this)parseType(beforeNamesubstring(typeSeparator + 1));
			/*JExpressionWrapper[content=new_JDeclaration(annotations, copy, new_Some<char*>(beforeType), type, name)]*/ jDeclaration = new_JDeclaration(annotations, copy, new_Some<char*>(beforeType), type, name);
			return new_Some<JDeclaration>(jDeclaration);
		}
	}
	return new_None<JDeclaration>();
}
auto lambda65(void* _ref, auto slice){
	return slicesubstring(1);
}
auto lambda66(void* _ref, auto slice){
	return !sliceisEmpty();
}
List<char*> collectAnnotations_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return StreamsfromObjArray(inputsplit(Patternquote("\n")))filter(lambda66)map(lambda65)map(F? { alloc(String), F?Table { strip }})toList();
}
int findTypeSeparator_Main(void* _ref, char* beforeName){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=-1]*/ typeSeparator = -1;
	/*JExpressionWrapper[content=0]*/ depth = 0;
	/*JExpressionWrapper[content=0]*/ i = 0;
	while (i < beforeNamelength()) {
		/*JExpressionWrapper[content=beforeNamecharAt(i)]*/ c = beforeNamecharAt(i);
		if (c == ' ' && depth == 0) 
			typeSeparator = i;
		if (c == '<') 
			depth++;
		if (c == '>') 
			depth--;
		i++;
	}
	return typeSeparator;
}
char* compileType_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return transformType((*_this)parseType(input))generate();
}
JType parseType_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*JExpressionWrapper[content=inputstrip()]*/ stripped = inputstrip();
	/*switch (stripped) {
			case "boolean", "Boolean" -> {
				return JPrimitiveType.Boolean;
			}
			case "Integer" -> {
				return JPrimitiveType.Int;
			}
			case "void" -> {
				return JPrimitiveType.Void;
			}
			case "String" -> {
				return JPrimitiveType.String;
			}
			case "Character" -> {
				return JPrimitiveType.Char;
			}
			case "var" -> {
				return JPrimitiveType.Var;
			}
		}*/
	if (strippedendsWith("[]")) {
		/*JExpressionWrapper[content=strippedsubstring(0, strippedlength() - 2)]*/ slice = strippedsubstring(0, strippedlength() - 2);
		/*JExpressionWrapper[content=(*_this)parseType(slice)]*/ type = (*_this)parseType(slice);
		return new_JArrayType(type);
	}
	if (strippedendsWith(">")) {
		/*JExpressionWrapper[content=strippedsubstring(0, strippedlength() - 1)]*/ substring = strippedsubstring(0, strippedlength() - 1);
		/*JExpressionWrapper[content=substringindexOf(" < ")]*/ i = substringindexOf(" < ");
		if (i >= 0) {
			/*JExpressionWrapper[content=substringsubstring(0, i)]*/ base = substringsubstring(0, i);
			/*JExpressionWrapper[content=substringsubstring(i + 1)]*/ parameters = substringsubstring(i + 1);
			/*JExpressionWrapper[content=(*_this)divide(parameters, new_ValueFolder())map(F? { alloc((*_this)), F?Table { parseType }})toList()]*/ list = (*_this)divide(parameters, new_ValueFolder())map(F? { alloc((*_this)), F?Table { parseType }})toList();
			return new_JGenericType(base, list);
		}
	}
	if ((*_this)isIdentifier(stripped)) 
		return new_Identifier(stripped);
	return new_Placeholder(stripped);
}
