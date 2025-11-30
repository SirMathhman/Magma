#include "intrinsics.h"
#include "string.h"

template <typename T>
T *moveToHeap(T value)
{
	T *pointer = (T *)malloc(sizeof(T));
	*pointer = value;
	return pointer;
}

int length_String(char **_this)
{
	return strlen(*_this);
}

char charAt_String(char **_this, int index)
{
	return (*_this)[index];
}