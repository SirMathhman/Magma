package magma;

import magma.compile.CompileState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionalInterfaceTypeTest {
    @Test
    void parsesSupplier() {
        var type = Parser.parseType("Supplier<String>", new CompileState());
        assertEquals("() => string", type.generate());
    }

    @Test
    void parsesFunction() {
        var type = Parser.parseType("Function<String, String>", new CompileState());
        assertEquals("(param0 : string) => string", type.generate());
    }

    @Test
    void parsesBiFunction() {
        var type = Parser.parseType("BiFunction<int, int, String>", new CompileState());
        assertEquals("(param0 : number, param1 : number) => string", type.generate());
    }
}
