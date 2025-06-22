package magma.app.divide;

import magma.api.list.ListLike;

public interface DivideState {
    DivideState append(char c);

    boolean isLevel();

    DivideState exit();

    DivideState enter();

    DivideState advance();

    ListLike<String> toList();
}
