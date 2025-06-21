package magma.app;

public interface State {
    State append(char c);

    State advance();

    ListLike<String> segments();
}
