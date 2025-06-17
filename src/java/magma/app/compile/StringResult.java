package magma.app.compile;

import magma.api.collect.iter.Iterable;

public sealed interface StringResult<Error> extends AppendableStringResult<StringResult<Error>>, PrependStringResult<StringResult<Error>>, AttachableToStateResult<Accumulator<String, Error, Iterable<Error>>> permits StringErr, StringOk {
}