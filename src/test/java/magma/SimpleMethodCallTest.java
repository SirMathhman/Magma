package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleMethodCallTest extends CompilerTestBase {

    @Test
    void simpleMethodCall() {
        String input = "let result = myCalculator.add(1, 2);";
        String expected = "int32_t result = add_Calculator(&myCalculator, 1, 2);";
        
        assertValid(input, expected);
    }
}