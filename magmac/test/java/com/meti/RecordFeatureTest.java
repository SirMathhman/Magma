package com.meti;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.meti.CompiledTest.assertCompile;
import static com.meti.Compiler.*;
import static com.meti.FeatureTest.TEST_SYMBOL;
import static com.meti.JavaLang.*;

public class RecordFeatureTest {
    public static final String TEST_BODY = "0";

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void testSimpleRecords(String name) {
        assertCompile(renderRecord(name), renderMagmaClass(name, ""));
    }

    @Test
    void testPublicKeyword() {
        assertCompile(renderRecord(PUBLIC_KEYWORD, TEST_SYMBOL), renderMagmaClass(EXPORT_KEYWORD, TEST_SYMBOL, ""));
    }

    @Test
    void testBody() {
        assertCompile(renderRecord("", TEST_SYMBOL, "{}"),
                renderMagmaClass(TEST_SYMBOL, ""));
    }
}
