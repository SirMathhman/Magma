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

    @Test
    public void intrinsicPrintConcat() {
        String src = "intrinsic fn print<T>(value : T) : Void; print(1); print(2);";
        // Expect sequential prints to concatenate their outputs into the program result
        assertValid(src, "12");
    }

    @Test
    public void printFinalExpr() {
        // Program with sequential print in statement position followed by a final
        // expression. Expect the printed output to be prepended to the final
        // expression's value, producing a concatenated program result.
        String src = "intrinsic fn print<T>(value : T) : Void; print(1); 2";
        assertValid(src, "12");
    }

    @Test
    public void printStringLiteral() {
        // Ensure double-quoted string literals are supported and intrinsic
        // print returns the inner string without quotes.
        String src = "intrinsic fn print<T>(value : T) : Void; print(\"Hello World!\");";
        assertValid(src, "Hello World!");
    }
}
