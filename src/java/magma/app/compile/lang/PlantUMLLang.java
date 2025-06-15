package magma.app.compile.lang;

import magma.app.compile.SimpleRule;
import magma.app.compile.rule.DivideRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;

import java.util.List;

public class PlantUMLLang {
    public static SimpleRule createDependencyRule() {
        final var source = new StringRule("source");
        final var destination = new StringRule("destination");
        return new SuffixRule(new InfixRule(source, " --> ", destination), "\n");
    }

    public static SimpleRule createPlantUMLRootRule() {
        return new DivideRule("children", createPlantUMLRootSegmentRule());
    }

    private static SimpleRule createPlantUMLRootSegmentRule() {
        return new OrRule(List.of(createDependencyRule(), new EmptyRule()));
    }
}
