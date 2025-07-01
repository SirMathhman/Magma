package magma.rule;

import magma.compile.result.ResultFactory;
import magma.error.FormatError;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

import java.util.stream.IntStream;

public final class IdentifierRule<Node> implements Rule<Node, NodeResult<Node>, StringResult<FormatError>> {
    private final Rule<Node, NodeResult<Node>, StringResult<FormatError>> rule;
    private final ResultFactory<Node, StringResult<FormatError>> factory;

    public IdentifierRule(final Rule<Node, NodeResult<Node>, StringResult<FormatError>> rule,
                          final ResultFactory<Node, StringResult<FormatError>> factory) {
        this.rule = rule;
        this.factory = factory;
    }

    private static boolean isIdentifier(final CharSequence input) {
        return IntStream.range(0, input.length()).map(input::charAt).allMatch(Character::isLetter);
    }

    @Override
    public NodeResult<Node> lex(final String input) {
        if (IdentifierRule.isIdentifier(input)) return this.rule.lex(input);
        else
            return this.factory.createNodeError("Not an identifier", input);
    }

    @Override
    public StringResult<FormatError> generate(final Node node) {
        return this.rule.generate(node);
    }
}
