package magma.app.compile.rule;

import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.rule.action.CompileResults;
import magma.app.compile.rule.modify.Modifier;
import magma.app.compile.rule.modify.PrefixModifier;
import magma.app.compile.rule.modify.StripModifier;
import magma.app.compile.rule.modify.SuffixModifier;

public final class ModifyRule<Node> implements Rule<Node, NodeResult<Node>, StringResult> {
    private final Rule<Node, NodeResult<Node>, StringResult> rule;
    private final Modifier modifier;

    public ModifyRule(Rule<Node, NodeResult<Node>, StringResult> rule, Modifier modifier) {
        this.modifier = modifier;
        this.rule = rule;
    }

    public static <Node> Rule<Node, NodeResult<Node>, StringResult> Prefix(String prefix, Rule<Node, NodeResult<Node>, StringResult> rule) {
        return new ModifyRule<>(rule, new PrefixModifier(prefix));
    }

    public static <Node> Rule<Node, NodeResult<Node>, StringResult> Strip(Rule<Node, NodeResult<Node>, StringResult> rule) {
        return new ModifyRule<>(rule, new StripModifier());
    }

    public static <Node> Rule<Node, NodeResult<Node>, StringResult> Suffix(Rule<Node, NodeResult<Node>, StringResult> rule, String suffix) {
        return new ModifyRule<>(rule, new SuffixModifier(suffix));
    }

    @Override
    public NodeResult<Node> lex(String input) {
        return this.modifier.truncate(input)
                .map(this.rule::lex)
                .orElseGet(() -> CompileResults.fromNodeError("Invalid value", input));
    }

    @Override
    public StringResult generate(Node node) {
        return this.rule.generate(node)
                .map(this.modifier::complete);
    }
}