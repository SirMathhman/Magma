package magma.app.compile.rule.or;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.rule.Rule;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class OrRule<Node extends DisplayNode, Error, StringResult> implements Rule<Node, Result<Node, Error>, StringResult> {
    private final List<Rule<Node, Result<Node, Error>, StringResult>> rules;
    private final ResultFactory<Node, Result<Node, Error>, StringResult> factory;

    public OrRule(List<Rule<Node, Result<Node, Error>, StringResult>> rules, ResultFactory<Node, Result<Node, Error>, StringResult> factory) {
        this.rules = rules;
        this.factory = factory;
    }

    @Override
    public Result<Node, Error> lex(String input) {
        return this.or(rule1 -> rule1.lex(input),
                this.factory::fromNode,
                () -> this.factory.fromStringErr("No combination present", input));
    }

    private <Value, Return> Return or(Function<Rule<Node, Result<Node, Error>, StringResult>, Return> mapper, Function<Value, Return> whenPresent, Supplier<Return> whenEmpty) {
        return this.rules.stream()
                .<OrState<Value, Error, Result<Value, Error>>>reduce(new MutableOrState<>(), (state, rule) -> {
                    return switch ((Result<Value, Error>) mapper.apply(rule)) {
                        case Ok(Value value) -> state.withValue(value);
                        case Err(Error error) -> state.withError(error);
                        default -> null;
                    };
                }, (_, next) -> next)
                .maybeValue()
                .map(whenPresent)
                .orElseGet(whenEmpty);
    }

    @Override
    public StringResult generate(Node node) {
        return this.or(rule1 -> rule1.generate(node),
                this.factory::fromString,
                () -> this.factory.fromNodeErr("No combination present", node));
    }
}
