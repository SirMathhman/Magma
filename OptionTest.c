//
// Created by mathm on 11/23/2020.
//

#include "OptionTest.h"

void testSomeIsPresent() {
    setUp("Some.isPresent");
    char *value = "test";
    Option this = Some(value);
    assertTrue(this.isPresent(&this));
}

void testSomeGet() {
    setUp("Some.get");
    char *value = "test";
    Option this = Some(value);
    assertSame(value, this.get(&this));
}

void testNoneIsPresent() {
    setUp("None.isPresent");
    Option this = None();
    assertFalse(this.isPresent(&this));
}

void testNoneGet() {
    setUp("None.get");
    Option this = None();
    assertNull(this.get(&this));
}

Void testSome() {
    testSomeIsPresent();
    testSomeGet();
}

Void testNone() {
    testNoneIsPresent();
    testNoneGet();
}

Void testOptions() {
    testSome();
    testNone();
}
