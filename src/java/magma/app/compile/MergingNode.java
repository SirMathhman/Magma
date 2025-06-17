package magma.app.compile;

public interface MergingNode<Self> {
    Self merge(Self other);
}
