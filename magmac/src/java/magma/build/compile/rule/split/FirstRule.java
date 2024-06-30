package magma.build.compile.rule.split;

import magma.build.compile.rule.Rule;
import magma.build.compile.rule.Rules;

public final class FirstRule extends SplitOnceRule {
    public FirstRule(Rule leftRule, String slice, Rule rightRule) {
        super(leftRule, slice, rightRule, input -> Rules.wrapIndex(input.indexOf(slice)));
    }
}