package magma.build.compile.parse.split;

import magma.build.compile.parse.Rule;
import magma.build.compile.parse.Rules;

public final class FirstRule extends SplitOnceRule {
    public FirstRule(Rule leftRule, String slice, Rule rightRule) {
        super(leftRule, slice, rightRule, input -> Rules.wrapIndex(input.indexOf(slice)));
    }
}