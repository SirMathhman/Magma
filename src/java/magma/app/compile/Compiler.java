package magma.app.compile;

import magma.app.compile.error.CompileResult;

import java.util.Map;

public interface Compiler {
    CompileResult<String> compile(Map<String, String> inputs);
}
