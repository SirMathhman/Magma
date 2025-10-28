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
struct CStructureSegment;
struct CNode;
struct CCaller;
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
struct EmptyCStructureSegment;
struct CContent;
struct CStatement;
struct CFieldAccess;
struct Frames;
struct Frame;
struct CStructureType;
struct CConstruction;
struct CInvocation;
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
	ArrayList<CType> list;
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
	CNode content1;
	int depth;
};
struct CFieldAccess {
	CExpression child;
	char* name;
};
struct Frame {
	ArrayList<CDefinition> definitions;
	Option<CStructureHeader> maybeHeader;
};
struct Frames {
};
struct CStructureType {
	char* name;
	ArrayList<CDefinition> fields;
};
struct CConstruction {
	CType type;
};
struct CInvocation {
	CCaller caller;
	ArrayList<CExpression> arguments;
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
	PlaceholderTag
};
union CTypeData {
	CIdentifier cidentifier;
	CPointerType cpointertype;
	CPrimitiveType cprimitivetype;
	CStructureType cstructuretype;
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
enum CStructureSegmentTag {
	CStatementTag,
	EmptyCStructureSegmentTag,
	PlaceholderTag
};
union CStructureSegmentData {
	CStatement cstatement;
	EmptyCStructureSegment emptycstructuresegment;
	Placeholder placeholder;
};
struct CStructureSegment {
	CStructureSegmentTag tag;
	CStructureSegmentData data;
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
App VoidValue = App { "void" };
App CharValue = App { "char" };
App IntValue = App { "int" };
CType toCType_CPrimitiveType(void* _ref){
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	CTypeData data;
	data.cprimitivetype = _this;
	return CType { CPrimitiveTypeTag, data };
}
App new_App(void* _ref, char* content) {
	App _this = *((App*) _ref);
	_this.content = content;
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.content;
}
char* getSimpleName_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.content;
}
template <typename T, typename R>
R apply_App(void* _ref, T arg);
template <typename A, typename B, typename R>
R apply_App(void* _ref, A left, B right);
template <typename T>
void accept_App(void* _ref, T value);
template <typename T>
Option<T> next_App(void* _ref);
template <typename T, typename C>
C createInitial_App(void* _ref);
template <typename T, typename C>
C fold_App(void* _ref, C current, T element);
template <typename T>
T get_App(void* _ref);
char* generate_App(void* _ref);
char* getSimpleName_App(void* _ref);
char* generate_App(void* _ref);
template <typename T, typename T>
Option<T> of_App(void* _ref, T element) {
	App _this = *((App*) _ref);
	return new_Some<T>(element);
}
template <typename T, typename T>
Option<T> empty_App(void* _ref) {
	App _this = *((App*) _ref);
	return new_None<T>();
}
template <typename T, typename R>
Option<R> map_App(void* _ref, Function<T, R> mapper);
template <typename T>
void ifPresent_App(void* _ref, Consumer<T> consumer);
template <typename T>
Option<T> or_App(void* _ref, Supplier<Option<T>> other);
template <typename T>
int isEmpty_App(void* _ref);
template <typename T>
T get_App(void* _ref);
template <typename T, typename R>
Option<R> flatMap_App(void* _ref, Function<T, Option<R>> mapper);
template <typename T>
T orElse_App(void* _ref, T other);
template <typename T>
T orElseGet_App(void* _ref, Supplier<T> other);
template <typename T>
int isPresent_App(void* _ref);
template <typename T>
Stream<T> stream_App(void* _ref);
template <typename T>
int test_App(void* _ref, T element);
char* generate_App(void* _ref);
char* generate_App(void* _ref);
App new_App(void* _ref);
char* generate_App(void* _ref);
template <typename T>
Head<T> toHead_SingleHead(void* _ref){
	SingleHead<T> _this = *((SingleHead<T>*) _ref);
	HeadData<T> data;
	data.singlehead = _this;
	return Head<T> { SingleHeadTag, data };
}
template <typename T>
App new_App(void* _ref, T element) {
	App _this = *((App*) _ref);
	_this.element = element;
	_this.retrieved = 0;
}
template <typename T>
Option<T> next_App(void* _ref) {
	App _this = *((App*) _ref);
	if (_this.retrieved) {
		return new_None<T>();
	}
	_this.retrieved = 1;
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
Option<T> next_App(void* _ref) {
	App _this = *((App*) _ref);
	return new_None<T>();
}
template <typename T, typename T>
Stream<T> of_App(void* _ref, T element) {
	App _this = *((App*) _ref);
	return new_Stream<T>(new_SingleHead<T>(element));
}
template <typename T, typename T>
Stream<T> empty_App(void* _ref) {
	App _this = *((App*) _ref);
	return new_Stream<T>(new_EmptyHead<T>());
}
template <typename T, typename R>
Stream<R> map_App(void* _ref, Function<T, R> mapper) {
	App _this = *((App*) _ref);
	return new_Stream<R>(new_MapHead<T, R>(_this.head, mapper));
}
template <typename T, typename R>
R fold_App(void* _ref, R initial, BiFunction<R, T, R> folder) {
	App _this = *((App*) _ref);
	R current = initial;
	while (1) {
		Head<T> head = _this.head;
		/*Placeholder[input=Does not have a type of structure: Head<T>]*/ maybeNext = head.next();
		if (/*maybeNext instanceof Some*/ < /*T>*/(/*var next*/)) {
			current = folder.apply(current, /*next*/);
		}
		else {
			return current;
		}
	}
}
template <typename T, typename C>
C collect_App(void* _ref, Collector<T, C> collector) {
	App _this = *((App*) _ref);
	return _this.fold(collector.createInitial(), fold_collector);
}
template <typename T>
ArrayList<T> toList_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.collect(new_ListCollector<T>());
}
auto _lambda1_(auto _ref, auto element) {
	auto _this = _ref;
	return _this.applyFilter(predicate, /*element*/);
};
template <typename T>
Stream<T> filter_App(void* _ref, Predicate<T> predicate) {
	App _this = *((App*) _ref);
	return _this.flatMap(_lambda1_);
}
template <typename T>
Stream<T> applyFilter_App(void* _ref, Predicate<T> predicate, T element) {
	App _this = *((App*) _ref);
	if (predicate.test(element)) {
		return /*Stream*/.of(element);
	}
	return /*Stream*/.empty();
}
template <typename T, typename R>
Stream<R> flatMap_App(void* _ref, Function<T, Stream<R>> mapper) {
	App _this = *((App*) _ref);
	return new_Stream<R>(new_FlatMapHead<T, R>(_this.head, mapper));
}
template <typename T>
App new_App(void* _ref, List<T> inner) {
	App _this = *((App*) _ref);
	_this.inner = inner;
}
template <typename T>
App new_App(void* _ref) {
	App _this = *((App*) _ref);
	_this(new_java.util.ArrayList<T>());
}
template <typename T, typename T>
ArrayList<T> of_App(void* _ref) {
	App _this = *((App*) _ref);
	return new_ArrayList<T>(new_java.util.ArrayList<T>(/*Arrays*/.asList(/*elements*/)));
}
template <typename T, typename T>
ArrayList<T> empty_App(void* _ref) {
	App _this = *((App*) _ref);
	return new_ArrayList<T>();
}
template <typename T>
char* toString_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.inner.stream().map(toString_Objects).collect(/*Collectors*/.joining(", ", "[", "]"));
}
template <typename T>
int size_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.inner.size();
}
template <typename T>
Stream<T> stream_App(void* _ref) {
	App _this = *((App*) _ref);
	return new_Stream<T>(new_ListHead<T>(_this));
}
template <typename T>
ArrayList<T> addLast_App(void* _ref, T element) {
	App _this = *((App*) _ref);
	_this.inner.add(element);
	return _this;
}
template <typename T>
int isEmpty_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.inner.isEmpty();
}
template <typename T>
ArrayList<T> copy_App(void* _ref) {
	App _this = *((App*) _ref);
	return new_ArrayList<T>(new_java.util.ArrayList<T>(_this.inner));
}
template <typename T>
ArrayList<T> addFirst_App(void* _ref, T element) {
	App _this = *((App*) _ref);
	_this.inner.addFirst(element);
	return _this;
}
template <typename T>
ArrayList<T> removeLast_App(void* _ref) {
	App _this = *((App*) _ref);
	_this.inner.removeLast();
	return _this;
}
template <typename T>
T getLast_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.inner.getLast();
}
template <typename T>
ArrayList<T> setLast_App(void* _ref, T element) {
	App _this = *((App*) _ref);
	_this.inner.set(_this.inner.size() - 1, element);
	return _this;
}
template <typename T>
ArrayList<T> addAllLast_App(void* _ref, ArrayList<T> others) {
	App _this = *((App*) _ref);
	return others.stream().fold(_this, addLast_ArrayList);
}
template <typename T>
ArrayList<T> mapLast_App(void* _ref, Function<T, T> mapper) {
	App _this = *((App*) _ref);
	if (_this.isEmpty()) {
		return _this;
	}
	return _this.setLast(mapper.apply(_this.getLast()));
}
template <typename T>
ArrayList<T> reverse_App(void* _ref) {
	App _this = *((App*) _ref);
	/*Placeholder[input=new_java.util.ArrayList<T>]*/ copy = new_java.util.ArrayList<T>(_this.inner);
	/*Collections*/.reverse(copy);
	return new_ArrayList<T>(copy);
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
Option<R> map_App(void* _ref, Function<T, R> mapper) {
	App _this = *((App*) _ref);
	return new_Some<R>(mapper.apply(_this.value));
}
template <typename T>
char* toString_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.value.toString();
}
template <typename T>
void ifPresent_App(void* _ref, Consumer<T> consumer) {
	App _this = *((App*) _ref);
	consumer.accept(_this.value);
}
template <typename T>
Option<T> or_App(void* _ref, Supplier<Option<T>> other) {
	App _this = *((App*) _ref);
	return _this;
}
template <typename T>
int isEmpty_App(void* _ref) {
	App _this = *((App*) _ref);
	return 0;
}
template <typename T>
T get_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.value;
}
template <typename T, typename R>
Option<R> flatMap_App(void* _ref, Function<T, Option<R>> mapper) {
	App _this = *((App*) _ref);
	return mapper.apply(_this.value);
}
template <typename T>
T orElse_App(void* _ref, T other) {
	App _this = *((App*) _ref);
	return _this.value;
}
template <typename T>
T orElseGet_App(void* _ref, Supplier<T> other) {
	App _this = *((App*) _ref);
	return _this.value;
}
template <typename T>
int isPresent_App(void* _ref) {
	App _this = *((App*) _ref);
	return 1;
}
template <typename T>
Stream<T> stream_App(void* _ref) {
	App _this = *((App*) _ref);
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
Option<R> map_App(void* _ref, Function<T, R> mapper) {
	App _this = *((App*) _ref);
	return new_None<R>();
}
template <typename T>
void ifPresent_App(void* _ref, Consumer<T> consumer) {
	App _this = *((App*) _ref);
}
template <typename T>
Option<T> or_App(void* _ref, Supplier<Option<T>> other) {
	App _this = *((App*) _ref);
	return other.get();
}
template <typename T>
int isEmpty_App(void* _ref) {
	App _this = *((App*) _ref);
	return 1;
}
template <typename T>
T get_App(void* _ref) {
	App _this = *((App*) _ref);
	return /*null*/;
}
template <typename T, typename R>
Option<R> flatMap_App(void* _ref, Function<T, Option<R>> mapper) {
	App _this = *((App*) _ref);
	return new_None<R>();
}
template <typename T>
T orElse_App(void* _ref, T other) {
	App _this = *((App*) _ref);
	return other;
}
template <typename T>
T orElseGet_App(void* _ref, Supplier<T> other) {
	App _this = *((App*) _ref);
	return other.get();
}
template <typename T>
int isPresent_App(void* _ref) {
	App _this = *((App*) _ref);
	return 0;
}
template <typename T>
Stream<T> stream_App(void* _ref) {
	App _this = *((App*) _ref);
	return /*Stream*/.empty();
}
CType toCType_CPointerType(void* _ref){
	CPointerType _this = *((CPointerType*) _ref);
	CTypeData data;
	data.cpointertype = _this;
	return CType { CPointerTypeTag, data };
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.type.generate() + "*";
}
char* getSimpleName_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.type.getSimpleName() + "_ref";
}
CType toCType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CTypeData data;
	data.ctemplatetype = _this;
	return CType { CTemplateTypeTag, data };
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	ArrayList<CType> list = _this.list;
	/*Placeholder[input=Does not have a type of structure: ArrayList<CType>]*/ stream = list.stream();
	/*Placeholder[input=Does not have a type of structure: var]*/ map = stream.map(generate_CType);
	/*Placeholder[input=new_Joiner]*/ collector = new_Joiner(", ");
	/*Placeholder[input=Does not have a type of structure: var]*/ joined = map.collect(collector);
	return _this.base + "<" + joined + ">";
}
char* getSimpleName_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.base;
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.value;
}
char* getSimpleName_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.value;
}
char* wrap_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ withoutStart = input.replace("/*", "start");
	/*Placeholder[input=Does not have a type of structure: var]*/ withoutEnd = withoutStart.replace("*/", "end");
	return "/*" + withoutEnd + "*/";
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return /*wrap*/(_this.input);
}
char* getSimpleName_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.generate();
}
App new_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	_this.input = input;
	_this.buffer = "";
	_this.depth = 0;
	_this.segments = new_ArrayList<char*>();
	_this.index = 0;
}
State enter_App(void* _ref) {
	App _this = *((App*) _ref);
	_this.depth = _this.depth + 1;
	return _this;
}
State exit_App(void* _ref) {
	App _this = *((App*) _ref);
	_this.depth = _this.depth - 1;
	return _this;
}
State advance_App(void* _ref) {
	App _this = *((App*) _ref);
	_this.segments = _this.segments.addLast(_this.buffer);
	_this.buffer = "";
	return _this;
}
int isShallow_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.depth == 1;
}
State append_App(void* _ref, char c) {
	App _this = *((App*) _ref);
	_this.buffer +  = c;
	return _this;
}
int isLevel_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.depth == 0;
}
Option<char> pop_App(void* _ref) {
	App _this = *((App*) _ref);
	if (_this.index < _this.input.length()) {
		int counter = _this.index;
		_this.index++;
		/*Placeholder[input=Does not have a type of structure: char*]*/ element = _this.input.charAt(counter);
		return /*Option*/.of(element);
	}
	else {
		return /*Option*/.empty();
	}
}
Stream<char*> stream_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.segments.stream();
}
auto _lambda3_(auto _ref, auto next) {
		/*Placeholder[input=Undefined field 'append' in 'State']*/ appended = _this.append(/*next*/);
		return new_Tuple<char, State>(/*next*/, appended);
	}Option<Tuple<char, State>> popAndAppendToTuple_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.pop().map(_lambda3_);
}
Option<State> popAndAppendToOption_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.popAndAppendToTuple().map(right_Tuple);
}
char peek_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.input.charAt(_this.index);
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.type().generate() + " " + _this.name();
}
char* toString_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.generate();
}
CDefinition withType_App(void* _ref, CType type) {
	App _this = *((App*) _ref);
	return new_CDefinition(_this.typeParameters, type, _this.name);
}
CType toType_App(void* _ref) {
	App _this = *((App*) _ref);
	if (_this.typeParameters.isEmpty()) {
		return new_CIdentifier(_this.name);
	}
	/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=typeof(_this.typeParameters.stream(). < startCType>mapend)]end]*/ list = _this.typeParameters.stream(). < /*CType>map*/(new_CIdentifier).toList();
	return new_CTemplateType(_this.name, list);
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return /*App*/.createTemplateString(_this.typeParameters()) + "struct " + _this.name();
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.CStructureHeader().generate() + " {" + this.fields() + System.lineSeparator() + "};";
}
template <typename T, typename R>
Head<R> toHead_MapHead(void* _ref){
	MapHead<T, R> _this = *((MapHead<T, R>*) _ref);
	HeadData<T, R> data;
	data.maphead = _this;
	return Head<R> { MapHeadTag, data };
}
template <typename T, typename R>
App new_App(void* _ref, Head<T> head, Function<T, R> mapper) {
	App _this = *((App*) _ref);
	_this.mapper = mapper;
	_this.head = head;
}
template <typename T, typename R>
Option<R> next_App(void* _ref) {
	App _this = *((App*) _ref);
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
App new_App(void* _ref, ArrayList<T> list) {
	App _this = *((App*) _ref);
	_this.list = list;
	_this.counter = 0;
}
template <typename T>
Option<T> next_App(void* _ref) {
	App _this = *((App*) _ref);
	if (_this.counter < _this.list.size()) {
		/*Placeholder[input=Does not have a type of structure: startDoes not have a type of structure: ArrayList<T>end]*/ element = _this.list.inner.get(_this.counter);
		_this.counter++;
		return /*Option*/.of(element);
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
ArrayList<T> createInitial_App(void* _ref) {
	App _this = *((App*) _ref);
	return new_ArrayList<T>();
}
template <typename T>
ArrayList<T> fold_App(void* _ref, ArrayList<T> current, T element) {
	App _this = *((App*) _ref);
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
App new_App(void* _ref, Head<T> head, Function<T, Stream<R>> mapper) {
	App _this = *((App*) _ref);
	_this.head = head;
	_this.mapper = mapper;
	_this.current = new_EmptyHead<R>();
}
template <typename T, typename R>
Option<R> next_App(void* _ref) {
	App _this = *((App*) _ref);
	while (1) {
		/*Placeholder[input=Does not have a type of structure: Head<R>]*/ maybeNext = _this.current.next();
		if (maybeNext.isPresent()) {
			return maybeNext;
		}
		/*Placeholder[input=Does not have a type of structure: Head<T>]*/ maybeOuter = _this.head.next();
		if (maybeOuter.isEmpty()) {
			return /*Option*/.empty();
		}
		_this.current = _this.mapper.apply(maybeOuter.get()).head;
	}
}
Collector<char*, char*> toCollector_Joiner(void* _ref){
	Joiner _this = *((Joiner*) _ref);
	CollectorData data;
	data.joiner = _this;
	return Collector<char*, char*> { JoinerTag, data };
}
App new_App(void* _ref) {
	App _this = *((App*) _ref);
	_this("");
}
char* createInitial_App(void* _ref) {
	App _this = *((App*) _ref);
	return "";
}
char* fold_App(void* _ref, char* current, char* element) {
	App _this = *((App*) _ref);
	if (current.isEmpty()) {
		return element;
	}
	return current + _this.delimiter + element;
}
CStructureSegment toCStructureSegment_EmptyCStructureSegment(void* _ref){
	EmptyCStructureSegment _this = *((EmptyCStructureSegment*) _ref);
	CStructureSegmentData data;
	data.emptycstructuresegment = _this;
	return CStructureSegment { EmptyCStructureSegmentTag, data };
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return "";
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.content;
}
CStructureSegment toCStructureSegment_CStatement(void* _ref){
	CStatement _this = *((CStatement*) _ref);
	CStructureSegmentData data;
	data.cstatement = _this;
	return CStructureSegment { CStatementTag, data };
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return /*App*/.generateWithIndent(_this.content1().generate(), _this.depth()) + ";";
}
CExpression toCExpression_CFieldAccess(void* _ref){
	CFieldAccess _this = *((CFieldAccess*) _ref);
	CExpressionData data;
	data.cfieldaccess = _this;
	return CExpression { CFieldAccessTag, data };
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.child.generate() + "." + _this.name;
}
App new_App(void* _ref) {
	App _this = *((App*) _ref);
	_this(/*ArrayList*/.empty(), new_None<CStructureHeader>());
}
Frame defineAll_App(void* _ref, ArrayList<CDefinition> params) {
	App _this = *((App*) _ref);
	return new_Frame(_this.definitions.addAllLast(params), _this.maybeHeader);
}
Frame define_App(void* _ref, CDefinition definition) {
	App _this = *((App*) _ref);
	return new_Frame(_this.definitions.addLast(definition), _this.maybeHeader);
}
Frame withHeader_App(void* _ref, CStructureHeader header) {
	App _this = *((App*) _ref);
	return new_Frame(_this.definitions, new_Some<CStructureHeader>(header));
}
auto _lambda8_(auto _ref, auto definition) {
	auto _this = _ref;
	return /*definition*/.name.equals(name);
};
Option<CDefinition> resolve_App(void* _ref, char* name) {
	App _this = *((App*) _ref);
	return _this.definitions.stream().filter(_lambda8_).head.next();
}
/*private ArrayList<Frame> frames = ArrayList.empty*/(void* _ref);
App new_App(void* _ref) {
	App _this = *((App*) _ref);
}
auto _lambda10_(auto _ref, auto last) {
	auto _this = _ref;
	return /*last*/.defineAll(params);
};
Frames defineAll_App(void* _ref, ArrayList<CDefinition> params) {
	App _this = *((App*) _ref);
	_this.frames = _this.frames.mapLast(_lambda10_);
	return _this;
}
template <typename T>
Tuple<T, Frames> within_App(void* _ref, Supplier<T> mapper) {
	App _this = *((App*) _ref);
	_this.frames = _this.frames.addLast(new_Frame());
	/*Placeholder[input=Does not have a type of structure: Supplier<T>]*/ result = mapper.get();
	_this.frames = _this.frames.removeLast();
	return new_Tuple<T, Frames>(result, _this);
}
auto _lambda12_(auto _ref, auto last) {
	auto _this = _ref;
	return /*last*/.define(definition);
};
Frames define_App(void* _ref, CDefinition definition) {
	App _this = *((App*) _ref);
	_this.frames = _this.frames.mapLast(_lambda12_);
	return _this;
}
auto _lambda19_(auto _ref, auto frame) {
	auto _this = _ref;
	return /*frame*/.resolve(name);
};
Option<CDefinition> resolve_App(void* _ref, char* name) {
	App _this = *((App*) _ref);
	return _this.frames.stream().map(_lambda19_).flatMap(stream_Option).head.next();
}
auto _lambda21_(auto _ref, auto last) {
	auto _this = _ref;
	return /*last*/.withHeader(header);
};
Frames defineStructure_App(void* _ref, CStructureHeader header) {
	App _this = *((App*) _ref);
	_this.frames = _this.frames.mapLast(_lambda21_);
	return _this;
}
auto _lambda27_(auto _ref, auto header) {
	auto _this = _ref;
	return /*header*/.typeParameters;
};
ArrayList<char*> collectTypeParameters_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.streamHeaders().map(_lambda27_).flatMap(stream_ArrayList).collect(new_ListCollector<char*>());
}
Option<CStructureHeader> findCurrentStructure_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.streamHeaders().head.next();
}
auto _lambda31_(auto _ref, auto frame) {
	auto _this = _ref;
	return /*frame*/.maybeHeader;
};
Stream<CStructureHeader> streamHeaders_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.frames.stream().map(_lambda31_).flatMap(stream_Option);
}
auto _lambda40_(auto _ref, auto header) {
	auto _this = _ref;
	return new_Tuple<CStructureHeader, ArrayList<CDefinition>>(/*header*/, /*frame*/.definitions);
};
auto _lambda38_(auto _ref, auto frame) {
	auto _this = _ref;
	return /*frame*/.maybeHeader.map(_lambda40_);
};
Option<Tuple<CStructureHeader, ArrayList<CDefinition>>> findCurrentScope_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.frames.copy().reverse().stream().map(_lambda38_).flatMap(stream_Option).head.next();
}
CType toCType_CStructureType(void* _ref){
	CStructureType _this = *((CStructureType*) _ref);
	CTypeData data;
	data.cstructuretype = _this;
	return CType { CStructureTypeTag, data };
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.name;
}
char* getSimpleName_App(void* _ref) {
	App _this = *((App*) _ref);
	return _this.name;
}
auto _lambda45_(auto _ref, auto field) {
	auto _this = _ref;
	return /*field*/.type;
};
auto _lambda48_(auto _ref, auto field) {
	auto _this = _ref;
	return /*field*/.name.equals(name);
};
Option<CType> resolve_App(void* _ref, char* name) {
	App _this = *((App*) _ref);
	return _this.fields.stream().filter(_lambda48_).map(_lambda45_).head.next();
}
CCaller toCCaller_CConstruction(void* _ref){
	CConstruction _this = *((CConstruction*) _ref);
	CCallerData data;
	data.cconstruction = _this;
	return CCaller { CConstructionTag, data };
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	return "new_" + _this.type().generate();
}
CExpression toCExpression_CInvocation(void* _ref){
	CInvocation _this = *((CInvocation*) _ref);
	CExpressionData data;
	data.cinvocation = _this;
	return CExpression { CInvocationTag, data };
}
char* generate_App(void* _ref) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startCTemplateType[base=ArrayList, list=[CIdentifier[value=CExpression]]]end]end]end]*/ joinedArguments = _this.arguments().stream().map(generate_CExpression).collect(new_Joiner(", "));
	return _this.caller().generate() + "(" + joinedArguments + ")";
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
auto _lambda52_(auto _ref, auto slice) {
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
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: ArrayList<char*>]end]end]*/ collect = typeParameters.stream().map(_lambda52_).collect(new_Joiner(", "));
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
	/*Placeholder[input=Does not have a type of structure: startPathsend]*/ source = /*Paths*/.get(".", "src", "main", "java", "magma", "App.java");
	/*Placeholder[input=Undefined field 'readString' in 'App']*/ input = _this.readString(source);
	return _switch54_;
}
auto _lambda56_(auto _ref) {
	auto _this = _ref;
	return _this.compileNative(target);
};
Option<IOException> compilePath_App(void* _ref, Path source, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: Path]*/ target = source.resolveSibling("App.cpp");
	/*Placeholder[input=Undefined field 'compile' in 'App']*/ output = _this.compile(input);
	return _this.writeString(target, output).or(_lambda56_);
}
Option<IOException> compileNative_App(void* _ref, Path target) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Undefined field 'startCommand' in 'App']*/ clang = _this.startCommand(/*ArrayList*/.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));
	return _switch58_;
}
Option<IOException> waitForProcess_App(void* _ref, Process process) {
	App _this = *((App*) _ref);
	return _switch60_;
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
	/*Placeholder[input=Undefined field 'compileStatements' in 'App']*/ compiled = _this.compileStatements(input, compileRootSegment_this);
	/*Placeholder[input=Does not have a type of structure: startStringend]*/ joinedForwardDeclarations = /*String*/.join("", _this.forwardDeclarations.inner);
	/*Placeholder[input=Does not have a type of structure: startStringend]*/ joinedFunctions = /*String*/.join("", _this.functions.inner);
	/*Placeholder[input=Does not have a type of structure: startStringend]*/ joinedStructures = /*String*/.join("", _this.structures.inner);
	/*Placeholder[input=Does not have a type of structure: startStringend]*/ joinedSealedStructures = /*String*/.join("", _this.sealedStructures.inner);
	/*Placeholder[input=Does not have a type of structure: startStringend]*/ joinedGlobals = /*String*/.join("", _this.globals.inner);
	return joinedForwardDeclarations + compiled + joinedStructures + joinedSealedStructures + joinedGlobals + joinedFunctions + "int main(){" + System.lineSeparator() + "\treturn " + "0;" + System.lineSeparator() +
					 "}";
}
char* compileStatements_App(void* _ref, char* input, Function<char*, char*> mapper) {
	App _this = *((App*) _ref);
	return _this.divide(input, foldStatement_this).map(mapper).collect(new_Joiner(""));
}
Stream<char*> divide_App(void* _ref, char* input, BiFunction<State, char, State> folder) {
	App _this = *((App*) _ref);
	/*Placeholder[input=new_State]*/ current = new_State(input);
	while (1) {
		/*Placeholder[input=Does not have a type of structure: var]*/ maybeNext = current.pop();
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
		/*Placeholder[input=Does not have a type of structure: State]*/ current0 = current.append(next);
		while (1) {
			/*Placeholder[input=Does not have a type of structure: var]*/ maybeTuple = current0.popAndAppendToTuple();
			if (maybeTuple.isEmpty()) {
				break;
			}
			/*Placeholder[input=Does not have a type of structure: var]*/ tuple = maybeTuple.get();
			current0 = tuple.right;
			/*Does not have a type of structure: var*/ nextInQuotes = tuple.left;
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
	/*Placeholder[input=Does not have a type of structure: State]*/ appended = state.append(c);
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
auto _lambda62_(auto _ref) {
	auto _this = _ref;
	return /*Placeholder*/.wrap(input);
};
char* compileRootSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ stripped = input.strip();
	if (/*stripped.startsWith("package ") || stripped*/.startsWith("import ")) {
		return "";
	}
	return _this.compileStructure("class", stripped).map(generate_CStructureSegment).orElseGet(_lambda62_);
}
auto _lambda67_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
auto _lambda72_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
auto _lambda76_(auto _ref, auto content1) {
	auto _this = _ref;
	return /*App*/.generateWithIndent(/*content1*/, 1);
};
auto _lambda79_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*slice*/ + "Tag";
};
auto _lambda83_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*System*/.lineSeparator() + "\t" + slice + typeArguments + " " + slice.toLowerCase() + ";";
};
auto _lambda87_(auto _ref, auto slice) {
	auto _this = _ref;
	return new_CStatement(new_CContent(/*slice*/), 1).generate();
};
auto _lambda89_(auto _ref) {
					_this.frames = _this.frames.defineStructure(header).defineAll(finalRecordFields);
					/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Undefined field 'divide' in 'App']end]end]*/ members = _this.divide(content, foldStatement_this).map(compileClassSegment_this).collect(new_ListCollector<CStructureSegment>());
					/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]end]*/ joinedFields = members.stream().map(generate_CStructureSegment).collect(new_Joiner(""));
					/*typeof(generatedFields + joinedFields)*/ outputContent = generatedFields + joinedFields;
					return dependencies + new_CStructure(header, outputContent).generate() + /*System*/.lineSeparator();
				}Option<CStructureSegment> compileStructure_App(void* _ref, char* type, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ classIndex = input.indexOf(type);
	if (classIndex >= 0) {
		/*Placeholder[input=Does not have a type of structure: char*]*/ afterKeyword = input.substring(classIndex + type.length());
		/*Placeholder[input=Does not have a type of structure: var]*/ contentStart = afterKeyword.indexOf("{");
		if (contentStart >= 0) {
			/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ beforeContent = afterKeyword.substring(0, contentStart).strip();
			/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
			if (withEnd.endsWith("}")) {
				/*Placeholder[input=Does not have a type of structure: var]*/ content = withEnd.substring(0, withEnd.length() - 1);
				/*Placeholder[input=Does not have a type of structure: var]*/ permitsIndex = beforeContent.indexOf("permits");
				/*Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/ variants = /*ArrayList*/. < /*String>empty*/();
				if (permitsIndex >= 0) {
					/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ variantsArray = beforeContent.substring(permitsIndex + "permits".length()).split(/*Pattern*/.quote(","));
					beforeContent = beforeContent.substring(0, permitsIndex).strip();
					variants = new_ArrayList<char*>(/*Arrays*/.stream(variantsArray).map(strip_char*).filter(_lambda67_).toList());
				}
				/*Placeholder[input=Does not have a type of structure: var]*/ implementsIndex = beforeContent.indexOf("implements");
				Option<CType> maybeInterfaceType = /*Option*/.empty();
				if (implementsIndex >= 0) {
					/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ slice = beforeContent.substring(implementsIndex + "implements".length()).strip();
					maybeInterfaceType = _this.compileType(slice);
					beforeContent = beforeContent.substring(0, implementsIndex).strip();
				}
				/*Placeholder[input=typeof(startArrayListend. < startCDefinition>emptyend)]*/ recordFields = /*ArrayList*/. < /*CDefinition>empty*/();
				if (/*beforeContent.endsWith(")"*/) /*) {
						final var slice = beforeContent.substring(0, beforeContent.length() - 1);
						final var i = slice.indexOf("(");
						if (i >= 0) {
							final var params = slice.substring(i + 1);
							beforeContent = slice.substring(0, i).strip();

							recordFields = this.compileParametersToList(params);
						}
					}*/
				/*Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/ typeParameters = /*ArrayList*/. < /*String>empty*/();
				if (beforeContent.endsWith(">")) {
					/*Placeholder[input=Does not have a type of structure: var]*/ withoutEnd = beforeContent.substring(0, beforeContent.length() - 1);
					/*Placeholder[input=Does not have a type of structure: var]*/ typeParamStart = withoutEnd.indexOf("<");
					if (typeParamStart >= 0) {
						beforeContent = withoutEnd.substring(0, typeParamStart);
						/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ typeParamsArray = withoutEnd.substring(typeParamStart + 1).split(/*Pattern*/.quote(","));
						typeParameters = new_ArrayList<char*>(/*Arrays*/.stream(typeParamsArray).map(strip_char*).filter(_lambda72_).toList());
					}
				}
				if (/*!this*/.isIdentifier(beforeContent)) {
					return /*Option*/.empty();
				}
				/*Placeholder[input=Does not have a type of structure: startAppend]*/ templateString = /*App*/.createTemplateString(typeParameters);
				char* dependencies;
				if (variants.isEmpty()) {
					dependencies = "";
				}
				else {
					/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]end]end]*/ enumFields = variants.stream().map(_lambda79_).map(_lambda76_).collect(new_Joiner(","));
					/*Placeholder[input=Undefined field 'joinTypeArguments' in 'App']*/ typeArguments = _this.joinTypeArguments(typeParameters);
					/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]end]*/ unionFields = variants.stream().map(_lambda83_).collect(new_Joiner(""));
					dependencies = "enum " + beforeContent + "Tag {" + enumFields + /*System*/.lineSeparator() + "};" + /*System*/.lineSeparator() + templateString + "union " + beforeContent + "Data {" + unionFields + /*System*/.lineSeparator() + "};" + /*System*/.lineSeparator();
				}
				char* generatedFields;
				if (variants.isEmpty()) {
					generatedFields = recordFields.stream().map(generate_CDefinition).map(_lambda87_).collect(new_Joiner(""));
				}
				else {
					generatedFields = /*new CStatement(new CContent(beforeContent*/ + /*"Tag tag"), 1).generate()*/ + /*new CStatement*/(new_CContent(beforeContent + "Data" + this.joinTypeArguments(typeParameters) + " " + "data"), 1).generate();
				}
				if (maybeInterfaceType.isPresent()) {
					/*Placeholder[input=Does not have a type of structure: Option<CType>]*/ interfaceType = maybeInterfaceType.get();
					/*Placeholder[input=Undefined field 'joinTypeArguments' in 'App']*/ joinedTypeArguments = _this.joinTypeArguments(typeParameters);
					/*typeof(beforeContent + joinedTypeArguments)*/ thisType = beforeContent + joinedTypeArguments;
					_this.functions = _this.functions.addLast(templateString + interfaceType.generate() + " to" + interfaceType.getSimpleName() + "_" + beforeContent + "(void* _ref" + "){" + /*new CStatement(new CContent(thisType*/ + " _this = *((" + thisType + /*"*) _ref)"), 1).generate()*/ + new_CStatement(/*new CContent(interfaceType.getSimpleName(*/) + "Data" + joinedTypeArguments + /*" data"),
															 1).generate()*/ + /*new CStatement(new CContent("data."*/ + beforeContent.toLowerCase() + /*" = _this"), 1).generate()*/ + /*new CStatement(new CContent(
										"return "*/ + interfaceType.generate() + " { " + beforeContent + "Tag, " + /*"data }"), 1).generate()*/ + /*System*/.lineSeparator() + "}" + /*System*/.lineSeparator());
				}
				_this.forwardDeclarations = _this.forwardDeclarations.addLast(templateString + "struct " + beforeContent + ";" + /*System*/.lineSeparator());
				/*Placeholder[input=new_CStructureHeader]*/ header = new_CStructureHeader(typeParameters, beforeContent);
				var finalRecordFields = recordFields;
				/*Placeholder[input=Does not have a type of structure: Frames]*/ within1 = _this.frames.within(_lambda89_);
				/*Does not have a type of structure: var*/ generated = within1.left;
				_this.frames = within1.right;
				if (variants.isEmpty()) {
					_this.structures = _this.structures.addLast(generated);
				}
				else {
					_this.sealedStructures = _this.sealedStructures.addLast(generated);
				}
				return /*Option*/.of(new_EmptyCStructureSegment());
			}
		}
	}
	return /*Option*/.empty();
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
auto _lambda91_(auto _ref) {
	auto _this = _ref;
	return _this.compileDefinitionToField0(slice);
};
auto _lambda93_(auto _ref) {
	auto _this = _ref;
	return new_Placeholder(input);
};
CStructureSegment compileClassSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	if (input.isBlank()) {
		return new_EmptyCStructureSegment();
	}
	/*Placeholder[input=Undefined field 'compileStructure' in 'App']*/ maybeClass = _this.compileStructure("class", input);
	if (maybeClass.isPresent()) {
		return maybeClass.get();
	}
	/*Placeholder[input=Undefined field 'compileStructure' in 'App']*/ maybeInterface = _this.compileStructure("interface", input);
	if (maybeInterface.isPresent()) {
		return maybeInterface.get();
	}
	/*Placeholder[input=Undefined field 'compileStructure' in 'App']*/ maybeRecord = _this.compileStructure("record", input);
	if (maybeRecord.isPresent()) {
		return maybeRecord.get();
	}
	/*Placeholder[input=Undefined field 'compileStructure' in 'App']*/ maybeEnum = _this.compileStructure("enum", input);
	if (maybeEnum.isPresent()) {
		return maybeEnum.get();
	}
	if (input.endsWith(";")) {
		/*Placeholder[input=Does not have a type of structure: char*]*/ slice = input.substring(0, input.length() - 1);
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Undefined field 'compileEnumValues' in 'App']end]*/ maybeClassStatement = _this.compileEnumValues(slice).or(_lambda91_);
		if (maybeClassStatement.isPresent()) {
			return maybeClassStatement.get();
		}
	}
	return _this.compileMethod(input).orElseGet(_lambda93_);
}
auto _lambda97_(auto _ref) {
					/*Placeholder[input=Does not have a type of structure: starttypeof(thisDefinition + _this.compileMethodSegments(content) + startSystemend)end]*/ outputContent = thisDefinition + _this.compileMethodSegments(content) + /*System*/.lineSeparator();
					return templateString + headerWithParameters + " {" + outputContent + "}" + /*System*/.lineSeparator();
				}auto _lambda95_(auto _ref) {
				_this.frames = _this.frames.defineAll(params);
				/*Placeholder[input=Does not have a type of structure: Frames]*/ withBlock = _this.frames.within(_lambda97_);
				_this.frames = withBlock.right;
				return withBlock.left;
			}Option<CStructureSegment> compileMethod_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return /*Option*/.empty();
	}
	/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: char*]end]*/ definition = input.substring(0, paramStart).strip();
	/*Placeholder[input=Does not have a type of structure: char*]*/ withParams = input.substring(paramStart + 1);
	/*Placeholder[input=Does not have a type of structure: var]*/ paramEnd = withParams.indexOf(")");
	if (paramEnd < 0) {
		return /*Option*/.empty();
	}
	/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ inputParams = withParams.substring(0, paramEnd).strip();
	/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ withBraces = withParams.substring(paramEnd + 1).strip();
	/*Placeholder[input=Undefined field 'compileFunctionHeader' in 'App']*/ header = _this.compileFunctionHeader(definition);
	/*Placeholder[input=Undefined field 'compileParametersToList' in 'App']*/ params = _this.compileParametersToList(inputParams);
	/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]end]end]end]*/ outputParams = params.copy().addFirst(new_CDefinition(/*ArrayList*/.empty(), new_CPointerType(/*CPrimitiveType*/.Void), "_ref")).stream().map(generate_CDefinition).collect(new_Joiner(", "));
	/*typeof(header.generate() + "(" + outputParams + ")")*/ headerWithParameters = header.generate() + "(" + outputParams + ")";
	ArrayList<char*> typeParameters;
	if (/*header instanceof CDefinition definition1*/) {
		typeParameters = _this.frames.collectTypeParameters().copy().addAllLast(/*definition1*/.typeParameters);
	}
	else {
		typeParameters = _this.frames.collectTypeParameters().copy().addAllLast(/*ArrayList*/.empty());
	}
	/*Placeholder[input=createTemplateString]*/ templateString = /*createTemplateString*/(typeParameters);
	char* generated = templateString + headerWithParameters + ";" + /*System*/.lineSeparator();
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		/*Placeholder[input=Does not have a type of structure: var]*/ content = withBraces.substring(1, withBraces.length() - 1);
		/*Placeholder[input=Does not have a type of structure: Frames]*/ maybeCurrentStructure = _this.frames.findCurrentStructure();
		if (/*maybeCurrentStructure instanceof Some*/ < /*CStructureHeader>*/(/*var currentStructure*/)) {
			/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=new_CStatement]end]*/ thisDefinition = new_CStatement(new_CContent(/*currentStructure*/.toType().generate() + " _this = *((" + currentStructure.name() + "*) _ref)"), 1).generate();
			/*Placeholder[input=Does not have a type of structure: Frames]*/ framesWithParams = _this.frames.within(_lambda95_);
			generated = framesWithParams.left;
			_this.frames = framesWithParams.right;
		}
	}
	_this.functions = _this.functions.addLast(generated);
	return /*Option*/.of(new_EmptyCStructureSegment());
}
auto _lambda99_(auto _ref) {
	auto _this = _ref;
	return new_Placeholder(input);
};
auto _lambda102_(auto _ref) {
	auto _this = _ref;
	return _this.compileConstructor(input);
};
auto _lambda109_(auto _ref, auto header) {
	auto _this = _ref;
	return /*header*/.name;
};
auto _lambda105_(auto _ref, auto item) {
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: Frames]end]end]*/ currentStructureName = _this.frames.findCurrentStructure().map(_lambda109_).orElse("???");
		return new_CDefinition(/*item*/.typeParameters, /*item*/.type, /*item*/.name + "_" + currentStructureName);
	}CFunctionHeader compileFunctionHeader_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.compileDefinition(input). < /*CFunctionHeader>map*/(_lambda105_).or(_lambda102_).orElseGet(_lambda99_);
}
Option<CStructureSegment> compileDefinitionToField0_App(void* _ref, char* slice) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Undefined field 'compileDefinition' in 'App']*/ maybeDefinition = _this.compileDefinition(slice);
	if (/*!maybeDefinition*/.isPresent()) {
		return new_None<CStructureSegment>();
	}
	/*Placeholder[input=Does not have a type of structure: var]*/ definition = maybeDefinition.get();
	_this.frames = _this.frames.define(definition);
	return new_Some<CStructureSegment>(new_CStatement(definition, 1));
}
char* compileMethodSegments_App(void* _ref, char* content) {
	App _this = *((App*) _ref);
	return _this.compileStatements(content, compileMethodSegmentOrPlaceholder_this);
}
auto _lambda113_(auto _ref, auto header) {
	auto _this = _ref;
	return /*header*/.name;
};
Option<CFunctionHeader> compileConstructor_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ i = input.lastIndexOf(" ");
	if (i >= 0) {
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: char*]end]*/ name = input.substring(i + 1).strip();
		if (_this.isIdentifier(name)) {
			/*Placeholder[input=Does not have a type of structure: Frames]*/ peek0 = _this.frames.findCurrentStructure();
			if (/*peek0 instanceof Some*/ < /*CStructureHeader>*/(/*var peek*/)) {
				return /*Option*/.of(new_CDefinition(/*ArrayList*/.empty(), /*peek*/.toType(), "new_" + /*peek*/.name));
			}
		}
	}
	else {
		if (_this.isIdentifier(input)) {
			/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: Frames]end]end]*/ structName = _this.frames.findCurrentStructure().map(_lambda113_).orElse("???");
			return /*Option*/.of(new_CDefinition(/*ArrayList*/.empty(), new_CIdentifier(structName), "new_" + structName));
		}
	}
	return /*Option*/.empty();
}
auto _lambda118_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
Option<CStructureSegment> compileEnumValues_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=new_ArrayList<char*>]*/ segments = new_ArrayList<char*>(/*Arrays*/.stream(input.split(/*Pattern*/.quote(","))).map(strip_char*).filter(_lambda118_).toList());/*

		for (var segment : segments.inner) {
			final var stripped = segment.strip();
			final var maybeEnumValue = this.compileEnumValue(stripped);
			if (maybeEnumValue.isPresent()) {
				this.globals = this.globals.addLast(maybeEnumValue.get());
			} else {
				return Option.empty();
			}
		}*/
	return /*Option*/.of(new_EmptyCStructureSegment());
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
					return Option.of(structureName + " " + name + "Value = " + structureName + " { " + arguments + " };" +
													 System.lineSeparator());
				}
			}
		}*/
	return /*Option*/.empty();
}
auto _lambda120_(auto _ref) {
	auto _this = _ref;
	return /*Placeholder*/.wrap(input);
};
char* compileMethodSegmentOrPlaceholder_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.compileMethodSegment(input).orElseGet(_lambda120_);
}
auto _lambda122_(auto _ref) {
	auto _this = _ref;
	return _this.compileMethodSegments(content);
};
Option<char*> compileMethodSegment_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ stripped = input.strip();
	if (/*stripped.isEmpty() || stripped.startsWith("try ") || stripped*/.startsWith("catch ")) {
		return /*Option*/.of("");
	}
	if (stripped.startsWith("{") && stripped.endsWith("}")) {
		/*Placeholder[input=Does not have a type of structure: var]*/ content = stripped.substring(1, stripped.length() - 1);
		_this.depth++;
		/*Placeholder[input=Does not have a type of structure: Frames]*/ within = _this.frames.within(_lambda122_);
		/*Does not have a type of structure: var*/ compiled = within.left;
		_this.frames = within.right;
		/*this.depth--*/;
		return /*Option*/.of("{" + compiled + App.generateIndent(this.depth) + "}");
	}
	/*Placeholder[input=Undefined field 'compileConditional' in 'App']*/ maybeIf = _this.compileConditional(stripped, "if");
	if (maybeIf.isPresent()) {
		return maybeIf;
	}
	/*Placeholder[input=Undefined field 'compileConditional' in 'App']*/ maybeWhile = _this.compileConditional(stripped, "while");
	if (maybeWhile.isPresent()) {
		return maybeWhile;
	}
	if (stripped.endsWith(";")) {
		/*Placeholder[input=Does not have a type of structure: var]*/ slice = stripped.substring(0, stripped.length() - 1);
		return /*Option*/.of(new_CStatement(new_CContent(_this.compileMethodStatement(slice)), _this.depth).generate());
	}
	if (stripped.startsWith("else ")) {
		/*Placeholder[input=Does not have a type of structure: var]*/ substring = stripped.substring(5);
		return /*Option*/.of(/*App*/.generateIndent(_this.depth) + "else " + _this.compileMethodSegmentOrPlaceholder(substring));
	}
	return /*Option*/.empty();
}
Option<char*> compileConditional_App(void* _ref, char* input, char* type) {
	App _this = *((App*) _ref);
	if (input.startsWith(type)) {
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: char*]end]*/ substring = input.substring(type.length()).strip();/*
			if (substring.startsWith("(")) {
				final var withCondition = substring.substring(1);
				final var conditionEnd = this.findConditionEnd(withCondition);

				if (conditionEnd >= 0) {
					final var condition = withCondition.substring(0, conditionEnd).strip();
					final var substring2 = withCondition.substring(conditionEnd + 1).strip();
					return Option.of(App.generateIndent(this.depth) + type + " (" + this.compileExpression(condition) + ") " +
													 this.compileMethodSegmentOrPlaceholder(substring2));
				}
			}*/
	}
	return /*Option*/.empty();
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
auto _lambda124_(auto _ref) {
	auto _this = _ref;
	return /*Placeholder*/.wrap(stripped);
};
auto _lambda127_(auto _ref) {
	auto _this = _ref;
	return _this.parseAndDefineDefinitionAsStatement(input);
};
char* compileMethodStatement_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ stripped = input.strip();
	if (stripped.startsWith("return ")) {
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ slice = stripped.substring("return ".length()).strip();
		return "return " + _this.compileExpression(slice);
	}
	/*Placeholder[input=Undefined field 'compileAssignment' in 'App']*/ maybeAssignment = _this.compileAssignment(stripped);
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
	return _this.compileInvocation(stripped).map(generate_CExpression).or(_lambda127_).orElseGet(_lambda124_);
}
Option<char*> compileAssignment_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ separator = stripped.indexOf('=');
	if (separator < 0) {
		return new_None<char*>();
	}
	/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: char*]end]*/ destinationString = stripped.substring(0, separator).strip();
	/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: char*]end]*/ sourceString = stripped.substring(separator + 1).strip();
	/*Placeholder[input=Undefined field 'parseExpression' in 'App']*/ source = _this.parseExpression(sourceString);
	/*Placeholder[input=Does not have a type of structure: starttypeof(_this.compileAssignmentContent(destinationString, source) + " = " + source)end]*/ generated = _this.compileAssignmentContent(destinationString, source) + " = " + source.generate();
	return new_Some<char*>(generated);
}
char* compileAssignmentContent_App(void* _ref, char* destinationString, CExpression source) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Undefined field 'parseAndDefineDefinition' in 'App']*/ stringOption = _this.parseAndDefineDefinition(destinationString);
	return _switch129_;
}
CType resolveExpression_App(void* _ref, CExpression expression) {
	App _this = *((App*) _ref);
	return _switch131_;
}
CType resolveCaller_App(void* _ref, CCaller caller) {
	App _this = *((App*) _ref);
	return _switch133_;
}
CType resolveIdentifier_App(void* _ref, CIdentifier identifier) {
	App _this = *((App*) _ref);
	if (identifier.value.equals("_this")) {
		/*Placeholder[input=Does not have a type of structure: Frames]*/ maybeCurrentScope = _this.frames.findCurrentScope();
		if (/*maybeCurrentScope instanceof Some*/(/*var currentScope*/)) {
			return new_CStructureType(/*currentScope*/.left.name, /*currentScope*/.right);
		}
	}
	/*Placeholder[input=Does not have a type of structure: Frames]*/ maybeDefinition = _this.frames.resolve(identifier.value);
	if (/*maybeDefinition instanceof Some*/ < /*CDefinition>*/(/*var found*/)) {
		return /*found*/.type;
	}
	return new_Placeholder(identifier.value);
}
Option<char*> parseAndDefineDefinitionAsStatement_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.parseAndDefineDefinition(input).map(generate_CDefinition);
}
Option<CDefinition> parseAndDefineDefinition_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Undefined field 'compileDefinition' in 'App']*/ maybeDefinition = _this.compileDefinition(input);
	if (/*!maybeDefinition*/.isPresent()) {
		return new_None<CDefinition>();
	}
	/*Placeholder[input=Does not have a type of structure: var]*/ definition = maybeDefinition.get();
	_this.frames = _this.frames.define(definition);
	return new_Some<CDefinition>(definition);
}
char* compileExpression_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.parseExpression(input).generate();
}
auto _lambda135_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "<");
};
auto _lambda138_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, ">=");
};
auto _lambda141_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "==");
};
auto _lambda144_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "&&");
};
auto _lambda147_(auto _ref) {
	auto _this = _ref;
	return _this.compileOperator(stripped, "-");
};
CExpression parseExpression_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ stripped = input.strip();
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
	/*Placeholder[input=Undefined field 'compileLambda' in 'App']*/ maybeLambda = _this.compileLambda(stripped);
	if (maybeLambda.isPresent()) {
		return new_CContent(maybeLambda.get());
	}
	/*Placeholder[input=Undefined field 'compileInvocation' in 'App']*/ maybeInvocation = _this.compileInvocation(stripped);
	if (maybeInvocation.isPresent()) {
		return maybeInvocation.get();
	}
	/*Placeholder[input=Does not have a type of structure: var]*/ i = stripped.lastIndexOf(".");
	if (i >= 0) {
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ child = stripped.substring(0, i).strip();
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ name = stripped.substring(i + 1).strip();
		if (_this.isIdentifier(name)) {
			/*Placeholder[input=Undefined field 'parseExpression' in 'App']*/ newChild = _this.parseExpression(child);
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
	/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Undefined field 'compileOperator' in 'App']end]end]end]end]end]*/ maybeOperator = _this.compileOperator(stripped, "+").or(_lambda147_).or(_lambda144_).or(_lambda141_).or(_lambda138_).or(_lambda135_);
	if (maybeOperator.isPresent()) {
		return new_CContent(maybeOperator.get());
	}
	/*Placeholder[input=Does not have a type of structure: var]*/ i2 = stripped.lastIndexOf("::");
	if (i2 >= 0) {
		/*Placeholder[input=Does not have a type of structure: var]*/ substring = stripped.substring(0, i2);
		/*Placeholder[input=Does not have a type of structure: var]*/ substring1 = stripped.substring(i2 + 2);
		return new_CContent(substring1 + "_" + _this.compileType(substring).map(generate_CType).orElse("?"));
	}
	if (_this.isNumber(stripped)) {
		return new_CContent(stripped);
	}
	return new_Placeholder(stripped);
}
auto _lambda152_(auto _ref, auto segment) {
	auto _this = _ref;
	return "auto " + /*segment*/;
};
auto _lambda155_(auto _ref, auto segment) {
	auto _this = _ref;
	return /*!segment*/.isEmpty();
};
auto _lambda158_(auto _ref) {
			/*Placeholder[input=Undefined field 'compileExpression' in 'App']*/ expression = _this.compileExpression(content);
			return "{" + new_CStatement(new_CContent("auto _this = _ref"), 1).generate() + /*new CStatement(new CContent("return "*/ + /*expression),
																																			1).generate()*/ + /*System*/.lineSeparator() + "};" + /*System*/.lineSeparator();
		}Option<char*> compileLambda_App(void* _ref, char* stripped) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ arrowIndex = stripped.indexOf("->");
	if (arrowIndex >= 0) {
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: char*]end]*/ names = stripped.substring(0, arrowIndex).strip();
		/*Placeholder[input=Does not have a type of structure: char*]*/ content = stripped.substring(arrowIndex + 2);
		/*Placeholder[input=Undefined field 'createName' in 'App']*/ functionName = _this.createName("lambda");
		ArrayList<char*> parameters;
		if (_this.isIdentifier(names)) {
			parameters = /*ArrayList*/.of("auto " + names);
		}
		else 
		if (names.startsWith("(") && names.endsWith(")")) {
			/*Placeholder[input=Does not have a type of structure: var]*/ slice = names.substring(1, names.length() - 1);
			parameters = _this.divide(slice, foldValue_this).map(strip_char*).filter(_lambda155_).map(_lambda152_).toList();
		}
		else {
			return /*Option*/.empty();
		}
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: ArrayList<char*>]end]*/ copy = parameters.copy().addFirst("auto _ref");
		_this.functions = _this.functions.addLast("auto " + functionName + "(" + /*String*/.join(", ", copy.inner) + ") " + _this.compileMethodSegment(content).orElseGet(_lambda158_));
		return /*Option*/.of(functionName);
	}
	return /*Option*/.empty();
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
					return Option.of(new CInvocation(maybeCaller.get(), arguments));
				}
			}
		}*/
	return /*Option*/.empty();
}
Option<CCaller> parseCaller_App(void* _ref, char* caller) {
	App _this = *((App*) _ref);
	if (caller.startsWith("new ")) {
		/*Placeholder[input=Does not have a type of structure: char*]*/ substring = caller.substring("new ".length());
		/*Placeholder[input=Undefined field 'compileType' in 'App']*/ maybeType = _this.compileType(substring);
		if (maybeType.isPresent()) {
			/*Placeholder[input=Does not have a type of structure: var]*/ type = maybeType.get();
			return /*Option*/.of(new_CConstruction(type));
		}
	}
	return /*Option*/.of(_this.parseExpression(caller));
}
Option<char*> compileOperator_App(void* _ref, char* stripped, char* separator) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ i1 = stripped.indexOf(separator);
	if (i1 >= 0) {
		/*Placeholder[input=Does not have a type of structure: char*]*/ substring = stripped.substring(0, i1);
		/*Placeholder[input=Does not have a type of structure: char*]*/ substring1 = stripped.substring(i1 + separator.length());
		return /*Option*/.of(_this.compileExpression(substring) + " " + separator + " " + _this.compileExpression(substring1));
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
	return 1;
}
auto _lambda166_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
ArrayList<CDefinition> compileParametersToList_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	return _this.divide(input, foldValue_this).map(strip_char*).filter(_lambda166_).map(compileDefinition_this).flatMap(stream_Option).toList();
}
auto _lambda170_(auto _ref, auto segment) {
	auto _this = _ref;
	return /*!segment*/.isEmpty();
};
auto _lambda172_(auto _ref, auto cType) {
	auto _this = _ref;
	return new_CDefinition(finalTypeParameters, /*cType*/, name);
};
auto _lambda174_(auto _ref, auto cType) {
	auto _this = _ref;
	return new_CDefinition(/*ArrayList*/.empty(), /*cType*/, name);
};
Option<CDefinition> compileDefinition_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ nameSeparator = input.lastIndexOf(" ");
	if (nameSeparator < 0) {
		return /*Option*/.empty();
	}
	/*Placeholder[input=Does not have a type of structure: char*]*/ beforeName = input.substring(0, nameSeparator);
	/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: char*]end]*/ name = input.substring(nameSeparator + 1).strip();
	if (/*!this*/.isIdentifier(name)) {
		return /*Option*/.empty();
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
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ beforeType = beforeName.substring(0, typeSeparator).strip();
		/*Placeholder[input=typeof(startArrayListend. < startString>emptyend)]*/ typeParameters = /*ArrayList*/. < /*String>empty*/();
		if (beforeType.endsWith(">")) {
			/*Placeholder[input=Does not have a type of structure: var]*/ slice = beforeType.substring(0, beforeType.length() - 1);
			/*Placeholder[input=Does not have a type of structure: var]*/ i = slice.indexOf("<");
			if (i >= 0) {
				/*Placeholder[input=Does not have a type of structure: var]*/ typeParametersString = slice.substring(i + 1);
				typeParameters = _this.divide(typeParametersString, foldValue_this).map(strip_char*).filter(_lambda170_).collect(new_ListCollector<char*>());
			}
		}
		/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: var]end]*/ type = beforeName.substring(typeSeparator + 1).strip();
		var finalTypeParameters = typeParameters;
		return _this.compileType(type).map(_lambda172_);
	}
	return _this.compileType(beforeName).map(_lambda174_);
}
auto _lambda182_(auto _ref, auto slice) {
	auto _this = _ref;
	return /*!slice*/.isEmpty();
};
Option<CType> compileType_App(void* _ref, char* input) {
	App _this = *((App*) _ref);
	/*Placeholder[input=Does not have a type of structure: char*]*/ stripped = input.strip();/*

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
	if (stripped.endsWith("[]")) {
		/*Placeholder[input=Does not have a type of structure: var]*/ slice = stripped.substring(0, stripped.length() - 2);
		return _this.compileType(slice).map(new_CPointerType);
	}
	if (stripped.equals("String")) {
		return /*Option*/.of(new_CPointerType(/*CPrimitiveType*/.Char));
	}
	if (stripped.endsWith(">")) {
		/*Placeholder[input=Does not have a type of structure: var]*/ withoutEnd = stripped.substring(0, stripped.length() - 1);
		/*Placeholder[input=Does not have a type of structure: var]*/ i = withoutEnd.indexOf("<");
		if (i >= 0) {
			/*Placeholder[input=Does not have a type of structure: var]*/ base = withoutEnd.substring(0, i);
			/*Placeholder[input=Does not have a type of structure: var]*/ typeArguments = withoutEnd.substring(i + 1);
			/*Placeholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Does not have a type of structure: startPlaceholder[input=Undefined field 'divide' in 'App']end]end]end]end]end]*/ list = _this.divide(typeArguments, foldValue_this).map(strip_char*).filter(_lambda182_).map(compileType_this).flatMap(stream_Option).toList();
			return /*Option*/.of(new_CTemplateType(base, list));
		}
	}
	if (_this.isIdentifier(stripped)) {
		if (/*stripped.equals("public") || stripped*/.equals("private")) {
			return /*Option*/.empty();
		}
		return /*Option*/.of(new_CIdentifier(stripped));
	}
	return /*Option*/.empty();
}
State foldValue_App(void* _ref, State state, char next) {
	App _this = *((App*) _ref);
	if (next == ',' && state.isLevel()) {
		return state.advance();
	}
	/*Placeholder[input=Does not have a type of structure: State]*/ appended = state.append(next);
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