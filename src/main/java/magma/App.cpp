struct App;
struct CPrimitiveType;
template <typename T, typename R>
struct Function;
template <typename A, typename B, typename R>
struct BiFunction;
template <typename T>
struct Consumer;
template <typename T>
struct Head;
template <typename T, typename C>
struct Collector;
template <typename T>
struct Supplier;
template <typename T, typename X>
struct Result;
struct CType;
struct CFunctionHeader;
template <typename T>
struct Option;
template <typename T>
struct Predicate;
struct CStructureMember;
struct CNode;
struct CCaller;
template <typename T>
struct SingleHead;
template <typename T>
struct EmptyHead;
template <typename T, typename R>
struct ZipHead;
template <typename T>
struct Stream;
template <typename T>
struct ArrayList;
template <typename T, typename X>
struct Err;
template <typename T, typename X>
struct Ok;
template <typename T>
struct Some;
template <typename T>
struct None;
struct CPointerType;
struct CTemplateType;
struct CIdentifier;
struct Placeholder;
template <typename A, typename B>
struct Tuple;
struct State;
struct CDefinition;
struct CStructureHeader;
struct CStructure;
template <typename T, typename R>
struct MapHead;
template <typename T>
struct ListHead;
template <typename T>
struct ListCollector;
template <typename T, typename R>
struct FlatMapHead;
struct Joiner;
struct EmptyCStructureSegment;
struct CContent;
struct CStatement;
struct CFieldAccess;
struct Frames;
template <typename K, typename V>
struct HashMap;
template <typename K, typename V>
struct MapCollector;
struct CStructureType;
struct CConstruction;
struct CInvocation;
struct CMethodMember;
struct CFunctionType;
struct Frame;
template <typename T>
struct ArrayHead;
/*
*/struct CPrimitiveType {
	char* content;
};
template <typename T, typename R>
struct Function {
};
template <typename A, typename B, typename R>
struct BiFunction {
};
template <typename T>
struct Consumer {
};
template <typename T>
struct Head {
};
template <typename T, typename C>
struct Collector {
};
template <typename T>
struct Supplier {
};
template <typename T>
struct Predicate {
};
struct CNode {
};
template <typename T>
struct SingleHead {
	T element;
	int retrieved;
};
template <typename T>
struct EmptyHead {
};
template <typename T, typename R>
struct ZipHead {
	Head<T> head;
	Head<R> otherHead;
};
template <typename T>
struct Stream {
	Head<T> head;
};
template <typename T>
struct ArrayList {
	List<T> inner;
};
template <typename T, typename X>
struct Err {
	X error;
};
template <typename T, typename X>
struct Ok {
	T value;
};
template <typename T>
struct Some {
	T value;
};
template <typename T>
struct None {
};
struct CPointerType {
	CType type;
};
struct CTemplateType {
	char* base;
	ArrayList<CType> typeArguments;
};
struct CIdentifier {
	char* value;
};
struct Placeholder {
	char* input;
};
template <typename A, typename B>
struct Tuple {
	A left;
	B right;
};
struct State {
	char* input;
	ArrayList<char*> segments;
	char* buffer;
	int depth;
	int index;
};
struct CDefinition {
	ArrayList<char*> typeParameters;
	CType type;
	char* name;
};
struct CStructureHeader {
	ArrayList<char*> typeParameters;
	char* name;
};
struct CStructure {
	CStructureHeader CStructureHeader;
	char* fields;
};
template <typename T, typename R>
struct MapHead {
	Function<T, R> mapper;
	Head<T> head;
};
template <typename T>
struct ListHead {
	ArrayList<T> list;
	int counter;
};
template <typename T>
struct ListCollector {
};
template <typename T, typename R>
struct FlatMapHead {
	Head<T> head;
	Function<T, Stream<R>> mapper;
	Head<R> current;
};
struct Joiner {
	char* delimiter;
};
struct EmptyCStructureSegment {
};
struct CContent {
	char* content;
};
struct CStatement {
	CNode content;
	int depth;
};
struct CFieldAccess {
	CExpression child;
	char* name;
};
struct Frames {
	ArrayList<Frame> frames;
};
template <typename K, typename V>
struct HashMap {
	java.util.HashMap<K, V> internal;
};
template <typename K, typename V>
struct MapCollector {
};
struct CStructureType {
	char* name;
	ArrayList<char*> typeParameters;
	ArrayList<CDefinition> fields;
};
struct CConstruction {
	CType type;
};
struct CInvocation {
	CCaller caller;
	ArrayList<CExpression> arguments;
};
struct CMethodMember {
	CDefinition definition;
};
struct CFunctionType {
	CType returnType;
	ArrayList<CType> paramTypes;
};
struct Frame {
	Option<CStructureHeader> maybeHeader;
	ArrayList<CDefinition> definitions;
	ArrayList<CStructureType> structures;
};
template <typename T>
struct ArrayHead {
	T* array;/*
		private int counter = 0;*/
};
struct App {
	Frames frames;
	ArrayList<char*> globals;
	ArrayList<char*> forwardDeclarations;
	ArrayList<char*> structures;
	ArrayList<char*> sealedStructures;
	ArrayList<char*> functions;
	int counter;
	int depth;
};
enum ResultTag {
	ErrTag,
	OkTag
};
template <typename T, typename X>
union ResultData {
	Err<T, X> err;
	Ok<T, X> ok;
};
template <typename T, typename X>
struct Result {
	ResultTag tag;
	ResultData<T, X> data;
};
enum CTypeTag {
	CIdentifierTag,
	CPointerTypeTag,
	CPrimitiveTypeTag,
	CStructureTypeTag,
	CTemplateTypeTag,
	CFunctionTypeTag,
	PlaceholderTag
};
union CTypeData {
	CIdentifier cidentifier;
	CPointerType cpointertype;
	CPrimitiveType cprimitivetype;
	CStructureType cstructuretype;
	CTemplateType ctemplatetype;
	CFunctionType cfunctiontype;
	Placeholder placeholder;
};
struct CType {
	CTypeTag tag;
	CTypeData data;
};
enum CFunctionHeaderTag {
	CDefinitionTag,
	PlaceholderTag
};
union CFunctionHeaderData {
	CDefinition cdefinition;
	Placeholder placeholder;
};
struct CFunctionHeader {
	CFunctionHeaderTag tag;
	CFunctionHeaderData data;
};
enum OptionTag {
	NoneTag,
	SomeTag
};
template <typename T>
union OptionData {
	None<T> none;
	Some<T> some;
};
template <typename T>
struct Option {
	OptionTag tag;
	OptionData<T> data;
};
enum CStructureMemberTag {
	CMethodMemberTag,
	CStructureSegmentTag
};
union CStructureMemberData {
	CMethodMember cmethodmember;
	CStructureSegment cstructuresegment;
};
struct CStructureMember {
	CStructureMemberTag tag;
	CStructureMemberData data;
};
enum CCallerTag {
	CConstructionTag,
	CExpressionTag
};
union CCallerData {
	CConstruction cconstruction;
	CExpression cexpression;
};
struct CCaller {
	CCallerTag tag;
	CCallerData data;
};
CPrimitiveType VoidValue = CPrimitiveType { "void" };
CPrimitiveType CharValue = CPrimitiveType { "char" };
CPrimitiveType IntValue = CPrimitiveType { "int" };
App new_App() {
	App _this;
	return _this;
}
CPrimitiveType new_CPrimitiveType() {
	CPrimitiveType _this;
	return _this;
}
CType toCType_CPrimitiveType(void* _ref){
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	CTypeData data;
	data.cprimitivetype = _this;
	return CType { CPrimitiveTypeTag, data };
}
CPrimitiveType new_CPrimitiveType(void* _ref, char* content) {
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	_this.content = content;
}
char* generate_CPrimitiveType(void* _ref) {
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	return _this.content;
}
char* getSimpleName_CPrimitiveType(void* _ref) {
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	return _this.content;
}
CType replaceIdentifiersWithMapping_CPrimitiveType(void* _ref, HashMap<char*, CType> mapping) {
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	return _this;
}
template <typename T, typename R>
Function<T, R> new_Function() {
	Function<T, R> _this;
	return _this;
}
template <typename T, typename R>
R apply_Function(void* _ref, T arg);
template <typename A, typename B, typename R>
BiFunction<A, B, R> new_BiFunction() {
	BiFunction<A, B, R> _this;
	return _this;
}
template <typename A, typename B, typename R>
R apply_BiFunction(void* _ref, A left, B right);
template <typename T>
Consumer<T> new_Consumer() {
	Consumer<T> _this;
	return _this;
}
template <typename T>
void accept_Consumer(void* _ref, T value);
template <typename T>
Head<T> new_Head() {
	Head<T> _this;
	return _this;
}
template <typename T>
Option<T> next_Head(void* _ref);
template <typename T, typename C>
Collector<T, C> new_Collector() {
	Collector<T, C> _this;
	return _this;
}
template <typename T, typename C>
C createInitial_Collector(void* _ref);
template <typename T, typename C>
C fold_Collector(void* _ref, C current, T element);
template <typename T>
Supplier<T> new_Supplier() {
	Supplier<T> _this;
	return _this;
}
template <typename T>
T get_Supplier(void* _ref);
template <typename T, typename X>
Result<T, X> new_Result() {
	Result<T, X> _this;
	return _this;
}
CType new_CType() {
	CType _this;
	return _this;
}
char* generate_CType(void* _ref);
char* getSimpleName_CType(void* _ref);
CType replaceIdentifiersWithMapping_CType(void* _ref, HashMap<char*, CType> mapping);
CFunctionHeader new_CFunctionHeader() {
	CFunctionHeader _this;
	return _this;
}
char* generate_CFunctionHeader(void* _ref);
template <typename T>
Option<T> new_Option() {
	Option<T> _this;
	return _this;
}
template <typename T, typename R>
Option<R> map_Option(void* _ref, Function<T, R> mapper);
template <typename T>
void ifPresent_Option(void* _ref, Consumer<T> consumer);
template <typename T>
Option<T> or_Option(void* _ref, Supplier<Option<T>> other);
template <typename T>
int isEmpty_Option(void* _ref);
template <typename T>
T get_Option(void* _ref);
template <typename T, typename R>
Option<R> flatMap_Option(void* _ref, Function<T, Option<R>> mapper);
template <typename T>
T orElse_Option(void* _ref, T other);
template <typename T>
T orElseGet_Option(void* _ref, Supplier<T> other);
template <typename T>
int isPresent_Option(void* _ref);
template <typename T>
Stream<T> stream_Option(void* _ref);
template <typename T, typename R>
Option<Tuple<T, R>> and_Option(void* _ref, Supplier<Option<R>> other);
template <typename T>
Predicate<T> new_Predicate() {
	Predicate<T> _this;
	return _this;
}
template <typename T>
int test_Predicate(void* _ref, T element);
App new_App(void* _ref);
CStructureMember new_CStructureMember() {
	CStructureMember _this;
	return _this;
}
Option<CDefinition> toDefinition_CStructureMember(void* _ref);
CNode new_CNode() {
	CNode _this;
	return _this;
}
char* generate_CNode(void* _ref);
App new_App(void* _ref);
CCaller new_CCaller() {
	CCaller _this;
	return _this;
}
char* generate_CCaller(void* _ref);
template <typename T>
SingleHead<T> new_SingleHead() {
	SingleHead<T> _this;
	return _this;
}
template <typename T>
Head<T> toHead_SingleHead(void* _ref){
	SingleHead<T> _this = *((SingleHead<T>*) _ref);
	HeadData<T> data;
	data.singlehead = _this;
	return Head<T> { SingleHeadTag, data };
}
template <typename T>
SingleHead<T> new_SingleHead(void* _ref, T element) {
	SingleHead<T> _this = *((SingleHead*) _ref);
	_this.element = element;
	_this.retrieved = 0;
}
template <typename T>
Option<T> next_SingleHead(void* _ref) {
	SingleHead<T> _this = *((SingleHead*) _ref);
	if (_this.retrieved) {
		return new_None<T>();
	}
	_this.retrieved = 1;
	return new_Some<T>(_this.element);
}
template <typename T>
EmptyHead<T> new_EmptyHead() {
	EmptyHead<T> _this;
	return _this;
}
template <typename T>
Head<T> toHead_EmptyHead(void* _ref){
	EmptyHead<T> _this = *((EmptyHead<T>*) _ref);
	HeadData<T> data;
	data.emptyhead = _this;
	return Head<T> { EmptyHeadTag, data };
}
template <typename T>
Option<T> next_EmptyHead(void* _ref) {
	EmptyHead<T> _this = *((EmptyHead*) _ref);
	return new_None<T>();
}
template <typename T, typename R>
ZipHead<T, R> new_ZipHead(Head<T> head, Head<R> otherHead) {
	ZipHead<T, R> _this;
	_this.head = head;
	_this.otherHead = otherHead;
	return _this;
}
template <typename T, typename R>
Head<Tuple<T, R>> toHead_ZipHead(void* _ref){
	ZipHead<T, R> _this = *((ZipHead<T, R>*) _ref);
	HeadData<T, R> data;
	data.ziphead = _this;
	return Head<Tuple<T, R>> { ZipHeadTag, data };
}
template <typename T, typename R>
Option<Tuple<T, R>> next_ZipHead(void* _ref) {
	ZipHead<T, R> _this = *((ZipHead*) _ref);
	return and_/*Not a function type: Placeholder[input=next_Head<T>]*/(next_?);
}
template <typename T>
Stream<T> new_Stream(Head<T> head) {
	Stream<T> _this;
	_this.head = head;
	return _this;
}
template <typename T, typename R>
Stream<R> map_Stream(void* _ref, Function<T, R> mapper) {
	Stream<T> _this = *((Stream*) _ref);
	return new_Stream<R>(new_MapHead<T, R>(_this.head, mapper));
}
template <typename T, typename R>
R fold_Stream(void* _ref, R initial, BiFunction<R, T, R> folder) {
	Stream<T> _this = *((Stream*) _ref);
	R current = initial;
	while (1) {
		Head<T> head = _this.head;
		/*Not a function type: Placeholder[input=next_Head]*/ maybeNext = next_Head();
		if (/*maybeNext instanceof Some*/ < /*T>*/(/*var next*/)) {
			current = apply_BiFunction(current, /*next*/);
		}
		else {
			return current;
		}
	}
}
template <typename T, typename C>
C collect_Stream(void* _ref, Collector<T, C> collector) {
	Stream<T> _this = *((Stream*) _ref);
	return fold_Stream(createInitial_Collector(), fold_collector);
}
template <typename T>
ArrayList<T> toList_Stream(void* _ref) {
	Stream<T> _this = *((Stream*) _ref);
	return collect_Stream(new_ListCollector<T>());
}
auto _lambda1_(auto _ref, auto element) {
	auto _this = _ref;
	return applyFilter_Stream(predicate, /*element*/);
};
template <typename T>
Stream<T> filter_Stream(void* _ref, Predicate<T> predicate) {
	Stream<T> _this = *((Stream*) _ref);
	return flatMap_Stream(_lambda1_);
}
template <typename T>
Stream<T> applyFilter_Stream(void* _ref, Predicate<T> predicate, T element) {
	Stream<T> _this = *((Stream*) _ref);
	if (test_Predicate(element)) {
		return new_Stream<T>(new_SingleHead<T>(element));
	}
	return new_Stream<T>(new_EmptyHead<T>());
}
template <typename T, typename R>
Stream<R> flatMap_Stream(void* _ref, Function<T, Stream<R>> mapper) {
	Stream<T> _this = *((Stream*) _ref);
	return new_Stream<R>(new_FlatMapHead<T, R>(_this.head, mapper));
}
template <typename T, typename R>
Stream<Tuple<T, R>> zip_Stream(void* _ref, Stream<R> other) {
	Stream<T> _this = *((Stream*) _ref);
	return new_Stream<Tuple<T, R>>(new_ZipHead<T, R>(_this.head, other.head));
}
template <typename T>
ArrayList<T> new_ArrayList() {
	ArrayList<T> _this;
	return _this;
}
template <typename T>
ArrayList<T> new_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	_this.inner = new_java.util.ArrayList<T>();
}
template <typename T, typename T>
ArrayList<T> of_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	/*Not a function type: Placeholder[input=new_ArrayList<T>]*/ current = new_ArrayList<T>();/*
			for (var i = 0; i < elements.length; i++) {
				current = current.addLast(elements[i]);
			}*/
	return current;
}
template <typename T, typename T>
ArrayList<T> empty_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return new_ArrayList<T>();
}
template <typename T>
char* toString_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_List<T>]end]*/(joining_/*Collectors*/(", ", "[", "]"));
}
template <typename T>
int size_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return size_List<T>();
}
template <typename T>
Stream<T> stream_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return new_Stream<T>(new_ListHead<T>(_this));
}
template <typename T>
ArrayList<T> addLast_ArrayList(void* _ref, T element) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	add_List<T>(element);
	return _this;
}
template <typename T>
int isEmpty_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return isEmpty_List<T>();
}
template <typename T>
ArrayList<T> copy_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return collect_/*Not a function type: Placeholder[input=stream_ArrayList]*/(new_ListCollector<T>());
}
template <typename T>
ArrayList<T> addFirst_ArrayList(void* _ref, T element) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	addFirst_List<T>(element);
	return _this;
}
template <typename T>
ArrayList<T> removeLast_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	removeLast_List<T>();
	return _this;
}
template <typename T>
T getLast_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return getLast_List<T>();
}
template <typename T>
ArrayList<T> setLast_ArrayList(void* _ref, T element) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	set_List<T>(size_List<T>() - 1, element);
	return _this;
}
template <typename T>
ArrayList<T> addAllLast_ArrayList(void* _ref, ArrayList<T> others) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return fold_/*Not a function type: Placeholder[input=stream_ArrayList<T>]*/(_this, addLast_ArrayList);
}
template <typename T>
ArrayList<T> mapLast_ArrayList(void* _ref, Function<T, T> mapper) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	if (isEmpty_ArrayList()) {
		return _this;
	}
	return setLast_ArrayList(apply_Function(getLast_ArrayList()));
}
template <typename T>
ArrayList<T> reverse_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	/*Not a function type: Placeholder[input=new_ArrayList<T>]*/ current = new_ArrayList<T>();/*
			for (var i = 0; i < this.inner.size(); i++) {
				current = current.addLast(this.inner.get(this.inner.size() - i - 1));
			}*/
	return current;
}
template <typename T, typename X>
Err<T, X> new_Err(X error) {
	Err<T, X> _this;
	_this.error = error;
	return _this;
}
template <typename T, typename X>
Result<T, X> toResult_Err(void* _ref){
	Err<T, X> _this = *((Err<T, X>*) _ref);
	ResultData<T, X> data;
	data.err = _this;
	return Result<T, X> { ErrTag, data };
}
template <typename T, typename X>
Ok<T, X> new_Ok(T value) {
	Ok<T, X> _this;
	_this.value = value;
	return _this;
}
template <typename T, typename X>
Result<T, X> toResult_Ok(void* _ref){
	Ok<T, X> _this = *((Ok<T, X>*) _ref);
	ResultData<T, X> data;
	data.ok = _this;
	return Result<T, X> { OkTag, data };
}
template <typename T>
Some<T> new_Some(T value) {
	Some<T> _this;
	_this.value = value;
	return _this;
}
template <typename T>
Option<T> toOption_Some(void* _ref){
	Some<T> _this = *((Some<T>*) _ref);
	OptionData<T> data;
	data.some = _this;
	return Option<T> { SomeTag, data };
}
template <typename T, typename R>
Option<R> map_Some(void* _ref, Function<T, R> mapper) {
	Some<T> _this = *((Some*) _ref);
	return new_Some<R>(apply_Function(_this.value));
}
template <typename T>
char* toString_Some(void* _ref) {
	Some<T> _this = *((Some*) _ref);
	return toString_T();
}
template <typename T>
void ifPresent_Some(void* _ref, Consumer<T> consumer) {
	Some<T> _this = *((Some*) _ref);
	accept_Consumer(_this.value);
}
template <typename T>
Option<T> or_Some(void* _ref, Supplier<Option<T>> other) {
	Some<T> _this = *((Some*) _ref);
	return _this;
}
template <typename T>
int isEmpty_Some(void* _ref) {
	Some<T> _this = *((Some*) _ref);
	return 0;
}
template <typename T>
T get_Some(void* _ref) {
	Some<T> _this = *((Some*) _ref);
	return _this.value;
}
template <typename T, typename R>
Option<R> flatMap_Some(void* _ref, Function<T, Option<R>> mapper) {
	Some<T> _this = *((Some*) _ref);
	return apply_Function(_this.value);
}
template <typename T>
T orElse_Some(void* _ref, T other) {
	Some<T> _this = *((Some*) _ref);
	return _this.value;
}
template <typename T>
T orElseGet_Some(void* _ref, Supplier<T> other) {
	Some<T> _this = *((Some*) _ref);
	return _this.value;
}
template <typename T>
int isPresent_Some(void* _ref) {
	Some<T> _this = *((Some*) _ref);
	return 1;
}
template <typename T>
Stream<T> stream_Some(void* _ref) {
	Some<T> _this = *((Some*) _ref);
	return new_Stream<T>(new_SingleHead<T>(_this.value));
}
auto _lambda3_(auto _ref, auto otherValue) {
	auto _this = _ref;
	return new_Tuple<T, R>(_this.value, /*otherValue*/);
};
template <typename T, typename R>
Option<Tuple<T, R>> and_Some(void* _ref, Supplier<Option<R>> other) {
	Some<T> _this = *((Some*) _ref);
	return map_/*Not a function type: Placeholder[input=get_Supplier]*/(_lambda3_);
}
template <typename T>
None<T> new_None() {
	None<T> _this;
	return _this;
}
template <typename T>
Option<T> toOption_None(void* _ref){
	None<T> _this = *((None<T>*) _ref);
	OptionData<T> data;
	data.none = _this;
	return Option<T> { NoneTag, data };
}
template <typename T, typename R>
Option<R> map_None(void* _ref, Function<T, R> mapper) {
	None<T> _this = *((None*) _ref);
	return new_None<R>();
}
template <typename T>
void ifPresent_None(void* _ref, Consumer<T> consumer) {
	None<T> _this = *((None*) _ref);
}
template <typename T>
Option<T> or_None(void* _ref, Supplier<Option<T>> other) {
	None<T> _this = *((None*) _ref);
	return get_Supplier();
}
template <typename T>
int isEmpty_None(void* _ref) {
	None<T> _this = *((None*) _ref);
	return 1;
}
template <typename T>
T get_None(void* _ref) {
	None<T> _this = *((None*) _ref);
	return /*null*/;
}
template <typename T, typename R>
Option<R> flatMap_None(void* _ref, Function<T, Option<R>> mapper) {
	None<T> _this = *((None*) _ref);
	return new_None<R>();
}
template <typename T>
T orElse_None(void* _ref, T other) {
	None<T> _this = *((None*) _ref);
	return other;
}
template <typename T>
T orElseGet_None(void* _ref, Supplier<T> other) {
	None<T> _this = *((None*) _ref);
	return get_Supplier();
}
template <typename T>
int isPresent_None(void* _ref) {
	None<T> _this = *((None*) _ref);
	return 0;
}
template <typename T>
Stream<T> stream_None(void* _ref) {
	None<T> _this = *((None*) _ref);
	return new_Stream<T>(new_EmptyHead<T>());
}
template <typename T, typename R>
Option<Tuple<T, R>> and_None(void* _ref, Supplier<Option<R>> other) {
	None<T> _this = *((None*) _ref);
	return new_None<Tuple<T, R>>();
}
CPointerType new_CPointerType(CType type) {
	CPointerType _this;
	_this.type = type;
	return _this;
}
CType toCType_CPointerType(void* _ref){
	CPointerType _this = *((CPointerType*) _ref);
	CTypeData data;
	data.cpointertype = _this;
	return CType { CPointerTypeTag, data };
}
char* generate_CPointerType(void* _ref) {
	CPointerType _this = *((CPointerType*) _ref);
	return generate_CType() + "*";
}
char* getSimpleName_CPointerType(void* _ref) {
	CPointerType _this = *((CPointerType*) _ref);
	return getSimpleName_CType() + "_ref";
}
CType replaceIdentifiersWithMapping_CPointerType(void* _ref, HashMap<char*, CType> mapping) {
	CPointerType _this = *((CPointerType*) _ref);
	return new_CPointerType(replaceIdentifiersWithMapping_CType(mapping));
}
CTemplateType new_CTemplateType(char* base, ArrayList<CType> typeArguments) {
	CTemplateType _this;
	_this.base = base;
	_this.typeArguments = typeArguments;
	return _this;
}
CType toCType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CTypeData data;
	data.ctemplatetype = _this;
	return CType { CTemplateTypeTag, data };
}
char* generate_CTemplateType(void* _ref) {
	CTemplateType _this = *((CTemplateType*) _ref);
	/*Not a function type: Placeholder[input=stream_ArrayList<CType>]*/ stream = stream_ArrayList<CType>();
	/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<CType>]end]*/ map = map_/*Not a function type: Placeholder[input=stream_ArrayList<CType>]*/(generate_CType);
	/*Not a function type: Placeholder[input=new_Joiner]*/ collector = new_Joiner(", ");
	/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<CType>]end]end]*/ joined = collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<CType>]end]*/(collector);
	char* s;
	if (isEmpty_ArrayList<CType>()) {
		s = "";
	}
	else {
		s = "<" + joined + ">";
	}
	return _this.base + s;
}
char* getSimpleName_CTemplateType(void* _ref) {
	CTemplateType _this = *((CTemplateType*) _ref);
	return _this.base;
}
auto _lambda7_(auto _ref, auto arg) {
	auto _this = _ref;
	return replaceIdentifiersWithMapping_/*arg*/(mapping);
};
CType replaceIdentifiersWithMapping_CTemplateType(void* _ref, HashMap<char*, CType> mapping) {
	CTemplateType _this = *((CTemplateType*) _ref);
	/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<CType>]end]end]*/ collect = collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<CType>]end]*/(new_ListCollector<CType>());
	return new_CTemplateType(_this.base, collect);
}
CIdentifier new_CIdentifier(char* value) {
	CIdentifier _this;
	_this.value = value;
	return _this;
}
char* generate_CIdentifier(void* _ref) {
	CIdentifier _this = *((CIdentifier*) _ref);
	return _this.value;
}
char* getSimpleName_CIdentifier(void* _ref) {
	CIdentifier _this = *((CIdentifier*) _ref);
	return _this.value;
}
CType replaceIdentifiersWithMapping_CIdentifier(void* _ref, HashMap<char*, CType> mapping) {
	CIdentifier _this = *((CIdentifier*) _ref);
	return orElse_/*Not a function type: Placeholder[input=get_HashMap<char*, CType>]*/(_this);
}
Placeholder new_Placeholder(char* input) {
	Placeholder _this;
	_this.input = input;
	return _this;
}
char* wrap_Placeholder(void* _ref, char* input) {
	Placeholder _this = *((Placeholder*) _ref);
	/*Not a function type: Placeholder[input=replace_char*]*/ withoutStart = replace_char*("/*", "start");
	/*Not a function type: Placeholder[input=replace_startNot a function type: Placeholder[input=replace_char*]end]*/ withoutEnd = replace_/*Not a function type: Placeholder[input=replace_char*]*/("*/", "end");
	return "/*" + withoutEnd + "*/";
}
char* generate_Placeholder(void* _ref) {
	Placeholder _this = *((Placeholder*) _ref);
	return /*wrap*/(_this.input);
}
char* getSimpleName_Placeholder(void* _ref) {
	Placeholder _this = *((Placeholder*) _ref);
	return generate_Placeholder();
}
CType replaceIdentifiersWithMapping_Placeholder(void* _ref, HashMap<char*, CType> mapping) {
	Placeholder _this = *((Placeholder*) _ref);
	return _this;
}
Option<CDefinition> toDefinition_Placeholder(void* _ref) {
	Placeholder _this = *((Placeholder*) _ref);
	return new_None<CDefinition>();
}
template <typename A, typename B>
Tuple<A, B> new_Tuple(A left, B right) {
	Tuple<A, B> _this;
	_this.left = left;
	_this.right = right;
	return _this;
}
State new_State() {
	State _this;
	return _this;
}
State new_State(void* _ref, char* input) {
	State _this = *((State*) _ref);
	_this.input = input;
	_this.buffer = "";
	_this.depth = 0;
	_this.segments = new_ArrayList<char*>();
	_this.index = 0;
}
State enter_State(void* _ref) {
	State _this = *((State*) _ref);
	_this.depth = _this.depth + 1;
	return _this;
}
State exit_State(void* _ref) {
	State _this = *((State*) _ref);
	_this.depth = _this.depth - 1;
	return _this;
}
State advance_State(void* _ref) {
	State _this = *((State*) _ref);
	_this.segments = addLast_ArrayList<char*>(_this.buffer);
	_this.buffer = "";
	return _this;
}
int isShallow_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.depth == 1;
}
State append_State(void* _ref, char c) {
	State _this = *((State*) _ref);
	_this.buffer +  = c;
	return _this;
}
int isLevel_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.depth == 0;
}
Option<char> pop_State(void* _ref) {
	State _this = *((State*) _ref);
	if (length_/*Does not have a type of structure: starttypeof(_this.index < _this)end*/()) {
		int counter = _this.index;
		_this.index++;
		/*Not a function type: Placeholder[input=charAt_char*]*/ element = charAt_char*(counter);
		return new_Some<char>(element);
	}
	else {
		return new_None<char>();
	}
}
Stream<char*> stream_State(void* _ref) {
	State _this = *((State*) _ref);
	return stream_ArrayList<char*>();
}
auto _lambda9_(auto _ref, auto next) {
		/*Not a function type: Placeholder[input=append_State]*/ appended = append_State(/*next*/);
		return new_Tuple<char, State>(/*next*/, appended);
	}Option<Tuple<char, State>> popAndAppendToTuple_State(void* _ref) {
	State _this = *((State*) _ref);
	return map_/*Not a function type: Placeholder[input=pop_State]*/(_lambda9_);
}
Option<State> popAndAppendToOption_State(void* _ref) {
	State _this = *((State*) _ref);
	return map_/*Not a function type: Placeholder[input=popAndAppendToTuple_State]*/(right_Tuple);
}
char peek_State(void* _ref) {
	State _this = *((State*) _ref);
	return charAt_char*(_this.index);
}
CDefinition new_CDefinition(ArrayList<char*> typeParameters, CType type, char* name) {
	CDefinition _this;
	_this.typeParameters = typeParameters;
	_this.type = type;
	_this.name = name;
	return _this;
}
char* generate_CDefinition(void* _ref) {
	CDefinition _this = *((CDefinition*) _ref);
	return name_/*typeof(generate_startNot a function type: Placeholder[input=type_CDefinition]end() + " " + _this)*/();
}
char* toString_CDefinition(void* _ref) {
	CDefinition _this = *((CDefinition*) _ref);
	return generate_CDefinition();
}
CDefinition withType_CDefinition(void* _ref, CType type) {
	CDefinition _this = *((CDefinition*) _ref);
	return new_CDefinition(_this.typeParameters, type, _this.name);
}
CDefinition mapType_CDefinition(void* _ref, Function<CType, CType> mapper) {
	CDefinition _this = *((CDefinition*) _ref);
	return new_CDefinition(_this.typeParameters, apply_Function(_this.type), _this.name);
}
CStructureHeader new_CStructureHeader(ArrayList<char*> typeParameters, char* name) {
	CStructureHeader _this;
	_this.typeParameters = typeParameters;
	_this.name = name;
	return _this;
}
CType toType_CStructureHeader(void* _ref) {
	CStructureHeader _this = *((CStructureHeader*) _ref);
	if (isEmpty_ArrayList<char*>()) {
		return new_CIdentifier(_this.name);
	}
	/*Not a function type: Placeholder[input=toList_startNot a function type: Placeholder[input=typeof(stream_ArrayList<char*>(). < startCType>mapend)]end]*/ list = toList_/*Not a function type: Placeholder[input=typeof(stream_ArrayList<char*>(). < startCType>mapend)]*/();
	return new_CTemplateType(_this.name, list);
}
char* generate_CStructureHeader(void* _ref) {
	CStructureHeader _this = *((CStructureHeader*) _ref);
	return name_/*typeof(createTemplateString_startAppend(typeParameters_CStructureHeader()) + "struct " + _this)*/();
}
CStructureType withFields_CStructureHeader(void* _ref, ArrayList<CDefinition> fields) {
	CStructureHeader _this = *((CStructureHeader*) _ref);
	return new_CStructureType(_this.name, _this.typeParameters, fields);
}
CStructure new_CStructure(CStructureHeader CStructureHeader, char* fields) {
	CStructure _this;
	_this.CStructureHeader = CStructureHeader;
	_this.fields = fields;
	return _this;
}
char* generate_CStructure(void* _ref) {
	CStructure _this = *((CStructure*) _ref);
	return generate_/*Not a function type: Placeholder[input=CStructureHeader_CStructure]*/() + " {" + this.fields() + System.lineSeparator() + "};";
}
template <typename T, typename R>
MapHead<T, R> new_MapHead() {
	MapHead<T, R> _this;
	return _this;
}
template <typename T, typename R>
Head<R> toHead_MapHead(void* _ref){
	MapHead<T, R> _this = *((MapHead<T, R>*) _ref);
	HeadData<T, R> data;
	data.maphead = _this;
	return Head<R> { MapHeadTag, data };
}
template <typename T, typename R>
MapHead<T, R> new_MapHead(void* _ref, Head<T> head, Function<T, R> mapper) {
	MapHead<T, R> _this = *((MapHead*) _ref);
	_this.mapper = mapper;
	_this.head = head;
}
template <typename T, typename R>
Option<R> next_MapHead(void* _ref) {
	MapHead<T, R> _this = *((MapHead*) _ref);
	return map_/*Not a function type: Placeholder[input=next_Head<T>]*/(_this.mapper);
}
template <typename T>
ListHead<T> new_ListHead() {
	ListHead<T> _this;
	return _this;
}
template <typename T>
Head<T> toHead_ListHead(void* _ref){
	ListHead<T> _this = *((ListHead<T>*) _ref);
	HeadData<T> data;
	data.listhead = _this;
	return Head<T> { ListHeadTag, data };
}
template <typename T>
ListHead<T> new_ListHead(void* _ref, ArrayList<T> list) {
	ListHead<T> _this = *((ListHead*) _ref);
	_this.list = list;
	_this.counter = 0;
}
template <typename T>
Option<T> next_ListHead(void* _ref) {
	ListHead<T> _this = *((ListHead*) _ref);
	if (size_/*Does not have a type of structure: starttypeof(_this.counter < _this)end*/()) {
		/*Not a function type: Placeholder[input=get_startDoes not have a type of structure: ArrayList<T>end]*/ element = get_/*Does not have a type of structure: ArrayList<T>*/(_this.counter);
		_this.counter++;
		return new_Some<T>(element);
	}
	return new_None<T>();
}
template <typename T>
ListCollector<T> new_ListCollector() {
	ListCollector<T> _this;
	return _this;
}
template <typename T>
Collector<T, ArrayList<T>> toCollector_ListCollector(void* _ref){
	ListCollector<T> _this = *((ListCollector<T>*) _ref);
	CollectorData<T> data;
	data.listcollector = _this;
	return Collector<T, ArrayList<T>> { ListCollectorTag, data };
}
template <typename T>
ArrayList<T> createInitial_ListCollector(void* _ref) {
	ListCollector<T> _this = *((ListCollector*) _ref);
	return new_ArrayList<T>();
}
template <typename T>
ArrayList<T> fold_ListCollector(void* _ref, ArrayList<T> current, T element) {
	ListCollector<T> _this = *((ListCollector*) _ref);
	return addLast_ArrayList(element);
}
template <typename T, typename R>
FlatMapHead<T, R> new_FlatMapHead() {
	FlatMapHead<T, R> _this;
	return _this;
}
template <typename T, typename R>
Head<R> toHead_FlatMapHead(void* _ref){
	FlatMapHead<T, R> _this = *((FlatMapHead<T, R>*) _ref);
	HeadData<T, R> data;
	data.flatmaphead = _this;
	return Head<R> { FlatMapHeadTag, data };
}
template <typename T, typename R>
FlatMapHead<T, R> new_FlatMapHead(void* _ref, Head<T> head, Function<T, Stream<R>> mapper) {
	FlatMapHead<T, R> _this = *((FlatMapHead*) _ref);
	_this.head = head;
	_this.mapper = mapper;
	_this.current = new_EmptyHead<R>();
}
template <typename T, typename R>
Option<R> next_FlatMapHead(void* _ref) {
	FlatMapHead<T, R> _this = *((FlatMapHead*) _ref);
	while (1) {
		/*Not a function type: Placeholder[input=next_Head<R>]*/ maybeNext = next_Head<R>();
		if (isPresent_/*Not a function type: Placeholder[input=next_Head<R>]*/()) {
			return maybeNext;
		}
		/*Not a function type: Placeholder[input=next_Head<T>]*/ maybeOuter = next_Head<T>();
		if (isEmpty_/*Not a function type: Placeholder[input=next_Head<T>]*/()) {
			return new_None<R>();
		}
		_this.current = apply_Function<T, Stream<R>>(get_/*Not a function type: Placeholder[input=next_Head<T>]*/()).head;
	}
}
Joiner new_Joiner(char* delimiter) {
	Joiner _this;
	_this.delimiter = delimiter;
	return _this;
}
Collector<char*, char*> toCollector_Joiner(void* _ref){
	Joiner _this = *((Joiner*) _ref);
	CollectorData data;
	data.joiner = _this;
	return Collector<char*, char*> { JoinerTag, data };
}
Joiner new_Joiner(void* _ref) {
	Joiner _this = *((Joiner*) _ref);
	_this("");
}
char* createInitial_Joiner(void* _ref) {
	Joiner _this = *((Joiner*) _ref);
	return "";
}
char* fold_Joiner(void* _ref, char* current, char* element) {
	Joiner _this = *((Joiner*) _ref);
	if (isEmpty_char*()) {
		return element;
	}
	return current + _this.delimiter + element;
}
EmptyCStructureSegment new_EmptyCStructureSegment() {
	EmptyCStructureSegment _this;
	return _this;
}
CStructureSegment toCStructureSegment_EmptyCStructureSegment(void* _ref){
	EmptyCStructureSegment _this = *((EmptyCStructureSegment*) _ref);
	CStructureSegmentData data;
	data.emptycstructuresegment = _this;
	return CStructureSegment { EmptyCStructureSegmentTag, data };
}
char* generate_EmptyCStructureSegment(void* _ref) {
	EmptyCStructureSegment _this = *((EmptyCStructureSegment*) _ref);
	return "";
}
Option<CDefinition> toDefinition_EmptyCStructureSegment(void* _ref) {
	EmptyCStructureSegment _this = *((EmptyCStructureSegment*) _ref);
	return new_None<CDefinition>();
}
CContent new_CContent(char* content) {
	CContent _this;
	_this.content = content;
	return _this;
}
char* generate_CContent(void* _ref) {
	CContent _this = *((CContent*) _ref);
	return _this.content;
}
CStatement new_CStatement(CNode content, int depth) {
	CStatement _this;
	_this.content = content;
	_this.depth = depth;
	return _this;
}
CStructureSegment toCStructureSegment_CStatement(void* _ref){
	CStatement _this = *((CStatement*) _ref);
	CStructureSegmentData data;
	data.cstatement = _this;
	return CStructureSegment { CStatementTag, data };
}
char* generate_CStatement(void* _ref) {
	CStatement _this = *((CStatement*) _ref);
	return generateWithIndent_/*App*/(generate_/*Not a function type: Placeholder[input=content_CStatement]*/(), depth_CStatement()) + ";";
}
Option<CDefinition> toDefinition_CStatement(void* _ref) {
	CStatement _this = *((CStatement*) _ref);
	if (/*this.content instanceof CDefinition definition*/) {
		return new_Some<CDefinition>(/*definition*/);
	}
	else {
		return new_None<CDefinition>();
	}
}
CFieldAccess new_CFieldAccess(CExpression child, char* name) {
	CFieldAccess _this;
	_this.child = child;
	_this.name = name;
	return _this;
}
CExpression toCExpression_CFieldAccess(void* _ref){
	CFieldAccess _this = *((CFieldAccess*) _ref);
	CExpressionData data;
	data.cfieldaccess = _this;
	return CExpression { CFieldAccessTag, data };
}
char* generate_CFieldAccess(void* _ref) {
	CFieldAccess _this = *((CFieldAccess*) _ref);
	return generate_CExpression() + "." + _this.name;
}
Frames new_Frames() {
	Frames _this;
	return _this;
}
Frames new_Frames(void* _ref) {
	Frames _this = *((Frames*) _ref);
	_this.frames = empty_/*ArrayList*/();
}
auto _lambda11_(auto _ref, auto last) {
	auto _this = _ref;
	return defineAll_/*last*/(params);
};
Frames defineAll_Frames(void* _ref, ArrayList<CDefinition> params) {
	Frames _this = *((Frames*) _ref);
	_this.frames = mapLast_ArrayList<Frame>(_lambda11_);
	return _this;
}
template <typename T>
Tuple<T, Frames> within_Frames(void* _ref, Supplier<T> mapper) {
	Frames _this = *((Frames*) _ref);
	_this.frames = addLast_ArrayList<Frame>(new_Frame());
	/*Not a function type: Placeholder[input=get_Supplier]*/ result = get_Supplier();
	_this.frames = removeLast_ArrayList<Frame>();
	return new_Tuple<T, Frames>(result, _this);
}
auto _lambda13_(auto _ref, auto last) {
	auto _this = _ref;
	return define_/*last*/(definition);
};
Frames define_Frames(void* _ref, CDefinition definition) {
	Frames _this = *((Frames*) _ref);
	_this.frames = mapLast_ArrayList<Frame>(_lambda13_);
	return _this;
}
auto _lambda20_(auto _ref, auto frame) {
	auto _this = _ref;
	return resolve_/*frame*/(name);
};
Option<CDefinition> resolve_Frames(void* _ref, char* name) {
	Frames _this = *((Frames*) _ref);
	return next_/*Does not have a type of structure: startNot a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<Frame>]end]end]end*/();
}
auto _lambda22_(auto _ref, auto last) {
	auto _this = _ref;
	return withHeader_/*last*/(header);
};
Frames withStructureHeader_Frames(void* _ref, CStructureHeader header) {
	Frames _this = *((Frames*) _ref);
	_this.frames = mapLast_ArrayList<Frame>(_lambda22_);
	return _this;
}
auto _lambda28_(auto _ref, auto header) {
	auto _this = _ref;
	return /*header*/.typeParameters;
};
ArrayList<char*> collectTypeParameters_Frames(void* _ref) {
	Frames _this = *((Frames*) _ref);
	return collect_/*Not a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=streamHeaders_Frames]end]end]*/(new_ListCollector<char*>());
}
auto _lambda35_(auto _ref, auto frame) {
	auto _this = _ref;
	return /*frame*/.maybeHeader;
};
Option<CStructureHeader> findCurrentStructure_Frames(void* _ref) {
	Frames _this = *((Frames*) _ref);
	return next_/*Does not have a type of structure: startNot a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=reverse_startNot a function type: Placeholder[input=copy_ArrayList<Frame>]end]end]end]end]end*/();
}
auto _lambda39_(auto _ref, auto frame) {
	auto _this = _ref;
	return /*frame*/.maybeHeader;
};
Stream<CStructureHeader> streamHeaders_Frames(void* _ref) {
	Frames _this = *((Frames*) _ref);
	return flatMap_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<Frame>]end]*/(stream_Option);
}
auto _lambda48_(auto _ref, auto header) {
	auto _this = _ref;
	return new_Tuple<CStructureHeader, ArrayList<CDefinition>>(/*header*/, /*frame*/.definitions);
};
auto _lambda46_(auto _ref, auto frame) {
	auto _this = _ref;
	return map_/*Does not have a type of structure: startframeend*/(_lambda48_);
};
Option<Tuple<CStructureHeader, ArrayList<CDefinition>>> findCurrentScope_Frames(void* _ref) {
	Frames _this = *((Frames*) _ref);
	return next_/*Does not have a type of structure: startNot a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=reverse_startNot a function type: Placeholder[input=copy_ArrayList<Frame>]end]end]end]end]end*/();
}
auto _lambda55_(auto _ref, auto frame) {
	auto _this = _ref;
	return findStructure_/*frame*/(structName);
};
Option<CStructureType> findStructure_Frames(void* _ref, char* structName) {
	Frames _this = *((Frames*) _ref);
	return next_/*Does not have a type of structure: startNot a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<Frame>]end]end]end*/();
}
auto _lambda57_(auto _ref, auto last) {
	auto _this = _ref;
	return defineStructure_/*last*/(type);
};
Frames defineStructure_Frames(void* _ref, CStructureType type) {
	Frames _this = *((Frames*) _ref);
	_this.frames = mapLast_ArrayList<Frame>(_lambda57_);
	return _this;
}
template <typename K, typename V>
HashMap<K, V> new_HashMap(java.util.HashMap<K, V> internal) {
	HashMap<K, V> _this;
	_this.internal = internal;
	return _this;
}
template <typename K, typename V>
HashMap<K, V> new_HashMap(void* _ref) {
	HashMap<K, V> _this = *((HashMap*) _ref);
	_this(new_java.util.HashMap<K, V>());
}
template <typename K, typename V>
HashMap<K, V> with_HashMap(void* _ref, K key, V value) {
	HashMap<K, V> _this = *((HashMap*) _ref);
	put_java.util.HashMap<K, V>(key, value);
	return _this;
}
template <typename K, typename V>
Option<V> get_HashMap(void* _ref, K key) {
	HashMap<K, V> _this = *((HashMap*) _ref);
	if (containsKey_java.util.HashMap<K, V>(key)) {
		return new_Some<V>(get_java.util.HashMap<K, V>(key));
	}
	return new_None<V>();
}
template <typename K, typename V>
MapCollector<K, V> new_MapCollector() {
	MapCollector<K, V> _this;
	return _this;
}
template <typename K, typename V>
Collector<Tuple<K, V>, HashMap<K, V>> toCollector_MapCollector(void* _ref){
	MapCollector<K, V> _this = *((MapCollector<K, V>*) _ref);
	CollectorData<K, V> data;
	data.mapcollector = _this;
	return Collector<Tuple<K, V>, HashMap<K, V>> { MapCollectorTag, data };
}
template <typename K, typename V>
HashMap<K, V> createInitial_MapCollector(void* _ref) {
	MapCollector<K, V> _this = *((MapCollector*) _ref);
	return new_HashMap<K, V>();
}
template <typename K, typename V>
HashMap<K, V> fold_MapCollector(void* _ref, HashMap<K, V> current, Tuple<K, V> element) {
	MapCollector<K, V> _this = *((MapCollector*) _ref);
	return with_HashMap(element.left, element.right);
}
CStructureType new_CStructureType(char* name, ArrayList<char*> typeParameters, ArrayList<CDefinition> fields) {
	CStructureType _this;
	_this.name = name;
	_this.typeParameters = typeParameters;
	_this.fields = fields;
	return _this;
}
CType toCType_CStructureType(void* _ref){
	CStructureType _this = *((CStructureType*) _ref);
	CTypeData data;
	data.cstructuretype = _this;
	return CType { CStructureTypeTag, data };
}
char* generate_CStructureType(void* _ref) {
	CStructureType _this = *((CStructureType*) _ref);
	return _this.name;
}
char* getSimpleName_CStructureType(void* _ref) {
	CStructureType _this = *((CStructureType*) _ref);
	return _this.name;
}
auto _lambda63_(auto _ref, auto type) {
	auto _this = _ref;
	return replaceIdentifiersWithMapping_/*type*/(mapping);
};
auto _lambda61_(auto _ref, auto field) {
	auto _this = _ref;
	return mapType_/*field*/(_lambda63_);
};
CType replaceIdentifiersWithMapping_CStructureType(void* _ref, HashMap<char*, CType> mapping) {
	CStructureType _this = *((CStructureType*) _ref);
	/*Not a function type: Placeholder[input=toList_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<CDefinition>]end]end]*/ list = toList_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<CDefinition>]end]*/();
	return new_CStructureType(_this.name, _this.typeParameters, list);
}
auto _lambda68_(auto _ref, auto field) {
	auto _this = _ref;
	return /*field*/.type;
};
auto _lambda71_(auto _ref, auto field) {
	auto _this = _ref;
	return equals_/*Does not have a type of structure: startfieldend*/(name);
};
Option<CType> findField_CStructureType(void* _ref, char* name) {
	CStructureType _this = *((CStructureType*) _ref);
	return next_/*Does not have a type of structure: startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=stream_ArrayList<CDefinition>]end]end]end*/();
}
auto _lambda77_(auto _ref, auto type) {
	auto _this = _ref;
	return replaceIdentifiersWithMapping_/*type*/(mapping);
};
auto _lambda75_(auto _ref, auto field) {
	auto _this = _ref;
	return mapType_/*field*/(_lambda77_);
};
CStructureType withTypeArguments_CStructureType(void* _ref, ArrayList<CType> typeArguments) {
	CStructureType _this = *((CStructureType*) _ref);
	/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=zip_startNot a function type: Placeholder[input=stream_ArrayList<char*>]end]end]*/ mapping = collect_/*Not a function type: Placeholder[input=zip_startNot a function type: Placeholder[input=stream_ArrayList<char*>]end]*/(new_MapCollector<char*, CType>());
	/*Not a function type: Placeholder[input=toList_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<CDefinition>]end]end]*/ newFields = toList_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<CDefinition>]end]*/();
	return new_CStructureType(_this.name, empty_/*ArrayList*/(), newFields);
}
CConstruction new_CConstruction(CType type) {
	CConstruction _this;
	_this.type = type;
	return _this;
}
CCaller toCCaller_CConstruction(void* _ref){
	CConstruction _this = *((CConstruction*) _ref);
	CCallerData data;
	data.cconstruction = _this;
	return CCaller { CConstructionTag, data };
}
char* generate_CConstruction(void* _ref) {
	CConstruction _this = *((CConstruction*) _ref);
	return generate_/*Not a function type: Placeholder[input=type_starttypeof("new_" + _this)end]*/();
}
CInvocation new_CInvocation(CCaller caller, ArrayList<CExpression> arguments) {
	CInvocation _this;
	_this.caller = caller;
	_this.arguments = arguments;
	return _this;
}
CExpression toCExpression_CInvocation(void* _ref){
	CInvocation _this = *((CInvocation*) _ref);
	CExpressionData data;
	data.cinvocation = _this;
	return CExpression { CInvocationTag, data };
}
char* generate_CInvocation(void* _ref) {
	CInvocation _this = *((CInvocation*) _ref);
	/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=arguments_CInvocation]end]end]end]*/ joinedArguments = collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=arguments_CInvocation]end]end]*/(new_Joiner(", "));
	return generate_/*Not a function type: Placeholder[input=caller_CInvocation]*/() + "(" + joinedArguments + ")";
}
CMethodMember new_CMethodMember(CDefinition definition) {
	CMethodMember _this;
	_this.definition = definition;
	return _this;
}
CStructureMember toCStructureMember_CMethodMember(void* _ref){
	CMethodMember _this = *((CMethodMember*) _ref);
	CStructureMemberData data;
	data.cmethodmember = _this;
	return CStructureMember { CMethodMemberTag, data };
}
Option<CDefinition> toDefinition_CMethodMember(void* _ref) {
	CMethodMember _this = *((CMethodMember*) _ref);
	return new_Some<CDefinition>(_this.definition);
}
CFunctionType new_CFunctionType(CType returnType, ArrayList<CType> paramTypes) {
	CFunctionType _this;
	_this.returnType = returnType;
	_this.paramTypes = paramTypes;
	return _this;
}
CType toCType_CFunctionType(void* _ref){
	CFunctionType _this = *((CFunctionType*) _ref);
	CTypeData data;
	data.cfunctiontype = _this;
	return CType { CFunctionTypeTag, data };
}
char* generate_CFunctionType(void* _ref) {
	CFunctionType _this = *((CFunctionType*) _ref);
	return "???";
}
char* getSimpleName_CFunctionType(void* _ref) {
	CFunctionType _this = *((CFunctionType*) _ref);
	return "???";
}
auto _lambda81_(auto _ref, auto type) {
	auto _this = _ref;
	return replaceIdentifiersWithMapping_/*type*/(mapping);
};
CType replaceIdentifiersWithMapping_CFunctionType(void* _ref, HashMap<char*, CType> mapping) {
	CFunctionType _this = *((CFunctionType*) _ref);
	/*Not a function type: Placeholder[input=toList_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<CType>]end]end]*/ replacedParamTypes = toList_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList<CType>]end]*/();
	return new_CFunctionType(replaceIdentifiersWithMapping_CType(mapping), replacedParamTypes);
}
Frame new_Frame(Option<CStructureHeader> maybeHeader, ArrayList<CDefinition> definitions, ArrayList<CStructureType> structures) {
	Frame _this;
	_this.maybeHeader = maybeHeader;
	_this.definitions = definitions;
	_this.structures = structures;
	return _this;
}
Frame new_Frame(void* _ref) {
	Frame _this = *((Frame*) _ref);
	_this(new_None<CStructureHeader>(), empty_/*ArrayList*/(), empty_/*ArrayList*/());
}
Frame defineAll_Frame(void* _ref, ArrayList<CDefinition> params) {
	Frame _this = *((Frame*) _ref);
	return fold_/*Not a function type: Placeholder[input=stream_ArrayList]*/(_this, define_Frame);
}
Frame define_Frame(void* _ref, CDefinition definition) {
	Frame _this = *((Frame*) _ref);
	if (equals_/*typeof(startdefinition.type instanceof CIdentifierend(startvar valueend) && startvalueend)*/("var")) {
		/*throw new RuntimeException*/();
	}
	return new_Frame(_this.maybeHeader, addLast_ArrayList<CDefinition>(definition), _this.structures);
}
Frame withHeader_Frame(void* _ref, CStructureHeader header) {
	Frame _this = *((Frame*) _ref);
	return new_Frame(new_Some<CStructureHeader>(header), _this.definitions, _this.structures);
}
auto _lambda86_(auto _ref, auto definition) {
	auto _this = _ref;
	return equals_/*Does not have a type of structure: startdefinitionend*/(name);
};
Option<CDefinition> resolve_Frame(void* _ref, char* name) {
	Frame _this = *((Frame*) _ref);
	return next_/*Does not have a type of structure: startNot a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=stream_ArrayList<CDefinition>]end]end*/();
}
auto _lambda91_(auto _ref, auto type) {
	auto _this = _ref;
	return equals_/*Does not have a type of structure: starttypeend*/(name);
};
Option<CStructureType> findStructure_Frame(void* _ref, char* name) {
	Frame _this = *((Frame*) _ref);
	return next_/*Does not have a type of structure: startNot a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=stream_ArrayList<CStructureType>]end]end*/();
}
Frame defineStructure_Frame(void* _ref, CStructureType type) {
	Frame _this = *((Frame*) _ref);
	return new_Frame(_this.maybeHeader, _this.definitions, addLast_ArrayList<CStructureType>(type));
}
template <typename T>
ArrayHead<T> new_ArrayHead() {
	ArrayHead<T> _this;
	return _this;
}
template <typename T>
Head<T> toHead_ArrayHead(void* _ref){
	ArrayHead<T> _this = *((ArrayHead<T>*) _ref);
	HeadData<T> data;
	data.arrayhead = _this;
	return Head<T> { ArrayHeadTag, data };
}
template <typename T>
ArrayHead<T> new_ArrayHead(void* _ref, T* array) {
	ArrayHead<T> _this = *((ArrayHead*) _ref);
	_this.array = array;
}
template <typename T>
Option<T> next_ArrayHead(void* _ref) {
	ArrayHead<T> _this = *((ArrayHead*) _ref);
	if (_this.counter >= _this.array.length) {
		return new_None<T>();
	}
	/*this.array[this.counter]*/ element = /*this.array[this.counter]*/;
	_this.counter++;
	return new_Some<T>(element);
}
App new_App(void* _ref) {
	App _this = *((App*) _ref);
	_this.globals = empty_/*ArrayList*/();
	_this.frames = new_Frames();
	_this.functions = empty_/*ArrayList*/();
	_this.forwardDeclarations = empty_/*ArrayList*/();
	_this.structures = empty_/*ArrayList*/();
	_this.sealedStructures = empty_/*ArrayList*/();
	_this.depth = 1;
	_this.counter = 0;
}
void main_App(void* _ref, char** args) {
	App _this = *((App*) _ref);
	ifPresent_/*Not a function type: Placeholder[input=run_startNot a function type: Placeholder[input=new_App]end]*/(printStackTrace_Throwable);
}
auto _lambda95_(auto _ref, auto slice) {
	auto _this = _ref;
	return "typename " + /*slice*/;
};
char* createTemplateString_App(void* _ref, ArrayList<char*> typeParameters) {
	App _this = *((App*) _ref);
	char* templateString;
	if (isEmpty_ArrayList()) {
		templateString = "";
	}
	else {
		/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList]end]end]*/ collect = collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList]end]*/(new_Joiner(", "));
		templateString = lineSeparator_/*typeof("template <" + collect + ">" + startSystemend)*/();
	}
	return templateString;
}
char* generateWithIndent_App(void* _ref, char* content, int depth) {
	App _this = *((App*) _ref);
	return generateIndent_/*App*/(depth) + content;
}
char* generateIndent_App(void* _ref, int depth) {
	App _this = *((App*) _ref);
	return repeat_/*typeof(lineSeparator_startSystemend() + "\t")*/(depth);
}
Option<IOException> run_App(void* _ref) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=get_startPathsend]*/ source = get_/*Paths*/(".", "src", "main", "java", "magma", "App.java");
	/*Not a function type: Placeholder[input=readString_App]*/ input = readString_App(source);
	return _switch97_;
}
auto _lambda99_(auto _ref) {
	auto _this = _ref;
	return compileNative_App(target);
};
Option<IOException> compilePath_App(void* _ref, Path source, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=resolveSibling_Path]*/ target = resolveSibling_Path("App.cpp");
	/*Not a function type: Placeholder[input=compile_App]*/ output = compile_App(input);
	return or_/*Not a function type: Placeholder[input=writeString_App]*/(_lambda99_);
}
Option<IOException> compileNative_App(void* _ref, Path target) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=startCommand_App]*/ clang = startCommand_App(of_/*ArrayList*/("clang", toString_/*Not a function type: Placeholder[input=toAbsolutePath_Path]*/(), "-o", "main.exe"));
	return _switch101_;
}
Option<IOException> waitForProcess_App(void* _ref, Process process) {
	App _this = *((App*) _ref);
	return _switch103_;
}
Result<Integer, IOException> waitFor_App(void* _ref, Process process) {
	App _this = *((App*) _ref);
}
Result<Process, IOException> startCommand_App(void* _ref, ArrayList<char*> command) {
	App _this = *((App*) _ref);
}
Option<IOException> writeString_App(void* _ref, Path target, char* output) {
	App _this = *((App*) _ref);
}
Result<char*, IOException> readString_App(void* _ref, Path source) {
	App _this = *((App*) _ref);
}
char* compile_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=compileStatements_App]*/ compiled = compileStatements_App(input, compileRootSegment_this);
	/*Not a function type: Placeholder[input=join_startStringend]*/ joinedForwardDeclarations = join_/*String*/("", _this.forwardDeclarations.inner);
	/*Not a function type: Placeholder[input=join_startStringend]*/ joinedFunctions = join_/*String*/("", _this.functions.inner);
	/*Not a function type: Placeholder[input=join_startStringend]*/ joinedStructures = join_/*String*/("", _this.structures.inner);
	/*Not a function type: Placeholder[input=join_startStringend]*/ joinedSealedStructures = join_/*String*/("", _this.sealedStructures.inner);
	/*Not a function type: Placeholder[input=join_startStringend]*/ joinedGlobals = join_/*String*/("", _this.globals.inner);
	return joinedForwardDeclarations + compiled + joinedStructures + joinedSealedStructures + joinedGlobals + joinedFunctions + "int main(){" + System.lineSeparator() + "\treturn " + "0;" + System.lineSeparator() +
					 "}";
}
char* compileStatements_App(void* _ref, char* input, Function<char*, char*> mapper) {
	App _this = *((App*) _ref);
	return collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=divide_App]end]*/(new_Joiner(""));
}
Stream<char*> divide_App(void* _ref, char* input, BiFunction<State, char, State> folder) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=new_State]*/ current = new_State(input);
	while (1) {
		/*Not a function type: Placeholder[input=pop_startNot a function type: Placeholder[input=new_State]end]*/ maybeNext = pop_/*Not a function type: Placeholder[input=new_State]*/();
		if (isEmpty_/*Not a function type: Placeholder[input=pop_startNot a function type: Placeholder[input=new_State]end]*/()) {
			break;
		}
		current = foldEscaped_App(current, get_/*Not a function type: Placeholder[input=pop_startNot a function type: Placeholder[input=new_State]end]*/(), folder);
	}
	return stream_/*Not a function type: Placeholder[input=advance_startNot a function type: Placeholder[input=new_State]end]*/();
}
State foldEscaped_App(void* _ref, State current, char next, BiFunction<State, char, State> folder) {
	App _this = *((App*) _ref);
	if (next == '\'') {
		return orElse_/*Not a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=popAndAppendToTuple_startNot a function type: Placeholder[input=append_State]end]end]end]*/(current);
	}
	if (next == '\"') {
		/*Not a function type: Placeholder[input=append_State]*/ current0 = append_State(next);
		while (1) {
			/*Not a function type: Placeholder[input=popAndAppendToTuple_startNot a function type: Placeholder[input=append_State]end]*/ maybeTuple = popAndAppendToTuple_/*Not a function type: Placeholder[input=append_State]*/();
			if (isEmpty_/*Not a function type: Placeholder[input=popAndAppendToTuple_startNot a function type: Placeholder[input=append_State]end]*/()) {
				break;
			}
			/*Not a function type: Placeholder[input=get_startNot a function type: Placeholder[input=popAndAppendToTuple_startNot a function type: Placeholder[input=append_State]end]end]*/ tuple = get_/*Not a function type: Placeholder[input=popAndAppendToTuple_startNot a function type: Placeholder[input=append_State]end]*/();
			current0 = tuple.right;
			/*Does not have a type of structure: startNot a function type: Placeholder[input=get_startNot a function type: Placeholder[input=popAndAppendToTuple_startNot a function type: Placeholder[input=append_State]end]end]end*/ nextInQuotes = tuple.left;
			if (nextInQuotes == '\\') {
				current0 = orElse_/*Not a function type: Placeholder[input=popAndAppendToOption_startNot a function type: Placeholder[input=append_State]end]*/(current0);
				continue;
			}
			if (nextInQuotes == '\"') {
				break;
			}
		}
		return current0;
	}
	return apply_BiFunction(current, next);
}
State foldSingleEscapeChar_App(void* _ref, Tuple<char, State> tuple) {
	App _this = *((App*) _ref);
	if (tuple.left == '\\') {
		return orElse_/*Not a function type: Placeholder[input=popAndAppendToOption_State]*/(tuple.right);
	}
	return tuple.right;
}
State foldStatement_App(void* _ref, State state, char c) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=append_State]*/ appended = append_State(c);
	if (isLevel_/*typeof(c == ';' && appended)*/()) {
		return advance_/*Not a function type: Placeholder[input=append_State]*/();
	}
	if (isShallow_/*typeof(c == '}' && appended)*/()) {
		State state1;
		if (peek_/*Not a function type: Placeholder[input=append_State]*/() == ';') {
			state1 = orElse_/*Not a function type: Placeholder[input=popAndAppendToOption_startNot a function type: Placeholder[input=append_State]end]*/(appended);
		}
		else {
			state1 = appended;
		}
		return exit_/*Not a function type: Placeholder[input=advance_State]*/();
	}/*

		if (c == '{' || c == '(') {
			return appended.enter();
		}*/
	if (c == '}' || c == ') /*') {
			return appended.exit();
		}*/
	return appended;
}
auto _lambda105_(auto _ref) {
	auto _this = _ref;
	return wrap_/*Placeholder*/(input);
};
auto _lambda108_(auto _ref, auto member) {
		if (/*member instanceof CStructureSegment segment*/) {
			return generate_/*segment*/();
		}
		else {
			return "???";
		}
	}char* compileRootSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=strip_char*]*/ stripped = strip_char*();
	if (startsWith_/*typeof(startsWith_startNot a function type: Placeholder[input=strip_char*]end("package ") || stripped)*/("import ")) {
		return "";
	}
	return orElseGet_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=parseStructure_App]end]*/(_lambda105_);
}
auto _lambda112_(auto _ref, auto slice) {
	auto _this = _ref;
	return isEmpty_/*!slice*/();
};
auto _lambda116_(auto _ref, auto slice) {
	auto _this = _ref;
	return isEmpty_/*!slice*/();
};
auto _lambda120_(auto _ref, auto content1) {
	auto _this = _ref;
	return generateWithIndent_/*App*/(/*content1*/, 1);
};
auto _lambda123_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*slice*/ + "Tag";
};
auto _lambda127_(auto _ref, auto slice) {
	auto _this = _ref;
	return lineSeparator_/*System*/() + "\t" + slice + typeArguments + " " + slice.toLowerCase() + ";";
};
auto _lambda131_(auto _ref, auto parameter) {
	auto _this = _ref;
	return lineSeparator_/*System*/() + "\t_this." + parameter.name + " = " + parameter.name + ";";
};
auto _lambda135_(auto _ref, auto slice) {
	auto _this = _ref;
	return generate_/*Not a function type: Placeholder[input=new_CStatement]*/();
};
auto _lambda137_(auto _ref) {
					_this.frames = defineAll_/*Not a function type: Placeholder[input=withStructureHeader_Frames]*/(finalRecordFields);
					/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=divide_App]end]end]*/ members = collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=divide_App]end]*/(new_ListCollector<CStructureMember>());
					/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=divide_App]end]end]end]end]end]end]*/ joinedFields = collect_/*Not a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=divide_App]end]end]end]end]end]*/(new_Joiner(""));
					/*typeof(generatedFields + joinedFields)*/ outputContent = generatedFields + joinedFields;
					/*Not a function type: Placeholder[input=lineSeparator_starttypeof(finalDependencies + generate_startNot a function type: Placeholder[input=new_CStructure]end() + startSystemend)end]*/ generated = lineSeparator_/*typeof(finalDependencies + generate_startNot a function type: Placeholder[input=new_CStructure]end() + startSystemend)*/();
					return new_Tuple<ArrayList<CStructureMember>, char*>(members, generated);
				}Option<CStructureMember> parseStructure_App(void* _ref, char* type, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=indexOf_char*]*/ classIndex = indexOf_char*(type);
	if (classIndex >= 0) {
		/*Not a function type: Placeholder[input=substring_char*]*/ afterKeyword = substring_char*(length_/*typeof(classIndex + type)*/());
		/*Not a function type: Placeholder[input=indexOf_startNot a function type: Placeholder[input=substring_char*]end]*/ contentStart = indexOf_/*Not a function type: Placeholder[input=substring_char*]*/("{");
		if (contentStart >= 0) {
			/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/ beforeContent = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]*/();
			/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/ withEnd = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]*/();
			if (endsWith_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/("}")) {
				/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/ content = substring_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/(0, length_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/() - 1);
				/*Not a function type: Placeholder[input=indexOf_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/ permitsIndex = indexOf_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/("permits");
				/*Not a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/ variants = /*ArrayList*/. < /*String>empty*/();
				if (permitsIndex >= 0) {
					/*Not a function type: Placeholder[input=split_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]end]*/ variantsArray = split_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/(quote_/*Pattern*/(","));
					beforeContent = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/();
					variants = toList_/*Not a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=new_Stream<char*>]end]end]*/();
				}
				/*Not a function type: Placeholder[input=indexOf_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/ implementsIndex = indexOf_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/("implements");
				Option<CType> maybeInterfaceType = new_None<CType>();
				if (implementsIndex >= 0) {
					/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]end]*/ slice = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/();
					/*maybeInterfaceType*/ = compileType_App(slice);
					beforeContent = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/();
				}
				/*Not a function type: Placeholder[input=typeof(startArrayListend. < startCDefinition>emptyend)]*/ recordParameters = /*ArrayList*/. < /*CDefinition>empty*/();
				if (/*beforeContent.endsWith(")"*/) /*) {
						final var slice = beforeContent.substring(0, beforeContent.length() - 1);
						final var i = slice.indexOf("(");
						if (i >= 0) {
							final var params = slice.substring(i + 1);
							beforeContent = slice.substring(0, i).strip();

							recordParameters = this.compileParametersToList(params);
						}
					}*/
				/*Not a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/ typeParameters = /*ArrayList*/. < /*String>empty*/();
				if (endsWith_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/(">")) {
					/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/ withoutEnd = substring_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/(0, length_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/() - 1);
					/*Not a function type: Placeholder[input=indexOf_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]end]*/ typeParamStart = indexOf_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/("<");
					if (typeParamStart >= 0) {
						beforeContent = substring_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/(0, typeParamStart);
						/*Not a function type: Placeholder[input=split_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]end]end]*/ typeParamsArray = split_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]end]*/(quote_/*Pattern*/(","));
						typeParameters = toList_/*Not a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=new_Stream<char*>]end]end]*/();
					}
				}
				if (isIdentifier_/*!this*/(beforeContent)) {
					return new_None<CStructureMember>();
				}
				/*Not a function type: Placeholder[input=createTemplateString_startAppend]*/ templateString = createTemplateString_/*App*/(typeParameters);
				char* dependencies;
				if (isEmpty_/*!variants*/()) {
					/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]end]end]end]end]*/ enumFields = collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]end]end]end]*/(new_Joiner(","));
					/*Not a function type: Placeholder[input=joinTypeArguments_App]*/ typeArguments = joinTypeArguments_App(typeParameters);
					/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]end]end]end]*/ unionFields = collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]end]end]*/(new_Joiner(""));
					dependencies = lineSeparator_/*typeof("enum " + beforeContent + "Tag {" + enumFields + lineSeparator_startSystemend() + "};" + lineSeparator_startSystemend() + templateString + "union " + beforeContent + "Data {" + unionFields + lineSeparator_startSystemend() + "};" + startSystemend)*/();
				}
				else {
					dependencies = "";
				}
				/*Not a function type: Placeholder[input=toList_startNot a function type: Placeholder[input=typeof(stream_startNot a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]end(). < startCType>mapend)]end]*/ types = toList_/*Not a function type: Placeholder[input=typeof(stream_startNot a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]end(). < startCType>mapend)]*/();
				/*Not a function type: Placeholder[input=new_CTemplateType]*/ thisType = new_CTemplateType(beforeContent, types);
				/*Not a function type: Placeholder[input=new_CDefinition]*/ constructorHeader = new_CDefinition(empty_/*ArrayList*/(), thisType, "new_" + beforeContent);
				/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=typeof(startArrayListend. < startCDefinition>emptyend)]end]end]end]*/ assignments = collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=typeof(startArrayListend. < startCDefinition>emptyend)]end]end]*/(new_Joiner());
				/*Not a function type: Placeholder[input=lineSeparator_starttypeof(lineSeparator_startSystemend() + "\t" + generate_startNot a function type: Placeholder[input=new_CTemplateType]end() + " _this;" + assignments + lineSeparator_startSystemend() + "\treturn _this;" + startSystemend)end]*/ constructorContent1 = lineSeparator_/*typeof(lineSeparator_startSystemend() + "\t" + generate_startNot a function type: Placeholder[input=new_CTemplateType]end() + " _this;" + assignments + lineSeparator_startSystemend() + "\treturn _this;" + startSystemend)*/();
				_this.functions = addLast_ArrayList<char*>(generateMethod_App(typeParameters, constructorHeader, constructorContent1, recordParameters));
				char* generatedFields;
				if (isEmpty_/*Not a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/()) {
					generatedFields = collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=typeof(startArrayListend. < startCDefinition>emptyend)]end]end]end]*/(new_Joiner(""));
				}
				else {
					generatedFields = generate_/*Not a function type: Placeholder[input=typeof(startnew CStatement(new CContent(beforeContentend + start"Tag tag"), 1).generate()end + startnew CStatementend)]*/();
				}
				if (isPresent_/*maybeInterfaceType*/()) {
					/*Not a function type: Placeholder[input=get_startmaybeInterfaceTypeend]*/ interfaceType = get_/*maybeInterfaceType*/();
					/*Not a function type: Placeholder[input=joinTypeArguments_App]*/ joinedTypeArguments = joinTypeArguments_App(typeParameters);
					/*typeof(beforeContent + joinedTypeArguments)*/ thisTypeString = beforeContent + joinedTypeArguments;
					_this.functions = addLast_ArrayList<char*>(lineSeparator_/*typeof(templateString + generate_startNot a function type: Placeholder[input=get_startmaybeInterfaceTypeend]end() + " to" + getSimpleName_startNot a function type: Placeholder[input=get_startmaybeInterfaceTypeend]end() + "_" + beforeContent + "(void* _ref" + "){" + startnew CStatement(new CContent(thisTypeStringend + " _this = *((" + thisTypeString + start"*) _ref)"),
															 1).generate()end + new_CStatement(startnew CContent(interfaceType.getSimpleName(end) + "Data" + joinedTypeArguments + start" data"),
															 1).generate()end + startnew CStatement(new CContent("data."end + toLowerCase_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end() + start" = _this"), 1).generate()end + startnew CStatement(new CContent(
										"return "end + generate_startNot a function type: Placeholder[input=get_startmaybeInterfaceTypeend]end() + " { " + beforeContent + "Tag, " + start"data }"), 1).generate()end + lineSeparator_startSystemend() + "}" + startSystemend)*/());
				}
				_this.forwardDeclarations = addLast_ArrayList<char*>(lineSeparator_/*typeof(templateString + "struct " + beforeContent + ";" + startSystemend)*/());
				/*Not a function type: Placeholder[input=new_CStructureHeader]*/ header = new_CStructureHeader(typeParameters, beforeContent);
				/*Not a function type: Placeholder[input=typeof(startArrayListend. < startCDefinition>emptyend)]*/ finalRecordFields = recordParameters;
				char* finalDependencies = dependencies;
				/*Not a function type: Placeholder[input=within_Frames]*/ within1 = within_Frames(_lambda137_);
				/*Does not have a type of structure: startNot a function type: Placeholder[input=within_Frames]end*/ result = within1.left;
				/*Does not have a type of structure: startDoes not have a type of structure: startNot a function type: Placeholder[input=within_Frames]endend*/ members = result.left;
				/*Does not have a type of structure: startDoes not have a type of structure: startNot a function type: Placeholder[input=within_Frames]endend*/ generated = result.right;
				/*Not a function type: Placeholder[input=toList_startNot a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startDoes not have a type of structure: startDoes not have a type of structure: startNot a function type: Placeholder[input=within_Frames]endendend]end]end]end]*/ memberDefinitions = toList_/*Not a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startDoes not have a type of structure: startDoes not have a type of structure: startNot a function type: Placeholder[input=within_Frames]endendend]end]end]*/();
				/*Not a function type: Placeholder[input=addAllLast_startNot a function type: Placeholder[input=typeof(startArrayListend. < startCDefinition>emptyend)]end]*/ allMembers = addAllLast_/*Not a function type: Placeholder[input=typeof(startArrayListend. < startCDefinition>emptyend)]*/(memberDefinitions);
				_this.frames = defineStructure_/*Does not have a type of structure: startNot a function type: Placeholder[input=within_Frames]end*/(withFields_/*Not a function type: Placeholder[input=new_CStructureHeader]*/(allMembers));
				if (isEmpty_/*Not a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/()) {
					_this.structures = addLast_ArrayList<char*>(generated);
				}
				else {
					_this.sealedStructures = addLast_ArrayList<char*>(generated);
				}
				return new_Some<CStructureMember>(new_EmptyCStructureSegment());
			}
		}
	}
	return new_None<CStructureMember>();
}
Option<char*> generateField_App(void* _ref, CStructureMember member) {
	App _this = *((App*) _ref);
	if (/*member instanceof CStructureSegment segment*/) {
		return new_Some<char*>(generate_/*segment*/());
	}
	else {
		return new_None<char*>();
	}
}
char* joinTypeArguments_App(void* _ref, ArrayList<char*> typeParameters) {
	App _this = *((App*) _ref);
	char* joinedTypeArguments;
	if (isEmpty_ArrayList()) {
		joinedTypeArguments = "";
	}
	else {
		joinedTypeArguments = "<" + String.join(", ", typeParameters.inner) + ">";
	}
	return joinedTypeArguments;
}
int isIdentifier_App(void* _ref, char* input) {
	App _this = *((App*) _ref);/*
		for (var i = 0; i < input.length(); i++) {
			final var next = input.charAt(i);
			if (Character.isLetter(next) || (i != 0 && Character.isDigit(next))) {continue;}
			return false;
		}*/
	return 1;
}
auto _lambda139_(auto _ref) {
	auto _this = _ref;
	return compileDefinitionToField0_App(slice);
};
auto _lambda141_(auto _ref) {
	auto _this = _ref;
	return new_Placeholder(input);
};
CStructureMember compileClassSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	if (isBlank_char*()) {
		return new_EmptyCStructureSegment();
	}
	/*Not a function type: Placeholder[input=parseStructure_App]*/ maybeClass = parseStructure_App("class", input);
	if (isPresent_/*Not a function type: Placeholder[input=parseStructure_App]*/()) {
		return get_/*Not a function type: Placeholder[input=parseStructure_App]*/();
	}
	/*Not a function type: Placeholder[input=parseStructure_App]*/ maybeInterface = parseStructure_App("interface", input);
	if (isPresent_/*Not a function type: Placeholder[input=parseStructure_App]*/()) {
		return get_/*Not a function type: Placeholder[input=parseStructure_App]*/();
	}
	/*Not a function type: Placeholder[input=parseStructure_App]*/ maybeRecord = parseStructure_App("record", input);
	if (isPresent_/*Not a function type: Placeholder[input=parseStructure_App]*/()) {
		return get_/*Not a function type: Placeholder[input=parseStructure_App]*/();
	}
	/*Not a function type: Placeholder[input=parseStructure_App]*/ maybeEnum = parseStructure_App("enum", input);
	if (isPresent_/*Not a function type: Placeholder[input=parseStructure_App]*/()) {
		return get_/*Not a function type: Placeholder[input=parseStructure_App]*/();
	}
	if (endsWith_char*(";")) {
		/*Not a function type: Placeholder[input=substring_char*]*/ slice = substring_char*(0, length_char*() - 1);
		/*Not a function type: Placeholder[input=or_startNot a function type: Placeholder[input=compileEnumValues_App]end]*/ maybeClassStatement = or_/*Not a function type: Placeholder[input=compileEnumValues_App]*/(_lambda139_);
		if (isPresent_/*Not a function type: Placeholder[input=or_startNot a function type: Placeholder[input=compileEnumValues_App]end]*/()) {
			return get_/*Not a function type: Placeholder[input=or_startNot a function type: Placeholder[input=compileEnumValues_App]end]*/();
		}
	}
	return orElseGet_/*Not a function type: Placeholder[input=parseMethod_App]*/(_lambda141_);
}
auto _lambda145_(auto _ref) {
					/*Not a function type: Placeholder[input=lineSeparator_starttypeof(thisDefinition + compileMethodSegments_App(content) + startSystemend)end]*/ outputContent = lineSeparator_/*typeof(thisDefinition + compileMethodSegments_App(content) + startSystemend)*/();
					return generateMethod_App(typeParameters, header, outputContent, paramsWithThis);
				}auto _lambda143_(auto _ref) {
				_this.frames = defineAll_Frames(params);
				/*Not a function type: Placeholder[input=within_Frames]*/ withBlock = within_Frames(_lambda145_);
				_this.frames = withBlock.right;
				return withBlock.left;
			}auto _lambda149_(auto _ref, auto type) {
	auto _this = _ref;
	return new_CFunctionType(/*type*/, paramTypes);
};
Option<CStructureMember> parseMethod_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=indexOf_char*]*/ paramStart = indexOf_char*("(");
	if (paramStart < 0) {
		return new_None<CStructureMember>();
	}
	/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_char*]end]*/ definition = strip_/*Not a function type: Placeholder[input=substring_char*]*/();
	/*Not a function type: Placeholder[input=substring_char*]*/ withParams = substring_char*(paramStart + 1);
	/*Not a function type: Placeholder[input=indexOf_startNot a function type: Placeholder[input=substring_char*]end]*/ paramEnd = indexOf_/*Not a function type: Placeholder[input=substring_char*]*/(")");
	if (paramEnd < 0) {
		return new_None<CStructureMember>();
	}
	/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/ inputParams = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]*/();
	/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/ withBraces = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]*/();
	/*Not a function type: Placeholder[input=parseFunctionHeader_App]*/ header = parseFunctionHeader_App(definition);
	/*Not a function type: Placeholder[input=compileParametersToList_App]*/ params = compileParametersToList_App(inputParams);
	ArrayList<char*> typeParameters;
	if (/*header instanceof CDefinition definition1*/) {
		typeParameters = addAllLast_/*Not a function type: Placeholder[input=copy_startNot a function type: Placeholder[input=collectTypeParameters_Frames]end]*/(/*definition1*/.typeParameters);
	}
	else {
		typeParameters = addAllLast_/*Not a function type: Placeholder[input=copy_startNot a function type: Placeholder[input=collectTypeParameters_Frames]end]*/(empty_/*ArrayList*/());
	}
	/*Not a function type: Placeholder[input=addFirst_startNot a function type: Placeholder[input=copy_startNot a function type: Placeholder[input=compileParametersToList_App]end]end]*/ paramsWithThis = addFirst_/*Not a function type: Placeholder[input=copy_startNot a function type: Placeholder[input=compileParametersToList_App]end]*/(new_CDefinition(empty_/*ArrayList*/(), new_CPointerType(/*CPrimitiveType*/.Void), "_ref"));
	/*Not a function type: Placeholder[input=lineSeparator_starttypeof(startcreateTemplateStringend(typeParameters) + generateHeaderWithParameters_App(header, paramsWithThis) + ";" + startSystemend)end]*/ generated = lineSeparator_/*typeof(startcreateTemplateStringend(typeParameters) + generateHeaderWithParameters_App(header, paramsWithThis) + ";" + startSystemend)*/();
	if (endsWith_/*typeof(startsWith_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end("{") && withBraces)*/("}")) {
		/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/ content = substring_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/(1, length_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/() - 1);
		/*Not a function type: Placeholder[input=findCurrentStructure_Frames]*/ maybeCurrentStructure = findCurrentStructure_Frames();
		if (/*maybeCurrentStructure instanceof Some*/ < /*CStructureHeader>*/(/*var currentStructure*/)) {
			/*Not a function type: Placeholder[input=generate_startNot a function type: Placeholder[input=new_CStatement]end]*/ thisDefinition = generate_/*Not a function type: Placeholder[input=new_CStatement]*/();
			/*Not a function type: Placeholder[input=within_Frames]*/ framesWithParams = within_Frames(_lambda143_);
			generated = framesWithParams.left;
			_this.frames = framesWithParams.right;
		}
	}
	_this.functions = addLast_ArrayList<char*>(generated);
	if (/*header instanceof CDefinition definition1*/) {
		/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=compileParametersToList_App]end]end]end]*/ paramTypes = collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_startNot a function type: Placeholder[input=compileParametersToList_App]end]end]*/(new_ListCollector<CType>());
		return new_Some<CStructureMember>(new_CMethodMember(mapType_/*definition1*/(_lambda149_)));
	}
	else {
		return new_Some<CStructureMember>(new_EmptyCStructureSegment());
	}
}
char* generateMethod_App(void* _ref, ArrayList<char*> typeParameters, CFunctionHeader header, char* content, ArrayList<CDefinition> params) {
	App _this = *((App*) _ref);
	return lineSeparator_/*typeof(startcreateTemplateStringend(typeParameters) + generateHeaderWithParameters_App(header, params) + " {" + content + "}" + startSystemend)*/();
}
char* generateHeaderWithParameters_App(void* _ref, CFunctionHeader header, ArrayList<CDefinition> params) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=collect_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList]end]end]*/ outputParams = collect_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=stream_ArrayList]end]*/(new_Joiner(", "));
	return generate_CFunctionHeader() + "(" + outputParams + ")";
}
auto _lambda151_(auto _ref) {
	auto _this = _ref;
	return new_Placeholder(input);
};
auto _lambda154_(auto _ref) {
	auto _this = _ref;
	return compileConstructor_App(input);
};
auto _lambda161_(auto _ref, auto header) {
	auto _this = _ref;
	return /*header*/.name;
};
auto _lambda157_(auto _ref, auto item) {
		/*Not a function type: Placeholder[input=orElse_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=findCurrentStructure_Frames]end]end]*/ currentStructureName = orElse_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=findCurrentStructure_Frames]end]*/("???");
		return new_CDefinition(/*item*/.typeParameters, /*item*/.type, /*item*/.name + "_" + currentStructureName);
	}CFunctionHeader parseFunctionHeader_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return orElseGet_/*Not a function type: Placeholder[input=or_startNot a function type: Placeholder[input=typeof(compileDefinition_App(input). < startCFunctionHeader>mapend)]end]*/(_lambda151_);
}
Option<CStructureMember> compileDefinitionToField0_App(void* _ref, char* slice) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=compileDefinition_App]*/ maybeDefinition = compileDefinition_App(slice);
	if (isPresent_/*!maybeDefinition*/()) {
		return new_None<CStructureMember>();
	}
	/*Not a function type: Placeholder[input=get_startNot a function type: Placeholder[input=compileDefinition_App]end]*/ definition = get_/*Not a function type: Placeholder[input=compileDefinition_App]*/();
	_this.frames = define_Frames(definition);
	return new_Some<CStructureMember>(new_CStatement(definition, 1));
}
char* compileMethodSegments_App(void* _ref, char* content) {
	App _this = *((App*) _ref);
	return compileStatements_App(content, compileMethodSegmentOrPlaceholder_this);
}
auto _lambda165_(auto _ref, auto header) {
	auto _this = _ref;
	return /*header*/.name;
};
Option<CFunctionHeader> compileConstructor_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=lastIndexOf_char*]*/ i = lastIndexOf_char*(" ");
	if (i >= 0) {
		/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_char*]end]*/ name = strip_/*Not a function type: Placeholder[input=substring_char*]*/();
		if (isIdentifier_App(name)) {
			/*Not a function type: Placeholder[input=findCurrentStructure_Frames]*/ peek0 = findCurrentStructure_Frames();
			if (/*peek0 instanceof Some*/ < /*CStructureHeader>*/(/*var peek*/)) {
				return new_Some<CFunctionHeader>(new_CDefinition(empty_/*ArrayList*/(), toType_/*peek*/(), "new_" + /*peek*/.name));
			}
		}
	}
	else {
		if (isIdentifier_App(input)) {
			/*Not a function type: Placeholder[input=orElse_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=findCurrentStructure_Frames]end]end]*/ structName = orElse_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=findCurrentStructure_Frames]end]*/("???");
			return new_Some<CFunctionHeader>(new_CDefinition(empty_/*ArrayList*/(), new_CIdentifier(structName), "new_" + structName));
		}
	}
	return new_None<CFunctionHeader>();
}
auto _lambda169_(auto _ref, auto slice) {
	auto _this = _ref;
	return isEmpty_/*!slice*/();
};
Option<CStructureMember> compileEnumValues_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=toList_startNot a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=new_Stream<char*>]end]end]end]*/ segments = toList_/*Not a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=new_Stream<char*>]end]end]*/();/*

		for (var segment : segments.inner) {
			final var stripped = segment.strip();
			final var maybeEnumValue = this.compileEnumValue(stripped);
			if (maybeEnumValue.isPresent()) {
				this.globals = this.globals.addLast(maybeEnumValue.get());
			} else {
				return new None<CStructureMember>();
			}
		}*/
	return new_Some<CStructureMember>(new_EmptyCStructureSegment());
}
Option<char*> compileEnumValue_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	if (/*stripped.endsWith(")"*/) /*) {
			final var slice = stripped.substring(0, stripped.length() - 1);
			final var i = slice.indexOf("(");
			if (i >= 0) {
				final var name = slice.substring(0, i).strip();
				final var arguments = slice.substring(i + 1);
				if (this.isIdentifier(name)) {
					final var structureName = this.frames.findCurrentStructure().map(header -> header.name).orElse("???");
					return new Some<String>(structureName + " " + name + "Value = " + structureName + " { " + arguments + " };" +
																	System.lineSeparator());
				}
			}
		}*/
	return new_None<char*>();
}
auto _lambda171_(auto _ref) {
	auto _this = _ref;
	return wrap_/*Placeholder*/(input);
};
char* compileMethodSegmentOrPlaceholder_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return orElseGet_/*Not a function type: Placeholder[input=compileMethodSegment_App]*/(_lambda171_);
}
auto _lambda173_(auto _ref) {
	auto _this = _ref;
	return compileMethodSegments_App(content);
};
Option<char*> compileMethodSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=strip_char*]*/ stripped = strip_char*();
	if (startsWith_/*typeof(isEmpty_startNot a function type: Placeholder[input=strip_char*]end() || startsWith_startNot a function type: Placeholder[input=strip_char*]end("try ") || stripped)*/("catch ")) {
		return new_Some<char*>("");
	}
	if (endsWith_/*typeof(startsWith_startNot a function type: Placeholder[input=strip_char*]end("{") && stripped)*/("}")) {
		/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/ content = substring_/*Not a function type: Placeholder[input=strip_char*]*/(1, length_/*Not a function type: Placeholder[input=strip_char*]*/() - 1);
		_this.depth++;
		/*Not a function type: Placeholder[input=within_Frames]*/ within = within_Frames(_lambda173_);
		/*Does not have a type of structure: startNot a function type: Placeholder[input=within_Frames]end*/ compiled = within.left;
		_this.frames = within.right;
		/*this.depth--*/;
		return new_Some<char*>("{" + compiled + App.generateIndent(this.depth) + "}");
	}
	/*Not a function type: Placeholder[input=compileConditional_App]*/ maybeIf = compileConditional_App(stripped, "if");
	if (isPresent_/*Not a function type: Placeholder[input=compileConditional_App]*/()) {
		return maybeIf;
	}
	/*Not a function type: Placeholder[input=compileConditional_App]*/ maybeWhile = compileConditional_App(stripped, "while");
	if (isPresent_/*Not a function type: Placeholder[input=compileConditional_App]*/()) {
		return maybeWhile;
	}
	if (endsWith_/*Not a function type: Placeholder[input=strip_char*]*/(";")) {
		/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/ slice = substring_/*Not a function type: Placeholder[input=strip_char*]*/(0, length_/*Not a function type: Placeholder[input=strip_char*]*/() - 1);
		return new_Some<char*>(generate_/*Not a function type: Placeholder[input=new_CStatement]*/());
	}
	if (startsWith_/*Not a function type: Placeholder[input=strip_char*]*/("else ")) {
		/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/ substring = substring_/*Not a function type: Placeholder[input=strip_char*]*/(5);
		return new_Some<char*>(compileMethodSegmentOrPlaceholder_/*typeof(generateIndent_startAppend(_this.depth) + "else " + _this)*/(substring));
	}
	return new_None<char*>();
}
Option<char*> compileConditional_App(void* _ref, char* input, char* type) {
	App _this = *((App*) _ref);
	if (startsWith_char*(type)) {
		/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_char*]end]*/ substring = strip_/*Not a function type: Placeholder[input=substring_char*]*/();/*
			if (substring.startsWith("(")) {
				final var withCondition = substring.substring(1);
				final var conditionEnd = this.findConditionEnd(withCondition);

				if (conditionEnd >= 0) {
					final var condition = withCondition.substring(0, conditionEnd).strip();
					final var substring2 = withCondition.substring(conditionEnd + 1).strip();
					return new Some<String>(
							App.generateIndent(this.depth) + type + " (" + this.compileExpression(condition) + ") " +
							this.compileMethodSegmentOrPlaceholder(substring2));
				}
			}*/
	}
	return new_None<char*>();
}
int findConditionEnd_App(void* _ref, char* withCondition) {
	App _this = *((App*) _ref);
	/*typeof( - 1)*/ conditionEnd =  - 1;
	/*typeof(0)*/ depth = 0;/*
		for (var i = 0; i < withCondition.length(); i++) {
			final var c = withCondition.charAt(i);
			if (c == ')') {
				depth--;
				if (depth == -1) {
					conditionEnd = i;
					break;
				}
			}

			if (c == '(') {
				depth++;
			}
		}*/
	return conditionEnd;
}
auto _lambda175_(auto _ref) {
	auto _this = _ref;
	return wrap_/*Placeholder*/(stripped);
};
auto _lambda178_(auto _ref) {
	auto _this = _ref;
	return parseAndDefineDefinitionAsStatement_App(input);
};
char* compileMethodStatement_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=strip_char*]*/ stripped = strip_char*();
	if (startsWith_/*Not a function type: Placeholder[input=strip_char*]*/("return ")) {
		/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]end]*/ slice = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/();
		return compileExpression_/*typeof("return " + _this)*/(slice);
	}
	/*Not a function type: Placeholder[input=compileAssignment_App]*/ maybeAssignment = compileAssignment_App(stripped);
	if (isPresent_/*Not a function type: Placeholder[input=compileAssignment_App]*/()) {
		return get_/*Not a function type: Placeholder[input=compileAssignment_App]*/();
	}
	if (endsWith_/*Not a function type: Placeholder[input=strip_char*]*/("++")) {
		return compileExpression_App(substring_/*Not a function type: Placeholder[input=strip_char*]*/(0, length_/*Not a function type: Placeholder[input=strip_char*]*/() - 2)) + "++";
	}
	if (equals_/*Not a function type: Placeholder[input=strip_char*]*/("break")) {
		return "break";
	}
	if (equals_/*Not a function type: Placeholder[input=strip_char*]*/("continue")) {
		return "continue";
	}
	return orElseGet_/*Not a function type: Placeholder[input=or_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=compileInvocation_App]end]end]*/(_lambda175_);
}
Option<char*> compileAssignment_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=indexOf_char*]*/ separator = indexOf_char*('=');
	if (separator < 0) {
		return new_None<char*>();
	}
	/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_char*]end]*/ destinationString = strip_/*Not a function type: Placeholder[input=substring_char*]*/();
	/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_char*]end]*/ sourceString = strip_/*Not a function type: Placeholder[input=substring_char*]*/();
	/*Not a function type: Placeholder[input=parseExpression_App]*/ source = parseExpression_App(sourceString);
	/*Not a function type: Placeholder[input=generate_starttypeof(compileAssignmentContent_App(destinationString, source) + " = " + source)end]*/ generated = generate_/*typeof(compileAssignmentContent_App(destinationString, source) + " = " + source)*/();
	return new_Some<char*>(generated);
}
char* compileAssignmentContent_App(void* _ref, char* destinationString, CExpression source) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=compileDefinition_App]*/ maybeDefinition = compileDefinition_App(destinationString);
	if (isPresent_/*!maybeDefinition*/()) {
		return compileExpression_App(destinationString);
	}
	/*Not a function type: Placeholder[input=get_startNot a function type: Placeholder[input=compileDefinition_App]end]*/ definition = get_/*Not a function type: Placeholder[input=compileDefinition_App]*/();
	if (equals_/*typeof(startdefinition.type instanceof CIdentifierend(startvar nameend) && startnameend)*/("var")) {
		/*Not a function type: Placeholder[input=resolveExpression_App]*/ newType = resolveExpression_App(source);
		/*Not a function type: Placeholder[input=withType_startNot a function type: Placeholder[input=get_startNot a function type: Placeholder[input=compileDefinition_App]end]end]*/ withNewType = withType_/*Not a function type: Placeholder[input=get_startNot a function type: Placeholder[input=compileDefinition_App]end]*/(newType);
		_this.frames = define_Frames(withNewType);
		return generate_/*Not a function type: Placeholder[input=withType_startNot a function type: Placeholder[input=get_startNot a function type: Placeholder[input=compileDefinition_App]end]end]*/();
	}
	return generate_/*Not a function type: Placeholder[input=get_startNot a function type: Placeholder[input=compileDefinition_App]end]*/();
}
CType resolveExpression_App(void* _ref, CExpression expression) {
	App _this = *((App*) _ref);
	return _switch180_;
}
CType resolveCaller_App(void* _ref, CCaller caller) {
	App _this = *((App*) _ref);
	return _switch182_;
}
CType resolveIdentifier_App(void* _ref, CIdentifier identifier) {
	App _this = *((App*) _ref);
	if (equals_/*Does not have a type of structure: CIdentifier*/("_this")) {
		/*Not a function type: Placeholder[input=findCurrentScope_Frames]*/ maybeCurrentScope = findCurrentScope_Frames();
		if (/*maybeCurrentScope instanceof Some*/(/*var currentScope*/)) {
			return new_CStructureType(/*currentScope*/.left.name, /*currentScope*/.left.typeParameters, /*currentScope*/.right);
		}
	}
	/*Not a function type: Placeholder[input=resolve_Frames]*/ maybeDefinition = resolve_Frames(identifier.value);
	if (/*maybeDefinition instanceof Some*/ < /*CDefinition>*/(/*var found*/)) {
		/*Does not have a type of structure: startfoundend*/ foundType = /*found*/.type;
		if (/*foundType instanceof CTemplateType*/(/*var base*/, /*var typeArguments*/)) {
			/*Not a function type: Placeholder[input=findStructure_Frames]*/ maybeStructureType = findStructure_Frames(/*base*/);
			if (/*maybeStructureType instanceof Some*/ < /*CStructureType>*/(/*var structureType*/)) {
				return withTypeArguments_/*structureType*/(/*typeArguments*/);
			}
		}
		return foundType;
	}
	return new_Placeholder(identifier.value);
}
Option<char*> parseAndDefineDefinitionAsStatement_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return map_/*Not a function type: Placeholder[input=parseAndDefineDefinition_App]*/(generate_CDefinition);
}
Option<CDefinition> parseAndDefineDefinition_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=compileDefinition_App]*/ maybeDefinition = compileDefinition_App(input);
	if (isPresent_/*!maybeDefinition*/()) {
		return new_None<CDefinition>();
	}
	/*Not a function type: Placeholder[input=get_startNot a function type: Placeholder[input=compileDefinition_App]end]*/ definition = get_/*Not a function type: Placeholder[input=compileDefinition_App]*/();
	_this.frames = define_Frames(definition);
	return new_Some<CDefinition>(definition);
}
char* compileExpression_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return generate_/*Not a function type: Placeholder[input=parseExpression_App]*/();
}
auto _lambda184_(auto _ref) {
	auto _this = _ref;
	return compileOperator_App(stripped, "<");
};
auto _lambda187_(auto _ref) {
	auto _this = _ref;
	return compileOperator_App(stripped, ">=");
};
auto _lambda190_(auto _ref) {
	auto _this = _ref;
	return compileOperator_App(stripped, "==");
};
auto _lambda193_(auto _ref) {
	auto _this = _ref;
	return compileOperator_App(stripped, "||");
};
auto _lambda196_(auto _ref) {
	auto _this = _ref;
	return compileOperator_App(stripped, "&&");
};
auto _lambda199_(auto _ref) {
	auto _this = _ref;
	return compileOperator_App(stripped, "-");
};
CExpression parseExpression_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=strip_char*]*/ stripped = strip_char*();
	if (equals_/*Not a function type: Placeholder[input=strip_char*]*/("false")) {
		return new_CContent("0");
	}
	if (equals_/*Not a function type: Placeholder[input=strip_char*]*/("true")) {
		return new_CContent("1");
	}
	if (endsWith_/*typeof(startsWith_startNot a function type: Placeholder[input=strip_char*]end("'") && stripped)*/("'")) {
		return new_CContent(stripped);
	}
	if (endsWith_/*typeof(startsWith_startNot a function type: Placeholder[input=strip_char*]end("\"") && stripped)*/("\"")) {
		return new_CContent(stripped);
	}
	/*Not a function type: Placeholder[input=compileLambda_App]*/ maybeLambda = compileLambda_App(stripped);
	if (isPresent_/*Not a function type: Placeholder[input=compileLambda_App]*/()) {
		return new_CContent(get_/*Not a function type: Placeholder[input=compileLambda_App]*/());
	}
	/*Not a function type: Placeholder[input=compileInvocation_App]*/ maybeInvocation = compileInvocation_App(stripped);
	if (isPresent_/*Not a function type: Placeholder[input=compileInvocation_App]*/()) {
		return get_/*Not a function type: Placeholder[input=compileInvocation_App]*/();
	}
	/*Not a function type: Placeholder[input=lastIndexOf_startNot a function type: Placeholder[input=strip_char*]end]*/ i = lastIndexOf_/*Not a function type: Placeholder[input=strip_char*]*/(".");
	if (i >= 0) {
		/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]end]*/ child = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/();
		/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]end]*/ name = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/();
		if (isIdentifier_App(name)) {
			/*Not a function type: Placeholder[input=parseExpression_App]*/ newChild = parseExpression_App(child);
			return new_CFieldAccess(newChild, name);
		}
	}
	if (isIdentifier_App(stripped)) {
		if (equals_/*Not a function type: Placeholder[input=strip_char*]*/("this")) {
			return new_CIdentifier("_this");
		}
		if (isPresent_/*Not a function type: Placeholder[input=resolve_Frames]*/()) {
			return new_CIdentifier(stripped);
		}
	}
	if (startsWith_/*Not a function type: Placeholder[input=strip_char*]*/("switch")) {
		return new_CContent(createName_App("switch"));
	}
	/*Not a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=compileOperator_App]end]end]end]end]end]end]*/ maybeOperator = or_/*Not a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=compileOperator_App]end]end]end]end]end]*/(_lambda184_);
	if (isPresent_/*Not a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=compileOperator_App]end]end]end]end]end]end]*/()) {
		return new_CContent(get_/*Not a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=or_startNot a function type: Placeholder[input=compileOperator_App]end]end]end]end]end]end]*/());
	}
	/*Not a function type: Placeholder[input=lastIndexOf_startNot a function type: Placeholder[input=strip_char*]end]*/ i2 = lastIndexOf_/*Not a function type: Placeholder[input=strip_char*]*/("::");
	if (i2 >= 0) {
		/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/ substring = substring_/*Not a function type: Placeholder[input=strip_char*]*/(0, i2);
		/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/ substring1 = substring_/*Not a function type: Placeholder[input=strip_char*]*/(i2 + 2);
		/*Not a function type: Placeholder[input=orElse_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=compileType_App]end]end]*/ maybeType = orElse_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=compileType_App]end]*/("?");
		return new_CContent(substring1 + "_" + maybeType);
	}
	if (isNumber_App(stripped)) {
		return new_CContent(stripped);
	}
	return new_Placeholder(stripped);
}
auto _lambda204_(auto _ref, auto segment) {
	auto _this = _ref;
	return "auto " + /*segment*/;
};
auto _lambda207_(auto _ref, auto segment) {
	auto _this = _ref;
	return isEmpty_/*!segment*/();
};
auto _lambda210_(auto _ref) {
			/*Not a function type: Placeholder[input=compileExpression_App]*/ expression = compileExpression_App(content);
			return lineSeparator_/*typeof("{" + generate_startNot a function type: Placeholder[input=new_CStatement]end() + startnew CStatement(new CContent("return "end + startexpression),
																																			1).generate()end + lineSeparator_startSystemend() + "};" + startSystemend)*/();
		}Option<char*> compileLambda_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=indexOf_char*]*/ arrowIndex = indexOf_char*("->");
	if (arrowIndex >= 0) {
		/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_char*]end]*/ names = strip_/*Not a function type: Placeholder[input=substring_char*]*/();
		/*Not a function type: Placeholder[input=substring_char*]*/ content = substring_char*(arrowIndex + 2);
		/*Not a function type: Placeholder[input=createName_App]*/ functionName = createName_App("lambda");
		ArrayList<char*> parameters;
		if (isIdentifier_App(names)) {
			parameters = of_/*ArrayList*/("auto " + names);
		}
		else 
		if (startsWith_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_char*]end]*/("(") && names.endsWith(")")) {
			/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_char*]end]end]*/ slice = substring_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_char*]end]*/(1, length_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_char*]end]*/() - 1);
			parameters = toList_/*Not a function type: Placeholder[input=map_startNot a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=divide_App]end]end]end]*/();
		}
		else {
			return new_None<char*>();
		}
		/*Not a function type: Placeholder[input=addFirst_startNot a function type: Placeholder[input=copy_ArrayList]end]*/ copy = addFirst_/*Not a function type: Placeholder[input=copy_ArrayList]*/("auto _ref");
		_this.functions = addLast_ArrayList<char*>(orElseGet_/*Not a function type: Placeholder[input=compileMethodSegment_starttypeof("auto " + functionName + "(" + join_startStringend(", ", copy.inner) + ") " + _this)end]*/(_lambda210_));
		return new_Some<char*>(functionName);
	}
	return new_None<char*>();
}
char* createName_App(void* _ref, char* type) {
	App _this = *((App*) _ref);
	/*typeof("_" + type + this.counter + "_")*/ s = "_" + type + this.counter + "_";
	_this.counter++;
	return s;
}
Option<CExpression> compileInvocation_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	if (/*stripped.endsWith(")"*/) /*) {
			final var slice = stripped.substring(0, stripped.length() - 1);
			var argStart = -1;
			var depth = 0;
			for (var i = 0; i < slice.length(); i++) {
				final var next = slice.charAt(i);
				if (next == '(') {
					if (depth == 0) {
						argStart = i;
					}

					depth++;
				}
				if (next == ')') {
					depth--;
				}
			}

			if (argStart >= 0) {
				final var callerString = slice.substring(0, argStart).strip();
				final var arguments = this
						.divide(slice.substring(argStart + 1), this::foldValue)
						.map(String::strip)
						.filter(segment -> !segment.isEmpty())
						.map(this::parseExpression)
						.toList();

				final var maybeCaller = this.parseCaller(callerString);
				if (maybeCaller.isPresent()) {
					final var caller = maybeCaller.get();
					if (caller instanceof CFieldAccess(var child, var name)) {
						final var childType = this.resolveExpression(child);
						final var newCallerAlias = name + "_" + childType.generate();
						return new Some<CExpression>(new CInvocation(new CIdentifier(newCallerAlias), arguments));
					}

					return new Some<CExpression>(new CInvocation(caller, arguments));
				}
			}
		}*/
	return new_None<CExpression>();
}
Option<CCaller> parseCaller_App(void* _ref, char* caller) {
	App _this = *((App*) _ref);
	if (startsWith_char*("new ")) {
		/*Not a function type: Placeholder[input=substring_char*]*/ substring = substring_char*(length_/*typeof("new ")*/());
		/*Not a function type: Placeholder[input=compileType_App]*/ maybeType = compileType_App(substring);
		if (isPresent_/*Not a function type: Placeholder[input=compileType_App]*/()) {
			/*Not a function type: Placeholder[input=get_startNot a function type: Placeholder[input=compileType_App]end]*/ type = get_/*Not a function type: Placeholder[input=compileType_App]*/();
			return new_Some<CCaller>(new_CConstruction(type));
		}
	}
	return new_Some<CCaller>(parseExpression_App(caller));
}
Option<char*> compileOperator_App(void* _ref, char* stripped, char* separator) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=indexOf_char*]*/ i1 = indexOf_char*(separator);
	if (i1 >= 0) {
		/*Not a function type: Placeholder[input=substring_char*]*/ substring = substring_char*(0, i1);
		/*Not a function type: Placeholder[input=substring_char*]*/ substring1 = substring_char*(length_/*typeof(i1 + separator)*/());
		return new_Some<char*>(compileExpression_/*typeof(compileExpression_App(substring) + " " + separator + " " + _this)*/(substring1));
	}
	return new_None<char*>();
}
int isNumber_App(void* _ref, char* input) {
	App _this = *((App*) _ref);/*
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (!Character.isDigit(c)) {
				return false;
			}
		}*/
	return 1;
}
auto _lambda218_(auto _ref, auto slice) {
	auto _this = _ref;
	return isEmpty_/*!slice*/();
};
ArrayList<CDefinition> compileParametersToList_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return toList_/*Not a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=divide_App]end]end]end]end]*/();
}
auto _lambda222_(auto _ref, auto segment) {
	auto _this = _ref;
	return isEmpty_/*!segment*/();
};
auto _lambda224_(auto _ref, auto cType) {
	auto _this = _ref;
	return new_CDefinition(finalTypeParameters, /*cType*/, name);
};
auto _lambda226_(auto _ref, auto cType) {
	auto _this = _ref;
	return new_CDefinition(empty_/*ArrayList*/(), /*cType*/, name);
};
Option<CDefinition> compileDefinition_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=lastIndexOf_char*]*/ nameSeparator = lastIndexOf_char*(" ");
	if (nameSeparator < 0) {
		return new_None<CDefinition>();
	}
	/*Not a function type: Placeholder[input=substring_char*]*/ beforeName = substring_char*(0, nameSeparator);
	/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_char*]end]*/ name = strip_/*Not a function type: Placeholder[input=substring_char*]*/();
	if (isIdentifier_/*!this*/(name)) {
		return new_None<CDefinition>();
	}
	/*typeof( - 1)*/ typeSeparator =  - 1;
	/*typeof(0)*/ depth = 0;/*
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
		}*/
	if (typeSeparator >= 0) {
		/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/ beforeType = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]*/();
		/*Not a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/ typeParameters = /*ArrayList*/. < /*String>empty*/();
		if (endsWith_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/(">")) {
			/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/ slice = substring_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/(0, length_/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/() - 1);
			/*Not a function type: Placeholder[input=indexOf_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]end]*/ i = indexOf_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/("<");
			if (i >= 0) {
				/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]end]*/ typeParametersString = substring_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]end]*/(i + 1);
				typeParameters = collect_/*Not a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=divide_App]end]end]*/(new_ListCollector<char*>());
			}
		}
		/*Not a function type: Placeholder[input=strip_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]end]*/ type = strip_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_char*]end]*/();
		/*Not a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/ finalTypeParameters = typeParameters;
		return map_/*Not a function type: Placeholder[input=compileType_App]*/(_lambda224_);
	}
	return map_/*Not a function type: Placeholder[input=compileType_App]*/(_lambda226_);
}
auto _lambda234_(auto _ref, auto slice) {
	auto _this = _ref;
	return isEmpty_/*!slice*/();
};
Option<CType> compileType_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=strip_char*]*/ stripped = strip_char*();/*

		switch (stripped) {
			case "Character" -> {
				return new Some<CType>(CPrimitiveType.Char);
			}
			case "boolean" -> {
				return new Some<CType>(CPrimitiveType.Int);
			}
			case "void" -> {
				return new Some<CType>(CPrimitiveType.Void);
			}
		}*/
	if (endsWith_/*Not a function type: Placeholder[input=strip_char*]*/("[]")) {
		/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/ slice = substring_/*Not a function type: Placeholder[input=strip_char*]*/(0, length_/*Not a function type: Placeholder[input=strip_char*]*/() - 2);
		return map_/*Not a function type: Placeholder[input=compileType_App]*/(new_CPointerType);
	}
	if (equals_/*Not a function type: Placeholder[input=strip_char*]*/("String")) {
		return new_Some<CType>(new_CPointerType(/*CPrimitiveType*/.Char));
	}
	if (endsWith_/*Not a function type: Placeholder[input=strip_char*]*/(">")) {
		/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/ withoutEnd = substring_/*Not a function type: Placeholder[input=strip_char*]*/(0, length_/*Not a function type: Placeholder[input=strip_char*]*/() - 1);
		/*Not a function type: Placeholder[input=indexOf_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]end]*/ i = indexOf_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/("<");
		if (i >= 0) {
			/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]end]*/ base = substring_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/(0, i);
			/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]end]*/ typeArguments = substring_/*Not a function type: Placeholder[input=substring_startNot a function type: Placeholder[input=strip_char*]end]*/(i + 1);
			/*Not a function type: Placeholder[input=toList_startNot a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=divide_App]end]end]end]end]end]*/ list = toList_/*Not a function type: Placeholder[input=flatMap_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=filter_startNot a function type: Placeholder[input=map_startNot a function type: Placeholder[input=divide_App]end]end]end]end]*/();
			return new_Some<CType>(new_CTemplateType(base, list));
		}
	}
	if (isIdentifier_App(stripped)) {
		if (equals_/*typeof(equals_startNot a function type: Placeholder[input=strip_char*]end("public") || stripped)*/("private")) {
			return new_None<CType>();
		}
		return new_Some<CType>(new_CIdentifier(stripped));
	}
	return new_None<CType>();
}
State foldValue_App(void* _ref, State state, char next) {
	App _this = *((App*) _ref);
	if (isLevel_/*typeof(next == ',' && state)*/()) {
		return advance_State();
	}
	/*Not a function type: Placeholder[input=append_State]*/ appended = append_State(next);
	if (next == ' - ') {
		if (peek_/*Not a function type: Placeholder[input=append_State]*/() == '>') {
			return orElse_/*Not a function type: Placeholder[input=popAndAppendToOption_startNot a function type: Placeholder[input=append_State]end]*/(appended);
		}
	}/*

		if (next == '<' || next == '(') {
			return appended.enter();
		}*/
	if (next == '>' || next == ') /*') {
			return appended.exit();
		}*/
	return appended;
}
int main(){
	return 0;
}