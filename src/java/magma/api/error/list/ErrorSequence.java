package magma.api.error.list;

import java.util.function.Function;

public interface ErrorSequence<Error> {
    String join(Function<Error, String> mapper);
}
