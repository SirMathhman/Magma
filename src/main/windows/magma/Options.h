// File generated from '.\src\main\java\magma\Options.java'. This is not source code!
#ifndef OPTIONS_H
#define OPTIONS_H
#include "../magma/Main.h"
template <typename T>
struct Option;
template <typename T>
struct Some;
template <typename T>
struct None;
struct Options;
enum OptionTag {
	SomeTag,
	NoneTag
};
template <typename T>
union OptionData {
	Some<T> some;
	None<T> none;
};
template <typename T>
struct Option {
	OptionTag tag;
	OptionData<T> data;
};
template <typename T>
struct Some {
	T value;
};
template <typename T>
struct None {
};
struct Options {
};
Option<R> map_Option(void* _ref, Functions.Function<T, R> mapper);
Option<T> or_Option(void* _ref, Supplier<Option<T>> other);
T orElseGet_Option(void* _ref, Supplier<T> other);
Option<R> flatMap_Option(void* _ref, Functions.Function<T, Option<R>> mapper);
T orElse_Option(void* _ref, T other);
Option<Tuple<T, R>> and_Option(void* _ref, Supplier<Option<R>> supplier);
#endif