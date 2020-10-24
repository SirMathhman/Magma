//
// Created by mathm on 10/23/2020.
//

#include "../core/Core.h"
#include "../core/Exception.h"
#include "Array.h"
#include "stdlib.h"

char get_CharArray(CharArray *this, int index) {
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

char set_CharArray(CharArray *this, int index, char value) {
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

void delete_CharArray(CharArray *this) {
    free(this->array);
    free(this);
}

CharArray *CharArray_empty(int length) {
    char *buffer = malloc(sizeof(char) * length);
    return CharArrays._(buffer, length);
}

CharArray *CharArray_(char *array, int length) {
    CharArray *this = malloc(sizeof(struct CharArray));
    this->array = array;
    this->length = length;
    this->set = set_CharArray;
    this->get = get_CharArray;
    this->delete = delete_CharArray;
    return this;
}

CharArray *CharArray__() {
    return CharArrays.empty(0);
}

CharArrays_ CharArrays = {CharArray_empty, CharArray_, CharArray__};