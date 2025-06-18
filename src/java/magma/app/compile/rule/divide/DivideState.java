package magma.app.compile.rule.divide;

import magma.api.list.Foldable;

public interface DivideState {
    DivideState append(char c);

    DivideState advance();

    Foldable<String> segments();

    boolean isLevel();

    DivideState enter();

    DivideState exit();
}
