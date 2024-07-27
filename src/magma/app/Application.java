package magma.app;

import magma.app.io.Source;
import magma.app.io.SourceSet;
import magma.app.io.TargetSet;
import magma.app.compile.ApplicationException;
import magma.app.compile.Compiler;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Application(SourceSet sourceSet, TargetSet targetSet) {
    private static String readSafely(Source source) throws ApplicationException {
        try {
            return source.read();
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public void run() throws ApplicationException {
        var set = walkSafely().collect(Collectors.toSet());

        for (var source : set) {
            var input = readSafely(source);
            var output = new Compiler().compile(input);
            writeSafely(source, output);
        }
    }

    private void writeSafely(Source source, String output) throws ApplicationException {
        try {
            targetSet.writeTarget(source, output);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    private Stream<Source> walkSafely() throws ApplicationException {
        try {
            return sourceSet.walk();
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}