package magma.app.compile.lang.magma;

import magma.app.compile.lang.common.Blocks;
import magma.app.compile.rule.Last;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

public class Functions {
    public static final String FUNCTION_TYPE = "function";
    public static final String VALUE = "value";

    public static TypeRule createFunctionRule0(Rule definition, Rule statement) {
        var value = new NodeRule(VALUE, Blocks.createBlockRule(statement));
        var content = new PrefixRule("{", new SuffixRule(value, "}"));
        var definitionProperty = new NodeRule(MagmaDefinition.DEFINITION, definition);
        return new TypeRule(FUNCTION_TYPE, new LocateRule(definitionProperty, new Last(" => "), content));
    }
}
