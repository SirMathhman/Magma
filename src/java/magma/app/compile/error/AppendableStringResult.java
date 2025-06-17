package magma.app.compile.error;

import java.util.function.Supplier;

public interface AppendableStringResult<Self> {
    Self appendResult(Supplier<Self> generate);

    Self appendSlice(String infix);
}
