package magma.app.maybe;

public interface MaybeString extends Appendable<MaybeString> {
    String orElse(String other);

    MaybeString prependString(String other);
}
