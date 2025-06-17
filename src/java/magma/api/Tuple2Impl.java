package magma.api;

public record Tuple2Impl<Left, Right>(Left left, Right right) implements Tuple2<Left, Right> {
}
