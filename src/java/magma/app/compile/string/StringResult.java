package magma.app.compile.string;

import magma.app.compile.AppendableStringResult;
import magma.app.compile.AttachableToStateResult;
import magma.app.compile.FormattedError;
import magma.app.compile.PrependStringResult;

public sealed interface StringResult extends AppendableStringResult<StringResult>, PrependStringResult<StringResult>, AttachableToStateResult<String, FormattedError> permits StringErr, StringOk {
}
