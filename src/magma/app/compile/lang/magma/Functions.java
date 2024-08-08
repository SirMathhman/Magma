package magma.app.compile.lang.magma;

import magma.app.compile.lang.common.Blocks;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.OptionalRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.locate.Last;
import magma.app.compile.rule.locate.LocateRule;

import java.util.List;

public class Functions {
    public static final String FUNCTION = "function";
    public static final String VALUE = "value";
    public static final String DEFINITION = "definition";

    public static Rule createFunctionRule(LazyRule function, Rule definition, Rule statement, Rule value) {
        var definitionProperty = new NodeRule(DEFINITION, definition);
        var block = new NodeRule(VALUE, Blocks.createBlockRule(statement));
        var blockInBraces = new PrefixRule("{", new SuffixRule(block, "}"));
        var content = new DisjunctionRule(List.of(
                blockInBraces,
                new NodeRule(VALUE, value)
        ));

        var withContent = new LocateRule(definitionProperty, new Last(" => "), content);
        var typeRule = new TypeRule(FUNCTION, new OptionalRule(VALUE,
                withContent,
                new SuffixRule(definitionProperty, ";")
        ));
        function.set(typeRule);
        return function;
    }
}
