package magma.app.compile.string;

public interface Appendable<Self> {
    Self appendString(String other);

    Self appendMaybe(Self other);
}
