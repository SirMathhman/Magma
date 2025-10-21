// File generated from '.\src\main\java\magma\Options.java'. This is not source code!
#include "Main.h"
template <typename T>
Option<R> map_Option(void* _ref, Function<T, R> mapper){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, mapper);
}
template <typename T>
Option<T> or_Option(void* _ref, Supplier<Option<T>> other){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, other);
}
template <typename T>
T orElseGet_Option(void* _ref, Supplier<T> other){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, other);
}
template <typename T>
Option<R> flatMap_Option(void* _ref, Function<T, Option<R>> mapper){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, mapper);
}
template <typename T>
T orElse_Option(void* _ref, T other){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, other);
}
template <typename T>
Option<Tuple<T, R>> and_Option(void* _ref, Supplier<Option<R>> supplier){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, supplier);
}
template <typename T>
Option<R> map_Some(void* _ref, Function<T, R> mapper){
	return new_Some<R>(mapper.apply(this.value));
}
template <typename T>
Option<T> or_Some(void* _ref, Supplier<Option<T>> other){
	return this;
}
template <typename T>
T orElseGet_Some(void* _ref, Supplier<T> other){
	return this.value;
}
template <typename T>
Option<R> flatMap_Some(void* _ref, Function<T, Option<R>> mapper){
	return mapper.apply(this.value);
}
template <typename T>
T orElse_Some(void* _ref, T other){
	return this.value;
}
auto __lambda0__(auto rValue) {
	return new_Tuple<T, R>(this.value, rValue);
}
template <typename T>
Option<Tuple<T, R>> and_Some(void* _ref, Supplier<Option<R>> supplier){
	return supplier.get().map(__lambda0__);
}
template <typename T>
Option<R> map_None(void* _ref, Function<T, R> mapper){
	return new_None<R>();
}
template <typename T>
Option<T> or_None(void* _ref, Supplier<Option<T>> other){
	return other.get();
}
template <typename T>
T orElseGet_None(void* _ref, Supplier<T> other){
	return other.get();
}
template <typename T>
Option<R> flatMap_None(void* _ref, Function<T, Option<R>> mapper){
	return new_None<R>();
}
template <typename T>
T orElse_None(void* _ref, T other){
	return other;
}
template <typename T>
Option<Tuple<T, R>> and_None(void* _ref, Supplier<Option<R>> supplier){
	return new_None<Tuple<T, R>>();
}
int main(){
	main_Main();
	return 0;
}