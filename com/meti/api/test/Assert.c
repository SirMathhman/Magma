//
// Created by mathm on 10/23/2020.
//

#include "../string/String.h"
#include "../core/Exception.h"
#include <stdio.h>
#include "Assert.h"

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
        void *value = option.get(&option);
        char* message = value;
        printf("FAIL -- %s: %s", testName_, message);
    }
}

void assertCharsEqual(char *testName_, char expected, char actual) {
    if (expected == actual) {
        printf("PASS -- %s\n", testName_);
    } else {
        printf("FAIL -- %s: Expected %c, but was actually %c\n.", testName_, expected, actual);
    }
}

void assertIntsEqual(char *testName_, int expected, int actual) {
    if (expected == actual) {
        printf("PASS -- %s\n", testName_);
    } else {
        printf("FAIL -- %s: Expected %d, but was actually %d\n", testName_, expected, actual);
    }
}

void assertStringsEqual(char *testName_, String expected, String actual) {
    int expectedLength = expected.length(&expected);
    int actualLength = actual.length(&actual);
    if(expectedLength == actualLength) {
        for (int i = 0; i < expectedLength; ++i) {
            char expectedChar = expected.charAt(&expected, i);
            char actualChar = actual.charAt(&actual, i);
            if (expectedLength != actualLength) {
                printf("FAIL -- %s: Expected a value of %c, but was actually %c, at index %d\n",
                       testName_, expectedChar, actualChar, i);
            }
        }
        printf("PASS -- %s\n", testName_);
    } else {
        printf("FAIL -- %s: Expected a length of %zu, but was actually %zu.\n", testName_, expectedLength, actualLength);
    }
}