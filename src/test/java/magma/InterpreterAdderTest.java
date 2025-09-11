package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterAdderTest {
    @Test
    public void adderImplicitParams() {
        String src = "fn Adder(first : I32, second : I32) => {fn add() => first + second; this} Adder(3, 4).add()";
        assertEquals("7", TestUtils.runAndAssertOk(src));
    }

    @Test
    public void adderThisFields() {
        String src = "fn Adder(first : I32, second : I32) => {fn add() => this.first + this.second; this} Adder(3, 4).add()";
        assertEquals("7", TestUtils.runAndAssertOk(src));
    }
}
