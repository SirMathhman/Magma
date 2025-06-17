package magma.app.compile.error;

public sealed interface StringResult extends AppendableStringResult<StringResult> permits StringErr, StringOk {
    StringResult prepend(String slice);
}
