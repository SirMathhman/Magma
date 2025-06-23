package magma.app.compile.string;

import magma.app.compile.node.result.Matching;

public sealed interface StringResult<Error> extends Appending<StringResult<Error>>,
        Matching<String, Error>,
        Prepending<StringResult<Error>> permits
        StringOk,
        StringErr {
}
