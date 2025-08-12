package magma;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static magma.TestUtils.assertValid;

import java.util.stream.Stream;

class CompilerTypeTest {
    static Stream<Arguments> typeProvider() {
        return Stream.of(
            Arguments.of("let a : U8 = 1;", "uint8_t a = 1;"),
            Arguments.of("let a : U16 = 2;", "uint16_t a = 2;"),
            Arguments.of("let a : U32 = 3;", "uint32_t a = 3;"),
            Arguments.of("let a : U64 = 4;", "uint64_t a = 4;"),
            Arguments.of("let a : I8 = 5;", "int8_t a = 5;"),
            Arguments.of("let a : I16 = 6;", "int16_t a = 6;"),
            Arguments.of("let a : I32 = 7;", "int32_t a = 7;"),
            Arguments.of("let a : I64 = 8;", "int64_t a = 8;"),
            Arguments.of("let x = 1U8;", "uint8_t x = 1;"),
            Arguments.of("let x = 2U16;", "uint16_t x = 2;"),
            Arguments.of("let x = 3U32;", "uint32_t x = 3;"),
            Arguments.of("let x = 4U64;", "uint64_t x = 4;"),
            Arguments.of("let x = 5I8;", "int8_t x = 5;"),
            Arguments.of("let x = 6I16;", "int16_t x = 6;"),
            Arguments.of("let x = 7I32;", "int32_t x = 7;"),
            Arguments.of("let x = 8I64;", "int64_t x = 8;")
        );
    }

    @ParameterizedTest
    @MethodSource("typeProvider")
    void testTypes(String input, String output) {
        assertValid(input, output);
    }
}
