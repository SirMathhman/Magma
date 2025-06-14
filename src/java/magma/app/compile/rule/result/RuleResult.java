package magma.app.compile.rule.result;

import java.util.Optional;
import java.util.function.Function;

public interface RuleResult<T> {
    RuleResult<T> flatMap(Function<T, RuleResult<T>> mapper);

    <R> RuleResult<R> map(Function<T, R> mapper);

    Optional<T> findValue();
}