package magma;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import magma.result.Results;
import magma.JVMPath;
import magma.Relation;
import magma.Sources;

import static org.junit.jupiter.api.Assertions.*;

public class OptionDependencyTest {

    @Test
    public void stubsDependOnSomeAndNoneNotOption() {
        List<String> files = List.of(
                Results.unwrap(JVMPath.of("src/java/magma/TypeScriptStubs.java").readString()),
                Results.unwrap(JVMPath.of("src/java/magma/option/Some.java").readString()),
                Results.unwrap(JVMPath.of("src/java/magma/option/None.java").readString()),
                Results.unwrap(JVMPath.of("src/java/magma/option/Option.java").readString())
        );
        Sources sources = new Sources(files);
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
