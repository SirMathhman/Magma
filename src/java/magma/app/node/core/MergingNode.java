package magma.app.node.core;

public interface MergingNode<S> {
    S merge(S other);
}