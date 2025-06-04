package magma;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    private String readTs(String name) {
        try {
            return Files.readString(Path.of("src/node").resolve(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        assertTrue(content.contains("interface Result\n"), "Diagram missing interface Result");
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
    public void diagramContainsSources() {
        String content = diagramContent();
        assertTrue(content.contains("class Sources\n"), "Diagram missing class Sources");
    }

    @Test
    public void diagramContainsRelation() {
        String content = diagramContent();
        assertTrue(content.contains("class Relation\n"), "Diagram missing class Relation");
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
    public void diagramOmitsGenerateDiagramResultDependency() {
        String content = diagramContent();
        assertTrue(!content.contains("GenerateDiagram --> Result\n"), "Diagram should omit dependency GenerateDiagram --> Result");
    }

    @Test
    public void diagramContainsGenerateDiagramOkDependency() {
        String content = diagramContent();
        assertTrue(content.contains("GenerateDiagram --> Ok\n"), "Diagram missing dependency GenerateDiagram --> Ok");
    }

    @Test
    public void diagramContainsGenerateDiagramErrDependency() {
        String content = diagramContent();
        assertTrue(content.contains("GenerateDiagram --> Err\n"), "Diagram missing dependency GenerateDiagram --> Err");
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

    @Test
    public void tsGenerateDiagramImportsOk() {
        String content = readTs("magma/GenerateDiagram.ts");
        assertTrue(content.contains("import { Ok } from \"./result/Ok\";"), "GenerateDiagram.ts missing import Ok");
    }

    @Test
    public void tsSourcesImportsResult() {
        String content = readTs("magma/Sources.ts");
        assertTrue(content.contains("import { Result } from \"./result/Result\";"), "Sources.ts missing import Result");
    }

    @Test
    public void tsGenerateDiagramContainsClass() {
        String content = readTs("magma/GenerateDiagram.ts");
        assertTrue(content.contains("export class GenerateDiagram {}"), "GenerateDiagram.ts missing class");
    }

    @Test
    public void tsRelationIsClass() {
        String content = readTs("magma/Relation.ts");
        assertTrue(content.contains("export class Relation {}"), "Relation.ts missing class");
    }

    @Test
    public void tsSourcesIsClass() {
        String content = readTs("magma/Sources.ts");
        assertTrue(content.contains("export class Sources {}"), "Sources.ts missing class");
    }

    @Test
    public void tsOkIsClass() {
        String content = readTs("magma/result/Ok.ts");
        assertTrue(content.contains("export class Ok {}"), "Ok.ts missing class");
    }

    @Test
    public void tsErrIsClass() {
        String content = readTs("magma/result/Err.ts");
        assertTrue(content.contains("export class Err {}"), "Err.ts missing class");
    }

    @Test
    public void tsResultIsInterface() {
        String content = readTs("magma/result/Result.ts");
        assertTrue(content.contains("export interface Result {}"), "Result.ts missing interface");
    }
}
