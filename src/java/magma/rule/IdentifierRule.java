package magma.rule;

import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public record IdentifierRule(Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> rule)
        implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> {
    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        if (this.isIdentifier(input)) return this.rule.lex(input);
        else return NodeErr.create("Not an identifier", input);
    }

    private boolean isIdentifier(final String input) {
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            if (Character.isLetter(c)) continue;
            return false;
        }
        return true;
    }

    @Override
    public StringResult<FormatError> generate(final EverythingNode everythingNode) {
        return this.rule.generate(everythingNode);
    }
}
