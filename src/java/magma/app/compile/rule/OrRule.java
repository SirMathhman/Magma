package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;

import java.util.List;

public final class OrRule<Node, Error, R extends Rule<Node, Error>> implements Rule<Node, Error> {
    private final List<R> rules;
    private final CompileResultFactory<Node, Error, StringResult<Error>, NodeResult<Node, Error>, NodeListResult<Node, Error>> factory;

    public OrRule(List<R> rules, CompileResultFactory<Node, Error, StringResult<Error>, NodeResult<Node, Error>, NodeListResult<Node, Error>> factory) {
        this.rules = rules;
        this.factory = factory;
    }

    @Override
    public NodeResult<Node, Error> lex(String input) {
        return this.rules.stream()
                .<OrState<Node, Error>>reduce(new MutableOrState<>(), (nodeState, rule) -> rule.lex(input)
                        .attachToState(nodeState), (_, next) -> next)
                .toResult()
                .match(this.factory::fromNode, errors -> this.factory.fromStringErrorWithChildren("Invalid combination", input, errors));
    }

    @Override
    public StringResult<Error> generate(Node node) {
        return this.rules.stream()
                .<OrState<String, Error>>reduce(new MutableOrState<>(), (nodeState, rule) -> rule.generate(node)
                        .attachToState(nodeState), (_, next) -> next)
                .toResult()
                .match(this.factory::fromString, errors -> this.factory.fromNodeErrorWithChildren("Invalid combination", node, errors));
    }
}
