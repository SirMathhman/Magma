package magma.app.maybe;

public interface Appendable<Self> {
    Self appendString(String other);

    Self appendMaybe(Self other);
}
