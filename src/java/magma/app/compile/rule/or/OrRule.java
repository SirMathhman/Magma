package magma.app.compile.rule.or;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactoryImpl;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.rule.Rule;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record OrRule<Node extends DisplayNode>(
        List<Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>>> rules) implements Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    @Override
    public Result<Node, FormattedError> lex(String input) {
        return this.or(rule1 -> rule1.lex(input),
                ResultFactoryImpl.create()::fromNode,
                () -> ResultFactoryImpl.create()
                        .fromStringErr("No combination present", input));
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
                ResultFactoryImpl.create()::fromString,
                () -> ResultFactoryImpl.create()
                        .fromNodeErr("No combination present", node));
    }
}
