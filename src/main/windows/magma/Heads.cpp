// File generated from '.\src\main\java\magma\Heads.java'. This is not source code!
#include "Main.h"
ListHead new_ListHead(ArrayList<T> self){
	ListHead this;
	this.self = self;
	this.index = 0;
	return this;
}
Option<T> get_ListHead(){
	if (this.index < this.self.size()) {
		return this.self.get(this.index + );
	}
	return new_None<T>();
}
ArrayHead new_ArrayHead(T* elements, int length){
	ArrayHead this;
	this.elements = elements;
	this.counter = 0;
	this.length = length;
	return this;
}
Option<T> get_ArrayHead(){
	if (this.counter < this.length) {
		T element = /* this.elements[this.counter]*/;
		this.counter++;
		return new_Some<T>(element);
	}
	else {
		return new_None<T>();
	}
}
SingletonHead new_SingletonHead(T value){
	SingletonHead this;
	this.value = value;
	this.retrieved = false;
	return this;
}
Option<T> get_SingletonHead(){
	if (this.retrieved) {
		return new_None<T>();
	}
	this.retrieved = true;
	return new_Some<T>(this.value);
}
FlatMapHead new_FlatMapHead(Supplier<Option<T>> sourceHead, Stream<R> (*)(T) mapper){
	FlatMapHead this;
	this.sourceHead = sourceHead;
	this.mapper = mapper;
	this.currentStream = new_None<Stream<R>>();
	return this;
}
Option<R> get_FlatMapHead(){
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
				Option<T> nextSource = this.sourceHead.get();
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
private RangeHead_RangeHead(int length){
	this.length = length;
	this.index = 0;
}
RangeHead createRangeStream_RangeHead(int length){
	return new_RangeHead(length);
}
Option<Integer> get_RangeHead(){
	if (this.index < this.length) {
		int preserve = this.index;
		this.index++;
		return new_Some<Integer>(preserve);
	}
	else {
		return new_None<Integer>();
	}
}
Option<T> get_EmptyHead(){
	return new_None<T>();
}
int main(){
	main_Main();
	return 0;
}