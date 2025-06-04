package magma;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SourcesClassParsingTest {
    private static Sources sampleSources() {
        String result = "public interface Result {}";
        String ok = "public class Ok implements Result {}";
        String err = "public class Err implements Result {}";
        String gen = "public class GenerateDiagram { Ok ok; Err err; }";
        return new Sources(List.of(result, ok, err, gen));
    }

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
        assertEquals(List.of("Result"), impl.get("Ok"));
        assertEquals(List.of("Result"), impl.get("Err"));
    }
}
