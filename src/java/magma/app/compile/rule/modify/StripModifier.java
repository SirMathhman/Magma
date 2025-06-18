package magma.app.compile.rule.modify;

import java.util.Optional;

public class StripModifier implements Modifier {
    @Override
    public String complete(String value) {
        return value;
    }

    @Override
    public Optional<String> truncate(String input) {
        return Optional.of(input.strip());
    }
}
