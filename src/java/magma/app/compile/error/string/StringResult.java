package magma.app.compile.error.string;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface StringResult permits StringErr, StringOk {
    @Deprecated
    Optional<String> findValue();

    StringResult appendSlice(String suffix);

    StringResult appendResult(Supplier<StringResult> other);

    StringResult prependSlice(String prefix);

    StringResult map(Function<String, String> mapper);
}
