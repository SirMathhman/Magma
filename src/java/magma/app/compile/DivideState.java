package magma.app.compile;

import magma.api.list.Iterable;

public interface DivideState {
    DivideState append(char c);

    DivideState advance();

    Iterable<String> segments();

    boolean isLevel();

    DivideState enter();

    DivideState exit();
}
