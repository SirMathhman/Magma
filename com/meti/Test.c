//
// Created by SirMathhman on 10/17/2020.
//

#include "app.h"
#include "api/test/Assert.h"
#include "api/core/Exception.h"

void testAssertions() {
    assertTrue("Assert True", true);
    assertFalse("Assert False", false);

    int testValue = 420;
    assertSame("Assert Same", &testValue, &testValue);
}

void testSomeIsPresent() {
    int value = 5;
    Some some = Some_(&value);
    Option option = some.Option(&some);
    assertTrue("Some.isPresent", option.isPresent(&option));
}

void testSomeIsEmpty() {
    int value = 5;
    Some some = Some_(&value);
    Option option = some.Option(&some);
    assertFalse("Some.isEmpty", option.isEmpty(&option));
}

void testSomeOrElse() {
    int value = 420;
    int other = 421;
    Some some = Some_(&value);
    Option option = some.Option(&some);
    assertSame("Some.orElse", &value, option.orElse(&option, &other));
}

void testNoneIsPresent() {
    None none = None_();
    Option option = none.Option(&none);
    assertFalse("None.isPresent", option.isPresent(&option));
}

void testNoneIsEmpty() {
    None none = None_();
    Option option = none.Option(&none);
    assertTrue("None.isEmpty", option.isEmpty(&option));
}

void testNoneOrElse() {
    int other = 421;
    None none = None_();
    Option option = none.Option(&none);
    assertSame("None.orElse", &other, option.orElse(&option, &other));
}

void testSome() {
    testSomeIsPresent();
    testSomeIsEmpty();
    testSomeOrElse();
}

void testNone() {
    testNoneIsPresent();
    testNoneIsEmpty();
    testNoneOrElse();
}

void testOption() {
    testSome();
    testNone();
}

int thrownDummy = 100;

void testAssertThrowsImpl(Any *caller) {
    throw(&thrownDummy);
}

void testAssertThrows() {
    assertThrows("Assert Throws", &thrownDummy, Global_(testAssertThrowsImpl));
}

#include "stdlib.h"
#include "api/core/Option.h"
#include "api/core/Exception.h"
#include "api/core/Core.h"
#include "api/collect/Array.h"
#include "api/string/String.h"
#include "api/test/Assert.h"
#include "api/collect/ArrayTest.h"

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

#include <string.h>

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

int main() {
    testAssertions();
    testOption();
    testAssertThrows();
    testCharArray();
    testStrings();
    return 0;
}