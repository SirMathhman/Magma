#include "intrinsics.h"

template <typename T>
T *moveToHeap(T value)
{
	T *pointer = (T *)malloc(sizeof(T));
	*pointer = value;
	return pointer;
}