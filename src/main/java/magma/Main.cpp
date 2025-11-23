struct CPrimitiveType {
	/*???*/ content;
};
struct JPrimitiveType {/*Int, Void, Boolean, Char, Var*/
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
	Iter<T> (*iter)(void*);
	int (*isEmpty)(void*);
	List<T> (*addLast)(void*, T);
	int (*contains)(void*, T);
	List<T> (*addFirst)(void*, T);
	List<T> (*addAll)(void*, List<T>);
	int (*size)(void*);
	T (*getFirst)(void*);
	List<T> (*subList)(void*, int, int);
	List<T> (*clear)(void*);
	List<T> (*removeLast)(void*);
	List<T> (*mapLast)(void*, Function<T, T>);
	Iter<T> (*iterReversed)(void*);
};
template <typename T>
struct List {
	ListTable<T> table;
	void* data;
};
struct PathTable {
	Path (*resolveSibling)(void*, /*???*/);
	Option<IOError> (*writeString)(void*, /*???*/);
	Result</*???*/, IOError> (*readString)(void*);
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
	/*???*/ (*display)(void*);
};
struct IOError {
	IOErrorTable table;
	void* data;
};
struct CFunctionDeclarationTable {
	CFunctionDeclaration (*mapTypeParameters)(void*, F1R<List</*???*/>, List</*???*/>>);
	CFunctionDeclaration (*mapName)(void*, F1R</*???*/, /*???*/>);
	/*???*/ (*generate)(void*);
};
struct CFunctionDeclaration {
	CFunctionDeclarationTable table;
	void* data;
};
struct CAssignableTable {
	/*???*/ (*generate)(void*);
};
struct CAssignable {
	CAssignableTable table;
	void* data;
};
enum JTypeVariant {
	IdentifierVariant,
	JArrayTypeVariant,
	JFunctionalTypeVariant,
	JGenericTypeVariant,
	JObjectTypeVariant,
	JPrimitiveTypeVariant,
	JRecursiveTypeVariant,
	PlaceholderVariant
};
union JTypeData {
	Identifier Identifier;
	JArrayType JArrayType;
	JFunctionalType JFunctionalType;
	JGenericType JGenericType;
	JObjectType JObjectType;
	JPrimitiveType JPrimitiveType;
	JRecursiveType JRecursiveType;
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
enum JCallerVariant {
	JConstructionVariant,
	JExpressionVariant
};
union JCallerData {
	JConstruction JConstruction;
	JExpression JExpression;
};
struct JCaller {
	JCallerVariant variant;
	JCallerData data;
};
struct StringBuilders {
};
struct StringBuilder {
	List<char> list;
};
template <typename T>
struct Iter {
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
	/*???*/ input;
	StringBuilder buffer;
	List</*???*/> segments;
	int index;
	int depth;
};
struct CPointerType {
	CType type;
};
struct CTemplateType {
	/*???*/ base;
	List<CType> list;
};
struct CQuantity {
	CExpression expression;
};
struct CDereference {
	CExpression expression;
};
struct Identifier {
	/*???*/ value;
};
struct Placeholder {
	/*???*/ input;
};
struct JConstructor {
	/*???*/ type;
};
struct JDeclaration {
	List</*???*/> annotations;
	List</*???*/> typeParameters;
	Option</*???*/> maybeBeforeType;
	JType type;
	/*???*/ name;
};
struct F1RDeclaration {
	CType type;
	/*???*/ name;
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
	F1R<T, Iter<R>> mapper;
	Option<Iter<R>> maybeCurrent;
};
template <typename T>
struct EmptyHead {
};
template <typename T>
struct AnyMatch {
	F1R<T, int> predicate;
};
struct Joiner {
	/*???*/ delimiter;
};
template <typename T>
struct ListCollector {
};
struct Paths {
};
struct CDeclaration {
	List</*???*/> typeParameters;
	CType type;
	/*???*/ name;
};
struct JExpressionWrapper {
	/*???*/ content;
};
struct CExpressionWrapper {
	/*???*/ content;
};
struct JArrayType {
	JType type;
};
struct JGenericType {
	/*???*/ base;
	List<JType> typeArguments;
};
struct CPointerAccess {
	CExpression instance;
	/*???*/ fieldName;
};
struct CFieldAccess {
	CExpression instance;
	/*???*/ fieldName;
};
struct JMemberAccess {
	JExpression instance;
	/*???*/ memberName;
};
struct JConstruction {
	JType jType;
};
struct CInvocation {
	CExpression expression;
	List<CExpression> cArguments;
};
struct JInvokable {
	JCaller caller;
	List<JExpression> arguments;
};
struct JFunctionalType {
	List<JType> parameterTypes;
	JType returnType;
};
struct Environment {/*private List<Frame> frames = new JavaList<Frame>*/
};
struct Frame {
	Option</*???*/> maybeName;
	List<JDeclaration> definitions;
};
struct JObjectType {
	/*???*/ name;
	List<JDeclaration> members;
};
struct JRecursiveType {/*private Option<JType> internal = new None<JType>*/
};
struct Main {/*private interface CExpression extends CAssignable {}*//*private static final JType StringType = JRecursiveType.create*/
	List</*???*/> functionDeclarations;
	List</*???*/> globals;
	List</*???*/> structures;
	List</*???*/> functions;
	int counter;
};
CPrimitiveType CPrimitiveTypeVoid = new_CPrimitiveType("void");
CPrimitiveType CPrimitiveTypeChar = new_CPrimitiveType("char");
CPrimitiveType CPrimitiveTypeInt = new_CPrimitiveType("int");
CPrimitiveType<> new_CPrimitiveType(/*???*/ content);
/*???*/ generate_CPrimitiveType(void* _ref);
/*???*/ toBaseName_CPrimitiveType(void* _ref);
template <typename T>
Option<T> next_Head(void* _ref);
template <typename T>
Iter<T> iter_List(void* _ref);
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
template <typename T>
List<T> removeLast_List(void* _ref);
template <typename T>
List<T> mapLast_List(void* _ref, Function<T, T> mapper);
template <typename T>
Iter<T> iterReversed_List(void* _ref);
Path resolveSibling_Path(void* _ref, /*???*/ sibling);
Option<IOError> writeString_Path(void* _ref, /*???*/ output);
Result</*???*/, IOError> readString_Path(void* _ref);
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
Iter<T> iter_Option(void* _ref);
template <typename T>
Option<T> or_Option(void* _ref, FR<Option<T>> other);
template <typename T>
Tuple<int, T> toTuple_Option(void* _ref, FR<T> other);
template <typename T0, typename R>
R apply_F1R(void* _ref, T0 value);
template <typename R, typename T, typename X>
Result<R, X> mapValue_Result(void* _ref, F1R<T, R> mapper);
/*???*/ generate_CType(void* _ref);
/*???*/ toBaseName_CType(void* _ref);
/*???*/ generate_CStructMember(void* _ref);
State apply_Folder(void* _ref, State state, char character);
template <typename A, typename B, typename R>
R apply_F2R(void* _ref, A a, B b);
template <typename T, typename C>
C createInitial_Collector(void* _ref);
template <typename T, typename C>
C fold_Collector(void* _ref, C c, T t);
/*???*/ display_IOError(void* _ref);
CFunctionDeclaration mapTypeParameters_CFunctionDeclaration(void* _ref, F1R<List</*???*/>, List</*???*/>> mapper);
CFunctionDeclaration mapName_CFunctionDeclaration(void* _ref, F1R</*???*/, /*???*/> mapper);
/*???*/ generate_CFunctionDeclaration(void* _ref);
/*???*/ generate_CAssignable(void* _ref);
CAssignable toAssignable_Main(void* _ref);
CExpression toExpression_JCaller(void* _ref);
StringBuilder empty_StringBuilders(void* _ref);
StringBuilder appendChar_StringBuilder(void* _ref, char next);
StringBuilder clear_StringBuilder(void* _ref);
StringBuilder appendString_StringBuilder(void* _ref, /*???*/ chars);
/*???*/ toString_StringBuilder(void* _ref);
template <typename T, typename T>
Iter<T> of_Iter(void* _ref, T value);
template <typename T, typename T>
Iter<T> empty_Iter(void* _ref);
template <typename R, typename T>
Iter<R> map_Iter(void* _ref, F1R<T, R> mapper);
template <typename R, typename T>
R fold_Iter(void* _ref, R initial, F2R<R, T, R> folder);
template <typename C, typename T>
C collect_Iter(void* _ref, Collector<T, C> collector);
template <typename T>
List<T> toList_Iter(void* _ref);
template <typename T>
Iter<T> filter_Iter(void* _ref, F1R<T, int> predicate);
template <typename R, typename T>
Iter<R> flatMap_Iter(void* _ref, F1R<T, Iter<R>> mapper);
template <typename T>
Option<T> next_Iter(void* _ref);
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
State<> new_State(/*???*/ input);
int isShallow_State(void* _ref);
int isLevel_State(void* _ref);
State append_State(void* _ref, char next);
Option<char> pop_State(void* _ref);
State advance_State(void* _ref);
State enter_State(void* _ref);
State exit_State(void* _ref);
Iter</*???*/> stream_State(void* _ref);
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref);
Option<State> popAndAppendToOption_State(void* _ref);
Option<char> peek_State(void* _ref);
/*???*/ generate_CPointerType(void* _ref);
/*???*/ toBaseName_CPointerType(void* _ref);
/*???*/ generate_CTemplateType(void* _ref);
/*???*/ toBaseName_CTemplateType(void* _ref);
/*???*/ generate_CQuantity(void* _ref);
/*???*/ generate_CDereference(void* _ref);
/*???*/ generate_Identifier(void* _ref);
/*???*/ toBaseName_Identifier(void* _ref);
CExpression toExpression_Identifier(void* _ref);
/*???*/ generate_Placeholder(void* _ref);
/*???*/ toBaseName_Placeholder(void* _ref);
CAssignable toCAssignable_Placeholder(void* _ref);
CType toCType_Placeholder(void* _ref);
JDeclaration<> new_JDeclaration(JType type, /*???*/ name);
JDeclaration mapName_JDeclaration(void* _ref, F1R</*???*/, /*???*/> mapper);
CDeclaration toCDeclaration_JDeclaration(void* _ref);
CAssignable toCAssignable_JDeclaration(void* _ref);
JDeclaration withType_JDeclaration(void* _ref, JType type);
/*???*/ generate_F1RDeclaration(void* _ref);
/*???*/ generate_EmptyStructMember(void* _ref);
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
Iter<T> iter_Some(void* _ref);
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
Iter<T> iter_None(void* _ref);
template <typename T>
Option<T> or_None(void* _ref, FR<Option<T>> other);
template <typename T>
Tuple<int, T> toTuple_None(void* _ref, FR<T> other);
State apply_ConditionEndLocator(void* _ref, State state, char c);
/*???*/ generate_CField(void* _ref);
template <typename T>
Iter<T> fromObjArray_Streams(void* _ref, T* elements);
Iter<char> fromCharArray_Streams(void* _ref, char* array);
template <typename T, typename R>
Option<R> next_MapHead(void* _ref);
template <typename T>
SingleHead<T> new_SingleHead(T value);
template <typename T>
Option<T> next_SingleHead(void* _ref);
template <typename T, typename R>
FlatMapHead<T, R> new_FlatMapHead(Head<T> head, F1R<T, Iter<R>> mapper);
template <typename T, typename R>
Option<R> next_FlatMapHead(void* _ref);
template <typename T>
Option<T> next_EmptyHead(void* _ref);
template <typename T>
int createInitial_AnyMatch(void* _ref);
template <typename T>
int fold_AnyMatch(void* _ref, int aBoolean, T t);
Joiner<> new_Joiner();
/*???*/ createInitial_Joiner(void* _ref);
/*???*/ fold_Joiner(void* _ref, /*???*/ current, /*???*/ element);
template <typename T>
List<T> createInitial_ListCollector(void* _ref);
template <typename T>
List<T> fold_ListCollector(void* _ref, List<T> tList, T t);
Path get_Paths(/*???*/ first, /*String...*/ more);
CDeclaration<> new_CDeclaration(CType type, /*???*/ name);
CFunctionDeclaration mapName_CDeclaration(void* _ref, F1R</*???*/, /*???*/> mapper);
CFunctionDeclaration mapTypeParameters_CDeclaration(void* _ref, F1R<List</*???*/>, List</*???*/>> mapper);
/*???*/ generate_CDeclaration(void* _ref);
CExpression toExpression_JExpressionWrapper(void* _ref);
CAssignable toAssignable_JExpressionWrapper(void* _ref);
/*???*/ generate_CExpressionWrapper(void* _ref);
CType toCType_JArrayType(void* _ref);
CType toCType_JGenericType(void* _ref);
/*???*/ generate_CPointerAccess(void* _ref);
/*???*/ generate_CFieldAccess(void* _ref);
CExpression toExpression_JMemberAccess(void* _ref);
CExpression toExpression_JConstruction(void* _ref);
/*???*/ generate_CInvocation(void* _ref);
CExpression toExpression_JInvokable(void* _ref);
JFunctionalType<> new_JFunctionalType(JType returnType);
/*private List<Frame> frames = new JavaList<Frame>*/();
Option<JDeclaration> resolveExpression_Environment(void* _ref, /*???*/ identifier);
template <typename T>
Tuple<Environment, T> withinScoped_Environment(void* _ref, F1R<Environment, Tuple<Environment, T>> supplier);
Environment defineAll_Environment(void* _ref, List<JDeclaration> declarations);
template <typename T>
Tuple<Environment, T> within_Environment(void* _ref, Supplier<T> supplier);
Environment define_Environment(void* _ref, JDeclaration declaration);
Option<JObjectType> resolveCurrent_Environment(void* _ref);
Environment withName_Environment(void* _ref, /*???*/ name);
Frame<> new_Frame(Option</*???*/> maybeName, List<JDeclaration> defined);
Frame<> new_Frame();
Frame defineAll_Frame(void* _ref, List<JDeclaration> declarations);
Option<JDeclaration> resolve_Frame(void* _ref, /*???*/ identifier);
Frame define_Frame(void* _ref, JDeclaration declaration);
Option<JObjectType> toStructureType_Frame(void* _ref);
Frame withName_Frame(void* _ref, /*???*/ name);
Option<JType> resolve_JObjectType(void* _ref, /*???*/ name);
/*private Option<JType> internal = new None<JType>*/();
JType create_JRecursiveType(void* _ref, F1R<JType, JType> mapper);
void set_JRecursiveType(void* _ref, JType created);
/*private static final JType StringType = JRecursiveType.create*/();
new Environment_Main(void* _ref);
Main<> new_Main();
/*???*/ generateTemplateString_Main(void* _ref, List</*???*/> typeParameters);
/*???*/ wrap_Main(void* _ref, /*???*/ input);
void main_Main(void* _ref, /*???*/* args);
/*???*/ generateStatement_Main(void* _ref, int depth, /*???*/ content);
/*???*/ generateIndent_Main(void* _ref, int depth);
CType transformType_Main(void* _ref, JType jType);
CType transformPrimitiveType_Main(void* _ref, JPrimitiveType type);
Option<IOError> run_Main(void* _ref);
/*???*/ compile_Main(void* _ref, /*???*/ input);
/*???*/ joinStrings_Main(void* _ref, /*???*/ delimiter, List</*???*/> structures);
/*???*/ compileStatements_Main(void* _ref, /*???*/ input, F1R</*???*/, /*???*/> mapper);
/*???*/ compileAll_Main(void* _ref, /*???*/ input, F1R</*???*/, /*???*/> mapper, Folder folder);
Iter</*???*/> divide_Main(void* _ref, /*???*/ input, Folder folder);
State foldStatement_Main(void* _ref, State current, char next);
/*???*/ compileRootSegment_Main(void* _ref, /*???*/ input);
Option<CStructMember> compileStructure_Main(void* _ref, /*???*/ type, /*???*/ stripped);
/*???*/ getString_Main(void* _ref, CType implementee, /*???*/ name, /*???*/ joinedTypeParameters, /*???*/ templateString);
/*???*/ joinTypeParameters_Main(void* _ref, List</*???*/> typeParameters);
/*???*/ generateStatement_Main(void* _ref, /*???*/ content);
List</*???*/> splitValues_Main(void* _ref, /*???*/ input);
int isIdentifier_Main(void* _ref, /*???*/ input);
Option<CStructMember> compileClassSegment_Main(void* _ref, /*???*/ input, /*???*/ structName, List</*???*/> typeParameters, List</*???*/> variants);
Option<CStructMember> compileMethod_Main(void* _ref, /*???*/ structName, List</*???*/> typeParameters, List</*???*/> variants, /*???*/ input);
CType toConstructorReturnType_Main(void* _ref, /*???*/ base, List</*???*/> typeParameters);
/*???*/ compileMethodsSegments_Main(void* _ref, /*???*/ inputContent, int indent);
/*???*/ generateCase_Main(void* _ref, JDeclaration declaration, /*???*/ variant);
JMethodDeclaration parseMethodDeclaration_Main(void* _ref, /*???*/ declaration, /*???*/ structName);
JMethodDeclaration toInterface_Main(void* _ref, JDeclaration value);
Option<JMethodDeclaration> parseConstructor_Main(void* _ref, /*???*/ declaration, /*???*/ structName);
Option<CStructMember> compileEnumValues_Main(void* _ref, /*???*/ input, /*???*/ structName);
Option<CStructMember> compileEnumValue_Main(void* _ref, /*???*/ structName, /*???*/ enumValue);
/*???*/ compileMethodSegment_Main(void* _ref, /*???*/ input, int indent);
Option</*???*/> compileConditional_Main(void* _ref, /*???*/ type, int indent, /*???*/ input);
/*???*/ compileMethodStatement_Main(void* _ref, /*???*/ input);
Option</*???*/> compileAssignment_Main(void* _ref, /*???*/ stripped);
CAssignable transformAssignable_Main(void* _ref, JAssignable assignable, JExpression source);
JType resolveType_Main(void* _ref, JExpression source, JType type);
JType resolveExpression_Main(void* _ref, JExpression source);
JType getJType_Main(void* _ref, JMemberAccess access, JObjectType type, JType instanceType);
JType resolveCaller_Main(void* _ref, JCaller caller);
JAssignable parseAssignable_Main(void* _ref, /*???*/ input);
Option</*???*/> post_Main(void* _ref, /*???*/ stripped, /*???*/ slice);
/*???*/ compileExpressionOrPlaceholder_Main(void* _ref, /*???*/ input);
Option<CExpression> parseCExpression_Main(void* _ref, /*???*/ input);
Option<JExpression> parseExpression_Main(void* _ref, /*???*/ input);
Option</*???*/> compileLambda_Main(void* _ref, /*???*/ input);
/*???*/ generateName_Main(void* _ref);
Option</*???*/> compileOperator_Main(void* _ref, /*???*/ input, /*???*/ operator);
Option<JExpression> parseInvokable_Main(void* _ref, /*???*/ stripped);
int findCallerStart_Main(void* _ref, /*???*/ withoutEnd);
int isNumber_Main(void* _ref, /*???*/ input);
int allDigits_Main(void* _ref, /*???*/ input);
Option<JCaller> parseCaller_Main(void* _ref, /*???*/ input);
Option<JDeclaration> parseDeclaration_Main(void* _ref, /*???*/ input);
List</*???*/> collectAnnotations_Main(void* _ref, /*???*/ input);
int findTypeSeparator_Main(void* _ref, /*???*/ beforeName);
JType parseType_Main(void* _ref, /*???*/ input);
CType toCType_CPrimitiveType(void* _ref){
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	CTypeData data;
	data.CPrimitiveType = _this;
	return { CPrimitiveTypeVariant, data };
}
CPrimitiveType<> new_CPrimitiveType(/*???*/ content){
	CPrimitiveType _this;
	_this->content = (*_this);
	return _this;
}
/*???*/ generate_CPrimitiveType(void* _ref){
	CPrimitiveType* _this = (CPrimitiveType*) _ref;
	return _this->content;
}
/*???*/ toBaseName_CPrimitiveType(void* _ref){
	CPrimitiveType* _this = (CPrimitiveType*) _ref;
	return _this->content;
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
Iter<T> iter_List(void* _ref){
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
template <typename T>
List<T> removeLast_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.removeLast(_this->data);
}
template <typename T>
List<T> mapLast_List(void* _ref, Function<T, T> mapper){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.mapLast(_this->data, mapper);
}
template <typename T>
Iter<T> iterReversed_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.iterReversed(_this->data);
}
Path resolveSibling_Path(void* _ref, /*???*/ sibling){
	Path* _this = (Path*) _ref;
	return _this->table.resolveSibling(_this->data, sibling);
}
Option<IOError> writeString_Path(void* _ref, /*???*/ output){
	Path* _this = (Path*) _ref;
	return _this->table.writeString(_this->data, output);
}
Result</*???*/, IOError> readString_Path(void* _ref){
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
Iter<T> iter_Option(void* _ref){
	Option<T>* _this = (Option<T>*) _ref;
	Iter<T> _ret;
	switch (_this->variant) {
		case NoneVariant:
			_ret = iter_None(&(_this->data.None));
			break;
		case SomeVariant:
			_ret = iter_Some(&(_this->data.Some));
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
/*???*/ generate_CType(void* _ref){
	CType* _this = (CType*) _ref;
	/*???*/ _ret;
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
/*???*/ toBaseName_CType(void* _ref){
	CType* _this = (CType*) _ref;
	/*???*/ _ret;
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
/*???*/ generate_CStructMember(void* _ref){
	CStructMember* _this = (CStructMember*) _ref;
	/*???*/ _ret;
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
/*???*/ display_IOError(void* _ref){
	IOError* _this = (IOError*) _ref;
	return _this->table.display(_this->data);
}
CFunctionDeclaration mapTypeParameters_CFunctionDeclaration(void* _ref, F1R<List</*???*/>, List</*???*/>> mapper){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return (*_this);
}
CFunctionDeclaration mapName_CFunctionDeclaration(void* _ref, F1R</*???*/, /*???*/> mapper){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return (*_this);
}
/*???*/ generate_CFunctionDeclaration(void* _ref){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return _this->table.generate(_this->data);
}
/*???*/ generate_CAssignable(void* _ref){
	CAssignable* _this = (CAssignable*) _ref;
	return _this->table.generate(_this->data);
}
CAssignable toAssignable_Main(void* _ref){
	Main* _this = (Main*) _ref;
	return _this->toExpression((*_this));
	/*}

		CExpression toExpression()*/;
}
CExpression toExpression_JCaller(void* _ref){
	JCaller* _this = (JCaller*) _ref;
	CExpression _ret;
	switch (_this->variant) {
		case JConstructionVariant:
			_ret = toExpression_JConstruction(&(_this->data.JConstruction));
			break;
		case JExpressionVariant:
			_ret = toExpression_JExpression(&(_this->data.JExpression));
			break;
	}
	return _ret;
}
StringBuilder empty_StringBuilders(void* _ref){
	StringBuilders* _this = (StringBuilders*) _ref;
	return new_StringBuilder((*_this).empty((*_this)));
}
StringBuilder appendChar_StringBuilder(void* _ref, char next){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder(_this->list.addLast((*_this)));
}
StringBuilder clear_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder(_this->list.clear((*_this)));
}
StringBuilder appendString_StringBuilder(void* _ref, /*???*/ chars){
	StringBuilder* _this = (StringBuilder*) _ref;
	return (*_this).fromCharArray((*_this).toCharArray((*_this))).fold((*_this), F? { alloc((*_this)), F?Table { appendChar }});
}
/*???*/ toString_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return _this->list.iter((*_this)).map(F? { alloc((*_this)), F?Table { valueOf }}).collect(new_Joiner((*_this)));
}
template <typename T, typename T>
Iter<T> of_Iter(void* _ref, T value){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter<T>(new_SingleHead<T>((*_this)));
}
template <typename T, typename T>
Iter<T> empty_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter<T>(new_EmptyHead<T>((*_this)));
}
template <typename R, typename T>
Iter<R> map_Iter(void* _ref, F1R<T, R> mapper){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter<R>(new_MapHead<T, R>(_this->head, (*_this)));
}
auto lambda0(void* _ref, auto element){
	return (*_this).apply((*_this), (*_this));
}
auto lambda1(void* _ref, auto ()){
	return (*_this);
}
template <typename R, typename T>
R fold_Iter(void* _ref, R initial, F2R<R, T, R> folder){
	Iter<T>* _this = (Iter<T>*) _ref;
	R current = (*_this);
	while ((*_this)) {
		R finalCurrent = (*_this);
		/*Not a functional type: Placeholder[input=Cannot access member 'toTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Member 'head' not defined in 'JObjectType[name=Iter, members=magma.Main$JavaList@3d82c5f3]']', not an object.]]', not an object.]]', not an object.]*/ tuple = _this->head.next((*_this)).map(lambda0).toTuple(lambda1);
		if ((*_this).left) 
			(*_this) = (*_this).right;
		return current;
	}
}
template <typename C, typename T>
C collect_Iter(void* _ref, Collector<T, C> collector){
	Iter<T>* _this = (Iter<T>*) _ref;
	return _this->fold((*_this).createInitial((*_this)), F? { alloc((*_this)), F?Table { fold }});
}
template <typename T>
List<T> toList_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return _this->collect(new_ListCollector<T>((*_this)));
}
auto lambda2(void* _ref, auto element){
	if ((*_this).apply((*_this))) 
		return new_Iter<T>(new_SingleHead<T>((*_this)));
	return new_Iter<T>(new_EmptyHead<T>((*_this)));
}
template <typename T>
Iter<T> filter_Iter(void* _ref, F1R<T, int> predicate){
	Iter<T>* _this = (Iter<T>*) _ref;
	return _this->flatMap(lambda2);
}
template <typename R, typename T>
Iter<R> flatMap_Iter(void* _ref, F1R<T, Iter<R>> mapper){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter<R>(new_FlatMapHead<T, R>(_this->head, (*_this)));
}
template <typename T>
Option<T> next_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return _this->head.next((*_this));
}
Head<int> toHead_RangeHead(void* _ref){
	RangeHead _this = *((RangeHead*) _ref);
	HeadData data;
	data.RangeHead = _this;
	return { RangeHeadVariant, data };
}
RangeHead<> new_RangeHead(int length){
	RangeHead _this;
	_this->length = (*_this);
	_this->counter = 0;
	return _this;
}
Option<int> next_RangeHead(void* _ref){
	RangeHead* _this = (RangeHead*) _ref;
	if (_this->counter < (*_this).length) {
		int value = _this->counter;
		_this->counter++;
		return new_Some<int>((*_this));
	}
	/*else return new None<Integer>()*/;
}
template <typename T>
List<T> of_Lists(void* _ref, /*T...*/ elements){
	Lists* _this = (Lists*) _ref;
	return (*_this).fromObjArray((*_this)).collect(new_ListCollector<T>((*_this)));
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
	return new_Ok<R, X>((*_this).apply(_this->value));
}
State<> new_State(/*???*/ input){
	State _this;
	_this->input = (*_this);
	_this->index = 0;
	_this->buffer = (*_this).empty((*_this));
	_this->depth = 0;
	_this->segments = (*_this).empty((*_this));
	return _this;
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
	_this->buffer = _this->buffer.appendChar((*_this));
	return (*_this);
}
Option<char> pop_State(void* _ref){
	State* _this = (State*) _ref;
	if (_this->index < (*_this).input.length((*_this))) {
		/*Not a functional type: Placeholder[input=Member 'charAt' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ value = _this->input.charAt(_this->index);
		_this->index++;
		return new_Some<char>((*_this));
	}
	/*else return new None<Character>()*/;
}
State advance_State(void* _ref){
	State* _this = (State*) _ref;
	_this->segments = _this->segments.addLast(_this->buffer.toString((*_this)));
	_this->buffer = _this->buffer.clear((*_this));
	return (*_this);
}
State enter_State(void* _ref){
	State* _this = (State*) _ref;
	_this->depth = _this->depth + 1;
	return (*_this);
}
State exit_State(void* _ref){
	State* _this = (State*) _ref;
	_this->depth = _this->depth - 1;
	return (*_this);
}
Iter</*???*/> stream_State(void* _ref){
	State* _this = (State*) _ref;
	return _this->segments.iter((*_this));
}
auto lambda3(void* _ref, auto popped){
	/*Not a functional type: Placeholder[input=Member 'append' not defined in 'JObjectType[name=State, members=magma.Main$JavaList@4f8e5cde]']*/ appended = _this->append((*_this));
	return new_Tuple<State, char>((*_this), (*_this));
}
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref){
	State* _this = (State*) _ref;
	return _this->pop((*_this)).map(lambda3);
}
auto lambda4(void* _ref, auto tuple){
	return (*_this).left;
}
Option<State> popAndAppendToOption_State(void* _ref){
	State* _this = (State*) _ref;
	return _this->popAndAppendToTuple((*_this)).map(lambda4);
}
Option<char> peek_State(void* _ref){
	State* _this = (State*) _ref;
	if (_this->index < (*_this).input.length((*_this))) 
		return new_Some<char>(_this->input.charAt(_this->index));
	return new_None<char>((*_this));
}
CType toCType_CPointerType(void* _ref){
	CPointerType _this = *((CPointerType*) _ref);
	CTypeData data;
	data.CPointerType = _this;
	return { CPointerTypeVariant, data };
}
/*???*/ generate_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return _this->type.generate((*_this)) + "*";
}
/*???*/ toBaseName_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return _this->type.toBaseName((*_this)) + "_ptr";
}
CType toCType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CTypeData data;
	data.CTemplateType = _this;
	return { CTemplateTypeVariant, data };
}
/*???*/ generate_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'list' not defined in 'JObjectType[name=CTemplateType, members=magma.Main$JavaList@3b764bce]']', not an object.]]', not an object.]]', not an object.]*/ typeArguments = _this->list.iter((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner(", "));
	return _this->base + " < " + (*_this) + ">";
}
/*???*/ toBaseName_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	return _this->base;
}
CExpression toCExpression_CQuantity(void* _ref){
	CQuantity _this = *((CQuantity*) _ref);
	CExpressionData data;
	data.CQuantity = _this;
	return { CQuantityVariant, data };
}
/*???*/ generate_CQuantity(void* _ref){
	CQuantity* _this = (CQuantity*) _ref;
	return "(" + this.expression.generate() + ")";
}
CExpression toCExpression_CDereference(void* _ref){
	CDereference _this = *((CDereference*) _ref);
	CExpressionData data;
	data.CDereference = _this;
	return { CDereferenceVariant, data };
}
/*???*/ generate_CDereference(void* _ref){
	CDereference* _this = (CDereference*) _ref;
	return "*" + (*_this).expression.generate((*_this));
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
/*???*/ generate_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
/*???*/ toBaseName_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
CExpression toExpression_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return new_CQuantity(new_CDereference(new_Identifier("_this")));
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
/*???*/ generate_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return (*_this)(_this->input);
}
/*???*/ toBaseName_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return (*_this)(_this->input);
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
JDeclaration<> new_JDeclaration(JType type, /*???*/ name){
	JDeclaration _this;
	(*_this)((*_this).empty((*_this)), (*_this).empty((*_this)), new_None</*???*/>((*_this)), (*_this), (*_this));
	return _this;
}
JDeclaration mapName_JDeclaration(void* _ref, F1R</*???*/, /*???*/> mapper){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_JDeclaration(_this->annotations, _this->typeParameters, _this->maybeBeforeType, _this->type, (*_this).apply(_this->name));
}
CDeclaration toCDeclaration_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_CDeclaration(_this->typeParameters, (*_this)(_this->type), _this->name);
}
CAssignable toCAssignable_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	return _this->toCDeclaration((*_this));
}
JDeclaration withType_JDeclaration(void* _ref, JType type){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_JDeclaration(_this->annotations, _this->typeParameters, _this->maybeBeforeType, (*_this), _this->name);
}
CStructMember toCStructMember_F1RDeclaration(void* _ref){
	F1RDeclaration _this = *((F1RDeclaration*) _ref);
	CStructMemberData data;
	data.F1RDeclaration = _this;
	return { F1RDeclarationVariant, data };
}
/*???*/ generate_F1RDeclaration(void* _ref){
	F1RDeclaration* _this = (F1RDeclaration*) _ref;
	/*Unwrapped expression: "(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", ")) + ")"*/ joinedParameterTypes = "(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", ")) + ")";
	return _this->type.generate((*_this)) + " (*" + this.name + ")" + (*_this);
}
CStructMember toCStructMember_EmptyStructMember(void* _ref){
	EmptyStructMember _this = *((EmptyStructMember*) _ref);
	CStructMemberData data;
	data.EmptyStructMember = _this;
	return { EmptyStructMemberVariant, data };
}
/*???*/ generate_EmptyStructMember(void* _ref){
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
	if ((*_this).right == '\\') 
		return (*_this).left.popAndAppendToOption((*_this)).orElse((*_this).left);
	return (*_this).left;
}
State apply_EscapedFolder(void* _ref, State state, char next){
	EscapedFolder* _this = (EscapedFolder*) _ref;
	if ((*_this) == '\'') {
		/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = (*_this).append((*_this));
		return (*_this).popAndAppendToTuple((*_this)).map(lambda5).flatMap(F? { alloc((*_this)), F?Table { popAndAppendToOption }}).orElse((*_this));
	}
	if ((*_this) == '\"') {
		/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ current = (*_this).append((*_this));
		while ((*_this)) {
			/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/ maybeTuple = (*_this).popAndAppendToTuple((*_this));
			if (!(*_this)((*_this).variant = ?.SomeVariant)) 
				break;
			(*_this) = (*_this).left;
			/*Cannot access member 'right' in 'Placeholder[input=Undefined identifier: value]', not an object.*/ right = (*_this).right;
			if ((*_this) == '\\') 
				(*_this) = (*_this).popAndAppendToOption((*_this)).orElse((*_this));
			if ((*_this) == '\"') 
				break;
		}
		return (*_this);
	}
	return _this->folder.apply((*_this), (*_this));
}
Folder toFolder_ValueFolder(void* _ref){
	ValueFolder _this = *((ValueFolder*) _ref);
	FolderData data;
	data.ValueFolder = _this;
	return { ValueFolderVariant, data };
}
State apply_ValueFolder(void* _ref, State state, char next){
	ValueFolder* _this = (ValueFolder*) _ref;
	if ((*_this) == ',' && (*_this).isLevel((*_this))) 
		return (*_this).advance((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = (*_this).append((*_this));
	if ((*_this) == '-') {
		/*Not a functional type: Placeholder[input=Cannot access member 'peek' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/ peeked = (*_this).peek((*_this));
		if ((*_this).variant = ?.SomeVariant) 
			return (*_this).popAndAppendToOption((*_this)).orElse((*_this));
		return appended;
	}
	if ((*_this) == '<' || next == '(') 
		return (*_this).enter((*_this));
	if ((*_this) == '>' || next == ')') 
		return (*_this).exit((*_this));
	return (*_this);
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
	return new_Some<R>((*_this).apply(_this->value));
}
template <typename T>
T orElse_Some(void* _ref, T other){
	Some<T>* _this = (Some<T>*) _ref;
	return _this->value;
}
template <typename R, typename T>
Option<R> flatMap_Some(void* _ref, F1R<T, Option<R>> mapper){
	Some<T>* _this = (Some<T>*) _ref;
	return (*_this).apply(_this->value);
}
template <typename T>
T orElseGet_Some(void* _ref, FR<T> other){
	Some<T>* _this = (Some<T>*) _ref;
	return _this->value;
}
template <typename T>
Iter<T> iter_Some(void* _ref){
	Some<T>* _this = (Some<T>*) _ref;
	return (*_this).of(_this->value);
}
template <typename T>
Option<T> or_Some(void* _ref, FR<Option<T>> other){
	Some<T>* _this = (Some<T>*) _ref;
	return (*_this);
}
template <typename T>
Tuple<int, T> toTuple_Some(void* _ref, FR<T> other){
	Some<T>* _this = (Some<T>*) _ref;
	return new_Tuple<int, T>((*_this), _this->value);
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
	return new_None<R>((*_this));
}
template <typename T>
T orElse_None(void* _ref, T other){
	None<T>* _this = (None<T>*) _ref;
	return (*_this);
}
template <typename R, typename T>
Option<R> flatMap_None(void* _ref, F1R<T, Option<R>> mapper){
	None<T>* _this = (None<T>*) _ref;
	return new_None<R>((*_this));
}
template <typename T>
T orElseGet_None(void* _ref, FR<T> other){
	None<T>* _this = (None<T>*) _ref;
	return (*_this).apply((*_this));
}
template <typename T>
Iter<T> iter_None(void* _ref){
	None<T>* _this = (None<T>*) _ref;
	return (*_this).empty((*_this));
}
template <typename T>
Option<T> or_None(void* _ref, FR<Option<T>> other){
	None<T>* _this = (None<T>*) _ref;
	return (*_this).apply((*_this));
}
template <typename T>
Tuple<int, T> toTuple_None(void* _ref, FR<T> other){
	None<T>* _this = (None<T>*) _ref;
	return new_Tuple<int, T>((*_this), (*_this).apply((*_this)));
}
Folder toFolder_ConditionEndLocator(void* _ref){
	ConditionEndLocator _this = *((ConditionEndLocator*) _ref);
	FolderData data;
	data.ConditionEndLocator = _this;
	return { ConditionEndLocatorVariant, data };
}
State apply_ConditionEndLocator(void* _ref, State state, char c){
	ConditionEndLocator* _this = (ConditionEndLocator*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = (*_this).append((*_this));
	if ((*_this) == '(') 
		return (*_this).enter((*_this));
	if ((*_this) == ')') {
		if ((*_this).isLevel((*_this))) 
			return (*_this).advance((*_this));
		return (*_this).exit((*_this));
	}
	return (*_this);
}
CStructMember toCStructMember_CField(void* _ref){
	CField _this = *((CField*) _ref);
	CStructMemberData data;
	data.CField = _this;
	return { CFieldVariant, data };
}
/*???*/ generate_CField(void* _ref){
	CField* _this = (CField*) _ref;
	return (*_this).generateStatement(1, _this->declaration.generate((*_this)));
}
auto lambda6(void* _ref, auto index){
	return /*elements[index]*/;
}
template <typename T>
Iter<T> fromObjArray_Streams(void* _ref, T* elements){
	Streams* _this = (Streams*) _ref;
	return new_Iter<int>(new_RangeHead((*_this).length)).map(lambda6);
}
auto lambda7(void* _ref, auto index){
	return /*array[index]*/;
}
Iter<char> fromCharArray_Streams(void* _ref, char* array){
	Streams* _this = (Streams*) _ref;
	return new_Iter<int>(new_RangeHead((*_this).length)).map(lambda7);
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
	return _this->head.next((*_this)).map(_this->mapper);
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
	_this->value = (*_this);
	_this->retrieved = (*_this);
	return _this;
}
template <typename T>
Option<T> next_SingleHead(void* _ref){
	SingleHead<T>* _this = (SingleHead<T>*) _ref;
	if (_this->retrieved) 
		return new_None<T>((*_this));
	_this->retrieved = (*_this);
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
FlatMapHead<T, R> new_FlatMapHead(Head<T> head, F1R<T, Iter<R>> mapper){
	FlatMapHead _this;
	_this->head = (*_this);
	_this->mapper = (*_this);
	_this->maybeCurrent = new_None<Iter<R>>((*_this));
	return _this;
}
template <typename T, typename R>
Option<R> next_FlatMapHead(void* _ref){
	FlatMapHead<T, R>* _this = (FlatMapHead<T, R>*) _ref;
	while ((*_this)) {
		if (_this->maybeCurrent.variant = ?.SomeVariant) {
			/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Cannot access member 'head' in 'Placeholder[input=Undefined identifier: current]', not an object.]', not an object.]*/ next = (*_this).head.next((*_this));
			if ((*_this).variant = ?.SomeVariant) 
				return (*_this);
		}
		/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'JGenericType[base=Head, typeArguments=magma.Main$JavaList@6bf2d08e]', not an object.]*/ maybeNext = _this->head.next((*_this));
		if ((*_this).variant = ?.NoneVariant) 
			return new_None<R>((*_this));
		_this->maybeCurrent = (*_this).map(_this->mapper);
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
	return new_None<T>((*_this));
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
	return (*_this);
}
template <typename T>
int fold_AnyMatch(void* _ref, int aBoolean, T t){
	AnyMatch<T>* _this = (AnyMatch<T>*) _ref;
	return (*_this) || (*_this).predicate.apply((*_this));
}
Collector</*???*/, /*???*/> toCollector_Joiner(void* _ref){
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
/*???*/ createInitial_Joiner(void* _ref){
	Joiner* _this = (Joiner*) _ref;
	return "";
}
/*???*/ fold_Joiner(void* _ref, /*???*/ current, /*???*/ element){
	Joiner* _this = (Joiner*) _ref;
	if ((*_this).isEmpty((*_this))) 
		return (*_this);
	return (*_this) + _this->delimiter + (*_this);
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
	return (*_this).empty((*_this));
}
template <typename T>
List<T> fold_ListCollector(void* _ref, List<T> tList, T t){
	ListCollector<T>* _this = (ListCollector<T>*) _ref;
	return (*_this).addLast((*_this));
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
CDeclaration<> new_CDeclaration(CType type, /*???*/ name){
	CDeclaration _this;
	(*_this)((*_this).empty((*_this)), (*_this), (*_this));
	return _this;
}
CFunctionDeclaration mapName_CDeclaration(void* _ref, F1R</*???*/, /*???*/> mapper){
	CDeclaration* _this = (CDeclaration*) _ref;
	return new_CDeclaration(_this->typeParameters, _this->type, (*_this).apply(_this->name));
}
CFunctionDeclaration mapTypeParameters_CDeclaration(void* _ref, F1R<List</*???*/>, List</*???*/>> mapper){
	CDeclaration* _this = (CDeclaration*) _ref;
	return new_CDeclaration((*_this).apply(_this->typeParameters), _this->type, _this->name);
}
/*???*/ generate_CDeclaration(void* _ref){
	CDeclaration* _this = (CDeclaration*) _ref;
	/*Not a functional type: Placeholder[input=Undefined identifier: generateTemplateString]*/ template = (*_this)(_this->typeParameters);
	return (*_this) + _this->type.generate((*_this)) + " " + (*_this).name;
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
CExpression toExpression_JExpressionWrapper(void* _ref){
	JExpressionWrapper* _this = (JExpressionWrapper*) _ref;
	return new_CExpressionWrapper(_this->content);
}
CAssignable toAssignable_JExpressionWrapper(void* _ref){
	JExpressionWrapper* _this = (JExpressionWrapper*) _ref;
	return new_CExpressionWrapper(_this->content);
}
CExpression toCExpression_CExpressionWrapper(void* _ref){
	CExpressionWrapper _this = *((CExpressionWrapper*) _ref);
	CExpressionData data;
	data.CExpressionWrapper = _this;
	return { CExpressionWrapperVariant, data };
}
/*???*/ generate_CExpressionWrapper(void* _ref){
	CExpressionWrapper* _this = (CExpressionWrapper*) _ref;
	return _this->content;
}
JType toJType_JArrayType(void* _ref){
	JArrayType _this = *((JArrayType*) _ref);
	JTypeData data;
	data.JArrayType = _this;
	return { JArrayTypeVariant, data };
}
CType toCType_JArrayType(void* _ref){
	JArrayType* _this = (JArrayType*) _ref;
	return new_CPointerType((*_this)(_this->type));
}
JType toJType_JGenericType(void* _ref){
	JGenericType _this = *((JGenericType*) _ref);
	JTypeData data;
	data.JGenericType = _this;
	return { JGenericTypeVariant, data };
}
CType toCType_JGenericType(void* _ref){
	JGenericType* _this = (JGenericType*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'typeArguments' not defined in 'JObjectType[name=JGenericType, members=magma.Main$JavaList@5eb5c224]']', not an object.]]', not an object.]]', not an object.]*/ newTypeArguments = _this->typeArguments.iter((*_this)).map(F? { alloc((*_this)), F?Table { transformType }}).toList((*_this));
	return new_CTemplateType(_this->base, (*_this));
}
CExpression toCExpression_CPointerAccess(void* _ref){
	CPointerAccess _this = *((CPointerAccess*) _ref);
	CExpressionData data;
	data.CPointerAccess = _this;
	return { CPointerAccessVariant, data };
}
/*???*/ generate_CPointerAccess(void* _ref){
	CPointerAccess* _this = (CPointerAccess*) _ref;
	return _this->instance.generate((*_this)) + "->" + (*_this).fieldName;
}
CExpression toCExpression_CFieldAccess(void* _ref){
	CFieldAccess _this = *((CFieldAccess*) _ref);
	CExpressionData data;
	data.CFieldAccess = _this;
	return { CFieldAccessVariant, data };
}
/*???*/ generate_CFieldAccess(void* _ref){
	CFieldAccess* _this = (CFieldAccess*) _ref;
	return _this->instance.generate((*_this)) + "." + (*_this).fieldName;
}
JExpression toJExpression_JMemberAccess(void* _ref){
	JMemberAccess _this = *((JMemberAccess*) _ref);
	JExpressionData data;
	data.JMemberAccess = _this;
	return { JMemberAccessVariant, data };
}
CExpression toExpression_JMemberAccess(void* _ref){
	JMemberAccess* _this = (JMemberAccess*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'toExpression' in 'Placeholder[input=Member 'instance' not defined in 'JObjectType[name=JMemberAccess, members=magma.Main$JavaList@53e25b76]']', not an object.]*/ cExpression = _this->instance.toExpression((*_this));
	if (_this->instance.variant = ?.Identifier(var value) && value.equals("this")Variant) 
		return new_CPointerAccess(new_Identifier("_this"), _this->memberName);
	/*else return new CFieldAccess(cExpression, this.memberName)*/;
}
JCaller toJCaller_JConstruction(void* _ref){
	JConstruction _this = *((JConstruction*) _ref);
	JCallerData data;
	data.JConstruction = _this;
	return { JConstructionVariant, data };
}
CExpression toExpression_JConstruction(void* _ref){
	JConstruction* _this = (JConstruction*) _ref;
	return new_Identifier("new_" + (*_this)(_this->jType).generate((*_this)));
}
CExpression toCExpression_CInvocation(void* _ref){
	CInvocation _this = *((CInvocation*) _ref);
	CExpressionData data;
	data.CInvocation = _this;
	return { CInvocationVariant, data };
}
/*???*/ generate_CInvocation(void* _ref){
	CInvocation* _this = (CInvocation*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'cArguments' not defined in 'JObjectType[name=CInvocation, members=magma.Main$JavaList@73a8dfcc]']]', not an object.]]', not an object.]]', not an object.]*/ joinedArguments = _this->cArguments((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner(", "));
	return _this->expression((*_this)).generate((*_this)) + "(" + joinedArguments + ")";
}
JExpression toJExpression_JInvokable(void* _ref){
	JInvokable _this = *((JInvokable*) _ref);
	JExpressionData data;
	data.JInvokable = _this;
	return { JInvokableVariant, data };
}
CExpression toExpression_JInvokable(void* _ref){
	JInvokable* _this = (JInvokable*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'arguments' not defined in 'JObjectType[name=JInvokable, members=magma.Main$JavaList@ea30797]']]', not an object.]]', not an object.]]', not an object.]*/ cArguments = _this->arguments((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { toExpression }}).toList((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toExpression' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'caller' not defined in 'JObjectType[name=JInvokable, members=magma.Main$JavaList@ea30797]']]', not an object.]*/ expression = _this->caller((*_this)).toExpression((*_this));
	return new_CInvocation((*_this), (*_this));
}
JType toJType_JFunctionalType(void* _ref){
	JFunctionalType _this = *((JFunctionalType*) _ref);
	JTypeData data;
	data.JFunctionalType = _this;
	return { JFunctionalTypeVariant, data };
}
JFunctionalType<> new_JFunctionalType(JType returnType){
	JFunctionalType _this;
	(*_this)(new_JavaList<JType>((*_this)), (*_this));
	return _this;
}
/*private List<Frame> frames = new JavaList<Frame>*/(){?
}
auto lambda8(void* _ref, auto frame){
	return (*_this).resolve((*_this));
}
Option<JDeclaration> resolveExpression_Environment(void* _ref, /*???*/ identifier){
	Environment* _this = (Environment*) _ref;
	return _this->frames.iter((*_this)).map(lambda8).flatMap(F? { alloc((*_this)), F?Table { iter }}).next((*_this));
}
template <typename T>
Tuple<Environment, T> withinScoped_Environment(void* _ref, F1R<Environment, Tuple<Environment, T>> supplier){
	Environment* _this = (Environment*) _ref;
	_this->frames = _this->frames.addLast(new_Frame((*_this)));
	/*Not a functional type: Placeholder[input=Cannot access member 'apply' in 'JGenericType[base=F1R, typeArguments=magma.Main$JavaList@7e774085]', not an object.]*/ result = (*_this).apply((*_this));
	_this->frames = _this->frames.removeLast((*_this));
	return (*_this);
}
auto lambda9(void* _ref, auto last){
	return (*_this).defineAll((*_this));
}
Environment defineAll_Environment(void* _ref, List<JDeclaration> declarations){
	Environment* _this = (Environment*) _ref;
	_this->frames = _this->frames.mapLast(lambda9);
	return (*_this);
}
template <typename T>
Tuple<Environment, T> within_Environment(void* _ref, Supplier<T> supplier){
	Environment* _this = (Environment*) _ref;
	_this->frames = _this->frames.addLast(new_Frame((*_this)));
	/*Not a functional type: Placeholder[input=Cannot access member 'get' in 'JGenericType[base=Supplier, typeArguments=magma.Main$JavaList@3f8f9dd6]', not an object.]*/ result = (*_this).get((*_this));
	_this->frames = _this->frames.removeLast((*_this));
	return new_Tuple<Environment, T>((*_this), (*_this));
}
auto lambda10(void* _ref, auto last){
	return (*_this).define((*_this));
}
Environment define_Environment(void* _ref, JDeclaration declaration){
	Environment* _this = (Environment*) _ref;
	_this->frames = _this->frames.mapLast(lambda10);
	return (*_this);
}
Option<JObjectType> resolveCurrent_Environment(void* _ref){
	Environment* _this = (Environment*) _ref;
	return _this->frames.iterReversed((*_this)).map(F? { alloc((*_this)), F?Table { toStructureType }}).flatMap(F? { alloc((*_this)), F?Table { iter }}).next((*_this));
}
auto lambda11(void* _ref, auto last){
	return (*_this).withName((*_this));
}
Environment withName_Environment(void* _ref, /*???*/ name){
	Environment* _this = (Environment*) _ref;
	_this->frames = _this->frames.mapLast(lambda11);
	return (*_this);
}
Frame<> new_Frame(Option</*???*/> maybeName, List<JDeclaration> defined){
	Frame _this;
	_this->maybeName = (*_this);
	_this->definitions = (*_this);
	return _this;
}
Frame<> new_Frame(){
	Frame _this;
	(*_this)(new_None</*???*/>((*_this)), new_JavaList<JDeclaration>((*_this)));
	return _this;
}
Frame defineAll_Frame(void* _ref, List<JDeclaration> declarations){
	Frame* _this = (Frame*) _ref;
	return new_Frame(_this->maybeName, _this->definitions.addAll((*_this)));
}
auto lambda12(void* _ref, auto define){
	return (*_this).name.equals((*_this));
}
Option<JDeclaration> resolve_Frame(void* _ref, /*???*/ identifier){
	Frame* _this = (Frame*) _ref;
	return _this->definitions.iter((*_this)).filter(lambda12).next((*_this));
}
Frame define_Frame(void* _ref, JDeclaration declaration){
	Frame* _this = (Frame*) _ref;
	_this->definitions = _this->definitions.addLast((*_this));
	return (*_this);
}
auto lambda13(void* _ref, auto name){
	return new_JObjectType((*_this), _this->definitions);
}
Option<JObjectType> toStructureType_Frame(void* _ref){
	Frame* _this = (Frame*) _ref;
	return _this->maybeName.map(lambda13);
}
Frame withName_Frame(void* _ref, /*???*/ name){
	Frame* _this = (Frame*) _ref;
	return new_Frame(new_Some</*???*/>((*_this)), _this->definitions);
}
JType toJType_JObjectType(void* _ref){
	JObjectType _this = *((JObjectType*) _ref);
	JTypeData data;
	data.JObjectType = _this;
	return { JObjectTypeVariant, data };
}
auto lambda14(void* _ref, auto member){
	return (*_this).name.equals((*_this));
}
Option<JType> resolve_JObjectType(void* _ref, /*???*/ name){
	JObjectType* _this = (JObjectType*) _ref;
	return _this->members.iter((*_this)).filter(lambda14).next((*_this)).map(F? { alloc((*_this)), F?Table { type }});
}
JType toJType_JRecursiveType(void* _ref){
	JRecursiveType _this = *((JRecursiveType*) _ref);
	JTypeData data;
	data.JRecursiveType = _this;
	return { JRecursiveTypeVariant, data };
}
/*private Option<JType> internal = new None<JType>*/(){?
}
JType create_JRecursiveType(void* _ref, F1R<JType, JType> mapper){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	JRecursiveType created = new_JRecursiveType((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'apply' in 'JGenericType[base=F1R, typeArguments=magma.Main$JavaList@aec6354]', not an object.]*/ apply = (*_this).apply((*_this));
	(*_this).set((*_this));
	return (*_this);
}
void set_JRecursiveType(void* _ref, JType created){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	_this->internal = new_Some<JType>((*_this));
}
/*private static final JType StringType = JRecursiveType.create*/(){?
}
new Environment_Main(void* _ref){
	Main* _this = (Main*) _ref;
	return _this->table.Environment(_this->data);
}
Main<> new_Main(){
	Main _this;
	_this->structures = (*_this).empty((*_this));
	_this->functionDeclarations = (*_this).empty((*_this));
	_this->functions = (*_this).empty((*_this));
	_this->globals = (*_this).empty((*_this));
	_this->counter = 0;
	return _this;
}
auto lambda15(void* _ref, auto typeParam){
	return "typename " + (*_this);
}
/*???*/ generateTemplateString_Main(void* _ref, List</*???*/> typeParameters){
	Main* _this = (Main*) _ref;
	/*???*/ templateString;
	if ((*_this).isEmpty((*_this))) 
		(*_this) = "";
	else {
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@1c655221]', not an object.]]', not an object.]]', not an object.]*/ typeNames = (*_this).iter((*_this)).map(lambda15).collect(new_Joiner(", "));
		(*_this) = "template <" + (*_this) + ">" + (*_this).lineSeparator((*_this));
	}
	return (*_this);
}
/*???*/ wrap_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'replace' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'replace' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ replaced = (*_this).replace("/*", "start").replace("*/", "end");
	return "/*" + (*_this) + "*/";
}
void main_Main(void* _ref, /*???*/* args){
	Main* _this = (Main*) _ref;
	if (new_Main((*_this)).run((*_this)).variant = ?.SomeVariant) 
		(*_this).err.println((*_this).display((*_this)));
}
/*???*/ generateStatement_Main(void* _ref, int depth, /*???*/ content){
	Main* _this = (Main*) _ref;
	return (*_this)((*_this)) + (*_this) + ";";
}
/*???*/ generateIndent_Main(void* _ref, int depth){
	Main* _this = (Main*) _ref;
	return (*_this).lineSeparator((*_this)) + "\t".repeat((*_this));
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
	/*Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]*/ source = (*_this).get(".", "src", "main", "java", "magma", "Main.java");
	/*Not a functional type: Placeholder[input=Cannot access member 'resolveSibling' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]]', not an object.]*/ target = (*_this).resolveSibling("Main.cpp");
	/*Not a functional type: Placeholder[input=Cannot access member 'mapValue' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'readString' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]]', not an object.]]', not an object.]*/ input = (*_this).readString((*_this)).mapValue(F? { alloc((*_this)), F?Table { compile }});
	return _switch;
}
/*???*/ compile_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'compileStatements' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ all = _this->compileStatements((*_this), F? { alloc((*_this)), F?Table { compileRootSegment }});
	/*Not a functional type: Placeholder[input=Member 'joinStrings' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ joinedStructures = _this->joinStrings("", _this->structures);
	/*Not a functional type: Placeholder[input=Member 'joinStrings' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ joinedGlobals = _this->joinStrings("", _this->globals);
	/*Not a functional type: Placeholder[input=Member 'joinStrings' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ joinedFunctionDeclarations = _this->joinStrings("", _this->functionDeclarations);
	/*Not a functional type: Placeholder[input=Member 'joinStrings' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ joinedFunctions = _this->joinStrings("", _this->functions);
	return (*_this) + (*_this) + (*_this) + (*_this) + (*_this);
}
/*???*/ joinStrings_Main(void* _ref, /*???*/ delimiter, List</*???*/> structures){
	Main* _this = (Main*) _ref;
	return (*_this).iter((*_this)).collect(new_Joiner((*_this)));
}
/*???*/ compileStatements_Main(void* _ref, /*???*/ input, F1R</*???*/, /*???*/> mapper){
	Main* _this = (Main*) _ref;
	return _this->compileAll((*_this), (*_this), new_EscapedFolder(F? { alloc((*_this)), F?Table { foldStatement }}));
}
/*???*/ compileAll_Main(void* _ref, /*???*/ input, F1R</*???*/, /*???*/> mapper, Folder folder){
	Main* _this = (Main*) _ref;
	return _this->divide((*_this), (*_this)).map((*_this)).collect(new_Joiner(""));
}
Iter</*???*/> divide_Main(void* _ref, /*???*/ input, Folder folder){
	Main* _this = (Main*) _ref;
	State current = new_State((*_this));
	while ((*_this)) {
		/*Not a functional type: Placeholder[input=Cannot access member 'pop' in 'Identifier[value=State]', not an object.]*/ maybeNext = (*_this).pop((*_this));
		if (!(*_this)((*_this).variant = ?.SomeVariant)) 
			break;
		char next;
		(*_this) = (*_this);
		(*_this) = (*_this).apply((*_this), (*_this));
	}
	return (*_this).advance((*_this)).stream((*_this));
}
State foldStatement_Main(void* _ref, State current, char next){
	Main* _this = (Main*) _ref;
	if ((*_this) == '/' && (*_this).isLevel((*_this))) {
		/*Not a functional type: Placeholder[input=Cannot access member 'peek' in 'Identifier[value=State]', not an object.]*/ maybePeeked = (*_this).peek((*_this));
		if ((*_this).variant = ?.SomeVariant) {
			/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/ withoutLineCommentPrefix = (*_this).append('/').popAndAppendToOption((*_this)).orElse((*_this));
			while ((*_this)) {
				/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ maybeTuple = (*_this).popAndAppendToTuple((*_this));
				if ((*_this).variant = ?.SomeVariant) {
					(*_this) = (*_this).left;
					/*Cannot access member 'right' in 'Placeholder[input=Undefined identifier: tuple]', not an object.*/ right = (*_this).right;
					if ((*_this) == '\r' || right == '\n') 
						(*_this) = (*_this).advance((*_this));
				}
				return withoutLineCommentPrefix;
			}
		}
	}
	/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = (*_this).append((*_this));
	if ((*_this) == ';' && (*_this).isLevel((*_this))) 
		return (*_this).advance((*_this));
	if ((*_this) == '}' && (*_this).isShallow((*_this))) {
		State appended1;
		if ((*_this).peek((*_this)).variant = ?.SomeVariant) 
			(*_this) = (*_this).popAndAppendToOption((*_this)).orElse((*_this));
		else appended1 = (*_this);
		return (*_this).advance((*_this)).exit((*_this));
	}
	if ((*_this) == '{' || next == '(') 
		return (*_this).enter((*_this));
	if ((*_this) == '}' || next == ')') 
		return (*_this).exit((*_this));
	return (*_this);
}
auto lambda16(void* _ref, auto ()){
	return (*_this)((*_this));
}
/*???*/ compileRootSegment_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*???*/ stripped = (*_this).strip((*_this));
	if ((*_this).isEmpty((*_this))) 
		return "";
	if ((*_this).startsWith("package ") || (*_this).startsWith("import ")) 
		return "";
	return _this->compileStructure("class", (*_this)).map(F? { alloc((*_this)), F?Table { generate }}).orElseGet(lambda16);
}
auto lambda17(void* _ref, auto (state, character)){
	return new_ValueFolder((*_this)).apply((*_this), (*_this));
}
auto lambda18(void* _ref, auto slice){
	return !(*_this).isEmpty((*_this));
}
auto lambda19(void* _ref, auto input){
	return (*_this)(_this->parseType((*_this)));
}
auto lambda20(void* _ref, auto (state, character)){
	return new_ValueFolder((*_this)).apply((*_this), (*_this));
}
auto lambda21(void* _ref, auto slice){
	return !(*_this).isEmpty((*_this));
}
auto lambda22(void* _ref, auto implementee){
	return _this->getString((*_this), (*_this), (*_this), (*_this));
}
auto lambda23(void* _ref, auto (env)){
	return /*{
			final var withName = env.withName(name);

			// Note that withName is not used by members here*/;
}
auto lambda24(void* _ref, auto variant){
	return (*_this).lineSeparator((*_this)) + "\t" + (*_this) + "Variant";
}
auto lambda25(void* _ref, auto variant){
	return (*_this).lineSeparator((*_this)) + "\t" + (*_this) + (*_this) + " " + (*_this) + ";";
}
auto lambda26(void* _ref, auto member){
	return !(*_this)((*_this).variant = ?.F1RDeclarationVariant);
}
Option<CStructMember> compileStructure_Main(void* _ref, /*???*/ type, /*???*/ stripped){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'indexOf' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ i = (*_this).indexOf((*_this) + " ");
	if ((*_this) < 0) 
		return new_None<CStructMember>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ beforeType = (*_this).substring(0, (*_this)).strip((*_this));
	/*???*/ modifiers;
	List</*???*/> annotations = (*_this).empty((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'lastIndexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]*/ i5 = (*_this).lastIndexOf("\n");
	if ((*_this) >= 0) {
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]*/ substring = (*_this).substring(0, (*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]*/ substring1 = (*_this).substring((*_this) + 1);
		(*_this) = _this->collectAnnotations((*_this));
		(*_this) = (*_this);
	}
	else modifiers = (*_this);
	if ((*_this).contains("Actual")) 
		return new_Some<CStructMember>(new_EmptyStructMember((*_this)));
	/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ afterKeyword = (*_this).substring((*_this) + (*_this)((*_this) + " ").length((*_this))).strip((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'indexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]*/ i1 = (*_this).indexOf("{");
	if ((*_this) < 0) 
		return new_None<CStructMember>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]*/ beforeContent = (*_this).substring(0, (*_this)).strip((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]*/ withEnd = (*_this).substring((*_this) + 1).strip((*_this));
	if (!(*_this).endsWith("}")) 
		return new_None<CStructMember>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ inputContent = (*_this).substring(0, (*_this).length((*_this)) - 1);
	List</*???*/> variants = (*_this).empty((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'indexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ i2 = (*_this).indexOf("permits ");
	if ((*_this) >= 0) {
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ substring1 = (*_this).substring((*_this) + "permits ".length((*_this)));
		(*_this) = (*_this).substring(0, (*_this));
		(*_this) = _this->splitValues((*_this));
	}
	List<CType> implementees = (*_this).empty((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'indexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ i4 = (*_this).indexOf("implements ");
	if ((*_this) >= 0) {
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ implementeesString = (*_this).substring((*_this) + "implements ".length((*_this)));
		(*_this) = (*_this).substring(0, (*_this)).strip((*_this));
		(*_this) = _this->divide((*_this), lambda17).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda18).map(lambda19).toList((*_this));
	}
	List<JDeclaration> recordFields = (*_this).empty((*_this));
	if ((*_this).endsWith(")")) {
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'indexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]*/ i3 = (*_this).indexOf("(");
		if ((*_this) >= 0) {
			(*_this) = (*_this).substring(0, (*_this));
			(*_this) = _this->divide((*_this).substring((*_this) + 1), lambda20).map(F? { alloc((*_this)), F?Table { parseDeclaration }}).flatMap(F? { alloc((*_this)), F?Table { iter }}).toList((*_this));
		}
	}
	List</*???*/> typeParameters = (*_this).empty((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'indexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ i3 = (*_this).indexOf(" < ");
	if ((*_this) >= 0) {
		/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ substring1 = (*_this).substring((*_this) + 1).strip((*_this));
		if ((*_this).endsWith(">")) {
			(*_this) = (*_this).substring(0, (*_this));
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]*/ substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
			(*_this) = _this->splitValues((*_this));
		}
	}
	if (!(*_this).isIdentifier((*_this))) 
		return new_None<CStructMember>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'fromObjArray' in 'Placeholder[input=Undefined identifier: Streams]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ modifiersList = (*_this).fromObjArray((*_this).split((*_this).quote(" "))).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda21).toList((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ name = (*_this).strip((*_this));
	/*Not a functional type: Placeholder[input=Undefined identifier: generateTemplateString]*/ templateString = (*_this)((*_this));
	/*Not a functional type: Placeholder[input=Member 'joinTypeParameters' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ joinedTypeParameters = _this->joinTypeParameters((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'empty' in 'Placeholder[input=Undefined identifier: StringBuilders]', not an object.]*/ fields = (*_this).empty((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'empty' in 'Placeholder[input=Undefined identifier: StringBuilders]', not an object.]*/ dependencies = (*_this).empty((*_this));
	_this->functions = (*_this).iter((*_this)).map(lambda22).fold(_this->functions, F? { alloc((*_this)), F?Table { addLast }});
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@cac736f]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ joinedRecordFields = (*_this).iter((*_this)).map(F? { alloc((*_this)), F?Table { toCDeclaration }}).map(F? { alloc((*_this)), F?Table { generate }}).map(F? { alloc((*_this)), F?Table { generateStatement }}).collect(new_Joiner((*_this)));
	List</*???*/> finalTypeParameters = (*_this);
	List</*???*/> finalVariants = (*_this);
	/*Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']', not an object.]*/ within = _this->environment.withinScoped(lambda23);
	_this->environment = (*_this).left;
	/*Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']', not an object.]]', not an object.*/ members = (*_this).right;
	if ((*_this).contains("sealed")) {
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@5e265ba4]', not an object.]]', not an object.]]', not an object.]*/ enumFields = (*_this).iter((*_this)).map(lambda24).collect(new_Joiner(","));
		/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Placeholder[input=Unwrapped expression: "enum " + (*_this) + "Variant {" + (*_this) + (*_this).lineSeparator((*_this)) + "};" + (*_this)]', not an object.]*/ generatedEnum = "enum " + (*_this) + "Variant {" + (*_this) + (*_this).lineSeparator((*_this)) + "};" + (*_this).lineSeparator((*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@5e265ba4]', not an object.]]', not an object.]]', not an object.]*/ unionFields = (*_this).iter((*_this)).map(lambda25).collect(new_Joiner((*_this)));
		/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Placeholder[input=Unwrapped expression: (*_this) + "union " + (*_this) + "Data {" + (*_this) + (*_this).lineSeparator((*_this)) + "};" + (*_this)]', not an object.]*/ generatedUnion = (*_this) + "union " + (*_this) + "Data {" + (*_this) + (*_this).lineSeparator((*_this)) + "};" + (*_this).lineSeparator((*_this));
		/*Unwrapped expression: (*_this) + "Variant variant"*/ s = (*_this) + "Variant variant";
		/*Unwrapped expression: (*_this) + "Data" + (*_this) + " data"*/ s1 = (*_this) + "Data" + (*_this) + " data";
		/*Not a functional type: Placeholder[input=Cannot access member 'generateStatement' in 'Placeholder[input=Unwrapped expression: _this->generateStatement((*_this)) + (*_this)]', not an object.]*/ generatedFields = _this->generateStatement((*_this)) + (*_this).generateStatement((*_this));
		(*_this) = (*_this).appendString((*_this));
		(*_this) = (*_this).appendString((*_this)).appendString((*_this));
	}
	else 
	if ((*_this).equals("interface")) {
		/*Not a functional type: Placeholder[input=Member 'generateStatement' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ table = _this->generateStatement((*_this) + "Table" + (*_this) + " table");
		/*Not a functional type: Placeholder[input=Member 'generateStatement' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ data = _this->generateStatement("void* data");
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']', not an object.]]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ tableMembers = (*_this).iter((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).map(F? { alloc((*_this)), F?Table { generateStatement }}).collect(new_Joiner(""));
		/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Placeholder[input=Unwrapped expression: (*_this) + "struct " + (*_this) + "Table {" + (*_this) + (*_this).lineSeparator((*_this)) + "};" + (*_this)]', not an object.]*/ vTable = (*_this) + "struct " + (*_this) + "Table {" + (*_this) + (*_this).lineSeparator((*_this)) + "};" + (*_this).lineSeparator((*_this));
		(*_this) = (*_this).appendString((*_this));
		(*_this) = (*_this).appendString((*_this)).appendString((*_this));
	}
	else {
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']', not an object.]]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ joinedMembers = (*_this).iter((*_this)).filter(lambda26).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner((*_this)));
		(*_this) = (*_this).appendString((*_this));
	}
	/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Placeholder[input=Unwrapped expression: (*_this) + (*_this) + "struct " + (*_this) + " {" + (*_this) + (*_this) + (*_this).lineSeparator((*_this)) + "};" + (*_this)]', not an object.]*/ generated = (*_this) + (*_this) + "struct " + (*_this) + " {" + (*_this) + (*_this) + (*_this).lineSeparator((*_this)) + "};" + (*_this).lineSeparator((*_this));
	_this->structures = _this->structures.addLast((*_this));
	return new_Some<CStructMember>(new_EmptyStructMember((*_this)));
}
/*???*/ getString_Main(void* _ref, CType implementee, /*???*/ name, /*???*/ joinedTypeParameters, /*???*/ templateString){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'toBaseName' in 'Identifier[value=CType]', not an object.]*/ identifier = (*_this).toBaseName((*_this));
	/*Unwrapped expression: (*_this) + (*_this)*/ thisType = (*_this) + (*_this);
	/*Not a functional type: Placeholder[input=Member 'generateStatement' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ s = _this->generateStatement((*_this) + " _this = *((" + thisType + "*) _ref)");
	/*Not a functional type: Placeholder[input=Member 'generateStatement' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ s1 = _this->generateStatement((*_this) + "Data" + (*_this) + " data");
	/*Not a functional type: Placeholder[input=Member 'generateStatement' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ s2 = _this->generateStatement("data." + (*_this) + " = _this");
	/*Not a functional type: Placeholder[input=Member 'generateStatement' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ s3 = _this->generateStatement("return { " + (*_this) + "Variant, data }");
	/*Unwrapped expression: (*_this) + (*_this) + (*_this) + (*_this)*/ conversionF1RContent = (*_this) + (*_this) + (*_this) + (*_this);
	return (*_this) + (*_this).generate((*_this)) + " to" + (*_this) + "_" + (*_this) + "(void* _ref){" + (*_this) + (*_this).lineSeparator((*_this)) + "}" + (*_this).lineSeparator((*_this));
}
/*???*/ joinTypeParameters_Main(void* _ref, List</*???*/> typeParameters){
	Main* _this = (Main*) _ref;
	/*???*/ joinedTypeParameters;
	if ((*_this).isEmpty((*_this))) 
		(*_this) = "";
	else joinedTypeParameters = " < " + (*_this).iter((*_this)).collect(new_Joiner(", ")) + ">";
	return (*_this);
}
/*???*/ generateStatement_Main(void* _ref, /*???*/ content){
	Main* _this = (Main*) _ref;
	return (*_this)(1, (*_this));
}
auto lambda27(void* _ref, auto slice){
	return !(*_this).isEmpty((*_this));
}
List</*???*/> splitValues_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'split' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ segments = (*_this).split((*_this).quote(","));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ list = (*_this).stream((*_this)).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda27).toList((*_this));
	return new_JavaList</*???*/>((*_this));
}
auto lambda28(void* _ref, auto i){
	/*Not a functional type: Placeholder[input=Member 'charAt' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ c = (*_this).charAt((*_this));
	return (*_this).isLetter((*_this)) || (*_this)((*_this) != 0 && (*_this).isDigit((*_this)));
}
int isIdentifier_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*???*/ stripped = (*_this).strip((*_this));
	return (*_this).range(0, (*_this).length((*_this))).allMatch(lambda28);
}
Option<CStructMember> compileClassSegment_Main(void* _ref, /*???*/ input, /*???*/ structName, List</*???*/> typeParameters, List</*???*/> variants){
	Main* _this = (Main*) _ref;
	/*???*/ stripped = (*_this).strip((*_this));
	if ((*_this).isEmpty((*_this))) 
		return new_None<CStructMember>((*_this));
	/*Not a functional type: Placeholder[input=Member 'compileStructure' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeEnum = _this->compileStructure("enum", (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'compileStructure' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeInterface = _this->compileStructure("interface", (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'compileStructure' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeRecord = _this->compileStructure("record", (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'compileStructure' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeClass = _this->compileStructure("class", (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'compileEnumValues' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeEnumValues = _this->compileEnumValues((*_this), (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	if ((*_this).endsWith(";")) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
		/*Not a functional type: Placeholder[input=Member 'parseDeclaration' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeDeclaration = _this->parseDeclaration((*_this));
		if ((*_this).variant = ?.SomeVariant) {
			_this->environment = _this->environment.define((*_this));
			return new_Some<CStructMember>(new_CField((*_this).toCDeclaration((*_this))));
		}
	}
	/*Not a functional type: Placeholder[input=Member 'compileMethod' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeMethod = _this->compileMethod((*_this), (*_this), (*_this), (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	return new_Some<CStructMember>(new_Placeholder((*_this)));
}
auto lambda29(void* _ref, auto slice){
	return !(*_this).isEmpty((*_this));
}
auto lambda30(void* _ref, auto name){
	return (*_this) + "_" + (*_this);
}
auto lambda31(void* _ref){
	return new_Some</*???*/>(_this->compileMethodsSegments((*_this), 1));
}
auto lambda32(void* _ref, auto env){
	return (*_this).defineAll((*_this)).within(lambda31);
}
auto lambda33(void* _ref, auto parameter){
	return (*_this).name;
}
auto lambda34(void* _ref, auto variant){
	return _this->generateCase((*_this), (*_this));
}
auto lambda35(void* _ref){
	if ((*_this).isEmpty((*_this))) {
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'subList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ joinedParameters = (*_this).subList(1, (*_this).size((*_this))).iter((*_this)).map(lambda33).toList((*_this)).addFirst("_this->data").iter((*_this)).collect(new_Joiner(", "));
		return _this->generateStatement("return _this->table." + (*_this).name + "(" + joinedParameters + ")");
	}
	else {
		/*Not a functional type: Placeholder[input=Member 'generateStatement' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ returnValueDefinition = _this->generateStatement((*_this)((*_this).type).generate((*_this)) + " _ret");
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@123a439b]', not an object.]]', not an object.]]', not an object.]*/ cases = (*_this).iter((*_this)).map(lambda34).collect(new_Joiner((*_this)));
		return (*_this) + (*_this)(1) + "switch (" + "_this->variant" + ") {" + (*_this) + (*_this)(1) + "}" + (*_this).generateStatement("return _ret");
	}
}
auto lambda36(void* _ref, auto typeParameters0){
	return (*_this).addAll((*_this));
}
auto lambda37(void* _ref, auto name){
	return (*_this) + "_" + (*_this);
}
Option<CStructMember> compileMethod_Main(void* _ref, /*???*/ structName, List</*???*/> typeParameters, List</*???*/> variants, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'indexOf' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ i = (*_this).indexOf("(");
	if ((*_this) < 0) 
		return new_None<CStructMember>((*_this));
	/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ declarationString = (*_this).substring(0, (*_this));
	/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ substring1 = (*_this).substring((*_this) + 1);
	/*Not a functional type: Placeholder[input=Cannot access member 'indexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ i1 = (*_this).indexOf(")");
	if ((*_this) < 0) 
		return new_None<CStructMember>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ parametersString = (*_this).substring(0, (*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]*/ withBraces = (*_this).substring((*_this) + 1).strip((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ parameters = _this->divide((*_this), new_ValueFolder((*_this))).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda29).toList((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { parseDeclaration }}).flatMap(F? { alloc((*_this)), F?Table { iter }}).toList((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ cParameters = (*_this).iter((*_this)).map(F? { alloc((*_this)), F?Table { toCDeclaration }}).toList((*_this));
	/*Not a functional type: Placeholder[input=Member 'parseMethodDeclaration' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ methodDeclaration = _this->parseMethodDeclaration((*_this), (*_this));
	Option</*???*/> maybeCompiled = new_None</*???*/>((*_this));
	if ((*_this).variant = ?.JDeclaration declaration && declaration.annotations.contains("Actual")Variant) {
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = (*_this).iter((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner(", "));
		/*Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]]', not an object.]*/ modifiedMethodDeclaration = (*_this).mapName(lambda30).toCDeclaration((*_this));
		_this->functionDeclarations = _this->functionDeclarations.addLast((*_this).generate((*_this)) + "(" + compiledParameters + ");" + (*_this).lineSeparator((*_this)));
		return new_Some<CStructMember>(new_EmptyStructMember((*_this)));
	}
	if ((*_this).startsWith("{") && (*_this).endsWith("}")) {
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]*/ inputContent = (*_this).substring(1, (*_this).length((*_this)) - 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']', not an object.]*/ within = _this->environment.withinScoped(lambda32);
		_this->environment = (*_this).left;
		(*_this) = (*_this).right;
	}
	/*???*/ outputContent;
	if ((*_this).variant = ?.JConstructorVariant) {
		/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@156643d4]', not an object.]*/ compiled = (*_this).orElse("?");
		(*_this) = _this->generateStatement((*_this) + " _this") + (*_this) + (*_this).generateStatement("return " + "_this");
	}
	else 
	if ((*_this).variant = ?.JDeclaration declarationVariant) {
		(*_this) = (*_this).addFirst(new_CDeclaration(new_CPointerType((*_this).Void), "_ref"));
		/*Not a functional type: Placeholder[input=Member 'joinTypeParameters' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ joinedTypeParameters = _this->joinTypeParameters((*_this));
		/*Not a functional type: Placeholder[input=Member 'generateStatement' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ thisInitialization = _this->generateStatement((*_this) + (*_this) + "* _this = (" + structName + joinedTypeParameters + "*) _ref");
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ finalParameters = (*_this);
		(*_this) = (*_this) + (*_this).orElseGet(lambda35);
	}
	else outputContent = "?";
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = (*_this).iter((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner(", "));
	/*Unwrapped expression: _switch*/ modifiedMethodDeclaration = _switch;
	/*Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapTypeParameters' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ mapped = (*_this).mapTypeParameters(lambda36).mapName(lambda37);
	/*Unwrapped expression: (*_this).generate((*_this)) + "(" + compiledParameters + ")"*/ header = (*_this).generate((*_this)) + "(" + compiledParameters + ")";
	/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Placeholder[input=Unwrapped expression: (*_this) + "{" + (*_this) + (*_this).lineSeparator((*_this)) + "}" + (*_this)]', not an object.]*/ generated = (*_this) + "{" + (*_this) + (*_this).lineSeparator((*_this)) + "}" + (*_this).lineSeparator((*_this));
	_this->functionDeclarations = _this->functionDeclarations.addLast((*_this) + ";" + (*_this).lineSeparator((*_this)));
	_this->functions = _this->functions.addLast((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ parameterTypes = (*_this).iter((*_this)).map(F? { alloc((*_this)), F?Table { type }}).toList((*_this));
	return _switch;
}
CType toConstructorReturnType_Main(void* _ref, /*???*/ base, List</*???*/> typeParameters){
	Main* _this = (Main*) _ref;
	if ((*_this).isEmpty((*_this))) 
		return new_Identifier((*_this));
	/*final var typeArguments = typeParameters.iter().<CType>map(Identifier::new).toList()*/;
	return new_CTemplateType((*_this), (*_this));
}
auto lambda38(void* _ref, auto input){
	return _this->compileMethodSegment((*_this), (*_this));
}
/*???*/ compileMethodsSegments_Main(void* _ref, /*???*/ inputContent, int indent){
	Main* _this = (Main*) _ref;
	return _this->compileStatements((*_this), lambda38);
}
/*???*/ generateCase_Main(void* _ref, JDeclaration declaration, /*???*/ variant){
	Main* _this = (Main*) _ref;
	return (*_this)(2) + "case " + (*_this) + "Variant:" + (*_this)(3, "_ret = " + (*_this).name + "_" + (*_this) + "(&(_this->data." + variant + "))") + (*_this)(3, "break");
}
auto lambda39(void* _ref, auto ()){
	return _this->parseDeclaration((*_this)).map(F? { alloc((*_this)), F?Table { toInterface }});
}
auto lambda40(void* _ref, auto ()){
	return new_Placeholder((*_this));
}
JMethodDeclaration parseMethodDeclaration_Main(void* _ref, /*???*/ declaration, /*???*/ structName){
	Main* _this = (Main*) _ref;
	return _this->parseConstructor((*_this), (*_this)).or(lambda39).orElseGet(lambda40);
}
JMethodDeclaration toInterface_Main(void* _ref, JDeclaration value){
	Main* _this = (Main*) _ref;
	return (*_this);
}
Option<JMethodDeclaration> parseConstructor_Main(void* _ref, /*???*/ declaration, /*???*/ structName){
	Main* _this = (Main*) _ref;
	/*???*/ stripped = (*_this).strip((*_this));
	if ((*_this).equals((*_this))) 
		return new_Some<JMethodDeclaration>(new_JConstructor((*_this)));
	/*Not a functional type: Placeholder[input=Member 'lastIndexOf' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ i = (*_this).lastIndexOf(" ");
	if ((*_this) >= 0) {
		/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ substring = (*_this).substring((*_this) + 1).strip((*_this));
		if ((*_this).equals((*_this))) 
			return new_Some<JMethodDeclaration>(new_JConstructor((*_this)));
	}
	return new_None<JMethodDeclaration>((*_this));
}
auto lambda41(void* _ref, auto (state, character)){
	return new_ValueFolder((*_this)).apply((*_this), (*_this));
}
auto lambda42(void* _ref, auto slice){
	return !(*_this).isEmpty((*_this));
}
auto lambda43(void* _ref, auto enumValue){
	return _this->compileEnumValue((*_this), (*_this));
}
Option<CStructMember> compileEnumValues_Main(void* _ref, /*???*/ input, /*???*/ structName){
	Main* _this = (Main*) _ref;
	/*???*/ stripped = (*_this).strip((*_this));
	if (!(*_this).endsWith(";")) 
		return new_None<CStructMember>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]*/ enumValues = _this->divide((*_this).substring(0, (*_this).length((*_this)) - 1), lambda41).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda42).toList((*_this));
	if (!(*_this).isEmpty((*_this))) {
		/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ optionStream = (*_this).iter((*_this)).map(lambda43);
		/*final var areAnyInvalid =
					(boolean) optionStream.collect(new AnyMatch<Option<CStructMember>>(option -> option instanceof None<CStructMember>))*/;
		if ((*_this)) 
			return new_None<CStructMember>((*_this));
	}
	return new_Some<CStructMember>(new_EmptyStructMember((*_this)));
}
Option<CStructMember> compileEnumValue_Main(void* _ref, /*???*/ structName, /*???*/ enumValue){
	Main* _this = (Main*) _ref;
	if ((*_this).endsWith(")")) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'indexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ i = (*_this).indexOf("(");
		if ((*_this) >= 0) {
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ name = (*_this).substring(0, (*_this));
			if (!(*_this).isIdentifier((*_this))) 
				return new_None<CStructMember>((*_this));
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ substring2 = (*_this).substring((*_this) + 1);
			/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Placeholder[input=Unwrapped expression: (*_this) + " " + (*_this) + (*_this) + " = " + "new_" + (*_this) + "(" + substring2 + ")" + ";" + (*_this)]', not an object.]*/ generated = (*_this) + " " + (*_this) + (*_this) + " = " + "new_" + (*_this) + "(" + substring2 + ")" + ";" + (*_this).lineSeparator((*_this));
			_this->globals = _this->globals.addLast((*_this));
			return new_Some<CStructMember>(new_EmptyStructMember((*_this)));
		}
	}
	return new_None<CStructMember>((*_this));
}
/*???*/ compileMethodSegment_Main(void* _ref, /*???*/ input, int indent){
	Main* _this = (Main*) _ref;
	/*???*/ stripped = (*_this).strip((*_this));
	if ((*_this).isEmpty((*_this))) 
		return "";
	/*Not a functional type: Placeholder[input=Member 'compileConditional' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeIf = _this->compileConditional("if", (*_this), (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'compileConditional' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeWhile = _this->compileConditional("while", (*_this), (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	if ((*_this).endsWith(";")) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
		return (*_this)((*_this)) + _this->compileMethodStatement((*_this)) + ";";
	}
	if ((*_this).startsWith("else ")) {
		/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ substring = (*_this).substring("else ".length((*_this))).strip((*_this));
		if ((*_this).startsWith("{") && (*_this).endsWith("}")) {
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ substring1 = (*_this).substring(1, (*_this).length((*_this)) - 1);
			return (*_this)((*_this)) + "else {" + _this->compileMethodsSegments((*_this), (*_this) + 1) + (*_this)((*_this)) + "}";
		}
		/*else return generateIndent(indent) + "else " + this.compileMethodSegment(substring, indent)*/;
	}
	if ((*_this).startsWith("//")) 
		return (*_this)((*_this)) + (*_this);
	return (*_this).lineSeparator((*_this)) + "\t" + (*_this)((*_this));
}
auto lambda44(void* _ref, auto slice){
	return !(*_this).isEmpty((*_this));
}
Option</*???*/> compileConditional_Main(void* _ref, /*???*/ type, int indent, /*???*/ input){
	Main* _this = (Main*) _ref;
	if ((*_this).startsWith((*_this))) {
		/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ substring = (*_this).substring((*_this).length((*_this))).strip((*_this));
		if ((*_this).startsWith("(")) {
			/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]*/ afterConditionStart = (*_this).substring(1).strip((*_this));
			/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]*/ divisions = _this->divide((*_this), new_EscapedFolder(new_ConditionEndLocator((*_this)))).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda44).toList((*_this));
			if ((*_this).size((*_this)) < 2) 
				return new_None</*???*/>((*_this));
			/*Not a functional type: Placeholder[input=Cannot access member 'getFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ first = (*_this).getFirst((*_this));
			/*Not a functional type: Placeholder[input=Member 'joinStrings' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeWithBraces = _this->joinStrings("", (*_this).subList(1, (*_this).size((*_this))));
			if (!(*_this).endsWith(")")) 
				return new_None</*???*/>((*_this));
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'getFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ condition = (*_this).substring(0, (*_this).length((*_this)) - 1);
			if ((*_this).startsWith("{") && (*_this).endsWith("}")) {
				/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'joinStrings' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]*/ content = (*_this).substring(1, (*_this).length((*_this)) - 1);
				return new_Some</*???*/>((*_this)((*_this)) + (*_this) + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" + _this->compileMethodsSegments((*_this), (*_this) + 1) + (*_this)((*_this)) + "}");
			}
			return new_Some</*???*/>((*_this)((*_this)) + (*_this) + " (" + this.compileExpressionOrPlaceholder(condition) + ") " + (*_this).compileMethodSegment((*_this), (*_this) + 1));
		}
	}
	return new_None</*???*/>((*_this));
}
/*???*/ compileMethodStatement_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*???*/ stripped = (*_this).strip((*_this));
	if ((*_this).equals("break")) 
		return "break";
	if ((*_this).startsWith("return ")) 
		return "return " + (*_this).compileExpressionOrPlaceholder((*_this).substring("return ".length((*_this))));
	/*Not a functional type: Placeholder[input=Member 'compileAssignment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeAssignment = _this->compileAssignment((*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'parseInvokable' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeInvokable = _this->parseInvokable((*_this));
	if ((*_this).variant = ?.Some(var value)Variant) 
		return (*_this).toExpression((*_this)).generate((*_this));
	/*Not a functional type: Placeholder[input=Member 'post' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ instance = _this->post((*_this), "++");
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'post' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ instance0 = _this->post((*_this), "--");
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'parseDeclaration' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeDeclaration = _this->parseDeclaration((*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this).toCDeclaration((*_this)).generate((*_this));
	return (*_this)((*_this));
}
Option</*???*/> compileAssignment_Main(void* _ref, /*???*/ stripped){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'indexOf' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ index = (*_this).indexOf("=");
	if ((*_this) >= 0) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ destination = (*_this).substring(0, (*_this));
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ substring1 = (*_this).substring((*_this) + 1);
		/*Not a functional type: Placeholder[input=Member 'parseAssignable' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ assignable = _this->parseAssignable((*_this));
		/*Not a functional type: Placeholder[input=Member 'parseExpression' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeSource = _this->parseExpression((*_this));
		if ((*_this).variant = ?.SomeVariant) {
			/*Not a functional type: Placeholder[input=Member 'transformAssignable' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ cAssignable = _this->transformAssignable((*_this), (*_this));
			return new_Some</*???*/>((*_this).generate((*_this)) + " = " + (*_this).toAssignable((*_this)).generate((*_this)));
		}
	}
	return new_None</*???*/>((*_this));
}
CAssignable transformAssignable_Main(void* _ref, JAssignable assignable, JExpression source){
	Main* _this = (Main*) _ref;
	return _switch;
}
JType resolveType_Main(void* _ref, JExpression source, JType type){
	Main* _this = (Main*) _ref;
	if ((*_this).equals((*_this).Var)) 
		return _this->resolveExpression((*_this));
	return (*_this);
}
JType resolveExpression_Main(void* _ref, JExpression source){
	Main* _this = (Main*) _ref;
	return _switch;
}
auto lambda45(void* _ref, auto ()){
	return new_Placeholder("Member '" + (*_this).memberName + "' not defined in '" + (*_this) + "'");
}
JType getJType_Main(void* _ref, JMemberAccess access, JObjectType type, JType instanceType){
	Main* _this = (Main*) _ref;
	return (*_this).resolve((*_this).memberName).orElseGet(lambda45);
}
JType resolveCaller_Main(void* _ref, JCaller caller){
	Main* _this = (Main*) _ref;
	return _switch;
}
JAssignable parseAssignable_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	return /*this
				.parseExpression(input)
				.<JAssignable>map(value -> value)
				.or(() -> this.parseDeclaration(input).map(value -> value))
				.orElseGet(() -> new Placeholder(input))*/;
}
Option</*???*/> post_Main(void* _ref, /*???*/ stripped, /*???*/ slice){
	Main* _this = (Main*) _ref;
	if ((*_this).endsWith((*_this))) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ instance = (*_this).substring(0, (*_this).length((*_this)) - 2);
		return new_Some</*???*/>(_this->compileExpressionOrPlaceholder((*_this)) + (*_this));
	}
	return new_None</*???*/>((*_this));
}
auto lambda46(void* _ref, auto ()){
	return (*_this)((*_this));
}
/*???*/ compileExpressionOrPlaceholder_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	return _this->parseCExpression((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).orElseGet(lambda46);
}
Option<CExpression> parseCExpression_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	return _this->parseExpression((*_this)).map(F? { alloc((*_this)), F?Table { toExpression }});
}
auto lambda47(void* _ref, auto ()){
	return _this->compileOperator((*_this), " != ");
}
auto lambda48(void* _ref, auto ()){
	return _this->compileOperator((*_this), " < ");
}
auto lambda49(void* _ref, auto ()){
	return _this->compileOperator((*_this), " + ");
}
auto lambda50(void* _ref, auto ()){
	return _this->compileOperator((*_this), " - ");
}
auto lambda51(void* _ref, auto ()){
	return _this->compileOperator((*_this), " && ");
}
auto lambda52(void* _ref, auto ()){
	return _this->compileOperator((*_this), " || ");
}
auto lambda53(void* _ref, auto ()){
	return _this->compileOperator((*_this), " >= ");
}
Option<JExpression> parseExpression_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*???*/ stripped = (*_this).strip((*_this));
	if ((*_this).startsWith("switch ")) 
		return new_Some</*???*/>("_switch").map(F? { alloc((*_this)), F?Table { new }});
	/*Not a functional type: Placeholder[input=Member 'lastIndexOf' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ i2 = (*_this).lastIndexOf("::");
	if ((*_this) >= 0) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ substring = (*_this).substring(0, (*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ name = (*_this).substring((*_this) + 2).strip((*_this));
		if (_this->isIdentifier((*_this))) {
			/*Not a functional type: Placeholder[input=Member 'compileExpressionOrPlaceholder' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ compiled = _this->compileExpressionOrPlaceholder((*_this));
			/*Unwrapped expression: "F?"*/ functionalInterfaceName = "F?";
			return new_Some</*???*/>((*_this) + " { alloc(" + compiled + "), " + (*_this) + "Table { " + (*_this) + " }}").map(F? { alloc((*_this)), F?Table { new }});
		}
	}
	if ((*_this).startsWith("'") && (*_this).endsWith("'")) 
		return new_Some</*???*/>((*_this)).map(F? { alloc((*_this)), F?Table { new }});
	/*Not a functional type: Placeholder[input=Member 'compileLambda' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeLambda = _this->compileLambda((*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this).map(F? { alloc((*_this)), F?Table { new }});
	/*Not a functional type: Placeholder[input=Member 'indexOf' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ i3 = (*_this).indexOf(".variant = ?."Variant);
	if ((*_this) >= 0) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ substring = (*_this).substring(0, (*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ substring1 = (*_this).substring((*_this) + ".variant = ?.".length()Variant).strip((*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'parseCExpression' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]*/ maybeInstance = _this->parseCExpression((*_this)).map(F? { alloc((*_this)), F?Table { generate }});
		if ((*_this).variant = ?.SomeVariant) {
			/*Not a functional type: Placeholder[input=Cannot access member 'indexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]*/ i4 = (*_this).indexOf(" < ");
			/*???*/ substring2;
			if ((*_this) >= 0) 
				(*_this) = (*_this).substring(0, (*_this));
			else substring2 = (*_this);
			return new_Some</*???*/>((*_this) + ".variant = ?." + (*_this) + "Variant").map(F? { alloc((*_this)), F?Table { new }});
		}
	}
	/*Not a functional type: Placeholder[input=Member 'lastIndexOf' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ i = (*_this).lastIndexOf(".");
	if ((*_this) >= 0) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ instanceString = (*_this).substring(0, (*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ memberName = (*_this).substring((*_this) + 1).strip((*_this));
		if (_this->isIdentifier((*_this))) {
			/*Not a functional type: Placeholder[input=Member 'parseExpression' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeInstance = _this->parseExpression((*_this));
			if ((*_this).variant = ?.Some(var value)Variant) 
				return new_Some<JExpression>(new_JMemberAccess((*_this), (*_this)));
		}
	}
	/*Not a functional type: Placeholder[input=Member 'parseInvokable' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeInvokable = _this->parseInvokable((*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'compileOperator' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ maybeOperator = _this->compileOperator((*_this), " == ").or(lambda47).or(lambda48).or(lambda49).or(lambda50).or(lambda51).or(lambda52).or(lambda53);
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this).map(F? { alloc((*_this)), F?Table { new }});
	if (_this->isIdentifier((*_this))) 
		return new_Some<JExpression>(new_Identifier((*_this)));
	if ((*_this).startsWith("!")) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ substring = (*_this).substring(1);
		/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'parseCExpression' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]*/ maybeInstance = _this->parseCExpression((*_this)).map(F? { alloc((*_this)), F?Table { generate }});
		if ((*_this).variant = ?.SomeVariant) 
			return new_Some</*???*/>("!" + (*_this)).map(F? { alloc((*_this)), F?Table { new }});
	}
	if (_this->isNumber((*_this))) 
		return new_Some</*???*/>((*_this)).map(F? { alloc((*_this)), F?Table { new }});
	if ((*_this).startsWith("\"") && (*_this).endsWith("\"")) 
		return new_Some</*???*/>((*_this)).map(F? { alloc((*_this)), F?Table { new }});
	return new_None<JExpression>((*_this));
}
auto lambda54(void* _ref, auto slice){
	return !(*_this).isEmpty((*_this));
}
auto lambda55(void* _ref, auto param){
	return "auto " + (*_this);
}
Option</*???*/> compileLambda_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'indexOf' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ index = (*_this).indexOf("->");
	if ((*_this) < 0) 
		return new_None</*???*/>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ beforeContent = (*_this).substring(0, (*_this)).strip((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ maybeWithBraces = (*_this).substring((*_this) + 2).strip((*_this));
	List</*???*/> params;
	if (_this->isIdentifier((*_this))) 
		(*_this) = (*_this).of((*_this));
	else 
	if ((*_this).startsWith("(") && beforeContent.endsWith(")")) {
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]*/ substring = (*_this).substring(1, (*_this).length((*_this)) - 1);
		(*_this) = _this->divide((*_this), new_ValueFolder((*_this))).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda54).toList((*_this));
	}
	/*else return new None<String>()*/;
	if ((*_this).startsWith("{") && (*_this).endsWith("}")) {
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]*/ content = (*_this).substring(1, (*_this).length((*_this)) - 1);
		/*Not a functional type: Placeholder[input=Member 'compileMethodsSegments' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ compiled = _this->compileMethodsSegments((*_this), 1);
		/*Not a functional type: Placeholder[input=Member 'generateName' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ generatedName = _this->generateName((*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ paramList = (*_this).iter((*_this)).map(lambda55).toList((*_this)).addFirst("void* _ref");
		/*Not a functional type: Placeholder[input=Member 'joinStrings' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ joined = _this->joinStrings(", ", (*_this));
		_this->functions = _this->functions.addLast("auto " + (*_this) + "(" + joined + "){" + (*_this) + (*_this).lineSeparator((*_this)) + "}" + (*_this).lineSeparator((*_this)));
		return new_Some</*???*/>((*_this));
	}
	else {
		/*Not a functional type: Placeholder[input=Member 'generateName' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ generatedName = _this->generateName((*_this));
		_this->functions = _this->functions.addLast("auto " + (*_this) + "(void* _ref, auto " + beforeContent + ")" + "{" + _this->generateStatement("return " + (*_this).compileExpressionOrPlaceholder((*_this))) + (*_this).lineSeparator((*_this)) + "}" + (*_this).lineSeparator((*_this)));
		return new_Some</*???*/>((*_this));
	}
}
/*???*/ generateName_Main(void* _ref){
	Main* _this = (Main*) _ref;
	/*Cannot access member 'counter' in 'Placeholder[input=Unwrapped expression: "lambda" + (*_this)]', not an object.*/ generatedName = "lambda" + (*_this).counter;
	_this->counter++;
	return (*_this);
}
Option</*???*/> compileOperator_Main(void* _ref, /*???*/ input, /*???*/ operator){
	Main* _this = (Main*) _ref;
	if ((*_this).length((*_this)) < 3) 
		return new_None</*???*/>((*_this));
	if (!(*_this).contains((*_this))) 
		return new_None</*???*/>((*_this));
	/*Unwrapped expression: -1*/ i1 = -1;
	/*Unwrapped expression: 0*/ depth = 0;
	/*Unwrapped expression: 0*/ i = 0;
	while ((*_this) < (*_this).length((*_this)) - 1) {
		/*Not a functional type: Placeholder[input=Member 'charAt' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ c = (*_this).charAt((*_this));
		if ((*_this) == (*_this).charAt(0)) 
			if ((*_this) == 0) {
				(*_this) = (*_this);
				break;
			}
		if ((*_this) == '(') 
			(*_this)++;
		if ((*_this) == ')') 
			(*_this)--;
		(*_this)++;
	}
	if ((*_this) >= 0) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ leftString = (*_this).substring(0, (*_this));
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ right = (*_this).substring((*_this) + (*_this).length((*_this)));
		if (_this->parseCExpression((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).variant = ?.SomeVariant) 
			if (_this->parseCExpression((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).variant = ?.SomeVariant) 
				return new_Some</*???*/>((*_this) + " " + (*_this) + " " + (*_this));
	}
	return new_None</*???*/>((*_this));
}
Option<JExpression> parseInvokable_Main(void* _ref, /*???*/ stripped){
	Main* _this = (Main*) _ref;
	if (!(*_this).endsWith(")")) 
		return new_None<JExpression>((*_this));
	/*???*/ stripped1 = (*_this);
	/*Not a functional type: Placeholder[input=Member 'length' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ length = (*_this).length((*_this));
	/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ withoutEnd = (*_this).substring(0, (*_this) - 1);
	/*Not a functional type: Placeholder[input=Member 'findCallerStart' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ callerStart = _this->findCallerStart((*_this));
	if ((*_this) < 0) 
		return new_None<JExpression>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ callerString = (*_this).substring(0, (*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ argumentsString = (*_this).substring((*_this) + 1);
	/*Not a functional type: Placeholder[input=Member 'parseCaller' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeCaller = _this->parseCaller((*_this));
	if (!(*_this)((*_this).variant = ?.Some(var value)Variant)) 
		return new_None<JExpression>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]]', not an object.]*/ arguments = _this->divide((*_this), new_EscapedFolder(new_ValueFolder((*_this)))).map(F? { alloc((*_this)), F?Table { parseExpression }}).flatMap(F? { alloc((*_this)), F?Table { iter }}).toList((*_this));
	return new_Some<JExpression>(new_JInvokable((*_this), (*_this)));
}
int findCallerStart_Main(void* _ref, /*???*/ withoutEnd){
	Main* _this = (Main*) _ref;
	/*Unwrapped expression: -1*/ callerStart = -1;
	/*Unwrapped expression: 0*/ depth = 0;
	/*Unwrapped expression: 0*/ i = 0;
	while ((*_this) < (*_this).length((*_this))) {
		/*Not a functional type: Placeholder[input=Member 'charAt' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ c = (*_this).charAt((*_this));
		if ((*_this) == '(') {
			if ((*_this) == 0) 
				(*_this) = (*_this);
			(*_this)++;
		}
		if ((*_this) == ')') 
			(*_this)--;
		(*_this)++;
	}
	return (*_this);
}
int isNumber_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	if ((*_this).startsWith(" - ")) 
		return _this->allDigits((*_this).substring(1));
	return _this->allDigits((*_this));
}
int allDigits_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	return (*_this).range(0, (*_this).length((*_this))).mapToObj(F? { alloc((*_this)), F?Table { charAt }}).allMatch(F? { alloc((*_this)), F?Table { isDigit }});
}
Option<JCaller> parseCaller_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*???*/ stripped = (*_this).strip((*_this));
	/*Not a functional type: Placeholder[input=Member 'parseExpression' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ maybeExpression = _this->parseExpression((*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return new_Some<JCaller>((*_this));
	if ((*_this).startsWith("new ")) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ type = (*_this).substring("new ".length((*_this)));
		/*Not a functional type: Placeholder[input=Member 'parseType' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ jType = _this->parseType((*_this));
		return new_Some<JCaller>(new_JConstruction((*_this)));
	}
	return new_None<JCaller>((*_this));
}
Option<JDeclaration> parseDeclaration_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*???*/ stripped = (*_this).strip((*_this));
	/*Not a functional type: Placeholder[input=Member 'lastIndexOf' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ nameSeparator = (*_this).lastIndexOf(" ");
	if ((*_this) >= 0) {
		/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ beforeName = (*_this).substring(0, (*_this)).strip((*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ name = (*_this).substring((*_this) + 1).strip((*_this));
		/*Not a functional type: Placeholder[input=Member 'findTypeSeparator' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ typeSeparator = _this->findTypeSeparator((*_this));
		if (!(*_this).isIdentifier((*_this))) 
			return new_None<JDeclaration>((*_this));
		if ((*_this) < 0) {
			/*Not a functional type: Placeholder[input=Member 'parseType' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ type = _this->parseType((*_this));
			return new_Some<JDeclaration>(new_JDeclaration((*_this), (*_this)));
		}
		/*Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]*/ beforeType = (*_this).substring(0, (*_this)).strip((*_this));
		List</*???*/> copy = (*_this).empty((*_this));
		if ((*_this).endsWith(">")) {
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
			/*Not a functional type: Placeholder[input=Cannot access member 'indexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ i = (*_this).indexOf(" < ");
			if ((*_this) >= 0) {
				/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ substring2 = (*_this).substring((*_this) + 1);
				(*_this) = _this->splitValues((*_this));
				(*_this) = (*_this).substring(0, (*_this));
			}
		}
		List</*???*/> annotations = (*_this).empty((*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'lastIndexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'strip' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ i = (*_this).lastIndexOf("\n");
		if ((*_this) >= 0) {
			(*_this) = _this->collectAnnotations((*_this).substring(0, (*_this)));
			(*_this) = (*_this).substring((*_this) + 1).strip((*_this));
		}
		if (_this->isIdentifier((*_this))) {
			/*Not a functional type: Placeholder[input=Member 'parseType' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ type = _this->parseType((*_this).substring((*_this) + 1));
			JDeclaration jDeclaration = new_JDeclaration((*_this), (*_this), new_Some</*???*/>((*_this)), (*_this), (*_this));
			return new_Some<JDeclaration>((*_this));
		}
	}
	return new_None<JDeclaration>((*_this));
}
auto lambda56(void* _ref, auto slice){
	return !(*_this).isEmpty((*_this));
}
auto lambda57(void* _ref, auto slice){
	return (*_this).substring(1);
}
List</*???*/> collectAnnotations_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	return (*_this).fromObjArray((*_this).split((*_this).quote("\n"))).filter(lambda56).map(lambda57).map(F? { alloc((*_this)), F?Table { strip }}).toList((*_this));
}
int findTypeSeparator_Main(void* _ref, /*???*/ beforeName){
	Main* _this = (Main*) _ref;
	/*Unwrapped expression: -1*/ typeSeparator = -1;
	/*Unwrapped expression: 0*/ depth = 0;
	/*Unwrapped expression: 0*/ i = 0;
	while ((*_this) < (*_this).length((*_this))) {
		/*Not a functional type: Placeholder[input=Member 'charAt' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ c = (*_this).charAt((*_this));
		if ((*_this) == ' ' && (*_this) == 0) 
			(*_this) = (*_this);
		if ((*_this) == '<') 
			(*_this)++;
		if ((*_this) == '>') 
			(*_this)--;
		(*_this)++;
	}
	return (*_this);
}
JType parseType_Main(void* _ref, /*???*/ input){
	Main* _this = (Main*) _ref;
	/*???*/ stripped = (*_this).strip((*_this));
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
				return StringType;
			}
			case "Character" -> {
				return JPrimitiveType.Char;
			}
			case "var" -> {
				return JPrimitiveType.Var;
			}
		}*/
	if ((*_this).endsWith("[]")) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ slice = (*_this).substring(0, (*_this).length((*_this)) - 2);
		/*Not a functional type: Placeholder[input=Member 'parseType' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']*/ type = _this->parseType((*_this));
		return new_JArrayType((*_this));
	}
	if ((*_this).endsWith(">")) {
		/*Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']*/ substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'indexOf' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ i = (*_this).indexOf(" < ");
		if ((*_this) >= 0) {
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ base = (*_this).substring(0, (*_this));
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'substring' not defined in 'magma.Main$JRecursiveType@ba8a1dc']]', not an object.]*/ parameters = (*_this).substring((*_this) + 1);
			/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@1b701da1]']]', not an object.]]', not an object.]*/ list = _this->divide((*_this), new_ValueFolder((*_this))).map(F? { alloc((*_this)), F?Table { parseType }}).toList((*_this));
			return new_JGenericType((*_this), (*_this));
		}
	}
	if (_this->isIdentifier((*_this))) 
		return new_Identifier((*_this));
	return new_Placeholder((*_this));
}
