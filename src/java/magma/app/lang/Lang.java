package magma.app.lang;

import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.NodeResult;
import magma.app.maybe.StringResult;
import magma.app.rule.InfixRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;

public class Lang {
    public static Rule<Node, NodeResult<Node>, StringResult> createDependencyRule() {
        return new SuffixRule<>(new InfixRule<>(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }

    public static Rule<Node, NodeResult<Node>, StringResult> createImportRule() {
        return new StripRule<>(new PrefixRule<>("import ", new SuffixRule<>(new InfixRule<>(new StringRule("parent"), ".", new StringRule("destination")), ";")));
    }
}
