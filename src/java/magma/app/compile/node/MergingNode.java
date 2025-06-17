package magma.app.compile.node;

public interface MergingNode<Self> {
    Self merge(Self other);
}
