package magma.app.rule;

import magma.CompileError;
import magma.Context;
import magma.NodeContext;
import magma.StringContext;
import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.node.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record OrRule(List<Rule> rules) implements Rule {
    private record State<T>(Optional<T> maybeValue, List<CompileError> errors) {
        public State() {
            this(Optional.empty(), new ArrayList<>());
        }

        public State<T> withValue(T node) {
            return new State<>(Optional.of(node), this.errors);
        }

        public State<T> withError(CompileError error) {
            this.errors.add(error);
            return this;
        }

        public Result<T, CompileError> toResult(Context context) {
            return this.maybeValue.<Result<T, CompileError>>map(Ok::new)
                    .orElseGet(() -> new Err<>(new CompileError("No combination present", context)));
        }
    }

    @Override
    public Result<Node, CompileError> lex(String input) {
        return this.or(rule1 -> rule1.lex(input), new StringContext(input));
    }

    private <Value> Result<Value, CompileError> or(Function<Rule, Result<Value, CompileError>> mapper, Context context) {
        return this.rules.stream()
                .reduce(new State<Value>(), (state, rule) -> mapper.apply(rule)
                        .match(state::withValue, state::withError), (_, next) -> next)
                .toResult(context);
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return this.or(rule1 -> rule1.generate(node), new NodeContext(node));
    }
}
