package magma.string;

import magma.error.FormattedError;
import magma.node.result.Matching;
import magma.result.Result;

public interface StringResult extends Appending<StringResult>, Matching<String> {
    Result<String, FormattedError> toResult();

    StringResult prepend(String slice);
}
