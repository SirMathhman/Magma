package magma.app.compile.error;

import java.util.function.Function;

public interface StringResult<Error> extends Completable<Error, StringResult<Error>>, AttachableToState<String, Error>, Appendable<StringResult<Error>> {
    StringResult<Error> prependSlice(String slice);

    <Return> Return match(Function<String, Return> whenOk, Function<Error, Return> whenErr);
}
