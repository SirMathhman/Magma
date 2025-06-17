package magma.app.compile;

public interface MergeNode<Self> {
    Self merge(Self other);
}
