package magma.util.result;

import java.util.function.Function;

/** A simple result type representing success or failure. */
public sealed interface Result<T, X> permits Ok, Err {
    boolean isOk();
    boolean isErr();

    <U> Result<U, X> mapValue(Function<? super T, ? extends U> mapper);

    <U> Result<U, X> flatMapValue(Function<? super T, Result<U, X>> mapper);

    T unwrap();
    X unwrapErr();
}
