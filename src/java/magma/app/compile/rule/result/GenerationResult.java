package magma.app.compile.rule.result;

import java.util.Optional;
import java.util.function.Function;

public interface GenerationResult {
    GenerationResult flatMap(Function<String, GenerationResult> mapper);

    GenerationResult map(Function<String, String> mapper);

    Optional<String> findValue();
}
