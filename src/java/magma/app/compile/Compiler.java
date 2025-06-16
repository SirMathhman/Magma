package magma.app.compile;

import java.util.Map;

public interface Compiler {
    CompileResult<String> compile(Map<String, String> inputs);
}
