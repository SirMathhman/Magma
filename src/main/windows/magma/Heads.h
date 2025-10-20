#ifndef HEADS_H
#define HEADS_H
#include "../magma/Collections.h"
#include "../magma/Options.h"
#include "../magma/Streams.h"
template <typeparam T>
struct Head {
};
template <typeparam T>
struct ListHead {
	ArrayList<T> self;
	int index;
};
template <typeparam T>
struct ArrayHead {
	T* elements;
	int length;
	int counter;
};
template <typeparam T>
struct SingletonHead {
	T value;
	boolean retrieved;
};
template <typeparam T, typeparam R>
struct FlatMapHead {
	Head<T> sourceHead;
	Function<T, Stream<R>> mapper;
	Option<Stream<R>> currentStream;
};
struct RangeHead {
	int length;
	int index;
};
template <typeparam T>
struct EmptyHead {
};
struct Heads {
};
#endif