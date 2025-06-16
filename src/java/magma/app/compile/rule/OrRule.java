package magma.app.compile.rule;

import magma.app.compile.error.AttachableToState;
import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;

import java.util.List;

public final class OrRule<Node, Error, NodeResult extends AttachableToState<Node, Error>, StringResult extends AttachableToState<String, Error>> implements Rule<Node, NodeResult, StringResult> {
    private final List<Rule<Node, NodeResult, StringResult>> rules;
    private final CompileResultFactory<Node, Error, StringResult, NodeResult, NodeListResult<Node, NodeResult>> factory;

    public OrRule(List<Rule<Node, NodeResult, StringResult>> rules, CompileResultFactory<Node, Error, StringResult, NodeResult, NodeListResult<Node, NodeResult>> factory) {
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
    public StringResult generate(Node node) {
        return this.rules.stream()
                .<OrState<String, Error>>reduce(new MutableOrState<>(), (nodeState, rule) -> rule.generate(node)
                        .attachToState(nodeState), (_, next) -> next)
                .toResult()
                .match(this.factory::fromString, errors -> this.factory.fromNodeErrorWithChildren("Invalid combination", node, errors));
    }
}
