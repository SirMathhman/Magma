package magma.state;

import java.util.List;

public interface State {
    State enter();

    State exit();

    State advance();

    State append(char c);

    boolean isLevel();

    List<String> unwrap();
}
