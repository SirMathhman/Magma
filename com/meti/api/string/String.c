//
// Created by mathm on 10/23/2020.
//

#include <stdlib.h>
#include "../collect/Array.h"
#include "../core/Exception.h"
#include "String.h"

int String_length(String *this) {
    return this->array->length - 1;
}

char *String_asNative(String *this) {
    return this->array->array;
}

String *slice_String(String *this, int from, int to) {
    if (to < from) {
        throw("To is less than from.");
        return Strings.__();
    }
    int newLength = to - from;
    CharArray *oldArray = this->array;
    CharArray *newArray = CharArrays.empty(newLength + 1);
    for (int i = 0; i < newLength; ++i) {
        char oldChar = oldArray->get(oldArray, i + from);
        if (catch_()) return Strings.__();
        newArray->set(newArray, i, oldChar);
        if (catch_()) return Strings.__();
    }
    newArray->set(newArray, newLength, '\0');
    if (catch_()) return Strings.__();
    return Strings._(newArray);
}

void delete_String(String *this) {
    CharArray *array = this->array;
    free(array);
    free(this);
}

char charAt_String(String *this, int index) {
    CharArray *array = this->array;
    return array->get(array, index);
}

String *concat_String(String *this, String *other) {
    int oldLength = this->length(this);
    int addedLength = other->length(other);
    int newLength = oldLength + addedLength + 1;
    CharArray *array = CharArrays.empty(newLength);
    for (int i = 0; i < oldLength; ++i) {
        char oldChar = this->charAt(this, i);
        array->set(array, i, oldChar);
        if (catch_()) return Strings.__();
    }
    for (int i = 0; i < addedLength; ++i) {
        char oldChar = other->charAt(other, i);
        array->set(array, i + oldLength, oldChar);
        if (catch_()) return Strings.__();
    }
    array->set(array, array->length - 1, '\0');
    if (catch_()) return Strings.__();
    return Strings._(array);
}

String *String_of(char *value) {
    int length;
    for (int i = 0;; ++i) {
        if (value[i] == '\0') {
            length = i;
            break;
        }
    }
    CharArray *array = CharArrays._(value, length + 1);
    return Strings._(array);
}

String *String_(CharArray *array) {
    String *this = malloc(sizeof(String));
    this->array = array;
    this->asNative = String_asNative;
    this->charAt = charAt_String;
    this->concat = concat_String;
    this->delete = delete_String;
    this->length = String_length;
    this->slice = slice_String;
    return this;
}

String *String__() {
    return String_(CharArrays.__());
}

Strings_ Strings = {String_of, String_, String__};