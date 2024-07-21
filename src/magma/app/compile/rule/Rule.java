package magma.app.compile.rule;

import java.util.Map;
import java.util.Optional;

public interface Rule {
    Optional<Map<String, String>> parse(String input);
}
