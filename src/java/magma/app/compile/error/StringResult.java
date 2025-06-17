package magma.app.compile.error;

public sealed interface StringResult extends AppendableStringResult<StringResult>, PrependStringResult<StringResult>, AttachableToStateResult<String> permits StringErr, StringOk {
}
