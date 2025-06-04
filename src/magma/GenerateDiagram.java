package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenerateDiagram {
    public static void writeDiagram(Path output) throws IOException {
        String content = "@startuml\nBob -> Alice : hello\n@enduml\n";
        Files.writeString(output, content);
    }

    public static void main(String[] args) throws IOException {
        GenerateDiagram.writeDiagram(Path.of("diagram.puml"));
    }
}
