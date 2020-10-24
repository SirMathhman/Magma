//
// Created by mathm on 10/23/2020.
//

#ifndef MAGMA_ARRAY_H
#define MAGMA_ARRAY_H

#include "../core/Core.h"
#include "../core/Option.h"

typedef struct CharArray {
    char *array;

    char (*get)(struct CharArray *this, int index);

    char (*set)(struct CharArray *this, int index, char value);

    int length;

    void (*delete)(struct CharArray *this);
} CharArray;

typedef struct CharArrays {
    struct CharArray *(*empty)(int length);

    struct CharArray *(*_)(char *array, int length);

    struct CharArray *(*__)();
} CharArrays_;

CharArrays_ CharArrays;

#endif //MAGMA_ARRAY_H
