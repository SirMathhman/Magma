package magma.app.compile;

import magma.api.list.Streamable;

import java.util.function.Function;

public final class OrRule<Node, Error, NodeResult extends AttachableToStateResult<Node, Error>, StringResult extends AttachableToStateResult<String, Error>> implements Rule<Node, NodeResult, StringResult> {
    private final Streamable<Rule<Node, NodeResult, StringResult>> rules;
    private final ResultFactory<Node, Error, NodeResult, StringResult> factory;

    public OrRule(Streamable<Rule<Node, NodeResult, StringResult>> rules, ResultFactory<Node, Error, NodeResult, StringResult> factory) {
        this.rules = rules;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        return this.or(rule1 -> rule1.lex(input),
                this.factory::fromNode,
                errors -> this.factory.fromStringErrWithChildren("No combination present", input, errors));
    }

    private <Value, Result extends AttachableToStateResult<Value, Error>> Result or(Function<Rule<Node, NodeResult, StringResult>, Result> mapper, Function<Value, Result> whenOk, Function<Streamable<Error>, Result> whenErr) {
        final var reduce = this.rules.stream()
                .<Accumulator<Value, Error, Streamable<Error>>>fold(new MutableAccumulator<>(),
                        (state, rule) -> mapper.apply(rule)
                                .attachToState(state));
        return reduce.match(whenOk, whenErr);
    }

    @Override
    public StringResult generate(Node node) {
        return this.or(rule1 -> rule1.generate(node),
                this.factory::fromString,
                errors -> this.factory.fromNodeErrWithChildren("No combination present", node, errors));
    }
}
