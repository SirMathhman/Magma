import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.nio.file.*;

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
}
