package magma.app.compile.divide;

import magma.api.collect.list.ListLike;

public interface DivideState {
    DivideState append(char c);

    DivideState advance();

    ListLike<String> segments();
}
