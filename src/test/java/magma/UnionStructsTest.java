package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class UnionStructsTest {

    @Test
    public void unionStructsIsCheck() {
    // Evaluate first check
    Result<String, InterpretError> r1 = new Interpreter().interpret(
        "struct Ok { } struct Err { } type Result = Ok | Err; let a : Result = Ok { }; a is Ok");
        if (r1 instanceof Result.Err) {
            fail("Interpreter returned error: " + ((Result.Err<String, InterpretError>) r1).error());
        }
    assertEquals("true", ((Result.Ok<String, InterpretError>) r1).value());

    // Evaluate second check
    Result<String, InterpretError> r2 = new Interpreter().interpret(
        "struct Ok { } struct Err { } type Result = Ok | Err; let b : Result = Err { }; b is Err");
        if (r2 instanceof Result.Err) {
            fail("Interpreter returned error: " + ((Result.Err<String, InterpretError>) r2).error());
        }
    assertEquals("true", ((Result.Ok<String, InterpretError>) r2).value());
    }
}
