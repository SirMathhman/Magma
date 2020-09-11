//
// Created by SirMathhman on 9/11/2020.
//

#include "assert.h"
#include <stdio.h>

string _testName_;

void test(string name) {
    _testName_ = name;
}

void assertIntsEqual(int expected, int actual) {
    if(expected == actual) {
        printf("PASS - %s\n", _testName_);
    } else {
        printf("FAIL - %s: Expected value of '%i' doesn't equal actual value of '%i'.", _testName_, expected, actual);
    }
}

void assertCharsEqual(char expected, char actual) {
    if(expected == actual) {
        printf("PASS - %s\n", _testName_);
    } else {
        printf("FAIL - %s: Expected value of '%c' doesn't equal actual value of '%c'.", _testName_, expected, actual);
    }
}
