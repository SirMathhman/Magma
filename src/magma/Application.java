package magma;

import java.io.IOException;

public record Application(SourceSet sourceSet, TargetSet targetSet) {
    static String readSafe(Unit unit) throws CompileException {
        try {
            return unit.read();
        } catch (IOException e) {
            throw new CompileException(e);
        }
    }

    void run() throws CompileException {
        var stream = sourceSet().stream();
        for (var source : stream.toList()) {
            var input = readSafe(source);
            var output = Compiler.compile(input);
            targetSet().write(source, output);
        }
    }
}