package magma.app.compile.divide;

import magma.api.list.Sequence;

public interface DivideState {
    DivideState enter();

    DivideState exit();

    DivideState advance();

    DivideState append(char c);

    boolean isLevel();

    Sequence<String> unwrap();
}
