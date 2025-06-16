package magma.app.compile.error;

import java.util.function.Function;

public interface Completable<Error, Self> {
    Self complete(Function<String, String> mapper);
}
