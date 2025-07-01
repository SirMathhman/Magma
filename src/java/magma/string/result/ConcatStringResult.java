package magma.string.result;

public interface ConcatStringResult<Self> {
    Self appendResult(Self other);

    Self appendSlice(String slice);

    Self prependSlice(String other);
}
