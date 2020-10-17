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
               Any *(*get)(struct Option *this)) {
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

Any *Some_get(Option *this) {
    Some *super = this->super;
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

Any *None_get(Option *this) {
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
        thrown_ = null;
        return some.Option(&some);
    } else {
        None none = None_();
        thrown_ = null;
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

int String_length(String *this) {
    CharArray array = this->array;
    int length = array.length;
    return length - 1;
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

char *String_asNative(String *this) {
    CharArray array = this->array;
    return array.array;
}

#include <stdlib.h>

Bool catchAnything() {
    return thrown_ != null;
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
