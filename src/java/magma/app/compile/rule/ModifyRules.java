package magma.app.compile.rule;

import magma.app.compile.rule.modify.PrefixModifier;
import magma.app.compile.rule.modify.StripModifier;
import magma.app.compile.rule.modify.SuffixModifier;

public class ModifyRules {
    public static <Node> Rule<Node> Prefix(String prefix, Rule<Node> rule) {
        return new ModifyRule<>(rule, new PrefixModifier(prefix));
    }

    public static <Node> Rule<Node> Strip(Rule<Node> rule) {
        return new ModifyRule<>(rule, new StripModifier());
    }

    public static <Node> Rule<Node> Suffix(Rule<Node> rule, String suffix) {
        return new ModifyRule<>(rule, new SuffixModifier(suffix));
    }
}
