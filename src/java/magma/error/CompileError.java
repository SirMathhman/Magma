package magma.error;

import magma.string.Strings;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record CompileError(String message, String input, List<CompileError> errors) implements FormatError {
    public CompileError(final String message, final String input) {
        this(message, input, Collections.emptyList());
    }

    @Override
    public String format(final int depth) {
        final var joined = this.errors.stream()
                                      .map(compileError -> compileError.format(depth + 1))
                                      .map(value -> Strings.LINE_SEPARATOR + "\t".repeat(depth) + value)
                                      .collect(Collectors.joining());

        return this.message + ": " + this.input + joined;
    }
}
