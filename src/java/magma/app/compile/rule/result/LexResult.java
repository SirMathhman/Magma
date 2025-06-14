package magma.app.compile.rule.result;

import java.util.Optional;
import java.util.function.Function;

public interface LexResult<N> {
    LexResult<N> flatMap(Function<N, LexResult<N>> mapper);

    LexResult<N> map(Function<N, N> mapper);

    Optional<N> findValue();
}