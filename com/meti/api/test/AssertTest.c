//
// Created by mathm on 10/23/2020.
//

#include "Assert.h"
#include "../core/Exception.h"

int thrownDummy = 100;

void testAssertThrowsImpl(Any *caller) {
    throw(&thrownDummy);
}

void testAssertThrows() {
    assertThrows("Assert Throws", &thrownDummy, Global_(testAssertThrowsImpl));
}

void testAssertBooleans() {
    assertTrue("Assert True", true);
    assertFalse("Assert False", false);
}

void testAssertPointers() {
    int testValue = 420;
    assertSame("Assert Same", &testValue, &testValue);
}

void testAssertions() {
    testAssertBooleans();
    testAssertPointers();
    testAssertThrows();
}