package magma.app.compile.lang.common;

import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.locate.First;
import magma.app.compile.rule.locate.LocateRule;

import java.util.List;

public class Definitions {
    public static TypeRule createDefinitionStatement(Rule definition) {
        return new TypeRule("definition", new SuffixRule(definition, ";"));
    }

    public static TypeRule createAssignmentRule(Rule value) {
        var assignable = new LazyRule();
        assignable.set(new DisjunctionRule(List.of(
                new StringRule("value")
        )));

        var valueProperty = new NodeRule("value", value);
        return new TypeRule("assignment", new LocateRule(assignable, new First("="), new SuffixRule(valueProperty, ";")));
    }
}
