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
    assertThrowsNothing("String", Global_(testStringInitImpl));
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
    assertThrowsNothing("String.slice", Global_(testSliceImpl));
}

void testStrings() {
    testStringInit();
    testStringLength();
    testStringAsNative();
    testSlice();
}