//
// Created by SirMathhman on 9/11/2020.
//

#include "assert.h"

void testAssertions() {
    test("Assert Ints Equal");
    assertIntsEqual(3, 3);

    test("Assert Chars Equal");
    assertCharsEqual('x', 'x');
}

void testConversions() {
    test("Char to Int");
    assertIntsEqual(48, charToInt('0'));
    test("Int to Char");
    assertCharsEqual('0', intToChar(48));
}

int main(){
    testAssertions();
    testConversions();
    return 0;
}
