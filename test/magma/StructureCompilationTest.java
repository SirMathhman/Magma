package magma;

import magma.compile.CompileState;
import magma.util.Joiner;
import magma.util.Option;
import magma.util.Some;
import magma.util.Tuple;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class StructureCompilationTest {
    private static String compileStructure(String input, String keyword, String target) throws Exception {
        Method m = Main.class.getDeclaredMethod("compileStructure", String.class, String.class, String.class, CompileState.class);
        m.setAccessible(true);
        CompileState state = new CompileState();
        Option<?> res = (Option<?>) m.invoke(null, input, keyword, target, state);
        assertTrue(res instanceof Some<?>, "expected Some result");
        @SuppressWarnings("unchecked")
        Tuple<String, CompileState> tuple = (Tuple<String, CompileState>) ((Some<?>) res).get();
        return tuple.left() + tuple.right().structures.iter().collect(new Joiner()).orElse("");
    }

    @Test
    void compilesRecord() throws Exception {
        String output = compileStructure("public record R(int x) {}", "record ", "class");
        assertTrue(output.startsWith("export class R {"));
        assertTrue(output.contains("x: int;"));
        assertTrue(output.contains("constructor (x: int)"));
    }

    @Test
    void compilesInterface() throws Exception {
        String output = compileStructure("public interface I {}", "interface ", "interface");
        assertEquals("export interface I {\n}\n", output);
    }

    @Test
    void implementsRelationshipPreserved() throws Exception {
        String output = compileStructure("public class A implements I { int x; }", "class ", "class");
        assertTrue(output.startsWith("export class A implements I {"));
        assertTrue(output.contains("x: int;"));
    }
}
