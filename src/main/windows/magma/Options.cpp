// File generated from 'JavaPath[path=.\src\main\java\magma\Options.java]'. This is not source code!
#include "Main.h"
Option<R> map_Option(Function<T, R> mapper);
Option<T> or_Option(Supplier<Option<T>> other);
T orElseGet_Option(Supplier<T> other);
Option<R> flatMap_Option(Function<T, Option<R>> mapper);
T orElse_Option(T other);
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
int main(){
	main_Main();
	return 0;
}