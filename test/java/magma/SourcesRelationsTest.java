package magma;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SourcesRelationsTest {
    private static Sources sampleSources() {
        String result = "public interface Result {}";
        String ok = "public class Ok implements Result {}";
        String err = "public class Err implements Result {}";
        String gen = "public class GenerateDiagram { Ok ok; Err err; }";
        return new Sources(List.of(result, ok, err, gen));
    }

    @Test
    public void findsRelations() {
        Sources sources = sampleSources();
        List<String> classes = sources.findClasses();
        Map<String, List<String>> impl = sources.findImplementations();
        List<Relation> relations = sources.findRelations(classes, impl);

        Set<Relation> set = Set.copyOf(relations);
        Set<Relation> expected = Set.of(
                new Relation("Ok", "--|>", "Result"),
                new Relation("Err", "--|>", "Result"),
                new Relation("GenerateDiagram", "-->", "Ok"),
                new Relation("GenerateDiagram", "-->", "Err")
        );
        assertEquals(expected, set);
    }
}
