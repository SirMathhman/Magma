package magma.app.compile.lang.common;

import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.function.Function;

public class PrefixedStatements {
    public static final String BEFORE_BLOCK = "before-block";
    public static final String AFTER_BLOCK = "after-block";
    public static final String TRY = "try";

    public static TypeRule createPrefixedStatementRule(String type, String prefix, Rule statement, Function<Rule, Rule> function) {
        var children = new NodeRule("value", Blocks.createBlockRule(Blocks.createMembersRule(statement)));
        var childrenProperty = new StripRule(new PrefixRule("{", new SuffixRule(children, "}")), BEFORE_BLOCK, AFTER_BLOCK);
        return new TypeRule(type, new PrefixRule(prefix, function.apply(childrenProperty)));
    }

    public static TypeRule createTryRule(Rule statement) {
        return createPrefixedStatementRule(TRY, "try", statement, rule -> rule);
    }
}