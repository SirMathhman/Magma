//
// Created by mathm on 11/23/2020.
//

#include "AssertTest.h"

void testAssertTrue(){
    setUp("Assert True");
    assertTrue(true);
}

void testAssertFalse(){
    setUp("Assert False");
    assertFalse(false);
}

void testAssertSame(){
    setUp("Assert Same");
    I16 x = 10;
    assertSame(&x, &x);
}

void testAssertNull(){
    setUp("Assert Null");
    assertNull(null);
}

void testAssertions() {
    testAssertTrue();
    testAssertFalse();
    testAssertSame();
    testAssertNull();
}
