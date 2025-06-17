package magma.app.compile.rule.or;

import magma.app.compile.error.AttachableToStateResult;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.rule.Rule;

import java.util.List;

public final class OrRule<Node, NodeResult extends AttachableToStateResult<Node>, StringResult extends AttachableToStateResult<String>> implements Rule<Node, NodeResult, StringResult> {
    private final List<Rule<Node, NodeResult, StringResult>> rules;
    private final ResultFactory<Node, NodeResult, StringResult> factory;

    public OrRule(List<Rule<Node, NodeResult, StringResult>> rules, ResultFactory<Node, NodeResult, StringResult> factory) {
        this.rules = rules;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        return this.rules.stream()
                .<OrState<Node, FormattedError>>reduce(new MutableOrState<>(),
                        (state, rule) -> rule.lex(input)
                                .attachToState(state),
                        (_, next) -> next)
                .maybeValue()
                .map(this.factory::fromNode)
                .orElseGet(() -> this.factory.fromStringErr("No combination present", input));
    }

    @Override
    public StringResult generate(Node node) {
        return this.rules.stream()
                .<OrState<String, FormattedError>>reduce(new MutableOrState<>(),
                        (state, rule) -> rule.generate(node)
                                .attachToState(state),
                        (_, next) -> next)
                .maybeValue()
                .map(this.factory::fromString)
                .orElseGet(() -> this.factory.fromNodeErr("No combination present", node));
    }

}
