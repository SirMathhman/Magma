package magma.app.compile.rule;

import magma.app.compile.error.AttachableToState;
import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.StringResult;

import java.util.List;

public final class OrRule<Node, Error, NodeResult extends AttachableToState<Node, Error>> implements Rule<Node, Error, NodeResult> {
    private final List<Rule<Node, Error, NodeResult>> rules;
    private final CompileResultFactory<Node, Error, StringResult<Error>, NodeResult, NodeListResult<Node, Error>> factory;

    public OrRule(List<Rule<Node, Error, NodeResult>> rules, CompileResultFactory<Node, Error, StringResult<Error>, NodeResult, NodeListResult<Node, Error>> factory) {
        this.rules = rules;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
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
