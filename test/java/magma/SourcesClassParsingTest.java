package magma;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static magma.TestUtil.sampleSources;

import static org.junit.jupiter.api.Assertions.*;

public class SourcesClassParsingTest {

    @Test
    public void findsAllClasses() {
        Sources sources = sampleSources();
        List<String> classes = sources.findClasses();
        assertEquals(List.of("Err", "GenerateDiagram", "Ok", "Result"), classes);
    }

    @Test
    public void findsImplementations() {
        Sources sources = sampleSources();
        Map<String, List<String>> impl = sources.findImplementations();
        assertEquals(Map.of(
                "Ok", List.of("Result"),
                "Err", List.of("Result")
        ), impl);
    }
}
