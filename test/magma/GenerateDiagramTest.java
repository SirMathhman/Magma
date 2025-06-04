package magma;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Optional;

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
        Optional<IOException> result = GenerateDiagram.writeDiagram(output);
        assertTrue(result.isEmpty(),
                "writeDiagram failed: " + result.orElse(null));
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
    }

    @Test
    public void testReadSelfContainsClassName() {
        Result<String, IOException> sourceResult = GenerateDiagram.readSelf();
        assertTrue(sourceResult.isOk(), "readSelf failed");
        String source = ((Ok<String, IOException>) sourceResult).value();
        String classDecl = "class " + GenerateDiagram.class.getSimpleName();
        assertTrue(source.contains(classDecl),
                "Source should contain its own class declaration");

        Result<Boolean, IOException> hasDecl = GenerateDiagram.hasClassDeclaration();
        assertTrue(hasDecl.isOk() &&
                ((Ok<Boolean, IOException>) hasDecl).value(),
                "hasClassDeclaration should return true");
    }
}
