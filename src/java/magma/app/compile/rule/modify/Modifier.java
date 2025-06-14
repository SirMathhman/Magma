package magma.app.compile.rule.modify;

import magma.app.compile.CompileError;

import java.util.Optional;

public interface Modifier {
    String generate(String value);

    Optional<String> modify(String input);

    CompileError createError(String input);
}
