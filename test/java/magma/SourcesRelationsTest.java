package magma;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static magma.TestUtil.sampleSources;

import static org.junit.jupiter.api.Assertions.*;

public class SourcesRelationsTest {

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
