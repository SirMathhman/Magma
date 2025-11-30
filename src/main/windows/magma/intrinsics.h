#ifndef INTRINSICS_H
#define INTRINSICS_H

#include <stdlib.h>

template <typename T>
T *moveToHeap(T value);

int length_String(char **_this);
char charAt_String(char **_this, int index);

#endif // INTRINSICS_H