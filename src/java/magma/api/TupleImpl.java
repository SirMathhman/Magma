package magma.api;

public record TupleImpl<Left, Right>(Left left, Right right) implements Tuple<Left, Right> {
}
