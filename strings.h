//
// Created by mathm on 10/23/2020.
//

#ifndef MAGMA_STRINGS_H
#define MAGMA_STRINGS_H

#include "array.h"

typedef struct String {
    CharArray array;

    int (*length)(struct String *);

    char *(*asNative)(struct String *);

    struct String (*slice)(struct String *, int from, int to);

    void (*delete)(struct String *);

    char (*charAt)(struct String *, int index);
} String;

String String_fromArray(CharArray array);

String String_(char *value);

String String_init(CharArray array);

String String_default();

#include "core.h"
#include "option.h"

#endif //MAGMA_STRINGS_H
