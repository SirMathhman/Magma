package magma;

// TestHelpers.assertInterpEquals is used instead of inline assertions

import org.junit.jupiter.api.Test;

public class StructGenericsTest {

    @Test
    public void structWrap() {
    TestHelpers.assertInterpEquals("struct Wrapper<T> { value : T } let w = Wrapper { 100 }; w.value", "100");
    }

    @Test
    public void structPair() {
        // declare a generic struct with two type params and instantiate it
        String srcA = "struct Pair<T, U> { a : T, b : U } let p = Pair { 100, true }; p.a";
        TestHelpers.assertInterpEquals(srcA, "100");

        String srcB = "struct Pair<T, U> { a : T, b : U } let p = Pair { 100, true }; p.b";
        TestHelpers.assertInterpEquals(srcB, "true");
    }
}
