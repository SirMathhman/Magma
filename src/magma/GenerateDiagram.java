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
     * @return the contents of GenerateDiagram.java
     */
    public static String readSelf() throws IOException {
        Path self = Path.of("src/magma/GenerateDiagram.java");
        return Files.readString(self);
    }

    /**
     * Determines if the source code contains its own class declaration.
     */
    public static boolean hasClassDeclaration() throws IOException {
        return readSelf().contains("class GenerateDiagram");
    }

    public static void main(String[] args) throws IOException {
        GenerateDiagram.writeDiagram(Path.of("diagram.puml"));
    }
}
