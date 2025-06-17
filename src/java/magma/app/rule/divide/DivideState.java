package magma.app.rule.divide;

import java.util.List;

public interface DivideState {
    DivideState append(char c);

    DivideState advance();

    List<String> segments();

    boolean isLevel();

    DivideState enter();

    DivideState exit();
}
