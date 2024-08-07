package magma.app.compile.lang.magma;

import magma.app.compile.ParamSplitter;
import magma.app.compile.lang.common.Modifiers;
import magma.app.compile.lang.common.Symbols;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.locate.First;
import magma.app.compile.rule.locate.Last;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.locate.LocateRule;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.OptionalRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

import static magma.app.compile.lang.common.Modifiers.MODIFIERS;

public class MagmaDefinition {
    public static final String NAME = "name";
    public static final String DEFINITION = "definition";
    public static final String TYPE = "type";
    public static final String PARAMS = "params";
    public static final String SLICE = "slice";

    public static Rule createRule() {
        var definition = new LazyRule();

        var modifiers = Modifiers.createModifiersRule();
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

    private static Rule createTypeRule() {
        var type = new LazyRule();
        type.set(new DisjunctionRule(List.of(
                new TypeRule(SLICE, new PrefixRule("Slice<", new SuffixRule(new NodeRule("child", type), ">"))),
                Symbols.createSymbolRule()
        )));
        return type;
    }
}