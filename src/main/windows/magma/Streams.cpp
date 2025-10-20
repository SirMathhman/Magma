// File generated from 'JavaPath[path=.\src\main\java\magma\Streams.java]'. This is not source code!
#include "Main.h"
auto __lambda0__() {
	return this.head.next().map(mapper);
}
Stream<R> map_Stream(Function<T, R> mapper){
	return new_Stream<R>(__lambda0__);
}
auto __lambda1__() {
	while (true) {
		Options.Option < T >= nextValue == sourceHead.next();
		if (nextValue.tag == Options.Some) {
			if (predicate.test(value)) {
		Options.Some<T> _cast = nextValue.data.options.some;
		T value = _cast.value;
				return new_Options.Some<T>(value);
			}
			/*// Continue to next element*/
		}
		else {
			return new_Options.None<T>();
		}
	}
}
Stream<T> filter_Stream(Predicate<T> predicate){
	Heads.Head<T> sourceHead = this.head;
	return new_Stream<T>(__lambda1__);
}
C collect_Stream(Collectors.Collector<T, C> collector){
	return this.foldWithInitial(collector.createInitial(), fold_collector);
}
C foldWithInitial_Stream(C initial, BiFunction<C, T, C> folder){
	C accumulator = initial;
	Options.Option < T >= current == this.head.next();
	while (current.tag == Options.Some) {
		Options.Some<T> _cast = current.data.options.some;
		T value = _cast.value;
		accumulator == folder.apply(accumulator, value);
		current == this.head.next();
	}
	return accumulator;
}
Stream<R> flatMap_Stream(Function<T, Stream<R>> mapper){
	return new_Stream<R>(new_Heads.FlatMapHead<T, R>(this.head, mapper));
}
auto __lambda2__(auto inner) {
	return folder.apply(inner, element);
}
auto __lambda3__(auto current, auto element) {
	if (current.tag == Options.None) {
		Options.None<T> _cast = current.data.options.none;
		return new_Options.Some<T>(element);
	}
	return current.map(__lambda2__);
}
Options.Option<T> fold_Stream(BiFunction<T, T, T> folder){
	return this. < Options.Option < T >= foldWithInitial(new_Options.None<T>(), __lambda3__);
}
Options.Option<T> next_Stream(){
	return this.head.next();
}
Stream<T> fromRef_Streams(T* elements){
	return new_Stream<T>(new_Heads.ArrayHead<T>(elements, elements.length));
}
Stream<T> fromOption_Streams(Options.Option<T> option){
	return new_Stream<T>(/*switch (option) {
			case Options.None<T> _ -> new Heads.EmptyHead<T>();
			case Options.Some<T> v -> new Heads.SingletonHead<T>(v.value());
		}*/);
}
Stream<Integer> fromLength_Streams(int length){
	return new_Stream<Integer>(Heads.RangeHead.createRangeStream(length));
}
int main(){
	main_Main();
	return 0;
}