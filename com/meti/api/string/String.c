//
// Created by mathm on 10/23/2020.
//

#include <stdlib.h>
#include "../collect/Array.h"
#include "../core/Exception.h"
#include "String.h"

int String_length(String *this) {
    CharArray array = this->array;
    int length = array.length;
    return length - 1;
}

char *String_asNative(String *this) {
    CharArray array = this->array;
    return array.array;
}


String slice_String(String *this, int from, int to) {
    if (to < from) {
        throw("To is less than from.");
        return String_init(CharArray_("", 0));
    }
    int newLength = to - from;
    CharArray oldArray = this->array;
    char *buffer = malloc(sizeof(char) * (newLength + 1));
    buffer[newLength] = '\0';
    CharArray newArray = CharArray_(buffer, newLength + 1);
    for (int i = 0; i < newLength; ++i) {
        char oldChar = oldArray.get(&oldArray, i + from);
        if (catchAnything()) return String_default();
        newArray.set(&newArray, i, oldChar);
        if (catchAnything()) return String_default();
    }
    return String_fromArray(newArray);
}

void delete_String(String *this) {
    CharArray array = this->array;
    free(array.array);
}

char charAt_String(String *this, int index) {
    CharArray array = this->array;
    return array.get(&array, index);
}

String String_fromArray(CharArray array) {
    int length = array.length;
    char last = array.get(&array, length - 1);
    if (last == '\0') {
        return String_init(array);
    } else {
        throw("Invalid array format.");
        return String_init(CharArray_("", 0));
    }
}

String String_(char *value) {
    int length;
    for (int i = 0;; ++i) {
        if (value[i] == '\0') {
            length = i;
            break;
        }
    }
    return String_fromArray(CharArray_(value, length + 1));
}

String String_init(CharArray array) {
    String this = {};
    this.array = array;
    this.length = String_length;
    this.asNative = String_asNative;
    this.slice = slice_String;
    this.delete = delete_String;
    this.charAt = charAt_String;
    return this;
}

String String_default() {
    return String_init(CharArray_("", 0));
}