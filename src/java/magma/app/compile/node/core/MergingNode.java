package magma.app.compile.node.core;

public interface MergingNode<S> {
    S merge(S other);
}