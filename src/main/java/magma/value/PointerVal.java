package magma.value;

public record PointerVal(String targetName, boolean mutable) implements Value {
}
