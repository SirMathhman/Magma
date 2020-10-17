//
// Created by SirMathhman on 10/17/2020.
//

#include "app.h"

int run(int argc, char **argv) {
    return 0;
}

Any* thrown_;

void throw(Any *value) {
    thrown_=value;
}

Option Option_(Any *super, void *(*orElse)(struct Option *, Any *)) {
    Option result = {super, orElse};
    return result;
}

Any* Some_orElse(Option* this, Any* other){
    Some* super = this->super;
    return super->value;
}

Option Option_Some(Some* this) {
    return Option_(this, Some_orElse);
}

Some Some_(Any *value) {
    Some result = {value, Option_Some};
    return result;
}

Option catch() {
    Some some = Some_(thrown_);
    return some.Option(&some);
}




