package magma;

// TestHelpers.assertInterpEquals is used instead of inline assertions

import org.junit.jupiter.api.Test;

public class StructGenericsTest {

    @Test
    public void structWrap() {
        TestUtils.assertValid("struct Wrapper<T> { value : T } let w = Wrapper { 100 }; w.value", "100");
    }

    @Test
    public void structPair() {
        TestUtils.assertValid("struct Pair<T, U> { a : T, b : U } let p = Pair { 100, true }; p.a", "100");
        TestUtils.assertValid("struct Pair<T, U> { a : T, b : U } let p = Pair { 100, true }; p.b", "true");
    }
}
