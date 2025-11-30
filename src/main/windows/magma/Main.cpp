#include "intrinsics.h"
struct CPrimitiveType;
struct JPrimitiveType;
struct Operator;
template <typename K, typename V>
struct Map;
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
struct StringBuilders;
struct StringBuilder;
template <typename T, typename R>
struct ZipHead;
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
struct TupleMap;
template <typename K, typename V>
struct MapCollector;
struct CReference;
struct Char;
struct JOperator;
struct COperator;
struct StringNode;
struct JMethodAccess;
struct JInstanceOf;
struct JQuantity;
template <typename T>
struct AllMatch;
struct CompiledUnit;
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
struct CPrimitiveType {
	char* content;
};
struct JPrimitiveType {
	char* name;
};
template <typename T>
struct FRTable {
	T (*apply)(void*);
};
template <typename T0, typename R>
struct F1RTable {
	R (*apply)(void*, T0);
};
template <typename A, typename B, typename R>
struct F2RTable {
	R (*apply)(void*, A, B);
};
struct ActualTable {
};
template <typename T, typename C>
struct CollectorTable {
	C (*createInitial)(void*);
	C (*fold)(void*, C, T);
};
struct IOErrorTable {
	char* (*display)(void*);
};
struct CAssignableTable {
	char* (*generate)(void*);
};
struct JAssignableTable {
};
struct JExpressionTable {
};
struct CExpressionTable {
};
struct JCallerTable {
};
struct JObjectMemberTable {
};
struct StringBuilders {
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
struct Identifier {
	char* value;
};
struct Placeholder {
	char* input;
};
struct JConstructor {
	char* type;
};
struct EmptyStructMember {
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
struct Streams {
};
template <typename T>
struct SingleHead {
	T value;
	int retrieved;
};
template <typename T>
struct EmptyHead {
};
struct Joiner {
	char* delimiter;
};
template <typename T>
struct ListCollector {
};
struct Paths {
};
struct JNumber {
	char* value;
};
struct CNumber {
	char* value;
};
template <typename K, typename V>
struct MapCollector {
};
struct Char {
	char* value;
};
struct StringNode {
	char* slice;
};
struct JInstanceOf {
};
struct CompiledUnit {
	char* header;
	char* source;
};
template <typename T>
struct FR {
	void* data;
	FRTable<T> table;
};
template <typename T>
union OptionData {
	None<T> None;
	Some<T> Some;
};
template <typename T0, typename R>
struct F1R {
	void* data;
	F1RTable<T0, R> table;
};
template <typename T, typename X>
union ResultData {
	Err<T, X> Err;
	Ok<T, X> Ok;
};
template <typename A, typename B, typename R>
struct F2R {
	void* data;
	F2RTable<A, B, R> table;
};
struct Actual {
	void* data;
	ActualTable table;
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
struct CAssignable {
	void* data;
	CAssignableTable table;
};
struct JAssignable {
	void* data;
	JAssignableTable table;
};
struct JExpression {
	void* data;
	JExpressionTable table;
};
struct CExpression {
	void* data;
	CExpressionTable table;
};
struct JCaller {
	void* data;
	JCallerTable table;
};
struct JObjectMember {
	void* data;
	JObjectMemberTable table;
};
template <typename T>
struct Option {
	OptionTag variant;
	OptionData<T> data;
};
template <typename T, typename X>
struct Result {
	ResultTag variant;
	ResultData<T, X> data;
};
struct CQuantity {
	CExpression expression;
};
struct CDereference {
	CExpression expression;
};
template <typename T>
struct AnyMatch {
	F1R<T, int> predicate;
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
struct JNot {
	CExpression instance;
};
struct CNot {
	CExpression instance;
};
struct CReference {
	CExpression instance;
};
struct JMethodAccess {
	JExpression instance;
	char* methodName;
};
struct JQuantity {
	JExpression instance;
};
template <typename T>
struct AllMatch {
	F1R<T, int> predicate;
};
template <typename T>
struct HeadTable {
	Option<T> (*next)(void*);
};
struct PathTable {
	Option<IOError> (*writeString)(void*, char*);
	Result<char*, IOError> (*readString)(void*);
	Option<Path> (*getParent)(void*);
	Option<IOError> (*createDirectories)(void*);
	Path (*resolve)(void*, char*);
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
template <typename T, typename R>
struct ZipHead {
	Head<T> leftHead;
	Head<R> rightHead;
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
template <typename K, typename V>
struct MapTable {
	Map<K, V> (*put)(void*, K, V);
	int (*containsKey)(void*, K);
	Iter<Tuple<K, V>> (*iterEntries)(void*);
	int (*isEmpty)(void*);
	int (*size)(void*);
	Map<K, V> (*mapValues)(void*, F1R<V, V>);
	Map<K, V> (*removeKey)(void*, K);
	Option<V> (*get)(void*, K);
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
template <typename T, typename R>
struct FlatMapHead {
	Head<T> head;
	F1R<T, Iter<R>> mapper;
	Option<Iter<R>> maybeCurrent;
};
template <typename K, typename V>
struct Map {
	void* data;
	MapTable<K, V> table;
};
template <typename T>
struct List {
	void* data;
	ListTable<T> table;
};
struct CTypeTable {
	char* (*generate)(void*);
	char* (*toBaseName)(void*);
	List<char*> (*extractIdentifiers)(void*);
};
struct JTypeTable {
	char* (*stringify)(void*);
	JType (*replace)(void*, Map<char*, JType>);
};
struct StringBuilder {
	List<char> list;
};
struct CInvocation {
	CExpression expression;
	List<CExpression> cArguments;
};
struct JInvokable {
	JCaller caller;
	List<JExpression> arguments;
};
struct CEnum {
	char* name;
	List<char*> variants;
};
template <typename K, typename V>
struct TupleMap {
	List<Tuple<K, V>> entries;
};
struct CType {
	void* data;
	CTypeTable table;
};
struct JType {
	void* data;
	JTypeTable table;
};
struct State {
	char* input;
	StringBuilder buffer;
	List<char*> segments;
	int index;
	int depth;
};
struct Operator {
	char* value;
	JType type;
};
struct FolderTable {
	State (*apply)(void*, State, char);
};
struct CPointerType {
	CType type;
};
struct CTemplateType {
	char* base;
	List<CType> typeArguments;
};
struct JDeclaration {
	List<char*> annotations;
	List<char*> typeParameters;
	Option<char*> maybeBeforeType;
	JType type;
	char* name;
};
struct CFunctionDeclaration {
	CType type;
	char* name;
	List<CType> parameterTypes;
};
struct CDeclaration {
	List<char*> typeParameters;
	CType type;
	char* name;
};
struct JArrayType {
	JType type;
};
struct JGenericType {
	char* base;
	List<JType> typeArguments;
};
struct JConstruction {
	JType jType;
};
struct JFunctionalType {
	List<JType> parameterTypes;
	JType returnType;
};
struct JRecursiveType {
	Option<JType> maybeInternal;
};
union CNamedTypeData {
	CTemplateType CTemplateType;
	Identifier Identifier;
};
union JMethodDeclarationData {
	JConstructor JConstructor;
	JDeclaration JDeclaration;
	Placeholder Placeholder;
};
struct Folder {
	void* data;
	FolderTable table;
};
union CDefinableData {
	CDeclaration CDeclaration;
	CFunctionDeclaration CFunctionDeclaration;
	Placeholder Placeholder;
};
struct JObjectType {
	char* name;
	List<char*> variants;
	List<JDeclaration> members;
	List<char*> typeParameters;
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
};
struct JField {
	JDeclaration declaration;
};
struct JOperator {
	JExpression left;
	Operator op;
	JExpression rightCompiled;
};
struct COperator {
	CExpression left;
	Operator op;
	CExpression right;
};
struct CNamedType {
	CNamedTypeTag variant;
	CNamedTypeData data;
};
struct JMethodDeclaration {
	JMethodDeclarationTag variant;
	JMethodDeclarationData data;
};
struct CDefinable {
	CDefinableTag variant;
	CDefinableData data;
};
struct EscapedFolder {
	Folder folder;
};
struct Frame {
	Option<JObject> maybeObject;
	List<JDeclaration> definedExpressions;
	List<JObjectType> definedTypes;
};
struct CField {
	CDefinable declaration;
};
struct Environment {
	List<Frame> frames;
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
struct JMethod {
	List<char*> typeParameters;
	List<JDeclaration> parameters;
	JMethodDeclaration methodDeclaration;
	char* content;
};
union CStructMemberData {
	EmptyStructMember EmptyStructMember;
	CField CField;
	CFunctionDeclaration CFunctionDeclaration;
	Placeholder Placeholder;
};
union CStructureOrUnionData {
	CStructure CStructure;
	CUnion CUnion;
};
struct CFunction {
	CFunctionHeader header;
	char* content;
};
struct CStructMember {
	CStructMemberTag variant;
	CStructMemberData data;
};
struct CStructureOrUnion {
	CStructureOrUnionTag variant;
	CStructureOrUnionData data;
};
struct Main {
	JType StringType;
	List<char*> functionDeclarations;
	List<char*> globals;
	List<char*> globalDeclarations;
	List<char*> imports;
	List<char*> structureForwardDeclarations;
	List<CStructureOrUnion> structuresOrUnions;
	List<CFunction> functions;
	int counter;
	List<CEnum> enums;
};
CPrimitiveType new_CPrimitiveType(char* content);
char* generate_CPrimitiveType(void* _ref);
char* toBaseName_CPrimitiveType(void* _ref);
List<char*> extractIdentifiers_CPrimitiveType(void* _ref);
JPrimitiveType new_JPrimitiveType(char* name);
char* stringify_JPrimitiveType(void* _ref);
JType replace_JPrimitiveType(void* _ref, Map<char*, JType> mappings);
Operator new_Operator(char* value, JType type);
char* generate_Operator(void* _ref);
JType getType_Operator(void* _ref);
template <typename K, typename V>
Map<K, V> put_Map(void* _ref, K key, V value);
template <typename K, typename V>
int containsKey_Map(void* _ref, K key);
template <typename K, typename V>
Iter<Tuple<K, V>> iterEntries_Map(void* _ref);
template <typename K, typename V>
int isEmpty_Map(void* _ref);
template <typename K, typename V>
int size_Map(void* _ref);
template <typename K, typename V>
Map<K, V> mapValues_Map(void* _ref, F1R<V, V> mapper);
template <typename K, typename V>
Map<K, V> removeKey_Map(void* _ref, K key);
template <typename K, typename V>
Option<V> get_Map(void* _ref, K key);
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
Path resolve_Path(void* _ref, char* child);
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
template <typename R, typename T>
Option<Tuple<T, R>> and_Option(void* _ref, FR<Option<R>> other);
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
JType replace_JType(void* _ref, Map<char*, JType> mappings);
char* generate_CDefinable(void* _ref);
List<char*> extractIdentifiers_CDefinable(void* _ref);
CDefinable mapTypeParameters_CDefinable(void* _ref, F1R<List<char*>, List<char*>> mapper);
CDefinable mapName_CDefinable(void* _ref, F1R<char*, char*> mapper);
char* generate_CStructureOrUnion(void* _ref);
List<char*> findDependencies_CStructureOrUnion(void* _ref);
char* findName_CStructureOrUnion(void* _ref);
StringBuilder empty_StringBuilders(void* _ref);
StringBuilder appendChar_StringBuilder(void* _ref, char next);
StringBuilder clear_StringBuilder(void* _ref);
char* toString_StringBuilder(void* _ref);
template <typename T, typename R>
Option<Tuple<T, R>> next_ZipHead(void* _ref);
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
template <typename R, typename T>
Iter<Tuple<T, R>> zip_Iter(void* _ref, Iter<R> other);
RangeHead new_RangeHead(int length);
Option<int> next_RangeHead(void* _ref);
template <typename T>
List<T> empty_Lists();
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
JType replace_Identifier(void* _ref, Map<char*, JType> mappings);
char* getName_Identifier(void* _ref);
char* wrap_Placeholder(void* _ref, char* input);
char* generate_Placeholder(void* _ref);
char* toBaseName_Placeholder(void* _ref);
List<char*> extractIdentifiers_Placeholder(void* _ref);
CDefinable mapTypeParameters_Placeholder(void* _ref, F1R<List<char*>, List<char*>> mapper);
CDefinable mapName_Placeholder(void* _ref, F1R<char*, char*> mapper);
char* stringify_Placeholder(void* _ref);
JType replace_Placeholder(void* _ref, Map<char*, JType> mappings);
JDeclaration new_JDeclaration(char* name, JType type);
char* toString_JDeclaration(void* _ref);
JDeclaration mapName_JDeclaration(void* _ref, F1R<char*, char*> mapper);
JDeclaration withType_JDeclaration(void* _ref, JType type);
JDeclaration mapType_JDeclaration(void* _ref, F1R<JType, JType> mapper);
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
Option<Tuple<T, R>> and_Some(void* _ref, FR<Option<R>> other);
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
template <typename R, typename T>
Option<Tuple<T, R>> and_None(void* _ref, FR<Option<R>> other);
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
Path get_Paths(char* first);
CDeclaration new_CDeclaration(CType type, char* name);
CDefinable mapName_CDeclaration(void* _ref, F1R<char*, char*> mapper);
CDefinable mapTypeParameters_CDeclaration(void* _ref, F1R<List<char*>, List<char*>> mapper);
char* generate_CDeclaration(void* _ref);
List<char*> extractIdentifiers_CDeclaration(void* _ref);
char* stringify_JArrayType(void* _ref);
JType replace_JArrayType(void* _ref, Map<char*, JType> mappings);
char* stringify_JGenericType(void* _ref);
JType replace_JGenericType(void* _ref, Map<char*, JType> mappings);
char* generate_CPointerAccess(void* _ref);
char* generate_CFieldAccess(void* _ref);
char* generate_CInvocation(void* _ref);
JFunctionalType new_JFunctionalType(JType returnType);
char* stringify_JFunctionalType(void* _ref);
JType replace_JFunctionalType(void* _ref, Map<char*, JType> mappings);
Environment new_Environment();
Environment new_Environment(List<Frame> frames);
Option<JDeclaration> resolveExpression_Environment(void* _ref, char* identifier);
template <typename T>
Tuple<Environment, T> within_Environment(void* _ref, F1R<Environment, Tuple<Environment, T>> FR);
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
Option<JObjectType> toObjectType_Frame(void* _ref);
Frame withObject_Frame(void* _ref, JObject name);
Option<JObjectType> resolveType_Frame(void* _ref, char* name);
Frame defineAllTypes_Frame(void* _ref, List<JObjectType> types);
Option<JType> resolve_JObjectType(void* _ref, char* name);
char* stringify_JObjectType(void* _ref);
JType replace_JObjectType(void* _ref, Map<char*, JType> mappings);
JObjectType specialize_JObjectType(void* _ref, List<JType> typeArguments);
JRecursiveType new_JRecursiveType();
JType create_JRecursiveType(void* _ref, F1R<JType, JType> mapper);
void set_JRecursiveType(void* _ref, JType created);
char* stringify_JRecursiveType(void* _ref);
JType replace_JRecursiveType(void* _ref, Map<char*, JType> mappings);
char* findName_CStructure(void* _ref);
char* generate_CStructure(void* _ref);
List<char*> findDependencies_CStructure(void* _ref);
char* generate_CEnum(void* _ref);
char* findName_CUnion(void* _ref);
char* generate_CUnion(void* _ref);
List<char*> findDependencies_CUnion(void* _ref);
List<CFunction> createConversionFunctions_JObject(void* _ref, Environment environment1);
CFunction createConversionType_JObject(void* _ref, CType implementee, Environment environment);
JObjectType toType_JObject(void* _ref);
Option<JDeclaration> extractDefinition_JObject(void* _ref, JObjectMember child);
char* generate_CFunctionHeader(void* _ref);
char* generate_CFunction(void* _ref);
Option<JDeclaration> toDeclaration_JMethod(void* _ref);
JNumber new_JNumber(char* value);
CNumber new_CNumber(char* value);
char* generate_CNumber(void* _ref);
char* generate_CNot(void* _ref);
template <typename K, typename V>
TupleMap<K, V> new_TupleMap();
template <typename K, typename V>
Map<K, V> put_TupleMap(void* _ref, K key, V value);
template <typename K, typename V>
TupleMap<K, V> removeKey_TupleMap(void* _ref, K key);
template <typename K, typename V>
Option<V> get_TupleMap(void* _ref, K key);
template <typename K, typename V>
int containsKey_TupleMap(void* _ref, K key);
template <typename K, typename V>
Iter<Tuple<K, V>> iterEntries_TupleMap(void* _ref);
template <typename K, typename V>
int isEmpty_TupleMap(void* _ref);
template <typename K, typename V>
int size_TupleMap(void* _ref);
template <typename K, typename V>
Map<K, V> mapValues_TupleMap(void* _ref, F1R<V, V> mapper);
template <typename K, typename V>
Map<K, V> createInitial_MapCollector(void* _ref);
template <typename K, typename V>
Map<K, V> fold_MapCollector(void* _ref, Map<K, V> kvMap, Tuple<K, V> kvTuple);
char* generate_CReference(void* _ref);
char* generate_Char(void* _ref);
char* generate_COperator(void* _ref);
char* generate_StringNode(void* _ref);
template <typename T>
int createInitial_AllMatch(void* _ref);
template <typename T>
int fold_AllMatch(void* _ref, int aBoolean, T t);
new Environment_Main(void* _ref);
Main new_Main();
int isLetter_Main(void* _ref, char c);
char* generateTemplateString_Main(void* _ref, List<char*> typeParameters);
void main_Main(void* _ref, char** args);
char* generateStatement_Main(void* _ref, int depth, char* content);
char* generateIndent_Main(void* _ref, int depth);
CType transformPrimitiveType_Main(void* _ref, JPrimitiveType type);
char* generateStatement_Main(void* _ref, char* content);
char* joinTypeParameters_Main(void* _ref, List<char*> typeParameters);
Option<JObjectType> extractType_Main(void* _ref, JObjectMember jObjectMember);
int isDigit_Main(void* _ref, char c);
CDeclaration transformDeclaration_Main(void* _ref, JDeclaration declaration);
CType transformType_Main(void* _ref, JType jType);
Tuple<CExpression, List<CType>> transformCaller_Main(void* _ref, JCaller jCaller);
Tuple<CExpression, List<CType>> convertTypeToConstructionIdentifier_Main(void* _ref, JType type);
CExpression transformExpression_Main(void* _ref, JExpression expression);
CExpression transformInvocation_Main(void* _ref, JInvokable jInvokable);
Option<IOError> run_Main(void* _ref, char** args);
Option<IOError> compileFile_Main(void* _ref, char* file);
CompiledUnit compile_Main(void* _ref, char* name, char* input);
List<CStructureOrUnion> createTopologicallySortedList_Main(void* _ref);
Tuple<List<char*>, Map<char*, List<char*>>> getListMapTuple_Main(void* _ref, Tuple<List<char*>, Map<char*, List<char*>>> listMapTuple, char* cleanedDependencyMapKeyToRemove);
List<char*> trimDependencies_Main(void* _ref, Tuple<char*, List<char*>> entry, Map<char*, List<char*>> dependencyMap, char* oldKey);
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
char* createBodyForAbstractMethod_Main(void* _ref, List<char*> variants, CType type, char* name, List<char*> parameterNames, char* structName);
CDefinable transformMethodDeclaration_Main(void* _ref, char* structName, List<char*> typeParameters, JMethodDeclaration methodDeclaration);
CDefinable convertToFunctionDeclarations_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration);
CType toConstructorReturnType_Main(void* _ref, char* base, List<char*> typeParameters);
char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent);
char* generateCase_Main(void* _ref, char* variant, char* name, List<char*> parameterNames, char* baseName);
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
JType cleanupType_Main(void* _ref, JType type);
JType resolveMember_Main(void* _ref, JObjectType type, JType instanceType, char* name);
JType resolveCaller_Main(void* _ref, JCaller caller);
JAssignable parseAssignable_Main(void* _ref, char* input);
Option<char*> post_Main(void* _ref, char* stripped, char* slice);
char* compileExpressionOrPlaceholder_Main(void* _ref, char* input);
Option<CExpression> parseCExpression_Main(void* _ref, char* input);
Option<JExpression> parseExpression_Main(void* _ref, char* input);
Option<JExpression> compileLambda_Main(void* _ref, char* input);
Option<List<JDeclaration>> parseLambdaParams_Main(void* _ref, char* input);
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
JType parseTypeOrPlaceholder_Main(void* _ref, char* input);
Option<JType> parseType_Main(void* _ref, char* input);
CPrimitiveType CPrimitiveTypeVoid = new_CPrimitiveType("void");
CPrimitiveType CPrimitiveTypeChar = new_CPrimitiveType("char");
CPrimitiveType CPrimitiveTypeInt = new_CPrimitiveType("int");
JPrimitiveType JPrimitiveTypeInt = new_JPrimitiveType("int");
JPrimitiveType JPrimitiveTypeVoid = new_JPrimitiveType("void");
JPrimitiveType JPrimitiveTypeBoolean = new_JPrimitiveType("bool");
JPrimitiveType JPrimitiveTypeChar = new_JPrimitiveType("char");
JPrimitiveType JPrimitiveTypeVar = new_JPrimitiveType("var");
Operator OperatorEquals = new_Operator("==", JPrimitiveType.Boolean);
Operator OperatorNotEquals = new_Operator("!=", JPrimitiveType.Boolean);
Operator OperatorLessThan = new_Operator("<", JPrimitiveType.Boolean), Add("+", JPrimitiveType.Int), Subtract("-", JPrimitiveType.Int),
		And("&&", JPrimitiveType.Boolean), Or("||", JPrimitiveType.Boolean),
		GreaterThanOrEquals(">=", JPrimitiveType.Boolean);
CType toCType_CPrimitiveType(void* _ref){
	CPrimitiveType* _this = (CPrimitiveType*) _ref;
	return _impl;
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
	JPrimitiveType* _this = (JPrimitiveType*) _ref;
	return _impl;
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
JType replace_JPrimitiveType(void* _ref, Map<char*, JType> mappings){
	JPrimitiveType* _this = (JPrimitiveType*) _ref;
	return (*_this);
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
template <typename K, typename V>
Map<K, V> put_Map(void* _ref, K key, V value){
	Map<K, V>* _this = (Map<K, V>*) _ref;
	return _this->table.put(_this->data, key, value);
}
template <typename K, typename V>
int containsKey_Map(void* _ref, K key){
	Map<K, V>* _this = (Map<K, V>*) _ref;
	return _this->table.containsKey(_this->data, key);
}
template <typename K, typename V>
Iter<Tuple<K, V>> iterEntries_Map(void* _ref){
	Map<K, V>* _this = (Map<K, V>*) _ref;
	return _this->table.iterEntries(_this->data);
}
template <typename K, typename V>
int isEmpty_Map(void* _ref){
	Map<K, V>* _this = (Map<K, V>*) _ref;
	return _this->table.isEmpty(_this->data);
}
template <typename K, typename V>
int size_Map(void* _ref){
	Map<K, V>* _this = (Map<K, V>*) _ref;
	return _this->table.size(_this->data);
}
template <typename K, typename V>
Map<K, V> mapValues_Map(void* _ref, F1R<V, V> mapper){
	Map<K, V>* _this = (Map<K, V>*) _ref;
	return _this->table.mapValues(_this->data, mapper);
}
template <typename K, typename V>
Map<K, V> removeKey_Map(void* _ref, K key){
	Map<K, V>* _this = (Map<K, V>*) _ref;
	return _this->table.removeKey(_this->data, key);
}
template <typename K, typename V>
Option<V> get_Map(void* _ref, K key){
	Map<K, V>* _this = (Map<K, V>*) _ref;
	return _this->table.get(_this->data, key);
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
Path resolve_Path(void* _ref, char* child){
	Path* _this = (Path*) _ref;
	return _this->table.resolve(_this->data, child);
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
		case OptionTag::NoneVariant:
			_ret = map_None(&(_this->data.None), mapper);
			break;
		case OptionTag::SomeVariant:
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
		case OptionTag::NoneVariant:
			_ret = orElse_None(&(_this->data.None), other);
			break;
		case OptionTag::SomeVariant:
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
		case OptionTag::NoneVariant:
			_ret = flatMap_None(&(_this->data.None), mapper);
			break;
		case OptionTag::SomeVariant:
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
		case OptionTag::NoneVariant:
			_ret = orElseGet_None(&(_this->data.None), other);
			break;
		case OptionTag::SomeVariant:
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
		case OptionTag::NoneVariant:
			_ret = iter_None(&(_this->data.None));
			break;
		case OptionTag::SomeVariant:
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
		case OptionTag::NoneVariant:
			_ret = or_None(&(_this->data.None), other);
			break;
		case OptionTag::SomeVariant:
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
		case OptionTag::NoneVariant:
			_ret = toTuple_None(&(_this->data.None), other);
			break;
		case OptionTag::SomeVariant:
			_ret = toTuple_Some(&(_this->data.Some), other);
			break;
	}
	return _ret;
}
template <typename R, typename T>
Option<Tuple<T, R>> and_Option(void* _ref, FR<Option<R>> other){
	Option<T>* _this = (Option<T>*) _ref;
	Option<Tuple<T, R>> _ret;
	switch (_this->variant) {
		case OptionTag::NoneVariant:
			_ret = and_None(&(_this->data.None), other);
			break;
		case OptionTag::SomeVariant:
			_ret = and_Some(&(_this->data.Some), other);
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
		case ResultTag::ErrVariant:
			_ret = mapValue_Err(&(_this->data.Err), mapper);
			break;
		case ResultTag::OkVariant:
			_ret = mapValue_Ok(&(_this->data.Ok), mapper);
			break;
	}
	return _ret;
}
char* getName_CNamedType(void* _ref){
	CNamedType* _this = (CNamedType*) _ref;
	char* _ret;
	switch (_this->variant) {
		case CNamedTypeTag::CTemplateTypeVariant:
			_ret = getName_CTemplateType(&(_this->data.CTemplateType));
			break;
		case CNamedTypeTag::IdentifierVariant:
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
		case CStructMemberTag::EmptyStructMemberVariant:
			_ret = generate_EmptyStructMember(&(_this->data.EmptyStructMember));
			break;
		case CStructMemberTag::CFieldVariant:
			_ret = generate_CField(&(_this->data.CField));
			break;
		case CStructMemberTag::CFunctionDeclarationVariant:
			_ret = generate_CFunctionDeclaration(&(_this->data.CFunctionDeclaration));
			break;
		case CStructMemberTag::PlaceholderVariant:
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
JType replace_JType(void* _ref, Map<char*, JType> mappings){
	JType* _this = (JType*) _ref;
	return _this->table.replace(_this->data, mappings);
}
char* generate_CDefinable(void* _ref){
	CDefinable* _this = (CDefinable*) _ref;
	char* _ret;
	switch (_this->variant) {
		case CDefinableTag::CDeclarationVariant:
			_ret = generate_CDeclaration(&(_this->data.CDeclaration));
			break;
		case CDefinableTag::CFunctionDeclarationVariant:
			_ret = generate_CFunctionDeclaration(&(_this->data.CFunctionDeclaration));
			break;
		case CDefinableTag::PlaceholderVariant:
			_ret = generate_Placeholder(&(_this->data.Placeholder));
			break;
	}
	return _ret;
}
List<char*> extractIdentifiers_CDefinable(void* _ref){
	CDefinable* _this = (CDefinable*) _ref;
	List<char*> _ret;
	switch (_this->variant) {
		case CDefinableTag::CDeclarationVariant:
			_ret = extractIdentifiers_CDeclaration(&(_this->data.CDeclaration));
			break;
		case CDefinableTag::CFunctionDeclarationVariant:
			_ret = extractIdentifiers_CFunctionDeclaration(&(_this->data.CFunctionDeclaration));
			break;
		case CDefinableTag::PlaceholderVariant:
			_ret = extractIdentifiers_Placeholder(&(_this->data.Placeholder));
			break;
	}
	return _ret;
}
CDefinable mapTypeParameters_CDefinable(void* _ref, F1R<List<char*>, List<char*>> mapper){
	CDefinable* _this = (CDefinable*) _ref;
	CDefinable _ret;
	switch (_this->variant) {
		case CDefinableTag::CDeclarationVariant:
			_ret = mapTypeParameters_CDeclaration(&(_this->data.CDeclaration), mapper);
			break;
		case CDefinableTag::CFunctionDeclarationVariant:
			_ret = mapTypeParameters_CFunctionDeclaration(&(_this->data.CFunctionDeclaration), mapper);
			break;
		case CDefinableTag::PlaceholderVariant:
			_ret = mapTypeParameters_Placeholder(&(_this->data.Placeholder), mapper);
			break;
	}
	return _ret;
}
CDefinable mapName_CDefinable(void* _ref, F1R<char*, char*> mapper){
	CDefinable* _this = (CDefinable*) _ref;
	CDefinable _ret;
	switch (_this->variant) {
		case CDefinableTag::CDeclarationVariant:
			_ret = mapName_CDeclaration(&(_this->data.CDeclaration), mapper);
			break;
		case CDefinableTag::CFunctionDeclarationVariant:
			_ret = mapName_CFunctionDeclaration(&(_this->data.CFunctionDeclaration), mapper);
			break;
		case CDefinableTag::PlaceholderVariant:
			_ret = mapName_Placeholder(&(_this->data.Placeholder), mapper);
			break;
	}
	return _ret;
}
char* generate_CStructureOrUnion(void* _ref){
	CStructureOrUnion* _this = (CStructureOrUnion*) _ref;
	char* _ret;
	switch (_this->variant) {
		case CStructureOrUnionTag::CStructureVariant:
			_ret = generate_CStructure(&(_this->data.CStructure));
			break;
		case CStructureOrUnionTag::CUnionVariant:
			_ret = generate_CUnion(&(_this->data.CUnion));
			break;
	}
	return _ret;
}
List<char*> findDependencies_CStructureOrUnion(void* _ref){
	CStructureOrUnion* _this = (CStructureOrUnion*) _ref;
	List<char*> _ret;
	switch (_this->variant) {
		case CStructureOrUnionTag::CStructureVariant:
			_ret = findDependencies_CStructure(&(_this->data.CStructure));
			break;
		case CStructureOrUnionTag::CUnionVariant:
			_ret = findDependencies_CUnion(&(_this->data.CUnion));
			break;
	}
	return _ret;
}
char* findName_CStructureOrUnion(void* _ref){
	CStructureOrUnion* _this = (CStructureOrUnion*) _ref;
	char* _ret;
	switch (_this->variant) {
		case CStructureOrUnionTag::CStructureVariant:
			_ret = findName_CStructure(&(_this->data.CStructure));
			break;
		case CStructureOrUnionTag::CUnionVariant:
			_ret = findName_CUnion(&(_this->data.CUnion));
			break;
	}
	return _ret;
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
	return collect_Iter(&(map_Iter(&(iter_List(&(_this->list))), ??? access ???)), new_Joiner());
}
StringBuilder new_StringBuilder(List<char> list){
	StringBuilder _thisInstance;
	StringBuilder* _this = &_thisInstance;
	_this->list = list;
	return _thisInstance;
}
template <typename T, typename R>
Head<Tuple<T, R>> toHead_ZipHead(void* _ref){
	ZipHead<T, R>* _this = (ZipHead<T, R>*) _ref;
	return _impl;
}
template <typename T, typename R>
Option<Tuple<T, R>> next_ZipHead(void* _ref){
	ZipHead<T, R>* _this = (ZipHead<T, R>*) _ref;
	return and_Option(&(next_Head(&(_this->leftHead))), ??? access ???);
}
template <typename T, typename R>
ZipHead<T, R> new_ZipHead(Head<T> leftHead, Head<R> rightHead){
	ZipHead<T, R> _thisInstance;
	ZipHead<T, R>* _this = &_thisInstance;
	_this->leftHead = leftHead;
	_this->rightHead = rightHead;
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
/*Identifier 'R' has not been defined*/ lambda0(void* _ref, T element){
	return apply_F2R(&(folder), finalCurrent, element);
}
/*Identifier 'R' has not been defined*/ lambda1(void* _ref){
	return finalCurrent;
}
template <typename R, typename T>
R fold_Iter(void* _ref, R initial, F2R<R, T, R> folder){
	Iter<T>* _this = (Iter<T>*) _ref;
	/*Identifier 'R' has not been defined*/ current = initial;
	while (true) {
		/*Identifier 'R' has not been defined*/ finalCurrent = current;
		Tuple tuple = toTuple_Option(&(map_Option(&(next_Head(&(_this->head))), lambda0)), lambda1);
		if (tuple.left) 
			current = tuple.right;
		/*else
					return*/ current;
	}
}
template <typename C, typename T>
C collect_Iter(void* _ref, Collector<T, C> collector){
	Iter<T>* _this = (Iter<T>*) _ref;
	return fold_Iter(&((*_this)), createInitial_Collector(&(collector)), ??? access ???);
}
template <typename T>
List<T> toList_Iter(void* _ref){
	Iter<T>* _this = (Iter<T>*) _ref;
	return collect_Iter(&((*_this)), new_ListCollector());
}
todoLambda lambda2(void* _ref, T element){
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
template <typename R, typename T>
Iter<Tuple<T, R>> zip_Iter(void* _ref, Iter<R> other){
	Iter<T>* _this = (Iter<T>*) _ref;
	return new_Iter(new_ZipHead(_this->head, other.head));
}
template <typename T>
Iter<T> new_Iter(Head<T> head){
	Iter<T> _thisInstance;
	Iter<T>* _this = &_thisInstance;
	_this->head = head;
	return _thisInstance;
}
Head<int> toHead_RangeHead(void* _ref){
	RangeHead* _this = (RangeHead*) _ref;
	return _impl;
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
	if (_this->counter < _this->length) {
		int value = _this->counter;
		_this->counter++;
		return new_Some(value);
	}
	/*else
				return new None<Integer>()*/;
}
template <typename T, typename X>
Result<T, X> toResult_Err(void* _ref){
	Err<T, X>* _this = (Err<T, X>*) _ref;
	ResultData<T, X> data;
	data.Err = *_this;
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
	Ok<T, X>* _this = (Ok<T, X>*) _ref;
	ResultData<T, X> data;
	data.Ok = *_this;
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
	return _this->depth == 1;
}
int isLevel_State(void* _ref){
	State* _this = (State*) _ref;
	return _this->depth == 0;
}
State append_State(void* _ref, char next){
	State* _this = (State*) _ref;
	_this->buffer = appendChar_StringBuilder(&(_this->buffer), next);
	return (*_this);
}
Option<char> pop_State(void* _ref){
	State* _this = (State*) _ref;
	if (_this->index < length_String(&(_this->input))) {
		char value = charAt_String(&(_this->input), _this->index);
		_this->index++;
		return new_Some(value);
	}
	/*else
				return new None<Character>()*/;
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
todoLambda lambda3(void* _ref, char popped){
	State appended = append_State(&((*_this)), popped);
	return new_Tuple(appended, popped);
}
Option<Tuple<State, char>> popAndAppendToTuple_State(void* _ref){
	State* _this = (State*) _ref;
	return map_Option(&(pop_State(&((*_this)))), lambda3);
}
State lambda4(void* _ref, Tuple<State, char> tuple){
	return tuple.left;
}
Option<State> popAndAppendToOption_State(void* _ref){
	State* _this = (State*) _ref;
	return map_Option(&(popAndAppendToTuple_State(&((*_this)))), lambda4);
}
Option<char> peek_State(void* _ref){
	State* _this = (State*) _ref;
	if (_this->index < length_String(&(_this->input))) 
		return new_Some(charAt_String(&(_this->input), _this->index));
	return new_None();
}
CType toCType_CPointerType(void* _ref){
	CPointerType* _this = (CPointerType*) _ref;
	return _impl;
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
	CTemplateType* _this = (CTemplateType*) _ref;
	CNamedTypeData data;
	data.CTemplateType = *_this;
	return { CNamedTypeTag::CTemplateTypeVariant, data };
}
CTemplateType new_CTemplateType(char* base, List<CType> typeArguments){
	CTemplateType _thisInstance;
	CTemplateType* _this = &_thisInstance;
	_this->base = base;
	_this->typeArguments = typeArguments;
	;
	return _thisInstance;
}
char* generate_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	/*Identifier 'C' has not been defined*/ typeArguments = collect_Iter(&(map_Iter(&(iter_List(&(_this->typeArguments))), ??? access ???)), new_Joiner(", "));
	return _this->base + "<" + typeArguments + ">";
}
char* toBaseName_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	return _this->base;
}
List<char*> extractIdentifiers_CTemplateType(void* _ref){
	CTemplateType* _this = (CTemplateType*) _ref;
	return addLast_List(&(toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(_this->typeArguments))), ??? access ???)), ??? access ???)))), _this->base);
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
	CQuantity* _this = (CQuantity*) _ref;
	return _impl;
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
	CDereference* _this = (CDereference*) _ref;
	return _impl;
}
char* generate_CDereference(void* _ref){
	CDereference* _this = (CDereference*) _ref;
	return "*" + generate_CExpression(&(_this->expression));
}
CDereference new_CDereference(CExpression expression){
	CDereference _thisInstance;
	CDereference* _this = &_thisInstance;
	_this->expression = expression;
	return _thisInstance;
}
CNamedType toCNamedType_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	CNamedTypeData data;
	data.Identifier = *_this;
	return { CNamedTypeTag::IdentifierVariant, data };
}
JType toJType_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _impl;
}
JExpression toJExpression_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _impl;
}
CExpression toCExpression_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _impl;
}
todoLambda lambda5(void* _ref, int i){
	char c = charAt_String(&(stripped), i);
	return c == '_' || isLetter(c) || (i != 0 && isDigit(c));
}
int isIdentifier_Identifier(void* _ref, char* input){
	Identifier* _this = (Identifier*) _ref;
	char* stripped = strip_String(&(input));
	if (isEmpty_String(&(stripped)) || equals_String(&(stripped), "return")) 
		return false;
	return collect_Iter(&(new_Iter(new_RangeHead(length_String(&(stripped))))), new_AllMatch(lambda5));
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
	return /*Lists.<String>empty().addLast(this.value)*/;
}
char* stringify_Identifier(void* _ref){
	Identifier* _this = (Identifier*) _ref;
	return _this->value;
}
JType replace_Identifier(void* _ref, Map<char*, JType> mappings){
	Identifier* _this = (Identifier*) _ref;
	return orElse_Option(&(get_Map(&(mappings), _this->value)), (*_this));
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
	Placeholder* _this = (Placeholder*) _ref;
	return _impl;
}
JMethodDeclaration toJMethodDeclaration_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	JMethodDeclarationData data;
	data.Placeholder = *_this;
	return { JMethodDeclarationTag::PlaceholderVariant, data };
}
CStructMember toCStructMember_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	CStructMemberData data;
	data.Placeholder = *_this;
	return { CStructMemberTag::PlaceholderVariant, data };
}
CAssignable toCAssignable_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return _impl;
}
JAssignable toJAssignable_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return _impl;
}
JType toJType_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return _impl;
}
JObjectMember toJObjectMember_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return _impl;
}
CExpression toCExpression_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return _impl;
}
CDefinable toCDefinable_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	CDefinableData data;
	data.Placeholder = *_this;
	return { CDefinableTag::PlaceholderVariant, data };
}
JExpression toJExpression_Placeholder(void* _ref){
	Placeholder* _this = (Placeholder*) _ref;
	return _impl;
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
JType replace_Placeholder(void* _ref, Map<char*, JType> mappings){
	Placeholder* _this = (Placeholder*) _ref;
	return (*_this);
}
Placeholder new_Placeholder(char* input){
	Placeholder _thisInstance;
	Placeholder* _this = &_thisInstance;
	_this->input = input;
	return _thisInstance;
}
JMethodDeclaration toJMethodDeclaration_JConstructor(void* _ref){
	JConstructor* _this = (JConstructor*) _ref;
	JMethodDeclarationData data;
	data.JConstructor = *_this;
	return { JMethodDeclarationTag::JConstructorVariant, data };
}
JConstructor new_JConstructor(char* type){
	JConstructor _thisInstance;
	JConstructor* _this = &_thisInstance;
	_this->type = type;
	return _thisInstance;
}
JMethodDeclaration toJMethodDeclaration_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	JMethodDeclarationData data;
	data.JDeclaration = *_this;
	return { JMethodDeclarationTag::JDeclarationVariant, data };
}
JAssignable toJAssignable_JDeclaration(void* _ref){
	JDeclaration* _this = (JDeclaration*) _ref;
	return _impl;
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
	/*else
				annotationsString */ = "annotations=" + _this->annotations + ", ";
	char* typeParametersString;
	if (isEmpty_List(&(_this->typeParameters))) 
		typeParametersString = "";
	/*else
				typeParametersString */ = "typeParameters=" + _this->typeParameters + ", ";
	char* maybeBeforeTypeString;
	if (??? instanceof ???) 
		maybeBeforeTypeString = "maybeBeforeType=" + result + ", ";
	/*else
				maybeBeforeTypeString */ = "";
	return "JDeclaration {" + annotationsString + typeParametersString + maybeBeforeTypeString + "type=" + _this->type + ", name='" + _this->name + '\'' + '}';
}
JDeclaration mapName_JDeclaration(void* _ref, F1R<char*, char*> mapper){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_JDeclaration(_this->annotations, _this->typeParameters, _this->maybeBeforeType, _this->type, apply_F1R(&(mapper), _this->name));
}
JDeclaration withType_JDeclaration(void* _ref, JType type){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_JDeclaration(_this->annotations, _this->typeParameters, _this->maybeBeforeType, type, _this->name);
}
JDeclaration mapType_JDeclaration(void* _ref, F1R<JType, JType> mapper){
	JDeclaration* _this = (JDeclaration*) _ref;
	return new_JDeclaration(_this->annotations, _this->typeParameters, _this->maybeBeforeType, apply_F1R(&(mapper), _this->type), _this->name);
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
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	CDefinableData data;
	data.CFunctionDeclaration = *_this;
	return { CDefinableTag::CFunctionDeclarationVariant, data };
}
CStructMember toCStructMember_CFunctionDeclaration(void* _ref){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	CStructMemberData data;
	data.CFunctionDeclaration = *_this;
	return { CStructMemberTag::CFunctionDeclarationVariant, data };
}
char* generate_CFunctionDeclaration(void* _ref){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	char* joinedParameterTypes = "(" + this.parameterTypes.iter().map(CType::generate).collect(new Joiner(", "))
					+ ")";
	return generate_CType(&(_this->type)) + " (*" + this.name + ")" + joinedParameterTypes;
}
List<char*> extractIdentifiers_CFunctionDeclaration(void* _ref){
	CFunctionDeclaration* _this = (CFunctionDeclaration*) _ref;
	return addAllLast_List(&(extractIdentifiers_CType(&(_this->type))), toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(_this->parameterTypes))), ??? access ???)), ??? access ???))));
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
	EmptyStructMember* _this = (EmptyStructMember*) _ref;
	CStructMemberData data;
	data.EmptyStructMember = *_this;
	return { CStructMemberTag::EmptyStructMemberVariant, data };
}
JObjectMember toJObjectMember_EmptyStructMember(void* _ref){
	EmptyStructMember* _this = (EmptyStructMember*) _ref;
	return _impl;
}
char* generate_EmptyStructMember(void* _ref){
	EmptyStructMember* _this = (EmptyStructMember*) _ref;
	return "";
}
Folder toFolder_EscapedFolder(void* _ref){
	EscapedFolder* _this = (EscapedFolder*) _ref;
	return _impl;
}
todoLambda lambda6(void* _ref, Tuple<State, char> tuple){
	if (tuple.right == '\\') 
		return orElse_Option(&(popAndAppendToOption_State(&(tuple.left))), tuple.left);
	return tuple.left;
}
State apply_EscapedFolder(void* _ref, State state, char next){
	EscapedFolder* _this = (EscapedFolder*) _ref;
	if (next == '/') {
		Option peek = peek_State(&(state));
		if (??? instanceof ???) {
			State withNext = append_State(&(state), next);
			State current = orElse_Option(&(popAndAppendToOption_State(&(withNext))), withNext);
			while (true) {
				Option tupleOption = popAndAppendToTuple_State(&(current));
				if (??? instanceof ???) {
					current = pair.left;
					if (pair.right == '*') {
						Option peekAgain = peek_State(&(current));
						if (??? instanceof ???) {
							current = orElse_Option(&(popAndAppendToOption_State(&(current))), current);
							break;
						}
					}
				}
				/*else
							break*/;
			}
			return current;
		}
	}
	if (next == '\'') {
		State appended = append_State(&(state), next);
		return orElse_Option(&(flatMap_Option(&(map_Option(&(popAndAppendToTuple_State(&(appended))), lambda6)), ??? access ???)), appended);
	}
	if (next == '\"') {
		State current = append_State(&(state), next);
		while (true) {
			Option maybeTuple = popAndAppendToTuple_State(&(current));
			if (!(??? instanceof ???)) 
				break;
			current = value.left;
			/*Cannot access member 'right' in 'Placeholder[input=Undefined identifier: value]', not an object.*/ right = value.right;
			if (right == '\\') 
				current = orElse_Option(&(popAndAppendToOption_State(&(current))), current);
			if (right == '\"') 
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
	ValueFolder* _this = (ValueFolder*) _ref;
	return _impl;
}
State apply_ValueFolder(void* _ref, State state, char next){
	ValueFolder* _this = (ValueFolder*) _ref;
	if (next == ',' && isLevel_State(&(state))) 
		return advance_State(&(state));
	State appended = append_State(&(state), next);
	if (next == '-') {
		Option peeked = peek_State(&(appended));
		if (??? instanceof ???) 
			return orElse_Option(&(popAndAppendToOption_State(&(appended))), appended);
		/*else
					return*/ appended;
	}
	if (next == '<' || next == '(') 
		return enter_State(&(appended));
	if (next == '>' || next == ')') 
		return exit_State(&(appended));
	return appended;
}
template <typename T>
Option<T> toOption_Some(void* _ref){
	Some<T>* _this = (Some<T>*) _ref;
	OptionData<T> data;
	data.Some = *_this;
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
Tuple lambda7(void* _ref, R otherValue){
	return new_Tuple(_this->value, otherValue);
}
template <typename R, typename T>
Option<Tuple<T, R>> and_Some(void* _ref, FR<Option<R>> other){
	Some<T>* _this = (Some<T>*) _ref;
	return map_Option(&(apply_FR(&(other))), lambda7);
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
	None<T>* _this = (None<T>*) _ref;
	OptionData<T> data;
	data.None = *_this;
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
template <typename R, typename T>
Option<Tuple<T, R>> and_None(void* _ref, FR<Option<R>> other){
	None<T>* _this = (None<T>*) _ref;
	return new_None();
}
Folder toFolder_ConditionEndLocator(void* _ref){
	ConditionEndLocator* _this = (ConditionEndLocator*) _ref;
	return _impl;
}
State apply_ConditionEndLocator(void* _ref, State state, char c){
	ConditionEndLocator* _this = (ConditionEndLocator*) _ref;
	State appended = append_State(&(state), c);
	if (c == '(') 
		return enter_State(&(appended));
	if (c == ')') {
		if (isLevel_State(&(appended))) 
			return advance_State(&(appended));
		return exit_State(&(appended));
	}
	return appended;
}
CStructMember toCStructMember_CField(void* _ref){
	CField* _this = (CField*) _ref;
	CStructMemberData data;
	data.CField = *_this;
	return { CStructMemberTag::CFieldVariant, data };
}
char* generate_CField(void* _ref){
	CField* _this = (CField*) _ref;
	return generateStatement(1, generate_CDefinable(&(_this->declaration)));
}
CField new_CField(CDefinable declaration){
	CField _thisInstance;
	CField* _this = &_thisInstance;
	_this->declaration = declaration;
	return _thisInstance;
}
todoLambda lambda8(void* _ref, int index){
	return /*elements[index]*/;
}
template <typename T>
Iter<T> fromObjArray_Streams(void* _ref, T* elements){
	Streams* _this = (Streams*) _ref;
	return map_Iter(&(new_Iter(new_RangeHead(elements.length))), lambda8);
}
template <typename T, typename R>
Head<R> toHead_MapHead(void* _ref){
	MapHead<T, R>* _this = (MapHead<T, R>*) _ref;
	return _impl;
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
	SingleHead<T>* _this = (SingleHead<T>*) _ref;
	return _impl;
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
	FlatMapHead<T, R>* _this = (FlatMapHead<T, R>*) _ref;
	return _impl;
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
		if (??? instanceof ???) {
			/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Cannot access member 'head' in 'Placeholder[input=Undefined identifier: current]', not an object.]', not an object.]*/ next = next_/*Cannot access member 'head' in 'Placeholder[input=Undefined identifier: current]', not an object.*/(&(current.head));
			if (??? instanceof ???) 
				return next;
		}
		Option maybeNext = next_Head(&(_this->head));
		if (??? instanceof ???) 
			return new_None();
		_this->maybeCurrent = map_Option(&(maybeNext), _this->mapper);
	}
}
template <typename T>
Head<T> toHead_EmptyHead(void* _ref){
	EmptyHead<T>* _this = (EmptyHead<T>*) _ref;
	return _impl;
}
template <typename T>
Option<T> next_EmptyHead(void* _ref){
	EmptyHead<T>* _this = (EmptyHead<T>*) _ref;
	return new_None();
}
template <typename T>
Collector<T, int> toCollector_AnyMatch(void* _ref){
	AnyMatch<T>* _this = (AnyMatch<T>*) _ref;
	return _impl;
}
template <typename T>
int createInitial_AnyMatch(void* _ref){
	AnyMatch<T>* _this = (AnyMatch<T>*) _ref;
	return false;
}
template <typename T>
int fold_AnyMatch(void* _ref, int aBoolean, T t){
	AnyMatch<T>* _this = (AnyMatch<T>*) _ref;
	return aBoolean || apply_F1R(&(_this->predicate), t);
}
template <typename T>
AnyMatch<T> new_AnyMatch(F1R<T, int> predicate){
	AnyMatch<T> _thisInstance;
	AnyMatch<T>* _this = &_thisInstance;
	_this->predicate = predicate;
	return _thisInstance;
}
Collector<char*, char*> toCollector_Joiner(void* _ref){
	Joiner* _this = (Joiner*) _ref;
	return _impl;
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
	ListCollector<T>* _this = (ListCollector<T>*) _ref;
	return _impl;
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
	CDeclaration* _this = (CDeclaration*) _ref;
	return _impl;
}
CDefinable toCDefinable_CDeclaration(void* _ref){
	CDeclaration* _this = (CDeclaration*) _ref;
	CDefinableData data;
	data.CDeclaration = *_this;
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
	return template + generate_CType(&(_this->type)) + " " + _this->name;
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
	JArrayType* _this = (JArrayType*) _ref;
	return _impl;
}
char* stringify_JArrayType(void* _ref){
	JArrayType* _this = (JArrayType*) _ref;
	return stringify_JType(&(_this->type)) + "_array";
}
JType replace_JArrayType(void* _ref, Map<char*, JType> mappings){
	JArrayType* _this = (JArrayType*) _ref;
	return new_JFunctionalType(replace_JType(&(_this->type), mappings));
}
JArrayType new_JArrayType(JType type){
	JArrayType _thisInstance;
	JArrayType* _this = &_thisInstance;
	_this->type = type;
	return _thisInstance;
}
JType toJType_JGenericType(void* _ref){
	JGenericType* _this = (JGenericType*) _ref;
	return _impl;
}
int lambda9(void* _ref, char* slice){
	return "_" + slice;
}
char* stringify_JGenericType(void* _ref){
	JGenericType* _this = (JGenericType*) _ref;
	/*Identifier 'C' has not been defined*/ joined = collect_Iter(&(map_Iter(&(map_Iter(&(iter_List(&(_this->typeArguments))), ??? access ???)), lambda9)), new_Joiner());
	return _this->base + joined;
}
JType lambda10(void* _ref, JType typeArgument){
	return replace_JType(&(typeArgument), mappings);
}
JType replace_JGenericType(void* _ref, Map<char*, JType> mappings){
	JGenericType* _this = (JGenericType*) _ref;
	List newTypeArguments = toList_Iter(&(map_Iter(&(iter_List(&(_this->typeArguments))), lambda10)));
	return new_JGenericType(_this->base, newTypeArguments);
}
JGenericType new_JGenericType(char* base, List<JType> typeArguments){
	JGenericType _thisInstance;
	JGenericType* _this = &_thisInstance;
	_this->base = base;
	_this->typeArguments = typeArguments;
	return _thisInstance;
}
CExpression toCExpression_CPointerAccess(void* _ref){
	CPointerAccess* _this = (CPointerAccess*) _ref;
	return _impl;
}
char* generate_CPointerAccess(void* _ref){
	CPointerAccess* _this = (CPointerAccess*) _ref;
	return generate_CExpression(&(_this->instance)) + "->" + _this->fieldName;
}
CPointerAccess new_CPointerAccess(CExpression instance, char* fieldName){
	CPointerAccess _thisInstance;
	CPointerAccess* _this = &_thisInstance;
	_this->instance = instance;
	_this->fieldName = fieldName;
	return _thisInstance;
}
CExpression toCExpression_CFieldAccess(void* _ref){
	CFieldAccess* _this = (CFieldAccess*) _ref;
	return _impl;
}
char* generate_CFieldAccess(void* _ref){
	CFieldAccess* _this = (CFieldAccess*) _ref;
	return generate_CExpression(&(_this->instance)) + "." + _this->fieldName;
}
CFieldAccess new_CFieldAccess(CExpression instance, char* fieldName){
	CFieldAccess _thisInstance;
	CFieldAccess* _this = &_thisInstance;
	_this->instance = instance;
	_this->fieldName = fieldName;
	return _thisInstance;
}
JExpression toJExpression_JMemberAccess(void* _ref){
	JMemberAccess* _this = (JMemberAccess*) _ref;
	return _impl;
}
JMemberAccess new_JMemberAccess(JExpression instance, char* memberName){
	JMemberAccess _thisInstance;
	JMemberAccess* _this = &_thisInstance;
	_this->instance = instance;
	_this->memberName = memberName;
	return _thisInstance;
}
JCaller toJCaller_JConstruction(void* _ref){
	JConstruction* _this = (JConstruction*) _ref;
	return _impl;
}
JConstruction new_JConstruction(JType jType){
	JConstruction _thisInstance;
	JConstruction* _this = &_thisInstance;
	_this->jType = jType;
	return _thisInstance;
}
CExpression toCExpression_CInvocation(void* _ref){
	CInvocation* _this = (CInvocation*) _ref;
	return _impl;
}
char* generate_CInvocation(void* _ref){
	CInvocation* _this = (CInvocation*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CExpression]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=CExpression]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=CExpression], Identifier[value=CExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CExpression]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]*/ joinedArguments = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CExpression]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=CExpression]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=CExpression], Identifier[value=CExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CExpression]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CExpression]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=CExpression]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=CExpression], Identifier[value=CExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CExpression]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='removeElement'}], typeParameters=[]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CExpression]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=CExpression]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=CExpression], Identifier[value=CExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CExpression]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CExpression]]]], name='removeElement'}], typeParameters=[]]*/(&(cArguments_CInvocation(&((*_this)))))), ??? access ???)), new_Joiner(", "));
	return generate_/*Not a functional type: JObjectType[name=CExpression, variants=[], members=[], typeParameters=[]]*/(&(expression_CInvocation(&((*_this))))) + "(" + joinedArguments + ")";
}
CInvocation new_CInvocation(CExpression expression, List<CExpression> cArguments){
	CInvocation _thisInstance;
	CInvocation* _this = &_thisInstance;
	_this->expression = expression;
	_this->cArguments = cArguments;
	return _thisInstance;
}
JExpression toJExpression_JInvokable(void* _ref){
	JInvokable* _this = (JInvokable*) _ref;
	return _impl;
}
JInvokable new_JInvokable(JCaller caller, List<JExpression> arguments){
	JInvokable _thisInstance;
	JInvokable* _this = &_thisInstance;
	_this->caller = caller;
	_this->arguments = arguments;
	return _thisInstance;
}
JType toJType_JFunctionalType(void* _ref){
	JFunctionalType* _this = (JFunctionalType*) _ref;
	return _impl;
}
JFunctionalType new_JFunctionalType(JType returnType){
	JFunctionalType _thisInstance;
	JFunctionalType* _this = &_thisInstance;
	(*_this)(new_JavaList(), returnType);
	return _thisInstance;
}
int lambda11(void* _ref, char* slice){
	return "_" + slice;
}
char* stringify_JFunctionalType(void* _ref){
	JFunctionalType* _this = (JFunctionalType*) _ref;
	/*Identifier 'C' has not been defined*/ joined = collect_Iter(&(map_Iter(&(map_Iter(&(iter_List(&(_this->parameterTypes))), ??? access ???)), lambda11)), new_Joiner());
	return "func_" + stringify_JType(&(_this->returnType)) + joined;
}
JType lambda12(void* _ref, JType parameterType){
	return replace_JType(&(parameterType), mappings);
}
JType replace_JFunctionalType(void* _ref, Map<char*, JType> mappings){
	JFunctionalType* _this = (JFunctionalType*) _ref;
	return new_JFunctionalType(toList_Iter(&(map_Iter(&(iter_List(&(_this->parameterTypes))), lambda12))), replace_JType(&(_this->returnType), mappings));
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
Option lambda13(void* _ref, Frame frame){
	return resolveExpression_Frame(&(frame), identifier);
}
Option<JDeclaration> resolveExpression_Environment(void* _ref, char* identifier){
	Environment* _this = (Environment*) _ref;
	return next_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(_this->frames))), lambda13)), ??? access ???)));
}
template <typename T>
Tuple<Environment, T> within_Environment(void* _ref, F1R<Environment, Tuple<Environment, T>> FR){
	Environment* _this = (Environment*) _ref;
	Environment withLastEnv = enter_Environment(&((*_this)));
	Tuple result = apply_F1R(&(FR), withLastEnv);
	Environment exited = exit_Environment(&(result.left));
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
Frame lambda14(void* _ref, Frame last){
	return defineAllExpressions_Frame(&(last), declarations);
}
Environment defineAllExpressions_Environment(void* _ref, List<JDeclaration> declarations){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(&(_this->frames), lambda14));
}
Frame lambda15(void* _ref, Frame last){
	return defineExpression_Frame(&(last), declaration);
}
Environment defineExpression_Environment(void* _ref, JDeclaration declaration){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(&(_this->frames), lambda15));
}
Option<JObjectType> resolveCurrent_Environment(void* _ref){
	Environment* _this = (Environment*) _ref;
	return next_Iter(&(flatMap_Iter(&(map_Iter(&(iterReversed_List(&(_this->frames))), ??? access ???)), ??? access ???)));
}
Frame lambda16(void* _ref, Frame last){
	return withObject_Frame(&(last), object);
}
Environment withObject_Environment(void* _ref, JObject object){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(&(_this->frames), lambda16));
}
Option lambda17(void* _ref, Frame frame){
	return resolveType_Frame(&(frame), name);
}
Option<JObjectType> resolveType_Environment(void* _ref, char* name){
	Environment* _this = (Environment*) _ref;
	return next_Iter(&(flatMap_Iter(&(map_Iter(&(iterReversed_List(&(_this->frames))), lambda17)), ??? access ???)));
}
Frame lambda18(void* _ref, Frame last){
	return defineAllTypes_Frame(&(last), types);
}
Environment defineAllTypes_Environment(void* _ref, List<JObjectType> types){
	Environment* _this = (Environment*) _ref;
	return new_Environment(mapLast_List(&(_this->frames), lambda18));
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
/*Not a functional type: Placeholder[input=Member 'equals' not defined in 'magma.Main$JRecursiveType@58c2f281']*/ lambda19(void* _ref, JDeclaration define){
	return equals_String(&(define.name), identifier);
}
Option<JDeclaration> resolveExpression_Frame(void* _ref, char* identifier){
	Frame* _this = (Frame*) _ref;
	return next_Iter(&(filter_Iter(&(iter_List(&(_this->definedExpressions))), lambda19)));
}
Frame defineExpression_Frame(void* _ref, JDeclaration declaration){
	Frame* _this = (Frame*) _ref;
	return new_Frame(_this->maybeObject, _this->definedTypes, addLast_List(&(_this->definedExpressions), declaration));
}
JObjectType lambda20(void* _ref, JObject obj){
	return new_JObjectType(obj.name, obj.variants, _this->definedExpressions, obj.typeParameters);
}
Option<JObjectType> toObjectType_Frame(void* _ref){
	Frame* _this = (Frame*) _ref;
	return map_Option(&(_this->maybeObject), lambda20);
}
Frame withObject_Frame(void* _ref, JObject name){
	Frame* _this = (Frame*) _ref;
	return new_Frame(new_Some(name), _this->definedTypes, _this->definedExpressions);
}
/*Not a functional type: Placeholder[input=Member 'equals' not defined in 'magma.Main$JRecursiveType@58c2f281']*/ lambda21(void* _ref, JObjectType type){
	return equals_String(&(type.name), name);
}
Option<JObjectType> resolveType_Frame(void* _ref, char* name){
	Frame* _this = (Frame*) _ref;
	return next_Iter(&(filter_Iter(&(iter_List(&(_this->definedTypes))), lambda21)));
}
Frame defineAllTypes_Frame(void* _ref, List<JObjectType> types){
	Frame* _this = (Frame*) _ref;
	return new_Frame(_this->maybeObject, addAllLast_List(&(_this->definedTypes), types), _this->definedExpressions);
}
JType toJType_JObjectType(void* _ref){
	JObjectType* _this = (JObjectType*) _ref;
	return _impl;
}
/*Not a functional type: Placeholder[input=Member 'equals' not defined in 'magma.Main$JRecursiveType@58c2f281']*/ lambda22(void* _ref, JDeclaration member){
	return equals_String(&(member.name), name);
}
Option<JType> resolve_JObjectType(void* _ref, char* name){
	JObjectType* _this = (JObjectType*) _ref;
	return map_Option(&(next_Iter(&(filter_Iter(&(iter_List(&(_this->members))), lambda22)))), ??? access ???);
}
char* stringify_JObjectType(void* _ref){
	JObjectType* _this = (JObjectType*) _ref;
	return _this->name;
}
JType lambda23(void* _ref, JType type){
	return replace_JType(&(type), mappings);
}
JDeclaration lambda24(void* _ref, JDeclaration member){
	return mapType_JDeclaration(&(member), lambda23);
}
JType replace_JObjectType(void* _ref, Map<char*, JType> mappings){
	JObjectType* _this = (JObjectType*) _ref;
	List newMembers = toList_Iter(&(map_Iter(&(iter_List(&(_this->members))), lambda24)));
	return new_JObjectType(_this->name, _this->variants, newMembers, _this->typeParameters);
}
JType lambda25(void* _ref, JType type){
	return replace_JType(&(type), mappings);
}
JDeclaration lambda26(void* _ref, JDeclaration declaration){
	return mapType_JDeclaration(&(declaration), lambda25);
}
JObjectType specialize_JObjectType(void* _ref, List<JType> typeArguments){
	JObjectType* _this = (JObjectType*) _ref;
	/*Identifier 'C' has not been defined*/ mappings = collect_Iter(&(zip_Iter(&(iter_List(&(_this->typeParameters))), iter_List(&(typeArguments)))), new_MapCollector());
	List newMembers = toList_Iter(&(map_Iter(&(iter_List(&(_this->members))), lambda26)));
	return new_JObjectType(_this->name, _this->variants, newMembers, empty_Lists(&(Lists)));
}
JObjectType new_JObjectType(char* name, List<char*> variants, List<JDeclaration> members, List<char*> typeParameters){
	JObjectType _thisInstance;
	JObjectType* _this = &_thisInstance;
	_this->name = name;
	_this->variants = variants;
	_this->members = members;
	_this->typeParameters = typeParameters;
	return _thisInstance;
}
JType toJType_JRecursiveType(void* _ref){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	return _impl;
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
	JType apply = apply_F1R(&(mapper), created);
	set_JRecursiveType(&(created), apply);
	return created;
}
void set_JRecursiveType(void* _ref, JType created){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	_this->maybeInternal = new_Some(created);
}
char* stringify_JRecursiveType(void* _ref){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	return orElse_Option(&(map_Option(&(_this->maybeInternal), ??? access ???)), "?");
}
JType lambda27(void* _ref, JType type){
	return replace_JType(&(type), mappings);
}
JType replace_JRecursiveType(void* _ref, Map<char*, JType> mappings){
	JRecursiveType* _this = (JRecursiveType*) _ref;
	JRecursiveType jRecursiveType = new_JRecursiveType();
	jRecursiveType.maybeInternal = map_Option(&(_this->maybeInternal), lambda27);
	return jRecursiveType;
}
CStructureOrUnion toCStructureOrUnion_CStructure(void* _ref){
	CStructure* _this = (CStructure*) _ref;
	CStructureOrUnionData data;
	data.CStructure = *_this;
	return { CStructureOrUnionTag::CStructureVariant, data };
}
char* findName_CStructure(void* _ref){
	CStructure* _this = (CStructure*) _ref;
	return _this->name;
}
char* generate_CStructure(void* _ref){
	CStructure* _this = (CStructure*) _ref;
	/*Identifier 'C' has not been defined*/ joinedFields = collect_Iter(&(map_Iter(&(map_Iter(&(iter_List(&(_this->fields))), ??? access ???)), ??? access ???)), new_Joiner());
	return generateTemplateString(_this->typeParameters) + "struct " + _this->name + " {" + joinedFields + lineSeparator_/*Undefined identifier: System*/(&(System)) + "};" + lineSeparator_/*Undefined identifier: System*/(&(System));
}
/*Not a functional type: Placeholder[input=Cannot access member 'contains' in 'Placeholder[input=Cannot access member 'typeParameters' in 'Boolean', not an object.]', not an object.]*/ lambda28(void* _ref, char* value){
	return contains_/*Cannot access member 'typeParameters' in 'Boolean', not an object.*/(&(!(*_this).typeParameters), value);
}
List<char*> findDependencies_CStructure(void* _ref){
	CStructure* _this = (CStructure*) _ref;
	return collect_Iter(&(filter_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(_this->fields))), ??? access ???)), ??? access ???)), lambda28)), new_ListCollector());
}
CStructure new_CStructure(List<char*> typeParameters, char* name, List<CDefinable> fields){
	CStructure _thisInstance;
	CStructure* _this = &_thisInstance;
	_this->typeParameters = typeParameters;
	_this->name = name;
	_this->fields = fields;
	return _thisInstance;
}
int lambda29(void* _ref, char* variant){
	return variant + "Variant";
}
int lambda30(void* _ref, char* variant){
	return generateIndent(1) + variant;
}
char* generate_CEnum(void* _ref){
	CEnum* _this = (CEnum*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ enumFields = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]*/(&(variants_CEnum(&((*_this)))))), lambda29)), lambda30)), new_Joiner(","));
	return "enum class " + _this->name + " {" + enumFields + lineSeparator_/*Undefined identifier: System*/(&(System)) + "};" + lineSeparator_/*Undefined identifier: System*/(&(System));
}
CEnum new_CEnum(char* name, List<char*> variants){
	CEnum _thisInstance;
	CEnum* _this = &_thisInstance;
	_this->name = name;
	_this->variants = variants;
	return _thisInstance;
}
CStructureOrUnion toCStructureOrUnion_CUnion(void* _ref){
	CUnion* _this = (CUnion*) _ref;
	CStructureOrUnionData data;
	data.CUnion = *_this;
	return { CStructureOrUnionTag::CUnionVariant, data };
}
char* findName_CUnion(void* _ref){
	CUnion* _this = (CUnion*) _ref;
	return _this->name + "Data";
}
char* generate_CUnion(void* _ref){
	CUnion* _this = (CUnion*) _ref;
	/*Identifier 'C' has not been defined*/ unionFields = collect_Iter(&(map_Iter(&(map_Iter(&(iter_List(&(_this->members))), ??? access ???)), ??? access ???)), new_Joiner());
	return generateTemplateString(typeParameters_CUnion(&((*_this)))) + "union " + _this->name + "Data {" + unionFields + lineSeparator_/*Undefined identifier: System*/(&(System)) + "};" + lineSeparator_/*Undefined identifier: System*/(&(System));
}
List<char*> findDependencies_CUnion(void* _ref){
	CUnion* _this = (CUnion*) _ref;
	return toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(_this->members))), ??? access ???)), ??? access ???)));
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
	JObject* _this = (JObject*) _ref;
	return _impl;
}
CFunction lambda31(void* _ref, CType implementee){
	return createConversionType_JObject(&((*_this)), implementee, environment1);
}
List<CFunction> createConversionFunctions_JObject(void* _ref, Environment environment1){
	JObject* _this = (JObject*) _ref;
	return toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CType]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=CType]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=CType], Identifier[value=CType]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CType]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CType]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=CType]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=CType], Identifier[value=CType]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CType]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='removeElement'}], typeParameters=[]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CType]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=CType]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=CType], Identifier[value=CType]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CType]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CType]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CType]]]], name='removeElement'}], typeParameters=[]]*/(&(implementees_JObject(&((*_this)))))), lambda31)));
}
CFunction createConversionType_JObject(void* _ref, CType implementee, Environment environment){
	JObject* _this = (JObject*) _ref;
	char* joinedTypeParameters = joinTypeParameters(_this->typeParameters);
	// TODO: turn this into proper AST generation
	char* implementeeName = toBaseName_CType(&(implementee));
	int thisType = _this->name + joinedTypeParameters;
	char* thisPtr = generateStatement(thisType + "* _this = (" + thisType + "*) _ref");
	JObjectType jObjectType = orElse_Option(&(resolveType_Environment(&(environment), implementeeName)), null);
	/*assert jObjectType !*/ = null;
	char* content;
	/*start
			 * HeadTable<int> table = HeadTable<int>{
			 * next_RangeHead,
			 * };
			 * void *data = moveToHeap(*_this);
			 * return Head<int>{ data, table };
			 end
			if (jObjectType.variants.isEmpty())
				content = generateStatement("return _impl")*/;
	else {
		char* s1 = generateStatement(implementeeName + "Data" + joinedTypeParameters + " data");
		char* s2 = generateStatement("data." + _this->name + " = *_this");
		char* s3 = generateStatement("return { " + implementeeName + "Tag::" + _this->name + "Variant, data }");
		content = s1 + s2 + s3;
	}
	int conversionFunctionName = "to" + implementeeName + "_" + _this->name;
	/*final var parameters = Lists.<CDeclaration>empty()
					.addLast(new CDeclaration(new CPointerType(CPrimitiveType.Void), "_ref"))*/;
	CFunctionHeader header = new_CFunctionHeader(new_CDeclaration(_this->typeParameters, implementee, conversionFunctionName), parameters);
	return new_CFunction(header, thisPtr + content);
}
JObjectType toType_JObject(void* _ref){
	JObject* _this = (JObject*) _ref;
	List memberDefinitions = addAllLast_List(&(toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(_this->children))), ??? access ???)), ??? access ???)))), _this->recordFields);
	return new_JObjectType(_this->name, _this->variants, memberDefinitions, _this->typeParameters);
}
Option<JDeclaration> extractDefinition_JObject(void* _ref, JObjectMember child){
	JObject* _this = (JObject*) _ref;
	return /*TODO: switch*/;
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
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=CDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=CDeclaration], Identifier[value=CDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=CDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=CDeclaration], Identifier[value=CDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=CDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=CDeclaration], Identifier[value=CDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=CDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=CDeclaration], Identifier[value=CDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=CDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=CDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]]], name='removeElement'}], typeParameters=[]]*/(&(parameters_CFunctionHeader(&((*_this)))))), ??? access ???)), new_Joiner(", "));
	return generate_/*Not a functional type: JObjectType[name=CDefinable, variants=[CDeclaration, CFunctionDeclaration, Placeholder], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='generate'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='extractIdentifiers'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]], JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]]]], returnType=Identifier[value=CDefinable]], name='mapTypeParameters'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=Identifier[value=CDefinable]], name='mapName'}], typeParameters=[]]*/(&(definition_CFunctionHeader(&((*_this))))) + "(" + compiledParameters + ")";
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
	return generate_/*Not a functional type: JObjectType[name=CFunctionHeader, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='generate'}, JDeclaration {type=Identifier[value=CDefinable], name='definition'}, JDeclaration {type=JGenericType[base=List, typeArguments=[Identifier[value=CDeclaration]]], name='parameters'}], typeParameters=[]]*/(&(header_CFunction(&((*_this))))) + "{" + content_CFunction(&((*_this))) + lineSeparator_/*Undefined identifier: System*/(&(System)) + "}" + lineSeparator_/*Undefined identifier: System*/(&(System));
}
CFunction new_CFunction(CFunctionHeader header, char* content){
	CFunction _thisInstance;
	CFunction* _this = &_thisInstance;
	_this->header = header;
	_this->content = content;
	return _thisInstance;
}
JObjectMember toJObjectMember_JMethod(void* _ref){
	JMethod* _this = (JMethod*) _ref;
	return _impl;
}
Option<JDeclaration> toDeclaration_JMethod(void* _ref){
	JMethod* _this = (JMethod*) _ref;
	if (??? instanceof ???) {
		/*Cannot access member 'type' in 'Placeholder[input=Undefined identifier: declaration]', not an object.*/ returnType = declaration.type;
		List paramTypes = toList_Iter(&(map_Iter(&(iter_List(&(_this->parameters))), ??? access ???)));
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
	JField* _this = (JField*) _ref;
	return _impl;
}
JField new_JField(JDeclaration declaration){
	JField _thisInstance;
	JField* _this = &_thisInstance;
	_this->declaration = declaration;
	return _thisInstance;
}
JExpression toJExpression_JNumber(void* _ref){
	JNumber* _this = (JNumber*) _ref;
	return _impl;
}
JNumber new_JNumber(char* value){
	JNumber _thisInstance;
	JNumber* _this = &_thisInstance;
	_this->value = value;
	;
	return _thisInstance;
}
JNumber new_JNumber(char* value){
	JNumber _thisInstance;
	JNumber* _this = &_thisInstance;
	_this->value = value;
	return _thisInstance;
}
CExpression toCExpression_CNumber(void* _ref){
	CNumber* _this = (CNumber*) _ref;
	return _impl;
}
CNumber new_CNumber(char* value){
	CNumber _thisInstance;
	CNumber* _this = &_thisInstance;
	_this->value = value;
	;
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
	JNot* _this = (JNot*) _ref;
	return _impl;
}
JNot new_JNot(CExpression instance){
	JNot _thisInstance;
	JNot* _this = &_thisInstance;
	_this->instance = instance;
	return _thisInstance;
}
CExpression toCExpression_CNot(void* _ref){
	CNot* _this = (CNot*) _ref;
	return _impl;
}
char* generate_CNot(void* _ref){
	CNot* _this = (CNot*) _ref;
	return "!" + generate_CExpression(&(_this->instance));
}
CNot new_CNot(CExpression instance){
	CNot _thisInstance;
	CNot* _this = &_thisInstance;
	_this->instance = instance;
	return _thisInstance;
}
template <typename K, typename V>
Map<K, V> toMap_TupleMap(void* _ref){
	TupleMap<K, V>* _this = (TupleMap<K, V>*) _ref;
	return _impl;
}
template <typename K, typename V>
TupleMap<K, V> new_TupleMap(){
	TupleMap _thisInstance;
	TupleMap* _this = &_thisInstance;
	(*_this)(empty_Lists(&(Lists)));
	return _thisInstance;
}
template <typename K, typename V>
Map<K, V> put_TupleMap(void* _ref, K key, V value){
	TupleMap<K, V>* _this = (TupleMap<K, V>*) _ref;
	TupleMap<K, V> removed;
	if (containsKey_TupleMap(&((*_this)), key)) 
		removed = removeKey_TupleMap(&((*_this)), key);
	/*else
				removed */ = (*_this);
	return new_TupleMap(addLast_/*Cannot access member 'entries' in 'Placeholder[input=Undefined identifier: removed]', not an object.*/(&(removed.entries), new_Tuple(key, value)));
}
/*Not a functional type: Placeholder[input=Cannot access member 'equals' in 'Placeholder[input=Cannot access member 'left' in 'Boolean', not an object.]', not an object.]*/ lambda32(void* _ref, Tuple<K, V> entry){
	return equals_/*Cannot access member 'left' in 'Boolean', not an object.*/(&(!entry.left), key);
}
template <typename K, typename V>
TupleMap<K, V> removeKey_TupleMap(void* _ref, K key){
	TupleMap<K, V>* _this = (TupleMap<K, V>*) _ref;
	return new_TupleMap(toList_Iter(&(filter_Iter(&(iter_List(&(_this->entries))), lambda32))));
}
/*Not a functional type: Placeholder[input=Cannot access member 'equals' in 'Placeholder[input=Identifier 'K' has not been defined]', not an object.]*/ lambda33(void* _ref, Tuple<K, V> entry){
	return equals_/*Identifier 'K' has not been defined*/(&(entry.left), key);
}
/*Identifier 'V' has not been defined*/ lambda34(void* _ref, Tuple<K, V> entry){
	return entry.right;
}
template <typename K, typename V>
Option<V> get_TupleMap(void* _ref, K key){
	TupleMap<K, V>* _this = (TupleMap<K, V>*) _ref;
	return next_Iter(&(map_Iter(&(filter_Iter(&(iter_List(&(_this->entries))), lambda33)), lambda34)));
}
/*Not a functional type: Placeholder[input=Cannot access member 'equals' in 'Placeholder[input=Identifier 'K' has not been defined]', not an object.]*/ lambda35(void* _ref, Tuple<K, V> entry){
	return equals_/*Identifier 'K' has not been defined*/(&(entry.left), key);
}
template <typename K, typename V>
int containsKey_TupleMap(void* _ref, K key){
	TupleMap<K, V>* _this = (TupleMap<K, V>*) _ref;
	return collect_Iter(&(iter_List(&(_this->entries))), new_AnyMatch(lambda35));
}
template <typename K, typename V>
Iter<Tuple<K, V>> iterEntries_TupleMap(void* _ref){
	TupleMap<K, V>* _this = (TupleMap<K, V>*) _ref;
	return iter_List(&(_this->entries));
}
template <typename K, typename V>
int isEmpty_TupleMap(void* _ref){
	TupleMap<K, V>* _this = (TupleMap<K, V>*) _ref;
	return isEmpty_List(&(_this->entries));
}
template <typename K, typename V>
int size_TupleMap(void* _ref){
	TupleMap<K, V>* _this = (TupleMap<K, V>*) _ref;
	return size_List(&(_this->entries));
}
Tuple lambda36(void* _ref, Tuple<K, V> entry){
	return new_Tuple(entry.left, apply_F1R(&(mapper), entry.right));
}
template <typename K, typename V>
Map<K, V> mapValues_TupleMap(void* _ref, F1R<V, V> mapper){
	TupleMap<K, V>* _this = (TupleMap<K, V>*) _ref;
	List collected = toList_Iter(&(map_Iter(&(iter_List(&(_this->entries))), lambda36)));
	return new_TupleMap(collected);
}
template <typename K, typename V>
TupleMap<K, V> new_TupleMap(List<Tuple<K, V>> entries){
	TupleMap<K, V> _thisInstance;
	TupleMap<K, V>* _this = &_thisInstance;
	_this->entries = entries;
	return _thisInstance;
}
template <typename K, typename V>
Collector<Tuple<K, V>, Map<K, V>> toCollector_MapCollector(void* _ref){
	MapCollector<K, V>* _this = (MapCollector<K, V>*) _ref;
	return _impl;
}
template <typename K, typename V>
Map<K, V> createInitial_MapCollector(void* _ref){
	MapCollector<K, V>* _this = (MapCollector<K, V>*) _ref;
	return new_TupleMap();
}
template <typename K, typename V>
Map<K, V> fold_MapCollector(void* _ref, Map<K, V> kvMap, Tuple<K, V> kvTuple){
	MapCollector<K, V>* _this = (MapCollector<K, V>*) _ref;
	return put_Map(&(kvMap), kvTuple.left, kvTuple.right);
}
template <typename K, typename V>
MapCollector<K, V> new_MapCollector(){
	MapCollector<K, V> _thisInstance;
	MapCollector<K, V>* _this = &_thisInstance;
	return _thisInstance;
}
CExpression toCExpression_CReference(void* _ref){
	CReference* _this = (CReference*) _ref;
	return _impl;
}
char* generate_CReference(void* _ref){
	CReference* _this = (CReference*) _ref;
	return "&" + generate_CExpression(&(_this->instance));
}
CReference new_CReference(CExpression instance){
	CReference _thisInstance;
	CReference* _this = &_thisInstance;
	_this->instance = instance;
	return _thisInstance;
}
JExpression toJExpression_Char(void* _ref){
	Char* _this = (Char*) _ref;
	return _impl;
}
CExpression toCExpression_Char(void* _ref){
	Char* _this = (Char*) _ref;
	return _impl;
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
	JOperator* _this = (JOperator*) _ref;
	return _impl;
}
JOperator new_JOperator(JExpression left, Operator op, JExpression rightCompiled){
	JOperator _thisInstance;
	JOperator* _this = &_thisInstance;
	_this->left = left;
	_this->op = op;
	_this->rightCompiled = rightCompiled;
	return _thisInstance;
}
CExpression toCExpression_COperator(void* _ref){
	COperator* _this = (COperator*) _ref;
	return _impl;
}
char* generate_COperator(void* _ref){
	COperator* _this = (COperator*) _ref;
	return generate_CExpression(&(_this->left)) + " " + generate_Operator(&(_this->op)) + " " + generate_CExpression(&(_this->right));
}
COperator new_COperator(CExpression left, Operator op, CExpression right){
	COperator _thisInstance;
	COperator* _this = &_thisInstance;
	_this->left = left;
	_this->op = op;
	_this->right = right;
	return _thisInstance;
}
JExpression toJExpression_StringNode(void* _ref){
	StringNode* _this = (StringNode*) _ref;
	return _impl;
}
CExpression toCExpression_StringNode(void* _ref){
	StringNode* _this = (StringNode*) _ref;
	return _impl;
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
JExpression toJExpression_JMethodAccess(void* _ref){
	JMethodAccess* _this = (JMethodAccess*) _ref;
	return _impl;
}
JMethodAccess new_JMethodAccess(JExpression instance, char* methodName){
	JMethodAccess _thisInstance;
	JMethodAccess* _this = &_thisInstance;
	_this->instance = instance;
	_this->methodName = methodName;
	return _thisInstance;
}
JExpression toJExpression_JInstanceOf(void* _ref){
	JInstanceOf* _this = (JInstanceOf*) _ref;
	return _impl;
}
JInstanceOf new_JInstanceOf(){
	JInstanceOf _thisInstance;
	JInstanceOf* _this = &_thisInstance;
	return _thisInstance;
}
JExpression toJExpression_JQuantity(void* _ref){
	JQuantity* _this = (JQuantity*) _ref;
	return _impl;
}
JQuantity new_JQuantity(JExpression instance){
	JQuantity _thisInstance;
	JQuantity* _this = &_thisInstance;
	_this->instance = instance;
	return _thisInstance;
}
template <typename T>
Collector<T, int> toCollector_AllMatch(void* _ref){
	AllMatch<T>* _this = (AllMatch<T>*) _ref;
	return _impl;
}
template <typename T>
int createInitial_AllMatch(void* _ref){
	AllMatch<T>* _this = (AllMatch<T>*) _ref;
	return true;
}
template <typename T>
int fold_AllMatch(void* _ref, int aBoolean, T t){
	AllMatch<T>* _this = (AllMatch<T>*) _ref;
	return aBoolean && apply_F1R(&(_this->predicate), t);
}
template <typename T>
AllMatch<T> new_AllMatch(F1R<T, int> predicate){
	AllMatch<T> _thisInstance;
	AllMatch<T>* _this = &_thisInstance;
	_this->predicate = predicate;
	return _thisInstance;
}
CompiledUnit new_CompiledUnit(char* header, char* source){
	CompiledUnit _thisInstance;
	CompiledUnit* _this = &_thisInstance;
	_this->header = header;
	_this->source = source;
	return _thisInstance;
}
new Environment_Main(void* _ref){
	Main* _this = (Main*) _ref;
	return _this->table.Environment(_this->data);
}
todoLambda lambda37(void* _ref, JType StringType){
	return /*{
			// We don't need parameter types for now*/;
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
	_this->globalDeclarations = empty_Lists(&(Lists));
	_this->imports = empty_Lists(&(Lists));
	_this->counter = 0;
	_this->StringType = create_JRecursiveType(&(JRecursiveType), lambda37);
	return _thisInstance;
}
int isLetter_Main(void* _ref, char c){
	Main* _this = (Main*) _ref;
	return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
}
int lambda38(void* _ref, char* typeParam){
	return "typename " + typeParam;
}
char* generateTemplateString_Main(void* _ref, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	if (isEmpty_List(&(typeParameters))) 
		return "";
	/*Identifier 'C' has not been defined*/ typeNames = collect_Iter(&(map_Iter(&(iter_List(&(typeParameters))), lambda38)), new_Joiner(", "));
	return "template <" + typeNames + ">" + lineSeparator_/*Undefined identifier: System*/(&(System));
}
void main_Main(void* _ref, char** args){
	Main* _this = (Main*) _ref;
	if (??? instanceof ???) 
		println_/*Cannot access member 'err' in 'Placeholder[input=Undefined identifier: System]', not an object.*/(&(System.err), display_/*Undefined identifier: value*/(&(value)));
}
char* generateStatement_Main(void* _ref, int depth, char* content){
	Main* _this = (Main*) _ref;
	return generateIndent(depth) + content + ";";
}
char* generateIndent_Main(void* _ref, int depth){
	Main* _this = (Main*) _ref;
	return lineSeparator_/*Undefined identifier: System*/(&(System)) + repeat_String(&("\t"), depth);
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
	/*else
			joinedTypeParameters */ = "<" + collect_Iter(&(iter_List(&(typeParameters))), new_Joiner(", ")) + ">";
	return joinedTypeParameters;
}
Option<JObjectType> extractType_Main(void* _ref, JObjectMember jObjectMember){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
int isDigit_Main(void* _ref, char c){
	Main* _this = (Main*) _ref;
	return c >= '0' && c <= '9';
}
CDeclaration transformDeclaration_Main(void* _ref, JDeclaration declaration){
	Main* _this = (Main*) _ref;
	return new_CDeclaration(declaration.typeParameters, transformType_Main(&((*_this)), declaration.type), declaration.name);
}
CType transformType_Main(void* _ref, JType jType){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
Tuple<CExpression, List<CType>> transformCaller_Main(void* _ref, JCaller jCaller){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
Tuple<CExpression, List<CType>> convertTypeToConstructionIdentifier_Main(void* _ref, JType type){
	Main* _this = (Main*) _ref;
	CType cType = transformType_Main(&((*_this)), type);
	if (??? instanceof ???) 
		return new_Tuple(new_Identifier("new_" + value), empty_Lists(&(Lists)));
	if (??? instanceof ???) 
		return new_Tuple(new_Identifier("new_" + base), typeArguments);
	return new_Tuple(new_Identifier("new_" + generate_CType(&(cType))), empty_Lists(&(Lists)));
}
CExpression transformExpression_Main(void* _ref, JExpression expression){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
CExpression transformInvocation_Main(void* _ref, JInvokable jInvokable){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JExpression]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JExpression], Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]*/ arguments = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JExpression]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JExpression], Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JExpression]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JExpression], Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeElement'}], typeParameters=[]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JExpression]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JExpression], Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeElement'}], typeParameters=[]]*/(&(arguments_JInvokable(&(jInvokable))))), ??? access ???)));
	/*Not a functional type: JObjectType[name=JCaller, variants=[], members=[], typeParameters=[]]*/ caller = caller_JInvokable(&(jInvokable));
	if (??? instanceof ???) {
		JType jType = resolveExpression_Main(&((*_this)), instance);
		char* baseName = stringify_JType(&(jType));
		Tuple tuple = transformCaller_Main(&((*_this)), instance);
		CExpression left = tuple.left;
		CExpression element;
		if (??? instanceof ???) 
			element = expression;
		/*else
				element */ = new_CReference(new_CQuantity(left));
		if (??? instanceof ???) 
			element = expression;
		/*Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JExpression]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JExpression], Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ newArguments = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JExpression]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JExpression], Identifier[value=JExpression]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JExpression]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JExpression]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JExpression]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]*/(&(arguments), element);
		return new_CInvocation(new_Identifier(memberName + "_" + baseName), newArguments);
	}
	Tuple tuple = transformCaller_Main(&((*_this)), caller);
	return new_CInvocation(tuple.left, arguments);
}
int lambda39(void* _ref, Option<IOError> option){
	return ??? instanceof ???;
}
Option<IOError> run_Main(void* _ref, char** args){
	Main* _this = (Main*) _ref;
	List<char*> files;
	if (args.length == 0) {
		/*files = Lists.<String>empty().addLast("src/main/java/magma/Main.java")*/;
	}
	else {
		files = toList_Iter(&(fromObjArray_Streams(&(Streams), args)));
	}
	return orElse_/*Not a functional type: Placeholder[input=Cannot access member 'next' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: files]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(next_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: files]', not an object.]]', not an object.]]', not an object.]*/(&(filter_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: files]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: files]', not an object.]*/(&(iter_/*Undefined identifier: files*/(&(files))), ??? access ???)), lambda39)))), new_None());
}
Option<IOError> compileFile_Main(void* _ref, char* file){
	Main* _this = (Main*) _ref;
	Path source = get_Paths(&(Paths), file);
	Result input = readString_Path(&(source));
	return /*TODO: switch*/;
}
CompiledUnit compile_Main(void* _ref, char* name, char* input){
	Main* _this = (Main*) _ref;
	char* all = compileStatements_Main(&((*_this)), input, ??? access ???);
	char* joinedStructureForwardDeclarations = joinStrings_Main(&((*_this)), _this->structureForwardDeclarations);
	/*Identifier 'C' has not been defined*/ joinedStructures = collect_Iter(&(map_Iter(&(iter_List(&(createTopologicallySortedList_Main(&((*_this)))))), ??? access ???)), new_Joiner());
	char* joinedGlobals = joinStrings_Main(&((*_this)), _this->globals);
	char* joinedGlobalDeclarations = joinStrings_Main(&((*_this)), _this->globalDeclarations);
	char* joinedFunctionDeclarations = joinStrings_Main(&((*_this)), _this->functionDeclarations);
	/*Identifier 'C' has not been defined*/ joinedFunctions = collect_Iter(&(map_Iter(&(iter_List(&(_this->functions))), ??? access ???)), new_Joiner());
	/*Identifier 'C' has not been defined*/ joinedEnums = collect_Iter(&(map_Iter(&(iter_List(&(_this->enums))), ??? access ???)), new_Joiner());
	char* joinedImports = joinStrings_Main(&((*_this)), _this->imports);
	int header = "#pragma once" + lineSeparator_/*Undefined identifier: System*/(&(System)) + "#include \"intrinsics.h\"" + lineSeparator_/*Undefined identifier: System*/(&(System)) + joinedImports + joinedStructureForwardDeclarations + joinedEnums + joinedStructures + joinedFunctionDeclarations + joinedGlobalDeclarations;
	int source = "#include \"" + name + ".hpp\"" + lineSeparator_/*Undefined identifier: System*/(&(System)) + joinedGlobals + joinedFunctions + all;
	return new_CompiledUnit(header, source);
}
todoLambda lambda40(void* _ref, CStructureOrUnion value){
	List withoutDuplicates = removeDuplicates_Main(&((*_this)), findDependencies_CStructureOrUnion(&(value)));
	return new_Tuple(findName_CStructureOrUnion(&(value)), withoutDuplicates);
}
todoLambda lambda41(void* _ref, Tuple<char*, List<char*>> entry){
	char* oldKey = entry.left;
	List newValues = trimDependencies_Main(&((*_this)), entry, dependencyMap, oldKey);
	return new_Tuple(oldKey, newValues);
}
int lambda42(void* _ref, Tuple<char*, List<char*>> entry){
	return isEmpty_List(&(entry.right));
}
Tuple lambda43(void* _ref, CStructureOrUnion rootSegment){
	return new_Tuple(findName_CStructureOrUnion(&(rootSegment)), rootSegment);
}
List<CStructureOrUnion> createTopologicallySortedList_Main(void* _ref){
	Main* _this = (Main*) _ref;
	/*Identifier 'C' has not been defined*/ dependencyMap = collect_Iter(&(map_Iter(&(iter_List(&(_this->structuresOrUnions))), lambda40)), new_MapCollector());
	/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]*/ cleanedDependencyMap = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]*/(&(iterEntries_/*Identifier 'C' has not been defined*/(&(dependencyMap))), lambda41)), new_MapCollector());
	/*var dependencyOrder = Lists.<String>empty()*/;
	while (isEmpty_bool(&(!cleanedDependencyMap))) {
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ cleanedDependencyMapKeysToRemove = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(filter_/*Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(iterEntries_/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]*/(&(cleanedDependencyMap))), lambda42)), ??? access ???)));
		/*Not a functional type: Placeholder[input=Cannot access member 'size' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ oldLength = size_/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]*/(&(cleanedDependencyMap));
		/*Not a functional type: Placeholder[input=Cannot access member 'fold' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ fold = fold_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(cleanedDependencyMapKeysToRemove))), new_Tuple(dependencyOrder, cleanedDependencyMap), ??? access ???);
		dependencyOrder = fold.left;
		cleanedDependencyMap = fold.right;
		/*Not a functional type: Placeholder[input=Cannot access member 'size' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ newLength = size_/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iterEntries' in 'Placeholder[input=Identifier 'C' has not been defined]', not an object.]]', not an object.]]', not an object.]*/(&(cleanedDependencyMap));
		/*assert oldLength !*/ = newLength;
	}
	/*Identifier 'C' has not been defined*/ mapping = collect_Iter(&(map_Iter(&(iter_List(&(_this->structuresOrUnions))), lambda43)), new_MapCollector());
	return toList_/*Not a functional type: Placeholder[input=Cannot access member 'flatMap' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: dependencyOrder]', not an object.]]', not an object.]]', not an object.]*/(&(flatMap_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: dependencyOrder]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: dependencyOrder]', not an object.]*/(&(iter_/*Undefined identifier: dependencyOrder*/(&(dependencyOrder))), ??? access ???)), ??? access ???)));
}
List lambda44(void* _ref, List<char*> values){
	return removeElement_List(&(values), cleanedDependencyMapKeyToRemove);
}
Tuple<List<char*>, Map<char*, List<char*>>> getListMapTuple_Main(void* _ref, Tuple<List<char*>, Map<char*, List<char*>>> listMapTuple, char* cleanedDependencyMapKeyToRemove){
	Main* _this = (Main*) _ref;
	List left = listMapTuple.left;
	Map right = listMapTuple.right;
	List stringList = addLast_List(&(left), cleanedDependencyMapKeyToRemove);
	Map stringListMap = mapValues_Map(&(removeKey_Map(&(right), cleanedDependencyMapKeyToRemove)), lambda44);
	return new_Tuple(stringList, stringListMap);
}
List<char*> trimDependencies_Main(void* _ref, Tuple<char*, List<char*>> entry, Map<char*, List<char*>> dependencyMap, char* oldKey){
	Main* _this = (Main*) _ref;
	List newValues = toList_Iter(&(filter_Iter(&(iter_List(&(entry.right))), ??? access ???)));
	/*start
		 * This is to prevent circular dependencies with VTables.
		 * 
		 * Technically List -> ListTable -> List, but ListTable -> List is invalid
		 * ListTable does not have any fields, only dynamic members,
		 * and we can safely assume that any mention of List in ListTable is a pointer
		 * or in a function pointer.
		 * Therefore, we remove it.
		 end
		if (oldKey.endsWith("Table")) {
			final var keyWithoutTable = oldKey.substring(0, oldKey.length() - "Table".length());
			if (newValues.contains(keyWithoutTable))
				newValues = newValues.removeElement(keyWithoutTable);
		}*/
	return newValues;
}
todoLambda lambda45(void* _ref, List<char*> copy, char* element){
	if (contains_List(&(copy), element)) 
		return copy;
	return addLast_List(&(copy), element);
}
List<char*> removeDuplicates_Main(void* _ref, List<char*> list){
	Main* _this = (Main*) _ref;
	return fold_Iter(&(iter_List(&(list))), empty_Lists(&(Lists)), lambda45);
}
char* joinStrings_Main(void* _ref, List<char*> structures){
	Main* _this = (Main*) _ref;
	return collect_Iter(&(iter_List(&(structures))), new_Joiner(""));
}
char* compileStatements_Main(void* _ref, char* input, F1R<char*, char*> mapper){
	Main* _this = (Main*) _ref;
	return compileAll_Main(&((*_this)), input, mapper, new_EscapedFolder(??? access ???));
}
char* compileAll_Main(void* _ref, char* input, F1R<char*, char*> mapper, Folder folder){
	Main* _this = (Main*) _ref;
	return collect_Iter(&(map_Iter(&(divide_Main(&((*_this)), input, folder)), mapper)), new_Joiner(""));
}
Iter<char*> divide_Main(void* _ref, char* input, Folder folder){
	Main* _this = (Main*) _ref;
	State current = new_State(input);
	while (true) {
		Option maybeNext = pop_State(&(current));
		if (!(??? instanceof ???)) 
			break;
		char next;
		next = value;
		current = apply_Folder(&(folder), current, next);
	}
	return stream_State(&(advance_State(&(current))));
}
State foldStatement_Main(void* _ref, State current, char next){
	Main* _this = (Main*) _ref;
	if (next == '/' && isLevel_State(&(current))) {
		Option maybePeeked = peek_State(&(current));
		if (??? instanceof ???) {
			State withoutLineCommentPrefix = orElse_Option(&(popAndAppendToOption_State(&(append_State(&(current), '/')))), current);
			while (true) {
				Option maybeTuple = popAndAppendToTuple_State(&(withoutLineCommentPrefix));
				if (??? instanceof ???) {
					withoutLineCommentPrefix = tuple.left;
					/*Cannot access member 'right' in 'Placeholder[input=Undefined identifier: tuple]', not an object.*/ right = tuple.right;
					if (right == '\n') 
						return advance_State(&(withoutLineCommentPrefix));
				}
				/*else
						return*/ withoutLineCommentPrefix;
			}
		}
	}
	State appended = append_State(&(current), next);
	if (next == ';' && isLevel_State(&(appended))) 
		return advance_State(&(appended));
	if (next == '}' && isShallow_State(&(appended))) {
		State appended1;
		if (??? instanceof ???) 
			appended1 = orElse_Option(&(popAndAppendToOption_State(&(appended))), appended);
		/*else
				appended1 */ = appended;
		return exit_/*Not a functional type: Placeholder[input=Cannot access member 'advance' in 'Placeholder[input=Undefined identifier: appended1]', not an object.]*/(&(advance_/*Undefined identifier: appended1*/(&(appended1))));
	}
	if (next == '{' || next == '(') 
		return enter_State(&(appended));
	if (next == '}' || next == ')') 
		return exit_State(&(appended));
	return appended;
}
char* lambda46(void* _ref){
	return wrap_Placeholder(&(Placeholder), stripped);
}
char* compileRootSegment_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (isEmpty_String(&(stripped))) 
		return "";
	if (startsWith_String(&(stripped), "package ")) 
		return "";
	if (startsWith_String(&(stripped), "import ")) {
		char* imported = strip_String(&(substring_String(&(stripped), length_String(&("import ")), length_String(&(stripped)) - 1)));
		if (startsWith_bool(&(!imported), "java.")) {
			List parts = splitValues_Main(&((*_this)), replace_String(&(imported), ".", ","));
			char* name = orElse_Option(&(next_Iter(&(iterReversed_List(&(parts))))), imported);
			_this->imports = addLast_List(&(_this->imports), "#include \"" + name + ".hpp\"" + lineSeparator_/*Undefined identifier: System*/(&(System)));
		}
		return "";
	}
	return orElseGet_Option(&(map_Option(&(flatMap_Option(&(parseObject_Main(&((*_this)), "class", stripped)), ??? access ???)), ??? access ???)), lambda46);
}
/*Not a functional type: Placeholder[input=Cannot access member 'isEmpty' in 'Boolean', not an object.]*/ lambda47(void* _ref, char* slice){
	return isEmpty_bool(&(!slice));
}
CType lambda48(void* _ref, char* input){
	return transformType_Main(&((*_this)), parseTypeOrPlaceholder_Main(&((*_this)), input));
}
/*Not a functional type: Placeholder[input=Cannot access member 'isEmpty' in 'Boolean', not an object.]*/ lambda49(void* _ref, char* slice){
	return isEmpty_bool(&(!slice));
}
Option lambda50(void* _ref, char* slice){
	return parseObjectMember_Main(&((*_this)), slice, name, finalTypeParameters);
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
	/*else
			modifiers */ = beforeType;
	char* afterKeyword = strip_String(&(substring_String(&(stripped), i + length_int(&((type + " "))))));
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
		char* substring1 = substring_String(&(beforeContent), i2 + length_String(&("permits ")));
		beforeContent = substring_String(&(beforeContent), 0, i2);
		variants = splitValues_Main(&((*_this)), substring1);
	}
	// TODO: generate conversion methods
	List<CType> extensions = empty_Lists(&(Lists));
	int extendsIndex = indexOf_String(&(beforeContent), "extends ");
	// final var extensionsString = beforeContent.substring(extendsIndex + "extends
	/*// ".length())*/;
	if (extendsIndex >= 0) 
		beforeContent = strip_String(&(substring_String(&(beforeContent), 0, extendsIndex)));
	List<CType> implementees = empty_Lists(&(Lists));
	int i4 = indexOf_String(&(beforeContent), "implements ");
	if (i4 >= 0) {
		char* implementeesString = substring_String(&(beforeContent), i4 + length_String(&("implements ")));
		beforeContent = strip_String(&(substring_String(&(beforeContent), 0, i4)));
		implementees = toList_Iter(&(map_Iter(&(filter_Iter(&(map_Iter(&(divide_Main(&((*_this)), implementeesString, new_ValueFolder())), ??? access ???)), lambda47)), lambda48)));
	}
	List<JDeclaration> recordFields = empty_Lists(&(Lists));
	if (endsWith_String(&(beforeContent), ")")) {
		char* substring = substring_String(&(beforeContent), 0, length_String(&(beforeContent)) - 1);
		int i3 = indexOf_String(&(substring), "(");
		if (i3 >= 0) {
			beforeContent = substring_String(&(substring), 0, i3);
			recordFields = toList_Iter(&(flatMap_Iter(&(map_Iter(&(divide_Main(&((*_this)), substring_String(&(substring), i3 + 1), new_ValueFolder())), ??? access ???)), ??? access ???)));
		}
	}
	List<char*> typeParameters = empty_Lists(&(Lists));
	int i3 = indexOf_String(&(beforeContent), "<");
	if (i3 >= 0) {
		char* substring1 = strip_String(&(substring_String(&(beforeContent), i3 + 1)));
		if (endsWith_String(&(substring1), ">")) {
			beforeContent = substring_String(&(beforeContent), 0, i3);
			char* substring = substring_String(&(substring1), 0, length_String(&(substring1)) - 1);
			typeParameters = splitValues_Main(&((*_this)), substring);
		}
	}
	if (isIdentifier_bool(&(!Identifier), beforeContent)) 
		return new_None();
	List modifiersList = toList_Iter(&(filter_Iter(&(map_Iter(&(fromObjArray_Streams(&(Streams), split_/*Undefined identifier: modifiers*/(&(modifiers), quote_/*Undefined identifier: Pattern*/(&(Pattern), " ")))), ??? access ???)), lambda49)));
	char* name = strip_String(&(beforeContent));
	List finalTypeParameters = typeParameters;
	List divisions = toList_Iter(&(divide_Main(&((*_this)), inputContent, new_EscapedFolder(??? access ???))));
	List children = toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(divisions))), lambda50)), ??? access ???)));
	JObject prototype = new_JObject(type, annotations, modifiersList, name, typeParameters, recordFields, implementees, variants, children);
	return new_Some(prototype);
}
Option lambda51(void* _ref, JObjectMember wrapper){
	return transformObjectMemberPrototype_Main(&((*_this)), object, wrapper);
}
todoLambda lambda52(void* _ref, Environment env){
	environment = withObject_Environment(&(env), object);
	List types = toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(object.children))), ??? access ???)), ??? access ???)));
	List declarations = addAllLast_List(&(toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(object.children))), ??? access ???)), ??? access ???)))), object.recordFields);
	environment = defineAllTypes_/*Undefined identifier: environment*/(&(environment), types);
	environment = defineAllExpressions_/*Undefined identifier: environment*/(&(environment), declarations);
	List members = toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(object.children))), lambda51)), ??? access ???)));
	return new_Tuple(environment, members);
}
int lambda53(void* _ref, CDeclaration field){
	return "_this->" + field.name + " = " + field.name;
}
Option<CStructMember> transformObject_Main(void* _ref, JObject object){
	Main* _this = (Main*) _ref;
	if (contains_List(&(object.annotations), "Actual")) 
		return new_Some(new_EmptyStructMember());
	_this->functions = fold_Iter(&(iter_List(&(createConversionFunctions_JObject(&(object), environment)))), _this->functions, ??? access ???);
	/*Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Undefined identifier: environment]', not an object.]*/ within = within_/*Undefined identifier: environment*/(&(environment), lambda52);
	environment = within.left;
	/*Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Undefined identifier: environment]', not an object.]]', not an object.*/ members = within.right;
	/*var fields = object.recordFields
				.iter()
				.map(this::transformDeclaration)
				.<CDefinable>map((CDeclaration value) -> value)
				.toList()*/;
	if (equals_/*Not a functional type: magma.Main$JRecursiveType@58c2f281*/(&(type_JObject(&(object))), "interface")) 
		if (contains_/*Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]*/(&(modifiersList_JObject(&(object))), "sealed")) 
			fields = handleSealedInterface_Main(&((*_this)), object, fields);
	/*else
				fields */ = handleUnsealedInterface_Main(&((*_this)), object, members, fields);
	else {
		List retained = retainFields_Main(&((*_this)), members);
		fields = addAllLast_/*Undefined identifier: fields*/(&(fields), retained);
	}
	if (equals_String(&(object.type), "record")) {
		List recordFields = toList_Iter(&(map_Iter(&(iter_List(&(object.recordFields))), ??? access ???)));
		CNamedType structureType = createStructureType_Main(&((*_this)), object.name, object.typeParameters);
		CDeclaration definition = new_CDeclaration(object.typeParameters, structureType, "new_" + getName_CNamedType(&(structureType)));
		/*Identifier 'C' has not been defined*/ joinedAssignments = collect_Iter(&(map_Iter(&(map_Iter(&(iter_List(&(recordFields))), lambda53)), ??? access ???)), new_Joiner());
		int content = generateStatement(generate_CNamedType(&(structureType)) + " _thisInstance") + generateStatement(generate_CNamedType(&(structureType)) + "* _this = &_thisInstance") + joinedAssignments + generateStatement("return _thisInstance");
		_this->functions = addLast_List(&(_this->functions), new_CFunction(new_CFunctionHeader(definition, recordFields), content));
	}
	_this->structuresOrUnions = addLast_List(&(_this->structuresOrUnions), new_CStructure(typeParameters_JObject(&(object)), object.name, fields));
	_this->structureForwardDeclarations = addLast_List(&(_this->structureForwardDeclarations), generateTemplateString(object.typeParameters) + "struct " + object.name + ";" + lineSeparator_/*Undefined identifier: System*/(&(System)));
	return new_Some(new_EmptyStructMember());
}
List<CDefinable> handleUnsealedInterface_Main(void* _ref, JObject object, List<CStructMember> members, List<CDefinable> fields){
	Main* _this = (Main*) _ref;
	List list = toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(members))), ??? access ???)), ??? access ???)));
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
	/*else
			return new CTemplateType(name, typeArguments.iter().<CType>map(Identifier::new).toList())*/;
}
CDefinable lambda54(void* _ref, char* variant){
	return createUnionField_Main(&((*_this)), variant, typeArguments);
}
List<CDefinable> handleSealedInterface_Main(void* _ref, JObject object, List<CDefinable> fields){
	Main* _this = (Main*) _ref;
	char* name = object.name;
	/*Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]*/ typeParameters = typeParameters_JObject(&(object));
	/*Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]*/ variants = variants_JObject(&(object));
	CEnum jEnum = new_CEnum(name + "Tag", variants);
	/*final var typeArguments = typeParameters.iter().<CType>map(Identifier::new).toList()*/;
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]*/ unionMembers = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=magma.Main$JRecursiveType@58c2f281], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[magma.Main$JRecursiveType@58c2f281, magma.Main$JRecursiveType@58c2f281]]], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[magma.Main$JRecursiveType@58c2f281], returnType=JGenericType[base=List, typeArguments=[magma.Main$JRecursiveType@58c2f281]]], name='removeElement'}], typeParameters=[]]*/(&(variants))), lambda54)));
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
int lambda55(void* _ref, char* name){
	return name + "_" + object.name;
}
Tuple lambda56(void* _ref, Environment value){
	return new_Tuple(value, compileMethodsSegments_Main(&((*_this)), inputContent, 1));
}
todoLambda lambda57(void* _ref, Environment env){
	Environment self = defineAllExpressions_Environment(&(env), parameters_JMethod(&(jFunctionProto)));
	return within_Environment(&(self), lambda56);
}
CStructMember completeMethodProto_Main(void* _ref, JMethod jFunctionProto, JObject object){
	Main* _this = (Main*) _ref;
	Option<char*> maybeCompiled = new_None();
	if (??? instanceof ???) {
		/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]*/ cParameters = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]*/(&(parameters_JMethod(&(jFunctionProto))))), ??? access ???)));
		/*Not a functional type: Placeholder[input=Cannot access member 'collect' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ compiledParameters = collect_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]*/(&(cParameters))), ??? access ???)), new_Joiner(", "));
		CDeclaration modifiedMethodDeclaration = transformDeclaration_Main(&((*_this)), mapName_/*Undefined identifier: declaration*/(&(declaration), lambda55));
		_this->functionDeclarations = addLast_List(&(_this->functionDeclarations), generate_CDeclaration(&(modifiedMethodDeclaration)) + "(" + compiledParameters + ");" + lineSeparator_/*Undefined identifier: System*/(&(System)));
		return new_EmptyStructMember();
	}
	if (startsWith_String(&(jFunctionProto.content), "{") && endsWith_String(&(jFunctionProto.content), "}")) {
		char* inputContent = substring_String(&(jFunctionProto.content), 1, length_/*Not a functional type: magma.Main$JRecursiveType@58c2f281*/(&(content_JMethod(&(jFunctionProto)))) - 1);
		/*Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Undefined identifier: environment]', not an object.]*/ within = within_/*Undefined identifier: environment*/(&(environment), lambda57);
		environment = within.left;
		maybeCompiled = new_Some(within.right);
	}
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]*/ cParameters = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]*/(&(iter_/*Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]*/(&(parameters_JMethod(&(jFunctionProto))))), ??? access ???)));
	if (??? instanceof ???) 
		cParameters = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]*/(&(cParameters), new_CDeclaration(new_CPointerType(CPrimitiveType.Void), "_ref"));
	char* outputContent = computeMethodBody_Main(&((*_this)), typeParameters_JMethod(&(jFunctionProto)), methodDeclaration_JMethod(&(jFunctionProto)), cParameters, maybeCompiled, object.name, object.variants);
	CDefinable mapped = transformMethodDeclaration_Main(&((*_this)), object.name, typeParameters_JMethod(&(jFunctionProto)), methodDeclaration_JMethod(&(jFunctionProto)));
	CFunctionHeader header = new_CFunctionHeader(mapped, cParameters);
	CFunction cFunction = new_CFunction(header, outputContent);
	_this->functionDeclarations = addLast_List(&(_this->functionDeclarations), generate_CFunctionHeader(&(header)) + ";" + lineSeparator_/*Undefined identifier: System*/(&(System)));
	_this->functions = addLast_List(&(_this->functions), cFunction);
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ parameterTypes = toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/(&(iter_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Not a functional type: JObjectType[name=List, variants=[], members=[JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iter'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Boolean], name='isEmpty'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=Boolean], name='contains'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='addAllLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Int], name='size'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=Identifier[value=JDeclaration]], name='getFirst'}, JDeclaration {type=JFunctionalType[parameterTypes=[Int, Int], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='subList'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='clear'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[JGenericType[base=F1R, typeArguments=[Identifier[value=JDeclaration], Identifier[value=JDeclaration]]]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='mapLast'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=Iter, typeArguments=[Identifier[value=JDeclaration]]]], name='iterReversed'}, JDeclaration {type=JFunctionalType[parameterTypes=[], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='copy'}, JDeclaration {type=JFunctionalType[parameterTypes=[Identifier[value=JDeclaration]], returnType=JGenericType[base=List, typeArguments=[Identifier[value=JDeclaration]]]], name='removeElement'}], typeParameters=[]]]', not an object.]]', not an object.]]', not an object.]*/(&(cParameters))), ??? access ???)));
	return /*TODO: switch*/;
}
Option<JObjectMember> parseObjectMember_Main(void* _ref, char* input, char* name, List<char*> typeParameters){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (startsWith_String(&(stripped), "//")) 
		return new_Some(new_EmptyStructMember());
	if (isEmpty_String(&(stripped))) 
		return new_None();
	Option maybeEnum = parseObject_Main(&((*_this)), "enum", input);
	if (??? instanceof ???) 
		return new_Some(enum0);
	Option maybeInterface = parseObject_Main(&((*_this)), "interface", input);
	if (??? instanceof ???) 
		return new_Some(interface0);
	Option maybeRecord = parseObject_Main(&((*_this)), "record", input);
	if (??? instanceof ???) 
		return new_Some(record0);
	Option maybeClass = parseObject_Main(&((*_this)), "class", input);
	if (??? instanceof ???) 
		return new_Some(class0);
	Option maybeEnumValues = parseEnumValuesStatement_Main(&((*_this)), input, name);
	if (??? instanceof ???) 
		return new_Some(enumValues);
	if (endsWith_String(&(stripped), ";")) {
		char* substring = substring_String(&(stripped), 0, length_String(&(stripped)) - 1);
		Option maybeDeclaration = parseDeclaration_Main(&((*_this)), substring);
		if (??? instanceof ???) {
			environment = defineExpression_/*Undefined identifier: environment*/(&(environment), declaration);
			return new_Some(new_JField(declaration));
		}
	}
	Option maybeMethod = parseMethod_Main(&((*_this)), stripped, name, typeParameters);
	if (??? instanceof ???) 
		return new_Some(temp);
	return new_Some(new_Placeholder(stripped));
}
/*Not a functional type: Placeholder[input=Cannot access member 'isEmpty' in 'Boolean', not an object.]*/ lambda58(void* _ref, char* slice){
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
	List parameters = toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(toList_Iter(&(filter_Iter(&(map_Iter(&(divide_Main(&((*_this)), parametersString, new_ValueFolder())), ??? access ???)), lambda58)))))), ??? access ???)), ??? access ???)));
	JMethodDeclaration declaration = parseMethodDeclaration_Main(&((*_this)), declarationString, name);
	JMethod proto = new_JMethod(typeParameters, parameters, declaration, withBraces);
	return new_Some(proto);
}
/*TODO: switch*/ lambda59(void* _ref, CStructMember member1){
	return /*TODO: switch*/;
}
int lambda60(void* _ref, CDefinable member){
	return !(??? instanceof ???);
}
List<CDefinable> retainFields_Main(void* _ref, List<CStructMember> members){
	Main* _this = (Main*) _ref;
	List list = toList_Iter(&(flatMap_Iter(&(map_Iter(&(iter_List(&(members))), lambda59)), ??? access ???)));
	return toList_Iter(&(filter_Iter(&(iter_List(&(list))), lambda60)));
}
Option<CDefinable> retainDefinables_Main(void* _ref, CStructMember member){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
/*Not a functional type: Placeholder[input=Cannot access member 'isEmpty' in 'Boolean', not an object.]*/ lambda61(void* _ref, char* slice){
	return isEmpty_bool(&(!slice));
}
List<char*> splitValues_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	/*Not a functional type: Placeholder[input=Member 'split' not defined in 'magma.Main$JRecursiveType@58c2f281']*/ segments = split_String(&(input), quote_/*Undefined identifier: Pattern*/(&(Pattern), ","));
	/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ list = toList_/*Not a functional type: Placeholder[input=Cannot access member 'filter' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]]', not an object.]*/(&(filter_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'stream' in 'Placeholder[input=Undefined identifier: Arrays]', not an object.]*/(&(stream_/*Undefined identifier: Arrays*/(&(Arrays), segments)), ??? access ???)), lambda61)));
	return new_JavaList(list);
}
char* lambda62(void* _ref, CDeclaration parameter){
	return parameter.name;
}
todoLambda lambda63(void* _ref){
	CType type = transformType_Main(&((*_this)), declaration.type);
	List list = toList_Iter(&(map_Iter(&(iter_List(&(subList_List(&(cParameters), 1, size_List(&(cParameters)))))), lambda62)));
	return createBodyForAbstractMethod_Main(&((*_this)), structureVariants, type, declaration.name, list, structName);
}
char* computeMethodBody_Main(void* _ref, List<char*> typeParameters, JMethodDeclaration methodDeclaration, List<CDeclaration> cParameters, Option<char*> maybeContent, char* structName, List<char*> structureVariants){
	Main* _this = (Main*) _ref;
	if (??? instanceof ???) {
		char* compiled = orElse_Option(&(maybeContent), "?");
		return generateStatement(structName + " _thisInstance") + generateStatement(structName + "* _this = &_thisInstance") + compiled + generateStatement("return " + "_thisInstance");
	}
	if (??? instanceof ???) {
		char* joinedTypeParameters = joinTypeParameters(typeParameters);
		char* thisInitialization = generateStatement(structName + joinedTypeParameters + "* _this = (" + structName + joinedTypeParameters + "*) _ref");
		char* body = orElseGet_Option(&(maybeContent), lambda63);
		return thisInitialization + body;
	}
	return "?";
}
char* lambda64(void* _ref, char* variant){
	return generateCase_Main(&((*_this)), variant, name, parameterNames, structName);
}
char* createBodyForAbstractMethod_Main(void* _ref, List<char*> variants, CType type, char* name, List<char*> parameterNames, char* structName){
	Main* _this = (Main*) _ref;
	if (isEmpty_List(&(variants))) {
		/*Identifier 'C' has not been defined*/ joinedParameters = collect_Iter(&(iter_List(&(addFirst_List(&(parameterNames), "_this->data")))), new_Joiner(", "));
		return generateStatement("return _this->table." + name + "(" + joinedParameters + ")");
	}
	else {
		char* returnValueDefinition = generateStatement(generate_CType(&(type)) + " _ret");
		/*Identifier 'C' has not been defined*/ cases = collect_Iter(&(map_Iter(&(iter_List(&(variants))), lambda64)), new_Joiner());
		return returnValueDefinition + generateIndent(1) + "switch (" + "_this->variant" + ") {" + cases + generateIndent(1) + "}" + generateStatement("return _ret");
	}
}
List lambda65(void* _ref, List<char*> typeParameters0){
	return addAllLast_List(&(typeParameters0), typeParameters);
}
int lambda66(void* _ref, char* name){
	return name + "_" + structName;
}
CDefinable transformMethodDeclaration_Main(void* _ref, char* structName, List<char*> typeParameters, JMethodDeclaration methodDeclaration){
	Main* _this = (Main*) _ref;
	return mapName_CDefinable(&(mapTypeParameters_CDefinable(&(convertToFunctionDeclarations_Main(&((*_this)), typeParameters, methodDeclaration)), lambda65)), lambda66);
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
char* lambda67(void* _ref, char* input){
	return compileMethodSegment_Main(&((*_this)), input, indent);
}
char* compileMethodsSegments_Main(void* _ref, char* inputContent, int indent){
	Main* _this = (Main*) _ref;
	return compileStatements_Main(&((*_this)), inputContent, lambda67);
}
char* generateCase_Main(void* _ref, char* variant, char* name, List<char*> parameterNames, char* baseName){
	Main* _this = (Main*) _ref;
	char* s = "&(_this->data." + variant + ")";
	/*Identifier 'C' has not been defined*/ joined = collect_Iter(&(iter_List(&(addFirst_List(&(copy_List(&(parameterNames))), s)))), new_Joiner(", "));
	return generateIndent(2) + "case " + baseName + "Tag::" + variant + "Variant:" + generateStatement(3, "_ret = " + name + "_" + variant + "(" + joined + ")") + generateStatement(3, "break");
}
Option lambda68(void* _ref){
	return map_Option(&(parseDeclaration_Main(&((*_this)), declaration)), ??? access ???);
}
Placeholder lambda69(void* _ref){
	return new_Placeholder(declaration);
}
JMethodDeclaration parseMethodDeclaration_Main(void* _ref, char* declaration, char* structName){
	Main* _this = (Main*) _ref;
	return orElseGet_Option(&(or_Option(&(parseConstructor_Main(&((*_this)), declaration, structName)), lambda68)), lambda69);
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
State lambda70(void* _ref, State state, char character){
	return apply_ValueFolder(&(new_ValueFolder()), state, character);
}
/*Not a functional type: Placeholder[input=Cannot access member 'isEmpty' in 'Boolean', not an object.]*/ lambda71(void* _ref, char* slice){
	return isEmpty_bool(&(!slice));
}
Option lambda72(void* _ref, char* enumValue){
	return compileEnumValue_Main(&((*_this)), structName, enumValue);
}
int lambda73(void* _ref, AnyMatch</*Option<CStructMember>>((Option<CStructMember*/> option){
	return ??? instanceof ???;
}
Option<JObjectMember> parseEnumValues_Main(void* _ref, char* structName, char* input){
	Main* _this = (Main*) _ref;
	List enumValues = toList_Iter(&(filter_Iter(&(map_Iter(&(divide_Main(&((*_this)), input, lambda70)), ??? access ???)), lambda71)));
	if (isEmpty_bool(&(!enumValues))) {
		Iter optionStream = map_Iter(&(iter_List(&(enumValues))), lambda72);
		/*Undefined identifier: lambda73*/ areAnyInvalid = lambda73;
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
			int generated = structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2
						+ ")" + ";" + lineSeparator_/*Undefined identifier: System*/(&(System));
			_this->globals = addLast_List(&(_this->globals), generated);
			_this->globalDeclarations = addLast_List(&(_this->globalDeclarations), "extern " + structName + " " + structName + name + ";" + lineSeparator_/*Undefined identifier: System*/(&(System)));
			return new_Some(new_EmptyStructMember());
		}
	}
	if (isIdentifier_Identifier(&(Identifier), stripped)) {
		int generated = structName + " " + structName + stripped + " = " + "new_" + structName + "()" + ";" + lineSeparator_/*Undefined identifier: System*/(&(System));
		_this->globals = addLast_List(&(_this->globals), generated);
		_this->globalDeclarations = addLast_List(&(_this->globalDeclarations), "extern " + structName + " " + structName + stripped + ";" + lineSeparator_/*Undefined identifier: System*/(&(System)));
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
	if (??? instanceof ???) 
		return result;
	Option maybeWhile = compileConditional_Main(&((*_this)), "while", indent, stripped);
	if (??? instanceof ???) 
		return result;
	if (startsWith_String(&(stripped), "else ")) {
		char* substring = strip_String(&(substring_String(&(stripped), length_String(&("else ")))));
		if (startsWith_String(&(substring), "{") && endsWith_String(&(substring), "}")) {
			char* substring1 = substring_String(&(substring), 1, length_String(&(substring)) - 1);
			return generateIndent(indent) + "else {" + compileMethodsSegments_Main(&((*_this)), substring1, indent + 1) + generateIndent(indent) + "}";
		}
		/*else
				return generateIndent(indent) + "else " + this.compileMethodSegment(substring, indent + 1)*/;
	}
	if (endsWith_String(&(stripped), ";")) {
		char* substring = substring_String(&(stripped), 0, length_String(&(stripped)) - 1);
		return generateIndent(indent) + compileMethodStatement_Main(&((*_this)), substring) + ";";
	}
	if (startsWith_String(&(stripped), "//")) 
		return generateIndent(indent) + stripped;
	return lineSeparator_/*Undefined identifier: System*/(&(System)) + "\t" + wrap_Placeholder(&(Placeholder), stripped);
}
/*Not a functional type: Placeholder[input=Cannot access member 'isEmpty' in 'Boolean', not an object.]*/ lambda74(void* _ref, char* slice){
	return isEmpty_bool(&(!slice));
}
Option<char*> compileConditional_Main(void* _ref, char* type, int indent, char* input){
	Main* _this = (Main*) _ref;
	if (startsWith_String(&(input), type)) {
		char* substring = strip_String(&(substring_String(&(input), length_String(&(type)))));
		if (startsWith_String(&(substring), "(")) {
			char* afterConditionStart = strip_String(&(substring_String(&(substring), 1)));
			List divisions = toList_Iter(&(filter_Iter(&(map_Iter(&(divide_Main(&((*_this)), afterConditionStart, new_EscapedFolder(new_ConditionEndLocator()))), ??? access ???)), lambda74)));
			if (size_List(&(divisions)) < 2) 
				return new_None();
			/*Identifier 'R' has not been defined*/ first = getFirst_List(&(divisions));
			char* maybeWithBraces = joinStrings_Main(&((*_this)), subList_List(&(divisions), 1, size_List(&(divisions))));
			if (endsWith_bool(&(!first), ")")) 
				return new_None();
			/*Not a functional type: Placeholder[input=Cannot access member 'substring' in 'Placeholder[input=Identifier 'R' has not been defined]', not an object.]*/ condition = substring_/*Identifier 'R' has not been defined*/(&(first), 0, length_/*Identifier 'R' has not been defined*/(&(first)) - 1);
			if (startsWith_String(&(maybeWithBraces), "{") && endsWith_String(&(maybeWithBraces), "}")) {
				char* content = substring_String(&(maybeWithBraces), 1, length_String(&(maybeWithBraces)) - 1);
				return new_Some(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" + compileMethodsSegments_Main(&((*_this)), content, indent + 1) + generateIndent(indent) + "}");
			}
			return new_Some(generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") " + compileMethodSegment_Main(&((*_this)), maybeWithBraces, indent + 1));
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
		return "return " + compileExpressionOrPlaceholder_Main(&((*_this)), substring_String(&(stripped), length_String(&("return "))));
	Option maybeAssignment = compileAssignment_Main(&((*_this)), stripped);
	if (??? instanceof ???) 
		return assignment;
	Option maybeInvokable = parseInvokable_Main(&((*_this)), stripped);
	if (??? instanceof ???) 
		return generate_CExpression(&(transformExpression_Main(&((*_this)), value)));
	Option instance = post_Main(&((*_this)), stripped, "++");
	if (??? instanceof ???) 
		return x;
	Option instance0 = post_Main(&((*_this)), stripped, "--");
	if (??? instanceof ???) 
		return x;
	Option maybeDeclaration = parseDeclaration_Main(&((*_this)), input);
	if (??? instanceof ???) 
		return generate_CDeclaration(&(transformDeclaration_Main(&((*_this)), declaration)));
	if (startsWith_String(&(stripped), "assert ")) 
		return "";
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
		if (??? instanceof ???) {
			CAssignable cAssignable = transformAssignable_Main(&((*_this)), assignable, source);
			return new_Some(generate_CAssignable(&(cAssignable)) + " = " + generate_CExpression(&(transformExpression_Main(&((*_this)), source))));
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
		return /*environment
					.resolveCurrent()
					.<JType>map((JObjectType thisType) -> thisType)
					.orElseGet(() -> new Placeholder("Not within a struct"))*/;
	/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'resolveExpression' in 'Placeholder[input=Undefined identifier: environment]', not an object.]]', not an object.]*/ maybeFound = map_/*Not a functional type: Placeholder[input=Cannot access member 'resolveExpression' in 'Placeholder[input=Undefined identifier: environment]', not an object.]*/(&(resolveExpression_/*Undefined identifier: environment*/(&(environment), value)), ??? access ???);
	if (??? instanceof ???) 
		return found;
	if (??? instanceof ???) 
		return resolved;
	return new_Placeholder("Undefined identifier: " + value);
}
JType cleanupType_Main(void* _ref, JType type){
	Main* _this = (Main*) _ref;
	if (??? instanceof ???) {
		/*Not a functional type: Placeholder[input=Cannot access member 'resolveType' in 'Placeholder[input=Undefined identifier: environment]', not an object.]*/ maybeFound = resolveType_/*Undefined identifier: environment*/(&(environment), value);
		if (??? instanceof ???) 
			return found;
		/*else
				return new Placeholder("Identifier '" + value + "' has not been defined")*/;
	}
	if (??? instanceof ???) {
		/*Not a functional type: Placeholder[input=Cannot access member 'resolveType' in 'Placeholder[input=Undefined identifier: environment]', not an object.]*/ resolved = resolveType_/*Undefined identifier: environment*/(&(environment), base);
		if (??? instanceof ???) 
			return specialize_/*Undefined identifier: objType*/(&(objType), typeArguments);
		/*else
				return new Placeholder("Generic type '" + base + "' has not been defined")*/;
	}
	return type;
}
Placeholder lambda75(void* _ref){
	return new_Placeholder("Member '" + name + "' not defined in '" + instanceType + "'");
}
JType resolveMember_Main(void* _ref, JObjectType type, JType instanceType, char* name){
	Main* _this = (Main*) _ref;
	return orElseGet_Option(&(resolve_JObjectType(&(type), name)), lambda75);
}
JType resolveCaller_Main(void* _ref, JCaller caller){
	Main* _this = (Main*) _ref;
	return /*TODO: switch*/;
}
JAssignable parseAssignable_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return /*this
				.parseExpression(input)
				.<JAssignable>map((JExpression value) -> value)
				.or(() -> this.parseDeclaration(input).map((JDeclaration value) -> value))
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
char* lambda76(void* _ref){
	return wrap_Placeholder(&(Placeholder), input);
}
char* compileExpressionOrPlaceholder_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return orElseGet_Option(&(map_Option(&(parseCExpression_Main(&((*_this)), input)), ??? access ???)), lambda76);
}
Option<CExpression> parseCExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return map_Option(&(parseExpression_Main(&((*_this)), input)), ??? access ???);
}
Option lambda77(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.NotEquals);
}
Option lambda78(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.LessThan);
}
Option lambda79(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.Add);
}
Option lambda80(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.Subtract);
}
Option lambda81(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.And);
}
Option lambda82(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.Or);
}
Option lambda83(void* _ref){
	return compileOperator_Main(&((*_this)), stripped, Operator.GreaterThanOrEquals);
}
Option<JExpression> parseExpression_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (startsWith_String(&(stripped), "(") && stripped.endsWith(")")) {
		char* content = substring_String(&(stripped), 1, length_String(&(stripped)) - 1);
		Option maybeParsed = parseExpression_Main(&((*_this)), content);
		if (??? instanceof ???) 
			return new_Some(new_JQuantity(parsed));
	}
	if (startsWith_String(&(stripped), "switch ")) 
		return new_Some(new_Placeholder("TODO: switch"));
	int i2 = lastIndexOf_String(&(stripped), "::");
	if (i2 >= 0) {
		char* substring = substring_String(&(stripped), 0, i2);
		char* methodName = strip_String(&(substring_String(&(stripped), i2 + 2)));
		if (isIdentifier_Identifier(&(Identifier), methodName)) {
			Option maybeInstance = parseExpression_Main(&((*_this)), substring);
			if (??? instanceof ???) 
				return new_Some(new_JMethodAccess(instance, methodName));
		}
	}
	if (startsWith_String(&(stripped), "'") && endsWith_String(&(stripped), "'")) {
		char* content = substring_String(&(stripped), 1, length_String(&(stripped)) - 1);
		return new_Some(new_Char(content));
	}
	Option maybeLambda = compileLambda_Main(&((*_this)), stripped);
	if (??? instanceof ???) 
		return maybeLambda;
	int i3 = indexOf_String(&(stripped), "instanceof");
	if (i3 >= 0) {
		char* substring = substring_String(&(stripped), 0, i3);
		Option maybeInstance = map_Option(&(parseCExpression_Main(&((*_this)), substring)), ??? access ???);
		if (??? instanceof ???) 
			return new_Some(new_JInstanceOf());
	}
	Option maybeOperator = or_Option(&(or_Option(&(or_Option(&(or_Option(&(or_Option(&(or_Option(&(or_Option(&(compileOperator_Main(&((*_this)), stripped, Operator.Equals)), lambda77)), lambda78)), lambda79)), lambda80)), lambda81)), lambda82)), lambda83);
	if (??? instanceof ???) 
		return maybeOperator;
	Option maybeInvokable = parseInvokable_Main(&((*_this)), stripped);
	if (??? instanceof ???) 
		return maybeInvokable;
	int i = lastIndexOf_String(&(stripped), ".");
	if (i >= 0) {
		char* instanceString = substring_String(&(stripped), 0, i);
		char* memberName = strip_String(&(substring_String(&(stripped), i + 1)));
		if (isIdentifier_Identifier(&(Identifier), memberName)) {
			Option maybeInstance = parseExpression_Main(&((*_this)), instanceString);
			if (??? instanceof ???) 
				return new_Some(new_JMemberAccess(value, memberName));
		}
	}
	if (isIdentifier_Identifier(&(Identifier), stripped)) 
		return new_Some(new_Identifier(stripped));
	if (startsWith_String(&(stripped), "!")) {
		char* substring = substring_String(&(stripped), 1);
		Option maybeInstance = parseCExpression_Main(&((*_this)), substring);
		if (??? instanceof ???) 
			return new_Some(new_JNot(instance));
	}
	if (isNumber_Main(&((*_this)), stripped)) 
		return new_Some(new_JNumber(stripped));
	if (startsWith_String(&(stripped), "\"") && endsWith_String(&(stripped), "\"") && length_String(&(stripped)) >= 2) 
		return new_Some(new_StringNode(substring_String(&(stripped), 1, length_String(&(stripped)) - 1)));
	return new_None();
}
char* lambda84(void* _ref){
	return wrap_Placeholder(&(Placeholder), maybeWithBraces);
}
Identifier lambda85(void* _ref){
	return new_Identifier("todoLambda");
}
todoLambda lambda86(void* _ref, Environment env){
	Environment defined = defineAllExpressions_Environment(&(env), params);
	Tuple<JType, char*> jTypeStringTuple;
	if (startsWith_String(&(maybeWithBraces), "{") && endsWith_String(&(maybeWithBraces), "}")) {
		char* content = substring_String(&(maybeWithBraces), 1, length_String(&(maybeWithBraces)) - 1);
		char* output1 = compileMethodsSegments_Main(&((*_this)), content, 1);
		Identifier returnType1 = new_Identifier("todoLambda");
		jTypeStringTuple = new_Tuple(returnType1, output1);
	}
	else {
		Option jExpressionOption = parseExpression_Main(&((*_this)), maybeWithBraces);
		char* output1 = generateStatement("return " + orElseGet_Option(&(map_Option(&(map_Option(&(jExpressionOption), ??? access ???)), ??? access ???)), lambda84));
		/*Identifier 'R' has not been defined*/ returnType1 = orElseGet_Option(&(map_Option(&(jExpressionOption), ??? access ???)), lambda85);
		jTypeStringTuple = new_Tuple(returnType1, output1);
	}
	return new_Tuple(defined, jTypeStringTuple);
}
Option<JExpression> compileLambda_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	int index = indexOf_String(&(input), "->");
	if (index < 0) 
		return new_None();
	char* beforeContent = strip_String(&(substring_String(&(input), 0, index)));
	char* maybeWithBraces = strip_String(&(substring_String(&(input), index + 2)));
	Option maybeParams = parseLambdaParams_Main(&((*_this)), beforeContent);
	if (!(??? instanceof ???)) 
		return new_None();
	/*Not a functional type: Placeholder[input=Cannot access member 'addFirst' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]]', not an object.]]', not an object.]*/ paramList = addFirst_/*Not a functional type: Placeholder[input=Cannot access member 'toList' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]]', not an object.]*/(&(toList_/*Not a functional type: Placeholder[input=Cannot access member 'map' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]]', not an object.]*/(&(map_/*Not a functional type: Placeholder[input=Cannot access member 'iter' in 'Placeholder[input=Undefined identifier: params]', not an object.]*/(&(iter_/*Undefined identifier: params*/(&(params))), ??? access ???)))), new_CDeclaration(new_CPointerType(CPrimitiveType.Void), "_ref"));
	/*Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Undefined identifier: environment]', not an object.]*/ within = within_/*Undefined identifier: environment*/(&(environment), lambda86);
	environment = within.left;
	/*Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Undefined identifier: environment]', not an object.]]', not an object.*/ right = within.right;
	/*Cannot access member 'left' in 'Placeholder[input=Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Undefined identifier: environment]', not an object.]]', not an object.]', not an object.*/ returnType = right.left;
	/*Cannot access member 'right' in 'Placeholder[input=Cannot access member 'right' in 'Placeholder[input=Not a functional type: Placeholder[input=Cannot access member 'within' in 'Placeholder[input=Undefined identifier: environment]', not an object.]]', not an object.]', not an object.*/ output = right.right;
	char* generatedName = generateName_Main(&((*_this)));
	CDeclaration definition = new_CDeclaration(transformType_Main(&((*_this)), returnType), generatedName);
	CFunctionHeader header = new_CFunctionHeader(definition, paramList);
	CFunction cFunction = new_CFunction(header, output);
	_this->functions = addLast_List(&(_this->functions), cFunction);
	return new_Some(new_Identifier(generatedName));
}
/*Not a functional type: Placeholder[input=Cannot access member 'isEmpty' in 'Boolean', not an object.]*/ lambda87(void* _ref, char* slice){
	return isEmpty_bool(&(!slice));
}
Option<List<JDeclaration>> parseLambdaParams_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	if (startsWith_bool(&(!input), "(") || !input.endsWith(")")) 
		return new_None();
	char* substring = substring_String(&(input), 1, length_String(&(input)) - 1);
	List paramList = toList_Iter(&(flatMap_Iter(&(map_Iter(&(filter_Iter(&(map_Iter(&(divide_Main(&((*_this)), substring, new_ValueFolder())), ??? access ???)), lambda87)), ??? access ???)), ??? access ???)));
	return new_Some(paramList);
}
char* generateName_Main(void* _ref){
	Main* _this = (Main*) _ref;
	int generatedName = "lambda" + _this->counter;
	_this->counter++;
	return generatedName;
}
Option<JExpression> compileOperator_Main(void* _ref, char* input, Operator operator){
	Main* _this = (Main*) _ref;
	char* operatorValue = operator.value;
	if (contains_bool(&(!input), operatorValue)) 
		return new_None();
	int i1 = -1;
	int depth = 0;
	int i = 0;
	while (i < length_String(&(input)) - 1) {
		char c = charAt_String(&(input), i);
		if (c == charAt_String(&(operatorValue), 0)) 
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
		char* leftString = substring_String(&(input), 0, i1);
		char* rightString = substring_String(&(input), i1 + length_String(&(operatorValue)));
		if (??? instanceof ???) 
			if (??? instanceof ???) 
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
	if (!(??? instanceof ???)) 
		return new_None();
	List arguments = toList_Iter(&(flatMap_Iter(&(map_Iter(&(divide_Main(&((*_this)), argumentsString, new_EscapedFolder(new_ValueFolder()))), ??? access ???)), ??? access ???)));
	return new_Some(new_JInvokable(value, arguments));
}
int findCallerStart_Main(void* _ref, char* withoutEnd){
	Main* _this = (Main*) _ref;
	int callerStart = -1;
	int depth = 0;
	int i = 0;
	while (i < length_String(&(withoutEnd))) {
		char c = charAt_String(&(withoutEnd), i);
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
	char* stripped = strip_String(&(input));
	if (isEmpty_String(&(stripped))) 
		return false;
	if (startsWith_String(&(stripped), "-")) 
		return allDigits_Main(&((*_this)), substring_String(&(stripped), 1));
	return allDigits_Main(&((*_this)), stripped);
}
int allDigits_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return collect_Iter(&(map_Iter(&(new_Iter(new_RangeHead(length_String(&(input))))), ??? access ???)), new_AllMatch(??? access ???));
}
Option<JCaller> parseCaller_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	if (startsWith_String(&(stripped), "new ")) {
		char* type = substring_String(&(stripped), length_String(&("new ")));
		Option maybeParsedType = parseType_Main(&((*_this)), type);
		if (??? instanceof ???) 
			return new_Some(new_JConstruction(parsedType));
	}
	Option maybeExpression = parseExpression_Main(&((*_this)), stripped);
	if (??? instanceof ???) 
		return new_Some(expression);
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
			JType type = parseTypeOrPlaceholder_Main(&((*_this)), beforeName);
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
			JType type = parseTypeOrPlaceholder_Main(&((*_this)), substring_String(&(beforeName), typeSeparator + 1));
			JDeclaration jDeclaration = new_JDeclaration(annotations, copy, new_Some(beforeType), type, name);
			return new_Some(jDeclaration);
		}
	}
	return new_None();
}
/*Not a functional type: Placeholder[input=Cannot access member 'isEmpty' in 'Boolean', not an object.]*/ lambda88(void* _ref, char* slice){
	return isEmpty_bool(&(!slice));
}
char* lambda89(void* _ref, char* slice){
	return substring_String(&(slice), 1);
}
List<char*> collectAnnotations_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return toList_Iter(&(map_Iter(&(map_Iter(&(filter_Iter(&(fromObjArray_Streams(&(Streams), split_String(&(input), quote_/*Undefined identifier: Pattern*/(&(Pattern), "\n")))), lambda88)), lambda89)), ??? access ???)));
}
int findTypeSeparator_Main(void* _ref, char* beforeName){
	Main* _this = (Main*) _ref;
	int typeSeparator = -1;
	int depth = 0;
	int i = 0;
	while (i < length_String(&(beforeName))) {
		char c = charAt_String(&(beforeName), i);
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
Placeholder lambda90(void* _ref){
	return new_Placeholder(input);
}
JType parseTypeOrPlaceholder_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	return orElseGet_Option(&(parseType_Main(&((*_this)), input)), lambda90);
}
Option<JType> parseType_Main(void* _ref, char* input){
	Main* _this = (Main*) _ref;
	char* stripped = strip_String(&(input));
	/*switch (stripped) {
			case "boolean", "Boolean" -> {
				return new Some<JType>(JPrimitiveType.Boolean);
			}
			case "int", "Integer" -> {
				return new Some<JType>(JPrimitiveType.Int);
			}
			case "void" -> {
				return new Some<JType>(JPrimitiveType.Void);
			}
			case "String" -> {
				return new Some<JType>(this.StringType);
			}
			case "Character" -> {
				return new Some<JType>(JPrimitiveType.Char);
			}
			case "var" -> {
				return new Some<JType>(JPrimitiveType.Var);
			}
		}*/
	if (endsWith_String(&(stripped), "[]")) {
		char* slice = substring_String(&(stripped), 0, length_String(&(stripped)) - 2);
		JType type = parseTypeOrPlaceholder_Main(&((*_this)), slice);
		return new_Some(new_JArrayType(type));
	}
	if (endsWith_String(&(stripped), ">")) {
		char* substring = substring_String(&(stripped), 0, length_String(&(stripped)) - 1);
		int i = indexOf_String(&(substring), "<");
		if (i >= 0) {
			char* base = substring_String(&(substring), 0, i);
			char* parameters = substring_String(&(substring), i + 1);
			List list = toList_Iter(&(map_Iter(&(divide_Main(&((*_this)), parameters, new_ValueFolder())), ??? access ???)));
			return new_Some(new_JGenericType(base, list));
		}
	}
	if (isIdentifier_Identifier(&(Identifier), stripped)) 
		return new_Some(new_Identifier(stripped));
	// TODO: handle varargs through monomorphization
	return new_None();
}
