//
// Created by mathm on 10/23/2020.
//

#include "String.h"
#include "../test/Assert.h"
#include "../core/Exception.h"

void testStringInitImpl() {
    String *value = Strings.of("test");
    value->delete(value);
}

void testStringInit() {
    assertDoesNotThrow("String", Global_(testStringInitImpl));
}

void testStringLength() {
    String *value = Strings.of("test");
    assertIntsEqual("String.length", 4, value->length(value));
    value->delete(value);
}

void testStringAsNative() {
    char *expected = "test";
    String *value = Strings.of(expected);
    assertSame("String.asNative", expected, value->asNative(value));
    value->delete(value);
}

void testSliceImpl() {
    String *value = Strings.of("test");
    String *result = value->slice(value, 1, 2);
    if (catch_()) return;
    String *expected = Strings.of("e");
    assertStringsEqual("String.slice", expected, result);
    result->delete(result);
}

void testSlice() {
    assertDoesNotThrow("String.slice", Global_(testSliceImpl));
}

void testConcatImpl() {
    String *first = Strings.of("te");
    String *second = Strings.of("st");
    String* actual = first->concat(first, second);
    String *expected = Strings.of("test");
    assertStringsEqual("String.concat", expected, actual);
    first->delete(first);
    second->delete(second);
    expected->delete(expected);
    actual->delete(actual);
}

void testConcat(){
    assertDoesNotThrow("String.concat", Global_(testConcatImpl));
}

void testStrings() {
    testStringInit();
    testStringLength();
    testStringAsNative();
    testSlice();
    testConcat();
}