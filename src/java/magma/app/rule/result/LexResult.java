package magma.app.rule.result;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface LexResult<N> {
    LexResult<N> flatMap(Function<N, LexResult<N>> mapper);

    LexResult<N> merge(Supplier<LexResult<N>> other);

    LexResult<N> mapValue(Function<N, N> mapper);

    Optional<N> findValue();
}