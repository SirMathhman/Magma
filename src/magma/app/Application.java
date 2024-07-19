package magma.app;

import magma.app.compile.Compiler;
import magma.app.io.SourceSet;
import magma.app.io.TargetSet;
import magma.app.io.Unit;

import java.io.IOException;
import java.util.stream.Stream;

public record Application(SourceSet sourceSet, TargetSet targetSet) {
    static String readSafe(Unit unit) throws ApplicationException {
        try {
            return unit.read();
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public void run() throws ApplicationException {
        var stream = stream();
        for (var source : stream.toList()) {
            var input = readSafe(source);
            var output = Compiler.compile(input);
            targetSet().write(source, output);
        }
    }

    private Stream<Unit> stream() throws ApplicationException {
        try {
            return sourceSet().stream();
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}