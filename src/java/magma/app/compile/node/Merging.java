package magma.app.compile.node;

public interface Merging<Self> {
    Self merge(Self other);
}
