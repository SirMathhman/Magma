package magma;

import org.junit.jupiter.api.Test;

class InvalidImportExternTest extends CompilerTestBase {

    @Test
    void invalidImportMissingModule() {
        assertInvalid("import;");
    }

    @Test
    void invalidImportMissingSemicolon() {
        assertInvalid("import stdio");
    }

    @Test
    void invalidExternMissingFunction() {
        assertInvalid("extern;");
    }

    @Test
    void invalidExternMissingFn() {
        assertInvalid("extern printf() : Void;");
    }

    @Test
    void invalidExternMissingReturnType() {
        assertInvalid("extern fn printf();");
    }
}