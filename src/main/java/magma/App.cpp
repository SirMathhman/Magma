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
*/enum CPrimitiveTypeTag {
};
union CPrimitiveTypeData {
};
struct CPrimitiveType {
	char* content;
};
enum FunctionTag {
};
template <typename T, typename R>
union FunctionData {
};
template <typename T, typename R>
struct Function {
};
enum BiFunctionTag {
};
template <typename A, typename B, typename R>
union BiFunctionData {
};
template <typename A, typename B, typename R>
struct BiFunction {
};
enum ConsumerTag {
};
template <typename T>
union ConsumerData {
};
template <typename T>
struct Consumer {
};
enum HeadTag {
};
template <typename T>
union HeadData {
};
template <typename T>
struct Head {
};
enum CollectorTag {
};
template <typename T, typename C>
union CollectorData {
};
template <typename T, typename C>
struct Collector {
};
enum SupplierTag {
};
template <typename T>
union SupplierData {
};
template <typename T>
struct Supplier {
};
enum PredicateTag {
};
template <typename T>
union PredicateData {
};
template <typename T>
struct Predicate {
};
enum CNodeTag {
};
union CNodeData {
};
struct CNode {
};
enum SingleHeadTag {
};
template <typename T>
union SingleHeadData {
};
template <typename T>
struct SingleHead {
	T element;
	int retrieved;
};
enum EmptyHeadTag {
};
template <typename T>
union EmptyHeadData {
};
template <typename T>
struct EmptyHead {
};
enum ZipHeadTag {
};
template <typename T, typename R>
union ZipHeadData {
};
template <typename T, typename R>
struct ZipHead {
	Head<T> head;
	Head<R> otherHead;
};
enum StreamTag {
};
template <typename T>
union StreamData {
};
template <typename T>
struct Stream {
	Head<T> head;
};
enum ArrayListTag {
};
template <typename T>
union ArrayListData {
};
template <typename T>
struct ArrayList {
	List<T> inner;
};
enum ErrTag {
};
template <typename T, typename X>
union ErrData {
};
template <typename T, typename X>
struct Err {
	X error;
};
enum OkTag {
};
template <typename T, typename X>
union OkData {
};
template <typename T, typename X>
struct Ok {
	T value;
};
enum SomeTag {
};
template <typename T>
union SomeData {
};
template <typename T>
struct Some {
	T value;
};
enum NoneTag {
};
template <typename T>
union NoneData {
};
template <typename T>
struct None {
};
enum CPointerTypeTag {
};
union CPointerTypeData {
};
struct CPointerType {
	CType type;
};
enum CTemplateTypeTag {
};
union CTemplateTypeData {
};
struct CTemplateType {
	char* base;
	ArrayList<CType> typeArguments;
};
enum CIdentifierTag {
};
union CIdentifierData {
};
struct CIdentifier {
	char* value;
};
enum PlaceholderTag {
};
union PlaceholderData {
};
struct Placeholder {
	char* input;
};
enum TupleTag {
};
template <typename A, typename B>
union TupleData {
};
template <typename A, typename B>
struct Tuple {
	A left;
	B right;
};
enum StateTag {
};
union StateData {
};
struct State {
	char* input;
	ArrayList<char*> segments;
	char* buffer;
	int depth;
	int index;
};
enum CDefinitionTag {
};
union CDefinitionData {
};
struct CDefinition {
	ArrayList<char*> typeParameters;
	CType type;
	char* name;
};
enum CStructureHeaderTag {
};
union CStructureHeaderData {
};
struct CStructureHeader {
	ArrayList<char*> typeParameters;
	char* name;
};
enum CStructureTag {
};
union CStructureData {
};
struct CStructure {
	CStructureHeader CStructureHeader;
	char* fields;
};
enum MapHeadTag {
};
template <typename T, typename R>
union MapHeadData {
};
template <typename T, typename R>
struct MapHead {
	Function<T, R> mapper;
	Head<T> head;
};
enum ListHeadTag {
};
template <typename T>
union ListHeadData {
};
template <typename T>
struct ListHead {
	ArrayList<T> list;
	int counter;
};
enum ListCollectorTag {
};
template <typename T>
union ListCollectorData {
};
template <typename T>
struct ListCollector {
};
enum FlatMapHeadTag {
};
template <typename T, typename R>
union FlatMapHeadData {
};
template <typename T, typename R>
struct FlatMapHead {
	Head<T> head;
	Function<T, Stream<R>> mapper;
	Head<R> current;
};
enum JoinerTag {
};
union JoinerData {
};
struct Joiner {
	char* delimiter;
};
enum EmptyCStructureSegmentTag {
};
union EmptyCStructureSegmentData {
};
struct EmptyCStructureSegment {
};
enum CContentTag {
};
union CContentData {
};
struct CContent {
	char* content;
};
enum CStatementTag {
};
union CStatementData {
};
struct CStatement {
	CNode content;
	int depth;
};
enum CFieldAccessTag {
};
union CFieldAccessData {
};
struct CFieldAccess {
	CExpression child;
	char* name;
};
enum FramesTag {
};
union FramesData {
};
struct Frames {
};
enum HashMapTag {
};
template <typename K, typename V>
union HashMapData {
};
template <typename K, typename V>
struct HashMap {
	java.util.HashMap<K, V> internal;
};
enum MapCollectorTag {
};
template <typename K, typename V>
union MapCollectorData {
};
template <typename K, typename V>
struct MapCollector {
};
enum CStructureTypeTag {
};
union CStructureTypeData {
};
struct CStructureType {
	char* name;
	ArrayList<char*> typeParameters;
	ArrayList<CDefinition> fields;
};
enum CConstructionTag {
};
union CConstructionData {
};
struct CConstruction {
	CType type;
};
enum CInvocationTag {
};
union CInvocationData {
};
struct CInvocation {
	CCaller caller;
	ArrayList<CExpression> arguments;
};
enum CMethodMemberTag {
};
union CMethodMemberData {
};
struct CMethodMember {
	CDefinition definition;
};
enum CFunctionTypeTag {
};
union CFunctionTypeData {
};
struct CFunctionType {
	CType returnType;
	ArrayList<CType> paramTypes;
};
enum FrameTag {
};
union FrameData {
};
struct Frame {
	Option<CStructureHeader> maybeHeader;
	ArrayList<CDefinition> definitions;
	ArrayList<CStructureType> structures;
};
enum ArrayHeadTag {
};
template <typename T>
union ArrayHeadData {
};
template <typename T>
struct ArrayHead {
	T* array;/*
		private int counter = 0;*/
};
enum AppTag {
};
union AppData {
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
template <typename T, typename X>
struct Result {
	ResultTag tag;
	ResultData<T, X> data;
};
struct CType {
	CTypeTag tag;
	CTypeData data;
};
struct CFunctionHeader {
	CFunctionHeaderTag tag;
	CFunctionHeaderData data;
};
template <typename T>
struct Option {
	OptionTag tag;
	OptionData<T> data;
};
struct CStructureMember {
	CStructureMemberTag tag;
	CStructureMemberData data;
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
	return _this.head.next().and(next_?);
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
		Option<T> maybeNext = head.next();
		if (/*maybeNext instanceof Some*/ < /*T>*/(/*var next*/)) {
			current = folder.apply(current, /*next*/);
		}
		else {
			return current;
		}
	}
}
template <typename T, typename C>
C collect_Stream(void* _ref, Collector<T, C> collector) {
	Stream<T> _this = *((Stream*) _ref);
	return _this.fold(collector.createInitial(), fold_collector);
}
template <typename T>
ArrayList<T> toList_Stream(void* _ref) {
	Stream<T> _this = *((Stream*) _ref);
	return _this.collect(new_ListCollector<T>());
}
auto _lambda1_(auto _ref, auto element) {
	auto _this = _ref;
	return _this.applyFilter(predicate, /*element*/);
};
template <typename T>
Stream<T> filter_Stream(void* _ref, Predicate<T> predicate) {
	Stream<T> _this = *((Stream*) _ref);
	return _this.flatMap(_lambda1_);
}
template <typename T>
Stream<T> applyFilter_Stream(void* _ref, Predicate<T> predicate, T element) {
	Stream<T> _this = *((Stream*) _ref);
	if (predicate.test(element)) {
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
	return _this.inner.stream().map(toString_Objects).collect(/*Collectors*/.joining(", ", "[", "]"));
}
template <typename T>
int size_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return _this.inner.size();
}
template <typename T>
Stream<T> stream_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return new_Stream<T>(new_ListHead<T>(_this));
}
template <typename T>
ArrayList<T> addLast_ArrayList(void* _ref, T element) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	_this.inner.add(element);
	return _this;
}
template <typename T>
int isEmpty_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return _this.inner.isEmpty();
}
template <typename T>
ArrayList<T> copy_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return _this.stream().collect(new_ListCollector<T>());
}
template <typename T>
ArrayList<T> addFirst_ArrayList(void* _ref, T element) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	_this.inner.addFirst(element);
	return _this;
}
template <typename T>
ArrayList<T> removeLast_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	_this.inner.removeLast();
	return _this;
}
template <typename T>
T getLast_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return _this.inner.getLast();
}
template <typename T>
ArrayList<T> setLast_ArrayList(void* _ref, T element) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	_this.inner.set(_this.inner.size() - 1, element);
	return _this;
}
template <typename T>
ArrayList<T> addAllLast_ArrayList(void* _ref, ArrayList<T> others) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return others.stream().fold(_this, addLast_ArrayList);
}
template <typename T>
ArrayList<T> mapLast_ArrayList(void* _ref, Function<T, T> mapper) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	if (_this.isEmpty()) {
		return _this;
	}
	return _this.setLast(mapper.apply(_this.getLast()));
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
	return new_Some<R>(mapper.apply(_this.value));
}
template <typename T>
char* toString_Some(void* _ref) {
	Some<T> _this = *((Some*) _ref);
	return _this.value.toString();
}
template <typename T>
void ifPresent_Some(void* _ref, Consumer<T> consumer) {
	Some<T> _this = *((Some*) _ref);
	consumer.accept(_this.value);
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
	return mapper.apply(_this.value);
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
	return other.get().map(_lambda3_);
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
	return other.get();
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
	return other.get();
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
	return _this.type.generate() + "*";
}
char* getSimpleName_CPointerType(void* _ref) {
	CPointerType _this = *((CPointerType*) _ref);
	return _this.type.getSimpleName() + "_ref";
}
CType replaceIdentifiersWithMapping_CPointerType(void* _ref, HashMap<char*, CType> mapping) {
	CPointerType _this = *((CPointerType*) _ref);
	return new_CPointerType(_this.type.replaceIdentifiersWithMapping(mapping));
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
	/*Not a function type: Placeholder[input=Does not have a type of structure: ArrayList<CType>]*/ stream = _this.typeArguments.stream();
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: ArrayList<CType>]end]*/ map = stream.map(generate_CType);
	/*Not a function type: Placeholder[input=new_Joiner]*/ collector = new_Joiner(", ");
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: ArrayList<CType>]end]end]*/ joined = map.collect(collector);
	char* s;
	if (_this.typeArguments.isEmpty()) {
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
	return /*arg*/.replaceIdentifiersWithMapping(mapping);
};
CType replaceIdentifiersWithMapping_CTemplateType(void* _ref, HashMap<char*, CType> mapping) {
	CTemplateType _this = *((CTemplateType*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: ArrayList<CType>]end]end]*/ collect = _this.typeArguments.stream().map(_lambda7_).collect(new_ListCollector<CType>());
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
	return mapping.get(_this.value).orElse(_this);
}
Placeholder new_Placeholder(char* input) {
	Placeholder _this;
	_this.input = input;
	return _this;
}
char* wrap_Placeholder(void* _ref, char* input) {
	Placeholder _this = *((Placeholder*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ withoutStart = input.replace("/*", "start");
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ withoutEnd = withoutStart.replace("*/", "end");
	return "/*" + withoutEnd + "*/";
}
char* generate_Placeholder(void* _ref) {
	Placeholder _this = *((Placeholder*) _ref);
	return /*wrap*/(_this.input);
}
char* getSimpleName_Placeholder(void* _ref) {
	Placeholder _this = *((Placeholder*) _ref);
	return _this.generate();
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
	_this.segments = _this.segments.addLast(_this.buffer);
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
	if (_this.index < _this.input.length()) {
		int counter = _this.index;
		_this.index++;
		/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ element = _this.input.charAt(counter);
		return new_Some<char>(element);
	}
	else {
		return new_None<char>();
	}
}
Stream<char*> stream_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.segments.stream();
}
auto _lambda9_(auto _ref, auto next) {
		/*Not a function type: Placeholder[input=Undefined field 'append' in 'State' of type 'CStructureType[name=State, typeParameters=[], fields=[char* input, ArrayList<char*> segments, char* buffer, int depth, int index]]']*/ appended = _this.append(/*next*/);
		return new_Tuple<char, State>(/*next*/, appended);
	}Option<Tuple<char, State>> popAndAppendToTuple_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.pop().map(_lambda9_);
}
Option<State> popAndAppendToOption_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.popAndAppendToTuple().map(right_Tuple);
}
char peek_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.input.charAt(_this.index);
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
	return _this.type().generate() + " " + _this.name();
}
char* toString_CDefinition(void* _ref) {
	CDefinition _this = *((CDefinition*) _ref);
	return _this.generate();
}
CDefinition withType_CDefinition(void* _ref, CType type) {
	CDefinition _this = *((CDefinition*) _ref);
	return new_CDefinition(_this.typeParameters, type, _this.name);
}
CDefinition mapType_CDefinition(void* _ref, Function<CType, CType> mapper) {
	CDefinition _this = *((CDefinition*) _ref);
	return new_CDefinition(_this.typeParameters, mapper.apply(_this.type), _this.name);
}
CStructureHeader new_CStructureHeader(ArrayList<char*> typeParameters, char* name) {
	CStructureHeader _this;
	_this.typeParameters = typeParameters;
	_this.name = name;
	return _this;
}
CType toType_CStructureHeader(void* _ref) {
	CStructureHeader _this = *((CStructureHeader*) _ref);
	if (_this.typeParameters.isEmpty()) {
		return new_CIdentifier(_this.name);
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=typeof(_this.typeParameters.stream(). < startCType>mapend)]end]*/ list = _this.typeParameters.stream(). < /*CType>map*/(new_CIdentifier).toList();
	return new_CTemplateType(_this.name, list);
}
char* generate_CStructureHeader(void* _ref) {
	CStructureHeader _this = *((CStructureHeader*) _ref);
	return /*App*/.createTemplateString(_this.typeParameters()) + "struct " + _this.name();
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
	return _this.CStructureHeader().generate() + " {" + this.fields() + System.lineSeparator() + "};";
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
	return _this.head.next().map(_this.mapper);
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
	if (_this.counter < _this.list.size()) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startDoes not have a type of structure: ArrayList<T>end]*/ element = _this.list.inner.get(_this.counter);
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
	return current.addLast(element);
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
		/*Not a function type: Placeholder[input=Does not have a type of structure: Head<R>]*/ maybeNext = _this.current.next();
		if (maybeNext.isPresent()) {
			return maybeNext;
		}
		/*Not a function type: Placeholder[input=Does not have a type of structure: Head<T>]*/ maybeOuter = _this.head.next();
		if (maybeOuter.isEmpty()) {
			return new_None<R>();
		}
		_this.current = _this.mapper.apply(maybeOuter.get()).head;
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
	if (current.isEmpty()) {
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
	return /*App*/.generateWithIndent(_this.content().generate(), _this.depth()) + ";";
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
	return _this.child.generate() + "." + _this.name;
}
Frames new_Frames() {
	Frames _this;
	return _this;
}
/*private ArrayList<Frame> frames = ArrayList.empty*/(void* _ref);
Frames new_Frames(void* _ref) {
	Frames _this = *((Frames*) _ref);
}
auto _lambda11_(auto _ref, auto last) {
	auto _this = _ref;
	return /*last*/.defineAll(params);
};
Frames defineAll_Frames(void* _ref, ArrayList<CDefinition> params) {
	Frames _this = *((Frames*) _ref);
	_this.frames = _this.frames.mapLast(_lambda11_);
	return _this;
}
template <typename T>
Tuple<T, Frames> within_Frames(void* _ref, Supplier<T> mapper) {
	Frames _this = *((Frames*) _ref);
	_this.frames = _this.frames.addLast(new_Frame());
	T result = mapper.get();
	_this.frames = _this.frames.removeLast();
	return new_Tuple<T, Frames>(result, _this);
}
auto _lambda13_(auto _ref, auto last) {
	auto _this = _ref;
	return /*last*/.define(definition);
};
Frames define_Frames(void* _ref, CDefinition definition) {
	Frames _this = *((Frames*) _ref);
	_this.frames = _this.frames.mapLast(_lambda13_);
	return _this;
}
auto _lambda20_(auto _ref, auto frame) {
	auto _this = _ref;
	return /*frame*/.resolve(name);
};
Option<CDefinition> resolve_Frames(void* _ref, char* name) {
	Frames _this = *((Frames*) _ref);
	return _this.frames.stream().map(_lambda20_).flatMap(stream_Option).head.next();
}
auto _lambda22_(auto _ref, auto last) {
	auto _this = _ref;
	return /*last*/.withHeader(header);
};
Frames withStructureHeader_Frames(void* _ref, CStructureHeader header) {
	Frames _this = *((Frames*) _ref);
	_this.frames = _this.frames.mapLast(_lambda22_);
	return _this;
}
auto _lambda28_(auto _ref, auto header) {
	auto _this = _ref;
	return /*header*/.typeParameters;
};
ArrayList<char*> collectTypeParameters_Frames(void* _ref) {
	Frames _this = *((Frames*) _ref);
	return _this.streamHeaders().map(_lambda28_).flatMap(stream_ArrayList).collect(new_ListCollector<char*>());
}
auto _lambda35_(auto _ref, auto frame) {
	auto _this = _ref;
	return /*frame*/.maybeHeader;
};
Option<CStructureHeader> findCurrentStructure_Frames(void* _ref) {
	Frames _this = *((Frames*) _ref);
	return _this.frames.copy().reverse().stream().map(_lambda35_).flatMap(stream_Option).head.next();
}
auto _lambda39_(auto _ref, auto frame) {
	auto _this = _ref;
	return /*frame*/.maybeHeader;
};
Stream<CStructureHeader> streamHeaders_Frames(void* _ref) {
	Frames _this = *((Frames*) _ref);
	return _this.frames.stream().map(_lambda39_).flatMap(stream_Option);
}
auto _lambda48_(auto _ref, auto header) {
	auto _this = _ref;
	return new_Tuple<CStructureHeader, ArrayList<CDefinition>>(/*header*/, /*frame*/.definitions);
};
auto _lambda46_(auto _ref, auto frame) {
	auto _this = _ref;
	return /*frame*/.maybeHeader.map(_lambda48_);
};
Option<Tuple<CStructureHeader, ArrayList<CDefinition>>> findCurrentScope_Frames(void* _ref) {
	Frames _this = *((Frames*) _ref);
	return _this.frames.copy().reverse().stream().map(_lambda46_).flatMap(stream_Option).head.next();
}
auto _lambda55_(auto _ref, auto frame) {
	auto _this = _ref;
	return /*frame*/.findStructure(structName);
};
Option<CStructureType> findStructure_Frames(void* _ref, char* structName) {
	Frames _this = *((Frames*) _ref);
	return _this.frames.stream().map(_lambda55_).flatMap(stream_Option).head.next();
}
auto _lambda57_(auto _ref, auto last) {
	auto _this = _ref;
	return /*last*/.defineStructure(type);
};
Frames defineStructure_Frames(void* _ref, CStructureType type) {
	Frames _this = *((Frames*) _ref);
	_this.frames = _this.frames.mapLast(_lambda57_);
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
	_this.internal.put(key, value);
	return _this;
}
template <typename K, typename V>
Option<V> get_HashMap(void* _ref, K key) {
	HashMap<K, V> _this = *((HashMap*) _ref);
	if (_this.internal.containsKey(key)) {
		return new_Some<V>(_this.internal.get(key));
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
	return current.with(element.left, element.right);
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
	return /*type*/.replaceIdentifiersWithMapping(mapping);
};
auto _lambda61_(auto _ref, auto field) {
	auto _this = _ref;
	return /*field*/.mapType(_lambda63_);
};
CType replaceIdentifiersWithMapping_CStructureType(void* _ref, HashMap<char*, CType> mapping) {
	CStructureType _this = *((CStructureType*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: ArrayList<CDefinition>]end]end]*/ list = _this.fields.stream().map(_lambda61_).toList();
	return new_CStructureType(_this.name, _this.typeParameters, list);
}
auto _lambda68_(auto _ref, auto field) {
	auto _this = _ref;
	return /*field*/.type;
};
auto _lambda71_(auto _ref, auto field) {
	auto _this = _ref;
	return /*field*/.name.equals(name);
};
Option<CType> findField_CStructureType(void* _ref, char* name) {
	CStructureType _this = *((CStructureType*) _ref);
	return _this.fields.stream().filter(_lambda71_).map(_lambda68_).head.next();
}
auto _lambda77_(auto _ref, auto type) {
	auto _this = _ref;
	return /*type*/.replaceIdentifiersWithMapping(mapping);
};
auto _lambda75_(auto _ref, auto field) {
	auto _this = _ref;
	return /*field*/.mapType(_lambda77_);
};
CStructureType withTypeArguments_CStructureType(void* _ref, ArrayList<CType> typeArguments) {
	CStructureType _this = *((CStructureType*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: ArrayList<char*>]end]end]*/ mapping = _this.typeParameters.stream().zip(typeArguments.stream()).collect(new_MapCollector<char*, CType>());
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: ArrayList<CDefinition>]end]end]*/ newFields = _this.fields.stream().map(_lambda75_).toList();
	return new_CStructureType(_this.name, /*ArrayList*/.empty(), newFields);
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
	return "new_" + _this.type().generate();
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
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: CTemplateType[base=ArrayList, typeArguments=[CIdentifier[value=CExpression]]]end]end]end]*/ joinedArguments = _this.arguments().stream().map(generate_CExpression).collect(new_Joiner(", "));
	return _this.caller().generate() + "(" + joinedArguments + ")";
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
	return /*type*/.replaceIdentifiersWithMapping(mapping);
};
CType replaceIdentifiersWithMapping_CFunctionType(void* _ref, HashMap<char*, CType> mapping) {
	CFunctionType _this = *((CFunctionType*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: ArrayList<CType>]end]end]*/ replacedParamTypes = _this.paramTypes.stream().map(_lambda81_).toList();
	return new_CFunctionType(_this.returnType.replaceIdentifiersWithMapping(mapping), replacedParamTypes);
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
	_this(new_None<CStructureHeader>(), /*ArrayList*/.empty(), /*ArrayList*/.empty());
}
Frame defineAll_Frame(void* _ref, ArrayList<CDefinition> params) {
	Frame _this = *((Frame*) _ref);
	return params.stream().fold(_this, define_Frame);
}
Frame define_Frame(void* _ref, CDefinition definition) {
	Frame _this = *((Frame*) _ref);
	if (/*definition.type instanceof CIdentifier*/(/*var value*/) && /*value*/.equals("var")) {
		/*throw new RuntimeException*/();
	}
	return new_Frame(_this.maybeHeader, _this.definitions.addLast(definition), _this.structures);
}
Frame withHeader_Frame(void* _ref, CStructureHeader header) {
	Frame _this = *((Frame*) _ref);
	return new_Frame(new_Some<CStructureHeader>(header), _this.definitions, _this.structures);
}
auto _lambda86_(auto _ref, auto definition) {
	auto _this = _ref;
	return /*definition*/.name.equals(name);
};
Option<CDefinition> resolve_Frame(void* _ref, char* name) {
	Frame _this = *((Frame*) _ref);
	return _this.definitions.stream().filter(_lambda86_).head.next();
}
auto _lambda91_(auto _ref, auto type) {
	auto _this = _ref;
	return /*type*/.name.equals(name);
};
Option<CStructureType> findStructure_Frame(void* _ref, char* name) {
	Frame _this = *((Frame*) _ref);
	return _this.structures.stream().filter(_lambda91_).head.next();
}
Frame defineStructure_Frame(void* _ref, CStructureType type) {
	Frame _this = *((Frame*) _ref);
	return new_Frame(_this.maybeHeader, _this.definitions, _this.structures.addLast(type));
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
	_this.globals = /*ArrayList*/.empty();
	_this.frames = new_Frames();
	_this.functions = /*ArrayList*/.empty();
	_this.forwardDeclarations = /*ArrayList*/.empty();
	_this.structures = /*ArrayList*/.empty();
	_this.sealedStructures = /*ArrayList*/.empty();
	_this.depth = 1;
	_this.counter = 0;
}
void main_App(void* _ref, char** args) {
	App _this = *((App*) _ref);
	new_App().run().ifPresent(printStackTrace_Throwable);
}
auto _lambda95_(auto _ref, auto slice) {
	auto _this = _ref;
	return "typename " + /*slice*/;
};
char* createTemplateString_App(void* _ref, ArrayList<char*> typeParameters) {
	App _this = *((App*) _ref);
	char* templateString;
	if (typeParameters.isEmpty()) {
		templateString = "";
	}
	else {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: Stream<char*>]end]*/ collect = typeParameters.stream().map(_lambda95_).collect(new_Joiner(", "));
		templateString = "template <" + collect + ">" + /*System*/.lineSeparator();
	}
	return templateString;
}
char* generateWithIndent_App(void* _ref, char* content, int depth) {
	App _this = *((App*) _ref);
	return /*App*/.generateIndent(depth) + content;
}
char* generateIndent_App(void* _ref, int depth) {
	App _this = *((App*) _ref);
	return /*System*/.lineSeparator() + "\t".repeat(depth);
}
Option<IOException> run_App(void* _ref) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startPathsend]*/ source = /*Paths*/.get(".", "src", "main", "java", "magma", "App.java");
	/*Not a function type: Placeholder[input=Undefined field 'readString' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ input = _this.readString(source);
	return _switch97_;
}
auto _lambda99_(auto _ref) {
	auto _this = _ref;
	return _this.compileNative(target);
};
Option<IOException> compilePath_App(void* _ref, Path source, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: Path]*/ target = source.resolveSibling("App.cpp");
	/*Not a function type: Placeholder[input=Undefined field 'compile' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ output = _this.compile(input);
	return _this.writeString(target, output).or(_lambda99_);
}
Option<IOException> compileNative_App(void* _ref, Path target) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Undefined field 'startCommand' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ clang = _this.startCommand(/*ArrayList*/.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));
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
	/*Not a function type: Placeholder[input=Undefined field 'compileStatements' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ compiled = _this.compileStatements(input, compileRootSegment_this);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startStringend]*/ joinedForwardDeclarations = /*String*/.join("", _this.forwardDeclarations.inner);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startStringend]*/ joinedFunctions = /*String*/.join("", _this.functions.inner);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startStringend]*/ joinedStructures = /*String*/.join("", _this.structures.inner);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startStringend]*/ joinedSealedStructures = /*String*/.join("", _this.sealedStructures.inner);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startStringend]*/ joinedGlobals = /*String*/.join("", _this.globals.inner);
	return joinedForwardDeclarations + compiled + joinedStructures + joinedSealedStructures + joinedGlobals + joinedFunctions + "int main(){" + System.lineSeparator() + "\treturn " + "0;" + System.lineSeparator() +
					 "}";
}
char* compileStatements_App(void* _ref, char* input, Function<char*, char*> mapper) {
	App _this = *((App*) _ref);
	return _this.divide(input, foldStatement_this).map(mapper).collect(new_Joiner(""));
}
Stream<char*> divide_App(void* _ref, char* input, BiFunction<State, char, State> folder) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=new_State]*/ current = new_State(input);
	while (1) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=new_State]end]*/ maybeNext = current.pop();
		if (maybeNext.isEmpty()) {
			break;
		}
		current = _this.foldEscaped(current, maybeNext.get(), folder);
	}
	return current.advance().stream();
}
State foldEscaped_App(void* _ref, State current, char next, BiFunction<State, char, State> folder) {
	App _this = *((App*) _ref);
	if (next == '\'') {
		return current.append(next).popAndAppendToTuple().map(foldSingleEscapeChar_this).flatMap(popAndAppendToOption_State).orElse(current);
	}
	if (next == '\"') {
		/*Not a function type: Placeholder[input=Does not have a type of structure: State]*/ current0 = current.append(next);
		while (1) {
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: State]end]*/ maybeTuple = current0.popAndAppendToTuple();
			if (maybeTuple.isEmpty()) {
				break;
			}
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: State]end]end]*/ tuple = maybeTuple.get();
			current0 = tuple.right;
			/*Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: State]end]end]end*/ nextInQuotes = tuple.left;
			if (nextInQuotes == '\\') {
				current0 = current0.popAndAppendToOption().orElse(current0);
				continue;
			}
			if (nextInQuotes == '\"') {
				break;
			}
		}
		return current0;
	}
	return folder.apply(current, next);
}
State foldSingleEscapeChar_App(void* _ref, Tuple<char, State> tuple) {
	App _this = *((App*) _ref);
	if (tuple.left == '\\') {
		return tuple.right.popAndAppendToOption().orElse(tuple.right);
	}
	return tuple.right;
}
State foldStatement_App(void* _ref, State state, char c) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: State]*/ appended = state.append(c);
	if (c == ';' && appended.isLevel()) {
		return appended.advance();
	}
	if (c == '}' && appended.isShallow()) {
		State state1;
		if (appended.peek() == ';') {
			state1 = appended.popAndAppendToOption().orElse(appended);
		}
		else {
			state1 = appended;
		}
		return state1.advance().exit();
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
	return /*Placeholder*/.wrap(input);
};
auto _lambda108_(auto _ref, auto member) {
		if (/*member instanceof CStructureSegment segment*/) {
			return /*segment*/.generate();
		}
		else {
			return "???";
		}
	}char* compileRootSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ stripped = input.strip();
	if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
		return "";
	}
	return _this.parseStructure("class", stripped).map(_lambda108_).orElseGet(_lambda105_);
}
auto _lambda112_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
auto _lambda116_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
auto _lambda120_(auto _ref, auto content1) {
	auto _this = _ref;
	return /*App*/.generateWithIndent(/*content1*/, 1);
};
auto _lambda123_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*slice*/ + "Tag";
};
auto _lambda127_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*System*/.lineSeparator() + "\t" + slice + typeArguments + " " + slice.toLowerCase() + ";";
};
auto _lambda131_(auto _ref, auto parameter) {
	auto _this = _ref;
	return /*System*/.lineSeparator() + "\t_this." + parameter.name + " = " + parameter.name + ";";
};
auto _lambda135_(auto _ref, auto slice) {
	auto _this = _ref;
	return new_CStatement(new_CContent(/*slice*/), 1).generate();
};
auto _lambda137_(auto _ref) {
					_this.frames = _this.frames.withStructureHeader(header).defineAll(finalRecordFields);
					/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'divide' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]end]*/ members = _this.divide(content, foldStatement_this).map(compileClassSegment_this).collect(new_ListCollector<CStructureMember>());
					/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'divide' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]end]end]end]end]end]*/ joinedFields = members.stream().map(generateField_this).flatMap(stream_Option).collect(new_Joiner(""));
					/*typeof(generatedFields + joinedFields)*/ outputContent = generatedFields + joinedFields;
					/*Not a function type: Placeholder[input=Does not have a type of structure: starttypeof(finalDependencies + new_CStructure(header, outputContent).generate() + startSystemend)end]*/ generated = finalDependencies + new_CStructure(header, outputContent).generate() + /*System*/.lineSeparator();
					return new_Tuple<ArrayList<CStructureMember>, char*>(members, generated);
				}Option<CStructureMember> parseStructure_App(void* _ref, char* type, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ classIndex = input.indexOf(type);
	if (classIndex >= 0) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ afterKeyword = input.substring(classIndex + type.length());
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ contentStart = afterKeyword.indexOf("{");
		if (contentStart >= 0) {
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ beforeContent = afterKeyword.substring(0, contentStart).strip();
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
			if (withEnd.endsWith("}")) {
				/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]*/ content = withEnd.substring(0, withEnd.length() - 1);
				/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]*/ permitsIndex = beforeContent.indexOf("permits");
				/*Not a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/ variants = /*ArrayList*/. < /*String>empty*/();
				if (permitsIndex >= 0) {
					/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]end]*/ variantsArray = beforeContent.substring(permitsIndex + "permits".length()).split(/*Pattern*/.quote(","));
					beforeContent = beforeContent.substring(0, permitsIndex).strip();
					variants = new_Stream<char*>(new_ArrayHead<char*>(variantsArray)).map(strip_char*).filter(_lambda112_).toList();
				}
				/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]*/ implementsIndex = beforeContent.indexOf("implements");
				Option<CType> maybeInterfaceType = new_None<CType>();
				if (implementsIndex >= 0) {
					/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]end]*/ slice = beforeContent.substring(implementsIndex + "implements".length()).strip();
					/*maybeInterfaceType*/ = _this.compileType(slice);
					beforeContent = beforeContent.substring(0, implementsIndex).strip();
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
				if (beforeContent.endsWith(">")) {
					/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]*/ withoutEnd = beforeContent.substring(0, beforeContent.length() - 1);
					/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]end]*/ typeParamStart = withoutEnd.indexOf("<");
					if (typeParamStart >= 0) {
						beforeContent = withoutEnd.substring(0, typeParamStart);
						/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]end]end]*/ typeParamsArray = withoutEnd.substring(typeParamStart + 1).split(/*Pattern*/.quote(","));
						typeParameters = new_Stream<char*>(new_ArrayHead<char*>(typeParamsArray)).map(strip_char*).filter(_lambda116_).toList();
					}
				}
				if (/*!this*/.isIdentifier(beforeContent)) {
					return new_None<CStructureMember>();
				}
				/*Not a function type: Placeholder[input=Does not have a type of structure: startAppend]*/ templateString = /*App*/.createTemplateString(typeParameters);
				/*typeof("")*/ dependencies = "";
				if (variants.isEmpty()) {
					/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]end]end]end]end]*/ enumFields = variants.stream().map(_lambda123_).map(_lambda120_).collect(new_Joiner(","));
					/*Not a function type: Placeholder[input=Undefined field 'joinTypeArguments' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ typeArguments = _this.joinTypeArguments(typeParameters);
					/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]end]end]end]*/ unionFields = variants.stream().map(_lambda127_).collect(new_Joiner(""));
					dependencies +  = "enum " + beforeContent + "Tag {" + enumFields + /*System*/.lineSeparator() + "};" + /*System*/.lineSeparator() + templateString + "union " + beforeContent + "Data {" + unionFields + /*System*/.lineSeparator() + "};" + /*System*/.lineSeparator();
				}
				/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=typeof(typeParameters.stream(). < startCType>mapend)]end]*/ types = typeParameters.stream(). < /*CType>map*/(new_CIdentifier).toList();
				/*Not a function type: Placeholder[input=new_CTemplateType]*/ thisType = new_CTemplateType(beforeContent, types);
				/*Not a function type: Placeholder[input=new_CDefinition]*/ constructorHeader = new_CDefinition(/*ArrayList*/.empty(), thisType, "new_" + beforeContent);
				/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=typeof(startArrayListend. < startCDefinition>emptyend)]end]end]end]*/ assignments = recordParameters.stream().map(_lambda131_).collect(new_Joiner());
				/*Not a function type: Placeholder[input=Does not have a type of structure: starttypeof(startSystemend.lineSeparator() + "\t" + thisType.generate() + " _this;" + assignments + startSystemend.lineSeparator() + "\treturn _this;" + startSystemend)end]*/ constructorContent1 = /*System*/.lineSeparator() + "\t" + thisType.generate() + " _this;" + assignments + /*System*/.lineSeparator() + "\treturn _this;" + /*System*/.lineSeparator();
				_this.functions = _this.functions.addLast(_this.generateMethod(typeParameters, constructorHeader, constructorContent1, recordParameters));
				char* generatedFields;
				if (variants.isEmpty()) {
					generatedFields = recordParameters.stream().map(generate_CDefinition).map(_lambda135_).collect(new_Joiner(""));
				}
				else {
					generatedFields = /*new CStatement(new CContent(beforeContent*/ + /*"Tag tag"), 1).generate()*/ + /*new CStatement*/(new_CContent(beforeContent + "Data" + this.joinTypeArguments(typeParameters) + " " + "data"), 1).generate();
				}
				if (/*maybeInterfaceType*/.isPresent()) {
					/*Not a function type: Placeholder[input=Does not have a type of structure: startmaybeInterfaceTypeend]*/ interfaceType = /*maybeInterfaceType*/.get();
					/*Not a function type: Placeholder[input=Undefined field 'joinTypeArguments' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ joinedTypeArguments = _this.joinTypeArguments(typeParameters);
					/*typeof(beforeContent + joinedTypeArguments)*/ thisTypeString = beforeContent + joinedTypeArguments;
					_this.functions = _this.functions.addLast(templateString + interfaceType.generate() + " to" + interfaceType.getSimpleName() + "_" + beforeContent + "(void* _ref" + "){" + /*new CStatement(new CContent(thisTypeString*/ + " _this = *((" + thisTypeString + /*"*) _ref)"),
															 1).generate()*/ + new_CStatement(/*new CContent(interfaceType.getSimpleName(*/) + "Data" + joinedTypeArguments + /*" data"),
															 1).generate()*/ + /*new CStatement(new CContent("data."*/ + beforeContent.toLowerCase() + /*" = _this"), 1).generate()*/ + /*new CStatement(new CContent(
										"return "*/ + interfaceType.generate() + " { " + beforeContent + "Tag, " + /*"data }"), 1).generate()*/ + /*System*/.lineSeparator() + "}" + /*System*/.lineSeparator());
				}
				_this.forwardDeclarations = _this.forwardDeclarations.addLast(templateString + "struct " + beforeContent + ";" + /*System*/.lineSeparator());
				/*Not a function type: Placeholder[input=new_CStructureHeader]*/ header = new_CStructureHeader(typeParameters, beforeContent);
				/*Not a function type: Placeholder[input=typeof(startArrayListend. < startCDefinition>emptyend)]*/ finalRecordFields = recordParameters;
				/*typeof("")*/ finalDependencies = dependencies;
				/*Not a function type: Placeholder[input=Does not have a type of structure: Frames]*/ within1 = _this.frames.within(_lambda137_);
				/*Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: Frames]end*/ result = within1.left;
				/*Does not have a type of structure: startDoes not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: Frames]endend*/ members = result.left;
				/*Does not have a type of structure: startDoes not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: Frames]endend*/ generated = result.right;
				/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startDoes not have a type of structure: startDoes not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: Frames]endendend]end]end]end]*/ memberDefinitions = members.stream().map(toDefinition_CStructureMember).flatMap(stream_Option).toList();
				/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=typeof(startArrayListend. < startCDefinition>emptyend)]end]*/ allMembers = finalRecordFields.addAllLast(memberDefinitions);
				_this.frames = within1.right.defineStructure(header.withFields(allMembers));
				if (variants.isEmpty()) {
					_this.structures = _this.structures.addLast(generated);
				}
				else {
					_this.sealedStructures = _this.sealedStructures.addLast(generated);
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
		return new_Some<char*>(/*segment*/.generate());
	}
	else {
		return new_None<char*>();
	}
}
char* joinTypeArguments_App(void* _ref, ArrayList<char*> typeParameters) {
	App _this = *((App*) _ref);
	char* joinedTypeArguments;
	if (typeParameters.isEmpty()) {
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
	return _this.compileDefinitionToField0(slice);
};
auto _lambda141_(auto _ref) {
	auto _this = _ref;
	return new_Placeholder(input);
};
CStructureMember compileClassSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	if (input.isBlank()) {
		return new_EmptyCStructureSegment();
	}
	/*Not a function type: Placeholder[input=Undefined field 'parseStructure' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeClass = _this.parseStructure("class", input);
	if (maybeClass.isPresent()) {
		return maybeClass.get();
	}
	/*Not a function type: Placeholder[input=Undefined field 'parseStructure' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeInterface = _this.parseStructure("interface", input);
	if (maybeInterface.isPresent()) {
		return maybeInterface.get();
	}
	/*Not a function type: Placeholder[input=Undefined field 'parseStructure' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeRecord = _this.parseStructure("record", input);
	if (maybeRecord.isPresent()) {
		return maybeRecord.get();
	}
	/*Not a function type: Placeholder[input=Undefined field 'parseStructure' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeEnum = _this.parseStructure("enum", input);
	if (maybeEnum.isPresent()) {
		return maybeEnum.get();
	}
	if (input.endsWith(";")) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ slice = input.substring(0, input.length() - 1);
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'compileEnumValues' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]*/ maybeClassStatement = _this.compileEnumValues(slice).or(_lambda139_);
		if (maybeClassStatement.isPresent()) {
			return maybeClassStatement.get();
		}
	}
	return _this.parseMethod(input).orElseGet(_lambda141_);
}
auto _lambda145_(auto _ref) {
					/*Not a function type: Placeholder[input=Does not have a type of structure: starttypeof(thisDefinition + _this.compileMethodSegments(content) + startSystemend)end]*/ outputContent = thisDefinition + _this.compileMethodSegments(content) + /*System*/.lineSeparator();
					return _this.generateMethod(typeParameters, header, outputContent, paramsWithThis);
				}auto _lambda143_(auto _ref) {
				_this.frames = _this.frames.defineAll(params);
				/*Not a function type: Placeholder[input=Does not have a type of structure: Frames]*/ withBlock = _this.frames.within(_lambda145_);
				_this.frames = withBlock.right;
				return withBlock.left;
			}auto _lambda149_(auto _ref, auto type) {
	auto _this = _ref;
	return new_CFunctionType(/*type*/, paramTypes);
};
Option<CStructureMember> parseMethod_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return new_None<CStructureMember>();
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ definition = input.substring(0, paramStart).strip();
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ withParams = input.substring(paramStart + 1);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ paramEnd = withParams.indexOf(")");
	if (paramEnd < 0) {
		return new_None<CStructureMember>();
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ inputParams = withParams.substring(0, paramEnd).strip();
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ withBraces = withParams.substring(paramEnd + 1).strip();
	/*Not a function type: Placeholder[input=Undefined field 'parseFunctionHeader' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ header = _this.parseFunctionHeader(definition);
	/*Not a function type: Placeholder[input=Undefined field 'compileParametersToList' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ params = _this.compileParametersToList(inputParams);
	ArrayList<char*> typeParameters;
	if (/*header instanceof CDefinition definition1*/) {
		typeParameters = _this.frames.collectTypeParameters().copy().addAllLast(/*definition1*/.typeParameters);
	}
	else {
		typeParameters = _this.frames.collectTypeParameters().copy().addAllLast(/*ArrayList*/.empty());
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'compileParametersToList' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]end]*/ paramsWithThis = params.copy().addFirst(new_CDefinition(/*ArrayList*/.empty(), new_CPointerType(/*CPrimitiveType*/.Void), "_ref"));
	/*Not a function type: Placeholder[input=Does not have a type of structure: starttypeof(startcreateTemplateStringend(typeParameters) + _this.generateHeaderWithParameters(header, paramsWithThis) + ";" + startSystemend)end]*/ generated = /*createTemplateString*/(typeParameters) + _this.generateHeaderWithParameters(header, paramsWithThis) + ";" + /*System*/.lineSeparator();
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]*/ content = withBraces.substring(1, withBraces.length() - 1);
		/*Not a function type: Placeholder[input=Does not have a type of structure: Frames]*/ maybeCurrentStructure = _this.frames.findCurrentStructure();
		if (/*maybeCurrentStructure instanceof Some*/ < /*CStructureHeader>*/(/*var currentStructure*/)) {
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=new_CStatement]end]*/ thisDefinition = new_CStatement(new_CContent(/*currentStructure*/.toType().generate() + " _this = *((" + currentStructure.name() + "*) _ref)"), 1).generate();
			/*Not a function type: Placeholder[input=Does not have a type of structure: Frames]*/ framesWithParams = _this.frames.within(_lambda143_);
			generated = framesWithParams.left;
			_this.frames = framesWithParams.right;
		}
	}
	_this.functions = _this.functions.addLast(generated);
	if (/*header instanceof CDefinition definition1*/) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'compileParametersToList' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]end]end]*/ paramTypes = params.stream().map(type_CDefinition).collect(new_ListCollector<CType>());
		return new_Some<CStructureMember>(new_CMethodMember(/*definition1*/.mapType(_lambda149_)));
	}
	else {
		return new_Some<CStructureMember>(new_EmptyCStructureSegment());
	}
}
char* generateMethod_App(void* _ref, ArrayList<char*> typeParameters, CFunctionHeader header, char* content, ArrayList<CDefinition> params) {
	App _this = *((App*) _ref);
	return /*createTemplateString*/(typeParameters) + _this.generateHeaderWithParameters(header, params) + " {" + content + "}" + /*System*/.lineSeparator();
}
char* generateHeaderWithParameters_App(void* _ref, CFunctionHeader header, ArrayList<CDefinition> params) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: Stream<CDefinition>]end]*/ outputParams = params.stream().map(generate_CDefinition).collect(new_Joiner(", "));
	return header.generate() + "(" + outputParams + ")";
}
auto _lambda151_(auto _ref) {
	auto _this = _ref;
	return new_Placeholder(input);
};
auto _lambda154_(auto _ref) {
	auto _this = _ref;
	return _this.compileConstructor(input);
};
auto _lambda161_(auto _ref, auto header) {
	auto _this = _ref;
	return /*header*/.name;
};
auto _lambda157_(auto _ref, auto item) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: Frames]end]end]*/ currentStructureName = _this.frames.findCurrentStructure().map(_lambda161_).orElse("???");
		return new_CDefinition(/*item*/.typeParameters, /*item*/.type, /*item*/.name + "_" + currentStructureName);
	}CFunctionHeader parseFunctionHeader_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.compileDefinition(input). < /*CFunctionHeader>map*/(_lambda157_).or(_lambda154_).orElseGet(_lambda151_);
}
Option<CStructureMember> compileDefinitionToField0_App(void* _ref, char* slice) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Undefined field 'compileDefinition' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeDefinition = _this.compileDefinition(slice);
	if (/*!maybeDefinition*/.isPresent()) {
		return new_None<CStructureMember>();
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'compileDefinition' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]*/ definition = maybeDefinition.get();
	_this.frames = _this.frames.define(definition);
	return new_Some<CStructureMember>(new_CStatement(definition, 1));
}
char* compileMethodSegments_App(void* _ref, char* content) {
	App _this = *((App*) _ref);
	return _this.compileStatements(content, compileMethodSegmentOrPlaceholder_this);
}
auto _lambda165_(auto _ref, auto header) {
	auto _this = _ref;
	return /*header*/.name;
};
Option<CFunctionHeader> compileConstructor_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ i = input.lastIndexOf(" ");
	if (i >= 0) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ name = input.substring(i + 1).strip();
		if (_this.isIdentifier(name)) {
			/*Not a function type: Placeholder[input=Does not have a type of structure: Frames]*/ peek0 = _this.frames.findCurrentStructure();
			if (/*peek0 instanceof Some*/ < /*CStructureHeader>*/(/*var peek*/)) {
				return new_Some<CFunctionHeader>(new_CDefinition(/*ArrayList*/.empty(), /*peek*/.toType(), "new_" + /*peek*/.name));
			}
		}
	}
	else {
		if (_this.isIdentifier(input)) {
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: Frames]end]end]*/ structName = _this.frames.findCurrentStructure().map(_lambda165_).orElse("???");
			return new_Some<CFunctionHeader>(new_CDefinition(/*ArrayList*/.empty(), new_CIdentifier(structName), "new_" + structName));
		}
	}
	return new_None<CFunctionHeader>();
}
auto _lambda169_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
Option<CStructureMember> compileEnumValues_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=new_Stream<char*>]end]end]end]*/ segments = new_Stream<char*>(new_ArrayHead<char*>(input.split(/*Pattern*/.quote(",")))).map(strip_char*).filter(_lambda169_).toList();/*

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
	return /*Placeholder*/.wrap(input);
};
char* compileMethodSegmentOrPlaceholder_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.compileMethodSegment(input).orElseGet(_lambda171_);
}
auto _lambda173_(auto _ref) {
	auto _this = _ref;
	return _this.compileMethodSegments(content);
};
Option<char*> compileMethodSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ stripped = input.strip();
	if (stripped.isEmpty() || stripped.startsWith("try ") || stripped.startsWith("catch ")) {
		return new_Some<char*>("");
	}
	if (stripped.startsWith("{") && stripped.endsWith("}")) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ content = stripped.substring(1, stripped.length() - 1);
		_this.depth++;
		/*Not a function type: Placeholder[input=Does not have a type of structure: Frames]*/ within = _this.frames.within(_lambda173_);
		/*Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: Frames]end*/ compiled = within.left;
		_this.frames = within.right;
		/*this.depth--*/;
		return new_Some<char*>("{" + compiled + App.generateIndent(this.depth) + "}");
	}
	/*Not a function type: Placeholder[input=Undefined field 'compileConditional' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeIf = _this.compileConditional(stripped, "if");
	if (maybeIf.isPresent()) {
		return maybeIf;
	}
	/*Not a function type: Placeholder[input=Undefined field 'compileConditional' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeWhile = _this.compileConditional(stripped, "while");
	if (maybeWhile.isPresent()) {
		return maybeWhile;
	}
	if (stripped.endsWith(";")) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ slice = stripped.substring(0, stripped.length() - 1);
		return new_Some<char*>(new_CStatement(new_CContent(_this.compileMethodStatement(slice)), _this.depth).generate());
	}
	if (stripped.startsWith("else ")) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ substring = stripped.substring(5);
		return new_Some<char*>(/*App*/.generateIndent(_this.depth) + "else " + _this.compileMethodSegmentOrPlaceholder(substring));
	}
	return new_None<char*>();
}
Option<char*> compileConditional_App(void* _ref, char* input, char* type) {
	App _this = *((App*) _ref);
	if (input.startsWith(type)) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ substring = input.substring(type.length()).strip();/*
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
	return /*Placeholder*/.wrap(stripped);
};
auto _lambda178_(auto _ref) {
	auto _this = _ref;
	return _this.parseAndDefineDefinitionAsStatement(input);
};
char* compileMethodStatement_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ stripped = input.strip();
	if (stripped.startsWith("return ")) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ slice = stripped.substring("return ".length()).strip();
		return "return " + _this.compileExpression(slice);
	}
	/*Not a function type: Placeholder[input=Undefined field 'compileAssignment' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeAssignment = _this.compileAssignment(stripped);
	if (maybeAssignment.isPresent()) {
		return maybeAssignment.get();
	}
	if (stripped.endsWith("++")) {
		return _this.compileExpression(stripped.substring(0, stripped.length() - 2)) + "++";
	}
	if (stripped.equals("break")) {
		return "break";
	}
	if (stripped.equals("continue")) {
		return "continue";
	}
	return _this.compileInvocation(stripped).map(generate_CExpression).or(_lambda178_).orElseGet(_lambda175_);
}
Option<char*> compileAssignment_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ separator = stripped.indexOf('=');
	if (separator < 0) {
		return new_None<char*>();
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ destinationString = stripped.substring(0, separator).strip();
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ sourceString = stripped.substring(separator + 1).strip();
	/*Not a function type: Placeholder[input=Undefined field 'parseExpression' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ source = _this.parseExpression(sourceString);
	/*Not a function type: Placeholder[input=Does not have a type of structure: starttypeof(_this.compileAssignmentContent(destinationString, source) + " = " + source)end]*/ generated = _this.compileAssignmentContent(destinationString, source) + " = " + source.generate();
	return new_Some<char*>(generated);
}
char* compileAssignmentContent_App(void* _ref, char* destinationString, CExpression source) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Undefined field 'compileDefinition' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeDefinition = _this.compileDefinition(destinationString);
	if (/*!maybeDefinition*/.isPresent()) {
		return _this.compileExpression(destinationString);
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'compileDefinition' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]*/ definition = maybeDefinition.get();
	if (/*definition.type instanceof CIdentifier*/(/*var name*/) && /*name*/.equals("var")) {
		/*Not a function type: Placeholder[input=Undefined field 'resolveExpression' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ newType = _this.resolveExpression(source);
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'compileDefinition' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]end]*/ withNewType = definition.withType(newType);
		_this.frames = _this.frames.define(withNewType);
		return withNewType.generate();
	}
	return definition.generate();
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
	if (identifier.value.equals("_this")) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: Frames]*/ maybeCurrentScope = _this.frames.findCurrentScope();
		if (/*maybeCurrentScope instanceof Some*/(/*var currentScope*/)) {
			return new_CStructureType(/*currentScope*/.left.name, /*currentScope*/.left.typeParameters, /*currentScope*/.right);
		}
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: Frames]*/ maybeDefinition = _this.frames.resolve(identifier.value);
	if (/*maybeDefinition instanceof Some*/ < /*CDefinition>*/(/*var found*/)) {
		/*Does not have a type of structure: startfoundend*/ foundType = /*found*/.type;
		if (/*foundType instanceof CTemplateType*/(/*var base*/, /*var typeArguments*/)) {
			/*Not a function type: Placeholder[input=Does not have a type of structure: Frames]*/ maybeStructureType = _this.frames.findStructure(/*base*/);
			if (/*maybeStructureType instanceof Some*/ < /*CStructureType>*/(/*var structureType*/)) {
				return /*structureType*/.withTypeArguments(/*typeArguments*/);
			}
		}
		return foundType;
	}
	return new_Placeholder(identifier.value);
}
Option<char*> parseAndDefineDefinitionAsStatement_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.parseAndDefineDefinition(input).map(generate_CDefinition);
}
Option<CDefinition> parseAndDefineDefinition_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Undefined field 'compileDefinition' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeDefinition = _this.compileDefinition(input);
	if (/*!maybeDefinition*/.isPresent()) {
		return new_None<CDefinition>();
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'compileDefinition' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]*/ definition = maybeDefinition.get();
	_this.frames = _this.frames.define(definition);
	return new_Some<CDefinition>(definition);
}
char* compileExpression_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.parseExpression(input).generate();
}
auto _lambda184_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "<");
};
auto _lambda187_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, ">=");
};
auto _lambda190_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "==");
};
auto _lambda193_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "||");
};
auto _lambda196_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "&&");
};
auto _lambda199_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "-");
};
CExpression parseExpression_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ stripped = input.strip();
	if (stripped.equals("false")) {
		return new_CContent("0");
	}
	if (stripped.equals("true")) {
		return new_CContent("1");
	}
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return new_CContent(stripped);
	}
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return new_CContent(stripped);
	}
	/*Not a function type: Placeholder[input=Undefined field 'compileLambda' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeLambda = _this.compileLambda(stripped);
	if (maybeLambda.isPresent()) {
		return new_CContent(maybeLambda.get());
	}
	/*Not a function type: Placeholder[input=Undefined field 'compileInvocation' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeInvocation = _this.compileInvocation(stripped);
	if (maybeInvocation.isPresent()) {
		return maybeInvocation.get();
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ i = stripped.lastIndexOf(".");
	if (i >= 0) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ child = stripped.substring(0, i).strip();
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ name = stripped.substring(i + 1).strip();
		if (_this.isIdentifier(name)) {
			/*Not a function type: Placeholder[input=Undefined field 'parseExpression' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ newChild = _this.parseExpression(child);
			return new_CFieldAccess(newChild, name);
		}
	}
	if (_this.isIdentifier(stripped)) {
		if (stripped.equals("this")) {
			return new_CIdentifier("_this");
		}
		if (_this.frames.resolve(stripped).isPresent()) {
			return new_CIdentifier(stripped);
		}
	}
	if (stripped.startsWith("switch")) {
		return new_CContent(_this.createName("switch"));
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'compileOperator' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]end]end]end]end]end]*/ maybeOperator = _this.compileOperator(stripped, "+").or(_lambda199_).or(_lambda196_).or(_lambda193_).or(_lambda190_).or(_lambda187_).or(_lambda184_);
	if (maybeOperator.isPresent()) {
		return new_CContent(maybeOperator.get());
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ i2 = stripped.lastIndexOf("::");
	if (i2 >= 0) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ substring = stripped.substring(0, i2);
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ substring1 = stripped.substring(i2 + 2);
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'compileType' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]end]*/ maybeType = _this.compileType(substring).map(generate_CType).orElse("?");
		return new_CContent(substring1 + "_" + maybeType);
	}
	if (_this.isNumber(stripped)) {
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
	return /*!segment*/.isEmpty();
};
auto _lambda210_(auto _ref) {
			/*Not a function type: Placeholder[input=Undefined field 'compileExpression' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ expression = _this.compileExpression(content);
			return "{" + new_CStatement(new_CContent("auto _this = _ref"), 1).generate() + /*new CStatement(new CContent("return "*/ + /*expression),
																																			1).generate()*/ + /*System*/.lineSeparator() + "};" + /*System*/.lineSeparator();
		}Option<char*> compileLambda_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ arrowIndex = stripped.indexOf("->");
	if (arrowIndex >= 0) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ names = stripped.substring(0, arrowIndex).strip();
		/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ content = stripped.substring(arrowIndex + 2);
		/*Not a function type: Placeholder[input=Undefined field 'createName' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ functionName = _this.createName("lambda");
		ArrayList<char*> parameters;
		if (_this.isIdentifier(names)) {
			parameters = /*ArrayList*/.of("auto " + names);
		}
		else 
		if (names.startsWith("(") && names.endsWith(")")) {
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ slice = names.substring(1, names.length() - 1);
			parameters = _this.divide(slice, foldValue_this).map(strip_char*).filter(_lambda207_).map(_lambda204_).toList();
		}
		else {
			return new_None<char*>();
		}
		/*Not a function type: Placeholder[input=Does not have a type of structure: ArrayList<char*>]*/ copy = parameters.copy().addFirst("auto _ref");
		_this.functions = _this.functions.addLast("auto " + functionName + "(" + /*String*/.join(", ", copy.inner) + ") " + _this.compileMethodSegment(content).orElseGet(_lambda210_));
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
					return new Some<CExpression>(new CInvocation(maybeCaller.get(), arguments));
				}
			}
		}*/
	return new_None<CExpression>();
}
Option<CCaller> parseCaller_App(void* _ref, char* caller) {
	App _this = *((App*) _ref);
	if (caller.startsWith("new ")) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ substring = caller.substring("new ".length());
		/*Not a function type: Placeholder[input=Undefined field 'compileType' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']*/ maybeType = _this.compileType(substring);
		if (maybeType.isPresent()) {
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'compileType' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]*/ type = maybeType.get();
			return new_Some<CCaller>(new_CConstruction(type));
		}
	}
	return new_Some<CCaller>(_this.parseExpression(caller));
}
Option<char*> compileOperator_App(void* _ref, char* stripped, char* separator) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ i1 = stripped.indexOf(separator);
	if (i1 >= 0) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ substring = stripped.substring(0, i1);
		/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ substring1 = stripped.substring(i1 + separator.length());
		return new_Some<char*>(_this.compileExpression(substring) + " " + separator + " " + _this.compileExpression(substring1));
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
	return /*!slice*/.isEmpty();
};
ArrayList<CDefinition> compileParametersToList_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.divide(input, foldValue_this).map(strip_char*).filter(_lambda218_).map(compileDefinition_this).flatMap(stream_Option).toList();
}
auto _lambda222_(auto _ref, auto segment) {
	auto _this = _ref;
	return /*!segment*/.isEmpty();
};
auto _lambda224_(auto _ref, auto cType) {
	auto _this = _ref;
	return new_CDefinition(finalTypeParameters, /*cType*/, name);
};
auto _lambda226_(auto _ref, auto cType) {
	auto _this = _ref;
	return new_CDefinition(/*ArrayList*/.empty(), /*cType*/, name);
};
Option<CDefinition> compileDefinition_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ nameSeparator = input.lastIndexOf(" ");
	if (nameSeparator < 0) {
		return new_None<CDefinition>();
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ beforeName = input.substring(0, nameSeparator);
	/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ name = input.substring(nameSeparator + 1).strip();
	if (/*!this*/.isIdentifier(name)) {
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
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ beforeType = beforeName.substring(0, typeSeparator).strip();
		/*Not a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/ typeParameters = /*ArrayList*/. < /*String>empty*/();
		if (beforeType.endsWith(">")) {
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]*/ slice = beforeType.substring(0, beforeType.length() - 1);
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]end]*/ i = slice.indexOf("<");
			if (i >= 0) {
				/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]end]end]*/ typeParametersString = slice.substring(i + 1);
				typeParameters = _this.divide(typeParametersString, foldValue_this).map(strip_char*).filter(_lambda222_).collect(new_ListCollector<char*>());
			}
		}
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ type = beforeName.substring(typeSeparator + 1).strip();
		/*Not a function type: Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/ finalTypeParameters = typeParameters;
		return _this.compileType(type).map(_lambda224_);
	}
	return _this.compileType(beforeName).map(_lambda226_);
}
auto _lambda234_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
Option<CType> compileType_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Not a function type: Placeholder[input=Does not have a type of structure: char*]*/ stripped = input.strip();/*

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
	if (stripped.endsWith("[]")) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ slice = stripped.substring(0, stripped.length() - 2);
		return _this.compileType(slice).map(new_CPointerType);
	}
	if (stripped.equals("String")) {
		return new_Some<CType>(new_CPointerType(/*CPrimitiveType*/.Char));
	}
	if (stripped.endsWith(">")) {
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]*/ withoutEnd = stripped.substring(0, stripped.length() - 1);
		/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ i = withoutEnd.indexOf("<");
		if (i >= 0) {
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ base = withoutEnd.substring(0, i);
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: char*]end]end]*/ typeArguments = withoutEnd.substring(i + 1);
			/*Not a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Does not have a type of structure: startNot a function type: Placeholder[input=Undefined field 'divide' in 'App' of type 'CStructureType[name=App, typeParameters=[], fields=[Frames frames, ArrayList<char*> globals, ArrayList<char*> forwardDeclarations, ArrayList<char*> structures, ArrayList<char*> sealedStructures, ArrayList<char*> functions, int counter, int depth]]']end]end]end]end]end]*/ list = _this.divide(typeArguments, foldValue_this).map(strip_char*).filter(_lambda234_).map(compileType_this).flatMap(stream_Option).toList();
			return new_Some<CType>(new_CTemplateType(base, list));
		}
	}
	if (_this.isIdentifier(stripped)) {
		if (stripped.equals("public") || stripped.equals("private")) {
			return new_None<CType>();
		}
		return new_Some<CType>(new_CIdentifier(stripped));
	}
	return new_None<CType>();
}
State foldValue_App(void* _ref, State state, char next) {
	App _this = *((App*) _ref);
	if (next == ',' && state.isLevel()) {
		return state.advance();
	}
	/*Not a function type: Placeholder[input=Does not have a type of structure: State]*/ appended = state.append(next);
	if (next == ' - ') {
		if (appended.peek() == '>') {
			return appended.popAndAppendToOption().orElse(appended);
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