package magma.rule;

import magma.compile.result.ResultFactory;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

import java.util.stream.IntStream;

public final class IdentifierRule<Node, Error> implements Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>> {
    private final Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>> rule;
    private final ResultFactory<Node, Error, StringResult<Error>> factory;

    public IdentifierRule(final Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>> rule,
                          final ResultFactory<Node, Error, StringResult<Error>> factory) {
        this.rule = rule;
        this.factory = factory;
    }

    private static boolean isIdentifier(final CharSequence input) {
        return IntStream.range(0, input.length()).map(input::charAt).allMatch(Character::isLetter);
    }

    @Override
    public NodeResult<Node, Error, StringResult<Error>> lex(final String input) {
        if (IdentifierRule.isIdentifier(input)) return this.rule.lex(input);
        else
            return this.factory.createNodeError("Not an identifier", input);
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        return this.rule.generate(node);
    }
}
