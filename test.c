//
// Created by SirMathhman on 10/17/2020.
//

#include "app.h"
#include <stdio.h>

void assertTrue(char *testName_, Bool value) {
    if (!value) {
        printf("FAIL -- %s: Expected a true value, but was actually false.\n", testName_);
    } else {
        printf("PASS -- %s\n", testName_);
    }
}

void assertFalse(char *testName_, Bool value) {
    if (value) {
        printf("PASS -- %s\n", testName_);
    } else {
        printf("FAIL -- %s: Expected a false value, but was actually true.\n", testName_);
    }
}

void testAssertions() {
    assertTrue("Assert True", true);
    assertFalse("Assert False", false);
}

void testSomeIsPresent() {
    int value = 5;
    Some some = Some_(&value);
    Option option = some.Option(&some);
    assertTrue("Some.isPresent", option.isPresent(&option));
}

void testSomeIsEmpty(){
    int value = 5;
    Some some = Some_(&value);
    Option option = some.Option(&some);
    assertFalse("Some.isEmpty", option.isEmpty(&option));
}

void testSome() {
    testSomeIsPresent();
    testSomeIsEmpty();
}

int main() {
    testAssertions();
    testSome();
    return 0;
}