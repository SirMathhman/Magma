package magma.app.compile.rule.modify;

import java.util.Optional;

public interface Modifier {
    String complete(String value);

    Optional<String> truncate(String input);
}
