package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.node.TypedNode;
import magma.app.compile.rule.result.RuleResult;

import java.util.function.Function;

public final class TypeRule<Node extends TypedNode<Node> & DisplayableNode> implements Rule<Node, RuleResult<Node>, RuleResult<String>> {
    private final String type;
    private final Rule<Node, RuleResult<Node>, RuleResult<String>> rule;

    public TypeRule(String type, Rule<Node, RuleResult<Node>, RuleResult<String>> rule) {
        this.type = type;
        this.rule = rule;
    }

    @Override
    public RuleResult<Node> lex(String input) {
        RuleResult<Node> nodeRuleResult = this.rule.lex(input);
        return nodeRuleResult.<RuleResult<Node>>match(value -> new RuleResult.Ok<>(((Function<Node, Node>) node -> node.retype(this.type)).apply(value)), RuleResult.Err::new);
    }

    @Override
    public RuleResult<String> generate(Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);
        else
            return RuleResult.createFromNode("Type '" + this.type + "' not present", node);
    }
}
