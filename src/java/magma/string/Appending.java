package magma.string;

import java.util.function.Supplier;

public interface Appending<Self> {
    Self appendSlice(String slice);

    Self tryAppendResult(Supplier<Self> other);

    Self appendResult(Self other);
}
