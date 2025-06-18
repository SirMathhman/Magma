package magma.app.compile.rule.divide;

import magma.api.collect.fold.Folding;

public interface DivideState {
    DivideState append(char c);

    DivideState advance();

    Folding<String> segments();

    boolean isLevel();

    DivideState enter();

    DivideState exit();
}
