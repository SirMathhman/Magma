package magma.app.compile.rule.divide;

import magma.api.list.Sequence;

public interface DivideState {
    DivideState append(char c);

    DivideState advance();

    Sequence<String> segments();

    boolean isLevel();

    DivideState enter();

    DivideState exit();
}
