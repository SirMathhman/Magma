#ifndef OPTIONS_H
#define OPTIONS_H
#include "../java/util/function.h"
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
template <typeparam T>
struct Some {
	T value;
};
template <typeparam T>
struct None {
};
struct Options {
};
#endif