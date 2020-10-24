//
// Created by mathm on 10/23/2020.
//

#ifndef MAGMA_STRING_H
#define MAGMA_STRING_H

#include "../core/Core.h"
#include "../core/Option.h"
#include "../collect/Array.h"

typedef struct String {
    CharArray *array;

    int (*length)(struct String *);

    char *(*asNative)(struct String *);

    struct String *(*slice)(struct String *, int from, int to);

    void (*delete)(struct String *);

    char (*charAt)(struct String *, int index);

    struct String* (*concat)(struct String *, struct String* other);
} String;

typedef struct Strings {
    String *(*of)(char *value);

    String *(*_)(CharArray *array);

    String *(*__)();
} Strings_;

Strings_ Strings;

#endif //MAGMA_STRING_H
