package magma;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import magma.Err;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateDiagramTest {
    private Path createOutput() {
        try {
            Path dir = Files.createTempDirectory("diagram_test");
            return dir.resolve("diagram.puml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<IOException> writeDiagram(Path output) {
        return GenerateDiagram.writeDiagram(output);
    }

    private String readContent(Path output) {
        try {
            return Files.readString(output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path createDiagram() {
        Path output = createOutput();
        Optional<IOException> result = writeDiagram(output);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }
        return output;
    }

    private String diagramContent() {
        return readContent(createDiagram());
    }

    @Test
    public void diagramWriteSucceeds() {
        Path output = createOutput();
        Optional<IOException> result = writeDiagram(output);
        String message = result.isEmpty() ? "" : result.get().getMessage();
        assertTrue(result.isEmpty(), "writeDiagram failed: " + message);
    }

    @Test
    public void diagramFileCreated() {
        Path output = createDiagram();
        assertTrue(Files.exists(output), "diagram.puml was not created");
    }

    @Test
    public void diagramStartsCorrectly() {
        String content = diagramContent();
        assertTrue(content.startsWith("@startuml\n"), "diagram should start correctly");
    }

    @Test
    public void diagramEndsCorrectly() {
        String content = diagramContent();
        assertTrue(content.endsWith("@enduml\n"), "diagram should end correctly");
    }

    @Test
    public void diagramContainsGenerateDiagram() {
        String content = diagramContent();
        assertTrue(content.contains("class GenerateDiagram\n"), "Diagram missing class GenerateDiagram");
    }

    @Test
    public void diagramContainsResult() {
        String content = diagramContent();
        assertTrue(content.contains("class Result\n"), "Diagram missing class Result");
    }

    @Test
    public void diagramContainsOk() {
        String content = diagramContent();
        assertTrue(content.contains("class Ok\n"), "Diagram missing class Ok");
    }

    @Test
    public void diagramContainsErr() {
        String content = diagramContent();
        assertTrue(content.contains("class Err\n"), "Diagram missing class Err");
    }

    @Test
    public void diagramContainsOkRelation() {
        String content = diagramContent();
        assertTrue(content.contains("Ok --|> Result\n"), "Diagram missing relation Ok --|> Result");
    }

    @Test
    public void diagramContainsErrRelation() {
        String content = diagramContent();
        assertTrue(content.contains("Err --|> Result\n"), "Diagram missing relation Err --|> Result");
    }

    @Test
    public void diagramContainsGenerateDiagramResultDependency() {
        String content = diagramContent();
        assertTrue(content.contains("GenerateDiagram --> Result\n"), "Diagram missing dependency GenerateDiagram --> Result");
    }

    @Test
    public void diagramOmitsGenerateDiagramOkDependency() {
        String content = diagramContent();
        assertTrue(!content.contains("GenerateDiagram --> Ok\n"), "Diagram should omit dependency GenerateDiagram --> Ok");
    }

    @Test
    public void diagramOmitsGenerateDiagramErrDependency() {
        String content = diagramContent();
        assertTrue(!content.contains("GenerateDiagram --> Err\n"), "Diagram should omit dependency GenerateDiagram --> Err");
    }

    @Test
    public void diagramDoesNotContainOkErrDependency() {
        String content = diagramContent();
        assertTrue(!content.contains("Ok --> Err\n"), "Comments referencing Err should not create Ok --> Err");
    }

    @Test
    public void diagramDoesNotContainErrOkDependency() {
        String content = diagramContent();
        assertTrue(!content.contains("Err --> Ok\n"), "Comments referencing Ok should not create Err --> Ok");
    }
}
