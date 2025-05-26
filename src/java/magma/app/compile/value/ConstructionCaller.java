package magma.app.compile.value;

public record ConstructionCaller(String right) implements Caller {
    public String generate() {
        return "new " + this.right;
    }
}
