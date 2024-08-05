package magma.app.compile.lang.magma;

import magma.app.compile.ParamSplitter;
import magma.app.compile.lang.CommonLang;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.First;
import magma.app.compile.rule.Last;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.OptionalRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import static magma.app.compile.lang.CommonLang.MODIFIERS;

public class MagmaDefinition {
    public static final String NAME = "name";
    public static final String DEFINITION = "definition";
    public static final String TYPE = "type";
    public static final String PARAMS = "params";

    public static Rule createRule() {
        var definition = new LazyRule();

        var modifiers = CommonLang.createModifiersRule();
        var name = new StringRule(NAME);
        var params = new NodeListRule(new ParamSplitter(), PARAMS, definition);
        var type = new NodeRule(TYPE, createTypeRule());

        var maybeModifiers = new OptionalRule(MODIFIERS,
                new LocateRule(modifiers, new Last(" "), name),
                name
        );

        var maybeParams = new OptionalRule(PARAMS,
                new LocateRule(maybeModifiers, new First("("), new SuffixRule(params, ")")),
                maybeModifiers
        );

        var maybeType = new OptionalRule(TYPE,
                new LocateRule(maybeParams, new First(" : "), type),
                maybeParams
        );

        var maybeName = new OptionalRule(NAME,
                maybeType,
                new SuffixRule(EmptyRule.EMPTY_RULE, "()")
        );

        definition.set(new TypeRule(DEFINITION, maybeName));
        return definition;
    }

    private static TypeRule createTypeRule() {
        return new TypeRule("symbol", new StringRule("value"));
    }
}