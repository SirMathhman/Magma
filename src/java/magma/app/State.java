package magma.app;

import magma.app.list.ListLike;

public interface State {
    State append(char c);

    State advance();

    ListLike<String> segments();
}
