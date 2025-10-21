// File generated from '.\src\main\java\magma\Heads.java'. This is not source code!
#ifndef HEADS_H
#define HEADS_H
#include "../magma/Collections.h"
#include "../magma/Options.h"
#include "../magma/Streams.h"
template <typename T>
struct ListHead;
template <typename T>
struct ArrayHead;
template <typename T>
struct SingletonHead;
template <typename T, typename R>
struct FlatMapHead;
struct RangeHead;
template <typename T>
struct EmptyHead;
struct Heads;
template <typename T>
struct ListHead {
	ArrayList<T> self;
	int index;
};
template <typename T>
struct ArrayHead {
	T* elements;
	int length;
	int counter;
};
template <typename T>
struct SingletonHead {
	T value;
	boolean retrieved;
};
template <typename T, typename R>
struct FlatMapHead {
	Supplier<Option<T>> sourceHead;
	Functions.Function<T, Stream<R>> mapper;
	Option<Stream<R>> currentStream;
};
struct RangeHead {
	int length;
	int index;
};
template <typename T>
struct EmptyHead {
};
struct Heads {
};
#endif