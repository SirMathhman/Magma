//
// Created by SirMathhman on 10/23/2020.
//

#include "Option.h"
#include "Exception.h"

Any *thrown_;

void throw(Any *value) {
    thrown_ = value;
}

Option catch() {
    if (thrown_) {
        Some some = Some_(thrown_);
        thrown_ = null;
        return some.Option(&some);
    } else {
        None none = None_();
        thrown_ = null;
        return none.Option(&none);
    }
}

Bool catchAnything() {
    return thrown_ != null;
}