package magma.app;

import magma.app.string.Appendable;
import magma.app.string.Prependable;

public interface StringResult<Error> extends Appendable<StringResult<Error>>, Prependable<StringResult<Error>>, AttachableToOrState<String, Error> {
    ApplicationResult toApplicationResult();
}
