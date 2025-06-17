package magma.app.compile;

import magma.api.list.Streamable;

public interface DivideState {
    DivideState append(char c);

    DivideState advance();

    Streamable<String> segments();

    boolean isLevel();

    DivideState enter();

    DivideState exit();
}
