package magma.rule;

import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

import java.util.stream.IntStream;

public record IdentifierRule(Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> rule)
        implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> {
    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        if (IdentifierRule.isIdentifier(input)) return this.rule.lex(input);
        else
            return NodeErr.create("Not an identifier", input);
    }

    private static boolean isIdentifier(final CharSequence input) {
        return IntStream.range(0, input.length()).map(input::charAt).allMatch(Character::isLetter);
    }

    @Override
    public StringResult<FormatError> generate(final EverythingNode everythingNode) {
        return this.rule.generate(everythingNode);
    }
}
