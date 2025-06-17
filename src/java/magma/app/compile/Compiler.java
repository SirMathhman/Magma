package magma.app.compile;

import magma.api.Error;
import magma.api.result.Result;
import magma.app.io.Source;

import java.util.Map;

public interface Compiler {
    Result<String, Error> compile(Map<Source, String> inputs);
}
