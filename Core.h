//
// Created by mathm on 11/23/2020.
//

#ifndef MAGMA_CORE_H
#define MAGMA_CORE_H

#include <stdlib.h>

typedef char I8;
typedef int I16;
typedef void Any;
typedef int Bool;
typedef size_t Size;
typedef void Void;

#define false 0
#define true 1
#define null 0

typedef struct Option {
    Any *value;

    Bool (*isPresent)(struct Option *);

    Any *(*get)(struct Option *);
} Option;

Option Some(void* value);

Option None();

#endif //MAGMA_CORE_H
