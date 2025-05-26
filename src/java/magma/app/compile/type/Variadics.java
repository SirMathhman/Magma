package magma.app.compile.type;

import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

public final class Variadics {
    public static Rule createVariadicRule(Rule typeRule) {
        return new TypeRule("variadic", new StripRule(new SuffixRule("...", new NodeRule("child", typeRule))));
    }
}
