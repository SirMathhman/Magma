package magma;

import java.util.List;

public interface State {
    State advance();

    State append(char c);

    List<String> unwrap();

    boolean isLevel();

    State enter();

    State exit();

    boolean isShallow();
}
