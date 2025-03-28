package magma.app.compile.rule;

import magma.api.collect.List_;
import magma.api.result.Result;
import magma.api.result.Tuple;
import magma.app.compile.CompileError;
import magma.app.compile.ParseState;

public record OrRule(List_<Rule> rules) implements Rule {
    @Override
    public Result<Tuple<String, String>, CompileError> apply(ParseState state, String input) {
        return rules().stream().foldWithInitial(new OrState(), (orState, rule) -> {
            if (orState.isPresent()) return orState;
            return rule.apply(state, input).match(orState::withValue, orState::withErr);
        }).toResult().mapErr(errors -> new CompileError("No valid combinations", input, errors));
    }
}