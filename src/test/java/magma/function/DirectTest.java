package magma.function;

import magma.core.Compiler;
import org.junit.jupiter.api.Test;

public class DirectTest {
    @Test
    public void testDirectFunctionWithI16() {
        String input = "fn simple() : I16 => {return 0;}";
        Compiler compiler = new Compiler();
        String result = compiler.compile(input);
        System.out.println("INPUT: " + input);
        System.out.println("OUTPUT: " + result);
        System.out.println("EXPECTED: int16_t simple(){return 0;}");
    }
}