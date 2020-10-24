//
// Created by mathm on 10/23/2020.
//

#include "../core/Core.h"
#include "../core/Exception.h"
#include "Array.h"

char CharArray_get(CharArray *this, int index) {
    int length = this->length;
    if (index < 0) {
        throw("Index is negative.");
        return -1;
    }
    if (index >= length) {
        throw("Index exceeds or is equal to length.");
        return -1;
    }
    return this->array[index];
}

char CharArray_set(CharArray *this, int index, char value) {
    int length = this->length;
    if (index < 0) {
        throw("Index is negative.");
        return -1;
    }
    if (index >= length) {
        throw("Index exceeds or is equal to length.");
        return -1;
    }
    char *array = this->array;
    char previous = array[index];
    array[index] = value;
    return previous;
}

CharArray CharArray_(char *array, int length) {
    CharArray result = {array, CharArray_get, CharArray_set, length};
    return result;
}