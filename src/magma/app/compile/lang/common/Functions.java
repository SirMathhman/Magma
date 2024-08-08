package magma.app.compile.lang.common;

import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.split.ParamSplitter;

public class Functions {
    public static final String PARAMS = "params";

    public static TypeRule createReturnRule(Rule value) {
        var valueProperty = new NodeRule("value", value);
        return new TypeRule("return", new StripRule(new PrefixRule("return ", new SuffixRule(valueProperty, ";"))));
    }

    public static NodeListRule createParamsRule(Rule param) {
        return new NodeListRule(new ParamSplitter(), PARAMS, param);
    }
}
