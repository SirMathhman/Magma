package magma.app.compile;

import magma.app.compile.error.string.StringResult;

import java.util.Map;

public interface Compiler {
    StringResult compile(Map<String, String> inputs);
}
