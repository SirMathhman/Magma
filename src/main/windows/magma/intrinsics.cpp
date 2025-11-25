#include "intrinsics.h"
#include "string.h"

template <typename T>
T *moveToHeap(T value)
{
	T *pointer = (T *)malloc(sizeof(T));
	*pointer = value;
	return pointer;
}

int length_String(char** this) {
 return strlen(*this);
}