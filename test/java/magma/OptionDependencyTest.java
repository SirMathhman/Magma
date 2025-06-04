package magma;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import magma.result.Result;

import static org.junit.jupiter.api.Assertions.*;

public class OptionDependencyTest {

    @Test
    public void stubsDependOnSomeAndNoneNotOption() throws IOException {
        Result<List<String>, IOException> res = Sources.read(Path.of("src/java"));
        assertTrue(res.isOk(), "reading sources failed");
        Sources sources = new Sources(res.unwrap());
        List<String> classes = sources.findClasses();
        Map<String, List<String>> impl = sources.findImplementations();
        List<Relation> all = sources.findRelations(classes, impl);
        Set<Relation> tsDeps = all.stream()
                .filter(r -> r.from().equals("TypeScriptStubs"))
                .collect(Collectors.toSet());
        assertTrue(tsDeps.contains(new Relation("TypeScriptStubs", "-->", "Some")));
        assertTrue(tsDeps.contains(new Relation("TypeScriptStubs", "-->", "None")));
        assertFalse(tsDeps.contains(new Relation("TypeScriptStubs", "-->", "Option")));
    }
}
