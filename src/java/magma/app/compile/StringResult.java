package magma.app.compile;

public sealed interface StringResult<Error, Iterable> extends AppendableStringResult<StringResult<Error, Iterable>>,
        PrependStringResult<StringResult<Error, Iterable>>,
        AttachableToStateResult<Accumulator<String, Error, Iterable>> permits StringErr, StringOk {
}