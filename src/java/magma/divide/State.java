package magma.divide;

import magma.list.ListLike;

public interface State {
    State append(char c);

    State advance();

    ListLike<String> unwrap();

    boolean isLevel();

    State enter();

    State exit();

    boolean isShallow();
}
