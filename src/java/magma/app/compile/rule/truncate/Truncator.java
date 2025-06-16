package magma.app.compile.rule.truncate;

import java.util.Optional;

public interface Truncator {
    Optional<String> truncate(String input);

    String complete(String result);

    String createErrorMessage();
}
