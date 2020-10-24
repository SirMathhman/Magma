//
// Created by mathm on 10/23/2020.
//

#include "Exception.h"

Function Function_(Any *caller, Any *value) {
    Function result = {caller, value};
    return result;
}

Function Global_(Any *value) {
    Function result = {null, value};
    return result;
}