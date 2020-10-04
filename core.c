//
// Created by mathm on 10/4/2020.
//

#include "core.h"

Exception thrown_ = {null, null};
Bool wasThrown_ = false;

Exception throw_(Exception exception) {
    Exception previous = thrown_;
    thrown_ = exception;
    wasThrown_ = true;
    return previous;
}

void *catch_(void *(*action)(Exception *)) {
    if (wasThrown_) {
        return action(&thrown_);
    } else {
        return null;
    }
}
