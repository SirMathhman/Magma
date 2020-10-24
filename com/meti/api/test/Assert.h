//
// Created by mathm on 10/23/2020.
//

#ifndef MAGMA_ASSERT_H
#define MAGMA_ASSERT_H

#include "../string/String.h"

void assertTrue(char *testName_, Bool value);

void assertFalse(char *testName_, Bool value);

void assertSame(char *testName_, Any *expected, Any *actual);

void assertThrows(char *testName_, Any *expected, Function function);

void assertThrowsAnything(char *testName_, Function function);

void assertDoesNotThrow(char *testName_, Function function);

void assertCharsEqual(char *testName_, char expected, char actual);

void assertIntsEqual(char *testName_, int expected, int actual);

void assertStringsEqual(char *testName_, String* expected, String* actual);

#endif //MAGMA_ASSERT_H
