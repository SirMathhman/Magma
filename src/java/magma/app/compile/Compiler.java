package magma.app.compile;

import magma.app.compile.error.FormattedError;
import magma.app.compile.error.StringResult;

import java.util.Map;

public interface Compiler {
    StringResult<FormattedError> compile(Map<String, String> inputs);
}
