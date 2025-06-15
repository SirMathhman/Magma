package magma.app.compile;

import magma.app.ApplicationResult;
import magma.app.compile.string.Appendable;
import magma.app.compile.string.Prependable;

public interface StringResult<Error> extends Appendable<StringResult<Error>>, Prependable<StringResult<Error>>, AttachableToOrState<String, Error> {
    ApplicationResult toApplicationResult();
}
