package magma.app.compile.rule;

import magma.app.compile.AttachableToOrState;
import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeResult;
import magma.app.compile.SimpleRule;
import magma.app.compile.StringResult;
import magma.app.compile.node.NodeResults;
import magma.app.compile.rule.or.InlineOrState;
import magma.app.compile.rule.or.OrState;
import magma.app.compile.string.StringResults;

import java.util.List;
import java.util.function.Function;

public record OrRule(List<SimpleRule> rules) implements SimpleRule {
    @Override
    public StringResult<CompileError> generate(Node node) {
        return this.<AttachableToOrState<String, CompileError>, String, StringResult<CompileError>>or(rule1 -> rule1.generate(node), StringResults::createFromValue, errors -> StringResults.createFromNodeAndErrors("No valid combination", node, errors));
    }

    private <MaybeValue extends AttachableToOrState<Value, CompileError>, Value, Return> Return or(Function<SimpleRule, MaybeValue> mapper, Function<Value, Return> whenPresent, Function<List<CompileError>, Return> whenMissing) {
        final var reduce = this.rules.stream()
                .map(mapper)
                .<OrState<Value, CompileError>>reduce(new InlineOrState<>(), (orState, maybeString) -> {
                    if (orState.hasValue())
                        return orState;
                    return maybeString.attachTo(orState);
                }, (_, next) -> next);
        return reduce.match(whenPresent, whenMissing);
    }

    @Override
    public NodeResult<Node, CompileError> lex(String input) {
        return this.<AttachableToOrState<Node, CompileError>, Node, NodeResult<Node, CompileError>>or(rule1 -> rule1.lex(input), NodeResults::createFromValue, errors -> NodeResults.createFromStringAndErrors("No valid combination", input, errors));
    }
}
