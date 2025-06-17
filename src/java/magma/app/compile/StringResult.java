package magma.app.compile;

public interface StringResult<Error, Result> extends AppendableStringResult<StringResult<Error, magma.api.result.Result<String, Error>>>, PrependStringResult<StringResult<Error, magma.api.result.Result<String, Error>>>, AttachableToStateResult<String, Error> {
    Result toResult();
}
