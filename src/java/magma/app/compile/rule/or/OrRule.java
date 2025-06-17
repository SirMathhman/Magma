package magma.app.compile.rule.or;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.rule.Rule;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class OrRule<Node extends DisplayNode> implements Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    private final List<Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>>> rules;
    private final ResultFactory<Node, Result<Node, FormattedError>, Result<String, FormattedError>> factory;

    public OrRule(List<Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>>> rules, ResultFactory<Node, Result<Node, FormattedError>, Result<String, FormattedError>> factory) {
        this.rules = rules;
        this.factory = factory;
    }

    @Override
    public Result<Node, FormattedError> lex(String input) {
        return this.or(rule1 -> rule1.lex(input),
                this.factory::fromNode,
                () -> this.factory.fromStringErr("No combination present", input));
    }

    private <Value> Result<Value, FormattedError> or(Function<Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>>, Result<Value, FormattedError>> mapper, Function<Value, Result<Value, FormattedError>> whenPresent, Supplier<Result<Value, FormattedError>> whenEmpty) {
        return this.rules.stream()
                .<OrState<Value, FormattedError, Result<Value, FormattedError>>>reduce(new MutableOrState<>(),
                        (state, rule) -> mapper.apply(rule)
                                .match(state::withValue, state::withError),
                        (_, next) -> next)
                .maybeValue()
                .map(whenPresent)
                .orElseGet(whenEmpty);
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return this.or(rule1 -> rule1.generate(node),
                this.factory::fromString,
                () -> this.factory.fromNodeErr("No combination present", node));
    }
}
