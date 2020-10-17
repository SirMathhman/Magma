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

void assertThrows(char* testName_, Any* expected, Function function) {
    void (*executable)() = function.value;
    executable(function.caller);

    Option option = catch();
    if(option.isPresent(&option)) {
        Any* actual = option.get(&option);
        if(expected == actual) {
            printf("PASS -- %s\n", testName_);
        } else {
            printf("FAIL -- %s: Expected %p to be thrown, but was actually %p\n.", testName_, expected, actual);
        }
    } else {
        printf("FAIL -- %s: Nothing was thrown.", testName_);
    }
}

int thrownDummy = 100;

void testAssertThrowsImpl(Any* caller){
    throw(&thrownDummy);
}

void testAssertThrows(){
    assertThrows("Assert Throws", &thrownDummy, Global_(testAssertThrowsImpl));
}

int main() {
    testAssertions();
    testOption();
    testAssertThrows();
    return 0;
}