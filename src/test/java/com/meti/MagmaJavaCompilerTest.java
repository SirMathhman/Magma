package com.meti;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MagmaJavaCompilerTest {
    @Test
    void compile() {
        assertCompile("import native Test from org.junit.jupiter.api;", "import org.junit.jupiter.api.Test;class __index__{}");
    }

    private void assertCompile(String source, String expectedOutput) {
        try {
            var actualOutput = compileImpl(source);
            assertEquals(expectedOutput, actualOutput);
        } catch (ApplicationException e) {
            fail(e);
        }
    }

    @Test
    void compile_fail() {
        assertThrows(ApplicationException.class, () -> compileImpl(""));
    }

    private String compileImpl(String source) throws ApplicationException {
        var compiler = new MagmaJavaCompiler(source, "index");
        return compiler.compile();
    }

    @Test
    void import_another_braces() {
        assertCompile("import native { baz } from bar;", "import bar.baz;class __index__{}");
    }

    @Test
    void import_braces() {
        assertCompile("import native { foo } from bar;", "import bar.foo;class __index__{}");
    }

    @Test
    void import_empty_braces() {
        assertCompile("import native {} from bar;", "class __index__{}");
    }

    @Test
    void import_two_braces() {
        assertCompile("import native { first, second } from bar;", "import bar.first;import bar.second;class __index__{}");
    }

    @Test
    void import_two_other_braces() {
        assertCompile("import native { third, fourth } from bar;", "import bar.third;import bar.fourth;class __index__{}");
    }
}