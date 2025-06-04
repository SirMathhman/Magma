package magma;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateDiagramTest {
    @Test
    public void testDiagramCreatedWithExpectedContent() throws Exception {
        Path tempDir = Files.createTempDirectory("diagram_test");
        Path output = tempDir.resolve("diagram.puml");
        GenerateDiagram.writeDiagram(output);
        assertTrue(Files.exists(output), "diagram.puml was not created");
        String content = Files.readString(output);
        String expected = "@startuml\nBob -> Alice : hello\n@enduml\n";
        assertEquals(expected, content);
    }

    @Test
    public void testReadSelfContainsClassName() throws Exception {
        String source = GenerateDiagram.readSelf();
        String classDecl = "class " + GenerateDiagram.class.getSimpleName();
        assertTrue(source.contains(classDecl),
                "Source should contain its own class declaration");
        assertTrue(GenerateDiagram.hasClassDeclaration(),
                "hasClassDeclaration should return true");
    }
}
