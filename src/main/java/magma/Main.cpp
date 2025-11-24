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
CExpression toExpression_JConstruction(void* _ref);
char* generate_CInvocation(void* _ref);
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
CExpression transformCaller_Main(void* _ref, JCaller jCaller);
CExpression transformExpression_Main(void* _ref, JExpression expression);
CInvocation transformInvocation_Main(void* _ref, JInvokable jInvokable);
Option<IOError> run_Main(void* _ref);
char* compile_Main(void* _ref, char* input);
char* joinStrings_Main(void* _ref, List<char*> structures);
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper);
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder);
Iter<char*> divide_Main(void* _ref, char* input, Folder folder);
State foldStatement_Main(void* _ref, State current, char next);
char* compileRootSegment_Main(void* _ref, char* input);
Option<JObjectPrototype> partiallyParseObject_Main(void* _ref, char* type, char* stripped);
Option<CStructMember> transformObject_Main(void* _ref, JObjectPrototype object);
Option<JDeclaration> extractMethodDeclaration_Main(void* _ref, JObjectMemberPrototype prototype);
Option<CStructMember> completeObjectMemberPrototype_Main(void* _ref, JObjectPrototype object, JObjectMemberPrototype wrapper);
CStructMember completeMethodProto_Main(void* _ref, JMethodPrototype jFunctionProto, JObjectPrototype object);
Option<JObjectMemberPrototype> partiallyParseObjectMember_Main(void* _ref, JObjectPrototype object, char* input);
Option<JObjectMemberPrototype> parseMethod_Main(void* _ref, JObjectPrototype object, char* stripped);
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
	_this->content = content;
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
	return this;
}
CFunctionDeclaration mapName_CFunctionDeclaration(void* _ref, F1R<char*, char*> mapper){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return this;
}
char* generate_CFunctionDeclaration(void* _ref){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return _this->table.generate(_this->data);
}
char* generate_CAssignable(void* _ref){
	CAssignable* _this = (CAssignable*) _ref;
	return _this->table.generate(_this->data);
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
	return new_StringBuilder(empty_/*Undefined identifier: Lists*/(Lists, ));
}
StringBuilder appendChar_StringBuilder(void* _ref, char next){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder(addLast_/*Member 'list' not defined in 'JObjectType[name=StringBuilder, members=magma.Main$JavaList@64a294a6]'*/(_this->list, next));
}
StringBuilder clear_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder(clear_/*Member 'list' not defined in 'JObjectType[name=StringBuilder, members=magma.Main$JavaList@64a294a6]'*/(_this->list, ));
}
char* toString_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'list' not defined in 'JObjectType[name=StringBuilder, members=magma.Main$JavaList@64a294a6]']', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'list' not defined in 'JObjectType[name=StringBuilder, members=magma.Main$JavaList@64a294a6]']', not an object.]*/(iter_/*Member 'list' not defined in 'JObjectType[name=StringBuilder, members=magma.Main$JavaList@64a294a6]'*/(_this->list, ), F? { alloc(String), F?Table { valueOf }}), new_Joiner());
}
template <typename T, typename T>
Iter<T> of_Iter(void* _ref, T value){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter<T>(new_SingleHead<T>(value));
}
template <typename T, typename T>
Iter<T> empty_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter<T>(new_EmptyHead<T>());
}
template <typename R, typename T>
Iter<R> map_Iter(void* _ref, F1R<T, R> mapper){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter<R>(new_MapHead<T, R>(_this->head, mapper));
}
/*?*/ lambda0(void* _ref, /*?*/ element){
	return apply_F2R(folder, finalCurrent, element);
}
/*?*/ lambda1(void* _ref){
	return finalCurrent;
}
template <typename R, typename T>
R fold_Iter(void* _ref, R initial, F2R<R, T, R> folder){
	Iter<T>* _this = (Iter<T>*) _ref;
	R current = initial;
	while (true) {
		R finalCurrent = current;
		/*Not a functional type: Placeholder[input=Cannot access member 'toTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Member 'head' not defined in 'JObjectType[name=Iter, members=magma.Main$JavaList@3b764bce]']', not an object.]]', not an object.]]', not an object.]*/ tuple = toTuple_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Member 'head' not defined in 'JObjectType[name=Iter, members=magma.Main$JavaList@3b764bce]']', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Member 'head' not defined in 'JObjectType[name=Iter, members=magma.Main$JavaList@3b764bce]']', not an object.]*/(next_/*Member 'head' not defined in 'JObjectType[name=Iter, members=magma.Main$JavaList@3b764bce]'*/(_this->head, ), lambda0), lambda1);
		if (tuple.left) 
			current = tuple.right;
		return current;
	}
}
template <typename C, typename T>
C collect_Iter(void* _ref, Collector<T, C> collector){
	Iter<T>* _this = (Iter<T>*) _ref;
	return fold_Iter(this, createInitial_Collector(collector, ), F? { alloc(collector), F?Table { fold }});
}
template <typename T>
List<T> toList_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return collect_Iter(this, new_ListCollector<T>());
}
/*?*/ lambda2(void* _ref, /*?*/ element){
	if (apply_F1R(predicate, element)) 
		return new_Iter<T>(new_SingleHead<T>(element));
	return new_Iter<T>(new_EmptyHead<T>());
}
template <typename T>
Iter<T> filter_Iter(void* _ref, F1R<T, int> predicate){
	Iter<T>* _this = (Iter<T>*) _ref;
	return flatMap_Iter(this, lambda2);
}
template <typename R, typename T>
Iter<R> flatMap_Iter(void* _ref, F1R<T, Iter<R>> mapper){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter<R>(new_FlatMapHead<T, R>(_this->head, mapper));
}
template <typename T>
Option<T> next_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return next_/*Member 'head' not defined in 'JObjectType[name=Iter, members=magma.Main$JavaList@3b764bce]'*/(_this->head, );
}
Head<int> toHead_RangeHead(void* _ref){
	RangeHead _this = *((RangeHead*) _ref);
	HeadData data;
	data.RangeHead = _this;
	return { RangeHeadVariant, data };
}
RangeHead<> new_RangeHead(int length){
	RangeHead _this;
	_this->length = length;
	_this->counter = 0;
	return _this;
}
Option<int> next_RangeHead(void* _ref){
	RangeHead* _this = (RangeHead*) _ref;
	if (_this->counter < this.length) {
		int value = _this->counter;
		_this->counter++;
		return new_Some<int>(value);
	}
	/*else return new None<Integer>()*/;
}
template <typename T>
List<T> of_Lists(void* _ref, /*T...*/ elements){
	Lists* _this = (Lists*) _ref;
	return collect_/*Not a functional type: Placeholder[input=Cannot access member 'fromObjArray' in 'Placeholder[input=Undefined identifier: Streams]', not an object.]*/(fromObjArray_/*Undefined identifier: Streams*/(Streams, elements), new_ListCollector<T>());
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
	return new_Ok<R, X>(apply_F1R(mapper, _this->value));
}
State<> new_State(char* input){
	State _this;
	_this->input = input;
	_this->index = 0;
	_this->buffer = empty_/*Undefined identifier: StringBuilders*/(StringBuilders, );
	_this->depth = 0;
	_this->segments = empty_/*Undefined identifier: Lists*/(Lists, );
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
	_this->buffer = appendChar_StringBuilder(_this->buffer, next);
	return this;
}
Option<char> pop_State(void* _ref){
	State* _this = (State*) _ref;
	if (length_/*Cannot access member 'input' in 'Placeholder[input=Unwrapped expression: _this->index < this]', not an object.*/(_this->index < this.input, )) {
		char value = charAt_char_ptr(_this->input, _this->index);
		_this->index++;
		return new_Some<char>(value);
	}
	/*else return new None<Character>()*/;
}
State advance_State(void* _ref){
	State* _this = (State*) _ref;
	_this->segments = addLast_List(_this->segments, toString_StringBuilder(_this->buffer, ));
	_this->buffer = clear_StringBuilder(_this->buffer, );
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
Iter<char*> stream_State(void* _ref){
	State* _this = (State*) _ref;
	return iter_List(_this->segments, );
}
/*?*/ lambda3(void* _ref, /*?*/ popped){
	State appended = append_State(this, popped);
	return new_Tuple<State, char>(appended, popped);
}
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref){
	State* _this = (State*) _ref;
	return map_Option(pop_State(this, ), lambda3);
}
/*?*/ lambda4(void* _ref, /*?*/ tuple){
	return tuple.left;
}
Option<State> popAndAppendToOption_State(void* _ref){
	State* _this = (State*) _ref;
	return map_Option(popAndAppendToTuple_State(this, ), lambda4);
}
Option<char> peek_State(void* _ref){
	State* _this = (State*) _ref;
	if (length_/*Cannot access member 'input' in 'Placeholder[input=Unwrapped expression: _this->index < this]', not an object.*/(_this->index < this.input, )) 
		return new_Some<char>(charAt_char_ptr(_this->input, _this->index));
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
	return generate_/*Member 'type' not defined in 'JObjectType[name=CPointerType, members=magma.Main$JavaList@5eb5c224]'*/(_this->type, ) + "*";
}
char* toBaseName_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return toBaseName_/*Member 'type' not defined in 'JObjectType[name=CPointerType, members=magma.Main$JavaList@5eb5c224]'*/(_this->type, ) + "_ptr";
}
CType toCType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CTypeData data;
	data.CTemplateType = _this;
	return { CTemplateTypeVariant, data };
}
char* generate_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'list' not defined in 'JObjectType[name=CTemplateType, members=magma.Main$JavaList@73a8dfcc]']', not an object.]]', not an object.]]', not an object.]*/ typeArguments = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'list' not defined in 'JObjectType[name=CTemplateType, members=magma.Main$JavaList@73a8dfcc]']', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'list' not defined in 'JObjectType[name=CTemplateType, members=magma.Main$JavaList@73a8dfcc]']', not an object.]*/(iter_/*Member 'list' not defined in 'JObjectType[name=CTemplateType, members=magma.Main$JavaList@73a8dfcc]'*/(_this->list, ), F? { alloc(CType), F?Table { generate }}), new_Joiner(", "));
	return _this->base + " < " + typeArguments + ">";
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
	return generate_/*Cannot access member 'expression' in 'Placeholder[input=Unwrapped expression: "*" + this]', not an object.*/("*" + this.expression, );
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
	return wrap(_this->input);
}
char* toBaseName_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return wrap(_this->input);
}
CAssignable toCAssignable_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return this;
}
CType toCType_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return this;
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
	this(empty_/*Undefined identifier: Lists*/(Lists, ), empty_/*Undefined identifier: Lists*/(Lists, ), new_None<char*>(), type, name);
	return _this;
}
JDeclaration mapName_JDeclaration(void* _ref, F1R<char*, char*> mapper){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_JDeclaration(_this->annotations, _this->typeParameters, _this->maybeBeforeType, _this->type, apply_F1R(mapper, _this->name));
}
CDeclaration toCDeclaration_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_CDeclaration(_this->typeParameters, transformType(_this->type), _this->name);
}
CAssignable toCAssignable_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	return toCDeclaration_JDeclaration(this, );
}
JDeclaration withType_JDeclaration(void* _ref, JType type){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_JDeclaration(_this->annotations, _this->typeParameters, _this->maybeBeforeType, type, _this->name);
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
	return generate_/*Member 'type' not defined in 'JObjectType[name=FunctionDeclaration, members=magma.Main$JavaList@ea30797]'*/(_this->type, ) + " (*" + this.name + ")" + joinedParameterTypes;
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
	if (tuple.right == '\\') 
		return orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Cannot access member 'left' in 'Placeholder[input=Undefined identifier: tuple]', not an object.]', not an object.]*/(popAndAppendToOption_/*Cannot access member 'left' in 'Placeholder[input=Undefined identifier: tuple]', not an object.*/(tuple.left, ), tuple.left);
	return tuple.left;
}
State apply_EscapedFolder(void* _ref, State state, char next){
	EscapedFolder* _this = (EscapedFolder*) _ref;
	if (next == '\'') {
		/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = append_State(state, next);
		return orElse_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(popAndAppendToTuple_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, ), lambda5), F? { alloc(State), F?Table { popAndAppendToOption }}), appended);
	}
	if (next == '\"') {
		/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ current = append_State(state, next);
		while (true) {
			/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/ maybeTuple = popAndAppendToTuple_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(current, );
			if (!(maybeTuple.variant = ?.SomeVariant)) 
				break;
			current = value.left;
			/*Cannot access member 'right' in 'Placeholder[input=Undefined identifier: value]', not an object.*/ right = value.right;
			if (right == '\\') 
				current = orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(current, ), current);
			if (right == '\"') 
				break;
		}
		return current;
	}
	return apply_/*Member 'folder' not defined in 'JObjectType[name=EscapedFolder, members=magma.Main$JavaList@1b701da1]'*/(_this->folder, state, next);
}
Folder toFolder_ValueFolder(void* _ref){
	ValueFolder _this = *((ValueFolder*) _ref);
	FolderData data;
	data.ValueFolder = _this;
	return { ValueFolderVariant, data };
}
State apply_ValueFolder(void* _ref, State state, char next){
	ValueFolder* _this = (ValueFolder*) _ref;
	if (isLevel_/*Unwrapped expression: next == ',' && state*/(next == ',' && state, )) 
		return advance_State(state, );
	/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = append_State(state, next);
	if (next == '-') {
		/*Not a functional type: Placeholder[input=Cannot access member 'peek' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/ peeked = peek_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, );
		if (peeked.variant = ?.SomeVariant) 
			return orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, ), appended);
		return appended;
	}
	if (next == '<' || next == '(') 
		return enter_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, );
	if (next == '>' || next == ')') 
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, );
	return appended;
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
	return new_Some<R>(apply_F1R(mapper, _this->value));
}
template <typename T>
T orElse_Some(void* _ref, T other){
	Some<T>* _this = (Some<T>*) _ref;
	return _this->value;
}
template <typename R, typename T>
Option<R> flatMap_Some(void* _ref, F1R<T, Option<R>> mapper){
	Some<T>* _this = (Some<T>*) _ref;
	return apply_F1R(mapper, _this->value);
}
template <typename T>
T orElseGet_Some(void* _ref, FR<T> other){
	Some<T>* _this = (Some<T>*) _ref;
	return _this->value;
}
template <typename T>
Iter<T> iter_Some(void* _ref){
	Some<T>* _this = (Some<T>*) _ref;
	return of_/*Undefined identifier: Iter*/(Iter, _this->value);
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
	return apply_FR(other, );
}
template <typename T>
Iter<T> iter_None(void* _ref){
	None<T>* _this = (None<T>*) _ref;
	return empty_/*Undefined identifier: Iter*/(Iter, );
}
template <typename T>
Option<T> or_None(void* _ref, FR<Option<T>> other){
	None<T>* _this = (None<T>*) _ref;
	return apply_FR(other, );
}
template <typename T>
Tuple<int, T> toTuple_None(void* _ref, FR<T> other){
	None<T>* _this = (None<T>*) _ref;
	return new_Tuple<int, T>(false, apply_FR(other, ));
}
Folder toFolder_ConditionEndLocator(void* _ref){
	ConditionEndLocator _this = *((ConditionEndLocator*) _ref);
	FolderData data;
	data.ConditionEndLocator = _this;
	return { ConditionEndLocatorVariant, data };
}
State apply_ConditionEndLocator(void* _ref, State state, char c){
	ConditionEndLocator* _this = (ConditionEndLocator*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = append_State(state, c);
	if (c == '(') 
		return enter_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, );
	if (c == ')') {
		if (isLevel_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, )) 
			return advance_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, );
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, );
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
	return generateStatement_/*Undefined identifier: Main*/(Main, 1, generate_/*Member 'declaration' not defined in 'JObjectType[name=CField, members=magma.Main$JavaList@726f3b58]'*/(_this->declaration, ));
}
/*?*/ lambda6(void* _ref, /*?*/ index){
	return /*elements[index]*/;
}
template <typename T>
Iter<T> fromObjArray_Streams(void* _ref, T* elements){
	Streams* _this = (Streams*) _ref;
	return map_Iter(new_Iter<int>(new_RangeHead(elements.length)), lambda6);
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
	return map_/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Member 'head' not defined in 'JObjectType[name=MapHead, members=magma.Main$JavaList@442d9b6e]']', not an object.]*/(next_/*Member 'head' not defined in 'JObjectType[name=MapHead, members=magma.Main$JavaList@442d9b6e]'*/(_this->head, ), _this->mapper);
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
	_this->value = value;
	_this->retrieved = false;
	return _this;
}
template <typename T>
Option<T> next_SingleHead(void* _ref){
	SingleHead<T>* _this = (SingleHead<T>*) _ref;
	if (_this->retrieved) 
		return new_None<T>();
	_this->retrieved = true;
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
	_this->head = head;
	_this->mapper = mapper;
	_this->maybeCurrent = new_None<Iter<R>>();
	return _this;
}
template <typename T, typename R>
Option<R> next_FlatMapHead(void* _ref){
	FlatMapHead<T, R>* _this = (FlatMapHead<T, R>*) _ref;
	while (true) {
		if (_this->maybeCurrent.variant = ?.SomeVariant) {
			/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Cannot access member 'head' in 'Placeholder[input=Undefined identifier: current]', not an object.]', not an object.]*/ next = next_/*Cannot access member 'head' in 'Placeholder[input=Undefined identifier: current]', not an object.*/(current.head, );
			if (next.variant = ?.SomeVariant) 
				return next;
		}
		/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'JGenericType[base=Head, typeArguments=magma.Main$JavaList@ee7d9f1]', not an object.]*/ maybeNext = next_Head(_this->head, );
		if (maybeNext.variant = ?.NoneVariant) 
			return new_None<R>();
		_this->maybeCurrent = map_/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'JGenericType[base=Head, typeArguments=magma.Main$JavaList@ee7d9f1]', not an object.]*/(maybeNext, _this->mapper);
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
	return new_None<T>();
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
	return false;
}
template <typename T>
int fold_AnyMatch(void* _ref, int aBoolean, T t){
	AnyMatch<T>* _this = (AnyMatch<T>*) _ref;
	return apply_/*Cannot access member 'predicate' in 'Placeholder[input=Unwrapped expression: aBoolean || this]', not an object.*/(aBoolean || this.predicate, t);
}
Collector<char*, char*> toCollector_Joiner(void* _ref){
	Joiner _this = *((Joiner*) _ref);
	CollectorData data;
	data.Joiner = _this;
	return { JoinerVariant, data };
}
Joiner<> new_Joiner(){
	Joiner _this;
	this("");
	return _this;
}
char* createInitial_Joiner(void* _ref){
	Joiner* _this = (Joiner*) _ref;
	return "";
}
char* fold_Joiner(void* _ref, char* current, char* element){
	Joiner* _this = (Joiner*) _ref;
	if (isEmpty_char_ptr(current, )) 
		return element;
	return current + _this->delimiter + element;
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
	return empty_/*Undefined identifier: Lists*/(Lists, );
}
template <typename T>
List<T> fold_ListCollector(void* _ref, List<T> tList, T t){
	ListCollector<T>* _this = (ListCollector<T>*) _ref;
	return addLast_List(tList, t);
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
	this(empty_/*Undefined identifier: Lists*/(Lists, ), type, name);
	return _this;
}
CFunctionDeclaration mapName_CDeclaration(void* _ref, F1R<char*, char*> mapper){
	CDeclaration* _this = (CDeclaration*) _ref;
	return new_CDeclaration(_this->typeParameters, _this->type, apply_F1R(mapper, _this->name));
}
CFunctionDeclaration mapTypeParameters_CDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper){
	CDeclaration* _this = (CDeclaration*) _ref;
	return new_CDeclaration(apply_F1R(mapper, _this->typeParameters), _this->type, _this->name);
}
char* generate_CDeclaration(void* _ref){
	CDeclaration* _this = (CDeclaration*) _ref;
	char* template = generateTemplateString(_this->typeParameters);
	return template + generate_/*Member 'type' not defined in 'JObjectType[name=CDeclaration, members=magma.Main$JavaList@15615099]'*/(_this->type, ) + " " + this.name;
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
	return new_CPointerType(transformType(_this->type));
}
JType toJType_JGenericType(void* _ref){
	JGenericType _this = *((JGenericType*) _ref);
	JTypeData data;
	data.JGenericType = _this;
	return { JGenericTypeVariant, data };
}
CType toCType_JGenericType(void* _ref){
	JGenericType* _this = (JGenericType*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'typeArguments' not defined in 'JObjectType[name=JGenericType, members=magma.Main$JavaList@1edf1c96]']', not an object.]]', not an object.]]', not an object.]*/ newTypeArguments = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'typeArguments' not defined in 'JObjectType[name=JGenericType, members=magma.Main$JavaList@1edf1c96]']', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'typeArguments' not defined in 'JObjectType[name=JGenericType, members=magma.Main$JavaList@1edf1c96]']', not an object.]*/(iter_/*Member 'typeArguments' not defined in 'JObjectType[name=JGenericType, members=magma.Main$JavaList@1edf1c96]'*/(_this->typeArguments, ), F? { alloc(Main), F?Table { transformType }}), );
	return new_CTemplateType(_this->base, newTypeArguments);
}
CExpression toCExpression_CPointerAccess(void* _ref){
	CPointerAccess _this = *((CPointerAccess*) _ref);
	CExpressionData data;
	data.CPointerAccess = _this;
	return { CPointerAccessVariant, data };
}
char* generate_CPointerAccess(void* _ref){
	CPointerAccess* _this = (CPointerAccess*) _ref;
	return generate_/*Member 'instance' not defined in 'JObjectType[name=CPointerAccess, members=magma.Main$JavaList@368102c8]'*/(_this->instance, ) + "->" + this.fieldName;
}
CExpression toCExpression_CFieldAccess(void* _ref){
	CFieldAccess _this = *((CFieldAccess*) _ref);
	CExpressionData data;
	data.CFieldAccess = _this;
	return { CFieldAccessVariant, data };
}
char* generate_CFieldAccess(void* _ref){
	CFieldAccess* _this = (CFieldAccess*) _ref;
	return generate_/*Member 'instance' not defined in 'JObjectType[name=CFieldAccess, members=magma.Main$JavaList@6996db8]'*/(_this->instance, ) + "." + this.fieldName;
}
JExpression toJExpression_JMemberAccess(void* _ref){
	JMemberAccess _this = *((JMemberAccess*) _ref);
	JExpressionData data;
	data.JMemberAccess = _this;
	return { JMemberAccessVariant, data };
}
JCaller toJCaller_JConstruction(void* _ref){
	JConstruction _this = *((JConstruction*) _ref);
	JCallerData data;
	data.JConstruction = _this;
	return { JConstructionVariant, data };
}
CExpression toExpression_JConstruction(void* _ref){
	JConstruction* _this = (JConstruction*) _ref;
	return new_Identifier(generate_/*Not a functional type: Placeholder[input=Unwrapped expression: "new_" + transformType]*/("new_" + transformType(_this->jType), ));
}
CExpression toCExpression_CInvocation(void* _ref){
	CInvocation _this = *((CInvocation*) _ref);
	CExpressionData data;
	data.CInvocation = _this;
	return { CInvocationVariant, data };
}
char* generate_CInvocation(void* _ref){
	CInvocation* _this = (CInvocation*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'cArguments' not defined in 'JObjectType[name=CInvocation, members=magma.Main$JavaList@1963006a]']]', not an object.]]', not an object.]]', not an object.]*/ joinedArguments = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'cArguments' not defined in 'JObjectType[name=CInvocation, members=magma.Main$JavaList@1963006a]']]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'cArguments' not defined in 'JObjectType[name=CInvocation, members=magma.Main$JavaList@1963006a]']]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Member 'cArguments' not defined in 'JObjectType[name=CInvocation, members=magma.Main$JavaList@1963006a]']*/(cArguments_CInvocation(this, ), ), F? { alloc(CAssignable), F?Table { generate }}), new_Joiner(", "));
	return generate_/*Not a functional type: Placeholder[input=Member 'expression' not defined in 'JObjectType[name=CInvocation, members=magma.Main$JavaList@1963006a]']*/(expression_CInvocation(this, ), ) + "(" + joinedArguments + ")";
}
JExpression toJExpression_JInvokable(void* _ref){
	JInvokable _this = *((JInvokable*) _ref);
	JExpressionData data;
	data.JInvokable = _this;
	return { JInvokableVariant, data };
}
JType toJType_JFunctionalType(void* _ref){
	JFunctionalType _this = *((JFunctionalType*) _ref);
	JTypeData data;
	data.JFunctionalType = _this;
	return { JFunctionalTypeVariant, data };
}
JFunctionalType<> new_JFunctionalType(JType returnType){
	JFunctionalType _this;
	this(new_JavaList<JType>(), returnType);
	return _this;
}
/*private List<Frame> frames = new JavaList<Frame>*/(){?
}
/*?*/ lambda7(void* _ref, /*?*/ frame){
	return resolve_/*Undefined identifier: frame*/(frame, identifier);
}
Option<JDeclaration> resolveExpression_Environment(void* _ref, char* identifier){
	Environment* _this = (Environment*) _ref;
	return next_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]']', not an object.]]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]']', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]']', not an object.]*/(iter_/*Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]'*/(_this->frames, ), lambda7), F? { alloc(Option), F?Table { iter }}), );
}
template <typename T>
Tuple<Environment, T> withinScoped_Environment(void* _ref, F1R<Environment, Tuple<Environment, T>> supplier){
	Environment* _this = (Environment*) _ref;
	_this->frames = addLast_/*Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]'*/(_this->frames, new_Frame());
	/*Not a functional type: Placeholder[input=Cannot access member 'apply' in 'JGenericType[base=F1R, typeArguments=magma.Main$JavaList@41975e01]', not an object.]*/ result = apply_F1R(supplier, this);
	_this->frames = removeLast_/*Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]'*/(_this->frames, );
	return result;
}
/*?*/ lambda8(void* _ref, /*?*/ last){
	return defineAll_/*Undefined identifier: last*/(last, declarations);
}
Environment defineAll_Environment(void* _ref, List<JDeclaration> declarations){
	Environment* _this = (Environment*) _ref;
	_this->frames = mapLast_/*Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]'*/(_this->frames, lambda8);
	return this;
}
template <typename T>
Tuple<Environment, T> within_Environment(void* _ref, Supplier<T> supplier){
	Environment* _this = (Environment*) _ref;
	_this->frames = addLast_/*Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]'*/(_this->frames, new_Frame());
	/*Not a functional type: Placeholder[input=Cannot access member 'get' in 'JGenericType[base=Supplier, typeArguments=magma.Main$JavaList@c2e1f26]', not an object.]*/ result = get_Supplier(supplier, );
	_this->frames = removeLast_/*Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]'*/(_this->frames, );
	return new_Tuple<Environment, T>(this, result);
}
/*?*/ lambda9(void* _ref, /*?*/ last){
	return define_/*Undefined identifier: last*/(last, declaration);
}
Environment define_Environment(void* _ref, JDeclaration declaration){
	Environment* _this = (Environment*) _ref;
	_this->frames = mapLast_/*Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]'*/(_this->frames, lambda9);
	return this;
}
Option<JObjectType> resolveCurrent_Environment(void* _ref){
	Environment* _this = (Environment*) _ref;
	return next_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterReversed' in 'Placeholder[input=Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]']', not an object.]]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterReversed' in 'Placeholder[input=Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]']', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iterReversed' in 'Placeholder[input=Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]']', not an object.]*/(iterReversed_/*Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]'*/(_this->frames, ), F? { alloc(Frame), F?Table { toStructureType }}), F? { alloc(Option), F?Table { iter }}), );
}
/*?*/ lambda10(void* _ref, /*?*/ last){
	return withName_/*Undefined identifier: last*/(last, name);
}
Environment withName_Environment(void* _ref, char* name){
	Environment* _this = (Environment*) _ref;
	_this->frames = mapLast_/*Member 'frames' not defined in 'JObjectType[name=Environment, members=magma.Main$JavaList@7fbe847c]'*/(_this->frames, lambda10);
	return this;
}
Frame<> new_Frame(Option<char*> maybeName, List<JDeclaration> defined){
	Frame _this;
	_this->maybeName = maybeName;
	_this->definitions = defined;
	return _this;
}
Frame<> new_Frame(){
	Frame _this;
	this(new_None<char*>(), new_JavaList<JDeclaration>());
	return _this;
}
Frame defineAll_Frame(void* _ref, List<JDeclaration> declarations){
	Frame* _this = (Frame*) _ref;
	return new_Frame(_this->maybeName, addAllLast_List(_this->definitions, declarations));
}
/*?*/ lambda11(void* _ref, /*?*/ define){
	return equals_/*Cannot access member 'name' in 'JFunctionalType[parameterTypes=magma.Main$JavaList@dcf3e99, returnType=Identifier[value=Frame]]', not an object.*/(define.name, identifier);
}
Option<JDeclaration> resolve_Frame(void* _ref, char* identifier){
	Frame* _this = (Frame*) _ref;
	return next_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@6d9c638]', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@6d9c638]', not an object.]*/(iter_List(_this->definitions, ), lambda11), );
}
Frame define_Frame(void* _ref, JDeclaration declaration){
	Frame* _this = (Frame*) _ref;
	_this->definitions = addLast_List(_this->definitions, declaration);
	return this;
}
/*?*/ lambda12(void* _ref, /*?*/ name){
	return new_JObjectType(name, _this->definitions);
}
Option<JObjectType> toStructureType_Frame(void* _ref){
	Frame* _this = (Frame*) _ref;
	return map_Option(_this->maybeName, lambda12);
}
Frame withName_Frame(void* _ref, char* name){
	Frame* _this = (Frame*) _ref;
	return new_Frame(new_Some<char*>(name), _this->definitions);
}
JType toJType_JObjectType(void* _ref){
	JObjectType _this = *((JObjectType*) _ref);
	JTypeData data;
	data.JObjectType = _this;
	return { JObjectTypeVariant, data };
}
/*?*/ lambda13(void* _ref, /*?*/ member){
	return equals_/*Cannot access member 'name' in 'Placeholder[input=Undefined identifier: member]', not an object.*/(member.name, name);
}
Option<JType> resolve_JObjectType(void* _ref, char* name){
	JObjectType* _this = (JObjectType*) _ref;
	return map_/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'members' not defined in 'JObjectType[name=JObjectType, members=magma.Main$JavaList@7dc5e7b4]']', not an object.]]', not an object.]]', not an object.]*/(next_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'members' not defined in 'JObjectType[name=JObjectType, members=magma.Main$JavaList@7dc5e7b4]']', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Member 'members' not defined in 'JObjectType[name=JObjectType, members=magma.Main$JavaList@7dc5e7b4]']', not an object.]*/(iter_/*Member 'members' not defined in 'JObjectType[name=JObjectType, members=magma.Main$JavaList@7dc5e7b4]'*/(_this->members, ), lambda13), ), F? { alloc(JDeclaration), F?Table { type }});
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
	JRecursiveType created = new_JRecursiveType();
	/*Not a functional type: Placeholder[input=Cannot access member 'apply' in 'JGenericType[base=F1R, typeArguments=magma.Main$JavaList@1ee0005]', not an object.]*/ apply = apply_F1R(mapper, created);
	set_JRecursiveType(created, apply);
	return created;
}
void set_JRecursiveType(void* _ref, JType created){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	_this->internal = new_Some<JType>(created);
}
CRootSegment toCRootSegment_CStructure(void* _ref){
	CStructure _this = *((CStructure*) _ref);
	CRootSegmentData data;
	data.CStructure = _this;
	return { CStructureVariant, data };
}
char* generate_CStructure(void* _ref){
	CStructure* _this = (CStructure*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'fields' not defined in 'JObjectType[name=CStructure, members=magma.Main$JavaList@75a1cd57]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ joinedFields = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'fields' not defined in 'JObjectType[name=CStructure, members=magma.Main$JavaList@75a1cd57]']]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'fields' not defined in 'JObjectType[name=CStructure, members=magma.Main$JavaList@75a1cd57]']]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'fields' not defined in 'JObjectType[name=CStructure, members=magma.Main$JavaList@75a1cd57]']]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Member 'fields' not defined in 'JObjectType[name=CStructure, members=magma.Main$JavaList@75a1cd57]']*/(fields_CStructure(this, ), ), F? { alloc(CField), F?Table { new }}), F? { alloc(CField), F?Table { generate }}), new_Joiner());
	return lineSeparator_/*Unwrapped expression: generateTemplateString(typeParameters_CStructure(this, )) + "struct " + name_CStructure(this, ) + " {" + joinedFields + lineSeparator_startUndefined identifier: Systemend(System, ) + "};" + System*/(generateTemplateString(typeParameters_CStructure(this, )) + "struct " + name_CStructure(this, ) + " {" + joinedFields + lineSeparator_/*Undefined identifier: System*/(System, ) + "};" + System, );
}
CRootSegment toCRootSegment_CEnum(void* _ref){
	CEnum _this = *((CEnum*) _ref);
	CRootSegmentData data;
	data.CEnum = _this;
	return { CEnumVariant, data };
}
/*?*/ lambda14(void* _ref, /*?*/ variant){
	return variant + "Variant";
}
/*?*/ lambda15(void* _ref, /*?*/ variant){
	return generateIndent(1) + variant;
}
char* generate_CEnum(void* _ref){
	CEnum* _this = (CEnum*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'variants' not defined in 'JObjectType[name=CEnum, members=magma.Main$JavaList@3d012ddd]']]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ enumFields = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'variants' not defined in 'JObjectType[name=CEnum, members=magma.Main$JavaList@3d012ddd]']]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'variants' not defined in 'JObjectType[name=CEnum, members=magma.Main$JavaList@3d012ddd]']]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'variants' not defined in 'JObjectType[name=CEnum, members=magma.Main$JavaList@3d012ddd]']]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Member 'variants' not defined in 'JObjectType[name=CEnum, members=magma.Main$JavaList@3d012ddd]']*/(variants_CEnum(this, ), ), lambda14), lambda15), new_Joiner(","));
	return lineSeparator_/*Unwrapped expression: "enum " + name_CEnum(this, ) + "Variant {" + enumFields + lineSeparator_startUndefined identifier: Systemend(System, ) + "};" + System*/("enum " + name_CEnum(this, ) + "Variant {" + enumFields + lineSeparator_/*Undefined identifier: System*/(System, ) + "};" + System, );
}
CRootSegment toCRootSegment_JUnion(void* _ref){
	JUnion _this = *((JUnion*) _ref);
	CRootSegmentData data;
	data.JUnion = _this;
	return { JUnionVariant, data };
}
char* generate_JUnion(void* _ref){
	JUnion* _this = (JUnion*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'members' not defined in 'JObjectType[name=JUnion, members=magma.Main$JavaList@6f2b958e]']]', not an object.]]', not an object.]]', not an object.]*/ unionFields = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'members' not defined in 'JObjectType[name=JUnion, members=magma.Main$JavaList@6f2b958e]']]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'members' not defined in 'JObjectType[name=JUnion, members=magma.Main$JavaList@6f2b958e]']]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Member 'members' not defined in 'JObjectType[name=JUnion, members=magma.Main$JavaList@6f2b958e]']*/(members_JUnion(this, ), ), F? { alloc(Main), F?Table { generateStatement }}), new_Joiner());
	return lineSeparator_/*Unwrapped expression: generateTemplateString(typeParameters_JUnion(this, )) + "union " + name_JUnion(this, ) + "Data {" + unionFields + lineSeparator_startUndefined identifier: Systemend(System, ) + "};" + System*/(generateTemplateString(typeParameters_JUnion(this, )) + "union " + name_JUnion(this, ) + "Data {" + unionFields + lineSeparator_/*Undefined identifier: System*/(System, ) + "};" + System, );
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
	return toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'implementees' not defined in 'JObjectType[name=JObjectPrototype, members=magma.Main$JavaList@1eb44e46]']]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'implementees' not defined in 'JObjectType[name=JObjectPrototype, members=magma.Main$JavaList@1eb44e46]']]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Member 'implementees' not defined in 'JObjectType[name=JObjectPrototype, members=magma.Main$JavaList@1eb44e46]']*/(implementees_JObjectPrototype(this, ), ), F? { alloc(this), F?Table { createConversionType }}), );
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
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'parameters' not defined in 'JObjectType[name=CFunctionHeader, members=magma.Main$JavaList@6504e3b2]']]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'parameters' not defined in 'JObjectType[name=CFunctionHeader, members=magma.Main$JavaList@6504e3b2]']]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'parameters' not defined in 'JObjectType[name=CFunctionHeader, members=magma.Main$JavaList@6504e3b2]']]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Member 'parameters' not defined in 'JObjectType[name=CFunctionHeader, members=magma.Main$JavaList@6504e3b2]']*/(parameters_CFunctionHeader(this, ), ), F? { alloc(CDeclaration), F?Table { generate }}), new_Joiner(", "));
	return generate_/*Not a functional type: Placeholder[input=Member 'definition' not defined in 'JObjectType[name=CFunctionHeader, members=magma.Main$JavaList@6504e3b2]']*/(definition_CFunctionHeader(this, ), ) + "(" + compiledParameters + ")";
}
char* generate_CFunction(void* _ref){
	CFunction* _this = (CFunction*) _ref;
	return lineSeparator_/*Unwrapped expression: generate_startNot a functional type: Placeholder[input=Member 'header' not defined in 'JObjectType[name=CFunction, members=magma.Main$JavaList@515f550a]']end(header_CFunction(this, ), ) + "{" + content_CFunction(this, ) + lineSeparator_startUndefined identifier: Systemend(System, ) + "}" + System*/(generate_/*Not a functional type: Placeholder[input=Member 'header' not defined in 'JObjectType[name=CFunction, members=magma.Main$JavaList@515f550a]']*/(header_CFunction(this, ), ) + "{" + content_CFunction(this, ) + lineSeparator_/*Undefined identifier: System*/(System, ) + "}" + System, );
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
	_this->rootSegments = empty_/*Undefined identifier: Lists*/(Lists, );
	_this->functionDeclarations = empty_/*Undefined identifier: Lists*/(Lists, );
	_this->functions = empty_/*Undefined identifier: Lists*/(Lists, );
	_this->globals = empty_/*Undefined identifier: Lists*/(Lists, );
	_this->counter = 0;
	return _this;
}
/*?*/ lambda16(void* _ref, /*?*/ typeParam){
	return "typename " + typeParam;
}
char* generateTemplateString_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* templateString;
	if (isEmpty_List(typeParameters, )) 
		templateString = "";
	else {
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@626b2d4a]', not an object.]]', not an object.]]', not an object.]*/ typeNames = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@626b2d4a]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@626b2d4a]', not an object.]*/(iter_List(typeParameters, ), lambda16), new_Joiner(", "));
		templateString = lineSeparator_/*Unwrapped expression: "template <" + typeNames + ">" + System*/("template <" + typeNames + ">" + System, );
	}
	return templateString;
}
char* wrap_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'replace' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'replace' not defined in 'magma.Main$JRecursiveType@5e91993f']]', not an object.]*/ replaced = replace_/*Not a functional type: Placeholder[input=Member 'replace' not defined in 'magma.Main$JRecursiveType@5e91993f']*/(replace_char_ptr(input, "/*", "start"), "*/", "end");
	return "/*" + replaced + "*/";
}
void main_Main(void* _ref, char** args){
	Main* _this = (Main*) _ref;
	if (run_Main(new_Main(), ).variant = ?.SomeVariant) 
		println_/*Cannot access member 'err' in 'Placeholder[input=Undefined identifier: System]', not an object.*/(System.err, display_/*Undefined identifier: value*/(value, ));
}
char* generateStatement_Main(void* _ref, int depth, char* content){
	Main* _this = (Main*) _ref;
	return generateIndent(depth) + content + ";";
}
char* generateIndent_Main(void* _ref, int depth){
	Main* _this = (Main*) _ref;
	return repeat_/*Unwrapped expression: lineSeparator_startUndefined identifier: Systemend(System, ) + "\t"*/(lineSeparator_/*Undefined identifier: System*/(System, ) + "\t", depth);
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
	return generateStatement(1, content);
}
char* joinTypeParameters_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* joinedTypeParameters;
	if (isEmpty_List(typeParameters, )) 
		joinedTypeParameters = "";
	else joinedTypeParameters = " < " + collect_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@379619aa]', not an object.]*/(iter_List(typeParameters, ), new_Joiner(", ")) + ">";
	return joinedTypeParameters;
}
CExpression transformCaller_Main(void* _ref, JCaller jCaller){
	Main* _this = (Main*) _ref;
	return _switch;
}
CExpression transformExpression_Main(void* _ref, JExpression expression){
	Main* _this = (Main*) _ref;
	return _switch;
}
CInvocation transformInvocation_Main(void* _ref, JInvokable jInvokable){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ arguments = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]*/(arguments_JInvokable(jInvokable, ), ), F? { alloc(this), F?Table { transformExpression }}), );
	/*Not a functional type: Placeholder[input=Cannot access member 'caller' in 'Identifier[value=JInvokable]', not an object.]*/ caller = caller_JInvokable(jInvokable, );
	if (caller.variant = ?.JMemberAccess(var instance, var memberName)Variant) {
		JType jType = resolveExpression_Main(this, instance);
		/*Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ newArguments = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(arguments, transformCaller_Main(this, instance));
		/*Not a functional type: Placeholder[input=Cannot access member 'toBaseName' in 'Identifier[value=CType]', not an object.]*/ baseName = toBaseName_CType(transformType(jType), );
		return new_CInvocation(new_Identifier(memberName + "_" + baseName), newArguments);
	}
	return new_CInvocation(transformCaller_Main(this, caller), arguments);
}
Option<IOError> run_Main(void* _ref){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]*/ source = get_/*Undefined identifier: Paths*/(Paths, ".", "src", "main", "java", "magma", "Main.java");
	/*Not a functional type: Placeholder[input=Cannot access member 'resolveSibling' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]]', not an object.]*/ target = resolveSibling_/*Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]*/(source, "Main.cpp");
	/*Not a functional type: Placeholder[input=Cannot access member 'mapValue' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'readString' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]]', not an object.]]', not an object.]*/ input = mapValue_/*Not a functional type: Placeholder[input=Cannot access member 'readString' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]]', not an object.]*/(readString_/*Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Undefined identifier: Paths]', not an object.]*/(source, ), F? { alloc(this), F?Table { compile }});
	return _switch;
}
char* compile_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* all = compileStatements_Main(this, input, F? { alloc(this), F?Table { compileRootSegment }});
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@73a28541]', not an object.]]', not an object.]]', not an object.]*/ joinedStructures = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@73a28541]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@73a28541]', not an object.]*/(iter_List(_this->rootSegments, ), F? { alloc(CRootSegment), F?Table { generate }}), new_Joiner());
	char* joinedGlobals = joinStrings_Main(this, _this->globals);
	char* joinedFunctionDeclarations = joinStrings_Main(this, _this->functionDeclarations);
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@6f75e721]', not an object.]]', not an object.]]', not an object.]*/ joinedFunctions = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@6f75e721]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@6f75e721]', not an object.]*/(iter_List(_this->functions, ), F? { alloc(CFunction), F?Table { generate }}), new_Joiner());
	return joinedStructures + joinedGlobals + joinedFunctionDeclarations + joinedFunctions + all;
}
char* joinStrings_Main(void* _ref, List<char*> structures){
	Main* _this = (Main*) _ref;
	return collect_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@69222c14]', not an object.]*/(iter_List(structures, ), new_Joiner(""));
}
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper){
	Main* _this = (Main*) _ref;
	return compileAll_Main(this, input, mapper, new_EscapedFolder(F? { alloc(this), F?Table { foldStatement }}));
}
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder){
	Main* _this = (Main*) _ref;
	return collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]*/(map_Iter(divide_Main(this, input, folder), mapper), new_Joiner(""));
}
Iter<char*> divide_Main(void* _ref, char* input, Folder folder){
	Main* _this = (Main*) _ref;
	State current = new_State(input);
	while (true) {
		/*Not a functional type: Placeholder[input=Cannot access member 'pop' in 'Identifier[value=State]', not an object.]*/ maybeNext = pop_State(current, );
		if (!(maybeNext.variant = ?.SomeVariant)) 
			break;
		char next;
		next = value;
		current = apply_Folder(folder, current, next);
	}
	return stream_/*Not a functional type: Placeholder[input=Cannot access member 'advance' in 'Identifier[value=State]', not an object.]*/(advance_State(current, ), );
}
State foldStatement_Main(void* _ref, State current, char next){
	Main* _this = (Main*) _ref;
	if (isLevel_/*Unwrapped expression: next == '/' && current*/(next == '/' && current, )) {
		/*Not a functional type: Placeholder[input=Cannot access member 'peek' in 'Identifier[value=State]', not an object.]*/ maybePeeked = peek_State(current, );
		if (maybePeeked.variant = ?.SomeVariant) {
			/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/ withoutLineCommentPrefix = orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(append_State(current, '/'), ), current);
			while (true) {
				/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ maybeTuple = popAndAppendToTuple_/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/(withoutLineCommentPrefix, );
				if (maybeTuple.variant = ?.SomeVariant) {
					withoutLineCommentPrefix = tuple.left;
					/*Cannot access member 'right' in 'Placeholder[input=Undefined identifier: tuple]', not an object.*/ right = tuple.right;
					if (right == '\r' || right == '\n') 
						withoutLineCommentPrefix = advance_/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/(withoutLineCommentPrefix, );
				}
				return withoutLineCommentPrefix;
			}
		}
	}
	/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = append_State(current, next);
	if (isLevel_/*Unwrapped expression: next == ';' && appended*/(next == ';' && appended, )) 
		return advance_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, );
	if (isShallow_/*Unwrapped expression: next == '}' && appended*/(next == '}' && appended, )) {
		State appended1;
		if (peek_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, ).variant = ?.SomeVariant) 
			appended1 = orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, ), appended);
		else appended1 = appended;
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'advance' in 'Identifier[value=else]', not an object.]*/(advance_else(appended1, ), );
	}
	if (next == '{' || next == '(') 
		return enter_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, );
	if (next == '}' || next == ')') 
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended, );
	return appended;
}
/*?*/ lambda17(void* _ref){
	return wrap(stripped);
}
char* compileRootSegment_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_char_ptr(input, );
	if (isEmpty_char_ptr(stripped, )) 
		return "";
	if (startsWith_/*Unwrapped expression: startsWith_char_ptr(stripped, "package ") || stripped*/(startsWith_char_ptr(stripped, "package ") || stripped, "import ")) 
		return "";
	return orElseGet_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@782830e]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@782830e]', not an object.]*/(flatMap_Option(partiallyParseObject_Main(this, "class", stripped), F? { alloc(this), F?Table { transformObject }}), F? { alloc(CStructMember), F?Table { generate }}), lambda17);
}
/*?*/ lambda18(void* _ref, /*?*/ slice){
	return isEmpty_/*Unwrapped expression: !slice*/(!slice, );
}
/*?*/ lambda19(void* _ref, /*?*/ input){
	return transformType(parseType_Main(this, input));
}
/*?*/ lambda20(void* _ref, /*?*/ slice){
	return isEmpty_/*Unwrapped expression: !slice*/(!slice, );
}
Option<JObjectPrototype> partiallyParseObject_Main(void* _ref, char* type, char* stripped){
	Main* _this = (Main*) _ref;
	int i = indexOf_char_ptr(stripped, type + " ");
	if (i < 0) 
		return new_None<JObjectPrototype>();
	char* beforeType = strip_char_ptr(substring_char_ptr(stripped, 0, i), );
	char* modifiers;
	List<char*> annotations = empty_/*Undefined identifier: Lists*/(Lists, );
	int i5 = lastIndexOf_char_ptr(beforeType, "\n");
	if (i5 >= 0) {
		char* substring = substring_char_ptr(beforeType, 0, i5);
		char* substring1 = substring_char_ptr(beforeType, i5 + 1);
		annotations = collectAnnotations_Main(this, substring);
		modifiers = substring1;
	}
	else modifiers = beforeType;
	char* afterKeyword = strip_char_ptr(substring_char_ptr(stripped, length_/*Unwrapped expression: i + (type + " ")*/(i + (type + " "), )), );
	int i1 = indexOf_char_ptr(afterKeyword, "{");
	if (i1 < 0) 
		return new_None<JObjectPrototype>();
	char* beforeContent = strip_char_ptr(substring_char_ptr(afterKeyword, 0, i1), );
	char* withEnd = strip_char_ptr(substring_char_ptr(afterKeyword, i1 + 1), );
	if (endsWith_/*Unwrapped expression: !withEnd*/(!withEnd, "}")) 
		return new_None<JObjectPrototype>();
	char* inputContent = substring_char_ptr(withEnd, 0, length_char_ptr(withEnd, ) - 1);
	List<char*> variants = empty_/*Undefined identifier: Lists*/(Lists, );
	int i2 = indexOf_char_ptr(beforeContent, "permits ");
	if (i2 >= 0) {
		char* substring1 = substring_char_ptr(beforeContent, length_/*Unwrapped expression: i2 + "permits "*/(i2 + "permits ", ));
		beforeContent = substring_char_ptr(beforeContent, 0, i2);
		variants = splitValues_Main(this, substring1);
	}
	List<CType> implementees = empty_/*Undefined identifier: Lists*/(Lists, );
	int i4 = indexOf_char_ptr(beforeContent, "implements ");
	if (i4 >= 0) {
		char* implementeesString = substring_char_ptr(beforeContent, length_/*Unwrapped expression: i4 + "implements "*/(i4 + "implements ", ));
		beforeContent = strip_char_ptr(substring_char_ptr(beforeContent, 0, i4), );
		implementees = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]*/(map_Iter(divide_Main(this, implementeesString, new_ValueFolder()), F? { alloc(String), F?Table { strip }}), lambda18), lambda19), );
	}
	List<JDeclaration> recordFields = empty_/*Undefined identifier: Lists*/(Lists, );
	if (endsWith_char_ptr(beforeContent, ")")) {
		char* substring = substring_char_ptr(beforeContent, 0, length_char_ptr(beforeContent, ) - 1);
		int i3 = indexOf_char_ptr(substring, "(");
		if (i3 >= 0) {
			beforeContent = substring_char_ptr(substring, 0, i3);
			recordFields = toList_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]*/(map_Iter(divide_Main(this, substring_char_ptr(substring, i3 + 1), new_ValueFolder()), F? { alloc(this), F?Table { parseDeclaration }}), F? { alloc(Option), F?Table { iter }}), );
		}
	}
	List<char*> typeParameters = empty_/*Undefined identifier: Lists*/(Lists, );
	int i3 = indexOf_char_ptr(beforeContent, " < ");
	if (i3 >= 0) {
		char* substring1 = strip_char_ptr(substring_char_ptr(beforeContent, i3 + 1), );
		if (endsWith_char_ptr(substring1, ">")) {
			beforeContent = substring_char_ptr(beforeContent, 0, i3);
			char* substring = substring_char_ptr(substring1, 0, length_char_ptr(substring1, ) - 1);
			typeParameters = splitValues_Main(this, substring);
		}
	}
	if (isIdentifier_/*Unwrapped expression: !this*/(!this, beforeContent)) 
		return new_None<JObjectPrototype>();
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'fromObjArray' in 'Placeholder[input=Undefined identifier: Streams]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ modifiersList = toList_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'fromObjArray' in 'Placeholder[input=Undefined identifier: Streams]', not an object.]]', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'fromObjArray' in 'Placeholder[input=Undefined identifier: Streams]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'fromObjArray' in 'Placeholder[input=Undefined identifier: Streams]', not an object.]*/(fromObjArray_/*Undefined identifier: Streams*/(Streams, split_else(modifiers, quote_/*Undefined identifier: Pattern*/(Pattern, " "))), F? { alloc(String), F?Table { strip }}), lambda20), );
	char* name = strip_char_ptr(beforeContent, );
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
	return new_Some<JObjectPrototype>(prototype);
}
/*?*/ lambda21(void* _ref, /*?*/ env){
	return /*{
			final var withName = env.withName(object.name());

			// Note that withName is not used by members here*/;
}
Option<CStructMember> transformObject_Main(void* _ref, JObjectPrototype object){
	Main* _this = (Main*) _ref;
	if (contains_/*Cannot access member 'annotations' in 'Identifier[value=JObjectPrototype]', not an object.*/(object.annotations, "Actual")) 
		return new_Some<CStructMember>(new_EmptyStructMember());
	List<CRootSegment> dependencies = new_JavaList<CRootSegment>();
	_this->functions = fold_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'createConversionFunctions' in 'Identifier[value=JObjectPrototype]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'createConversionFunctions' in 'Identifier[value=JObjectPrototype]', not an object.]*/(createConversionFunctions_JObjectPrototype(object, ), ), _this->functions, F? { alloc(List), F?Table { addLast }});
	/*Not a functional type: Placeholder[input=Cannot access member 'collectCFields' in 'Identifier[value=JObjectPrototype]', not an object.]*/ fields = collectCFields_JObjectPrototype(object, );
	/*Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@470e2030]']', not an object.]*/ within = withinScoped_/*Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@470e2030]'*/(_this->environment, lambda21);
	_this->environment = within.left;
	/*Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@470e2030]']', not an object.]]', not an object.*/ members = within.right;
	if (equals_/*Not a functional type: Placeholder[input=Cannot access member 'type' in 'Identifier[value=JObjectPrototype]', not an object.]*/(type_JObjectPrototype(object, ), "interface")) 
		if (contains_/*Not a functional type: Placeholder[input=Cannot access member 'modifiersList' in 'Identifier[value=JObjectPrototype]', not an object.]*/(modifiersList_JObjectPrototype(object, ), "sealed")) {
			List<CRootSegment> elements = flattenSealedStructure_Main(this, name_JObjectPrototype(object, ), typeParameters_JObjectPrototype(object, ), variants_JObjectPrototype(object, ));
			fields = addLast_/*Not a functional type: Placeholder[input=Cannot access member 'addLast' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'collectCFields' in 'Identifier[value=JObjectPrototype]', not an object.]]', not an object.]*/(addLast_/*Not a functional type: Placeholder[input=Cannot access member 'collectCFields' in 'Identifier[value=JObjectPrototype]', not an object.]*/(fields, new_CDeclaration(new_Identifier(name_JObjectPrototype(object, ) + "Variant"), "variant")), new_CDeclaration(new_Identifier(joinTypeParameters_/*Unwrapped expression: name_JObjectPrototype(object, ) + "Data" + Main*/(name_JObjectPrototype(object, ) + "Data" + Main, typeParameters_JObjectPrototype(object, ))), "data"));
			dependencies = addAllLast_List(dependencies, elements);
		}
	else {
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@470e2030]']', not an object.]]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ list = toList_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@470e2030]']', not an object.]]', not an object.]', not an object.]]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@470e2030]']', not an object.]]', not an object.]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@470e2030]']', not an object.]]', not an object.]', not an object.]*/(iter_/*Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@470e2030]']', not an object.]]', not an object.*/(members, ), F? { alloc(this), F?Table { retainDefinables }}), F? { alloc(Option), F?Table { iter }}), );
		CStructure cStructure = new_CStructure(typeParameters_JObjectPrototype(object, ), name_JObjectPrototype(object, ) + "Table", list);
		dependencies = addLast_List(dependencies, cStructure);
		fields = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'addLast' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'collectCFields' in 'Identifier[value=JObjectPrototype]', not an object.]]', not an object.]*/(addLast_/*Not a functional type: Placeholder[input=Cannot access member 'collectCFields' in 'Identifier[value=JObjectPrototype]', not an object.]*/(fields, new_CDeclaration(new_Identifier(joinTypeParameters_/*Unwrapped expression: name_JObjectPrototype(object, ) + "Table" + Main*/(name_JObjectPrototype(object, ) + "Table" + Main, typeParameters_JObjectPrototype(object, ))), "table")), new_CDeclaration(new_CPointerType(CPrimitiveType.Void), "data"));
	}
	else fields = addAllLast_/*Not a functional type: Placeholder[input=Cannot access member 'collectCFields' in 'Identifier[value=JObjectPrototype]', not an object.]*/(fields, retainFields_Main(this, members));
	/*Not a functional type: Placeholder[input=Cannot access member 'addLast' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@3fb4f649]', not an object.]*/ elements = addLast_List(dependencies, new_CStructure(typeParameters_JObjectPrototype(object, ), name_JObjectPrototype(object, ), fields));
	_this->rootSegments = addAllLast_List(_this->rootSegments, elements);
	return new_Some<CStructMember>(new_EmptyStructMember());
}
Option<JDeclaration> extractMethodDeclaration_Main(void* _ref, JObjectMemberPrototype prototype){
	Main* _this = (Main*) _ref;
	if (prototype.variant = ?.JMethodPrototype methodPrototypeVariant) {
		/*Cannot access member 'methodDeclaration' in 'Placeholder[input=Undefined identifier: methodPrototype]', not an object.*/ methodDeclaration = methodPrototype.methodDeclaration;
		if (methodDeclaration.variant = ?.JDeclaration declarationVariant) {
			/*Cannot access member 'type' in 'Placeholder[input=Undefined identifier: declaration]', not an object.*/ returnType = declaration.type;
			/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'parameters' in 'Placeholder[input=Undefined identifier: methodPrototype]', not an object.]', not an object.]]', not an object.]]', not an object.]*/ paramTypes = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'parameters' in 'Placeholder[input=Undefined identifier: methodPrototype]', not an object.]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'parameters' in 'Placeholder[input=Undefined identifier: methodPrototype]', not an object.]', not an object.]*/(iter_/*Cannot access member 'parameters' in 'Placeholder[input=Undefined identifier: methodPrototype]', not an object.*/(methodPrototype.parameters, ), F? { alloc(JDeclaration), F?Table { type }}), );
			JFunctionalType functionalType = new_JFunctionalType(paramTypes, returnType);
			return new_Some<JDeclaration>(new_JDeclaration(declaration.name, functionalType));
		}
	}
	return new_None<JDeclaration>();
}
Option<CStructMember> completeObjectMemberPrototype_Main(void* _ref, JObjectPrototype object, JObjectMemberPrototype wrapper){
	Main* _this = (Main*) _ref;
	return _switch;
}
/*?*/ lambda22(void* _ref, /*?*/ name){
	return name + "_" + object.name;
}
/*?*/ lambda23(void* _ref){
	return new_Some<char*>(compileMethodsSegments_Main(this, inputContent, 1));
}
/*?*/ lambda24(void* _ref, /*?*/ env){
	return within_/*Not a functional type: Placeholder[input=Cannot access member 'defineAll' in 'Placeholder[input=Undefined identifier: env]', not an object.]*/(defineAll_/*Undefined identifier: env*/(env, parameters_JMethodPrototype(jFunctionProto, )), lambda23);
}
CStructMember completeMethodProto_Main(void* _ref, JMethodPrototype jFunctionProto, JObjectPrototype object){
	Main* _this = (Main*) _ref;
	Option<char*> maybeCompiled = new_None<char*>();
	if (methodDeclaration_JMethodPrototype(jFunctionProto, ).variant = ?.JDeclaration declaration &&
				declaration.annotations.contains("Actual")Variant) {
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ cParameters = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]*/(parameters_JMethodPrototype(jFunctionProto, ), ), F? { alloc(JDeclaration), F?Table { toCDeclaration }}), );
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(cParameters, ), F? { alloc(CDeclaration), F?Table { generate }}), new_Joiner(", "));
		/*Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]]', not an object.]*/ modifiedMethodDeclaration = toCDeclaration_/*Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]*/(mapName_/*Undefined identifier: declaration*/(declaration, lambda22), );
		_this->functionDeclarations = addLast_List(_this->functionDeclarations, lineSeparator_/*Unwrapped expression: generate_startNot a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]]', not an object.]end(modifiedMethodDeclaration, ) + "(" + compiledParameters + ");" + System*/(generate_/*Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]]', not an object.]*/(modifiedMethodDeclaration, ) + "(" + compiledParameters + ");" + System, ));
		return new_EmptyStructMember();
	}
	if (endsWith_/*Cannot access member 'content' in 'Placeholder[input=Unwrapped expression: startsWith_startCannot access member 'content' in 'Identifier[value=JMethodPrototype]', not an object.end(jFunctionProto.content, "{") && jFunctionProto]', not an object.*/(startsWith_/*Cannot access member 'content' in 'Identifier[value=JMethodPrototype]', not an object.*/(jFunctionProto.content, "{") && jFunctionProto.content, "}")) {
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Cannot access member 'content' in 'Identifier[value=JMethodPrototype]', not an object.]', not an object.]*/ inputContent = substring_/*Cannot access member 'content' in 'Identifier[value=JMethodPrototype]', not an object.*/(jFunctionProto.content, 1, length_/*Not a functional type: Placeholder[input=Cannot access member 'content' in 'Identifier[value=JMethodPrototype]', not an object.]*/(content_JMethodPrototype(jFunctionProto, ), ) - 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'withinScoped' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@470e2030]']', not an object.]*/ within = withinScoped_/*Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@470e2030]'*/(_this->environment, lambda24);
		_this->environment = within.left;
		maybeCompiled = within.right;
	}
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ cParameters = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]*/(parameters_JMethodPrototype(jFunctionProto, ), ), F? { alloc(JDeclaration), F?Table { toCDeclaration }}), );
	if (methodDeclaration_JMethodPrototype(jFunctionProto, ).variant = ?.JDeclarationVariant) 
		cParameters = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(cParameters, new_CDeclaration(new_CPointerType(CPrimitiveType.Void), "_ref"));
	char* outputContent = computeMethodBody_Main(this, typeParameters_JMethodPrototype(jFunctionProto, ), methodDeclaration_JMethodPrototype(jFunctionProto, ), cParameters, maybeCompiled, object.name, object.variants);
	CFunctionDeclaration mapped = transformMethodDeclaration_Main(this, object.name, typeParameters_JMethodPrototype(jFunctionProto, ), methodDeclaration_JMethodPrototype(jFunctionProto, ));
	CFunctionHeader header = new_CFunctionHeader(mapped, cParameters);
	CFunction cFunction = new_CFunction(header, outputContent);
	_this->functionDeclarations = addLast_List(_this->functionDeclarations, lineSeparator_/*Unwrapped expression: generate_CFunctionHeader(header, ) + ";" + System*/(generate_CFunctionHeader(header, ) + ";" + System, ));
	_this->functions = addLast_List(_this->functions, cFunction);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ parameterTypes = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethodPrototype]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(cParameters, ), F? { alloc(CDeclaration), F?Table { type }}), );
	return _switch;
}
Option<JObjectMemberPrototype> partiallyParseObjectMember_Main(void* _ref, JObjectPrototype object, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_char_ptr(input, );
	if (isEmpty_char_ptr(stripped, )) 
		return new_None<JObjectMemberPrototype>();
	Option<JObjectPrototype> maybeEnum = partiallyParseObject_Main(this, "enum", input);
	if (maybeEnum.variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>(enum0);
	Option<JObjectPrototype> maybeInterface = partiallyParseObject_Main(this, "interface", input);
	if (maybeInterface.variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>(interface0);
	Option<JObjectPrototype> maybeRecord = partiallyParseObject_Main(this, "record", input);
	if (maybeRecord.variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>(record0);
	Option<JObjectPrototype> maybeClass = partiallyParseObject_Main(this, "class", input);
	if (maybeClass.variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>(class0);
	Option<JObjectMemberPrototype> maybeEnumValues = compileEnumValues_Main(this, input, name_JObjectPrototype(object, ));
	if (maybeEnumValues.variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>(enumValues);
	if (endsWith_char_ptr(stripped, ";")) {
		char* substring = substring_char_ptr(stripped, 0, length_char_ptr(stripped, ) - 1);
		Option<JDeclaration> maybeDeclaration = parseDeclaration_Main(this, substring);
		if (maybeDeclaration.variant = ?.SomeVariant) {
			_this->environment = define_/*Member 'environment' not defined in 'JObjectType[name=Main, members=magma.Main$JavaList@470e2030]'*/(_this->environment, declaration);
			return new_Some<JObjectMemberPrototype>(new_JField(declaration));
		}
	}
	Option<JObjectMemberPrototype> maybeMethod = parseMethod_Main(this, object, stripped);
	if (maybeMethod.variant = ?.SomeVariant) 
		return new_Some<JObjectMemberPrototype>(temp);
	return new_Some<JObjectMemberPrototype>(new_Placeholder(stripped));
}
/*?*/ lambda25(void* _ref, /*?*/ slice){
	return isEmpty_/*Unwrapped expression: !slice*/(!slice, );
}
Option<JObjectMemberPrototype> parseMethod_Main(void* _ref, JObjectPrototype object, char* stripped){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'name' in 'Identifier[value=JObjectPrototype]', not an object.]*/ structName = name_JObjectPrototype(object, );
	int i = indexOf_char_ptr(stripped, "(");
	if (i < 0) 
		return new_None<JObjectMemberPrototype>();
	char* declarationString = substring_char_ptr(stripped, 0, i);
	char* substring1 = substring_char_ptr(stripped, i + 1);
	int i1 = indexOf_char_ptr(substring1, ")");
	if (i1 < 0) 
		return new_None<JObjectMemberPrototype>();
	char* parametersString = substring_char_ptr(substring1, 0, i1);
	char* withBraces = strip_char_ptr(substring_char_ptr(substring1, i1 + 1), );
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ parameters = toList_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]*/(toList_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]*/(map_Iter(divide_Main(this, parametersString, new_ValueFolder()), F? { alloc(String), F?Table { strip }}), lambda25), ), ), F? { alloc(this), F?Table { parseDeclaration }}), F? { alloc(Option), F?Table { iter }}), );
	JMethodDeclaration declaration = parseMethodDeclaration_Main(this, declarationString, structName);
	JMethodPrototype proto = new_JMethodPrototype(typeParameters_JObjectPrototype(object, ), parameters, declaration, withBraces);
	return new_Some<JObjectMemberPrototype>(proto);
}
/*?*/ lambda26(void* _ref, /*?*/ member){
	return !(member.variant = ?.CFunctionDeclarationVariant);
}
List<CDefinable> retainFields_Main(void* _ref, List<CStructMember> members){
	Main* _this = (Main*) _ref;
	return toList_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@33833882]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@33833882]', not an object.]]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@33833882]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@33833882]', not an object.]*/(iter_List(members, ), F? { alloc(this), F?Table { retainDefinables }}), F? { alloc(Option), F?Table { iter }}), lambda26), );
}
/*?*/ lambda27(void* _ref, /*?*/ variant){
	return variant + joinedTypeParameters + " " + variant;
}
List<CRootSegment> flattenSealedStructure_Main(void* _ref, char* name, List<char*> typeParameters, List<char*> variants){
	Main* _this = (Main*) _ref;
	CEnum jEnum = new_CEnum(name, variants);
	/*Not a functional type: Placeholder[input=Cannot access member 'joinTypeParameters' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ joinedTypeParameters = joinTypeParameters_/*Undefined identifier: Main*/(Main, typeParameters);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@200a570f]', not an object.]]', not an object.]]', not an object.]*/ unionMembers = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@200a570f]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@200a570f]', not an object.]*/(iter_List(variants, ), lambda27), );
	JUnion union = new_JUnion(typeParameters, name, unionMembers);
	return of_/*Undefined identifier: Lists*/(Lists, jEnum, union);
}
Option<CDefinable> retainDefinables_Main(void* _ref, CStructMember member){
	Main* _this = (Main*) _ref;
	if (member.variant = ?.CDefinable definableVariant) 
		return new_Some<CDefinable>(definable);
	/*else return new None<CDefinable>()*/;
}
/*?*/ lambda28(void* _ref, /*?*/ slice){
	return isEmpty_/*Unwrapped expression: !slice*/(!slice, );
}
List<char*> splitValues_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'split' not defined in 'magma.Main$JRecursiveType@5e91993f']*/ segments = split_char_ptr(input, quote_/*Undefined identifier: Pattern*/(Pattern, ","));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ list = toList_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]*/(stream_/*Undefined identifier: Arrays*/(Arrays, segments), F? { alloc(String), F?Table { strip }}), lambda28), );
	return new_JavaList<char*>(list);
}
/*?*/ lambda29(void* _ref, /*?*/ i){
	char c = charAt_char_ptr(stripped, i);
	return isLetter_/*Undefined identifier: Character*/(Character, c) || (isDigit_/*Unwrapped expression: i != 0 && Character*/(i != 0 && Character, c));
}
int isIdentifier_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_char_ptr(input, );
	return allMatch_/*Not a functional type: Placeholder[input=Cannot access member 'range' in 'Placeholder[input=Undefined identifier: IntStream]', not an object.]*/(range_/*Undefined identifier: IntStream*/(IntStream, 0, length_char_ptr(stripped, )), lambda29);
}
/*?*/ lambda30(void* _ref, /*?*/ parameter){
	return parameter.name;
}
/*?*/ lambda31(void* _ref){
	CType type = transformType(declaration.type);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'subList' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@e2d56bf]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ list = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'subList' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@e2d56bf]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'subList' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@e2d56bf]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'subList' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@e2d56bf]', not an object.]*/(subList_List(cParameters, 1, size_List(cParameters, )), ), lambda30), );
	return createBodyForAbstractMethod_Main(this, structureVariants, type, declaration.name, list);
}
char* computeMethodBody_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration, List<CDeclaration> cParameters, Option<char*> maybeContent, char* structName, List<char*> structureVariants){
	Main* _this = (Main*) _ref;
	if (methodDeclaration.variant = ?.JConstructorVariant) {
		/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@16b3fc9e]', not an object.]*/ compiled = orElse_Option(maybeContent, "?");
		return generateStatement_/*Unwrapped expression: generateStatement_startUndefined identifier: Mainend(Main, structName + " _this") + compiled + Main*/(generateStatement_/*Undefined identifier: Main*/(Main, structName + " _this") + compiled + Main, "return " + "_this");
	}
	if (methodDeclaration.variant = ?.JDeclaration declarationVariant) {
		/*Not a functional type: Placeholder[input=Cannot access member 'joinTypeParameters' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ joinedTypeParameters = joinTypeParameters_/*Undefined identifier: Main*/(Main, typeParameters);
		/*Not a functional type: Placeholder[input=Cannot access member 'generateStatement' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ thisInitialization = generateStatement_/*Undefined identifier: Main*/(Main, structName + joinedTypeParameters + "* _this = (" + structName + joinedTypeParameters + "*) _ref");
		/*Not a functional type: Placeholder[input=Cannot access member 'orElseGet' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@16b3fc9e]', not an object.]*/ body = orElseGet_Option(maybeContent, lambda31);
		return thisInitialization + body;
	}
	return "?";
}
/*?*/ lambda32(void* _ref, /*?*/ variant){
	return generateCase_Main(this, variant, name);
}
char* createBodyForAbstractMethod_Main(void* _ref, List<char*> variants, CType type, char* name, List<char*> parameterNames){
	Main* _this = (Main*) _ref;
	if (isEmpty_List(variants, )) {
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@244038d0]', not an object.]]', not an object.]]', not an object.]*/ joinedParameters = collect_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@244038d0]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@244038d0]', not an object.]*/(addFirst_List(parameterNames, "_this->data"), ), new_Joiner(", "));
		return generateStatement_/*Undefined identifier: Main*/(Main, "return _this->table." + name + "(" + joinedParameters + ")");
	}
	else {
		/*Not a functional type: Placeholder[input=Cannot access member 'generateStatement' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ returnValueDefinition = generateStatement_/*Undefined identifier: Main*/(Main, generate_CType(type, ) + " _ret");
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@5680a178]', not an object.]]', not an object.]]', not an object.]*/ cases = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@5680a178]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'JGenericType[base=List, typeArguments=magma.Main$JavaList@5680a178]', not an object.]*/(iter_List(variants, ), lambda32), new_Joiner());
		return generateStatement_/*Unwrapped expression: returnValueDefinition + generateIndent(1) + "switch (" + "_this->variant" + ") {" + cases + generateIndent(1) + "}" + Main*/(returnValueDefinition + generateIndent(1) + "switch (" + "_this->variant" + ") {" + cases + generateIndent(1) + "}" + Main, "return _ret");
	}
}
/*?*/ lambda33(void* _ref, /*?*/ typeParameters0){
	return addAllLast_/*Undefined identifier: typeParameters0*/(typeParameters0, typeParameters);
}
/*?*/ lambda34(void* _ref, /*?*/ name){
	return name + "_" + structName;
}
CFunctionDeclaration transformMethodDeclaration_Main(void* _ref, char* structName, List<char*> typeParameters, JMethodDeclaration methodDeclaration){
	Main* _this = (Main*) _ref;
	return mapName_/*Not a functional type: Placeholder[input=Cannot access member 'mapTypeParameters' in 'Identifier[value=CFunctionDeclaration]', not an object.]*/(mapTypeParameters_CFunctionDeclaration(convertToFunctionDeclarations_Main(this, typeParameters, methodDeclaration), lambda33), lambda34);
}
CFunctionDeclaration convertToFunctionDeclarations_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration){
	Main* _this = (Main*) _ref;
	return _switch;
}
CType toConstructorReturnType_Main(void* _ref, char* base, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	if (isEmpty_char_ptr(base, )) 
		return new_Identifier(base);
	/*final var typeArguments = typeParameters.iter().<CType>map(Identifier::new).toList()*/;
	return new_CTemplateType(base, typeArguments);
}
/*?*/ lambda35(void* _ref, /*?*/ input){
	return compileMethodSegment_Main(this, input, indent);
}
char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent){
	Main* _this = (Main*) _ref;
	return compileStatements_Main(this, inputContent, lambda35);
}
char* generateCase_Main(void* _ref, char* variant, char* name){
	Main* _this = (Main*) _ref;
	return generateIndent(2) + "case " + variant + "Variant:" + generateStatement(3, "_ret = " + name + "_" + variant + "(&(_this->data." + variant + "))") + generateStatement(3, "break");
}
/*?*/ lambda36(void* _ref){
	return map_Option(parseDeclaration_Main(this, declaration), F? { alloc(this), F?Table { toInterface }});
}
/*?*/ lambda37(void* _ref){
	return new_Placeholder(declaration);
}
JMethodDeclaration parseMethodDeclaration_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	return orElseGet_/*Not a functional type: Placeholder[input=Cannot access member 'or' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@5fdef03a]', not an object.]*/(or_Option(parseConstructor_Main(this, declaration, structName), lambda36), lambda37);
}
JMethodDeclaration toInterface_Main(void* _ref, JDeclaration value){
	Main* _this = (Main*) _ref;
	return value;
}
Option<JMethodDeclaration> parseConstructor_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	char* stripped = strip_char_ptr(declaration, );
	if (equals_char_ptr(stripped, structName)) 
		return new_Some<JMethodDeclaration>(new_JConstructor(structName));
	int i = lastIndexOf_char_ptr(stripped, " ");
	if (i >= 0) {
		char* substring = strip_char_ptr(substring_char_ptr(stripped, i + 1), );
		if (equals_char_ptr(substring, structName)) 
			return new_Some<JMethodDeclaration>(new_JConstructor(structName));
	}
	return new_None<JMethodDeclaration>();
}
/*?*/ lambda38(void* _ref, /*?*/ state, /*?*/ character){
	return apply_ValueFolder(new_ValueFolder(), state, character);
}
/*?*/ lambda39(void* _ref, /*?*/ slice){
	return isEmpty_/*Unwrapped expression: !slice*/(!slice, );
}
/*?*/ lambda40(void* _ref, /*?*/ enumValue){
	return compileEnumValue_Main(this, structName, enumValue);
}
Option<JObjectMemberPrototype> compileEnumValues_Main(void* _ref, char* input, char* structName){
	Main* _this = (Main*) _ref;
	char* stripped = strip_char_ptr(input, );
	if (endsWith_/*Unwrapped expression: !stripped*/(!stripped, ";")) 
		return new_None<JObjectMemberPrototype>();
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]*/ enumValues = toList_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]*/(map_Iter(divide_Main(this, substring_char_ptr(stripped, 0, length_char_ptr(stripped, ) - 1), lambda38), F? { alloc(String), F?Table { strip }}), lambda39), );
	if (isEmpty_/*Unwrapped expression: !enumValues*/(!enumValues, )) {
		/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ optionStream = map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]*/(enumValues, ), lambda40);
		/*final var areAnyInvalid =
					(boolean) optionStream.collect(new AnyMatch<Option<CStructMember>>(option -> option instanceof None<CStructMember>))*/;
		if (areAnyInvalid) 
			return new_None<JObjectMemberPrototype>();
	}
	return new_Some<JObjectMemberPrototype>(new_EmptyStructMember());
}
Option<CStructMember> compileEnumValue_Main(void* _ref, char* structName, char* enumValue){
	Main* _this = (Main*) _ref;
	if (endsWith_char_ptr(enumValue, ")")) {
		char* substring = substring_char_ptr(enumValue, 0, length_char_ptr(enumValue, ) - 1);
		int i = indexOf_char_ptr(substring, "(");
		if (i >= 0) {
			char* name = substring_char_ptr(substring, 0, i);
			if (isIdentifier_/*Unwrapped expression: !this*/(!this, name)) 
				return new_None<CStructMember>();
			char* substring2 = substring_char_ptr(substring, i + 1);
			/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Placeholder[input=Unwrapped expression: structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" + System]', not an object.]*/ generated = lineSeparator_/*Unwrapped expression: structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" + System*/(structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" + System, );
			_this->globals = addLast_List(_this->globals, generated);
			return new_Some<CStructMember>(new_EmptyStructMember());
		}
	}
	return new_None<CStructMember>();
}
char* compileMethodSegment_Main(void* _ref, char* input, int indent){
	Main* _this = (Main*) _ref;
	char* stripped = strip_char_ptr(input, );
	if (isEmpty_char_ptr(stripped, )) 
		return "";
	Option<char*> maybeIf = compileConditional_Main(this, "if", indent, stripped);
	if (maybeIf.variant = ?.SomeVariant) 
		return result;
	Option<char*> maybeWhile = compileConditional_Main(this, "while", indent, stripped);
	if (maybeWhile.variant = ?.SomeVariant) 
		return result;
	if (endsWith_char_ptr(stripped, ";")) {
		char* substring = substring_char_ptr(stripped, 0, length_char_ptr(stripped, ) - 1);
		return generateIndent(indent) + compileMethodStatement_Main(this, substring) + ";";
	}
	if (startsWith_char_ptr(stripped, "else ")) {
		char* substring = strip_char_ptr(substring_char_ptr(stripped, length_/*Unwrapped expression: "else "*/("else ", )), );
		if (endsWith_/*Unwrapped expression: startsWith_char_ptr(substring, "{") && substring*/(startsWith_char_ptr(substring, "{") && substring, "}")) {
			char* substring1 = substring_char_ptr(substring, 1, length_char_ptr(substring, ) - 1);
			return generateIndent(indent) + "else {" + compileMethodsSegments_Main(this, substring1, indent + 1) + generateIndent(indent) + "}";
		}
		/*else return generateIndent(indent) + "else " + this.compileMethodSegment(substring, indent)*/;
	}
	if (startsWith_char_ptr(stripped, "//")) 
		return generateIndent(indent) + stripped;
	return lineSeparator_/*Undefined identifier: System*/(System, ) + "\t" + wrap(stripped);
}
/*?*/ lambda41(void* _ref, /*?*/ slice){
	return isEmpty_/*Unwrapped expression: !slice*/(!slice, );
}
Option<char*> compileConditional_Main(void* _ref, char* type, int indent, char* input){
	Main* _this = (Main*) _ref;
	if (startsWith_char_ptr(input, type)) {
		char* substring = strip_char_ptr(substring_char_ptr(input, length_char_ptr(type, )), );
		if (startsWith_char_ptr(substring, "(")) {
			char* afterConditionStart = strip_char_ptr(substring_char_ptr(substring, 1), );
			/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]*/ divisions = toList_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]*/(map_Iter(divide_Main(this, afterConditionStart, new_EscapedFolder(new_ConditionEndLocator())), F? { alloc(String), F?Table { strip }}), lambda41), );
			if (size_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]*/(divisions, ) < 2) 
				return new_None<char*>();
			/*Not a functional type: Placeholder[input=Cannot access member 'getFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ first = getFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]*/(divisions, );
			char* maybeWithBraces = joinStrings_Main(this, subList_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]*/(divisions, 1, size_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]*/(divisions, )));
			if (endsWith_/*Unwrapped expression: !first*/(!first, ")")) 
				return new_None<char*>();
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'getFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ condition = substring_/*Not a functional type: Placeholder[input=Cannot access member 'getFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(first, 0, length_/*Not a functional type: Placeholder[input=Cannot access member 'getFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(first, ) - 1);
			if (endsWith_/*Unwrapped expression: startsWith_char_ptr(maybeWithBraces, "{") && maybeWithBraces*/(startsWith_char_ptr(maybeWithBraces, "{") && maybeWithBraces, "}")) {
				char* content = substring_char_ptr(maybeWithBraces, 1, length_char_ptr(maybeWithBraces, ) - 1);
				return new_Some<char*>(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" + compileMethodsSegments_Main(this, content, indent + 1) + generateIndent(indent) + "}");
			}
			return new_Some<char*>(compileMethodSegment_/*Unwrapped expression: generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") " + this*/(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") " + this, maybeWithBraces, indent + 1));
		}
	}
	return new_None<char*>();
}
char* compileMethodStatement_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_char_ptr(input, );
	if (equals_char_ptr(stripped, "break")) 
		return "break";
	if (startsWith_char_ptr(stripped, "return ")) 
		return compileExpressionOrPlaceholder_/*Unwrapped expression: "return " + this*/("return " + this, substring_char_ptr(stripped, length_/*Unwrapped expression: "return "*/("return ", )));
	Option<char*> maybeAssignment = compileAssignment_Main(this, stripped);
	if (maybeAssignment.variant = ?.SomeVariant) 
		return assignment;
	Option<JExpression> maybeInvokable = parseInvokable_Main(this, stripped);
	if (maybeInvokable.variant = ?.Some(var value)Variant) 
		return generate_CExpression(transformExpression_Main(this, value), );
	Option<char*> instance = post_Main(this, stripped, "++");
	if (instance.variant = ?.SomeVariant) 
		return x;
	Option<char*> instance0 = post_Main(this, stripped, "--");
	if (instance0.variant = ?.SomeVariant) 
		return x;
	Option<JDeclaration> maybeDeclaration = parseDeclaration_Main(this, input);
	if (maybeDeclaration.variant = ?.SomeVariant) 
		return generate_/*Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]*/(toCDeclaration_/*Undefined identifier: declaration*/(declaration, ), );
	return wrap(stripped);
}
Option<char*> compileAssignment_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	int index = indexOf_char_ptr(stripped, "=");
	if (index >= 0) {
		char* destination = substring_char_ptr(stripped, 0, index);
		char* substring1 = substring_char_ptr(stripped, index + 1);
		JAssignable assignable = parseAssignable_Main(this, destination);
		Option<JExpression> maybeSource = parseExpression_Main(this, substring1);
		if (maybeSource.variant = ?.SomeVariant) {
			CAssignable cAssignable = transformAssignable_Main(this, assignable, source);
			return new_Some<char*>(generate_/*Not a functional type: Placeholder[input=Cannot access member 'transformExpression' in 'Placeholder[input=Unwrapped expression: generate_CAssignable(cAssignable, ) + " = " + this]', not an object.]*/(transformExpression_/*Unwrapped expression: generate_CAssignable(cAssignable, ) + " = " + this*/(generate_CAssignable(cAssignable, ) + " = " + this, source), ));
		}
	}
	return new_None<char*>();
}
CAssignable transformAssignable_Main(void* _ref, JAssignable assignable, JExpression source){
	Main* _this = (Main*) _ref;
	return _switch;
}
JType resolveType_Main(void* _ref, JExpression source, JType type){
	Main* _this = (Main*) _ref;
	if (equals_JType(type, JPrimitiveType.Var)) 
		return resolveExpression_Main(this, source);
	return type;
}
JType resolveExpression_Main(void* _ref, JExpression source){
	Main* _this = (Main*) _ref;
	return _switch;
}
/*?*/ lambda42(void* _ref){
	return new_Placeholder("Member '" + access.memberName + "' not defined in '" + instanceType + "'");
}
JType getJType_Main(void* _ref, JMemberAccess access, JObjectType type, JType instanceType){
	Main* _this = (Main*) _ref;
	return orElseGet_/*Not a functional type: Placeholder[input=Cannot access member 'resolve' in 'Identifier[value=JObjectType]', not an object.]*/(resolve_JObjectType(type, access.memberName), lambda42);
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
	if (endsWith_char_ptr(stripped, slice)) {
		char* instance = substring_char_ptr(stripped, 0, length_char_ptr(stripped, ) - 2);
		return new_Some<char*>(compileExpressionOrPlaceholder_Main(this, instance) + slice);
	}
	return new_None<char*>();
}
/*?*/ lambda43(void* _ref){
	return wrap(input);
}
char* compileExpressionOrPlaceholder_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return orElseGet_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@3b22cdd0]', not an object.]*/(map_Option(parseCExpression_Main(this, input), F? { alloc(CExpression), F?Table { generate }}), lambda43);
}
Option<CExpression> parseCExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return map_Option(parseExpression_Main(this, input), F? { alloc(this), F?Table { transformExpression }});
}
/*?*/ lambda44(void* _ref){
	return compileOperator_Main(this, stripped, " != ");
}
/*?*/ lambda45(void* _ref){
	return compileOperator_Main(this, stripped, " < ");
}
/*?*/ lambda46(void* _ref){
	return compileOperator_Main(this, stripped, " + ");
}
/*?*/ lambda47(void* _ref){
	return compileOperator_Main(this, stripped, " - ");
}
/*?*/ lambda48(void* _ref){
	return compileOperator_Main(this, stripped, " && ");
}
/*?*/ lambda49(void* _ref){
	return compileOperator_Main(this, stripped, " || ");
}
/*?*/ lambda50(void* _ref){
	return compileOperator_Main(this, stripped, " >= ");
}
Option<JExpression> parseExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_char_ptr(input, );
	if (startsWith_char_ptr(stripped, "switch ")) 
		return map_Some(new_Some<char*>("_switch"), F? { alloc(JExpressionWrapper), F?Table { new }});
	int i2 = lastIndexOf_char_ptr(stripped, "::");
	if (i2 >= 0) {
		char* substring = substring_char_ptr(stripped, 0, i2);
		char* name = strip_char_ptr(substring_char_ptr(stripped, i2 + 2), );
		if (isIdentifier_Main(this, name)) {
			char* compiled = compileExpressionOrPlaceholder_Main(this, substring);
			/*Unwrapped expression: "F?"*/ functionalInterfaceName = "F?";
			return map_Some(new_Some<char*>(functionalInterfaceName + " { alloc(" + compiled + "), " + functionalInterfaceName + "Table { " + name + " }}"), F? { alloc(JExpressionWrapper), F?Table { new }});
		}
	}
	if (endsWith_/*Unwrapped expression: startsWith_char_ptr(stripped, "'") && stripped*/(startsWith_char_ptr(stripped, "'") && stripped, "'")) 
		return map_Some(new_Some<char*>(stripped), F? { alloc(JExpressionWrapper), F?Table { new }});
	Option<char*> maybeLambda = compileLambda_Main(this, stripped);
	if (maybeLambda.variant = ?.SomeVariant) 
		return map_Option(maybeLambda, F? { alloc(JExpressionWrapper), F?Table { new }});
	int i3 = indexOf_char_ptr(stripped, ".variant = ?."Variant);
	if (i3 >= 0) {
		char* substring = substring_char_ptr(stripped, 0, i3);
		char* substring1 = strip_char_ptr(substring_char_ptr(stripped, i3 + ".variant = ?.".length()Variant), );
		/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@3b22cdd0]', not an object.]*/ maybeInstance = map_Option(parseCExpression_Main(this, substring), F? { alloc(CExpression), F?Table { generate }});
		if (maybeInstance.variant = ?.SomeVariant) {
			int i4 = indexOf_char_ptr(substring1, " < ");
			char* substring2;
			if (i4 >= 0) 
				substring2 = substring_char_ptr(substring1, 0, i4);
			else substring2 = substring1;
			return map_Some(new_Some<char*>(instance + ".variant = ?." + substring2 + "Variant"), F? { alloc(JExpressionWrapper), F?Table { new }});
		}
	}
	int i = lastIndexOf_char_ptr(stripped, ".");
	if (i >= 0) {
		char* instanceString = substring_char_ptr(stripped, 0, i);
		char* memberName = strip_char_ptr(substring_char_ptr(stripped, i + 1), );
		if (isIdentifier_Main(this, memberName)) {
			Option<JExpression> maybeInstance = parseExpression_Main(this, instanceString);
			if (maybeInstance.variant = ?.Some(var value)Variant) 
				return new_Some<JExpression>(new_JMemberAccess(value, memberName));
		}
	}
	Option<JExpression> maybeInvokable = parseInvokable_Main(this, stripped);
	if (maybeInvokable.variant = ?.SomeVariant) 
		return maybeInvokable;
	/*Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@1e81f4dc]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ maybeOperator = or_/*Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@1e81f4dc]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(or_/*Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@1e81f4dc]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(or_/*Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@1e81f4dc]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(or_/*Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@1e81f4dc]', not an object.]]', not an object.]]', not an object.]*/(or_/*Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@1e81f4dc]', not an object.]]', not an object.]*/(or_/*Not a functional type: Placeholder[input=Cannot access member 'or' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@1e81f4dc]', not an object.]*/(or_Option(compileOperator_Main(this, stripped, " == "), lambda44), lambda45), lambda46), lambda47), lambda48), lambda49), lambda50);
	if (maybeOperator.variant = ?.SomeVariant) 
		return map_/*Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'or' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@1e81f4dc]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(maybeOperator, F? { alloc(JExpressionWrapper), F?Table { new }});
	if (isIdentifier_Main(this, stripped)) 
		return new_Some<JExpression>(new_Identifier(stripped));
	if (startsWith_char_ptr(stripped, "!")) {
		char* substring = substring_char_ptr(stripped, 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Option, typeArguments=magma.Main$JavaList@3b22cdd0]', not an object.]*/ maybeInstance = map_Option(parseCExpression_Main(this, substring), F? { alloc(CExpression), F?Table { generate }});
		if (maybeInstance.variant = ?.SomeVariant) 
			return map_Some(new_Some<char*>("!" + instance), F? { alloc(JExpressionWrapper), F?Table { new }});
	}
	if (isNumber_Main(this, stripped)) 
		return map_Some(new_Some<char*>(stripped), F? { alloc(JExpressionWrapper), F?Table { new }});
	if (endsWith_/*Unwrapped expression: startsWith_char_ptr(stripped, "\"") && stripped*/(startsWith_char_ptr(stripped, "\"") && stripped, "\"")) 
		return map_Some(new_Some<char*>(stripped), F? { alloc(JExpressionWrapper), F?Table { new }});
	return new_None<JExpression>();
}
/*?*/ lambda51(void* _ref, /*?*/ param){
	return new_CDeclaration(new_Placeholder("?"), param);
}
Option<char*> compileLambda_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	int index = indexOf_char_ptr(input, "->");
	if (index < 0) 
		return new_None<char*>();
	char* beforeContent = strip_char_ptr(substring_char_ptr(input, 0, index), );
	char* maybeWithBraces = strip_char_ptr(substring_char_ptr(input, index + 2), );
	Option<List<char*>> maybeParams = parseLambdaParams_Main(this, beforeContent);
	if (!(maybeParams.variant = ?.SomeVariant)) 
		return new_None<char*>();
	/*Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ paramList = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]]', not an object.]*/(toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]*/(iter_/*Undefined identifier: params*/(params, ), lambda51), ), new_CDeclaration(new_CPointerType(CPrimitiveType.Void), "_ref"));
	char* output;
	if (endsWith_/*Unwrapped expression: startsWith_char_ptr(maybeWithBraces, "{") && maybeWithBraces*/(startsWith_char_ptr(maybeWithBraces, "{") && maybeWithBraces, "}")) {
		char* content = substring_char_ptr(maybeWithBraces, 1, length_char_ptr(maybeWithBraces, ) - 1);
		output = compileMethodsSegments_Main(this, content, 1);
	}
	else output = generateStatement_/*Undefined identifier: Main*/(Main, compileExpressionOrPlaceholder_/*Unwrapped expression: "return " + this*/("return " + this, maybeWithBraces));
	char* generatedName = generateName_Main(this, );
	CFunction cFunction = new_CFunction(new_CFunctionHeader(new_CDeclaration(new_Placeholder("?"), generatedName), paramList), output);
	_this->functions = addLast_List(_this->functions, cFunction);
	return new_Some<char*>(generatedName);
}
/*?*/ lambda52(void* _ref, /*?*/ slice){
	return isEmpty_/*Unwrapped expression: !slice*/(!slice, );
}
Option<List<char*>> parseLambdaParams_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	if (isIdentifier_Main(this, input)) 
		return new_Some<List<char*>>(of_/*Undefined identifier: Lists*/(Lists, input));
	else 
	if (startsWith_char_ptr(input, "(") && input.endsWith(")")) {
		char* substring = substring_char_ptr(input, 1, length_char_ptr(input, ) - 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]*/ list = toList_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]*/(map_Iter(divide_Main(this, substring, new_ValueFolder()), F? { alloc(String), F?Table { strip }}), lambda52), );
		return new_Some<List<char*>>(list);
	}
	/*else return new None<List<String>>()*/;
}
char* generateName_Main(void* _ref){
	Main* _this = (Main*) _ref;
	/*Cannot access member 'counter' in 'Placeholder[input=Unwrapped expression: "lambda" + this]', not an object.*/ generatedName = "lambda" + this.counter;
	_this->counter++;
	return generatedName;
}
Option<char*> compileOperator_Main(void* _ref, char* input, char* operator){
	Main* _this = (Main*) _ref;
	if (length_char_ptr(input, ) < 3) 
		return new_None<char*>();
	if (contains_/*Unwrapped expression: !input*/(!input, operator)) 
		return new_None<char*>();
	/*Unwrapped expression: -1*/ i1 = -1;
	/*Unwrapped expression: 0*/ depth = 0;
	/*Unwrapped expression: 0*/ i = 0;
	while (i < length_char_ptr(input, ) - 1) {
		char c = charAt_char_ptr(input, i);
		if (charAt_/*Unwrapped expression: c == operator*/(c == operator, 0)) 
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
		char* leftString = substring_char_ptr(input, 0, i1);
		char* right = substring_char_ptr(input, length_/*Unwrapped expression: i1 + operator*/(i1 + operator, ));
		if (map_Option(parseCExpression_Main(this, leftString), F? { alloc(CExpression), F?Table { generate }}).variant = ?.SomeVariant) 
			if (map_Option(parseCExpression_Main(this, right), F? { alloc(CExpression), F?Table { generate }}).variant = ?.SomeVariant) 
				return new_Some<char*>(leftCompiled + " " + operator + " " + rightCompiled);
	}
	return new_None<char*>();
}
Option<JExpression> parseInvokable_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	if (endsWith_/*Unwrapped expression: !stripped*/(!stripped, ")")) 
		return new_None<JExpression>();
	int length = length_char_ptr(stripped, );
	char* withoutEnd = substring_char_ptr(stripped, 0, length - 1);
	int callerStart = findCallerStart_Main(this, withoutEnd);
	if (callerStart < 0) 
		return new_None<JExpression>();
	char* callerString = substring_char_ptr(withoutEnd, 0, callerStart);
	char* argumentsString = substring_char_ptr(withoutEnd, callerStart + 1);
	Option<JCaller> maybeCaller = parseCaller_Main(this, callerString);
	if (!(maybeCaller.variant = ?.Some(var value)Variant)) 
		return new_None<JExpression>();
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]]', not an object.]*/ arguments = toList_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]*/(map_Iter(divide_Main(this, argumentsString, new_EscapedFolder(new_ValueFolder())), F? { alloc(this), F?Table { parseExpression }}), F? { alloc(Option), F?Table { iter }}), );
	return new_Some<JExpression>(new_JInvokable(value, arguments));
}
int findCallerStart_Main(void* _ref, char* withoutEnd){
	Main* _this = (Main*) _ref;
	/*Unwrapped expression: -1*/ callerStart = -1;
	/*Unwrapped expression: 0*/ depth = 0;
	/*Unwrapped expression: 0*/ i = 0;
	while (length_/*Unwrapped expression: i < withoutEnd*/(i < withoutEnd, )) {
		char c = charAt_char_ptr(withoutEnd, i);
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
	if (startsWith_char_ptr(input, " - ")) 
		return allDigits_Main(this, substring_char_ptr(input, 1));
	return allDigits_Main(this, input);
}
int allDigits_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return allMatch_/*Not a functional type: Placeholder[input=Cannot access member 'mapToObj' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'range' in 'Placeholder[input=Undefined identifier: IntStream]', not an object.]]', not an object.]*/(mapToObj_/*Not a functional type: Placeholder[input=Cannot access member 'range' in 'Placeholder[input=Undefined identifier: IntStream]', not an object.]*/(range_/*Undefined identifier: IntStream*/(IntStream, 0, length_char_ptr(input, )), F? { alloc(input), F?Table { charAt }}), F? { alloc(Character), F?Table { isDigit }});
}
Option<JCaller> parseCaller_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_char_ptr(input, );
	Option<JExpression> maybeExpression = parseExpression_Main(this, stripped);
	if (maybeExpression.variant = ?.SomeVariant) 
		return new_Some<JCaller>(expression);
	if (startsWith_char_ptr(stripped, "new ")) {
		char* type = substring_char_ptr(stripped, length_/*Unwrapped expression: "new "*/("new ", ));
		JType jType = parseType_Main(this, type);
		return new_Some<JCaller>(new_JConstruction(jType));
	}
	return new_None<JCaller>();
}
Option<JDeclaration> parseDeclaration_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_char_ptr(input, );
	int nameSeparator = lastIndexOf_char_ptr(stripped, " ");
	if (nameSeparator >= 0) {
		char* beforeName = strip_char_ptr(substring_char_ptr(stripped, 0, nameSeparator), );
		char* name = strip_char_ptr(substring_char_ptr(stripped, nameSeparator + 1), );
		int typeSeparator = findTypeSeparator_Main(this, beforeName);
		if (isIdentifier_/*Unwrapped expression: !this*/(!this, name)) 
			return new_None<JDeclaration>();
		if (typeSeparator < 0) {
			JType type = parseType_Main(this, beforeName);
			return new_Some<JDeclaration>(new_JDeclaration(name, type));
		}
		char* beforeType = strip_char_ptr(substring_char_ptr(beforeName, 0, typeSeparator), );
		List<char*> copy = empty_/*Undefined identifier: Lists*/(Lists, );
		if (endsWith_char_ptr(beforeType, ">")) {
			char* substring = substring_char_ptr(beforeType, 0, length_char_ptr(beforeType, ) - 1);
			int i = indexOf_char_ptr(substring, " < ");
			if (i >= 0) {
				char* substring2 = substring_char_ptr(substring, i + 1);
				copy = splitValues_Main(this, substring2);
				beforeType = substring_char_ptr(substring, 0, i);
			}
		}
		List<char*> annotations = empty_/*Undefined identifier: Lists*/(Lists, );
		int i = lastIndexOf_char_ptr(beforeType, "\n");
		if (i >= 0) {
			annotations = collectAnnotations_Main(this, substring_char_ptr(beforeType, 0, i));
			beforeType = strip_char_ptr(substring_char_ptr(beforeType, i + 1), );
		}
		if (isIdentifier_Main(this, name)) {
			JType type = parseType_Main(this, substring_char_ptr(beforeName, typeSeparator + 1));
			JDeclaration jDeclaration = new_JDeclaration(annotations, copy, new_Some<char*>(beforeType), type, name);
			return new_Some<JDeclaration>(jDeclaration);
		}
	}
	return new_None<JDeclaration>();
}
/*?*/ lambda53(void* _ref, /*?*/ slice){
	return isEmpty_/*Unwrapped expression: !slice*/(!slice, );
}
/*?*/ lambda54(void* _ref, /*?*/ slice){
	return substring_/*Undefined identifier: slice*/(slice, 1);
}
List<char*> collectAnnotations_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'fromObjArray' in 'Placeholder[input=Undefined identifier: Streams]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'fromObjArray' in 'Placeholder[input=Undefined identifier: Streams]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'fromObjArray' in 'Placeholder[input=Undefined identifier: Streams]', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'fromObjArray' in 'Placeholder[input=Undefined identifier: Streams]', not an object.]*/(fromObjArray_/*Undefined identifier: Streams*/(Streams, split_char_ptr(input, quote_/*Undefined identifier: Pattern*/(Pattern, "\n"))), lambda53), lambda54), F? { alloc(String), F?Table { strip }}), );
}
int findTypeSeparator_Main(void* _ref, char* beforeName){
	Main* _this = (Main*) _ref;
	/*Unwrapped expression: -1*/ typeSeparator = -1;
	/*Unwrapped expression: 0*/ depth = 0;
	/*Unwrapped expression: 0*/ i = 0;
	while (length_/*Unwrapped expression: i < beforeName*/(i < beforeName, )) {
		char c = charAt_char_ptr(beforeName, i);
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
JType parseType_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_char_ptr(input, );
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
	if (endsWith_char_ptr(stripped, "[]")) {
		char* slice = substring_char_ptr(stripped, 0, length_char_ptr(stripped, ) - 2);
		JType type = parseType_Main(this, slice);
		return new_JArrayType(type);
	}
	if (endsWith_char_ptr(stripped, ">")) {
		char* substring = substring_char_ptr(stripped, 0, length_char_ptr(stripped, ) - 1);
		int i = indexOf_char_ptr(substring, " < ");
		if (i >= 0) {
			char* base = substring_char_ptr(substring, 0, i);
			char* parameters = substring_char_ptr(substring, i + 1);
			/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]]', not an object.]*/ list = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'JGenericType[base=Iter, typeArguments=magma.Main$JavaList@606d8acf]', not an object.]*/(map_Iter(divide_Main(this, parameters, new_ValueFolder()), F? { alloc(this), F?Table { parseType }}), );
			return new_JGenericType(base, list);
		}
	}
	if (isIdentifier_Main(this, stripped)) 
		return new_Identifier(stripped);
	return new_Placeholder(stripped);
}
