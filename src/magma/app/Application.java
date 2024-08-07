package magma.app;

import magma.app.compile.CompileResult;
import magma.app.compile.Compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

public final class Application {
    public static final String EXTENSION_SEPARATOR = ".";
    public static final Path ROOT_DIRECTORY = Paths.get(".");
    private final Path source;

    public Application(Path source) {
        this.source = source;
    }

    public void run() throws ApplicationException {
        try {
            if (!Files.exists(source)) return;

            var input = Files.readString(source);
            var result = Compiler.compile(input)
                    .mapValue(this::writeValue)
                    .match(value -> value, Optional::of);

            if (result.isPresent()) {
                throw result.get();
            }
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    private Optional<ApplicationException> writeValue(CompileResult value) {
        try {
            var fileName = source.getFileName().toString();
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

    public Path source() {
        return source;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Application) obj;
        return Objects.equals(this.source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source);
    }

    @Override
    public String toString() {
        return "Application[" +
               "source=" + source + ']';
    }


}