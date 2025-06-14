package magma.app.rule.result;

import java.util.Optional;
import java.util.function.Function;

public interface LexResult<N, S extends LexResult<N, S>> {
    S flatMap(Function<N, S> mapper);

    S mapValue(Function<N, N> mapper);

    Optional<N> findValue();
}
