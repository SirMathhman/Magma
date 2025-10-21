// File generated from '.\src\main\java\magma\Collections.java'. This is not source code!
#ifndef COLLECTIONS_H
#define COLLECTIONS_H
#include "../magma/Collectors.h"
#include "../magma/Heads.h"
#include "../magma/Utils.h"
#include "../magma/Options.h"
#include "../magma/Streams.h"
template <typename T>
struct Array;
template <typename T>
struct ArrayList;
struct Collections;
template <typename T>
struct Array {
	T* ref;
	int capacity;
	int length;
};
template <typename T>
struct ArrayList {
	Array<T> elements;
};
struct Collections {
};
#endif