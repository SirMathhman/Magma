package magma.api.result;

import java.util.function.Function;

public interface Matchable<T, X> {
    <R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
}
