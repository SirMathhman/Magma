package magma.app.compile.lang.magma;

import magma.app.compile.ParamSplitter;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.First;
import magma.app.compile.rule.Last;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.NodeListRule;
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
    public static final String PARAMS = "params";

    public static Rule createRule() {
        var definition = new LazyRule();
        var name = new StringRule(NAME);
        var maybeModifiers = new DisjunctionRule(List.of(
                new LocateRule(new StringListRule(MODIFIERS, " "), new Last(" "), name),
                name
        ));

        var params = new NodeListRule(new ParamSplitter(), PARAMS, definition);
        var maybeParams = new DisjunctionRule(List.of(
                new LocateRule(maybeModifiers, new First("("), new SuffixRule(params, ")")),
                maybeModifiers
        ));

        var maybeType = new DisjunctionRule(List.of(
                new LocateRule(maybeParams, new First(" : "), new NodeRule(TYPE, createTypeRule())),
                maybeParams
        ));

        definition.set(new TypeRule(DEFINITION, new DisjunctionRule(List.of(
                maybeType,
                new SuffixRule(EmptyRule.EMPTY_RULE, "()")
        ))));
        return definition;
    }

    private static TypeRule createTypeRule() {
        return new TypeRule("symbol", new StringRule("value"));
    }
}