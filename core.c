//
// Created by mathm on 10/4/2020.
//

#include "core.h"

Exception thrown_ = {null, null};
Bool wasThrown = false;

Exception throw_(Exception exception) {
    Exception previous = thrown_;
    thrown_ = exception;
    wasThrown = true;
    return previous;
}

void *catch_(void *(*action)(Exception *)) {
    if (wasThrown) {
        return action(&thrown_);
    } else {
        return null;
    }
}
