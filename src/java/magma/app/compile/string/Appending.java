package magma.app.compile.string;

public interface Appending<Self> {
    Self appendString(String other);

    Self appendMaybe(Self other);
}
