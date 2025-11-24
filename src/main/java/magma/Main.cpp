struct CPrimitiveType {
};
struct JPrimitiveType {
};
template <typename T>
struct HeadTable {
};
template <typename T>
struct Head {
	void* data;
	HeadTable<T> table;
};
template <typename T>
struct ListTable {
};
template <typename T>
struct List {
	void* data;
	ListTable<T> table;
};
struct PathTable {
};
struct Path {
	void* data;
	PathTable table;
};
template <typename T>
struct FRTable {
};
template <typename T>
struct FR {
	void* data;
	FRTable<T> table;
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
};
template <typename T0, typename R>
struct F1R {
	void* data;
	F1RTable<T0, R> table;
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
	FunctionDeclarationVariant,
	PlaceholderVariant
};
union CStructMemberData {
	EmptyStructMember EmptyStructMember;
	CField CField;
	FunctionDeclaration FunctionDeclaration;
	Placeholder Placeholder;
};
struct CStructMember {
	CStructMemberVariant variant;
	CStructMemberData data;
};
struct FolderTable {
};
struct Folder {
	void* data;
	FolderTable table;
};
template <typename A, typename B, typename R>
struct F2RTable {
};
template <typename A, typename B, typename R>
struct F2R {
	void* data;
	F2RTable<A, B, R> table;
};
struct ActualTable {
};
struct Actual {
	void* data;
	ActualTable table;
};
template <typename T, typename C>
struct CollectorTable {
};
template <typename T, typename C>
struct Collector {
	void* data;
	CollectorTable<T, C> table;
};
struct IOErrorTable {
};
struct IOError {
	void* data;
	IOErrorTable table;
};
struct CFunctionDeclarationTable {
};
struct CFunctionDeclaration {
	void* data;
	CFunctionDeclarationTable table;
};
struct CAssignableTable {
};
struct CAssignable {
	void* data;
	CAssignableTable table;
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
struct CDefinableTable {
};
struct CDefinable {
	void* data;
	CDefinableTable table;
};
enum CRootSegmentVariant {
	CStructureVariant,
	CEnumVariant,
	JUnionVariant
};
union CRootSegmentData {
	CStructure CStructure;
	CEnum CEnum;
	JUnion JUnion;
};
struct CRootSegment {
	CRootSegmentVariant variant;
	CRootSegmentData data;
};
enum JObjectMemberPrototypeVariant {
	EmptyStructMemberVariant,
	JFieldVariant,
	JMethodPrototypeVariant,
	JObjectPrototypeVariant,
	PlaceholderVariant
};
union JObjectMemberPrototypeData {
	EmptyStructMember EmptyStructMember;
	JField JField;
	JMethodPrototype JMethodPrototype;
	JObjectPrototype JObjectPrototype;
	Placeholder Placeholder;
};
struct JObjectMemberPrototype {
	JObjectMemberPrototypeVariant variant;
	JObjectMemberPrototypeData data;
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
};
struct CPointerType {
	CType type;
};
struct CTemplateType {
	char* base;
	List<CType> list;
};
struct CQuantity {
	CExpression expression;
};
struct CDereference {
	CExpression expression;
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
struct FunctionDeclaration {
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
	CDefinable declaration;
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
};
template <typename T, typename R>
struct FlatMapHead {
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
struct Environment {
};
struct Frame {
};
struct JObjectType {
	char* name;
	List<JDeclaration> members;
};
struct JRecursiveType {
};
struct CStructure {
	List<char*> typeParameters;
	char* name;
	List<CDefinable> fields;
};
struct CEnum {
	char* name;
	List<char*> variants;
};
struct JUnion {
	List<char*> typeParameters;
	char* name;
	List<char*> members;
};
struct JObjectPrototype {
	char* type;
	List<char*> annotations;
	List<char*> modifiersList;
	char* name;
	List<char*> typeParameters;
	List<JDeclaration> recordFields;
	List<CType> implementees;
	List<char*> variants;
	char* inputContent;
};
struct CFunctionHeader {
	CFunctionDeclaration definition;
	List<CDeclaration> parameters;
};
struct CFunction {
	CFunctionHeader header;
	char* content;
};
struct JMethodPrototype {
	List<char*> typeParameters;
	List<JDeclaration> parameters;
	JMethodDeclaration methodDeclaration;
	char* content;
};
struct JField {
	JDeclaration declaration;
};
struct Main {
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
List<T> addAllLast_List(void* _ref, List<T> elements);
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
Iter<T> iter_Option(void* _ref);
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
CAssignable toAssignable_Main(void* _ref);
CExpression toExpression_JCaller(void* _ref);
char* generate_CDefinable(void* _ref);
char* generate_CRootSegment(void* _ref);
StringBuilder empty_StringBuilders(void* _ref);
StringBuilder appendChar_StringBuilder(void* _ref, char next);
StringBuilder clear_StringBuilder(void* _ref);
char* toString_StringBuilder(void* _ref);
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
State<> new_State(char* input);
int isShallow_State(void* _ref);
int isLevel_State(void* _ref);
State append_State(void* _ref, char next);
Option<char> pop_State(void* _ref);
State advance_State(void* _ref);
State enter_State(void* _ref);
State exit_State(void* _ref);
Iter<char*> stream_State(void* _ref);
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref);
Option<State> popAndAppendToOption_State(void* _ref);
Option<char> peek_State(void* _ref);
char* generate_CPointerType(void* _ref);
char* toBaseName_CPointerType(void* _ref);
char* generate_CTemplateType(void* _ref);
char* toBaseName_CTemplateType(void* _ref);
char* generate_CQuantity(void* _ref);
char* generate_CDereference(void* _ref);
char* generate_Identifier(void* _ref);
char* toBaseName_Identifier(void* _ref);
CExpression toExpression_Identifier(void* _ref);
char* generate_Placeholder(void* _ref);
char* toBaseName_Placeholder(void* _ref);
CAssignable toCAssignable_Placeholder(void* _ref);
CType toCType_Placeholder(void* _ref);
JDeclaration<> new_JDeclaration(char* name, JType type);
JDeclaration mapName_JDeclaration(void* _ref, F1R<char*, char*> mapper);
CDeclaration toCDeclaration_JDeclaration(void* _ref);
CAssignable toCAssignable_JDeclaration(void* _ref);
JDeclaration withType_JDeclaration(void* _ref, JType type);
char* generate_FunctionDeclaration(void* _ref);
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
char* generate_CField(void* _ref);
template <typename T>
Iter<T> fromObjArray_Streams(void* _ref, T* elements);
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
CExpression toExpression_JExpressionWrapper(void* _ref);
CAssignable toAssignable_JExpressionWrapper(void* _ref);
char* generate_CExpressionWrapper(void* _ref);
CType toCType_JArrayType(void* _ref);
CType toCType_JGenericType(void* _ref);
char* generate_CPointerAccess(void* _ref);
char* generate_CFieldAccess(void* _ref);
CExpression toExpression_JMemberAccess(void* _ref);
CExpression toExpression_JConstruction(void* _ref);
char* generate_CInvocation(void* _ref);
CExpression toExpression_JInvokable(void* _ref);
JFunctionalType<> new_JFunctionalType(JType returnType);
/*private List<Frame> frames = new JavaList<Frame>*/();
Option<JDeclaration> resolveExpression_Environment(void* _ref, char* identifier);
template <typename T>
Tuple<Environment, T> withinScoped_Environment(void* _ref, F1R<Environment, Tuple<Environment, T>> supplier);
Environment defineAll_Environment(void* _ref, List<JDeclaration> declarations);
template <typename T>
Tuple<Environment, T> within_Environment(void* _ref, Supplier<T> supplier);
Environment define_Environment(void* _ref, JDeclaration declaration);
Option<JObjectType> resolveCurrent_Environment(void* _ref);
Environment withName_Environment(void* _ref, char* name);
Frame<> new_Frame(Option<char*> maybeName, List<JDeclaration> defined);
Frame<> new_Frame();
Frame defineAll_Frame(void* _ref, List<JDeclaration> declarations);
Option<JDeclaration> resolve_Frame(void* _ref, char* identifier);
Frame define_Frame(void* _ref, JDeclaration declaration);
Option<JObjectType> toStructureType_Frame(void* _ref);
Frame withName_Frame(void* _ref, char* name);
Option<JType> resolve_JObjectType(void* _ref, char* name);
/*private Option<JType> internal = new None<JType>*/();
JType create_JRecursiveType(void* _ref, F1R<JType, JType> mapper);
void set_JRecursiveType(void* _ref, JType created);
char* generate_CStructure(void* _ref);
char* generate_CEnum(void* _ref);
char* generate_JUnion(void* _ref);
List<CDefinable> collectCFields_JObjectPrototype(void* _ref);
List<CFunction> createConversionFunctions_JObjectPrototype(void* _ref);
CFunction createConversionType_JObjectPrototype(void* _ref, CType implementee);
/*var joinedTypeParameters = Main.joinTypeParameters*/();
/*final var identifier = implementee.toBaseName*/();
/*final var s = Main.generateStatement*/();
/*final var s1 = Main.generateStatement*/();
/*final var s2 = Main.generateStatement*/();
/*final var s3 = Main.generateStatement*/();
/*final var parameters = Lists.of*/();
new CFunctionHeader_JObjectPrototype(void* _ref, /*CDeclaration(implementee,*/ conversionFunctionName);
new CFunction_JObjectPrototype(void* _ref);
char* generate_CFunctionHeader(void* _ref);
char* generate_CFunction(void* _ref);
/*private static final JType StringType = JRecursiveType.create*/(/*-> {
		// We don't need parameter types for*/ now);
new Environment_Main(void* _ref);
Main<> new_Main();
char* generateTemplateString_Main(void* _ref, List<char*> typeParameters);
char* wrap_Main(void* _ref, char* input);
void main_Main(void* _ref, char** args);
char* generateStatement_Main(void* _ref, int depth, char* content);
char* generateIndent_Main(void* _ref, int depth);
CType transformType_Main(void* _ref, JType jType);
CType transformPrimitiveType_Main(void* _ref, JPrimitiveType type);
char* generateStatement_Main(void* _ref, char* content);
char* joinTypeParameters_Main(void* _ref, List<char*> typeParameters);
Option<IOError> run_Main(void* _ref);
char* compile_Main(void* _ref, char* input);
char* joinStrings_Main(void* _ref, char* delimiter, List<char*> structures);
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper);
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder);
Iter<char*> divide_Main(void* _ref, char* input, Folder folder);
State foldStatement_Main(void* _ref, State current, char next);
char* compileRootSegment_Main(void* _ref, char* input);
Option<JObjectPrototype> partiallyParseObject_Main(void* _ref, char* type, char* stripped);
Option<CStructMember> transformObject_Main(void* _ref, JObjectPrototype object);
Option<CStructMember> getCStructMemberOption_Main(void* _ref, JObjectPrototype object, JObjectMemberPrototype wrapper);
CStructMember completeMethodProto_Main(void* _ref, JMethodPrototype jFunctionProto, JObjectPrototype object);
Option<JObjectMemberPrototype> partiallyParseObjectMember_Main(void* _ref, JObjectPrototype object, char* input);
Option<JObjectMemberPrototype> compileMethod_Main(void* _ref, JObjectPrototype object, char* stripped);
List<CDefinable> retainFields_Main(void* _ref, List<CStructMember> members);
List<CRootSegment> flattenSealedStructure_Main(void* _ref, char* name, List<char*> typeParameters, List<char*> variants);
Option<CDefinable> retainDefinables_Main(void* _ref, CStructMember member);
List<char*> splitValues_Main(void* _ref, char* input);
int isIdentifier_Main(void* _ref, char* input);
char* computeMethodBody_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration, List<CDeclaration> cParameters, Option<char*> maybeContent, char* structName, List<char*> structureVariants);
char* createBodyForAbstractMethod_Main(void* _ref, List<char*> variants, CType type, char* name, List<char*> parameterNames);
CFunctionDeclaration transformMethodDeclaration_Main(void* _ref, char* structName, List<char*> typeParameters, JMethodDeclaration methodDeclaration);
CFunctionDeclaration convertToFunctionDeclarations_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration);
CType toConstructorReturnType_Main(void* _ref, char* base, List<char*> typeParameters);
char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent);
char* generateCase_Main(void* _ref, char* variant, char* name);
JMethodDeclaration parseMethodDeclaration_Main(void* _ref, char* declaration, char* structName);
JMethodDeclaration toInterface_Main(void* _ref, JDeclaration value);
Option<JMethodDeclaration> parseConstructor_Main(void* _ref, char* declaration, char* structName);
Option<JObjectMemberPrototype> compileEnumValues_Main(void* _ref, char* input, char* structName);
Option<CStructMember> compileEnumValue_Main(void* _ref, char* structName, char* enumValue);
char* compileMethodSegment_Main(void* _ref, char* input, int indent);
Option<char*> compileConditional_Main(void* _ref, char* type, int indent, char* input);
char* compileMethodStatement_Main(void* _ref, char* input);
Option<char*> compileAssignment_Main(void* _ref, char* stripped);
CAssignable transformAssignable_Main(void* _ref, JAssignable assignable, JExpression source);
JType resolveType_Main(void* _ref, JExpression source, JType type);
JType resolveExpression_Main(void* _ref, JExpression source);
JType getJType_Main(void* _ref, JMemberAccess access, JObjectType type, JType instanceType);
JType resolveCaller_Main(void* _ref, JCaller caller);
JAssignable parseAssignable_Main(void* _ref, char* input);
Option<char*> post_Main(void* _ref, char* stripped, char* slice);
char* compileExpressionOrPlaceholder_Main(void* _ref, char* input);
Option<CExpression> parseCExpression_Main(void* _ref, char* input);
Option<JExpression> parseExpression_Main(void* _ref, char* input);
Option<char*> compileLambda_Main(void* _ref, char* input);
Option<List<char*>> parseLambdaParams_Main(void* _ref, char* input);
char* generateName_Main(void* _ref);
Option<char*> compileOperator_Main(void* _ref, char* input, char* operator);
Option<JExpression> parseInvokable_Main(void* _ref, char* stripped);
int findCallerStart_Main(void* _ref, char* withoutEnd);
int isNumber_Main(void* _ref, char* input);
int allDigits_Main(void* _ref, char* input);
Option<JCaller> parseCaller_Main(void* _ref, char* input);
Option<JDeclaration> parseDeclaration_Main(void* _ref, char* input);
List<char*> collectAnnotations_Main(void* _ref, char* input);
int findTypeSeparator_Main(void* _ref, char* beforeName);
JType parseType_Main(void* _ref, char* input);
CType toCType_CPrimitiveType(void* _ref){
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	CTypeData data;
	data.CPrimitiveType = _this;
	return { CPrimitiveTypeVariant, data };
}
CPrimitiveType<> new_CPrimitiveType(char* content){
	CPrimitiveType _this;
	_this->content = (*_this);
	return _this;
}
char* generate_CPrimitiveType(void* _ref){
	CPrimitiveType* _this = (CPrimitiveType*) _ref;
	return _this->content;
}
char* toBaseName_CPrimitiveType(void* _ref){
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
List<T> addAllLast_List(void* _ref, List<T> elements){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.addAllLast(_this->data, elements);
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
		case FunctionDeclarationVariant:
			_ret = generate_FunctionDeclaration(&(_this->data.FunctionDeclaration));
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
char* generate_CDefinable(void* _ref){
	CDefinable* _this = (CDefinable*) _ref;
	return _this->table.generate(_this->data);
}
char* generate_CRootSegment(void* _ref){
	CRootSegment* _this = (CRootSegment*) _ref;
	char* _ret;
	switch (_this->variant) {
		case CStructureVariant:
			_ret = generate_CStructure(&(_this->data.CStructure));
			break;
		case CEnumVariant:
			_ret = generate_CEnum(&(_this->data.CEnum));
			break;
		case JUnionVariant:
			_ret = generate_JUnion(&(_this->data.JUnion));
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
char* toString_StringBuilder(void* _ref){
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
/*?*/ lambda0(void* _ref, /*?*/ element){
	return (*_this).apply((*_this), (*_this));
}
/*?*/ lambda1(void* _ref){
	return (*_this);
}
template <typename R, typename T>
R fold_Iter(void* _ref, R initial, F2R<R, T, R> folder){
	Iter<T>* _this = (Iter<T>*) _ref;
	R current = (*_this);
	while ((*_this)) {
		R finalCurrent = (*_this);
		/*Not a functional type: Placeholder[input=Cannot access member 'toTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Member 'head' not defined in 'JObjectType[name=Iter, members=magma.Main$JavaList@4e515669]']', not an object.]]', not an object.]]', not an object.]*/ tuple = _this->head.next((*_this)).map(lambda0).toTuple(lambda1);
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
/*?*/ lambda2(void* _ref, /*?*/ element){
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
State<> new_State(char* input){
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
		char value = _this->input.charAt(_this->index);
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
Iter<char*> stream_State(void* _ref){
	State* _this = (State*) _ref;
	return _this->segments.iter((*_this));
}
/*?*/ lambda3(void* _ref, /*?*/ popped){
	/*Not a functional type: Placeholder[input=Member 'append' not defined in 'JObjectType[name=State, members=magma.Main$JavaList@484b61fc]']*/ appended = _this->append((*_this));
	return new_Tuple<State, char>((*_this), (*_this));
}
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref){
	State* _this = (State*) _ref;
	return _this->pop((*_this)).map(lambda3);
}
/*?*/ lambda4(void* _ref, /*?*/ tuple){
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
char* generate_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return _this->type.generate((*_this)) + "*";
}
char* toBaseName_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return _this->type.toBaseName((*_this)) + "_ptr";
}
CType toCType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CTypeData data;
	data.CTemplateType = _this;
	return { CTemplateTypeVariant, data };
}
char* generate_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'list' not defined in 'JObjectType[name=CTemplateType, members=magma.Main$JavaList@4cdf35a9]']', not an object.]]', not an object.]]', not an object.]*/ typeArguments = _this->list.iter((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner(", "));
	return _this->base + " < " + (*_this) + ">";
}
char* toBaseName_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	return _this->base;
}
CExpression toCExpression_CQuantity(void* _ref){
	CQuantity _this = *((CQuantity*) _ref);
	CExpressionData data;
	data.CQuantity = _this;
	return { CQuantityVariant, data };
}
char* generate_CQuantity(void* _ref){
	CQuantity* _this = (CQuantity*) _ref;
	return "(" + this.expression.generate() + ")";
}
CExpression toCExpression_CDereference(void* _ref){
	CDereference _this = *((CDereference*) _ref);
	CExpressionData data;
	data.CDereference = _this;
	return { CDereferenceVariant, data };
}
char* generate_CDereference(void* _ref){
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
char* generate_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
char* toBaseName_Identifier(void* _ref){
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
JObjectMemberPrototype toJObjectMemberPrototype_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	JObjectMemberPrototypeData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
char* generate_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return (*_this)(_this->input);
}
char* toBaseName_Placeholder(void* _ref){
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
JDeclaration<> new_JDeclaration(char* name, JType type){
	JDeclaration _this;
	(*_this)((*_this).empty((*_this)), (*_this).empty((*_this)), new_None<char*>((*_this)), (*_this), (*_this));
	return _this;
}
JDeclaration mapName_JDeclaration(void* _ref, F1R<char*, char*> mapper){
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
CDefinable toCDefinable_FunctionDeclaration(void* _ref){
	FunctionDeclaration _this = *((FunctionDeclaration*) _ref);
	CDefinableData data;
	data.FunctionDeclaration = _this;
	return { FunctionDeclarationVariant, data };
}
CStructMember toCStructMember_FunctionDeclaration(void* _ref){
	FunctionDeclaration _this = *((FunctionDeclaration*) _ref);
	CStructMemberData data;
	data.FunctionDeclaration = _this;
	return { FunctionDeclarationVariant, data };
}
char* generate_FunctionDeclaration(void* _ref){
	FunctionDeclaration* _this = (FunctionDeclaration*) _ref;
	/*Unwrapped expression: "(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", ")) + ")"*/ joinedParameterTypes = "(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", ")) + ")";
	return _this->type.generate((*_this)) + " (*" + this.name + ")" + (*_this);
}
CStructMember toCStructMember_EmptyStructMember(void* _ref){
	EmptyStructMember _this = *((EmptyStructMember*) _ref);
	CStructMemberData data;
	data.EmptyStructMember = _this;
	return { EmptyStructMemberVariant, data };
}
JObjectMemberPrototype toJObjectMemberPrototype_EmptyStructMember(void* _ref){
	EmptyStructMember _this = *((EmptyStructMember*) _ref);
	JObjectMemberPrototypeData data;
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
/*?*/ lambda5(void* _ref, /*?*/ tuple){
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
char* generate_CField(void* _ref){
	CField* _this = (CField*) _ref;
	return (*_this).generateStatement(1, _this->declaration.generate((*_this)));
}
/*?*/ lambda6(void* _ref, /*?*/ index){
	return /*elements[index]*/;
}
template <typename T>
Iter<T> fromObjArray_Streams(void* _ref, T* elements){
	Streams* _this = (Streams*) _ref;
	return new_Iter<int>(new_RangeHead((*_this).length)).map(lambda6);
}
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
		/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'JGenericType[base=Head, typeArguments=magma.Main$JavaList@ea30797]', not an object.]*/ maybeNext = _this->head.next((*_this));
		if ((*_this).variant = ?.NoneVariant) 
			return new_None<R>((*_this));
		_this->maybeCurrent = (*_this).map(_this->mapper);
	}
}
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
	if ((*_this).isEmpty((*_this))) 
		return (*_this);
	return (*_this) + _this->delimiter + (*_this);
}
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
CDefinable toCDefinable_CDeclaration(void* _ref){
	CDeclaration _this = *((CDeclaration*) _ref);
	CDefinableData data;
	data.CDeclaration = _this;
	return { CDeclarationVariant, data };
}
CDeclaration<> new_CDeclaration(CType type, char* name){
	CDeclaration _this;
	(*_this)((*_this).empty((*_this)), (*_this), (*_this));
	return _this;
}
CFunctionDeclaration mapName_CDeclaration(void* _ref, F1R<char*, char*> mapper){
	CDeclaration* _this = (CDeclaration*) _ref;
	return new_CDeclaration(_this->typeParameters, _this->type, (*_this).apply(_this->name));
}
CFunctionDeclaration mapTypeParameters_CDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper){
	CDeclaration* _this = (CDeclaration*) _ref;
	return new_CDeclaration((*_this).apply(_this->typeParameters), _this->type, _this->name);
}
char* generate_CDeclaration(void* _ref){
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
char* generate_CExpressionWrapper(void* _ref){
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
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'typeArguments' not defined in 'JObjectType[name=JGenericType, members=magma.Main$JavaList@7e774085]']', not an object.]]', not an object.]]', not an object.]*/ newTypeArguments = _this->typeArguments.iter((*_this)).map(F? { alloc((*_this)), F?Table { transformType }}).toList((*_this));
	return new_CTemplateType(_this->base, (*_this));
}
CExpression toCExpression_CPointerAccess(void* _ref){
	CPointerAccess _this = *((CPointerAccess*) _ref);
	CExpressionData data;
	data.CPointerAccess = _this;
	return { CPointerAccessVariant, data };
}
char* generate_CPointerAccess(void* _ref){
	CPointerAccess* _this = (CPointerAccess*) _ref;
	return _this->instance.generate((*_this)) + "->" + (*_this).fieldName;
}
CExpression toCExpression_CFieldAccess(void* _ref){
	CFieldAccess _this = *((CFieldAccess*) _ref);
	CExpressionData data;
	data.CFieldAccess = _this;
	return { CFieldAccessVariant, data };
}
char* generate_CFieldAccess(void* _ref){
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
	/*Not a functional type: Placeholder[input=Cannot access member 'toExpression' in 'Placeholder[input=Member 'instance' not defined in 'JObjectType[name=JMemberAccess, members=magma.Main$JavaList@3f8f9dd6]']', not an object.]*/ cExpression = _this->instance.toExpression((*_this));
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
char* generate_CInvocation(void* _ref){
	CInvocation* _this = (CInvocation*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'cArguments' not defined in 'JObjectType[name=CInvocation, members=magma.Main$JavaList@aec6354]']]', not an object.]]', not an object.]]', not an object.]*/ joinedArguments = _this->cArguments((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner(", "));
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
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'arguments' not defined in 'JObjectType[name=JInvokable, members=magma.Main$JavaList@1c655221]']]', not an object.]]', not an object.]]', not an object.]*/ cArguments = _this->arguments((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { toExpression }}).toList((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toExpression' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'caller' not defined in 'JObjectType[name=JInvokable, members=magma.Main$JavaList@1c655221]']]', not an object.]*/ expression = _this->caller((*_this)).toExpression((*_this));
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
/*?*/ lambda7(void* _ref, /*?*/ frame){
	return (*_this).resolve((*_this));
}
Option<JDeclaration> resolveExpression_Environment(void* _ref, char* identifier){
	Environment* _this = (Environment*) _ref;
	return _this->frames.iter((*_this)).map(lambda7).flatMap(F? { alloc((*_this)), F?Table { iter }}).next((*_this));
}
template <typename T>
Tuple<Environment, T> withinScoped_Environment(void* _ref, F1R<Environment, Tuple<Environment, T>> supplier){
	Environment* _this = (Environment*) _ref;
	_this->frames = _this->frames.addLast(new_Frame((*_this)));
	/*Not a functional type: Placeholder[input=Cannot access member 'apply' in 'JGenericType[base=F1R, typeArguments=magma.Main$JavaList@58d25a40]', not an object.]*/ result = (*_this).apply((*_this));
	_this->frames = _this->frames.removeLast((*_this));
	return (*_this);
}
/*?*/ lambda8(void* _ref, /*?*/ last){
	return (*_this).defineAll((*_this));
}
Environment defineAll_Environment(void* _ref, List<JDeclaration> declarations){
	Environment* _this = (Environment*) _ref;
	_this->frames = _this->frames.mapLast(lambda8);
	return (*_this);
}
template <typename T>
Tuple<Environment, T> within_Environment(void* _ref, Supplier<T> supplier){
	Environment* _this = (Environment*) _ref;
	_this->frames = _this->frames.addLast(new_Frame((*_this)));
	/*Not a functional type: Placeholder[input=Cannot access member 'get' in 'JGenericType[base=Supplier, typeArguments=magma.Main$JavaList@1b701da1]', not an object.]*/ result = (*_this).get((*_this));
	_this->frames = _this->frames.removeLast((*_this));
	return new_Tuple<Environment, T>((*_this), (*_this));
}
/*?*/ lambda9(void* _ref, /*?*/ last){
	return (*_this).define((*_this));
}
Environment define_Environment(void* _ref, JDeclaration declaration){
	Environment* _this = (Environment*) _ref;
	_this->frames = _this->frames.mapLast(lambda9);
	return (*_this);
}
Option<JObjectType> resolveCurrent_Environment(void* _ref){
	Environment* _this = (Environment*) _ref;
	return _this->frames.iterReversed((*_this)).map(F? { alloc((*_this)), F?Table { toStructureType }}).flatMap(F? { alloc((*_this)), F?Table { iter }}).next((*_this));
}
/*?*/ lambda10(void* _ref, /*?*/ last){
	return (*_this).withName((*_this));
}
Environment withName_Environment(void* _ref, char* name){
	Environment* _this = (Environment*) _ref;
	_this->frames = _this->frames.mapLast(lambda10);
	return (*_this);
}
Frame<> new_Frame(Option<char*> maybeName, List<JDeclaration> defined){
	Frame _this;
	_this->maybeName = (*_this);
	_this->definitions = (*_this);
	return _this;
}
Frame<> new_Frame(){
	Frame _this;
	(*_this)(new_None<char*>((*_this)), new_JavaList<JDeclaration>((*_this)));
	return _this;
}
Frame defineAll_Frame(void* _ref, List<JDeclaration> declarations){
	Frame* _this = (Frame*) _ref;
	return new_Frame(_this->maybeName, _this->definitions.addAllLast((*_this)));
}
/*?*/ lambda11(void* _ref, /*?*/ define){
	return (*_this).name.equals((*_this));
}
Option<JDeclaration> resolve_Frame(void* _ref, char* identifier){
	Frame* _this = (Frame*) _ref;
	return _this->definitions.iter((*_this)).filter(lambda11).next((*_this));
}
Frame define_Frame(void* _ref, JDeclaration declaration){
	Frame* _this = (Frame*) _ref;
	_this->definitions = _this->definitions.addLast((*_this));
	return (*_this);
}
/*?*/ lambda12(void* _ref, /*?*/ name){
	return new_JObjectType((*_this), _this->definitions);
}
Option<JObjectType> toStructureType_Frame(void* _ref){
	Frame* _this = (Frame*) _ref;
	return _this->maybeName.map(lambda12);
}
Frame withName_Frame(void* _ref, char* name){
	Frame* _this = (Frame*) _ref;
	return new_Frame(new_Some<char*>((*_this)), _this->definitions);
}
JType toJType_JObjectType(void* _ref){
	JObjectType _this = *((JObjectType*) _ref);
	JTypeData data;
	data.JObjectType = _this;
	return { JObjectTypeVariant, data };
}
/*?*/ lambda13(void* _ref, /*?*/ member){
	return (*_this).name.equals((*_this));
}
Option<JType> resolve_JObjectType(void* _ref, char* name){
	JObjectType* _this = (JObjectType*) _ref;
	return _this->members.iter((*_this)).filter(lambda13).next((*_this)).map(F? { alloc((*_this)), F?Table { type }});
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
	/*Not a functional type: Placeholder[input=Cannot access member 'apply' in 'JGenericType[base=F1R, typeArguments=magma.Main$JavaList@726f3b58]', not an object.]*/ apply = (*_this).apply((*_this));
	(*_this).set((*_this));
	return (*_this);
}
void set_JRecursiveType(void* _ref, JType created){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	_this->internal = new_Some<JType>((*_this));
}
CRootSegment toCRootSegment_CStructure(void* _ref){
	CStructure _this = *((CStructure*) _ref);
	CRootSegmentData data;
	data.CStructure = _this;
	return { CStructureVariant, data };
}
char* generate_CStructure(void* _ref){
	CStructure* _this = (CStructure*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'fields' not defined in 'JObjectType[name=CStructure, members=magma.Main$JavaList@442d9b6e]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ joinedFields = _this->fields((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { new }}).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner((*_this)));
	return (*_this)(_this->typeParameters((*_this))) + "struct " + _this->name((*_this)) + " {" + (*_this) + (*_this).lineSeparator((*_this)) + "};" + (*_this).lineSeparator((*_this));
}
CRootSegment toCRootSegment_CEnum(void* _ref){
	CEnum _this = *((CEnum*) _ref);
	CRootSegmentData data;
	data.CEnum = _this;
	return { CEnumVariant, data };
}
/*?*/ lambda14(void* _ref, /*?*/ variant){
	return (*_this) + "Variant";
}
/*?*/ lambda15(void* _ref, /*?*/ variant){
	return (*_this)(1) + (*_this);
}
char* generate_CEnum(void* _ref){
	CEnum* _this = (CEnum*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'variants' not defined in 'JObjectType[name=CEnum, members=magma.Main$JavaList@ee7d9f1]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ enumFields = _this->variants((*_this)).iter((*_this)).map(lambda14).map(lambda15).collect(new_Joiner(","));
	return "enum " + _this->name((*_this)) + "Variant {" + (*_this) + (*_this).lineSeparator((*_this)) + "};" + (*_this).lineSeparator((*_this));
}
CRootSegment toCRootSegment_JUnion(void* _ref){
	JUnion _this = *((JUnion*) _ref);
	CRootSegmentData data;
	data.JUnion = _this;
	return { JUnionVariant, data };
}
char* generate_JUnion(void* _ref){
	JUnion* _this = (JUnion*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'members' not defined in 'JObjectType[name=JUnion, members=magma.Main$JavaList@15615099]']]', not an object.]]', not an object.]]', not an object.]*/ unionFields = _this->members((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { generateStatement }}).collect(new_Joiner((*_this)));
	return (*_this)(_this->typeParameters((*_this))) + "union " + _this->name((*_this)) + "Data {" + (*_this) + (*_this).lineSeparator((*_this)) + "};" + (*_this).lineSeparator((*_this));
}
JObjectMemberPrototype toJObjectMemberPrototype_JObjectPrototype(void* _ref){
	JObjectPrototype _this = *((JObjectPrototype*) _ref);
	JObjectMemberPrototypeData data;
	data.JObjectPrototype = _this;
	return { JObjectPrototypeVariant, data };
}
List<CDefinable> collectCFields_JObjectPrototype(void* _ref){
	JObjectPrototype* _this = (JObjectPrototype*) _ref;
	return /*this.recordFields.iter().map(JDeclaration::toCDeclaration).<CDefinable>map(value -> value).toList()*/;
}
List<CFunction> createConversionFunctions_JObjectPrototype(void* _ref){
	JObjectPrototype* _this = (JObjectPrototype*) _ref;
	return _this->implementees((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { createConversionType }}).toList((*_this));
}
CFunction createConversionType_JObjectPrototype(void* _ref, CType implementee){
	JObjectPrototype* _this = (JObjectPrototype*) _ref;
	return _this->table.createConversionType(_this->data, implementee);
}
/*var joinedTypeParameters = Main.joinTypeParameters*/(){?
}
/*final var identifier = implementee.toBaseName*/(){?
}
/*final var s = Main.generateStatement*/(){?
}
/*final var s1 = Main.generateStatement*/(){?
}
/*final var s2 = Main.generateStatement*/(){?
}
/*final var s3 = Main.generateStatement*/(){?
}
/*final var parameters = Lists.of*/(){?
}
new CFunctionHeader_JObjectPrototype(void* _ref, /*CDeclaration(implementee,*/ conversionFunctionName){
	JObjectPrototype* _this = (JObjectPrototype*) _ref;
	return _this->table.CFunctionHeader(_this->data, conversionFunctionName);
}
new CFunction_JObjectPrototype(void* _ref){
	JObjectPrototype* _this = (JObjectPrototype*) _ref;
	return _this->table.CFunction(_this->data);
}
char* generate_CFunctionHeader(void* _ref){
	CFunctionHeader* _this = (CFunctionHeader*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'parameters' not defined in 'JObjectType[name=CFunctionHeader, members=magma.Main$JavaList@1edf1c96]']]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = _this->parameters((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner(", "));
	return _this->definition((*_this)).generate((*_this)) + "(" + compiledParameters + ")";
}
char* generate_CFunction(void* _ref){
	CFunction* _this = (CFunction*) _ref;
	return _this->header((*_this)).generate((*_this)) + "{" + _this->content((*_this)) + (*_this).lineSeparator((*_this)) + "}" + (*_this).lineSeparator((*_this));
}
JObjectMemberPrototype toJObjectMemberPrototype_JMethodPrototype(void* _ref){
	JMethodPrototype _this = *((JMethodPrototype*) _ref);
	JObjectMemberPrototypeData data;
	data.JMethodPrototype = _this;
	return { JMethodPrototypeVariant, data };
}
JObjectMemberPrototype toJObjectMemberPrototype_JField(void* _ref){
	JField _this = *((JField*) _ref);
	JObjectMemberPrototypeData data;
	data.JField = _this;
	return { JFieldVariant, data };
}
/*private static final JType StringType = JRecursiveType.create*/(/*-> {
		// We don't need parameter types for*/ now){?
}
new Environment_Main(void* _ref){
	Main* _this = (Main*) _ref;
	return _this->table.Environment(_this->data);
}
Main<> new_Main(){
	Main _this;
	_this->rootSegments = (*_this).empty((*_this));
	_this->functionDeclarations = (*_this).empty((*_this));
	_this->functions = (*_this).empty((*_this));
	_this->globals = (*_this).empty((*_this));
	_this->counter = 0;
	return _this;
}
/*?*/ lambda16(void* _ref, /*?*/ typeParam){
	return "typename " + (*_this);
}
char* generateTemplateString_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* templateString;
	if ((*_this).isEmpty((*_this))) 
		(*_this) = "";
	else {
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@368102c8]', not an object.]]', not an object.]]', not an object.]*/ typeNames = (*_this).iter((*_this)).map(lambda16).collect(new_Joiner(", "));
		(*_this) = "template <" + (*_this) + ">" + (*_this).lineSeparator((*_this));
	}
	return (*_this);
}
char* wrap_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'replace' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'replace' not defined in 'magma.Main$JRecursiveType@6996db8']]', not an object.]*/ replaced = (*_this).replace("/*", "start").replace("*/", "end");
	return "/*" + (*_this) + "*/";
}
void main_Main(void* _ref, char** args){
	Main* _this = (Main*) _ref;
	if (new_Main((*_this)).run((*_this)).variant = ?.SomeVariant) 
		(*_this).err.println((*_this).display((*_this)));
}
char* generateStatement_Main(void* _ref, int depth, char* content){
	Main* _this = (Main*) _ref;
	return (*_this)((*_this)) + (*_this) + ";";
}
char* generateIndent_Main(void* _ref, int depth){
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
char* generateStatement_Main(void* _ref, char* content){
	Main* _this = (Main*) _ref;
	return (*_this)(1, (*_this));
}
char* joinTypeParameters_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* joinedTypeParameters;
	if ((*_this).isEmpty((*_this))) 
		(*_this) = "";
	else joinedTypeParameters = " < " + (*_this).iter((*_this)).collect(new_Joiner(", ")) + ">";
	return (*_this);
}
Option<IOError> run_Main(void* _ref){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]*/ source = (*_this).get(".", "src", "main", "java", "magma", "Main.java");
	/*Not a functional type: Placeholder[input=Cannot access member 'resolveSibling' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]]', not an object.]*/ target = (*_this).resolveSibling("Main.cpp");
	/*Not a functional type: Placeholder[input=Cannot access member 'mapValue' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'readString' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]]', not an object.]]', not an object.]*/ input = (*_this).readString((*_this)).mapValue(F? { alloc((*_this)), F?Table { compile }});
	return _switch;
}
char* compile_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'compileStatements' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ all = _this->compileStatements((*_this), F? { alloc((*_this)), F?Table { compileRootSegment }});
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@26f0a63f]', not an object.]]', not an object.]]', not an object.]*/ joinedStructures = _this->rootSegments.iter((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner((*_this)));
	/*Not a functional type: Placeholder[input=Member 'joinStrings' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ joinedGlobals = _this->joinStrings("", _this->globals);
	/*Not a functional type: Placeholder[input=Member 'joinStrings' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ joinedFunctionDeclarations = _this->joinStrings("", _this->functionDeclarations);
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@4361bd48]', not an object.]]', not an object.]]', not an object.]*/ joinedFunctions = _this->functions.iter((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner((*_this)));
	return (*_this) + (*_this) + (*_this) + (*_this) + (*_this);
}
char* joinStrings_Main(void* _ref, char* delimiter, List<char*> structures){
	Main* _this = (Main*) _ref;
	return (*_this).iter((*_this)).collect(new_Joiner((*_this)));
}
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper){
	Main* _this = (Main*) _ref;
	return _this->compileAll((*_this), (*_this), new_EscapedFolder(F? { alloc((*_this)), F?Table { foldStatement }}));
}
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder){
	Main* _this = (Main*) _ref;
	return _this->divide((*_this), (*_this)).map((*_this)).collect(new_Joiner(""));
}
Iter<char*> divide_Main(void* _ref, char* input, Folder folder){
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
/*?*/ lambda17(void* _ref){
	return (*_this)((*_this));
}
char* compileRootSegment_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = (*_this).strip((*_this));
	if ((*_this).isEmpty((*_this))) 
		return "";
	if ((*_this).startsWith("package ") || (*_this).startsWith("import ")) 
		return "";
	return _this->partiallyParseObject("class", (*_this)).flatMap(F? { alloc((*_this)), F?Table { transformObject }}).map(F? { alloc((*_this)), F?Table { generate }}).orElseGet(lambda17);
}
/*?*/ lambda18(void* _ref, /*?*/ slice){
	return !(*_this).isEmpty((*_this));
}
/*?*/ lambda19(void* _ref, /*?*/ input){
	return (*_this)(_this->parseType((*_this)));
}
/*?*/ lambda20(void* _ref, /*?*/ slice){
	return !(*_this).isEmpty((*_this));
}
Option<JObjectPrototype> partiallyParseObject_Main(void* _ref, char* type, char* stripped){
	Main* _this = (Main*) _ref;
	int i = (*_this).indexOf((*_this) + " ");
	if ((*_this) < 0) 
		return new_None<JObjectPrototype>((*_this));
	char* beforeType = (*_this).substring(0, (*_this)).strip((*_this));
	char* modifiers;
	List<char*> annotations = (*_this).empty((*_this));
	int i5 = (*_this).lastIndexOf("\n");
	if ((*_this) >= 0) {
		char* substring = (*_this).substring(0, (*_this));
		char* substring1 = (*_this).substring((*_this) + 1);
		(*_this) = _this->collectAnnotations((*_this));
		(*_this) = (*_this);
	}
	else modifiers = (*_this);
	char* afterKeyword = (*_this).substring((*_this) + (*_this)((*_this) + " ").length((*_this))).strip((*_this));
	int i1 = (*_this).indexOf("{");
	if ((*_this) < 0) 
		return new_None<JObjectPrototype>((*_this));
	char* beforeContent = (*_this).substring(0, (*_this)).strip((*_this));
	char* withEnd = (*_this).substring((*_this) + 1).strip((*_this));
	if (!(*_this).endsWith("}")) 
		return new_None<JObjectPrototype>((*_this));
	char* inputContent = (*_this).substring(0, (*_this).length((*_this)) - 1);
	List<char*> variants = (*_this).empty((*_this));
	int i2 = (*_this).indexOf("permits ");
	if ((*_this) >= 0) {
		char* substring1 = (*_this).substring((*_this) + "permits ".length((*_this)));
		(*_this) = (*_this).substring(0, (*_this));
		(*_this) = _this->splitValues((*_this));
	}
	List<CType> implementees = (*_this).empty((*_this));
	int i4 = (*_this).indexOf("implements ");
	if ((*_this) >= 0) {
		char* implementeesString = (*_this).substring((*_this) + "implements ".length((*_this)));
		(*_this) = (*_this).substring(0, (*_this)).strip((*_this));
		(*_this) = _this->divide((*_this), new_ValueFolder((*_this))).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda18).map(lambda19).toList((*_this));
	}
	List<JDeclaration> recordFields = (*_this).empty((*_this));
	if ((*_this).endsWith(")")) {
		char* substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
		int i3 = (*_this).indexOf("(");
		if ((*_this) >= 0) {
			(*_this) = (*_this).substring(0, (*_this));
			(*_this) = _this->divide((*_this).substring((*_this) + 1), new_ValueFolder((*_this))).map(F? { alloc((*_this)), F?Table { parseDeclaration }}).flatMap(F? { alloc((*_this)), F?Table { iter }}).toList((*_this));
		}
	}
	List<char*> typeParameters = (*_this).empty((*_this));
	int i3 = (*_this).indexOf(" < ");
	if ((*_this) >= 0) {
		char* substring1 = (*_this).substring((*_this) + 1).strip((*_this));
		if ((*_this).endsWith(">")) {
			(*_this) = (*_this).substring(0, (*_this));
			char* substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
			(*_this) = _this->splitValues((*_this));
		}
	}
	if (!(*_this).isIdentifier((*_this))) 
		return new_None<JObjectPrototype>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'fromObjArray' in 'Placeholder[input=Undefined identifier: Streams]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ modifiersList = (*_this).fromObjArray((*_this).split((*_this).quote(" "))).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda20).toList((*_this));
	char* name = (*_this).strip((*_this));
	// TODO: replace with the builder pattern
	/*final var prototype = new JObjectPrototype(type,*/
	/*annotations,*/
	/*modifiersList,*/
	/*name,*/
	/*typeParameters,*/
	/*recordFields,*/
	/*implementees,*/
	/*variants,*/
	/*inputContent)*/;
	return new_Some<JObjectPrototype>((*_this));
}
/*?*/ lambda21(void* _ref, /*?*/ env){
	return /*{
			final var withName = env.withName(object.name());

			// Note that withName is not used by members here*/;
}
Option<CStructMember> transformObject_Main(void* _ref, JObjectPrototype object){
	Main* _this = (Main*) _ref;
	if ((*_this).annotations.contains("Actual")) 
		return new_Some<CStructMember>(new_EmptyStructMember((*_this)));
	List<CRootSegment> dependencies = new_JavaList<CRootSegment>((*_this));
	_this->functions = (*_this).createConversionFunctions((*_this)).iter((*_this)).fold(_this->functions, F? { alloc((*_this)), F?Table { addLast }});
	/*Not a functional type: Placeholder[input=Cannot access member 'collectCFields' in 'Identifier[value=JObjectPrototype]', not an object.]*/ fields = (*_this).collectCFields((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']', not an object.]*/ within = _this->environment.withinScoped(lambda21);
	_this->environment = (*_this).left;
	/*Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']', not an object.]]', not an object.*/ members = (*_this).right;
	if ((*_this).type((*_this)).equals("interface")) 
		if ((*_this).modifiersList((*_this)).contains("sealed")) {
			/*Not a functional type: Placeholder[input=Member 'flattenSealedStructure' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ elements = _this->flattenSealedStructure((*_this).name((*_this)), (*_this).typeParameters((*_this)), (*_this).variants((*_this)));
			(*_this) = (*_this).addLast(new_CDeclaration(new_Identifier((*_this).name((*_this)) + "Variant"), "variant")).addLast(new_CDeclaration(new_Identifier((*_this).name((*_this)) + "Data" + (*_this).joinTypeParameters((*_this).typeParameters((*_this)))), "data"));
			(*_this) = (*_this).addAllLast((*_this));
		}
	else {
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']', not an object.]]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ list = (*_this).iter((*_this)).map(F? { alloc((*_this)), F?Table { retainDefinables }}).flatMap(F? { alloc((*_this)), F?Table { iter }}).toList((*_this));
		CStructure cStructure = new_CStructure((*_this).typeParameters((*_this)), (*_this).name((*_this)) + "Table", (*_this));
		(*_this) = (*_this).addLast((*_this));
		(*_this) = (*_this).addLast(new_CDeclaration(new_Identifier((*_this).name((*_this)) + "Table" + (*_this).joinTypeParameters((*_this).typeParameters((*_this)))), "table")).addFirst(new_CDeclaration(new_CPointerType((*_this).Void), "data"));
	}
	else fields = (*_this).addAllLast(_this->retainFields((*_this)));
	/*Not a functional type: Placeholder[input=Cannot access member 'addLast' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@53bd815b]', not an object.]*/ elements = (*_this).addLast(new_CStructure((*_this).typeParameters((*_this)), (*_this).name((*_this)), (*_this)));
	_this->rootSegments = _this->rootSegments.addAllLast((*_this));
	return new_Some<CStructMember>(new_EmptyStructMember((*_this)));
}
Option<CStructMember> getCStructMemberOption_Main(void* _ref, JObjectPrototype object, JObjectMemberPrototype wrapper){
	Main* _this = (Main*) _ref;
	return _switch;
}
/*?*/ lambda22(void* _ref, /*?*/ name){
	return (*_this) + "_" + (*_this).name;
}
/*?*/ lambda23(void* _ref){
	return new_Some<char*>(_this->compileMethodsSegments((*_this), 1));
}
/*?*/ lambda24(void* _ref, /*?*/ env){
	return (*_this).defineAll((*_this).parameters((*_this))).within(lambda23);
}
CStructMember completeMethodProto_Main(void* _ref, JMethodPrototype jFunctionProto, JObjectPrototype object){
	Main* _this = (Main*) _ref;
	Option<char*> maybeCompiled = new_None<char*>((*_this));
	if ((*_this).methodDeclaration((*_this)).variant = ?.JDeclaration declaration &&
				declaration.annotations.contains("Actual")Variant) {
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ cParameters = (*_this).parameters((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { toCDeclaration }}).toList((*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = (*_this).iter((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).collect(new_Joiner(", "));
		/*Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]]', not an object.]*/ modifiedMethodDeclaration = (*_this).mapName(lambda22).toCDeclaration((*_this));
		_this->functionDeclarations = _this->functionDeclarations.addLast((*_this).generate((*_this)) + "(" + compiledParameters + ");" + (*_this).lineSeparator((*_this)));
		return new_EmptyStructMember((*_this));
	}
	if ((*_this).content.startsWith("{") && (*_this).content.endsWith("}")) {
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Cannot access member 'content' in 'Identifier[value=JMethodPrototype]', not an object.]', not an object.]*/ inputContent = (*_this).content.substring(1, (*_this).content((*_this)).length((*_this)) - 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']', not an object.]*/ within = _this->environment.withinScoped(lambda24);
		_this->environment = (*_this).left;
		(*_this) = (*_this).right;
	}
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ cParameters = (*_this).parameters((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { toCDeclaration }}).toList((*_this));
	if ((*_this).methodDeclaration((*_this)).variant = ?.JDeclarationVariant) 
		(*_this) = (*_this).addFirst(new_CDeclaration(new_CPointerType((*_this).Void), "_ref"));
	/*Not a functional type: Placeholder[input=Member 'computeMethodBody' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ outputContent = _this->computeMethodBody((*_this).typeParameters((*_this)), (*_this).methodDeclaration((*_this)), (*_this), (*_this), (*_this).name, (*_this).variants);
	/*Not a functional type: Placeholder[input=Member 'transformMethodDeclaration' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ mapped = _this->transformMethodDeclaration((*_this).name, (*_this).typeParameters((*_this)), (*_this).methodDeclaration((*_this)));
	CFunctionHeader header = new_CFunctionHeader((*_this), (*_this));
	CFunction cFunction = new_CFunction((*_this), (*_this));
	_this->functionDeclarations = _this->functionDeclarations.addLast((*_this).generate((*_this)) + ";" + (*_this).lineSeparator((*_this)));
	_this->functions = _this->functions.addLast((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ parameterTypes = (*_this).iter((*_this)).map(F? { alloc((*_this)), F?Table { type }}).toList((*_this));
	return _switch;
}
Option<JObjectMemberPrototype> partiallyParseObjectMember_Main(void* _ref, JObjectPrototype object, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = (*_this).strip((*_this));
	if ((*_this).isEmpty((*_this))) 
		return new_None<JObjectMemberPrototype>((*_this));
	/*Not a functional type: Placeholder[input=Member 'partiallyParseObject' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeEnum = _this->partiallyParseObject("enum", (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>((*_this));
	/*Not a functional type: Placeholder[input=Member 'partiallyParseObject' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeInterface = _this->partiallyParseObject("interface", (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>((*_this));
	/*Not a functional type: Placeholder[input=Member 'partiallyParseObject' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeRecord = _this->partiallyParseObject("record", (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>((*_this));
	/*Not a functional type: Placeholder[input=Member 'partiallyParseObject' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeClass = _this->partiallyParseObject("class", (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>((*_this));
	/*Not a functional type: Placeholder[input=Member 'compileEnumValues' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeEnumValues = _this->compileEnumValues((*_this), (*_this).name((*_this)));
	if ((*_this).variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>((*_this));
	if ((*_this).endsWith(";")) {
		char* substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
		/*Not a functional type: Placeholder[input=Member 'parseDeclaration' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeDeclaration = _this->parseDeclaration((*_this));
		if ((*_this).variant = ?.SomeVariant) {
			_this->environment = _this->environment.define((*_this));
			return new_Some<JObjectMemberPrototype>(new_JField((*_this)));
		}
	}
	/*Not a functional type: Placeholder[input=Member 'compileMethod' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeMethod = _this->compileMethod((*_this), (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>((*_this));
	return new_Some<JObjectMemberPrototype>(new_Placeholder((*_this)));
}
/*?*/ lambda25(void* _ref, /*?*/ slice){
	return !(*_this).isEmpty((*_this));
}
Option<JObjectMemberPrototype> compileMethod_Main(void* _ref, JObjectPrototype object, char* stripped){
	Main* _this = (Main*) _ref;
	char* structName = (*_this).name((*_this));
	int i = (*_this).indexOf("(");
	if ((*_this) < 0) 
		return new_None<JObjectMemberPrototype>((*_this));
	char* declarationString = (*_this).substring(0, (*_this));
	char* substring1 = (*_this).substring((*_this) + 1);
	int i1 = (*_this).indexOf(")");
	if ((*_this) < 0) 
		return new_None<JObjectMemberPrototype>((*_this));
	char* parametersString = (*_this).substring(0, (*_this));
	char* withBraces = (*_this).substring((*_this) + 1).strip((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ parameters = _this->divide((*_this), new_ValueFolder((*_this))).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda25).toList((*_this)).iter((*_this)).map(F? { alloc((*_this)), F?Table { parseDeclaration }}).flatMap(F? { alloc((*_this)), F?Table { iter }}).toList((*_this));
	/*Not a functional type: Placeholder[input=Member 'parseMethodDeclaration' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ declaration = _this->parseMethodDeclaration((*_this), (*_this));
	JMethodPrototype proto = new_JMethodPrototype((*_this).typeParameters((*_this)), (*_this), (*_this), (*_this));
	return new_Some<JObjectMemberPrototype>((*_this));
}
/*?*/ lambda26(void* _ref, /*?*/ member){
	return !(*_this)((*_this).variant = ?.CFunctionDeclarationVariant);
}
List<CDefinable> retainFields_Main(void* _ref, List<CStructMember> members){
	Main* _this = (Main*) _ref;
	return (*_this).iter((*_this)).map(F? { alloc((*_this)), F?Table { retainDefinables }}).flatMap(F? { alloc((*_this)), F?Table { iter }}).filter(lambda26).toList((*_this));
}
/*?*/ lambda27(void* _ref, /*?*/ variant){
	return (*_this) + (*_this) + " " + (*_this);
}
List<CRootSegment> flattenSealedStructure_Main(void* _ref, char* name, List<char*> typeParameters, List<char*> variants){
	Main* _this = (Main*) _ref;
	CEnum jEnum = new_CEnum((*_this), (*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'joinTypeParameters' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ joinedTypeParameters = (*_this).joinTypeParameters((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@2401f4c3]', not an object.]]', not an object.]]', not an object.]*/ unionMembers = (*_this).iter((*_this)).map(lambda27).toList((*_this));
	JUnion union = new_JUnion((*_this), (*_this), (*_this));
	return (*_this).of((*_this), (*_this));
}
Option<CDefinable> retainDefinables_Main(void* _ref, CStructMember member){
	Main* _this = (Main*) _ref;
	if ((*_this).variant = ?.CDefinable definableVariant) 
		return new_Some<CDefinable>((*_this));
	/*else return new None<CDefinable>()*/;
}
/*?*/ lambda28(void* _ref, /*?*/ slice){
	return !(*_this).isEmpty((*_this));
}
List<char*> splitValues_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'split' not defined in 'magma.Main$JRecursiveType@6996db8']*/ segments = (*_this).split((*_this).quote(","));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ list = (*_this).stream((*_this)).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda28).toList((*_this));
	return new_JavaList<char*>((*_this));
}
/*?*/ lambda29(void* _ref, /*?*/ i){
	char c = (*_this).charAt((*_this));
	return (*_this).isLetter((*_this)) || (*_this)((*_this) != 0 && (*_this).isDigit((*_this)));
}
int isIdentifier_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = (*_this).strip((*_this));
	return (*_this).range(0, (*_this).length((*_this))).allMatch(lambda29);
}
/*?*/ lambda30(void* _ref, /*?*/ parameter){
	return (*_this).name;
}
/*?*/ lambda31(void* _ref){
	/*Not a functional type: Placeholder[input=Undefined identifier: transformType]*/ type = (*_this)((*_this).type);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'subList' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@4926097b]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ list = (*_this).subList(1, (*_this).size((*_this))).iter((*_this)).map(lambda30).toList((*_this));
	return _this->createBodyForAbstractMethod((*_this), (*_this), (*_this).name, (*_this));
}
char* computeMethodBody_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration, List<CDeclaration> cParameters, Option<char*> maybeContent, char* structName, List<char*> structureVariants){
	Main* _this = (Main*) _ref;
	if ((*_this).variant = ?.JConstructorVariant) {
		/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@7637f22]', not an object.]*/ compiled = (*_this).orElse("?");
		return (*_this).generateStatement((*_this) + " _this") + (*_this) + (*_this).generateStatement("return " + "_this");
	}
	if ((*_this).variant = ?.JDeclaration declarationVariant) {
		/*Not a functional type: Placeholder[input=Cannot access member 'joinTypeParameters' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ joinedTypeParameters = (*_this).joinTypeParameters((*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'generateStatement' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ thisInitialization = (*_this).generateStatement((*_this) + (*_this) + "* _this = (" + structName + joinedTypeParameters + "*) _ref");
		/*Not a functional type: Placeholder[input=Cannot access member 'orElseGet' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@7637f22]', not an object.]*/ body = (*_this).orElseGet(lambda31);
		return (*_this) + (*_this);
	}
	return "?";
}
/*?*/ lambda32(void* _ref, /*?*/ variant){
	return _this->generateCase((*_this), (*_this));
}
char* createBodyForAbstractMethod_Main(void* _ref, List<char*> variants, CType type, char* name, List<char*> parameterNames){
	Main* _this = (Main*) _ref;
	if ((*_this).isEmpty((*_this))) {
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@762efe5d]', not an object.]]', not an object.]]', not an object.]*/ joinedParameters = (*_this).addFirst("_this->data").iter((*_this)).collect(new_Joiner(", "));
		return (*_this).generateStatement("return _this->table." + (*_this) + "(" + joinedParameters + ")");
	}
	else {
		/*Not a functional type: Placeholder[input=Cannot access member 'generateStatement' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ returnValueDefinition = (*_this).generateStatement((*_this).generate((*_this)) + " _ret");
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@5d22bbb7]', not an object.]]', not an object.]]', not an object.]*/ cases = (*_this).iter((*_this)).map(lambda32).collect(new_Joiner((*_this)));
		return (*_this) + (*_this)(1) + "switch (" + "_this->variant" + ") {" + (*_this) + (*_this)(1) + "}" + (*_this).generateStatement("return _ret");
	}
}
/*?*/ lambda33(void* _ref, /*?*/ typeParameters0){
	return (*_this).addAllLast((*_this));
}
/*?*/ lambda34(void* _ref, /*?*/ name){
	return (*_this) + "_" + (*_this);
}
CFunctionDeclaration transformMethodDeclaration_Main(void* _ref, char* structName, List<char*> typeParameters, JMethodDeclaration methodDeclaration){
	Main* _this = (Main*) _ref;
	return _this->convertToFunctionDeclarations((*_this), (*_this)).mapTypeParameters(lambda33).mapName(lambda34);
}
CFunctionDeclaration convertToFunctionDeclarations_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration){
	Main* _this = (Main*) _ref;
	return _switch;
}
CType toConstructorReturnType_Main(void* _ref, char* base, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	if ((*_this).isEmpty((*_this))) 
		return new_Identifier((*_this));
	/*final var typeArguments = typeParameters.iter().<CType>map(Identifier::new).toList()*/;
	return new_CTemplateType((*_this), (*_this));
}
/*?*/ lambda35(void* _ref, /*?*/ input){
	return _this->compileMethodSegment((*_this), (*_this));
}
char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent){
	Main* _this = (Main*) _ref;
	return _this->compileStatements((*_this), lambda35);
}
char* generateCase_Main(void* _ref, char* variant, char* name){
	Main* _this = (Main*) _ref;
	return (*_this)(2) + "case " + (*_this) + "Variant:" + (*_this)(3, "_ret = " + (*_this) + "_" + (*_this) + "(&(_this->data." + variant + "))") + (*_this)(3, "break");
}
/*?*/ lambda36(void* _ref){
	return _this->parseDeclaration((*_this)).map(F? { alloc((*_this)), F?Table { toInterface }});
}
/*?*/ lambda37(void* _ref){
	return new_Placeholder((*_this));
}
JMethodDeclaration parseMethodDeclaration_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	return _this->parseConstructor((*_this), (*_this)).or(lambda36).orElseGet(lambda37);
}
JMethodDeclaration toInterface_Main(void* _ref, JDeclaration value){
	Main* _this = (Main*) _ref;
	return (*_this);
}
Option<JMethodDeclaration> parseConstructor_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	char* stripped = (*_this).strip((*_this));
	if ((*_this).equals((*_this))) 
		return new_Some<JMethodDeclaration>(new_JConstructor((*_this)));
	int i = (*_this).lastIndexOf(" ");
	if ((*_this) >= 0) {
		char* substring = (*_this).substring((*_this) + 1).strip((*_this));
		if ((*_this).equals((*_this))) 
			return new_Some<JMethodDeclaration>(new_JConstructor((*_this)));
	}
	return new_None<JMethodDeclaration>((*_this));
}
/*?*/ lambda38(void* _ref, /*?*/ state, /*?*/ character){
	return new_ValueFolder((*_this)).apply((*_this), (*_this));
}
/*?*/ lambda39(void* _ref, /*?*/ slice){
	return !(*_this).isEmpty((*_this));
}
/*?*/ lambda40(void* _ref, /*?*/ enumValue){
	return _this->compileEnumValue((*_this), (*_this));
}
Option<JObjectMemberPrototype> compileEnumValues_Main(void* _ref, char* input, char* structName){
	Main* _this = (Main*) _ref;
	char* stripped = (*_this).strip((*_this));
	if (!(*_this).endsWith(";")) 
		return new_None<JObjectMemberPrototype>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]]', not an object.]]', not an object.]*/ enumValues = _this->divide((*_this).substring(0, (*_this).length((*_this)) - 1), lambda38).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda39).toList((*_this));
	if (!(*_this).isEmpty((*_this))) {
		/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ optionStream = (*_this).iter((*_this)).map(lambda40);
		/*final var areAnyInvalid =
					(boolean) optionStream.collect(new AnyMatch<Option<CStructMember>>(option -> option instanceof None<CStructMember>))*/;
		if ((*_this)) 
			return new_None<JObjectMemberPrototype>((*_this));
	}
	return new_Some<JObjectMemberPrototype>(new_EmptyStructMember((*_this)));
}
Option<CStructMember> compileEnumValue_Main(void* _ref, char* structName, char* enumValue){
	Main* _this = (Main*) _ref;
	if ((*_this).endsWith(")")) {
		char* substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
		int i = (*_this).indexOf("(");
		if ((*_this) >= 0) {
			char* name = (*_this).substring(0, (*_this));
			if (!(*_this).isIdentifier((*_this))) 
				return new_None<CStructMember>((*_this));
			char* substring2 = (*_this).substring((*_this) + 1);
			/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Placeholder[input=Unwrapped expression: (*_this) + " " + (*_this) + (*_this) + " = " + "new_" + (*_this) + "(" + substring2 + ")" + ";" + (*_this)]', not an object.]*/ generated = (*_this) + " " + (*_this) + (*_this) + " = " + "new_" + (*_this) + "(" + substring2 + ")" + ";" + (*_this).lineSeparator((*_this));
			_this->globals = _this->globals.addLast((*_this));
			return new_Some<CStructMember>(new_EmptyStructMember((*_this)));
		}
	}
	return new_None<CStructMember>((*_this));
}
char* compileMethodSegment_Main(void* _ref, char* input, int indent){
	Main* _this = (Main*) _ref;
	char* stripped = (*_this).strip((*_this));
	if ((*_this).isEmpty((*_this))) 
		return "";
	/*Not a functional type: Placeholder[input=Member 'compileConditional' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeIf = _this->compileConditional("if", (*_this), (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'compileConditional' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeWhile = _this->compileConditional("while", (*_this), (*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	if ((*_this).endsWith(";")) {
		char* substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
		return (*_this)((*_this)) + _this->compileMethodStatement((*_this)) + ";";
	}
	if ((*_this).startsWith("else ")) {
		char* substring = (*_this).substring("else ".length((*_this))).strip((*_this));
		if ((*_this).startsWith("{") && (*_this).endsWith("}")) {
			char* substring1 = (*_this).substring(1, (*_this).length((*_this)) - 1);
			return (*_this)((*_this)) + "else {" + _this->compileMethodsSegments((*_this), (*_this) + 1) + (*_this)((*_this)) + "}";
		}
		/*else return generateIndent(indent) + "else " + this.compileMethodSegment(substring, indent)*/;
	}
	if ((*_this).startsWith("//")) 
		return (*_this)((*_this)) + (*_this);
	return (*_this).lineSeparator((*_this)) + "\t" + (*_this)((*_this));
}
/*?*/ lambda41(void* _ref, /*?*/ slice){
	return !(*_this).isEmpty((*_this));
}
Option<char*> compileConditional_Main(void* _ref, char* type, int indent, char* input){
	Main* _this = (Main*) _ref;
	if ((*_this).startsWith((*_this))) {
		char* substring = (*_this).substring((*_this).length((*_this))).strip((*_this));
		if ((*_this).startsWith("(")) {
			char* afterConditionStart = (*_this).substring(1).strip((*_this));
			/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]]', not an object.]]', not an object.]*/ divisions = _this->divide((*_this), new_EscapedFolder(new_ConditionEndLocator((*_this)))).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda41).toList((*_this));
			if ((*_this).size((*_this)) < 2) 
				return new_None<char*>((*_this));
			/*Not a functional type: Placeholder[input=Cannot access member 'getFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ first = (*_this).getFirst((*_this));
			/*Not a functional type: Placeholder[input=Member 'joinStrings' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeWithBraces = _this->joinStrings("", (*_this).subList(1, (*_this).size((*_this))));
			if (!(*_this).endsWith(")")) 
				return new_None<char*>((*_this));
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'getFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ condition = (*_this).substring(0, (*_this).length((*_this)) - 1);
			if ((*_this).startsWith("{") && (*_this).endsWith("}")) {
				/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'joinStrings' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]*/ content = (*_this).substring(1, (*_this).length((*_this)) - 1);
				return new_Some<char*>((*_this)((*_this)) + (*_this) + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" + _this->compileMethodsSegments((*_this), (*_this) + 1) + (*_this)((*_this)) + "}");
			}
			return new_Some<char*>((*_this)((*_this)) + (*_this) + " (" + this.compileExpressionOrPlaceholder(condition) + ") " + (*_this).compileMethodSegment((*_this), (*_this) + 1));
		}
	}
	return new_None<char*>((*_this));
}
char* compileMethodStatement_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = (*_this).strip((*_this));
	if ((*_this).equals("break")) 
		return "break";
	if ((*_this).startsWith("return ")) 
		return "return " + (*_this).compileExpressionOrPlaceholder((*_this).substring("return ".length((*_this))));
	/*Not a functional type: Placeholder[input=Member 'compileAssignment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeAssignment = _this->compileAssignment((*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'parseInvokable' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeInvokable = _this->parseInvokable((*_this));
	if ((*_this).variant = ?.Some(var value)Variant) 
		return (*_this).toExpression((*_this)).generate((*_this));
	/*Not a functional type: Placeholder[input=Member 'post' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ instance = _this->post((*_this), "++");
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'post' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ instance0 = _this->post((*_this), "--");
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Member 'parseDeclaration' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeDeclaration = _this->parseDeclaration((*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this).toCDeclaration((*_this)).generate((*_this));
	return (*_this)((*_this));
}
Option<char*> compileAssignment_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	int index = (*_this).indexOf("=");
	if ((*_this) >= 0) {
		char* destination = (*_this).substring(0, (*_this));
		char* substring1 = (*_this).substring((*_this) + 1);
		/*Not a functional type: Placeholder[input=Member 'parseAssignable' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ assignable = _this->parseAssignable((*_this));
		/*Not a functional type: Placeholder[input=Member 'parseExpression' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeSource = _this->parseExpression((*_this));
		if ((*_this).variant = ?.SomeVariant) {
			/*Not a functional type: Placeholder[input=Member 'transformAssignable' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ cAssignable = _this->transformAssignable((*_this), (*_this));
			return new_Some<char*>((*_this).generate((*_this)) + " = " + (*_this).toAssignable((*_this)).generate((*_this)));
		}
	}
	return new_None<char*>((*_this));
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
/*?*/ lambda42(void* _ref){
	return new_Placeholder("Member '" + (*_this).memberName + "' not defined in '" + (*_this) + "'");
}
JType getJType_Main(void* _ref, JMemberAccess access, JObjectType type, JType instanceType){
	Main* _this = (Main*) _ref;
	return (*_this).resolve((*_this).memberName).orElseGet(lambda42);
}
JType resolveCaller_Main(void* _ref, JCaller caller){
	Main* _this = (Main*) _ref;
	return _switch;
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
	if ((*_this).endsWith((*_this))) {
		char* instance = (*_this).substring(0, (*_this).length((*_this)) - 2);
		return new_Some<char*>(_this->compileExpressionOrPlaceholder((*_this)) + (*_this));
	}
	return new_None<char*>((*_this));
}
/*?*/ lambda43(void* _ref){
	return (*_this)((*_this));
}
char* compileExpressionOrPlaceholder_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return _this->parseCExpression((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).orElseGet(lambda43);
}
Option<CExpression> parseCExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return _this->parseExpression((*_this)).map(F? { alloc((*_this)), F?Table { toExpression }});
}
/*?*/ lambda44(void* _ref){
	return _this->compileOperator((*_this), " != ");
}
/*?*/ lambda45(void* _ref){
	return _this->compileOperator((*_this), " < ");
}
/*?*/ lambda46(void* _ref){
	return _this->compileOperator((*_this), " + ");
}
/*?*/ lambda47(void* _ref){
	return _this->compileOperator((*_this), " - ");
}
/*?*/ lambda48(void* _ref){
	return _this->compileOperator((*_this), " && ");
}
/*?*/ lambda49(void* _ref){
	return _this->compileOperator((*_this), " || ");
}
/*?*/ lambda50(void* _ref){
	return _this->compileOperator((*_this), " >= ");
}
Option<JExpression> parseExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = (*_this).strip((*_this));
	if ((*_this).startsWith("switch ")) 
		return new_Some<char*>("_switch").map(F? { alloc((*_this)), F?Table { new }});
	int i2 = (*_this).lastIndexOf("::");
	if ((*_this) >= 0) {
		char* substring = (*_this).substring(0, (*_this));
		char* name = (*_this).substring((*_this) + 2).strip((*_this));
		if (_this->isIdentifier((*_this))) {
			/*Not a functional type: Placeholder[input=Member 'compileExpressionOrPlaceholder' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ compiled = _this->compileExpressionOrPlaceholder((*_this));
			/*Unwrapped expression: "F?"*/ functionalInterfaceName = "F?";
			return new_Some<char*>((*_this) + " { alloc(" + compiled + "), " + (*_this) + "Table { " + (*_this) + " }}").map(F? { alloc((*_this)), F?Table { new }});
		}
	}
	if ((*_this).startsWith("'") && (*_this).endsWith("'")) 
		return new_Some<char*>((*_this)).map(F? { alloc((*_this)), F?Table { new }});
	/*Not a functional type: Placeholder[input=Member 'compileLambda' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeLambda = _this->compileLambda((*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this).map(F? { alloc((*_this)), F?Table { new }});
	int i3 = (*_this).indexOf(".variant = ?."Variant);
	if ((*_this) >= 0) {
		char* substring = (*_this).substring(0, (*_this));
		char* substring1 = (*_this).substring((*_this) + ".variant = ?.".length()Variant).strip((*_this));
		/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'parseCExpression' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]*/ maybeInstance = _this->parseCExpression((*_this)).map(F? { alloc((*_this)), F?Table { generate }});
		if ((*_this).variant = ?.SomeVariant) {
			int i4 = (*_this).indexOf(" < ");
			char* substring2;
			if ((*_this) >= 0) 
				(*_this) = (*_this).substring(0, (*_this));
			else substring2 = (*_this);
			return new_Some<char*>((*_this) + ".variant = ?." + (*_this) + "Variant").map(F? { alloc((*_this)), F?Table { new }});
		}
	}
	int i = (*_this).lastIndexOf(".");
	if ((*_this) >= 0) {
		char* instanceString = (*_this).substring(0, (*_this));
		char* memberName = (*_this).substring((*_this) + 1).strip((*_this));
		if (_this->isIdentifier((*_this))) {
			/*Not a functional type: Placeholder[input=Member 'parseExpression' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeInstance = _this->parseExpression((*_this));
			if ((*_this).variant = ?.Some(var value)Variant) 
				return new_Some<JExpression>(new_JMemberAccess((*_this), (*_this)));
		}
	}
	/*Not a functional type: Placeholder[input=Member 'parseInvokable' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeInvokable = _this->parseInvokable((*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this);
	/*Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'compileOperator' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ maybeOperator = _this->compileOperator((*_this), " == ").or(lambda44).or(lambda45).or(lambda46).or(lambda47).or(lambda48).or(lambda49).or(lambda50);
	if ((*_this).variant = ?.SomeVariant) 
		return (*_this).map(F? { alloc((*_this)), F?Table { new }});
	if (_this->isIdentifier((*_this))) 
		return new_Some<JExpression>(new_Identifier((*_this)));
	if ((*_this).startsWith("!")) {
		char* substring = (*_this).substring(1);
		/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'parseCExpression' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]*/ maybeInstance = _this->parseCExpression((*_this)).map(F? { alloc((*_this)), F?Table { generate }});
		if ((*_this).variant = ?.SomeVariant) 
			return new_Some<char*>("!" + (*_this)).map(F? { alloc((*_this)), F?Table { new }});
	}
	if (_this->isNumber((*_this))) 
		return new_Some<char*>((*_this)).map(F? { alloc((*_this)), F?Table { new }});
	if ((*_this).startsWith("\"") && (*_this).endsWith("\"")) 
		return new_Some<char*>((*_this)).map(F? { alloc((*_this)), F?Table { new }});
	return new_None<JExpression>((*_this));
}
/*?*/ lambda51(void* _ref, /*?*/ param){
	return new_CDeclaration(new_Placeholder("?"), (*_this));
}
Option<char*> compileLambda_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	int index = (*_this).indexOf("->");
	if ((*_this) < 0) 
		return new_None<char*>((*_this));
	char* beforeContent = (*_this).substring(0, (*_this)).strip((*_this));
	char* maybeWithBraces = (*_this).substring((*_this) + 2).strip((*_this));
	/*Not a functional type: Placeholder[input=Member 'parseLambdaParams' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeParams = _this->parseLambdaParams((*_this));
	if (!(*_this)((*_this).variant = ?.SomeVariant)) 
		return new_None<char*>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ paramList = (*_this).iter((*_this)).map(lambda51).toList((*_this)).addFirst(new_CDeclaration(new_CPointerType((*_this).Void), "_ref"));
	char* output;
	if ((*_this).startsWith("{") && (*_this).endsWith("}")) {
		char* content = (*_this).substring(1, (*_this).length((*_this)) - 1);
		(*_this) = _this->compileMethodsSegments((*_this), 1);
	}
	else output = (*_this).generateStatement("return " + (*_this).compileExpressionOrPlaceholder((*_this)));
	/*Not a functional type: Placeholder[input=Member 'generateName' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ generatedName = _this->generateName((*_this));
	CFunction cFunction = new_CFunction(new_CFunctionHeader(new_CDeclaration(new_Placeholder("?"), (*_this)), (*_this)), (*_this));
	_this->functions = _this->functions.addLast((*_this));
	return new_Some<char*>((*_this));
}
/*?*/ lambda52(void* _ref, /*?*/ slice){
	return !(*_this).isEmpty((*_this));
}
Option<List<char*>> parseLambdaParams_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	if (_this->isIdentifier((*_this))) 
		return new_Some<List<char*>>((*_this).of((*_this)));
	else 
	if ((*_this).startsWith("(") && input.endsWith(")")) {
		char* substring = (*_this).substring(1, (*_this).length((*_this)) - 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]]', not an object.]]', not an object.]*/ list = _this->divide((*_this), new_ValueFolder((*_this))).map(F? { alloc((*_this)), F?Table { strip }}).filter(lambda52).toList((*_this));
		return new_Some<List<char*>>((*_this));
	}
	/*else return new None<List<String>>()*/;
}
char* generateName_Main(void* _ref){
	Main* _this = (Main*) _ref;
	/*Cannot access member 'counter' in 'Placeholder[input=Unwrapped expression: "lambda" + (*_this)]', not an object.*/ generatedName = "lambda" + (*_this).counter;
	_this->counter++;
	return (*_this);
}
Option<char*> compileOperator_Main(void* _ref, char* input, char* operator){
	Main* _this = (Main*) _ref;
	if ((*_this).length((*_this)) < 3) 
		return new_None<char*>((*_this));
	if (!(*_this).contains((*_this))) 
		return new_None<char*>((*_this));
	/*Unwrapped expression: -1*/ i1 = -1;
	/*Unwrapped expression: 0*/ depth = 0;
	/*Unwrapped expression: 0*/ i = 0;
	while ((*_this) < (*_this).length((*_this)) - 1) {
		char c = (*_this).charAt((*_this));
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
		char* leftString = (*_this).substring(0, (*_this));
		char* right = (*_this).substring((*_this) + (*_this).length((*_this)));
		if (_this->parseCExpression((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).variant = ?.SomeVariant) 
			if (_this->parseCExpression((*_this)).map(F? { alloc((*_this)), F?Table { generate }}).variant = ?.SomeVariant) 
				return new_Some<char*>((*_this) + " " + (*_this) + " " + (*_this));
	}
	return new_None<char*>((*_this));
}
Option<JExpression> parseInvokable_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	if (!(*_this).endsWith(")")) 
		return new_None<JExpression>((*_this));
	int length = (*_this).length((*_this));
	char* withoutEnd = (*_this).substring(0, (*_this) - 1);
	/*Not a functional type: Placeholder[input=Member 'findCallerStart' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ callerStart = _this->findCallerStart((*_this));
	if ((*_this) < 0) 
		return new_None<JExpression>((*_this));
	char* callerString = (*_this).substring(0, (*_this));
	char* argumentsString = (*_this).substring((*_this) + 1);
	/*Not a functional type: Placeholder[input=Member 'parseCaller' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeCaller = _this->parseCaller((*_this));
	if (!(*_this)((*_this).variant = ?.Some(var value)Variant)) 
		return new_None<JExpression>((*_this));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]]', not an object.]]', not an object.]*/ arguments = _this->divide((*_this), new_EscapedFolder(new_ValueFolder((*_this)))).map(F? { alloc((*_this)), F?Table { parseExpression }}).flatMap(F? { alloc((*_this)), F?Table { iter }}).toList((*_this));
	return new_Some<JExpression>(new_JInvokable((*_this), (*_this)));
}
int findCallerStart_Main(void* _ref, char* withoutEnd){
	Main* _this = (Main*) _ref;
	/*Unwrapped expression: -1*/ callerStart = -1;
	/*Unwrapped expression: 0*/ depth = 0;
	/*Unwrapped expression: 0*/ i = 0;
	while ((*_this) < (*_this).length((*_this))) {
		char c = (*_this).charAt((*_this));
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
int isNumber_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	if ((*_this).startsWith(" - ")) 
		return _this->allDigits((*_this).substring(1));
	return _this->allDigits((*_this));
}
int allDigits_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return (*_this).range(0, (*_this).length((*_this))).mapToObj(F? { alloc((*_this)), F?Table { charAt }}).allMatch(F? { alloc((*_this)), F?Table { isDigit }});
}
Option<JCaller> parseCaller_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = (*_this).strip((*_this));
	/*Not a functional type: Placeholder[input=Member 'parseExpression' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ maybeExpression = _this->parseExpression((*_this));
	if ((*_this).variant = ?.SomeVariant) 
		return new_Some<JCaller>((*_this));
	if ((*_this).startsWith("new ")) {
		char* type = (*_this).substring("new ".length((*_this)));
		/*Not a functional type: Placeholder[input=Member 'parseType' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ jType = _this->parseType((*_this));
		return new_Some<JCaller>(new_JConstruction((*_this)));
	}
	return new_None<JCaller>((*_this));
}
Option<JDeclaration> parseDeclaration_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = (*_this).strip((*_this));
	int nameSeparator = (*_this).lastIndexOf(" ");
	if ((*_this) >= 0) {
		char* beforeName = (*_this).substring(0, (*_this)).strip((*_this));
		char* name = (*_this).substring((*_this) + 1).strip((*_this));
		/*Not a functional type: Placeholder[input=Member 'findTypeSeparator' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ typeSeparator = _this->findTypeSeparator((*_this));
		if (!(*_this).isIdentifier((*_this))) 
			return new_None<JDeclaration>((*_this));
		if ((*_this) < 0) {
			/*Not a functional type: Placeholder[input=Member 'parseType' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ type = _this->parseType((*_this));
			return new_Some<JDeclaration>(new_JDeclaration((*_this), (*_this)));
		}
		char* beforeType = (*_this).substring(0, (*_this)).strip((*_this));
		List<char*> copy = (*_this).empty((*_this));
		if ((*_this).endsWith(">")) {
			char* substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
			int i = (*_this).indexOf(" < ");
			if ((*_this) >= 0) {
				char* substring2 = (*_this).substring((*_this) + 1);
				(*_this) = _this->splitValues((*_this));
				(*_this) = (*_this).substring(0, (*_this));
			}
		}
		List<char*> annotations = (*_this).empty((*_this));
		int i = (*_this).lastIndexOf("\n");
		if ((*_this) >= 0) {
			(*_this) = _this->collectAnnotations((*_this).substring(0, (*_this)));
			(*_this) = (*_this).substring((*_this) + 1).strip((*_this));
		}
		if (_this->isIdentifier((*_this))) {
			/*Not a functional type: Placeholder[input=Member 'parseType' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ type = _this->parseType((*_this).substring((*_this) + 1));
			JDeclaration jDeclaration = new_JDeclaration((*_this), (*_this), new_Some<char*>((*_this)), (*_this), (*_this));
			return new_Some<JDeclaration>((*_this));
		}
	}
	return new_None<JDeclaration>((*_this));
}
/*?*/ lambda53(void* _ref, /*?*/ slice){
	return !(*_this).isEmpty((*_this));
}
/*?*/ lambda54(void* _ref, /*?*/ slice){
	return (*_this).substring(1);
}
List<char*> collectAnnotations_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return (*_this).fromObjArray((*_this).split((*_this).quote("\n"))).filter(lambda53).map(lambda54).map(F? { alloc((*_this)), F?Table { strip }}).toList((*_this));
}
int findTypeSeparator_Main(void* _ref, char* beforeName){
	Main* _this = (Main*) _ref;
	/*Unwrapped expression: -1*/ typeSeparator = -1;
	/*Unwrapped expression: 0*/ depth = 0;
	/*Unwrapped expression: 0*/ i = 0;
	while ((*_this) < (*_this).length((*_this))) {
		char c = (*_this).charAt((*_this));
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
JType parseType_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = (*_this).strip((*_this));
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
		char* slice = (*_this).substring(0, (*_this).length((*_this)) - 2);
		/*Not a functional type: Placeholder[input=Member 'parseType' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']*/ type = _this->parseType((*_this));
		return new_JArrayType((*_this));
	}
	if ((*_this).endsWith(">")) {
		char* substring = (*_this).substring(0, (*_this).length((*_this)) - 1);
		int i = (*_this).indexOf(" < ");
		if ((*_this) >= 0) {
			char* base = (*_this).substring(0, (*_this));
			char* parameters = (*_this).substring((*_this) + 1);
			/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'divide' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@182decdb]']]', not an object.]]', not an object.]*/ list = _this->divide((*_this), new_ValueFolder((*_this))).map(F? { alloc((*_this)), F?Table { parseType }}).toList((*_this));
			return new_JGenericType((*_this), (*_this));
		}
	}
	if (_this->isIdentifier((*_this))) 
		return new_Identifier((*_this));
	return new_Placeholder((*_this));
}
