package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertValid;

class ArrayTypeTest {
    @Test
    void letWithArrayTypeAnnotation() {
        assertValid("let x : [U8; 3] = [1, 2, 3];", "uint8_t x[3] = {1, 2, 3};");
    }
}