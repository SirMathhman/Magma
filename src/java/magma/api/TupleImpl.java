package magma.api;

public record TupleImpl<Left, Right>(Left left, Right right) implements Tuple<Left, Right> {
    @Override
    public Left left() {
        return this.left;
    }

    @Override
    public Right right() {
        return this.right;
    }
}
