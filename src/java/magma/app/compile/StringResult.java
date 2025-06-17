package magma.app.compile;

import magma.api.result.Result;

public interface StringResult<Error> extends AppendableStringResult<StringResult<Error>>, PrependStringResult<StringResult<Error>>, AttachableToStateResult<String, Error> {
    Result<String, Error> toResult();
}
