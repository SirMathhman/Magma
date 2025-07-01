package magma.rule;

import magma.compile.result.ResultFactory;

import java.util.stream.IntStream;

public final class IdentifierRule<Node, Error, NodeResult, StringResult>
        implements Rule<Node, NodeResult, StringResult> {
    private final Rule<Node, NodeResult, StringResult> rule;
    private final ResultFactory<Node, Error, StringResult, NodeResult> factory;

    public IdentifierRule(final Rule<Node, NodeResult, StringResult> rule,
                          final ResultFactory<Node, Error, StringResult, NodeResult> factory) {
        this.rule = rule;
        this.factory = factory;
    }

    private static boolean isIdentifier(final CharSequence input) {
        return IntStream.range(0, input.length()).map(input::charAt).allMatch(Character::isLetter);
    }

    @Override
    public NodeResult lex(final String input) {
        if (IdentifierRule.isIdentifier(input)) return this.rule.lex(input);
        else
            return this.factory.createNodeError("Not an identifier", input);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node);
    }
}
