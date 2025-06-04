package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Optional;

import static magma.Result.err;
import static magma.Result.ok;

public class GenerateDiagram {
    public static Optional<IOException> writeDiagram(Path output) {
        try {
            String className = GenerateDiagram.class.getSimpleName();
            String content = "@startuml\nclass " + className + "\n@enduml\n";
            Files.writeString(output, content);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

    /**
     * Reads the source code of this class.
     *
     * @return the contents of this class's source file
     */
    public static Result<String, IOException> readSelf() {
        try {
            String fileName = GenerateDiagram.class.getSimpleName() + ".java";
            Path self = Path.of("src/magma", fileName);
            return ok(Files.readString(self));
        } catch (IOException e) {
            return err(e);
        }
    }

    /**
     * Determines if the source code contains its own class declaration.
     */
    public static Result<Boolean, IOException> hasClassDeclaration() {
        Result<String, IOException> source = readSelf();
        if (source.isErr()) {
            return err(((Result.Err<String, IOException>) source).error());
        }
        String declaration = "class " + GenerateDiagram.class.getSimpleName();
        String src = ((Result.Ok<String, IOException>) source).value();
        return ok(src.contains(declaration));
    }

    public static void main(String[] args) {
        writeDiagram(Path.of("diagram.puml"));
    }
}
