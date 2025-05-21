package magma.app.compile.value;

public record ConstructionCaller(String type) implements Caller {
    public String generate() {
        return "new " + this.type;
    }

}
