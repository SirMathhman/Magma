//
// Created by SirMathhman on 10/17/2020.
//

#include "app.h"

int run(int argc, char **argv) {
    return 0;
}

Any *thrown_;

void throw(Any *value) {
    thrown_ = value;
}

Option Option_(Any *super,
               Any *(*orElse)(struct Option *, Any *),
               Bool (*isPresent)(struct Option *),
               Bool (*isEmpty)(struct Option *)) {
    Option result = {super, orElse, isPresent, isEmpty};
    return result;
}

Any *Some_orElse(Option *this, Any *other) {
    Some *super = this->super;
    return super->value;
}

Bool Some_isPresent(Option *this) {
    return true
}

Bool Some_isEmpty(Option *this) {
    return false
}

Option Option_Some(Some *this) {
    return Option_(this,
                   Some_orElse,
                   Some_isPresent,
                   Some_isEmpty);
}

Some Some_(Any *value) {
    Some result = {value, Option_Some};
    return result;
}

Any *None_orElse(Option *this, Any *other) {
    return other;
}

Bool None_isPresent(Option *this) {
    return false
}

Bool None_isEmpty(Option *this) {
    return true
}

Option None_Option(None *this) {
    Option super = Option_(this,
                           None_orElse,
                           None_isPresent,
                           None_isEmpty);
    return super;
}

Option catch() {
    if (thrown_) {
        Some some = Some_(thrown_);
        return some.Option(&some);
    } else {
        return None_.Option(&None_);
    }
}
