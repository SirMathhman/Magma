package magma.app.compile.divide;

import java.util.List;

public interface DivideState {
    DivideState enter();

    DivideState exit();

    DivideState advance();

    DivideState append(char c);

    boolean isLevel();

    List<String> unwrap();
}
