// File generated from '.\src\main\java\magma\Options.java'. This is not source code!
#ifndef OPTIONS_H
#define OPTIONS_H
#include "../magma/Functions.h"
#include "../magma/Utils.h"
template <typename T>
struct Option;
template <typename T>
struct Some;
template <typename T>
struct None;
struct Options;
template <typename T>
struct Some {
	T value;
};
template <typename T>
struct None {
};
enum OptionTag {
	SomeType,
	NoneType
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
Option<R> map_Option(void* _ref, Function<T, R> mapper);
template <typename T>
Option<T> or_Option(void* _ref, Supplier<Option<T>> other);
template <typename T>
T orElseGet_Option(void* _ref, Supplier<T> other);
template <typename T>
Option<R> flatMap_Option(void* _ref, Function<T, Option<R>> mapper);
template <typename T>
T orElse_Option(void* _ref, T other);
template <typename T>
Option<Tuple<T, R>> and_Option(void* _ref, Supplier<Option<R>> supplier);
#endif