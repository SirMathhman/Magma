package magma.app.compile;

import magma.app.io.source.Source;

import java.util.Map;

public interface Compiler {
    String compile(Map<Source, String> sourceMap);
}
