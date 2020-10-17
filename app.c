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

int String_length(String* this) {
    CharArray array = this->array;
    int length = array.length;
    return length - 1;
}

String String_fromArray(CharArray array) {
    int length = array.length;
    char last = array.get(&array, length - 1);
    if (last == '\0') {
        String result = {array, String_length};
        return result;
    } else {
        throw("Invalid array format.");
        String result = {CharArray_("", 0), String_length};
        return result;
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
