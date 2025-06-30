package magma.rule;

import magma.node.EverythingNode;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public record StripRule(Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> rule)
        implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.rule.generate(node);
    }
}