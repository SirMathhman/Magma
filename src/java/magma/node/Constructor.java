package magma.node;

public record Constructor(String beforeName, String name) implements Header {
    @Override
    public String generate() {
        return Placeholder.generate(this.beforeName() + " ") + "struct " + this.name() + " new_" + this.name();
    }
}