package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
    @Test
    void interpretEmptyInputReturnsOkEmpty() {
        assertInterpretsTo("", "");
    }

    private static void assertInterpretsTo(String program, String expectedValue) {
        Interpreter interpreter = new Interpreter();
        Result<String, InterpretError> expected = Result.ok(expectedValue);
        Result<String, InterpretError> actual = interpreter.interpret(program, "");
        assertEquals(expected, actual);
    }

    @Test
    void interpretEmptyQuotedStringLiteralReturnsItself() {
        assertInterpretsTo("\"\"", "\"\"");
    }

    @Test
    void interpretPassCallReturnsQuotedArgument() {
        assertInterpretsTo("fn pass(str : *[U8]) => str; pass(\"\")", "\"\"");
    }

    @Test
    void interpretZeroReturnsZero() {
        assertInterpretsTo("0", "0");
    }

    @Test
    void interpretLetAssignmentReturnsZero() {
        assertInterpretsTo("let x = 0; x", "0");
    }

    @Test
    void interpretTrueReturnsTrue() {
        assertInterpretsTo("true", "true");
    }

    @Test
    void interpretTypedLetAssignmentReturnsZero() {
        assertInterpretsTo("let x : I32 = 0; x", "0");
    }

    @Test
    void interpretTypedU8LetCharReturnsAscii() {
        assertInterpretsTo("let x : U8 = 'a'; x", "97");
    }

    @Test
    void interpretDuplicateLetDeclarationsProduceErr() {
        Interpreter interpreter = new Interpreter();
        Result<String, InterpretError> actual = interpreter.interpret("let x = 0; let x = 0;", "");
        // Expect an Err result when redeclaring the same name in the same scope
        assert (actual instanceof Result.Err);
    }

    @Test
    void interpretTypedBoolAssignmentWithNumberProducesErr() {
        Interpreter interpreter = new Interpreter();
        Result<String, InterpretError> actual = interpreter.interpret("let x : Bool = 0;", "");
        // Expect an Err when assigning numeric literal to Bool-annotated let
        assert (actual instanceof Result.Err);
    }

    @Test
    void interpretSingleParamFunctionReturnsArg() {
        assertInterpretsTo("fn pass(value : I32) => value; pass(3)", "3");
    }

    @Test
    void interpretLetInitializedFromZeroArgFunctionReturnsZero() {
        assertInterpretsTo("fn get() => 0; let x = get(); x", "0");
    }

    @Test
    void interpretIfTrueReturnsThenBranch() {
        assertInterpretsTo("fn cond() => true; if (cond()) 3 else 5", "3");
    }
}
