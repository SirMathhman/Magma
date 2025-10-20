// File generated from '.\src\main\java\magma\Options.java'. This is not source code!
#ifndef OPTIONS_H
#define OPTIONS_H
#include "../magma/Main.h"
enum OptionTag {
	Some,
	None
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
#endif