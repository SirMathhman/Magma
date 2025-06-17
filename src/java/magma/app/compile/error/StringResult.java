package magma.app.compile.error;

import java.util.function.Supplier;

public sealed interface StringResult permits StringErr, StringOk {
    StringResult appendResult(Supplier<StringResult> generate);

    StringResult prepend(String slice);

    StringResult appendSlice(String infix);
}
