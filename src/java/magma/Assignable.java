package magma;

public interface Assignable extends MethodHeader {
    default String generate() {
        return this.generateWithAfterName("");
    }
}
