package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenerateDiagram {
    public static void writeDiagram(Path output) throws IOException {
        String content = "@startuml\nBob -> Alice : hello\n@enduml\n";
        Files.writeString(output, content);
    }

    /**
     * Reads the source code of this class.
     *
     * @return the contents of this class's source file
     */
    public static String readSelf() throws IOException {
        String fileName = GenerateDiagram.class.getSimpleName() + ".java";
        Path self = Path.of("src/magma", fileName);
        return Files.readString(self);
    }

    /**
     * Determines if the source code contains its own class declaration.
     */
    public static boolean hasClassDeclaration() throws IOException {
        String declaration = "class " + GenerateDiagram.class.getSimpleName();
        return readSelf().contains(declaration);
    }

    public static void main(String[] args) throws IOException {
        GenerateDiagram.writeDiagram(Path.of("diagram.puml"));
    }
}
