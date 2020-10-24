//
// Created by mathm on 10/23/2020.
//

#include "Array.h"
#include "../test/Assert.h"

void testCharArrayGet() {
    CharArray *array = CharArrays._("test", 5);
    char value = array->get(array, 2);
    assertCharsEqual("CharArray.get", 's', value);
    array->delete(array);
}

void testCharArrayGetInvalidLowerImpl() {
    CharArray *array = CharArrays._("test", 5);
    array->get(array, -1);
    array->delete(array);
}

void testCharArrayGetInvalidLower() {
    assertThrowsAnything("CharArray.get Lower", Global_(testCharArrayGetInvalidLowerImpl));
}

void testCharArrayGetInvalidUpperImpl() {
    CharArray *array = CharArrays._("test", 5);
    array->get(array, 6);
}

void testCharArrayGetInvalidUpper() {
    assertThrowsAnything("CharArray.get Upper", Global_(testCharArrayGetInvalidUpperImpl));
}

void testCharArraySet() {
    char block = '\0';
    CharArray *array = CharArrays._(&block, 1);
    char previous = array->set(array, 0, 'x');
    assertCharsEqual("CharArray.set previous", 0, previous);
    assertCharsEqual("CharArray.set value", 'x', block);
    array->delete(array);
}

void testCharArray() {
    testCharArrayGet();
    testCharArrayGetInvalidLower();
    testCharArrayGetInvalidUpper();
    testCharArraySet();
}
