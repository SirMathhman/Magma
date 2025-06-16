package magma.app.compile.rule.truncate;

import java.util.Optional;

public interface Truncator {
    Optional<String> truncate(String input);

    String generate(String result);

    String createErrorMessage();
}
