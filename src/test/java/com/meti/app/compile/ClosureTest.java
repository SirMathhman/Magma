package com.meti.app.compile;

import org.junit.jupiter.api.Test;

import static com.meti.app.compile.CompiledTest.assertSourceCompile;

public class ClosureTest {
    @Test
    void in_block() {
        assertSourceCompile("{def test() => {}}", "void test(){}{}");
    }

    @Test
    void in_function() {
        assertSourceCompile("def outer() => {def inner() => {}}", "void inner(){}void outer(){}");
    }
}