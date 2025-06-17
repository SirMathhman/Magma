package magma.app.compile;

public sealed interface StringResult<Error> extends AppendableStringResult<StringResult<Error>>, PrependStringResult<StringResult<Error>>, AttachableToStateResult<String, Error> permits StringErr, StringOk {
}
