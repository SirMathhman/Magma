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

void testCharArrayGet() {
    CharArray array = CharArray_("test", 5);
    char value = array.get(&array, 2);
    assertCharsEqual("CharArray.get", 's', value);
}

void testCharArrayGetInvalidLowerImpl() {
    CharArray array = CharArray_("test", 5);
    array.get(&array, -1);
}

void testCharArrayGetInvalidLower() {
    assertThrowsAnything("CharArray.get Lower", Global_(testCharArrayGetInvalidLowerImpl));
}

void testCharArrayGetInvalidUpperImpl() {
    CharArray array = CharArray_("test", 5);
    array.get(&array, 6);
}

void testCharArrayGetInvalidUpper() {
    assertThrowsAnything("CharArray.get Upper", Global_(testCharArrayGetInvalidUpperImpl));
}

#include "stdlib.h"
#include "api/core/Option.h"
#include "api/core/Exception.h"
#include "api/core/Core.h"
#include "api/collect/Array.h"
#include "api/string/String.h"
#include "api/test/Assert.h"

void testCharArraySet() {
    char *block = malloc(sizeof(char));
    CharArray array = CharArray_(block, 1);
    char previous = array.set(&array, 0, 'x');
    assertCharsEqual("CharArray.set previous", 0, previous);
    assertCharsEqual("CharArray.set value", 'x', block[0]);
    free(block);
}

void testCharArray() {
    testCharArrayGet();
    testCharArrayGetInvalidLower();
    testCharArrayGetInvalidUpper();
    testCharArraySet();
}

void testStringInitImpl() {
    String_("test");
}

void testStringInit() {
    assertThrowsNothing("String", Global_(testStringInitImpl));
}

void testStringLength() {
    String value = String_("test");
    assertIntsEqual("String.length", 4, value.length(&value));
}

void testStringAsNative() {
    char *expected = "test";
    String value = String_(expected);
    assertSame("String.asNative", expected, value.asNative(&value));
}

#include <string.h>

void testSliceImpl() {
    String value = String_("test");
    String result = value.slice(&value, 1, 2);
    if (catchAnything()) return;
    assertStringsEqual("String.slice", String_("e"), result);
    result.delete(&result);
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