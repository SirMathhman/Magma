package magma.app.compile.divide;

import magma.api.list.ListLike;

public interface DivideState {
    DivideState append(char c);

    DivideState advance();

    ListLike<String> segments();

    boolean isLevel();

    DivideState enter();

    DivideState exit();
}
