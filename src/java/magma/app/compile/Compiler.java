package magma.app.compile;

import java.util.Map;

public interface Compiler<StringRule> {
    StringRule compile(Map<String, String> inputs);
}
