#ifndef INTRINSICS_H
#define INTRINSICS_H

#include <stdlib.h>

template <typename T>
T *moveToHeap(T value);

int length_String(char** this);
char charAt_String(char** this, int index);

#endif // INTRINSICS_H