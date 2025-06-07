package magma;

public record Symbol(String value) implements Value, Type {
    @Override
    public String generate() {
        return value;
    }

}
