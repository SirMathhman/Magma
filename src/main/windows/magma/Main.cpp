struct CPrimitiveType;
struct JPrimitiveType;
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
struct CFunctionDeclaration;
struct CAssignable;
struct JType;
struct JAssignable;
struct JExpression;
struct CExpression;
struct JCaller;
struct CDefinable;
struct CStructureOrUnion;
struct JObjectMember;
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
struct FunctionDeclaration;
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
struct JExpressionWrapper;
struct CExpressionWrapper;
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
struct Main;
enum OptionVariant {
	NoneVariant,
	SomeVariant
};
enum ResultVariant {
	ErrVariant,
	OkVariant
};
enum CTypeVariant {
	IdentifierVariant,
	PlaceholderVariant,
	CPointerTypeVariant,
	CPrimitiveTypeVariant,
	CTemplateTypeVariant
};
enum JMethodDeclarationVariant {
	JConstructorVariant,
	JDeclarationVariant,
	PlaceholderVariant
};
enum CStructMemberVariant {
	EmptyStructMemberVariant,
	CFieldVariant,
	FunctionDeclarationVariant,
	PlaceholderVariant
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
enum JAssignableVariant {
	JDeclarationVariant,
	JExpressionVariant,
	JExpressionWrapperVariant,
	PlaceholderVariant
};
enum JExpressionVariant {
	IdentifierVariant,
	JExpressionWrapperVariant,
	JInvokableVariant,
	JMemberAccessVariant,
	JNotVariant,
	JNumberVariant
};
enum JCallerVariant {
	JConstructionVariant,
	JExpressionVariant
};
enum CStructureOrUnionVariant {
	CStructureVariant,
	CUnionVariant
};
enum JObjectMemberVariant {
	EmptyStructMemberVariant,
	JFieldVariant,
	JMethodVariant,
	JObjectVariant,
	PlaceholderVariant
};
struct RangeHead {
	Option<int> (*next)(void*);
};
template <typename T0, typename R>
struct F1RTable {
	R (*apply)(void*, T0);
};
template <typename T0, typename R>
struct F1R {
	void* data;
	F1RTable<T0, R> table;
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
};
template <typename T>
struct List {
	void* data;
	ListTable<T> table;
};
template <typename T>
struct Some {
	T value;
	Option<R> (*map)(void*, F1R<T, R>);
	T (*orElse)(void*, T);
	Option<R> (*flatMap)(void*, F1R<T, Option<R>>);
	T (*orElseGet)(void*, FR<T>);
	Iter<T> (*iter)(void*);
	Option<T> (*or)(void*, FR<Option<T>>);
	Tuple<int, T> (*toTuple)(void*, FR<T>);
};
template <typename T>
struct None {
	Option<R> (*map)(void*, F1R<T, R>);
	T (*orElse)(void*, T);
	Option<R> (*flatMap)(void*, F1R<T, Option<R>>);
	T (*orElseGet)(void*, FR<T>);
	Iter<T> (*iter)(void*);
	Option<T> (*or)(void*, FR<Option<T>>);
	Tuple<int, T> (*toTuple)(void*, FR<T>);
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
struct JPrimitiveType {
	char* (*stringify)(void*);
};
struct Placeholder {
	char* input;
	char* (*wrap)(void*, char*);
	char* (*generate)(void*);
	char* (*toBaseName)(void*);
	char* (*stringify)(void*);
	CAssignable (*toCAssignable)(void*);
	CType (*toCType)(void*);
};
struct Identifier {
	char* value;
	char* (*generate)(void*);
	char* (*toBaseName)(void*);
	char* (*stringify)(void*);
};
struct JFunctionalType {
	List<JType> parameterTypes;
	JType returnType;
	char* (*stringify)(void*);
};
struct JArrayType {
	JType type;
	CType (*toCType)(void*);
	char* (*stringify)(void*);
};
struct JObjectType {
	char* name;
	List<JDeclaration> members;
	Option<JType> (*resolve)(void*, char*);
	char* (*stringify)(void*);
};
struct JGenericType {
	char* base;
	List<JType> typeArguments;
	CType (*toCType)(void*);
	char* (*stringify)(void*);
};
struct JRecursiveType {
	JType (*create)(void*, F1R<JType, JType>);
	void (*set)(void*, JType);
	char* (*stringify)(void*);
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
struct JDeclaration {
	List<char*> annotations;
	List<char*> typeParameters;
	Option<char*> maybeBeforeType;
	JType type;
	char* name;
	char* (*toString)(void*);
	JDeclaration (*mapName)(void*, F1R<char*, char*>);
	CDeclaration (*toCDeclaration)(void*);
	CAssignable (*toCAssignable)(void*);
	JDeclaration (*withType)(void*, JType);
};
struct CPrimitiveType {
	char* (*generate)(void*);
	char* (*toBaseName)(void*);
};
struct CType {
	CTypeVariant variant;
	CTypeData data;
};
struct CTemplateType {
	char* base;
	List<CType> list;
	char* (*generate)(void*);
	char* (*toBaseName)(void*);
};
struct CPointerType {
	CType type;
	char* (*generate)(void*);
	char* (*toBaseName)(void*);
};
union CTypeData {
	Identifier Identifier;
	Placeholder Placeholder;
	CPointerType CPointerType;
	CPrimitiveType CPrimitiveType;
	CTemplateType CTemplateType;
};
struct CExpressionTable {
};
struct CExpression {
	void* data;
	CExpressionTable table;
};
struct JNot {
	CExpression instance;
};
template <typename A, typename B, typename R>
struct F2RTable {
	R (*apply)(void*, A, B);
};
template <typename T, typename R>
struct FlatMapHead {
	Option<R> (*next)(void*);
};
template <typename T>
struct ListCollector {
	List<T> (*createInitial)(void*);
	List<T> (*fold)(void*, List<T>, T);
};
struct Lists {
	List<T> (*of)(void*, /*T...*/);
};
struct CFunctionDeclarationTable {
	CFunctionDeclaration (*mapTypeParameters)(void*, F1R<List<char*>, List<char*>>);
	CFunctionDeclaration (*mapName)(void*, F1R<char*, char*>);
	char* (*generate)(void*);
};
struct JField {
	JDeclaration declaration;
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
	List<CDefinable> (*collectCFields)(void*);
	List<CFunction> (*createConversionFunctions)(void*);
	CFunction (*createConversionType)(void*, CType);
	new (*CFunctionHeader)(void*, /*CDeclaration(implementee,*/);
	new (*CFunction)(void*);
	JObjectType (*toType)(void*);
	new (*JObjectType)(void*);
	Option<JDeclaration> (*extractDefinition)(void*, JObjectMember);
	/*return*/ (*switch)(void*);
};
struct JConstructor {
	char* type;
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
struct JMethod {
	List<char*> typeParameters;
	List<JDeclaration> parameters;
	JMethodDeclaration methodDeclaration;
	char* content;
	Option<JDeclaration> (*toDeclaration)(void*);
};
struct EmptyStructMember {
	char* (*generate)(void*);
};
union JObjectMemberData {
	EmptyStructMember EmptyStructMember;
	JField JField;
	JMethod JMethod;
	JObject JObject;
	Placeholder Placeholder;
};
struct JObjectMember {
	JObjectMemberVariant variant;
	JObjectMemberData data;
};
struct CDefinableTable {
	char* (*generate)(void*);
};
struct CDefinable {
	void* data;
	CDefinableTable table;
};
struct CField {
	CDefinable declaration;
	char* (*generate)(void*);
};
struct ValueFolder {
	State (*apply)(void*, State, char);
};
template <typename T>
struct HeadTable {
	Option<T> (*next)(void*);
};
template <typename T>
struct Head {
	void* data;
	HeadTable<T> table;
};
template <typename T>
struct Iter {
	Head<T> head;
	Iter<T> (*of)(void*, T);
	Iter<T> (*empty)(void*);
	Iter<R> (*map)(void*, F1R<T, R>);
	R (*fold)(void*, R, F2R<R, T, R>);
	C (*collect)(void*, Collector<T, C>);
	List<T> (*toList)(void*);
	Iter<T> (*filter)(void*, F1R<T, int>);
	Iter<R> (*flatMap)(void*, F1R<T, Iter<R>>);
	Option<T> (*next)(void*);
};
struct CFieldAccess {
	CExpression instance;
	char* fieldName;
	char* (*generate)(void*);
};
struct JConstruction {
	JType jType;
};
struct JExpressionWrapper {
	char* content;
};
struct JMemberAccess {
	JExpression instance;
	char* memberName;
};
struct JInvokable {
	JCaller caller;
	List<JExpression> arguments;
};
struct JNumber {
	char* value;
};
union JExpressionData {
	Identifier Identifier;
	JExpressionWrapper JExpressionWrapper;
	JInvokable JInvokable;
	JMemberAccess JMemberAccess;
	JNot JNot;
	JNumber JNumber;
};
struct JExpression {
	JExpressionVariant variant;
	JExpressionData data;
};
union JCallerData {
	JConstruction JConstruction;
	JExpression JExpression;
};
struct JCaller {
	JCallerVariant variant;
	JCallerData data;
};
template <typename A, typename B, typename R>
struct F2R {
	void* data;
	F2RTable<A, B, R> table;
};
template <typename T>
struct SingleHead {
	Option<T> (*next)(void*);
};
struct CFunctionDeclaration {
	void* data;
	CFunctionDeclarationTable table;
};
struct CDeclaration {
	List<char*> typeParameters;
	CType type;
	char* name;
	CFunctionDeclaration (*mapName)(void*, F1R<char*, char*>);
	CFunctionDeclaration (*mapTypeParameters)(void*, F1R<List<char*>, List<char*>>);
	char* (*generate)(void*);
};
struct CFunctionHeader {
	CFunctionDeclaration definition;
	List<CDeclaration> parameters;
	char* (*generate)(void*);
};
struct CFunction {
	CFunctionHeader header;
	char* content;
	char* (*generate)(void*);
};
template <typename T, typename R>
struct MapHead {
	Head<T> head;
	F1R<T, R> mapper;
	Option<R> (*next)(void*);
};
struct CPointerAccess {
	CExpression instance;
	char* fieldName;
	char* (*generate)(void*);
};
template <typename T, typename X>
struct Err {
	X error;
	Result<R, X> (*mapValue)(void*, F1R<T, R>);
};
template <typename T, typename X>
struct Ok {
	T value;
	Result<R, X> (*mapValue)(void*, F1R<T, R>);
};
template <typename T, typename X>
union ResultData {
	Err<T, X> Err;
	Ok<T, X> Ok;
};
struct CEnum {
	char* name;
	List<char*> variants;
	char* (*generate)(void*);
};
struct CStructure {
	List<char*> typeParameters;
	char* name;
	List<CDefinable> fields;
	char* (*generate)(void*);
};
struct ActualTable {
};
struct Actual {
	void* data;
	ActualTable table;
};
struct Frame {
	Frame (*defineAllExpressions)(void*, List<JDeclaration>);
	Option<JDeclaration> (*resolveExpression)(void*, char*);
	Frame (*defineExpression)(void*, JDeclaration);
	Option<JObjectType> (*toStructureType)(void*);
	Frame (*withObject)(void*, JObject);
	Option<JObjectType> (*resolveType)(void*, char*);
	Frame (*defineAllTypes)(void*, List<JObjectType>);
};
struct FolderTable {
	State (*apply)(void*, State, char);
};
struct Folder {
	void* data;
	FolderTable table;
};
struct CUnion {
	List<char*> typeParameters;
	char* name;
	List<char*> members;
	char* (*generate)(void*);
};
union CStructureOrUnionData {
	CStructure CStructure;
	CUnion CUnion;
};
struct StringBuilders {
	StringBuilder (*empty)(void*);
};
struct Paths {
};
struct PathTable {
	Path (*resolveSibling)(void*, char*);
	Option<IOError> (*writeString)(void*, char*);
	Result<char*, IOError> (*readString)(void*);
	Option<Path> (*getParent)(void*);
	Option<IOError> (*createDirectories)(void*);
};
struct Path {
	void* data;
	PathTable table;
};
struct CStructureOrUnion {
	CStructureOrUnionVariant variant;
	CStructureOrUnionData data;
};
template <typename T>
struct EmptyHead {
	Option<T> (*next)(void*);
};
union JAssignableData {
	JDeclaration JDeclaration;
	JExpression JExpression;
	JExpressionWrapper JExpressionWrapper;
	Placeholder Placeholder;
};
struct ConditionEndLocator {
	State (*apply)(void*, State, char);
};
struct CInvocation {
	CExpression expression;
	List<CExpression> cArguments;
	char* (*generate)(void*);
};
template <typename A, typename B>
struct Tuple {
	A left;
	B right;
};
struct FunctionDeclaration {
	CType type;
	char* name;
	List<CType> parameterTypes;
	char* (*generate)(void*);
};
union CStructMemberData {
	EmptyStructMember EmptyStructMember;
	CField CField;
	FunctionDeclaration FunctionDeclaration;
	Placeholder Placeholder;
};
struct IOErrorTable {
	char* (*display)(void*);
};
template <typename T, typename C>
struct CollectorTable {
	C (*createInitial)(void*);
	C (*fold)(void*, C, T);
};
template <typename T, typename X>
struct Result {
	ResultVariant variant;
	ResultData<T, X> data;
};
struct CNot {
	CExpression instance;
	char* (*generate)(void*);
};
struct CNumber {
	char* value;
	char* (*generate)(void*);
};
struct JAssignable {
	JAssignableVariant variant;
	JAssignableData data;
};
template <typename T>
struct AnyMatch {
	F1R<T, int> predicate;
	int (*createInitial)(void*);
	int (*fold)(void*, int, T);
};
struct CDereference {
	CExpression expression;
	char* (*generate)(void*);
};
struct State {
	int (*isShallow)(void*);
	int (*isLevel)(void*);
	State (*append)(void*, char);
	Option<char> (*pop)(void*);
	State (*advance)(void*);
	State (*enter)(void*);
	State (*exit)(void*);
	Iter<char*> (*stream)(void*);
	Option<Tuple<State, char>> (*popAndAppendToTuple)(void*);
	Option<State> (*popAndAppendToOption)(void*);
	Option<char> (*peek)(void*);
};
struct Environment {
	Option<JDeclaration> (*resolveExpression)(void*, char*);
	Tuple<Environment, T> (*within)(void*, F1R<Environment, Tuple<Environment, T>>);
	Environment (*exit)(void*);
	Environment (*enter)(void*);
	Environment (*defineAllExpressions)(void*, List<JDeclaration>);
	Environment (*defineExpression)(void*, JDeclaration);
	Option<JObjectType> (*resolveCurrent)(void*);
	Environment (*withObject)(void*, JObject);
	Option<JObjectType> (*resolveType)(void*, char*);
	Environment (*defineAllTypes)(void*, List<JObjectType>);
};
struct CStructMember {
	CStructMemberVariant variant;
	CStructMemberData data;
};
struct CAssignableTable {
	char* (*generate)(void*);
};
struct CAssignable {
	void* data;
	CAssignableTable table;
};
struct StringBuilder {
	List<char> list;
	StringBuilder (*appendChar)(void*, char);
	StringBuilder (*clear)(void*);
	char* (*toString)(void*);
};
struct CQuantity {
	CExpression expression;
	char* (*generate)(void*);
};
template <typename T>
struct FRTable {
	T (*apply)(void*);
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
struct Streams {
	Iter<T> (*fromObjArray)(void*, T*);
};
struct Joiner {
	char* delimiter;
	char* (*createInitial)(void*);
	char* (*fold)(void*, char*, char*);
};
struct EscapedFolder {
	Folder folder;
	State (*apply)(void*, State, char);
};
struct CExpressionWrapper {
	char* content;
	char* (*generate)(void*);
};
struct IOError {
	void* data;
	IOErrorTable table;
};
struct Main {
	new (*Environment)(void*);
	char* (*generateTemplateString)(void*, List<char*>);
	void (*main)(void*, char**);
	char* (*generateStatement)(void*, int, char*);
	char* (*generateIndent)(void*, int);
	CType (*transformType)(void*, JType);
	CType (*transformPrimitiveType)(void*, JPrimitiveType);
	char* (*generateStatement)(void*, char*);
	char* (*joinTypeParameters)(void*, List<char*>);
	Option<JObjectType> (*extractType)(void*, JObjectMember);
	CExpression (*transformCaller)(void*, JCaller);
	CExpression (*transformExpression)(void*, JExpression);
	CExpression (*transformInvocation)(void*, JInvokable);
	Option<IOError> (*run)(void*);
	char* (*compile)(void*, char*);
	List<CStructureOrUnion> (*createTopologicallySortedList)(void*);
	void (*topologicalSortDFS)(void*, char*, java.util.Set<char*>, java.util.Set<char*>, java.util.Map<char*, java.util.Set<char*>>, java.util.Map<char*, CStructureOrUnion>, ArrayList<CStructureOrUnion>);
	java.util.Set<char*> (*extractDependencies)(void*, CStructureOrUnion, java.util.Set<char*>);
	void (*extractTypeNames)(void*, CType, java.util.Set<char*>);
	void (*extractTypeNamesFromString)(void*, char*, java.util.Set<char*>);
	char* (*joinStrings)(void*, List<char*>);
	char* (*compileStatements)(void*, char*, F1R<char*, char*>);
	char* (*compileAll)(void*, char*, F1R<char*, char*>, Folder);
	Iter<char*> (*divide)(void*, char*, Folder);
	State (*foldStatement)(void*, State, char);
	char* (*compileRootSegment)(void*, char*);
	Option<JObject> (*parseObject)(void*, char*, char*);
	Option<CStructMember> (*transformObject)(void*, JObject);
	List<CDefinable> (*handleUnsealedInterface)(void*, JObject, List<CStructMember>, List<CDefinable>);
	List<CDefinable> (*handleSealedInterface)(void*, JObject, List<CDefinable>);
	Option<JDeclaration> (*extractField)(void*, JObjectMember);
	Option<CStructMember> (*transformObjectMemberPrototype)(void*, JObject, JObjectMember);
	CStructMember (*completeMethodProto)(void*, JMethod, JObject);
	Option<JObjectMember> (*parseObjectMember)(void*, char*, char*, List<char*>);
	Option<JObjectMember> (*parseMethod)(void*, char*, char*, List<char*>);
	List<CDefinable> (*retainFields)(void*, List<CStructMember>);
	Option<CDefinable> (*retainDefinables)(void*, CStructMember);
	List<char*> (*splitValues)(void*, char*);
	int (*isIdentifier)(void*, char*);
	char* (*computeMethodBody)(void*, List<char*>, JMethodDeclaration, List<CDeclaration>, Option<char*>, char*, List<char*>);
	char* (*createBodyForAbstractMethod)(void*, List<char*>, CType, char*, List<char*>);
	CFunctionDeclaration (*transformMethodDeclaration)(void*, char*, List<char*>, JMethodDeclaration);
	CFunctionDeclaration (*convertToFunctionDeclarations)(void*, List<char*>, JMethodDeclaration);
	CType (*toConstructorReturnType)(void*, char*, List<char*>);
	char* (*compileMethodsSegments)(void*, char*, int);
	char* (*generateCase)(void*, char*, char*);
	JMethodDeclaration (*parseMethodDeclaration)(void*, char*, char*);
	JMethodDeclaration (*toInterface)(void*, JDeclaration);
	Option<JMethodDeclaration> (*parseConstructor)(void*, char*, char*);
	Option<JObjectMember> (*parseEnumValuesStatement)(void*, char*, char*);
	Option<JObjectMember> (*parseEnumValues)(void*, char*, char*);
	Option<CStructMember> (*compileEnumValue)(void*, char*, char*);
	char* (*compileMethodSegment)(void*, char*, int);
	Option<char*> (*compileConditional)(void*, char*, int, char*);
	char* (*compileMethodStatement)(void*, char*);
	Option<char*> (*compileAssignment)(void*, char*);
	CAssignable (*transformAssignable)(void*, JAssignable, JExpression);
	JType (*resolveType)(void*, JExpression, JType);
	JType (*resolveExpression)(void*, JExpression);
	JType (*resolveUncleanedExpression)(void*, JExpression);
	JType (*resolveIdentifier)(void*, char*);
	JType (*cleanupType)(void*, JType);
	JType (*resolveMember)(void*, JObjectType, JType, char*);
	JType (*resolveCaller)(void*, JCaller);
	JAssignable (*parseAssignable)(void*, char*);
	Option<char*> (*post)(void*, char*, char*);
	char* (*compileExpressionOrPlaceholder)(void*, char*);
	Option<CExpression> (*parseCExpression)(void*, char*);
	Option<JExpression> (*parseExpression)(void*, char*);
	Option<char*> (*compileLambda)(void*, char*);
	Option<List<char*>> (*parseLambdaParams)(void*, char*);
	char* (*generateName)(void*);
	Option<char*> (*compileOperator)(void*, char*, char*);
	Option<JExpression> (*parseInvokable)(void*, char*);
	int (*findCallerStart)(void*, char*);
	int (*isNumber)(void*, char*);
	int (*allDigits)(void*, char*);
	Option<JCaller> (*parseCaller)(void*, char*);
	Option<JDeclaration> (*parseDeclaration)(void*, char*);
	List<char*> (*collectAnnotations)(void*, char*);
	int (*findTypeSeparator)(void*, char*);
	JType (*parseType)(void*, char*);
};
CPrimitiveType CPrimitiveTypeVoid = new_CPrimitiveType("void");
CPrimitiveType CPrimitiveTypeChar = new_CPrimitiveType("char");
CPrimitiveType CPrimitiveTypeInt = new_CPrimitiveType("int");
JPrimitiveType JPrimitiveTypeInt = new_JPrimitiveType();
JPrimitiveType JPrimitiveTypeVoid = new_JPrimitiveType();
JPrimitiveType JPrimitiveTypeBoolean = new_JPrimitiveType();
JPrimitiveType JPrimitiveTypeChar = new_JPrimitiveType();
JPrimitiveType JPrimitiveTypeVar = new_JPrimitiveType();
CPrimitiveType new_CPrimitiveType(char* content);
char* generate_CPrimitiveType(void* _ref);
char* toBaseName_CPrimitiveType(void* _ref);
char* stringify_JPrimitiveType(void* _ref);
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
Path resolveSibling_Path(void* _ref, char* sibling);
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
char* stringify_JType(void* _ref);
char* generate_CDefinable(void* _ref);
char* generate_CStructureOrUnion(void* _ref);
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
CTemplateType new_CTemplateType(char* base, List<CType> list);
char* generate_CTemplateType(void* _ref);
char* toBaseName_CTemplateType(void* _ref);
char* generate_CQuantity(void* _ref);
char* generate_CDereference(void* _ref);
Identifier new_Identifier(char* value);
char* generate_Identifier(void* _ref);
char* toBaseName_Identifier(void* _ref);
char* stringify_Identifier(void* _ref);
char* wrap_Placeholder(void* _ref, char* input);
char* generate_Placeholder(void* _ref);
char* toBaseName_Placeholder(void* _ref);
char* stringify_Placeholder(void* _ref);
CAssignable toCAssignable_Placeholder(void* _ref);
CType toCType_Placeholder(void* _ref);
JDeclaration new_JDeclaration(char* name, JType type);
char* toString_JDeclaration(void* _ref);
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
Joiner new_Joiner();
char* createInitial_Joiner(void* _ref);
char* fold_Joiner(void* _ref, char* current, char* element);
template <typename T>
List<T> createInitial_ListCollector(void* _ref);
template <typename T>
List<T> fold_ListCollector(void* _ref, List<T> tList, T t);
Path get_Paths(char* first, /*String...*/ more);
CDeclaration new_CDeclaration(CType type, char* name);
CFunctionDeclaration mapName_CDeclaration(void* _ref, F1R<char*, char*> mapper);
CFunctionDeclaration mapTypeParameters_CDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper);
char* generate_CDeclaration(void* _ref);
char* generate_CExpressionWrapper(void* _ref);
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
char* generate_CStructure(void* _ref);
char* generate_CEnum(void* _ref);
char* generate_CUnion(void* _ref);
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
new CFunctionHeader_JObject(void* _ref, /*CDeclaration(implementee,*/ conversionFunctionName);
new CFunction_JObject(void* _ref);
JObjectType toType_JObject(void* _ref);
/*final var memberDefinitions = this.children.iter*/();
new JObjectType_JObject(void* _ref);
Option<JDeclaration> extractDefinition_JObject(void* _ref, JObjectMember child);
/*return*/ switch_JObject(void* _ref);
/*case JField jField -> new Some<JDeclaration>*/();
/*case JMethod jMethod -> jMethod.toDeclaration*/();
/*case EmptyStructMember _,JObject _,Placeholder _ -> new None<JDeclaration>*/();
char* generate_CFunctionHeader(void* _ref);
char* generate_CFunction(void* _ref);
Option<JDeclaration> toDeclaration_JMethod(void* _ref);
JNumber new_JNumber(char* value);
CNumber new_CNumber();
CNumber new_CNumber(char* value);
char* generate_CNumber(void* _ref);
char* generate_CNot(void* _ref);
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
CExpression transformCaller_Main(void* _ref, JCaller jCaller);
CExpression transformExpression_Main(void* _ref, JExpression expression);
CExpression transformInvocation_Main(void* _ref, JInvokable jInvokable);
Option<IOError> run_Main(void* _ref);
char* compile_Main(void* _ref, char* input);
List<CStructureOrUnion> createTopologicallySortedList_Main(void* _ref);
void topologicalSortDFS_Main(void* _ref, char* name, java.util.Set<char*> visited, java.util.Set<char*> visiting, java.util.Map<char*, java.util.Set<char*>> dependencies, java.util.Map<char*, CStructureOrUnion> nameToStructure, ArrayList<CStructureOrUnion> result);
java.util.Set<char*> extractDependencies_Main(void* _ref, CStructureOrUnion structure, java.util.Set<char*> allStructureNames);
void extractTypeNames_Main(void* _ref, CType type, java.util.Set<char*> typeNames);
void extractTypeNamesFromString_Main(void* _ref, char* typeString, java.util.Set<char*> typeNames);
char* joinStrings_Main(void* _ref, List<char*> structures);
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper);
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder);
Iter<char*> divide_Main(void* _ref, char* input, Folder folder);
State foldStatement_Main(void* _ref, State current, char next);
char* compileRootSegment_Main(void* _ref, char* input);
Option<JObject> parseObject_Main(void* _ref, char* type, char* stripped);
Option<CStructMember> transformObject_Main(void* _ref, JObject object);
List<CDefinable> handleUnsealedInterface_Main(void* _ref, JObject object, List<CStructMember> members, List<CDefinable> fields);
List<CDefinable> handleSealedInterface_Main(void* _ref, JObject object, List<CDefinable> fields);
Option<JDeclaration> extractField_Main(void* _ref, JObjectMember prototype);
Option<CStructMember> transformObjectMemberPrototype_Main(void* _ref, JObject object, JObjectMember wrapper);
CStructMember completeMethodProto_Main(void* _ref, JMethod jFunctionProto, JObject object);
Option<JObjectMember> parseObjectMember_Main(void* _ref, char* input, char* name, List<char*> typeParameters);
Option<JObjectMember> parseMethod_Main(void* _ref, char* stripped, char* name, List<char*> typeParameters);
List<CDefinable> retainFields_Main(void* _ref, List<CStructMember> members);
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
CPrimitiveType new_CPrimitiveType(char* content){
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
char* stringify_JPrimitiveType(void* _ref){
	JPrimitiveType* _this = (JPrimitiveType*) _ref;
	return name_JPrimitiveType((*_this));
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
char* stringify_JType(void* _ref){
	JType* _this = (JType*) _ref;
	char* _ret;
	switch (_this->variant) {
		case IdentifierVariant:
			_ret = stringify_Identifier(&(_this->data.Identifier));
			break;
		case JArrayTypeVariant:
			_ret = stringify_JArrayType(&(_this->data.JArrayType));
			break;
		case JFunctionalTypeVariant:
			_ret = stringify_JFunctionalType(&(_this->data.JFunctionalType));
			break;
		case JGenericTypeVariant:
			_ret = stringify_JGenericType(&(_this->data.JGenericType));
			break;
		case JObjectTypeVariant:
			_ret = stringify_JObjectType(&(_this->data.JObjectType));
			break;
		case JPrimitiveTypeVariant:
			_ret = stringify_JPrimitiveType(&(_this->data.JPrimitiveType));
			break;
		case JRecursiveTypeVariant:
			_ret = stringify_JRecursiveType(&(_this->data.JRecursiveType));
			break;
		case PlaceholderVariant:
			_ret = stringify_Placeholder(&(_this->data.Placeholder));
			break;
	}
	return _ret;
}
char* generate_CDefinable(void* _ref){
	CDefinable* _this = (CDefinable*) _ref;
	return _this->table.generate(_this->data);
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
StringBuilder empty_StringBuilders(void* _ref){
	StringBuilders* _this = (StringBuilders*) _ref;
	return new_StringBuilder(empty_Lists(Lists));
}
StringBuilder appendChar_StringBuilder(void* _ref, char next){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder(addLast_List(_this->list, next));
}
StringBuilder clear_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return new_StringBuilder(clear_List(_this->list));
}
char* toString_StringBuilder(void* _ref){
	StringBuilder* _this = (StringBuilder*) _ref;
	return collect_Iter(map_Iter(iter_List(_this->list), F? { alloc(String), F?Table { valueOf }}), new_Joiner());
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
/*TODO:  resolve lambda return type*/ lambda0(void* _ref, /*TODO: resolve type of lambda param*/ element){
	return apply_F2R(folder, finalCurrent, element);
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
		Tuple tuple = toTuple_Option(map_Option(next_Head(_this->head), lambda0), lambda1);
		if (tuple.left) 
			current = tuple.right;
		/*else
					return*/ current;
	}
}
template <typename C, typename T>
C collect_Iter(void* _ref, Collector<T, C> collector){
	Iter<T>* _this = (Iter<T>*) _ref;
	return fold_Iter((*_this), createInitial_Collector(collector), F? { alloc(collector), F?Table { fold }});
}
template <typename T>
List<T> toList_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return collect_Iter((*_this), new_ListCollector<T>());
}
/*TODO:  resolve lambda return type*/ lambda2(void* _ref, /*TODO: resolve type of lambda param*/ element){
	if (apply_F1R(predicate, element)) 
		return new_Iter<T>(new_SingleHead<T>(element));
	return new_Iter<T>(new_EmptyHead<T>());
}
template <typename T>
Iter<T> filter_Iter(void* _ref, F1R<T, int> predicate){
	Iter<T>* _this = (Iter<T>*) _ref;
	return flatMap_Iter((*_this), lambda2);
}
template <typename R, typename T>
Iter<R> flatMap_Iter(void* _ref, F1R<T, Iter<R>> mapper){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter<R>(new_FlatMapHead<T, R>(_this->head, mapper));
}
template <typename T>
Option<T> next_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return next_Head(_this->head);
}
Head<int> toHead_RangeHead(void* _ref){
	RangeHead _this = *((RangeHead*) _ref);
	HeadData data;
	data.RangeHead = _this;
	return { RangeHeadVariant, data };
}
RangeHead new_RangeHead(int length){
	RangeHead _this;
	_this->length = length;
	_this->counter = 0;
	return _this;
}
Option<int> next_RangeHead(void* _ref){
	RangeHead* _this = (RangeHead*) _ref;
	if (_this->counter < (*_this).length) {
		int value = _this->counter;
		_this->counter++;
		return new_Some<int>(value);
	}
	/*else
				return new None<Integer>()*/;
}
template <typename T>
List<T> of_Lists(void* _ref, /*T...*/ elements){
	Lists* _this = (Lists*) _ref;
	return collect_Iter(fromObjArray_Streams(Streams, elements), new_ListCollector<T>());
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
State new_State(char* input){
	State _this;
	_this->input = input;
	_this->index = 0;
	_this->buffer = empty_StringBuilders(StringBuilders);
	_this->depth = 0;
	_this->segments = empty_Lists(Lists);
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
	return (*_this);
}
Option<char> pop_State(void* _ref){
	State* _this = (State*) _ref;
	if (length_/*Cannot access member 'input' in 'Placeholder[input=Unwrapped expression: _this->index < (*_this)]', not an object.*/(_this->index < (*_this).input)) {
		char value = charAt_String(_this->input, _this->index);
		_this->index++;
		return new_Some<char>(value);
	}
	/*else
				return new None<Character>()*/;
}
State advance_State(void* _ref){
	State* _this = (State*) _ref;
	_this->segments = addLast_List(_this->segments, toString_StringBuilder(_this->buffer));
	_this->buffer = clear_StringBuilder(_this->buffer);
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
	return iter_List(_this->segments);
}
/*TODO:  resolve lambda return type*/ lambda3(void* _ref, /*TODO: resolve type of lambda param*/ popped){
	State appended = append_State((*_this), popped);
	return new_Tuple<State, char>(appended, popped);
}
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref){
	State* _this = (State*) _ref;
	return map_Option(pop_State((*_this)), lambda3);
}
/*TODO:  resolve lambda return type*/ lambda4(void* _ref, /*TODO: resolve type of lambda param*/ tuple){
	return tuple.left;
}
Option<State> popAndAppendToOption_State(void* _ref){
	State* _this = (State*) _ref;
	return map_Option(popAndAppendToTuple_State((*_this)), lambda4);
}
Option<char> peek_State(void* _ref){
	State* _this = (State*) _ref;
	if (length_/*Cannot access member 'input' in 'Placeholder[input=Unwrapped expression: _this->index < (*_this)]', not an object.*/(_this->index < (*_this).input)) 
		return new_Some<char>(charAt_String(_this->input, _this->index));
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
	return generate_CType(_this->type) + "*";
}
char* toBaseName_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return toBaseName_CType(_this->type) + "_ptr";
}
CType toCType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CTypeData data;
	data.CTemplateType = _this;
	return { CTemplateTypeVariant, data };
}
CTemplateType new_CTemplateType(char* base, List<CType> list){
	CTemplateType _this;
	_this->base = base;
	_this->list = list;
	/*assert !list.isEmpty()*/;
	return _this;
}
char* generate_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	C typeArguments = collect_Iter(map_Iter(iter_List(_this->list), F? { alloc(CType), F?Table { generate }}), new_Joiner(", "));
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
	return generate_/*Cannot access member 'expression' in 'Placeholder[input=Unwrapped expression: "*" + (*_this)]', not an object.*/("*" + (*_this).expression);
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
Identifier new_Identifier(char* value){
	Identifier _this;
	_this->value = value;
	/*assert !value.isEmpty()*/;
	return _this;
}
char* generate_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
char* toBaseName_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
char* stringify_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
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
JObjectMember toJObjectMember_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	JObjectMemberData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
CExpression toCExpression_Placeholder(void* _ref){
	Placeholder _this = *((Placeholder*) _ref);
	CExpressionData data;
	data.Placeholder = _this;
	return { PlaceholderVariant, data };
}
char* wrap_Placeholder(void* _ref, char* input){
	Placeholder* _this = (Placeholder*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'replace' in 'Placeholder[input=Not a functional type: Placeholder[input=Member 'replace' not defined in 'magma.Main$JRecursiveType@7bbb8d49']]', not an object.]*/ replaced = replace_/*Not a functional type: Placeholder[input=Member 'replace' not defined in 'magma.Main$JRecursiveType@7bbb8d49']*/(replace_String(input, "/*", "start"), "*/", "end");
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
char* stringify_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return wrap(_this->input);
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
JDeclaration new_JDeclaration(char* name, JType type){
	JDeclaration _this;
	(*_this)(empty_Lists(Lists), empty_Lists(Lists), new_None<char*>(), type, name);
	return _this;
}
char* toString_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	char* annotationsString;
	if (isEmpty_List(_this->annotations)) 
		annotationsString = "";
	/*else
				annotationsString */ = "annotations=" + _this->annotations + ", ";
	char* typeParametersString;
	if (isEmpty_List(_this->typeParameters)) 
		typeParametersString = "";
	/*else
				typeParametersString */ = "typeParameters=" + _this->typeParameters + ", ";
	char* maybeBeforeTypeString;
	if (_this->maybeBeforeType.variant = ?.SomeVariant) 
		maybeBeforeTypeString = "maybeBeforeType=" + result + ", ";
	/*else
				maybeBeforeTypeString */ = "";
	return "JDeclaration {" + annotationsString + typeParametersString + maybeBeforeTypeString + "type=" + _this->type + ", name='" + _this->name + '\'' + '}';
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
	return toCDeclaration_JDeclaration((*_this));
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
	/*Unwrapped expression: "(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", "))
					+ ")"*/ joinedParameterTypes = "(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", "))
					+ ")";
	return generate_CType(_this->type) + " (*" + this.name + ")" + joinedParameterTypes;
}
CStructMember toCStructMember_EmptyStructMember(void* _ref){
	EmptyStructMember _this = *((EmptyStructMember*) _ref);
	CStructMemberData data;
	data.EmptyStructMember = _this;
	return { EmptyStructMemberVariant, data };
}
JObjectMember toJObjectMember_EmptyStructMember(void* _ref){
	EmptyStructMember _this = *((EmptyStructMember*) _ref);
	JObjectMemberData data;
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
/*TODO:  resolve lambda return type*/ lambda5(void* _ref, /*TODO: resolve type of lambda param*/ tuple){
	if (tuple.right == '\\') 
		return orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Cannot access member 'left' in 'Placeholder[input=Undefined identifier: tuple]', not an object.]', not an object.]*/(popAndAppendToOption_/*Cannot access member 'left' in 'Placeholder[input=Undefined identifier: tuple]', not an object.*/(tuple.left), tuple.left);
	return tuple.left;
}
State apply_EscapedFolder(void* _ref, State state, char next){
	EscapedFolder* _this = (EscapedFolder*) _ref;
	if (next == '\'') {
		/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = append_State(state, next);
		return orElse_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(popAndAppendToTuple_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended), lambda5), F? { alloc(State), F?Table { popAndAppendToOption }}), appended);
	}
	if (next == '\"') {
		/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ current = append_State(state, next);
		while (true) {
			/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/ maybeTuple = popAndAppendToTuple_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(current);
			if (/*!(maybeTuple instanceof Some<Tuple<State, Character>>(var value))*/) 
				break;
			current = value.left;
			/*Cannot access member 'right' in 'Placeholder[input=Undefined identifier: value]', not an object.*/ right = value.right;
			if (right == '\\') 
				current = orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(current), current);
			if (right == '\"') 
				break;
		}
		return current;
	}
	return apply_Folder(_this->folder, state, next);
}
Folder toFolder_ValueFolder(void* _ref){
	ValueFolder _this = *((ValueFolder*) _ref);
	FolderData data;
	data.ValueFolder = _this;
	return { ValueFolderVariant, data };
}
State apply_ValueFolder(void* _ref, State state, char next){
	ValueFolder* _this = (ValueFolder*) _ref;
	if (isLevel_/*Unwrapped expression: next == ',' && state*/(next == ',' && state)) 
		return advance_State(state);
	/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = append_State(state, next);
	if (next == '-') {
		/*Not a functional type: Placeholder[input=Cannot access member 'peek' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/ peeked = peek_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended);
		if (peeked.variant = ?.SomeVariant) 
			return orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended), appended);
		/*else
					return*/ appended;
	}
	if (next == '<' || next == '(') 
		return enter_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended);
	if (next == '>' || next == ')') 
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended);
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
	return of_Iter(Iter, _this->value);
}
template <typename T>
Option<T> or_Some(void* _ref, FR<Option<T>> other){
	Some<T>* _this = (Some<T>*) _ref;
	return (*_this);
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
	return apply_FR(other);
}
template <typename T>
Iter<T> iter_None(void* _ref){
	None<T>* _this = (None<T>*) _ref;
	return empty_Iter(Iter);
}
template <typename T>
Option<T> or_None(void* _ref, FR<Option<T>> other){
	None<T>* _this = (None<T>*) _ref;
	return apply_FR(other);
}
template <typename T>
Tuple<int, T> toTuple_None(void* _ref, FR<T> other){
	None<T>* _this = (None<T>*) _ref;
	return new_Tuple<int, T>(false, apply_FR(other));
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
		return enter_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended);
	if (c == ')') {
		if (isLevel_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended)) 
			return advance_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended);
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended);
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
	return generateStatement_/*Undefined identifier: Main*/(Main, 1, generate_CDefinable(_this->declaration));
}
/*TODO:  resolve lambda return type*/ lambda6(void* _ref, /*TODO: resolve type of lambda param*/ index){
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
	return map_Option(next_Head(_this->head), _this->mapper);
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
			/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Cannot access member 'head' in 'Placeholder[input=Undefined identifier: current]', not an object.]', not an object.]*/ next = next_/*Cannot access member 'head' in 'Placeholder[input=Undefined identifier: current]', not an object.*/(current.head);
			if (next.variant = ?.SomeVariant) 
				return next;
		}
		Option maybeNext = next_Head(_this->head);
		if (maybeNext.variant = ?.NoneVariant) 
			return new_None<R>();
		_this->maybeCurrent = map_Option(maybeNext, _this->mapper);
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
	return apply_/*Cannot access member 'predicate' in 'Placeholder[input=Unwrapped expression: aBoolean || (*_this)]', not an object.*/(aBoolean || (*_this).predicate, t);
}
Collector<char*, char*> toCollector_Joiner(void* _ref){
	Joiner _this = *((Joiner*) _ref);
	CollectorData data;
	data.Joiner = _this;
	return { JoinerVariant, data };
}
Joiner new_Joiner(){
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
	if (isEmpty_String(current)) 
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
	return empty_Lists(Lists);
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
CDeclaration new_CDeclaration(CType type, char* name){
	CDeclaration _this;
	(*_this)(empty_Lists(Lists), type, name);
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
	return template + generate_CType(_this->type) + " " + (*_this).name;
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
char* stringify_JArrayType(void* _ref){
	JArrayType* _this = (JArrayType*) _ref;
	return stringify_JType(_this->type) + "_array";
}
JType toJType_JGenericType(void* _ref){
	JGenericType _this = *((JGenericType*) _ref);
	JTypeData data;
	data.JGenericType = _this;
	return { JGenericTypeVariant, data };
}
CType toCType_JGenericType(void* _ref){
	JGenericType* _this = (JGenericType*) _ref;
	List newTypeArguments = toList_Iter(map_Iter(iter_List(_this->typeArguments), F? { alloc(Main), F?Table { transformType }}));
	return new_CTemplateType(_this->base, newTypeArguments);
}
/*TODO:  resolve lambda return type*/ lambda7(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return "_" + slice;
}
char* stringify_JGenericType(void* _ref){
	JGenericType* _this = (JGenericType*) _ref;
	C joined = collect_Iter(map_Iter(map_Iter(iter_List(_this->typeArguments), F? { alloc(JType), F?Table { stringify }}), lambda7), new_Joiner());
	return _this->base + joined;
}
CExpression toCExpression_CPointerAccess(void* _ref){
	CPointerAccess _this = *((CPointerAccess*) _ref);
	CExpressionData data;
	data.CPointerAccess = _this;
	return { CPointerAccessVariant, data };
}
char* generate_CPointerAccess(void* _ref){
	CPointerAccess* _this = (CPointerAccess*) _ref;
	return generate_CExpression(_this->instance) + "->" + (*_this).fieldName;
}
CExpression toCExpression_CFieldAccess(void* _ref){
	CFieldAccess _this = *((CFieldAccess*) _ref);
	CExpressionData data;
	data.CFieldAccess = _this;
	return { CFieldAccessVariant, data };
}
char* generate_CFieldAccess(void* _ref){
	CFieldAccess* _this = (CFieldAccess*) _ref;
	return generate_CExpression(_this->instance) + "." + (*_this).fieldName;
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
CExpression toCExpression_CInvocation(void* _ref){
	CInvocation _this = *((CInvocation*) _ref);
	CExpressionData data;
	data.CInvocation = _this;
	return { CInvocationVariant, data };
}
char* generate_CInvocation(void* _ref){
	CInvocation* _this = (CInvocation*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]]', not an object.]*/ joinedArguments = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]*/(iter_/*Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]*/(cArguments_CInvocation((*_this))), F? { alloc(CAssignable), F?Table { generate }}), new_Joiner(", "));
	return generate_/*Not a functional type: Identifier[value=CExpression]*/(expression_CInvocation((*_this))) + "(" + joinedArguments + ")";
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
JFunctionalType new_JFunctionalType(JType returnType){
	JFunctionalType _this;
	(*_this)(new_JavaList<JType>(), returnType);
	return _this;
}
/*TODO:  resolve lambda return type*/ lambda8(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return "_" + slice;
}
char* stringify_JFunctionalType(void* _ref){
	JFunctionalType* _this = (JFunctionalType*) _ref;
	C joined = collect_Iter(map_Iter(map_Iter(iter_List(_this->parameterTypes), F? { alloc(JType), F?Table { stringify }}), lambda8), new_Joiner());
	return "func_" + stringify_JType(_this->returnType) + joined;
}
Environment new_Environment(){
	Environment _this;
	(*_this)(new_JavaList<Frame>());
	return _this;
}
Environment new_Environment(List<Frame> frames){
	Environment _this;
	_this->frames = frames;
	return _this;
}
/*TODO:  resolve lambda return type*/ lambda9(void* _ref, /*TODO: resolve type of lambda param*/ frame){
	return resolveExpression_/*Undefined identifier: frame*/(frame, identifier);
}
Option<JDeclaration> resolveExpression_Environment(void* _ref, char* identifier){
	Environment* _this = (Environment*) _ref;
	return next_Iter(flatMap_Iter(map_Iter(iter_List(_this->frames), lambda9), F? { alloc(Option), F?Table { iter }}));
}
template <typename T>
Tuple<Environment, T> within_Environment(void* _ref, F1R<Environment, Tuple<Environment, T>> supplier){
	Environment* _this = (Environment*) _ref;
	Environment withLastEnv = enter_Environment((*_this));
	R result = apply_F1R(supplier, withLastEnv);
	/*Not a functional type: Placeholder[input=Cannot access member 'exit' in 'Placeholder[input=Cannot access member 'left' in 'Identifier[value=R]', not an object.]', not an object.]*/ exited = exit_/*Cannot access member 'left' in 'Identifier[value=R]', not an object.*/(result.left);
	return new_Tuple<Environment, T>(exited, result.right);
}
Environment exit_Environment(void* _ref){
	Environment* _this = (Environment*) _ref;
	return new_Environment(removeLast_List(_this->frames));
}
Environment enter_Environment(void* _ref){
	Environment* _this = (Environment*) _ref;
	return new_Environment(addLast_List(_this->frames, new_Frame()));
}
/*TODO:  resolve lambda return type*/ lambda10(void* _ref, /*TODO: resolve type of lambda param*/ last){
	return defineAllExpressions_/*Undefined identifier: last*/(last, declarations);
}
Environment defineAllExpressions_Environment(void* _ref, List<JDeclaration> declarations){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(_this->frames, lambda10));
}
/*TODO:  resolve lambda return type*/ lambda11(void* _ref, /*TODO: resolve type of lambda param*/ last){
	return defineExpression_/*Undefined identifier: last*/(last, declaration);
}
Environment defineExpression_Environment(void* _ref, JDeclaration declaration){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(_this->frames, lambda11));
}
Option<JObjectType> resolveCurrent_Environment(void* _ref){
	Environment* _this = (Environment*) _ref;
	return next_Iter(flatMap_Iter(map_Iter(iterReversed_List(_this->frames), F? { alloc(Frame), F?Table { toStructureType }}), F? { alloc(Option), F?Table { iter }}));
}
/*TODO:  resolve lambda return type*/ lambda12(void* _ref, /*TODO: resolve type of lambda param*/ last){
	return withObject_/*Undefined identifier: last*/(last, object);
}
Environment withObject_Environment(void* _ref, JObject object){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(_this->frames, lambda12));
}
/*TODO:  resolve lambda return type*/ lambda13(void* _ref, /*TODO: resolve type of lambda param*/ frame){
	return resolveType_/*Undefined identifier: frame*/(frame, name);
}
Option<JObjectType> resolveType_Environment(void* _ref, char* name){
	Environment* _this = (Environment*) _ref;
	return next_Iter(flatMap_Iter(map_Iter(iterReversed_List(_this->frames), lambda13), F? { alloc(Option), F?Table { iter }}));
}
/*TODO:  resolve lambda return type*/ lambda14(void* _ref, /*TODO: resolve type of lambda param*/ last){
	return defineAllTypes_/*Undefined identifier: last*/(last, types);
}
Environment defineAllTypes_Environment(void* _ref, List<JObjectType> types){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(_this->frames, lambda14));
}
Frame new_Frame(Option<JObject> maybeName, List<JObjectType> definedTypes, List<JDeclaration> definedExpressions){
	Frame _this;
	_this->maybeObject = maybeName;
	_this->definedTypes = definedTypes;
	_this->definedExpressions = definedExpressions;
	return _this;
}
Frame new_Frame(){
	Frame _this;
	(*_this)(new_None<JObject>(), new_JavaList<JObjectType>(), new_JavaList<JDeclaration>());
	return _this;
}
Frame defineAllExpressions_Frame(void* _ref, List<JDeclaration> definitions){
	Frame* _this = (Frame*) _ref;
	return new_Frame(_this->maybeObject, _this->definedTypes, addAllLast_List(_this->definedExpressions, definitions));
}
/*TODO:  resolve lambda return type*/ lambda15(void* _ref, /*TODO: resolve type of lambda param*/ define){
	return equals_/*Cannot access member 'name' in 'Placeholder[input=Undefined identifier: define]', not an object.*/(define.name, identifier);
}
Option<JDeclaration> resolveExpression_Frame(void* _ref, char* identifier){
	Frame* _this = (Frame*) _ref;
	return next_Iter(filter_Iter(iter_List(_this->definedExpressions), lambda15));
}
Frame defineExpression_Frame(void* _ref, JDeclaration declaration){
	Frame* _this = (Frame*) _ref;
	return new_Frame(_this->maybeObject, _this->definedTypes, addLast_List(_this->definedExpressions, declaration));
}
/*TODO:  resolve lambda return type*/ lambda16(void* _ref, /*TODO: resolve type of lambda param*/ obj){
	return new_JObjectType(obj.name, _this->definedExpressions);
}
Option<JObjectType> toStructureType_Frame(void* _ref){
	Frame* _this = (Frame*) _ref;
	return map_Option(_this->maybeObject, lambda16);
}
Frame withObject_Frame(void* _ref, JObject name){
	Frame* _this = (Frame*) _ref;
	return new_Frame(new_Some<JObject>(name), _this->definedTypes, _this->definedExpressions);
}
/*TODO:  resolve lambda return type*/ lambda17(void* _ref, /*TODO: resolve type of lambda param*/ type){
	return equals_/*Cannot access member 'name' in 'Placeholder[input=Undefined identifier: type]', not an object.*/(type.name, name);
}
Option<JObjectType> resolveType_Frame(void* _ref, char* name){
	Frame* _this = (Frame*) _ref;
	return next_Iter(filter_Iter(iter_List(_this->definedTypes), lambda17));
}
Frame defineAllTypes_Frame(void* _ref, List<JObjectType> types){
	Frame* _this = (Frame*) _ref;
	return new_Frame(_this->maybeObject, addAllLast_List(_this->definedTypes, types), _this->definedExpressions);
}
JType toJType_JObjectType(void* _ref){
	JObjectType _this = *((JObjectType*) _ref);
	JTypeData data;
	data.JObjectType = _this;
	return { JObjectTypeVariant, data };
}
/*TODO:  resolve lambda return type*/ lambda18(void* _ref, /*TODO: resolve type of lambda param*/ member){
	return equals_/*Cannot access member 'name' in 'Placeholder[input=Undefined identifier: member]', not an object.*/(member.name, name);
}
Option<JType> resolve_JObjectType(void* _ref, char* name){
	JObjectType* _this = (JObjectType*) _ref;
	return map_Option(next_Iter(filter_Iter(iter_List(_this->members), lambda18)), F? { alloc(JDeclaration), F?Table { type }});
}
char* stringify_JObjectType(void* _ref){
	JObjectType* _this = (JObjectType*) _ref;
	return _this->name;
}
JType toJType_JRecursiveType(void* _ref){
	JRecursiveType _this = *((JRecursiveType*) _ref);
	JTypeData data;
	data.JRecursiveType = _this;
	return { JRecursiveTypeVariant, data };
}
JRecursiveType new_JRecursiveType(){
	JRecursiveType _this;
	_this->maybeInternal = new_None<JType>();
	return _this;
}
JType create_JRecursiveType(void* _ref, F1R<JType, JType> mapper){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	JRecursiveType created = new_JRecursiveType();
	R apply = apply_F1R(mapper, created);
	set_JRecursiveType(created, apply);
	return created;
}
void set_JRecursiveType(void* _ref, JType created){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	_this->maybeInternal = new_Some<JType>(created);
}
/*TODO:  resolve lambda return type*/ lambda19(void* _ref, /*TODO: resolve type of lambda param*/ internal){
	return stringify_/*Undefined identifier: internal*/(internal);
}
char* stringify_JRecursiveType(void* _ref){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	return orElse_Option(map_Option(_this->maybeInternal, lambda19), "?");
}
CStructureOrUnion toCStructureOrUnion_CStructure(void* _ref){
	CStructure _this = *((CStructure*) _ref);
	CStructureOrUnionData data;
	data.CStructure = _this;
	return { CStructureVariant, data };
}
char* generate_CStructure(void* _ref){
	CStructure* _this = (CStructure*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ joinedFields = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]*/(iter_/*Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]*/(fields_CStructure((*_this))), F? { alloc(CField), F?Table { new }}), F? { alloc(CField), F?Table { generate }}), new_Joiner());
	return lineSeparator_/*Unwrapped expression: generateTemplateString(typeParameters_CStructure((*_this))) + "struct " + name_CStructure((*_this)) + " {" + joinedFields + lineSeparator_startUndefined identifier: Systemend(System) + "};" + System*/(generateTemplateString(typeParameters_CStructure((*_this))) + "struct " + name_CStructure((*_this)) + " {" + joinedFields + lineSeparator_/*Undefined identifier: System*/(System) + "};" + System);
}
/*TODO:  resolve lambda return type*/ lambda20(void* _ref, /*TODO: resolve type of lambda param*/ variant){
	return variant + "Variant";
}
/*TODO:  resolve lambda return type*/ lambda21(void* _ref, /*TODO: resolve type of lambda param*/ variant){
	return generateIndent(1) + variant;
}
char* generate_CEnum(void* _ref){
	CEnum* _this = (CEnum*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ enumFields = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]*/(iter_/*Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]*/(variants_CEnum((*_this))), lambda20), lambda21), new_Joiner(","));
	return lineSeparator_/*Unwrapped expression: "enum " + name_CEnum((*_this)) + "Variant {" + enumFields + lineSeparator_startUndefined identifier: Systemend(System) + "};" + System*/("enum " + name_CEnum((*_this)) + "Variant {" + enumFields + lineSeparator_/*Undefined identifier: System*/(System) + "};" + System);
}
CStructureOrUnion toCStructureOrUnion_CUnion(void* _ref){
	CUnion _this = *((CUnion*) _ref);
	CStructureOrUnionData data;
	data.CUnion = _this;
	return { CUnionVariant, data };
}
char* generate_CUnion(void* _ref){
	CUnion* _this = (CUnion*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]]', not an object.]*/ unionFields = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]*/(iter_/*Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]*/(members_CUnion((*_this))), F? { alloc(Main), F?Table { generateStatement }}), new_Joiner());
	return lineSeparator_/*Unwrapped expression: generateTemplateString(typeParameters_CUnion((*_this))) + "union " + name_CUnion((*_this)) + "Data {" + unionFields + lineSeparator_startUndefined identifier: Systemend(System) + "};" + System*/(generateTemplateString(typeParameters_CUnion((*_this))) + "union " + name_CUnion((*_this)) + "Data {" + unionFields + lineSeparator_/*Undefined identifier: System*/(System) + "};" + System);
}
JObjectMember toJObjectMember_JObject(void* _ref){
	JObject _this = *((JObject*) _ref);
	JObjectMemberData data;
	data.JObject = _this;
	return { JObjectVariant, data };
}
List<CDefinable> collectCFields_JObject(void* _ref){
	JObject* _this = (JObject*) _ref;
	return /*this.recordFields.iter().map(JDeclaration::toCDeclaration).<CDefinable>map(value -> value).toList()*/;
}
List<CFunction> createConversionFunctions_JObject(void* _ref){
	JObject* _this = (JObject*) _ref;
	return toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]*/(iter_/*Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]*/(implementees_JObject((*_this))), F? { alloc((*_this)), F?Table { createConversionType }}));
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
new CFunctionHeader_JObject(void* _ref, /*CDeclaration(implementee,*/ conversionFunctionName){
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
/*case EmptyStructMember _,JObject _,Placeholder _ -> new None<JDeclaration>*/(){?
}
char* generate_CFunctionHeader(void* _ref){
	CFunctionHeader* _this = (CFunctionHeader*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]]', not an object.]*/(iter_/*Not a functional type: JObjectType[name=List, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=T]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=int]], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=T]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], Identifier[value=int]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=T], Identifier[value=T]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=T]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=T]]]], name='iterReversed'}]]*/(parameters_CFunctionHeader((*_this))), F? { alloc(CDeclaration), F?Table { generate }}), new_Joiner(", "));
	return generate_/*Not a functional type: Identifier[value=CFunctionDeclaration]*/(definition_CFunctionHeader((*_this))) + "(" + compiledParameters + ")";
}
char* generate_CFunction(void* _ref){
	CFunction* _this = (CFunction*) _ref;
	return lineSeparator_/*Unwrapped expression: generate_startNot a functional type: Identifier[value=CFunctionHeader]end(header_CFunction((*_this))) + "{" + content_CFunction((*_this)) + lineSeparator_startUndefined identifier: Systemend(System) + "}" + System*/(generate_/*Not a functional type: Identifier[value=CFunctionHeader]*/(header_CFunction((*_this))) + "{" + content_CFunction((*_this)) + lineSeparator_/*Undefined identifier: System*/(System) + "}" + System);
}
JObjectMember toJObjectMember_JMethod(void* _ref){
	JMethod _this = *((JMethod*) _ref);
	JObjectMemberData data;
	data.JMethod = _this;
	return { JMethodVariant, data };
}
Option<JDeclaration> toDeclaration_JMethod(void* _ref){
	JMethod* _this = (JMethod*) _ref;
	if (_this->methodDeclaration.variant = ?.JDeclaration declarationVariant) {
		/*Cannot access member 'type' in 'Placeholder[input=Undefined identifier: declaration]', not an object.*/ returnType = declaration.type;
		List paramTypes = toList_Iter(map_Iter(iter_List(_this->parameters), F? { alloc(JDeclaration), F?Table { type }}));
		JFunctionalType functionalType = new_JFunctionalType(paramTypes, returnType);
		return new_Some<JDeclaration>(new_JDeclaration(declaration.name, functionalType));
	}
	return new_None<JDeclaration>();
}
JObjectMember toJObjectMember_JField(void* _ref){
	JField _this = *((JField*) _ref);
	JObjectMemberData data;
	data.JField = _this;
	return { JFieldVariant, data };
}
JExpression toJExpression_JNumber(void* _ref){
	JNumber _this = *((JNumber*) _ref);
	JExpressionData data;
	data.JNumber = _this;
	return { JNumberVariant, data };
}
JNumber new_JNumber(char* value){
	JNumber _this;
	_this->value = value;
	/*assert !value.isEmpty()*/;
	return _this;
}
CExpression toCExpression_CNumber(void* _ref){
	CNumber _this = *((CNumber*) _ref);
	CExpressionData data;
	data.CNumber = _this;
	return { CNumberVariant, data };
}
CNumber new_CNumber(){
	CNumber _this;?
	return _this;
}
CNumber new_CNumber(char* value){
	CNumber _this;
	_this->value = value;
	/*assert !value.isEmpty()*/;
	return _this;
}
char* generate_CNumber(void* _ref){
	CNumber* _this = (CNumber*) _ref;
	return _this->value;
}
JExpression toJExpression_JNot(void* _ref){
	JNot _this = *((JNot*) _ref);
	JExpressionData data;
	data.JNot = _this;
	return { JNotVariant, data };
}
CExpression toCExpression_CNot(void* _ref){
	CNot _this = *((CNot*) _ref);
	CExpressionData data;
	data.CNot = _this;
	return { CNotVariant, data };
}
char* generate_CNot(void* _ref){
	CNot* _this = (CNot*) _ref;
	return generate_/*Cannot access member 'instance' in 'Placeholder[input=Unwrapped expression: "!" + (*_this)]', not an object.*/("!" + (*_this).instance);
}
/*private static final JType StringType = JRecursiveType.create*/(/*-> {
		// We don't need parameter types for*/ now){?
}
new Environment_Main(void* _ref){
	Main* _this = (Main*) _ref;
	return _this->table.Environment(_this->data);
}
Main new_Main(){
	Main _this;
	_this->structuresOrUnions = empty_Lists(Lists);
	_this->enums = empty_Lists(Lists);
	_this->structureForwardDeclarations = empty_Lists(Lists);
	_this->functionDeclarations = empty_Lists(Lists);
	_this->functions = empty_Lists(Lists);
	_this->globals = empty_Lists(Lists);
	_this->counter = 0;
	return _this;
}
/*TODO:  resolve lambda return type*/ lambda22(void* _ref, /*TODO: resolve type of lambda param*/ typeParam){
	return "typename " + typeParam;
}
char* generateTemplateString_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	if (isEmpty_List(typeParameters)) 
		return "";
	C typeNames = collect_Iter(map_Iter(iter_List(typeParameters), lambda22), new_Joiner(", "));
	return lineSeparator_/*Unwrapped expression: "template <" + typeNames + ">" + System*/("template <" + typeNames + ">" + System);
}
void main_Main(void* _ref, char** args){
	Main* _this = (Main*) _ref;
	if (run_Main(new_Main()).variant = ?.SomeVariant) 
		println_/*Cannot access member 'err' in 'Placeholder[input=Undefined identifier: System]', not an object.*/(System.err, display_/*Undefined identifier: value*/(value));
}
char* generateStatement_Main(void* _ref, int depth, char* content){
	Main* _this = (Main*) _ref;
	return generateIndent(depth) + content + ";";
}
char* generateIndent_Main(void* _ref, int depth){
	Main* _this = (Main*) _ref;
	return repeat_/*Unwrapped expression: lineSeparator_startUndefined identifier: Systemend(System) + "\t"*/(lineSeparator_/*Undefined identifier: System*/(System) + "\t", depth);
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
	if (isEmpty_List(typeParameters)) 
		joinedTypeParameters = "";
	/*else
			joinedTypeParameters */ = " < " + collect_Iter(iter_List(typeParameters), new_Joiner(", ")) + ">";
	return joinedTypeParameters;
}
Option<JObjectType> extractType_Main(void* _ref, JObjectMember jObjectMember){
	Main* _this = (Main*) _ref;
	return _switch;
}
CExpression transformCaller_Main(void* _ref, JCaller jCaller){
	Main* _this = (Main*) _ref;
	return _switch;
}
CExpression transformExpression_Main(void* _ref, JExpression expression){
	Main* _this = (Main*) _ref;
	return _switch;
}
CExpression transformInvocation_Main(void* _ref, JInvokable jInvokable){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ arguments = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]*/(arguments_JInvokable(jInvokable)), F? { alloc((*_this)), F?Table { transformExpression }}));
	/*Not a functional type: Placeholder[input=Cannot access member 'caller' in 'Identifier[value=JInvokable]', not an object.]*/ caller = caller_JInvokable(jInvokable);
	if (caller.variant = ?.JMemberAccess(var instance, var memberName)Variant) {
		JType jType = resolveExpression_Main((*_this), instance);
		/*Not a functional type: Placeholder[input=Cannot access member 'stringify' in 'Identifier[value=JType]', not an object.]*/ baseName = stringify_JType(jType);
		/*Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ newArguments = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'arguments' in 'Identifier[value=JInvokable]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(arguments, transformCaller_Main((*_this), instance));
		return new_CInvocation(new_Identifier(memberName + "_" + baseName), newArguments);
	}
	return new_CInvocation(transformCaller_Main((*_this), caller), arguments);
}
Option<IOError> run_Main(void* _ref){
	Main* _this = (Main*) _ref;
	Path source = get_Paths(Paths, ".", "src", "main", "java", "magma", "Main.java");
	Path target = get_Paths(Paths, ".", "src", "main", "windows", "magma", "Main.cpp");
	/*Not a functional type: Placeholder[input=Cannot access member 'mapValue' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'readString' in 'Identifier[value=Path]', not an object.]]', not an object.]*/ input = mapValue_/*Not a functional type: Placeholder[input=Cannot access member 'readString' in 'Identifier[value=Path]', not an object.]*/(readString_Path(source), F? { alloc((*_this)), F?Table { compile }});
	return _switch;
}
char* compile_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* all = compileStatements_Main((*_this), input, F? { alloc((*_this)), F?Table { compileRootSegment }});
	char* joinedStructureForwardDeclarations = joinStrings_Main((*_this), _this->structureForwardDeclarations);
	C joinedStructures = collect_Iter(map_Iter(iter_List(createTopologicallySortedList_Main((*_this))), F? { alloc(CStructureOrUnion), F?Table { generate }}), new_Joiner());
	char* joinedGlobals = joinStrings_Main((*_this), _this->globals);
	char* joinedFunctionDeclarations = joinStrings_Main((*_this), _this->functionDeclarations);
	C joinedFunctions = collect_Iter(map_Iter(iter_List(_this->functions), F? { alloc(CFunction), F?Table { generate }}), new_Joiner());
	C joinedEnums = collect_Iter(map_Iter(iter_List(_this->enums), F? { alloc(CEnum), F?Table { generate }}), new_Joiner());
	return joinedStructureForwardDeclarations + joinedEnums + joinedStructures + joinedGlobals + joinedFunctionDeclarations + joinedFunctions + all;
}
List<CStructureOrUnion> createTopologicallySortedList_Main(void* _ref){
	Main* _this = (Main*) _ref;
	// Build a map of structure/union name to the structure itself
	/*Generic type 'java.util.HashMap' has not been defined*/ nameToStructure = new_java.util.HashMap<char*, CStructureOrUnion>();
	/*this.structuresOrUnions.iter().fold(null, (_, s) -> {*/
	/*final var name = switch (s) {*/
	/*case CStructure cs -> cs.name()*/;
	/*case CUnion cu -> cu.name() + "Data"*/;
	/*}*/;
	put_/*Generic type 'java.util.HashMap' has not been defined*/(nameToStructure, name, s);
	return null;
	/*})*/;
	// Extract dependencies for each structure
	/*Generic type 'java.util.HashMap' has not been defined*/ dependencies = new_java.util.HashMap<char*, java.util.Set<char*>>();
	/*this.structuresOrUnions.iter().fold(null, (_, s) -> {*/
	/*final var name = switch (s) {*/
	/*case CStructure cs -> cs.name()*/;
	/*case CUnion cu -> cu.name() + "Data"*/;
	/*}*/;
	/*Generic type 'java.util.Set' has not been defined*/ deps = extractDependencies(s, keySet_/*Generic type 'java.util.HashMap' has not been defined*/(nameToStructure));
	put_/*Generic type 'java.util.HashMap' has not been defined*/(dependencies, name, deps);
	return null;
	/*})*/;
	// Perform topological sort using DFS
	/*Generic type 'java.util.HashSet' has not been defined*/ visited = new_java.util.HashSet<char*>();
	/*Generic type 'java.util.HashSet' has not been defined*/ visiting = new_java.util.HashSet<char*>();
	/*Generic type 'ArrayList' has not been defined*/ result = new_ArrayList<CStructureOrUnion>();
	/*for (final var structureName : nameToStructure.keySet()) {*/
	if (contains_Boolean(!visited, structureName)) 
	/*{*/
	topologicalSortDFS(structureName, visited, visiting, dependencies, nameToStructure, result);
	/*}*/
	/*}*/
	// Convert ArrayList to custom List
	/*List<CStructureOrUnion> list = (List<CStructureOrUnion>) (Object) Lists.empty()*/;
	/*for (final var structure : result) {*/
	list = addLast_/*Undefined identifier: list*/(list, structure);
	/*}*/
	return list;
}
void topologicalSortDFS_Main(void* _ref, char* name, java.util.Set<char*> visited, java.util.Set<char*> visiting, java.util.Map<char*, java.util.Set<char*>> dependencies, java.util.Map<char*, CStructureOrUnion> nameToStructure, ArrayList<CStructureOrUnion> result){
	Main* _this = (Main*) _ref;
	if (contains_/*Generic type 'java.util.Set' has not been defined*/(visited, name)) 
		/*return*/;
	if (contains_/*Generic type 'java.util.Set' has not been defined*/(visiting, name)) {
		// Cycle detected, just continue (prioritize compilation over cycle detection)
		/*return*/;
	}
	add_/*Generic type 'java.util.Set' has not been defined*/(visiting, name);
	/*Not a functional type: Placeholder[input=Cannot access member 'getOrDefault' in 'Placeholder[input=Generic type 'java.util.Map' has not been defined]', not an object.]*/ deps = getOrDefault_/*Generic type 'java.util.Map' has not been defined*/(dependencies, name, new_java.util.HashSet</**/>());
	/*for (final var dep : deps) {
			if (nameToStructure.containsKey(dep)) {
				topologicalSortDFS(dep, visited, visiting, dependencies, nameToStructure, result);
			}
		}*/
	remove_/*Generic type 'java.util.Set' has not been defined*/(visiting, name);
	add_/*Generic type 'java.util.Set' has not been defined*/(visited, name);
	/*Not a functional type: Placeholder[input=Cannot access member 'get' in 'Placeholder[input=Generic type 'java.util.Map' has not been defined]', not an object.]*/ structure = get_/*Generic type 'java.util.Map' has not been defined*/(nameToStructure, name);
	if (structure != null) {
		add_/*Generic type 'ArrayList' has not been defined*/(result, structure);
	}
}
/*TODO:  resolve lambda return type*/ lambda23(void* _ref, /*TODO: resolve type of lambda param*/ name){
	return contains_Boolean(!allStructureNames, name);
}
java.util.Set<char*> extractDependencies_Main(void* _ref, CStructureOrUnion structure, java.util.Set<char*> allStructureNames){
	Main* _this = (Main*) _ref;
	/*Generic type 'java.util.HashSet' has not been defined*/ deps = new_java.util.HashSet<char*>();
	/*switch (structure) {
			case CStructure cs -> {
				cs.fields().iter().fold(null, (_, field) -> {
					if (field instanceof CDeclaration cd) {
						extractTypeNames(cd.type(), deps);
					}
					return null;
				});
			}
			case CUnion cu -> {
				// Union members are strings, extract type names from their string
				// representation
				cu.members().iter().fold(null, (_, member) -> {
					extractTypeNamesFromString(member, deps);
					return null;
				});
			}
		}*/
	// Only keep dependencies that are actual structures/unions we know about
	removeIf_/*Generic type 'java.util.HashSet' has not been defined*/(deps, lambda23);
	return deps;
}
void extractTypeNames_Main(void* _ref, CType type, java.util.Set<char*> typeNames){
	Main* _this = (Main*) _ref;
	/*switch (type) {
			case Identifier id -> extractTypeNamesFromString(id.value(), typeNames);
			case CPointerType ptr -> extractTypeNames(ptr.type(), typeNames);
			case CTemplateType template -> {
				typeNames.add(template.base());
				template.list().iter().fold(null, (_, t) -> {
					extractTypeNames(t, typeNames);
					return null;
				});
			}
			case CPrimitiveType _,Placeholder _ -> {
				// No dependencies
			}
		}*/
}
void extractTypeNamesFromString_Main(void* _ref, char* typeString, java.util.Set<char*> typeNames){
	Main* _this = (Main*) _ref;
	// Extract identifiers from the string representation
	// This is a simple heuristic: words that start with uppercase are likely type
	// names
	/*Not a functional type: Placeholder[input=Cannot access member 'compile' in 'Placeholder[input=Cannot access member 'Pattern' in 'Placeholder[input=Cannot access member 'regex' in 'Placeholder[input=Cannot access member 'util' in 'Placeholder[input=Undefined identifier: java]', not an object.]', not an object.]', not an object.]', not an object.]*/ pattern = compile_/*Cannot access member 'Pattern' in 'Placeholder[input=Cannot access member 'regex' in 'Placeholder[input=Cannot access member 'util' in 'Placeholder[input=Undefined identifier: java]', not an object.]', not an object.]', not an object.*/(java.util.regex.Pattern, "\\b([A-Z][a-zA-Z0-9]*)\\b");
	/*Not a functional type: Placeholder[input=Cannot access member 'matcher' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'compile' in 'Placeholder[input=Cannot access member 'Pattern' in 'Placeholder[input=Cannot access member 'regex' in 'Placeholder[input=Cannot access member 'util' in 'Placeholder[input=Undefined identifier: java]', not an object.]', not an object.]', not an object.]', not an object.]]', not an object.]*/ matcher = matcher_/*Not a functional type: Placeholder[input=Cannot access member 'compile' in 'Placeholder[input=Cannot access member 'Pattern' in 'Placeholder[input=Cannot access member 'regex' in 'Placeholder[input=Cannot access member 'util' in 'Placeholder[input=Undefined identifier: java]', not an object.]', not an object.]', not an object.]', not an object.]*/(pattern, typeString);
	while (find_/*Not a functional type: Placeholder[input=Cannot access member 'matcher' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'compile' in 'Placeholder[input=Cannot access member 'Pattern' in 'Placeholder[input=Cannot access member 'regex' in 'Placeholder[input=Cannot access member 'util' in 'Placeholder[input=Undefined identifier: java]', not an object.]', not an object.]', not an object.]', not an object.]]', not an object.]*/(matcher)) 
	/*{*/
	add_/*Generic type 'java.util.Set' has not been defined*/(typeNames, group_/*Not a functional type: Placeholder[input=Cannot access member 'matcher' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'compile' in 'Placeholder[input=Cannot access member 'Pattern' in 'Placeholder[input=Cannot access member 'regex' in 'Placeholder[input=Cannot access member 'util' in 'Placeholder[input=Undefined identifier: java]', not an object.]', not an object.]', not an object.]', not an object.]]', not an object.]*/(matcher, 1));
	/*}*/
}
char* joinStrings_Main(void* _ref, List<char*> structures){
	Main* _this = (Main*) _ref;
	return collect_Iter(iter_List(structures), new_Joiner(""));
}
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper){
	Main* _this = (Main*) _ref;
	return compileAll_Main((*_this), input, mapper, new_EscapedFolder(F? { alloc((*_this)), F?Table { foldStatement }}));
}
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder){
	Main* _this = (Main*) _ref;
	return collect_Iter(map_Iter(divide_Main((*_this), input, folder), mapper), new_Joiner(""));
}
Iter<char*> divide_Main(void* _ref, char* input, Folder folder){
	Main* _this = (Main*) _ref;
	State current = new_State(input);
	while (true) {
		/*Not a functional type: Placeholder[input=Cannot access member 'pop' in 'Identifier[value=State]', not an object.]*/ maybeNext = pop_State(current);
		if (/*!(maybeNext instanceof Some<Character>(var value))*/) 
			break;
		char next;
		next = value;
		current = apply_Folder(folder, current, next);
	}
	return stream_/*Not a functional type: Placeholder[input=Cannot access member 'advance' in 'Identifier[value=State]', not an object.]*/(advance_State(current));
}
State foldStatement_Main(void* _ref, State current, char next){
	Main* _this = (Main*) _ref;
	if (isLevel_/*Unwrapped expression: next == '/' && current*/(next == '/' && current)) {
		/*Not a functional type: Placeholder[input=Cannot access member 'peek' in 'Identifier[value=State]', not an object.]*/ maybePeeked = peek_State(current);
		if (maybePeeked.variant = ?.SomeVariant) {
			/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/ withoutLineCommentPrefix = orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(append_State(current, '/')), current);
			while (true) {
				/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToTuple' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ maybeTuple = popAndAppendToTuple_/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/(withoutLineCommentPrefix);
				if (maybeTuple.variant = ?.SomeVariant) {
					withoutLineCommentPrefix = tuple.left;
					/*Cannot access member 'right' in 'Placeholder[input=Undefined identifier: tuple]', not an object.*/ right = tuple.right;
					if (right == '\r' || right == '\n') 
						withoutLineCommentPrefix = advance_/*Not a functional type: Placeholder[input=Cannot access member 'orElse' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]]', not an object.]*/(withoutLineCommentPrefix);
				}
				/*else
						return*/ withoutLineCommentPrefix;
			}
		}
	}
	/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/ appended = append_State(current, next);
	if (isLevel_/*Unwrapped expression: next == ';' && appended*/(next == ';' && appended)) 
		return advance_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended);
	if (isShallow_/*Unwrapped expression: next == '}' && appended*/(next == '}' && appended)) {
		State appended1;
		if (peek_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended).variant = ?.SomeVariant) 
			appended1 = orElse_/*Not a functional type: Placeholder[input=Cannot access member 'popAndAppendToOption' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]]', not an object.]*/(popAndAppendToOption_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended), appended);
		/*else
				appended1 */ = appended;
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'advance' in 'Placeholder[input=Undefined identifier: appended1]', not an object.]*/(advance_/*Undefined identifier: appended1*/(appended1));
	}
	if (next == '{' || next == '(') 
		return enter_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended);
	if (next == '}' || next == ')') 
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'append' in 'Identifier[value=State]', not an object.]*/(appended);
	return appended;
}
/*TODO:  resolve lambda return type*/ lambda24(void* _ref){
	return wrap_Placeholder(Placeholder, stripped);
}
char* compileRootSegment_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(input);
	if (isEmpty_String(stripped)) 
		return "";
	if (startsWith_/*Unwrapped expression: startsWith_String(stripped, "package ") || stripped*/(startsWith_String(stripped, "package ") || stripped, "import ")) 
		return "";
	return orElseGet_Option(map_Option(flatMap_Option(parseObject_Main((*_this), "class", stripped), F? { alloc((*_this)), F?Table { transformObject }}), F? { alloc(CStructMember), F?Table { generate }}), lambda24);
}
Option<JObject> parseObject_Main(void* _ref, char* type, char* stripped){
	Main* _this = (Main*) _ref;
	int i = indexOf_String(stripped, type + " ");
	if (i < 0) 
		return new_None<JObject>();
	char* beforeType = strip_String(substring_String(stripped, 0, i));
	char* modifiers;
	List<char*> annotations = empty_Lists(Lists);
	int i5 = lastIndexOf_String(beforeType, "\n");
	if (i5 >= 0) {
		char* substring = substring_String(beforeType, 0, i5);
		char* substring1 = substring_String(beforeType, i5 + 1);
		annotations = collectAnnotations_Main((*_this), substring);
		modifiers = substring1;
	}
	/*else
			modifiers */ = beforeType;
	char* afterKeyword = strip_String(substring_String(stripped));
	int i1 = indexOf_String(afterKeyword, "{");
	if (i1 < 0) 
		return new_None<JObject>();
	char* beforeContent = strip_String(substring_String(afterKeyword, 0, i1));
	char* withEnd = strip_String(substring_String(afterKeyword, i1 + 1));
	if (endsWith_Boolean(!withEnd, "}")) 
		return new_None<JObject>();
	char* inputContent = substring_String(withEnd, 0, length_String(withEnd) - 1);
	List<char*> variants = empty_Lists(Lists);
	int i2 = indexOf_String(beforeContent, "permits ");
	if (i2 >= 0) {
		char* substring1 = substring_String(beforeContent, length_/*Unwrapped expression: i2 + "permits "*/(i2 + "permits "));
		beforeContent = substring_String(beforeContent, 0, i2);
		variants = splitValues_Main((*_this), substring1);
	}
	// TODO: generate conversion methods
	List<CType> extensions = empty_Lists(Lists);
	int extendsIndex = indexOf_String(beforeContent, "extends ");
	if (extendsIndex >= 0) 
	/*{*/
	char* extensionsString = substring_String(beforeContent, length_/*Unwrapped expression: extendsIndex + "extends "*/(extendsIndex + "extends "));
	beforeContent = strip_String(substring_String(beforeContent, 0, extendsIndex));
	/*extensions = this*/
	/*.divide(extensionsString, new ValueFolder())*/
	/*.map(String::strip)*/
	/*.filter(slice -> !slice.isEmpty())*/
	/*.map(input -> transformType(this.parseType(input)))*/
	/*.toList()*/;
	/*}*/
	List<CType> implementees = empty_Lists(Lists);
	int i4 = indexOf_String(beforeContent, "implements ");
	if (i4 >= 0) 
	/*{*/
	char* implementeesString = substring_String(beforeContent, length_/*Unwrapped expression: i4 + "implements "*/(i4 + "implements "));
	beforeContent = strip_String(substring_String(beforeContent, 0, i4));
	/*implementees = this*/
	/*.divide(implementeesString, new ValueFolder())*/
	/*.map(String::strip)*/
	/*.filter(slice -> !slice.isEmpty())*/
	/*.map(input -> transformType(this.parseType(input)))*/
	/*.toList()*/;
	/*}*/
	List<JDeclaration> recordFields = empty_Lists(Lists);
	if (endsWith_String(beforeContent, ")")) 
	/*{*/
	char* substring = substring_String(beforeContent, 0, length_String(beforeContent) - 1);
	int i3 = indexOf_String(substring, "(");
	if (i3 >= 0) 
	/*{*/
	beforeContent = substring_String(substring, 0, i3);
	/*recordFields = this*/
	/*.divide(substring.substring(i3 + 1), new ValueFolder())*/
	/*.map(this::parseDeclaration)*/
	/*.flatMap(Option::iter)*/
	/*.toList()*/;
	/*}*/
	/*}*/
	List<char*> typeParameters = empty_Lists(Lists);
	int i3 = indexOf_String(beforeContent, " < ");
	if (i3 >= 0) 
	/*{*/
	char* substring1 = strip_String(substring_String(beforeContent, i3 + 1));
	if (endsWith_String(substring1, ">")) 
	/*{*/
	beforeContent = substring_String(beforeContent, 0, i3);
	char* substring = substring_String(substring1, 0, length_String(substring1) - 1);
	typeParameters = splitValues_Main((*_this), substring);
	/*}*/
	/*}*/
	/*if (!this.isIdentifier(beforeContent))*/
	return new_None<JObject>();
	/*var modifiersList = Streams*/
	/*.fromObjArray(modifiers.split(Pattern.quote(" ")))*/
	/*.map(String::strip)*/
	/*.filter(slice -> !slice.isEmpty())*/
	/*.toList()*/;
	char* name = strip_String(beforeContent);
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
	return new_Some<JObject>(prototype);
}
/*TODO:  resolve lambda return type*/ lambda25(void* _ref, /*TODO: resolve type of lambda param*/ wrapper){
	return transformObjectMemberPrototype_Main((*_this), object, wrapper);
}
/*TODO:  resolve lambda return type*/ lambda26(void* _ref, /*TODO: resolve type of lambda param*/ env){
	_this->environment = withObject_/*Undefined identifier: env*/(env, object);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ types = toList_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]*/(iter_/*Cannot access member 'children' in 'Identifier[value=JObject]', not an object.*/(object.children), F? { alloc(Main), F?Table { extractType }}), F? { alloc(Option), F?Table { iter }}));
	/*Not a functional type: Placeholder[input=Cannot access member 'addAllLast' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ declarations = addAllLast_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(toList_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]*/(iter_/*Cannot access member 'children' in 'Identifier[value=JObject]', not an object.*/(object.children), F? { alloc((*_this)), F?Table { extractField }}), F? { alloc(Option), F?Table { iter }})), object.recordFields);
	_this->environment = defineAllTypes_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]'*/(_this->environment, types);
	_this->environment = defineAllExpressions_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]'*/(_this->environment, declarations);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ members = toList_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]]', not an object.]*/(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Cannot access member 'children' in 'Identifier[value=JObject]', not an object.]', not an object.]*/(iter_/*Cannot access member 'children' in 'Identifier[value=JObject]', not an object.*/(object.children), lambda25), F? { alloc(Option), F?Table { iter }}));
	return new_Tuple<Environment, List<CStructMember>>(_this->environment, members);
}
Option<CStructMember> transformObject_Main(void* _ref, JObject object){
	Main* _this = (Main*) _ref;
	if (contains_/*Cannot access member 'annotations' in 'Identifier[value=JObject]', not an object.*/(object.annotations, "Actual")) 
		return new_Some<CStructMember>(new_EmptyStructMember());
	_this->functions = fold_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'createConversionFunctions' in 'Identifier[value=JObject]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'createConversionFunctions' in 'Identifier[value=JObject]', not an object.]*/(createConversionFunctions_JObject(object)), _this->functions, F? { alloc(List), F?Table { addLast }});
	/*Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]*/ within = within_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]'*/(_this->environment, lambda26);
	_this->environment = within.left;
	/*Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]]', not an object.*/ members = within.right;
	/*Not a functional type: Placeholder[input=Cannot access member 'collectCFields' in 'Identifier[value=JObject]', not an object.]*/ fields = collectCFields_JObject(object);
	if (equals_/*Not a functional type: Placeholder[input=Cannot access member 'type' in 'Identifier[value=JObject]', not an object.]*/(type_JObject(object), "interface")) 
		if (contains_/*Not a functional type: Placeholder[input=Cannot access member 'modifiersList' in 'Identifier[value=JObject]', not an object.]*/(modifiersList_JObject(object), "sealed")) 
			fields = handleSealedInterface_Main((*_this), object, fields);
	/*else
				fields */ = handleUnsealedInterface_Main((*_this), object, members, fields);
	/*else
			fields */ = addAllLast_/*Not a functional type: Placeholder[input=Cannot access member 'collectCFields' in 'Identifier[value=JObject]', not an object.]*/(fields, retainFields_Main((*_this), members));
	// TODO: create a constructor for records
	// TODO: create a default empty constructor for a class with no fields
	/*this.structuresOrUnions = this.structuresOrUnions*/
	/*.addLast(new CStructure(object.typeParameters(), object.name(), fields))*/;
	/*this.structureForwardDeclarations = this.structureForwardDeclarations.addLast(*/
	lineSeparator_/*Unwrapped expression: generateTemplateString(object.typeParameters) + "struct " + object.name + ";" + System*/(generateTemplateString(object.typeParameters) + "struct " + object.name + ";" + System);
	return new_Some<CStructMember>(new_EmptyStructMember());
}
List<CDefinable> handleUnsealedInterface_Main(void* _ref, JObject object, List<CStructMember> members, List<CDefinable> fields){
	Main* _this = (Main*) _ref;
	List list = toList_Iter(flatMap_Iter(map_Iter(iter_List(members), F? { alloc((*_this)), F?Table { retainDefinables }}), F? { alloc(Option), F?Table { iter }}));
	CStructure cStructure = new_CStructure(typeParameters_JObject(object), name_JObject(object) + "Table", list);
	_this->structuresOrUnions = addLast_List(_this->structuresOrUnions, cStructure);
	fields = addFirst_List(addLast_List(fields, new_CDeclaration(new_Identifier(joinTypeParameters_/*Unwrapped expression: name_JObject(object) + "Table" + Main*/(name_JObject(object) + "Table" + Main, typeParameters_JObject(object))), "table")), new_CDeclaration(new_CPointerType(CPrimitiveType.Void), "data"));
	return fields;
}
/*TODO:  resolve lambda return type*/ lambda27(void* _ref, /*TODO: resolve type of lambda param*/ variant){
	return variant + joinedTypeParameters + " " + variant;
}
List<CDefinable> handleSealedInterface_Main(void* _ref, JObject object, List<CDefinable> fields){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'name' in 'Identifier[value=JObject]', not an object.]*/ name = name_JObject(object);
	/*Not a functional type: Placeholder[input=Cannot access member 'typeParameters' in 'Identifier[value=JObject]', not an object.]*/ typeParameters = typeParameters_JObject(object);
	/*Not a functional type: Placeholder[input=Cannot access member 'variants' in 'Identifier[value=JObject]', not an object.]*/ variants = variants_JObject(object);
	CEnum jEnum = new_CEnum(name, variants);
	/*Not a functional type: Placeholder[input=Cannot access member 'joinTypeParameters' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ joinedTypeParameters = joinTypeParameters_/*Undefined identifier: Main*/(Main, typeParameters);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'variants' in 'Identifier[value=JObject]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ unionMembers = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'variants' in 'Identifier[value=JObject]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'variants' in 'Identifier[value=JObject]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'variants' in 'Identifier[value=JObject]', not an object.]*/(variants), lambda27));
	CUnion union = new_CUnion(typeParameters, name, unionMembers);
	fields = addLast_List(addLast_List(fields, new_CDeclaration(new_Identifier(name_JObject(object) + "Variant"), "variant")), new_CDeclaration(new_Identifier(joinTypeParameters_/*Unwrapped expression: name_JObject(object) + "Data" + Main*/(name_JObject(object) + "Data" + Main, typeParameters_JObject(object))), "data"));
	_this->enums = addLast_List(_this->enums, jEnum);
	_this->structuresOrUnions = addLast_List(_this->structuresOrUnions, union);
	return fields;
}
Option<JDeclaration> extractField_Main(void* _ref, JObjectMember prototype){
	Main* _this = (Main*) _ref;
	return _switch;
}
Option<CStructMember> transformObjectMemberPrototype_Main(void* _ref, JObject object, JObjectMember wrapper){
	Main* _this = (Main*) _ref;
	return _switch;
}
/*TODO:  resolve lambda return type*/ lambda28(void* _ref, /*TODO: resolve type of lambda param*/ name){
	return name + "_" + object.name;
}
/*TODO:  resolve lambda return type*/ lambda29(void* _ref, /*TODO: resolve type of lambda param*/ value){
	return new_Tuple<Environment, char*>(value, compileMethodsSegments_Main((*_this), inputContent, 1));
}
/*TODO:  resolve lambda return type*/ lambda30(void* _ref, /*TODO: resolve type of lambda param*/ env){
	/*Not a functional type: Placeholder[input=Cannot access member 'defineAllExpressions' in 'Placeholder[input=Undefined identifier: env]', not an object.]*/ self = defineAllExpressions_/*Undefined identifier: env*/(env, parameters_JMethod(jFunctionProto));
	return within_/*Not a functional type: Placeholder[input=Cannot access member 'defineAllExpressions' in 'Placeholder[input=Undefined identifier: env]', not an object.]*/(self, lambda29);
}
CStructMember completeMethodProto_Main(void* _ref, JMethod jFunctionProto, JObject object){
	Main* _this = (Main*) _ref;
	Option<char*> maybeCompiled = new_None<char*>();
	if (methodDeclaration_JMethod(jFunctionProto).variant = ?.JDeclaration declaration &&
				declaration.annotations.contains("Actual")Variant) {
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ cParameters = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]*/(parameters_JMethod(jFunctionProto)), F? { alloc(JDeclaration), F?Table { toCDeclaration }}));
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(cParameters), F? { alloc(CDeclaration), F?Table { generate }}), new_Joiner(", "));
		/*Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]]', not an object.]*/ modifiedMethodDeclaration = toCDeclaration_/*Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]*/(mapName_/*Undefined identifier: declaration*/(declaration, lambda28));
		_this->functionDeclarations = addLast_List(_this->functionDeclarations, lineSeparator_/*Unwrapped expression: generate_startNot a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]]', not an object.]end(modifiedMethodDeclaration) + "(" + compiledParameters + ");" + System*/(generate_/*Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'mapName' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]]', not an object.]*/(modifiedMethodDeclaration) + "(" + compiledParameters + ");" + System));
		return new_EmptyStructMember();
	}
	if (endsWith_/*Cannot access member 'content' in 'Placeholder[input=Unwrapped expression: startsWith_startCannot access member 'content' in 'Identifier[value=JMethod]', not an object.end(jFunctionProto.content, "{") && jFunctionProto]', not an object.*/(startsWith_/*Cannot access member 'content' in 'Identifier[value=JMethod]', not an object.*/(jFunctionProto.content, "{") && jFunctionProto.content, "}")) {
		/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Cannot access member 'content' in 'Identifier[value=JMethod]', not an object.]', not an object.]*/ inputContent = substring_/*Cannot access member 'content' in 'Identifier[value=JMethod]', not an object.*/(jFunctionProto.content, 1, length_/*Not a functional type: Placeholder[input=Cannot access member 'content' in 'Identifier[value=JMethod]', not an object.]*/(content_JMethod(jFunctionProto)) - 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]*/ within = within_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]'*/(_this->environment, lambda30);
		_this->environment = within.left;
		maybeCompiled = new_Some<char*>(within.right);
	}
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ cParameters = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]*/(parameters_JMethod(jFunctionProto)), F? { alloc(JDeclaration), F?Table { toCDeclaration }}));
	if (methodDeclaration_JMethod(jFunctionProto).variant = ?.JDeclarationVariant) 
		cParameters = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(cParameters, new_CDeclaration(new_CPointerType(CPrimitiveType.Void), "_ref"));
	char* outputContent = computeMethodBody_Main((*_this), typeParameters_JMethod(jFunctionProto), methodDeclaration_JMethod(jFunctionProto), cParameters, maybeCompiled, object.name, object.variants);
	CFunctionDeclaration mapped = transformMethodDeclaration_Main((*_this), object.name, typeParameters_JMethod(jFunctionProto), methodDeclaration_JMethod(jFunctionProto));
	CFunctionHeader header = new_CFunctionHeader(mapped, cParameters);
	CFunction cFunction = new_CFunction(header, outputContent);
	_this->functionDeclarations = addLast_List(_this->functionDeclarations, lineSeparator_/*Unwrapped expression: generate_CFunctionHeader(header) + ";" + System*/(generate_CFunctionHeader(header) + ";" + System));
	_this->functions = addLast_List(_this->functions, cFunction);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ parameterTypes = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'parameters' in 'Identifier[value=JMethod]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(cParameters), F? { alloc(CDeclaration), F?Table { type }}));
	return _switch;
}
Option<JObjectMember> parseObjectMember_Main(void* _ref, char* input, char* name, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(input);
	if (isEmpty_String(stripped)) 
		return new_None<JObjectMember>();
	Option maybeEnum = parseObject_Main((*_this), "enum", input);
	if (maybeEnum.variant = ?.SomeVariant) 
		return new_Some<JObjectMember>(enum0);
	Option maybeInterface = parseObject_Main((*_this), "interface", input);
	if (maybeInterface.variant = ?.SomeVariant) 
		return new_Some<JObjectMember>(interface0);
	Option maybeRecord = parseObject_Main((*_this), "record", input);
	if (maybeRecord.variant = ?.SomeVariant) 
		return new_Some<JObjectMember>(record0);
	Option maybeClass = parseObject_Main((*_this), "class", input);
	if (maybeClass.variant = ?.SomeVariant) 
		return new_Some<JObjectMember>(class0);
	Option maybeEnumValues = parseEnumValuesStatement_Main((*_this), input, name);
	if (maybeEnumValues.variant = ?.SomeVariant) 
		return new_Some<JObjectMember>(enumValues);
	if (endsWith_String(stripped, ";")) {
		char* substring = substring_String(stripped, 0, length_String(stripped) - 1);
		Option maybeDeclaration = parseDeclaration_Main((*_this), substring);
		if (maybeDeclaration.variant = ?.SomeVariant) {
			_this->environment = defineExpression_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]'*/(_this->environment, declaration);
			return new_Some<JObjectMember>(new_JField(declaration));
		}
	}
	Option maybeMethod = parseMethod_Main((*_this), stripped, name, typeParameters);
	if (maybeMethod.variant = ?.SomeVariant) 
		return new_Some<JObjectMember>(temp);
	return new_Some<JObjectMember>(new_Placeholder(stripped));
}
/*TODO:  resolve lambda return type*/ lambda31(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_Boolean(!slice);
}
Option<JObjectMember> parseMethod_Main(void* _ref, char* stripped, char* name, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	int i = indexOf_String(stripped, "(");
	if (i < 0) 
		return new_None<JObjectMember>();
	char* declarationString = substring_String(stripped, 0, i);
	char* substring1 = substring_String(stripped, i + 1);
	int i1 = indexOf_String(substring1, ")");
	if (i1 < 0) 
		return new_None<JObjectMember>();
	char* parametersString = substring_String(substring1, 0, i1);
	char* withBraces = strip_String(substring_String(substring1, i1 + 1));
	List parameters = toList_Iter(flatMap_Iter(map_Iter(iter_List(toList_Iter(filter_Iter(map_Iter(divide_Main((*_this), parametersString, new_ValueFolder()), F? { alloc(String), F?Table { strip }}), lambda31))), F? { alloc((*_this)), F?Table { parseDeclaration }}), F? { alloc(Option), F?Table { iter }}));
	JMethodDeclaration declaration = parseMethodDeclaration_Main((*_this), declarationString, name);
	JMethod proto = new_JMethod(typeParameters, parameters, declaration, withBraces);
	return new_Some<JObjectMember>(proto);
}
/*TODO:  resolve lambda return type*/ lambda32(void* _ref, /*TODO: resolve type of lambda param*/ member1){
	return _switch;
}
/*TODO:  resolve lambda return type*/ lambda33(void* _ref, /*TODO: resolve type of lambda param*/ member){
	return /*!(member instanceof CFunctionDeclaration)*/;
}
List<CDefinable> retainFields_Main(void* _ref, List<CStructMember> members){
	Main* _this = (Main*) _ref;
	return toList_Iter(filter_Iter(flatMap_Iter(map_Iter(iter_List(members), lambda32), F? { alloc(Option), F?Table { iter }}), lambda33));
}
Option<CDefinable> retainDefinables_Main(void* _ref, CStructMember member){
	Main* _this = (Main*) _ref;
	return _switch;
}
/*TODO:  resolve lambda return type*/ lambda34(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_Boolean(!slice);
}
List<char*> splitValues_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'split' not defined in 'magma.Main$JRecursiveType@7bbb8d49']*/ segments = split_String(input, quote_/*Undefined identifier: Pattern*/(Pattern, ","));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ list = toList_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]]', not an object.]*/(filter_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]*/(stream_/*Undefined identifier: Arrays*/(Arrays, segments), F? { alloc(String), F?Table { strip }}), lambda34));
	return new_JavaList<char*>(list);
}
/*TODO:  resolve lambda return type*/ lambda35(void* _ref, /*TODO: resolve type of lambda param*/ i){
	char c = charAt_String(stripped, i);
	return /*Character.isLetter(c) || (i != 0 && Character.isDigit(c))*/;
}
int isIdentifier_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(input);
	if (equals_/*Unwrapped expression: isEmpty_String(stripped) || stripped*/(isEmpty_String(stripped) || stripped, "return")) 
		return false;
	return allMatch_/*Not a functional type: Placeholder[input=Cannot access member 'range' in 'Placeholder[input=Undefined identifier: IntStream]', not an object.]*/(range_/*Undefined identifier: IntStream*/(IntStream, 0, length_String(stripped)), lambda35);
}
/*TODO:  resolve lambda return type*/ lambda36(void* _ref, /*TODO: resolve type of lambda param*/ parameter){
	return parameter.name;
}
/*TODO:  resolve lambda return type*/ lambda37(void* _ref){
	CType type = transformType(declaration.type);
	List list = toList_Iter(map_Iter(iter_List(subList_List(cParameters, 1, size_List(cParameters))), lambda36));
	return createBodyForAbstractMethod_Main((*_this), structureVariants, type, declaration.name, list);
}
char* computeMethodBody_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration, List<CDeclaration> cParameters, Option<char*> maybeContent, char* structName, List<char*> structureVariants){
	Main* _this = (Main*) _ref;
	if (methodDeclaration.variant = ?.JConstructorVariant) {
		T compiled = orElse_Option(maybeContent, "?");
		return generateStatement_/*Unwrapped expression: generateStatement_startUndefined identifier: Mainend(Main, structName + " _this") + compiled + Main*/(generateStatement_/*Undefined identifier: Main*/(Main, structName + " _this") + compiled + Main, "return " + "_this");
	}
	if (methodDeclaration.variant = ?.JDeclaration declarationVariant) {
		/*Not a functional type: Placeholder[input=Cannot access member 'joinTypeParameters' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ joinedTypeParameters = joinTypeParameters_/*Undefined identifier: Main*/(Main, typeParameters);
		/*Not a functional type: Placeholder[input=Cannot access member 'generateStatement' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ thisInitialization = generateStatement_/*Undefined identifier: Main*/(Main, structName + joinedTypeParameters + "* _this = (" + structName + joinedTypeParameters + "*) _ref");
		T body = orElseGet_Option(maybeContent, lambda37);
		return thisInitialization + body;
	}
	return "?";
}
/*TODO:  resolve lambda return type*/ lambda38(void* _ref, /*TODO: resolve type of lambda param*/ variant){
	return generateCase_Main((*_this), variant, name);
}
char* createBodyForAbstractMethod_Main(void* _ref, List<char*> variants, CType type, char* name, List<char*> parameterNames){
	Main* _this = (Main*) _ref;
	if (isEmpty_List(variants)) {
		C joinedParameters = collect_Iter(iter_List(addFirst_List(parameterNames, "_this->data")), new_Joiner(", "));
		return generateStatement_/*Undefined identifier: Main*/(Main, "return _this->table." + name + "(" + joinedParameters + ")");
	}
	else {
		/*Not a functional type: Placeholder[input=Cannot access member 'generateStatement' in 'Placeholder[input=Undefined identifier: Main]', not an object.]*/ returnValueDefinition = generateStatement_/*Undefined identifier: Main*/(Main, generate_CType(type) + " _ret");
		C cases = collect_Iter(map_Iter(iter_List(variants), lambda38), new_Joiner());
		return generateStatement_/*Unwrapped expression: returnValueDefinition + generateIndent(1) + "switch (" + "_this->variant" + ") {" + cases + generateIndent(1) + "}" + Main*/(returnValueDefinition + generateIndent(1) + "switch (" + "_this->variant" + ") {" + cases + generateIndent(1) + "}" + Main, "return _ret");
	}
}
/*TODO:  resolve lambda return type*/ lambda39(void* _ref, /*TODO: resolve type of lambda param*/ typeParameters0){
	return addAllLast_/*Undefined identifier: typeParameters0*/(typeParameters0, typeParameters);
}
/*TODO:  resolve lambda return type*/ lambda40(void* _ref, /*TODO: resolve type of lambda param*/ name){
	return name + "_" + structName;
}
CFunctionDeclaration transformMethodDeclaration_Main(void* _ref, char* structName, List<char*> typeParameters, JMethodDeclaration methodDeclaration){
	Main* _this = (Main*) _ref;
	return mapName_/*Not a functional type: Placeholder[input=Cannot access member 'mapTypeParameters' in 'Identifier[value=CFunctionDeclaration]', not an object.]*/(mapTypeParameters_CFunctionDeclaration(convertToFunctionDeclarations_Main((*_this), typeParameters, methodDeclaration), lambda39), lambda40);
}
CFunctionDeclaration convertToFunctionDeclarations_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration){
	Main* _this = (Main*) _ref;
	return _switch;
}
CType toConstructorReturnType_Main(void* _ref, char* base, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	if (isEmpty_String(base)) 
		return new_Identifier(base);
	/*final var typeArguments = typeParameters.iter().<CType>map(Identifier::new).toList()*/;
	if (isEmpty_/*Undefined identifier: typeArguments*/(typeArguments)) 
		return new_Identifier(base);
	return new_CTemplateType(base, typeArguments);
}
/*TODO:  resolve lambda return type*/ lambda41(void* _ref, /*TODO: resolve type of lambda param*/ input){
	return compileMethodSegment_Main((*_this), input, indent);
}
char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent){
	Main* _this = (Main*) _ref;
	return compileStatements_Main((*_this), inputContent, lambda41);
}
char* generateCase_Main(void* _ref, char* variant, char* name){
	Main* _this = (Main*) _ref;
	return generateIndent(2) + "case " + variant + "Variant:" + generateStatement(3, "_ret = " + name + "_" + variant + "(&(_this->data." + variant + "))") + generateStatement(3, "break");
}
/*TODO:  resolve lambda return type*/ lambda42(void* _ref){
	return map_Option(parseDeclaration_Main((*_this), declaration), F? { alloc((*_this)), F?Table { toInterface }});
}
/*TODO:  resolve lambda return type*/ lambda43(void* _ref){
	return new_Placeholder(declaration);
}
JMethodDeclaration parseMethodDeclaration_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	return orElseGet_Option(or_Option(parseConstructor_Main((*_this), declaration, structName), lambda42), lambda43);
}
JMethodDeclaration toInterface_Main(void* _ref, JDeclaration value){
	Main* _this = (Main*) _ref;
	return value;
}
Option<JMethodDeclaration> parseConstructor_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(declaration);
	if (equals_String(stripped, structName)) 
		return new_Some<JMethodDeclaration>(new_JConstructor(structName));
	int i = lastIndexOf_String(stripped, " ");
	if (i >= 0) {
		char* substring = strip_String(substring_String(stripped, i + 1));
		if (equals_String(substring, structName)) 
			return new_Some<JMethodDeclaration>(new_JConstructor(structName));
	}
	return new_None<JMethodDeclaration>();
}
Option<JObjectMember> parseEnumValuesStatement_Main(void* _ref, char* input, char* structName){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(input);
	if (endsWith_String(stripped, ";")) 
		return parseEnumValues_Main((*_this), structName, substring_String(stripped, 0, length_String(stripped) - 1));
	return parseEnumValues_Main((*_this), structName, stripped);
}
/*TODO:  resolve lambda return type*/ lambda44(void* _ref, /*TODO: resolve type of lambda param*/ state, /*TODO: resolve type of lambda param*/ character){
	return apply_ValueFolder(new_ValueFolder(), state, character);
}
/*TODO:  resolve lambda return type*/ lambda45(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_Boolean(!slice);
}
/*TODO:  resolve lambda return type*/ lambda46(void* _ref, /*TODO: resolve type of lambda param*/ enumValue){
	return compileEnumValue_Main((*_this), structName, enumValue);
}
Option<JObjectMember> parseEnumValues_Main(void* _ref, char* structName, char* input){
	Main* _this = (Main*) _ref;
	List enumValues = toList_Iter(filter_Iter(map_Iter(divide_Main((*_this), input, lambda44), F? { alloc(String), F?Table { strip }}), lambda45));
	if (isEmpty_Boolean(!enumValues)) {
		Iter optionStream = map_Iter(iter_List(enumValues), lambda46);
		/*final var areAnyInvalid = (boolean) optionStream
					.collect(new AnyMatch<Option<CStructMember>>(option -> option instanceof None<CStructMember>))*/;
		if (areAnyInvalid) 
			return new_None<JObjectMember>();
	}
	return new_Some<JObjectMember>(new_EmptyStructMember());
}
Option<CStructMember> compileEnumValue_Main(void* _ref, char* structName, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(input);
	if (endsWith_String(stripped, ")")) {
		char* substring = substring_String(stripped, 0, length_String(stripped) - 1);
		int i = indexOf_String(substring, "(");
		if (i >= 0) {
			char* name = substring_String(substring, 0, i);
			if (isIdentifier_Boolean(!(*_this), name)) 
				return new_None<CStructMember>();
			char* substring2 = substring_String(substring, i + 1);
			/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Placeholder[input=Unwrapped expression: structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2
						+ ")" + ";" + System]', not an object.]*/ generated = lineSeparator_/*Unwrapped expression: structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2
						+ ")" + ";" + System*/(structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2
						+ ")" + ";" + System);
			_this->globals = addLast_List(_this->globals, generated);
			return new_Some<CStructMember>(new_EmptyStructMember());
		}
	}
	if (isIdentifier_Main((*_this), stripped)) {
		/*Not a functional type: Placeholder[input=Cannot access member 'lineSeparator' in 'Placeholder[input=Unwrapped expression: structName + " " + structName + stripped + " = " + "new_" + structName + "()" + ";" + System]', not an object.]*/ generated = lineSeparator_/*Unwrapped expression: structName + " " + structName + stripped + " = " + "new_" + structName + "()" + ";" + System*/(structName + " " + structName + stripped + " = " + "new_" + structName + "()" + ";" + System);
		_this->globals = addLast_List(_this->globals, generated);
		return new_Some<CStructMember>(new_EmptyStructMember());
	}
	return new_None<CStructMember>();
}
char* compileMethodSegment_Main(void* _ref, char* input, int indent){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(input);
	if (isEmpty_String(stripped)) 
		return "";
	Option maybeIf = compileConditional_Main((*_this), "if", indent, stripped);
	if (maybeIf.variant = ?.SomeVariant) 
		return result;
	Option maybeWhile = compileConditional_Main((*_this), "while", indent, stripped);
	if (maybeWhile.variant = ?.SomeVariant) 
		return result;
	if (startsWith_String(stripped, "else ")) {
		char* substring = strip_String(substring_String(stripped, length_/*Unwrapped expression: "else "*/("else ")));
		if (endsWith_/*Unwrapped expression: startsWith_String(substring, "{") && substring*/(startsWith_String(substring, "{") && substring, "}")) {
			char* substring1 = substring_String(substring, 1, length_String(substring) - 1);
			return generateIndent(indent) + "else {" + compileMethodsSegments_Main((*_this), substring1, indent + 1) + generateIndent(indent) + "}";
		}
		/*else
				return generateIndent(indent) + "else " + this.compileMethodSegment(substring, indent + 1)*/;
	}
	if (endsWith_String(stripped, ";")) {
		char* substring = substring_String(stripped, 0, length_String(stripped) - 1);
		return generateIndent(indent) + compileMethodStatement_Main((*_this), substring) + ";";
	}
	if (startsWith_String(stripped, "//")) 
		return generateIndent(indent) + stripped;
	return wrap_/*Unwrapped expression: lineSeparator_startUndefined identifier: Systemend(System) + "\t" + Placeholder*/(lineSeparator_/*Undefined identifier: System*/(System) + "\t" + Placeholder, stripped);
}
/*TODO:  resolve lambda return type*/ lambda47(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_Boolean(!slice);
}
Option<char*> compileConditional_Main(void* _ref, char* type, int indent, char* input){
	Main* _this = (Main*) _ref;
	if (startsWith_String(input, type)) {
		char* substring = strip_String(substring_String(input, length_String(type)));
		if (startsWith_String(substring, "(")) {
			char* afterConditionStart = strip_String(substring_String(substring, 1));
			List divisions = toList_Iter(filter_Iter(map_Iter(divide_Main((*_this), afterConditionStart, new_EscapedFolder(new_ConditionEndLocator())), F? { alloc(String), F?Table { strip }}), lambda47));
			if (size_List(divisions) < 2) 
				return new_None<char*>();
			T first = getFirst_List(divisions);
			char* maybeWithBraces = joinStrings_Main((*_this), subList_List(divisions, 1, size_List(divisions)));
			if (endsWith_Boolean(!first, ")")) 
				return new_None<char*>();
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Identifier[value=T]', not an object.]*/ condition = substring_T(first, 0, length_T(first) - 1);
			if (endsWith_/*Unwrapped expression: startsWith_String(maybeWithBraces, "{") && maybeWithBraces*/(startsWith_String(maybeWithBraces, "{") && maybeWithBraces, "}")) {
				char* content = substring_String(maybeWithBraces, 1, length_String(maybeWithBraces) - 1);
				return new_Some<char*>(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" + compileMethodsSegments_Main((*_this), content, indent + 1) + generateIndent(indent) + "}");
			}
			return new_Some<char*>(compileMethodSegment_/*Unwrapped expression: generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") " + (*_this)*/(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") " + (*_this), maybeWithBraces, indent + 1));
		}
	}
	return new_None<char*>();
}
char* compileMethodStatement_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(input);
	if (equals_String(stripped, "break")) 
		return "break";
	if (startsWith_String(stripped, "return ")) 
		return compileExpressionOrPlaceholder_/*Unwrapped expression: "return " + (*_this)*/("return " + (*_this), substring_String(stripped, length_/*Unwrapped expression: "return "*/("return ")));
	Option maybeAssignment = compileAssignment_Main((*_this), stripped);
	if (maybeAssignment.variant = ?.SomeVariant) 
		return assignment;
	Option maybeInvokable = parseInvokable_Main((*_this), stripped);
	if (maybeInvokable.variant = ?.Some(var value)Variant) 
		return generate_CExpression(transformExpression_Main((*_this), value));
	Option instance = post_Main((*_this), stripped, "++");
	if (instance.variant = ?.SomeVariant) 
		return x;
	Option instance0 = post_Main((*_this), stripped, "--");
	if (instance0.variant = ?.SomeVariant) 
		return x;
	Option maybeDeclaration = parseDeclaration_Main((*_this), input);
	if (maybeDeclaration.variant = ?.SomeVariant) 
		return generate_/*Not a functional type: Placeholder[input=Cannot access member 'toCDeclaration' in 'Placeholder[input=Undefined identifier: declaration]', not an object.]*/(toCDeclaration_/*Undefined identifier: declaration*/(declaration));
	return wrap_Placeholder(Placeholder, stripped);
}
Option<char*> compileAssignment_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	int index = indexOf_String(stripped, "=");
	if (index >= 0) {
		char* destination = substring_String(stripped, 0, index);
		char* substring1 = substring_String(stripped, index + 1);
		JAssignable assignable = parseAssignable_Main((*_this), destination);
		Option maybeSource = parseExpression_Main((*_this), substring1);
		if (maybeSource.variant = ?.SomeVariant) {
			CAssignable cAssignable = transformAssignable_Main((*_this), assignable, source);
			return new_Some<char*>(generate_/*Not a functional type: Placeholder[input=Cannot access member 'transformExpression' in 'Placeholder[input=Unwrapped expression: generate_CAssignable(cAssignable) + " = " + (*_this)]', not an object.]*/(transformExpression_/*Unwrapped expression: generate_CAssignable(cAssignable) + " = " + (*_this)*/(generate_CAssignable(cAssignable) + " = " + (*_this), source)));
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
		return resolveExpression_Main((*_this), source);
	return type;
}
JType resolveExpression_Main(void* _ref, JExpression source){
	Main* _this = (Main*) _ref;
	return cleanupType_Main((*_this), resolveUncleanedExpression_Main((*_this), source));
}
JType resolveUncleanedExpression_Main(void* _ref, JExpression source){
	Main* _this = (Main*) _ref;
	return _switch;
}
JType resolveIdentifier_Main(void* _ref, char* value){
	Main* _this = (Main*) _ref;
	if (equals_String(value, "this")) 
		return /*this.environment
					.resolveCurrent()
					.<JType>map(thisType -> thisType)
					.orElseGet(() -> new Placeholder("Not within a struct"))*/;
	/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'resolveExpression' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]]', not an object.]*/ maybeFound = map_/*Not a functional type: Placeholder[input=Cannot access member 'resolveExpression' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]*/(resolveExpression_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]'*/(_this->environment, value), F? { alloc(JDeclaration), F?Table { type }});
	if (maybeFound.variant = ?.SomeVariant) 
		return found;
	if (resolveType_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]'*/(_this->environment, value).variant = ?.SomeVariant) 
		return resolved;
	return new_Placeholder("Undefined identifier: " + value);
}
JType cleanupType_Main(void* _ref, JType found){
	Main* _this = (Main*) _ref;
	if (found.variant = ?.JGenericType genericTypeVariant) {
		/*Not a functional type: Placeholder[input=Cannot access member 'resolveType' in 'Placeholder[input=Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]']', not an object.]*/ resolved = resolveType_/*Member 'environment' not defined in 'JObjectType[name=Main, members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=new]], name='Environment'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='functionDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='globals'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], name='structureForwardDeclarations'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]], name='structuresOrUnions'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CFunction]]], name='functions'}, JDeclaration {maybeBeforeType=private, type=Identifier[value=int], name='counter'}, JDeclaration {maybeBeforeType=private, type=JGenericType[base=List, typeArguments=[Identifier[value=CEnum]]], name='enums'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateTemplateString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JArrayType[type=magma.Main$JRecursiveType@7bbb8d49]], returnType=Void], name='main'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateIndent'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=CType]], name='transformType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JPrimitiveType]], returnType=Identifier[value=CType]], name='transformPrimitiveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectType]]]], name='extractType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=CExpression]], name='transformCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=CExpression]], name='transformExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JInvokable]], returnType=Identifier[value=CExpression]], name='transformInvocation'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=IOError]]]], name='run'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compile'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CStructureOrUnion]]]], name='createTopologicallySortedList'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]], JGenericType[base=java.util.Map, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=CStructureOrUnion]]], JGenericType[base=ArrayList, typeArguments=[Identifier[value=CStructureOrUnion]]]], returnType=Void], name='topologicalSortDFS'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructureOrUnion], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='extractDependencies'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType], JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNames'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=java.util.Set, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Void], name='extractTypeNamesFromString'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='joinStrings'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileStatements'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=Folder]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileAll'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=Folder]], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='divide'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=State], Char], returnType=Identifier[value=State]], name='foldStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileRootSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObject]]]], name='parseObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObject'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleUnsealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='handleSealedInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='extractField'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObject], Identifier[value=JObjectMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='transformObjectMemberPrototype'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JMethod], Identifier[value=JObject]], returnType=Identifier[value=CStructMember]], name='completeMethodProto'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseObjectMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CStructMember]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDefinable]]]], name='retainFields'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CStructMember]], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CDefinable]]]], name='retainDefinables'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='splitValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration], JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='computeMethodBody'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=CType], magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='createBodyForAbstractMethod'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='transformMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]], Identifier[value=JMethodDeclaration]], returnType=Identifier[value=CFunctionDeclaration]], name='convertToFunctionDeclarations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], returnType=Identifier[value=CType]], name='toConstructorReturnType'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodsSegments'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateCase'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JMethodDeclaration]], name='parseMethodDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Identifier[value=JMethodDeclaration]], name='toInterface'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JMethodDeclaration]]]], name='parseConstructor'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValuesStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JObjectMember]]]], name='parseEnumValues'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CStructMember]]]], name='compileEnumValue'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int]], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodSegment'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, Identifier[value=int], magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileConditional'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileMethodStatement'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileAssignment'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JAssignable], Identifier[value=JExpression]], returnType=Identifier[value=CAssignable]], name='transformAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression], Identifier[value=JType]], returnType=Identifier[value=JType]], name='resolveType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Identifier[value=JType]], name='resolveUncleanedExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveIdentifier'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JType]], returnType=Identifier[value=JType]], name='cleanupType'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JObjectType], Identifier[value=JType], magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='resolveMember'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JCaller]], returnType=Identifier[value=JType]], name='resolveCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JAssignable]], name='parseAssignable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='post'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=magma.Main$JRecursiveType@7bbb8d49], name='compileExpressionOrPlaceholder'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=CExpression]]]], name='parseCExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseExpression'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileLambda'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]]]], name='parseLambdaParams'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@7bbb8d49], name='generateName'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49, magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='compileOperator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JExpression]]]], name='parseInvokable'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findCallerStart'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='isNumber'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Boolean], name='allDigits'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JCaller]]]], name='parseCaller'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=Option, typeArguments=[Identifier[value=JDeclaration]]]], name='parseDeclaration'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@7bbb8d49]]], name='collectAnnotations'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=int]], name='findTypeSeparator'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@7bbb8d49], returnType=Identifier[value=JType]], name='parseType'}]]'*/(_this->environment, genericType.base);
		if (resolved.variant = ?.SomeVariant) 
			return objType;
		/*else
				return new Placeholder("Generic type '" + genericType.base + "' has not been defined")*/;
	}
	return found;
}
/*TODO:  resolve lambda return type*/ lambda48(void* _ref){
	return new_Placeholder("Member '" + name + "' not defined in '" + instanceType + "'");
}
JType resolveMember_Main(void* _ref, JObjectType type, JType instanceType, char* name){
	Main* _this = (Main*) _ref;
	return orElseGet_/*Not a functional type: Placeholder[input=Cannot access member 'resolve' in 'Identifier[value=JObjectType]', not an object.]*/(resolve_JObjectType(type, name), lambda48);
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
	if (endsWith_String(stripped, slice)) {
		char* instance = substring_String(stripped, 0, length_String(stripped) - 2);
		return new_Some<char*>(compileExpressionOrPlaceholder_Main((*_this), instance) + slice);
	}
	return new_None<char*>();
}
/*TODO:  resolve lambda return type*/ lambda49(void* _ref){
	return wrap_Placeholder(Placeholder, input);
}
char* compileExpressionOrPlaceholder_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return orElseGet_Option(map_Option(parseCExpression_Main((*_this), input), F? { alloc(CExpression), F?Table { generate }}), lambda49);
}
Option<CExpression> parseCExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return map_Option(parseExpression_Main((*_this), input), F? { alloc((*_this)), F?Table { transformExpression }});
}
/*TODO:  resolve lambda return type*/ lambda50(void* _ref){
	return compileOperator_Main((*_this), stripped, " != ");
}
/*TODO:  resolve lambda return type*/ lambda51(void* _ref){
	return compileOperator_Main((*_this), stripped, " < ");
}
/*TODO:  resolve lambda return type*/ lambda52(void* _ref){
	return compileOperator_Main((*_this), stripped, " + ");
}
/*TODO:  resolve lambda return type*/ lambda53(void* _ref){
	return compileOperator_Main((*_this), stripped, " - ");
}
/*TODO:  resolve lambda return type*/ lambda54(void* _ref){
	return compileOperator_Main((*_this), stripped, " && ");
}
/*TODO:  resolve lambda return type*/ lambda55(void* _ref){
	return compileOperator_Main((*_this), stripped, " || ");
}
/*TODO:  resolve lambda return type*/ lambda56(void* _ref){
	return compileOperator_Main((*_this), stripped, " >= ");
}
Option<JExpression> parseExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(input);
	if (startsWith_String(stripped, "switch ")) 
		return map_Some(new_Some<char*>("_switch"), F? { alloc(JExpressionWrapper), F?Table { new }});
	int i2 = lastIndexOf_String(stripped, "::");
	if (i2 >= 0) {
		char* substring = substring_String(stripped, 0, i2);
		char* name = strip_String(substring_String(stripped, i2 + 2));
		if (isIdentifier_Main((*_this), name)) {
			char* compiled = compileExpressionOrPlaceholder_Main((*_this), substring);
			/*Unwrapped expression: "F?"*/ functionalInterfaceName = "F?";
			return map_Some(new_Some<char*>(functionalInterfaceName + " { alloc(" + compiled + "), " + functionalInterfaceName + "Table { " + name + " }}"), F? { alloc(JExpressionWrapper), F?Table { new }});
		}
	}
	if (endsWith_/*Unwrapped expression: startsWith_String(stripped, "'") && stripped*/(startsWith_String(stripped, "'") && stripped, "'")) 
		return map_Some(new_Some<char*>(stripped), F? { alloc(JExpressionWrapper), F?Table { new }});
	Option maybeLambda = compileLambda_Main((*_this), stripped);
	if (maybeLambda.variant = ?.SomeVariant) 
		return map_Option(maybeLambda, F? { alloc(JExpressionWrapper), F?Table { new }});
	int i3 = indexOf_String(stripped, ".variant = ?."Variant);
	if (i3 >= 0) {
		char* substring = substring_String(stripped, 0, i3);
		char* substring1 = strip_String(substring_String(stripped, i3 + ".variant = ?.".length()Variant));
		Option maybeInstance = map_Option(parseCExpression_Main((*_this), substring), F? { alloc(CExpression), F?Table { generate }});
		if (maybeInstance.variant = ?.SomeVariant) {
			int i4 = indexOf_String(substring1, " < ");
			char* substring2;
			if (i4 >= 0) 
				substring2 = substring_String(substring1, 0, i4);
			/*else
					substring2 */ = substring1;
			return map_Some(new_Some<char*>(instance + ".variant = ?." + substring2 + "Variant"), F? { alloc(JExpressionWrapper), F?Table { new }});
		}
	}
	int i = lastIndexOf_String(stripped, ".");
	if (i >= 0) {
		char* instanceString = substring_String(stripped, 0, i);
		char* memberName = strip_String(substring_String(stripped, i + 1));
		if (isIdentifier_Main((*_this), memberName)) {
			Option maybeInstance = parseExpression_Main((*_this), instanceString);
			if (maybeInstance.variant = ?.Some(var value)Variant) 
				return new_Some<JExpression>(new_JMemberAccess(value, memberName));
		}
	}
	Option maybeInvokable = parseInvokable_Main((*_this), stripped);
	if (maybeInvokable.variant = ?.SomeVariant) 
		return maybeInvokable;
	Option maybeOperator = or_Option(or_Option(or_Option(or_Option(or_Option(or_Option(or_Option(compileOperator_Main((*_this), stripped, " == "), lambda50), lambda51), lambda52), lambda53), lambda54), lambda55), lambda56);
	if (maybeOperator.variant = ?.SomeVariant) 
		return map_Option(maybeOperator, F? { alloc(JExpressionWrapper), F?Table { new }});
	if (isIdentifier_Main((*_this), stripped)) 
		return new_Some<JExpression>(new_Identifier(stripped));
	if (startsWith_String(stripped, "!")) {
		char* substring = substring_String(stripped, 1);
		Option maybeInstance = parseCExpression_Main((*_this), substring);
		if (maybeInstance.variant = ?.Some(var instance)Variant) 
			return new_Some<JExpression>(new_JNot(instance));
	}
	if (isNumber_Main((*_this), stripped)) 
		return new_Some<JExpression>(new_JNumber(stripped));
	if (endsWith_/*Unwrapped expression: startsWith_String(stripped, "\"") && stripped*/(startsWith_String(stripped, "\"") && stripped, "\"")) 
		return map_Some(new_Some<char*>(stripped), F? { alloc(JExpressionWrapper), F?Table { new }});
	return new_None<JExpression>();
}
/*TODO:  resolve lambda return type*/ lambda57(void* _ref, /*TODO: resolve type of lambda param*/ param){
	return new_CDeclaration(new_Placeholder("TODO: resolve type of lambda param"), param);
}
Option<char*> compileLambda_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	int index = indexOf_String(input, "->");
	if (index < 0) 
		return new_None<char*>();
	char* beforeContent = strip_String(substring_String(input, 0, index));
	char* maybeWithBraces = strip_String(substring_String(input, index + 2));
	Option maybeParams = parseLambdaParams_Main((*_this), beforeContent);
	if (/*!(maybeParams instanceof Some<List<String>>(var params))*/) 
		return new_None<char*>();
	/*Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ paramList = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]]', not an object.]*/(toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]*/(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]*/(iter_/*Undefined identifier: params*/(params), lambda57)), new_CDeclaration(new_CPointerType(CPrimitiveType.Void), "_ref"));
	char* output;
	if (endsWith_/*Unwrapped expression: startsWith_String(maybeWithBraces, "{") && maybeWithBraces*/(startsWith_String(maybeWithBraces, "{") && maybeWithBraces, "}")) {
		char* content = substring_String(maybeWithBraces, 1, length_String(maybeWithBraces) - 1);
		output = compileMethodsSegments_Main((*_this), content, 1);
	}
	/*else
			output */ = generateStatement_/*Undefined identifier: Main*/(Main, compileExpressionOrPlaceholder_/*Unwrapped expression: "return " + (*_this)*/("return " + (*_this), maybeWithBraces));
	char* generatedName = generateName_Main((*_this));
	CFunction cFunction = new_CFunction(new_CFunctionHeader(new_CDeclaration(new_Placeholder("TODO:  resolve lambda return type"), generatedName), paramList), output);
	_this->functions = addLast_List(_this->functions, cFunction);
	return new_Some<char*>(generatedName);
}
/*TODO:  resolve lambda return type*/ lambda58(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_Boolean(!slice);
}
Option<List<char*>> parseLambdaParams_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	if (isIdentifier_Main((*_this), input)) 
		return new_Some<List<char*>>(of_Lists(Lists, input));
	else 
		if (startsWith_String(input, "(") && input.endsWith(")")) {
			char* substring = substring_String(input, 1, length_String(input) - 1);
			List list = toList_Iter(filter_Iter(map_Iter(divide_Main((*_this), substring, new_ValueFolder()), F? { alloc(String), F?Table { strip }}), lambda58));
			return new_Some<List<char*>>(list);
		}
	/*else
			return new None<List<String>>()*/;
}
char* generateName_Main(void* _ref){
	Main* _this = (Main*) _ref;
	/*Cannot access member 'counter' in 'Placeholder[input=Unwrapped expression: "lambda" + (*_this)]', not an object.*/ generatedName = "lambda" + (*_this).counter;
	_this->counter++;
	return generatedName;
}
Option<char*> compileOperator_Main(void* _ref, char* input, char* operator){
	Main* _this = (Main*) _ref;
	if (length_String(input) < 3) 
		return new_None<char*>();
	if (contains_Boolean(!input, operator)) 
		return new_None<char*>();
	int i1 = -1;
	int depth = 0;
	int i = 0;
	while (i < length_String(input) - 1) {
		char c = charAt_String(input, i);
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
		char* leftString = substring_String(input, 0, i1);
		char* right = substring_String(input, length_/*Unwrapped expression: i1 + operator*/(i1 + operator));
		if (map_Option(parseCExpression_Main((*_this), leftString), F? { alloc(CExpression), F?Table { generate }}).variant = ?.SomeVariant) 
			if (map_Option(parseCExpression_Main((*_this), right), F? { alloc(CExpression), F?Table { generate }}).variant = ?.SomeVariant) 
				return new_Some<char*>(leftCompiled + " " + operator + " " + rightCompiled);
	}
	return new_None<char*>();
}
Option<JExpression> parseInvokable_Main(void* _ref, char* stripped){
	Main* _this = (Main*) _ref;
	if (endsWith_Boolean(!stripped, ")")) 
		return new_None<JExpression>();
	int length = length_String(stripped);
	char* withoutEnd = substring_String(stripped, 0, length - 1);
	int callerStart = findCallerStart_Main((*_this), withoutEnd);
	if (callerStart < 0) 
		return new_None<JExpression>();
	char* callerString = substring_String(withoutEnd, 0, callerStart);
	char* argumentsString = substring_String(withoutEnd, callerStart + 1);
	Option maybeCaller = parseCaller_Main((*_this), callerString);
	if (/*!(maybeCaller instanceof Some(var value))*/) 
		return new_None<JExpression>();
	List arguments = toList_Iter(flatMap_Iter(map_Iter(divide_Main((*_this), argumentsString, new_EscapedFolder(new_ValueFolder())), F? { alloc((*_this)), F?Table { parseExpression }}), F? { alloc(Option), F?Table { iter }}));
	return new_Some<JExpression>(new_JInvokable(value, arguments));
}
int findCallerStart_Main(void* _ref, char* withoutEnd){
	Main* _this = (Main*) _ref;
	int callerStart = -1;
	int depth = 0;
	int i = 0;
	while (length_/*Unwrapped expression: i < withoutEnd*/(i < withoutEnd)) {
		char c = charAt_String(withoutEnd, i);
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
	char* stripped = strip_String(input);
	if (isEmpty_String(stripped)) 
		return false;
	if (startsWith_String(stripped, " - ")) 
		return allDigits_Main((*_this), substring_String(stripped, 1));
	return allDigits_Main((*_this), stripped);
}
int allDigits_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return allMatch_/*Not a functional type: Placeholder[input=Cannot access member 'mapToObj' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'range' in 'Placeholder[input=Undefined identifier: IntStream]', not an object.]]', not an object.]*/(mapToObj_/*Not a functional type: Placeholder[input=Cannot access member 'range' in 'Placeholder[input=Undefined identifier: IntStream]', not an object.]*/(range_/*Undefined identifier: IntStream*/(IntStream, 0, length_String(input)), F? { alloc(input), F?Table { charAt }}), F? { alloc(Character), F?Table { isDigit }});
}
Option<JCaller> parseCaller_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(input);
	Option maybeExpression = parseExpression_Main((*_this), stripped);
	if (maybeExpression.variant = ?.SomeVariant) 
		return new_Some<JCaller>(expression);
	if (startsWith_String(stripped, "new ")) {
		char* type = substring_String(stripped, length_/*Unwrapped expression: "new "*/("new "));
		JType jType = parseType_Main((*_this), type);
		return new_Some<JCaller>(new_JConstruction(jType));
	}
	return new_None<JCaller>();
}
Option<JDeclaration> parseDeclaration_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(input);
	int nameSeparator = lastIndexOf_String(stripped, " ");
	if (nameSeparator >= 0) {
		char* beforeName = strip_String(substring_String(stripped, 0, nameSeparator));
		char* name = strip_String(substring_String(stripped, nameSeparator + 1));
		int typeSeparator = findTypeSeparator_Main((*_this), beforeName);
		if (isIdentifier_Boolean(!(*_this), name)) 
			return new_None<JDeclaration>();
		if (typeSeparator < 0) {
			JType type = parseType_Main((*_this), beforeName);
			return new_Some<JDeclaration>(new_JDeclaration(name, type));
		}
		char* beforeType = strip_String(substring_String(beforeName, 0, typeSeparator));
		List<char*> copy = empty_Lists(Lists);
		if (endsWith_String(beforeType, ">")) {
			char* substring = substring_String(beforeType, 0, length_String(beforeType) - 1);
			int i = indexOf_String(substring, " < ");
			if (i >= 0) {
				char* substring2 = substring_String(substring, i + 1);
				copy = splitValues_Main((*_this), substring2);
				beforeType = substring_String(substring, 0, i);
			}
		}
		List<char*> annotations = empty_Lists(Lists);
		int i = lastIndexOf_String(beforeType, "\n");
		if (i >= 0) {
			annotations = collectAnnotations_Main((*_this), substring_String(beforeType, 0, i));
			beforeType = strip_String(substring_String(beforeType, i + 1));
		}
		if (isIdentifier_Main((*_this), name)) {
			JType type = parseType_Main((*_this), substring_String(beforeName, typeSeparator + 1));
			JDeclaration jDeclaration = new_JDeclaration(annotations, copy, new_Some<char*>(beforeType), type, name);
			return new_Some<JDeclaration>(jDeclaration);
		}
	}
	return new_None<JDeclaration>();
}
/*TODO:  resolve lambda return type*/ lambda59(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return isEmpty_Boolean(!slice);
}
/*TODO:  resolve lambda return type*/ lambda60(void* _ref, /*TODO: resolve type of lambda param*/ slice){
	return substring_/*Undefined identifier: slice*/(slice, 1);
}
List<char*> collectAnnotations_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return toList_Iter(map_Iter(map_Iter(filter_Iter(fromObjArray_Streams(Streams, split_String(input, quote_/*Undefined identifier: Pattern*/(Pattern, "\n"))), lambda59), lambda60), F? { alloc(String), F?Table { strip }}));
}
int findTypeSeparator_Main(void* _ref, char* beforeName){
	Main* _this = (Main*) _ref;
	int typeSeparator = -1;
	int depth = 0;
	int i = 0;
	while (length_/*Unwrapped expression: i < beforeName*/(i < beforeName)) {
		char c = charAt_String(beforeName, i);
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
	char* stripped = strip_String(input);
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
	if (endsWith_String(stripped, "[]")) {
		char* slice = substring_String(stripped, 0, length_String(stripped) - 2);
		JType type = parseType_Main((*_this), slice);
		return new_JArrayType(type);
	}
	if (endsWith_String(stripped, ">")) {
		char* substring = substring_String(stripped, 0, length_String(stripped) - 1);
		int i = indexOf_String(substring, " < ");
		if (i >= 0) {
			char* base = substring_String(substring, 0, i);
			char* parameters = substring_String(substring, i + 1);
			List list = toList_Iter(map_Iter(divide_Main((*_this), parameters, new_ValueFolder()), F? { alloc((*_this)), F?Table { parseType }}));
			return new_JGenericType(base, list);
		}
	}
	if (isIdentifier_Main((*_this), stripped)) 
		return new_Identifier(stripped);
	// TODO: handle varargs through monomorphization
	return new_Placeholder(stripped);
}
