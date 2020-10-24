//
// Created by mathm on 10/23/2020.
//

#ifndef MAGMA_OPTION_H
#define MAGMA_OPTION_H

#include "Core.h"

typedef struct Option {
    Any *super;

    Any *(*orElse)(struct Option *this, Any *other);

    Bool (*isPresent)(struct Option *this);

    Bool (*isEmpty)(struct Option *this);

    Any *(*get)(struct Option *this);
} Option;

typedef struct Some {
    void *value;

    Option (*Option)(struct Some *this);
} Some;
typedef struct None {
    Option (*Option)(struct None *this);
} None;

struct Option Option_(Any *super,
                      Any *(*orElse)(struct Option *this, Any *other),
                      Bool (*isPresent)(struct Option *this),
                      Bool (*isEmpty)(struct Option *this),
                      Any *(*get)(struct Option *this));

Some Some_(void *value);

Option None_Option(None *this);

None None_();

#endif //MAGMA_OPTION_H
