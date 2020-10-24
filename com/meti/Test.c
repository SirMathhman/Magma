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

#include "api/collect/ArrayTest.h"
#include "api/string/StringTest.h"

int main() {
    testAssertions();
    testOption();
    testAssertThrows();
    testCharArray();
    testStrings();
    return 0;
}