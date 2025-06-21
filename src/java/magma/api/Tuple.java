package magma.api;

public interface Tuple<Left, Right> {
    Left left();

    Right right();
}
