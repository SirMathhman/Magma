package magma;

// use TestHelpers.assertInterpEquals for common assertions

import org.junit.jupiter.api.Test;

public class GenericsTests {

    @Test
    public void structWrapper() {
        TestHelpers.assertInterpEquals("struct Wrapper<T> { value : T } let w = Wrapper { 100 }; w.value", "100");
    }

    @Test
    public void structPair() {
        TestHelpers.assertInterpEquals("struct Pair<T, U> { a : T, b : U } let p = Pair { 100, true }; p.a", "100");
        TestHelpers.assertInterpEquals("struct Pair<T, U> { a : T, b : U } let p = Pair { 100, true }; p.b", "true");
    }

    @Test
    public void classWrapper() {
        // Acceptance test: class-style constructor with a single type parameter
        TestHelpers.assertInterpEquals("class fn Wrapper<T>(value : T) => this; let w = Wrapper(100); w.value", "100");
    }

}
