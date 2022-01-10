package com.meti.compile;

import com.meti.compile.clang.CFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CompiledTest {
    public static void assertSourceCompile(String input, String output) {
        assertCompile(CFormat.Source, input, output);
    }

    static void assertHeaderCompile(String input, String output) {
        assertCompile(CFormat.Header, input, output);
    }

    private static void assertCompile(CFormat format, String input, String output) {
        try {
            var compiler = new MagmaCCompiler(input);
            var actual = compiler.compile().apply(format, "");
            assertEquals(output, actual);
        } catch (CompileException e) {
            fail(e);
        }
    }
}
