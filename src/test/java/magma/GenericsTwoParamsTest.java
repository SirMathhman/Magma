package magma;

import org.junit.jupiter.api.Test;

public class GenericsTwoParamsTest {
    @Test
    public void genericFnTwoParams() {
        TestUtils.assertValid("fn pass2<T, U>(a : T, b : U) => a; pass2(100, true)", "100");
    }
}
