// File generated from '.\src\main\java\magma\Streams.java'. This is not source code!
#include "Main.h"
auto __lambda0__() {
	return this.head.get().map(mapper);
}
Stream<R> map_Stream(R (*)(T) mapper){
	return new_Stream<R>(__lambda0__);
}
auto __lambda1__() {
	while (true) {
		Option < T >= nextValue == this.head.get();
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
	return new_Stream<T>(__lambda1__);
}
C collect_Stream(Collector<T, C> collector){
	return this.foldWithInitial(collector.createInitial(), fold_collector);
}
C foldWithInitial_Stream(C initial, BiFunction<C, T, C> folder){
	C accumulator = initial;
	Option < T >= current == this.head.get();
	while (current.tag == Some) {
		Some<T> _cast = current.data.some;
		T value = _cast.value;
		accumulator == folder.apply(accumulator, value);
		current == this.head.get();
	}
	return accumulator;
}
Stream<R> flatMap_Stream(Stream<R> (*)(T) mapper){
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
	return this.head.get();
}
auto __lambda4__() {
	return this.head.get().and(next_stream);
}
Stream<Tuple<T, R>> zip_Stream(Stream<R> stream){
	return new_Stream<Tuple<T, R>>(__lambda4__);
}
Stream<T> fromRef_Streams(T* elements){
	return new_Stream<T>(new_ArrayHead<T>(elements, elements.length));
}
Stream<Integer> fromLength_Streams(int length){
	return new_Stream<Integer>(RangeHead.createRangeStream(length));
}
int main(){
	main_Main();
	return 0;
}