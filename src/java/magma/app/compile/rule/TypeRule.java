package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.node.TypedNode;
import magma.app.compile.rule.result.ResultRuleResults;
import magma.app.compile.rule.result.RuleResult;

public final class TypeRule<Node extends TypedNode<Node> & DisplayableNode> implements Rule<Node, RuleResult<Node>, RuleResult<String>> {
    private final String type;
    private final Rule<Node, RuleResult<Node>, RuleResult<String>> rule;

    public TypeRule(String type, Rule<Node, RuleResult<Node>, RuleResult<String>> rule) {
        this.type = type;
        this.rule = rule;
    }

    @Override
    public RuleResult<Node> lex(String input) {
        return this.rule.lex(input).mapValue(node -> node.retype(this.type));
    }

    @Override
    public RuleResult<String> generate(Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);
        else
            return ResultRuleResults.createFromNode("Type '" + this.type + "' not present", node);
    }
}
