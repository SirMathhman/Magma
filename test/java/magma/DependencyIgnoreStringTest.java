package magma;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DependencyIgnoreStringTest {
    @Test
    public void ignoresStringLiteralReferences() {
        String main = "public class Main {}";
        String util = "public class Util { String s = \"Main.main([])\"; }";

        Sources sources = new Sources(List.of(main, util));
        List<String> classes = sources.findClasses();
        Map<String, List<String>> impl = sources.findImplementations();
        List<Relation> relations = sources.findRelations(classes, impl);

        assertFalse(relations.contains(new Relation("Util", "-->", "Main")));
    }
}
