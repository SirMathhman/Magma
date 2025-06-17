package magma.app.compile;

import magma.api.List;

public interface DivideState {
    DivideState append(char c);

    DivideState advance();

    List<String> segments();

    boolean isLevel();

    DivideState enter();

    DivideState exit();
}
