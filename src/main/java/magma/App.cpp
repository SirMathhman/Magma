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
template <typename T>
struct SingleHead;
template <typename T>
struct EmptyHead;
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
/*
*/struct CPrimitiveType {

	char* content;};
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
template <typename T>
struct SingleHead {

	T element;
	int retrieved;};
template <typename T>
struct EmptyHead {
};
template <typename T>
struct Stream {
	Head<T> head;
};
template <typename T>
struct ArrayList {

	List<T> inner;};
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
	ArrayList<CType> list;
};
struct CIdentifier {
	char* input;
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
	int index;};
struct CDefinition {
	ArrayList<char*> typeParameters;
	CType cType;
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
	Head<T> head;};
template <typename T>
struct ListHead {

	ArrayList<T> list;
	int counter;};
template <typename T>
struct ListCollector {
};
template <typename T, typename R>
struct FlatMapHead {

	Head<T> head;
	Function<T, Stream<R>> mapper;
	Head<R> current;};
struct Joiner {
	char* delimiter;
};
struct App {

	ArrayList<CStructureHeader> structureHeaders;
	ArrayList<char*> globals;
	ArrayList<char*> forwardDeclarations;
	ArrayList<char*> structures;
	ArrayList<char*> sealedStructures;
	ArrayList<char*> functions;
	int counter;
	int depth;};
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
	CPrimitiveTypeTag,
	CPointerTypeTag,
	CTemplateTypeTag,
	PlaceholderTag
};
union CTypeData {
	CIdentifier cidentifier;
	CPrimitiveType cprimitivetype;
	CPointerType cpointertype;
	CTemplateType ctemplatetype;
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
CPrimitiveType VoidValue = CPrimitiveType { "void" };
CPrimitiveType CharValue = CPrimitiveType { "char" };
CPrimitiveType IntValue = CPrimitiveType { "int" };
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
template <typename T, typename R>
R apply_Function(void* _ref, T arg);
template <typename A, typename B, typename R>
R apply_BiFunction(void* _ref, A left, B right);
template <typename T>
void accept_Consumer(void* _ref, T value);
template <typename T>
Option<T> next_Head(void* _ref);
template <typename T, typename C>
C createInitial_Collector(void* _ref);
template <typename T, typename C>
C fold_Collector(void* _ref, C current, T element);
template <typename T>
T get_Supplier(void* _ref);
char* generate_CType(void* _ref);
char* getSimpleName_CType(void* _ref);
char* generate_CFunctionHeader(void* _ref);
template <typename T, typename T>
Option<T> of_Option(void* _ref, T element) {
	Option<T> _this = *((Option*) _ref);
	return new_Some<T>(element);
}
template <typename T, typename T>
Option<T> empty_Option(void* _ref) {
	Option<T> _this = *((Option*) _ref);
	return new_None<T>();
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
template <typename T>
int test_Predicate(void* _ref, T element);
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
	_this.retrieved = /*false*/;
}
template <typename T>
Option<T> next_SingleHead(void* _ref) {
	SingleHead<T> _this = *((SingleHead*) _ref);
	if (_this.retrieved) {
		return new_None<T>();
	}
	_this.retrieved = /*true*/;
	return new_Some<T>(_this.element);
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
template <typename T, typename T>
Stream<T> of_Stream(void* _ref, T element) {
	Stream<T> _this = *((Stream*) _ref);
	return new_Stream<T>(new_SingleHead<T>(element));
}
template <typename T, typename T>
Stream<T> empty_Stream(void* _ref) {
	Stream<T> _this = *((Stream*) _ref);
	return new_Stream<T>(new_EmptyHead<T>());
}
template <typename T, typename R>
Stream<R> map_Stream(void* _ref, Function<T, R> mapper) {
	Stream<T> _this = *((Stream*) _ref);
	return new_Stream<R>(new_MapHead<T, R>(_this.head, mapper));
}
template <typename T, typename R>
R fold_Stream(void* _ref, R initial, BiFunction<R, T, R> folder) {
	Stream<T> _this = *((Stream*) _ref);
	var current = initial;
	while (/*true*/) {
		var maybeNext = _this.head.next();
		if (/*maybeNext instanceof Some*/ < /*T>*/(/*var next*/)) {
			/*current*/ = folder.apply(/*current*/, /*next*/);
		}
		else {
			return /*current*/;
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
		return /*Stream*/.of(element);
	}
	return /*Stream*/.empty();
}
template <typename T, typename R>
Stream<R> flatMap_Stream(void* _ref, Function<T, Stream<R>> mapper) {
	Stream<T> _this = *((Stream*) _ref);
	return new_Stream<R>(new_FlatMapHead<T, R>(_this.head, mapper));
}
template <typename T>
ArrayList<T> new_ArrayList(void* _ref, List<T> inner) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	_this.inner = inner;
}
template <typename T>
ArrayList<T> new_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	_this(new_java.util.ArrayList<T>());
}
template <typename T, typename T>
ArrayList<T> of_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return new_ArrayList<T>(new_java.util.ArrayList<T>(/*Arrays*/.asList(/*elements*/)));
}
template <typename T, typename T>
ArrayList<T> empty_ArrayList(void* _ref) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return new_ArrayList<T>();
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
	return new_ArrayList<T>(new_java.util.ArrayList<T>(_this.inner));
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
ArrayList<T> addAllLast_ArrayList(void* _ref, ArrayList<T> others) {
	ArrayList<T> _this = *((ArrayList*) _ref);
	return others.stream().fold(_this, addLast_ArrayList);
}
template <typename T, typename X>
Result<T, X> toResult_Err(void* _ref){
	Err<T, X> _this = *((Err<T, X>*) _ref);
	ResultData<T, X> data;
	data.err = _this;
	return Result<T, X> { ErrTag, data };
}
template <typename T, typename X>
Result<T, X> toResult_Ok(void* _ref){
	Ok<T, X> _this = *((Ok<T, X>*) _ref);
	ResultData<T, X> data;
	data.ok = _this;
	return Result<T, X> { OkTag, data };
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
	return /*false*/;
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
	return /*true*/;
}
template <typename T>
Stream<T> stream_Some(void* _ref) {
	Some<T> _this = *((Some*) _ref);
	return /*Stream*/.of(_this.value);
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
	return /*true*/;
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
	return /*false*/;
}
template <typename T>
Stream<T> stream_None(void* _ref) {
	None<T> _this = *((None*) _ref);
	return /*Stream*/.empty();
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
CType toCType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CTypeData data;
	data.ctemplatetype = _this;
	return CType { CTemplateTypeTag, data };
}
char* generate_CTemplateType(void* _ref) {
	CTemplateType _this = *((CTemplateType*) _ref);
	var joined = _this.list.stream().map(generate_CType).collect(new_Joiner(", "));
	return _this.base + "<" + joined + ">";
}
char* getSimpleName_CTemplateType(void* _ref) {
	CTemplateType _this = *((CTemplateType*) _ref);
	return _this.base;
}
CType toCType_CIdentifier(void* _ref){
	CIdentifier _this = *((CIdentifier*) _ref);
	CTypeData data;
	data.cidentifier = _this;
	return CType { CIdentifierTag, data };
}
char* generate_CIdentifier(void* _ref) {
	CIdentifier _this = *((CIdentifier*) _ref);
	return _this.input;
}
char* toString_CIdentifier(void* _ref) {
	CIdentifier _this = *((CIdentifier*) _ref);
	return "";
}
char* getSimpleName_CIdentifier(void* _ref) {
	CIdentifier _this = *((CIdentifier*) _ref);
	return _this.input;
}
char* wrap_Placeholder(void* _ref, char* input) {
	Placeholder _this = *((Placeholder*) _ref);
	var replaced = input.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
char* generate_Placeholder(void* _ref) {
	Placeholder _this = *((Placeholder*) _ref);
	return /*wrap*/(_this.input);
}
char* getSimpleName_Placeholder(void* _ref) {
	Placeholder _this = *((Placeholder*) _ref);
	return _this.generate();
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
		var counter = _this.index;
		_this.index++;
		var element = _this.input.charAt(/*counter*/);
		return /*Option*/.of(/*element*/);
	}
	else {
		return /*Option*/.empty();
	}
}
Stream<char*> stream_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.segments.stream();
}
auto _lambda3_(auto _ref, auto next) {
		var appended = _this.append(/*next*/);
		return new_Tuple<char, State>(/*next*/, /*appended*/);
	}Option<Tuple<char, State>> popAndAppendToTuple_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.pop().map(_lambda3_);
}
Option<State> popAndAppendToOption_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.popAndAppendToTuple().map(right_Tuple);
}
char peek_State(void* _ref) {
	State _this = *((State*) _ref);
	return _this.input.charAt(_this.index);
}
CFunctionHeader toCFunctionHeader_CDefinition(void* _ref){
	CDefinition _this = *((CDefinition*) _ref);
	CFunctionHeaderData data;
	data.cdefinition = _this;
	return CFunctionHeader { CDefinitionTag, data };
}
char* generate_CDefinition(void* _ref) {
	CDefinition _this = *((CDefinition*) _ref);
	return _this.cType().generate() + " " + _this.name();
}
CType toType_CStructureHeader(void* _ref) {
	CStructureHeader _this = *((CStructureHeader*) _ref);
	if (_this.typeParameters.isEmpty()) {
		return new_CIdentifier(_this.name);
	}
	var list = _this.typeParameters.stream(). < /*CType>map*/(new_CIdentifier).toList();
	return new_CTemplateType(_this.name, /*list*/);
}
char* generate_CStructureHeader(void* _ref) {
	CStructureHeader _this = *((CStructureHeader*) _ref);
	return /*App*/.createTemplateString(_this.typeParameters()) + "struct " + _this.name();
}
char* createTemplateString_CStructureHeader(void* _ref) {
	CStructureHeader _this = *((CStructureHeader*) _ref);
	return /*App*/.createTemplateString(_this.typeParameters);
}
char* generate_CStructure(void* _ref) {
	CStructure _this = *((CStructure*) _ref);
	return _this.CStructureHeader().generate() + " {" + this.fields() + "};";
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
		var element = _this.list.inner.get(_this.counter);
		_this.counter++;
		return /*Option*/.of(/*element*/);
	}
	return /*Option*/.empty();
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
	while (/*true*/) {
		var maybeNext = _this.current.next();
		if (/*maybeNext*/.isPresent()) {
			return /*maybeNext*/;
		}
		var maybeOuter = _this.head.next();
		if (/*maybeOuter*/.isEmpty()) {
			return /*Option*/.empty();
		}
		_this.current = _this.mapper.apply(/*maybeOuter*/.get()).head;
	}
}
Collector<char*, char*> toCollector_Joiner(void* _ref){
	Joiner _this = *((Joiner*) _ref);
	CollectorData data;
	data.joiner = _this;
	return Collector<char*, char*> { JoinerTag, data };
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
/*private ArrayList<ArrayList<CDefinition>> definitions = ArrayList.empty*/(void* _ref);
App new_App(void* _ref) {
	App _this = *((App*) _ref);
	_this.globals = /*ArrayList*/.empty();
	_this.structureHeaders = /*ArrayList*/.empty();
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
auto _lambda7_(auto _ref, auto slice) {
	auto _this = _ref;
	return "typename " + /*slice*/;
};
char* createTemplateString_App(void* _ref, ArrayList<char*> typeParameters) {
	App _this = *((App*) _ref);
	char* templateString;
	if (typeParameters.isEmpty()) {
		/*templateString*/ = "";
	}
	else {
		var collect = typeParameters.stream().map(_lambda7_).collect(new_Joiner(", "));
		/*templateString*/ = "template <" + /*collect*/ + ">" + /*System*/.lineSeparator();
	}
	return /*templateString*/;
}
Option<IOException> run_App(void* _ref) {
	App _this = *((App*) _ref);
	var source = /*Paths*/.get(".", "src", "main", "java", "magma", "App.java");
	var input = _this.readString(/*source*/);
	return _switch9_;
}
auto _lambda11_(auto _ref) {
	auto _this = _ref;
	return _this.compileNative(/*target*/);
};
Option<IOException> compilePath_App(void* _ref, Path source, char* input) {
	App _this = *((App*) _ref);
	var target = source.resolveSibling("App.cpp");
	var output = _this.compile(input);
	return _this.writeString(/*target*/, /*output*/).or(_lambda11_);
}
Option<IOException> compileNative_App(void* _ref, Path target) {
	App _this = *((App*) _ref);
	var clang = _this.startCommand(/*ArrayList*/.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));
	return _switch13_;
}
Option<IOException> waitForProcess_App(void* _ref, Process process) {
	App _this = *((App*) _ref);
	return _switch15_;
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
	var compiled = _this.compileStatements(input, compileRootSegment_this);
	var joinedForwardDeclarations = /*String*/.join("", _this.forwardDeclarations.inner);
	var joinedFunctions = /*String*/.join("", _this.functions.inner);
	var joinedStructures = /*String*/.join("", _this.structures.inner);
	var joinedSealedStructures = /*String*/.join("", _this.sealedStructures.inner);
	var joinedGlobals = /*String*/.join("", _this.globals.inner);
	return /*joinedForwardDeclarations*/ + /*compiled*/ + /*joinedStructures*/ + /*joinedSealedStructures*/ + /*joinedGlobals*/ + /*joinedFunctions*/ + "int main(){" + System.lineSeparator() + "\treturn " + "0;" + System.lineSeparator() +
					 "}";
}
char* compileStatements_App(void* _ref, char* input, Function<char*, char*> mapper) {
	App _this = *((App*) _ref);
	return _this.divide(input, foldStatement_this).map(mapper).collect(new_Joiner(""));
}
Stream<char*> divide_App(void* _ref, char* input, BiFunction<State, char, State> folder) {
	App _this = *((App*) _ref);
	var current = new_State(input);
	while (/*true*/) {
		var maybeNext = /*current*/.pop();
		if (/*maybeNext*/.isEmpty()) {
			break;
		}
		/*current*/ = _this.foldEscaped(/*current*/, /*maybeNext*/.get(), folder);
	}
	return /*current*/.advance().stream();
}
State foldEscaped_App(void* _ref, State current, char next, BiFunction<State, char, State> folder) {
	App _this = *((App*) _ref);
	if (next == '\'') {
		return current.append(next).popAndAppendToTuple().map(foldSingleEscapeChar_this).flatMap(popAndAppendToOption_State).orElse(current);
	}
	if (next == '\"') {
		var current0 = current.append(next);
		while (/*true*/) {
			var maybeTuple = /*current0*/.popAndAppendToTuple();
			if (/*maybeTuple*/.isEmpty()) {
				break;
			}
			var tuple = /*maybeTuple*/.get();
			/*current0*/ = /*tuple*/.right;
			var nextInQuotes = /*tuple*/.left;
			if (/*nextInQuotes*/ == '\\') {
				/*current0*/ = /*current0*/.popAndAppendToOption().orElse(/*current0*/);
				continue;
			}
			if (/*nextInQuotes*/ == '\"') {
				break;
			}
		}
		return /*current0*/;
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
	var appended = state.append(c);
	if (c == ';' && /*appended*/.isLevel()) {
		return /*appended*/.advance();
	}
	if (c == '}' && /*appended*/.isShallow()) {
		State state1;
		if (/*appended*/.peek() == ';') {
			/*state1*/ = /*appended*/.popAndAppendToOption().orElse(/*appended*/);
		}
		else {
			/*state1*/ = /*appended*/;
		}
		return /*state1*/.advance().exit();
	}/*

		if (c == '{' || c == '(') {
			return appended.enter();
		}*/
	if (c == '}' || c == ') /*') {
			return appended.exit();
		}*/
	return /*appended*/;
}
auto _lambda17_(auto _ref) {
	auto _this = _ref;
	return /*Placeholder*/.wrap(input);
};
char* compileRootSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	var stripped = input.strip();
	if (/*stripped.startsWith("package ") || stripped*/.startsWith("import ")) {
		return "";
	}
	return _this.compileStructure("class", /*stripped*/).orElseGet(_lambda17_);
}
auto _lambda22_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
auto _lambda27_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
auto _lambda31_(auto _ref, auto content1) {
	auto _this = _ref;
	return _this.generateWithIndent(/*content1*/, 1);
};
auto _lambda34_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*slice*/ + "Tag";
};
auto _lambda38_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*System*/.lineSeparator() + "\t" + slice + typeArguments + " " + slice.toLowerCase() + ";";
};
auto _lambda42_(auto _ref, auto slice) {
	auto _this = _ref;
	return _this.generateStatement(/*slice*/, 1);
};
Option<char*> compileStructure_App(void* _ref, char* type, char* input) {
	App _this = *((App*) _ref);
	var classIndex = input.indexOf(type);
	if (/*classIndex*/ >= 0) {
		var afterKeyword = input.substring(/*classIndex*/ + type.length());
		var contentStart = /*afterKeyword*/.indexOf("{");
		if (/*contentStart*/ >= 0) {
			var beforeContent = /*afterKeyword*/.substring(0, /*contentStart*/).strip();
			var withEnd = /*afterKeyword*/.substring(/*contentStart*/ + "{".length()).strip();
			if (/*withEnd*/.endsWith("}")) {
				var content = /*withEnd*/.substring(0, /*withEnd*/.length() - 1);
				var permitsIndex = /*beforeContent*/.indexOf("permits");
				var variants = /*ArrayList*/. < /*String>empty*/();
				if (/*permitsIndex*/ >= 0) {
					var variantsArray = /*beforeContent*/.substring(/*permitsIndex*/ + "permits".length()).split(/*Pattern*/.quote(","));
					/*beforeContent*/ = /*beforeContent*/.substring(0, /*permitsIndex*/).strip();
					/*variants*/ = new_ArrayList<char*>(/*Arrays*/.stream(/*variantsArray*/).map(strip_char*).filter(_lambda22_).toList());
				}
				var implementsIndex = /*beforeContent*/.indexOf("implements");
				Option<CType> maybeInterfaceType = /*Option*/.empty();
				if (/*implementsIndex*/ >= 0) {
					var slice = /*beforeContent*/.substring(/*implementsIndex*/ + "implements".length()).strip();
					/*maybeInterfaceType*/ = _this.compileType(/*slice*/);
					/*beforeContent*/ = /*beforeContent*/.substring(0, /*implementsIndex*/).strip();
				}
				var recordFields = /*ArrayList*/. < /*CDefinition>empty*/();
				if (/*beforeContent.endsWith(")"*/) /*) {
						final var slice = beforeContent.substring(0, beforeContent.length() - 1);
						final var i = slice.indexOf("(");
						if (i >= 0) {
							final var params = slice.substring(i + 1);
							beforeContent = slice.substring(0, i).strip();

							recordFields = this.compileParametersToList(params);
						}
					}*/
				var typeParameters = /*ArrayList*/. < /*String>empty*/();
				if (/*beforeContent*/.endsWith(">")) {
					var withoutEnd = /*beforeContent*/.substring(0, /*beforeContent*/.length() - 1);
					var typeParamStart = /*withoutEnd*/.indexOf("<");
					if (/*typeParamStart*/ >= 0) {
						/*beforeContent*/ = /*withoutEnd*/.substring(0, /*typeParamStart*/);
						var typeParamsArray = /*withoutEnd*/.substring(/*typeParamStart*/ + 1).split(/*Pattern*/.quote(","));
						/*typeParameters*/ = new_ArrayList<char*>(/*Arrays*/.stream(/*typeParamsArray*/).map(strip_char*).filter(_lambda27_).toList());
					}
				}
				if (/*!this*/.isIdentifier(/*beforeContent*/)) {
					return /*Option*/.empty();
				}
				var templateString = /*App*/.createTemplateString(/*typeParameters*/);
				char* dependencies;
				if (/*variants*/.isEmpty()) {
					/*dependencies*/ = "";
				}
				else {
					var enumFields = /*variants*/.stream().map(_lambda34_).map(_lambda31_).collect(new_Joiner(","));
					var typeArguments = _this.joinTypeArguments(/*typeParameters*/);
					var unionFields = /*variants*/.stream().map(_lambda38_).collect(new_Joiner(""));
					/*dependencies*/ = "enum " + /*beforeContent*/ + "Tag {" + /*enumFields*/ + /*System*/.lineSeparator() + "};" + /*System*/.lineSeparator() + /*templateString*/ + "union " + /*beforeContent*/ + "Data {" + /*unionFields*/ + /*System*/.lineSeparator() + "};" + /*System*/.lineSeparator();
				}
				char* fields;
				if (/*variants*/.isEmpty()) {
					/*fields*/ = /*recordFields*/.stream().map(generate_CDefinition).map(_lambda42_).collect(new_Joiner(""));
				}
				else {
					/*fields*/ = /*this.generateStatement(beforeContent*/ + /*"Tag tag", 1)*/ + _this.generateStatement(/*beforeContent*/ + "Data" + this.joinTypeArguments(typeParameters) + " " + "data", 1);
				}
				if (/*maybeInterfaceType*/.isPresent()) {
					var interfaceType = /*maybeInterfaceType*/.get();
					var joinedTypeArguments = _this.joinTypeArguments(/*typeParameters*/);
					var thisType = /*beforeContent*/ + /*joinedTypeArguments*/;
					_this.functions = _this.functions.addLast(/*templateString*/ + /*interfaceType*/.generate() + " to" + /*interfaceType*/.getSimpleName() + "_" + /*beforeContent*/ + "(void* _ref" + "){" + /*this.generateStatement(thisType*/ + " _this = *((" + /*thisType*/ + /*"*) _ref)", 1)*/ + _this.generateStatement(/*interfaceType.getSimpleName(*/) + "Data" + /*joinedTypeArguments*/ + /*" data", 1)*/ + /*this.generateStatement("data."*/ + /*beforeContent*/.toLowerCase() + /*" = _this", 1)*/ + /*this.generateStatement(
										"return "*/ + /*interfaceType*/.generate() + " { " + /*beforeContent*/ + "Tag, " + /*"data }",
										1)*/ + /*System*/.lineSeparator() + "}" + /*System*/.lineSeparator());
				}
				_this.forwardDeclarations = _this.forwardDeclarations.addLast(/*templateString*/ + "struct " + /*beforeContent*/ + ";" + /*System*/.lineSeparator());
				var header = new_CStructureHeader(/*typeParameters*/, /*beforeContent*/);
				_this.structureHeaders = _this.structureHeaders.addLast(/*header*/);
				var outputContent = /*fields*/ + /*System*/.lineSeparator() + _this.compileStatements(/*content*/, compileClassSegment_this);
				var generated = /*dependencies*/ + new_CStructure(/*header*/, /*outputContent*/).generate() + /*System*/.lineSeparator();
				_this.structureHeaders = _this.structureHeaders.removeLast();
				if (/*variants*/.isEmpty()) {
					_this.structures = _this.structures.addLast(/*generated*/);
				}
				else {
					_this.sealedStructures = _this.sealedStructures.addLast(/*generated*/);
				}
				return /*Option*/.of("");
			}
		}
	}
	return /*Option*/.empty();
}
char* joinTypeArguments_App(void* _ref, ArrayList<char*> typeParameters) {
	App _this = *((App*) _ref);
	char* joinedTypeArguments;
	if (typeParameters.isEmpty()) {
		/*joinedTypeArguments*/ = "";
	}
	else {
		/*joinedTypeArguments*/ = "<" + String.join(", ", typeParameters.inner) + ">";
	}
	return /*joinedTypeArguments*/;
}
char* generateStatement_App(void* _ref, char* content, int depth) {
	App _this = *((App*) _ref);
	return _this.generateWithIndent(content, depth) + ";";
}
char* generateWithIndent_App(void* _ref, char* content, int depth) {
	App _this = *((App*) _ref);
	return _this.generateIndent(depth) + content;
}
char* generateIndent_App(void* _ref, int depth) {
	App _this = *((App*) _ref);
	return /*System*/.lineSeparator() + "\t".repeat(depth);
}
int isIdentifier_App(void* _ref, char* input) {
	App _this = *((App*) _ref);/*
		for (var i = 0; i < input.length(); i++) {
			final var next = input.charAt(i);
			if (Character.isLetter(next) || (i != 0 && Character.isDigit(next))) {continue;}
			return false;
		}*/
	return /*true*/;
}
auto _lambda44_(auto _ref) {
	auto _this = _ref;
	return _this.compileDefinitionToField(/*slice*/);
};
auto _lambda46_(auto _ref) {
	auto _this = _ref;
	return /*Placeholder*/.wrap(input);
};
char* compileClassSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	if (input.isBlank()) {
		return "";
	}
	var maybeClass = _this.compileStructure("class", input);
	if (/*maybeClass*/.isPresent()) {
		return /*maybeClass*/.get();
	}
	var maybeInterface = _this.compileStructure("interface", input);
	if (/*maybeInterface*/.isPresent()) {
		return /*maybeInterface*/.get();
	}
	var maybeRecord = _this.compileStructure("record", input);
	if (/*maybeRecord*/.isPresent()) {
		return /*maybeRecord*/.get();
	}
	var maybeEnum = _this.compileStructure("enum", input);
	if (/*maybeEnum*/.isPresent()) {
		return /*maybeEnum*/.get();
	}
	if (input.endsWith(";")) {
		var slice = input.substring(0, input.length() - 1);
		var maybeClassStatement = _this.compileEnumValues(/*slice*/).or(_lambda44_);
		if (/*maybeClassStatement*/.isPresent()) {
			return /*maybeClassStatement*/.get();
		}
	}
	return _this.compileMethod(input).orElseGet(_lambda46_);
}
Option<char*> compileMethod_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	var paramStart = input.indexOf("(");
	if (/*paramStart*/ < 0) {
		return /*Option*/.empty();
	}
	var definition = input.substring(0, /*paramStart*/).strip();
	var withParams = input.substring(/*paramStart*/ + 1);
	var paramEnd = /*withParams*/.indexOf(")");
	if (/*paramEnd*/ < 0) {
		return /*Option*/.empty();
	}
	var inputParams = /*withParams*/.substring(0, /*paramEnd*/).strip();
	var withBraces = /*withParams*/.substring(/*paramEnd*/ + 1).strip();
	var header = _this.compileFunctionHeader(/*definition*/);
	var params = _this.compileParametersToList(/*inputParams*/);
	var outputParams = /*params*/.copy().addFirst(new_CDefinition(/*ArrayList*/.empty(), new_CPointerType(/*CPrimitiveType*/.Void), "_ref")).stream().map(generate_CDefinition).collect(new_Joiner(", "));
	var headerWithParameters = /*header*/.generate() + "(" + outputParams + ")";
	ArrayList<char*> typeParameters;
	if (/*header instanceof CDefinition definition1*/) {
		/*typeParameters*/ = _this.structureHeaders.getLast().typeParameters.copy().addAllLast(/*definition1*/.typeParameters);
	}
	else {
		/*typeParameters*/ = _this.structureHeaders.getLast().typeParameters.copy().addAllLast(/*ArrayList*/.empty());
	}
	var templateString = /*createTemplateString*/(/*typeParameters*/);
	char* generated;
	if (/*withBraces*/.startsWith("{") && /*withBraces*/.endsWith("}")) {
		var content = /*withBraces*/.substring(1, /*withBraces*/.length() - 1);
		var currentStructureType = _this.structureHeaders.getLast();
		var thisDefinition = _this.generateStatement(/*currentStructureType*/.toType().generate() + " _this = *((" + currentStructureType.name() + "*) _ref)", 1);
		_this.definitions = _this.definitions.addLast(/*params*/);
		/*generated*/ = /*templateString*/ + /*headerWithParameters*/ + " {" + /*thisDefinition*/ + _this.compileMethodSegments(/*content*/) + /*System*/.lineSeparator() + "}" + /*System*/.lineSeparator();
		_this.definitions = _this.definitions.removeLast();
	}
	else {
		/*generated*/ = /*templateString*/ + /*headerWithParameters*/ + ";" + /*System*/.lineSeparator();
	}
	_this.functions = _this.functions.addLast(/*generated*/);
	return /*Option*/.of("");
}
auto _lambda48_(auto _ref) {
	auto _this = _ref;
	return new_Placeholder(input);
};
auto _lambda51_(auto _ref) {
	auto _this = _ref;
	return _this.compileConstructor(input);
};
auto _lambda54_(auto _ref, auto item) {
	auto _this = _ref;
	return new_CDefinition(/*item*/.typeParameters, /*item*/.cType, /*item*/.name + "_" + _this.structureHeaders.getLast().name);
};
CFunctionHeader compileFunctionHeader_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.compileDefinition(input). < /*CFunctionHeader>map*/(_lambda54_).or(_lambda51_).orElseGet(_lambda48_);
}
auto _lambda56_(auto _ref, auto content) {
	auto _this = _ref;
	return _this.generateStatement(/*content*/, 1);
};
Option<char*> compileDefinitionToField_App(void* _ref, char* slice) {
	App _this = *((App*) _ref);
	return _this.compileDefinition(slice).map(generate_CDefinition).map(_lambda56_);
}
char* compileMethodSegments_App(void* _ref, char* content) {
	App _this = *((App*) _ref);
	return _this.compileStatements(content, compileMethodSegmentOrPlaceholder_this);
}
Option<CFunctionHeader> compileConstructor_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	var i = input.lastIndexOf(" ");
	if (/*i*/ >= 0) {
		var name = input.substring(/*i*/ + 1).strip();
		if (_this.isIdentifier(/*name*/)) {
			var peek = _this.structureHeaders.getLast();
			return /*Option*/.of(new_CDefinition(/*ArrayList*/.empty(), /*peek*/.toType(), "new_" + /*peek*/.name));
		}
	}
	else {
		if (_this.isIdentifier(input)) {
			var structName = _this.structureHeaders.getLast().name;
			return /*Option*/.of(new_CDefinition(/*ArrayList*/.empty(), new_CIdentifier(/*structName*/), "new_" + /*structName*/));
		}
	}
	return /*Option*/.empty();
}
auto _lambda61_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
Option<char*> compileEnumValues_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	var segments = new_ArrayList<char*>(/*Arrays*/.stream(input.split(/*Pattern*/.quote(","))).map(strip_char*).filter(_lambda61_).toList());/*

		for (var segment : segments.inner) {
			final var stripped = segment.strip();
			final var maybeEnumValue = this.compileEnumValue(stripped);
			if (maybeEnumValue.isPresent()) {
				this.globals = this.globals.addLast(maybeEnumValue.get());
			} else {
				return Option.empty();
			}
		}*/
	return /*Option*/.of("");
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
					final var structureName = this.structureHeaders.getLast().name;
					return Option.of(structureName + " " + name + "Value = " + structureName + " { " + arguments + " };" +
													 System.lineSeparator());
				}
			}
		}*/
	return /*Option*/.empty();
}
auto _lambda63_(auto _ref) {
	auto _this = _ref;
	return /*Placeholder*/.wrap(input);
};
char* compileMethodSegmentOrPlaceholder_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.compileMethodSegment(input).orElseGet(_lambda63_);
}
Option<char*> compileMethodSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	var stripped = input.strip();
	if (/*stripped.isEmpty() || stripped.startsWith("try ") || stripped*/.startsWith("catch ")) {
		return /*Option*/.of("");
	}
	if (/*stripped*/.startsWith("{") && /*stripped*/.endsWith("}")) {
		var content = /*stripped*/.substring(1, /*stripped*/.length() - 1);
		_this.depth++;
		var compiled = _this.compileMethodSegments(/*content*/);
		/*this.depth--*/;
		return /*Option*/.of("{" + compiled + this.generateIndent(this.depth) + "}");
	}
	var maybeIf = _this.compileConditional(/*stripped*/, "if");
	if (/*maybeIf*/.isPresent()) {
		return /*maybeIf*/;
	}
	var maybeWhile = _this.compileConditional(/*stripped*/, "while");
	if (/*maybeWhile*/.isPresent()) {
		return /*maybeWhile*/;
	}
	if (/*stripped*/.endsWith(";")) {
		var slice = /*stripped*/.substring(0, /*stripped*/.length() - 1);
		return /*Option*/.of(_this.generateStatement(_this.compileMethodStatement(/*slice*/), _this.depth));
	}
	if (/*stripped*/.startsWith("else ")) {
		var substring = /*stripped*/.substring(5);
		return /*Option*/.of(_this.generateIndent(_this.depth) + "else " + _this.compileMethodSegmentOrPlaceholder(/*substring*/));
	}
	return /*Option*/.empty();
}
Option<char*> compileConditional_App(void* _ref, char* input, char* type) {
	App _this = *((App*) _ref);
	if (input.startsWith(type)) {
		var substring = input.substring(type.length()).strip();/*
			if (substring.startsWith("(")) {
				final var withCondition = substring.substring(1);
				final var conditionEnd = this.findConditionEnd(withCondition);

				if (conditionEnd >= 0) {
					final var condition = withCondition.substring(0, conditionEnd).strip();
					final var substring2 = withCondition.substring(conditionEnd + 1).strip();
					return Option.of(this.generateIndent(this.depth) + type + " (" + this.compileExpression(condition) + ") " +
													 this.compileMethodSegmentOrPlaceholder(substring2));
				}
			}*/
	}
	return /*Option*/.empty();
}
int findConditionEnd_App(void* _ref, char* withCondition) {
	App _this = *((App*) _ref);
	var conditionEnd =  - 1;
	var depth = 0;/*
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
	return /*conditionEnd*/;
}
auto _lambda65_(auto _ref) {
	auto _this = _ref;
	return _this.compileExpression(/*substring*/);
};
auto _lambda67_(auto _ref) {
	auto _this = _ref;
	return /*Placeholder*/.wrap(/*stripped*/);
};
auto _lambda70_(auto _ref) {
	auto _this = _ref;
	return _this.compileDefinition(input).map(generate_CDefinition);
};
char* compileMethodStatement_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	var stripped = input.strip();
	if (/*stripped*/.startsWith("return ")) {
		var slice = /*stripped*/.substring("return ".length()).strip();
		return "return " + _this.compileExpression(/*slice*/);
	}
	var separator = /*stripped*/.indexOf('=');
	if (/*separator*/ >= 0) {
		var substring = /*stripped*/.substring(0, /*separator*/).strip();
		var substring1 = /*stripped*/.substring(/*separator*/ + 1).strip();
		var s = _this.compileDefinition(/*substring*/).map(generate_CDefinition).orElseGet(_lambda65_);
		return /*s*/ + " = " + _this.compileExpression(/*substring1*/);
	}
	if (/*stripped*/.endsWith("++")) {
		return _this.compileExpression(/*stripped*/.substring(0, /*stripped*/.length() - 2)) + "++";
	}
	if (/*stripped*/.equals("break")) {
		return "break";
	}
	if (/*stripped*/.equals("continue")) {
		return "continue";
	}
	return _this.compileInvocation(/*stripped*/).or(_lambda70_).orElseGet(_lambda67_);
}
auto _lambda77_(auto _ref, auto definition) {
	auto _this = _ref;
	return /*definition*/.name.endsWith(/*stripped*/);
};
auto _lambda79_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(/*stripped*/, "<");
};
auto _lambda82_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(/*stripped*/, ">=");
};
auto _lambda85_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(/*stripped*/, "==");
};
auto _lambda88_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(/*stripped*/, "&&");
};
auto _lambda91_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(/*stripped*/, "-");
};
char* compileExpression_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	var stripped = input.strip();
	if (/*stripped*/.startsWith("'") && /*stripped*/.endsWith("'")) {
		return /*stripped*/;
	}
	if (/*stripped*/.startsWith("\"") && /*stripped*/.endsWith("\"")) {
		return /*stripped*/;
	}
	var maybeLambda = _this.compileLambda(/*stripped*/);
	if (/*maybeLambda*/.isPresent()) {
		return /*maybeLambda*/.get();
	}
	var maybeInvocation = _this.compileInvocation(/*stripped*/);
	if (/*maybeInvocation*/.isPresent()) {
		return /*maybeInvocation*/.get();
	}
	var i = /*stripped*/.lastIndexOf(".");
	if (/*i*/ >= 0) {
		var child = /*stripped*/.substring(0, /*i*/).strip();
		var name = /*stripped*/.substring(/*i*/ + 1).strip();
		if (_this.isIdentifier(/*name*/)) {
			return _this.compileExpression(/*child*/) + "." + /*name*/;
		}
	}
	if (_this.isIdentifier(/*stripped*/)) {
		if (/*stripped*/.equals("this")) {
			return "_this";
		}
		if (_this.definitions.stream().flatMap(stream_ArrayList).filter(_lambda77_).head.next().isPresent()) {
			return /*stripped*/;
		}
	}
	if (/*stripped*/.startsWith("switch")) {
		return _this.createName("switch");
	}
	var maybeOperator = _this.compileOperator(/*stripped*/, "+").or(_lambda91_).or(_lambda88_).or(_lambda85_).or(_lambda82_).or(_lambda79_);
	if (/*maybeOperator*/.isPresent()) {
		return /*maybeOperator*/.get();
	}
	var i2 = /*stripped*/.lastIndexOf("::");
	if (/*i2*/ >= 0) {
		var substring = /*stripped*/.substring(0, /*i2*/);
		var substring1 = /*stripped*/.substring(/*i2*/ + 2);
		return /*substring1*/ + "_" + _this.compileType(/*substring*/).map(generate_CType).orElse("?");
	}
	if (_this.isNumber(/*stripped*/)) {
		return /*stripped*/;
	}
	return /*Placeholder*/.wrap(/*stripped*/);
}
auto _lambda96_(auto _ref, auto segment) {
	auto _this = _ref;
	return "auto " + /*segment*/;
};
auto _lambda99_(auto _ref, auto segment) {
	auto _this = _ref;
	return /*!segment*/.isEmpty();
};
auto _lambda102_(auto _ref) {
			var expression = _this.compileExpression(/*content*/);
			return "{" + _this.generateStatement("auto _this = _ref", 1) + /*this.generateStatement("return "*/ + /*expression, 1)*/ + /*System*/.lineSeparator() + "};" + /*System*/.lineSeparator();
		}Option<char*> compileLambda_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	var arrowIndex = stripped.indexOf("->");
	if (/*arrowIndex*/ >= 0) {
		var names = stripped.substring(0, /*arrowIndex*/).strip();
		var content = stripped.substring(/*arrowIndex*/ + 2);
		var functionName = _this.createName("lambda");
		ArrayList<char*> parameters;
		if (_this.isIdentifier(/*names*/)) {
			/*parameters*/ = /*ArrayList*/.of("auto " + /*names*/);
		}
		else 
		if (/*names*/.startsWith("(") && names.endsWith(")")) {
			var slice = /*names*/.substring(1, /*names*/.length() - 1);
			/*parameters*/ = _this.divide(/*slice*/, foldValue_this).map(strip_char*).filter(_lambda99_).map(_lambda96_).toList();
		}
		else {
			return /*Option*/.empty();
		}
		var copy = /*parameters*/.copy().addFirst("auto _ref");
		_this.functions = _this.functions.addLast("auto " + /*functionName*/ + "(" + /*String*/.join(", ", /*copy*/.inner) + ") " + _this.compileMethodSegment(/*content*/).orElseGet(_lambda102_));
		return /*Option*/.of(/*functionName*/);
	}
	return /*Option*/.empty();
}
char* createName_App(void* _ref, char* type) {
	App _this = *((App*) _ref);
	var s = "_" + type + this.counter + "_";
	_this.counter++;
	return /*s*/;
}
Option<char*> compileInvocation_App(void* _ref, char* stripped) {
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
				final var caller = slice.substring(0, argStart).strip();
				final var arguments = this
						.divide(slice.substring(argStart + 1), this::foldValue)
						.map(String::strip)
						.filter(segment -> !segment.isEmpty())
						.map(this::compileExpression)
						.toList();

				final var maybeCaller = this.compileCaller(caller);
				if (maybeCaller.isPresent()) {
					return Option.of(maybeCaller.get() + "(" + String.join(", ", arguments.inner) + ")");
				}
			}
		}*/
	return /*Option*/.empty();
}
Option<char*> compileCaller_App(void* _ref, char* caller) {
	App _this = *((App*) _ref);
	if (caller.startsWith("new ")) {
		var substring = caller.substring("new ".length());
		var maybeType = _this.compileType(/*substring*/);
		if (/*maybeType*/.isPresent()) {
			return /*Option*/.of("new_" + /*maybeType*/.get().generate());
		}
	}
	return /*Option*/.of(_this.compileExpression(caller));
}
Option<char*> compileOperator_App(void* _ref, char* stripped, char* separator) {
	App _this = *((App*) _ref);
	var i1 = stripped.indexOf(separator);
	if (/*i1*/ >= 0) {
		var substring = stripped.substring(0, /*i1*/);
		var substring1 = stripped.substring(/*i1*/ + separator.length());
		return /*Option*/.of(_this.compileExpression(/*substring*/) + " " + separator + " " + _this.compileExpression(/*substring1*/));
	}
	return /*Option*/.empty();
}
int isNumber_App(void* _ref, char* input) {
	App _this = *((App*) _ref);/*
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (!Character.isDigit(c)) {
				return false;
			}
		}*/
	return /*true*/;
}
auto _lambda110_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
ArrayList<CDefinition> compileParametersToList_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.divide(input, foldValue_this).map(strip_char*).filter(_lambda110_).map(compileDefinition_this).flatMap(stream_Option).toList();
}
auto _lambda114_(auto _ref, auto segment) {
	auto _this = _ref;
	return /*!segment*/.isEmpty();
};
auto _lambda116_(auto _ref, auto cType) {
	auto _this = _ref;
	return new_CDefinition(/*finalTypeParameters*/, /*cType*/, /*name*/);
};
auto _lambda118_(auto _ref, auto cType) {
	auto _this = _ref;
	return new_CDefinition(/*ArrayList*/.empty(), /*cType*/, /*name*/);
};
Option<CDefinition> compileDefinition_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	var nameSeparator = input.lastIndexOf(" ");
	if (/*nameSeparator*/ < 0) {
		return /*Option*/.empty();
	}
	var beforeName = input.substring(0, /*nameSeparator*/);
	var name = input.substring(/*nameSeparator*/ + 1).strip();
	if (/*!this*/.isIdentifier(/*name*/)) {
		return /*Option*/.empty();
	}
	var typeSeparator =  - 1;
	var depth = 0;/*
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
	if (/*typeSeparator*/ >= 0) {
		var beforeType = /*beforeName*/.substring(0, /*typeSeparator*/).strip();
		var typeParameters = /*ArrayList*/. < /*String>empty*/();
		if (/*beforeType*/.endsWith(">")) {
			var slice = /*beforeType*/.substring(0, /*beforeType*/.length() - 1);
			var i = /*slice*/.indexOf("<");
			if (/*i*/ >= 0) {
				var typeParametersString = /*slice*/.substring(/*i*/ + 1);
				/*typeParameters*/ = _this.divide(/*typeParametersString*/, foldValue_this).map(strip_char*).filter(_lambda114_).collect(new_ListCollector<char*>());
			}
		}
		var type = /*beforeName*/.substring(/*typeSeparator*/ + 1).strip();
		var finalTypeParameters = /*typeParameters*/;
		return _this.compileType(/*type*/).map(_lambda116_);
	}
	return _this.compileType(/*beforeName*/).map(_lambda118_);
}
auto _lambda126_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
Option<CType> compileType_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	var stripped = input.strip();/*

		switch (stripped) {
			case "Character" -> {
				return Option.of(CPrimitiveType.Char);
			}
			case "boolean" -> {
				return Option.of(CPrimitiveType.Int);
			}
			case "void" -> {
				return Option.of(CPrimitiveType.Void);
			}
		}*/
	if (/*stripped*/.endsWith("[]")) {
		var slice = /*stripped*/.substring(0, /*stripped*/.length() - 2);
		return _this.compileType(/*slice*/).map(new_CPointerType);
	}
	if (/*stripped*/.equals("String")) {
		return /*Option*/.of(new_CPointerType(/*CPrimitiveType*/.Char));
	}
	if (/*stripped*/.endsWith(">")) {
		var withoutEnd = /*stripped*/.substring(0, /*stripped*/.length() - 1);
		var i = /*withoutEnd*/.indexOf("<");
		if (/*i*/ >= 0) {
			var base = /*withoutEnd*/.substring(0, /*i*/);
			var typeArguments = /*withoutEnd*/.substring(/*i*/ + 1);
			var list = _this.divide(/*typeArguments*/, foldValue_this).map(strip_char*).filter(_lambda126_).map(compileType_this).flatMap(stream_Option).toList();
			return /*Option*/.of(new_CTemplateType(/*base*/, /*list*/));
		}
	}
	if (_this.isIdentifier(/*stripped*/)) {
		if (/*stripped.equals("public") || stripped*/.equals("private")) {
			return /*Option*/.empty();
		}
		return /*Option*/.of(new_CIdentifier(/*stripped*/));
	}
	return /*Option*/.empty();
}
State foldValue_App(void* _ref, State state, char next) {
	App _this = *((App*) _ref);
	if (next == ',' && state.isLevel()) {
		return state.advance();
	}
	var appended = state.append(next);
	if (next == ' - ') {
		if (/*appended*/.peek() == '>') {
			return /*appended*/.popAndAppendToOption().orElse(/*appended*/);
		}
	}/*

		if (next == '<' || next == '(') {
			return appended.enter();
		}*/
	if (next == '>' || next == ') /*') {
			return appended.exit();
		}*/
	return /*appended*/;
}
int main(){
	return 0;
}