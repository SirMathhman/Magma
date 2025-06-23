package magma.app.compile.rule;

import magma.app.compile.divide.Divide;

public record DivideRule<Node, NodeResult, StringResult>(String key,
                                                         Rule<Node, NodeResult, StringResult> rule) implements Rule<Node, NodeResult, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        return Divide.divide(input)
                .stream()
                .map(rule::lex)
                .<NodeListResult<NodeResult>>reduce(new NodeListOk<NodeResult>(),
                        NodeListResult::add,
                        (_, next) -> next)
                .toNode(key);
    }

    @Override
    public StringResult generate(final Node node) {
        return null;
    }
}
