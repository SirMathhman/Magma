package magma.app.compile.error;

import java.util.function.Function;
import java.util.function.Supplier;

public interface StringResult<Error> extends Completable<Error, StringResult<Error>>, AttachableToState<String, Error> {
    StringResult<Error> appendResult(Supplier<StringResult<Error>> other);

    StringResult<Error> prependSlice(String slice);

    StringResult<Error> appendSlice(String slice);

    <Return> Return match(Function<String, Return> whenOk, Function<Error, Return> whenErr);
}
