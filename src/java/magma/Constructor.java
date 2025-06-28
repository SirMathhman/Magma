package magma;

public final class Constructor implements MethodHeader {
    @Override
    public String generateWithAfterName(final String afterName) {
        return "constructor " + afterName;
    }
}
