package magma.app.compile;

import magma.app.ApplicationResult;
import magma.app.compile.string.Appending;
import magma.app.compile.string.Prepend;

public interface StringResult<Error> extends Appending<StringResult<Error>>, Prepend<StringResult<Error>>, AttachableToOrState<String, Error> {
    ApplicationResult toApplicationResult();
}
