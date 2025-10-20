// File generated from '.\src\main\java\magma\Heads.java'. This is not source code!
#ifndef HEADS_H
#define HEADS_H
#include "../magma/Collections.h"
#include "../magma/Options.h"
#include "../magma/Streams.h"
template <typename T>
struct Head {
};
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
	Head<T> sourceHead;
	Function<T, Stream<R>> mapper;
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