package magma.app.compile.error.string;

import magma.app.compile.error.AppendableStringResult;
import magma.app.compile.error.AttachableToStateResult;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.PrependStringResult;

public sealed interface StringResult extends AppendableStringResult<StringResult>, PrependStringResult<StringResult>, AttachableToStateResult<String, FormattedError> permits StringErr, StringOk {
}
