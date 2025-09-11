package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterAdderTest {
    @Test
    public void adderTwoParamCapture() {
        String source = "fn Adder(first : I32, second : I32) => {fn add() => first + second; this} Adder(3, 4).add()";
        assertEquals("7", TestUtils.runAndAssertOk(source));
    }
}
