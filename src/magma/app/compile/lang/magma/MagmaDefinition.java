package magma.app.compile.lang.magma;

import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.First;
import magma.app.compile.rule.Last;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringListRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

import static magma.app.compile.lang.CommonLang.MODIFIERS;

public class MagmaDefinition {
    public static final String NAME = "name";
    public static final String DEFINITION = "definition";
    public static final String TYPE = "type";

    public static Rule createRule() {
        var name = new StringRule(NAME);
        var maybeModifiers = new DisjunctionRule(List.of(
                new LocateRule(new StringListRule(MODIFIERS, " "), new Last(" "), name),
                name
        ));

        var maybeType = new DisjunctionRule(List.of(
                new LocateRule(maybeModifiers, new First(" : "), new NodeRule(TYPE, createTypeRule())),
                maybeModifiers
        ));

        return new TypeRule(DEFINITION, new DisjunctionRule(List.of(
                maybeType,
                new SuffixRule(EmptyRule.EMPTY_RULE, "()")
        )));
    }

    private static TypeRule createTypeRule() {
        return new TypeRule("symbol", new StringRule("value"));
    }
}