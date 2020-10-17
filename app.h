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

typedef struct None {
    Option (*Option)(struct None* this);
} None;

Option None_Option(None* this);

None None_ = {None_Option};

void throw(Any* value);

Option catch();

#endif //MAGMA_APP_H
