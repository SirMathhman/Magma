// File generated from '.\src\main\java\magma\Options.java'. This is not source code!
#include "Main.h"
Option<R> map_Option(void* _ref, Functions.Function<T, R> mapper){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, mapper);
}
Option<T> or_Option(void* _ref, Supplier<Option<T>> other){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, other);
}
T orElseGet_Option(void* _ref, Supplier<T> other){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, other);
}
Option<R> flatMap_Option(void* _ref, Functions.Function<T, Option<R>> mapper){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, mapper);
}
T orElse_Option(void* _ref, T other){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, other);
}
Option<Tuple<T, R>> and_Option(void* _ref, Supplier<Option<R>> supplier){
	Option this = *((Option*) _ref);
	return this.vtable.apply(this.data, supplier);
}
Option<R> map_Some(void* _ref, Functions.Function<T, R> mapper){
	return new_Some<R>(mapper.apply(this.value));
}
Option<T> or_Some(void* _ref, Supplier<Option<T>> other){
	return this;
}
T orElseGet_Some(void* _ref, Supplier<T> other){
	return this.value;
}
Option<R> flatMap_Some(void* _ref, Functions.Function<T, Option<R>> mapper){
	return mapper.apply(this.value);
}
T orElse_Some(void* _ref, T other){
	return this.value;
}
auto __lambda0__(auto rValue) {
	return new_Tuple<T, R>(this.value, rValue);
}
Option<Tuple<T, R>> and_Some(void* _ref, Supplier<Option<R>> supplier){
	return supplier.get().map(__lambda0__);
}
Option<R> map_None(void* _ref, Functions.Function<T, R> mapper){
	return new_None<R>();
}
Option<T> or_None(void* _ref, Supplier<Option<T>> other){
	return other.get();
}
T orElseGet_None(void* _ref, Supplier<T> other){
	return other.get();
}
Option<R> flatMap_None(void* _ref, Functions.Function<T, Option<R>> mapper){
	return new_None<R>();
}
T orElse_None(void* _ref, T other){
	return other;
}
Option<Tuple<T, R>> and_None(void* _ref, Supplier<Option<R>> supplier){
	return new_None<Tuple<T, R>>();
}
int main(){
	main_Main();
	return 0;
}