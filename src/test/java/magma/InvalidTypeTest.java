package magma;

import org.junit.jupiter.api.Test;

class InvalidTypeTest extends CompilerTestBase {

    @Test
    void invalidTypeEmptyAngleBrackets() {
        assertInvalid("let x : <> = 42;");
    }

    @Test
    void invalidTypeUnmatchedAngleBrackets() {
        assertInvalid("let x : List<I32 = 42;");
    }

    @Test
    void invalidPointerSyntax() {
        assertInvalid("let x : *;");
    }

    @Test
    void invalidArraySyntaxMissingType() {
        assertInvalid("let x : [; 5] = {};");
    }

    @Test
    void invalidArraySyntaxMissingSize() {
        assertInvalid("let x : [I32;] = {};");
    }

    @Test
    void invalidArraySyntaxMissingBracket() {
        assertInvalid("let x : [I32; 5 = {};");
    }
}