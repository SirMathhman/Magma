//
// Created by mathm on 11/23/2020.
//

#include "Assert.h"
#include <stdio.h>

I8 *_testName_;

Void setUp(I8 *name) {
    _testName_ = name;
}

Void pass() {
    printf("PASS - %s\n", _testName_);
}

Void fail() {
    printf("FAIL - %s: ", _testName_);
}

Void assertTrue(Bool value) {
    if (value) {
        pass();
    } else {
        fail();
        printf("Value was unexpectedly false.\n");
    }
}

Void assertFalse(Bool value) {
    if (value) {
        fail();
        printf("Value was unexpectedly true.\n");
    } else {
        pass();
    }
}

Void assertSame(Any *expected, Any *actual) {
    if (expected == actual) {
        pass();
    } else {
        fail();
        printf("Expected '%p' but was actually '%p'.\n", expected, actual);
    }
}

Void assertNull(Any *value) {
    if (value) {
        fail();
        printf("Expected a null value but was actually '%p'.\n", value);
    } else {
        pass();
    }
}






