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

int thrownDummy = 100;

void testAssertThrowsImpl(Any *caller) {
    throw(&thrownDummy);
}

void testAssertThrows() {
    assertThrows("Assert Throws", &thrownDummy, Global_(testAssertThrowsImpl));
}E

#include "api/collect/ArrayTest.h"
#include "api/string/StringTest.h"
#include "api/core/OptionTest.h"

int main() {
    testAssertions();
    testOption();
    testAssertThrows();
    testCharArray();
    testStrings();
    return 0;
}