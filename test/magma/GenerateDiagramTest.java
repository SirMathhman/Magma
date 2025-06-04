package magma;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;



import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateDiagramTest {
    @Test
    public void testDiagramCreatedWithExpectedContent() {
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory("diagram_test");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path output = tempDir.resolve("diagram.puml");
        Result<Void, IOException> result = GenerateDiagram.writeDiagram(output);
        String message = result.isErr() ? ((Err<Void, IOException>) result).error().getMessage() : "";
        assertTrue(result.isOk(), "writeDiagram failed: " + message);
        boolean exists;
        String content;
        try {
            exists = Files.exists(output);
            content = Files.readString(output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(exists, "diagram.puml was not created");
        assertTrue(content.startsWith("@startuml\n"), "diagram should start correctly");
        assertTrue(content.endsWith("@enduml\n"), "diagram should end correctly");
        String[] expectedClasses = {"GenerateDiagram", "Result", "Ok", "Err"};
        for (String cls : expectedClasses) {
            assertTrue(content.contains("class " + cls + "\n"),
                    "Diagram missing class " + cls);
        }
        String[] expectedRelations = {"Ok --|> Result", "Err --|> Result"};
        for (String rel : expectedRelations) {
            assertTrue(content.contains(rel + "\n"),
                    "Diagram missing relation " + rel);
        }
    }

}
