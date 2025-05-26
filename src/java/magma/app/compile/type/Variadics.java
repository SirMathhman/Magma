package magma.app.compile.type;

import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;

public class Variadics {
    public static StripRule createVariadicRule(Rule typeRule) {
        return new StripRule(new SuffixRule("...", new NodeRule("child", typeRule)));
    }
}
