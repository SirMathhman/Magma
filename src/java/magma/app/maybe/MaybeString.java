package magma.app.maybe;

public interface MaybeString {
    String orElse(String other);

    MaybeString appendString(String other);

    MaybeString appendMaybe(MaybeString other);

    MaybeString prependString(String other);
}
