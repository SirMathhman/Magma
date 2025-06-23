package magma.app.compile.divide;

import magma.api.list.Streamable;

public interface DivideState {
    DivideState append(char c);

    boolean isLevel();

    DivideState exit();

    DivideState enter();

    DivideState advance();

    Streamable<String> toList();
}
