package magma.app.maybe;

public interface MaybeString extends Appendable<MaybeString>, Prependable<MaybeString> {
    String orElse(String other);
}
