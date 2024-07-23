package magma;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Result<T, E extends Exception> {
    T unwrap() throws E;

    <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> mapper);

    <R> Result<R, E> mapValue(Function<T, R> mapper);
}
