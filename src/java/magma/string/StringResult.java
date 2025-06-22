package magma.string;

import magma.error.CompileError;
import magma.node.result.Matching;
import magma.result.Result;

public interface StringResult extends Appending<StringResult>, Matching<String> {
    Result<String, CompileError> toResult();

    StringResult prepend(String slice);
}
