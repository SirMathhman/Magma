JPrimitiveType JPrimitiveType_Void = new_JPrimitiveType(CPrimitiveType.Void);
JPrimitiveType JPrimitiveType_String = new_JPrimitiveType(/*new CPointerType*/(CPrimitiveType.Char));
CPrimitiveType CPrimitiveType_Char = new_CPrimitiveType(/*"char"*/);
CPrimitiveType CPrimitiveType_Void = new_CPrimitiveType(/*"void"*/);
struct JPrimitiveType {
	CType cType;
};
struct CPrimitiveType {
	char* content;
};
enum ResultTag {
	ErrType,
	OkType
};
template <typename T, typename X>
union ResultData {
	Err<T, X> err;
	Ok<T, X> ok;
}
template <typename T, typename X>
struct Result {
	ResultTag _tag;
	ResultData<T, X> _data;
};
enum CTypeTag {
	CFunctionTypeType,
	CIdentifierType,
	CPlaceholderType,
	CPointerTypeType,
	CPrimitiveTypeType,
	CStructureTypeType,
	CTemplateTypeType
};
union CTypeData {
	CFunctionType cfunctiontype;
	CIdentifier cidentifier;
	CPlaceholder cplaceholder;
	CPointerType cpointertype;
	CPrimitiveType cprimitivetype;
	CStructureType cstructuretype;
	CTemplateType ctemplatetype;
}
struct CType {
	CTypeTag _tag;
	CTypeData _data;
};
enum CDefinableTag {
	CDefinitionType,
	CPlaceholderType
};
union CDefinableData {
	CDefinition cdefinition;
	CPlaceholder cplaceholder;
}
struct CDefinable {
	CDefinableTag _tag;
	CDefinableData _data;
};
enum JMethodHeaderTag {
	JConstructorType,
	JDefinitionType,
	JPlaceholderType
};
union JMethodHeaderData {
	JConstructor jconstructor;
	JDefinition jdefinition;
	JPlaceholder jplaceholder;
}
struct JMethodHeader {
	JMethodHeaderTag _tag;
	JMethodHeaderData _data;
};
struct JType {
};
enum JExpressionTag {
	JIdentifierType,
	JInvocationType,
	JMemberAccessType,
	JPlaceholderType
};
union JExpressionData {
	JIdentifier jidentifier;
	JInvocation jinvocation;
	JMemberAccess jmemberaccess;
	JPlaceholder jplaceholder;
}
struct JExpression {
	JExpressionTag _tag;
	JExpressionData _data;
};
enum CExpressionTag {
	CFieldAccessType,
	CIdentifierType,
	CInvocationType,
	CPlaceholderType
};
union CExpressionData {
	CFieldAccess cfieldaccess;
	CIdentifier cidentifier;
	CInvocation cinvocation;
	CPlaceholder cplaceholder;
}
struct CExpression {
	CExpressionTag _tag;
	CExpressionData _data;
};
template <typename T>
struct Head {
};
template <typename T, typename C>
struct Collector {
};
enum JClassSegmentTag {
	JClassSegmentWrapperType,
	JPlaceholderType
};
union JClassSegmentData {
	JClassSegmentWrapper jclasssegmentwrapper;
	JPlaceholder jplaceholder;
}
struct JClassSegment {
	JClassSegmentTag _tag;
	JClassSegmentData _data;
};
enum JIncompleteClassSegmentTag {
	JClassSegmentWrapperType,
	JIncompleteMethodType,
	JPlaceholderType
};
union JIncompleteClassSegmentData {
	JClassSegmentWrapper jclasssegmentwrapper;
	JIncompleteMethod jincompletemethod;
	JPlaceholder jplaceholder;
}
struct JIncompleteClassSegment {
	JIncompleteClassSegmentTag _tag;
	JIncompleteClassSegmentData _data;
};
struct CStructureSegment {
};
template <typename T>
struct Stream {
	Head<T> head;
};
template <typename T>
struct List {
	java.util.List<T> nativeList;
};
template <typename T, typename X>
struct Err {
	X error;
};
template <typename T, typename X>
struct Ok {
	T value;
};
struct CPointerType {
	CType child;
};
struct CTemplateType {
	char* base;
	List<CType> typeArguments;
};
struct CIdentifier {
	char* input;
};
struct CPlaceholder {
	char* input;
};
struct CDefinition {
	CType type;
	char* name;
};
struct JDefinition {
	Optional<char*> beforeType;
	JType type;
	char* name;
};
struct JConstructor {
	char* input;
};
struct JPlaceholder {
	char* input;
};
struct JArrayType {
	JType type;
};
struct JGenericType {
	char* base;
	List<JType> typeArguments;
};
struct JIdentifier {
	char* value;
};
struct CFieldAccess {
	CExpression child;
	char* name;
};
struct JMemberAccess {
	JExpression child;
	char* name;
};
struct CStructureType {
	char* name;
	List<CDefinition> fields;
};
struct JClassType {
	char* name;
	List<JDefinition> members;
};
struct CInvocation {
	CExpression cExpression;
	List<CExpression> arguments;
};
struct JInvocation {
	JExpression caller;
	List<JExpression> arguments;
};
struct CFunctionType {
	CType returnType;
	List<CType> paramTypes;
};
struct JMethodType {
	JType returnType;
	List<JType> paramTypes;
};
template <typename T>
struct AllMatch {
	Predicate<T> predicate;
};
template <typename T, typename R>
struct MapHead {
	Head<T> head;
	/*R>*/ mapper;
};
struct CStructureSegmentWrapper {
	char* output;
};
struct JClassSegmentWrapper {
	char* output;
};
struct CFunction {
	CDefinable header;
	List<CDefinable> cParameters;
	char* content;
};
struct JIncompleteMethod {
	JMethodHeader header;
	List<JDefinition> parameters;
	char* content;
};
struct Main {
};
JType toJType_JPrimitiveType(void* _ref){
	JPrimitiveType _this = *((JPrimitiveType*) _ref);
	JTypeData data;
	data.err = this;
	return JType { JPrimitiveTypeType, data };
}
JPrimitiveType new_JPrimitiveType(CType cType) {
	JPrimitiveType this;
	this.cType = cType;
	return this;
}
CType toCType_JPrimitiveType(void* _ref) {
	JPrimitiveType _this = *((JPrimitiveType*) _ref);
	return this.cType;
}
CType toCType_CPrimitiveType(void* _ref){
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	CTypeData data;
	data.err = this;
	return CType { CPrimitiveTypeType, data };
}
CPrimitiveType new_CPrimitiveType(char* content) {
	CPrimitiveType this;
	this.content = content;
	return this;
}
char* generate_CPrimitiveType(void* _ref) {
	CPrimitiveType _this = *((CPrimitiveType*) _ref);
	return this.content;
}
char* generate_CType(void* _ref);
char* generate_CDefinable(void* _ref);
CDefinable toCDefinition_JMethodHeader(void* _ref);
CType toCType_JType(void* _ref);
List<char*> findTypeParameters_JType(void* _ref) {
	JType _this = *((JType*) _ref);
	return /*new List<String>*/();
}
JType remap_JType(void* _ref, /*JType>*/ mapping) {
	JType _this = *((JType*) _ref);
	return this;
}
CExpression toCExpression_JExpression(void* _ref);
char* generate_CExpression(void* _ref);
Optional<T> next_Head(void* _ref);
C createInitial_Collector(void* _ref);
C fold_Collector(void* _ref, C current, T element);
CStructureSegment toCStructureSegment_JClassSegment(void* _ref);
JClassSegment complete_JIncompleteClassSegment(void* _ref);
Optional<JDefinition> createDefinition_JIncompleteClassSegment(void* _ref);
char* generate_CStructureSegment(void* _ref);
Stream<T> fromOptional_Stream(void* _ref, Optional<T> optional) {
	Stream _this = *((Stream*) _ref);
	return /*new Stream<T>*/(/*optional.<Head<T>>map*/(/*SingleHead::new).orElseGet(EmptyHead::new*/));
}
Stream<T> fromArray_Stream(void* _ref, T* array) {
	Stream _this = *((Stream*) _ref);
	return /*new Stream<Integer>*/(/*new LengthHead(array.length)).map(index -> array[index]*/);
}
Stream<T> concat_Stream(void* _ref, Stream<T> second) {
	Stream _this = *((Stream*) _ref);
	return /*new Stream<T>*/((/*) -> this.next().or(second::next*/));
}
Optional<T> next_Stream(void* _ref) {
	Stream _this = *((Stream*) _ref);
	return this.head.next();
}
Stream<R> map_Stream(void* _ref, /*R>*/ mapper) {
	Stream _this = *((Stream*) _ref);
	return /*new Stream<R>*/(/*new MapHead<T*/, /*R>(this*/.head, /*mapper)*/);
}
C collect_Stream(void* _ref, /*C>*/ collector) {
	Stream _this = *((Stream*) _ref);
	return this.fold(collector.createInitial(), /*collector::fold*/);
}
R fold_Stream(void* _ref, R initial, /*R>*/ folder) {
	Stream _this = *((Stream*) _ref);
	R current = initial;/*while (true) {
				final var maybeNext = this.head.next();
				if (maybeNext.isPresent()) {
					final var next = maybeNext.get();
					current = folder.apply(current, next);
				} else {
					return current;
				}
			}*/
}
List<T> toList_Stream(void* _ref) {
	Stream _this = *((Stream*) _ref);
	return this.collect(/*new ListCollector<T>*/());
}
Stream<T> filter_Stream(void* _ref, Predicate<T> predicate) {
	Stream _this = *((Stream*) _ref);/*return this.flatMap(element -> {
				if (predicate.test(element)) {
					return new Stream<T>(new SingleHead<T>(element));
				}
				return new Stream<T>(new EmptyHead<T>());
			}*/
	/*)*/;
}
Stream<R> flatMap_Stream(void* _ref, Stream</*R>*/> mapper) {
	Stream _this = *((Stream*) _ref);
	return /*new Stream<R>*/(/*new FlatMapHead<T*/, /*R>(this*/.head, /*mapper)*/);
}
Optional<T> findFirst_Stream(void* _ref) {
	Stream _this = *((Stream*) _ref);
	return this.head.next();
}
public List_List(void* _ref) {
	List _this = *((List*) _ref);
	/*this(new ArrayList<T>())*/;
}
List<T> of_List(void* _ref, /*T...*/ elements) {
	List _this = *((List*) _ref);
	return /*new List<T>*/(/*new ArrayList<T>*/(Arrays.asList(elements)));
}
void forEach_List(void* _ref, Consumer<T> consumer) {
	List _this = *((List*) _ref);
	/*this.nativeList.forEach(consumer)*/;
}
Stream<T> stream_List(void* _ref) {
	List _this = *((List*) _ref);
	return /*new Stream<Integer>*/(/*new LengthHead(this.nativeList.size())).map(this.nativeList::get*/);
}
List<T> addLast_List(void* _ref, T definition) {
	List _this = *((List*) _ref);
	/*this.nativeList.addLast(definition)*/;
	return this;
}
List<T> reversed_List(void* _ref) {
	List _this = *((List*) _ref);
	return /*new List<T>*/(this.nativeList.reversed());
}
int size_List(void* _ref) {
	List _this = *((List*) _ref);
	return this.nativeList.size();
}
T get_List(void* _ref, int index) {
	List _this = *((List*) _ref);
	return this.nativeList.get(index);
}
T getLast_List(void* _ref) {
	List _this = *((List*) _ref);
	return this.nativeList.getLast();
}
List<T> removeLast_List(void* _ref) {
	List _this = *((List*) _ref);
	/*this.nativeList.removeLast()*/;
	return this;
}
List<T> set_List(void* _ref, int index, T element) {
	List _this = *((List*) _ref);
	/*this.nativeList.set(index, element)*/;
	return this;
}
boolean isEmpty_List(void* _ref) {
	List _this = *((List*) _ref);
	return this.nativeList.isEmpty();
}
List<T> addFirst_List(void* _ref, T element) {
	List _this = *((List*) _ref);
	/*this.nativeList.addFirst(element)*/;
	return this;
}
List<T> mapLast_List(void* _ref, /*T>*/ mapper) {
	List _this = *((List*) _ref);
	/*this.nativeList.set(this.nativeList.size() - 1, mapper.apply(this.nativeList.getLast()))*/;
	return this;
}
List<T> addAllLast_List(void* _ref, List<T> others) {
	List _this = *((List*) _ref);
	return others.stream(/*).fold(this*/, /*List::addLast*/);
}
boolean contains_List(void* _ref, T element) {
	List _this = *((List*) _ref);
	return this.nativeList.contains(element);
}
Result<T, X> toResult<T, X>_Err(void* _ref){
	Err<T, X> _this = *((Err<T, X>*) _ref);
	Result<T, X>Data data;
	data.err = this;
	return Result<T, X> { ErrType, data };
}
Result<T, X> toResult<T, X>_Ok(void* _ref){
	Ok<T, X> _this = *((Ok<T, X>*) _ref);
	Result<T, X>Data data;
	data.err = this;
	return Result<T, X> { OkType, data };
}
CType toCType_CPointerType(void* _ref){
	CPointerType _this = *((CPointerType*) _ref);
	CTypeData data;
	data.err = this;
	return CType { CPointerTypeType, data };
}
char* generate_CPointerType(void* _ref) {
	CPointerType _this = *((CPointerType*) _ref);
	return /*this.child.generate() + "*"*/;
}
CType toCType_CTemplateType(void* _ref){
	CTemplateType _this = *((CTemplateType*) _ref);
	CTypeData data;
	data.err = this;
	return CType { CTemplateTypeType, data };
}
char* generate_CTemplateType(void* _ref) {
	CTemplateType _this = *((CTemplateType*) _ref);
	CTemplateType cTemplateType = this;
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JGenericType[base=List, typeArguments=List[nativeList=[JIdentifier[value=CType]]]]]*/ stream = cTemplateType.typeArguments.stream();
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JPlaceholder[input=Failed to resolve caller: JPlaceholder[input=Not a structure type: JGenericType[base=List, typeArguments=List[nativeList=[JIdentifier[value=CType]]]]]]]*/ stringStream = stream.map(/*CType::generate*/);
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JPlaceholder[input=Failed to resolve caller: JPlaceholder[input=Not a structure type: JPlaceholder[input=Failed to resolve caller: JPlaceholder[input=Not a structure type: JGenericType[base=List, typeArguments=List[nativeList=[JIdentifier[value=CType]]]]]]]]]*/ joined = stringStream.collect(/*new Collectors.Joiner("*/, /*")*/);
	return /*this.base + "<" + joined + ">"*/;
}
/*CType, CExpression*/ to/*CType, CExpression*/_CIdentifier(void* _ref){
	CIdentifier _this = *((CIdentifier*) _ref);
	/*CType, CExpression*/Data data;
	data.err = this;
	return /*CType, CExpression*/ { CIdentifierType, data };
}
char* generate_CIdentifier(void* _ref) {
	CIdentifier _this = *((CIdentifier*) _ref);
	return this.input;
}
/*CType, CDefinable, CExpression, CStructureSegment*/ to/*CType, CDefinable, CExpression, CStructureSegment*/_CPlaceholder(void* _ref){
	CPlaceholder _this = *((CPlaceholder*) _ref);
	/*CType, CDefinable, CExpression, CStructureSegment*/Data data;
	data.err = this;
	return /*CType, CDefinable, CExpression, CStructureSegment*/ { CPlaceholderType, data };
}
char* wrap_CPlaceholder(void* _ref, char* input) {
	CPlaceholder _this = *((CPlaceholder*) _ref);
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: String]*/ replaced = input.replace(/*"start"*/, /*"start").replace("end"*/, /*"end"*/);
	return /*"start" + replaced + "end"*/;
}
char* generate_CPlaceholder(void* _ref) {
	CPlaceholder _this = *((CPlaceholder*) _ref);
	return wrap(this.input);
}
CDefinable toCDefinable_CDefinition(void* _ref){
	CDefinition _this = *((CDefinition*) _ref);
	CDefinableData data;
	data.err = this;
	return CDefinable { CDefinitionType, data };
}
char* generate_CDefinition(void* _ref) {
	CDefinition _this = *((CDefinition*) _ref);
	return /*this.type.generate() + " " + this*/.name;
}
JMethodHeader toJMethodHeader_JDefinition(void* _ref){
	JDefinition _this = *((JDefinition*) _ref);
	JMethodHeaderData data;
	data.err = this;
	return JMethodHeader { JDefinitionType, data };
}
CDefinable toCDefinition_JDefinition(void* _ref) {
	JDefinition _this = *((JDefinition*) _ref);
	return /*new CDefinition*/(this.type.toCType(), this.name);
}
JDefinition mapType_JDefinition(void* _ref, /*JType>*/ mapper) {
	JDefinition _this = *((JDefinition*) _ref);
	return /*new JDefinition*/(this.beforeType, mapper.apply(this.type), this.name);
}
JMethodHeader toJMethodHeader_JConstructor(void* _ref){
	JConstructor _this = *((JConstructor*) _ref);
	JMethodHeaderData data;
	data.err = this;
	return JMethodHeader { JConstructorType, data };
}
CDefinable toCDefinition_JConstructor(void* _ref) {
	JConstructor _this = *((JConstructor*) _ref);
	/*Failed to resolve caller: JPlaceholder[input=JPlaceholder[input=new CIdentifier]]*/ type = /*new CIdentifier*/(this.input);
	return /*new CDefinition*/(type, /*"new_" + this*/.input);
}
/*JMethodHeader, JType, JExpression, JIncompleteClassSegment, JClassSegment*/ to/*JMethodHeader, JType, JExpression, JIncompleteClassSegment, JClassSegment*/_JPlaceholder(void* _ref){
	JPlaceholder _this = *((JPlaceholder*) _ref);
	/*JMethodHeader, JType, JExpression, JIncompleteClassSegment, JClassSegment*/Data data;
	data.err = this;
	return /*JMethodHeader, JType, JExpression, JIncompleteClassSegment, JClassSegment*/ { JPlaceholderType, data };
}
CDefinable toCDefinition_JPlaceholder(void* _ref) {
	JPlaceholder _this = *((JPlaceholder*) _ref);
	return /*new CPlaceholder*/(this.input);
}
CType toCType_JPlaceholder(void* _ref) {
	JPlaceholder _this = *((JPlaceholder*) _ref);
	return /*new CPlaceholder*/(this.input);
}
CExpression toCExpression_JPlaceholder(void* _ref) {
	JPlaceholder _this = *((JPlaceholder*) _ref);
	return /*new CPlaceholder*/(this.input);
}
JClassSegment complete_JPlaceholder(void* _ref) {
	JPlaceholder _this = *((JPlaceholder*) _ref);
	return /*new JPlaceholder*/(this.input);
}
Optional<JDefinition> createDefinition_JPlaceholder(void* _ref) {
	JPlaceholder _this = *((JPlaceholder*) _ref);
	return Optional.empty();
}
CStructureSegment toCStructureSegment_JPlaceholder(void* _ref) {
	JPlaceholder _this = *((JPlaceholder*) _ref);
	return /*new CPlaceholder*/(this.input);
}
JType toJType_JArrayType(void* _ref){
	JArrayType _this = *((JArrayType*) _ref);
	JTypeData data;
	data.err = this;
	return JType { JArrayTypeType, data };
}
CType toCType_JArrayType(void* _ref) {
	JArrayType _this = *((JArrayType*) _ref);
	return /*new CPointerType*/(this.type.toCType());
}
JType toJType_JGenericType(void* _ref){
	JGenericType _this = *((JGenericType*) _ref);
	JTypeData data;
	data.err = this;
	return JType { JGenericTypeType, data };
}
CType toCType_JGenericType(void* _ref) {
	JGenericType _this = *((JGenericType*) _ref);
	return /*new CTemplateType*/(this.base, this.typeArguments.stream(/*).map(JType::toCType).toList(*/));
}
/*JType, JExpression*/ to/*JType, JExpression*/_JIdentifier(void* _ref){
	JIdentifier _this = *((JIdentifier*) _ref);
	/*JType, JExpression*/Data data;
	data.err = this;
	return /*JType, JExpression*/ { JIdentifierType, data };
}
CType toCType_JIdentifier(void* _ref) {
	JIdentifier _this = *((JIdentifier*) _ref);
	return /*new CIdentifier*/(this.value);
}
CExpression toCExpression_JIdentifier(void* _ref) {
	JIdentifier _this = *((JIdentifier*) _ref);
	return /*new CIdentifier*/(this.value);
}
CExpression toCExpression_CFieldAccess(void* _ref){
	CFieldAccess _this = *((CFieldAccess*) _ref);
	CExpressionData data;
	data.err = this;
	return CExpression { CFieldAccessType, data };
}
char* generate_CFieldAccess(void* _ref) {
	CFieldAccess _this = *((CFieldAccess*) _ref);
	return /*this.child.generate() + "." + this*/.name;
}
JExpression toJExpression_JMemberAccess(void* _ref){
	JMemberAccess _this = *((JMemberAccess*) _ref);
	JExpressionData data;
	data.err = this;
	return JExpression { JMemberAccessType, data };
}
CExpression toCExpression_JMemberAccess(void* _ref) {
	JMemberAccess _this = *((JMemberAccess*) _ref);/*if (this.child instanceof JIdentifier(String enumName)) {
				if (enumNames.contains(enumName)) {
					return new CIdentifier(enumName + "_" + this.name);
				}
			}*/
	return /*new CFieldAccess*/(this.child.toCExpression(), this.name);
}
CType toCType_CStructureType(void* _ref){
	CStructureType _this = *((CStructureType*) _ref);
	CTypeData data;
	data.err = this;
	return CType { CStructureTypeType, data };
}
char* generate_CStructureType(void* _ref) {
	CStructureType _this = *((CStructureType*) _ref);
	return this.name;
}
JType toJType_JClassType(void* _ref){
	JClassType _this = *((JClassType*) _ref);
	JTypeData data;
	data.err = this;
	return JType { JClassTypeType, data };
}
CType toCType_JClassType(void* _ref) {
	JClassType _this = *((JClassType*) _ref);
	return /*new CStructureType*/(this.name, this.members.stream(/*)
																		.map(definition -> new CDefinition(definition.type.toCType(*/), /*definition.name))*/.toList());
}
Optional<JType> resolve_JClassType(void* _ref, char* name) {
	JClassType _this = *((JClassType*) _ref);
	return this.members.stream(/*)
					.filter(definition -> definition.name.equals(name))
					.map(definition -> definition.type)
					.findFirst(*/);
}
JClassType attachMembers_JClassType(void* _ref, List<JDefinition> otherMembers) {
	JClassType _this = *((JClassType*) _ref);
	return /*new JClassType*/(this.name, this.members.addAllLast(otherMembers));
}
CExpression toCExpression_CInvocation(void* _ref){
	CInvocation _this = *((CInvocation*) _ref);
	CExpressionData data;
	data.err = this;
	return CExpression { CInvocationType, data };
}
char* generate_CInvocation(void* _ref) {
	CInvocation _this = *((CInvocation*) _ref);
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JGenericType[base=List, typeArguments=List[nativeList=[JIdentifier[value=CExpression]]]]]*/ joined = this.arguments.stream(/*).map(CExpression::generate).collect(new Collectors.Joiner("*/, /*")*/);
	return /*this.cExpression.generate() + "(" + joined + ")"*/;
}
JExpression toJExpression_JInvocation(void* _ref){
	JInvocation _this = *((JInvocation*) _ref);
	JExpressionData data;
	data.err = this;
	return JExpression { JInvocationType, data };
}
CExpression toCExpression_JInvocation(void* _ref) {
	JInvocation _this = *((JInvocation*) _ref);
	return /*new CInvocation*/(this.caller.toCExpression(), this.arguments.stream(/*).map(JExpression::toCExpression).toList(*/));
}
CType toCType_CFunctionType(void* _ref){
	CFunctionType _this = *((CFunctionType*) _ref);
	CTypeData data;
	data.err = this;
	return CType { CFunctionTypeType, data };
}
char* generate_CFunctionType(void* _ref) {
	CFunctionType _this = *((CFunctionType*) _ref);
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JGenericType[base=List, typeArguments=List[nativeList=[JIdentifier[value=CType]]]]]*/ joinedParameterTypes = this.paramTypes.stream(/*).map(CType::generate).collect(new Collectors.Joiner("*/, /*")*/);
	return /*this.returnType.generate() + " (*)(" + joinedParameterTypes + ")"*/;
}
JType toJType_JMethodType(void* _ref){
	JMethodType _this = *((JMethodType*) _ref);
	JTypeData data;
	data.err = this;
	return JType { JMethodTypeType, data };
}
CType toCType_JMethodType(void* _ref) {
	JMethodType _this = *((JMethodType*) _ref);
	return /*new CFunctionType*/(this.returnType.toCType(), this.paramTypes.stream(/*).map(JType::toCType).toList(*/));
}
Collector<T, Boolean> toCollector<T, Boolean>_AllMatch(void* _ref){
	AllMatch<T> _this = *((AllMatch<T>*) _ref);
	Collector<T, Boolean>Data data;
	data.err = this;
	return Collector<T, Boolean> { AllMatchType, data };
}
Boolean createInitial_AllMatch(void* _ref) {
	AllMatch _this = *((AllMatch*) _ref);
	return true;
}
Boolean fold_AllMatch(void* _ref, Boolean current, T element) {
	AllMatch _this = *((AllMatch*) _ref);
	return /*current && this*/.predicate.test(element);
}
Head<R> toHead<R>_MapHead(void* _ref){
	MapHead<T, R> _this = *((MapHead<T, R>*) _ref);
	Head<R>Data data;
	data.err = this;
	return Head<R> { MapHeadType, data };
}
Optional<R> next_MapHead(void* _ref) {
	MapHead _this = *((MapHead*) _ref);
	return this.head.next(/*).map(this*/.mapper);
}
CStructureSegment toCStructureSegment_CStructureSegmentWrapper(void* _ref){
	CStructureSegmentWrapper _this = *((CStructureSegmentWrapper*) _ref);
	CStructureSegmentData data;
	data.err = this;
	return CStructureSegment { CStructureSegmentWrapperType, data };
}
char* generate_CStructureSegmentWrapper(void* _ref) {
	CStructureSegmentWrapper _this = *((CStructureSegmentWrapper*) _ref);
	return this.output;
}
/*JIncompleteClassSegment, JClassSegment*/ to/*JIncompleteClassSegment, JClassSegment*/_JClassSegmentWrapper(void* _ref){
	JClassSegmentWrapper _this = *((JClassSegmentWrapper*) _ref);
	/*JIncompleteClassSegment, JClassSegment*/Data data;
	data.err = this;
	return /*JIncompleteClassSegment, JClassSegment*/ { JClassSegmentWrapperType, data };
}
JClassSegment complete_JClassSegmentWrapper(void* _ref) {
	JClassSegmentWrapper _this = *((JClassSegmentWrapper*) _ref);
	return /*new JClassSegmentWrapper*/(this.output);
}
Optional<JDefinition> createDefinition_JClassSegmentWrapper(void* _ref) {
	JClassSegmentWrapper _this = *((JClassSegmentWrapper*) _ref);
	return Optional.empty();
}
CStructureSegment toCStructureSegment_JClassSegmentWrapper(void* _ref) {
	JClassSegmentWrapper _this = *((JClassSegmentWrapper*) _ref);
	return /*new CStructureSegmentWrapper*/(this.output);
}
char* generate_CFunction(void* _ref) {
	CFunction _this = *((CFunction*) _ref);
	return this.header(/*).generate() + "(" +
						 this.cParameters().stream().map(CDefinable::generate).collect(new Collectors.Joiner("*/, /*")) + ")" +
						 this.content + System.lineSeparator(*/);
}
JIncompleteClassSegment toJIncompleteClassSegment_JIncompleteMethod(void* _ref){
	JIncompleteMethod _this = *((JIncompleteMethod*) _ref);
	JIncompleteClassSegmentData data;
	data.err = this;
	return JIncompleteClassSegment { JIncompleteMethodType, data };
}
JClassSegment complete_JIncompleteMethod(void* _ref) {
	JIncompleteMethod _this = *((JIncompleteMethod*) _ref);
	/*Failed to resolve caller: JPlaceholder[input=Property not present: completeWithParameters]*/ function = this.completeWithParameters();
	functions = functions.addLast(function);
	return /*new JClassSegmentWrapper*/(/*""*/);
}
Optional<JDefinition> createDefinition_JIncompleteMethod(void* _ref) {
	JIncompleteMethod _this = *((JIncompleteMethod*) _ref);
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JGenericType[base=List, typeArguments=List[nativeList=[JIdentifier[value=JDefinition]]]]]*/ parameterTypes = this.parameters.stream(/*).map(definition -> definition.type).toList(*/);/*if (this.header instanceof JDefinition definition) {
				return Optional.of(definition.mapType(type -> new JMethodType(type, parameterTypes)));
			}*//*else {
				return Optional.empty();
			}*/
}
CFunction completeWithParameters_JIncompleteMethod(void* _ref) {
	JIncompleteMethod _this = *((JIncompleteMethod*) _ref);
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JGenericType[base=List, typeArguments=List[nativeList=[JIdentifier[value=JDefinition]]]]]*/ cParameters = this.parameters.stream(/*).map(JDefinition::toCDefinition).toList(*/);
	/*CDefinable outputDefinition*/;/*if (this.header instanceof JDefinition jDefinition) {
				cParameters = cParameters.addFirst(new CDefinition(new CPointerType(CPrimitiveType.Void), "_ref"));
				outputDefinition =
						new CDefinition(jDefinition.type.toCType(), jDefinition.name + "_" + scope.getCurrentStructName());
			}*//*else {
				outputDefinition = this.header.toCDefinition();
			}*/
	/*Failed to resolve caller: String*/ withBraces = this.content();/*if (!withBraces.startsWith("{") || !withBraces.endsWith("}*//*")) {
				return new CFunction(outputDefinition, cParameters, ";");
			}*/
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JPlaceholder[input=Failed to resolve caller: String]]*/ content1 = withBraces.substring(/*1*/, /*withBraces.length() - 1*/);
	scope = scope.enter(/*).defineAll(this.parameters()).enter(*/);
	/*Failed to resolve caller: JPlaceholder[input=Unresolved identifier: compileStatements]*/ compiledContent = compileStatements(content1, /*Main::compileMethodSegment*/);
	scope = scope.exit(/*).exit(*/);
	/*final String outputContent*/;/*if (this.header instanceof JConstructor(var name)) {
				outputContent = generateStatement(name + " this") + compiledContent + generateStatement("return this");
			}*//*else if (this.header instanceof JDefinition) {
				outputContent = generateDereferenceThis(scope.getCurrentStructName()) + compiledContent;
			}*//*else {
				outputContent = compiledContent;
			}*//*final var contentWithBraces = " {" + outputContent + System.lineSeparator() + "}*/
	/*"*/;
	return /*new CFunction*/(outputDefinition, cParameters, contentWithBraces);
}
/*0;

		public*/ LengthHead_Main(void* _ref, int length) {
	Main _this = *((Main*) _ref);
	this.length = length;/*}

		@Override
		public Optional<Integer> next() {
			if (this.counter < this.length) {
				final var value = this.counter;
				this.counter++;
				return Optional.of(value);
			}*//*else {
				return Optional.empty();
			}*//*}*/
}
/*delimiter;

			public*/ Joiner_Main(void* _ref, char* delimiter) {
	Main _this = *((Main*) _ref);
	this.delimiter = delimiter;
	/*}

			@Override
			public String createInitial() {
				return ""*/;/*}

			@Override
			public String fold(String current, String element) {
				if (current.isEmpty()) {
					return element;
				}*/
	return /*current + this.delimiter + element*/;/*}
		}*/
}
/*definedMembers;

		private*/ Frame_Main(void* _ref, Optional<char*> maybeStructureName, List<JDefinition> definedMembers, /*JType>*/ definedTypes) {
	Main _this = *((Main*) _ref);
	this.maybeStructureName = maybeStructureName;
	this.definedMembers = definedMembers;
	this.definedTypes = definedTypes;
	/*}

		public Frame() {
			this(Optional.empty(), new List<JDefinition>(), new HashMap<String, JType>())*/;
	/*}

		private Optional<JClassType> toClassType() {
			return this.maybeStructureName.map(structureName -> new JClassType(structureName, this.definedMembers))*/;
	/*}

		public void defineAllExpressions(List<JDefinition> definitions) {
			definitions.forEach(this::defineExpression)*/;
	/*}

		public void defineExpression(JDefinition definition) {
			assert !this.isVar(definition)*/;
	this.definedMembers = this.definedMembers.addLast(definition);
	/*Not a structure type: JPlaceholder[input=Unresolved identifier: definition]*/ type = definition.type;
	return /*type instanceof JIdentifier*/(/*var value) && value.equals("var"*/);
	/*}

		public Frame withStructureName(String structureName) {
			return new Frame(Optional.of(structureName), this.definedMembers, this.definedTypes)*/;
	/*}

		public Optional<JType> resolveType(String key) {
			return Optional.ofNullable(this.definedTypes.get(key))*/;
	/*}

		public Frame defineType(String key, JType type) {
			this.definedTypes.put(key, type)*/;
	return this;/*}*/
}
/*frames;

		private*/ Scope_Main(void* _ref, List<Frame> frames) {
	Main _this = *((Main*) _ref);
	this.frames = frames;
	/*}

		public Scope() {
			this(new List<Frame>().addLast(new Frame()))*/;/*}

		private Optional<JType> resolveExpression(String input) {
			if (input.equals("this")) {
				return this.getThisType().map(type -> type);
			}*/
	return this.frames.reversed(/*)
					.stream()
					.map(frame -> frame.definedMembers.stream().filter(definition -> definition.name.equals(input)).findFirst())
					.flatMap(Stream::fromOptional)
					.map(JDefinition::type)
					.findFirst()
					.map(this::finalizeType*/);
	/*}

		private Optional<JClassType> getThisType() {
			return this.frames
					.reversed()
					.stream()
					.map(Frame::toClassType)
					.flatMap(Stream::fromOptional)
					.findFirst()
					.map(value -> value)*/;/*}

		private JType finalizeType(JType type) {
			if (type instanceof JGenericType(var base, var typeArguments)) {
				final var maybeResolved = scope.resolveType(base);
				if (maybeResolved.isPresent()) {
					final var jType = maybeResolved.get();
					final var typeParameters = jType.findTypeParameters();
					final var mapping = this.createMapping(typeArguments, typeParameters);
					return jType.remap(mapping);
				} else {
					return new JPlaceholder("Unknown base generic type: " + base);
				}
			}*//*else {
				return type;
			}*/
	/*Failed to resolve caller: JPlaceholder[input=JPlaceholder[input=new HashMap<String, JType>]]*/ mapping = /*new HashMap<String, JType>*/();
	/*(var*/ i = /*0*/;
	/*i < typeParameters.size()*/;/*i++) {
				final var typeParameter = typeParameters.get(i);
				final var typeArgument = typeArguments.get(i);
				mapping.put(typeParameter, typeArgument);
			}*/
	return mapping;
	/*}

		private Optional<JType> resolveType(String key) {
			return this.frames
					.reversed()
					.stream()
					.map(frame -> frame.resolveType(key))
					.flatMap(Stream::fromOptional)
					.findFirst()*/;
	/*}

		public Scope enter() {
			this*/.frames = this.frames.addLast(/*new Frame*/());
	return this;
	/*}

		public Scope defineAll(List<JDefinition> definitions) {
			this.frames.getLast().defineAllExpressions(definitions)*/;
	return this;
	/*}

		public Scope exit() {
			this*/.frames = this.frames.removeLast();
	return this;
	/*}

		public Scope define(JDefinition definition) {
			this.frames.getLast().defineExpression(definition)*/;
	return this;
	/*}

		public Scope withStructureName(String name) {
			this*/.frames = this.frames.set(/*this.frames.size() - 1*/, this.frames.getLast(/*).withStructureName(name*/));
	return this;
	/*}

		public String getCurrentStructName() {
			return this.frames
					.reversed()
					.stream()
					.map(frame -> frame.maybeStructureName)
					.flatMap(Stream::fromOptional)
					.findFirst()
					.orElse("?")*/;
	/*}

		public Scope defineType(String name, JType type) {
			this*/.frames = this.frames.mapLast(/*last -> last.defineType(name*/, /*type)*/);
	return this;/*}*/
}
List<T> createInitial_Main(void* _ref) {
	Main _this = *((Main*) _ref);
	return /*new List<T>*/();
	/*}

		@Override
		public List<T> fold(List<T> current, T element) {
			return current.addLast(element)*/;/*}*/
}
/*false;

		public*/ SingleHead_Main(void* _ref, T element) {
	Main _this = *((Main*) _ref);
	this.element = element;/*}

		@Override
		public Optional<T> next() {
			if (this.retrieved) {
				return Optional.empty();
			}*/
	this.retrieved = true;
	return Optional.of(this.element);/*}*/
}
Optional<T> next_Main(void* _ref) {
	Main _this = *((Main*) _ref);
	return Optional.empty();/*}*/
}
/*current;

		public*/ FlatMapHead_Main(void* _ref, Head<T> head, Stream</*R>*/> mapper) {
	Main _this = *((Main*) _ref);
	this.head = head;
	this.mapper = mapper;
	this.current = head.next(/*)*/.map(/*mapper)*/.orElseGet(/*() -> new Stream<R>(new EmptyHead<R>(*/)));/*}

		@Override
		public Optional<R> next() {
			while (true) {
				final var maybeNext = this.current.next();
				if (maybeNext.isPresent()) {
					return maybeNext;
				}

				final var nextHead = this.head.next();
				if (nextHead.isEmpty()) {
					return Optional.empty();
				}

				this.current = this.mapper.apply(nextHead.get());
			}*//*}*/
}
public static final List<String> enumNames = new List<String> new_public static final List<String> enumNames = new List<String>();
private static List<String> structures = new List<String> new_private static List<String> structures = new List<String>();
private static List<CFunction> functions = new List<CFunction> new_private static List<CFunction> functions = new List<CFunction>();
private static List<String> globals = new List<String> new_private static List<String> globals = new List<String>();
new Scope_Main(void* _ref);
void main_Main(void* _ref, char** args) {
	Main _this = *((Main*) _ref);
	/*run().ifPresent(Throwable::printStackTrace)*/;
}
Optional<IOException> run_Main(void* _ref) {
	Main _this = *((Main*) _ref);
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JPlaceholder[input=Unresolved identifier: Paths]]*/ source = Paths.get(/*"."*/, /*"src"*/, /*"main"*/, /*"java"*/, /*"magma"*/, /*"Main.java"*/);/*return switch (readString(source)) {
			case Ok(var input) -> {
				final var target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");
				final var output = compile(input);
				yield writeString(target, output);
			}
			case Err<String, IOException> v -> Optional.of(v.error);
		}*/
	/**/;
}
Optional<IOException> writeString_Main(void* _ref, Path target, char* output) {
	Main _this = *((Main*) _ref);/*try {
			Files.writeString(target, output);
			return Optional.empty();
		}*//*catch (IOException e) {
			return Optional.of(e);
		}*/
}
/*IOException>*/ readString_Main(void* _ref, Path source) {
	Main _this = *((Main*) _ref);/*try {
			return new Ok<String, IOException>(Files.readString(source));
		}*//*catch (IOException e) {
			return new Err<String, IOException>(e);
		}*/
}
char* compile_Main(void* _ref, char* input) {
	Main _this = *((Main*) _ref);
	scope = scope.enter();
	/*Failed to resolve caller: JPlaceholder[input=Unresolved identifier: compileStatements]*/ compiled = compileStatements(input, /*Main::compileRootSegment*/);
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JPlaceholder[input=Unresolved identifier: globals]]*/ joinedGlobals = globals.stream(/*)*/.collect(/*new Collectors.Joiner(""*/));
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JPlaceholder[input=Unresolved identifier: structures]]*/ joinedStructures = structures.stream(/*)*/.collect(/*new Collectors.Joiner(""*/));
	/*Failed to resolve caller: JPlaceholder[input=Not a structure type: JPlaceholder[input=Unresolved identifier: functions]]*/ joinedFunctions = functions.stream(/*)*/.map(/*CFunction::generate).collect(new Collectors.Joiner(""*/));
	return /*joinedGlobals + joinedStructures + joinedFunctions + compiled*/;
}
char* compileStatements_Main(void* _ref, char* input, /*String>*/ mapper) {
	Main _this = *((Main*) _ref);
	return divide(/*input)*/.map(/*mapper).collect(new Collectors.Joiner(""*/));
}
Stream<char*> divide_Main(void* _ref, char* input) {
	Main _this = *((Main*) _ref);
	/*Failed to resolve caller: JPlaceholder[input=JPlaceholder[input=new List<String>]]*/ segments = /*new List<String>*/();
	/*Failed to resolve caller: JPlaceholder[input=JPlaceholder[input=new StringBuilder]]*/ buffer = /*new StringBuilder*/();
	/*JPlaceholder[input=0]*/ depth = /*0*/;
	/*(var*/ i = /*0*/;
	/*i < input.length()*/;/*i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments = segments.addLast(buffer.toString());
				buffer = new StringBuilder();
			} else if (c == '}*//*' && depth == 1) {
				segments = segments.addLast(buffer.toString());
				buffer = new StringBuilder();
				depth--;
			}*//*else if (c == '{') {
				depth++;
			} else if (c == '}*//*') {
				depth--;
			}*/
}
segments = segments.addLast new_segments = segments.addLast();
return segments.stream new_return segments.stream();
/*private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return compileStructure(stripped, "class")
				.map(JClassSegmentWrapper::complete)
				.map(JClassSegment::toCStructureSegment)
				.map(CStructureSegment::generate)
				.orElseGet(() -> CPlaceholder.wrap(stripped));
	}*//*private static Optional<JClassSegmentWrapper> compileStructure(String stripped, String type) {
		final var i = stripped.indexOf(type + " ");
		if (i >= 0) {
			final var substring = stripped.substring(i + (type + " ").length()).strip();
			if (substring.endsWith("}")) {
				final var substring1 = substring.substring(0, substring.length() - 1);
				final var i1 = substring1.indexOf("{");
				if (i1 >= 0) {
					var beforeContent = substring1.substring(0, i1).strip();
					final var content = substring1.substring(i1 + 1).strip();

					final var i2 = beforeContent.indexOf("permits");
					var variants = new List<String>();
					if (i2 >= 0) {
						final var stripped1 = beforeContent.substring(i2 + "permits".length()).strip().split(Pattern.quote(","));

						variants = Stream.fromArray(stripped1).map(String::strip).filter(segment -> !segment.isEmpty()).toList();

						beforeContent = beforeContent.substring(0, i2).strip();
					}

					Optional<JType> maybeImplements = Optional.empty();
					final var i4 = beforeContent.indexOf("implements ");
					if (i4 >= 0) {
						final var substring2 = beforeContent.substring(i4 + "implements ".length());
						maybeImplements = Optional.of(parseType(substring2.strip()));

						beforeContent = beforeContent.substring(0, i4).strip();
					}

					var recordFields = new List<JDefinition>();
					if (beforeContent.endsWith(")")) {
						final var substring2 = beforeContent.substring(0, beforeContent.length() - 1);
						final var i3 = substring2.indexOf("(");
						if (i3 >= 0) {
							final var substring4 = substring2.substring(i3 + 1);
							recordFields = Stream
									.fromArray(substring4.split(Pattern.quote(",")))
									.map(String::strip)
									.filter(slice -> !slice.isEmpty())
									.map(Main::parseDefinition)
									.flatMap(Stream::fromOptional)
									.toList();

							beforeContent = substring2.substring(0, i3);
						}
					}

					var typeParameters = new List<String>();
					if (beforeContent.endsWith(">")) {
						final var substring2 = beforeContent.substring(0, beforeContent.length() - 1);
						final var i3 = substring2.indexOf("<");
						if (i3 >= 0) {
							final var substring3 = substring2.substring(i3 + 1).strip().split(Pattern.quote(","));
							typeParameters =
									Stream.fromArray(substring3).map(String::strip).filter(slice -> !slice.isEmpty()).toList();

							beforeContent = beforeContent.substring(0, i3).strip();
						}
					}

					var templateString = "";
					if (!typeParameters.isEmpty()) {
						final var joined =
								typeParameters.stream().map(slice -> "typename " + slice).collect(new Collectors.Joiner(", "));

						templateString = "template <" + joined + ">" + System.lineSeparator();
					}

					final String typeArguments;
					if (typeParameters.isEmpty()) {
						typeArguments = "";
					} else {
						typeArguments = "<" + typeParameters.stream().collect(new Collectors.Joiner(", ")) + ">";
					}

					var beforeStruct = "";
					if (maybeImplements.isPresent()) {
						final var superType = maybeImplements.get().toCType();
						final var superTypeString = superType.generate();

						final var thisType = beforeContent + typeArguments;
						final var s = generateStatement(superTypeString + "Data data");
						final var s1 = generateStatement("data.err = this");
						final var s2 = generateStatement("return " + superTypeString + " { " + beforeContent + "Type, data " +
																						 "}");
						final var content1 = s + s1 + s2;
						final var s3 = "to" + superTypeString + "_" + beforeContent;
						final var outputContent = "{" + generateDereferenceThis(thisType) + content1 + System.lineSeparator() +
																			"}";

						final var refDef = new CDefinition(new CPointerType(CPrimitiveType.Void), "_ref");
						functions =
								functions.addLast(new CFunction(new CDefinition(superType, s3), List.of(refDef), outputContent));
					}

					var generatedFields = new List<CDefinition>();
					if (!variants.isEmpty()) {
						final var enumFields = variants
								.stream()
								.map(segment -> System.lineSeparator() + "\t" + segment + "Type")
								.collect(new Collectors.Joiner(","));

						final var tagType = beforeContent + "Tag";
						final var generatedEnum =
								"enum " + tagType + " {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();

						final var unionFields = variants
								.stream()
								.map(segment -> System.lineSeparator() + "\t" + segment + typeArguments + " " + segment.toLowerCase() +
																";")
								.collect(new Collectors.Joiner(""));

						final var unionType = beforeContent + "Data";
						final var generatedUnion =
								templateString + "union " + unionType + " {" + unionFields + System.lineSeparator() + "}" +
								System.lineSeparator();

						beforeStruct += generatedEnum + generatedUnion;

						generatedFields = List.of(new CDefinition(new CIdentifier(tagType), "_tag"),
																			new CDefinition(new CIdentifier(unionType + typeArguments), "_data"));
					}

					scope = scope.enter().withStructureName(beforeContent).defineAll(recordFields);

					final var recordFieldsStream = recordFields.stream().map(JDefinition::toCDefinition);
					final var structureFields = recordFieldsStream
							.concat(generatedFields.stream().map(item -> item))
							.map(CDefinable::generate)
							.map(Main::generateStatement)
							.collect(new Collectors.Joiner(""));

					final var inputSegments = divide(content).map(Main::parseClassSegment).toList();
					final var incompleteSegments = inputSegments.stream().toList();

					final var methodDefinitions = inputSegments
							.stream()
							.map(JIncompleteClassSegment::createDefinition)
							.flatMap(Stream::fromOptional)
							.toList();

					final var outputContent = incompleteSegments
							.stream()
							.map(JIncompleteClassSegment::complete)
							.map(JClassSegment::toCStructureSegment)
							.map(CStructureSegment::generate)
							.collect(new Collectors.Joiner(""));

					final var generated =
							beforeStruct + templateString + "struct " + beforeContent + " {" + structureFields + outputContent +
							System.lineSeparator() + "};" + System.lineSeparator();

					structures = structures.addLast(generated);

					final var thisType = scope
							.getThisType()
							.<JType>map(classType -> classType.attachMembers(methodDefinitions))
							.orElse(JPrimitiveType.Void);

					scope = scope.exit().defineType(beforeContent, thisType);
					return Optional.of(new JClassSegmentWrapper(""));
				}
			}
		}

		return Optional.empty();
	}*//*private static String generateDereferenceThis(String thisType) {
		return generateStatement(thisType + " _this = *((" + thisType + "*) _ref)");
	}*//*private static String generateStatement(String content) {
		return System.lineSeparator() + "\t" + content + ";";
	}*//*private static boolean isIdentifier(String input) {
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (Character.isLetter(c) || (i != 0 && Character.isDigit(c))) {continue;}
			return false;
		}

		return true;
	}*//*private static JIncompleteClassSegment parseClassSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return new JClassSegmentWrapper("");
		}

		final var maybeInterface = compileStructure(stripped, "interface");
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final var maybeRecord = compileStructure(stripped, "record");
		if (maybeRecord.isPresent()) {
			return maybeRecord.get();
		}

		final var maybeEnum = compileStructure(stripped, "enum");
		if (maybeEnum.isPresent()) {
			return maybeEnum.get();
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var maybeClassStatement = compileClassStatement(substring);
			if (maybeClassStatement.isPresent()) {
				return maybeClassStatement.get();
			}
		}

		return compileMethod(stripped).orElseGet(() -> new JPlaceholder(stripped));
	}*//*private static Optional<JIncompleteClassSegment> compileMethod(String stripped) {
		final var i = stripped.indexOf("(");
		if (i >= 0) {
			final var substring = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var paramString = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();
				final var header = parseDefinition(substring)
						.<JMethodHeader>map(definition -> definition)
						.or(() -> parseConstructor(substring))
						.orElseGet(() -> new JPlaceholder(substring));

				final var jParameters = Stream
						.fromArray(paramString.split(Pattern.quote(",")))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(Main::parseDefinition)
						.flatMap(Stream::fromOptional)
						.toList();

				return Optional.of(new JIncompleteMethod(header, jParameters, withBraces));
			}
		}

		return Optional.empty();
	}*//*private static Optional<JMethodHeader> parseConstructor(String input) {
		final var stripped = input.strip();
		return Optional.of(new JConstructor(stripped));
	}*//*private static Optional<JClassSegmentWrapper> compileClassStatement(String input) {
		return compileDefinition(input).map(Main::generateStatement).map(JClassSegmentWrapper::new).or(() -> {
			final var enumValues = Stream
					.fromArray(input.split(Pattern.quote(",")))
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.toList();

			final var name = scope.getCurrentStructName();
			if (!enumValues.stream().collect(new AllMatch<String>(segment -> compileEnumValue(segment, name)))) {
				return Optional.empty();
			}
			return Optional.of(new JClassSegmentWrapper(""));
		});
	}*//*private static boolean compileEnumValue(String segment, String enumName) {
		final var stripped = segment.strip();
		if (stripped.endsWith(")")) {
			final var substring = stripped.substring(0, stripped.length() - 1);

			final var i = substring.indexOf("(");
			if (i >= 0) {
				final var memberName = substring.substring(0, i);
				final var substring2 = substring.substring(i + 1);

				if (isIdentifier(memberName)) {
					globals = globals.addLast(enumName + " " + enumName + "_" + memberName + " = new_" + enumName + "(" +
																		compileExpression(substring2) + ");" + System.lineSeparator());
					return true;
				}
			}
		}

		return false;
	}*//*private static String compileExpression(String input) {
		return parseExpression(input).toCExpression().generate();
	}*//*private static JExpression parseExpression(String input) {
		final var stripped = input.strip();
		if (isIdentifier(stripped)) {
			return new JIdentifier(stripped);
		}

		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var substring = stripped.substring(0, i).strip();
			final var name = stripped.substring(i + 1).strip();
			if (isIdentifier(name)) {
				final var child = parseExpression(substring);
				return new JMemberAccess(child, name);
			}
		}

		if (stripped.endsWith(")")) {
			final var slice = stripped.substring(0, stripped.length() - 1);
			final var i1 = slice.indexOf("(");
			if (i1 >= 0) {
				final var substring = slice.substring(0, i1);
				final var arguments = Stream
						.fromArray(slice.substring(i1 + 1).split(Pattern.quote(",")))
						.map(String::strip)
						.filter(segment -> !segment.isEmpty())
						.map(Main::parseExpression)
						.toList();

				return new JInvocation(parseExpression(substring), arguments);
			}
		}

		return new JPlaceholder(stripped);
	}*//*private static boolean isDefined(String input) {
		if (input.equals("this")) {
			return true;
		}

		return scope.resolveExpression(input).isPresent();
	}*//*private static String compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			return generateStatement(compileMethodSegmentValue(substring));
		}

		return CPlaceholder.wrap(stripped);
	}*//*private static String compileMethodSegmentValue(String input) {
		if (input.startsWith("return ")) {
			final var slice = input.substring("return ".length());
			return "return " + compileExpression(slice);
		}

		final var i = input.indexOf("=");
		if (i >= 0) {
			final var destination = input.substring(0, i);
			final var substring1 = input.substring(i + 1);
			final var source = parseExpression(substring1);
			final var sourceString = source.toCExpression().generate();

			return parseDefinition(destination).map(definition -> {
				final var jDefinition = withResolvedType(definition, source);
				scope = scope.define(jDefinition);
				return jDefinition.toCDefinition().generate() + " = " + sourceString;
			}).orElseGet(() -> compileExpression(destination) + " = " + sourceString);
		}

		return CPlaceholder.wrap(input);
	}*//*private static JDefinition withResolvedType(JDefinition definition, JExpression source) {
		return definition.mapType(type -> {
			if (type instanceof JIdentifier(var value) && value.equals("var")) {
				return resolveExpression(source);
			}

			return type;
		});
	}*//*private static JType resolveExpression(JExpression expression) {
		if (expression instanceof JIdentifier(var input)) {
			return scope.resolveExpression(input).orElseGet(() -> new JPlaceholder("Unresolved identifier: " + input));
		}

		if (expression instanceof JMemberAccess(var child, var name)) {
			final var resolved = resolveExpression(child);
			if (resolved instanceof JClassType type0) {
				return type0.resolve(name).orElseGet(() -> new JPlaceholder("Property not present: " + name));
			}
			return new JPlaceholder("Not a structure type: " + resolved);
		}

		if (expression instanceof JInvocation invocation) {
			final var caller = invocation.caller;
			final var callerType = resolveExpression(caller);
			if (callerType instanceof JMethodType methodType) {
				return methodType.returnType;
			} else {
				return new JPlaceholder("Failed to resolve caller: " + callerType);
			}
		}

		return new JPlaceholder(expression.toString());
	}*//*private static Optional<String> compileDefinition(String input) {
		return parseDefinition(input).map(JDefinition::toCDefinition).map(CDefinable::generate);
	}*//*private static Optional<JDefinition> parseDefinition(String input) {
		final var stripped = input.strip();
		final var i = stripped.lastIndexOf(" ");
		if (i < 0) {return Optional.empty();}
		final var beforeName = stripped.substring(0, i).strip();
		final var name = stripped.substring(i + 1).strip();

		if (!isIdentifier(name)) {
			return Optional.empty();
		}

		final var i1 = beforeName.lastIndexOf(" ");
		if (i1 >= 0) {
			final var beforeType = beforeName.substring(0, i1);
			final var type = beforeName.substring(i1 + 1);
			return Optional.of(new JDefinition(Optional.of(beforeType), parseType(type), name));
		} else {
			return Optional.of(new JDefinition(Optional.empty(), parseType(beforeName), name));
		}
	}*//*private static String compileTypeToString(String input) {
		return parseType(input).toCType().generate();
	}*//*private static JType parseType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return JPrimitiveType.Void;
		}

		if (stripped.endsWith("[]")) {
			final var cType = parseType(stripped.substring(0, stripped.length() - 2));
			return new JArrayType(cType);
		}

		if (stripped.equals("String")) {
			return JPrimitiveType.String;
		}

		if (stripped.endsWith(">")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i = substring.indexOf("<");
			if (i >= 0) {
				final var base = substring.substring(0, i);
				final var typeArgumentsArray = substring.substring(i + 1).split(Pattern.quote(","));

				final var typeArguments = Stream
						.fromArray(typeArgumentsArray)
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(Main::parseType)
						.toList();

				return new JGenericType(base, typeArguments);
			}
		}

		if (isIdentifier(stripped)) {
			return new JIdentifier(stripped);
		}

		return new JPlaceholder(stripped);
	}*//*}*/