package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
    @Test
    void interpretEmptyInputReturnsOkEmpty() {
        TestHelper.assertInterpretsTo("", "");
    }

    // use shared TestHelper.assertInterpretsTo

    @Test
    void interpretEmptyQuotedStringLiteralReturnsItself() {
        TestHelper.assertInterpretsTo("\"\"", "\"\"");
    }

    @Test
    void interpretPassCallReturnsQuotedArgument() {
        TestHelper.assertInterpretsTo("fn pass(str : *[U8]) => str; pass(\"\")", "\"\"");
    }

    @Test
    void interpretZeroReturnsZero() {
        TestHelper.assertInterpretsTo("0", "0");
    }

    @Test
    void interpretLetAssignmentReturnsZero() {
        TestHelper.assertInterpretsTo("let x = 0; x", "0");
    }

    @Test
    void interpretTrueReturnsTrue() {
        TestHelper.assertInterpretsTo("true", "true");
    }

    @Test
    void interpretTypedLetAssignmentReturnsZero() {
        TestHelper.assertInterpretsTo("let x : I32 = 0; x", "0");
    }

    @Test
    void interpretTypedU8LetCharReturnsAscii() {
        TestHelper.assertInterpretsTo("let x : U8 = 'a'; x", "97");
    }

    @Test
    void interpretDuplicateLetDeclarationsProduceErr() {
    TestHelper.assertInterpretsToErr("let x = 0; let x = 0;");
    }

    @Test
    void interpretTypedBoolAssignmentWithNumberProducesErr() {
    TestHelper.assertInterpretsToErr("let x : Bool = 0;");
    }

    @Test
    void interpretSingleParamFunctionReturnsArg() {
        TestHelper.assertInterpretsTo("fn pass(value : I32) => value; pass(3)", "3");
    }

    @Test
    void interpretSingleParamFunctionBoolArgReturnsArg() {
        TestHelper.assertInterpretsTo("fn pass(value : Bool) => value; pass(true)", "true");
    }

    @Test
    void interpretLetInitializedFromZeroArgFunctionReturnsZero() {
        TestHelper.assertInterpretsTo("fn get() => 0; let x = get(); x", "0");
    }

    @Test
    void interpretIfTrueReturnsThenBranch() {
        TestHelper.assertInterpretsTo("fn cond() => true; if (cond()) 3 else 5", "3");
    }

    @Test
    void interpretGreaterThanReturnsTrue() {
        TestHelper.assertInterpretsTo("5 > 3", "true");
    }

    @Test
    void interpretGreaterThanReturnsFalse() {
        TestHelper.assertInterpretsTo("3 > 5", "false");
    }

    @Test
    void interpretCharLiteralReturnsAscii() {
        // 'I' has ASCII code 73
        TestHelper.assertInterpretsTo("'I'", "73");
    }

    @Test
    void interpretCharPlusOneReturnsNextChar() {
        TestHelper.assertInterpretsTo("'a' + 1", "'b'");
    }
}
