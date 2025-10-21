// File generated from '.\src\main\java\magma\Options.java'. This is not source code!
#include "Main.h"
Option<R> map_Option(Functions.Function<T, R> mapper);
Option<T> or_Option(Supplier<Option<T>> other);
T orElseGet_Option(Supplier<T> other);
Option<R> flatMap_Option(Functions.Function<T, Option<R>> mapper);
T orElse_Option(T other);
Option<Tuple<T, R>> and_Option(Supplier<Option<R>> supplier);
Option<R> map_Some(Functions.Function<T, R> mapper){
	return new_Some<R>(mapper.apply(this.value));
}
Option<T> or_Some(Supplier<Option<T>> other){
	return this;
}
T orElseGet_Some(Supplier<T> other){
	return this.value;
}
Option<R> flatMap_Some(Functions.Function<T, Option<R>> mapper){
	return mapper.apply(this.value);
}
T orElse_Some(T other){
	return this.value;
}
auto __lambda0__(auto rValue) {
	return new_Tuple<T, R>(this.value, rValue);
}
Option<Tuple<T, R>> and_Some(Supplier<Option<R>> supplier){
	return supplier.get().map(__lambda0__);
}
Option<R> map_None(Functions.Function<T, R> mapper){
	return new_None<R>();
}
Option<T> or_None(Supplier<Option<T>> other){
	return other.get();
}
T orElseGet_None(Supplier<T> other){
	return other.get();
}
Option<R> flatMap_None(Functions.Function<T, Option<R>> mapper){
	return new_None<R>();
}
T orElse_None(T other){
	return other;
}
Option<Tuple<T, R>> and_None(Supplier<Option<R>> supplier){
	return new_None<Tuple<T, R>>();
}
int main(){
	main_Main();
	return 0;
}