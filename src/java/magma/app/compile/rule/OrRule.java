package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;

import java.util.List;

public final class OrRule<Node, R extends Rule<Node>> implements Rule<Node> {
    private final List<R> rules;
    private final CompileResultFactory<Node, StringResult, NodeResult<Node>, NodeListResult<Node>> factory;

    public OrRule(List<R> rules, CompileResultFactory<Node, StringResult, NodeResult<Node>, NodeListResult<Node>> factory) {
        this.rules = rules;
        this.factory = factory;
    }

    @Override
    public NodeResult<Node> lex(String input) {
        return this.rules.stream()
                .reduce(new State<Node>(), (nodeState, rule) -> rule.lex(input)
                        .attachToState(nodeState), (_, next) -> next)
                .toResult()
                .match(this.factory::fromNode, errors -> this.factory.fromStringErrorWithChildren("Invalid combination", input, errors));
    }

    @Override
    public StringResult generate(Node node) {
        return this.rules.stream()
                .reduce(new State<String>(), (nodeState, rule) -> rule.generate(node)
                        .attachToState(nodeState), (_, next) -> next)
                .toResult()
                .match(this.factory::fromString, errors -> this.factory.fromNodeErrorWithChildren("Invalid combination", node, errors));
    }
}
