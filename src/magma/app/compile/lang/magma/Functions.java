package magma.app.compile.lang.magma;

import magma.app.compile.lang.common.Blocks;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.OptionalRule;
import magma.app.compile.rule.locate.Last;
import magma.app.compile.rule.locate.LocateRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

public class Functions {
    public static final String FUNCTION = "function";
    public static final String VALUE = "value";
    public static final String DEFINITION = "definition";

    public static TypeRule createFunctionRule(Rule definition, Rule statement) {
        var definitionProperty = new NodeRule(DEFINITION, definition);
        var value = new NodeRule(VALUE, Blocks.createBlockRule(statement));
        var content = new PrefixRule("{", new SuffixRule(value, "}"));
        var withContent = new LocateRule(definitionProperty, new Last(" => "), content);
        return new TypeRule(FUNCTION, new OptionalRule(VALUE,
                withContent,
                new SuffixRule(definitionProperty, ";")
        ));
    }
}
