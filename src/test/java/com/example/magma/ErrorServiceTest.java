package com.example.magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorServiceTest {

    @Test
    public void testTranslateSyntaxWithEmptyString() {
        ErrorService errorService = new ErrorService();
        String result = errorService.translateSyntax("");
        assertEquals("", result);
    }
    
    @ParameterizedTest
    @CsvSource({
        "'let x : I8 = 127;', 'int8_t x = 127;'",
        "'let y : I16 = 32767;', 'int16_t y = 32767;'",
        "'let z : I32 = 0;', 'int32_t z = 0;'",
        "'let w : I64 = 9223372036854775807;', 'int64_t w = 9223372036854775807;'",
        "'let a : U8 = 255;', 'uint8_t a = 255;'",
        "'let b : U16 = 65535;', 'uint16_t b = 65535;'",
        "'let c : U32 = 4294967295;', 'uint32_t c = 4294967295;'",
        "'let d : U64 = 18446744073709551615;', 'uint64_t d = 18446744073709551615;'"
    })
    public void testTranslateSyntaxWithIntegerTypes(String input, String expectedDeclaration) {
        ErrorService errorService = new ErrorService();
        String result = errorService.translateSyntax(input);
        String expected = "#include <stdint.h>\n" + expectedDeclaration;
        assertEquals(expected, result);
    }

    @Test
    public void testTranslateSyntaxWithUnsupportedInputThrowsException() {
        ErrorService errorService = new ErrorService();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            errorService.translateSyntax("test");
        });

        assertEquals("Error processing unsupported input", exception.getMessage());
    }
}