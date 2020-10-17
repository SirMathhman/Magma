//
// Created by SirMathhman on 10/17/2020.
//

#include "app.h"
#include <stdio.h>

void assertTrue(char* testName_, Bool value){
    if (!value) {
        printf("FAIL -- %s: Expected a true value, but was actually false.\n", testName_);
    } else {
        printf("PASS -- %s\n", testName_);
    }
}

void assertFalse(char* testName_, Bool value) {
    if (value) {
        printf("PASS -- %s\n", testName_);
    } else {
        printf("FAIL -- %s: Expected a false value, but was actually true.\n", testName_);
    }
}

void testAssertions(){
    assertTrue("Assert True", true);
    assertFalse("Assert False", false);
}

int main(){
    testAssertions();
    return 0;
}