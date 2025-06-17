package magma.app.compile;

public sealed interface StringResult extends AppendableStringResult<StringResult>, PrependStringResult<StringResult>, AttachableToStateResult<String, FormattedError> permits StringErr, StringOk {
}
