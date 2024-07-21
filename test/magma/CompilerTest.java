package magma;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void packageName(String name) throws CompileException {
        var value = Compiler.compile(renderPackageStatement(name));
        assertEquals("", value);
    }

    private static String renderPackageStatement(String name) {
        return Compiler.PACKAGE_KEYWORD_WITH_SPACE + name + ";";
    }
}