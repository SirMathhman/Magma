package magma.app.compile.rule;

import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.rule.action.CompileResults;
import magma.app.compile.rule.modify.Modifier;
import magma.app.compile.rule.modify.PrefixModifier;
import magma.app.compile.rule.modify.StripModifier;
import magma.app.compile.rule.modify.SuffixModifier;

import java.util.Optional;

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

    private Optional<String> generate0(Node node) {
        return this.rule.generate(node)
                .findValue()
                .map(this.modifier::complete);
    }

    private Optional<Node> lex0(String input) {
        return this.modifier.truncate(input)
                .flatMap(input1 -> (this.rule).lex(input1)
                        .findValue());
    }

    @Override
    public NodeResult<Node> lex(String input) {
        return CompileResults.fromOptionWithString(this.lex0(input), input);
    }

    @Override
    public StringResult generate(Node node) {
        return CompileResults.fromOptionWithNode(this.generate0(node), node);
    }
}