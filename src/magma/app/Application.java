package magma.app;

import magma.api.Result;
import magma.api.Results;
import magma.app.compile.CompileException;
import magma.app.compile.Compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record Application(Path source) {
    public static final String EXTENSION_SEPARATOR = ".";
    public static final Path ROOT_DIRECTORY = Paths.get(".");

    public void run() throws ApplicationException {
        try {
            if (!Files.exists(source)) return;

            var input = Files.readString(source);
            Result<String, CompileException> stringCompileExceptionResult = Compiler.compile(input);
            var output = Results.unwrap(stringCompileExceptionResult);

            var fileName = source.getFileName().toString();
            var separator = fileName.lastIndexOf('.');
            var name = fileName.substring(0, separator);
            var target = ROOT_DIRECTORY.resolve(name + EXTENSION_SEPARATOR + "mgs");
            Files.writeString(target, output);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

}