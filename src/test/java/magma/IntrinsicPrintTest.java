package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertValid;

public class IntrinsicPrintTest {

    @Test
    public void intrinsicPrintArg() {
        String src = "intrinsic fn print<T>(value : T) : Void; print(100);";
        // Expect print to return the printed value as the program result
        assertValid(src, "100");
    }
}
