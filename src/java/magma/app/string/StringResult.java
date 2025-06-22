package magma.app.string;

import magma.app.node.result.Matching;

public sealed interface StringResult<Error> extends Appending<StringResult<Error>>, Matching<String, Error> permits
        StringOk,
        StringErr {
    StringResult<Error> prepend(String slice);
}
