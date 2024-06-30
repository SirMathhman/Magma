package magma.build.compile.parse.rule.split;

import magma.build.compile.parse.rule.Rule;
import magma.build.compile.parse.rule.Rules;

public final class FirstRule extends SplitOnceRule {
    public FirstRule(Rule leftRule, String slice, Rule rightRule) {
        super(leftRule, slice, rightRule, input -> Rules.wrapIndex(input.indexOf(slice)));
    }
}