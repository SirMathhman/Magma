package magma.app.compile.divide;

import magma.api.list.ListLike;

public interface DivideState {
    DivideState enter();

    DivideState exit();

    DivideState advance();

    DivideState append(char c);

    boolean isLevel();

    ListLike<String> unwrap();
}
