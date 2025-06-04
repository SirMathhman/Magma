package magma;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import magma.Result;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertTrue(result.isOk(),
                "writeDiagram failed: " + (result.isErr()
                        ? ((Result.Err<Void, IOException>) result).error()
                        : ""));
        boolean exists;
        String content;
        try {
            exists = Files.exists(output);
            content = Files.readString(output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(exists, "diagram.puml was not created");
        String expected = "@startuml\nclass " +
                GenerateDiagram.class.getSimpleName() + "\n@enduml\n";
        assertEquals(expected, content);
    }

    @Test
    public void testReadSelfContainsClassName() {
        Result<String, IOException> sourceResult = GenerateDiagram.readSelf();
        assertTrue(sourceResult.isOk(), "readSelf failed");
        String source = ((Result.Ok<String, IOException>) sourceResult).value();
        String classDecl = "class " + GenerateDiagram.class.getSimpleName();
        assertTrue(source.contains(classDecl),
                "Source should contain its own class declaration");

        Result<Boolean, IOException> hasDecl = GenerateDiagram.hasClassDeclaration();
        assertTrue(hasDecl.isOk() &&
                ((Result.Ok<Boolean, IOException>) hasDecl).value(),
                "hasClassDeclaration should return true");
    }
}
