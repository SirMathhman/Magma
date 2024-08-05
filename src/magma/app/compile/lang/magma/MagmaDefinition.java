package magma.app.compile.lang.magma;

import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

public class MagmaDefinition {
    public static final String DEFINITION_NAME = "name";
    public static final String DEFINITION_TYPE = "definition";

    public static Rule createRule() {
        return new TypeRule(DEFINITION_TYPE, new DisjunctionRule(List.of(
                new StringRule(DEFINITION_NAME),
                new SuffixRule(EmptyRule.EMPTY_RULE, "()")
        )));
    }
}