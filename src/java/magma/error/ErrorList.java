package magma.error;

import java.util.function.Function;

public interface ErrorList<Error> {
    String join(Function<Error, String> mapper);

    ErrorList<Error> add(Error error);
}
