// File generated from 'JavaPath[path=.\src\main\java\magma\Lib.java]'. This is not source code!
#include "Main.h"
enum ResultTag {
	Err,
	Ok
};
template <typeparam T, typeparam X>
union ResultData {
	Err<T, X> err;
	Ok<T, X> ok;
};
template <typeparam T, typeparam X>
struct Result {
	ResultTag tag;
	ResultData<T, X> data;
};
enum OptionTag {
	Some,
	None
};
template <typeparam T>
union OptionData {
	Some<T> some;
	None<T> none;
};
template <typeparam T>
struct Option {
	OptionTag tag;
	OptionData<T> data;
};
struct IOError {
};
struct Path {
};
template <typeparam T, typeparam C>
struct Collector {
};
struct Actual {
};
template <typeparam T>
struct Head {
};
template <typeparam T, typeparam X>
struct Ok {
	T value;
};
template <typeparam T, typeparam X>
struct Err {
	X error;
};
template <typeparam T>
struct Some {
	T value;
};
template <typeparam T>
struct None {
};
template <typeparam T>
struct Stream {
	Head<T> head;
};
template <typeparam T>
struct Array {
	T* ref;
	int capacity;
	int length;
};
struct MemUtils {
};
template <typeparam T>
struct ArrayList {
	Array<T> elements;
};
struct Streams {
};
template <typeparam T>
struct ListHead {
	ArrayList<T> self;
	int index;
};
template <typeparam T>
struct ListCollector {
};
struct Joiner {
	char* delimiter;
};
template <typeparam T>
struct ArrayHead {
	T* elements;
	int length;
	int counter;
};
template <typeparam T>
struct AnyMatch {
	Predicate<T> predicate;
};
template <typeparam T>
struct EmptyHead {
};
template <typeparam T>
struct SingletonHead {
	T value;
	boolean retrieved;
};
struct RangeStream {
	int length;
	int index;
};
template <typeparam T, typeparam R>
struct FlatMapHead {
	Head<T> sourceHead;
	Function<T, Stream<R>> mapper;
	Option<Stream<R>> currentStream;
};
struct Lib {
};
Option<R> map_Option(Function<T, R> mapper);
Option<T> or_Option(Supplier<Option<T>> other);
T orElseGet_Option(Supplier<T> other);
Option<R> flatMap_Option(Function<T, Option<R>> mapper);
T orElse_Option(T other);
char* display_IOError();
boolean exists_Path();
Result<char*, IOError> readString_Path();
Option<IOError> createDirectories_Path();
Option<IOError> writeString_Path(char* output);
Path getParent_Path();
Result<ArrayList<Path>, IOError> walk_Path();
char* asString_Path();
Path relativize_Path(Path path);
Path resolveByPath_Path(Path path);
Stream<char*> stream_Path();
Path getFileName_Path();
Path resolveByString_Path(char* name);
C createInitial_Collector();
C fold_Collector(C current, T element);
Option<T> next_Head();
Option<R> map_Some(Function<T, R> mapper){
	return new_Some<R>(mapper.apply(this.value));
}
Option<T> or_Some(Supplier<Option<T>> other){
	return this;
}
T orElseGet_Some(Supplier<T> other){
	return this.value;
}
Option<R> flatMap_Some(Function<T, Option<R>> mapper){
	return mapper.apply(this.value);
}
T orElse_Some(T other){
	return this.value;
}
Option<R> map_None(Function<T, R> mapper){
	return new_None<R>();
}
Option<T> or_None(Supplier<Option<T>> other){
	return other.get();
}
T orElseGet_None(Supplier<T> other){
	return other.get();
}
Option<R> flatMap_None(Function<T, Option<R>> mapper){
	return new_None<R>();
}
T orElse_None(T other){
	return other;
}
auto __lambda0__() {
	return this.head.next().map(mapper);
}
Stream<R> map_Stream(Function<T, R> mapper){
	return new_Stream<R>(__lambda0__);
}
auto __lambda1__() {
	while (true) {
		Option < T >= nextValue == sourceHead.next();
		if (nextValue.tag == Some) {
			if (predicate.test(value)) {
		Some<T> _cast = nextValue.data.some;
		T value = _cast.value;
				return new_Some<T>(value);
			}
			/*// Continue to next element*/
		}
		else {
			return new_None<T>();
		}
	}
}
Stream<T> filter_Stream(Predicate<T> predicate){
	Head<T> sourceHead = this.head;
	return new_Stream<T>(__lambda1__);
}
C collect_Stream(Collector<T, C> collector){
	return this.foldWithInitial(collector.createInitial(), fold_collector);
}
C foldWithInitial_Stream(C initial, BiFunction<C, T, C> folder){
	C accumulator = initial;
	Option < T >= current == this.head.next();
	while (current.tag == Some) {
		Some<T> _cast = current.data.some;
		T value = _cast.value;
		accumulator == folder.apply(accumulator, value);
		current == this.head.next();
	}
	return accumulator;
}
Stream<R> flatMap_Stream(Function<T, Stream<R>> mapper){
	return new_Stream<R>(new_FlatMapHead<T, R>(this.head, mapper));
}
auto __lambda2__(auto inner) {
	return folder.apply(inner, element);
}
auto __lambda3__(auto current, auto element) {
	if (current.tag == None) {
		None<T> _cast = current.data.none;
		return new_Some<T>(element);
	}
	return current.map(__lambda2__);
}
Option<T> fold_Stream(BiFunction<T, T, T> folder){
	return this. < Option < T >= foldWithInitial(new_None<T>(), __lambda3__);
}
Option<T> next_Stream(){
	return this.head.next();
}
private Array_Array(T* ref, int capacity){
	this.ref = ref;
	this.capacity = capacity;
	this.length = 0;
}
Array<T> alloc_Array(int length){
	return new_Array<T>(MemUtils.malloc(length), length);
}
auto __lambda4__(auto element) {
	return Objects.equals(element, test);
}
boolean contains_Array(T test){
	Stream < T >= tStream == this.stream();
	return tStream.collect(new_AnyMatch<T>(__lambda4__));
}
Stream<T> stream_Array(){
	return new_Stream<T>(new_ArrayHead<T>(this.ref, this.length));
}
void setNext_Array(T element){
	if (this.length < this.ref.length) {
		/*this.ref[this.length] */ = element;
		this.length++;
	}
}
Option<T> get_Array(int index){
	if (index < this.length) {
		return new_Some<T>(/*this.ref[index]*/);
	}
	else {
		return new_None<T>();
	}
}
void setFirst_Array(T element){
	/*this.ref[0] */ = element;
}
T* malloc_MemUtils(int length);
void memCopy_MemUtils(Array<T> src, int srcPos, Array<T> dest, int destPos, int length);
private ArrayList_ArrayList(Array<T> elements){
	this.elements = elements;
}
ArrayList new_ArrayList(){
	ArrayList this;
	this(Array.alloc(10));
	return this;
}
void ensureCapacity_ArrayList(int minCapacity){
	if (minCapacity <  == this.elements.capacity) {
		/*return*/;
	}
	int newCapacity = /* this.elements.capacity * 2*/;
	if (newCapacity < minCapacity) {
		newCapacity = minCapacity;
	}
	Array < T >= newElements == Array.alloc(newCapacity);
	MemUtils.memCopy(this.elements, 0, newElements, 0, (this.elements).length);
	newElements.length = (this.elements).length;
	this.elements = newElements;
}
ArrayList<T> add_ArrayList(T element){
	this.ensureCapacity((this.elements).length + 1);
	this.elements.setNext(element);
	return this;
}
ArrayList<T> clear_ArrayList(){
	this.elements == Array.alloc(10);
	return this;
}
int size_ArrayList(){
	return (this.elements).length;
}
Stream<T> stream_ArrayList(){
	return new_Stream<T>(new_ListHead<T>(this));
}
Option<T> get_ArrayList(int index){
	if (index < 0 || index >= (this.elements).length) {
		return new_None<T>();
	}
	return this.elements.get(index);
}
boolean isEmpty_ArrayList(){
	return this.size() == 0;
}
ArrayList<T> addFirst_ArrayList(T element){
	this.ensureCapacity((this.elements).length + 1);
	/*// Shift all elements one position to the right
			MemUtils.memCopy(this.elements, 0, this.elements, 1, (this.elements).length)*/;
	this.elements.setFirst(element);
	this.elements.length++;
	return this;
}
ArrayList<T> addLast_ArrayList(T element){
	return this.add(element);
}
boolean contains_ArrayList(T element){
	return this.elements.contains(element);
}
Option<T> getFirst_ArrayList(){
	return this.get(0);
}
Option<ArrayList<T>> subList_ArrayList(int start, int end){
	if (start < 0 || end >= (this.elements).length || start >= end) {
		return new_None<ArrayList<T>>();
	}
	int subSize = end - start;
	Array < T >= newElements == Array.alloc(Math.max(10, subSize));
	MemUtils.memCopy(this.elements, start, newElements, 0, subSize);
	newElements.length = subSize;
	return new_Some<ArrayList<T>>(new_ArrayList<T>(newElements));
}
ArrayList<T> addAll_ArrayList(ArrayList<T> elements){
	int elementsSize = elements.size();
	this.ensureCapacity((this.elements).length + elementsSize);
	/*for (int i = 0; i < elementsSize; i++) {
				this.elements.setNext(elements.get(i).orElse(null));
			}*/
	return this;
}
Option<ArrayList<T>> addAllAt_ArrayList(int index, ArrayList<T> elements){
	if (index < 0 || index >= (this.elements).length) {
		return new_None<ArrayList<T>>();
	}
	int elementsSize = elements.size();
	this.ensureCapacity((this.elements).length + elementsSize);
	/*// Shift elements to the right to make room
			MemUtils.memCopy(this.elements, index, this.elements, index + elementsSize, (this.elements).length - index)*/;
	/*// Copy inserted elements - need to use set with supplier since we're inserting in middle
			for (int i = 0; i < elementsSize; i++) {
				this.elements.ref[index + i] = elements.get(i).orElse(null);
			}*/
	this.elements.length +  = elementsSize;
	return new_Some<ArrayList<T>>(this);
}
Option<T> getLast_ArrayList(){
	return this.get((this.elements).length - 1);
}
ArrayList<T> copy_ArrayList(){
	ArrayList<T> list = new_ArrayList<T>();
	if ((this.elements).length >= 0) {
		MemUtils.memCopy(this.elements, 0, list.elements, 0, (this.elements).length);
		list.elements.length = (this.elements).length;
	}
	return list;
}
Stream<T> fromRef_Streams(T* elements){
	return new_Stream<T>(new_ArrayHead<T>(elements, elements.length));
}
Stream<T> fromOption_Streams(Option<T> option){
	return new_Stream<T>(/*switch (option) {
				case None<T> _ -> new EmptyHead<T>();
				case Some<T> v -> new SingletonHead<T>(v.value());
			}*/);
}
Stream<Integer> fromLength_Streams(int length){
	return new_Stream<Integer>(new_RangeStream(length));
}
ListHead new_ListHead(ArrayList<T> self){
	ListHead this;
	this.self = self;
	this.index = 0;
	return this;
}
Option<T> next_ListHead(){
	if (this.index < this.self.size()) {
		return this.self.get(this.index + );
	}
	return new_None<T>();
}
ListCollector new_ListCollector(){
	ListCollector this;
	return this;
}
ArrayList<T> createInitial_ListCollector(){
	return new_ArrayList<T>();
}
ArrayList<T> fold_ListCollector(ArrayList<T> current, T element){
	return current.add(element);
}
Joiner new_Joiner(char* delimiter){
	Joiner this;
	this.delimiter = delimiter;
	return this;
}
Joiner new_Joiner(){
	Joiner this;
	this("");
	return this;
}
char* createInitial_Joiner(){
	return "";
}
char* fold_Joiner(char* current, char* element){
	if (current.isEmpty()) {
		return element;
	}
	return current + this.delimiter + element;
}
ArrayHead new_ArrayHead(T* elements, int length){
	ArrayHead this;
	this.elements = elements;
	this.counter = 0;
	this.length = length;
	return this;
}
Option<T> next_ArrayHead(){
	if (this.counter < this.length) {
		T element = /* this.elements[this.counter]*/;
		this.counter++;
		return new_Some<T>(element);
	}
	else {
		return new_None<T>();
	}
}
Boolean createInitial_AnyMatch(){
	return false;
}
Boolean fold_AnyMatch(Boolean current, T element){
	return current || this.predicate.test(element);
}
Option<T> next_EmptyHead(){
	return new_None<T>();
}
SingletonHead new_SingletonHead(T value){
	SingletonHead this;
	this.value = value;
	this.retrieved = false;
	return this;
}
Option<T> next_SingletonHead(){
	if (this.retrieved) {
		return new_None<T>();
	}
	this.retrieved = true;
	return new_Some<T>(this.value);
}
RangeStream new_RangeStream(int length){
	RangeStream this;
	this.length = length;
	this.index = 0;
	return this;
}
Option<Integer> next_RangeStream(){
	if (this.index < this.length) {
		int preserve = this.index;
		this.index++;
		return new_Some<Integer>(preserve);
	}
	else {
		return new_None<Integer>();
	}
}
FlatMapHead new_FlatMapHead(Head<T> sourceHead, Function<T, Stream<R>> mapper){
	FlatMapHead this;
	this.sourceHead = sourceHead;
	this.mapper = mapper;
	this.currentStream = new_None<Stream<R>>();
	return this;
}
Option<R> next_FlatMapHead(){
	while (true) {
		/*// Try to get next element from current inner stream
				if (this.currentStream instanceof Some<Stream<R>>(Stream<R> stream)) {
					Option<R> nextValue = stream.next();
					if (nextValue instanceof Some<R> _) {
						return nextValue;
					}
					// Current stream is exhausted, move to next
					this.currentStream = new None<Stream<R>>();
				}*/
		stream
				Option<T> nextSource = this.sourceHead.next();
		if (nextSource.tag == Some) {
		Some<T> _cast = nextSource.data.some;
		T value = _cast.value;
			this.currentStream = new_Some<Stream<R>>(this.mapper.apply(value));
		}
		else {
			/*// No more source elements
					return new None<R>()*/;
		}
	}
}
int main(){
	main_Main();
	return 0;
}