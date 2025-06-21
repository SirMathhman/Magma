package magma.divide;

import magma.ListLike;

public interface DivideState {
    DivideState append(char c);

    DivideState advance();

    ListLike<String> segments();
}
