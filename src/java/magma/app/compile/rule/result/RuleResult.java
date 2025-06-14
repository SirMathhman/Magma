package magma.app.compile.rule.result;

import java.util.Optional;
import java.util.function.Function;

public interface RuleResult<N> {
    RuleResult<N> flatMap(Function<N, RuleResult<N>> mapper);

    RuleResult<N> map(Function<N, N> mapper);

    Optional<N> findValue();
}