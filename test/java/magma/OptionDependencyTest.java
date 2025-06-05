package magma;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import magma.result.Result;

import static org.junit.jupiter.api.Assertions.*;

public class OptionDependencyTest {

    private Set<Relation> tsDeps() {
        Result<List<String>, IOException> res = Sources.read(JVMPath.of("src/java"));
        if (res.isErr()) {
            throw new RuntimeException(((magma.result.Err<List<String>, IOException>) res).error());
        }
        Sources sources = new Sources(magma.result.Results.unwrap(res));
        List<String> classes = sources.findClasses();
        Map<String, List<String>> impl = sources.findImplementations();
        List<Relation> all = sources.findRelations(classes, impl);
        return all.stream()
                .filter(r -> r.from().equals("TypeScriptStubs"))
                .collect(Collectors.toSet());
    }

    @Test
    public void dependsOnSome() {
        Set<Relation> deps = tsDeps();
        assertTrue(deps.contains(new Relation("TypeScriptStubs", "-->", "Some")));
    }

    @Test
    public void dependsOnNone() {
        Set<Relation> deps = tsDeps();
        assertTrue(deps.contains(new Relation("TypeScriptStubs", "-->", "None")));
    }

    @Test
    public void doesNotDependOnOptionInterface() {
        Set<Relation> deps = tsDeps();
        assertFalse(deps.contains(new Relation("TypeScriptStubs", "-->", "Option")));
    }
}
