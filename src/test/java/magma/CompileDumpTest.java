package magma;

import magma.ast.Unit;
import magma.compiler.Compiler;
import magma.util.Ok;
import magma.util.Result;
import magma.util.Err;
import magma.diagnostics.CompileError;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

public class CompileDumpTest {
    @Test
    public void dumpArrayCase() {
        var src = TestUtils.PRELUDE + " let x : [I32; 1] = [readInt()]; x[0];";
        var compiler = new Compiler("typescript");
        var unit = new Unit(new magma.parser.Location(Collections.emptyList(), ""), ".mgs", src);
        var res = compiler.compile(Collections.singleton(unit));
        if (res instanceof Ok) {
            var set = ((Ok<Set<Unit>, CompileError>) res).value();
            for (var u : set) {
                System.out.println("Generated: " + u.location().name() + u.extension());
                System.out.println(u.input());
            }
        } else if (res instanceof Err(var e)) {
            System.out.println("Compile failed: " + e);
        }
    }
}
