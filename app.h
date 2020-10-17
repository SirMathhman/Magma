//
// Created by SirMathhman on 10/17/2020.
//

#ifndef MAGMA_APP_H
#define MAGMA_APP_H

typedef void Any;
typedef int bool;

int run(int argc, char **argv);

typedef struct Option {
    Any* super;
    Any *(*orElse)(struct Option* this, Any *other);
} Option;

Option Option_(Any* super, Any *(*orElse)(struct Option* this, Any *other));

typedef struct Some {
    Any* value;
    Option (*Option)(struct Some* this);
} Some;

Some Some_(Any* value);

void throw(Any* value);

Option catch();

#endif //MAGMA_APP_H
