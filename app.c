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
               Bool (*isEmpty)(struct Option *),
               Any* (*get)(struct Option* this)) {
    Option result = {super, orElse, isPresent, isEmpty, get};
    return result;
}

Any *Some_orElse(Option *this, Any *other) {
    Some *super = this->super;
    return super->value;
}

Bool Some_isPresent(Option *this) {
    return true;
}

Bool Some_isEmpty(Option *this) {
    return false;
}

Any* Some_get(Option* this) {
    Some* super = this->super;
    return super->value;
}

Option Option_Some(Some *this) {
    return Option_(this,
                   Some_orElse,
                   Some_isPresent,
                   Some_isEmpty,
                   Some_get);
}

Some Some_(Any *value) {
    Some result = {value, Option_Some};
    return result;
}

Any *None_orElse(Option *this, Any *other) {
    return other;
}

Bool None_isPresent(Option *this) {
    return false;
}

Bool None_isEmpty(Option *this) {
    return true;
}

Any* None_get(Option* this) {
    return null;
}

Option None_Option(None *this) {
    Option super = Option_(this,
                           None_orElse,
                           None_isPresent,
                           None_isEmpty,
                           None_get);
    return super;
}

Option catch() {
    if (thrown_) {
        Some some = Some_(thrown_);
        return some.Option(&some);
    } else {
        None none = None_();
        return none.Option(&none);
    }
}

None None_() {
    None result = {None_Option};;
    return result;
}

Function Function_(Any *caller, Any *value) {
    Function result = {caller, value};
    return result;
}

Function Global_(Any *value) {
    Function result = {null, value};
    return result;
}
