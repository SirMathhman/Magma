package magma.app;

import magma.app.compile.CompileResult;
import magma.app.compile.Compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class Application {
    public static final String EXTENSION_SEPARATOR = ".";
    public static final Path ROOT_DIRECTORY = Paths.get(".");
    private final SourceSet sourceSet;

    public Application(SourceSet sourceSet) {
        this.sourceSet = sourceSet;
    }

    public void run() throws ApplicationException {
        try {
            var set = sourceSet.collectSources();

            for (Path path : set) {
                var input = Files.readString(path);
                var result = Compiler.compile(input)
                        .mapValue(value1 -> writeValue(value1, path))
                        .match(value -> value, Optional::of);

                if (result.isPresent()) throw result.get();
            }
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    private Optional<ApplicationException> writeValue(CompileResult value, Path source1) {
        try {
            var fileName = source1.getFileName().toString();
            var separator = fileName.lastIndexOf('.');
            var name = fileName.substring(0, separator);

            var target = ROOT_DIRECTORY.resolve(name + EXTENSION_SEPARATOR + "mgs");
            Files.writeString(target, value.output());

            var input = ROOT_DIRECTORY.resolve(name + EXTENSION_SEPARATOR + "input.xml");
            Files.writeString(input, value.inputNode().toXML().format(0));

            var output = ROOT_DIRECTORY.resolve(name + EXTENSION_SEPARATOR + "output.xml");
            Files.writeString(output, value.outputNode().toXML().format(0));
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(new ApplicationException(e));
        }
    }
}