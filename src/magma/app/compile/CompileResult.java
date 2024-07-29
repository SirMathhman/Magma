package magma.app.compile;

import magma.api.Err;
import magma.api.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public record CompileResult<T>(Result<T, CompileException> result, List<CompileResult<T>> children) {
    public CompileResult(Result<T, CompileException> result) {
        this(result, Collections.emptyList());
    }

    public boolean isValid() {
        return result.isOk();
    }

    public boolean isInvalid() {
        return result.isErr();
    }

    public <R> R match(Function<T, R> onValid, BiFunction<CompileException, List<CompileResult<T>>, R> onErr) {
        return result.match(onValid, e -> onErr.apply(e, children));
    }

    public Optional<CompileException> findError() {
        return result.findErr();
    }

    public CompileResult<T> wrapErr(Supplier<CompileException> factory) {
        return new CompileResult<T>(new Err<>(factory.get()), Collections.singletonList(this));
    }

    public CompileResult<T> and(CompileResult<T> other, BiFunction<T, T, T> merger) {
        var merged = result.and(() -> other.result).mapValue(tuple -> merger.apply(tuple.left(), tuple.right()));

        var copy = new ArrayList<>(children);
        copy.addAll(other.children);

        return new CompileResult<T>(merged, copy);
    }

    public CompileResult<T> wrapValue(Function<T, T> mapper) {
        return new CompileResult<>(result.mapValue(mapper), children);
    }
}
