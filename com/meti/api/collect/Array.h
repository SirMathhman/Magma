//
// Created by mathm on 10/23/2020.
//

#ifndef MAGMA_ARRAY_H
#define MAGMA_ARRAY_H

typedef struct CharArray {
    char *array;

    char (*get)(struct CharArray *this, int index);

    char (*set)(struct CharArray *this, int index, char value);

    int length;
} CharArray;

struct CharArray CharArray_(char *array, int length);

#include "../core/Core.h"
#include "../core/Option.h"

#endif //MAGMA_ARRAY_H
