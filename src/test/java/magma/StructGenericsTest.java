package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class StructGenericsTest {

    @Test
    public void structWithTypeParamValueAccess() {
        // declare a generic struct and instantiate it
        Result<String, InterpretError> r = new Interpreter().interpret(
                "struct Wrapper<T> { value : T } let w = Wrapper { 100 }; w.value");
        if (r instanceof Result.Err) {
            fail("Interpreter returned error: " + ((Result.Err<String, InterpretError>) r).error());
        }
        assertEquals("100", ((Result.Ok<String, InterpretError>) r).value());
    }

    @Test
    public void structWithTwoTypeParamsValueAccess() {
        // declare a generic struct with two type params and instantiate it
        String srcA = "struct Pair<T, U> { a : T, b : U } let p = Pair { 100, true }; p.a";
        Result<String, InterpretError> rA = new Interpreter().interpret(srcA);
        if (rA instanceof Result.Err) {
            fail("Interpreter returned error: " + ((Result.Err<String, InterpretError>) rA).error());
        }
        assertEquals("100", ((Result.Ok<String, InterpretError>) rA).value());

        String srcB = "struct Pair<T, U> { a : T, b : U } let p = Pair { 100, true }; p.b";
        Result<String, InterpretError> rB = new Interpreter().interpret(srcB);
        if (rB instanceof Result.Err) {
            fail("Interpreter returned error: " + ((Result.Err<String, InterpretError>) rB).error());
        }
        assertEquals("true", ((Result.Ok<String, InterpretError>) rB).value());
    }
}
