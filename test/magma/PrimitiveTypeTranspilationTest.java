package magma;

import magma.compile.CompileState;
import magma.ast.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrimitiveTypeTranspilationTest {
    @Test
    void intMapsToNumber() {
        Type type = Parser.parseType("int", new CompileState());
        assertEquals("number", type.generate());
    }

    @Test
    void doubleMapsToNumber() {
        Type type = Parser.parseType("double", new CompileState());
        assertEquals("number", type.generate());
    }

    @Test
    void booleanMapsToBoolean() {
        Type type = Parser.parseType("boolean", new CompileState());
        assertEquals("boolean", type.generate());
    }

    @Test
    void charMapsToString() {
        Type type = Parser.parseType("char", new CompileState());
        assertEquals("string", type.generate());
    }
}
