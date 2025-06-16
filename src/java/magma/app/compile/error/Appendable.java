package magma.app.compile.error;

import java.util.function.Supplier;

public interface Appendable<Self> {
    Self appendResult(Supplier<Self> other);

    Self appendSlice(String slice);
}