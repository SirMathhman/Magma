//
// Created by SirMathhman on 10/17/2020.
//

#include "app.h"
#include <stdio.h>

void assertTrue(char *testName_, Bool value) {
    if (value) {
        printf("PASS -- %s\n", testName_);
    } else {
        printf("FAIL -- %s: Expected a true value, but was actually false.\n", testName_);
    }
}

void assertFalse(char *testName_, Bool value) {
    if (value) {
        printf("FAIL -- %s: Expected a false value, but was actually true.\n", testName_);
    } else {
        printf("PASS -- %s\n", testName_);
    }
}

void assertSame(char *testName_, Any *expected, Any *actual) {
    if (expected == actual) {
        printf("PASS -- %s\n", testName_);
    } else {
        printf("FAIL -- %s: Expected %p, but was actually %p\n.", testName_, expected, actual);
    }
}

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

void assertThrows(char *testName_, Any *expected, Function function) {
    void (*executable)() = function.value;
    executable(function.caller);

    Option option = catch();
    if (option.isPresent(&option)) {
        Any *actual = option.get(&option);
        if (expected == actual) {
            printf("PASS -- %s\n", testName_);
        } else {
            printf("FAIL -- %s: Expected %p to be thrown, but was actually %p\n.", testName_, expected, actual);
        }
    } else {
        printf("FAIL -- %s: Nothing was thrown.", testName_);
    }
}

void assertThrowsAnything(char *testName_, Function function) {
    void (*executable)(Any *) = function.value;
    executable(function.caller);

    Option option = catch();
    if (option.isPresent(&option)) {
        printf("PASS -- %s\n", testName_);
    } else {
        printf("FAIL -- %s: Nothing was thrown.", testName_);
    }
}

void assertThrowsNothing(char *testName_, Function function) {
    void (*executable)(Any *) = function.value;
    executable(function.caller);

    Option option = catch();
    if (option.isEmpty(&option)) {
        printf("PASS -- %s\n", testName_);
    } else {
        printf("FAIL -- %s: %p was thrown.", testName_, option.get(&option));
    }
}

int thrownDummy = 100;

void testAssertThrowsImpl(Any *caller) {
    throw(&thrownDummy);
}

void testAssertThrows() {
    assertThrows("Assert Throws", &thrownDummy, Global_(testAssertThrowsImpl));
}

void assertCharsEqual(char *testName_, char expected, char actual) {
    if (expected == actual) {
        printf("PASS -- %s\n", testName_);
    } else {
        printf("FAIL -- %s: Expected %c, but was actually %c\n.", testName_, expected, actual);
    }
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

void assertIntsEqual(char* testName_, int expected, int actual){
    if(expected == actual) {
        printf("PASS -- %s\n", testName_);
    } else {
        printf("FAIL -- %s: Expected %d, but was actually %d\n", testName_, expected, actual);
    }
}

void testStringInitImpl(){
    String_("test");
}

void testStringInit(){
    assertThrowsNothing("String", Global_(testStringInitImpl));
}

void testStringLength(){
    String value = String_("test");
    assertIntsEqual("String.length", 4, value.length(&value));
}

void testStringAsNative(){
    char *expected = "test";
    String value = String_(expected);
    assertSame("String.asNative", expected, value.asNative(&value));
}

void testStrings(){
    testStringInit();
    testStringLength();
    testStringAsNative();
}

int main() {
    testAssertions();
    testOption();
    testAssertThrows();
    testCharArray();
    testStrings();
    return 0;
}