package magma.app.compile.merge;

public interface MergeNode<Self> {
    Self merge(Self other);
}
