package magma;

public record Construction(Type type) implements Caller {
    @Override
    public String generate() {
        return "new " + type.generate();
    }
}
