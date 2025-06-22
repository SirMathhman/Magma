package magma.app.compile.string;

import magma.app.compile.node.result.Matching;

public sealed interface StringResult<Error> extends Appending<StringResult<Error>>, Matching<String, Error> permits
        StringOk,
        StringErr {
    StringResult<Error> prepend(String slice);
}
