package magma.app.compile.rule;

import magma.api.Result;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record RuleResult<T, E>(Result<T, E> result, List<RuleResult<T, E>> children) {
    public RuleResult(Result<T, E> result) {
        this(result, Collections.emptyList());
    }

    public boolean isValid() {
        return result.isOk();
    }

    public Optional<T> findValue() {
        return result.findValue();
    }

    public RuleResult<T, E> wrapValue(Function<T, T> mapper) {
        return new RuleResult<>(result.mapValue(mapper), children);
    }

    public RuleResult<T, E> wrapErr(Function<E, E> mapper) {
        return new RuleResult<>(result.mapErr(mapper), Collections.singletonList(this));
    }
}
