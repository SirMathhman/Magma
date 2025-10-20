// File generated from '.\src\main\java\magma\Collections.java'. This is not source code!
#ifndef COLLECTIONS_H
#define COLLECTIONS_H
#include "../magma/Collectors.h"
#include "../magma/Heads.h"
#include "../magma/Main.h"
#include "../magma/Options.h"
#include "../magma/Streams.h"
#include "../java/util.h"
template <typeparam T>
struct Array {
	T* ref;
	int capacity;
	int length;
};
template <typeparam T>
struct ArrayList {
	Array<T> elements;
};
struct Collections {
};
#endif