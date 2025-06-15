package magma.app.maybe.string;

public interface Appendable<Self> {
    Self appendString(String other);

    Self appendMaybe(Self other);
}
