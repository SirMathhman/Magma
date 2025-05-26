package magma.api;

public record Tuple2Impl<A, B>(A leftNode, B rightNode) implements Tuple2<A, B> {
    @Override
    public A left() {
        return this.leftNode;
    }

    @Override
    public B right() {
        return this.rightNode;
    }
}
