package magma.string;

import magma.error.CompileError;
import magma.result.Result;

public interface StringResult extends Appending<StringResult> {
    Result<String, CompileError> toResult();

    StringResult prepend(String slice);
}
