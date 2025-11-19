package com.magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Optional;

import com.magma.result.Ok;
import com.magma.result.Result;
import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void testInterpret() {
        final Result<String, String> result1 = Main.interpret(Optional.of("5"));
        assertInstanceOf(Ok.class, result1);
        final Ok<String, String> ok1 = (Ok<String, String>) result1;
        assertEquals("5", ok1.getValue());

        final Result<String, String> result2 =
                Main.interpret(Optional.of("5U8"));
        assertInstanceOf(Ok.class, result2);
        final Ok<String, String> ok2 = (Ok<String, String>) result2;
        assertEquals("5", ok2.getValue());
    }
}

