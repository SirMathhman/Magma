struct CPrimitiveType;
struct JPrimitiveType;
struct Operator;
template <typename T>
struct Head;
template <typename T>
struct List;
struct Path;
template <typename T>
struct FR;
template <typename T>
struct Option;
template <typename T0, typename R>
struct F1R;
template <typename T, typename X>
struct Result;
struct CNamedType;
struct CType;
struct JMethodDeclaration;
struct CStructMember;
struct Folder;
template <typename A, typename B, typename R>
struct F2R;
struct Actual;
template <typename T, typename C>
struct Collector;
struct IOError;
struct CAssignable;
struct JType;
struct JAssignable;
struct JExpression;
struct CExpression;
struct JCaller;
struct CDefinable;
struct CStructureOrUnion;
struct JObjectMember;
template <typename T>
struct F1;
struct StringBuilders;
struct StringBuilder;
template <typename T>
struct Iter;
struct RangeHead;
struct Lists;
template <typename T, typename X>
struct Err;
template <typename T, typename X>
struct Ok;
template <typename A, typename B>
struct Tuple;
struct State;
struct CPointerType;
struct CTemplateType;
struct CQuantity;
struct CDereference;
struct Identifier;
struct Placeholder;
struct JConstructor;
struct JDeclaration;
struct CFunctionDeclaration;
struct EmptyStructMember;
struct EscapedFolder;
struct ValueFolder;
template <typename T>
struct Some;
template <typename T>
struct None;
struct ConditionEndLocator;
struct CField;
struct Streams;
template <typename T, typename R>
struct MapHead;
template <typename T>
struct SingleHead;
template <typename T, typename R>
struct FlatMapHead;
template <typename T>
struct EmptyHead;
template <typename T>
struct AnyMatch;
struct Joiner;
template <typename T>
struct ListCollector;
struct Paths;
struct CDeclaration;
struct JArrayType;
struct JGenericType;
struct CPointerAccess;
struct CFieldAccess;
struct JMemberAccess;
struct JConstruction;
struct CInvocation;
struct JInvokable;
struct JFunctionalType;
struct Environment;
struct Frame;
struct JObjectType;
struct JRecursiveType;
struct CStructure;
struct CEnum;
struct CUnion;
struct JObject;
struct CFunctionHeader;
struct CFunction;
struct JMethod;
struct JField;
struct JNumber;
struct CNumber;
struct JNot;
struct CNot;
template <typename K, typename V>
struct MapCollector;
struct CReference;
struct Char;
struct JOperator;
struct COperator;
struct StringNode;
struct Main;
enum class OptionTag {
	NoneVariant,
	SomeVariant
};
enum class ResultTag {
	ErrVariant,
	OkVariant
};
enum class CNamedTypeTag {
	CTemplateTypeVariant,
	IdentifierVariant
};
enum class JMethodDeclarationTag {
	JConstructorVariant,
	JDeclarationVariant,
	PlaceholderVariant
};
enum class CStructMemberTag {
	EmptyStructMemberVariant,
	CFieldVariant,
	CFunctionDeclarationVariant,
	PlaceholderVariant
};
enum class CDefinableTag {
	CDeclarationVariant,
	CFunctionDeclarationVariant,
	PlaceholderVariant
};
enum class CStructureOrUnionTag {
	CStructureVariant,
	CUnionVariant
};
struct RangeHead {
	int length;
	int counter;
};
template <typename A, typename B, typename R>
struct F2RTable {
	R (*apply)(void*, A, B);
};
struct CExpressionTable {
};
template <typename T>
struct ListCollector {
};
struct Lists {
};
struct ValueFolder {
};
template <typename T>
struct SingleHead {
	T value;
	int retrieved;
};
template <typename T>
struct Some {
	T value;
};
struct JExpressionTable {
};
struct JPrimitiveType {
	char* name;
};
struct Placeholder {
	char* input;
};
template <typename T, typename X>
struct Err {
	X error;
};
struct JConstructor {
	char* type;
};
struct JCallerTable {
};
struct StringBuilders {
};
struct Char {
	char* value;
};
template <typename T0, typename R>
struct F1RTable {
	R (*apply)(void*, T0);
};
struct Paths {
};
struct JObjectMemberTable {
};
template <typename K, typename V>
struct MapCollector {
};
template <typename T>
struct EmptyHead {
};
struct ConditionEndLocator {
};
struct JTypeTable {
	char* (*stringify)(void*);
};
struct ActualTable {
};
struct JNumber {
	char* value;
};
template <typename T>
struct None {
};
template <typename A, typename B>
struct Tuple {
	A left;
	B right;
};
struct IOErrorTable {
	char* (*display)(void*);
};
template <typename T, typename C>
struct CollectorTable {
	C (*createInitial)(void*);
	C (*fold)(void*, C, T);
};
struct EmptyStructMember {
};
struct Identifier {
	char* value;
};
struct CNumber {
	char* value;
};
template <typename T>
struct F1Table {
	void (*apply)(void*, T);
};
template <typename T>
struct FRTable {
	T (*apply)(void*);
};
struct StringNode {
	char* slice;
};
struct Streams {
};
struct Joiner {
	char* delimiter;
};
struct JAssignableTable {
};
struct CPrimitiveType {
	char* content;
};
struct CAssignableTable {
	char* (*generate)(void*);
};
template <typename T, typename X>
struct Ok {
	T value;
};
template <typename T0, typename R>
struct F1R {
	void* data;
	F1RTable<T0, R> table;
};
struct JObjectMember {
	void* data;
	JObjectMemberTable table;
};
struct JCaller {
	void* data;
	JCallerTable table;
};
template <typename A, typename B, typename R>
struct F2R {
	void* data;
	F2RTable<A, B, R> table;
};
template <typename T, typename X>
union ResultData {
	Err<T, X> Err;
	Ok<T, X> Ok;
};
struct Actual {
	void* data;
	ActualTable table;
};
struct JExpression {
	void* data;
	JExpressionTable table;
};
template <typename T>
union OptionData {
	None<T> None;
	Some<T> Some;
};
struct CExpression {
	void* data;
	CExpressionTable table;
};
struct JType {
	void* data;
	JTypeTable table;
};
struct JAssignable {
	void* data;
	JAssignableTable table;
};
template <typename T>
struct F1 {
	void* data;
	F1Table<T> table;
};
struct CAssignable {
	void* data;
	CAssignableTable table;
};
template <typename T>
struct FR {
	void* data;
	FRTable<T> table;
};
template <typename T, typename C>
struct Collector {
	void* data;
	CollectorTable<T, C> table;
};
struct IOError {
	void* data;
	IOErrorTable table;
};
struct JNot {
	CExpression instance;
};
struct CFieldAccess {
	CExpression instance;
	char* fieldName;
};
struct CPointerAccess {
	CExpression instance;
	char* fieldName;
};
struct JConstruction {
	JType jType;
};
template <typename T, typename X>
struct Result {
	ResultTag variant;
	ResultData<T, X> data;
};
struct CNot {
	CExpression instance;
};
template <typename T>
struct AnyMatch {
	F1R<T, int> predicate;
};
struct CDereference {
	CExpression expression;
};
struct JArrayType {
	JType type;
};
template <typename T>
struct Option {
	OptionTag variant;
	OptionData<T> data;
};
struct Operator {
	char* value;
	JType type;
};
struct CQuantity {
	CExpression expression;
};
struct CReference {
	CExpression instance;
};
struct JMemberAccess {
	JExpression instance;
	char* memberName;
};
struct JRecursiveType {
	Option<JType> maybeInternal;
};
template <typename T>
struct HeadTable {
	Option<T> (*next)(void*);
};
struct JOperator {
	JExpression left;
	Operator operator;
	JExpression rightCompiled;
};
struct PathTable {
	Option<IOError> (*writeString)(void*, char*);
	Result<char*, IOError> (*readString)(void*);
	Option<Path> (*getParent)(void*);
	Option<IOError> (*createDirectories)(void*);
};
struct COperator {
	CExpression left;
	Operator operator;
	CExpression right;
};
template <typename T>
struct Head {
	void* data;
	HeadTable<T> table;
};
struct Path {
	void* data;
	PathTable table;
};
template <typename T>
struct Iter {
	Head<T> head;
};
template <typename T, typename R>
struct MapHead {
	Head<T> head;
	F1R<T, R> mapper;
};
template <typename T, typename R>
struct FlatMapHead {
	Head<T> head;
	F1R<T, Iter<R>> mapper;
	Option<Iter<R>> maybeCurrent;
};
template <typename T>
struct ListTable {
	Iter<T> (*iter)(void*);
	int (*isEmpty)(void*);
	List<T> (*addLast)(void*, T);
	int (*contains)(void*, T);
	List<T> (*addFirst)(void*, T);
	List<T> (*addAllLast)(void*, List<T>);
	int (*size)(void*);
	T (*getFirst)(void*);
	List<T> (*subList)(void*, int, int);
	List<T> (*clear)(void*);
	List<T> (*removeLast)(void*);
	List<T> (*mapLast)(void*, F1R<T, T>);
	Iter<T> (*iterReversed)(void*);
	List<T> (*copy)(void*);
	List<T> (*removeElement)(void*, T);
};
template <typename T>
struct List {
	void* data;
	ListTable<T> table;
};
struct JDeclaration {
	List<char*> annotations;
	List<char*> typeParameters;
	Option<char*> maybeBeforeType;
	JType type;
	char* name;
};
struct JInvokable {
	JCaller caller;
	List<JExpression> arguments;
};
struct CEnum {
	char* name;
	List<char*> variants;
};
struct CTypeTable {
	char* (*generate)(void*);
	char* (*toBaseName)(void*);
	List<char*> (*extractIdentifiers)(void*);
};
struct JGenericType {
	char* base;
	List<JType> typeArguments;
};
struct CInvocation {
	CExpression expression;
	List<CExpression> cArguments;
};
struct JFunctionalType {
	List<JType> parameterTypes;
	JType returnType;
};
struct StringBuilder {
	List<char> list;
};
struct JField {
	JDeclaration declaration;
};
struct JObjectType {
	char* name;
	List<JDeclaration> members;
};
union JMethodDeclarationData {
	JConstructor JConstructor;
	JDeclaration JDeclaration;
	Placeholder Placeholder;
};
struct State {
	char* input;
	StringBuilder buffer;
	List<char*> segments;
	int index;
	int depth;
};
struct CType {
	void* data;
	CTypeTable table;
};
struct CFunctionDeclaration {
	CType type;
	char* name;
	List<CType> parameterTypes;
};
struct CTemplateType {
	char* base;
	List<CType> typeArguments;
};
struct CPointerType {
	CType type;
};
struct FolderTable {
	State (*apply)(void*, State, char);
};
struct JMethodDeclaration {
	JMethodDeclarationTag variant;
	JMethodDeclarationData data;
};
struct CDeclaration {
	List<char*> typeParameters;
	CType type;
	char* name;
};
struct JObject {
	char* type;
	List<char*> annotations;
	List<char*> modifiersList;
	char* name;
	List<char*> typeParameters;
	List<JDeclaration> recordFields;
	List<CType> implementees;
	List<char*> variants;
	List<JObjectMember> children;
	/*+*/ joinedTypeParameters;
	/*+*/ s3;
};
union CNamedTypeData {
	CTemplateType CTemplateType;
	Identifier Identifier;
};
struct Frame {
	Option<JObject> maybeObject;
	List<JDeclaration> definedExpressions;
	List<JObjectType> definedTypes;
};
struct Folder {
	void* data;
	FolderTable table;
};
struct JMethod {
	List<char*> typeParameters;
	List<JDeclaration> parameters;
	JMethodDeclaration methodDeclaration;
	char* content;
};
union CDefinableData {
	CDeclaration CDeclaration;
	CFunctionDeclaration CFunctionDeclaration;
	Placeholder Placeholder;
};
struct CDefinable {
	CDefinableTag variant;
	CDefinableData data;
};
struct CNamedType {
	CNamedTypeTag variant;
	CNamedTypeData data;
};
struct Environment {
	List<Frame> frames;
};
struct EscapedFolder {
	Folder folder;
};
struct CField {
	CDefinable declaration;
};
struct CStructure {
	List<char*> typeParameters;
	char* name;
	List<CDefinable> fields;
};
struct CUnion {
	List<char*> typeParameters;
	char* name;
	List<CDefinable> members;
};
struct CFunctionHeader {
	CDefinable definition;
	List<CDeclaration> parameters;
};
struct CFunction {
	CFunctionHeader header;
	char* content;
};
union CStructureOrUnionData {
	CStructure CStructure;
	CUnion CUnion;
};
union CStructMemberData {
	EmptyStructMember EmptyStructMember;
	CField CField;
	CFunctionDeclaration CFunctionDeclaration;
	Placeholder Placeholder;
};
struct CStructureOrUnion {
	CStructureOrUnionTag variant;
	CStructureOrUnionData data;
};
struct CStructMember {
	CStructMemberTag variant;
	CStructMemberData data;
};
struct Main {
	List<char*> functionDeclarations;
	List<char*> globals;
	List<char*> structureForwardDeclarations;
	List<CStructureOrUnion> structuresOrUnions;
	List<CFunction> functions;
	int counter;
	List<CEnum> enums;
};
CPrimitiveType CPrimitiveTypeVoid = new_CPrimitiveType("void");
CPrimitiveType CPrimitiveTypeChar = new_CPrimitiveType("char");
CPrimitiveType CPrimitiveTypeInt = new_CPrimitiveType("int");
JPrimitiveType JPrimitiveTypeInt = new_JPrimitiveType("int");
JPrimitiveType JPrimitiveTypeVoid = new_JPrimitiveType("void");
JPrimitiveType JPrimitiveTypeBoolean = new_JPrimitiveType("bool");
JPrimitiveType JPrimitiveTypeChar = new_JPrimitiveType("char");
JPrimitiveType JPrimitiveTypeVar = new_JPrimitiveType("var");
Operator OperatorEquals = new_Operator("!=", JPrimitiveType.Boolean);
Operator OperatorNotEquals = new_Operator("!=", JPrimitiveType.Boolean);
Operator OperatorLessThan = new_Operator("<", JPrimitiveType.Boolean), Add("+", JPrimitiveType.Int), Subtract("-", JPrimitiveType.Int),
		And("&&", JPrimitiveType.Boolean), Or("||", JPrimitiveType.Boolean),
		GreaterThanOrEquals(">=", JPrimitiveType.Boolean);
CPrimitiveType new_CPrimitiveType(char* content);
char* generate_CPrimitiveType(void* _ref);
char* toBaseName_CPrimitiveType(void* _ref);
List<char*> extractIdentifiers_CPrimitiveType(void* _ref);
JPrimitiveType new_JPrimitiveType(char* name);
char* stringify_JPrimitiveType(void* _ref);
Operator new_Operator(char* value, JType type);
char* generate_Operator(void* _ref);
JType getType_Operator(void* _ref);
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
List<T> mapLast_List(void* _ref, F1R<T, T> mapper);
template <typename T>
Iter<T> iterReversed_List(void* _ref);
template <typename T>
List<T> copy_List(void* _ref);
template <typename T>
List<T> removeElement_List(void* _ref, T element);
Option<IOError> writeString_Path(void* _ref, char* output);
Result<char*, IOError> readString_Path(void* _ref);
Option<Path> getParent_Path(void* _ref);
Option<IOError> createDirectories_Path(void* _ref);
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
char* getName_CNamedType(void* _ref);
char* generate_CType(void* _ref);
char* toBaseName_CType(void* _ref);
List<char*> extractIdentifiers_CType(void* _ref);
char* generate_CStructMember(void* _ref);
State apply_Folder(void* _ref, State state, char character);
template <typename A, typename B, typename R>
R apply_F2R(void* _ref, A a, B b);
template <typename T, typename C>
C createInitial_Collector(void* _ref);
template <typename T, typename C>
C fold_Collector(void* _ref, C c, T t);
char* display_IOError(void* _ref);
char* generate_CAssignable(void* _ref);
char* stringify_JType(void* _ref);
char* generate_CDefinable(void* _ref);
List<char*> extractIdentifiers_CDefinable(void* _ref);
CDefinable mapTypeParameters_CDefinable(void* _ref, F1R<List<char*>, List<char*>> mapper);
CDefinable mapName_CDefinable(void* _ref, F1R<char*, char*> mapper);
char* generate_CStructureOrUnion(void* _ref);
List<char*> findDependencies_CStructureOrUnion(void* _ref);
char* findName_CStructureOrUnion(void* _ref);
template <typename T>
void apply_F1(void* _ref, T element);
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
template <typename T>
void forEach_Iter(void* _ref, F1<T> consumer);
RangeHead new_RangeHead(int length);
Option<int> next_RangeHead(void* _ref);
template <typename T>
List<T> empty_Lists();
template <typename T>
List<T> of_Lists(void* _ref, /*T...*/ elements);
template <typename R, typename T, typename X>
Result<R, X> mapValue_Err(void* _ref, F1R<T, R> mapper);
template <typename R, typename T, typename X>
Result<R, X> mapValue_Ok(void* _ref, F1R<T, R> mapper);
State new_State(char* input);
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
List<char*> extractIdentifiers_CPointerType(void* _ref);
CTemplateType new_CTemplateType(char* base, List<CType> typeArguments);
char* generate_CTemplateType(void* _ref);
char* toBaseName_CTemplateType(void* _ref);
List<char*> extractIdentifiers_CTemplateType(void* _ref);
char* getName_CTemplateType(void* _ref);
char* generate_CQuantity(void* _ref);
char* generate_CDereference(void* _ref);
int isIdentifier_Identifier(void* _ref, char* input);
char* generate_Identifier(void* _ref);
char* toBaseName_Identifier(void* _ref);
List<char*> extractIdentifiers_Identifier(void* _ref);
char* stringify_Identifier(void* _ref);
char* getName_Identifier(void* _ref);
char* wrap_Placeholder(void* _ref, char* input);
char* generate_Placeholder(void* _ref);
char* toBaseName_Placeholder(void* _ref);
List<char*> extractIdentifiers_Placeholder(void* _ref);
CDefinable mapTypeParameters_Placeholder(void* _ref, F1R<List<char*>, List<char*>> mapper);
CDefinable mapName_Placeholder(void* _ref, F1R<char*, char*> mapper);
char* stringify_Placeholder(void* _ref);
JDeclaration new_JDeclaration(char* name, JType type);
char* toString_JDeclaration(void* _ref);
JDeclaration mapName_JDeclaration(void* _ref, F1R<char*, char*> mapper);
CDeclaration toCDeclaration_JDeclaration(void* _ref);
CAssignable toCAssignable_JDeclaration(void* _ref);
JDeclaration withType_JDeclaration(void* _ref, JType type);
char* generate_CFunctionDeclaration(void* _ref);
List<char*> extractIdentifiers_CFunctionDeclaration(void* _ref);
CDefinable mapTypeParameters_CFunctionDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper);
CDefinable mapName_CFunctionDeclaration(void* _ref, F1R<char*, char*> mapper);
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
Joiner new_Joiner();
char* createInitial_Joiner(void* _ref);
char* fold_Joiner(void* _ref, char* current, char* element);
template <typename T>
List<T> createInitial_ListCollector(void* _ref);
template <typename T>
List<T> fold_ListCollector(void* _ref, List<T> tList, T t);
Path get_Paths(char* first, /*String...*/ more);
CDeclaration new_CDeclaration(CType type, char* name);
CDefinable mapName_CDeclaration(void* _ref, F1R<char*, char*> mapper);
CDefinable mapTypeParameters_CDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper);
char* generate_CDeclaration(void* _ref);
List<char*> extractIdentifiers_CDeclaration(void* _ref);
CType toCType_JArrayType(void* _ref);
char* stringify_JArrayType(void* _ref);
CType toCType_JGenericType(void* _ref);
char* stringify_JGenericType(void* _ref);
char* generate_CPointerAccess(void* _ref);
char* generate_CFieldAccess(void* _ref);
char* generate_CInvocation(void* _ref);
JFunctionalType new_JFunctionalType(JType returnType);
char* stringify_JFunctionalType(void* _ref);
Environment new_Environment();
Environment new_Environment(List<Frame> frames);
Option<JDeclaration> resolveExpression_Environment(void* _ref, char* identifier);
template <typename T>
Tuple<Environment, T> within_Environment(void* _ref, F1R<Environment, Tuple<Environment, T>> supplier);
Environment exit_Environment(void* _ref);
Environment enter_Environment(void* _ref);
Environment defineAllExpressions_Environment(void* _ref, List<JDeclaration> declarations);
Environment defineExpression_Environment(void* _ref, JDeclaration declaration);
Option<JObjectType> resolveCurrent_Environment(void* _ref);
Environment withObject_Environment(void* _ref, JObject object);
Option<JObjectType> resolveType_Environment(void* _ref, char* name);
Environment defineAllTypes_Environment(void* _ref, List<JObjectType> types);
Frame new_Frame(Option<JObject> maybeName, List<JObjectType> definedTypes, List<JDeclaration> definedExpressions);
Frame new_Frame();
Frame defineAllExpressions_Frame(void* _ref, List<JDeclaration> definitions);
Option<JDeclaration> resolveExpression_Frame(void* _ref, char* identifier);
Frame defineExpression_Frame(void* _ref, JDeclaration declaration);
Option<JObjectType> toStructureType_Frame(void* _ref);
Frame withObject_Frame(void* _ref, JObject name);
Option<JObjectType> resolveType_Frame(void* _ref, char* name);
Frame defineAllTypes_Frame(void* _ref, List<JObjectType> types);
Option<JType> resolve_JObjectType(void* _ref, char* name);
char* stringify_JObjectType(void* _ref);
JRecursiveType new_JRecursiveType();
JType create_JRecursiveType(void* _ref, F1R<JType, JType> mapper);
void set_JRecursiveType(void* _ref, JType created);
char* stringify_JRecursiveType(void* _ref);
char* findName_CStructure(void* _ref);
char* generate_CStructure(void* _ref);
List<char*> findDependencies_CStructure(void* _ref);
char* generate_CEnum(void* _ref);
char* findName_CUnion(void* _ref);
char* generate_CUnion(void* _ref);
List<char*> findDependencies_CUnion(void* _ref);
List<CDefinable> collectCFields_JObject(void* _ref);
List<CFunction> createConversionFunctions_JObject(void* _ref);
CFunction createConversionType_JObject(void* _ref, CType implementee);
/*var joinedTypeParameters = Main.joinTypeParameters*/();
/*final var identifier = implementee.toBaseName*/();
/*final var s = Main.generateStatement*/();
/*final var s1 = Main.generateStatement*/();
/*final var s2 = Main.generateStatement*/();
/*final var s3 = Main.generateStatement*/();
/*final var parameters = Lists.of*/();
new CFunctionHeader_JObject(void* _ref, /*implementee,*/ conversionFunctionName);
new CFunction_JObject(void* _ref);
JObjectType toType_JObject(void* _ref);
/*final var memberDefinitions = this.children.iter*/();
new JObjectType_JObject(void* _ref);
Option<JDeclaration> extractDefinition_JObject(void* _ref, JObjectMember child);
/*return*/ switch_JObject(void* _ref);
/*case JField jField -> new Some<JDeclaration>*/();
/*case JMethod jMethod -> jMethod.toDeclaration*/();
/*case EmptyStructMember _, JObject _, Placeholder _ -> new None<JDeclaration>*/();
/*-> throw new*/ IllegalStateException_JObject(void* _ref, /*+*/ child);
char* generate_CFunctionHeader(void* _ref);
char* generate_CFunction(void* _ref);
Option<JDeclaration> toDeclaration_JMethod(void* _ref);
JNumber new_JNumber(char* value);
CNumber new_CNumber(char* value);
char* generate_CNumber(void* _ref);
char* generate_CNot(void* _ref);
template <typename K, typename V>
Map<K, V> createInitial_MapCollector(void* _ref);
template <typename K, typename V>
Map<K, V> fold_MapCollector(void* _ref, Map<K, V> kvMap, Tuple<K, V> kvTuple);
char* generate_CReference(void* _ref);
char* generate_Char(void* _ref);
char* generate_COperator(void* _ref);
char* generate_StringNode(void* _ref);
/*private static final JType StringType = JRecursiveType.create*/(/*-> {
		// We don't need parameter types for*/ now);
new Environment_Main(void* _ref);
Main new_Main();
char* generateTemplateString_Main(void* _ref, List<char*> typeParameters);
void main_Main(void* _ref, char** args);
char* generateStatement_Main(void* _ref, int depth, char* content);
char* generateIndent_Main(void* _ref, int depth);
CType transformType_Main(void* _ref, JType jType);
CType transformPrimitiveType_Main(void* _ref, JPrimitiveType type);
char* generateStatement_Main(void* _ref, char* content);
char* joinTypeParameters_Main(void* _ref, List<char*> typeParameters);
Option<JObjectType> extractType_Main(void* _ref, JObjectMember jObjectMember);
Tuple<CExpression, List<CType>> transformCaller_Main(void* _ref, JCaller jCaller);
Tuple<CExpression, List<CType>> destroyConstruction_Main(void* _ref, JConstruction jConstruction);
CExpression transformExpression_Main(void* _ref, JExpression expression);
CExpression transformInvocation_Main(void* _ref, JInvokable jInvokable);
Option<IOError> run_Main(void* _ref);
char* compile_Main(void* _ref, char* input);
List<CStructureOrUnion> createTopologicallySortedList_Main(void* _ref);
void removeKeyFromValues_Main(void* _ref, Map<char*, List<char*>> dependencyMap, char* dependencyMapKeyToRemove);
List<char*> removeDuplicates_Main(void* _ref, List<char*> list);
char* joinStrings_Main(void* _ref, List<char*> structures);
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper);
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder);
Iter<char*> divide_Main(void* _ref, char* input, Folder folder);
State foldStatement_Main(void* _ref, State current, char next);
char* compileRootSegment_Main(void* _ref, char* input);
Option<JObject> parseObject_Main(void* _ref, char* type, char* stripped);
Option<CStructMember> transformObject_Main(void* _ref, JObject object);
List<CDefinable> handleUnsealedInterface_Main(void* _ref, JObject object, List<CStructMember> members, List<CDefinable> fields);
CNamedType createStructureType_Main(void* _ref, char* name, List<char*> typeArguments);
List<CDefinable> handleSealedInterface_Main(void* _ref, JObject object, List<CDefinable> fields);
CDefinable createUnionField_Main(void* _ref, char* variant, List<CType> typeArguments);
Option<JDeclaration> extractField_Main(void* _ref, JObjectMember prototype);
Option<CStructMember> transformObjectMemberPrototype_Main(void* _ref, JObject object, JObjectMember wrapper);
CStructMember completeMethodProto_Main(void* _ref, JMethod jFunctionProto, JObject object);
Option<JObjectMember> parseObjectMember_Main(void* _ref, char* input, char* name, List<char*> typeParameters);
Option<JObjectMember> parseMethod_Main(void* _ref, char* stripped, char* name, List<char*> typeParameters);
List<CDefinable> retainFields_Main(void* _ref, List<CStructMember> members);
Option<CDefinable> retainDefinables_Main(void* _ref, CStructMember member);
List<char*> splitValues_Main(void* _ref, char* input);
char* computeMethodBody_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration, List<CDeclaration> cParameters, Option<char*> maybeContent, char* structName, List<char*> structureVariants);
char* createBodyForAbstractMethod_Main(void* _ref, List<char*> variants, CType type, char* name, List<char*> parameterNames);
CDefinable transformMethodDeclaration_Main(void* _ref, char* structName, List<char*> typeParameters, JMethodDeclaration methodDeclaration);
CDefinable convertToFunctionDeclarations_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration);
CType toConstructorReturnType_Main(void* _ref, char* base, List<char*> typeParameters);
char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent);
char* generateCase_Main(void* _ref, char* variant, char* name, List<char*> parameterNames);
JMethodDeclaration parseMethodDeclaration_Main(void* _ref, char* declaration, char* structName);
JMethodDeclaration toInterface_Main(void* _ref, JDeclaration value);
Option<JMethodDeclaration> parseConstructor_Main(void* _ref, char* declaration, char* structName);
Option<JObjectMember> parseEnumValuesStatement_Main(void* _ref, char* input, char* structName);
Option<JObjectMember> parseEnumValues_Main(void* _ref, char* structName, char* input);
Option<CStructMember> compileEnumValue_Main(void* _ref, char* structName, char* input);
char* compileMethodSegment_Main(void* _ref, char* input, int indent);
Option<char*> compileConditional_Main(void* _ref, char* type, int indent, char* input);
char* compileMethodStatement_Main(void* _ref, char* input);
Option<char*> compileAssignment_Main(void* _ref, char* stripped);
CAssignable transformAssignable_Main(void* _ref, JAssignable assignable, JExpression source);
JType resolveType_Main(void* _ref, JExpression source, JType type);
JType resolveExpression_Main(void* _ref, JExpression source);
JType resolveUncleanedExpression_Main(void* _ref, JExpression source);
JType resolveIdentifier_Main(void* _ref, char* value);
JType cleanupType_Main(void* _ref, JType found);
JType resolveMember_Main(void* _ref, JObjectType type, JType instanceType, char* name);
JType resolveCaller_Main(void* _ref, JCaller caller);
JAssignable parseAssignable_Main(void* _ref, char* input);
Option<char*> post_Main(void* _ref, char* stripped, char* slice);
char* compileExpressionOrPlaceholder_Main(void* _ref, char* input);
Option<CExpression> parseCExpression_Main(void* _ref, char* input);
Option<JExpression> parseExpression_Main(void* _ref, char* input);
Option<JExpression> compileLambda_Main(void* _ref, char* input);
Option<List<char*>> parseLambdaParams_Main(void* _ref, char* input);
char* generateName_Main(void* _ref);
Option<JExpression> compileOperator_Main(void* _ref, char* input, Operator operator);
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
	return { CTypeTag::CPrimitiveTypeVariant, data };
}
CPrimitiveType new_CPrimitiveType(char* content){
	CPrimitiveType _thisInstance;
	CPrimitiveType* _this = &_thisInstance;
	_this->content = content;
	return _thisInstance;
}
char* generate_CPrimitiveType(void* _ref){
	CPrimitiveType* _this = (CPrimitiveType*) _ref;
	return _this->content;
}
char* toBaseName_CPrimitiveType(void* _ref){
	CPrimitiveType* _this = (CPrimitiveType*) _ref;
	return _this->content;
}
List<char*> extractIdentifiers_CPrimitiveType(void* _ref){
	CPrimitiveType* _this = (CPrimitiveType*) _ref;
	return empty_Lists(&(Lists));
}
JType toJType_JPrimitiveType(void* _ref){
	JPrimitiveType _this = *((JPrimitiveType*) _ref);
	JTypeData data;
	data.JPrimitiveType = _this;
	return { JTypeTag::JPrimitiveTypeVariant, data };
}
JPrimitiveType new_JPrimitiveType(char* name){
	JPrimitiveType _thisInstance;
	JPrimitiveType* _this = &_thisInstance;
	_this->name = name;
	return _thisInstance;
}
char* stringify_JPrimitiveType(void* _ref){
	JPrimitiveType* _this = (JPrimitiveType*) _ref;
	return _this->name;
}
Operator new_Operator(char* value, JType type){
	Operator _thisInstance;
	Operator* _this = &_thisInstance;
	_this->value = value;
	_this->type = type;
	return _thisInstance;
}
char* generate_Operator(void* _ref){
	Operator* _this = (Operator*) _ref;
	return _this->value;
}
JType getType_Operator(void* _ref){
	Operator* _this = (Operator*) _ref;
	return _this->type;
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
List<T> mapLast_List(void* _ref, F1R<T, T> mapper){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.mapLast(_this->data, mapper);
}
template <typename T>
Iter<T> iterReversed_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.iterReversed(_this->data);
}
template <typename T>
List<T> copy_List(void* _ref){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.copy(_this->data);
}
template <typename T>
List<T> removeElement_List(void* _ref, T element){
	List<T>* _this = (List<T>*) _ref;
	return _this->table.removeElement(_this->data, element);
}
Option<IOError> writeString_Path(void* _ref, char* output){
	Path* _this = (Path*) _ref;
	return _this->table.writeString(_this->data, output);
}
Result<char*, IOError> readString_Path(void* _ref){
	Path* _this = (Path*) _ref;
	return _this->table.readString(_this->data);
}
Option<Path> getParent_Path(void* _ref){
	Path* _this = (Path*) _ref;
	return _this->table.getParent(_this->data);
}
Option<IOError> createDirectories_Path(void* _ref){
	Path* _this = (Path*) _ref;
	return _this->table.createDirectories(_this->data);
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
			_ret = map_None(&(_this->data.None), mapper);
			break;
		case SomeVariant:
			_ret = map_Some(&(_this->data.Some), mapper);
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
			_ret = orElse_None(&(_this->data.None), other);
			break;
		case SomeVariant:
			_ret = orElse_Some(&(_this->data.Some), other);
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
			_ret = flatMap_None(&(_this->data.None), mapper);
			break;
		case SomeVariant:
			_ret = flatMap_Some(&(_this->data.Some), mapper);
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
			_ret = orElseGet_None(&(_this->data.None), other);
			break;
		case SomeVariant:
			_ret = orElseGet_Some(&(_this->data.Some), other);
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
			_ret = or_None(&(_this->data.None), other);
			break;
		case SomeVariant:
			_ret = or_Some(&(_this->data.Some), other);
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
			_ret = toTuple_None(&(_this->data.None), other);
			break;
		case SomeVariant:
			_ret = toTuple_Some(&(_this->data.Some), other);
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
			_ret = mapValue_Err(&(_this->data.Err), mapper);
			break;
		case OkVariant:
			_ret = mapValue_Ok(&(_this->data.Ok), mapper);
			break;
	}
	return _ret;
}
char* getName_CNamedType(void* _ref){
	CNamedType* _this = (CNamedType*) _ref;
	char* _ret;
	switch (_this->variant) {
		case CTemplateTypeVariant:
			_ret = getName_CTemplateType(&(_this->data.CTemplateType));
			break;
		case IdentifierVariant:
			_ret = getName_Identifier(&(_this->data.Identifier));
			break;
	}
	return _ret;
}
char* generate_CType(void* _ref){
	CType* _this = (CType*) _ref;
	return _this->table.generate(_this->data);
}
char* toBaseName_CType(void* _ref){
	CType* _this = (CType*) _ref;
	return _this->table.toBaseName(_this->data);
}
List<char*> extractIdentifiers_CType(void* _ref){
	CType* _this = (CType*) _ref;
	return _this->table.extractIdentifiers(_this->data);
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
		case CFunctionDeclarationVariant:
			_ret = generate_CFunctionDeclaration(&(_this->data.CFunctionDeclaration));
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
char* generate_CAssignable(void* _ref){
	CAssignable* _this = (CAssignable*) _ref;
	return _this->table.generate(_this->data);
}
char* stringify_JType(void* _ref){
	JType* _this = (JType*) _ref;
	return _this->table.stringify(_this->data);
}
char* generate_CDefinable(void* _ref){
	CDefinable* _this = (CDefinable*) _ref;
	char* _ret;
	switch (_this->variant) {
		case CDeclarationVariant:
			_ret = generate_CDeclaration(&(_this->data.CDeclaration));
			break;
		case CFunctionDeclarationVariant:
			_ret = generate_CFunctionDeclaration(&(_this->data.CFunctionDeclaration));
			break;
		case PlaceholderVariant:
			_ret = generate_Placeholder(&(_this->data.Placeholder));
			break;
	}
	return _ret;
}
List<char*> extractIdentifiers_CDefinable(void* _ref){
	CDefinable* _this = (CDefinable*) _ref;
	List<char*> _ret;
	switch (_this->variant) {
		case CDeclarationVariant:
			_ret = extractIdentifiers_CDeclaration(&(_this->data.CDeclaration));
			break;
		case CFunctionDeclarationVariant:
			_ret = extractIdentifiers_CFunctionDeclaration(&(_this->data.CFunctionDeclaration));
			break;
		case PlaceholderVariant:
			_ret = extractIdentifiers_Placeholder(&(_this->data.Placeholder));
			break;
	}
	return _ret;
}
CDefinable mapTypeParameters_CDefinable(void* _ref, F1R<List<char*>, List<char*>> mapper){
	CDefinable* _this = (CDefinable*) _ref;
	CDefinable _ret;
	switch (_this->variant) {
		case CDeclarationVariant:
			_ret = mapTypeParameters_CDeclaration(&(_this->data.CDeclaration), mapper);
			break;
		case CFunctionDeclarationVariant:
			_ret = mapTypeParameters_CFunctionDeclaration(&(_this->data.CFunctionDeclaration), mapper);
			break;
		case PlaceholderVariant:
			_ret = mapTypeParameters_Placeholder(&(_this->data.Placeholder), mapper);
			break;
	}
	return _ret;
}
CDefinable mapName_CDefinable(void* _ref, F1R<char*, char*> mapper){
	CDefinable* _this = (CDefinable*) _ref;
	CDefinable _ret;
	switch (_this->variant) {
		case CDeclarationVariant:
			_ret = mapName_CDeclaration(&(_this->data.CDeclaration), mapper);
			break;
		case CFunctionDeclarationVariant:
			_ret = mapName_CFunctionDeclaration(&(_this->data.CFunctionDeclaration), mapper);
			break;
		case PlaceholderVariant:
			_ret = mapName_Placeholder(&(_this->data.Placeholder), mapper);
			break;
	}
	return _ret;
}
char* generate_CStructureOrUnion(void* _ref){
	CStructureOrUnion* _this = (CStructureOrUnion*) _ref;
	char* _ret;
	switch (_this->variant) {
		case CStructureVariant:
			_ret = generate_CStructure(&(_this->data.CStructure));
			break;
		case CUnionVariant:
			_ret = generate_CUnion(&(_this->data.CUnion));
			break;
	}
	return _ret;
}
List<char*> findDependencies_CStructureOrUnion(void* _ref){
	CStructureOrUnion* _this = (CStructureOrUnion*) _ref;
	List<char*> _ret;
	switch (_this->variant) {
		case CStructureVariant:
			_ret = findDependencies_CStructure(&(_this->data.CStructure));
			break;
		case CUnionVariant:
			_ret = findDependencies_CUnion(&(_this->data.CUnion));
			break;
	}
	return _ret;
}
char* findName_CStructureOrUnion(void* _ref){
	CStructureOrUnion* _this = (CStructureOrUnion*) _ref;
	char* _ret;
	switch (_this->variant) {
		case CStructureVariant:
			_ret = findName_CStructure(&(_this->data.CStructure));
			break;
		case CUnionVariant:
			_ret = findName_CUnion(&(_this->data.CUnion));
			break;
	}
	return _ret;
}
template <typename T>
void apply_F1(void* _ref, T element){
	F1<T>* _this = (F1<T>*) _ref;
	return _this->table.apply(_this->data, element);
}
StringBuilder empty_StringBuilders(void* _ref){
	StringBuilders* _this = (StringBuilders*) _ref;
	return new_StringBuilder(empty_Lists(&(Lists)));
}
StringBuilder appendChar_StringBuilder(void* _ref, char next){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder(addLast_List(&(_this->list), next));
}
StringBuilder clear_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder(clear_List(&(_this->list)));
}
char* toString_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return collect_Iter(&(map_Iter(&(iter_List(&(_this->list))), /*Convert method references to a closure*/)), new_Joiner());
}
StringBuilder new_StringBuilder(List<char> list){
	StringBuilder _thisInstance;
	StringBuilder* _this = &_thisInstance;
	_this->list = list;
	return _thisInstance;
}
template <typename T, typename T>
Iter<T> of_Iter(void* _ref, T value){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter(new_SingleHead(value));
}
template <typename T, typename T>
Iter<T> empty_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter(new_EmptyHead());
}
template <typename R, typename T>
Iter<R> map_Iter(void* _ref, F1R<T, R> mapper){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter(new_MapHead(_this->head, mapper));
}
/*TODO:  resolve lambda return type*/ lambda0(void* _ref, /*TODO: resolve type of lambda param*/ element){
	return apply_F2R(&(folder), finalCurrent, element);
}
/*TODO:  resolve lambda return type*/ lambda1(void* _ref){
	return finalCurrent;
}
template <typename R, typename T>
R fold_Iter(void* _ref, R initial, F2R<R, T, R> folder){
	Iter<T>* _this = (Iter<T>*) _ref;
	R current = initial;
	while (true) {
		R finalCurrent = current;
		Tuple tuple = toTuple_Option(&(map_Option(&(next_Head(&(_this->head))), lambda0)), lambda1);
		if (tuple.left) 
			current = tuple.right;
		else 
			return current;
	}
}
template <typename C, typename T>
C collect_Iter(void* _ref, Collector<T, C> collector){
	Iter<T>* _this = (Iter<T>*) _ref;
	return fold_Iter(&((*_this)), createInitial_Collector(&(collector)), /*Convert method references to a closure*/);
}
template <typename T>
List<T> toList_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return collect_Iter(&((*_this)), new_ListCollector());
}
/*TODO:  resolve lambda return type*/ lambda2(void* _ref, /*TODO: resolve type of lambda param*/ element){
	if (apply_F1R(&(predicate), element)) 
		return new_Iter(new_SingleHead(element));
	return new_Iter(new_EmptyHead());
}
template <typename T>
Iter<T> filter_Iter(void* _ref, F1R<T, int> predicate){
	Iter<T>* _this = (Iter<T>*) _ref;
	return flatMap_Iter(&((*_this)), lambda2);
}
template <typename R, typename T>
Iter<R> flatMap_Iter(void* _ref, F1R<T, Iter<R>> mapper){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter(new_FlatMapHead(_this->head, mapper));
}
template <typename T>
Option<T> next_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return next_Head(&(_this->head));
}
template <typename T>
void forEach_Iter(void* _ref, F1<T> consumer){
	Iter<T>* _this = (Iter<T>*) _ref;
	while (true) {
		Option next = next_Head(&(_this->head));
		if (/*instance*/) 
			apply_F1(&(consumer), next0);
		else 
			break;
	}
}
template <typename T>
Iter<T> new_Iter(Head<T> head){
	Iter<T> _thisInstance;
	Iter<T>* _this = &_thisInstance;
	_this->head = head;
	return _thisInstance;
}
Head<int> toHead_RangeHead(void* _ref){
	RangeHead _this = *((RangeHead*) _ref);
	HeadData data;
	data.RangeHead = _this;
	return { HeadTag::RangeHeadVariant, data };
}
RangeHead new_RangeHead(int length){
	RangeHead _thisInstance;
	RangeHead* _this = &_thisInstance;
	_this->length = length;
	_this->counter = 0;
	return _thisInstance;
}
Option<int> next_RangeHead(void* _ref){
	RangeHead* _this = (RangeHead*) _ref;
	if (_this->counter < (*_this).length) {
		int value = _this->counter;
		_this->counter++;
		return new_Some(value);
	}
	else 
		return new_None();
}
template <typename T>
List<T> of_Lists(void* _ref, /*T...*/ elements){
	Lists* _this = (Lists*) _ref;
	return collect_Iter(&(fromObjArray_Streams(&(Streams), elements)), new_ListCollector());
}
template <typename T, typename X>
Result<T, X> toResult_Err(void* _ref){
	Err<T, X> _this = *((Err<T, X>*) _ref);
	ResultData<T, X> data;
	data.Err = _this;
	return { ResultTag::ErrVariant, data };
}
template <typename R, typename T, typename X>
Result<R, X> mapValue_Err(void* _ref, F1R<T, R> mapper){
	Err<T, X>* _this = (Err<T, X>*) _ref;
	return new_Err(_this->error);
}
template <typename T, typename X>
Err<T, X> new_Err(X error){
	Err<T, X> _thisInstance;
	Err<T, X>* _this = &_thisInstance;
	_this->error = error;
	return _thisInstance;
}
template <typename T, typename X>
Result<T, X> toResult_Ok(void* _ref){
	Ok<T, X> _this = *((Ok<T, X>*) _ref);
	ResultData<T, X> data;
	data.Ok = _this;
	return { ResultTag::OkVariant, data };
}
template <typename R, typename T, typename X>
Result<R, X> mapValue_Ok(void* _ref, F1R<T, R> mapper){
	Ok<T, X>* _this = (Ok<T, X>*) _ref;
	return new_Ok(apply_F1R(&(mapper), _this->value));
}
template <typename T, typename X>
Ok<T, X> new_Ok(T value){
	Ok<T, X> _thisInstance;
	Ok<T, X>* _this = &_thisInstance;
	_this->value = value;
	return _thisInstance;
}
template <typename A, typename B>
Tuple<A, B> new_Tuple(A left, B right){
	Tuple<A, B> _thisInstance;
	Tuple<A, B>* _this = &_thisInstance;
	_this->left = left;
	_this->right = right;
	return _thisInstance;
}
State new_State(char* input){
	State _thisInstance;
	State* _this = &_thisInstance;
	_this->input = input;
	_this->index = 0;
	_this->buffer = empty_StringBuilders(&(StringBuilders));
	_this->depth = 0;
	_this->segments = empty_Lists(&(Lists));
	return _thisInstance;
}
int isShallow_State(void* _ref){
	State* _this = (State*) _ref;
	return /*this.depth == 1*/;
}
int isLevel_State(void* _ref){
	State* _this = (State*) _ref;
	return /*this.depth == 0*/;
}
State append_State(void* _ref, char next){
	State* _this = (State*) _ref;
	_this->buffer = appendChar_StringBuilder(&(_this->buffer), next);
	return (*_this);
}
Option<char> pop_State(void* _ref){
	State* _this = (State*) _ref;
	if (length_/*Cannot access member 'input' in 'Boolean', not an object.*/(&(_this->index < (*_this).input))) {
		char value = charAt_String(&(_this->input), _this->index);
		_this->index++;
		return new_Some(value);
	}
	else 
		return new_None();
}
State advance_State(void* _ref){
	State* _this = (State*) _ref;
	_this->segments = addLast_List(&(_this->segments), toString_StringBuilder(&(_this->buffer)));
	_this->buffer = clear_StringBuilder(&(_this->buffer));
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
	return iter_List(&(_this->segments));
}
/*TODO:  resolve lambda return type*/ lambda3(void* _ref, /*TODO: resolve type of lambda param*/ popped){
	State appended = append_State(&((*_this)), popped);
	return new_Tuple(appended, popped);
}
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref){
	State* _this = (State*) _ref;
	return map_Option(&(pop_State(&((*_this)))), lambda3);
}
/*TODO:  resolve lambda return type*/ lambda4(void* _ref, /*TODO: resolve type of lambda param*/ tuple){
	return tuple.left;
}
Option<State> popAndAppendToOption_State(void* _ref){
	State* _this = (State*) _ref;
	return map_Option(&(popAndAppendToTuple_State(&((*_this)))), lambda4);
}
Option<char> peek_State(void* _ref){
	State* _this = (State*) _ref;
	if (length_/*Cannot access member 'input' in 'Boolean', not an object.*/(&(_this->index < (*_this).input))) 
		return new_Some(charAt_String(&(_this->input), _this->index));
	return new_None();
}
CType toCType_CPointerType(void* _ref){
	CPointerType _this = *((CPointerType*) _ref);
	CTypeData data;
	data.CPointerType = _this;
	return { CTypeTag::CPointerTypeVariant, data };
}
char* generate_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return generate_CType(&(_this->type)) + "*";
}
char* toBaseName_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return toBaseName_CType(&(_this->type)) + "_ptr";
}
List<char*> extractIdentifiers_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return extractIdentifiers_CType(&(_this->type));
}
CPointerType new_CPointerType(CType type){
	CPointerType _thisInstance;
	CPointerType* _this = &_thisInstance;
	_this->type = type;
	return _thisInstance;
}
CNamedType toCNamedType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CNamedTypeData data;
	data.CTemplateType = _this;
	return { CNamedTypeTag::CTemplateTypeVariant, data };
}
CTemplateType new_CTemplateType(char* base, List<CType> typeArguments){
	CTemplateType _thisInstance;
	CTemplateType* _this = &_thisInstance;
	_this->base = base;
	_this->typeArguments = typeArguments;
	/*assert !typeArguments.isEmpty()*/;
	return _thisInstance;
}
char* generate_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	C typeArguments = collect_Iter(&(map_Iter(&(iter_List(&(_this->typeArguments))), /*Convert method references to a closure*/)), new_Joiner(", "));
	return _this->base + "<" + typeArguments + ">";
}
char* toBaseName_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	return _this->base;
}
List<char*> extractIdentifiers_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	return addLast_List(&(toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(_this->typeArguments))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)))), _this->base);
}
char* getName_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	return _this->base;
}
CTemplateType new_CTemplateType(char* base, List<CType> typeArguments){
	CTemplateType _thisInstance;
	CTemplateType* _this = &_thisInstance;
	_this->base = base;
	_this->typeArguments = typeArguments;
	return _thisInstance;
}
CExpression toCExpression_CQuantity(void* _ref){
	CQuantity _this = *((CQuantity*) _ref);
	CExpressionData data;
	data.CQuantity = _this;
	return { CExpressionTag::CQuantityVariant, data };
}
char* generate_CQuantity(void* _ref){
	CQuantity* _this = (CQuantity*) _ref;
	return "(" + this.expression.generate() + ")";
}
CQuantity new_CQuantity(CExpression expression){
	CQuantity _thisInstance;
	CQuantity* _this = &_thisInstance;
	_this->expression = expression;
	return _thisInstance;
}
CExpression toCExpression_CDereference(void* _ref){
	CDereference _this = *((CDereference*) _ref);
	CExpressionData data;
	data.CDereference = _this;
	return { CExpressionTag::CDereferenceVariant, data };
}
char* generate_CDereference(void* _ref){
	CDereference* _this = (CDereference*) _ref;
	return generate_/*Cannot access member 'expression' in 'Int', not an object.*/(&("*" + (*_this).expression));
}
CDereference new_CDereference(CExpression expression){
	CDereference _thisInstance;
	CDereference* _this = &_thisInstance;
	_this->expression = expression;
	return _thisInstance;
}
CNamedType toCNamedType_Identifier(void* _ref){
	Identifier _this = *((Identifier*) _ref);
	CNamedTypeData data;
	data.Identifier = _this;
	return { CNamedTypeTag::IdentifierVariant, data };
}
JType toJType_Identifier(void* _ref){
	Identifier _this = *((Identifier*) _ref);
	JTypeData data;
	data.Identifier = _this;
	return { JTypeTag::IdentifierVariant, data };
}
JExpression toJExpression_Identifier(void* _ref){
	Identifier _this = *((Identifier*) _ref);
	JExpressionData data;
	data.Identifier = _this;
	return { JExpressionTag::IdentifierVariant, data };
}
CExpression toCExpression_Identifier(void* _ref){
	Identifier _this = *((Identifier*) _ref);
	CExpressionData data;
	data.Identifier = _this;
	return { CExpressionTag::IdentifierVariant, data };
}
/*TODO:  resolve lambda return type*/ lambda5(void* _ref, /*TODO: resolve type of lambda param*/ i){
	char c = charAt_String(&(stripped), i);
	return /*c == '_' || Character.isLetter(c) || (i != 0 && Character.isDigit(c))*/;
}
int isIdentifier_Identifier(void* _ref, char* input){
	Identifier* _this = (Identifier*) _ref;
	char* stripped = strip_String(&(input));
	if (equals_bool(&(isEmpty_String(&(stripped)) || stripped), "return")) 
		return false;
	return allMatch_/*Not a functional type: Placeholder[input=Cannot access member 'range' in 'Placeholder[input=Undefined identifier: IntStream]', not an object.]*/(&(range_/*Undefined identifier: IntStream*/(&(IntStream), 0, length_String(&(stripped)))), lambda5);
}
char* generate_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
char* toBaseName_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
List<char*> extractIdentifiers_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return of_Lists(&(Lists), _this->value);
}
char* stringify_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
char* getName_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
Identifier new_Identifier(char* value){
	Identifier _thisInstance;
	Identifier* _this = &_thisInstance;
	_this->value = value;
	return _thisInstance;
}
CType toCType_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CTypeData data;
	data.Placeholder = _this;
	return { CTypeTag::PlaceholderVariant, data };
}
JMethodDeclaration toJMethodDeclaration_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	JMethodDeclarationData data;
	data.Placeholder = _this;
	return { JMethodDeclarationTag::PlaceholderVariant, data };
}
CStructMember toCStructMember_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CStructMemberData data;
	data.Placeholder = _this;
	return { CStructMemberTag::PlaceholderVariant, data };
}
CAssignable toCAssignable_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CAssignableData data;
	data.Placeholder = _this;
	return { CAssignableTag::PlaceholderVariant, data };
}
JAssignable toJAssignable_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	JAssignableData data;
	data.Placeholder = _this;
	return { JAssignableTag::PlaceholderVariant, data };
}
JType toJType_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	JTypeData data;
	data.Placeholder = _this;
	return { JTypeTag::PlaceholderVariant, data };
}
JObjectMember toJObjectMember_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	JObjectMemberData data;
	data.Placeholder = _this;
	return { JObjectMemberTag::PlaceholderVariant, data };
}
CExpression toCExpression_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CExpressionData data;
	data.Placeholder = _this;
	return { CExpressionTag::PlaceholderVariant, data };
}
CDefinable toCDefinable_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CDefinableData data;
	data.Placeholder = _this;
	return { CDefinableTag::PlaceholderVariant, data };
}
JExpression toJExpression_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	JExpressionData data;
	data.Placeholder = _this;
	return { JExpressionTag::PlaceholderVariant, data };
}
char* wrap_Placeholder(void* _ref, char* input){
	Placeholder* _this = (Placeholder*) _ref;
	char* replaced = replace_String(&(replace_String(&(input), "/*", "start")), "*/", "end");
	return "/*" + replaced + "*/";
}
char* generate_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return wrap(_this->input);
}
char* toBaseName_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return wrap(_this->input);
}
List<char*> extractIdentifiers_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return empty_Lists(&(Lists));
}
CDefinable mapTypeParameters_Placeholder(void* _ref, F1R<List<char*>, List<char*>> mapper){
	Placeholder* _this = (Placeholder*) _ref;
	return (*_this);
}
CDefinable mapName_Placeholder(void* _ref, F1R<char*, char*> mapper){
	Placeholder* _this = (Placeholder*) _ref;
	return (*_this);
}
char* stringify_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return wrap(_this->input);
}
Placeholder new_Placeholder(char* input){
	Placeholder _thisInstance;
	Placeholder* _this = &_thisInstance;
	_this->input = input;
	return _thisInstance;
}
JMethodDeclaration toJMethodDeclaration_JConstructor(void* _ref){
	JConstructor _this = *((JConstructor*) _ref);
	JMethodDeclarationData data;
	data.JConstructor = _this;
	return { JMethodDeclarationTag::JConstructorVariant, data };
}
JConstructor new_JConstructor(char* type){
	JConstructor _thisInstance;
	JConstructor* _this = &_thisInstance;
	_this->type = type;
	return _thisInstance;
}
JMethodDeclaration toJMethodDeclaration_JDeclaration(void* _ref){
	JDeclaration _this = *((JDeclaration*) _ref);
	JMethodDeclarationData data;
	data.JDeclaration = _this;
	return { JMethodDeclarationTag::JDeclarationVariant, data };
}
JAssignable toJAssignable_JDeclaration(void* _ref){
	JDeclaration _this = *((JDeclaration*) _ref);
	JAssignableData data;
	data.JDeclaration = _this;
	return { JAssignableTag::JDeclarationVariant, data };
}
JDeclaration new_JDeclaration(char* name, JType type){
	JDeclaration _thisInstance;
	JDeclaration* _this = &_thisInstance;
	(*_this)(empty_Lists(&(Lists)), empty_Lists(&(Lists)), new_None(), type, name);
	return _thisInstance;
}
char* toString_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	char* annotationsString;
	if (isEmpty_List(&(_this->annotations))) 
		annotationsString = "";
	else 
		annotationsString = "annotations=" + _this->annotations + ", ";
	char* typeParametersString;
	if (isEmpty_List(&(_this->typeParameters))) 
		typeParametersString = "";
	else 
		typeParametersString = "typeParameters=" + _this->typeParameters + ", ";
	char* maybeBeforeTypeString;
	if (/*instance*/) 
		maybeBeforeTypeString = "maybeBeforeType=" + result + ", ";
	else 
		maybeBeforeTypeString = "";
	return "JDeclaration {" + annotationsString + typeParametersString + maybeBeforeTypeString + "type=" + _this->type + ", name='" + _this->name + ''\'' + '}'';
}
JDeclaration mapName_JDeclaration(void* _ref, F1R<char*, char*> mapper){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_JDeclaration(_this->annotations, _this->typeParameters, _this->maybeBeforeType, _this->type, apply_F1R(&(mapper), _this->name));
}
CDeclaration toCDeclaration_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_CDeclaration(_this->typeParameters, transformType(_this->type), _this->name);
}
CAssignable toCAssignable_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	return toCDeclaration_JDeclaration(&((*_this)));
}
JDeclaration withType_JDeclaration(void* _ref, JType type){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_JDeclaration(_this->annotations, _this->typeParameters, _this->maybeBeforeType, type, _this->name);
}
JDeclaration new_JDeclaration(List<char*> annotations, List<char*> typeParameters, Option<char*> maybeBeforeType, JType type, char* name){
	JDeclaration _thisInstance;
	JDeclaration* _this = &_thisInstance;
	_this->annotations = annotations;
	_this->typeParameters = typeParameters;
	_this->maybeBeforeType = maybeBeforeType;
	_this->type = type;
	_this->name = name;
	return _thisInstance;
}
CDefinable toCDefinable_CFunctionDeclaration(void* _ref){
	CFunctionDeclaration _this = *((CFunctionDeclaration*) _ref);
	CDefinableData data;
	data.CFunctionDeclaration = _this;
	return { CDefinableTag::CFunctionDeclarationVariant, data };
}
CStructMember toCStructMember_CFunctionDeclaration(void* _ref){
	CFunctionDeclaration _this = *((CFunctionDeclaration*) _ref);
	CStructMemberData data;
	data.CFunctionDeclaration = _this;
	return { CStructMemberTag::CFunctionDeclarationVariant, data };
}
char* generate_CFunctionDeclaration(void* _ref){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	char* joinedParameterTypes = "(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", ")) + ")";
	return generate_CType(&(_this->type)) + " (*" + this.name + ")" + joinedParameterTypes;
}
List<char*> extractIdentifiers_CFunctionDeclaration(void* _ref){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return addAllLast_/*Not a functional type: Placeholder[input=Cannot access member 'extractIdentifiers' in 'Identifier[value=CType]', not an object.]*/(&(extractIdentifiers_CType(&(_this->type))), toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(_this->parameterTypes))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/))));
}
CDefinable mapTypeParameters_CFunctionDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return (*_this);
}
CDefinable mapName_CFunctionDeclaration(void* _ref, F1R<char*, char*> mapper){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return new_CFunctionDeclaration(_this->type, apply_F1R(&(mapper), _this->name), _this->parameterTypes);
}
CFunctionDeclaration new_CFunctionDeclaration(CType type, char* name, List<CType> parameterTypes){
	CFunctionDeclaration _thisInstance;
	CFunctionDeclaration* _this = &_thisInstance;
	_this->type = type;
	_this->name = name;
	_this->parameterTypes = parameterTypes;
	return _thisInstance;
}
CStructMember toCStructMember_EmptyStructMember(void* _ref){
	EmptyStructMember _this = *((EmptyStructMember*) _ref);
	CStructMemberData data;
	data.EmptyStructMember = _this;
	return { CStructMemberTag::EmptyStructMemberVariant, data };
}
JObjectMember toJObjectMember_EmptyStructMember(void* _ref){
	EmptyStructMember _this = *((EmptyStructMember*) _ref);
	JObjectMemberData data;
	data.EmptyStructMember = _this;
	return { JObjectMemberTag::EmptyStructMemberVariant, data };
}
char* generate_EmptyStructMember(void* _ref){
	EmptyStructMember* _this = (EmptyStructMember*) _ref;
	return "";
}
Folder toFolder_EscapedFolder(void* _ref){
	EscapedFolder _this = *((EscapedFolder*) _ref);
	FolderData data;
	data.EscapedFolder = _this;
	return { FolderTag::EscapedFolderVariant, data };
}
/*TODO:  resolve lambda return type*/ lambda6(void* _ref, /*TODO: resolve type of lambda param*/ tuple){
	if (/*tuple.right == '\\'*/) 
		return orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Cannot access member 'left' in 'Placeholder[input=Undefined identifier: tuple]', not an object.]', not an object.]*/(&(popAndAppendToOption_/*Cannot access member 'left' in 'Placeholder[input=Undefined identifier: tuple]', not an object.*/(&(tuple.left))), tuple.left);
	return tuple.left;
}
State apply_EscapedFolder(void* _ref, State state, char next){
	EscapedFolder* _this = (EscapedFolder*) _ref;
	if (/*next == '\''*/) {
		/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = append_State(&(state), next);
		return orElse_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(&(popAndAppendToTuple_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended))), lambda6)), /*Convert method references to a closure*/)), appended);
	}
	if (/*next == '\"'*/) {
		/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ current = append_State(&(state), next);
		while (true) {
			/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/ maybeTuple = popAndAppendToTuple_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(current));
			if (/*!(maybeTuple instanceof Some<Tuple<State, Character>>(var value))*/) 
				break;
			current = value.left;
			/*Cannot access member 'right' in 'Placeholder[input=Undefined identifier: value]', not an object.*/ right = value.right;
			if (/*right == '\\'*/) 
				current = orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(&(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(current))), current);
			if (/*right == '\"'*/) 
				break;
		}
		return current;
	}
	return apply_Folder(&(_this->folder), state, next);
}
EscapedFolder new_EscapedFolder(Folder folder){
	EscapedFolder _thisInstance;
	EscapedFolder* _this = &_thisInstance;
	_this->folder = folder;
	return _thisInstance;
}
Folder toFolder_ValueFolder(void* _ref){
	ValueFolder _this = *((ValueFolder*) _ref);
	FolderData data;
	data.ValueFolder = _this;
	return { FolderTag::ValueFolderVariant, data };
}
State apply_ValueFolder(void* _ref, State state, char next){
	ValueFolder* _this = (ValueFolder*) _ref;
	if (/*next == ',' && state.isLevel()*/) 
		return advance_State(&(state));
	/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = append_State(&(state), next);
	if (/*next == '-'*/) {
		/*Not a functional type: Placeholder[input=Cannot access member 'peek' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/ peeked = peek_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended));
		if (/*instance*/) 
			return orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(&(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended))), appended);
		else 
			return appended;
	}
	if (/*next == '<' || next == '('*/) 
		return enter_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended));
	if (/*next == '>' || next == ')'*/) 
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended));
	return appended;
}
template <typename T>
Option<T> toOption_Some(void* _ref){
	Some<T> _this = *((Some<T>*) _ref);
	OptionData<T> data;
	data.Some = _this;
	return { OptionTag::SomeVariant, data };
}
template <typename R, typename T>
Option<R> map_Some(void* _ref, F1R<T, R> mapper){
	Some<T>* _this = (Some<T>*) _ref;
	return new_Some(apply_F1R(&(mapper), _this->value));
}
template <typename T>
T orElse_Some(void* _ref, T other){
	Some<T>* _this = (Some<T>*) _ref;
	return _this->value;
}
template <typename R, typename T>
Option<R> flatMap_Some(void* _ref, F1R<T, Option<R>> mapper){
	Some<T>* _this = (Some<T>*) _ref;
	return apply_F1R(&(mapper), _this->value);
}
template <typename T>
T orElseGet_Some(void* _ref, FR<T> other){
	Some<T>* _this = (Some<T>*) _ref;
	return _this->value;
}
template <typename T>
Iter<T> iter_Some(void* _ref){
	Some<T>* _this = (Some<T>*) _ref;
	return of_Iter(&(Iter), _this->value);
}
template <typename T>
Option<T> or_Some(void* _ref, FR<Option<T>> other){
	Some<T>* _this = (Some<T>*) _ref;
	return (*_this);
}
template <typename T>
Tuple<int, T> toTuple_Some(void* _ref, FR<T> other){
	Some<T>* _this = (Some<T>*) _ref;
	return new_Tuple(true, _this->value);
}
template <typename T>
Some<T> new_Some(T value){
	Some<T> _thisInstance;
	Some<T>* _this = &_thisInstance;
	_this->value = value;
	return _thisInstance;
}
template <typename T>
Option<T> toOption_None(void* _ref){
	None<T> _this = *((None<T>*) _ref);
	OptionData<T> data;
	data.None = _this;
	return { OptionTag::NoneVariant, data };
}
template <typename R, typename T>
Option<R> map_None(void* _ref, F1R<T, R> mapper){
	None<T>* _this = (None<T>*) _ref;
	return new_None();
}
template <typename T>
T orElse_None(void* _ref, T other){
	None<T>* _this = (None<T>*) _ref;
	return other;
}
template <typename R, typename T>
Option<R> flatMap_None(void* _ref, F1R<T, Option<R>> mapper){
	None<T>* _this = (None<T>*) _ref;
	return new_None();
}
template <typename T>
T orElseGet_None(void* _ref, FR<T> other){
	None<T>* _this = (None<T>*) _ref;
	return apply_FR(&(other));
}
template <typename T>
Iter<T> iter_None(void* _ref){
	None<T>* _this = (None<T>*) _ref;
	return empty_Iter(&(Iter));
}
template <typename T>
Option<T> or_None(void* _ref, FR<Option<T>> other){
	None<T>* _this = (None<T>*) _ref;
	return apply_FR(&(other));
}
template <typename T>
Tuple<int, T> toTuple_None(void* _ref, FR<T> other){
	None<T>* _this = (None<T>*) _ref;
	return new_Tuple(false, apply_FR(&(other)));
}
Folder toFolder_ConditionEndLocator(void* _ref){
	ConditionEndLocator _this = *((ConditionEndLocator*) _ref);
	FolderData data;
	data.ConditionEndLocator = _this;
	return { FolderTag::ConditionEndLocatorVariant, data };
}
State apply_ConditionEndLocator(void* _ref, State state, char c){
	ConditionEndLocator* _this = (ConditionEndLocator*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = append_State(&(state), c);
	if (/*c == '('*/) 
		return enter_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended));
	if (/*c == ')'*/) {
		if (isLevel_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended))) 
			return advance_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended));
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended));
	}
	return appended;
}
CStructMember toCStructMember_CField(void* _ref){
	CField _this = *((CField*) _ref);
	CStructMemberData data;
	data.CField = _this;
	return { CStructMemberTag::CFieldVariant, data };
}
char* generate_CField(void* _ref){
	CField* _this = (CField*) _ref;
	return generateStatement_/*Undefined identifier: Main*/(&(Main), 1, generate_CDefinable(&(_this->declaration)));
}
CField new_CField(CDefinable declaration){
	CField _thisInstance;
	CField* _this = &_thisInstance;
	_this->declaration = declaration;
	return _thisInstance;
}
/*TODO:  resolve lambda return type*/ lambda7(void* _ref, /*TODO: resolve type of lambda param*/ index){
	return /*elements[index]*/;
}
template <typename T>
Iter<T> fromObjArray_Streams(void* _ref, T* elements){
	Streams* _this = (Streams*) _ref;
	return map_Iter(&(new_Iter(new_RangeHead(elements.length))), lambda7);
}
template <typename T, typename R>
Head<R> toHead_MapHead(void* _ref){
	MapHead<T, R> _this = *((MapHead<T, R>*) _ref);
	HeadData<T, R> data;
	data.MapHead = _this;
	return { HeadTag::MapHeadVariant, data };
}
template <typename T, typename R>
Option<R> next_MapHead(void* _ref){
	MapHead<T, R>* _this = (MapHead<T, R>*) _ref;
	return map_Option(&(next_Head(&(_this->head))), _this->mapper);
}
template <typename T, typename R>
MapHead<T, R> new_MapHead(Head<T> head, F1R<T, R> mapper){
	MapHead<T, R> _thisInstance;
	MapHead<T, R>* _this = &_thisInstance;
	_this->head = head;
	_this->mapper = mapper;
	return _thisInstance;
}
template <typename T>
Head<T> toHead_SingleHead(void* _ref){
	SingleHead<T> _this = *((SingleHead<T>*) _ref);
	HeadData<T> data;
	data.SingleHead = _this;
	return { HeadTag::SingleHeadVariant, data };
}
template <typename T>
SingleHead<T> new_SingleHead(T value){
	SingleHead _thisInstance;
	SingleHead* _this = &_thisInstance;
	_this->value = value;
	_this->retrieved = false;
	return _thisInstance;
}
template <typename T>
Option<T> next_SingleHead(void* _ref){
	SingleHead<T>* _this = (SingleHead<T>*) _ref;
	if (_this->retrieved) 
		return new_None();
	_this->retrieved = true;
	return new_Some(_this->value);
}
template <typename T, typename R>
Head<R> toHead_FlatMapHead(void* _ref){
	FlatMapHead<T, R> _this = *((FlatMapHead<T, R>*) _ref);
	HeadData<T, R> data;
	data.FlatMapHead = _this;
	return { HeadTag::FlatMapHeadVariant, data };
}
template <typename T, typename R>
FlatMapHead<T, R> new_FlatMapHead(Head<T> head, F1R<T, Iter<R>> mapper){
	FlatMapHead _thisInstance;
	FlatMapHead* _this = &_thisInstance;
	_this->head = head;
	_this->mapper = mapper;
	_this->maybeCurrent = new_None();
	return _thisInstance;
}
template <typename T, typename R>
Option<R> next_FlatMapHead(void* _ref){
	FlatMapHead<T, R>* _this = (FlatMapHead<T, R>*) _ref;
	while (true) {
		if (/*instance*/) {
			/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Cannot access member 'head' in 'Placeholder[input=Undefined identifier: current]', not an object.]', not an object.]*/ next = next_/*Cannot access member 'head' in 'Placeholder[input=Undefined identifier: current]', not an object.*/(&(current.head));
			if (/*instance*/) 
				return next;
		}
		Option maybeNext = next_Head(&(_this->head));
		if (/*instance*/) 
			return new_None();
		_this->maybeCurrent = map_Option(&(maybeNext), _this->mapper);
	}
}
template <typename T>
Head<T> toHead_EmptyHead(void* _ref){
	EmptyHead<T> _this = *((EmptyHead<T>*) _ref);
	HeadData<T> data;
	data.EmptyHead = _this;
	return { HeadTag::EmptyHeadVariant, data };
}
template <typename T>
Option<T> next_EmptyHead(void* _ref){
	EmptyHead<T>* _this = (EmptyHead<T>*) _ref;
	return new_None();
}
template <typename T>
Collector<T, int> toCollector_AnyMatch(void* _ref){
	AnyMatch<T> _this = *((AnyMatch<T>*) _ref);
	CollectorData<T> data;
	data.AnyMatch = _this;
	return { CollectorTag::AnyMatchVariant, data };
}
template <typename T>
int createInitial_AnyMatch(void* _ref){
	AnyMatch<T>* _this = (AnyMatch<T>*) _ref;
	return false;
}
template <typename T>
int fold_AnyMatch(void* _ref, int aBoolean, T t){
	AnyMatch<T>* _this = (AnyMatch<T>*) _ref;
	return apply_/*Cannot access member 'predicate' in 'Boolean', not an object.*/(&(aBoolean || (*_this).predicate), t);
}
template <typename T>
AnyMatch<T> new_AnyMatch(F1R<T, int> predicate){
	AnyMatch<T> _thisInstance;
	AnyMatch<T>* _this = &_thisInstance;
	_this->predicate = predicate;
	return _thisInstance;
}
Collector<char*, char*> toCollector_Joiner(void* _ref){
	Joiner _this = *((Joiner*) _ref);
	CollectorData data;
	data.Joiner = _this;
	return { CollectorTag::JoinerVariant, data };
}
Joiner new_Joiner(){
	Joiner _thisInstance;
	Joiner* _this = &_thisInstance;
	(*_this)("");
	return _thisInstance;
}
char* createInitial_Joiner(void* _ref){
	Joiner* _this = (Joiner*) _ref;
	return "";
}
char* fold_Joiner(void* _ref, char* current, char* element){
	Joiner* _this = (Joiner*) _ref;
	if (isEmpty_String(&(current))) 
		return element;
	return current + _this->delimiter + element;
}
Joiner new_Joiner(char* delimiter){
	Joiner _thisInstance;
	Joiner* _this = &_thisInstance;
	_this->delimiter = delimiter;
	return _thisInstance;
}
template <typename T>
Collector<T, List<T>> toCollector_ListCollector(void* _ref){
	ListCollector<T> _this = *((ListCollector<T>*) _ref);
	CollectorData<T> data;
	data.ListCollector = _this;
	return { CollectorTag::ListCollectorVariant, data };
}
template <typename T>
List<T> createInitial_ListCollector(void* _ref){
	ListCollector<T>* _this = (ListCollector<T>*) _ref;
	return empty_Lists(&(Lists));
}
template <typename T>
List<T> fold_ListCollector(void* _ref, List<T> tList, T t){
	ListCollector<T>* _this = (ListCollector<T>*) _ref;
	return addLast_List(&(tList), t);
}
CAssignable toCAssignable_CDeclaration(void* _ref){
	CDeclaration _this = *((CDeclaration*) _ref);
	CAssignableData data;
	data.CDeclaration = _this;
	return { CAssignableTag::CDeclarationVariant, data };
}
CDefinable toCDefinable_CDeclaration(void* _ref){
	CDeclaration _this = *((CDeclaration*) _ref);
	CDefinableData data;
	data.CDeclaration = _this;
	return { CDefinableTag::CDeclarationVariant, data };
}
CDeclaration new_CDeclaration(CType type, char* name){
	CDeclaration _thisInstance;
	CDeclaration* _this = &_thisInstance;
	(*_this)(empty_Lists(&(Lists)), type, name);
	return _thisInstance;
}
CDefinable mapName_CDeclaration(void* _ref, F1R<char*, char*> mapper){
	CDeclaration* _this = (CDeclaration*) _ref;
	return new_CDeclaration(_this->typeParameters, _this->type, apply_F1R(&(mapper), _this->name));
}
CDefinable mapTypeParameters_CDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper){
	CDeclaration* _this = (CDeclaration*) _ref;
	return new_CDeclaration(apply_F1R(&(mapper), _this->typeParameters), _this->type, _this->name);
}
char* generate_CDeclaration(void* _ref){
	CDeclaration* _this = (CDeclaration*) _ref;
	char* template = generateTemplateString(_this->typeParameters);
	return template + generate_CType(&(_this->type)) + " " + (*_this).name;
}
List<char*> extractIdentifiers_CDeclaration(void* _ref){
	CDeclaration* _this = (CDeclaration*) _ref;
	return extractIdentifiers_CType(&(_this->type));
}
CDeclaration new_CDeclaration(List<char*> typeParameters, CType type, char* name){
	CDeclaration _thisInstance;
	CDeclaration* _this = &_thisInstance;
	_this->typeParameters = typeParameters;
	_this->type = type;
	_this->name = name;
	return _thisInstance;
}
JType toJType_JArrayType(void* _ref){
	JArrayType _this = *((JArrayType*) _ref);
	JTypeData data;
	data.JArrayType = _this;
	return { JTypeTag::JArrayTypeVariant, data };
}
CType toCType_JArrayType(void* _ref){
	JArrayType* _this = (JArrayType*) _ref;
	return new_CPointerType(transformType(_this->type));
}
char* stringify_JArrayType(void* _ref){
	JArrayType* _this = (JArrayType*) _ref;
	return stringify_JType(&(_this->type)) + "_array";
}
JArrayType new_JArrayType(JType type){
	JArrayType _thisInstance;
	JArrayType* _this = &_thisInstance;
	_this->type = type;
	return _thisInstance;
}
JType toJType_JGenericType(void* _ref){
	JGenericType _this = *((JGenericType*) _ref);
	JTypeData data;
	data.JGenericType = _this;
	return { JTypeTag::JGenericTypeVariant, data };
}
CType toCType_JGenericType(void* _ref){
	JGenericType* _this = (JGenericType*) _ref;
	List newTypeArguments = toList_Iter(&(map_Iter(&(iter_List(&(_this->typeArguments))), /*Convert method references to a closure*/)));
	return new_CTemplateType(_this->base, newTypeArguments);
}
/*TODO:  resolve lambda return type*/ lambda8(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return "_" + slice;
}
char* stringify_JGenericType(void* _ref){
	JGenericType* _this = (JGenericType*) _ref;
	C joined = collect_Iter(&(map_Iter(&(map_Iter(&(iter_List(&(_this->typeArguments))), /*Convert method references to a closure*/)), lambda8)), new_Joiner());
	return _this->base + joined;
}
JGenericType new_JGenericType(char* base, List<JType> typeArguments){
	JGenericType _thisInstance;
	JGenericType* _this = &_thisInstance;
	_this->base = base;
	_this->typeArguments = typeArguments;
	return _thisInstance;
}
CExpression toCExpression_CPointerAccess(void* _ref){
	CPointerAccess _this = *((CPointerAccess*) _ref);
	CExpressionData data;
	data.CPointerAccess = _this;
	return { CExpressionTag::CPointerAccessVariant, data };
}
char* generate_CPointerAccess(void* _ref){
	CPointerAccess* _this = (CPointerAccess*) _ref;
	return generate_CExpression(&(_this->instance)) + "->" + (*_this).fieldName;
}
CPointerAccess new_CPointerAccess(CExpression instance, char* fieldName){
	CPointerAccess _thisInstance;
	CPointerAccess* _this = &_thisInstance;
	_this->instance = instance;
	_this->fieldName = fieldName;
	return _thisInstance;
}
CExpression toCExpression_CFieldAccess(void* _ref){
	CFieldAccess _this = *((CFieldAccess*) _ref);
	CExpressionData data;
	data.CFieldAccess = _this;
	return { CExpressionTag::CFieldAccessVariant, data };
}
char* generate_CFieldAccess(void* _ref){
	CFieldAccess* _this = (CFieldAccess*) _ref;
	return generate_CExpression(&(_this->instance)) + "." + (*_this).fieldName;
}
CFieldAccess new_CFieldAccess(CExpression instance, char* fieldName){
	CFieldAccess _thisInstance;
	CFieldAccess* _this = &_thisInstance;
	_this->instance = instance;
	_this->fieldName = fieldName;
	return _thisInstance;
}
JExpression toJExpression_JMemberAccess(void* _ref){
	JMemberAccess _this = *((JMemberAccess*) _ref);
	JExpressionData data;
	data.JMemberAccess = _this;
	return { JExpressionTag::JMemberAccessVariant, data };
}
JMemberAccess new_JMemberAccess(JExpression instance, char* memberName){
	JMemberAccess _thisInstance;
	JMemberAccess* _this = &_thisInstance;
	_this->instance = instance;
	_this->memberName = memberName;
	return _thisInstance;
}
JCaller toJCaller_JConstruction(void* _ref){
	JConstruction _this = *((JConstruction*) _ref);
	JCallerData data;
	data.JConstruction = _this;
	return { JCallerTag::JConstructionVariant, data };
}
JConstruction new_JConstruction(JType jType){
	JConstruction _thisInstance;
	JConstruction* _this = &_thisInstance;
	_this->jType = jType;
	return _thisInstance;
}
CExpression toCExpression_CInvocation(void* _ref){
	CInvocation _this = *((CInvocation*) _ref);
	CExpressionData data;
	data.CInvocation = _this;
	return { CExpressionTag::CInvocationVariant, data };
}
char* generate_CInvocation(void* _ref){
	CInvocation* _this = (CInvocation*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]]', not an object.]]', not an object.]*/ joinedArguments = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]*/(&(cArguments_CInvocation(&((*_this)))))), /*Convert method references to a closure*/)), new_Joiner(", "));
	return generate_/*Not a functional type: Identifier[value=CExpression]*/(&(expression_CInvocation(&((*_this))))) + "(" + joinedArguments + ")";
}
CInvocation new_CInvocation(CExpression expression, List<CExpression> cArguments){
	CInvocation _thisInstance;
	CInvocation* _this = &_thisInstance;
	_this->expression = expression;
	_this->cArguments = cArguments;
	return _thisInstance;
}
JExpression toJExpression_JInvokable(void* _ref){
	JInvokable _this = *((JInvokable*) _ref);
	JExpressionData data;
	data.JInvokable = _this;
	return { JExpressionTag::JInvokableVariant, data };
}
JInvokable new_JInvokable(JCaller caller, List<JExpression> arguments){
	JInvokable _thisInstance;
	JInvokable* _this = &_thisInstance;
	_this->caller = caller;
	_this->arguments = arguments;
	return _thisInstance;
}
JType toJType_JFunctionalType(void* _ref){
	JFunctionalType _this = *((JFunctionalType*) _ref);
	JTypeData data;
	data.JFunctionalType = _this;
	return { JTypeTag::JFunctionalTypeVariant, data };
}
JFunctionalType new_JFunctionalType(JType returnType){
	JFunctionalType _thisInstance;
	JFunctionalType* _this = &_thisInstance;
	(*_this)(new_JavaList(), returnType);
	return _thisInstance;
}
/*TODO:  resolve lambda return type*/ lambda9(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return "_" + slice;
}
char* stringify_JFunctionalType(void* _ref){
	JFunctionalType* _this = (JFunctionalType*) _ref;
	C joined = collect_Iter(&(map_Iter(&(map_Iter(&(iter_List(&(_this->parameterTypes))), /*Convert method references to a closure*/)), lambda9)), new_Joiner());
	return "func_" + stringify_JType(&(_this->returnType)) + joined;
}
JFunctionalType new_JFunctionalType(List<JType> parameterTypes, JType returnType){
	JFunctionalType _thisInstance;
	JFunctionalType* _this = &_thisInstance;
	_this->parameterTypes = parameterTypes;
	_this->returnType = returnType;
	return _thisInstance;
}
Environment new_Environment(){
	Environment _thisInstance;
	Environment* _this = &_thisInstance;
	(*_this)(new_JavaList());
	return _thisInstance;
}
Environment new_Environment(List<Frame> frames){
	Environment _thisInstance;
	Environment* _this = &_thisInstance;
	_this->frames = frames;
	return _thisInstance;
}
/*TODO:  resolve lambda return type*/ lambda10(void* _ref, /*TODO: resolve type of lambda param*/ frame){
	return resolveExpression_/*Undefined identifier: frame*/(&(frame), identifier);
}
Option<JDeclaration> resolveExpression_Environment(void* _ref, char* identifier){
	Environment* _this = (Environment*) _ref;
	return next_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(_this->frames))), lambda10)), /*Convert method references to a closure*/)));
}
template <typename T>
Tuple<Environment, T> within_Environment(void* _ref, F1R<Environment, Tuple<Environment, T>> supplier){
	Environment* _this = (Environment*) _ref;
	Environment withLastEnv = enter_Environment(&((*_this)));
	R result = apply_F1R(&(supplier), withLastEnv);
	/*Not a functional type: Placeholder[input=Cannot access member 'exit' in 'Placeholder[input=Cannot access member 'left' in 'Identifier[value=R]', not an object.]', not an object.]*/ exited = exit_/*Cannot access member 'left' in 'Identifier[value=R]', not an object.*/(&(result.left));
	return new_Tuple(exited, result.right);
}
Environment exit_Environment(void* _ref){
	Environment* _this = (Environment*) _ref;
	return new_Environment(removeLast_List(&(_this->frames)));
}
Environment enter_Environment(void* _ref){
	Environment* _this = (Environment*) _ref;
	return new_Environment(addLast_List(&(_this->frames), new_Frame()));
}
/*TODO:  resolve lambda return type*/ lambda11(void* _ref, /*TODO: resolve type of lambda param*/ last){
	return defineAllExpressions_/*Undefined identifier: last*/(&(last), declarations);
}
Environment defineAllExpressions_Environment(void* _ref, List<JDeclaration> declarations){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(&(_this->frames), lambda11));
}
/*TODO:  resolve lambda return type*/ lambda12(void* _ref, /*TODO: resolve type of lambda param*/ last){
	return defineExpression_/*Undefined identifier: last*/(&(last), declaration);
}
Environment defineExpression_Environment(void* _ref, JDeclaration declaration){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(&(_this->frames), lambda12));
}
Option<JObjectType> resolveCurrent_Environment(void* _ref){
	Environment* _this = (Environment*) _ref;
	return next_Iter(&(flatMap_Iter(&(map_Iter(&(iterReversed_List(&(_this->frames))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)));
}
/*TODO:  resolve lambda return type*/ lambda13(void* _ref, /*TODO: resolve type of lambda param*/ last){
	return withObject_/*Undefined identifier: last*/(&(last), object);
}
Environment withObject_Environment(void* _ref, JObject object){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(&(_this->frames), lambda13));
}
/*TODO:  resolve lambda return type*/ lambda14(void* _ref, /*TODO: resolve type of lambda param*/ frame){
	return resolveType_/*Undefined identifier: frame*/(&(frame), name);
}
Option<JObjectType> resolveType_Environment(void* _ref, char* name){
	Environment* _this = (Environment*) _ref;
	return next_Iter(&(flatMap_Iter(&(map_Iter(&(iterReversed_List(&(_this->frames))), lambda14)), /*Convert method references to a closure*/)));
}
/*TODO:  resolve lambda return type*/ lambda15(void* _ref, /*TODO: resolve type of lambda param*/ last){
	return defineAllTypes_/*Undefined identifier: last*/(&(last), types);
}
Environment defineAllTypes_Environment(void* _ref, List<JObjectType> types){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(&(_this->frames), lambda15));
}
Frame new_Frame(Option<JObject> maybeName, List<JObjectType> definedTypes, List<JDeclaration> definedExpressions){
	Frame _thisInstance;
	Frame* _this = &_thisInstance;
	_this->maybeObject = maybeName;
	_this->definedTypes = definedTypes;
	_this->definedExpressions = definedExpressions;
	return _thisInstance;
}
Frame new_Frame(){
	Frame _thisInstance;
	Frame* _this = &_thisInstance;
	(*_this)(new_None(), new_JavaList(), new_JavaList());
	return _thisInstance;
}
Frame defineAllExpressions_Frame(void* _ref, List<JDeclaration> definitions){
	Frame* _this = (Frame*) _ref;
	return new_Frame(_this->maybeObject, _this->definedTypes, addAllLast_List(&(_this->definedExpressions), definitions));
}
/*TODO:  resolve lambda return type*/ lambda16(void* _ref, /*TODO: resolve type of lambda param*/ define){
	return equals_/*Cannot access member 'name' in 'Placeholder[input=Undefined identifier: define]', not an object.*/(&(define.name), identifier);
}
Option<JDeclaration> resolveExpression_Frame(void* _ref, char* identifier){
	Frame* _this = (Frame*) _ref;
	return next_Iter(&(filter_Iter(&(iter_List(&(_this->definedExpressions))), lambda16)));
}
Frame defineExpression_Frame(void* _ref, JDeclaration declaration){
	Frame* _this = (Frame*) _ref;
	return new_Frame(_this->maybeObject, _this->definedTypes, addLast_List(&(_this->definedExpressions), declaration));
}
/*TODO:  resolve lambda return type*/ lambda17(void* _ref, /*TODO: resolve type of lambda param*/ obj){
	return new_JObjectType(obj.name, _this->definedExpressions);
}
Option<JObjectType> toStructureType_Frame(void* _ref){
	Frame* _this = (Frame*) _ref;
	return map_Option(&(_this->maybeObject), lambda17);
}
Frame withObject_Frame(void* _ref, JObject name){
	Frame* _this = (Frame*) _ref;
	return new_Frame(new_Some(name), _this->definedTypes, _this->definedExpressions);
}
/*TODO:  resolve lambda return type*/ lambda18(void* _ref, /*TODO: resolve type of lambda param*/ type){
	return equals_/*Cannot access member 'name' in 'Placeholder[input=Undefined identifier: type]', not an object.*/(&(type.name), name);
}
Option<JObjectType> resolveType_Frame(void* _ref, char* name){
	Frame* _this = (Frame*) _ref;
	return next_Iter(&(filter_Iter(&(iter_List(&(_this->definedTypes))), lambda18)));
}
Frame defineAllTypes_Frame(void* _ref, List<JObjectType> types){
	Frame* _this = (Frame*) _ref;
	return new_Frame(_this->maybeObject, addAllLast_List(&(_this->definedTypes), types), _this->definedExpressions);
}
JType toJType_JObjectType(void* _ref){
	JObjectType _this = *((JObjectType*) _ref);
	JTypeData data;
	data.JObjectType = _this;
	return { JTypeTag::JObjectTypeVariant, data };
}
/*TODO:  resolve lambda return type*/ lambda19(void* _ref, /*TODO: resolve type of lambda param*/ member){
	return equals_/*Cannot access member 'name' in 'Placeholder[input=Undefined identifier: member]', not an object.*/(&(member.name), name);
}
Option<JType> resolve_JObjectType(void* _ref, char* name){
	JObjectType* _this = (JObjectType*) _ref;
	return map_Option(&(next_Iter(&(filter_Iter(&(iter_List(&(_this->members))), lambda19)))), /*Convert method references to a closure*/);
}
char* stringify_JObjectType(void* _ref){
	JObjectType* _this = (JObjectType*) _ref;
	return _this->name;
}
JObjectType new_JObjectType(char* name, List<JDeclaration> members){
	JObjectType _thisInstance;
	JObjectType* _this = &_thisInstance;
	_this->name = name;
	_this->members = members;
	return _thisInstance;
}
JType toJType_JRecursiveType(void* _ref){
	JRecursiveType _this = *((JRecursiveType*) _ref);
	JTypeData data;
	data.JRecursiveType = _this;
	return { JTypeTag::JRecursiveTypeVariant, data };
}
JRecursiveType new_JRecursiveType(){
	JRecursiveType _thisInstance;
	JRecursiveType* _this = &_thisInstance;
	_this->maybeInternal = new_None();
	return _thisInstance;
}
JType create_JRecursiveType(void* _ref, F1R<JType, JType> mapper){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	JRecursiveType created = new_JRecursiveType();
	R apply = apply_F1R(&(mapper), created);
	set_JRecursiveType(&(created), apply);
	return created;
}
void set_JRecursiveType(void* _ref, JType created){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	_this->maybeInternal = new_Some(created);
}
char* stringify_JRecursiveType(void* _ref){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	return orElse_Option(&(map_Option(&(_this->maybeInternal), /*Convert method references to a closure*/)), "?");
}
CStructureOrUnion toCStructureOrUnion_CStructure(void* _ref){
	CStructure _this = *((CStructure*) _ref);
	CStructureOrUnionData data;
	data.CStructure = _this;
	return { CStructureOrUnionTag::CStructureVariant, data };
}
char* findName_CStructure(void* _ref){
	CStructure* _this = (CStructure*) _ref;
	return _this->name;
}
char* generate_CStructure(void* _ref){
	CStructure* _this = (CStructure*) _ref;
	C joinedFields = collect_Iter(&(map_Iter(&(map_Iter(&(iter_List(&(_this->fields))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)), new_Joiner());
	return lineSeparator_int(&(generateTemplateString(_this->typeParameters) + "struct " + _this->name + " {" + joinedFields + lineSeparator_/*Undefined identifier: System*/(&(System)) + "};" + System));
}
/*TODO:  resolve lambda return type*/ lambda20(void* _ref, /*TODO: resolve type of lambda param*/ value){
	return contains_/*Cannot access member 'typeParameters' in 'Boolean', not an object.*/(&(!(*_this).typeParameters), value);
}
List<char*> findDependencies_CStructure(void* _ref){
	CStructure* _this = (CStructure*) _ref;
	return collect_Iter(&(filter_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(_this->fields))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)), lambda20)), new_ListCollector());
}
CStructure new_CStructure(List<char*> typeParameters, char* name, List<CDefinable> fields){
	CStructure _thisInstance;
	CStructure* _this = &_thisInstance;
	_this->typeParameters = typeParameters;
	_this->name = name;
	_this->fields = fields;
	return _thisInstance;
}
/*TODO:  resolve lambda return type*/ lambda21(void* _ref, /*TODO: resolve type of lambda param*/ variant){
	return variant + "Variant";
}
/*TODO:  resolve lambda return type*/ lambda22(void* _ref, /*TODO: resolve type of lambda param*/ variant){
	return generateIndent(1) + variant;
}
char* generate_CEnum(void* _ref){
	CEnum* _this = (CEnum*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ enumFields = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]*/(&(variants_CEnum(&((*_this)))))), lambda21)), lambda22)), new_Joiner(","));
	return lineSeparator_int(&("enum class " + _this->name + " {" + enumFields + lineSeparator_/*Undefined identifier: System*/(&(System)) + "};" + System));
}
CEnum new_CEnum(char* name, List<char*> variants){
	CEnum _thisInstance;
	CEnum* _this = &_thisInstance;
	_this->name = name;
	_this->variants = variants;
	return _thisInstance;
}
CStructureOrUnion toCStructureOrUnion_CUnion(void* _ref){
	CUnion _this = *((CUnion*) _ref);
	CStructureOrUnionData data;
	data.CUnion = _this;
	return { CStructureOrUnionTag::CUnionVariant, data };
}
char* findName_CUnion(void* _ref){
	CUnion* _this = (CUnion*) _ref;
	return _this->name + "Data";
}
char* generate_CUnion(void* _ref){
	CUnion* _this = (CUnion*) _ref;
	C unionFields = collect_Iter(&(map_Iter(&(map_Iter(&(iter_List(&(_this->members))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)), new_Joiner());
	return lineSeparator_int(&(generateTemplateString(typeParameters_CUnion(&((*_this)))) + "union " + _this->name + "Data {" + unionFields + lineSeparator_/*Undefined identifier: System*/(&(System)) + "};" + System));
}
List<char*> findDependencies_CUnion(void* _ref){
	CUnion* _this = (CUnion*) _ref;
	return toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(_this->members))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)));
}
CUnion new_CUnion(List<char*> typeParameters, char* name, List<CDefinable> members){
	CUnion _thisInstance;
	CUnion* _this = &_thisInstance;
	_this->typeParameters = typeParameters;
	_this->name = name;
	_this->members = members;
	return _thisInstance;
}
JObjectMember toJObjectMember_JObject(void* _ref){
	JObject _this = *((JObject*) _ref);
	JObjectMemberData data;
	data.JObject = _this;
	return { JObjectMemberTag::JObjectVariant, data };
}
List<CDefinable> collectCFields_JObject(void* _ref){
	JObject* _this = (JObject*) _ref;
	return /*this.recordFields.iter().map(JDeclaration::toCDeclaration).<CDefinable>map(value -> value).toList()*/;
}
List<CFunction> createConversionFunctions_JObject(void* _ref){
	JObject* _this = (JObject*) _ref;
	return toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]*/(&(implementees_JObject(&((*_this)))))), /*Convert method references to a closure*/)));
}
CFunction createConversionType_JObject(void* _ref, CType implementee){
	JObject* _this = (JObject*) _ref;
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
new CFunctionHeader_JObject(void* _ref, /*implementee,*/ conversionFunctionName){
	JObject* _this = (JObject*) _ref;
	return _this->table.CFunctionHeader(_this->data, conversionFunctionName);
}
new CFunction_JObject(void* _ref){
	JObject* _this = (JObject*) _ref;
	return _this->table.CFunction(_this->data);
}
JObjectType toType_JObject(void* _ref){
	JObject* _this = (JObject*) _ref;
	return _this->table.toType(_this->data);
}
/*final var memberDefinitions = this.children.iter*/(){?
}
new JObjectType_JObject(void* _ref){
	JObject* _this = (JObject*) _ref;
	return _this->table.JObjectType(_this->data);
}
Option<JDeclaration> extractDefinition_JObject(void* _ref, JObjectMember child){
	JObject* _this = (JObject*) _ref;
	return _this->table.extractDefinition(_this->data, child);
}
/*return*/ switch_JObject(void* _ref){
	JObject* _this = (JObject*) _ref;
	return _this->table.switch(_this->data);
}
/*case JField jField -> new Some<JDeclaration>*/(){?
}
/*case JMethod jMethod -> jMethod.toDeclaration*/(){?
}
/*case EmptyStructMember _, JObject _, Placeholder _ -> new None<JDeclaration>*/(){?
}
/*-> throw new*/ IllegalStateException_JObject(void* _ref, /*+*/ child){
	JObject* _this = (JObject*) _ref;
	return _this->table.IllegalStateException(_this->data, child);
}
JObject new_JObject(char* type, List<char*> annotations, List<char*> modifiersList, char* name, List<char*> typeParameters, List<JDeclaration> recordFields, List<CType> implementees, List<char*> variants, List<JObjectMember> children){
	JObject _thisInstance;
	JObject* _this = &_thisInstance;
	_this->type = type;
	_this->annotations = annotations;
	_this->modifiersList = modifiersList;
	_this->name = name;
	_this->typeParameters = typeParameters;
	_this->recordFields = recordFields;
	_this->implementees = implementees;
	_this->variants = variants;
	_this->children = children;
	return _thisInstance;
}
char* generate_CFunctionHeader(void* _ref){
	CFunctionHeader* _this = (CFunctionHeader*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeElement'}]]*/(&(parameters_CFunctionHeader(&((*_this)))))), /*Convert method references to a closure*/)), new_Joiner(", "));
	return generate_/*Not a functional type: Identifier[value=CDefinable]*/(&(definition_CFunctionHeader(&((*_this))))) + "(" + compiledParameters + ")";
}
CFunctionHeader new_CFunctionHeader(CDefinable definition, List<CDeclaration> parameters){
	CFunctionHeader _thisInstance;
	CFunctionHeader* _this = &_thisInstance;
	_this->definition = definition;
	_this->parameters = parameters;
	return _thisInstance;
}
char* generate_CFunction(void* _ref){
	CFunction* _this = (CFunction*) _ref;
	return lineSeparator_int(&(generate_/*Not a functional type: Identifier[value=CFunctionHeader]*/(&(header_CFunction(&((*_this))))) + "{" + content_CFunction(&((*_this))) + lineSeparator_/*Undefined identifier: System*/(&(System)) + "}" + System));
}
CFunction new_CFunction(CFunctionHeader header, char* content){
	CFunction _thisInstance;
	CFunction* _this = &_thisInstance;
	_this->header = header;
	_this->content = content;
	return _thisInstance;
}
JObjectMember toJObjectMember_JMethod(void* _ref){
	JMethod _this = *((JMethod*) _ref);
	JObjectMemberData data;
	data.JMethod = _this;
	return { JObjectMemberTag::JMethodVariant, data };
}
Option<JDeclaration> toDeclaration_JMethod(void* _ref){
	JMethod* _this = (JMethod*) _ref;
	if (/*instance*/) {
		/*Cannot access member 'type' in 'Placeholder[input=Undefined identifier: declaration]', not an object.*/ returnType = declaration.type;
		List paramTypes = toList_Iter(&(map_Iter(&(iter_List(&(_this->parameters))), /*Convert method references to a closure*/)));
		JFunctionalType functionalType = new_JFunctionalType(paramTypes, returnType);
		return new_Some(new_JDeclaration(declaration.name, functionalType));
	}
	return new_None();
}
JMethod new_JMethod(List<char*> typeParameters, List<JDeclaration> parameters, JMethodDeclaration methodDeclaration, char* content){
	JMethod _thisInstance;
	JMethod* _this = &_thisInstance;
	_this->typeParameters = typeParameters;
	_this->parameters = parameters;
	_this->methodDeclaration = methodDeclaration;
	_this->content = content;
	return _thisInstance;
}
JObjectMember toJObjectMember_JField(void* _ref){
	JField _this = *((JField*) _ref);
	JObjectMemberData data;
	data.JField = _this;
	return { JObjectMemberTag::JFieldVariant, data };
}
JField new_JField(JDeclaration declaration){
	JField _thisInstance;
	JField* _this = &_thisInstance;
	_this->declaration = declaration;
	return _thisInstance;
}
JExpression toJExpression_JNumber(void* _ref){
	JNumber _this = *((JNumber*) _ref);
	JExpressionData data;
	data.JNumber = _this;
	return { JExpressionTag::JNumberVariant, data };
}
JNumber new_JNumber(char* value){
	JNumber _thisInstance;
	JNumber* _this = &_thisInstance;
	_this->value = value;
	/*assert !value.isEmpty()*/;
	return _thisInstance;
}
JNumber new_JNumber(char* value){
	JNumber _thisInstance;
	JNumber* _this = &_thisInstance;
	_this->value = value;
	return _thisInstance;
}
CExpression toCExpression_CNumber(void* _ref){
	CNumber _this = *((CNumber*) _ref);
	CExpressionData data;
	data.CNumber = _this;
	return { CExpressionTag::CNumberVariant, data };
}
CNumber new_CNumber(char* value){
	CNumber _thisInstance;
	CNumber* _this = &_thisInstance;
	_this->value = value;
	/*assert !value.isEmpty()*/;
	return _thisInstance;
}
char* generate_CNumber(void* _ref){
	CNumber* _this = (CNumber*) _ref;
	return _this->value;
}
CNumber new_CNumber(char* value){
	CNumber _thisInstance;
	CNumber* _this = &_thisInstance;
	_this->value = value;
	return _thisInstance;
}
JExpression toJExpression_JNot(void* _ref){
	JNot _this = *((JNot*) _ref);
	JExpressionData data;
	data.JNot = _this;
	return { JExpressionTag::JNotVariant, data };
}
JNot new_JNot(CExpression instance){
	JNot _thisInstance;
	JNot* _this = &_thisInstance;
	_this->instance = instance;
	return _thisInstance;
}
CExpression toCExpression_CNot(void* _ref){
	CNot _this = *((CNot*) _ref);
	CExpressionData data;
	data.CNot = _this;
	return { CExpressionTag::CNotVariant, data };
}
char* generate_CNot(void* _ref){
	CNot* _this = (CNot*) _ref;
	return generate_/*Cannot access member 'instance' in 'Int', not an object.*/(&("!" + (*_this).instance));
}
CNot new_CNot(CExpression instance){
	CNot _thisInstance;
	CNot* _this = &_thisInstance;
	_this->instance = instance;
	return _thisInstance;
}
template <typename K, typename V>
Collector<Tuple<K, V>, Map<K, V>> toCollector_MapCollector(void* _ref){
	MapCollector<K, V> _this = *((MapCollector<K, V>*) _ref);
	CollectorData<K, V> data;
	data.MapCollector = _this;
	return { CollectorTag::MapCollectorVariant, data };
}
template <typename K, typename V>
Map<K, V> createInitial_MapCollector(void* _ref){
	MapCollector<K, V>* _this = (MapCollector<K, V>*) _ref;
	return new_HashMap();
}
template <typename K, typename V>
Map<K, V> fold_MapCollector(void* _ref, Map<K, V> kvMap, Tuple<K, V> kvTuple){
	MapCollector<K, V>* _this = (MapCollector<K, V>*) _ref;
	put_/*Generic type 'Map' has not been defined*/(&(kvMap), kvTuple.left, kvTuple.right);
	return kvMap;
}
template <typename K, typename V>
MapCollector<K, V> new_MapCollector(){
	MapCollector<K, V> _thisInstance;
	MapCollector<K, V>* _this = &_thisInstance;
	return _thisInstance;
}
CExpression toCExpression_CReference(void* _ref){
	CReference _this = *((CReference*) _ref);
	CExpressionData data;
	data.CReference = _this;
	return { CExpressionTag::CReferenceVariant, data };
}
char* generate_CReference(void* _ref){
	CReference* _this = (CReference*) _ref;
	return generate_/*Cannot access member 'instance' in 'Int', not an object.*/(&("&" + (*_this).instance));
}
CReference new_CReference(CExpression instance){
	CReference _thisInstance;
	CReference* _this = &_thisInstance;
	_this->instance = instance;
	return _thisInstance;
}
JExpression toJExpression_Char(void* _ref){
	Char _this = *((Char*) _ref);
	JExpressionData data;
	data.Char = _this;
	return { JExpressionTag::CharVariant, data };
}
CExpression toCExpression_Char(void* _ref){
	Char _this = *((Char*) _ref);
	CExpressionData data;
	data.Char = _this;
	return { CExpressionTag::CharVariant, data };
}
char* generate_Char(void* _ref){
	Char* _this = (Char*) _ref;
	return "'" + _this->value + "'";
}
Char new_Char(char* value){
	Char _thisInstance;
	Char* _this = &_thisInstance;
	_this->value = value;
	return _thisInstance;
}
JExpression toJExpression_JOperator(void* _ref){
	JOperator _this = *((JOperator*) _ref);
	JExpressionData data;
	data.JOperator = _this;
	return { JExpressionTag::JOperatorVariant, data };
}
JOperator new_JOperator(JExpression left, Operator operator, JExpression rightCompiled){
	JOperator _thisInstance;
	JOperator* _this = &_thisInstance;
	_this->left = left;
	_this->operator = operator;
	_this->rightCompiled = rightCompiled;
	return _thisInstance;
}
CExpression toCExpression_COperator(void* _ref){
	COperator _this = *((COperator*) _ref);
	CExpressionData data;
	data.COperator = _this;
	return { CExpressionTag::COperatorVariant, data };
}
char* generate_COperator(void* _ref){
	COperator* _this = (COperator*) _ref;
	return generate_/*Cannot access member 'right' in 'Int', not an object.*/(&(generate_CExpression(&(_this->left)) + " " + generate_Operator(&(_this->operator)) + " " + (*_this).right));
}
COperator new_COperator(CExpression left, Operator operator, CExpression right){
	COperator _thisInstance;
	COperator* _this = &_thisInstance;
	_this->left = left;
	_this->operator = operator;
	_this->right = right;
	return _thisInstance;
}
JExpression toJExpression_StringNode(void* _ref){
	StringNode _this = *((StringNode*) _ref);
	JExpressionData data;
	data.StringNode = _this;
	return { JExpressionTag::StringNodeVariant, data };
}
CExpression toCExpression_StringNode(void* _ref){
	StringNode _this = *((StringNode*) _ref);
	CExpressionData data;
	data.StringNode = _this;
	return { CExpressionTag::StringNodeVariant, data };
}
char* generate_StringNode(void* _ref){
	StringNode* _this = (StringNode*) _ref;
	return "\"" + _this->slice + "\"";
}
StringNode new_StringNode(char* slice){
	StringNode _thisInstance;
	StringNode* _this = &_thisInstance;
	_this->slice = slice;
	return _thisInstance;
}
/*private static final JType StringType = JRecursiveType.create*/(/*-> {
		// We don't need parameter types for*/ now){?
}
new Environment_Main(void* _ref){
	Main* _this = (Main*) _ref;
	return _this->table.Environment(_this->data);
}
Main new_Main(){
	Main _thisInstance;
	Main* _this = &_thisInstance;
	_this->structuresOrUnions = empty_Lists(&(Lists));
	_this->enums = empty_Lists(&(Lists));
	_this->structureForwardDeclarations = empty_Lists(&(Lists));
	_this->functionDeclarations = empty_Lists(&(Lists));
	_this->functions = empty_Lists(&(Lists));
	_this->globals = empty_Lists(&(Lists));
	_this->counter = 0;
	return _thisInstance;
}
/*TODO:  resolve lambda return type*/ lambda23(void* _ref, /*TODO: resolve type of lambda param*/ typeParam){
	return "typename " + typeParam;
}
char* generateTemplateString_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	if (isEmpty_List(&(typeParameters))) 
		return "";
	C typeNames = collect_Iter(&(map_Iter(&(iter_List(&(typeParameters))), lambda23)), new_Joiner(", "));
	return lineSeparator_int(&("template <" + typeNames + ">" + System));
}
void main_Main(void* _ref, char** args){
	Main* _this = (Main*) _ref;
	if (/*instance*/) 
		println_/*Cannot access member 'err' in 'Placeholder[input=Undefined identifier: System]', not an object.*/(&(System.err), display_/*Undefined identifier: value*/(&(value)));
}
char* generateStatement_Main(void* _ref, int depth, char* content){
	Main* _this = (Main*) _ref;
	return generateIndent(depth) + content + ";";
}
char* generateIndent_Main(void* _ref, int depth){
	Main* _this = (Main*) _ref;
	return repeat_int(&(lineSeparator_/*Undefined identifier: System*/(&(System)) + "\t"), depth);
}
CType transformType_Main(void* _ref, JType jType){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
CType transformPrimitiveType_Main(void* _ref, JPrimitiveType type){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
char* generateStatement_Main(void* _ref, char* content){
	Main* _this = (Main*) _ref;
	return generateStatement(1, content);
}
char* joinTypeParameters_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* joinedTypeParameters;
	if (isEmpty_List(&(typeParameters))) 
		joinedTypeParameters = "";
	else 
		joinedTypeParameters = "<" + collect_Iter(&(iter_List(&(typeParameters))), new_Joiner(", ")) + ">";
	return joinedTypeParameters;
}
Option<JObjectType> extractType_Main(void* _ref, JObjectMember jObjectMember){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
Tuple<CExpression, List<CType>> transformCaller_Main(void* _ref, JCaller jCaller){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
Tuple<CExpression, List<CType>> destroyConstruction_Main(void* _ref, JConstruction jConstruction){
	Main* _this = (Main*) _ref;
	CType cType = transformType(jConstruction.jType);
	if (/*instance*/) 
		return new_Tuple(new_Identifier("new_" + value), empty_Lists(&(Lists)));
	if (/*instance*/) 
		return new_Tuple(new_Identifier("new_" + base), typeArguments);
	return new_Tuple(new_Identifier(generate_int(&("new_" + cType))), empty_Lists(&(Lists)));
}
CExpression transformExpression_Main(void* _ref, JExpression expression){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
CExpression transformInvocation_Main(void* _ref, JInvokable jInvokable){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ arguments = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]*/(&(iter_/*Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]*/(&(arguments_JInvokable(&(jInvokable))))), /*Convert method references to a closure*/)));
	/*Not a functional type: Placeholder[input=Cannot access member 'caller' in 'Identifier[value=JInvokable]', not an object.]*/ caller = caller_JInvokable(&(jInvokable));
	if (/*instance*/) {
		JType jType = resolveExpression_Main(&((*_this)), instance);
		/*Not a functional type: Placeholder[input=Cannot access member 'stringify' in 'Identifier[value=JType]', not an object.]*/ baseName = stringify_JType(&(jType));
		Tuple tuple = transformCaller_Main(&((*_this)), instance);
		/*Member 'left' not defined in 'JObjectType[name=Tuple, members=[]]'*/ left = tuple.left;
		CExpression element;
		if (/*instance*/) 
			element = expression;
		else 
			element = new_CReference(new_CQuantity(left));
		if (/*instance*/) 
			element = expression;
		/*Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ newArguments = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(arguments), element);
		return new_CInvocation(new_Identifier(memberName + "_" + baseName), newArguments);
	}
	Tuple tuple = transformCaller_Main(&((*_this)), caller);
	return new_CInvocation(tuple.left, arguments);
}
Option<IOError> run_Main(void* _ref){
	Main* _this = (Main*) _ref;
	Path source = get_Paths(&(Paths), ".", "src", "main", "java", "magma", "Main.java");
	Path target = get_Paths(&(Paths), ".", "src", "main", "windows", "magma", "Main.cpp");
	/*Not a functional type: Placeholder[input=Cannot access member 'mapValue' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'readString' in 'Identifier[value=Path]', not an object.]]', not an object.]*/ input = mapValue_/*Not a functional type: Placeholder[input=Cannot access member 'readString' in 'Identifier[value=Path]', not an object.]*/(&(readString_Path(&(source))), /*Convert method references to a closure*/);
	return /*TODO: switch*/;
}
char* compile_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* all = compileStatements_Main(&((*_this)), input, /*Convert method references to a closure*/);
	char* joinedStructureForwardDeclarations = joinStrings_Main(&((*_this)), _this->structureForwardDeclarations);
	C joinedStructures = collect_Iter(&(map_Iter(&(iter_List(&(createTopologicallySortedList_Main(&((*_this)))))), /*Convert method references to a closure*/)), new_Joiner());
	char* joinedGlobals = joinStrings_Main(&((*_this)), _this->globals);
	char* joinedFunctionDeclarations = joinStrings_Main(&((*_this)), _this->functionDeclarations);
	C joinedFunctions = collect_Iter(&(map_Iter(&(iter_List(&(_this->functions))), /*Convert method references to a closure*/)), new_Joiner());
	C joinedEnums = collect_Iter(&(map_Iter(&(iter_List(&(_this->enums))), /*Convert method references to a closure*/)), new_Joiner());
	return joinedStructureForwardDeclarations + joinedEnums + joinedStructures + joinedGlobals + joinedFunctionDeclarations + joinedFunctions + all;
}
/*TODO:  resolve lambda return type*/ lambda24(void* _ref, /*TODO: resolve type of lambda param*/ value){
	List withoutDuplicates = removeDuplicates_Main(&((*_this)), findDependencies_/*Undefined identifier: value*/(&(value)));
	return new_Tuple(findName_/*Undefined identifier: value*/(&(value)), withoutDuplicates);
}
/*TODO:  resolve lambda return type*/ lambda25(void* _ref, /*TODO: resolve type of lambda param*/ entry){
	return isEmpty_/*Not a functional type: Placeholder[input=Cannot access member 'getValue' in 'Placeholder[input=Undefined identifier: entry]', not an object.]*/(&(getValue_/*Undefined identifier: entry*/(&(entry))));
}
/*TODO:  resolve lambda return type*/ lambda26(void* _ref, /*TODO: resolve type of lambda param*/ rootSegment){
	return new_Tuple(findName_/*Undefined identifier: rootSegment*/(&(rootSegment)), rootSegment);
}
/*TODO:  resolve lambda return type*/ lambda27(void* _ref, /*TODO: resolve type of lambda param*/ element){
	/*Not a functional type: Placeholder[input=Cannot access member 'get' in 'Identifier[value=C]', not an object.]*/ cStructureOrUnion = get_C(&(mapping), element);
	/*toReturn[0] = toReturn[0].addLast(cStructureOrUnion)*/;
}
List<CStructureOrUnion> createTopologicallySortedList_Main(void* _ref){
	Main* _this = (Main*) _ref;
	C dependencyMap = collect_Iter(&(map_Iter(&(iter_List(&(_this->structuresOrUnions))), lambda24)), new_MapCollector());
	/*for (var entry : dependencyMap.entrySet()) {
			var newValues = entry.getValue().iter().filter(dependencyMap::containsKey).toList();
			final var oldKey = entry.getKey();

			start
			This is to prevent circular dependencies with VTables.

			Technically List -> ListTable -> List, but ListTable -> List is invalid
			ListTable does not have any fields, only dynamic members,
			and we can safely assume that any mention of List in ListTable is a pointer or in a function pointer.
			Therefore, we remove it.
			 end
			if (oldKey.endsWith("Table")) {
				final var keyWithoutTable = oldKey.substring(0, oldKey.length() - "Table".length());
				if (newValues.contains(keyWithoutTable)) newValues = newValues.removeElement(keyWithoutTable);
			}

			dependencyMap.put(oldKey, newValues);
		}*/
	/*var dependencyOrder = Lists.<String>empty()*/;
	while (isEmpty_bool(&(!dependencyMap))) {
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'entrySet' in 'Identifier[value=C]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ dependencyMapKeysToRemove = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'entrySet' in 'Identifier[value=C]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'entrySet' in 'Identifier[value=C]', not an object.]]', not an object.]]', not an object.]*/(&(filter_/*Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'entrySet' in 'Identifier[value=C]', not an object.]]', not an object.]*/(&(stream_/*Not a functional type: Placeholder[input=Cannot access member 'entrySet' in 'Identifier[value=C]', not an object.]*/(&(entrySet_C(&(dependencyMap))))), lambda25)), /*Convert method references to a closure*/)));
		/*Not a functional type: Placeholder[input=Cannot access member 'size' in 'Identifier[value=C]', not an object.]*/ oldLength = size_C(&(dependencyMap));
	/*for (var dependencyMapKeyToRemove : dependencyMapKeysToRemove) {
				dependencyMap.remove(dependencyMapKeyToRemove);
				dependencyOrder = dependencyOrder.addLast(dependencyMapKeyToRemove);

				this.removeKeyFromValues(dependencyMap, dependencyMapKeyToRemove);
			}*/
		/*Not a functional type: Placeholder[input=Cannot access member 'size' in 'Identifier[value=C]', not an object.]*/ newLength = size_C(&(dependencyMap));
		/*assert oldLength !*/ = newLength;
	}
	C mapping = collect_Iter(&(map_Iter(&(iter_List(&(_this->structuresOrUnions))), lambda26)), new_MapCollector());
	/*final List<CStructureOrUnion>[] toReturn = new List[]{Lists.<CStructureOrUnion>empty()}*/;
	forEach_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: dependencyOrder]', not an object.]*/(&(iter_/*Undefined identifier: dependencyOrder*/(&(dependencyOrder))), lambda27);
	return /*toReturn[0]*/;
}
void removeKeyFromValues_Main(void* _ref, Map<char*, List<char*>> dependencyMap, char* dependencyMapKeyToRemove){
	Main* _this = (Main*) _ref;
	/*for (var anyDependencyMapKey : dependencyMap.keySet()) {
			final var values = dependencyMap.get(anyDependencyMapKey);
			if (values.contains(dependencyMapKeyToRemove)) {
				var newValues = values.removeElement(dependencyMapKeyToRemove);
				dependencyMap.put(anyDependencyMapKey, newValues);
			}
		}*/
}
/*TODO:  resolve lambda return type*/ lambda28(void* _ref, /*TODO: resolve type of lambda param*/ copy, /*TODO: resolve type of lambda param*/ element){
	if (contains_/*Undefined identifier: copy*/(&(copy), element)) 
		return copy;
	return addLast_/*Undefined identifier: copy*/(&(copy), element);
}
List<char*> removeDuplicates_Main(void* _ref, List<char*> list){
	Main* _this = (Main*) _ref;
	return fold_Iter(&(iter_List(&(list))), empty_Lists(&(Lists)), lambda28);
}
char* joinStrings_Main(void* _ref, List<char*> structures){
	Main* _this = (Main*) _ref;
	return collect_Iter(&(iter_List(&(structures))), new_Joiner(""));
}
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper){
	Main* _this = (Main*) _ref;
	return compileAll_Main(&((*_this)), input, mapper, new_EscapedFolder(/*Convert method references to a closure*/));
}
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder){
	Main* _this = (Main*) _ref;
	return collect_Iter(&(map_Iter(&(divide_Main(&((*_this)), input, folder)), mapper)), new_Joiner(""));
}
Iter<char*> divide_Main(void* _ref, char* input, Folder folder){
	Main* _this = (Main*) _ref;
	State current = new_State(input);
	while (true) {
		/*Not a functional type: Placeholder[input=Cannot access member 'pop' in 'Identifier[value=State]', not an object.]*/ maybeNext = pop_State(&(current));
		if (/*!(maybeNext instanceof Some<Character>(var value))*/) 
			break;
		char next;
		next = value;
		current = apply_Folder(&(folder), current, next);
	}
	return stream_/*Not a functional type: Placeholder[input=Cannot access member 'advance' in 'Identifier[value=State]', not an object.]*/(&(advance_State(&(current))));
}
State foldStatement_Main(void* _ref, State current, char next){
	Main* _this = (Main*) _ref;
	if (/*next == '/' && current.isLevel()*/) {
		/*Not a functional type: Placeholder[input=Cannot access member 'peek' in 'Identifier[value=State]', not an object.]*/ maybePeeked = peek_State(&(current));
		if (/*instance*/) {
			/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/ withoutLineCommentPrefix = orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(&(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(append_State(&(current), ''/'')))), current);
			while (true) {
				/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ maybeTuple = popAndAppendToTuple_/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/(&(withoutLineCommentPrefix));
				if (/*instance*/) {
					withoutLineCommentPrefix = tuple.left;
					/*Cannot access member 'right' in 'Placeholder[input=Undefined identifier: tuple]', not an object.*/ right = tuple.right;
					if (/*right == '\r' || right == '\n'*/) 
						withoutLineCommentPrefix = advance_/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/(&(withoutLineCommentPrefix));
				}
				else 
					return withoutLineCommentPrefix;
			}
		}
	}
	/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = append_State(&(current), next);
	if (/*next == ';' && appended.isLevel()*/) 
		return advance_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended));
	if (/*next == '}' && appended.isShallow()*/) {
		State appended1;
		if (/*instance*/) 
			appended1 = orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(&(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended))), appended);
		else 
			appended1 = appended;
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'advance' in 'Placeholder[input=Undefined identifier: appended1]', not an object.]*/(&(advance_/*Undefined identifier: appended1*/(&(appended1))));
	}
	if (/*next == '{' || next == '('*/) 
		return enter_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended));
	if (/*next == '}' || next == ')'*/) 
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(&(appended));
	return appended;
}
/*TODO:  resolve lambda return type*/ lambda29(void* _ref){
	return wrap_Placeholder(&(Placeholder), stripped);
}
char* compileRootSegment_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (isEmpty_String(&(stripped))) 
		return "";
	if (startsWith_bool(&(startsWith_String(&(stripped), "package ") || stripped), "import ")) 
		return "";
	return orElseGet_Option(&(map_Option(&(flatMap_Option(&(parseObject_Main(&((*_this)), "class", stripped)), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)), lambda29);
}
Option<JObject> parseObject_Main(void* _ref, char* type, char* stripped){
	Main* _this = (Main*) _ref;
	int i = indexOf_String(&(stripped), type + " ");
	if (i < 0) 
		return new_None();
	char* beforeType = strip_String(&(substring_String(&(stripped), 0, i)));
	char* modifiers;
	List<char*> annotations = empty_Lists(&(Lists));
	int i5 = lastIndexOf_String(&(beforeType), "\n");
	if (i5 >= 0) {
		char* substring = substring_String(&(beforeType), 0, i5);
		char* substring1 = substring_String(&(beforeType), i5 + 1);
		annotations = collectAnnotations_Main(&((*_this)), substring);
		modifiers = substring1;
	}
	else 
		modifiers = beforeType;
	char* afterKeyword = strip_String(&(substring_String(&(stripped))));
	int i1 = indexOf_String(&(afterKeyword), "{");
	if (i1 < 0) 
		return new_None();
	char* beforeContent = strip_String(&(substring_String(&(afterKeyword), 0, i1)));
	char* withEnd = strip_String(&(substring_String(&(afterKeyword), i1 + 1)));
	if (endsWith_bool(&(!withEnd), "}")) 
		return new_None();
	char* inputContent = substring_String(&(withEnd), 0, length_String(&(withEnd)) - 1);
	List<char*> variants = empty_Lists(&(Lists));
	int i2 = indexOf_String(&(beforeContent), "permits ");
	if (i2 >= 0) {
		char* substring1 = substring_String(&(beforeContent), length_int(&(i2 + "permits ")));
		beforeContent = substring_String(&(beforeContent), 0, i2);
		variants = splitValues_Main(&((*_this)), substring1);
	}
	// TODO: generate conversion methods
	List<CType> extensions = empty_Lists(&(Lists));
	int extendsIndex = indexOf_String(&(beforeContent), "extends ");
	if (extendsIndex >= 0) 
	/*{*/
	char* extensionsString = substring_String(&(beforeContent), length_int(&(extendsIndex + "extends ")));
	beforeContent = strip_String(&(substring_String(&(beforeContent), 0, extendsIndex)));
	/*extensions = this*/
	/*.divide(extensionsString, new ValueFolder())*/
	/*.map(String::strip)*/
	/*.filter(slice -> !slice.isEmpty())*/
	/*.map(input -> transformType(this.parseType(input)))*/
	/*.toList()*/;
	/*}*/
	List<CType> implementees = empty_Lists(&(Lists));
	int i4 = indexOf_String(&(beforeContent), "implements ");
	if (i4 >= 0) 
	/*{*/
	char* implementeesString = substring_String(&(beforeContent), length_int(&(i4 + "implements ")));
	beforeContent = strip_String(&(substring_String(&(beforeContent), 0, i4)));
	/*implementees = this*/
	/*.divide(implementeesString, new ValueFolder())*/
	/*.map(String::strip)*/
	/*.filter(slice -> !slice.isEmpty())*/
	/*.map(input -> transformType(this.parseType(input)))*/
	/*.toList()*/;
	/*}*/
	List<JDeclaration> recordFields = empty_Lists(&(Lists));
	if (endsWith_String(&(beforeContent), ")")) 
	/*{*/
	char* substring = substring_String(&(beforeContent), 0, length_String(&(beforeContent)) - 1);
	int i3 = indexOf_String(&(substring), "(");
	if (i3 >= 0) 
	/*{*/
	beforeContent = substring_String(&(substring), 0, i3);
	/*recordFields = this*/
	/*.divide(substring.substring(i3 + 1), new ValueFolder())*/
	/*.map(this::parseDeclaration)*/
	/*.flatMap(Option::iter)*/
	/*.toList()*/;
	/*}*/
	/*}*/
	List<char*> typeParameters = empty_Lists(&(Lists));
	int i3 = indexOf_String(&(beforeContent), "<");
	if (i3 >= 0) 
	/*{*/
	char* substring1 = strip_String(&(substring_String(&(beforeContent), i3 + 1)));
	if (endsWith_String(&(substring1), ">")) 
	/*{*/
	beforeContent = substring_String(&(beforeContent), 0, i3);
	char* substring = substring_String(&(substring1), 0, length_String(&(substring1)) - 1);
	typeParameters = splitValues_Main(&((*_this)), substring);
	/*}*/
	/*}*/
	if (isIdentifier_bool(&(!Identifier), beforeContent)) 
		return new_None();
	/*var modifiersList = Streams*/
	/*.fromObjArray(modifiers.split(Pattern.quote(" ")))*/
	/*.map(String::strip)*/
	/*.filter(slice -> !slice.isEmpty())*/
	/*.toList()*/;
	char* name = strip_String(&(beforeContent));
	List finalTypeParameters = typeParameters;
	/*final var children = this*/
	/*.divide(inputContent, new EscapedFolder(this::foldStatement))*/
	/*.map(slice -> this.parseObjectMember(slice, name, finalTypeParameters))*/
	/*.flatMap(Option::iter)*/
	/*.toList()*/;
	/*final var prototype = new JObject(type,*/
	/*annotations,*/
	/*modifiersList,*/
	/*name,*/
	/*typeParameters,*/
	/*recordFields,*/
	/*implementees,*/
	/*variants,*/
	/*children)*/;
	return new_Some(prototype);
}
/*TODO:  resolve lambda return type*/ lambda30(void* _ref, /*TODO: resolve type of lambda param*/ wrapper){
	return transformObjectMemberPrototype_Main(&((*_this)), object, wrapper);
}
/*TODO:  resolve lambda return type*/ lambda31(void* _ref, /*TODO: resolve type of lambda param*/ env){
	_this->environment = withObject_/*Undefined identifier: env*/(&(env), object);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ types = toList_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]*/(&(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]*/(&(iter_/*Cannot access member 'children' in 'Identifier[value=JObject]', not an object.*/(&(object.children))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)));
	/*Not a functional type: Placeholder[input=Cannot access member 'addAllLast' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ declarations = addAllLast_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(toList_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]*/(&(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]*/(&(iter_/*Cannot access member 'children' in 'Identifier[value=JObject]', not an object.*/(&(object.children))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)))), object.recordFields);
	_this->environment = defineAllTypes_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]'*/(&(_this->environment), types);
	_this->environment = defineAllExpressions_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]'*/(&(_this->environment), declarations);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ members = toList_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]*/(&(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]*/(&(iter_/*Cannot access member 'children' in 'Identifier[value=JObject]', not an object.*/(&(object.children))), lambda30)), /*Convert method references to a closure*/)));
	return new_Tuple(_this->environment, members);
}
/*TODO:  resolve lambda return type*/ lambda32(void* _ref, /*TODO: resolve type of lambda param*/ field){
	return "_this->" + field.name + " = " + field.name;
}
Option<CStructMember> transformObject_Main(void* _ref, JObject object){
	Main* _this = (Main*) _ref;
	if (contains_/*Cannot access member 'annotations' in 'Identifier[value=JObject]', not an object.*/(&(object.annotations), "Actual")) 
		return new_Some(new_EmptyStructMember());
	_this->functions = fold_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'createConversionFunctions' in 'Identifier[value=JObject]', not an object.]]', not an object.]*/(&(iter_/*Not a functional type: Placeholder[input=Cannot access member 'createConversionFunctions' in 'Identifier[value=JObject]', not an object.]*/(&(createConversionFunctions_JObject(&(object))))), _this->functions, /*Convert method references to a closure*/);
	/*Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]*/ within = within_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]'*/(&(_this->environment), lambda31);
	_this->environment = within.left;
	/*Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]]', not an object.*/ members = within.right;
	/*Not a functional type: Placeholder[input=Cannot access member 'collectCFields' in 'Identifier[value=JObject]', not an object.]*/ fields = collectCFields_JObject(&(object));
	if (equals_/*Not a functional type: Placeholder[input=Cannot access member 'type' in 'Identifier[value=JObject]', not an object.]*/(&(type_JObject(&(object))), "interface")) 
		if (contains_/*Not a functional type: Placeholder[input=Cannot access member 'modifiersList' in 'Identifier[value=JObject]', not an object.]*/(&(modifiersList_JObject(&(object))), "sealed")) 
			fields = handleSealedInterface_Main(&((*_this)), object, fields);
	else 
		fields = handleUnsealedInterface_Main(&((*_this)), object, members, fields);
	else {
		List retained = retainFields_Main(&((*_this)), members);
		fields = addAllLast_/*Not a functional type: Placeholder[input=Cannot access member 'collectCFields' in 'Identifier[value=JObject]', not an object.]*/(&(fields), retained);
	}
	if (equals_/*Cannot access member 'type' in 'Identifier[value=JObject]', not an object.*/(&(object.type), "record")) {
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'recordFields' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]*/ recordFields = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'recordFields' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'recordFields' in 'Identifier[value=JObject]', not an object.]', not an object.]*/(&(iter_/*Cannot access member 'recordFields' in 'Identifier[value=JObject]', not an object.*/(&(object.recordFields))), /*Convert method references to a closure*/)));
		CNamedType structureType = createStructureType_Main(&((*_this)), object.name, object.typeParameters);
		CDeclaration definition = new_CDeclaration(object.typeParameters, structureType, getName_int(&("new_" + structureType)));
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'recordFields' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ joinedAssignments = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'recordFields' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'recordFields' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'recordFields' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'recordFields' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]*/(&(recordFields))), lambda32)), /*Convert method references to a closure*/)), new_Joiner());
		/*Not a functional type: Int*/ content = generateStatement(generate_CNamedType(&(structureType)) + " _thisInstance") + generateStatement(generate_CNamedType(&(structureType)) + "* _this = &_thisInstance") + joinedAssignments + generateStatement("return _thisInstance");
		_this->functions = addLast_List(&(_this->functions), new_CFunction(new_CFunctionHeader(definition, recordFields), content));
	}
	_this->structuresOrUnions = addLast_List(&(_this->structuresOrUnions), new_CStructure(typeParameters_JObject(&(object)), object.name, fields));
	_this->structureForwardDeclarations = addLast_List(&(_this->structureForwardDeclarations), lineSeparator_int(&(generateTemplateString(object.typeParameters) + "struct " + object.name + ";" + System)));
	return new_Some(new_EmptyStructMember());
}
List<CDefinable> handleUnsealedInterface_Main(void* _ref, JObject object, List<CStructMember> members, List<CDefinable> fields){
	Main* _this = (Main*) _ref;
	List list = toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(members))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)));
	CStructure cStructure = new_CStructure(typeParameters_JObject(&(object)), object.name + "Table", list);
	_this->structuresOrUnions = addLast_List(&(_this->structuresOrUnions), cStructure);
	CNamedType tableType = createStructureType_Main(&((*_this)), object.name + "Table", object.typeParameters);
	fields = addFirst_List(&(addLast_List(&(fields), new_CDeclaration(tableType, "table"))), new_CDeclaration(new_CPointerType(CPrimitiveType.Void), "data"));
	return fields;
}
CNamedType createStructureType_Main(void* _ref, char* name, List<char*> typeArguments){
	Main* _this = (Main*) _ref;
	if (isEmpty_List(&(typeArguments))) 
		return new_Identifier(name);
	else 
		return new_CTemplateType(name);
}
/*TODO:  resolve lambda return type*/ lambda33(void* _ref, /*TODO: resolve type of lambda param*/ variant){
	return createUnionField_Main(&((*_this)), variant, typeArguments);
}
List<CDefinable> handleSealedInterface_Main(void* _ref, JObject object, List<CDefinable> fields){
	Main* _this = (Main*) _ref;
	/*Cannot access member 'name' in 'Identifier[value=JObject]', not an object.*/ name = object.name;
	/*Not a functional type: Placeholder[input=Cannot access member 'typeParameters' in 'Identifier[value=JObject]', not an object.]*/ typeParameters = typeParameters_JObject(&(object));
	/*Not a functional type: Placeholder[input=Cannot access member 'variants' in 'Identifier[value=JObject]', not an object.]*/ variants = variants_JObject(&(object));
	CEnum jEnum = new_CEnum(name + "Tag", variants);
	/*final var typeArguments = typeParameters.iter().<CType>map(Identifier::new).toList()*/;
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'variants' in 'Identifier[value=JObject]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ unionMembers = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'variants' in 'Identifier[value=JObject]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'variants' in 'Identifier[value=JObject]', not an object.]]', not an object.]*/(&(iter_/*Not a functional type: Placeholder[input=Cannot access member 'variants' in 'Identifier[value=JObject]', not an object.]*/(&(variants))), lambda33)));
	CUnion union = new_CUnion(typeParameters, name, unionMembers);
	fields = addLast_List(&(addLast_List(&(fields), new_CDeclaration(new_Identifier(object.name + "Tag"), "variant"))), new_CDeclaration(createStructureType_Main(&((*_this)), name + "Data", object.typeParameters), "data"));
	_this->enums = addLast_List(&(_this->enums), jEnum);
	_this->structuresOrUnions = addLast_List(&(_this->structuresOrUnions), union);
	return fields;
}
CDefinable createUnionField_Main(void* _ref, char* variant, List<CType> typeArguments){
	Main* _this = (Main*) _ref;
	/*final var type = typeArguments.isEmpty() ? new Identifier(variant) : new CTemplateType(variant, typeArguments)*/;
	return new_CDeclaration(type, variant);
}
Option<JDeclaration> extractField_Main(void* _ref, JObjectMember prototype){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
Option<CStructMember> transformObjectMemberPrototype_Main(void* _ref, JObject object, JObjectMember wrapper){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
/*TODO:  resolve lambda return type*/ lambda34(void* _ref, /*TODO: resolve type of lambda param*/ name){
	return name + "_" + object.name;
}
/*TODO:  resolve lambda return type*/ lambda35(void* _ref, /*TODO: resolve type of lambda param*/ value){
	return new_Tuple(value, compileMethodsSegments_Main(&((*_this)), inputContent, 1));
}
/*TODO:  resolve lambda return type*/ lambda36(void* _ref, /*TODO: resolve type of lambda param*/ env){
	/*Not a functional type: Placeholder[input=Cannot access member 'defineAllExpressions' in 'Placeholder[input=Undefined identifier: env]', not an object.]*/ self = defineAllExpressions_/*Undefined identifier: env*/(&(env), parameters_JMethod(&(jFunctionProto)));
	return within_/*Not a functional type: Placeholder[input=Cannot access member 'defineAllExpressions' in 'Placeholder[input=Undefined identifier: env]', not an object.]*/(&(self), lambda35);
}
CStructMember completeMethodProto_Main(void* _ref, JMethod jFunctionProto, JObject object){
	Main* _this = (Main*) _ref;
	Option<char*> maybeCompiled = new_None();
	if (/*instance*/) {
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ cParameters = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]*/(&(iter_/*Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]*/(&(parameters_JMethod(&(jFunctionProto))))), /*Convert method references to a closure*/)));
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(cParameters))), /*Convert method references to a closure*/)), new_Joiner(", "));
		/*Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]]', not an object.]*/ modifiedMethodDeclaration = toCDeclaration_/*Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]*/(&(mapName_/*Undefined identifier: declaration*/(&(declaration), lambda34)));
		_this->functionDeclarations = addLast_List(&(_this->functionDeclarations), lineSeparator_int(&(generate_/*Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]]', not an object.]*/(&(modifiedMethodDeclaration)) + "(" + compiledParameters + ");" + System)));
		return new_EmptyStructMember();
	}
	if (endsWith_/*Cannot access member 'content' in 'Boolean', not an object.*/(&(startsWith_/*Cannot access member 'content' in 'Identifier[value=JMethod]', not an object.*/(&(jFunctionProto.content), "{") && jFunctionProto.content), "}")) {
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Cannot access member 'content' in 'Identifier[value=JMethod]', not an object.]', not an object.]*/ inputContent = substring_/*Cannot access member 'content' in 'Identifier[value=JMethod]', not an object.*/(&(jFunctionProto.content), 1, length_/*Not a functional type: Placeholder[input=Cannot access member 'content' in 'Identifier[value=JMethod]', not an object.]*/(&(content_JMethod(&(jFunctionProto)))) - 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]*/ within = within_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]'*/(&(_this->environment), lambda36);
		_this->environment = within.left;
		maybeCompiled = new_Some(within.right);
	}
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ cParameters = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]*/(&(iter_/*Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]*/(&(parameters_JMethod(&(jFunctionProto))))), /*Convert method references to a closure*/)));
	if (/*instance*/) 
		cParameters = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(cParameters), new_CDeclaration(new_CPointerType(CPrimitiveType.Void), "_ref"));
	char* outputContent = computeMethodBody_Main(&((*_this)), typeParameters_JMethod(&(jFunctionProto)), methodDeclaration_JMethod(&(jFunctionProto)), cParameters, maybeCompiled, object.name, object.variants);
	CDefinable mapped = transformMethodDeclaration_Main(&((*_this)), object.name, typeParameters_JMethod(&(jFunctionProto)), methodDeclaration_JMethod(&(jFunctionProto)));
	CFunctionHeader header = new_CFunctionHeader(mapped, cParameters);
	CFunction cFunction = new_CFunction(header, outputContent);
	_this->functionDeclarations = addLast_List(&(_this->functionDeclarations), lineSeparator_int(&(generate_CFunctionHeader(&(header)) + ";" + System)));
	_this->functions = addLast_List(&(_this->functions), cFunction);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ parameterTypes = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(cParameters))), /*Convert method references to a closure*/)));
	return /*TODO: switch*/;
}
Option<JObjectMember> parseObjectMember_Main(void* _ref, char* input, char* name, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (isEmpty_String(&(stripped))) 
		return new_None();
	Option maybeEnum = parseObject_Main(&((*_this)), "enum", input);
	if (/*instance*/) 
		return new_Some(enum0);
	Option maybeInterface = parseObject_Main(&((*_this)), "interface", input);
	if (/*instance*/) 
		return new_Some(interface0);
	Option maybeRecord = parseObject_Main(&((*_this)), "record", input);
	if (/*instance*/) 
		return new_Some(record0);
	Option maybeClass = parseObject_Main(&((*_this)), "class", input);
	if (/*instance*/) 
		return new_Some(class0);
	Option maybeEnumValues = parseEnumValuesStatement_Main(&((*_this)), input, name);
	if (/*instance*/) 
		return new_Some(enumValues);
	if (endsWith_String(&(stripped), ";")) {
		char* substring = substring_String(&(stripped), 0, length_String(&(stripped)) - 1);
		Option maybeDeclaration = parseDeclaration_Main(&((*_this)), substring);
		if (/*instance*/) {
			_this->environment = defineExpression_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]'*/(&(_this->environment), declaration);
			return new_Some(new_JField(declaration));
		}
	}
	Option maybeMethod = parseMethod_Main(&((*_this)), stripped, name, typeParameters);
	if (/*instance*/) 
		return new_Some(temp);
	return new_Some(new_Placeholder(stripped));
}
/*TODO:  resolve lambda return type*/ lambda37(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_bool(&(!slice));
}
Option<JObjectMember> parseMethod_Main(void* _ref, char* stripped, char* name, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	int i = indexOf_String(&(stripped), "(");
	if (i < 0) 
		return new_None();
	char* declarationString = substring_String(&(stripped), 0, i);
	char* substring1 = substring_String(&(stripped), i + 1);
	int i1 = indexOf_String(&(substring1), ")");
	if (i1 < 0) 
		return new_None();
	char* parametersString = substring_String(&(substring1), 0, i1);
	char* withBraces = strip_String(&(substring_String(&(substring1), i1 + 1)));
	List parameters = toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(toList_Iter(&(filter_Iter(&(map_Iter(&(divide_Main(&((*_this)), parametersString, new_ValueFolder())), /*Convert method references to a closure*/)), lambda37)))))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)));
	JMethodDeclaration declaration = parseMethodDeclaration_Main(&((*_this)), declarationString, name);
	JMethod proto = new_JMethod(typeParameters, parameters, declaration, withBraces);
	return new_Some(proto);
}
/*TODO:  resolve lambda return type*/ lambda38(void* _ref, /*TODO: resolve type of lambda param*/ member1){
	return /*TODO: switch*/;
}
/*TODO:  resolve lambda return type*/ lambda39(void* _ref, /*TODO: resolve type of lambda param*/ member){
	return /*!(member instanceof CFunctionDeclaration)*/;
}
List<CDefinable> retainFields_Main(void* _ref, List<CStructMember> members){
	Main* _this = (Main*) _ref;
	List list = toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(members))), lambda38)), /*Convert method references to a closure*/)));
	return toList_Iter(&(filter_Iter(&(iter_List(&(list))), lambda39)));
}
Option<CDefinable> retainDefinables_Main(void* _ref, CStructMember member){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
/*TODO:  resolve lambda return type*/ lambda40(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_bool(&(!slice));
}
List<char*> splitValues_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'split' not defined in 'magma.Main$JRecursiveType@62043840']*/ segments = split_String(&(input), quote_/*Undefined identifier: Pattern*/(&(Pattern), ","));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ list = toList_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]]', not an object.]*/(&(filter_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]*/(&(stream_/*Undefined identifier: Arrays*/(&(Arrays), segments)), /*Convert method references to a closure*/)), lambda40)));
	return new_JavaList(list);
}
/*TODO:  resolve lambda return type*/ lambda41(void* _ref, /*TODO: resolve type of lambda param*/ parameter){
	return parameter.name;
}
/*TODO:  resolve lambda return type*/ lambda42(void* _ref){
	CType type = transformType(declaration.type);
	List list = toList_Iter(&(map_Iter(&(iter_List(&(subList_List(&(cParameters), 1, size_List(&(cParameters)))))), lambda41)));
	return createBodyForAbstractMethod_Main(&((*_this)), structureVariants, type, declaration.name, list);
}
char* computeMethodBody_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration, List<CDeclaration> cParameters, Option<char*> maybeContent, char* structName, List<char*> structureVariants){
	Main* _this = (Main*) _ref;
	if (/*instance*/) {
		T compiled = orElse_Option(&(maybeContent), "?");
		return generateStatement_int(&(generateStatement_/*Undefined identifier: Main*/(&(Main), structName + " _thisInstance") + generateStatement(structName + "* _this = &_thisInstance") + compiled + Main), "return " + "_thisInstance");
	}
	if (/*instance*/) {
		/*Not a functional type: Placeholder[input=Cannot access member 'joinTypeParameters' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ joinedTypeParameters = joinTypeParameters_/*Undefined identifier: Main*/(&(Main), typeParameters);
		/*Not a functional type: Placeholder[input=Cannot access member 'generateStatement' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ thisInitialization = generateStatement_/*Undefined identifier: Main*/(&(Main), structName + joinedTypeParameters + "* _this = (" + structName + joinedTypeParameters + "*) _ref");
		T body = orElseGet_Option(&(maybeContent), lambda42);
		return thisInitialization + body;
	}
	return "?";
}
/*TODO:  resolve lambda return type*/ lambda43(void* _ref, /*TODO: resolve type of lambda param*/ variant){
	return generateCase_Main(&((*_this)), variant, name, parameterNames);
}
char* createBodyForAbstractMethod_Main(void* _ref, List<char*> variants, CType type, char* name, List<char*> parameterNames){
	Main* _this = (Main*) _ref;
	if (isEmpty_List(&(variants))) {
		C joinedParameters = collect_Iter(&(iter_List(&(addFirst_List(&(parameterNames), "_this->data")))), new_Joiner(", "));
		return generateStatement_/*Undefined identifier: Main*/(&(Main), "return _this->table." + name + "(" + joinedParameters + ")");
	}
	else {
		/*Not a functional type: Placeholder[input=Cannot access member 'generateStatement' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ returnValueDefinition = generateStatement_/*Undefined identifier: Main*/(&(Main), generate_CType(&(type)) + " _ret");
		C cases = collect_Iter(&(map_Iter(&(iter_List(&(variants))), lambda43)), new_Joiner());
		return generateStatement_int(&(returnValueDefinition + generateIndent(1) + "switch (" + "_this->variant" + ") {" + cases + generateIndent(1) + "}" + Main), "return _ret");
	}
}
/*TODO:  resolve lambda return type*/ lambda44(void* _ref, /*TODO: resolve type of lambda param*/ typeParameters0){
	return addAllLast_/*Undefined identifier: typeParameters0*/(&(typeParameters0), typeParameters);
}
/*TODO:  resolve lambda return type*/ lambda45(void* _ref, /*TODO: resolve type of lambda param*/ name){
	return name + "_" + structName;
}
CDefinable transformMethodDeclaration_Main(void* _ref, char* structName, List<char*> typeParameters, JMethodDeclaration methodDeclaration){
	Main* _this = (Main*) _ref;
	return mapName_/*Not a functional type: Placeholder[input=Cannot access member 'mapTypeParameters' in 'Identifier[value=CDefinable]', not an object.]*/(&(mapTypeParameters_CDefinable(&(convertToFunctionDeclarations_Main(&((*_this)), typeParameters, methodDeclaration)), lambda44)), lambda45);
}
CDefinable convertToFunctionDeclarations_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
CType toConstructorReturnType_Main(void* _ref, char* base, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	if (isEmpty_String(&(base))) 
		return new_Identifier(base);
	/*final var typeArguments = typeParameters.iter().<CType>map(Identifier::new).toList()*/;
	if (isEmpty_/*Undefined identifier: typeArguments*/(&(typeArguments))) 
		return new_Identifier(base);
	return new_CTemplateType(base, typeArguments);
}
/*TODO:  resolve lambda return type*/ lambda46(void* _ref, /*TODO: resolve type of lambda param*/ input){
	return compileMethodSegment_Main(&((*_this)), input, indent);
}
char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent){
	Main* _this = (Main*) _ref;
	return compileStatements_Main(&((*_this)), inputContent, lambda46);
}
char* generateCase_Main(void* _ref, char* variant, char* name, List<char*> parameterNames){
	Main* _this = (Main*) _ref;
	char* s = "&(_this->data." + variant + ")";
	C joined = collect_Iter(&(iter_List(&(addFirst_List(&(copy_List(&(parameterNames))), s)))), new_Joiner(", "));
	return generateIndent(2) + "case " + variant + "Variant:" + generateStatement(3, "_ret = " + name + "_" + variant + "(" + joined + ")") + generateStatement(3, "break");
}
/*TODO:  resolve lambda return type*/ lambda47(void* _ref){
	return map_Option(&(parseDeclaration_Main(&((*_this)), declaration)), /*Convert method references to a closure*/);
}
/*TODO:  resolve lambda return type*/ lambda48(void* _ref){
	return new_Placeholder(declaration);
}
JMethodDeclaration parseMethodDeclaration_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	return orElseGet_Option(&(or_Option(&(parseConstructor_Main(&((*_this)), declaration, structName)), lambda47)), lambda48);
}
JMethodDeclaration toInterface_Main(void* _ref, JDeclaration value){
	Main* _this = (Main*) _ref;
	return value;
}
Option<JMethodDeclaration> parseConstructor_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(declaration));
	if (equals_String(&(stripped), structName)) 
		return new_Some(new_JConstructor(structName));
	int i = lastIndexOf_String(&(stripped), " ");
	if (i >= 0) {
		char* substring = strip_String(&(substring_String(&(stripped), i + 1)));
		if (equals_String(&(substring), structName)) 
			return new_Some(new_JConstructor(structName));
	}
	return new_None();
}
Option<JObjectMember> parseEnumValuesStatement_Main(void* _ref, char* input, char* structName){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (endsWith_String(&(stripped), ";")) 
		return parseEnumValues_Main(&((*_this)), structName, substring_String(&(stripped), 0, length_String(&(stripped)) - 1));
	return parseEnumValues_Main(&((*_this)), structName, stripped);
}
/*TODO:  resolve lambda return type*/ lambda49(void* _ref, /*TODO: resolve type of lambda param*/ state, /*TODO: resolve type of lambda param*/ character){
	return apply_ValueFolder(&(new_ValueFolder()), state, character);
}
/*TODO:  resolve lambda return type*/ lambda50(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_bool(&(!slice));
}
/*TODO:  resolve lambda return type*/ lambda51(void* _ref, /*TODO: resolve type of lambda param*/ enumValue){
	return compileEnumValue_Main(&((*_this)), structName, enumValue);
}
Option<JObjectMember> parseEnumValues_Main(void* _ref, char* structName, char* input){
	Main* _this = (Main*) _ref;
	List enumValues = toList_Iter(&(filter_Iter(&(map_Iter(&(divide_Main(&((*_this)), input, lambda49)), /*Convert method references to a closure*/)), lambda50)));
	if (isEmpty_bool(&(!enumValues))) {
		Iter optionStream = map_Iter(&(iter_List(&(enumValues))), lambda51);
		/*final var areAnyInvalid =
					(boolean) optionStream.collect(new AnyMatch<Option<CStructMember>>(option -> option instanceof None<CStructMember>))*/;
		if (areAnyInvalid) 
			return new_None();
	}
	return new_Some(new_EmptyStructMember());
}
Option<CStructMember> compileEnumValue_Main(void* _ref, char* structName, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (endsWith_String(&(stripped), ")")) {
		char* substring = substring_String(&(stripped), 0, length_String(&(stripped)) - 1);
		int i = indexOf_String(&(substring), "(");
		if (i >= 0) {
			char* name = substring_String(&(substring), 0, i);
			if (isIdentifier_bool(&(!Identifier), name)) 
				return new_None();
			char* substring2 = substring_String(&(substring), i + 1);
			/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Int', not an object.]*/ generated = lineSeparator_int(&(structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" + System));
			_this->globals = addLast_List(&(_this->globals), generated);
			return new_Some(new_EmptyStructMember());
		}
	}
	if (isIdentifier_Identifier(&(Identifier), stripped)) {
		/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Int', not an object.]*/ generated = lineSeparator_int(&(structName + " " + structName + stripped + " = " + "new_" + structName + "()" + ";" + System));
		_this->globals = addLast_List(&(_this->globals), generated);
		return new_Some(new_EmptyStructMember());
	}
	return new_None();
}
char* compileMethodSegment_Main(void* _ref, char* input, int indent){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (isEmpty_String(&(stripped))) 
		return "";
	Option maybeIf = compileConditional_Main(&((*_this)), "if", indent, stripped);
	if (/*instance*/) 
		return result;
	Option maybeWhile = compileConditional_Main(&((*_this)), "while", indent, stripped);
	if (/*instance*/) 
		return result;
	if (startsWith_String(&(stripped), "else ")) {
		char* substring = strip_String(&(substring_String(&(stripped), length_String(&("else ")))));
		if (endsWith_bool(&(startsWith_String(&(substring), "{") && substring), "}")) {
			char* substring1 = substring_String(&(substring), 1, length_String(&(substring)) - 1);
			return generateIndent(indent) + "else {" + compileMethodsSegments_Main(&((*_this)), substring1, indent + 1) + generateIndent(indent) + "}";
		}
		else 
			return compileMethodSegment_int(&(generateIndent(indent) + "else " + (*_this)), substring, indent + 1);
	}
	if (endsWith_String(&(stripped), ";")) {
		char* substring = substring_String(&(stripped), 0, length_String(&(stripped)) - 1);
		return generateIndent(indent) + compileMethodStatement_Main(&((*_this)), substring) + ";";
	}
	if (startsWith_String(&(stripped), "//")) 
		return generateIndent(indent) + stripped;
	return wrap_int(&(lineSeparator_/*Undefined identifier: System*/(&(System)) + "\t" + Placeholder), stripped);
}
/*TODO:  resolve lambda return type*/ lambda52(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_bool(&(!slice));
}
Option<char*> compileConditional_Main(void* _ref, char* type, int indent, char* input){
	Main* _this = (Main*) _ref;
	if (startsWith_String(&(input), type)) {
		char* substring = strip_String(&(substring_String(&(input), length_String(&(type)))));
		if (startsWith_String(&(substring), "(")) {
			char* afterConditionStart = strip_String(&(substring_String(&(substring), 1)));
			List divisions = toList_Iter(&(filter_Iter(&(map_Iter(&(divide_Main(&((*_this)), afterConditionStart, new_EscapedFolder(new_ConditionEndLocator()))), /*Convert method references to a closure*/)), lambda52)));
			if (size_List(&(divisions)) < 2) 
				return new_None();
			T first = getFirst_List(&(divisions));
			char* maybeWithBraces = joinStrings_Main(&((*_this)), subList_List(&(divisions), 1, size_List(&(divisions))));
			if (endsWith_bool(&(!first), ")")) 
				return new_None();
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Identifier[value=T]', not an object.]*/ condition = substring_T(&(first), 0, length_T(&(first)) - 1);
			if (endsWith_bool(&(startsWith_String(&(maybeWithBraces), "{") && maybeWithBraces), "}")) {
				char* content = substring_String(&(maybeWithBraces), 1, length_String(&(maybeWithBraces)) - 1);
				return new_Some(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" + compileMethodsSegments_Main(&((*_this)), content, indent + 1) + generateIndent(indent) + "}");
			}
			return new_Some(compileMethodSegment_int(&(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") " + (*_this)), maybeWithBraces, indent + 1));
		}
	}
	return new_None();
}
char* compileMethodStatement_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (equals_String(&(stripped), "break")) 
		return "break";
	if (startsWith_String(&(stripped), "return ")) 
		return compileExpressionOrPlaceholder_int(&("return " + (*_this)), substring_String(&(stripped), length_String(&("return "))));
	Option maybeAssignment = compileAssignment_Main(&((*_this)), stripped);
	if (/*instance*/) 
		return assignment;
	Option maybeInvokable = parseInvokable_Main(&((*_this)), stripped);
	if (/*instance*/) 
		return generate_CExpression(&(transformExpression_Main(&((*_this)), value)));
	Option instance = post_Main(&((*_this)), stripped, "++");
	if (/*instance*/) 
		return x;
	Option instance0 = post_Main(&((*_this)), stripped, "--");
	if (/*instance*/) 
		return x;
	Option maybeDeclaration = parseDeclaration_Main(&((*_this)), input);
	if (/*instance*/) 
		return generate_/*Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]*/(&(toCDeclaration_/*Undefined identifier: declaration*/(&(declaration))));
	return wrap_Placeholder(&(Placeholder), stripped);
}
Option<char*> compileAssignment_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	int index = indexOf_String(&(stripped), "=");
	if (index >= 0) {
		char* destination = substring_String(&(stripped), 0, index);
		char* substring1 = substring_String(&(stripped), index + 1);
		JAssignable assignable = parseAssignable_Main(&((*_this)), destination);
		Option maybeSource = parseExpression_Main(&((*_this)), substring1);
		if (/*instance*/) {
			CAssignable cAssignable = transformAssignable_Main(&((*_this)), assignable, source);
			return new_Some(generate_/*Not a functional type: Placeholder[input=Cannot access member 'transformExpression' in 'Int', not an object.]*/(&(transformExpression_int(&(generate_CAssignable(&(cAssignable)) + " = " + (*_this)), source))));
		}
	}
	return new_None();
}
CAssignable transformAssignable_Main(void* _ref, JAssignable assignable, JExpression source){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
JType resolveType_Main(void* _ref, JExpression source, JType type){
	Main* _this = (Main*) _ref;
	if (equals_JType(&(type), JPrimitiveType.Var)) 
		return resolveExpression_Main(&((*_this)), source);
	return type;
}
JType resolveExpression_Main(void* _ref, JExpression source){
	Main* _this = (Main*) _ref;
	return cleanupType_Main(&((*_this)), resolveUncleanedExpression_Main(&((*_this)), source));
}
JType resolveUncleanedExpression_Main(void* _ref, JExpression source){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
JType resolveIdentifier_Main(void* _ref, char* value){
	Main* _this = (Main*) _ref;
	if (equals_String(&(value), "this")) 
		return /*this.environment
				.resolveCurrent()
				.<JType>map(thisType -> thisType)
				.orElseGet(() -> new Placeholder("Not within a struct"))*/;
	/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'resolveExpression' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]]', not an object.]*/ maybeFound = map_/*Not a functional type: Placeholder[input=Cannot access member 'resolveExpression' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]*/(&(resolveExpression_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]'*/(&(_this->environment), value)), /*Convert method references to a closure*/);
	if (/*instance*/) 
		return found;
	if (/*instance*/) 
		return resolved;
	return new_Placeholder("Undefined identifier: " + value);
}
JType cleanupType_Main(void* _ref, JType found){
	Main* _this = (Main*) _ref;
	if (/*instance*/) {
		/*Not a functional type: Placeholder[input=Cannot access member 'resolveType' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]*/ resolved = resolveType_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Int, name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@62043840]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int], returnType=magma.Main$JRecursiveType@62043840], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JConstruction]], returnType=JGenericType[base=Tuple, typeArguments=[Identifier[value=CExpression], JGenericType[base=List, typeArguments=[Identifier[value=CType]]]]]], name='destroyConstruction'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=Map, typeArguments=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]], magma.Main$JRecursiveType@62043840], returnType=Void], name='removeKeyFromValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='removeDuplicates'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@62043840], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CNamedType]], name='createStructureType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=Identifier[value=CDefinable]], name='createUnionField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=CType], magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CDefinable]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], returnType=magma.Main$JRecursiveType@62043840], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Int, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=magma.Main$JRecursiveType@62043840], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@62043840], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840, Identifier[value=Operator]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@62043840]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Int], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@62043840], returnType=Identifier[value=JType]], name='parseType'}]]'*/(&(_this->environment), genericType.base);
		if (/*instance*/) 
			return objType;
		else 
			return new_Placeholder("Generic type '" + genericType.base + "' has not been defined");
	}
	return found;
}
/*TODO:  resolve lambda return type*/ lambda53(void* _ref){
	return new_Placeholder("Member '" + name + "' not defined in '" + instanceType + "'");
}
JType resolveMember_Main(void* _ref, JObjectType type, JType instanceType, char* name){
	Main* _this = (Main*) _ref;
	return orElseGet_/*Not a functional type: Placeholder[input=Cannot access member 'resolve' in 'Identifier[value=JObjectType]', not an object.]*/(&(resolve_JObjectType(&(type), name)), lambda53);
}
JType resolveCaller_Main(void* _ref, JCaller caller){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
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
	if (endsWith_String(&(stripped), slice)) {
		char* instance = substring_String(&(stripped), 0, length_String(&(stripped)) - 2);
		return new_Some(compileExpressionOrPlaceholder_Main(&((*_this)), instance) + slice);
	}
	return new_None();
}
/*TODO:  resolve lambda return type*/ lambda54(void* _ref){
	return wrap_Placeholder(&(Placeholder), input);
}
char* compileExpressionOrPlaceholder_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return orElseGet_Option(&(map_Option(&(parseCExpression_Main(&((*_this)), input)), /*Convert method references to a closure*/)), lambda54);
}
Option<CExpression> parseCExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return map_Option(&(parseExpression_Main(&((*_this)), input)), /*Convert method references to a closure*/);
}
/*TODO:  resolve lambda return type*/ lambda55(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.NotEquals);
}
/*TODO:  resolve lambda return type*/ lambda56(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.LessThan);
}
/*TODO:  resolve lambda return type*/ lambda57(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.Add);
}
/*TODO:  resolve lambda return type*/ lambda58(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.Subtract);
}
/*TODO:  resolve lambda return type*/ lambda59(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.And);
}
/*TODO:  resolve lambda return type*/ lambda60(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.Or);
}
/*TODO:  resolve lambda return type*/ lambda61(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.GreaterThanOrEquals);
}
Option<JExpression> parseExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (startsWith_String(&(stripped), "switch ")) 
		return new_Some(new_Placeholder("TODO: switch"));
	int i2 = lastIndexOf_String(&(stripped), "::");
	if (i2 >= 0) {
		char* substring = substring_String(&(stripped), 0, i2);
		char* name = strip_String(&(substring_String(&(stripped), i2 + 2)));
		if (isIdentifier_Identifier(&(Identifier), name)) {
			char* compiled = compileExpressionOrPlaceholder_Main(&((*_this)), substring);
			return new_Some(new_Placeholder("Convert method references to a closure"));
		}
	}
	if (endsWith_bool(&(startsWith_String(&(stripped), "'") && stripped), "'")) 
		return new_Some(new_Char(stripped));
	Option maybeLambda = compileLambda_Main(&((*_this)), stripped);
	if (/*instance*/) 
		return maybeLambda;
	int i3 = indexOf_String(&(stripped), "instanceof");
	if (i3 >= 0) {
		char* substring = substring_String(&(stripped), 0, i3);
		char* substring1 = strip_String(&(substring_String(&(stripped), length_int(&(i3 + "instanceof")))));
		Option maybeInstance = map_Option(&(parseCExpression_Main(&((*_this)), substring)), /*Convert method references to a closure*/);
		if (/*instance*/) {
			int i4 = indexOf_String(&(substring1), "<");
			return new_Some(new_Placeholder("instance"));
		}
	}
	int i = lastIndexOf_String(&(stripped), ".");
	if (i >= 0) {
		char* instanceString = substring_String(&(stripped), 0, i);
		char* memberName = strip_String(&(substring_String(&(stripped), i + 1)));
		if (isIdentifier_Identifier(&(Identifier), memberName)) {
			Option maybeInstance = parseExpression_Main(&((*_this)), instanceString);
			if (/*instance*/) 
				return new_Some(new_JMemberAccess(value, memberName));
		}
	}
	Option maybeInvokable = parseInvokable_Main(&((*_this)), stripped);
	if (/*instance*/) 
		return maybeInvokable;
	Option maybeOperator = or_Option(&(or_Option(&(or_Option(&(or_Option(&(or_Option(&(or_Option(&(or_Option(&(compileOperator_Main(&((*_this)), stripped, Operator.Equals)), lambda55)), lambda56)), lambda57)), lambda58)), lambda59)), lambda60)), lambda61);
	if (/*instance*/) 
		return maybeOperator;
	if (isIdentifier_Identifier(&(Identifier), stripped)) 
		return new_Some(new_Identifier(stripped));
	if (startsWith_String(&(stripped), "!")) {
		char* substring = substring_String(&(stripped), 1);
		Option maybeInstance = parseCExpression_Main(&((*_this)), substring);
		if (/*instance*/) 
			return new_Some(new_JNot(instance));
	}
	if (isNumber_Main(&((*_this)), stripped)) 
		return new_Some(new_JNumber(stripped));
	if (startsWith_String(&(stripped), "\"") && endsWith_String(&(stripped), "\"") && length_String(&(stripped)) >= 2) 
		return new_Some(new_StringNode(substring_String(&(stripped), 1, length_String(&(stripped)) - 1)));
	return new_None();
}
/*TODO:  resolve lambda return type*/ lambda62(void* _ref, /*TODO: resolve type of lambda param*/ param){
	return new_CDeclaration(new_Placeholder("TODO: resolve type of lambda param"), param);
}
Option<JExpression> compileLambda_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	int index = indexOf_String(&(input), "->");
	if (index < 0) 
		return new_None();
	char* beforeContent = strip_String(&(substring_String(&(input), 0, index)));
	char* maybeWithBraces = strip_String(&(substring_String(&(input), index + 2)));
	Option maybeParams = parseLambdaParams_Main(&((*_this)), beforeContent);
	if (/*!(maybeParams instanceof Some<List<String>>(var params))*/) 
		return new_None();
	/*Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ paramList = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]]', not an object.]*/(&(toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]*/(&(iter_/*Undefined identifier: params*/(&(params))), lambda62)))), new_CDeclaration(new_CPointerType(CPrimitiveType.Void), "_ref"));
	char* output;
	if (endsWith_bool(&(startsWith_String(&(maybeWithBraces), "{") && maybeWithBraces), "}")) {
		char* content = substring_String(&(maybeWithBraces), 1, length_String(&(maybeWithBraces)) - 1);
		output = compileMethodsSegments_Main(&((*_this)), content, 1);
	}
	else 
		output = generateStatement_/*Undefined identifier: Main*/(&(Main), compileExpressionOrPlaceholder_int(&("return " + (*_this)), maybeWithBraces));
	char* generatedName = generateName_Main(&((*_this)));
	CFunction cFunction = new_CFunction(new_CFunctionHeader(new_CDeclaration(new_Placeholder("TODO:  resolve lambda return type"), generatedName), paramList), output);
	_this->functions = addLast_List(&(_this->functions), cFunction);
	return new_Some(new_Identifier(generatedName));
}
/*TODO:  resolve lambda return type*/ lambda63(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_bool(&(!slice));
}
Option<List<char*>> parseLambdaParams_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	if (isIdentifier_Identifier(&(Identifier), input)) 
		return new_Some(of_Lists(&(Lists), input));
	else 
		if (startsWith_String(&(input), "(") && input.endsWith(")")) {
			char* substring = substring_String(&(input), 1, length_String(&(input)) - 1);
			List list = toList_Iter(&(filter_Iter(&(map_Iter(&(divide_Main(&((*_this)), substring, new_ValueFolder())), /*Convert method references to a closure*/)), lambda63)));
			return new_Some(list);
		}
	else 
		return new_None();
}
char* generateName_Main(void* _ref){
	Main* _this = (Main*) _ref;
	/*Cannot access member 'counter' in 'Int', not an object.*/ generatedName = "lambda" + (*_this).counter;
	_this->counter++;
	return generatedName;
}
Option<JExpression> compileOperator_Main(void* _ref, char* input, Operator operator){
	Main* _this = (Main*) _ref;
	if (length_String(&(input)) < 3) 
		return new_None();
	/*Cannot access member 'value' in 'Identifier[value=Operator]', not an object.*/ operatorValue = operator.value;
	if (contains_bool(&(!input), operatorValue)) 
		return new_None();
	int i1 = -1;
	int depth = 0;
	int i = 0;
	while (i < length_String(&(input)) - 1) {
		char c = charAt_String(&(input), i);
		if (/*c == operatorValue.charAt(0)*/) 
			if (/*depth == 0*/) {
				i1 = i;
				break;
			}
		if (/*c == '('*/) 
			depth++;
		if (/*c == ')'*/) 
			depth--;
		i++;
	}
	if (i1 >= 0) {
		char* leftString = substring_String(&(input), 0, i1);
		char* rightString = substring_String(&(input), length_int(&(i1 + operatorValue)));
		if (/*instance*/) 
			if (/*instance*/) 
				return new_Some(new_JOperator(leftCompiled, operator, rightCompiled));
	}
	return new_None();
}
Option<JExpression> parseInvokable_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	if (endsWith_bool(&(!stripped), ")")) 
		return new_None();
	int length = length_String(&(stripped));
	char* withoutEnd = substring_String(&(stripped), 0, length - 1);
	int callerStart = findCallerStart_Main(&((*_this)), withoutEnd);
	if (callerStart < 0) 
		return new_None();
	char* callerString = substring_String(&(withoutEnd), 0, callerStart);
	char* argumentsString = substring_String(&(withoutEnd), callerStart + 1);
	Option maybeCaller = parseCaller_Main(&((*_this)), callerString);
	if (/*!(maybeCaller instanceof Some(var value))*/) 
		return new_None();
	List arguments = toList_Iter(&(flatMap_Iter(&(map_Iter(&(divide_Main(&((*_this)), argumentsString, new_EscapedFolder(new_ValueFolder()))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/)));
	return new_Some(new_JInvokable(value, arguments));
}
int findCallerStart_Main(void* _ref, char* withoutEnd){
	Main* _this = (Main*) _ref;
	int callerStart = -1;
	int depth = 0;
	int i = 0;
	while (length_bool(&(i < withoutEnd))) {
		char c = charAt_String(&(withoutEnd), i);
		if (/*c == '('*/) {
			if (/*depth == 0*/) 
				callerStart = i;
			depth++;
		}
		if (/*c == ')'*/) 
			depth--;
		i++;
	}
	return callerStart;
}
int isNumber_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (isEmpty_String(&(stripped))) 
		return false;
	if (startsWith_String(&(stripped), "-")) 
		return allDigits_Main(&((*_this)), substring_String(&(stripped), 1));
	return allDigits_Main(&((*_this)), stripped);
}
int allDigits_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return allMatch_/*Not a functional type: Placeholder[input=Cannot access member 'mapToObj' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'range' in 'Placeholder[input=Undefined identifier: IntStream]', not an object.]]', not an object.]*/(&(mapToObj_/*Not a functional type: Placeholder[input=Cannot access member 'range' in 'Placeholder[input=Undefined identifier: IntStream]', not an object.]*/(&(range_/*Undefined identifier: IntStream*/(&(IntStream), 0, length_String(&(input)))), /*Convert method references to a closure*/)), /*Convert method references to a closure*/);
}
Option<JCaller> parseCaller_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	Option maybeExpression = parseExpression_Main(&((*_this)), stripped);
	if (/*instance*/) 
		return new_Some(expression);
	if (startsWith_String(&(stripped), "new ")) {
		char* type = substring_String(&(stripped), length_String(&("new ")));
		JType jType = parseType_Main(&((*_this)), type);
		return new_Some(new_JConstruction(jType));
	}
	return new_None();
}
Option<JDeclaration> parseDeclaration_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	int nameSeparator = lastIndexOf_String(&(stripped), " ");
	if (nameSeparator >= 0) {
		char* beforeName = strip_String(&(substring_String(&(stripped), 0, nameSeparator)));
		char* name = strip_String(&(substring_String(&(stripped), nameSeparator + 1)));
		int typeSeparator = findTypeSeparator_Main(&((*_this)), beforeName);
		if (isIdentifier_bool(&(!Identifier), name)) 
			return new_None();
		if (typeSeparator < 0) {
			JType type = parseType_Main(&((*_this)), beforeName);
			return new_Some(new_JDeclaration(name, type));
		}
		char* beforeType = strip_String(&(substring_String(&(beforeName), 0, typeSeparator)));
		List<char*> copy = empty_Lists(&(Lists));
		if (endsWith_String(&(beforeType), ">")) {
			char* substring = substring_String(&(beforeType), 0, length_String(&(beforeType)) - 1);
			int i = indexOf_String(&(substring), "<");
			if (i >= 0) {
				char* substring2 = substring_String(&(substring), i + 1);
				copy = splitValues_Main(&((*_this)), substring2);
				beforeType = substring_String(&(substring), 0, i);
			}
		}
		List<char*> annotations = empty_Lists(&(Lists));
		int i = lastIndexOf_String(&(beforeType), "\n");
		if (i >= 0) {
			annotations = collectAnnotations_Main(&((*_this)), substring_String(&(beforeType), 0, i));
			beforeType = strip_String(&(substring_String(&(beforeType), i + 1)));
		}
		if (isIdentifier_Identifier(&(Identifier), name)) {
			JType type = parseType_Main(&((*_this)), substring_String(&(beforeName), typeSeparator + 1));
			JDeclaration jDeclaration = new_JDeclaration(annotations, copy, new_Some(beforeType), type, name);
			return new_Some(jDeclaration);
		}
	}
	return new_None();
}
/*TODO:  resolve lambda return type*/ lambda64(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_bool(&(!slice));
}
/*TODO:  resolve lambda return type*/ lambda65(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return substring_/*Undefined identifier: slice*/(&(slice), 1);
}
List<char*> collectAnnotations_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return toList_Iter(&(map_Iter(&(map_Iter(&(filter_Iter(&(fromObjArray_Streams(&(Streams), split_String(&(input), quote_/*Undefined identifier: Pattern*/(&(Pattern), "\n")))), lambda64)), lambda65)), /*Convert method references to a closure*/)));
}
int findTypeSeparator_Main(void* _ref, char* beforeName){
	Main* _this = (Main*) _ref;
	int typeSeparator = -1;
	int depth = 0;
	int i = 0;
	while (length_bool(&(i < beforeName))) {
		char c = charAt_String(&(beforeName), i);
		if (/*c == ' ' && depth == 0*/) 
			typeSeparator = i;
		if (/*c == '<'*/) 
			depth++;
		if (/*c == '>'*/) 
			depth--;
		i++;
	}
	return typeSeparator;
}
JType parseType_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	/*switch (stripped) {
			case "boolean", "Boolean" -> {
				return JPrimitiveType.Boolean;
			}
			case "int", "Integer" -> {
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
	if (endsWith_String(&(stripped), "[]")) {
		char* slice = substring_String(&(stripped), 0, length_String(&(stripped)) - 2);
		JType type = parseType_Main(&((*_this)), slice);
		return new_JArrayType(type);
	}
	if (endsWith_String(&(stripped), ">")) {
		char* substring = substring_String(&(stripped), 0, length_String(&(stripped)) - 1);
		int i = indexOf_String(&(substring), "<");
		if (i >= 0) {
			char* base = substring_String(&(substring), 0, i);
			char* parameters = substring_String(&(substring), i + 1);
			List list = toList_Iter(&(map_Iter(&(divide_Main(&((*_this)), parameters, new_ValueFolder())), /*Convert method references to a closure*/)));
			return new_JGenericType(base, list);
		}
	}
	if (isIdentifier_Identifier(&(Identifier), stripped)) 
		return new_Identifier(stripped);
	// TODO: handle varargs through monomorphization
	return new_Placeholder(stripped);
}
