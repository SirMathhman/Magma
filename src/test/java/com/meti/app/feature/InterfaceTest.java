package com.meti.app.feature;

import org.junit.jupiter.api.Test;

public class InterfaceTest extends CompiledTest {
    @Test
    void generic() {
        assertCompile("interface Test<T> {}", "trait Test<T>{}");
    }

    @Test
    void test() {
        assertCompile("interface Test {}", "trait Test {}");
    }
}
